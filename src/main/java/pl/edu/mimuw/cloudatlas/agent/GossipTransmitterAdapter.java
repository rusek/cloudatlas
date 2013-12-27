package pl.edu.mimuw.cloudatlas.agent;

import java.util.Map;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.TimeValue;
import pl.edu.mimuw.cloudatlas.zones.ZMI;

public class GossipTransmitterAdapter<RId> implements GossipTransmitterEndpoint<RId> {

	@Override
	public void contactForGossipingReceived(RId requestId, ContactValue contact) {
		throw new RuntimeException("Unexpected callback invoked");
	}

	@Override
	public void timestampsOffered(RId requestId,
			Map<String, TimeValue> timestamps) {
		throw new RuntimeException("Unexpected callback invoked");
	}

	@Override
	public void timestampsForZMIsExchanged(RId requestId, Map<String, ZMI> zmis) {
		throw new RuntimeException("Unexpected callback invoked");
	}

	@Override
	public void zmisExchanged(RId requestId, Map<String, ZMI> zmis) {
		throw new RuntimeException("Unexpected callback invoked");
	}

}
