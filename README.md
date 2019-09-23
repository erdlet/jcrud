# JDBC CRUD Operations

Execute SQL queries in the style of Spring's JDBC Template - but without Spring :)

## Status

Work in progress

## Why this library?
I really like to work with Spring's `JdbcTemplate`, because it is easy to use and a nice
abstraction of the base JDBC API. But for projects which are not using Spring, adding the
`spring-jdbc` artifact adds a lot of transient dependencies to the project. The `jdbc-crud-operations`
shall be a dependency-less, easy to use alternative to Spring's great solution.