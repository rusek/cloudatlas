package pl.edu.mimuw.cloudatlas.islands;

public interface ChildIsland extends Island {

	public ChildEndpoint mountMother(MotherEndpoint motherEndpoint);
}
