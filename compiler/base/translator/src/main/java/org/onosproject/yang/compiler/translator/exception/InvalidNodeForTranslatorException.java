/*
 * Copyright 2016-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.yang.compiler.translator.exception;

/**
 * Represents custom translator exception for translator's operations.
 */
public class InvalidNodeForTranslatorException extends RuntimeException {

    private static final long serialVersionUID = 20160311L;
    private transient String fileName;
    private transient int lineNumber;
    private transient int charPosition;

    /**
     * Creates a new exception.
     */
    public InvalidNodeForTranslatorException() {
        super();
    }

    /**
     * Creates a new exception with given message.
     *
     * @param message the detail of exception in string
     */
    public InvalidNodeForTranslatorException(String message) {
        super(message);
    }

    /**
     * Creates a new exception from given message and cause.
     *
     * @param message the detail of exception in string
     * @param cause   underlying cause of the error
     */
    public InvalidNodeForTranslatorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception from cause.
     *
     * @param cause underlying cause of the error
     */
    public InvalidNodeForTranslatorException(final Throwable cause) {
        super(cause);
    }

    /**
     * Returns generated file name for the exception.
     *
     * @return generated file name for the exception
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Returns line number of the exception.
     *
     * @return line number of the exception
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * Returns position of the exception.
     *
     * @return position of the exception
     */
    public int getCharPositionInLine() {
        return this.charPosition;
    }

    /**
     * Sets line number of YANG file.
     *
     * @param line line number of YANG file
     */
    public void setLine(int line) {
        this.lineNumber = line;
    }

    /**
     * Sets position of exception.
     *
     * @param charPosition position of exception
     */
    public void setCharPosition(int charPosition) {
        this.charPosition = charPosition;
    }

    /**
     * Sets file name in exception.
     *
     * @param fileName YANG file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
