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

package org.onosproject.yang.compiler.datamodel;

/**
 * Abstraction to unify the YANG identity and typedef name collision detection
 * functionality.
 */
public interface ConflictResolveNode extends YangSchemaNode {

    /**
     * Returns the suffix for the given conflicted type.
     *
     * @return suffix
     */
    String getSuffix();

    /**
     * Returns the flag to identify is there YANG identity and typedef name
     * conflict.
     *
     * @return true, if identity and typedef name conflicts otherwise false
     */
    boolean isNameConflict();

    /**
     * Sets the conflict flag for requested identity or typedef node.
     */
    void setConflictFlag();
}
