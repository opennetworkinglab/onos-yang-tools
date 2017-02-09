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

package org.onosproject.yang.model;

import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;

/**
 * Represents a leaf attribute in class.
 */
public class SingleInstanceLeaf<E extends LeafIdentifier> extends AtomicPath {

    private E leafIdentifier;

    /**
     * Creates a single instance leaf node object.
     *
     * @param leaf leaf attribute of generated default class
     */
    public SingleInstanceLeaf(E leaf) {
        super(SINGLE_INSTANCE_LEAF_VALUE_NODE);
        this.leafIdentifier = leaf;
    }

    /**
     * Returns leaf identifier.
     *
     * @return leaf identifier
     */
    public E leafIdentifier() {
        return leafIdentifier;
    }

    /**
     * Sets the leaf identifier of leaf-list.
     *
     * @param leaf leaf identifier of leaf-list
     */
    public void leafIdentifier(E leaf) {
        this.leafIdentifier = leaf;
    }
}
