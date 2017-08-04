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
 * Represents method body types.
 */
enum MethodBodyTypes {

    //Getter method body.
    GETTER,

    //Setter method body.
    SETTER,

    //Constructor method body.
    CONSTRUCTOR,

    //Default constructor method body.
    DEFAULT_CONSTRUCTOR,

    //Hash code method body.
    HASH_CODE,

    //To string method body.
    TO_STRING,

    //Equals method body.
    EQUALS,

    //add to list method body.
    ADD_TO_LIST,

    //Manager methods
    MANAGER_METHODS,

    //Of method.
    OF_METHOD,

    //Hash code method
    HASH_CODE_METHOD,

    //Equals method.
    EQUALS_METHOD,

    //To string method
    TO_STRING_METHOD,

    //Enum method int value.
    ENUM_METHOD_INT_VALUE,

    //Enum method string value.
    ENUM_METHOD_STRING_VALUE

}
