/*
 * MIT License
 *
 * Copyright (c) 2019 Tobias Erdle
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package de.erdlet.jcrud;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.erdlet.jcrud.exception.InvalidStatementException;
import de.erdlet.jcrud.exception.TooManyResultsException;
import de.erdlet.jcrud.helper.model.Todo;
import de.erdlet.jcrud.results.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JCrudImplTest {

  private static BasicDataSource dataSource;
  private JCrudImpl systemUnderTest;

  @BeforeAll
  static void initDatabase() throws Exception {
    final var ds = new BasicDataSource();
    ds.setUrl("jdbc:h2:mem:test");
    ds.setUsername("sa");

    performDatabaseMigration(ds);

    dataSource = ds;
  }

  @BeforeEach
  void setUp() {
    this.systemUnderTest = new JCrudImpl(dataSource);
  }

  @AfterAll
  static void closeDb() throws Exception {
    dataSource.close();
  }

  @Test
  void testSelectExpectEmptyCollectionWhenNoResultIsFound() {
    final var result = systemUnderTest.select("SELECT * FROM EMPTY_TABLE", rs -> null);

    assertTrue(result.isEmpty());
  }

  @Test
  void testSelectExpectCorrectResultsWhenResultSetIsNotEmpty() {
    final var result = systemUnderTest.select("SELECT * FROM TODOS", rs -> new Todo(rs.getString("title"), rs.getString("body")));

    assertAll(
        () -> assertEquals(new Todo("My first todo", "An example todo for JDBC Mapper"), result.get(0)),
        () -> assertEquals(new Todo("Get stuff from bakery", "bread"), result.get(1)),
        () -> assertEquals(new Todo("Buy milk", null), result.get(2))
    );
  }

  @Test
  void testSelectWithParameterExpectEmptyResultWhenNoResultIsFound() {
    final var result = systemUnderTest.select("SELECT * FROM TODOS t WHERE t.title = ?", new TodoRowMapper(), "Unknown name");

    assertTrue(result.isEmpty());
  }

  @Test
  void testSelectSingleWhenNoResultReturnsExpectEmptyOptional() {
    final var result = systemUnderTest.selectSingle("SELECT * FROM TODOS t WHERE t.title = ?", new TodoRowMapper(), "Unknown name");

    assertTrue(result.isEmpty());
  }

  @Test
  void testSelectSingleWhenSingleResultReturnsExpectFilledOptional() {
    final var expectedTodo = new Todo("Buy milk", null);

    final var result = systemUnderTest.selectSingle("SELECT * FROM TODOS t WHERE t.title = ?", new TodoRowMapper(), "Buy milk");

    assertEquals(expectedTodo, result.get());
  }

  @Test
  void testSelectSingleWhenMultipleResultReturnsExpectTooManyResultsException() {
    assertThrows(TooManyResultsException.class,
        () -> systemUnderTest.selectSingle("SELECT * FROM TODOS t", new TodoRowMapper()));
  }

  @Test
  void testSelectSingleWhenMultipleResultReturnsExpectExceptionMessageContainsQuery() {
    final var exception = assertThrows(TooManyResultsException.class,
        () -> systemUnderTest.selectSingle("SELECT * FROM TODOS t", new TodoRowMapper()));

    assertEquals("Too many results for query 'SELECT * FROM TODOS t' with params '[]'", exception.getMessage());
  }

  @Test
  void testInsertExpectResultIsSavedInDatabase() {
    final var entity = new Todo("Neues Todo", "Neuer Todo Body");
    systemUnderTest.insert("INSERT INTO TODOS (TITLE, BODY) VALUES (?, ?)", entity, pstmt -> {
      pstmt.setString(1, entity.getTitle());
      pstmt.setString(2, entity.getBody());
    });

    final var result = systemUnderTest.select("SELECT * FROM TODOS t WHERE t.title = ?", new TodoRowMapper(), entity.getTitle());

    assertEquals(entity, result.get(0));
  }

  @Test
  void testInsertThrowsExceptionWhenNoInsertStatementIsProvided() {
    final var entity = new Todo("Neues Todo", "Neuer Todo Body");

    assertThrows(InvalidStatementException.class,
        () -> systemUnderTest.insert("UPDATE TODOS SET t.title='Foo'", entity, pstmt -> {
          pstmt.setString(1, entity.getTitle());
          pstmt.setString(2, entity.getBody());
        }));
  }


  private static void performDatabaseMigration(final DataSource dataSource) throws SQLException {
    try (final var conn = dataSource.getConnection()) {
      try (final var statement = conn.prepareStatement("CREATE TABLE TODOS (\n"
          + "    ID INT PRIMARY KEY AUTO_INCREMENT ,\n"
          + "    TITLE VARCHAR NOT NULL ,\n"
          + "    BODY VARCHAR\n"
          + ");\n"
          + "\n"
          + "CREATE TABLE EMPTY_TABLE (\n"
          + "    ID INT PRIMARY KEY AUTO_INCREMENT\n"
          + ");\n"
          + "\n"
          + "INSERT INTO TODOS (TITLE, BODY) VALUES ('My first todo', 'An example todo for JDBC Mapper');\n"
          + "INSERT INTO TODOS (TITLE, BODY) VALUES ('Get stuff from bakery', 'bread');\n"
          + "INSERT INTO TODOS (TITLE, BODY) VALUES ('Buy milk', null);")) {

        statement.execute();
      }
    }
  }

  private static class TodoRowMapper implements RowMapper<Todo> {

    @Override
    public Todo map(final ResultSet rs) throws SQLException {
      return new Todo(rs.getString("title"), rs.getString("body"));
    }
  }
}