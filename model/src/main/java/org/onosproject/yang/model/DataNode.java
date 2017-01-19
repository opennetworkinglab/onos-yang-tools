/*
 * Copyright 2016-present Open Networking Laboratory
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

/**
 * Abstraction of an entity which represents data tree node. Information
 * exchange between YANG runtime, protocol and store will be based on this
 * node, agnostic of schema.
 */
public interface DataNode {

    /**
     * Returns the node schema identifier.
     *
     * @return node schema identifier
     */
    SchemaId identifier();

    /**
     * Returns the type of node.
     *
     * @return node type
     */
    Type type();

    /**
     * Returns the key to identify a branching node.
     *
     * @return key to identify a branching node
     */
    NodeKey key();

    /**
     * Represents type of node in data store.
     */
    enum Type {

        /**
         * Single instance node.
         */
        SINGLE_INSTANCE_NODE,

        /**
         * Multi instance node.
         */
        MULTI_INSTANCE_NODE,

        /**
         * Single instance leaf node.
         */
        SINGLE_INSTANCE_LEAF_VALUE_NODE,

        /**
         * Multi instance leaf node.
         */
        MULTI_INSTANCE_LEAF_VALUE_NODE
    }
}
