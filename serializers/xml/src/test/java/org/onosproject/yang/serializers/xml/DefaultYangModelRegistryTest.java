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

package org.onosproject.yang.serializers.xml;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.model.DefaultYangModuleId;
import org.onosproject.yang.model.YangModel;
import org.onosproject.yang.model.YangModule;
import org.onosproject.yang.model.YangModuleId;
import org.onosproject.yang.runtime.impl.DefaultYangModelRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.unRegister;

/**
 * Unit test for model registry with model id configured using maven as "xml".
 */
public class DefaultYangModelRegistryTest {

    private static final String SCHEMA_NAME_3 = "animal";
    private static final String SCHEMA_NAME_4_15 = "Logistics-manager@2016-05-24";
    private static final String SCHEMA_NAME_4 = "Logistics-manager";
    private static final String SCHEMA_NAME_5 = "attributes";
    private static final String MODEL_ID = "xml";

    private DefaultYangModelRegistry setUp() {
        processSchemaRegistry();
        return registry();
    }

    /**
     * Unit test case in which schema node should be present.
     *
     * @throws IOException when fails to do IO operation
     */
    @Test
    public void testForGetSchemaNode()
            throws IOException {

        List<YangNode> nodes = new ArrayList<>();
        DefaultYangModelRegistry registry = setUp();

        YangModel model = registry.getModel(MODEL_ID);
        assertThat(true, is(model != null));

        Set<YangModule> modules = getYangModules(model, MODEL_ID);
        assertThat(true, is(modules.size() != 0));
        assertThat(true, is(modules != null));

        YangModuleId moduleId = new DefaultYangModuleId("animal",
                                                        "2016-06-24");

        YangModule module = registry.getModule(moduleId);
        assertThat(true, is(moduleId.equals(module.getYangModuleId())));

        YangSchemaNode yangNode = registry.getForSchemaName(SCHEMA_NAME_3);
        assertThat(true, is(SCHEMA_NAME_3.equals(yangNode.getName())));

        //Unregister service
        nodes.add((YangNode) yangNode);
        unRegister(nodes);

        yangNode = registry.getForSchemaName(SCHEMA_NAME_3);
        assertThat(true, is(yangNode == null));

        module = registry.getModule(moduleId);
        assertThat(true, is(module == null));

        Set<YangModel> models = registry.getModels();
        assertThat(true, is(models.size() == 0));

        model = registry.getModel(MODEL_ID);
        assertThat(true, is(model == null));

        modules = getYangModules(model, MODEL_ID);
        assertThat(true, is(modules.size() == 0));
    }

    /**
     * Unit test case in which schema node should be present with multi
     * revisions.
     *
     * @throws IOException when fails to do IO operation
     */
    @Test
    public void testForGetSchemaNodeWhenNoRevision()
            throws IOException {

        DefaultYangModelRegistry registry = setUp();

        List<YangNode> nodes = new ArrayList<>();

        YangModel model = registry.getModel(MODEL_ID);
        assertThat(true, is(model != null));

        Set<YangModule> modules = getYangModules(model, MODEL_ID);
        assertThat(true, is(modules.size() != 0));
        assertThat(true, is(modules != null));

        YangModuleId moduleId = new DefaultYangModuleId("Logistics-manager",
                                                        "2016-05-24");

        YangModule module = registry.getModule(moduleId);
        assertThat(true, is(moduleId.equals(module.getYangModuleId())));

        //Service with rev.
        YangSchemaNode yangNode = registry.getForSchemaName(SCHEMA_NAME_4_15);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        //unregister SERVICE_NAME_REV_15.
        nodes.add((YangNode) yangNode);
        unRegister(nodes);

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode == null));

        //Here the yangNode should be the node which does not have revision.
        // asset should pass with false.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_5);
        assertThat(true, is(((YangNode) yangNode).getRevision() == null));

        //---------------------------------------------------------------//

        module = registry.getModule(moduleId);
        assertThat(true, is(module == null));

        Set<YangModel> models = registry.getModels();
        assertThat(true, is(models.size() == 0));

        model = registry.getModel(MODEL_ID);
        assertThat(true, is(model == null));

        modules = getYangModules(model, MODEL_ID);
        assertThat(true, is(modules.size() == 0));
    }

    private Set<YangModule> getYangModules(YangModel model, String modelId) {
        Set<org.onosproject.yang.model.YangModule> modules =
                new LinkedHashSet<>();
        if (model != null) {
            modules.addAll(model.getYangModules());
        }
        return ImmutableSet.copyOf(modules);
    }
}
