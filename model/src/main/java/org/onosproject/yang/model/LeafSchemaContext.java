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
 * Representation of a context for obtaining leaf/leaf-list schema related
 * extended information.
 */
public interface LeafSchemaContext extends SchemaContext {

    /**
     * Returns type of the leaf object.
     *
     * @return type of leaf object
     */
    LeafObjectType getLeafObjectType();

    /**
     * Returns the restrictions associated with leaf/leaf-list. Returns null
     * if no restrictions are associated with leaf.
     *
     * @param <T> restriction type
     * @return restriction
     */
    <T extends LeafRestriction> T getLeafRestrictions();

    /**
     * Returns object from string value of a leaf.
     *
     * @param value leaf value in string
     * @return leaf's object
     * @throws IllegalArgumentException when input value is not as per the
     *                                  schema
     */
    Object fromString(String value);

    /**
     * Returns value namespace.
     *
     * @param value leaf value in string
     * @return value namespace, null indicates that value namespace is same
     * as leaf namespace
     */
    YangNamespace getValueNamespace(String value);

    /**
     * Returns type of the leaf.
     *
     * @param value leaf value in string
     * @return type of leaf
     * @throws IllegalArgumentException when input value is not as per the
     *                                  schema
     */
    LeafType getLeafType(String value) throws IllegalArgumentException;
}
