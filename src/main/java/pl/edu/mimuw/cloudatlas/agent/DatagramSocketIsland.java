package pl.edu.mimuw.cloudatlas.agent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.edu.mimuw.cloudatlas.agent.DatagramStreamRepository.Stream;
import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.TimeValue;
import pl.edu.mimuw.cloudatlas.islands.ChildEndpoint;
import pl.edu.mimuw.cloudatlas.islands.ChildIsland;
import pl.edu.mimuw.cloudatlas.islands.IslandException;
import pl.edu.mimuw.cloudatlas.islands.MotherEndpoint;
import pl.edu.mimuw.cloudatlas.islands.PluggableIsland;
import pl.edu.mimuw.cloudatlas.islands.TimerEndpoint;
import pl.edu.mimuw.cloudatlas.islands.TimerFeedbackEndpoint;
import pl.edu.mimuw.cloudatlas.islands.TimerFeedbackIsland;
import pl.edu.mimuw.cloudatlas.zones.ZMI;
import pl.edu.mimuw.cloudatlas.zones.ZoneNames;

public class DatagramSocketIsland extends PluggableIsland implements
		ChildIsland,
		ChildEndpoint,
		DatagramStreamRepository.DatagramSender,
		TimerFeedbackIsland<Runnable>,
		TimerFeedbackEndpoint<Runnable>,
		GossipTransmitterIsland<GossipTransmitterEndpoint<Void>> {
	
	public static long DEFAULT_GOSSIP_INTERVAL = 5000;
	
	private static Logger log = LogManager.getFormatterLogger(DatagramSocketIsland.class);

	private MotherEndpoint motherEndpoint;
	private TimerEndpoint<Runnable> timerEndpoint;
	private GossipListenerEndpoint<GossipTransmitterEndpoint<Void>> gossipListenerEndpoint;
	
	private final String zoneName;
	private boolean extinguishing = false;
	private String host;
	private int port;
	private long gossipInterval = DEFAULT_GOSSIP_INTERVAL;
	
	private DatagramSocket socket;
	private ReceiverThread receiverThread;
	private DatagramStreamRepository streamRepository;
	
	public DatagramSocketIsland(String zoneName, Properties properties) {
		this.zoneName = zoneName;
		this.host = PropertyReader.getHost(properties);
		this.port = PropertyReader.getPort(properties);
		
		String gossipIntervalString = properties.getProperty("gossipInterval");
		if (gossipIntervalString != null) {
			gossipInterval = Long.parseLong(gossipIntervalString);
		}
		
		this.streamRepository = new DatagramStreamRepository(new DatagramStreamRepository.StreamAcceptor() {
			
			@Override
			public void acceptStream(Stream stream) {
				new StreamHandler(stream); // stream.setHandler() called in constructor
			}
		}, this);
	}
	
	@Override
	public ChildEndpoint mountMother(MotherEndpoint motherEndpoint) {
		assert this.motherEndpoint == null;
		this.motherEndpoint = motherEndpoint;
		
		return this;
	}

	@Override
	public TimerFeedbackEndpoint<Runnable> mountTimer(
			TimerEndpoint<Runnable> timerEndpoint) {
		assert this.timerEndpoint == null;
		this.timerEndpoint = timerEndpoint;
		
		return this;
	}

	@Override
	public GossipTransmitterEndpoint<GossipTransmitterEndpoint<Void>> mountGossipListener(
			GossipListenerEndpoint<GossipTransmitterEndpoint<Void>> gossipListener) {
		gossipListenerEndpoint = gossipListener;
		
		return new GossipTransmitterDispatcher();
	}

	@Override
	public void ignite() {
		log.debug("Creating DatagramSocket, host: %s, port: %d.", host, port);
		try {
			socket = new DatagramSocket(new InetSocketAddress(host, port));
		} catch (SocketException e) {
			throw new IslandException(e);
		}
		receiverThread = new ReceiverThread(socket);
		receiverThread.start();
		
		Runnable gossipTask = new Runnable() {

			@Override
			public void run() {
				if (extinguishing) {
					return;
				}
				streamRepository.tick();
				
				gossipListenerEndpoint.getContactForGossiping(new GossipTransmitterAdapter<Void>() {

					@Override
					public void contactForGossipingReceived(Void requestId,
							ContactValue contact) {
						if (contact == null) {
							log.debug("No contact for gossiping");
							return;
						}
						
						InetSocketAddress address = new InetSocketAddress(contact.getHost(), contact.getPort());
						new StreamHandler(streamRepository.createStream(address)).initiateGossiping();
					}
					
				});
				
				timerEndpoint.schedule(this, gossipInterval);
			}
			
		};
		 
		timerEndpoint.schedule(gossipTask, gossipInterval);
	}

	@Override
	public void extinguish() {
		extinguishing = true;
		log.info("Closing network socket.");
		socket.close();
		socket = null;
		// Now expecting receiveFailed() to be called
	}

	@Override
	public void fire(Runnable object) {
		if (!extinguishing) {
			object.run();
		}
	}

	@Override
	public void sendPacket(DatagramPacket packet) {
		try {
			log.debug("Sending network packet, length: %d, target: %s", packet.getLength(), packet.getSocketAddress());
			
			socket.send(packet);
		} catch (IOException e) {
			throw new IslandException(e);
		}
	}
	
	private void packetReceived(DatagramPacket packet, long timestamp) {
		if (!extinguishing) {
			streamRepository.receivePacket(packet, timestamp);
		}
	}
	
	private void receiveFailed(IOException e) {
		if (socket != null) {
			throw new IslandException(e);
		} else {
			// Exception was thrown due to socket.close()
			log.info("Network socket closed.");
			motherEndpoint.childExtinguished();
		}
	}
	
	public void destroy() {
		try {
			receiverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static enum StreamState {
		NEW,
		EXPECTING_TIMESTAMPS,
		EXPECTING_FORMER_ZMIS,
		EXPECTING_LATTER_ZMIS,
		WAITING_FOR_LISTENER,
		COMPLETED
	}
	
	public class StreamHandler extends GossipTransmitterAdapter<Void> implements
			DatagramStreamRepository.StreamHandler {
		
		private final Stream stream;
		private StreamState state = StreamState.NEW;
		private String peerName;
		private Set<String> sentZMIs;
		private Set<String> receivedZMIs;
		
		public StreamHandler(Stream stream) {
			this.stream = stream;
			stream.setHandler(this);
		}

		public void initiateGossiping() {
			assert state == StreamState.NEW;
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DataOutputStream output = new DataOutputStream(outputStream);
			
			try {
				output.writeUTF(zoneName);
			} catch (IOException e) {
				log.error("Error while constructing message bytes", e);
				state = StreamState.COMPLETED;
				stream.close();
				return;
			}
			
			log.debug("Sending message with zone name");
			state = StreamState.EXPECTING_TIMESTAMPS;
			stream.sendMessage(outputStream.toByteArray());
		}

		@Override
		public void receiveMessage(Stream stream, byte[] data) {
			assert !extinguishing;
			
			DataInput input = new DataInputStream(new ByteArrayInputStream(data));
			
			try {
				switch (state) {
				case NEW:
					receiveZoneNameMessage(input);
					break;
					
				case EXPECTING_TIMESTAMPS:
					receiveTimestampsMessage(input);
					break;
					
				case EXPECTING_FORMER_ZMIS:
					receiveFormerZMIsMessage(input);
					break;
					
				case EXPECTING_LATTER_ZMIS:
					receiveLatterZMIsMessage(input);
					break;
				
				case COMPLETED:
				case WAITING_FOR_LISTENER:
					throw new IOException("Invalid state: " + state);
				}
			} catch (IOException e) {
				log.error("Error while reading message bytes", e);
				state = StreamState.COMPLETED;
				stream.close();
				return;
			}
		}
		
		private void receiveZoneNameMessage(DataInput input) throws IOException {
			String peerName = input.readUTF();
			if (!ZoneNames.isGlobalName(peerName)) {
				throw new IOException("Not a global name: " + peerName);
			}
			
			log.debug("Received message with zone name %s", peerName);
			this.peerName = peerName;
			state = StreamState.WAITING_FOR_LISTENER;
			gossipListenerEndpoint.offerTimestamps(this, peerName);
		}
		
		private void receiveTimestampsMessage(DataInput input) throws IOException {
			String peerName = input.readUTF();
			if (!ZoneNames.isGlobalName(peerName)) {
				throw new IOException("Not a global name: " + peerName);
			}
			
			int size = input.readInt();
			if (size < 0) {
				throw new IOException("Negative length");
			}
			
			Map<String, TimeValue> timestamps = new HashMap<String, TimeValue>();
			long timeDiff = stream.getSenderTimeDiff();
			for (int i = 0; i < size; i++) {
				String zoneName = input.readUTF();
				TimeValue timestamp = SimpleType.TIME.compactReadValue(input).addDuration(timeDiff);
				timestamps.put(zoneName, timestamp);
			}
			
			log.debug("Received message with timestamps", timestamps);
			this.peerName = peerName;
			state = StreamState.WAITING_FOR_LISTENER;
			gossipListenerEndpoint.exchangeTimestampsForZMIs(this, peerName, timestamps);
		}
		
		private void receiveFormerZMIsMessage(DataInput input) throws IOException {
			Map<String, ZMI> zmis = readZMIs(input);
			
			log.debug("Received message with former ZMIs: %s", zmis.keySet());
			receivedZMIs = zmis.keySet();
			state = StreamState.WAITING_FOR_LISTENER;
			gossipListenerEndpoint.exchangeZMIs(this, peerName, zmis);
		}
		
		private void receiveLatterZMIsMessage(DataInput input) throws IOException {
			Map<String, ZMI> zmis = readZMIs(input);
			
			log.debug("Received message with latter ZMIs: %s", zmis.keySet());
			receivedZMIs = zmis.keySet();
			state = StreamState.COMPLETED;
			gossipListenerEndpoint.acceptZMIs(zmis);
			
			log.info("Gossiping to %s completed, sent: %s, received: %s", peerName, sentZMIs, receivedZMIs);
		}
		
		private TimeValue getTimestamp(ZMI zmi) {
			return (TimeValue) zmi.getAttributeValue("timestamp");
		}
		
		private Map<String, ZMI> readZMIs(DataInput input) throws IOException {
			int size = input.readInt();
			if (size < 0) {
				throw new IOException("Negative length");
			}
			long timeDiff = stream.getSenderTimeDiff();
			Map<String, ZMI> zmis = new HashMap<String, ZMI>();
			for (int i = 0; i < size; i++) {
				String zoneName = input.readUTF();
				if (!ZoneNames.isGlobalName(zoneName)) {
					throw new IOException("Not a global name: " + zoneName);
				}
				ZMI zmi = ZMI.compactRead(input);
				zmi.setAttribute("timestamp", getTimestamp(zmi).addDuration(timeDiff));
				zmis.put(zoneName, zmi);
			}
			return zmis;
		}

		@Override
		public void timestampsOffered(Void requestId,
				Map<String, TimeValue> timestamps) {
			if (extinguishing || state != StreamState.WAITING_FOR_LISTENER) {
				return;
			}
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DataOutputStream output = new DataOutputStream(outputStream);
			
			try {
				output.writeUTF(zoneName);
				output.writeInt(timestamps.size());
				for (Map.Entry<String, TimeValue> entry : timestamps.entrySet()) {
					output.writeUTF(entry.getKey());
					entry.getValue().compactWrite(output);
				}
			} catch (IOException e) {
				log.error("Error while constructing message bytes", e);
				state = StreamState.COMPLETED;
				stream.close();
				return;
			}
			
			log.debug("Sending message with timestamps", timestamps);
			state = StreamState.EXPECTING_FORMER_ZMIS;
			stream.sendMessage(outputStream.toByteArray());
		}

		@Override
		public void timestampsForZMIsExchanged(Void requestId,
				Map<String, ZMI> zmis) {
			if (extinguishing || state != StreamState.WAITING_FOR_LISTENER) {
				return;
			}
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DataOutputStream output = new DataOutputStream(outputStream);
			
			try {
				writeZMIs(output, zmis);
			} catch (IOException e) {
				log.error("Error while constructing message bytes", e);
				state = StreamState.COMPLETED;
				stream.close();
				return;
			}
			
			log.debug("Timestamps exchanged for former ZMIs: %s", zmis.keySet());
			sentZMIs = zmis.keySet();
			state = StreamState.EXPECTING_LATTER_ZMIS;
			stream.sendMessage(outputStream.toByteArray());
		}
		
		private void writeZMIs(DataOutput output, Map<String, ZMI> zmis) throws IOException {
			output.writeInt(zmis.size());
			for (Map.Entry<String, ZMI> entry : zmis.entrySet()) {
				output.writeUTF(entry.getKey());
				entry.getValue().compactWrite(output);
			}
		}

		@Override
		public void zmisExchanged(Void requestId, Map<String, ZMI> zmis) {
			if (extinguishing || state != StreamState.WAITING_FOR_LISTENER) {
				return;
			}
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DataOutputStream output = new DataOutputStream(outputStream);
			
			try {
				writeZMIs(output, zmis);
			} catch (IOException e) {
				log.error("Error while constructing message bytes", e);
				state = StreamState.COMPLETED;
				stream.close();
				return;
			}
			
			log.debug("Former ZMIs exchanged for latter ZMIs %s", zmis.keySet());
			sentZMIs = zmis.keySet();
			state = StreamState.COMPLETED;
			stream.sendMessage(outputStream.toByteArray());
			
			log.info("Gossiping from %s completed, sent: %s, received: %s", peerName, sentZMIs, receivedZMIs);
		}
		
	}

	private class ReceiverThread extends Thread {
		
		private DatagramSocket socket;
		
		public ReceiverThread(DatagramSocket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			for(;;) {
				byte[] data = new byte[DatagramStreamRepository.DatagramSender.MAX_PACKET_SIZE];
				final DatagramPacket packet = new DatagramPacket(data, data.length);
				
				try {
					log.debug("Waiting for network packet.");
					socket.receive(packet);
					final long timestamp = new Date().getTime();
					
					log.debug("Packet received, length: %d, sender: %s", packet.getLength(),
							packet.getSocketAddress());
					
					getCarousel().enqueue(new Runnable() {

						@Override
						public void run() {
							packetReceived(packet, timestamp);
						}
						
					});
				} catch (final IOException e) {
					getCarousel().enqueue(new Runnable() {

						@Override
						public void run() {
							receiveFailed(e);
						}
						
					});
					return;
				}
			}
		}
	}
}
