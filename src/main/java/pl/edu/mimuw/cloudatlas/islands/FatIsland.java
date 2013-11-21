package pl.edu.mimuw.cloudatlas.islands;

import java.util.concurrent.LinkedBlockingDeque;

public class FatIsland implements Island {
	
	private final LinkedBlockingDeque<Runnable> queue = new LinkedBlockingDeque<Runnable>();// should be queue

	@Override
	public Carousel getCarousel() {
		return new Carousel() {

			@Override
			public void enqueue(Runnable runnable) {
				queue.add(runnable);
			}
			
		};
	}

	public void runOnce() throws InterruptedException {
		queue.take().run();
	}
	
	public void runForever() throws InterruptedException {
		while (true) {
			runOnce();
		}
	}
}
