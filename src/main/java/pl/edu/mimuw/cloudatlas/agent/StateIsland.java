package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.islands.PluggableIsland;
import pl.edu.mimuw.cloudatlas.zones.Zone;

public class StateIsland extends PluggableIsland implements StateProviderIsland {
	
	private Zone rootZone;
	private Zone myZone;
	
	public StateIsland() {
		String myName = "/uw/violet07";
		
		rootZone = Zone.createRootWithOwner(myName);
		
		Zone uwZone = rootZone.addChildWithOwner("uw", myName);
		
		myZone = uwZone.addChildWithOwner("violet07", myName);
		myZone.getZMI().setAttribute("cardinality", new IntegerValue(1));
	}

	@Override
	public <RId> StateProviderEndpoint<RId> mountStateReceiver(final StateReceiverEndpoint<RId> receiverEndpoint) {
		return new StateProviderEndpoint<RId>() {

			@Override
			public void getZoneAttribute(RId requestId, String globalName,
					String attributeName) {
				Zone requestedZone = rootZone.findZone(globalName);
				if (requestedZone == null) {
					receiverEndpoint.zoneNotFound(requestId);
				} else {
					receiverEndpoint.zoneAttributeReceived(requestId,
							requestedZone.getZMI().getAttribute(attributeName));
				}
				
			}
			
		};
	}
}
