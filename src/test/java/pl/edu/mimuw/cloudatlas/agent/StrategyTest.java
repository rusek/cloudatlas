package pl.edu.mimuw.cloudatlas.agent;

import junit.framework.TestCase;

public class StrategyTest extends TestCase {

	public static void testWeightedRandomStrategy() {
		WeightedRandomStrategy strategy = new WeightedRandomStrategy("/a/b/c", 2.0);
		
		String result;
		int aCount = 0;
		int bCount = 0;
		int cCount = 0;
		int repeats = 100000;
		for (int i = 0; i < repeats; i++) {
			switch ((result = strategy.nextLevel())) {
			case "/a":
				aCount++;
				break;
			case "/a/b":
				bCount++;
				break;
			case "/a/b/c":
				cCount++;
				break;
			default:
				assertTrue("Bad result: " + result, false);
			}
		}
		assertTrue("aCount: " + aCount, aCount >= repeats * (4.0 / 7.0) * 0.9);
		assertTrue("bCount: " + bCount, bCount >= repeats * (2.0 / 7.0) * 0.9);
		assertTrue("cCount: " + cCount, cCount >= repeats * (1.0 / 7.0) * 0.9);
	}
	
	public static void testWeightedRoundRobinStrategy() {
		WeightedRoundRobinStrategy strategy = new WeightedRoundRobinStrategy("/a/b/c", 2.0);
		
		for (int i = 0; i < 100; i++) {
			assertEquals("/a", strategy.nextLevel());
			assertEquals("/a/b", strategy.nextLevel());
			assertEquals("/a/b/c", strategy.nextLevel());
			assertEquals("/a", strategy.nextLevel());
			assertEquals("/a", strategy.nextLevel());
			assertEquals("/a/b", strategy.nextLevel());
			assertEquals("/a", strategy.nextLevel());
		}
	}
	
	public static void testWeightedRoundRobinStrategyWithExponent1IsSameAsRoundRobinStrategy() {
		WeightedRoundRobinStrategy strategy = new WeightedRoundRobinStrategy("/a/b/c", 1.0);

		for (int i = 0; i < 100; i++) {
			assertEquals("/a", strategy.nextLevel());
			assertEquals("/a/b", strategy.nextLevel());
			assertEquals("/a/b/c", strategy.nextLevel());
		}
	}
}
