package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.islands.Island;

public interface StateProviderIsland extends Island {

	public <RId> StateProviderEndpoint<RId> mountStateReceiver(StateReceiverEndpoint<RId> receiverEndpoint);
}
