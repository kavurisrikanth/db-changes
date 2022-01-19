package gqltosql2;

public interface IValue {

	Object read(Object[] row, OutObject obj) throws Exception;

}
