package pl.edu.mimuw.cloudatlas.islands;

public class BrokenCarousel implements Carousel {
	
	private BrokenCarousel() {}

	@Override
	public void enqueue(Runnable runnable) {
		throw new BrokenCarouselException("This carousel is clearly broken. You shouldn't have played with it.");
	}

	public static final BrokenCarousel INSTANCE = new BrokenCarousel();
}
