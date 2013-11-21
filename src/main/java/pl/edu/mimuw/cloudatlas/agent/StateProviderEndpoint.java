package pl.edu.mimuw.cloudatlas.agent;

public interface StateProviderEndpoint<RId> {
	
	public void getZoneAttribute(RId requestId, String globalName, String attributeName);

}
