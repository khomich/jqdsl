package me.khomich.jqdsl;

import java.util.ArrayList;
import java.util.List;

public class Conditions {
	public static final Sql.Condition EMPTY = new Sql.Condition() {
		@Override public boolean isDegenerated() { return true; }
		@Override public boolean isComplex() { return false; }
		@Override public void genConditionSql(SqlBuilder sqlBuilder) { throw new IllegalStateException();}
	};

	public static Sql.Condition binary(SqlOp op, Sql.ValueLike value, Object param) {
		return new BinaryCondition(op, value, param);
	}

	public static Sql.Condition range(SqlOp op, Sql.ValueLike value, Object from, Object to) {
		if (null == from) {
			if (null == to) {
				return EMPTY;
			} else {
				return binary(SqlOp.LE, value, to);
			}
		} else {
			if (null == to) {
				return binary(SqlOp.GE, value, from);
			} else {
		 		return new RangeCondition(op, value, from, to);
			}
		}
	}

	public static Sql.Condition chain(SqlOp op, Sql.Condition ... conds) {
		List<Sql.Condition> conditions = new ArrayList<Sql.Condition>();

		for (Sql.Condition cond : conds) {
			if (!cond.isDegenerated()) {
				conditions.add(cond);
			}
		}
		return new ChainCondition(op, conditions);
	}

	public static Sql.Condition prefix(final SqlOp op, final Sql.Condition cond) {
		return new PrefixCondition(op, cond);
	}

	public static Sql.Condition sufiixCheck(final SqlOp op, Sql.ValueLike value) {
		return new SuffixCondition(op, value);
	}

	public static Sql.Condition in(final SqlOp op, Sql.ValueLike value, final Object ... values) {
		return new InCondition(op, value, values);
	}

	//------------------------------------------------------------------------------------------------------------------

	protected abstract static class ConditionOp implements Sql.Condition {
		protected final SqlOp operator;

		protected ConditionOp(SqlOp operator) {
			this.operator = operator;
		}

		@Override
		public boolean isComplex() {
			return false;
		}
	}

	protected static class BinaryCondition extends ConditionOp {
		private final Sql.ValueLike value;
		private final Object param;

		public BinaryCondition(SqlOp operator, Sql.ValueLike value, Object param) {
			super(operator);
			this.value = value;
			this.param = param;
		}

		@Override
		public boolean isDegenerated() {
			return null == param;
		}

		@Override
		public void genConditionSql(SqlBuilder sqlBuilder) {
			value.genValueSql(sqlBuilder);
			sqlBuilder.add(operator);
			sqlBuilder.param(param);
		}
	}

	protected static class ChainCondition extends ConditionOp {
		private final List<Sql.Condition> conditions;

		ChainCondition(SqlOp operator, List<Sql.Condition> conditions) {
			super(operator);
			this.conditions = conditions;
		}

		@Override
		public boolean isDegenerated() {
			return conditions.isEmpty();
		}

		@Override
		public boolean isComplex() {
			return conditions.size() > 1;
		}

		@Override
		public void genConditionSql(SqlBuilder sqlBuilder) {
			boolean populated = false;
			for (Sql.Condition cond : conditions) {
				if (populated) { sqlBuilder.add(operator); }
				sqlBuilder.add(cond);
				populated = true;
			}
		}
	}

	protected static class PrefixCondition extends ConditionOp {
		private Sql.Condition other;

		PrefixCondition(SqlOp operator, Sql.Condition other) {
			super(operator);
			this.other = other;
		}

		@Override
		public boolean isDegenerated() {
			return other.isDegenerated();
		}

		@Override
		public void genConditionSql(SqlBuilder sqlBuilder) {
			sqlBuilder.add(operator).space().add(other);
		}
	}

	protected static class SuffixCondition extends ConditionOp {
		private final Sql.ValueLike value;

		public SuffixCondition(SqlOp operator, Sql.ValueLike value) {
			super(operator);
			this.value = value;
		}

		@Override
		public boolean isDegenerated() {
			return false;
		}

		@Override
		public void genConditionSql(SqlBuilder sqlBuilder) {
			sqlBuilder.add(value).add(operator);
		}
	}

	protected static class InCondition extends ConditionOp {
		private Sql.ValueLike value;
		private Object[] params;

		public InCondition(SqlOp operator, Sql.ValueLike value, Object[] params) {
			super(operator);
			this.value = value;
			this.params = params;
		}

		@Override
		public boolean isDegenerated() {
			return params.length == 0;
		}

		@Override
		public boolean isComplex() {
			return false;
		}

		@Override
		public void genConditionSql(SqlBuilder sqlBuilder) {
			sqlBuilder.add(value);
			sqlBuilder.add(operator);
			sqlBuilder.add("(");
			boolean populated = false;
			for (Object value : params) {
				if (populated) { sqlBuilder.sep(); }
				sqlBuilder.param(value);
				populated = true;
			}
			sqlBuilder.add(")");
		}
	}

	protected static class RangeCondition extends ConditionOp {
		private final Sql.ValueLike value;
		private final Object from;
		private final Object to;

		public RangeCondition(SqlOp operator, Sql.ValueLike value, Object from, Object to) {
			super(operator);
			this.value = value;
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean isDegenerated() {
			return false;
		}

		@Override
		public void genConditionSql(SqlBuilder sqlBuilder) {
			sqlBuilder.add(value).add(operator).param(from).add(SqlOp.AND).param(to);
		}
	}
}
