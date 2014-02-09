package me.khomich.jqdsl;

public enum SqlOp {
	EQ("="),
	NOT_EQ("<>"),
	GT(">"),
	LT("<"),
	GE(">="),
	LE("<="),
	IN("IN"),
	NOT_IN("NOT IN"),
	IS_NULL("IS NULL"),
	IS_NOT_NULL("IS NOT NULL"),
	BETWEEN("BETWEEN"),
	LIKE("LIKE"),
	NOT_LIKE("NOT LIKE"),
	NOT("NOT"),
	AND("AND"),
	OR("OR"),
	;

	private final String text;

	private SqlOp(String text) {
		this.text = text;
	}

	public String text() {
		return text;
	}
}
