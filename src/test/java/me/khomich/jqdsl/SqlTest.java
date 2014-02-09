package me.khomich.jqdsl;

import org.junit.Test;

import static me.khomich.jqdsl.Sql.*;

public class SqlTest extends SqlTestSupport {
	private final TableLike u = table("User", "u");
	private final TableLike g = table("Group", "g");

	@Test
	public void test() {


		Query query = select(u).from(u).where(u.col("email").eq("khomich")).toQuery();

		System.out.println(query);

		Query query2 = select(u).from(u).where(and(u.col("email").eq("khomich"), u.col("accountId").in(10, 20, 30))).toQuery();

		System.out.println(query2);

		Query query3 = select(u).from(u).where(and(u.col("email").eq("khomich"), or(u.col("accountId").in(10, 20, 30), u.col("email").notLike("%gmail.com")))).toQuery();

		System.out.println(query3);

		Query query4 =
			select(u).
			from(u).
			where(
			  and(
				u.col("email").eq(null),
				or(u.col("accountId").in(), u.col("email").like("%gmail.com"))
			  )
			).
			toQuery();

		System.out.println(query4);
	}

	@Test
	public void testSelect() {
		assertQuery("SELECT * FROM User u", select().from(u));
		assertQuery("SELECT u.* FROM User u", select(u).from(u));
		assertQuery("SELECT u.id FROM User u", select(u.col("id")).from(u));
		assertQuery("SELECT u.id, u.name AS UserName FROM User u", select(u.col("id"), u.col("name").as("UserName")).from(u));
		assertQuery("SELECT u.id, u.name AS \"User Name\" FROM User u", select(u.col("id"), u.col("name").as("User Name")).from(u));
	}

	private final Column u_name = u.col("name");
	private final Column u_email = u.col("email");
	private final Column u_age = u.col("age");
	private final Column u_group = u.col("group");

	final String REF_FROM = "SELECT * FROM User u";
	final String REF_ORDER = REF_FROM + " ORDER BY";
	final String REF_WHERE = "SELECT * FROM User u WHERE";
	final String REF_WHERE_NAME = REF_FROM + " WHERE u.name";

	public static final int AGE = 20;

	private final String NAME_1 = "Alex";
	private final String NAME_2 = "Serg";
	private final String NAME_3 = "Kate";
	private final String NAME_PAT = "Alex%";

	private final String GROUP_1 = "group1";
	private final String GROUP_2 = "group2";

	private WhereChain condQuery(Condition cond) {
		return select().from(u).where(cond);
	}

	private OrderChain orderQuery(Ordering ... orderings) {
		return select().from(u).order(orderings);
	}

	@Test
	public void testOps() {
		//EQ
		assertQuery(REF_WHERE_NAME + " = ?", condQuery(u_name.eq(NAME_1)), NAME_1);
		assertQuery(REF_FROM, condQuery(u_name.eq(null)));

		//NE
		assertQuery(REF_WHERE_NAME + " <> ?", condQuery(u_name.ne(NAME_1)), NAME_1);
		assertQuery(REF_FROM, condQuery(u_name.ne(null)));

		//GT
		assertQuery(REF_WHERE_NAME + " > ?", condQuery(u_name.gt(NAME_1)), NAME_1);
		assertQuery(REF_FROM, condQuery(u_name.gt(null)));

		//LT
		assertQuery(REF_WHERE_NAME + " < ?", condQuery(u_name.lt(NAME_1)), NAME_1);
		assertQuery(REF_FROM, condQuery(u_name.lt(null)));

		//GE
		assertQuery(REF_WHERE_NAME + " >= ?", condQuery(u_name.ge(NAME_1)), NAME_1);
		assertQuery(REF_FROM, condQuery(u_name.ge(null)));

		//LE
		assertQuery(REF_WHERE_NAME + " <= ?", condQuery(u_name.le(NAME_1)), NAME_1);
		assertQuery(REF_FROM, condQuery(u_name.le(null)));

		//BETWEEN
		assertQuery(REF_WHERE_NAME + " BETWEEN ? AND ?", condQuery(u_name.between(NAME_1, NAME_2)), NAME_1, NAME_2);
		assertQuery(REF_WHERE_NAME + " <= ?", condQuery(u_name.between(null, NAME_2)), NAME_2);
		assertQuery(REF_WHERE_NAME + " >= ?", condQuery(u_name.between(NAME_1, null)), NAME_1);
		assertQuery(REF_FROM, condQuery(u_name.between(null, null)));

		//LIKE
		assertQuery(REF_WHERE_NAME + " LIKE ?", condQuery(u_name.like(NAME_PAT)), NAME_PAT);
		assertQuery(REF_FROM, condQuery(u_name.like(null)));

		//NOT LIKE
		assertQuery(REF_WHERE_NAME + " NOT LIKE ?", condQuery(u_name.notLike(NAME_PAT)), NAME_PAT);
		assertQuery(REF_FROM, condQuery(u_name.notLike(null)));

		//IN
		assertQuery(REF_WHERE_NAME + " IN (?, ?, ?)", condQuery(u_name.in(NAME_1, NAME_2, NAME_3)), NAME_1, NAME_2, NAME_3);
		assertQuery(REF_FROM, condQuery(u_name.in()));

		//NOT IN
		assertQuery(REF_WHERE_NAME + " NOT IN (?, ?, ?)", condQuery(u_name.notIn(NAME_1, NAME_2, NAME_3)), NAME_1, NAME_2, NAME_3);
		assertQuery(REF_FROM, condQuery(u_name.notIn()));

		//IS NULL
		assertQuery(REF_WHERE_NAME + " IS NULL", condQuery(u_name.isNull()));

		//IS NOT NULL
		assertQuery(REF_WHERE_NAME + " IS NOT NULL", condQuery(u_name.isNotNull()));
	}

	@Test
	public void testAnd() {
		assertQuery(REF_WHERE + " u.name = ? AND u.email LIKE ?", condQuery(and(u_name.eq(NAME_1), u_email.like(NAME_PAT))), NAME_1, NAME_PAT);
		assertQuery(REF_WHERE + " u.name = ? AND u.email LIKE ? AND u.age > ?", condQuery(and(u_name.eq(NAME_1), u_email.like(NAME_PAT), u_age.gt(AGE))), NAME_1, NAME_PAT, AGE);
		assertQuery(REF_WHERE + " (u.name = ? AND u.email LIKE ?) AND u.age > ?", condQuery(and(and(u_name.eq(NAME_1), u_email.like(NAME_PAT)), u_age.gt(AGE))), NAME_1, NAME_PAT, AGE);
		assertQuery(REF_WHERE + " u.name = ? AND u.age > ?", condQuery(and(u_name.eq(NAME_1), u_email.like(null), u.col("age").gt(AGE))), NAME_1, AGE);
		assertQuery(REF_WHERE + " u.age > ?", condQuery(and(u_name.eq(null), u_email.like(null), u_age.gt(20))), AGE);
		assertQuery(REF_FROM, condQuery(and(u_name.eq(null), u_email.like(null), u_age.gt(null))));
	}

	@Test
	public void testOr() {
		assertQuery(REF_WHERE + " u.name = ? OR u.email LIKE ?", condQuery(or(u_name.eq(NAME_1), u_email.like(NAME_PAT))), NAME_1, NAME_PAT);
		assertQuery(REF_WHERE + " u.name = ? OR u.email LIKE ? OR u.age > ?", condQuery(or(u_name.eq(NAME_1), u_email.like(NAME_PAT), u_age.gt(AGE))), NAME_1, NAME_PAT, AGE);
		assertQuery(REF_WHERE + " (u.name = ? OR u.email LIKE ?) OR u.age > ?", condQuery(or(or(u_name.eq(NAME_1), u_email.like(NAME_PAT)), u_age.gt(AGE))), NAME_1, NAME_PAT, AGE);
		assertQuery(REF_WHERE + " u.name = ? OR u.age > ?", condQuery(or(u_name.eq(NAME_1), u_email.like(null), u.col("age").gt(AGE))), NAME_1, AGE);
		assertQuery(REF_WHERE + " u.age > ?", condQuery(or(u_name.eq(null), u_email.like(null), u_age.gt(AGE))), AGE);
		assertQuery(REF_FROM, condQuery(or(u_name.eq(null), u_email.like(null), u_age.gt(null))));
	}

	@Test
	public void testNot() {
		assertQuery(REF_WHERE + " NOT u.name = ?", condQuery(not(u_name.eq(NAME_1))), NAME_1);
		assertQuery(REF_WHERE + " NOT (u.name = ? OR u.email LIKE ?)", condQuery(not(or(u_name.eq(NAME_1), u_email.like(NAME_PAT)))), NAME_1, NAME_PAT);
	}

	@Test
	public void testAndOr() {
		assertQuery(REF_WHERE + " (u.name = ? OR u.email LIKE ?) AND (u.age > ? OR u.group IN (?, ?))", condQuery(and(or(u_name.eq(NAME_1), u_email.like(NAME_PAT)), or(u_age.gt(AGE), u_group.in(GROUP_1, GROUP_2)))), NAME_1, NAME_PAT, AGE, GROUP_1, GROUP_2);
		assertQuery(REF_WHERE + " u.email LIKE ? AND (u.age > ? OR u.group IN (?, ?))", condQuery(and(or(u_name.eq(null), u_email.like(NAME_PAT)), or(u_age.gt(AGE), u_group.in(GROUP_1, GROUP_2)))), NAME_PAT, AGE, GROUP_1, GROUP_2);
		assertQuery(REF_WHERE + " u.email LIKE ? AND u.group IN (?, ?)", condQuery(and(or(u_name.eq(null), u_email.like(NAME_PAT)), or(u_age.gt(null), u_group.in(GROUP_1, GROUP_2)))), NAME_PAT, GROUP_1, GROUP_2);
		assertQuery(REF_WHERE + " u.email LIKE ?", condQuery(and(or(u_name.eq(null), u_email.like(NAME_PAT)), or(u_age.gt(null), u_group.in()))), NAME_PAT);
		assertQuery(REF_FROM, condQuery(and(or(u_name.eq(null), u_email.like(null)), or(u_age.gt(null), u_group.in()))));
	}

	@Test
	public void testOrderBy() {
		assertQuery(REF_ORDER + " u.name ASC", orderQuery(u_name.asc()));
		assertQuery(REF_ORDER + " u.name DESC", orderQuery(u_name.desc()));
		assertQuery(REF_ORDER + " u.name ASC, u.age DESC", orderQuery(u_name.asc(), u_age.desc()));
		assertQuery(REF_ORDER + " u.name ASC, u.age DESC, u.email DESC", orderQuery(u_name.asc(), u_age.desc(), u_email.asc(false)));
	}

}
