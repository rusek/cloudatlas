package pl.edu.mimuw.cloudatlas.agent;

import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * How time synchronization works
 * 
 *   - dA - difference between agent clocks as observed by agent A (time at A = time at B + dA)
 *   - lat - network latency (constant)
 *   
 * Each exchanged message contains two values used for time synchronization: agent time recorded just before
 * sending the message (senderTimestamp) and observed time shift (real time difference + latency).
 * 
 * When agent A sends initial message, he appends (TA1, -) to the message, where TA1 is current time and "-" indicates
 * that observed time shift  is not yet available. When agent B receives message, he reads current time (TB1) and
 * memoizes internally time shift: S1 = TB1 - TA1 = lat + dA. Prior to sending reply to agent A he retrieves time TB2
 * and appends (TB2, S1) to  the message. After receiving the reply agent A retrieves local time TA2 (= TB2 - dA + lat)
 * and computes:   
 *     dA = (TB2 + S1 - TA2) / 2
 *     S2 = TB2 - TA2 (time shift value)
 */
public class DatagramStreamRepository {
	
	private static long MAX_STREAM_IDLE_DURATION = 60 * 1000; // 60 seconds
	
	private static Logger log = LogManager.getFormatterLogger(DatagramStreamRepository.class);
	
	private static final int HEADER_SIZE = 20;
	
	private final Map<StreamKey, Stream> streams = new HashMap<StreamKey, Stream>();
	
	private Random random = new Random();
	private DatagramSender sender;
	private StreamAcceptor acceptor;
	
	public DatagramStreamRepository(StreamAcceptor acceptor, DatagramSender sender) {
		this.acceptor = acceptor;
		this.sender = sender;
	}
	
	public Stream createStream(SocketAddress address) {
		int streamId = random.nextInt();
		StreamKey key = new StreamKey(address, streamId);
		Stream stream = new Stream(key);
		streams.put(key, stream);
		return stream;
	}
	
	protected long now() { // overridden in unittests 
		return new Date().getTime();
	}
	
	public void tick() {
		long lastActivityThreshold = now() - MAX_STREAM_IDLE_DURATION;
		List<Stream> streams = new ArrayList<Stream>();
		streams.addAll(this.streams.values());
		
		for (Stream stream : streams) {
			if (stream.lastActivity < lastActivityThreshold) {
				log.debug("Dropping idle stream %s", stream.key);
				stream.close();
			}
		}
	}
	
	public void receivePacket(DatagramPacket packet, long timestamp) {
		if (packet.getLength() < HEADER_SIZE) {
			log.warn("Invalid packet received");
			return;
		}
		
		int streamId = ByteArrays.readInt(packet.getData(), packet.getOffset());
		int seqNumAndBoundary = ByteArrays.readInt(packet.getData(), packet.getOffset() + 4);
		int seqNum = seqNumAndBoundary & ~(1 << 31);
		boolean boundary = (seqNumAndBoundary & (1 << 31)) != 0;
		long senderTimestamp = ByteArrays.readLong(packet.getData(), packet.getOffset() + 8);
		int senderShift = ByteArrays.readInt(packet.getData(), packet.getOffset() + 16);
		log.debug("Received packet, streamId: %d, seqNum: %d, boundary: %b, timestamp: %d, shift: %d",
				streamId, seqNum, boundary, senderTimestamp, senderShift);
		
		StreamKey key = new StreamKey(packet.getSocketAddress(), streamId);
		StreamFrame frame = new StreamFrame(boundary, packet.getData(), packet.getOffset() + HEADER_SIZE,
				packet.getLength() - HEADER_SIZE);
		Stream stream = streams.get(key);
		if (stream == null) {
			log.debug("Accepting new stream, streamId: %d, address: %s", streamId, packet.getSocketAddress());
			stream = new Stream(key);
			streams.put(key, stream);
			acceptor.acceptStream(stream);
			if (!streams.containsKey(key)) { // closed during accept?
				return;
			}
		}
		stream.addFrame(seqNum, frame, timestamp, senderTimestamp, senderShift);
	}
	
	public static class StreamKey {
		private final SocketAddress address;
		private final int streamId;
		
		public StreamKey(SocketAddress address, int streamId) {
			this.address = address;
			this.streamId = streamId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((address == null) ? 0 : address.hashCode());
			result = prime * result + streamId;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StreamKey other = (StreamKey) obj;
			if (address == null) {
				if (other.address != null)
					return false;
			} else if (!address.equals(other.address))
				return false;
			if (streamId != other.streamId)
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "StreamKey(" + address + ", " + streamId + ")";
		}
	}
	
	public class Stream {
		private Map<Integer, StreamFrame> frames = new HashMap<Integer, StreamFrame>();
		private int startSeqNum = 0;
		private int endSeqNum = 0;
		private int outSeqNum = 0;
		private final StreamKey key;
		private StreamHandler handler = null;
		private int observedTimeShift = Integer.MIN_VALUE;
		private Long timeDiff = null;
		private boolean closed = false;
		private long lastActivity = now();
		
		private Stream(StreamKey key) {
			this.key = key;
		}
		
		// myTime + timeDiff = peerTime
		public long getTimeDiff() {
			if (timeDiff == null) {
				log.warn("No time difference available yet");
				return 0;
			} else {
				return timeDiff;
			}
		}
		
		public boolean isClosed() {
			return closed;
		}
		
		public void setHandler(StreamHandler handler) {
			this.handler = handler;
		}
		
		public void sendMessage(byte[] data) {
			if (closed) {
				return;
			}
			lastActivity = now();
			
			int offset = 0;
			while (offset != data.length) {
				int sentThisTime = Math.min(DatagramSender.MAX_PACKET_SIZE - HEADER_SIZE, data.length - offset);
				byte[] datagramData = new byte[sentThisTime + HEADER_SIZE];
				ByteArrays.writeInt(datagramData, 0, key.streamId);
				System.arraycopy(data, offset, datagramData, HEADER_SIZE, sentThisTime);
				offset += sentThisTime;
				ByteArrays.writeInt(datagramData, 4, outSeqNum | (offset == data.length ? 1 << 31 : 0));
				ByteArrays.writeLong(datagramData, 8, now());
				ByteArrays.writeInt(datagramData, 16, observedTimeShift);
				outSeqNum++;
				
				DatagramPacket packet;
				packet = new DatagramPacket(datagramData, datagramData.length);
				try {
					packet.setSocketAddress(key.address);
				} catch (IllegalArgumentException e) {
					log.warn("Could not construct packet to %s (error: %s)", key.address, e.getMessage());
					return;
				}
				sender.sendPacket(packet);
			}
		}
		
		public void close() {
			if (closed) {
				return;
			}
			closed = true;
			streams.remove(key);
		}
		
		private void addFrame(int seqNum, StreamFrame frame, long timestamp, long senderTimestamp, int senderShift) {
			frames.put(seqNum, frame);
			observedTimeShift = (int) (timestamp - senderTimestamp);
			if (senderShift != Integer.MIN_VALUE) {
				timeDiff = (senderTimestamp + senderShift - timestamp) / 2;
			}
			log.debug("Adding frame to stream, observedShift = %d, timeDiff = %d", observedTimeShift, timeDiff);
			advanceBoundary();
			log.debug("Completed adding frame, startSeqNum: %d, endSeqNum: %d", startSeqNum, endSeqNum);
		}
		
		private void advanceBoundary() {
			for (;;) {
				StreamFrame frame = frames.get(endSeqNum);
				if (frame == null) {
					return;
				}
				endSeqNum++;
				if (frame.hasBoundary()) {
					emitMessage();
					startSeqNum = endSeqNum;
				}
			}
		}
		
		private void emitMessage() {
			log.debug("Emitting message from stream, startSeqNum: %d, endSeqNum: %d", startSeqNum, endSeqNum);
			lastActivity = now();
			int length = 0;
			for (int i = startSeqNum; i < endSeqNum; i++) {
				length += frames.get(i).getLength();
			}
			
			byte[] data = new byte[length];
			int offset = 0;
			for (int i = startSeqNum; i < endSeqNum; i++) {
				StreamFrame frame = frames.get(i);
				System.arraycopy(frame.getData(), frame.getOffset(), data, offset, frame.getLength());
				offset += frame.getLength();
				frames.remove(i);
			}
			
			this.handler.receiveMessage(this, data);
		}
	}
	
	private static class StreamFrame {
		private boolean boundary;
		private byte[] data;
		private int offset;
		private int length;
		
		public StreamFrame(boolean boundary, byte[] data, int offset, int length) {
			this.boundary = boundary;
			this.data = data;
			this.offset = offset;
			this.length = length;
		}
		
		public boolean hasBoundary() {
			return boundary;
		}
		
		public byte[] getData() {
			return data;
		}

		public int getOffset() {
			return offset;
		}

		public int getLength() {
			return length;
		}
	}
	
	public static interface StreamHandler {
		
		public void receiveMessage(Stream stream, byte[] data);
	}
	
	public static interface StreamAcceptor {
		
		public void acceptStream(Stream stream); 
	}
	
	public static interface DatagramSender {
		
		public static final int MAX_PACKET_SIZE = 1500;

		public void sendPacket(DatagramPacket packet);
	}

}
