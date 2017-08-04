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

import java.util.LinkedList;
import java.util.List;

/**
 * Representation of an entity that is composition of model objects identifier
 * and its data.
 */
public class DefaultModelObjectData implements ModelObjectData {

    private final List<ModelObject> objects;
    private final ModelObjectId identifier;

    /**
     * Creates an instance of model object data.
     *
     * @param b model object data builder
     */
    protected DefaultModelObjectData(Builder b) {
        objects = b.objects;
        identifier = b.identifier;
    }

    @Override
    public List<ModelObject> modelObjects() {
        return objects;
    }

    @Override
    public ModelObjectId identifier() {
        return identifier;
    }

    /**
     * Retrieves a new model object data builder.
     *
     * @return model object data builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Represents composite model data builder.
     */
    public static class Builder implements ModelObjectData.Builder {

        private List<ModelObject> objects;
        private ModelObjectId identifier;

        /**
         * Creates an instance of composite model data builder.
         */
        public Builder() {
            objects = new LinkedList<>();
        }

        @Override
        public Builder addModelObject(ModelObject o) {
            objects.add(o);
            return this;
        }

        @Override
        public Builder identifier(ModelObjectId id) {
            identifier = id;
            return this;
        }

        @Override
        public ModelObjectData build() {
            return new DefaultModelObjectData(this);
        }
    }
}
