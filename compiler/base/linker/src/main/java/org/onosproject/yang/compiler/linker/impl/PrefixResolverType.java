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

package org.onosproject.yang.compiler.linker.impl;

/**
 * Enum for prefix resolver type when augment has come in path.
 */
enum PrefixResolverType {

    /**
     * When prefix changes from inter file to intra file.
     */
    INTER_TO_INTRA,

    /**
     * When prefix changes from intra file to inter file.
     */
    INTRA_TO_INTER,

    /**
     * When prefix changes from one inter file to other inter file.
     */
    INTER_TO_INTER,

    /**
     * When no prefix change occurs.
     */
    NO_PREFIX_CHANGE_FOR_INTRA,

    /**
     * When no prefix change occurs.
     */
    NO_PREFIX_CHANGE_FOR_INTER
}
