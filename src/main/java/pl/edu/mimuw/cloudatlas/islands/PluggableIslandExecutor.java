package pl.edu.mimuw.cloudatlas.islands;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PluggableIslandExecutor {
	
	private MotherIsland mother;
	private ExecutorService service = Executors.newSingleThreadExecutor();
	
	public PluggableIslandExecutor(MotherIsland mother) {
		this.mother = mother;
	}
	
	public void destroy() {
		this.service.shutdown();
	}
	
	private Carousel serviceCarousel = new Carousel() {

		@Override
		public void enqueue(final Runnable runnable) {
			service.execute(new Runnable() {

				@Override
				public void run() {
					try {
						runnable.run();
					} catch (Exception ex) {
						mother.throwException(ex);
					}
				}
				
			});
		}
		
	};
	
	public void addIsland(PluggableIsland island) {
		island.plugCarousel(serviceCarousel);
	}
}
