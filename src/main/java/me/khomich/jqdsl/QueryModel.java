package me.khomich.jqdsl;

public class QueryModel {

	protected static class JoinModel {
		private final SqlKeyword joinType;
		private final Sql.Source source;
		private Sql.Condition condition;
		private final boolean canOptimize;

		public JoinModel(SqlKeyword joinType, Sql.Source source, boolean canOptimize) {
			this.joinType = joinType;
			this.source = source;
			this.canOptimize = canOptimize;
		}

		public Sql.Source source() {
			return source;
		}

		public Sql.Condition condition() {
			return condition;
		}

		public void setCondition(Sql.Condition condition) {
			if (this.condition == null) {
				this.condition = condition;
			} else {
				throw new IllegalStateException("Repeating ON condition. Each ON should follow after corresponding JOIN");
			}
		}

		public void genJoinSql(SqlBuilder sqlBuilder) {
			if (null != joinType) {
				sqlBuilder.add(joinType);
			}

			sqlBuilder.add(SqlKeyword.JOIN);
			sqlBuilder.add(source);

			if (null != condition) {
				sqlBuilder.add(SqlKeyword.ON);
				sqlBuilder.add(condition);
			}
		}
	}
}
