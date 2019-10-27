# jCRUD 
[![CircleCI](https://circleci.com/gh/erdlet/jcrud.svg?style=svg&circle-token=c30ecba548cdeebf02eb7541feeca5a9ca984be2)](https://circleci.com/gh/erdlet/jcrud) [![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/erdlet/jcrud/blob/master/LICENSE)[![Maven Central](https://img.shields.io/maven-central/v/de.erdlet.jcrud/jcrud.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22de.erdlet.jcrud%22%20AND%20a:%22jcrud%22) 

Execute SQL queries in the style of Spring's JDBC Template - but without Spring :)

## Motivation behind this library?
I really like to work with Spring's `JdbcTemplate`, because it is easy to use and a nice
abstraction of the base JDBC API. But for projects which are not using Spring, adding the
`spring-jdbc` artifact causes the addition of nearly the complete Spring core to the project.

To avoid this problem, `jCRUD`shall be a dependency-less, small and easy to use alternative to Spring's great `JdbcTemplate`.
