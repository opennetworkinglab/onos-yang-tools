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

package org.onosproject.yang.runtime;


import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.NodeKey;

import java.util.Map;

/**
 * Represents implementation of data node walker, which walks the data tree.
 */
public final class DefaultDataNodeWalker {

    // Forbid construction.
    private DefaultDataNodeWalker() {
    }

    /**
     * Walks the data node tree from given node.
     *
     * @param listener data node listener implemented by the protocol
     * @param node     root node of data tree
     */
    public static void walk(DataNodeListener listener, DataNode node) {

        listener.enterDataNode(node);
        // Walking all child's of given root node.
        walkChildNode(listener, node);
        listener.exitDataNode(node);
    }

    /**
     * Walks the all child nodes of given root node.
     *
     * @param listener data node listener implemented by the protocol
     * @param node     root node of data tree
     */
    private static void walkChildNode(DataNodeListener listener, DataNode node) {
        Map<NodeKey, DataNode> childMap;
        if (node instanceof InnerNode) {
            childMap = ((InnerNode) node).childNodes();
            for (Map.Entry<NodeKey, DataNode> entry : childMap.entrySet()) {
                DataNode n = entry.getValue();
                listener.enterDataNode(n);
                if (n instanceof InnerNode) {
                    walkChildNode(listener, n);
                }
                listener.exitDataNode(n);
            }
        }
    }
}
