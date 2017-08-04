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
 * Abstraction of an entity that provides common basis to specify atomic and
 * non atomic model.
 */
public abstract class ModelObject {

    /*
 * Represents type of node in data store.
 */
    public enum ModelObjectType {

        /**
         * Atomic node.
         */
        ATOMIC,

        /**
         * Non atomic node.
         */
        NON_ATOMIC
    }

    private ModelObjectType modelObjectType;

    /**
     * Creates an instance of model object.
     *
     * @param t type of model object
     */
    protected ModelObject(ModelObjectType t) {
        modelObjectType = t;
    }

    /**
     * Returns type of model object.
     *
     * @return type
     */
    ModelObjectType getModelObjectType() {
        return modelObjectType;
    }
}
