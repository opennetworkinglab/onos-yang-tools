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

package org.onosproject.yang.runtime.impl;

import org.onosproject.yang.model.ModelConverter;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ResourceData;

/**
 * Represents implementation of default model converter.
 */
public class DefaultModelConverter implements ModelConverter {

    private final DefaultYangModelRegistry reg;

    /**
     * Creates an instance of default model converter.
     *
     * @param registry default YANG model registry
     */
    public DefaultModelConverter(DefaultYangModelRegistry registry) {
        reg = registry;
    }

    @Override
    public ModelObjectData createModel(ResourceData data) {
        DefaultYobBuilder builder = new DefaultYobBuilder(reg);
        return builder.getYangObject(data);
    }

    @Override
    public ResourceData createDataNode(ModelObjectData modelData) {
        DefaultDataTreeBuilder builder = new DefaultDataTreeBuilder(reg);
        return builder.getResourceData(modelData);
    }
}
