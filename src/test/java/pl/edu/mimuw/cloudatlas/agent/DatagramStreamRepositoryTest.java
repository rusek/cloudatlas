package pl.edu.mimuw.cloudatlas.agent;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;

import pl.edu.mimuw.cloudatlas.agent.DatagramStreamRepository.Stream;
import junit.framework.TestCase;

public class DatagramStreamRepositoryTest extends TestCase {
	
	private static Random random = new Random();
	
	private static byte[] genBytes(int size) {
		byte[] bytes = new byte[size];
		for (int i = 0; i < size; i++) {
			bytes[i] = (byte) random.nextInt();
		}
		return bytes;
	}
	
	public static void testFragmentation() {
		StreamTester first = new StreamTester(0);
		StreamTester second = new StreamTester(0, first);
		first.createStream();
		
		int maxPacketSize = DatagramStreamRepository.DatagramSender.MAX_PACKET_SIZE;
		int numPackets = 10;
		byte[] req = genBytes(maxPacketSize * numPackets);
		first.sendMessage(req);
		assertTrue(Arrays.equals(req, second.fetchMessage()));
		assertEquals(
				numPackets + 1, // +1 because some headers are prepended to each packet
				first.getNumSentPackets());
	}

	public static void testTimeDiff() {
		genericTimeDiffTest(1000, 2000, 10);
		genericTimeDiffTest(500, 100, 10);
	}
	
	public static void genericTimeDiffTest(long firstTime, long secondTime, long latency) {
		// Setup
		StreamTester first = new StreamTester(firstTime);
		StreamTester second = new StreamTester(secondTime, first);
		first.createStream();
		
		// Send request
		{
			byte[] req = new byte[]{1, 2, 3};
			second.incTime(latency);
			first.sendMessage(req);
			first.incTime(latency);
			assertTrue(Arrays.equals(req, second.fetchMessage()));
			assertEquals(0, second.getTimeDiff());
		}
		
		// Some work
		first.incTime(5000);
		second.incTime(5000);
		
		// Send response
		{
			byte[] resp = new byte[]{4, 5, 6, 7};
			first.incTime(latency);
			second.sendMessage(resp);
			second.incTime(latency);
			assertTrue(Arrays.equals(resp, first.fetchMessage()));
			assertEquals(secondTime - firstTime, first.getTimeDiff());
			assertEquals(0, second.getTimeDiff());
		}
		
		// More work
		first.incTime(1777);
		second.incTime(1777);
		
		// Send second request
			{
			byte[] req = new byte[]{10, 20};
			second.incTime(latency);
			first.sendMessage(req);
			first.incTime(latency);
			assertTrue(Arrays.equals(req, second.fetchMessage()));
			assertEquals(secondTime - firstTime, first.getTimeDiff());
			assertEquals(firstTime - secondTime, second.getTimeDiff());
		}
	}
	
	private static class StreamTester implements DatagramStreamRepository.StreamAcceptor,
			DatagramStreamRepository.DatagramSender, DatagramStreamRepository.StreamHandler {
		
		private static int counter = 10000;
		
		private StreamRepository repository;
		private SocketAddress address;
		private StreamTester other = null;
		private DatagramStreamRepository.Stream stream = null;
		private byte[] message = null;
		private int numSentPackets = 0;
		
		public StreamTester(long time) {
			address = new InetSocketAddress("localhost", counter++);
			repository = new StreamRepository(this, this);
			repository.time = time;
		}
		
		public StreamTester(long time, StreamTester other) {
			this(time);
			this.other = other;
			other.other = this;
		}
		
		public int getNumSentPackets() {
			return numSentPackets;
		}

		@Override
		public void sendPacket(DatagramPacket packet) {
			numSentPackets++;
			assertEquals(other.address, packet.getSocketAddress());
			byte[] data = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getOffset() + packet.getLength());
			try {
				DatagramPacket otherPacket = new DatagramPacket(data, data.length, address);
				other.repository.receivePacket(otherPacket, other.repository.time);
			} catch (SocketException e) {
				assertTrue(e.toString(), false);
			}
		}
		
		public void sendMessage(byte[] data) {
			assertNotNull(stream);
			stream.sendMessage(data);
		}
		
		public void incTime(long diff) {
			repository.time += diff;
		}
		
		public long getTimeDiff() {
			assertNotNull(this.stream);
			return this.stream.getTimeDiff();
		}
		
		public byte[] fetchMessage() {
			assertNotNull(this.message);
			byte[] message = this.message;
			this.message = null;
			return message;
		}
		
		public void createStream() {
			assertNull(this.stream);
			this.stream = repository.createStream(other.address);
			stream.setHandler(this);
		}

		@Override
		public void acceptStream(Stream stream) {
			assertNull(this.stream);
			this.stream = stream;
			stream.setHandler(this);
		}

		@Override
		public void receiveMessage(Stream stream, byte[] data) {
			assertNull(this.message);
			assertSame(stream, this.stream);
			this.message = data;
		}
		
	}
	
	private static class StreamRepository extends DatagramStreamRepository {
		
		private long time = 0;

		public StreamRepository(StreamAcceptor acceptor, DatagramSender sender) {
			super(acceptor, sender);
		}
		
		@Override
		protected long now() {
			return time;
		}
		
	}
}
