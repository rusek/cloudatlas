package pl.edu.mimuw.cloudatlas.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.islands.Tube;
import pl.edu.mimuw.cloudatlas.zones.Attribute;
import pl.edu.mimuw.cloudatlas.zones.AttributeNames;
import pl.edu.mimuw.cloudatlas.zones.ZoneNames;

public class StateTube<RId> extends Tube<StateReceiverEndpoint<RId>, StateProviderEndpoint<RId>> implements
		StateReceiverEndpoint<RId>, StateProviderEndpoint<RId> {
	
	public static <RId> StateTube<RId> entangle(StateReceiverIsland<RId> stateReceiver,
			StateProviderIsland stateProvider) {
		StateTube<RId> tube = new StateTube<RId>();
		tube.setLeft(stateReceiver.getCarousel(), stateReceiver.mountStateProvider(tube));
		tube.setRight(stateProvider.getCarousel(), stateProvider.mountStateReceiver(tube));
		
		return tube;
	}

	@Override
	public void fetchZoneAttribute(final RId requestId, final String globalName,
			final String attributeName) {
		assert globalName != null;
		assert attributeName != null;
		
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().fetchZoneAttribute(requestId, globalName, attributeName);
			}
			
		});
	}
	
	@Override
	public void updateMyZoneAttributes(final RId requestId, List<Attribute> attributes) {
		assert attributes != null;
		
		final List<Attribute> attributesCopy = new ArrayList<Attribute>();
		for (Attribute attribute : attributes) {
			attributesCopy.add(attribute.deepCopy());
		}
		
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().updateMyZoneAttributes(requestId, attributesCopy);
			}
			
		});
		
	}

	@Override
	public void fetchZoneAttributeNames(final RId requestId, final String zoneName) {
		assert zoneName != null;
		
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().fetchZoneAttributeNames(requestId, zoneName);
			}
			
		});
	}

	@Override
	public void fetchZoneNames(final RId requestId) {
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().fetchZoneNames(requestId);
			}
			
		});
		
	}

	@Override
	public void fetchMyZoneName(final RId requestId) {
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().fetchMyZoneName(requestId);
			}
			
		});
	}

	@Override
	public void updateFallbackContacts(Collection<ContactValue> fallbackContacts) {
		final Collection<ContactValue> fallbackContactsCopy = new ArrayList<ContactValue>();
		fallbackContactsCopy.addAll(fallbackContacts);
		
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().updateFallbackContacts(fallbackContactsCopy);
			}
			
		});
	}
	
	@Override
	public void installQuery(final RId requestId, final String attributeName,
			final String zoneName, final String query) {
		assert AttributeNames.isSpecialName(attributeName);
		assert zoneName == null || ZoneNames.isGlobalName(zoneName);
		assert query != null;
		
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().installQuery(requestId, attributeName, zoneName, query);
			}
			
		});
	}

	@Override
	public void uninstallQuery(final RId requestId, final String attributeName,
			final String zoneName) {
		assert AttributeNames.isSpecialName(attributeName);
		assert zoneName == null || ZoneNames.isGlobalName(zoneName);
		
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().uninstallQuery(requestId, attributeName, zoneName);
			}
			
		});
	}

	@Override
	public void fetchZoneAttributes(final RId requestId, final String zoneName) {
		assert ZoneNames.isGlobalName(zoneName);
		
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().fetchZoneAttributes(requestId, zoneName);
			}
			
		});
		
	}

	@Override
	public void zoneAttributeFetched(final RId requestId, Attribute attribute) {
		assert attribute != null;
		
		final Attribute copiedAttribute = attribute.deepCopy();
		
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().zoneAttributeFetched(requestId, copiedAttribute);
			}
			
		});
		
	}

	@Override
	public void zoneNotFound(final RId requestId) {
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().zoneNotFound(requestId);
			}
			
		});
		
	}

	@Override
	public void myZoneAttributesUpdated(final RId requestId) {
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().myZoneAttributesUpdated(requestId);
			}
			
		});
	}

	@Override
	public void zoneNamesFetched(final RId requestId, Collection<String> zoneNames) {
		assert zoneNames != null;
		
		final List<String> zoneNamesCopy = new ArrayList<String>();
		zoneNamesCopy.addAll(zoneNames);
		
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().zoneNamesFetched(requestId, zoneNamesCopy);
			}
			
		});
	}

	@Override
	public void zoneAttributeNamesFetched(final RId requestId,
			Collection<String> attributeNames) {
		assert attributeNames != null;
		
		final List<String> attributeNamesCopy = new ArrayList<String>();
		attributeNamesCopy.addAll(attributeNames);
		
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().zoneAttributeNamesFetched(requestId, attributeNamesCopy);
			}
			
		});
	}

	@Override
	public void myZoneNameFetched(final RId requestId, final String zoneName) {
		assert zoneName != null;
		
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().myZoneNameFetched(requestId, zoneName);
			}
			
		});
	}

	@Override
	public void attributeNotFound(final RId requestId) {
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().attributeNotFound(requestId);
			}
			
		});
	}

	@Override
	public void queryInstalled(final RId requestId) {
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().queryInstalled(requestId);
			}
			
		});
	}

	@Override
	public void queryUninstalled(final RId requestId) {
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().queryUninstalled(requestId);
			}
			
		});
		
	}

	@Override
	public void zoneAttributesFetched(final RId requestId,
			Collection<Attribute> attributes) {
		final List<Attribute> attributesCopy = new ArrayList<Attribute>();
		for (Attribute attribute : attributes) {
			attributesCopy.add(attribute.deepCopy());
		}
		
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().zoneAttributesFetched(requestId, attributesCopy);
			}
			
		});
	}
}
