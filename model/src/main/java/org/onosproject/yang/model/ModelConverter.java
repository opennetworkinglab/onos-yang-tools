/*
 * Copyright 2016-present Open Networking Foundation
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
     * Produces a POJO of the specified type.
     * <p>
     * Resource identifier will be converted to model object identifier and
     * list of data nodes will be converted to list of model objects.
     *
     * @param data resource data
     * @return model object data of specified resource data
     * @throws ModelConverterException when fails to perform model conversion
     */
    ModelObjectData createModel(ResourceData data);

    /**
     * Produces an immutable tree structure rooted at the returned DataNode
     * using the supplied model POJO object.
     * <p>
     * Model object identifier will be converted to resource identifier and
     * list of model objects will be converted to list of data nodes.
     *
     * @param modelData model object data
     * @return resource data corresponds to model object
     * @throws ModelConverterException when fails to perform model conversion
     */
    ResourceData createDataNode(ModelObjectData modelData);
}
