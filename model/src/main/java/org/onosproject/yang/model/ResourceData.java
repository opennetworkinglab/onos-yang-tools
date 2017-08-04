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

package org.onosproject.yang.model;

import java.util.List;

/**
 * Abstraction of an entity that is representation of resource data.
 */
public interface ResourceData {

    /**
     * Returns list of data nodes.
     *
     * @return list of data nodes
     */
    List<DataNode> dataNodes();

    /**
     * Returns resource identifier.
     *
     * @return resource identifier
     */
    ResourceId resourceId();

    /**
     * Abstraction of an entity that represents builder of composite data.
     */
    interface Builder {

        /**
         * Adds a data node.
         *
         * @param node data node
         * @return builder
         */
        Builder addDataNode(DataNode node);

        /**
         * Sets resource identifier.
         *
         * @param identifier resource identifier
         * @return builder
         */
        Builder resourceId(ResourceId identifier);

        /**
         * Builds an instance of resource data.
         *
         * @return resource data
         */
        ResourceData build();
    }
}
