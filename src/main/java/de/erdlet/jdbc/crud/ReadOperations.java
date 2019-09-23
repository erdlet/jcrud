package de.erdlet.jdbc.crud;

import de.erdlet.jdbc.crud.results.RowMapper;
import java.util.Collection;

public interface ReadOperations {

  <T> Collection<T> select(final String query, final RowMapper<T> rowMapper, final Object... params);
}
