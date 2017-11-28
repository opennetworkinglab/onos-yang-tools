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
 * Represents x path linking types.
 */
public enum XpathLinkingTypes {

    // Augment path linking.
    AUGMENT_LINKING,

    // Leaf ref path linking.
    LEAF_REF_LINKING,

    // Compiler annotation linking.
    COMPILER_ANNOTATION_LINKING,

    // Deviation linking.
    DEVIATION_LINKING,

    // Uses augment linking
    USES_AUGMENT_LINKING,

    // Anydata linking
    ANYDATA_LINKING
}
