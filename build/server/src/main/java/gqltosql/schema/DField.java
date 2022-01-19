package gqltosql.schema;

public abstract class DField<T, R> {

	private int index;
	private String name;
	private String column;
	private DModel<?> ref;
	private String collTable;
	private String mappedByColumn;
	private DModel<T> decl;
	private boolean notNull;

	public DField(DModel<T> decl, int index, String name, String column) {
		this.decl = decl;
		this.index = index;
		this.name = name;
		this.column = column;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public int getEnumType() {
		return 0;
	}

	public abstract FieldType getType();

	public abstract FieldPrimitiveType getPrimitiveType();

	public DModel<?> getReference() {
		return ref;
	}

	public void setRef(DModel<?> ref) {
		this.ref = ref;
	}

	public String getColumnName() {
		return column;
	}

	public void setCollTable(String collTable) {
		this.collTable = collTable;
	}

	public String getCollTableName(String parentTable) {
		return collTable;
	}

	public DModel<T> declType() {
		return decl;
	}

	public String getMappedByColumn() {
		return mappedByColumn;
	}

	public boolean isChild() {
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public boolean isNotNull() {
		return notNull;
	}

	public DField<?, ?> notNull() {
		this.notNull = true;
		return this;
	}

	public abstract Object getValue(T _this);

	public abstract Object fetchValue(T _this, IDataFetcher fetcher);

	public abstract void setValue(T _this, R value);
}
