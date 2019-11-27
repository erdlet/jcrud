/*
 * MIT License
 *
 * Copyright (c) 2019 Tobias Erdle
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
import de.erdlet.jcrud.parameter.ParamSetter;
import de.erdlet.jcrud.results.RowMapper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void truncateTablesAfterTest() throws Exception {
        truncateTables(dataSource);
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
        final var todo1 = new Todo("My first todo", "An example todo for JDBC Mapper");
        final var todo2 = new Todo("Get stuff from bakery", "bread");
        final var todo3 = new Todo("Buy milk", null);

        List.of(todo1, todo2, todo3).forEach(JCrudImplTest::insertTodo);

        final var result = systemUnderTest.select("SELECT * FROM TODOS",
            rs -> new Todo(rs.getString("title"), rs.getString("body")));

        assertAll(
            () -> assertEquals(todo1, result.get(0)),
            () -> assertEquals(todo2, result.get(1)),
            () -> assertEquals(todo3, result.get(2)));
    }

    @Test
    void testSelectWithParameterExpectEmptyResultWhenNoResultIsFound() {
        final var result = systemUnderTest.select("SELECT * FROM TODOS t WHERE t.title = ?",
            new TodoRowMapper(), "Unknown name");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSelectSingleWhenNoResultReturnsExpectEmptyOptional() {
        final var result = systemUnderTest.selectSingle("SELECT * FROM TODOS t WHERE t.title = ?",
            new TodoRowMapper(), "Unknown name");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSelectSingleWhenSingleResultReturnsExpectFilledOptional() {
        final var expectedTodo = new Todo("Buy milk", null);

        insertTodo(expectedTodo);

        final var result = systemUnderTest.selectSingle("SELECT * FROM TODOS t WHERE t.title = ?",
            new TodoRowMapper(), "Buy milk");

        assertEquals(expectedTodo, result.get());
    }

    @Test
    void testSelectSingleWhenMultipleResultReturnsExpectTooManyResultsException() {
        insertTodo(new Todo("First todo", "Do something"));
        insertTodo(new Todo("Second todo", "Do something other"));

        assertThrows(TooManyResultsException.class,
            () -> systemUnderTest.selectSingle("SELECT * FROM TODOS t", new TodoRowMapper()));
    }

    @Test
    void testSelectSingleWhenMultipleResultReturnsExpectExceptionMessageContainsQuery() {
        insertTodo(new Todo("First todo", "Do something"));
        insertTodo(new Todo("Second todo", "Do something other"));

        final var exception = assertThrows(TooManyResultsException.class,
            () -> systemUnderTest.selectSingle("SELECT * FROM TODOS t", new TodoRowMapper()));

        assertEquals("Too many results for query 'SELECT * FROM TODOS t' with params '[]'",
            exception.getMessage());
    }

    @Test
    void testInsertExpectResultIsSavedInDatabase() {
        final var entity = new Todo("Neues Todo", "Neuer Todo Body");
        systemUnderTest.insert("INSERT INTO TODOS (TITLE, BODY) VALUES (?, ?)", entity, new TodoParamSetter());

        final var result = systemUnderTest.select("SELECT * FROM TODOS t WHERE t.title = ?",
            new TodoRowMapper(), entity.getTitle());

        assertEquals(entity, result.get(0));
    }

    @Test
    void testInsertThrowsExceptionWhenNoInsertStatementIsProvided() {
        final var entity = new Todo("Neues Todo", "Neuer Todo Body");

        assertThrows(InvalidStatementException.class,
            () -> systemUnderTest.insert("UPDATE TODOS SET t.title='Foo'", entity, new TodoParamSetter()));
    }

    @Test
    void testInsertWithMultipleEntitiesThrowsExceptionWhenStatementIsNotInsert() {
        assertThrows(InvalidStatementException.class,
            () -> systemUnderTest.insert("SELECT * FROM FOOBAR", List.of(new Todo("Foo", "Bar")), (ParamSetter<Todo>) (entity, pstmt) -> {
            }));
    }

    @Test
    void testInsertWithMulitpleEntitiesSavesSingleEntityWhenOnlyOneIsProvided() {
        final var entity = new Todo("My todo", "");

        systemUnderTest.insert("INSERT INTO TODOS (TITLE, BODY) VALUES (?, ?)", Collections.singletonList(entity),
            new TodoParamSetter());

        final var result = systemUnderTest.selectSingle("SELECT * FROM TODOS t WHERE t.title = ?",
            new TodoRowMapper(), entity.getTitle());

        assertTrue(result.isPresent());
    }

    @Test
    void testInsertWithMultipleEntitiesSavesMultipleValues() {
        final var entity1 = new Todo("First todo", "");
        final var entity2 = new Todo("Second todo", "");
        final var entity3 = new Todo("Third todo", "");

        systemUnderTest.insert("INSERT INTO TODOS (TITLE, BODY) VALUES (?, ?)", List.of(entity1, entity2, entity3),
            new TodoParamSetter());

        final var results = systemUnderTest.select("SELECT * FROM TODOS", new TodoRowMapper());

        assertAll(
            () -> assertEquals(entity1, results.get(0)),
            () -> assertEquals(entity2, results.get(1)),
            () -> assertEquals(entity3, results.get(2))
        );
    }

    @Test
    void testUpdateThrowsExceptionWhenNoUpdateStatementIsProvided() {
        final var entity = new Todo("Stored entity", "To be updated!");

        assertThrows(InvalidStatementException.class, () -> systemUnderTest
            .update("INSERT INTO TODOS (TITLE, BODY) VALUES ('Buy milk', null);", entity, new TodoParamSetter()));
    }

    @Test
    void testUpdateExpectEntityToBeUpdatedSuccessfully() {
        final var expectedTitle = "Changed title";
        final var entity = new Todo("Title to change", "To be updated!");

        insertTodo(entity);

        entity.changeTitle(expectedTitle);

        systemUnderTest.update("UPDATE TODOS SET TITLE = ? WHERE TITLE='Title to change'", entity,
            (ent, pstmt) -> {
                pstmt.setString(1, ent.getTitle());
            });

        final var result = systemUnderTest.selectSingle("SELECT * FROM TODOS t WHERE t.title = ?",
            new TodoRowMapper(), expectedTitle);

        assertEquals(expectedTitle, result.get().getTitle());
    }

    @Test
    void testDeleteThrowsExceptionWhenNoDeleteStatementIsProvided() {
        final var entity = new Todo("Stored entity", "To be Deleted!");

        assertThrows(InvalidStatementException.class, () -> systemUnderTest
            .update("INSERT INTO TODOS (TITLE, BODY) VALUES ('Buy milk', null);", entity, new TodoParamSetter()));
    }

    @Test
    void testDeleteExpectEntityToBeDeletedSuccessfully() {
        final var entity = new Todo("Title to delete", "To be delete!");

        insertTodo(entity);

        systemUnderTest.delete("DELETE FROM TODOS WHERE TITLE=?", entity, (ent, pstmt) -> {
            pstmt.setString(1, ent.getTitle());
        });

        final var result = systemUnderTest.selectSingle("SELECT * FROM TODOS t WHERE t.title = ?",
            new TodoRowMapper(), "Title to delete");

        assertTrue(result.isEmpty());
    }

    @Test
    void testCountExpectExpectionWhenStatementIsNoCountStatement() {
        assertThrows(InvalidStatementException.class, () -> systemUnderTest.count("SELECT * FROM foobar"));
    }

    @Test
    void testCountExpectZeroWhenNoResultsAreFound() {
        final var count = systemUnderTest.count("SELECT COUNT(id) FROM EMPTY_TABLE");

        assertEquals(0, count);
    }

    @Test
    void testCountExpectCorrectAmountWhenResultsAreFound() {
        insertTodo(new Todo("First todo", ""));
        insertTodo(new Todo("Second todo", ""));

        final var count = systemUnderTest.count("SELECT COUNT(id) FROM TODOS");

        assertEquals(2, count);
    }

    private static void insertTodo(final Todo todo) {
        try (final var conn = dataSource.getConnection();
            final var pstmt = conn.prepareStatement("INSERT INTO TODOS (TITLE, BODY) VALUES (?, ?)")) {
            conn.setAutoCommit(true);

            pstmt.setString(1, todo.getTitle());
            pstmt.setString(2, todo.getBody());

            pstmt.execute();
        } catch (final SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void performDatabaseMigration(final DataSource dataSource) throws SQLException {
        try (final var conn = dataSource.getConnection()) {
            try (final var statement = conn.prepareStatement("CREATE TABLE TODOS (\n"
                + "    ID INT PRIMARY KEY AUTO_INCREMENT ,\n" + "    TITLE VARCHAR NOT NULL ,\n"
                + "    BODY VARCHAR\n" + ");\n" + "\n" + "CREATE TABLE EMPTY_TABLE (\n"
                + "    ID INT PRIMARY KEY AUTO_INCREMENT\n" + ");")) {

                conn.setAutoCommit(true);

                statement.execute();
            }
        }
    }

    private static void truncateTables(final DataSource dataSource) throws SQLException {
        try (final var conn = dataSource.getConnection();
            final var statement = conn.prepareStatement("TRUNCATE TABLE TODOS")) {

            conn.setAutoCommit(true);

            statement.execute();
        }
    }

    private static class TodoRowMapper implements RowMapper<Todo> {

        @Override
        public Todo map(final ResultSet rs) throws SQLException {
            return new Todo(rs.getString("title"), rs.getString("body"));
        }
    }

    private static class TodoParamSetter implements ParamSetter<Todo> {

        @Override
        public void setStatementParams(final Todo entity, final PreparedStatement pstmt) throws SQLException {
            pstmt.setString(1, entity.getTitle());
            pstmt.setString(2, entity.getBody());
        }
    }
}
