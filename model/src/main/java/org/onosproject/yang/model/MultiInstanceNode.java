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

import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;

import java.util.Objects;

import com.google.common.base.MoreObjects;

/**
 * Represents a multi instance object.
 */
public class MultiInstanceNode<T extends InnerModelObject, K extends KeyInfo>
        extends AtomicPath {

    private Class<T> listClass;
    private K key;

    /**
     * Creates a multi instance object.
     *
     * @param list generated class for list
     * @param k    key object to uniquely identify the list
     */
    public MultiInstanceNode(Class<T> list, K k) {
        super(MULTI_INSTANCE_NODE);
        listClass = list;
        key = k;
    }

    /**
     * Returns the generated java class for list.
     *
     * @return the generated list class
     */
    public Class<T> listClass() {
        return listClass;
    }

    /**
     * Sets the generated java class for list.
     *
     * @param list the generated class for list
     */
    public void listClass(Class<T> list) {
        listClass = list;
    }

    /**
     * Returns the key object for list.
     *
     * @return the key object for list
     */
    public K key() {
        return key;
    }

    /**
     * Sets the key object for list.
     *
     * @param k the object for list
     */
    public void key(K k) {
        key = k;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MultiInstanceNode) {
            MultiInstanceNode<?, ?> that = (MultiInstanceNode<?, ?>) obj;
            // super.type is ensured to be equal
            return Objects.equals(this.listClass, that.listClass) &&
                   Objects.equals(this.key, that.key);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type(), listClass, key);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("listClass", listClass)
                .add("key", key)
                .toString();
    }
}
