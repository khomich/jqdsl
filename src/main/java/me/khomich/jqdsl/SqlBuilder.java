package me.khomich.jqdsl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Generic SQL writer
 */
public class SqlBuilder {
	public static final char SPACE = ' ';
	public static final char SQL_QUOTE = '"';
	public static final String SQL_QUOTE_STR = String.valueOf(SQL_QUOTE);
	public static final String SQL_DBL_QUOTE_STR = SQL_QUOTE_STR + SQL_QUOTE_STR;

	private StringBuffer buffer = new StringBuffer();
	private List<Object> params = new ArrayList<Object>();

	public void merge(SqlBuilder other) {

	}

	public SqlBuilder add(SqlKeyword keyword) {
		space();
		buffer.append(keyword);
		space();
		return this;
	}

	public SqlBuilder add(SqlOp operator) {
		space();
		buffer.append(operator.text());
		space();
		return this;
	}

	public SqlBuilder add(Sql.Selectable selectable) {
		selectable.genSelectableSql(this);
		return this;
	}

	public SqlBuilder add(Sql.Source source) {
		source.genSourceSql(this);
		return this;
	}

	public SqlBuilder add(Sql.ValueLike value) {
		value.genValueSql(this);
		return this;
	}

	public SqlBuilder add(Sql.Condition condition) {
		if (condition.isComplex()) {
			buffer.append("(");
			condition.genConditionSql(this);
			buffer.append(")");
		} else {
			condition.genConditionSql(this);
		}
		return this;
	}

	public SqlBuilder add(String text) {
		buffer.append(text);
		return this;
	}

	public SqlBuilder wildcard() {
		buffer.append("*");
		return this;
	}

	public SqlBuilder nl() {
		buffer.append("\n");
		return this;
	}

	public SqlBuilder dot() {
		buffer.append(".");
		return this;
	}

	private boolean isLastSpace() {
		return buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == SPACE;
	}

	private void trimSpace() {
		if (isLastSpace()) {
			buffer.setLength(buffer.length() - 1);
		}
	}

	/**
	 * Soft space emits new space only if previous character was not separator
	 */
	public SqlBuilder space() {
		if (!isLastSpace()) {
			buffer.append(SPACE);
		}
		return this;
	}

	/**
	 * Adds the qualified SQL name to the build stream
	 */
	public SqlBuilder addName(String sqlName) {
		buffer.append(tryEscapeName(sqlName));
		return this;
	}

	/**
	 * Adds the qualified SQL alias to the build stream
	 */
	public SqlBuilder addAlias(String alias) {
		buffer.append(tryEscapeName(alias));
		return this;
	}

	private Pattern IDENTIFIER_PATTERN = Pattern.compile("[a-z_][a-z0-9_]*", Pattern.CASE_INSENSITIVE);

	//TODO implement properly with dialects....
	private String tryEscapeName(String sqlName) {
		if (IDENTIFIER_PATTERN.matcher(sqlName).matches()) {
			return sqlName;
		}  else {
			return escapeName(sqlName);
		}
	}

	protected String escapeName(String sqlName) {
		return SQL_QUOTE + sqlName.replace(SQL_QUOTE_STR, SQL_DBL_QUOTE_STR) + SQL_QUOTE;
	}

	public SqlBuilder sep() {
		trimSpace();
		buffer.append(", ");
		return this;
	}

	public SqlBuilder param(Object value) {
		if (value instanceof Sql.ValueLike) {
			add((Sql.ValueLike)value);
		} else {
			buffer.append("?");
			params.add(value);
		}
		return this;
	}

	public String toSql() {
		return buffer.toString();
	}

	public Object[] getParams() {
		return params.toArray();
	}
}
