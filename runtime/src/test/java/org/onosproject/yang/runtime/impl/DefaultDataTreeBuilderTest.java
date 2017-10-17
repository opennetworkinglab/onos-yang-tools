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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.DefaultModKey;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.ModKey;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.ModKeyKeys;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.DefaultCont;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.Tpdfun0;
import org.onosproject.yang.gen.v1.yrtietfte.rev20170310.yrtietfte.DefaultTe;
import org.onosproject.yang.gen.v1.yrtietfte.rev20170310.yrtietfte.tunnelp2pproperties.DefaultState;
import org.onosproject.yang.gen.v1.yrtietfte.rev20170310.yrtietfte.tunnelsgrouping.DefaultTunnels;
import org.onosproject.yang.gen.v1.yrtietfte.rev20170310.yrtietfte.tunnelsgrouping.tunnels.DefaultTunnel;
import org.onosproject.yang.gen.v1.yrtietftetypes.rev20160320.yrtietftetypes.TunnelP2p;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Cont1;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Def1;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Def2;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Def3;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Def6;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Def7;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.DefaultCont1;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Physical;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Virtual;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.cont1.Cont2;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.cont1.DefaultCont2;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.cont1.cont2.AugmentedCont2;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.cont1.cont2.DefaultAugmentedCont2;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.def1.Def1Union;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.def7.Def7Union;
import org.onosproject.yang.gen.v1.ytbietfschedule.rev20160826.YtbIetfSchedule;
import org.onosproject.yang.gen.v1.ytbietfschedule.rev20160826.ytbietfschedule.Enum1Enum;
import org.onosproject.yang.gen.v1.ytbietfschedule.rev20160826.ytbietfschedule.Enum2Enum;
import org.onosproject.yang.gen.v1.ytbmodulewithcontainer.rev20160826.ytbmodulewithcontainer.DefaultSched;
import org.onosproject.yang.gen.v1.ytbmodulewithleaflist.rev20160826.YtbModuleWithLeafList;
import org.onosproject.yang.gen.v1.ytbrpc.rev20160826.ytbrpc.content.DefaultContentInput;
import org.onosproject.yang.gen.v1.ytbrpc.rev20160826.ytbrpc.content.DefaultContentOutput;
import org.onosproject.yang.gen.v1.ytbrpc.rev20160826.ytbrpc.content.contentinput.DefaultIn;
import org.onosproject.yang.gen.v1.ytbrpc.rev20160826.ytbrpc.content.contentinput.InTypedef;
import org.onosproject.yang.gen.v1.ytbrpc.rev20160826.ytbrpc.content.contentoutput.outch.DefaultFirst;
import org.onosproject.yang.gen.v1.ytbtreebuilderforlisthavinglist.rev20160826.ytbtreebuilderforlisthavinglist.DefaultCarrier;
import org.onosproject.yang.gen.v1.ytbtreebuilderforlisthavinglist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.DefaultMultiplexes;
import org.onosproject.yang.gen.v1.ytbtreebuilderforlisthavinglist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.Multiplexes;
import org.onosproject.yang.gen.v1.ytbtreebuilderforlisthavinglist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.multiplexes.ApplicationAreas;
import org.onosproject.yang.gen.v1.ytbtreebuilderforlisthavinglist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.multiplexes.DefaultApplicationAreas;
import org.onosproject.yang.gen.v1.ytbtreebuilderforlisthavinglist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.multiplexes.TypesEnum;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultModelObjectData.Builder;
import org.onosproject.yang.model.InnerModelObject;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafIdentifier;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.LeafModelObject;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.runtime.mockclass.testmodule.DefaultTestNotification;
import org.onosproject.yang.runtime.mockclass.testmodule.testnotification.DefaultTestContainer;
import org.onosproject.yang.runtime.mockclass.testmodule.testrpc.DefaultTestInput;
import org.onosproject.yang.runtime.mockclass.testmodule.testrpc.DefaultTestOutput;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.Cont.LeafIdentifier.LFENUM1;
import static org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.tpdfun0.Tpdfun0Enum.SUCCESSFUL_EXIT;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF1;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF10;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF11;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF12;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF13;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF14;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF2;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF3;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF4;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF5;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF6;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF7;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF8;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LEAF9;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL1;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL10;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL11;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL12;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL13;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL14;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL15;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL2;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL3;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL4;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL5;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL6;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL7;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL8;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.YtbDataTypes.LeafIdentifier.LL9;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Cont1.LeafIdentifier.LEAF15;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Cont1.LeafIdentifier.LEAF16;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Cont1.LeafIdentifier.LL16;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.def1.def1union.Def1UnionEnum1.of;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateLeafDataNode;

/*
 *
 *  ModelObjectId | List<ModelObject> |    ResourceId       | List<DN>
 *    Null        |    Null           | For module          |  null
 *    Class       |    Null           | Form module to node |  null
 *    Class +Leaf |    Null           | From module to leaf |  null
 *    Class       |    Class          | From module to node |  class
 *    Leaf        |    Null           | From module to leaf |  null
 *    Class+leaf  |    Object         |  INVALID case       | -----
 */

/**
 * Unit test cases for YANG tree builder with different YANG object
 * configuration.
 */
public class DefaultDataTreeBuilderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ResourceData rscData;
    private DefaultDataTreeBuilder treeBuilder;
    private DefaultYangModelRegistry registry;
    private ResourceId id;
    private List<NodeKey> keys;
    private String nameSpace;
    private SchemaId sid;
    private List<DataNode> dataNodes;
    private DataNode node;
    private ModelObjectId mid;
    private Builder data;

    /**
     * Do the prior setup for each UT.
     */
    @Before
    public void setUp() {
        processSchemaRegistry();
        registry = registry();
        treeBuilder = new DefaultDataTreeBuilder(registry);
    }

    /**
     * Unit test to test resource data generation of a module with leaf
     * .resource id should start form "/" and should not contain info about the
     * leaf node . the list of data node should only have one entry and that
     * should be for leaf.
     */
    @Test
    public void processModuleAndLeaf() {
        //  As an application, creates the object.

        LeafModelObject modelObject = new LeafModelObject();
        modelObject.leafIdentifier(YtbIetfSchedule.LeafIdentifier.TIME);
        List<Object> objects = new ArrayList<>();
        objects.add(9);
        modelObject.values(objects);
        // Builds YANG tree in YTB.
        data = new Builder();
        data.addModelObject(modelObject);
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:module:with:leaf:ietfschedule";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(1, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);

        validateDataNode(node, "time", nameSpace, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "9");
    }

    /**
     * Unit test to test resource data generation of a module with leaf-list
     * .resource id should start form "/" and should not contain info about the
     * leaf list node . the list of data node should only have 3 entry and
     * that should be for leaf-list and its value.
     */
    @Test
    public void processModuleAndLeafList() {
        //As an application, creates the object.

        LeafModelObject modelObject = new LeafModelObject();
        modelObject.leafIdentifier(YtbModuleWithLeafList.LeafIdentifier.TIME);
        List<Object> objects = new ArrayList<>();
        objects.add(1);
        objects.add(2);
        objects.add(3);
        modelObject.values(objects);

        //Builds YANG tree in YTB.
        data = new Builder();
        data.addModelObject(modelObject);
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:module:with:leaflist";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(1, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(3, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "time", nameSpace, MULTI_INSTANCE_LEAF_VALUE_NODE,
                         true, "1");

        node = dataNodes.get(1);
        validateDataNode(node, "time", nameSpace, MULTI_INSTANCE_LEAF_VALUE_NODE,
                         true, "2");

        node = dataNodes.get(2);
        validateDataNode(node, "time", nameSpace, MULTI_INSTANCE_LEAF_VALUE_NODE,
                         true, "3");
    }

    /**
     * Unit test to test resource data generation of a module with one
     * instance of list with one leaf.resource id should start form "/" and
     * should not contain info about the list node. list of data node will
     * only contain node of list.
     */
    @Test
    public void processModuleListAndKeyOneIn() {
        //As an application, creates the object.
        DefaultModKey m1 = new DefaultModKey();
        m1.types(1);

        data = new Builder();

        data.addModelObject(m1);
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:tree:builder:for:list:having:list";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(1, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "modKey", nameSpace, MULTI_INSTANCE_NODE,
                         true, null);

        DataNode dataNode = node;

        Map<NodeKey, DataNode> childMap = ((InnerNode) dataNode).childNodes();
        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "types", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "1");

        Iterator<KeyLeaf> keyIt = ((ListKey) node.key()).keyLeafs().iterator();

        validateLeafDataNode(keyIt.next(), "types", nameSpace, "1");
    }

    /**
     * Unit test to test resource data generation of a module with multi
     * instance of list with one leaf.resource id should start form "/" and
     * should not contain info about the list node. list of data node will
     * only contain list nodes
     */
    @Test
    public void processModuleListAndKey() {
        //As an application, creates the object.
        DefaultModKey m1 = new DefaultModKey();
        m1.types(1);

        DefaultModKey m2 = new DefaultModKey();
        m2.types(2);

        DefaultModKey m3 = new DefaultModKey();
        m3.types(3);

        data = new Builder();

        data.addModelObject(m1).addModelObject(m2).addModelObject(m3);
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:tree:builder:for:list:having:list";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(1, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(3, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "modKey", nameSpace, MULTI_INSTANCE_NODE,
                         true, null);

        DataNode dataNode = node;

        Map<NodeKey, DataNode> childMap = ((InnerNode) dataNode).childNodes();
        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "types", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "1");

        Iterator<KeyLeaf> keyIt = ((ListKey) node.key()).keyLeafs().iterator();

        validateLeafDataNode(keyIt.next(), "types", nameSpace, "1");

        node = dataNodes.get(1);
        validateDataNode(node, "modKey", nameSpace, MULTI_INSTANCE_NODE,
                         true, null);

        dataNode = node;

        childMap = ((InnerNode) dataNode).childNodes();
        it = childMap.entrySet().iterator();
        n = it.next();
        validateDataNode(n.getValue(), "types", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "2");

        keyIt = ((ListKey) node.key()).keyLeafs().iterator();

        validateLeafDataNode(keyIt.next(), "types", nameSpace, "2");

        node = dataNodes.get(2);
        validateDataNode(node, "modKey", nameSpace, MULTI_INSTANCE_NODE,
                         true, null);

        dataNode = node;

        childMap = ((InnerNode) dataNode).childNodes();
        it = childMap.entrySet().iterator();
        n = it.next();
        validateDataNode(n.getValue(), "types", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "3");

        keyIt = ((ListKey) node.key()).keyLeafs().iterator();

        validateLeafDataNode(keyIt.next(), "types", nameSpace, "3");
    }

    /**
     * Unit test to test resource data generation of a module with multi
     * instance of list with one leaf.resource id should start form "/" and
     * should contain info about the list node as its part of model object
     * identifier. list of data node will only contain leaf nodes because we
     * have added list node objects in model object list.
     */
    @Test
    public void processModuleListAndKeyListModId() {
        data = new Builder();

        LeafModelObject object = new LeafModelObject();
        object.leafIdentifier(ModKey.LeafIdentifier.TYPES);
        List<Object> objects = new ArrayList<>();
        objects.add(1);
        object.values(objects);

        data.addModelObject(object);

        object = new LeafModelObject();
        object.leafIdentifier(ModKey.LeafIdentifier.TYPES);
        objects = new ArrayList<>();
        objects.add(2);
        object.values(objects);

        data.addModelObject(object);

        object = new LeafModelObject();
        object.leafIdentifier(ModKey.LeafIdentifier.TYPES);
        objects = new ArrayList<>();
        objects.add(3);
        object.values(objects);

        data.addModelObject(object);
        ModKeyKeys keyKeys = new ModKeyKeys();
        keyKeys.types(10);
        mid = ModelObjectId.builder()
                .addChild(DefaultModKey.class, keyKeys).build();

        data.identifier(mid);
        rscData = treeBuilder.getResourceData(data.build());

        id = rscData.resourceId();
        nameSpace = "yms:test:ytb:tree:builder:for:list:having:list";

        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("modKey", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        ListKey listKey = (ListKey) keys.get(1);

        Iterator<KeyLeaf> it = listKey.keyLeafs().iterator();
        assertThat(1, is(listKey.keyLeafs().size()));

        KeyLeaf keyLeaf = it.next();
        sid = keyLeaf.leafSchema();
        assertThat("types", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));
        assertThat(10, is(keyLeaf.leafValue()));

        dataNodes = rscData.dataNodes();
        assertThat(3, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "types", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "1");

        node = dataNodes.get(1);
        validateDataNode(node, "types", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "2");

        node = dataNodes.get(2);
        validateDataNode(node, "types", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "3");
    }


    /**
     * Unit test to test resource data generation of a module with multi
     * instance of list with one leaf.resource id should start form "/" and
     * should contain info about the list node and leaf node as these are part
     * of model object identifier. list of data node not contain anything
     * because in case of leaf being a part of model object identifier we
     * can't have any model object in model object list. so we will not
     * generate any data node.
     */
    @Test
    public void processModuleListAndKeyLeafModId() {
        data = new Builder();
        ModKeyKeys keyKeys = new ModKeyKeys();
        keyKeys.types(10);
        mid = ModelObjectId.builder()
                .addChild(DefaultModKey.class, keyKeys)
                .addChild(ModKey.LeafIdentifier.TYPES).build();

        data.identifier(mid);
        rscData = treeBuilder.getResourceData(data.build());

        id = rscData.resourceId();
        nameSpace = "yms:test:ytb:tree:builder:for:list:having:list";

        keys = id.nodeKeys();
        assertThat(3, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("modKey", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        ListKey listKey = (ListKey) keys.get(1);

        Iterator<KeyLeaf> it = listKey.keyLeafs().iterator();
        assertThat(1, is(listKey.keyLeafs().size()));

        KeyLeaf keyLeaf = it.next();
        sid = keyLeaf.leafSchema();
        assertThat("types", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));
        assertThat(10, is(keyLeaf.leafValue()));

        sid = keys.get(2).schemaId();
        assertThat("types", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        assertThat(true, is(rscData.dataNodes().isEmpty()));
    }

    /**
     * Unit test to test resource data generation of a module node with leaf
     * and multi instance leaf-list of enum types. resource identifier will
     * only contain module info "/" and will not contain leaf info. data
     * nodes will be for leaf and leaf-list. enum is an non data node so it
     * will not have a data node created for him. for each instance of
     * leaf-list one leaf instance data node will be added to data node list.
     */
    @Test
    public void processWithTypeEnum() {
        data = new Builder();
        //As an application, creates the object.
        LeafModelObject object = new LeafModelObject();
        object.leafIdentifier(YtbIetfSchedule.LeafIdentifier.TIME);
        List<Object> objects = new ArrayList<>();
        objects.add(9);
        object.values(objects);
        data.addModelObject(object);

        object = new LeafModelObject();
        object.leafIdentifier(YtbIetfSchedule.LeafIdentifier.ENUM1);
        objects = new ArrayList<>();
        objects.add(Enum1Enum.HUNDRED);
        object.values(objects);
        data.addModelObject(object);

        object = new LeafModelObject();
        object.leafIdentifier(YtbIetfSchedule.LeafIdentifier.ENUM2);
        objects = new ArrayList<>();
        objects.add(Enum2Enum.HUNDRED_100);
        objects.add(Enum2Enum.TEN_10);
        objects.add(Enum2Enum.THOUSAND_1000);
        object.values(objects);
        data.addModelObject(object);

        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:module:with:leaf:ietfschedule";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(1, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(5, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "time", nameSpace, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "9");

        node = dataNodes.get(1);
        validateDataNode(node, "enum1", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "hundred");

        node = dataNodes.get(2);
        validateDataNode(node, "enum2", nameSpace,
                         MULTI_INSTANCE_LEAF_VALUE_NODE,
                         true, "hundred-100");

        node = dataNodes.get(3);
        validateDataNode(node, "enum2", nameSpace, MULTI_INSTANCE_LEAF_VALUE_NODE,
                         true, "ten-10");

        node = dataNodes.get(4);
        validateDataNode(node, "enum2", nameSpace, MULTI_INSTANCE_LEAF_VALUE_NODE,
                         true, "thousand-1000");
    }

    /**
     * Unit test case for a module with container node in it. As model object
     * identifier is null so resource identifier should start from "/" and
     * should not contain any other info. Data node list should contain info
     * about container node.
     */
    @Test
    public void processModuleWithContainer() {
        // As an application, creates the object.

        //Creates container object with leaf of decimal type.
        BigDecimal dec = BigDecimal.valueOf(98989);
        DefaultSched sched = new DefaultSched();
        sched.predict(dec);

        // Builds YANG tree in YTB.
        data = new Builder();
        data.addModelObject(sched);
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:module:with:container";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(1, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "sched", nameSpace, SINGLE_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(1, is(child.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = ((InnerNode) node)
                .childNodes().entrySet().iterator();

        Map.Entry<NodeKey, DataNode> n = it.next();

        validateDataNode(n.getValue(), "predict", nameSpace, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "98989");
    }

    /**
     * Unit test case for a module with container node in it. As model object
     * identifier is having container so resource identifier should start from
     * "/" and it will have container node in it.and should not contain any
     * other info. Data node list should contain info about container node's
     * leaf node.
     */
    @Test
    public void processModuleWithContainerModId() {
        data = new Builder();
        //As an application, creates the object.
        LeafModelObject object = new LeafModelObject();
        object.leafIdentifier(DefaultSched.LeafIdentifier.PREDICT);
        List<Object> objects = new ArrayList<>();
        objects.add(BigDecimal.valueOf(98989));
        object.values(objects);
        data.addModelObject(object);

        // Builds YANG tree in YTB.
        mid = ModelObjectId.builder().addChild(DefaultSched.class).build();
        data.identifier(mid);

        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:module:with:container";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("sched", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));
        node = dataNodes.get(0);

        validateDataNode(node, "predict", nameSpace, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "98989");
    }

    /**
     * Unit test to process a module with a list and another list as child of
     * the list. Model object is null so resource id will be from "/". and
     * data nodes will be created from the first child node. that is container.
     */
    @Test
    public void processTreeBuilderForListHavingList() {
        // Creates two binary leaf -lists for two list app areas.
        List<byte[]> destArea1 = new ArrayList<>();
        byte[] arr = Base64.getDecoder().decode("QXdnRQ==");
        byte[] arr1 = Base64.getDecoder().decode("QlFZQg==");
        destArea1.add(arr);
        destArea1.add(arr1);

        List<byte[]> destArea2 = new ArrayList<>();
        byte[] arr2 = Base64.getDecoder().decode("QndjSA==");
        byte[] arr3 = Base64.getDecoder().decode("QUFFPQ==");
        destArea2.add(arr2);
        destArea2.add(arr3);

        //Creates two app areas list.
        ApplicationAreas appArea1 = new DefaultApplicationAreas();
        appArea1.destinationAreas(destArea1);
        ApplicationAreas appArea2 = new DefaultApplicationAreas();
        appArea2.destinationAreas(destArea2);

        List<ApplicationAreas> applicationAreasList = new ArrayList<>();
        applicationAreasList.add(appArea1);
        applicationAreasList.add(appArea2);

        //Adds two lists under the multiplex list for content 1.
        DefaultMultiplexes mpx1 = new DefaultMultiplexes();
        mpx1.types(TypesEnum.TIME_DIVISION);
        mpx1.applicationAreas(applicationAreasList);

        //Creates two binary leaf -lists for two list app areas.
        List<byte[]> destArea3 = new ArrayList<>();
        byte[] arrB = Base64.getDecoder().decode("QUtqaA==");
        byte[] arr1B = Base64.getDecoder().decode("TkJGag==");
        destArea3.add(arrB);
        destArea3.add(arr1B);

        List<byte[]> destArea4 = new ArrayList<>();
        byte[] arr2B = Base64.getDecoder().decode("SkhJOA==");
        byte[] arr3B = Base64.getDecoder().decode("MTExMQ==");
        destArea4.add(arr2B);
        destArea4.add(arr3B);

        //Creates two app areas list.
        ApplicationAreas appArea3 = new DefaultApplicationAreas();
        appArea3.destinationAreas(destArea3);
        ApplicationAreas appArea4 = new DefaultApplicationAreas();
        appArea4.destinationAreas(destArea4);

        List<ApplicationAreas> applicationAreasListB = new ArrayList<>();
        applicationAreasListB.add(appArea3);
        applicationAreasListB.add(appArea4);

        //Adds two lists under the multiplex list for content 2.
        DefaultMultiplexes mpx2 = new DefaultMultiplexes();
        mpx2.types(TypesEnum.FREQUENCY_DIVISION);
        mpx2.applicationAreas(applicationAreasListB);

        List<Multiplexes> multiplexList = new ArrayList<>();
        multiplexList.add(mpx1);
        multiplexList.add(mpx2);

        //Sets it in the container carrier.
        DefaultCarrier carrier = new DefaultCarrier();
        carrier.multiplexes(multiplexList);

        data = new Builder();
        data.addModelObject(carrier);
        rscData = treeBuilder.getResourceData(data.build());
        nameSpace = "yms:test:ytb:tree:builder:for:list:having:list";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(1, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "carrier", nameSpace,
                         SINGLE_INSTANCE_NODE, true, null);
        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(2, is(child.size()));
        Iterator<Map.Entry<NodeKey, DataNode>> it1 = child.entrySet()
                .iterator();

        Map.Entry<NodeKey, DataNode> n = it1.next();
        validateDataNode(n.getValue(), "multiplexes", nameSpace,
                         MULTI_INSTANCE_NODE, true, null);

        NodeKey key = n.getKey();
        assertThat(true, is(key instanceof ListKey));

        ListKey listKey = (ListKey) key;
        List<KeyLeaf> keyLeaves = listKey.keyLeafs();
        assertThat(1, is(keyLeaves.size()));

        validateLeafDataNode(keyLeaves.get(0), "types", nameSpace,
                             "time-division");

        node = n.getValue();
        child = ((InnerNode) node).childNodes();
        assertThat(2, is(child.size()));
        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();

        n = it.next();
        validateDataNode(n.getValue(), "types", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "time-division");

        n = it.next();
        validateDataNode(n.getValue(), "application-areas", nameSpace,
                         MULTI_INSTANCE_NODE, true, null);

        node = n.getValue();
        child = ((InnerNode) node).childNodes();
        assertThat(2, is(child.size()));
        it = child.entrySet().iterator();

        n = it.next();
        key = n.getKey();
        assertTrue(key instanceof LeafListKey);

        LeafListKey leafListKey = (LeafListKey) key;
        assertEquals("Wrong Base64 value", "QndjSA==", leafListKey.value());

        sid = key.schemaId();
        assertThat("destination-areas", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        n = it.next();
        key = n.getKey();
        assertThat(true, is(key instanceof LeafListKey));

        leafListKey = (LeafListKey) key;
        assertEquals("Wrong Base64 value", "QUFFPQ==", leafListKey.value());

        sid = key.schemaId();
        assertThat("destination-areas", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));
        //Check for max2.
        n = it1.next();
        validateDataNode(n.getValue(), "multiplexes", nameSpace,
                         MULTI_INSTANCE_NODE, true, null);

        key = n.getKey();
        assertThat(true, is(key instanceof ListKey));

        listKey = (ListKey) key;
        keyLeaves = listKey.keyLeafs();
        assertThat(1, is(keyLeaves.size()));

        validateLeafDataNode(keyLeaves.get(0), "types", nameSpace,
                             "frequency-division");

        node = n.getValue();
        child = ((InnerNode) node).childNodes();
        assertThat(2, is(child.size()));
        it = child.entrySet().iterator();

        n = it.next();
        validateDataNode(n.getValue(), "types", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "frequency-division");
        n = it.next();
        validateDataNode(n.getValue(), "application-areas", nameSpace,
                         MULTI_INSTANCE_NODE, true, null);

        node = n.getValue();
        child = ((InnerNode) node).childNodes();
        assertThat(2, is(child.size()));
        it = child.entrySet().iterator();

        n = it.next();
        key = n.getKey();
        assertThat(true, is(key instanceof LeafListKey));

        leafListKey = (LeafListKey) key;
        assertEquals("Wrong Base64 value", "SkhJOA==", leafListKey.value());

        sid = key.schemaId();
        assertThat("destination-areas", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        n = it.next();
        key = n.getKey();
        assertThat(true, is(key instanceof LeafListKey));

        leafListKey = (LeafListKey) key;
        assertEquals("Wrong Base64 value", "MTExMQ==", leafListKey.value());

        sid = key.schemaId();
        assertThat("destination-areas", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));
    }

    /**
     * Unit test to process a module with notification. for notification we
     * must add notification class in model object id. the resource
     * identifier will contain "/ and notification node in it. and
     * notification does not have any child not or leaf node datanodes will
     * be empty.
     */
    @Test
    public void processNotification() {
        MoIdToRscIdTest ut = new MoIdToRscIdTest();
        ut.addMockModWithNotification();

        data = new Builder();
        mid = ModelObjectId.builder()
                .addChild(DefaultTestNotification.class).build();
        data.identifier(mid);

        registry = ut.reg;
        DefaultDataTreeBuilder builder = new DefaultDataTreeBuilder(registry);
        rscData = builder.getResourceData(data.build());

        nameSpace = "testNamespace";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("test-notification", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(0, is(dataNodes.size()));
    }

    /**
     * Unit test to process a module with notification. for notification we
     * must add notification class in model object id. the resource
     * identifier will contain "/ and notification node in it. and
     * notification's child node will be in data node list.
     */
    @Test
    public void processNotificationWithContainer() {
        MoIdToRscIdTest ut = new MoIdToRscIdTest();
        ut.addMockModWithNotification();

        data = new Builder();
        mid = ModelObjectId.builder()
                .addChild(DefaultTestNotification.class).build();
        data.identifier(mid);
        data.addModelObject(new DefaultTestContainer());

        registry = ut.reg;
        DefaultDataTreeBuilder builder = new DefaultDataTreeBuilder(registry);
        rscData = builder.getResourceData(data.build());

        nameSpace = "testNamespace";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("test-notification", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);

        validateDataNode(node, "test-container", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);
    }

    /**
     * Unit test for a module containing rpc. Model object contains input
     * class so resource identifier will start from "/" and will go till
     * input node. it will contains rpc node as well. as object is given for
     * input node so data node will be empty.
     */
    @Test
    public void processRpcWithInput() {
        MoIdToRscIdTest ut = new MoIdToRscIdTest();
        ut.addMockModWithRpc();

        data = new Builder();
        mid = ModelObjectId.builder()
                .addChild(DefaultTestInput.class).build();
        data.identifier(mid);

        registry = ut.reg;
        DefaultDataTreeBuilder builder = new DefaultDataTreeBuilder(registry);
        rscData = builder.getResourceData(data.build());

        nameSpace = "testNamespace";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(3, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("test-rpc", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = keys.get(2).schemaId();
        assertThat("input", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(0, is(dataNodes.size()));
    }

    /**
     * Unit test for a module containing rpc. Model object contains input
     * class so resource identifier will start from "/" and will go till
     * rpc node.as object is given for input node so data node will be having
     * input node.
     */
    @Test
    public void processRpcWithInputDataNode() {
        MoIdToRscIdTest ut = new MoIdToRscIdTest();
        ut.addMockModWithRpc();

        DefaultTestInput input = new DefaultTestInput();
        input.testContainer(new DefaultTestContainer());
        data = new Builder();
        data.addModelObject(input);

        registry = ut.reg;
        DefaultDataTreeBuilder builder = new DefaultDataTreeBuilder(registry);
        rscData = builder.getResourceData(data.build());

        nameSpace = "testNamespace";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("test-rpc", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "input", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);
    }

    /**
     * Unit test for a module containing rpc. Model object contains input
     * class so resource identifier will start from "/" and will go till
     * rpc node.as object is given for output node so data node will be having
     * input node.
     */
    @Test
    public void processRpcWithOutputDataNode() {
        MoIdToRscIdTest ut = new MoIdToRscIdTest();
        ut.addMockModWithRpc();

        data = new Builder();
        data.addModelObject(new DefaultTestOutput());

        registry = ut.reg;
        DefaultDataTreeBuilder builder = new DefaultDataTreeBuilder(registry);
        rscData = builder.getResourceData(data.build());

        nameSpace = "testNamespace";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("test-rpc", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "output", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);
    }

    /**
     * Unit test for a module containing rpc. Model object contains input
     * class so resource identifier will start from "/" and will go till
     * input node. it will contains rpc node as well. as object is given for
     * input node so data node will be for container node.
     */
    @Test
    public void processRpcWithInputModId() {
        MoIdToRscIdTest ut = new MoIdToRscIdTest();
        ut.addMockModWithRpc();

        data = new Builder();
        mid = ModelObjectId.builder()
                .addChild(DefaultTestInput.class).build();
        data.identifier(mid);
        data.addModelObject(new org.onosproject.yang.runtime.mockclass
                .testmodule.testrpc.testinput.DefaultTestContainer());

        registry = ut.reg;
        DefaultDataTreeBuilder builder = new DefaultDataTreeBuilder(registry);
        rscData = builder.getResourceData(data.build());

        nameSpace = "testNamespace";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(3, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("test-rpc", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = keys.get(2).schemaId();
        assertThat("input", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);

        validateDataNode(node, "test-container", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);
    }

    /**
     * Unit test for leaf node with enumeration type.
     */
    @Test
    public void processEnumeration() {
        data = new Builder();
        ModelObjectId.Builder moIdBdlr = ModelObjectId.builder()
                .addChild(DefaultCont.class);
        LeafModelObject mo = new LeafModelObject();
        mo.leafIdentifier(LFENUM1);
        mo.addValue(new Tpdfun0(SUCCESSFUL_EXIT));
        data.addModelObject(mo);
        data.identifier(moIdBdlr.build());
        DefaultDataTreeBuilder builder = new DefaultDataTreeBuilder(registry);
        rscData = builder.getResourceData(data.build());

        nameSpace = "simple:data:types";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("cont", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));
        node = dataNodes.get(0);
        validateDataNode(node, "lfenum1", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "successful exit");
    }

    /**
     * Unit test for identity-ref.
     */
    @Test
    public void processIdentityRef() {
        DefaultState state = new DefaultState();
        state.type(TunnelP2p.class);
        DefaultTunnel tunnel = new DefaultTunnel();
        tunnel.state(state);
        DefaultTunnels tunnels = new DefaultTunnels();
        tunnels.addToTunnel(tunnel);
        DefaultTe te = new DefaultTe();
        te.tunnels(tunnels);

        data = new Builder();
        data.addModelObject(te);
        DefaultDataTreeBuilder builder = new DefaultDataTreeBuilder(registry);
        rscData = builder.getResourceData(data.build());

        DataNode node = rscData.dataNodes().get(0);
        String ns = "urn:ietf:params:xml:ns:yang:ietf-te";
        validateDataNode(node, "te", ns, SINGLE_INSTANCE_NODE, true, null);
        NodeKey key = NodeKey.builder().schemaId("tunnels", ns).build();
        DataNode childNode = ((InnerNode) node).childNodes().get(key);
        key = NodeKey.builder().schemaId("tunnel", ns).build();
        childNode = ((InnerNode) childNode).childNodes().get(key);
        key = NodeKey.builder().schemaId("state", ns).build();
        childNode = ((InnerNode) childNode).childNodes().get(key);
        key = NodeKey.builder().schemaId("type", ns).build();
        childNode = ((InnerNode) childNode).childNodes().get(key);
        validateDataNode(childNode, "type", ns, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "tunnel-p2p");
    }

    /**
     * Unit test for RPC with grouping and augment in file.
     */
    @Test
    public void processRpc() {
        InTypedef typedef = new InTypedef("con-leaf");
        DefaultIn con = new DefaultIn();
        DefaultContentInput input = new DefaultContentInput();
        con.conIn(typedef);
        input.in(con);

        data = new Builder();
        data.addModelObject(input);
        rscData = treeBuilder.getResourceData(data.build());

        String ns = "yms:test:ytb:ytb:rpc";
        List<DataNode> inDn = rscData.dataNodes();
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("content", is(sid.name()));
        assertThat(ns, is(sid.namespace()));

        DataNode node = inDn.get(0);
        validateDataNode(node, "input", ns, SINGLE_INSTANCE_NODE, true, null);

        List<Short> ll = new LinkedList<>();
        DefaultFirst first = new DefaultFirst();
        DefaultContentOutput output = new DefaultContentOutput();

        ll.add((short) 9);
        first.call(ll);
        output.outCh(first);

        data = new Builder();
        data.addModelObject(output);
        rscData = treeBuilder.getResourceData(data.build());

        inDn = rscData.dataNodes();
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("content", is(sid.name()));
        assertThat(ns, is(sid.namespace()));

        node = inDn.get(0);
        validateDataNode(node, "output", ns, SINGLE_INSTANCE_NODE, true, null);
    }

    /**
     * Unit test for proper conversion of data types to data node.
     */
    @Test
    public void processDataTypesToDataNode() {
        data = new Builder();
        data = buildRootLeafAndLeafList(data);
        data = buildContainer(data);
        rscData = treeBuilder.getResourceData(data.build());

        List<DataNode> inDn = rscData.dataNodes();
        String ns = "yms:test:ytb:data:types";
        Iterator<DataNode> it = inDn.iterator();

        it = validate(it, ns);

        DataNode cont1 = it.next();
        validateDataNode(cont1, "cont1", ns, SINGLE_INSTANCE_NODE, true, null);

        Map<NodeKey, DataNode> c1 = ((InnerNode) cont1).childNodes();
        List<DataNode> cont1DN = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : c1.entrySet()) {
            cont1DN.add(c.getValue());
        }
        Iterator<DataNode> it1 = cont1DN.iterator();
        it1 = validate(it1, ns);

        DataNode cont2 = it1.next();
        validateDataNode(cont2, "cont2", ns, SINGLE_INSTANCE_NODE, true, null);

        Map<NodeKey, DataNode> c2 = ((InnerNode) cont2).childNodes();
        List<DataNode> cont2DN = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : c2.entrySet()) {
            cont2DN.add(c.getValue());
        }
        Iterator<DataNode> it2 = cont2DN.iterator();
        validate(it2, ns);
    }

    /**
     * Validates the leaf and leaf-list value under the specified node.
     *
     * @param it data node iterator
     * @param ns name space
     * @return data node iterator
     */
    private Iterator<DataNode> validate(Iterator<DataNode> it, String ns) {

        validateDataNode(it.next(), "leaf1", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "leaf1");
        validateDataNode(it.next(), "leaf2", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "thousand");
        validateDataNode(it.next(), "leaf3", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "MTEwMTE="); //Base 64 encoding of '11011'
        validateDataNode(it.next(), "leaf4", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "thousand");
        validateDataNode(it.next(), "leaf5", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "physical");
        validateDataNode(it.next(), "leaf6", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "/cont1");
        validateDataNode(it.next(), "leaf7", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "b1 b2");
        validateDataNode(it.next(), "leaf8", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, null);
        validateDataNode(it.next(), "leaf9", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "b1 b2");
        validateDataNode(it.next(), "leaf11", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, null);
        validateDataNode(it.next(), "leaf12", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                        "MTEwMTE="); //Base 64 encoding of '11011'
        validateDataNode(it.next(), "leaf13", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "*");
        validateDataNode(it.next(), "leaf14", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "physical");
        validateDataNode(it.next(), "leaf15", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "physical");
        validateDataNode(it.next(), "leaf16", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "3.3ms");
        validateDataNode(it.next(), "ll1", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "leaf-list1");
        validateDataNode(it.next(), "ll1", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "leaf-list1-2");
        validateDataNode(it.next(), "ll2", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "thousand");
        validateDataNode(it.next(), "ll2", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "ten");
        validateDataNode(it.next(), "ll3", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true,
                         "MTEwMTE="); //Base 64 encoding of '11011'
        validateDataNode(it.next(), "ll3", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true,
                        "MTEwMTEx"); //Base 64 encoding of '110111'
        validateDataNode(it.next(), "ll4", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "thousand");
        validateDataNode(it.next(), "ll4", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "ten");
        validateDataNode(it.next(), "ll5", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "physical");
        validateDataNode(it.next(), "ll5", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "virtual");
        validateDataNode(it.next(), "ll6", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "/cont1");
        validateDataNode(it.next(), "ll6", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "/cont1/cont2");
        validateDataNode(it.next(), "ll7", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "b1 b2");
        validateDataNode(it.next(), "ll7", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "b1 b2 b3");
        validateDataNode(it.next(), "ll8", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, null);
        validateDataNode(it.next(), "ll9", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "b1 b2");
        validateDataNode(it.next(), "ll9", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "b1 b2 b3");
        validateDataNode(it.next(), "ll11", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, null);
        validateDataNode(it.next(), "ll12", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true,
                         "MTEwMTE="); //Base 64 encoding of '11011'
        validateDataNode(it.next(), "ll12", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true,
                         "MTEwMTEx"); //Base 64 encoding of '110111'
        validateDataNode(it.next(), "ll13", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "*");
        validateDataNode(it.next(), "ll14", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "physical");
        validateDataNode(it.next(), "ll14", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "virtual");
        validateDataNode(it.next(), "ll15", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "physical");
        validateDataNode(it.next(), "ll16", ns,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "3.3ms");
        return it;
    }

    /**
     * Builds the container node with required value and returns the model
     * object data.
     *
     * @param data model object data
     * @return model object data builder
     */
    private Builder buildContainer(Builder data) {
        Cont2 cont2 = buildAugmentForCont2();
        Def1Union union = new Def1Union(of(1000));
        Def1 def1 = new Def1(union);
        Def1Union union2 = new Def1Union(of(10));
        Def1Union union22 = new Def1Union(of(10000));
        Def1Union union33 = new Def1Union(of(100000));
        Def1 def13 = new Def1(union33);
        Def1 def11 = new Def1(union22);
        Def1 def12 = new Def1(union2);
        byte[] arr = Base64.getDecoder().decode("MTEwMTE=");
        byte[] arr1 = Base64.getDecoder().decode("MTEwMTEx");
        BitSet bits = new BitSet();
        bits.set(0, 2, true);
        BitSet bits1 = new BitSet();
        bits1.set(0, 3, true);
        Def2 def2 = new Def2(bits);
        Def2 def21 = new Def2(bits1);
        Def3 def3 = new Def3(false);
        Def3 def31 = new Def3(true);
        Def6 def6 = new Def6(Physical.class);
        Def6 def61 = new Def6(Virtual.class);
        Def7Union def7 = new Def7Union(Physical.class);
        Def7 def71 = new Def7(def7);
        Cont1 cont1 = new DefaultCont1();
        cont1.leaf1("leaf1");
        cont1.leaf2(def1);
        cont1.leaf3(arr);
        cont1.leaf4(def1);
        cont1.leaf5(Physical.class);
        cont1.leaf6("/cont1");
        cont1.leaf7(bits);
        cont1.leaf8(true);
        cont1.leaf9(def2);
        cont1.leaf10(def3);
        cont1.leaf11(true);
        cont1.leaf12(arr);
        cont1.leaf13(def11);
        cont1.leaf14(def6);
        cont1.leaf15(def71);
        cont1.leaf16(def13);
        cont1.addToLl1("leaf-list1");
        cont1.addToLl1("leaf-list1-2");
        cont1.addToLl2(def1);
        cont1.addToLl2(def12);
        cont1.addToLl3(arr);
        cont1.addToLl3(arr1);
        cont1.addToLl4(def1);
        cont1.addToLl4(def12);
        cont1.addToLl5(Physical.class);
        cont1.addToLl5(Virtual.class);
        cont1.addToLl6("/cont1");
        cont1.addToLl6("/cont1/cont2");
        cont1.addToLl7(bits);
        cont1.addToLl7(bits1);
        cont1.addToLl8(true);
        cont1.addToLl8(false);
        cont1.addToLl9(def2);
        cont1.addToLl9(def21);
        cont1.addToLl10(def3);
        cont1.addToLl10(def31);
        cont1.addToLl11(true);
        cont1.addToLl11(true);
        cont1.addToLl12(arr);
        cont1.addToLl12(arr1);
        cont1.addToLl13(def11);
        cont1.addToLl14(def6);
        cont1.addToLl14(def61);
        cont1.addToLl15(def71);
        cont1.addToLl16(def13);
        cont1.cont2(cont2);
        data.addModelObject((ModelObject) cont1);
        return data;
    }

    /**
     * Builds the augment node with required value and updates it in the
     * container cont2.
     *
     * @return cont2
     */
    private Cont2 buildAugmentForCont2() {
        Cont2 cont2 = new DefaultCont2();
        AugmentedCont2 augC = new DefaultAugmentedCont2();
        Def1Union union = new Def1Union(of(1000));
        Def1 def1 = new Def1(union);
        Def1Union union2 = new Def1Union(of(10));
        Def1Union union22 = new Def1Union(of(10000));
        Def1 def11 = new Def1(union22);
        Def1 def12 = new Def1(union2);
        Def1Union union33 = new Def1Union(of(100000));
        Def1 def13 = new Def1(union33);
        byte[] arr = Base64.getDecoder().decode("MTEwMTE=");
        byte[] arr1 = Base64.getDecoder().decode("MTEwMTEx");
        BitSet bits = new BitSet();
        bits.set(0, 2, true);
        BitSet bits1 = new BitSet();
        bits1.set(0, 3, true);
        Def2 def2 = new Def2(bits);
        Def2 def21 = new Def2(bits1);
        Def3 def3 = new Def3(false);
        Def3 def31 = new Def3(true);
        Def6 def6 = new Def6(Physical.class);
        Def6 def61 = new Def6(Virtual.class);
        Def7Union def7 = new Def7Union(Physical.class);
        Def7 def71 = new Def7(def7);
        augC.leaf1("leaf1");
        augC.leaf2(def1);
        augC.leaf3(arr);
        augC.leaf4(def1);
        augC.leaf5(Physical.class);
        augC.leaf6("/cont1");
        augC.leaf7(bits);
        augC.leaf8(true);
        augC.leaf9(def2);
        augC.leaf10(def3);
        augC.leaf11(true);
        augC.leaf12(arr);
        augC.leaf13(def11);
        augC.leaf14(def6);
        augC.leaf15(def71);
        augC.leaf16(def13);
        augC.addToLl1("leaf-list1");
        augC.addToLl1("leaf-list1-2");
        augC.addToLl2(def1);
        augC.addToLl2(def12);
        augC.addToLl3(arr);
        augC.addToLl3(arr1);
        augC.addToLl4(def1);
        augC.addToLl4(def12);
        augC.addToLl5(Physical.class);
        augC.addToLl5(Virtual.class);
        augC.addToLl6("/cont1");
        augC.addToLl6("/cont1/cont2");
        augC.addToLl7(bits);
        augC.addToLl7(bits1);
        augC.addToLl8(true);
        augC.addToLl8(false);
        augC.addToLl9(def2);
        augC.addToLl9(def21);
        augC.addToLl10(def3);
        augC.addToLl10(def31);
        augC.addToLl11(true);
        augC.addToLl11(true);
        augC.addToLl12(arr);
        augC.addToLl12(arr1);
        augC.addToLl13(def11);
        augC.addToLl14(def6);
        augC.addToLl14(def61);
        augC.addToLl15(def71);
        augC.addToLl16(def13);
        cont2.addAugmentation((InnerModelObject) augC);
        return cont2;
    }

    /**
     * Builds the root leaf and leaf-list and adds it in the model object
     * data builder.
     *
     * @param data model object data
     * @return model object data builder
     */
    private Builder buildRootLeafAndLeafList(Builder data) {

        data = addLeafModelObject(LEAF1, "leaf1", data);

        Def1Union union = new Def1Union(of("thousand"));
        Def1 def1 = new Def1(union);
        Def1Union union22 = new Def1Union(of("*"));
        Def1 def11 = new Def1(union22);
        Def1Union union33 = new Def1Union(of("3.3ms"));
        Def1 def13 = new Def1(union33);
        data = addLeafModelObject(LEAF2, def1, data);

        byte[] arr = Base64.getDecoder().decode("MTEwMTE=");
        data = addLeafModelObject(LEAF3, arr, data);

        data = addLeafModelObject(LEAF4, def1, data);

        data = addLeafModelObject(LEAF5, Physical.class, data);

        data = addLeafModelObject(LEAF6, "/cont1", data);

        BitSet bits = new BitSet();
        bits.set(0, 2, true);
        data = addLeafModelObject(LEAF7, bits, data);

        data = addLeafModelObject(LEAF8, true, data);

        Def2 def2 = new Def2(bits);
        data = addLeafModelObject(LEAF9, def2, data);

        Def3 def3 = new Def3(false);
        data = addLeafModelObject(LEAF10, def3, data);

        data = addLeafModelObject(LEAF11, true, data);

        data = addLeafModelObject(LEAF12, arr, data);

        data = addLeafModelObject(LEAF13, def11, data);

        Def6 def6 = new Def6(Physical.class);
        Def6 def61 = new Def6(Virtual.class);
        data = addLeafModelObject(LEAF14, def6, data);

        Def7Union def7 = new Def7Union(Physical.class);
        Def7 def71 = new Def7(def7);
        data = addLeafModelObject(LEAF15, def71, data);

        data = addLeafModelObject(LEAF16, def13, data);
        List<Object> objs = new LinkedList<>();
        objs.add("leaf-list1");
        objs.add("leaf-list1-2");
        data = addLeafListModelObject(LL1, objs, data);

        Def1Union union2 = new Def1Union(of("ten"));
        Def1 def12 = new Def1(union2);
        objs = new LinkedList<>();
        objs.add(def1);
        objs.add(def12);
        data = addLeafListModelObject(LL2, objs, data);

        byte[] arr1 = Base64.getDecoder().decode("MTEwMTEx");
        objs = new LinkedList<>();
        objs.add(arr);
        objs.add(arr1);
        data = addLeafListModelObject(LL3, objs, data);

        objs = new LinkedList<>();
        objs.add(def1);
        objs.add(def12);
        data = addLeafListModelObject(LL4, objs, data);

        objs = new LinkedList<>();
        objs.add(Physical.class);
        objs.add(Virtual.class);
        data = addLeafListModelObject(LL5, objs, data);

        objs = new LinkedList<>();
        objs.add("/cont1");
        objs.add("/cont1/cont2");
        data = addLeafListModelObject(LL6, objs, data);

        BitSet bits2 = new BitSet();
        bits2.set(0, 3, true);
        objs = new LinkedList<>();
        objs.add(bits);
        objs.add(bits2);
        data = addLeafListModelObject(LL7, objs, data);

        objs = new LinkedList<>();
        objs.add(true);
        objs.add(false);
        data = addLeafListModelObject(LL8, objs, data);

        Def2 def21 = new Def2(bits2);
        objs = new LinkedList<>();
        objs.add(def2);
        objs.add(def21);
        data = addLeafListModelObject(LL9, objs, data);

        Def3 def31 = new Def3(true);
        objs = new LinkedList<>();
        objs.add(def3);
        objs.add(def31);
        data = addLeafListModelObject(LL10, objs, data);

        objs = new LinkedList<>();
        objs.add(true);
        objs.add(true);
        data = addLeafListModelObject(LL11, objs, data);

        objs = new LinkedList<>();
        objs.add(arr);
        objs.add(arr1);
        data = addLeafListModelObject(LL12, objs, data);

        objs = new LinkedList<>();
        objs.add(def11);
        data = addLeafListModelObject(LL13, objs, data);

        objs = new LinkedList<>();
        objs.add(def6);
        objs.add(def61);
        data = addLeafListModelObject(LL14, objs, data);

        objs = new LinkedList<>();
        objs.add(def71);
        data = addLeafListModelObject(LL15, objs, data);

        objs = new LinkedList<>();
        objs.add(def13);
        data = addLeafListModelObject(LL16, objs, data);
        return data;
    }

    /**
     * Adds the leaf model object to the model object data builder with leaf
     * value.
     *
     * @param lId    leaf identifier
     * @param object leaf object
     * @param data   model object data
     * @return model object data builder
     */
    private Builder addLeafModelObject(LeafIdentifier lId, Object object,
                                       Builder data) {
        LeafModelObject obj = new LeafModelObject();
        obj.leafIdentifier(lId);
        obj.addValue(object);
        data.addModelObject(obj);
        return data;
    }

    /**
     * Adds the leaf-list model objects to the model object data builder with
     * leaf-list values.
     *
     * @param lId    leaf-list identifier
     * @param object leaf-list object
     * @param data   model object data
     * @return model object data builder
     */
    private Builder addLeafListModelObject(LeafIdentifier lId,
                                           List<Object> object, Builder data) {
        LeafModelObject obj = new LeafModelObject();
        obj.leafIdentifier(lId);
        for (Object o : object) {
            obj.addValue(o);
        }
        data.addModelObject(obj);
        return data;
    }
}
