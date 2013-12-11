package pl.edu.mimuw.cloudatlas.agent;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.edu.mimuw.cloudatlas.agent.DatagramStreamRepository.Stream;
import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.islands.ChildEndpoint;
import pl.edu.mimuw.cloudatlas.islands.ChildIsland;
import pl.edu.mimuw.cloudatlas.islands.IslandException;
import pl.edu.mimuw.cloudatlas.islands.MotherEndpoint;
import pl.edu.mimuw.cloudatlas.islands.PluggableIsland;
import pl.edu.mimuw.cloudatlas.islands.TimerEndpoint;
import pl.edu.mimuw.cloudatlas.islands.TimerFeedbackEndpoint;
import pl.edu.mimuw.cloudatlas.islands.TimerFeedbackIsland;

public class DatagramSocketIsland extends PluggableIsland implements
		ChildIsland,
		ChildEndpoint,
		DatagramStreamRepository.DatagramSender,
		TimerFeedbackIsland<Runnable>,
		TimerFeedbackEndpoint<Runnable>,
		StateReceiverIsland<DatagramSocketIsland.StreamHandler> {
	
	private static Logger log = LogManager.getFormatterLogger(DatagramSocketIsland.class);

	private MotherEndpoint motherEndpoint;
	private TimerEndpoint<Runnable> timerEndpoint;
	private StateProviderEndpoint<StreamHandler> stateEndpoint;
	
	private boolean extinguishing = false;
	private String host;
	private int port;
	
	private DatagramSocket socket;
	private ReceiverThread receiverThread;
	private DatagramStreamRepository streamRepository;
	
	public DatagramSocketIsland(Properties properties) {
		this.host = properties.getProperty("host", "localhost");
		
		String portString = properties.getProperty("port");
		if (portString == null) {
			throw new IllegalArgumentException("Missing port property");
		}
		this.port = Integer.parseInt(portString);
		
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
		this.timerEndpoint = timerEndpoint;
		
		return this;
	}

	@Override
	public StateReceiverEndpoint<StreamHandler> mountStateProvider(
			StateProviderEndpoint<StreamHandler> providerEndpoint) {
		this.stateEndpoint = providerEndpoint;
		
		return new StateReceiverAdapter<StreamHandler>() {

			@Override
			public void contactForGossipingReceived(StreamHandler requestId,
					ContactValue contact) {
				if (contact == null) {
					log.info("No contact for gossiping");
					return;
				}
				InetSocketAddress address = new InetSocketAddress(contact.getHost(), contact.getPort());
				new StreamHandler(streamRepository.createStream(address)).sendRoundMessage(0);
			}
			
		};
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
				stateEndpoint.getContactForGossiping(null);
				//new StreamHandler(streamRepository.createStream(new InetSocketAddress("localhost", 6660))).sendRoundMessage(0);
				
				timerEndpoint.schedule(this, 5000);
			}
			
		};
		
		if (port == 6666) 
			timerEndpoint.schedule(gossipTask, 5000);
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
	
	public class StreamHandler implements DatagramStreamRepository.StreamHandler {
		
		private final Stream stream;
		
		public StreamHandler(Stream stream) {
			this.stream = stream;
			stream.setHandler(this);
		}

		public void sendRoundMessage(int roundNo) {
			log.info("Sending message, round: %d", roundNo);
			byte[] output = new byte[4];
			ByteArrays.writeInt(output, 0, roundNo);
			stream.sendMessage(output);
		}
		
		@Override
		public void receiveMessage(Stream stream, byte[] data) {
			DataInput input = new DataInputStream(new ByteArrayInputStream(data));
			try {
				int roundNo = input.readInt();
				log.info("Received message, round: %d", roundNo);
				if (roundNo < 10) {
					sendRoundMessage(roundNo + 1);
				}
				
			} catch (IOException e) {
				throw new IslandException(e);
			}
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
					
					log.debug("Packet received, length: %d, sender: %s", packet.getLength(), packet.getSocketAddress());
					
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
