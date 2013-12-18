package pl.edu.mimuw.cloudatlas.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.TimeValue;
import pl.edu.mimuw.cloudatlas.attributes.ValueFormatException;
import pl.edu.mimuw.cloudatlas.islands.PluggableIsland;
import pl.edu.mimuw.cloudatlas.zones.Attribute;
import pl.edu.mimuw.cloudatlas.zones.Zone;
import pl.edu.mimuw.cloudatlas.zones.ZoneNames;

public class StateIsland extends PluggableIsland implements StateProviderIsland {
	
	private Random random = new Random();
	private Zone rootZone;
	private Zone myZone;
	private List<ContactValue> fallbackContacts = new ArrayList<ContactValue>();
	
	public StateIsland(String zoneName, Properties properties) {
		rootZone = Zone.createRootWithOwner(zoneName);
		
		Zone zone = rootZone;
		for (String localName : ZoneNames.splitGlobalName(zoneName)) {
			zone = zone.addChildWithOwner(localName, zoneName);
		}
		
		myZone = zone;
		myZone.getZMI().setAttribute("cardinality", new IntegerValue(1));
		
		initFallbackContacts(properties.getProperty("fallbackContacts"));
	}
	
	private void initFallbackContacts(String contacts) {
		if (contacts == null) {
			return;
		}
		for (String contact : StringUtils.trim(contacts).split(",\\s*")) {
			try {
				fallbackContacts.add(ContactValue.parseContact(contact));
			} catch (ValueFormatException e) {
				throw new IllegalArgumentException(e);
			}
		}
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
			public void updateMyZoneAttributes(RId requestId, List<Attribute> attributes) {
				for (Attribute attribute : attributes) {
					myZone.getZMI().setAttribute(attribute.getName(), attribute.getType(), attribute.getValue());
				}
				myZone.getZMI().setAttribute("timestamp", TimeValue.now());
				
				receiverEndpoint.myZoneAttributesUpdated(requestId);
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

			@Override
			public void updateFallbackContacts(
					Collection<ContactValue> fallbackContacts) {
				StateIsland.this.fallbackContacts.clear();
				StateIsland.this.fallbackContacts.addAll(fallbackContacts);
			}

			@Override
			public void getContactForGossiping(RId requestId) {
				ContactValue contact = null;
				if (!fallbackContacts.isEmpty()) {
					contact = fallbackContacts.get(random.nextInt(fallbackContacts.size()));
				}
				receiverEndpoint.contactForGossipingReceived(requestId, contact);
				
			}
			
		};
	}
}
