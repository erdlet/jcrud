# jCRUD 
[![CI](https://github.com/erdlet/jcrud/actions/workflows/main.yml/badge.svg?branch=master)](https://github.com/erdlet/jcrud/actions/workflows/main.yml) [![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/erdlet/jcrud/blob/master/LICENSE)[![Maven Central](https://img.shields.io/maven-central/v/de.erdlet.jcrud/jcrud.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22de.erdlet.jcrud%22%20AND%20a:%22jcrud%22) 

Execute SQL queries in the style of Spring's JDBC Template - but without Spring :)

## Motivation behind this library?
I really like to work with Spring's `JdbcTemplate`, because it is easy to use and a nice
abstraction of the base JDBC API. But for projects which are not using Spring, adding the
`spring-jdbc` artifact causes the addition of nearly the complete Spring core to the project.

To avoid this problem, `jCRUD`shall be a dependency-less, small and easy to use alternative to Spring's great `JdbcTemplate`.

## Prerequisites
This library is built on top of **Java 11** and doesn't support older Java releases.
