package pl.edu.mimuw.cloudatlas.islands;

public class SynchronizedIsland implements Island {
	
	@Override
	public Carousel getCarousel() {
		return SynchronousCarousel.INSTANCE;
	}
}
