package me.khomich.jqdsl;

import org.junit.Assert;

public class SqlTestSupport {

	void assertQuery(String expectedSql, Sql.QuitChain chain, Object ... expectedParams) {
		Sql.Query query = chain.toQuery();
		String sql = query.sql();
		Object[] params = query.params();

		Assert.assertEquals(expectedSql, chain.toQuery().sql());
		Assert.assertArrayEquals("Query parameters", params, expectedParams);
	}


}
