package pl.edu.mimuw.cloudatlas.agent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.edu.mimuw.cloudatlas.islands.ChildEndpoint;
import pl.edu.mimuw.cloudatlas.islands.ChildIsland;
import pl.edu.mimuw.cloudatlas.islands.MotherEndpoint;
import pl.edu.mimuw.cloudatlas.islands.PluggableIsland;

public class DatagramSocketIsland extends PluggableIsland implements ChildIsland, ChildEndpoint {
	
	private static final int BUFFER_SIZE = 8192;
	
	private static Logger log = LogManager.getFormatterLogger(DatagramSocketIsland.class);

	private MotherEndpoint motherEndpoint;
	
	private String host;
	private int port;
	
	private DatagramSocket socket;
	private ReceiverThread receiverThread;
	
	public DatagramSocketIsland(Properties properties) {
		this.host = properties.getProperty("host", "localhost");
		
		String portString = properties.getProperty("port");
		if (portString == null) {
			throw new IllegalArgumentException("Missing port property");
		}
		this.port = Integer.parseInt(portString);
	}
	
	@Override
	public ChildEndpoint mountMother(MotherEndpoint motherEndpoint) {
		assert this.motherEndpoint == null;
		this.motherEndpoint = motherEndpoint;
		
		return this;
	}

	@Override
	public void ignite() {
		log.debug("Creating DatagramSocket, host: %s, port: %d.", host, port);
		try {
			socket = new DatagramSocket(new InetSocketAddress(host, port));
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		receiverThread = new ReceiverThread(socket);
		receiverThread.start();
	}

	@Override
	public void extinguish() {
		log.info("Closing network socket.");
		socket.close();
		socket = null;
		// Now expecting receiveFailed() to be called
	}
	
	private void packetReceived(DatagramPacket packet) {
		
	}
	
	private void receiveFailed(IOException e) {
		if (socket != null) {
			throw new RuntimeException(e);
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

	private class ReceiverThread extends Thread {
		
		private DatagramSocket socket;
		
		public ReceiverThread(DatagramSocket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			for(;;) {
				final DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
				
				try {
					log.debug("Waiting for network packet.");
					socket.receive(packet);
					log.debug("Packet received, length: %d.", packet.getLength());
				} catch (final IOException e) {
					getCarousel().enqueue(new Runnable() {

						@Override
						public void run() {
							receiveFailed(e);
						}
						
					});
					return;
				}
				
				getCarousel().enqueue(new Runnable() {

					@Override
					public void run() {
						packetReceived(packet);
					}
					
				});
			}
		}
	}
}
