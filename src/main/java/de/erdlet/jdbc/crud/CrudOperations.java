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
package de.erdlet.jdbc.crud;

import de.erdlet.jdbc.crud.exception.DatabaseException;
import de.erdlet.jdbc.crud.parameter.ParamSetter;

/**
 * Interface containing all CRUD operations on a database.
 *
 * @author Tobias Erdle
 */
public interface CrudOperations extends ReadOperations {

  /**
   * Insert an entity into the database.
   *
   * @param statement the statement to execute for insertion
   * @param entity the entity to be inserted
   * @param paramSetter the {@link ParamSetter} which sets the entity attributes into the {@link java.sql.PreparedStatement}
   * @param <T> the type of the entity to be saved
   * @throws DatabaseException in case an {@link java.sql.SQLException} is thrown by the underneath driver
   */
  <T> void insert(final String statement, final T entity, final ParamSetter paramSetter);

}
