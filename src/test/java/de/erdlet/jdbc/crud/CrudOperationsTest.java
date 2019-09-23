package de.erdlet.jdbc.crud;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.erdlet.jdbc.crud.model.Todo;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CrudOperationsTest {

  private static BasicDataSource dataSource;
  private CrudOperations systemUnderTest;

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
    this.systemUnderTest = new CrudOperations(dataSource);
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
    final var result = systemUnderTest.select("SELECT * FROM TODOS t WHERE t.title = ?",
        rs -> new Todo(rs.getString("title"), rs.getString("body")), "Get some stuff");

    assertTrue(result.isEmpty());
  }

  private static void performDatabaseMigration(final DataSource dataSource) throws SQLException {
    try (final var conn = dataSource.getConnection()) {
      try(final var statement = conn.prepareStatement("CREATE TABLE TODOS (\n"
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
}