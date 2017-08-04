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

package org.onosproject.yang.model;

/**
 * Represents mapping of java data types with corresponding yang leaf's data
 * type.
 */
public enum LeafType {

    /**
     * Represents INT8.
     */
    BYTE,

    /**
     * Represents INT16, UINT8.
     */
    SHORT,

    /**
     * Represents UINT16, INT32.
     */
    INT,

    /**
     * Represents UINT32, INT64.
     */
    LONG,

    /**
     * Represents BOOLEAN, EMPTY.
     */
    BOOLEAN,

    /**
     * Represents BITS, IDENTITYREF, ENUMERATION, STRING.
     */
    STRING,

    /**
     * Represents UINT64.
     */
    BIG_INTEGER,

    /**
     * Represents BASE64.
     */
    BYTE_ARRAY,

    /**
     * Represents DECIMAL64.
     * The decimal64 type represents a subset of the real numbers, which can
     * be represented by decimal numerals. The value space of decimal64 is
     * the set of numbers that can be obtained by multiplying a 64-bit
     * signed integer by a negative power of ten, i.e., expressible as
     * "i x 10^-n" where i is an integer64 and n is an integer between 1 and
     * 18, inclusively.
     */
    BIG_DECIMAL,

    /**
     * Represents union.
     */
    UNION
}
