package pl.edu.mimuw.cloudatlas.agent;

import java.util.Date;
import java.util.Properties;

import pl.edu.mimuw.cloudatlas.zones.ZoneNames;

public abstract class LevelSelectionStrategy {

	private final String[] levelNames;

	protected LevelSelectionStrategy(String zoneName) {
		int level = ZoneNames.getLevel(zoneName);
		
		String[] levelNames = new String[level];
		for (int i = level - 1; i >= 0; i--) {
			levelNames[i] = zoneName;
			zoneName = ZoneNames.getParentName(zoneName);
		}
		this.levelNames = levelNames;
	}
	
	protected int size() {
		return levelNames.length;
	}
	
	protected String get(int index) {
		return levelNames[index];
	}

	public abstract String nextLevel();
	
	public static LevelSelectionStrategy createStrategy(String zoneName, Properties properties) {
		String strategy = properties.getProperty("gossipLevelSelectionStrategy", "random");
		if (strategy.equals("whatever")) {
			switch ((int) (new Date().getTime() % 4)) {
			case 0:
				strategy = "random";
				break;
			case 1:
				strategy = "weightedRandom";
				break;
			case 2:
				strategy = "roundRobin";
				break;
			case 3:
				strategy = "weightedRoundRobin";
				break;
			}
		}
		
		switch (strategy) {
		case "random":
			return new RandomStrategy(zoneName);
			
		case "weightedRandom":
			return new WeightedRandomStrategy(zoneName, Math.E);
			
		case "roundRobin":
			return new RoundRobinStrategy(zoneName);
			
		case "weightedRoundRobin":
			return new WeightedRoundRobinStrategy(zoneName, Math.E);
		
		default:
			throw new IllegalArgumentException("Invalid stragegy: " + strategy);
		}
	}
}
