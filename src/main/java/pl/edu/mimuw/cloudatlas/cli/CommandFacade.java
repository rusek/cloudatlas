package pl.edu.mimuw.cloudatlas.cli;

import java.rmi.Remote;
import java.rmi.RemoteException;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;

public interface CommandFacade extends Remote {
	public static final String BIND_NAME = "CommandFacade";
	
	public Value getAttributeValue(String zoneName, String attrName) throws RemoteException;
	
	public void setMyAttribute(String attrName, Type<? extends Value> attrType, Value attrValue) throws RemoteException;
	
	public String getMyGlobalName() throws RemoteException;
	
	public void extinguish() throws RemoteException;
}

