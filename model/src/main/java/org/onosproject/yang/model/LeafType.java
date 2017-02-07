/*
 * Copyright 2016-present Open Networking Laboratory
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
 * Representation of leaf's type.
 */
public enum LeafType {

    /**
     * Represents JAVA short.
     */
    SHORT,

    /**
     * Represents JAVA int.
     */
    INT,

    /**
     * Represents JAVA long.
     */
    LONG,

    /**
     * Represents JAVA float.
     */
    FLOAT,

    /**
     * Represents JAVA double.
     */
    DOUBLE,

    /**
     * Represents JAVA boolean.
     */
    BOOLEAN,

    /**
     * Represents JAVA char.
     */
    CHAR,

    /**
     * Represents JAVA byte.
     */
    BYTE,

    /**
     * Represents JAVA string.
     */
    STRING,

    /**
     * Represents JAVA big integer.
     */
    BIG_INTEGER,

    /**
     * Represents JAVA enum type.
     */
    ENUM,

    /**
     * Represents big decimal.
     * The decimal64 type represents a subset of the real numbers, which can
     * be represented by decimal numerals. The value space of decimal64 is
     * the set of numbers that can be obtained by multiplying a 64-bit
     * signed integer by a negative power of ten, i.e., expressible as
     * "i x 10^-n" where i is an integer64 and n is an integer between 1 and
     * 18, inclusively.
     */
    BIG_DECIMAL,

    /**
     * Represents binary. The binary type represents any binary data,
     * i.e., a sequence of octets.
     */
    BINARY,

    /**
     * Represents bits. The bits type represents a bit set.  That
     * is, a bits value is a set of flags identified by small integer
     * position numbers starting at 0. Each bit number has an assigned name.
     */
    BITS,

    /**
     * Represents union type. The union type represents a value that
     * corresponds to one of its member types.
     */
    UNION,

    /**
     * The resource identifier type is used to uniquely identify a
     * particular instance node in the data tree.
     */
    RESOURCE_IDENTIFIER,

    /**
     * The identityref type is used to reference an existing identity.
     */
    IDENTITY_REF,

    /**
     * The leafref type is used to reference a particular leaf instance in
     * the data tree.
     */
    LEAF_REF,

    /**
     * The empty type represents a leaf that does not have any
     * value, it conveys information by its presence or absence.
     */
    EMPTY
}
