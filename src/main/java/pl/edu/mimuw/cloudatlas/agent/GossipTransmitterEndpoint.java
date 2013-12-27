package pl.edu.mimuw.cloudatlas.agent;

import java.util.Map;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.TimeValue;
import pl.edu.mimuw.cloudatlas.zones.ZMI;

public interface GossipTransmitterEndpoint<RId> {

	
	public void contactForGossipingReceived(RId requestId, ContactValue contact);
	
	public void timestampsOffered(RId requestId, Map<String, TimeValue> timestamps);
	
	public void timestampsForZMIsExchanged(RId requestId, Map<String, ZMI> zmis);
	
	public void zmisExchanged(RId requestId, Map<String, ZMI> zmis);
}
