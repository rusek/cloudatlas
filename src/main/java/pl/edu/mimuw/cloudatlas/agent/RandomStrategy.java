package pl.edu.mimuw.cloudatlas.agent;

import java.util.Random;

public class RandomStrategy extends LevelSelectionStrategy {
	
	private final Random random = new Random();

	public RandomStrategy(String zoneName) {
		super(zoneName);
	}
	
	@Override
	public String nextLevel() {
		return get(random.nextInt(size()));
	}

}
