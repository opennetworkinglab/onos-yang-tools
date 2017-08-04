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

package org.onosproject.yang.compiler.translator.tojava;

/**
 * Represents type of temporary files generated.
 */
public final class GeneratedTempFileType {

    /**
     * Attributes definition temporary file.
     */
    public static final int ATTRIBUTES_MASK = 1; // 1 << 0

    /**
     * Getter methods for interface.
     */
    public static final int GETTER_FOR_INTERFACE_MASK = 1 << 1;

    /**
     * Getter methods for class.
     */
    public static final int GETTER_FOR_CLASS_MASK = 1 << 2;

    /**
     * Setter methods for interface.
     */
    public static final int SETTER_FOR_INTERFACE_MASK = 1 << 3;

    /**
     * Setter methods for class.
     */
    public static final int SETTER_FOR_CLASS_MASK = 1 << 4;

    /**
     * Constructor method of class.
     */
    public static final int CONSTRUCTOR_IMPL_MASK = 1 << 5;

    /**
     * Hash code implementation of class.
     */
    public static final int HASH_CODE_IMPL_MASK = 1 << 6;

    /**
     * Equals implementation of class.
     */
    public static final int EQUALS_IMPL_MASK = 1 << 7;

    /**
     * To string implementation of class.
     */
    public static final int TO_STRING_IMPL_MASK = 1 << 8;

    /**
     * Of string implementation of class.
     */
    public static final int OF_STRING_IMPL_MASK = 1 << 9;

    /**
     * Constructor for type class like typedef, union.
     */
    public static final int CONSTRUCTOR_FOR_TYPE_MASK = 1 << 10;

    /**
     * From string implementation of class.
     */
    public static final int FROM_STRING_IMPL_MASK = 1 << 11;

    /**
     * Enum implementation of class.
     */
    public static final int ENUM_IMPL_MASK = 1 << 12;

    /**
     * Rpc interface of module / sub module.
     */
    public static final int RPC_INTERFACE_MASK = 1 << 13;

    /**
     * Rpc implementation of module / sub module.
     */
    public static final int RPC_IMPL_MASK = 1 << 14;

    /**
     * Event enum implementation of class.
     */
    public static final int EVENT_ENUM_MASK = 1 << 15;

    /**
     * Event method implementation of class.
     */
    public static final int EVENT_METHOD_MASK = 1 << 16;

    /**
     * Event subject attribute implementation of class.
     */
    public static final int EVENT_SUBJECT_ATTRIBUTE_MASK = 1 << 17;

    /**
     * Event subject getter implementation of class.
     */
    public static final int EVENT_SUBJECT_GETTER_MASK = 1 << 18;

    /**
     * Event subject setter implementation of class.
     */
    public static final int EVENT_SUBJECT_SETTER_MASK = 1 << 19;

    /**
     * Add to list method interface for class.
     */
    public static final int ADD_TO_LIST_INTERFACE_MASK = 1 << 20;

    /**
     * Add to list method implementation for class.
     */
    public static final int ADD_TO_LIST_IMPL_MASK = 1 << 21;

    /**
     * Leaf identifier enum attributes for class.
     */
    public static final int LEAF_IDENTIFIER_ENUM_ATTRIBUTES_MASK = 1 << 22;

    /**
     * Edit config class content for class.
     */
    public static final int EDIT_CONTENT_MASK = 1 << 26;

    // No instantiation.
    private GeneratedTempFileType() {
    }
}
