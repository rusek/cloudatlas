package pl.edu.mimuw.cloudatlas.islands;

public class MotherTube extends Tube<ChildEndpoint, MotherEndpoint> implements ChildEndpoint, MotherEndpoint {

	public static MotherTube entangle(ChildIsland childIsland, MotherIsland motherIsland) {
		MotherTube tube = new MotherTube();
		tube.setLeft(childIsland.getCarousel(), childIsland.mountMother(tube));
		tube.setRight(motherIsland.getCarousel(), motherIsland.mountChild(tube));
		return tube;
	}

	@Override
	public void ignite() {
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().ignite();
			}
			
		});
	}

	@Override
	public void extinguish() {
		getLeftCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getLeftEndpoint().extinguish();
			}
			
		});
	}

	@Override
	public void initiateExtinguishing() {
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().initiateExtinguishing();
			}
			
		});
	}

	@Override
	public void childExtinguished() {
		getRightCarousel().enqueue(new Runnable() {

			@Override
			public void run() {
				getRightEndpoint().childExtinguished();
			}
			
		});
	}
}
