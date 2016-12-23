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
 * Abstraction of an entity that provides mechanism for converting between
 * DataNode and ModelObject instances.
 */
public interface ModelConverter {

    /**
     * Produces a POJO of the specified type, initialized and backed by
     * the specified data node.
     *
     * @param node data node
     * @param <T>  type of model object
     * @return POJO of specified data node
     */
    <T extends ModelObject> T createModel(DataNode node);

    /**
     * Produces an immutable tree structure rooted at the returned DataNode
     * using the supplied model POJO object.
     *
     * @param obj model object
     * @return data node corresponds to model object
     */
    DataNode createDataNode(ModelObject obj);
}
