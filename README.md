JQDSL
=====

Summary
-------

JQDSL is going to be a quite useful SQL Builder tool for Java which features:
-fluent DSL syntax
-conditional constructing
-high reusability of SQL snippets
-user friendly JDBC wrapper
-spring JdbcTemplate API ready

Motivation
----------

One of the typical tasks in my primary project is to build huge multi-parameter queries/filters where only few of parameters are usually used at the same time.
The complexity of query depends on business purposes but most-likely query contains couple of joins which are not in use sometimes but producing impact on execution.
We can greatly optimize an execution by eliminating "dead" branches from SQL string. The common solution is to use SQL concatenation checking each branch for being degenerated or not...
But JQDSL is going to make this process fully automated and give you an ability to focus on logic and business rules while library include only sensible conditions and query components.