package me.khomich.jqdsl;

public class SelectQueryBuilder implements Sql.SelectChain, Sql.SelectIntoChain, Sql.FromChain, Sql.OnOp, Sql.WhereChain, Sql.OrderChain {
	private Sql.Selectable[] results;
	private Sql.Source from;
	private Sql.Condition condition = Conditions.EMPTY;
	private Sql.Ordering[] orderings = new Sql.Ordering[0];

	public SelectQueryBuilder(Sql.Selectable[] results) {
		this.results = results;
	}

	@Override
	public Sql.FromChain from(Sql.Source source) {
		if (null == source) throw new NullPointerException("source");
		this.from = source;
		return this;
	}

	@Override
	public Sql.SelectChain into(String tempTable) {
		if (null == tempTable) throw new NullPointerException("tempTable");

		return this;
	}

	@Override
	public Sql.OnOp join(Sql.Source source) {
		if (null == source) throw new NullPointerException("source");

		//TODO create one more join object

		return this;
	}

	@Override
	public Sql.FromChain on(Sql.Condition conditions) {
		//TODO take last join and set conditions on there...
		return this;
	}

	@Override
	public Sql.OrderChain order(Sql.Ordering... orderings) {
		this.orderings = orderings;
		return this;
	}

	@Override
	public Sql.WhereChain where(Sql.Condition condition) {
		this.condition = condition;
		return this;
	}

	@Override
	public Sql.Query toQuery() {
		final SqlBuilder sqlBuilder = new SqlBuilder();
		sqlBuilder.add(SqlKeyword.SELECT);

		if (results.length > 0) {
			boolean populated = false;
			for (Sql.Selectable sel : results) {
				if (populated) { sqlBuilder.sep(); }
				sqlBuilder.add(sel);
				populated = true;
			}
		} else {
			sqlBuilder.wildcard();
		}

		if (null != from) {
			sqlBuilder.add(SqlKeyword.FROM);
			from.genSourceSql(sqlBuilder);
		}

		if (!condition.isDegenerated()) {
			sqlBuilder.add(SqlKeyword.WHERE);
			condition.genConditionSql(sqlBuilder);
		}

		if (orderings.length > 0) {
			boolean populated = false;
			sqlBuilder.add(SqlKeyword.ORDER).add(SqlKeyword.BY);
			for (Sql.Ordering ordering : orderings) {
				if (populated) { sqlBuilder.sep(); }
				ordering.genOrderingSql(sqlBuilder);
				populated = true;
			}
		}

		return new Sql.Query() {
			@Override
			public String toString() {
				return sqlBuilder.toSql().trim();
			}

			@Override
			public String sql() {
				return sqlBuilder.toSql().trim();
			}

			@Override
			public Object[] params() {
				return sqlBuilder.getParams();
			}
		};
	}
}
