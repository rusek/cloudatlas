package pl.edu.mimuw.cloudatlas.agent;

import java.util.List;

import pl.edu.mimuw.cloudatlas.zones.Attribute;

public interface StateProviderEndpoint<RId> {
	
	public void fetchZoneAttribute(RId requestId, String zoneName, String attributeName);
	
	public void updateMyZoneAttributes(RId requestId, List<Attribute> attributes);

	public void fetchZoneAttributeNames(RId requestId, String zoneName);
	
	public void fetchZoneNames(RId requestId);
	
	public void fetchMyZoneName(RId requestId);
}
