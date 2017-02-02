/*
 * Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang.runtime.api;

import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ResourceId;

import java.util.List;

/**
 * Representation of an entity which is comprised of composite data having
 * resource identifier with list of data node.
 */
public interface CompositeData {

    /**
     * Returns list of data nodes.
     *
     * @return list of data nodes
     */
    List<DataNode> dataNodes();

    /**
     * Adds a data node.
     *
     * @param node data node
     */
    void addDataNode(DataNode node);

    /**
     * Returns resource identifier.
     *
     * @return resource identifier
     */
    ResourceId resourceId();

    /**
     * Sets resource identifier.
     *
     * @param identifier resource identifier
     */
    void resourceId(ResourceId identifier);
}
