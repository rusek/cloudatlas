package pl.edu.mimuw.cloudatlas.agent;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.cli.CommandFacade;
import pl.edu.mimuw.cloudatlas.islands.ChildEndpoint;
import pl.edu.mimuw.cloudatlas.islands.ChildIsland;
import pl.edu.mimuw.cloudatlas.islands.MotherEndpoint;
import pl.edu.mimuw.cloudatlas.islands.PluggableIsland;
import pl.edu.mimuw.cloudatlas.query.ParseException;
import pl.edu.mimuw.cloudatlas.query.Parsers;
import pl.edu.mimuw.cloudatlas.zones.Attribute;
import pl.edu.mimuw.cloudatlas.zones.AttributeNames;
import pl.edu.mimuw.cloudatlas.zones.ZoneNames;

public class CommandFacadeIsland extends PluggableIsland implements ChildIsland,
		StateReceiverIsland<StateReceiverEndpoint<Void>> {
	
	private static Logger log = LogManager.getFormatterLogger(CommandFacadeIsland.class);
	
	private final String zoneName;
	
	private MotherEndpoint motherEndpoint;
	private StateProviderEndpoint<StateReceiverEndpoint<Void>> stateProviderEndpoint;

	private CommandFacadeImpl facadeImpl;
	private CommandFacade facadeStub;
	private Registry registry;

	public CommandFacadeIsland(String zoneName) {
		this.zoneName = zoneName;
	}
	
	@Override
	public ChildEndpoint mountMother(final MotherEndpoint motherEndpoint) {
		this.motherEndpoint = motherEndpoint;
		
		return new ChildEndpoint() {

			@Override
			public void ignite() {
				try {
					log.info("Registering command facade in RMI: %s", CommandFacade.BIND_NAME + ":" + zoneName);
					
					if (System.getSecurityManager() == null) {
						System.setSecurityManager(new SecurityManager());
					}
					
					facadeImpl = new CommandFacadeImpl();
					facadeStub = (CommandFacade) UnicastRemoteObject.exportObject(facadeImpl, 0);
					registry = LocateRegistry.getRegistry();
					registry.rebind(CommandFacade.BIND_NAME, facadeStub);
					registry.rebind(CommandFacade.BIND_NAME + ":" + zoneName, facadeStub);
					
					log.info("RMI ready.");
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void extinguish() {
				try {
					log.info("Unregistering command facade.");
					// FIXME deadlock if waiting for feedback from state island
					try {
						registry.unbind(CommandFacade.BIND_NAME);
					} catch (NotBoundException e) { }
					try {
						registry.unbind(CommandFacade.BIND_NAME + ":" + zoneName);
					} catch (NotBoundException e) { }
					UnicastRemoteObject.unexportObject(facadeImpl, false);
					
					log.info("Command facade unregistered.");
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}
				
				motherEndpoint.childExtinguished();
			}
			
		};
	}

	@Override
	public StateReceiverEndpoint<StateReceiverEndpoint<Void>> mountStateProvider(
			StateProviderEndpoint<StateReceiverEndpoint<Void>> providerEndpoint) {
		this.stateProviderEndpoint = providerEndpoint;
		
		return new StateReceiverEndpoint<StateReceiverEndpoint<Void>>() {

			@Override
			public void zoneAttributeFetched(
					StateReceiverEndpoint<Void> requestId, Attribute attribute) {
				requestId.zoneAttributeFetched(null, attribute);
			}

			@Override
			public void zoneNotFound(StateReceiverEndpoint<Void> requestId) {
				requestId.zoneNotFound(null);
			}

			@Override
			public void myZoneAttributesUpdated(
					StateReceiverEndpoint<Void> requestId) {
				requestId.myZoneAttributesUpdated(null);
			}

			@Override
			public void zoneNamesFetched(StateReceiverEndpoint<Void> requestId,
					Collection<String> zoneNames) {
				requestId.zoneNamesFetched(null, zoneNames);
			}

			@Override
			public void zoneAttributeNamesFetched(
					StateReceiverEndpoint<Void> requestId,
					Collection<String> attributeNames) {
				requestId.zoneAttributeNamesFetched(null, attributeNames);
			}

			@Override
			public void myZoneNameFetched(
					StateReceiverEndpoint<Void> requestId, String zoneName) {
				requestId.myZoneNameFetched(null, zoneName);
			}

			@Override
			public void attributeNotFound(StateReceiverEndpoint<Void> requestId) {
				requestId.attributeNotFound(null);
			}

			@Override
			public void queryInstalled(StateReceiverEndpoint<Void> requestId) {
				requestId.queryInstalled(null);
				
			}

			@Override
			public void queryUninstalled(StateReceiverEndpoint<Void> requestId) {
				requestId.queryUninstalled(null);
			}

			@Override
			public void zoneAttributesFetched(
					StateReceiverEndpoint<Void> requestId,
					Collection<Attribute> attributes) {
				requestId.zoneAttributesFetched(null, attributes);
			}
			
		};
	}

	
	private class CommandFacadeImpl implements CommandFacade {

		@Override
		public Value getAttributeValue(String zoneName, String attrName)
				throws RemoteException {
			log.info("Received command getAttributeValue(%s, %s)", zoneName, attrName);
			
			if (zoneName == null) {
				throw new RemoteException("Zone name is null");
			}
			if (!ZoneNames.isGlobalName(zoneName)) {
				throw new RemoteException("Invalid zone name: " + zoneName);
			}
			if (attrName == null) {
				throw new RemoteException("Attribute name is null");
			}
			
			RequestHandler<Value> handler = new RequestHandler<Value>() {

				@Override
				public void zoneAttributeFetched(Void requestId, Attribute attribute) {
					setResult(attribute.getValue());
				}

				@Override
				public void zoneNotFound(Void requestId) {
					setException(new RemoteException("Zone not found"));
				}
				
				@Override
				public void attributeNotFound(Void requestId) {
					setException(new RemoteException("Attribute not found"));
				}
				
			};
			stateProviderEndpoint.fetchZoneAttribute(handler, zoneName, attrName);
			
			return handler.get();
		}

		@Override
		public void extinguish() {
			log.info("Received command extinguish()");
			
			motherEndpoint.initiateExtinguishing();
		}

		@Override
		public String getMyGlobalName() throws RemoteException {
			log.info("Received command getMyGlobalName()");
			
			RequestHandler<String> handler = new RequestHandler<String>() {
				
				@Override
				public void myZoneNameFetched(Void requestId, String zoneName) {
					setResult(zoneName);
				}
				
			};
			stateProviderEndpoint.fetchMyZoneName(handler);
			
			return handler.get();
		}

		@Override
		public void setMyAttributes(List<Attribute> attributes) throws RemoteException {
			log.info("Received command setMyAttributes(%s)", attributes);
			
			if (attributes == null) {
				throw new RemoteException("Attribute list is null");
			}
			for (Attribute attribute : attributes) {
				if (attribute == null) {
					throw new RemoteException("One of attributes is null");
				}
			}
			
			RequestHandler<Void> handler = new RequestHandler<Void>() {
				
				@Override
				public void myZoneAttributesUpdated(Void requestId) {
					setResult(null);
				}
			};
			stateProviderEndpoint.updateMyZoneAttributes(handler, attributes);
			
			handler.get();
		}

		@Override
		public void setFallbackContacts(List<ContactValue> contacts)
				throws RemoteException {
			log.info("Received command setFallbackContacts(%s)", contacts);
			stateProviderEndpoint.updateFallbackContacts(contacts);
		}
		
		private void validateQuery(String query) throws RemoteException {
			if (query == null) {
				throw new RemoteException("Query is null");
			}
			try {
				Parsers.parseQuery(query);
			} catch (ParseException e) {
				throw new RemoteException(e.getMessage());
			}
		}

		@Override
		public void installQuery(String attributeName, String query)
				throws RemoteException {
			log.info("Received command installQuery(%s, %s)", attributeName, query);
			if (attributeName == null || !AttributeNames.isSpecialName(attributeName)) {
				throw new RemoteException("Invalid attribute name: " + attributeName);
			}
			validateQuery(query);
			
			RequestHandler<Void> handler = new RequestHandler<Void>() {

				@Override
				public void queryInstalled(Void requestId) {
					setResult(null);
				}
				
			};
			
			stateProviderEndpoint.installQuery(handler, attributeName, null, query);
			
			handler.get();
		}

		@Override
		public void installQueryAt(String zoneName, String attributeName,
				String query) throws RemoteException {
			log.info("Received command installQueryAt(%s, %s, %s)", attributeName, zoneName, query);
			if (attributeName == null || !AttributeNames.isSpecialName(attributeName)) {
				throw new RemoteException("Invalid attribute name: " + attributeName);
			}
			if (zoneName == null || !ZoneNames.isGlobalName(zoneName)) {
				throw new RemoteException("Invalid zone name: " + zoneName);
			}
			validateQuery(query);
			
			RequestHandler<Void> handler = new RequestHandler<Void>() {

				@Override
				public void queryInstalled(Void requestId) {
					setResult(null);
				}
				
				@Override
				public void zoneNotFound(Void requestId) {
					setException(new RemoteException("Zone not found"));
				}
				
			};
			
			stateProviderEndpoint.installQuery(handler, attributeName, zoneName, query);
			
			handler.get();
		}

		@Override
		public void uninstallQuery(String attributeName) throws RemoteException {
			log.info("Received command uninstallQuery(%s)", attributeName);
			if (attributeName == null || !AttributeNames.isSpecialName(attributeName)) {
				throw new RemoteException("Invalid attribute name: " + attributeName);
			}
			
			RequestHandler<Void> handler = new RequestHandler<Void>() {

				@Override
				public void queryUninstalled(Void requestId) {
					setResult(null);
				}
				
			};
			
			stateProviderEndpoint.uninstallQuery(handler, attributeName, null);
			
			handler.get();
		}

		@Override
		public void uninstallQueryAt(String zoneName, String attributeName)
				throws RemoteException {
			log.info("Received command uninstallQueryAt(%s, %s)", zoneName, attributeName);
			if (attributeName == null || !AttributeNames.isSpecialName(attributeName)) {
				throw new RemoteException("Invalid attribute name: " + attributeName);
			}
			if (zoneName == null || !ZoneNames.isGlobalName(zoneName)) {
				throw new RemoteException("Invalid zone name: " + zoneName);
			}
			
			RequestHandler<Void> handler = new RequestHandler<Void>() {

				@Override
				public void queryUninstalled(Void requestId) {
					setResult(null);
				}
				
				@Override
				public void zoneNotFound(Void requestId) {
					setException(new RemoteException("Zone not found"));
				}
				
			};
			
			stateProviderEndpoint.uninstallQuery(handler, attributeName, zoneName);
			
			handler.get();
		}

		@Override
		public List<String> getZoneNames() throws RemoteException {
			log.info("Received command getZoneNames()");
			
			RequestHandler<List<String>> handler = new RequestHandler<List<String>>() {

				@Override
				public void zoneNamesFetched(Void requestId,
						Collection<String> zoneNames) {
					List<String> zoneNameList = new ArrayList<String>();
					zoneNameList.addAll(zoneNames);
					setResult(zoneNameList);
				}

			};
			
			stateProviderEndpoint.fetchZoneNames(handler);
			
			return handler.get();
		}

		@Override
		public List<Attribute> getAttributes(String zoneName)
				throws RemoteException {
			log.info("Received command getAttributes(%s)", zoneName);
			if (zoneName == null || !ZoneNames.isGlobalName(zoneName)) {
				throw new RemoteException("Invalid zone name: " + zoneName);
			}
			
			RequestHandler<List<Attribute>> handler = new RequestHandler<List<Attribute>>() {

				@Override
				public void zoneNotFound(Void requestId) {
					setException(new RemoteException("Zone not found"));
				}

				@Override
				public void zoneAttributesFetched(Void requestId,
						Collection<Attribute> attributes) {
					List<Attribute> attributeList = new ArrayList<Attribute>();
					attributeList.addAll(attributes);
					setResult(attributeList);
				}
			};
			
			stateProviderEndpoint.fetchZoneAttributes(handler, zoneName);
			
			return handler.get();
		}
		
	}
	
	private static class RequestHandler<R> extends StateReceiverAdapter<Void> {
		private final Semaphore semaphore = new Semaphore(0);
		private R result;
		private RemoteException exception;
		
		protected void setResult(R result) {
			this.result = result;
			this.semaphore.release();
		}
		
		protected void setException(RemoteException exception) {
			this.exception = exception;
			this.semaphore.release();
		}
		
		public R get() throws RemoteException {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				throw new RemoteException("Interrupted");
			}
			if (exception != null) {
				throw exception;
			} else {
				return result;
			}
		}
	}
}
