package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.islands.Tube;
import pl.edu.mimuw.cloudatlas.zones.Attribute;

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
	public void getZoneAttribute(final RId requestId, final String globalName,
			final String attributeName) {
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().getZoneAttribute(requestId, globalName, attributeName);
			}
			
		});
	}

	@Override
	public void zoneAttributeReceived(final RId requestId, Attribute attribute) {
		final Attribute copiedAttribute = attribute == null ? null : attribute.deepCopy();
		
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().zoneAttributeReceived(requestId, copiedAttribute);
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
}
