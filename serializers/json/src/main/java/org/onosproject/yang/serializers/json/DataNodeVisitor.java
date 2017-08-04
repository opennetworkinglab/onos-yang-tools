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

import org.onosproject.yang.model.DataNode;

/**
 * Representation of the visitor to data nodes during the
 * data tree traversal.
 */
public interface DataNodeVisitor {
    /**
     * Enters the data node by the visitor. The function is called by
     * the walker of the data tree when it enters the given data node.
     * The visitor uses this function to process the data of this node.
     *
     * @param dataNode    data node which the tree walker visits
     * @param siblingType indicates whether the data node the the first
     *                    or last sibling instance of a multi-instance node
     */
    void enterDataNode(DataNode dataNode, DataNodeSiblingPositionType siblingType);

    /**
     * Exits the data node by the visitor. The function is called by
     * the walker of the data tree when it's about to leave the given
     * data node. The visitor uses this function to run cleanup work
     * of the data processing.
     *
     * @param dataNode    data node which the tree walker finishes the visit
     * @param siblingType indicates whether the data node the the first
     *                    or last sibling instance of a multi-instance node
     */
    void exitDataNode(DataNode dataNode, DataNodeSiblingPositionType siblingType);
}
