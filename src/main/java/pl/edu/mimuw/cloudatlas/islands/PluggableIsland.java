package pl.edu.mimuw.cloudatlas.islands;

public abstract class PluggableIsland implements Island {

	private final Carousel carouselProxy = new Carousel() {

		@Override
		public void enqueue(Runnable runnable) {
			carousel.enqueue(runnable);
		}
		
	};
	
	private Carousel carousel = BrokenCarousel.INSTANCE;
	
	@Override
	public Carousel getCarousel() {
		return carouselProxy;
	}
	
	void plugCarousel(Carousel carousel) {
		this.carousel = carousel;
	}
}
