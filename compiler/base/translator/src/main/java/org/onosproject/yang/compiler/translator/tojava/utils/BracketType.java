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
 * Represents different bracket types.
 */
enum BracketType {

    //Open close bracket '()'.
    OPEN_CLOSE_BRACKET,

    //Open close bracket with value '(value)).
    OPEN_CLOSE_BRACKET_WITH_VALUE,

    //Open close bracket with value '(String value)).
    OPEN_CLOSE_BRACKET_WITH_VALUE_AND_RETURN_TYPE,

    //Open close bracket with value '(String value,.
    OPEN_BRACKET_WITH_VALUE,

    //Open close bracket with value ',String value)).
    CLOSE_BRACKET_WITH_VALUE,

    //Open close diamond bracket '<>'.
    OPEN_CLOSE_DIAMOND,

    //Open close diamond bracket with value '<String>'
    OPEN_CLOSE_DIAMOND_WITH_VALUE
}
