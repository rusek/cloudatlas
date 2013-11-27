package pl.edu.mimuw.cloudatlas.islands;

import java.util.concurrent.LinkedBlockingQueue;

// TODO probably should be deleted, doesn't work with DatagramSocket.receive in idle()
public abstract class IdleIsland implements Island {
	
	private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	private volatile boolean destroyed = false;
	
	private final Thread thread = new Thread() {
		
		@Override
		public void run() {
			while (true) {
				if (destroyed) {
					return;
				}
				
				Runnable task = queue.poll();
				if (task == null) {
					try {
						idle();
					} catch (InterruptedException e) {
						// Some work arrived or destroy() was called
					}
				} else {
					task.run();
				}
			}
		}
	};
	
	private final Carousel carousel = new Carousel() {

		@Override
		public void enqueue(Runnable runnable) {
			queue.add(runnable);
			thread.interrupt();
		}
		
	};
	
	public IdleIsland() {
		thread.start();
	}
	
	protected void idle() throws InterruptedException {
		queue.take().run();
	}
	
	public void destroy() {
		destroyed = true;
		thread.interrupt();
		
		boolean interrupted = false;
		boolean done = false;
		while (!done) {
			try {
				thread.join();
				done = true;
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}
		if (interrupted) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public Carousel getCarousel() {
		return carousel;
	}
}
