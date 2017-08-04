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

package org.onosproject.yang.compiler.translator.tojava.utils;

/**
 * Represents translator error type.
 */
public enum TranslatorErrorType {
    /**
     * Represents the invalid node for translation.
     */
    INVALID_TRANSLATION_NODE("Invalid node for translation"),

    /**
     * Represents the missing parent node.
     */
    MISSING_PARENT_NODE("Missing parent node to get current node's java " +
                                "information"),

    /**
     * Represents the invalid parent node.
     */
    INVALID_PARENT_NODE("Invalid parent node to get current node's java " +
                                "information"),

    /**
     * Represents the invalid holder of leaf.
     */
    INVALID_LEAF_HOLDER("Invalid holder of leaf"),

    /**
     * Represents the invalid child node.
     */
    INVALID_CHILD_NODE("Invalid child of node "),

    /**
     * Represents the invalid leaf list without JAVA information.
     */
    INVALID_LEAF_LIST("Leaf-list does not have java information"),

    /**
     * Represents the invalid leaf without JAVA information.
     */
    INVALID_LEAF("Leaf does not have java information"),

    /**
     * Represents the invalid node without JAVA information.
     */
    INVALID_NODE("Missing java file information to get the package details " +
                         "of attribute corresponding to child node"),

    /**
     * Represents that code generation failed for a node at exit.
     */
    FAIL_AT_EXIT("Failed to generate code for "),

    /**
     * Represents that code generation preparation failed for a node at entry.
     */
    FAIL_AT_ENTRY("Failed to prepare generate code entry for ");

    // Prefix of an error type.
    private final String prefix;

    /**
     * Creates translator error type.
     *
     * @param p prefix string
     */
    TranslatorErrorType(String p) {
        prefix = p;
    }

    /**
     * Returns prefix for a given enum type.
     *
     * @return prefix
     */
    public String prefix() {
        return prefix;
    }
}
