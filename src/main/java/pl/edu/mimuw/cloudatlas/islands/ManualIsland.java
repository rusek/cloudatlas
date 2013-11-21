package pl.edu.mimuw.cloudatlas.islands;

import java.util.concurrent.LinkedBlockingQueue;

// Island which carousel must be manually spinned
public class ManualIsland implements Island {
	
	private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

	private final Carousel carousel = new Carousel() {

		@Override
		public void enqueue(Runnable runnable) {
			queue.add(runnable);
		}
		
	};
	
	@Override
	public Carousel getCarousel() {
		return carousel;
	}

	public void spinCarouselOnce() throws InterruptedException {
		queue.take().run();
	}
	
	public void spinCarouselForever() throws InterruptedException {
		while (true) {
			spinCarouselOnce();
		}
	}
	
	public void spinCarouselUntilInterrupted() {
		try {
			spinCarouselForever();
		} catch (InterruptedException ex) {
			// Just return
		}
	}
}
