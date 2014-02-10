package me.khomich.jqdsl;

import java.util.LinkedList;

public class SelectQueryModel extends QueryModel implements Sql.SelectChain, Sql.SelectIntoChain, Sql.FromChain, Sql.OnOp, Sql.WhereChain, Sql.OrderChain {
	private Sql.Selectable[] results;
	private Sql.Source from;
	private LinkedList<JoinModel> joins = new LinkedList<JoinModel>();
	private Sql.Condition condition = Conditions.EMPTY;
	private Sql.Ordering[] orderings = new Sql.Ordering[0];

	public SelectQueryModel(Sql.Selectable[] results) {
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
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Sql.OnOp join(Sql.Source source) {
		if (null == source) throw new NullPointerException("source");

		joins.add(new JoinModel(SqlKeyword.INNER, source, true));

		return this;
	}

	@Override
	public Sql.OnOp innerJoin(Sql.Source source) {
		if (null == source) throw new NullPointerException("source");
		joins.add(new JoinModel(SqlKeyword.INNER, source, false));
		return this;
	}

	@Override
	public Sql.OnOp leftJoin(Sql.Source source) {
		if (null == source) throw new NullPointerException("source");
		joins.add(new JoinModel(SqlKeyword.LEFT, source, false));
		return this;
	}

	@Override
	public Sql.OnOp rightJoin(Sql.Source source) {
		if (null == source) throw new NullPointerException("source");
		joins.add(new JoinModel(SqlKeyword.RIGHT, source, false));
		return this;
	}

	@Override
	public Sql.FromChain crossJoin(Sql.Source source) {
		if (null == source) throw new NullPointerException("source");
		joins.add(new JoinModel(SqlKeyword.CROSS, source, false));
		return this;
	}

	@Override
	public Sql.FromChain on(Sql.Condition condition) {
		if (joins.isEmpty()) throw new IllegalStateException("Unexpected ON operator, no forwarding JOINs found");
		if (null == condition) throw new NullPointerException("condition");
		joins.getLast().setCondition(condition);
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
		final SqlBuilder builder = new SqlBuilder();
		builder.add(SqlKeyword.SELECT);

		if (results.length > 0) {
			boolean populated = false;
			for (Sql.Selectable sel : results) {
				if (populated) { builder.sep(); }
				builder.add(sel);
				populated = true;
			}
		} else {
			builder.wildcard();
		}

		if (null != from) {
			builder.add(SqlKeyword.FROM);
			from.genSourceSql(builder);
		}

		for (JoinModel join : joins) {
			join.genJoinSql(builder);
		}

		if (!condition.isDegenerated()) {
			builder.add(SqlKeyword.WHERE);
			condition.genConditionSql(builder);
		}

		if (orderings.length > 0) {
			boolean populated = false;
			builder.add(SqlKeyword.ORDER).add(SqlKeyword.BY);
			for (Sql.Ordering ordering : orderings) {
				if (populated) { builder.sep(); }
				ordering.genOrderingSql(builder);
				populated = true;
			}
		}

		return new Sql.Query() {
			@Override
			public String toString() {
				return builder.toSql().trim();
			}

			@Override
			public String sql() {
				return builder.toSql().trim();
			}

			@Override
			public Object[] params() {
				return builder.getParams();
			}
		};
	}
}
