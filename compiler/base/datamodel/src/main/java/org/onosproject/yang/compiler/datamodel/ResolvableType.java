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

package org.onosproject.yang.compiler.datamodel;

/**
 * Type of the resolvable info.
 */
public enum ResolvableType {

    /**
     * Identifies the derived data type.
     */
    YANG_DERIVED_DATA_TYPE,

    /**
     * Identifies the uses.
     */
    YANG_USES,

    /**
     * Identifies the if-feature.
     */
    YANG_IF_FEATURE,

    /**
     * Identifies the leafref.
     */
    YANG_LEAFREF,

    /**
     * Identifies the base.
     */
    YANG_BASE,

    /**
     * Identifies the identityref.
     */
    YANG_IDENTITYREF,

    /**
     * Identifies the augment.
     */
    YANG_AUGMENT,

    /**
     * Identifies the compiler annotations.
     */
    YANG_COMPILER_ANNOTATION,

    /**
     * Identifies the deviation.
     */
    YANG_DEVIATION,

    /**
     * Identifies augment inside uses.
     */
    YANG_USES_AUGMENT
}
