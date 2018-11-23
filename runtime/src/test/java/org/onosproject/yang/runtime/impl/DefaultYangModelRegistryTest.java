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

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangRevision;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.model.DefaultYangModuleId;
import org.onosproject.yang.model.YangModel;
import org.onosproject.yang.model.YangModule;
import org.onosproject.yang.model.YangModuleId;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.getDateInStringFormat;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processModelTest;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.unRegister;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Unit test for model registry.
 */
public class DefaultYangModelRegistryTest {

    private static final Logger log = getLogger(DefaultYangModelRegistryTest.class);

    private static final String SCHEMA_NAME_3 = "ietf-network3";
    private static final String INTERFACE_NAME_3 =
            "org.onosproject.yang.gen.v1.ietfnetwork3.rev20151208.IetfNetwork3";

    private static final String SCHEMA_NAME_4_14 = "ietf-network4@2014-12-08";
    private static final String SCHEMA_NAME_4_15 = "ietf-network4@2015-12-08";
    private static final String SCHEMA_NAME_4_16 = "ietf-network4@2016-12-08";
    private static final String SCHEMA_NAME_4_17 = "ietf-network4@2017-12-08";
    private static final String SCHEMA_NAME_4 = "ietf-network4";
    private static final String INTERFACE_NAME_REV_14 =
            "org.onosproject.yang.gen.v1.ietfnetwork4.rev20141208.IetfNetwork4";
    private static final String INTERFACE_NAME_REV_15 =
            "org.onosproject.yang.gen.v1.ietfnetwork4.rev20151208.IetfNetwork4";
    private static final String INTERFACE_NAME_REV_16 =
            "org.onosproject.yang.gen.v1.ietfnetwork4.rev20161208.IetfNetwork4";
    private static final String INTERFACE_NAME_REV_17 =
            "org.onosproject.yang.gen.v1.ietfnetwork4.rev20171208.IetfNetwork4";
    private static final String INTERFACE_NAME_NO_REV =
            "org.onosproject.yang.gen.v1.ietfnetwork4.IetfNetwork4";

    private static final String CHECK = "check";
    private static final String DATE_NAMESPACE = "2015-12-08";
    private static final String NAMESPACE =
            "urn:ietf:params:xml:ns:yang:ietf-network4:check:namespace";
    private static final String MODEL_ID = "onos-yang-runtime";


    /**
     * Do the prior setup for each UT.
     */
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

        YangModuleId moduleId = new DefaultYangModuleId("ietf-network4",
                                                        "2017-12-08");

        YangModule module = registry.getModule(moduleId);
        assertThat(true, is(moduleId.equals(module.getYangModuleId())));

        YangSchemaNode yangNode = registry.getForSchemaName(SCHEMA_NAME_3);
        assertThat(true, is(SCHEMA_NAME_3.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_3);
        assertThat(true, is(SCHEMA_NAME_3.equals(yangNode.getName())));

        //Unregister service
        nodes.add((YangNode) yangNode);
        unRegister(nodes);

        yangNode = registry.getForSchemaName(SCHEMA_NAME_3);
        assertThat(true, is(yangNode == null));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_3);
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
     * Unit test case for getting the metadata for a particular module.
     *
     * @throws IOException when fails to do IO operation
     */
    @Test
    public void testForGetMetaData()
            throws IOException {
        DefaultYangModelRegistry registry = setUp();
        YangNode node = null;
        List<YangNode> nodes = new ArrayList<>();

        YangModel model = registry.getModel(MODEL_ID);
        assertThat(true, is(model != null));

        Set<YangModule> modules = getYangModules(model, MODEL_ID);
        assertThat(true, is(modules.size() != 0));
        assertThat(true, is(modules != null));
        processModelTest();
        YangModuleId moduleId = new DefaultYangModuleId("ietf-network4",
                                                        "2017-12-08");

        // TODO: fix this for yang model test
        YangModule module = registry.getModule(moduleId);
        assertThat(true, is(moduleId.equals(module.getYangModuleId())));

        InputStream stream = module.getMetadata();
        ObjectInputStream objectInputStream = new ObjectInputStream(stream);
        try {
            node = ((YangNode) objectInputStream.readObject());
        } catch (ClassNotFoundException e) {
            log.error("Class not found", e);
        }
        objectInputStream.close();
        assertThat(true, is(
                node.getName().equals(moduleId.moduleName())));

        //Service with rev.
        YangSchemaNode yangNode = registry.getForSchemaName(SCHEMA_NAME_4_15);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_15);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        //unregister SERVICE_NAME_REV_15.
        nodes.add((YangNode) yangNode);
        unRegister(nodes);

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode != null));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_15);
        assertThat(true, is(yangNode == null));

        //Here the yangNode should be the node which does not have revision.
        // asset should pass with false.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(((YangNode) yangNode).getRevision() == null));

        //---------------------------------------------------------------//

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_NO_REV);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        //unregister SERVICE_NAME_NO_REV.
        nodes.add((YangNode) yangNode);
        unRegister(nodes);

        //Here the yangNode should be the node which have different revision.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode != null));
        assertThat(true, is(((YangNode) yangNode).getRevision() != null));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_NO_REV);
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
        YangNode node = null;
        List<YangNode> nodes = new ArrayList<>();

        YangModel model = registry.getModel(MODEL_ID);
        assertThat(true, is(model != null));

        Set<YangModule> modules = getYangModules(model, MODEL_ID);
        assertThat(true, is(modules.size() != 0));
        assertThat(true, is(modules != null));

        YangModuleId moduleId = new DefaultYangModuleId("ietf-network4",
                                                        "2017-12-08");

        // TODO: fix this for yang model test
        YangModule module = registry.getModule(moduleId);
        assertThat(true, is(moduleId.equals(module.getYangModuleId())));

        //Service with rev.
        YangSchemaNode yangNode = registry.getForSchemaName(SCHEMA_NAME_4_15);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_15);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        //unregister SERVICE_NAME_REV_15.
        nodes.add((YangNode) yangNode);
        unRegister(nodes);

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode != null));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_15);
        assertThat(true, is(yangNode == null));

        //Here the yangNode should be the node which does not have revision.
        // asset should pass with false.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(((YangNode) yangNode).getRevision() == null));

        //---------------------------------------------------------------//

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_NO_REV);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        //unregister SERVICE_NAME_NO_REV.
        nodes.add((YangNode) yangNode);
        unRegister(nodes);

        //Here the yangNode should be the node which have different revision.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode != null));
        assertThat(true, is(((YangNode) yangNode).getRevision() != null));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_NO_REV);
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

    private Set<YangModule> getYangModules(YangModel model, String modelId) {
        Set<org.onosproject.yang.model.YangModule> modules =
                new LinkedHashSet<>();
        if (model != null) {
            modules.addAll(model.getYangModules());
        }
        return ImmutableSet.copyOf(modules);
    }

    /**
     * Unit test case in which schema node should be present with multi
     * revisions.
     *
     * @throws IOException when fails to do IO operation
     */
    @Test
    public void testForGetSchemaNodeWhenMultiRevision()
            throws IOException {

        DefaultYangModelRegistry registry = setUp();

        List<YangNode> nodes = new ArrayList<>();

        YangModel model = registry.getModel(MODEL_ID);
        assertThat(true, is(model != null));

        Set<YangModule> modules = getYangModules(model, MODEL_ID);
        assertThat(true, is(modules.size() != 0));
        assertThat(true, is(modules != null));

        YangModuleId moduleId = new DefaultYangModuleId("ietf-network4",
                                                        "2017-12-08");

        YangModule module = registry.getModule(moduleId);
        assertThat(true, is(moduleId.equals(module.getYangModuleId())));

        //Service with rev.
        YangSchemaNode yangNode = registry.getForSchemaName(SCHEMA_NAME_4_15);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_15);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        //Unregister SERVICE_NAME_REV_15.
        nodes.add((YangNode) yangNode);
        unRegister(nodes);

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode != null));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_15);
        assertThat(true, is(yangNode == null));

        //Here the yangNode should be the node which does not have revision.
        // asset should pass with false.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(((YangNode) yangNode).getRevision() == null));

        //---------------------------------------------------------------//

        //Here the yangNode should be the node which does not have revision.
        // asset should pass with false.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(((YangNode) yangNode).getRevision() == null));

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4_16);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_16);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        //Unregister SERVICE_NAME_REV_16.
        nodes.add((YangNode) yangNode);
        unRegister(nodes);

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_16);
        assertThat(true, is(yangNode == null));

        //Here the yangNode should be the node which have different revision.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(((YangNode) yangNode).getRevision() == null));

        //---------------------------------------------------------------//

        //Service with different revision.

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4_17);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_17);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        //Unregister SERVICE_NAME_REV_17.
        nodes.add((YangNode) yangNode);
        unRegister(nodes);

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_17);
        assertThat(true, is(yangNode == null));

        //Here the yangNode should be the node which have different revision.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(((YangNode) yangNode).getRevision() == null));

        //---------------------------------------------------------------//

        //Service no revision.

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_NO_REV);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        //Unregister SERVICE_NAME_NO_REV.
        nodes.add((YangNode) yangNode);
        unRegister(nodes);

        yangNode = registry.getForRegClassName(INTERFACE_NAME_NO_REV);
        assertThat(true, is(yangNode == null));

        //Here the yangNode should be the node which have different revision.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode != null));
        assertThat(true, is(((YangNode) yangNode).getRevision() != null));

        //---------------------------------------------------------------//

        //Service with different revision.

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4_14);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_14);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        //Unregister SERVICE_NAME_REV_14.
        nodes.add((YangNode) yangNode);
        unRegister(nodes);

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode == null));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_14);
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
     * get schema for namespace in decode test.
     */
    @Test
    public void testGetNodeWrtNamespace() {
        DefaultYangModelRegistry registry = setUp();

        YangSchemaNode yangNode = registry.getForNameSpace(NAMESPACE, false);
        assertThat(true, is(CHECK.equals(yangNode.getName())));

        YangRevision rev = ((YangNode) yangNode).getRevision();
        assertThat(true, is(rev != null));

        String date = getDateInStringFormat((YangNode) yangNode);
        assertThat(true, is(DATE_NAMESPACE.equals(date)));
    }
}
