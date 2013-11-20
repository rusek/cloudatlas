/*
 * Distributed Systems Lab
 * Copyright (C) Konrad Iwanicki, 2012-2014
 *
 * This file contains code samples for the distributed systems
 * course. It is intended for internal use only.
 */
package pl.edu.mimuw.cloudatlas.cli;

import java.math.BigDecimal;
import java.rmi.Remote;
import java.rmi.RemoteException;

import pl.edu.mimuw.cloudatlas.attributes.Value;

public interface CommandFacade extends Remote {
	public Value getAttributeValue(String zoneName, String attrName) throws RemoteException;
}

