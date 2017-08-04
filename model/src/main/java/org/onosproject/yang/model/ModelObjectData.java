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

import java.util.List;

/**
 * Abstraction of an entity that is composition of model object identifier
 * and its data.
 */
public interface ModelObjectData {

    /**
     * Returns model objects.
     *
     * @return model objects
     */
    List<ModelObject> modelObjects();

    /**
     * Returns module object identifier.
     *
     * @return identifier
     */
    ModelObjectId identifier();

    /**
     * Represents builder of composite model data.
     */
    interface Builder {

        /**
         * Adds a model object.
         *
         * @param o model object to be added
         * @return builder
         */
        Builder addModelObject(ModelObject o);

        /**
         * Sets module object identifier. In case of schema nodes under
         * module level id should be null.
         * <p>
         * in case of RPC id should be constructed using input's class.
         * <p>
         * in case of notification id can be constructed similar as container.
         *
         * @param id identifier
         * @return builder
         */
        Builder identifier(ModelObjectId id);

        /**
         * Builds an instance of model object data.
         *
         * @return model object data
         */
        ModelObjectData build();
    }
}
