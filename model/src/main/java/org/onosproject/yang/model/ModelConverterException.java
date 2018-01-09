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

package org.onosproject.yang.model;

/**
 * Represents exception that needs to be handled by model converter.
 */
public class ModelConverterException extends RuntimeException {

    private static final long serialVersionUID = 4586537426529302237L;

    /**
     * Creates  model converter exception with an exception message.
     *
     * @param exceptionMessage message with which exception must be thrown
     */
    public ModelConverterException(String exceptionMessage) {
        super(exceptionMessage);
    }

    /**
     * Creates  model converter exception with an exception message and cause.
     *
     * @param exceptionMessage message with which exception must be thrown
     * @param cause cause of the exception
     */
    public ModelConverterException(String exceptionMessage, Throwable cause) {
        super(exceptionMessage, cause);
    }

    /**
     * Creates  model converter exception with the cause for it.
     *
     * @param cause cause of the exception
     */
    public ModelConverterException(Throwable cause) {
        super(cause);
    }
}
