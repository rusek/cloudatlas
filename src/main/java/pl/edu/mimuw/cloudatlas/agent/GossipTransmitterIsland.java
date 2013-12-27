package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.islands.Island;

public interface GossipTransmitterIsland<RId> extends Island {

	public GossipTransmitterEndpoint<RId> mountGossipListener(GossipListenerEndpoint<RId> gossipListener);
}
