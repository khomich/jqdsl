package me.khomich.jqdsl;

public class Orderings {

	public static Sql.Ordering num(final SqlKeyword dir, final int pos) {
		return new Sql.Ordering() {
			@Override
			public void genOrderingSql(SqlBuilder sqlBuilder) {
				sqlBuilder.add(String.valueOf(pos)).space().add(dir);
			}
		};
	}

	public static Sql.Ordering value(final SqlKeyword dir, final Sql.ValueLike value) {
		return new Sql.Ordering() {
			@Override
			public void genOrderingSql(SqlBuilder sqlBuilder) {
				sqlBuilder.add(value).space().add(dir);
			}
		};
	}
}
