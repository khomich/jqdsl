package me.khomich.jqdsl;

import java.util.Collection;

/**
 * @author Alexander Khomich
 *
 * The root class for building DSL-like queries.
 *
 * It is recommended to make static import of all its components to get fluent query expression.
 */
public class Sql {

	public interface Query {
		String sql();
		Object[] params();
	}

	/**
	 * Declares an SQL table or view for further usage
	 * @param name table/view original name
	 * @param alias shorten name (alias) to use
	 * @return a new table reference object
	 */
	public static TableLike table(String name, String alias) {
		return new TableRef(name, alias);
	}

	/**
	 * Creates a custom SQL expression snipped.
	 * @param sqlExpression an expression body with place holders, like {1}, {2} ...., if you need to specify
	 * @param values values to substitute expression placeholders, ValueLike references like Column will be recognized and properly interpolated with possible parametrization
	 * @return new expression instance
	 */
	public static Expression expr(String sqlExpression, Object... values) {
		return null;
	}

	public static Condition and(Condition ... conds) {
		return Conditions.chain(SqlOp.AND, conds);
	}

	public static Condition or(Condition ... conds) {
		return Conditions.chain(SqlOp.OR, conds);
	}

	public static Condition not(Condition cond) {
		return Conditions.prefix(SqlOp.NOT, cond);
	}

	/**
	 * Starts select query
	 * @param elements the list of all selectable elements. If elements are not specified then SELECT * is considered
	 */
	public static SelectIntoChain select(Selectable ... elements) {
		return new SelectQueryBuilder(elements);
	}

	public interface QuitChain {
		/**
		 * Compiles DSL operation chain into Query model.
		 * This step includes basic verification and branch optimizations.
		 * It means query object may differs depending on data specified while building.
		 */
		Query toQuery();
	}

	public interface SelectIntoChain extends IntoOp, FromOp { }

	public interface SelectChain extends FromOp { }

	public interface FromChain extends JoinOp, WhereOp, OrderOp, QuitChain { }

	public interface WhereChain extends OrderOp, QuitChain { }

	public interface OrderChain extends QuitChain { }

	public interface IntoOp {
		/**
		 * Specifies a temp table target for sql query results.
		 * @param tempTable name of the temp table with the namespace specifier (#, ## etc)
		 */
		SelectChain into(String tempTable);
	}

	public interface FromOp {
		/**
		 * Specifies the main source of the query.
		 * @param source any component compliant with Source interface. It can be table, view or other query (even CTE, when supported)
		 */
		FromChain from(Source source);
	}

	public interface JoinOp {
		/**
		 * Joins an additional source
		 * @param source any component compliant with Source interface. It can be table, view or other query (even CTE, when supported)
		 */
		OnOp join(Source source);
	}

	public interface OnOp {
		/**
		 * Specifies a list of conditions for previous join operation
		 */
		FromChain on(Condition conditions);
	}

	public interface WhereOp {
		/**
		 * Specifies a condition for rows to be returned in results
		 */
		WhereChain where(Condition condition);
	}

	public interface OrderOp {
		/**
		 * Specifies a list of ordering rules for query result
		 */
		OrderChain order(Ordering ... orderings);
	}

	/**
	 * Minimal interface for element to be included into SELECT elements
	 */
	public interface Selectable {
		/** Provides public final name of the selected element */
		String name();

		/**
		 * Emits a a valid SQL expression for SELECT ... statement
		 */
		void genSelectableSql(SqlBuilder sqlBuilder);
	}

	/**
	 * Some entities are selectable by nature but provides default alias, which can be clarified
	 */
	public interface SelectableLike extends Selectable {
		Selectable as(String alias);
	}

	public interface Condition {
		/**
		 * Checks is condition degenerated which means it doesn't have any sense
		 */
		boolean isDegenerated();


		/**
		 * Checks is this condition complex and aggregates multiple of other conditions
		 */
		boolean isComplex();

		/**
		 * Emits condition body SQL
		 */
		void genConditionSql(SqlBuilder sqlBuilder);
	}

	public interface Orderable {
		/** Creates an ascending ordering by this value */
		Ordering asc();

		/** Creates a descending ordering by this value */
		Ordering desc();

		/** Creates an ascending or descending ordering by this value
		 * @param asc choose ASC ordering if set to <c>true</c>, otherwise DESC will be chosen
		 */
		Ordering asc(boolean asc);
	}

	public interface Ordering {
		//TODO implement NULL of bottom modifier

		/**
		 * Emits an ordering SQL
		 */
		void genOrderingSql(SqlBuilder sqlBuilder);
	}

	public interface Source {
		/**
		 * Provides an alias for source
		 */
		String alias();

		/**
		 * Emits a valid SQL declaration of source for FROM ..., JOIN ... operation
		 */
		void genSourceSql(SqlBuilder sqlBuilder);
	}

	public interface TableLike extends Source, Selectable  {
		/**
		 * Creates a new column reference for this object. May cause
		 * @param name name of the reference retrieved
		 * @return column reference
		 */
		Column col(String name);
	}

	public interface ValueLike extends Orderable {
		/**
		 * Creates an equality condition between values
		 * @param value any object or <c>null</c>. If <c>null</c> specified then condition will be considered as degenerated
		 */
		Condition eq(Object value);

		/**
		 * Creates a non-equality condition between values
		 * @param value any object or <c>null</c>. If <c>null</c> specified then condition will be considered as degenerated
		 */
		Condition ne(Object value);

		/**
		 * Creates a "greater than" condition between values
		 * @param value any object or <c>null</c>. If <c>null</c> specified then condition will be considered as degenerated
		 */
		Condition gt(Object value);

		/**
		 * Creates a "less than" condition between values
		 * @param value any object or <c>null</c>. If <c>null</c> specified then condition will be considered as degenerated
		 */
		Condition lt(Object value);

		/**
		 * Creates a "greater or equal" condition between values
		 * @param value any object or <c>null</c>. If <c>null</c> specified then condition will be considered as degenerated
		 */
		Condition ge(Object value);

		/**
		 * Creates a "less or equal" condition between values
		 * @param value any object or <c>null</c>. If <c>null</c> specified then condition will be considered as degenerated
		 */
		Condition le(Object value);

		/**
		 * Creates a condition that left-side value matches right-seide pattern
		 * @param pattern standard SQL LIKE pattern %, _ etc... If <c>null</c> is specified then condition will be considered as degenerated
		 */
		Condition like(String pattern);

		/**
		 * Opposite to like.
		 */
		Condition notLike(String pattern);

		/**
		 * Creates a range condition which is satisfied when left-side value exists between from and to (inclusive).
		 * If you specify <c>null</c> for both from and to then entire condition will be degenerated
		 *
		 * @param from any object or <c>null</c>. If <c>null</c> specified then from condition is degenerated
		 * @param to any object or <c>null</c>. If <c>null</c> specified then to condition is degenerated
		 */
		Condition between(Object from, Object to);

		/**
		 * Creates an contains condition between left-side value and specified collection
		 * @param values any count of the values of the same type. Objects by itself can not be nulls. If collection is empty then condition will be considered as degenerated
		 */
		<T> Condition in(T ... values);

		/**
		 * Creates a contains condition between left-side value and specified collection
		 * @param values any count of the values of the same type. If collection is empty then condition will be considered as degenerated
		 */
		<T> Condition in(Collection<T> values);

		/**
		 * Creates an not contains condition between left-side value and specified collection
		 * @param values any count of the values of the same type. Objects by itself can not be nulls. If collection is empty then condition will be considered as degenerated
		 */
		<T> Condition notIn(T ... values);

		/**
		 * Creates a not contains condition between left-side value and specified collection
		 * @param values any count of the values of the same type. If collection is empty then condition will be considered as degenerated
		 */
		<T> Condition notIn(Collection<T> values);

		/**
		 * Creates condition which is true only when value is NULL
		 */
		Condition isNull();

		/**
		 * Creates condition which is true when value has any value different from NULL
		 */
		Condition isNotNull();

		/**
		 * Emits value body SQL
		 */
		void genValueSql(SqlBuilder sqlBuilder);
	}

	public interface Expression extends SelectableLike, ValueLike {
		/**
		 * Emits a valid SQL declaration of source for FROM ..., JOIN ... operation
		 */
		String emitExpression(SqlBuilder sqlBuilder);
	}

	public interface Column extends Expression {
		/**
		 * Provides a source of this column
		 */
		Source source();
	}
}

