package pl.edu.mimuw.cloudatlas.agent;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.cli.CommandFacade;
import pl.edu.mimuw.cloudatlas.islands.ChildEndpoint;
import pl.edu.mimuw.cloudatlas.islands.ChildIsland;
import pl.edu.mimuw.cloudatlas.islands.MotherEndpoint;
import pl.edu.mimuw.cloudatlas.islands.PluggableIsland;
import pl.edu.mimuw.cloudatlas.zones.Attribute;

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
			public void wakeUp() {
				try {
					log.info("Registering command facade in RMI");
					
					if (System.getSecurityManager() == null) {
						System.setSecurityManager(new SecurityManager());
					}
					
					facadeImpl = new CommandFacadeImpl();
					facadeStub = (CommandFacade) UnicastRemoteObject.exportObject(facadeImpl, 0);
					registry = LocateRegistry.getRegistry();
					registry.rebind(CommandFacade.BIND_NAME, facadeStub);
					
					log.info("RMI ready");
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void goToBed() {
				try {
					registry.unbind(CommandFacade.BIND_NAME);
					UnicastRemoteObject.unexportObject(facadeImpl, false);
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				} catch (NotBoundException e) {
					throw new RuntimeException(e);
				}
				
				motherEndpoint.wentToBed();
			}
			
		};
	}

	@Override
	public StateReceiverEndpoint<StateReceiverEndpoint<Void>> mountStateProvider(
			StateProviderEndpoint<StateReceiverEndpoint<Void>> providerEndpoint) {
		this.stateProviderEndpoint = providerEndpoint;
		
		return new StateReceiverEndpoint<StateReceiverEndpoint<Void>>() {

			@Override
			public void zoneAttributeReceived(
					StateReceiverEndpoint<Void> requestId, Attribute attribute) {
				requestId.zoneAttributeReceived(null, attribute);
			}

			@Override
			public void zoneNotFound(StateReceiverEndpoint<Void> requestId) {
				requestId.zoneNotFound(null);
			}
			
		};
	}

	
	private class CommandFacadeImpl implements CommandFacade {

		@Override
		public Value getAttributeValue(String zoneName, String attrName)
				throws RemoteException {
			log.info("Received command getAttributeValue(%s, %s)", zoneName, attrName);
			
			RequestHandler<Value> handler = new RequestHandler<Value>() {

				@Override
				public void zoneAttributeReceived(Void requestId, Attribute attribute) {
					setResult(attribute.getValue());
				}

				@Override
				public void zoneNotFound(Void requestId) {
					setException(new RemoteException("Zone not found"));
				}
				
			};
			stateProviderEndpoint.getZoneAttribute(handler, zoneName, attrName);
			
			return handler.get();
		}

		@Override
		public void shutdown() {
			log.info("Received command shutdown()");
			motherEndpoint.stop();
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
		public void zoneAttributeReceived(Void requestId, Attribute attribute) {
			throw new RuntimeException("Unexpected callback invoked");
		}

		@Override
		public void zoneNotFound(Void requestId) {
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
