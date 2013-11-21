package pl.edu.mimuw.cloudatlas.islands;

public class MotherTube extends Tube<ChildEndpoint, MotherEndpoint> implements ChildEndpoint, MotherEndpoint {

	public static MotherTube entangle(ChildIsland childIsland, MotherIsland motherIsland) {
		MotherTube tube = new MotherTube();
		tube.setLeft(childIsland.getCarousel(), childIsland.mountMother(tube));
		tube.setRight(motherIsland.getCarousel(), motherIsland.mountChild(tube));
		return tube;
	}

	@Override
	public void wakeUp() {
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().wakeUp();
			}
			
		});
	}

	@Override
	public void goToBed() {
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().goToBed();
			}
			
		});
	}

	@Override
	public void stop() {
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().stop();
			}
			
		});
	}

	@Override
	public void wentToBed() {
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().wentToBed();
			}
			
		});
	}
}
