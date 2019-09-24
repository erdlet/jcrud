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
package de.erdlet.jdbc.crud.exception;

import de.erdlet.jdbc.crud.parameter.ParamSetter;

/**
 * Exception which has to be thrown in case the statement doesn't match to the {@link de.erdlet.jdbc.crud.CrudOperations}
 * selected method. For example, this is the case when an 'UPDATE' statement should be called inside {@link
 * de.erdlet.jdbc.crud.CrudOperations#insert(String, Object, ParamSetter)}. Technically, this may be the same, but the API of {@link
 * de.erdlet.jdbc.crud.CrudOperations}
 * and its usage should be clear and not confusing to the caller.
 *
 * @author Tobias Erdle
 */
public class InvalidStatementException extends RuntimeException {

  public InvalidStatementException(final Keywords expectedKeyword, final String statement) {
    super(String.format("Expected operation '%s' but got '%s'", expectedKeyword.name(), statement));
  }

  public enum Keywords {
    INSERT()
  }
}
