package pl.edu.mimuw.cloudatlas.islands;

import java.util.concurrent.Semaphore;

public class SingleUseCarousel implements Carousel {

	private final Semaphore semaphore = new Semaphore(0);
	volatile Runnable runnable = null;
	
	@Override
	public void enqueue(Runnable runnable) {
		assert runnable != null;
		
		if (this.runnable != null) {
			throw new IllegalStateException("Carousel was already used.");
		}
		this.runnable = runnable;
		semaphore.release();
	}
	
	public void spin() throws InterruptedException {
		semaphore.acquire();
	}

}
