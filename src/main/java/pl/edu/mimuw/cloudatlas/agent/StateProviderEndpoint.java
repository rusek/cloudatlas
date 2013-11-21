package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public interface StateProviderEndpoint<RId> {
	
	public void fetchZoneAttribute(RId requestId, String zoneName, String attributeName);
	
	public void updateMyZoneAttribute(RId requestId, String attributeName,
			Type<? extends Value> attributeType, Value attributeValue);

	public void fetchZoneAttributeNames(RId requestId, String zoneName);
	
	public void fetchZoneNames(RId requestId);
	
	public void fetchMyZoneName(RId requestId);
}
