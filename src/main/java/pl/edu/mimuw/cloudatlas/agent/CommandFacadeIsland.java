package pl.edu.mimuw.cloudatlas.agent;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.edu.mimuw.cloudatlas.attributes.Type;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.cli.CommandFacade;
import pl.edu.mimuw.cloudatlas.islands.ChildEndpoint;
import pl.edu.mimuw.cloudatlas.islands.ChildIsland;
import pl.edu.mimuw.cloudatlas.islands.MotherEndpoint;
import pl.edu.mimuw.cloudatlas.islands.PluggableIsland;
import pl.edu.mimuw.cloudatlas.zones.Attribute;
import pl.edu.mimuw.cloudatlas.zones.ZoneNames;

public class CommandFacadeIsland extends PluggableIsland implements ChildIsland,
		StateReceiverIsland<StateReceiverEndpoint<Void>> {
	
	private static Logger log = LogManager.getFormatterLogger(CommandFacadeIsland.class);
	
	private MotherEndpoint motherEndpoint;
	private StateProviderEndpoint<StateReceiverEndpoint<Void>> stateProviderEndpoint;

	private CommandFacadeImpl facadeImpl;
	private CommandFacade facadeStub;
	private Registry registry;
	
	@Override
	public ChildEndpoint mountMother(final MotherEndpoint motherEndpoint) {
		this.motherEndpoint = motherEndpoint;
		
		return new ChildEndpoint() {

			@Override
			public void ignite() {
				try {
					log.info("Registering command facade in RMI.");
					
					if (System.getSecurityManager() == null) {
						System.setSecurityManager(new SecurityManager());
					}
					
					facadeImpl = new CommandFacadeImpl();
					facadeStub = (CommandFacade) UnicastRemoteObject.exportObject(facadeImpl, 0);
					registry = LocateRegistry.getRegistry();
					registry.rebind(CommandFacade.BIND_NAME, facadeStub);
					
					log.info("RMI ready.");
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void extinguish() {
				try {
					log.info("Unregistering command facade.");
					
					registry.unbind(CommandFacade.BIND_NAME);
					UnicastRemoteObject.unexportObject(facadeImpl, false);
					
					log.info("Command facade unregistered.");
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				} catch (NotBoundException e) {
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
			public void myZoneAttributeUpdated(
					StateReceiverEndpoint<Void> requestId) {
				requestId.myZoneAttributeUpdated(null);
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
		public void setMyAttribute(String attributeName,
				Type<? extends Value> attributeType, Value attributeValue) throws RemoteException {
			log.info("Received command setMyAttribute(%s, %s, %s)", attributeName, attributeType, attributeValue);
			
			if (attributeName == null) {
				throw new RemoteException("Attribute name is null");
			}
			if (attributeType == null) {
				throw new RemoteException("Attribute type is null");
			}
			if (attributeValue != null && !attributeValue.getType().equals(attributeType)) {
				throw new RemoteException("Attribute value has invalid type");
			}
			
			RequestHandler<Void> handler = new RequestHandler<Void>() {
				
				@Override
				public void myZoneAttributeUpdated(Void requestId) {
					setResult(null);
				}
			};
			stateProviderEndpoint.updateMyZoneAttribute(handler, attributeName, attributeType, attributeValue);
			
			handler.get();
		}
		
	}
	
	private static class RequestHandler<R> implements StateReceiverEndpoint<Void> {
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
		
		@Override
		public void zoneAttributeFetched(Void requestId, Attribute attribute) {
			throw new RuntimeException("Unexpected callback invoked");
		}

		@Override
		public void zoneNotFound(Void requestId) {
			throw new RuntimeException("Unexpected callback invoked");
		}

		@Override
		public void myZoneAttributeUpdated(Void requestId) {
			throw new RuntimeException("Unexpected callback invoked");
			
		}

		@Override
		public void zoneNamesFetched(Void requestId,
				Collection<String> zoneNames) {
			throw new RuntimeException("Unexpected callback invoked");
		}

		@Override
		public void zoneAttributeNamesFetched(Void requestId,
				Collection<String> attributeNames) {
			throw new RuntimeException("Unexpected callback invoked");
		}

		@Override
		public void myZoneNameFetched(Void requestId, String zoneName) {
			throw new RuntimeException("Unexpected callback invoked");
		}

		@Override
		public void attributeNotFound(Void requestId) {
			throw new RuntimeException("Unexpected callback invoked");
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
