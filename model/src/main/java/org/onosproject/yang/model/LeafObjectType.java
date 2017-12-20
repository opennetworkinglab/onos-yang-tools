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
 * Represents mapping of java data types with corresponding yang leaf's data
 * type.
 */
public enum LeafObjectType {

    /**
     * Represents YANG "int8" mapped to JAVA "byte".
     */
    BYTE,

    /**
     * Represents YANG "int16", "uint8" mapped to JAVA "short".
     */
    SHORT,

    /**
     * Represents YANG "uint16", "int32" mapped to JAVA "int".
     */
    INT,

    /**
     * Represents YANG "uint32", "int64" mapped to JAVA "long".
     */
    LONG,

    /**
     * Represents YANG "boolean", "empty" mapped to JAVA "boolean".
     */
    BOOLEAN,

    /**
     * Represents YANG "bits", "identityref", "enumeration", "string" mapped to
     * JAVA "String".
     */
    STRING,

    /**
     * Represents YANG "uint64" mapped to JAVA "BigInteger".
     */
    BIG_INTEGER,

    /**
     * Represents YANG "binary" mapped to JAVA "byte[]".
     */
    BYTE_ARRAY,

    /**
     * Represents YANG decimal64 mapped to JAVA BigDecimal.
     * The decimal64 type represents a subset of the real numbers, which can
     * be represented by decimal numerals. The value space of decimal64 is
     * the set of numbers that can be obtained by multiplying a 64-bit
     * signed integer by a negative power of ten, i.e., expressible as
     * "i x 10^-n" where i is an integer64 and n is an integer between 1 and
     * 18, inclusively.
     */
    BIG_DECIMAL,

    /**
     * Represents YANG union, in JAVA it's mapped to a custom generated
     * class. LeafNode value would be of one among the other types
     * listed in LeafType.
     */
    UNION
}
