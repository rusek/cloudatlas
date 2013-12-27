package pl.edu.mimuw.cloudatlas.agent;

import java.util.Map;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.TimeValue;
import pl.edu.mimuw.cloudatlas.zones.ZMI;

public class GossipTransmitterDispatcher implements GossipTransmitterEndpoint<GossipTransmitterEndpoint<Void>> {

	@Override
	public void contactForGossipingReceived(
			GossipTransmitterEndpoint<Void> requestId, ContactValue contact) {
		requestId.contactForGossipingReceived(null, contact);
	}

	@Override
	public void timestampsOffered(GossipTransmitterEndpoint<Void> requestId,
			Map<String, TimeValue> timestamps) {
		requestId.timestampsOffered(null, timestamps);
	}

	@Override
	public void timestampsForZMIsExchanged(
			GossipTransmitterEndpoint<Void> requestId, Map<String, ZMI> zmis) {
		requestId.timestampsForZMIsExchanged(null, zmis);
	}

	@Override
	public void zmisExchanged(GossipTransmitterEndpoint<Void> requestId,
			Map<String, ZMI> zmis) {
		requestId.zmisExchanged(null, zmis);
	}
}
