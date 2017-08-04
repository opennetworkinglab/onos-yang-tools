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

/**
 * Abstraction of an entity which provide call back methods which are called
 * by data node walker while walking the data tree.
 * <p>
 * This interface needs to be implemented by protocol implementing listener's
 * based call backs while data walk.
 */
public interface DataNodeListener {

    /**
     * Data node's entry, it will be called during a node entry.
     * All the related information about the node can be obtain from the node.
     *
     * @param node data node
     */
    //TODO add additional context which may contain information like location
    // of multi instance node in map to later let json serializer use this.
    void enterDataNode(DataNode node);

    /**
     * Data node's exit, it will be called during a node exit.
     * All the related information about the node can be obtain from the node.
     *
     * @param node data node
     */
    //TODO add additional context which may contain information like location
    // of multi instance node in map to later let json serializer use this.
    void exitDataNode(DataNode node);
}
