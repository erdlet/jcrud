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

import java.util.List;
import java.util.Optional;
import de.erdlet.jcrud.results.RowMapper;
import de.erdlet.jcrud.exception.DatabaseException;
import de.erdlet.jcrud.parameter.ParamSetter;

/**
 * Interface containing all CRUD operations on a database.
 *
 * @author Tobias Erdle
 */
public interface CrudOperations {

  <T> List<T> select(final String query, final RowMapper<T> rowMapper, final Object... params);

  /**
   * Select an single entity from the database. It is expected that the query either returns one or no result. In case
   * the query returns a list of results, it is an exceptional behaviour.
   *
   * @param query the query to execute
   * @param rowMapper the {@link RowMapper} to map the result columns to the entity
   * @param params the query parameter for resolving the entity
   * @param <T> the target type of the entity
   * @return optionally the found entity or an empty result if no entity was found
   * @throws de.erdlet.jcrud.exception.TooManyResultsException in case there is more than one result
   */
  <T> Optional<T> selectSingle(final String query, final RowMapper<T> rowMapper, final Object... params);

  /**
   * Insert an entity into the database.
   *
   * @param statement the statement to execute for insertion
   * @param entity the entity to be inserted
   * @param paramSetter the {@link ParamSetter} which sets the entity attributes into the {@link java.sql.PreparedStatement}
   * @param <T> the type of the entity to be saved
   * @throws DatabaseException in case an {@link java.sql.SQLException} is thrown by the underneath driver
   * @throws de.erdlet.jcrud.exception.InvalidStatementException in case the statement is no INSERT statement
   */
  <T> void insert(final String statement, final T entity, final ParamSetter paramSetter);

}
