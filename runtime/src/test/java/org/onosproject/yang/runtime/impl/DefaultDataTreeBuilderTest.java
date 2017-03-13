/*
 * Copyright 2017-present Open Networking Laboratory
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.gen.v1.yms.test.ytb.module.with.container.rev20160826.ytbmodulewithcontainer.DefaultSched;
import org.onosproject.yang.gen.v1.yms.test.ytb.module.with.leaf.ietfschedule.rev20160826.YtbIetfSchedule;
import org.onosproject.yang.gen.v1.yms.test.ytb.module.with.leaf.ietfschedule.rev20160826.ytbietfschedule.Enum1Enum;
import org.onosproject.yang.gen.v1.yms.test.ytb.module.with.leaf.ietfschedule.rev20160826.ytbietfschedule.Enum2Enum;
import org.onosproject.yang.gen.v1.yms.test.ytb.module.with.leaflist.rev20160826.YtbModuleWithLeafList;
import org.onosproject.yang.gen.v1.yms.test.ytb.tree.builder.yangautoprefixfor.yangautoprefixlist.having.yangautoprefixlist.rev20160826.modulelistandkey.DefaultModKey;
import org.onosproject.yang.gen.v1.yms.test.ytb.tree.builder.yangautoprefixfor.yangautoprefixlist.having.yangautoprefixlist.rev20160826.modulelistandkey.ModKey;
import org.onosproject.yang.gen.v1.yms.test.ytb.tree.builder.yangautoprefixfor.yangautoprefixlist.having.yangautoprefixlist.rev20160826.modulelistandkey.ModKeyKeys;
import org.onosproject.yang.gen.v1.yms.test.ytb.tree.builder.yangautoprefixfor.yangautoprefixlist.having.yangautoprefixlist.rev20160826.ytbtreebuilderforlisthavinglist.DefaultCarrier;
import org.onosproject.yang.gen.v1.yms.test.ytb.tree.builder.yangautoprefixfor.yangautoprefixlist.having.yangautoprefixlist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.DefaultMultiplexes;
import org.onosproject.yang.gen.v1.yms.test.ytb.tree.builder.yangautoprefixfor.yangautoprefixlist.having.yangautoprefixlist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.Multiplexes;
import org.onosproject.yang.gen.v1.yms.test.ytb.tree.builder.yangautoprefixfor.yangautoprefixlist.having.yangautoprefixlist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.multiplexes.ApplicationAreas;
import org.onosproject.yang.gen.v1.yms.test.ytb.tree.builder.yangautoprefixfor.yangautoprefixlist.having.yangautoprefixlist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.multiplexes.DefaultApplicationAreas;
import org.onosproject.yang.gen.v1.yms.test.ytb.tree.builder.yangautoprefixfor.yangautoprefixlist.having.yangautoprefixlist.rev20160826.ytbtreebuilderforlisthavinglist.carrier.multiplexes.TypesEnum;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultModelObjectData;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.LeafModelObject;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.runtime.mockclass.testmodule.DefaultTestNotification;
import org.onosproject.yang.runtime.mockclass.testmodule.testnotification.DefaultTestContainer;
import org.onosproject.yang.runtime.mockclass.testmodule.testrpc.DefaultTestInput;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
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
    private DefaultModelObjectData.Builder data;
    private final TestYangSchemaNodeProvider schemaProvider = new
            TestYangSchemaNodeProvider();

    /**
     * Do the prior setup for each UT.
     */
    private void setUp() {
        schemaProvider.processSchemaRegistry();
        registry = schemaProvider.registry();
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
        setUp();

        //  As an application, creates the object.

        LeafModelObject modelObject = new LeafModelObject();
        modelObject.leafIdentifier(YtbIetfSchedule.LeafIdentifier.TIME);
        List<Object> objects = new ArrayList<>();
        objects.add(9);
        modelObject.values(objects);
        // Builds YANG tree in YTB.
        data = new DefaultModelObjectData.Builder();
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

        setUp();
        //As an application, creates the object.

        LeafModelObject modelObject = new LeafModelObject();
        modelObject.leafIdentifier(YtbModuleWithLeafList.LeafIdentifier.TIME);
        List<Object> objects = new ArrayList<>();
        objects.add(1);
        objects.add(2);
        objects.add(3);
        modelObject.values(objects);

        //Builds YANG tree in YTB.
        data = new DefaultModelObjectData.Builder();
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
        setUp();

        //As an application, creates the object.
        DefaultModKey m1 = new DefaultModKey();
        m1.types(1);

        data = new DefaultModelObjectData.Builder();

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
        setUp();

        //As an application, creates the object.
        DefaultModKey m1 = new DefaultModKey();
        m1.types(1);

        DefaultModKey m2 = new DefaultModKey();
        m2.types(2);

        DefaultModKey m3 = new DefaultModKey();
        m3.types(3);

        data = new DefaultModelObjectData.Builder();

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

        setUp();

        data = new DefaultModelObjectData.Builder();

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

        setUp();

        data = new DefaultModelObjectData
                .Builder();
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
        setUp();

        data = new DefaultModelObjectData.Builder();
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
        setUp();

        // As an application, creates the object.

        //Creates container object with leaf of decimal type.
        BigDecimal dec = BigDecimal.valueOf(98989);
        DefaultSched sched = new DefaultSched();
        sched.predict(dec);

        // Builds YANG tree in YTB.
        data = new DefaultModelObjectData.Builder();
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

        setUp();
        data = new DefaultModelObjectData.Builder();
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
        setUp();
        // Creates two binary leaf -lists for two list app areas.
        List<byte[]> destArea1 = new ArrayList<>();
        byte[] arr = new byte[]{1, 6, 3};
        byte[] arr1 = new byte[]{2, 7, 4};
        destArea1.add(arr);
        destArea1.add(arr1);

        List<byte[]> destArea2 = new ArrayList<>();
        byte[] arr2 = new byte[]{3, 8, 4};
        byte[] arr3 = new byte[]{5, 6, 1};
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
        byte[] arrB = new byte[]{0, 0, 1};
        byte[] arr1B = new byte[]{1, 0, 0};
        destArea3.add(arrB);
        destArea3.add(arr1B);

        List<byte[]> destArea4 = new ArrayList<>();
        byte[] arr2B = new byte[]{7, 7, 7};
        byte[] arr3B = new byte[]{0, 1};
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

        data = new DefaultModelObjectData.Builder();
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
        assertThat(true, is(key instanceof LeafListKey));

        LeafListKey leafListKey = (LeafListKey) key;
        assertThat("AwgE", is(leafListKey.value()));

        sid = key.schemaId();
        assertThat("destination-areas", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        n = it.next();
        key = n.getKey();
        assertThat(true, is(key instanceof LeafListKey));

        leafListKey = (LeafListKey) key;
        assertThat("BQYB", is(leafListKey.value()));

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
        assertThat("BwcH", is(leafListKey.value()));

        sid = key.schemaId();
        assertThat("destination-areas", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        n = it.next();
        key = n.getKey();
        assertThat(true, is(key instanceof LeafListKey));

        leafListKey = (LeafListKey) key;
        assertThat("AAE=", is(leafListKey.value()));

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

        data = new DefaultModelObjectData.Builder();
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

        data = new DefaultModelObjectData.Builder();
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
        ut.addMockModWithInput();

        data = new DefaultModelObjectData.Builder();
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
     * input node. it will contains rpc node as well. as object is given for
     * input node so data node will be for container node.
     */
    @Test
    public void processRpcWithInputModId() {
        MoIdToRscIdTest ut = new MoIdToRscIdTest();
        ut.addMockModWithInput();

        data = new DefaultModelObjectData.Builder();
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
}
