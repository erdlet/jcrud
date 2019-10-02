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
package de.erdlet.jcrud.results;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper which maps the columns of a {@link ResultSet} row to the expected type.
 *
 * @param <T> the type of the expected result class
 * @author Tobias Erdle
 */
public interface RowMapper<T> {

  /**
   * Maps the current {@link ResultSet} row to a new instance of the expected type.
   *
   * @param rs the current, not closed {@link ResultSet}
   * @return an instance of the target type with values from the {@link ResultSet}
   * @throws SQLException in case problems occur during the {@link ResultSet} processing
   */
  T map(final ResultSet rs) throws SQLException;

}
