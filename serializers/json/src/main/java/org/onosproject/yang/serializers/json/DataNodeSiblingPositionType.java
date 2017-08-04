/*
 *  Copyright 2017-present Open Networking Foundation
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

package org.onosproject.yang.serializers.json;

/**
 * Denotation of the relative position of a multi-instance data node
 * relative to its siblings.
 */
public enum DataNodeSiblingPositionType {
    /**
     * Denotes that the given node is not a multi-instance or leaf-list node.
     */
    NOT_MULTI_INSTANCE_NODE,

    /**
     * Denotes that the node is the first instance in the sibling list.
     */
    FIRST_INSTANCE,

    /**
     * Denotes that the node is the last instance in the sibling list.
     */
    LAST_INSTANCE,

    /**
     * Denotes that the node is one of the middle instances.
     */
    MIDDLE_INSTANCE,

    /**
     * Denotes that the given node is the only instance in the multi-instance
     * data node.
     */
    SINGLE_INSTANCE_IN_MULTI_NODE,
    /**
     * Used for error case or as uninitialized data.
     */
    UNKNOWN_TYPE,
}
