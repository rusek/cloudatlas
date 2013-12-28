package pl.edu.mimuw.cloudatlas.agent;

public class WeightedRoundRobinStrategy extends LevelSelectionStrategy {
	
	private int nextIndex = 0;
	
	// penalties[i] = 1.0 / w[i] - 1.0, where w[i] is i-th element normalized (from range [0.0, 1.0]) weight
	private final double[] penalties;
	
	private final double[] accumulatedPenalties;

	public WeightedRoundRobinStrategy(String zoneName, double exponent) {
		super(zoneName);

		int size = size();
		double[] weights = new double[size];
		double max = 1.0; // computed in case someone passes exponent < 1.0
		
		weights[0] = 1.0;
		for (int i = 1; i < size; i++) {
			weights[i] = weights[i - 1] / exponent;
			max = Math.max(max, weights[i]);
		}
		
		// Replace weights with corresponding penalties
		for (int i = 0; i < size; i++) {
			weights[i] = Math.max(0.0, max / weights[i] - 1.0);
		}
		
		penalties = weights;
		accumulatedPenalties = new double[size];
	}
	
	@Override
	public String nextLevel() {
		int foundIndex = -1;
		int index = nextIndex;
		int size = size();
		while (foundIndex == -1) {
			if (accumulatedPenalties[index] < 1.0) {
				accumulatedPenalties[index] += penalties[index];
				foundIndex = index;
			} else {
				accumulatedPenalties[index] -= 1.0;
			}
			index++;
			if (index == size) {
				index = 0;
			}
		}
		nextIndex = index;
		return get(foundIndex);
	}

}
