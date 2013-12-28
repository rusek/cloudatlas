package pl.edu.mimuw.cloudatlas.agent;

public class RoundRobinStrategy extends LevelSelectionStrategy {
	
	int nextIndex = 0;

	public RoundRobinStrategy(String zoneName) {
		super(zoneName);
	}

	@Override
	public String nextLevel() {
		int index = nextIndex++;
		if (nextIndex == size()) {
			nextIndex = 0;
		}
		return get(index);
	}

}
