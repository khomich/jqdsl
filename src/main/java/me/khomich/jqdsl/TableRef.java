package me.khomich.jqdsl;

public class TableRef implements Sql.TableLike {
	private final String name;
	private final String alias;

	public TableRef(String name, String alias) {
		this.name = name;
		this.alias = alias;
	}

	@Override
	public Sql.Column col(String name) {
		return new ColumnRef(name, this);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String alias() {
		return alias;
	}

	@Override
	public void genSelectableSql(SqlBuilder sqlBuilder) {
		sqlBuilder.addAlias(alias).dot().wildcard();
	}

	@Override
	public void genSourceSql(SqlBuilder sqlBuilder) {
		sqlBuilder.addName(name).space().addAlias(alias);
	}
}
