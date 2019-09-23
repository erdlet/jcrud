package de.erdlet.jdbc.crud;

public final class DatabaseException extends RuntimeException {

  private static final long serialVersionUID = -5230497776370845839L;

  public DatabaseException(final Throwable cause) {
    super(cause);
  }
}
