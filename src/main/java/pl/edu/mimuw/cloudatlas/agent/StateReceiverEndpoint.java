package pl.edu.mimuw.cloudatlas.agent;

import java.util.Collection;

import pl.edu.mimuw.cloudatlas.zones.Attribute;

public interface StateReceiverEndpoint<RId> {
	
	public void zoneAttributeFetched(RId requestId, Attribute attribute);
	
	public void myZoneAttributesUpdated(RId requestId);
	
	public void zoneNamesFetched(RId requestId, Collection<String> zoneNames);
	
	public void zoneAttributeNamesFetched(RId requestId, Collection<String> attributeNames);
	
	public void zoneAttributesFetched(RId requestId, Collection<Attribute> attributes);
	
	public void myZoneNameFetched(RId requestId, String zoneName);
	
	public void zoneNotFound(RId requestId);

	public void attributeNotFound(RId requestId);
	
	public void queryInstalled(RId requestId);
	
	public void queryUninstalled(RId requestId);
}
