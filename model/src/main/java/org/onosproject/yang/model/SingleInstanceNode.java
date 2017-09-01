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

import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;

import java.util.Objects;

import com.google.common.base.MoreObjects;

/**
 * Represents a single instance object.
 */
public class SingleInstanceNode<T extends InnerModelObject> extends AtomicPath {

    private Class<T> containerClass;

    /**
     * Creates a single instance node object.
     *
     * @param container generated class for container
     */
    public SingleInstanceNode(Class<T> container) {
        super(SINGLE_INSTANCE_NODE);
        containerClass = container;
    }

    /**
     * Returns the generated java class for container.
     *
     * @return the generated container class
     */
    public Class<T> container() {
        return containerClass;
    }

    /**
     * Sets the generated java class for list.
     *
     * @param container the generated class for container
     */
    public void container(Class<T> container) {
        containerClass = container;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SingleInstanceNode) {
            SingleInstanceNode<?> that = (SingleInstanceNode<?>) obj;
            // super.type is ensured to be equal
            return Objects.equals(this.containerClass, that.containerClass);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type(), containerClass);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("containerClass", containerClass)
                .toString();
    }
}
