package de.erdlet.jdbc.crud;

import de.erdlet.jdbc.crud.results.RowMapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class CrudOperations implements ReadOperations, WriteOperations {

  private final DataSource dataSource;

  public CrudOperations(final DataSource dataSource) {
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
