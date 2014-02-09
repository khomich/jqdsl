package me.khomich.jqdsl;

import java.util.Collection;

public abstract class ValueRef implements Sql.ValueLike {
	@Override
	public Sql.Condition eq(Object value) {
		return Conditions.binary(SqlOp.EQ, this, value);
	}

	@Override
	public Sql.Condition ne(Object value) {
		return Conditions.binary(SqlOp.NOT_EQ, this, value);
	}

	@Override
	public Sql.Condition gt(Object value) {
		return Conditions.binary(SqlOp.GT, this, value);
	}

	@Override
	public Sql.Condition lt(Object value) {
		return Conditions.binary(SqlOp.LT, this, value);
	}

	@Override
	public Sql.Condition ge(Object value) {
		return Conditions.binary(SqlOp.GE, this, value);
	}

	@Override
	public Sql.Condition le(Object value) {
		return Conditions.binary(SqlOp.LE, this, value);
	}

	@Override
	public Sql.Condition like(String pattern) {
		return Conditions.binary(SqlOp.LIKE, this, pattern);
	}

	@Override
	public Sql.Condition notLike(String pattern) {
		return Conditions.binary(SqlOp.NOT_LIKE, this, pattern);
	}

	@Override
	public Sql.Condition between(Object from, Object to) {
		return Conditions.range(SqlOp.BETWEEN, this, from, to);
	}

	@Override
	public <T> Sql.Condition in(T... values) {
		return Conditions.in(SqlOp.IN, this, values);
	}

	@Override
	public <T> Sql.Condition in(Collection<T> values) {
		return in(values.toArray());
	}

	@Override
	public <T> Sql.Condition notIn(T... values) {
		return Conditions.in(SqlOp.NOT_IN, this, values);
	}

	@Override
	public <T> Sql.Condition notIn(Collection<T> values) {
		return in(values.toArray());
	}

	@Override
	public Sql.Condition isNull() {
		return Conditions.sufiixCheck(SqlOp.IS_NULL, this);
	}

	@Override
	public Sql.Condition isNotNull() {
		return Conditions.sufiixCheck(SqlOp.IS_NOT_NULL, this);
	}

	@Override
	public Sql.Ordering asc() {
		return Orderings.value(SqlKeyword.ASC, this);
	}

	@Override
	public Sql.Ordering desc() {
		return Orderings.value(SqlKeyword.DESC, this);
	}

	@Override
	public Sql.Ordering asc(boolean asc) {
		return asc ? asc() : desc();
	}
}
