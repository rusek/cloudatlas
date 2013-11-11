package pl.edu.mimuw.cloudatlas.query;

public interface ResultVisitor<R, E extends Exception> {
	public R visit(OneResult result) throws E;
	public R visit(ListResult result) throws E;
	public R visit(ColumnResult result) throws E;
}
