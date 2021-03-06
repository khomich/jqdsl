JQDSL
=====

Summary
-------

JQDSL is going to be a quite useful SQL Builder tool for Java which features:
* fluent DSL syntax
* conditional constructing
* high reusability of SQL snippets
* user friendly JDBC wrapper
* spring JdbcTemplate API ready

Motivation
----------

One of the typical tasks in my primary project is to build huge multi-parameter queries/filters where only few of parameters are usually used at the same time.
 The complexity of the query depends on business purposes but most-likely the query contains all of possible joins which are not in use sometimes but producing impact on execution.

We can greatly optimize an execution by eliminating "dead" branches from SQL string. The common solution is to use SQL concatenation (hello StringBuffer) and checking each branch for being degenerated...
 It becomes nightmare when doing this more than once.

 JQDSL will make an effort to run this process practically automated and give you an ability to focus on a logic and business rules while the library includes only sensible conditions and query components. You write nice readable DSL query, use actual arguments and get shortest possible SQL which can be run any suitable for you way.