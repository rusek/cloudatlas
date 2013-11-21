package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.islands.Island;

public interface StateReceiverIsland<RId> extends Island {

	public StateReceiverEndpoint<RId> mountStateProvider(StateProviderEndpoint<RId> providerEndpoint);
}
