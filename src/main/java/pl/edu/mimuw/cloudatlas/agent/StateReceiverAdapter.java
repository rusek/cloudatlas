package pl.edu.mimuw.cloudatlas.agent;

import java.util.Collection;

import pl.edu.mimuw.cloudatlas.zones.Attribute;

public class StateReceiverAdapter<RId> implements StateReceiverEndpoint<RId> {
	
	@Override
	public void zoneAttributeFetched(RId requestId, Attribute attribute) {
		throw new RuntimeException("Unexpected callback invoked");
	}
	
	@Override
	public void zoneNotFound(RId requestId) {
		throw new RuntimeException("Unexpected callback invoked");
	}
	
	@Override
	public void myZoneAttributesUpdated(RId requestId) {
		throw new RuntimeException("Unexpected callback invoked");
		
	}
	
	@Override
	public void zoneNamesFetched(RId requestId,
			Collection<String> zoneNames) {
		throw new RuntimeException("Unexpected callback invoked");
	}
	
	@Override
	public void zoneAttributeNamesFetched(RId requestId,
			Collection<String> attributeNames) {
		throw new RuntimeException("Unexpected callback invoked");
	}
	
	@Override
	public void myZoneNameFetched(RId requestId, String zoneName) {
		throw new RuntimeException("Unexpected callback invoked");
	}
	
	@Override
	public void attributeNotFound(RId requestId) {
		throw new RuntimeException("Unexpected callback invoked");
	}

	@Override
	public void queryInstalled(RId requestId) {
		throw new RuntimeException("Unexpected callback invoked");
	}

	@Override
	public void queryUninstalled(RId requestId) {
		throw new RuntimeException("Unexpected callback invoked");
	}
}
