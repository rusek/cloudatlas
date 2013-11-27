package pl.edu.mimuw.cloudatlas.agent;

import java.util.ArrayList;
import java.util.List;

import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.islands.PluggableIsland;
import pl.edu.mimuw.cloudatlas.zones.Attribute;
import pl.edu.mimuw.cloudatlas.zones.Zone;
import pl.edu.mimuw.cloudatlas.zones.ZoneNames;

public class StateIsland extends PluggableIsland implements StateProviderIsland {
	
	private Zone rootZone;
	private Zone myZone;
	
	public StateIsland(String zoneName) {
		rootZone = Zone.createRootWithOwner(zoneName);
		
		Zone zone = rootZone;
		for (String localName : ZoneNames.splitGlobalName(zoneName)) {
			zone = zone.addChildWithOwner(localName, zoneName);
		}
		
		myZone = zone;
		myZone.getZMI().setAttribute("cardinality", new IntegerValue(1));
	}

	@Override
	public <RId> StateProviderEndpoint<RId> mountStateReceiver(final StateReceiverEndpoint<RId> receiverEndpoint) {
		return new StateProviderEndpoint<RId>() {

			@Override
			public void fetchZoneAttribute(RId requestId, String zoneName,
					String attributeName) {
				Zone requestedZone = rootZone.findZone(zoneName);
				if (requestedZone == null) {
					receiverEndpoint.zoneNotFound(requestId);
				} else {
					Attribute attribute = requestedZone.getZMI().getAttribute(attributeName);
					if (attribute == null) {
						receiverEndpoint.attributeNotFound(requestId);
					} else {	
						receiverEndpoint.zoneAttributeFetched(requestId, attribute);
					}
				}
			}

			@Override
			public void updateMyZoneAttribute(RId requestId, 
					String attributeName, Type<? extends Value> attributeType,
					Value attributeValue) {
				myZone.getZMI().setAttribute(attributeName, attributeType, attributeValue);
				
				receiverEndpoint.myZoneAttributeUpdated(requestId);
			}

			@Override
			public void fetchZoneAttributeNames(RId requestId, String zoneName) {
				Zone requestedZone = rootZone.findZone(zoneName);
				if (requestedZone == null) {
					receiverEndpoint.zoneNotFound(requestId);
				} else {
					receiverEndpoint.zoneAttributeNamesFetched(requestId, requestedZone.getZMI().getAttributeNames());
				}
			}

			@Override
			public void fetchZoneNames(RId requestId) {
				List<String> zoneNames = new ArrayList<String>();
				addZoneNames(zoneNames, rootZone);
				
				receiverEndpoint.zoneNamesFetched(requestId, zoneNames);
			}
			
			private void addZoneNames(List<String> zoneNames, Zone zone) {
				zoneNames.add(zone.getGlobalName());
				for (Zone childZone : zone.getChildren()) {
					addZoneNames(zoneNames, childZone);
				}
			}

			@Override
			public void fetchMyZoneName(RId requestId) {
				receiverEndpoint.myZoneNameFetched(requestId, myZone.getGlobalName());
			}
			
		};
	}
}
