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

import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.runtime.Annotation;
import org.onosproject.yang.runtime.YangSerializerContext;

import java.util.List;

import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;

/**
 * Tests the default schema context provider methods.
 */
public class TestYangSerializerContext implements YangSerializerContext {

    // Reference for YANG model registry
    private DefaultYangModelRegistry reg;

    /**
     * Returns the YANG model registry.
     *
     * @return YANG model registry
     */
    public DefaultYangModelRegistry getRegistry() {
        return reg;
    }

    @Override
    public SchemaContext getContext() {
        processSchemaRegistry();
        reg = registry();
        return reg;
    }

    @Override
    public List<Annotation> getProtocolAnnotations() {
        return null;
    }
}
