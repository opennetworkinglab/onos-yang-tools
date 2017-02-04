/*
 * Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang.runtime.api;

/**
 * Representation of annotated attribute.
 */
public interface Annotation {

    /**
     * Returns name of an annotated attribute.
     *
     * @return name of the attribute
     */
    String name();

    /**
     * Sets name of the annotated attribute.
     *
     * @param name of the attribute
     */
    void name(String name);

    /**
     * Returns value of the annotation.
     *
     * @return annotation value
     */
    String value();

    /**
     * Sets value of the annotation.
     *
     * @param value value of the annotation
     */
    void value(String value);
}