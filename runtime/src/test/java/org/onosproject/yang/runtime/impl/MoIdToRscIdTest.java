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

import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangContainer;
import org.onosproject.yang.compiler.datamodel.YangInput;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNotification;
import org.onosproject.yang.compiler.datamodel.YangOutput;
import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.gen.v1.check.check.Cont53;
import org.onosproject.yang.gen.v1.check.check.DefaultCont53;
import org.onosproject.yang.gen.v1.check.check.DefaultList56;
import org.onosproject.yang.gen.v1.check.check.List56;
import org.onosproject.yang.gen.v1.check.check.List56Keys;
import org.onosproject.yang.gen.v1.check.check.group1.DefaultCont58;
import org.onosproject.yang.gen.v1.check.check.group1.DefaultList57;
import org.onosproject.yang.gen.v1.check.check.group1.List57Keys;
import org.onosproject.yang.gen.v1.check.check.list56.augmentedlist56.Cont56;
import org.onosproject.yang.gen.v1.check.check.list56.augmentedlist56.DefaultCont56;
import org.onosproject.yang.gen.v1.check.check.list56.cont56.augmentedcont56.DefaultCont57;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.DefaultFirstLevel;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.DefaultContainerLeaf;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.containerleaf.AugmentedContainerLeaf;
import org.onosproject.yang.gen.v1.ytbmodulewithleaflist.rev20160826.YtbModuleWithLeafList;
import org.onosproject.yang.gen.v1.ytbtreebuilderforlisthavinglist.rev20160826.ytbtreebuilderforlisthavinglist.DefaultCarrier;
import org.onosproject.yang.gen.v1.ytbtreebuilderforlisthavinglist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.DefaultMultiplexes;
import org.onosproject.yang.gen.v1.ytbtreebuilderforlisthavinglist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.MultiplexesKeys;
import org.onosproject.yang.gen.v1.ytbtreebuilderforlisthavinglist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.multiplexes.ApplicationAreasKeys;
import org.onosproject.yang.gen.v1.ytbtreebuilderforlisthavinglist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.multiplexes.DefaultApplicationAreas;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.model.YangNamespace;
import org.onosproject.yang.runtime.mockclass.testmodule.DefaultTestNotification;
import org.onosproject.yang.runtime.mockclass.testmodule.testnotification.DefaultTestContainer;
import org.onosproject.yang.runtime.mockclass.testmodule.testrpc.DefaultTestInput;
import org.onosproject.yang.runtime.mockclass.testmodule.testrpc.DefaultTestOutput;
import org.slf4j.Logger;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT32;
import static org.onosproject.yang.gen.v1.ytbtreebuilderforlisthavinglist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.multiplexes.TypesEnum.SPACE_DIVISION;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.addMockNode;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Unit test case for model object identifier to resource identifier converter.
 */
public class MoIdToRscIdTest {

    private final Logger log = getLogger(getClass());
    ModIdToRscIdConverter builder;
    DefaultYangModelRegistry reg;
    private ResourceId rscId;
    private List<NodeKey> nodeKeys;
    private SchemaId sid;
    private ModelObjectId mid;

    /**
     * Sets up all prerequisite.
     */
    private void setUp() {
        processSchemaRegistry();
        reg = registry();
        builder = new ModIdToRscIdConverter(reg);
    }

    /**
     * Adds mock node in registry.
     */
    void addMockModWithRpc() {
        setUp();
        YangModule mod = null;
        try {
            mod = (YangModule) getModuleWithRpc();
        } catch (DataModelException e) {
            log.info("test error");
        }
        addMockNode(mod, getQualifiedName());
    }

    /**
     * Adds mock node in registry.
     */
    void addMockModWithNotification() {
        setUp();
        YangModule mod = null;
        try {
            mod = (YangModule) getModuleWithNotification();
        } catch (DataModelException e) {
            log.info("test error");
        }
        addMockNode(mod, getQualifiedName());
    }

    /**
     * Unit test case for model object identifier as null.
     */
    @Test
    public void nullMoId() {
        setUp();
        rscId = builder.fetchResourceId(null).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(1, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));
    }

    /**
     * Unit test case for model object identifier as empty.
     */
    @Test
    public void emptyMoId() {
        setUp();
        mid = ModelObjectId.builder().build();
        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(1, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));
    }

    /**
     * Unit test case for model object identifier with leaf list.
     */
    @Test
    public void moIdWithLeaf() {
        setUp();
        mid = ModelObjectId.builder()
                .addChild(YtbModuleWithLeafList.LeafIdentifier.TIME, 0)
                .build();
        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(2, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("time", is(sid.name()));
        assertThat("yms:test:ytb:module:with:leaflist", is(sid.namespace()));
    }

    /**
     * Unit test case for model object identifier as container class.
     */
    @Test
    public void moIdWithContainer() {
        setUp();
        mid = ModelObjectId.builder().addChild(DefaultCont53.class).build();
        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(2, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("cont53", is(sid.name()));
        assertThat("modelObjectTest", is(sid.namespace()));
    }

    /**
     * Unit test case for model object identifier as container class and leaf.
     */
    @Test
    public void moIdWithContainerAndLeaf() {
        setUp();
        mid = ModelObjectId.builder().addChild(DefaultCont53.class)
                .addChild(Cont53.LeafIdentifier.LEAF55).build();
        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(3, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("cont53", is(sid.name()));
        assertThat("modelObjectTest", is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("leaf55", is(sid.name()));
        assertThat("modelObjectTest", is(sid.namespace()));
    }

    /**
     * Unit test case for model object identifier as list.
     */
    @Test
    public void moIdWithList() {
        setUp();
        List56Keys key = new List56Keys();
        mid = new ModelObjectId.Builder()
                .addChild(DefaultList56.class, key).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(2, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("list56", is(sid.name()));
        assertThat("modelObjectTest", is(sid.namespace()));
    }

    /**
     * Unit test with current node having augmented leaf as model object
     * identifier.
     */
    @Test
    public void modIdWithAugmentLeaf() {
        setUp();
        mid = ModelObjectId.builder()
                .addChild(DefaultFirstLevel.class)
                .addChild(DefaultContainerLeaf.class)
                .addChild(AugmentedContainerLeaf.LeafIdentifier.LEAFAUG)
                .build();
        rscId = builder.fetchResourceId(mid).build();

        nodeKeys = rscId.nodeKeys();
        assertThat(4, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("first-level", is(sid.name()));
        assertThat("yrt:model:converter:model:data:to:resource:data", is(sid.namespace()));
    }

    /**
     * Unit test case for model object identifier as list with leaf.
     */
    @Test
    public void moIdWithListAndLeaf() {
        setUp();
        List56Keys key = new List56Keys();
        mid = new ModelObjectId.Builder()
                .addChild(DefaultList56.class, key)
                .addChild(List56.LeafIdentifier.LEAF57, 10).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(3, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("list56", is(sid.name()));
        assertThat("modelObjectTest", is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("leaf57", is(sid.name()));
        assertThat("modelObjectTest", is(sid.namespace()));

        LeafListKey listKey = (LeafListKey) nodeKeys.get(2);
        assertThat(10, is(listKey.value()));
    }

    //TODO: check this how to do or discuss.

    /**
     * Unit test case for model object identifier as input.
     */
    @Test
    public void moIdWithInput() {
        addMockModWithRpc();

        mid = new ModelObjectId.Builder()
                .addChild(DefaultTestInput.class).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(3, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("test-rpc", is(sid.name()));
        assertThat("testNamespace", is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("input", is(sid.name()));
        assertThat("testNamespace", is(sid.namespace()));
    }

    /**
     * Unit test case for model object identifier input with container.
     */
    @Test
    public void moIdWithInputAndContainer() {
        addMockModWithRpc();

        mid = new ModelObjectId.Builder()
                .addChild(DefaultTestInput.class)
                .addChild(org.onosproject.yang.runtime.mockclass.testmodule
                                  .testrpc.testinput.DefaultTestContainer.class)
                .build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(4, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("test-rpc", is(sid.name()));
        assertThat("testNamespace", is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("input", is(sid.name()));
        assertThat("testNamespace", is(sid.namespace()));

        sid = nodeKeys.get(3).schemaId();
        assertThat("test-container", is(sid.name()));
        assertThat("testNamespace", is(sid.namespace()));
    }

    /**
     * Unit test case for model object identifier output.
     */
    @Test
    public void moIdWithOutput() {
        addMockModWithRpc();

        mid = new ModelObjectId.Builder()
                .addChild(DefaultTestOutput.class)
                .build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(3, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("test-rpc", is(sid.name()));
        assertThat("testNamespace", is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("output", is(sid.name()));
        assertThat("testNamespace", is(sid.namespace()));
    }

    /**
     * Unit test case for model object identifier as notification.
     */
    @Test
    public void moIdWithNotification() {
        addMockModWithNotification();

        mid = new ModelObjectId.Builder()
                .addChild(DefaultTestNotification.class).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(2, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("test-notification", is(sid.name()));
        assertThat("testNamespace", is(sid.namespace()));
    }

    /**
     * Unit test case for model object identifier as notification with
     * container.
     */
    @Test
    public void moIdWithNotificationAndContainer() {
        addMockModWithNotification();

        mid = new ModelObjectId.Builder()
                .addChild(DefaultTestNotification.class)
                .addChild(DefaultTestContainer.class).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(3, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("test-notification", is(sid.name()));
        assertThat("testNamespace", is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("test-container", is(sid.name()));
        assertThat("testNamespace", is(sid.namespace()));
    }

    @Test
    public void modWithMultiChildren() {
        setUp();

        MultiplexesKeys keys1 = new MultiplexesKeys();
        keys1.types(SPACE_DIVISION);
        ApplicationAreasKeys key2 = new ApplicationAreasKeys();
        mid = ModelObjectId.builder().addChild(DefaultCarrier.class)
                .addChild(DefaultMultiplexes.class, keys1)
                .addChild(DefaultApplicationAreas.class, key2).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(4, is(nodeKeys.size()));
        String nameSpace = "yms:test:ytb:tree:builder:for:list:having:list";

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("carrier", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("multiplexes", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(3).schemaId();
        assertThat("application-areas", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));
    }

    @Test
    public void modIdWithAugment() {

        setUp();
        List56Keys keys = new List56Keys();
        mid = ModelObjectId.builder()
                .addChild(DefaultList56.class, keys)
                .addChild(DefaultCont56.class).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(3, is(nodeKeys.size()));
        String nameSpace = "modelObjectTest";

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("list56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("cont56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        //-------------------------------------------//

        mid = ModelObjectId.builder()
                .addChild(DefaultList56.class, keys)
                .addChild(DefaultCont56.class)
                .addChild(Cont56.LeafIdentifier.CL56).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(4, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("list56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("cont56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(3).schemaId();
        assertThat("cl56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));
    }

    @Test
    public void modIdWithGroupings() {
        setUp();
        List56Keys keys = new List56Keys();
        mid = ModelObjectId.builder()
                .addChild(DefaultList56.class, keys)
                .addChild(DefaultCont56.class).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(3, is(nodeKeys.size()));
        String nameSpace = "modelObjectTest";

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("list56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("cont56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        //-------------------------------------------//

        mid = ModelObjectId.builder()
                .addChild(DefaultList56.class, keys)
                .addChild(DefaultCont56.class)
                .addChild(Cont56.LeafIdentifier.CL56).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(4, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("list56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("cont56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(3).schemaId();
        assertThat("cl56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        mid = ModelObjectId.builder()
                .addChild(DefaultList56.class, keys)
                .addChild(DefaultCont56.class)
                .addChild(DefaultCont57.class).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(4, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("list56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("cont56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(3).schemaId();
        assertThat("cont57", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        //-------------------------------------------//

    }

    @Test
    public void modIdWithGroupingTwo() {
        setUp();

        String nameSpace = "modelObjectTest";
        List56Keys keys = new List56Keys();
        mid = ModelObjectId.builder()
                .addChild(DefaultList56.class, keys)
                .addChild(DefaultCont56.class)
                .addChild(DefaultCont57.class)
                .addChild(DefaultCont58.class).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(5, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("list56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("cont56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(3).schemaId();
        assertThat("cont57", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(4).schemaId();
        assertThat("cont58", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        //-------------------------------------------//

        List57Keys keys1 = new List57Keys();
        keys1.gl57("gl57");
        mid = ModelObjectId.builder()
                .addChild(DefaultList56.class, keys)
                .addChild(DefaultCont56.class)
                .addChild(DefaultCont57.class)
                .addChild(DefaultList57.class, keys1).build();

        rscId = builder.fetchResourceId(mid).build();
        nodeKeys = rscId.nodeKeys();
        assertThat(5, is(nodeKeys.size()));

        sid = nodeKeys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = nodeKeys.get(1).schemaId();
        assertThat("list56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(2).schemaId();
        assertThat("cont56", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = nodeKeys.get(3).schemaId();
        assertThat("cont57", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        NodeKey key = nodeKeys.get(4);
        assertThat(true, is(key instanceof ListKey));

        sid = key.schemaId();
        assertThat("list57", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        ListKey listKey = (ListKey) key;
        List<KeyLeaf> leaves = listKey.keyLeafs();

        assertThat(1, is(leaves.size()));

        KeyLeaf leaf = leaves.get(0);
        sid = leaf.leafSchema();
        assertThat("gl57", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));
        assertThat("gl57", is(leaf.leafValue()));
    }


    /**
     * Returns mock node tree with module, rpc, input and output.
     *
     * @return mock node tree with module, rpc, input and output
     * @throws DataModelException when fails to do data model operations
     */
    private YangNode getModuleWithRpc() throws DataModelException {
        YangModule mod = getYangModule();
        YangRpc rpc = getRpcNode();
        mod.addChild(rpc);
        YangInput in = getInputNode();
        rpc.addChild(in);
        try {
            in.addChild(getMockContainer("org.onosproject.yang.runtime.mockclass" +
                                                 ".testmodule.testrpc" +
                                                 ".testinput"));
        } catch (DataModelException e) {
            log.info("test error");
        }
        rpc.addChild(getOutputNode());
        return mod;
    }

    /**
     * Returns mock node tree with module and notification.
     *
     * @return mock node tree with module and notification
     * @throws DataModelException when fails to do data model operations
     */
    private YangNode getModuleWithNotification() throws DataModelException {
        YangModule mod = getYangModule();
        YangNotification not = getNotificationNode();
        mod.addChild(not);
        try {
            not.addChild(getMockContainer("org.onosproject.yang.runtime" +
                                                  ".mockclass.testmodule" +
                                                  ".testnotification"));
        } catch (DataModelException e) {
            log.info("test error");
        }
        return mod;
    }

    /**
     * Returns mock module node.
     *
     * @return mock module node
     */
    private YangModule getYangModule() {
        YangModule mod = new YangModule() {
            @Override
            public List<YangNode> getNotificationNodes() {
                return null;
            }

            @Override
            public String getJavaPackage() {
                return "org.onosproject.yang.runtime.mockclass";
            }

            @Override
            public String getJavaClassNameOrBuiltInType() {
                return getJavaAttributeName();
            }

            @Override
            public String getJavaAttributeName() {
                return "testModule";
            }
        };
        mod.setName("test-module");
        mod.addLeaf(getLeafNode());
        mod.setNameSpace(getMockNamespace());
        return mod;
    }

    /**
     * Returns mock rpc node.
     *
     * @return mock rpc node
     */
    private YangRpc getRpcNode() {
        YangRpc rpc = new YangRpc() {
            @Override
            public String getJavaPackage() {
                return "org.onosproject.yang.runtime.mockclass.testmodule";
            }

            @Override
            public String getJavaClassNameOrBuiltInType() {
                return getJavaAttributeName();
            }

            @Override
            public String getJavaAttributeName() {
                return "testRpc";
            }
        };
        rpc.setName("test-rpc");
        rpc.setNameSpace(getMockNamespace());
        return rpc;
    }

    /**
     * Returns mock input node.
     *
     * @return mock input node
     */
    private YangInput getInputNode() {

        YangInput in = new YangInput() {
            @Override
            public String getJavaPackage() {
                return "org.onosproject.yang.runtime.mockclass" +
                        ".testmodule.testrpc";
            }

            @Override
            public String getJavaClassNameOrBuiltInType() {
                return getJavaAttributeName();
            }

            @Override
            public String getJavaAttributeName() {
                return "testInput";
            }
        };
        in.setName("input");
        in.setNameSpace(getMockNamespace());
        return in;
    }

    /**
     * Returns mock output node.
     *
     * @return mock output node
     */
    private YangOutput getOutputNode() {

        YangOutput out = new YangOutput() {
            @Override
            public String getJavaPackage() {
                return "org.onosproject.yang.runtime" +
                        ".mockclass.testmodule.testrpc";
            }

            @Override
            public String getJavaClassNameOrBuiltInType() {
                return getJavaAttributeName();
            }

            @Override
            public String getJavaAttributeName() {
                return "testOutput";
            }
        };
        out.setName("output");
        out.setNameSpace(getMockNamespace());
        return out;
    }

    /**
     * Returns mock notification node.
     *
     * @return mock notification node
     */
    private YangNotification getNotificationNode() {

        YangNotification not = new YangNotification() {
            @Override
            public String getJavaPackage() {
                return "org.onosproject.yang.runtime" +
                        ".mockclass.testmodule";
            }

            @Override
            public String getJavaClassNameOrBuiltInType() {
                return getJavaAttributeName();
            }

            @Override
            public String getJavaAttributeName() {
                return "testNotification";
            }
        };
        not.setName("test-notification");
        not.setNameSpace(getMockNamespace());
        return not;
    }

    /**
     * Returns mock container node.
     *
     * @return mock container node
     */
    private YangContainer getMockContainer(String pkg) {
        YangContainer con = new YangContainer() {
            @Override
            public String getJavaPackage() {
                return pkg;
            }

            @Override
            public String getJavaClassNameOrBuiltInType() {
                return getJavaAttributeName();
            }

            @Override
            public String getJavaAttributeName() {
                return "testContainer";
            }
        };
        con.setName("test-container");
        con.setNameSpace(getMockNamespace());
        return con;
    }

    /**
     * Returns mock leaf node.
     *
     * @return mock leaf node
     */
    private YangLeaf getLeafNode() {
        YangLeaf leaf = new YangLeaf() {
            @Override
            public String getJavaPackage() {
                return null;
            }

            @Override
            public String getJavaClassNameOrBuiltInType() {
                return getJavaAttributeName();
            }

            @Override
            public String getJavaAttributeName() {
                return "testLeaf";
            }
        };
        leaf.setName("test-leaf");
        leaf.setNameSpace(getMockNamespace());
        leaf.setDataType(getMockYangType());
        return leaf;
    }

    /**
     * Returns mock YANG type.
     *
     * @return data type
     */
    private YangType<?> getMockYangType() {
        YangType<?> type = new YangType<>();
        type.setDataType(INT32);
        type.setDataTypeName("int32");
        return type;
    }

    /**
     * Returns mock namespace.
     *
     * @return mock namespace
     */
    private YangNamespace getMockNamespace() {
        return new YangNamespace() {
            @Override
            public String getModuleNamespace() {
                return "testNamespace";
            }

            @Override
            public String getModuleName() {
                return "test-module";
            }
        };
    }

    /**
     * Returns qualified java name of node.
     *
     * @return name
     */
    private String getQualifiedName() {
        return "org.onosproject.yang.runtime.mockclass.TestModule";
    }
}
