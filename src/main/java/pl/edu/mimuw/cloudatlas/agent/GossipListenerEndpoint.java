package pl.edu.mimuw.cloudatlas.agent;

import java.util.Map;

import pl.edu.mimuw.cloudatlas.attributes.TimeValue;
import pl.edu.mimuw.cloudatlas.zones.ZMI;

public interface GossipListenerEndpoint<RId> {
	
	public void getContactForGossiping(RId requestId);

	public void offerTimestamps(RId requestId, String peerName);
	
	public void exchangeTimestampsForZMIs(RId requestId, String peerName, Map<String, TimeValue> timestamps);
	
	public void exchangeZMIs(RId requestId, String peerName, Map<String, ZMI> zmis);
	
	public void acceptZMIs(Map<String, ZMI> zmis);
}
