package pl.edu.mimuw.cloudatlas.cli;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.zones.Attribute;

public interface CommandFacade extends Remote {
	public static final String BIND_NAME = "CommandFacade";
	
	public Value getAttributeValue(String zoneName, String attrName) throws RemoteException;
	
	public List<Attribute> getAttributes(String zoneName) throws RemoteException;
	
	public void setMyAttributes(List<Attribute> attributes) throws RemoteException;
	
	public List<String> getZoneNames() throws RemoteException;
	
	public String getMyGlobalName() throws RemoteException;
	
	public void setFallbackContacts(List<ContactValue> contacts) throws RemoteException;
	
	public void extinguish() throws RemoteException;
	
	public void installQuery(String attributeName, String query) throws RemoteException;
	
	public void installQueryAt(String zoneName, String attributeName, String query) throws RemoteException;
	
	public void uninstallQuery(String attributeName) throws RemoteException;
	
	public void uninstallQueryAt(String zoneName, String attributeName) throws RemoteException;
}

