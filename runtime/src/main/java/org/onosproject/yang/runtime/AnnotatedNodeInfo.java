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

import org.onosproject.yang.model.ResourceId;

import java.util.List;

/**
 * Abstraction of an entity that represents annotated nodes information.
 */
public interface AnnotatedNodeInfo {

    /**
     * Returns resource identifier of the annotated node.
     *
     * @return resource identifier
     */
    ResourceId resourceId();

    /**
     * Returns annotations associated with the node.
     *
     * @return annotations
     */
    List<Annotation> annotations();

    /**
     * Abstraction of an entity that represents builder of annotated node
     * information.
     */
    interface Builder {


        /**
         * Sets resource identifier of the annotated node.
         *
         * @param id resource identifier
         * @return builder
         */
        Builder resourceId(ResourceId id);

        /**
         * Adds annotation to a node.
         *
         * @param annotation annotation information
         * @return builder
         */
        Builder addAnnotation(Annotation annotation);

        /**
         * Builds an instance of annotated node information.
         *
         * @return annotated node info
         */
        AnnotatedNodeInfo build();
    }
}

