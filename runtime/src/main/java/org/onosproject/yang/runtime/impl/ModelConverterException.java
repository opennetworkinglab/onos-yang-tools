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

package org.onosproject.yang.runtime.impl;

/**
 * Represents exception that needs to be handled by model converter.
 */
// extending ModelConvertorException for backward compatibility,
// directly extend RuntimeException when removing Exception class with typo
@SuppressWarnings("deprecation")
class ModelConverterException
    extends ModelConvertorException {

    private static final long serialVersionUID = 4586537426529302237L;

    /**
     * Creates  model converter exception with an exception message.
     *
     * @param exceptionMessage message with which exception must be thrown
     */
    ModelConverterException(String exceptionMessage) {
        super(exceptionMessage);
    }

    /**
     * Creates  model converter exception with an exception message and cause.
     *
     * @param exceptionMessage message with which exception must be thrown
     * @param cause cause of the exception
     */
    ModelConverterException(String exceptionMessage, Throwable cause) {
        super(exceptionMessage, cause);
    }

    /**
     * Creates  model converter exception with the cause for it.
     *
     * @param cause cause of the exception
     */
    ModelConverterException(Throwable cause) {
        super(cause);
    }
}
