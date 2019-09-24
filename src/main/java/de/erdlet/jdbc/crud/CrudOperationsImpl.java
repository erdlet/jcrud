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

import de.erdlet.jdbc.crud.parameter.ParamSetter;
import de.erdlet.jdbc.crud.results.RowMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * Core class containing all possible CRUD operations for a {@link DataSource}. This class is designed to be
 * used independent from any technology like CDI or Spring.
 * <p>
 * To use the {@link CrudOperationsImpl}, you can simply create an instance with any datasource. In case you want it to use with CDI,
 * you can create a small producer to configure the {@link CrudOperationsImpl}:
 * <pre>
 * {@code
 * public class CrudOperationsProducer {
 *
 *  @Produces
 *  @ApplicationScoped
 *  public CrudOperations crudOperations(final DataSource datasource) {
 *    return new CrudOperationsImpl(datasource);
 *  }
 *
 * }
 * }
 * </pre>
 * If you prefer to use Spring instead, you can use a Java config like this for example:
 * <pre>
 * {@code
 * @Configuration
 * public class CrudOperationsConfig {
 *
 *  @Bean
 *  public CrudOperations crudOperations(final DataSource datasource) {
 *    return new CrudOperationsImpl(datasource);
 *  }
 *
 * }
 * }
 * </pre>
 *
 * @author Tobias Erdle
 */
@SuppressFBWarnings(value = {"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE"},
    justification = "False positive warnings when using try-with-resource in Java 11")
public class CrudOperationsImpl implements CrudOperations {

  private final DataSource dataSource;

  public CrudOperationsImpl(final DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public <T> List<T> select(final String query, final RowMapper<T> rowMapper, final Object... params) {
    try (final var connection = dataSource.getConnection();
        final var pstmt = connection.prepareStatement(query)) {
      applyStatementParams(pstmt, params);

      return executeQuery(pstmt, rowMapper);
    } catch (final SQLException ex) {
      throw new DatabaseException(ex);
    }
  }

  @Override
  public <T> void insert(final String statement, final T entity, final ParamSetter paramSetter) {
    try (final var connection = dataSource.getConnection();
        final var pstmt = connection.prepareStatement(statement)) {

      paramSetter.setStatementParams(pstmt);

      pstmt.execute();
    } catch (final SQLException ex) {
      throw new DatabaseException(ex);
    }
  }

  private void applyStatementParams(final PreparedStatement pstmt, final Object[] params) throws SQLException {
    for (int i = 1; i <= params.length; i++) {
      pstmt.setObject(i, params[i - 1]);
    }
  }

  private <T> List<T> executeQuery(final PreparedStatement pstmt, final RowMapper<T> rowMapper) throws SQLException {
    try (final var rs = pstmt.executeQuery()) {

      final var result = new ArrayList<T>();
      while (rs.next()) {
        result.add(rowMapper.map(rs));
      }

      return result;
    }
  }
}
