package pl.edu.mimuw.cloudatlas.islands;

public class SynchronousCarousel implements Carousel {
	
	private SynchronousCarousel() {}

	@Override
	public void enqueue(Runnable runnable) {
		runnable.run();
	}

	public static final SynchronousCarousel INSTANCE = new SynchronousCarousel();
}
