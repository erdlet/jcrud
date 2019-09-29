# jCRUD Operations 
[![CircleCI](https://circleci.com/gh/erdlet/jdbc-crud-operations.svg?style=svg&circle-token=c30ecba548cdeebf02eb7541feeca5a9ca984be2)](https://circleci.com/gh/erdlet/jdbc-crud-operations)

Execute SQL queries in the style of Spring's JDBC Template - but without Spring :)

## Status

Work in progress

## Motivation behind this library?
I really like to work with Spring's `JdbcTemplate`, because it is easy to use and a nice
abstraction of the base JDBC API. But for projects which are not using Spring, adding the
`spring-jdbc` artifact causes the addition of nearly the complete Spring core to the project.

To avoid this problem, `jCRUD`shall be a dependency-less, small and easy to use alternative to Spring's great `JdbcTemplate`.
