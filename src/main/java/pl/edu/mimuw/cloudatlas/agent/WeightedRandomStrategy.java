package pl.edu.mimuw.cloudatlas.agent;

import java.util.Arrays;
import java.util.Random;

public class WeightedRandomStrategy extends LevelSelectionStrategy {
	
	private final Random random = new Random();
	private final double[] thresholds;

	public WeightedRandomStrategy(String zoneName, double exponent) {
		super(zoneName);
		
		double[] weights = new double[size()];
		double sum = 1.0;
		int size = size();
		
		weights[0] = 1.0;
		for (int i = 1; i < size; i++) {
			weights[i] = weights[i - 1] / exponent;
			sum += weights[i];
		}
		
		weights[0] = weights[0] / sum;
		for (int i = 1; i < size; i++) {
			weights[i] = weights[i] / sum + weights[i - 1];
		}
		weights[size - 1] = 1.0;
		
		thresholds = weights;
	}
	
	@Override
	public String nextLevel() {
		double rand = random.nextDouble();
		int index = Arrays.binarySearch(thresholds, rand);
		if (index < 0) {
			index = -index - 1;
		}
		return get(index);
	}

}
