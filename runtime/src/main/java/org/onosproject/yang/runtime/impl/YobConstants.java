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

package org.onosproject.yang.runtime.impl;

/**
 * Represents common constant utility for YANG object builder.
 */
final class YobConstants {

    private YobConstants() {
    }

    static final String FROM_STRING = "fromString";
    static final String OP_PARAM = "OpParam";
    static final String DEFAULT = "Default";
    static final String ADD_TO = "addTo";
    static final String OF = "of";
    static final String PERIOD = ".";
    static final String ADD_AUGMENT_METHOD = "addAugmentation";
    static final String YANG = "yang";
    static final String JAVA_LANG = "java.lang";

    //Error strings
    static final String E_HAS_NO_CHILD = " does not have child ";
    static final String E_FAIL_TO_GET_FIELD = "Failed to get field for" +
            " class: ";
    static final String L_FAIL_TO_GET_FIELD =
            "Failed to get field for class: {}";
    static final String E_FAIL_TO_GET_METHOD =
            "Failed to get method for class: ";
    static final String L_FAIL_TO_GET_METHOD =
            "Failed to get method for class: {}";
    static final String E_FAIL_TO_LOAD_CLASS =
            "Failed to load class for class: ";
    static final String E_FAIL_TO_LOAD_LEAF_IDENTIFIER_CLASS =
            "Failed to load leaf identifier class";
    static final String L_FAIL_TO_LOAD_CLASS =
            "Failed to load class for class: {}";
    static final String E_DATA_NODE_TYPE_IS_NOT_SUPPORT =
            "Given data node type is not supported.";
    static final String E_FAIL_TO_CREATE_OBJ =
            "Failed to create an object for class: ";
    static final String L_FAIL_TO_CREATE_OBJ =
            "Failed to create an object for class: {}";
    static final String E_REFLECTION_FAIL_TO_CREATE_OBJ =
            "Reflection failed to create an object for class: ";
    static final String L_REFLECTION_FAIL_TO_CREATE_OBJ =
            "Reflection failed to create an object for class: {}";
    static final String E_FAIL_TO_LOAD_CONSTRUCTOR =
            "Failed to load constructor for class: {}";
    static final String E_FAIL_TO_INVOKE_METHOD =
            "Failed to invoke method for class: ";
    static final String L_FAIL_TO_INVOKE_METHOD =
            "Failed to invoke method for class: {}";
    static final String E_DATA_TYPE_NOT_SUPPORT =
            "Given data type is not supported.";
    static final String E_INVALID_IDENTITY_DATA =
            "Value for identityref data type is invalid ";
}
