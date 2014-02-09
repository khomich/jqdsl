package me.khomich.jqdsl;

public class ColumnRef extends ValueRef implements Sql.Column {
	private final String name;
	private final Sql.Source source;

	public ColumnRef(String name, Sql.Source source) {
		this.name = name;
		this.source = source;
	}

	@Override
	public Sql.Source source() {
		return source;
	}

	@Override
	public String emitExpression(SqlBuilder sqlBuilder) {
		return null;
	}

	@Override
	public Sql.Selectable as(final String alias) {
		return new Sql.Selectable() {
			@Override
			public String name() {
				return alias;
			}

			@Override
			public void genSelectableSql(SqlBuilder sqlBuilder) {
				ColumnRef.this.genSelectableSql(sqlBuilder);
				sqlBuilder.add(SqlKeyword.AS);
				sqlBuilder.addName(alias);
			}
		};
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void genSelectableSql(SqlBuilder sqlBuilder) {
		genValueSql(sqlBuilder);
	}

	@Override
	public void genValueSql(SqlBuilder sqlBuilder) {
		sqlBuilder.addAlias(source.alias()).dot().addName(name);
	}
}
