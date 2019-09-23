package de.erdlet.jdbc.crud.model;

import java.util.Objects;

public final class Todo {

  private final String title;
  private final String body;

  public Todo(final String title, final String body) {
    this.title = title;
    this.body = body;
  }

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Todo todo = (Todo) o;
    return Objects.equals(title, todo.title) &&
        Objects.equals(body, todo.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, body);
  }

  @Override
  public String toString() {
    return "Todo{" +
        "title='" + title + '\'' +
        ", body='" + body + '\'' +
        '}';
  }
}
