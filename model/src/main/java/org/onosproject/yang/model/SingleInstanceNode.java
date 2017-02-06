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

import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;

/**
 * Represents a single instance object.
 */
public class SingleInstanceNode<T extends ModelObject> extends AtomicPath {

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
}
