package de.erdlet.jdbc.crud.results;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {

  T map(final ResultSet rs) throws SQLException;

}
