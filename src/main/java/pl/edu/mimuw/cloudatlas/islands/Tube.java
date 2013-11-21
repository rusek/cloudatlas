package pl.edu.mimuw.cloudatlas.islands;

public abstract class Tube<L, R> {
	
	private Carousel leftCarousel = BrokenCarousel.INSTANCE;
	private L leftEndpoint;

	private Carousel rightCarousel = BrokenCarousel.INSTANCE;
	private R rightEndpoint;
	
	protected void setLeft(Carousel leftCarousel, L leftEndpoint) {
		assert leftCarousel != null;
		assert leftEndpoint != null;
		assert leftCarousel != SynchronousCarousel.INSTANCE || rightCarousel != SynchronousCarousel.INSTANCE;
		
		this.leftCarousel = leftCarousel;
		this.leftEndpoint = leftEndpoint;
	}
	
	protected void setRight(Carousel rightCarousel, R rightEndpoint) {
		assert rightCarousel != null;
		assert rightEndpoint != null;
		assert leftCarousel != SynchronousCarousel.INSTANCE || rightCarousel != SynchronousCarousel.INSTANCE;
		
		this.rightCarousel = rightCarousel;
		this.rightEndpoint = rightEndpoint;
	}

	protected Carousel getLeftCarousel() {
		return leftCarousel;
	}

	protected L getLeftEndpoint() {
		return leftEndpoint;
	}

	protected Carousel getRightCarousel() {
		return rightCarousel;
	}

	protected R getRightEndpoint() {
		return rightEndpoint;
	}

}
