package pl.edu.mimuw.cloudatlas.agent;

import java.util.HashMap;
import java.util.Map;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.TimeValue;
import pl.edu.mimuw.cloudatlas.islands.Tube;
import pl.edu.mimuw.cloudatlas.zones.ZMI;

public class GossipTube<RId> extends Tube<GossipTransmitterEndpoint<RId>, GossipListenerEndpoint<RId>>
		implements GossipTransmitterEndpoint<RId>, GossipListenerEndpoint<RId> {

	public static <RId> GossipTube<RId> entangle(
			GossipTransmitterIsland<RId> transmitter,
			GossipListenerIsland listener) {
		GossipTube<RId> tube = new GossipTube<RId>();
		tube.setLeft(transmitter.getCarousel(), transmitter.mountGossipListener(tube));
		tube.setRight(listener.getCarousel(), listener.mountGossipTransmitter(tube));
		
		return tube;
	}

	private Map<String, ZMI> deepCopyZMIs(Map<String, ZMI> zmis) {
		final Map<String, ZMI> zmisCopy = new HashMap<String, ZMI>();
		for (Map.Entry<String, ZMI> entry : zmis.entrySet()) {
			zmisCopy.put(entry.getKey(), entry.getValue().deepCopy());
		}
		return zmisCopy;
	}
	
	@Override
	public void contactForGossipingReceived(final RId requestId, final ContactValue contact) {
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().contactForGossipingReceived(requestId, contact);
			}
			
		});
	}

	@Override
	public void timestampsOffered(final RId requestId,
			Map<String, TimeValue> timestamps) {
		final Map<String, TimeValue> timestampsCopy = new HashMap<String, TimeValue>();
		timestampsCopy.putAll(timestamps);
		
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().timestampsOffered(requestId, timestampsCopy);
			}
			
		});
	}

	@Override
	public void timestampsForZMIsExchanged(final RId requestId, Map<String, ZMI> zmis) {
		final Map<String, ZMI> zmisCopy = deepCopyZMIs(zmis);
		
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().timestampsForZMIsExchanged(requestId, zmisCopy);
			}
			
		});
		
	}

	@Override
	public void zmisExchanged(final RId requestId, Map<String, ZMI> zmis) {
		final Map<String, ZMI> zmisCopy = deepCopyZMIs(zmis);
		
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().zmisExchanged(requestId, zmisCopy);
			}
			
		});
		
	}

	@Override
	public void getContactForGossiping(final RId requestId) {
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().getContactForGossiping(requestId);
			}
			
		});
	}

	@Override
	public void offerTimestamps(final RId requestId, final String peerName) {
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().offerTimestamps(requestId, peerName);
			}
			
		});
	}

	@Override
	public void exchangeTimestampsForZMIs(final RId requestId, final String peerName,
			Map<String, TimeValue> timestamps) {
		final Map<String, TimeValue> timestampsCopy = new HashMap<String, TimeValue>();
		timestampsCopy.putAll(timestamps);
		
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().exchangeTimestampsForZMIs(requestId, peerName, timestampsCopy);
			}
			
		});
		
	}

	@Override
	public void exchangeZMIs(final RId requestId, final String peerName, Map<String, ZMI> zmis) {
		final Map<String, ZMI> zmisCopy = deepCopyZMIs(zmis);
		
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().exchangeZMIs(requestId, peerName, zmisCopy);
			}
			
		});
	}

	@Override
	public void acceptZMIs(Map<String, ZMI> zmis) {
		final Map<String, ZMI> zmisCopy = deepCopyZMIs(zmis);
		
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().acceptZMIs(zmisCopy);
			}
			
		});
	}
}
