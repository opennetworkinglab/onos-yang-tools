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

import org.onosproject.yang.model.ResourceData;

import java.util.List;

/**
 * Abstraction of an entity that is composition of resource data and
 * associated annotations information.
 */
public interface CompositeData {

    /**
     * Returns resource node.
     *
     * @return resource node
     */
    ResourceData resourceData();

    /**
     * Returns annotated nodes information.
     *
     * @return annotated nodes information
     */
    List<AnnotatedNodeInfo> annotatedNodesInfo();

    /**
     * Abstraction of an entity that represents builder of composite data.
     */
    interface Builder {

        /**
         * Sets resource node.
         *
         * @param node resource node
         * @return builder
         */
        Builder resourceData(ResourceData node);

        /**
         * Adds information about annotated node.
         *
         * @param info annotated node information
         * @return builder
         */
        Builder addAnnotatedNodeInfo(AnnotatedNodeInfo info);

        /**
         * Builds an instance of composite data.
         *
         * @return composite data
         */
        CompositeData build();
    }
}
