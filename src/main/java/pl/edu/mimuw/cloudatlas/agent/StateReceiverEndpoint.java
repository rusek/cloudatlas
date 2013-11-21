package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.zones.Attribute;

public interface StateReceiverEndpoint<RId> {
	
	public void zoneAttributeReceived(RId requestId, Attribute attribute);
	
	public void zoneNotFound(RId requestId);

}
