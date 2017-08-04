/*
 * Copyright 2017-present Open Networking Foundation
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

package org.onosproject.yang.runtime;

/**
 * Represents base class for exceptions in YANG runtime operations.
 */
public class YangRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 20161028L;

    /**
     * Creates a new YANG runtime exception with given message. Exception
     * message received from serializer is conveyed in this message.
     *
     * @param message the detail of exception in string
     */
    public YangRuntimeException(String message) {
        super(message);
    }

    /**
     * Creates a new YANG runtime exception from given message and cause.
     * Exception message and cause received from serializer is conveyed in this
     * message.
     *
     * @param message the detail of exception in string
     * @param cause   underlying cause of the error
     */
    public YangRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new YANG runtime exception with given message. Exception
     * cause received from serializer is conveyed in this message.
     *
     * @param cause underlying cause of the error
     */
    public YangRuntimeException(final Throwable cause) {
        super(cause);
    }
}
