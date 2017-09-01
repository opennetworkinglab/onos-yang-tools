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

import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;

import java.util.Objects;

import com.google.common.base.MoreObjects;

/**
 * Represents a leaf-list attribute in class.
 */
public class MultiInstanceLeaf<E extends LeafIdentifier>
        extends AtomicPath {

    private E leafIdentifier;
    private Object value;

    /**
     * Creates a multi instance leaf object.
     *
     * @param ll attribute of generated default class
     * @param v  value of leaflist
     */
    public MultiInstanceLeaf(E ll, Object v) {
        super(MULTI_INSTANCE_LEAF_VALUE_NODE);
        leafIdentifier = ll;
        value = v;
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
     * @param ll leaf identifier of leaf-list
     */
    public void leafIdentifier(E ll) {
        leafIdentifier = ll;
    }

    /**
     * Returns value of leaf-list.
     *
     * @return value of leaf-list
     */
    public Object value() {
        return value;
    }

    /**
     * Sets the value of leaf-list.
     *
     * @param v the value of leaf-list
     */
    public void value(Object v) {
        value = v;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MultiInstanceLeaf) {
            MultiInstanceLeaf<?> that = (MultiInstanceLeaf<?>) obj;
            // super.type is ensured to be equal
            return Objects.equals(this.leafIdentifier, that.leafIdentifier) &&
                   Objects.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type(), leafIdentifier, value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("leafIdentifier", leafIdentifier)
                .add("value", value)
                .toString();
    }
}
