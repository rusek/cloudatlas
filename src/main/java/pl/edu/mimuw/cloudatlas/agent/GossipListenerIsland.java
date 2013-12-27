package pl.edu.mimuw.cloudatlas.agent;

import pl.edu.mimuw.cloudatlas.islands.Island;

public interface GossipListenerIsland extends Island {

	public <RId> GossipListenerEndpoint<RId> mountGossipTransmitter(GossipTransmitterEndpoint<RId> transmitterEndpoint);
}
