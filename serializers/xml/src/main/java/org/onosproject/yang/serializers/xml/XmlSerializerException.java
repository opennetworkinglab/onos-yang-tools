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

package org.onosproject.yang.serializers.xml;

/**
 * Represents base class for exceptions in XML serializer operations.
 */
public class XmlSerializerException extends RuntimeException {
    private static final long serialVersionUID = 20160211L;

    /**
     * Creates a new XML serializer exception.
     */
    public XmlSerializerException() {
    }

    /**
     * Creates a new XML serializer exception with given message.
     *
     * @param message the detail of exception in string
     */
    public XmlSerializerException(String message) {
        super(message);
    }

    /**
     * Creates a new XML serializer exception from given message and cause.
     *
     * @param message the detail of exception in string
     * @param cause   underlying cause of the error
     */
    public XmlSerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new XML serializer exception from cause.
     *
     * @param cause underlying cause of the error
     */
    public XmlSerializerException(Throwable cause) {
        super(cause);
    }
}
