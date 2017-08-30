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
import org.onosproject.yang.gen.v1.augmentchoice.rev20160826.augmentchoice.PrivateIp;
import org.onosproject.yang.gen.v1.augmentchoice.rev20160826.augmentchoice.contenttest.valid.AugmentedSchValid;
import org.onosproject.yang.gen.v1.augmentchoice.rev20160826.augmentchoice.contenttest.valid.DefaultAugmentedSchValid;
import org.onosproject.yang.gen.v1.augmentchoice.rev20160826.augmentchoice.contenttest.valid.augmentedschvalid.AugCaseModKey;
import org.onosproject.yang.gen.v1.augmentchoice.rev20160826.augmentchoice.contenttest.valid.augmentedschvalid.DefaultAugCaseModKey;
import org.onosproject.yang.gen.v1.augmentchoice.rev20160826.augmentchoice.contenttest.valid.augmentedschvalid.DefaultTestedCont;
import org.onosproject.yang.gen.v1.augmentchoice.rev20160826.augmentchoice.contenttest.valid.augmentedschvalid.DefaultUnlistedVal;
import org.onosproject.yang.gen.v1.augmentchoice.rev20160826.augmentchoice.contenttest.valid.augmentedschvalid.TestedCont;
import org.onosproject.yang.gen.v1.augmentchoice.rev20160826.augmentchoice.contenttest.valid.augmentedschvalid.UnlistedVal;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.BinaryTypedef;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.DefaultFirstLevel;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.UnionTypedef;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.Uri;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.DefaultContainerChoice;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.DefaultContainerGrouping;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.DefaultContainerLeaf;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.DefaultContainerLeafList;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.DefaultContainerList;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.DefaultOnlyContainer;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.containerchoice.choicecase.DefaultLeafCase;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.containerchoice.choicecase.leafcase.DefaultAugmentedLeafCase;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.containerleaf.DefaultAugmentedContainerLeaf;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.containerleaflist.LeafList2Enum;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.containerlist.DefaultListLeaf;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.containerlist.ListLeaf;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.containerlist.ListLeafKeys;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.firstlevel.containerlist.listleaf.DefaultAugmentedListLeaf;
import org.onosproject.yang.gen.v1.modeldatatoresourcedata.rev20160826.modeldatatoresourcedata.group1.DefaultGroupContainer;
import org.onosproject.yang.gen.v1.modeldatatoresourcedatainterfile.rev20160826.modeldatatoresourcedatainterfile.firstlevel.containerchoice.choicecase.augmentedschchoicecase.DefaultLeafInterAug;
import org.onosproject.yang.gen.v1.modeldatatoresourcedatainterfile.rev20160826.modeldatatoresourcedatainterfile.firstlevel.containerchoice.choicecase.leafcase.DefaultAugmentedSchLeafCase;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.DefaultModKey;
import org.onosproject.yang.gen.v1.modulelistandkeyaugment.rev20160826.modulelistandkeyaugment.modkey.DefaultAugmentedSchModKey;
import org.onosproject.yang.gen.v1.modulelistandkeyaugment.rev20160826.modulelistandkeyaugment.modkey.augmentedschmodkey.AugListModKey;
import org.onosproject.yang.gen.v1.modulelistandkeyaugment.rev20160826.modulelistandkeyaugment.modkey.augmentedschmodkey.DefaultAugListModKey;
import org.onosproject.yang.gen.v1.ytbchoicewithcontainerandleaflist.rev20160826.ytbchoicewithcontainerandleaflist.contenttest.DefaultValid;
import org.onosproject.yang.gen.v1.ytbchoicewithcontainerandleaflist.rev20160826.ytbchoicewithcontainerandleaflist.contenttest.Valid;
import org.onosproject.yang.gen.v1.ytbchoicewithcontainerandleaflist.rev20160826.ytbchoicewithcontainerandleaflist.contenttest.choicecontainer.ChoiceContainer;
import org.onosproject.yang.gen.v1.ytbchoicewithcontainerandleaflist.rev20160826.ytbchoicewithcontainerandleaflist.contenttest.choicecontainer.DefaultChoiceContainer;
import org.onosproject.yang.gen.v1.ytbchoicewithcontainerandleaflist.rev20160826.ytbchoicewithcontainerandleaflist.contenttest.choicecontainer.choicecontainer.DefaultPredict;
import org.onosproject.yang.gen.v1.ytbchoicewithcontainerandleaflist.rev20160826.ytbchoicewithcontainerandleaflist.contenttest.choicecontainer.choicecontainer.Predict;
import org.onosproject.yang.gen.v1.ytbchoicewithcontainerandleaflist.rev20160826.ytbchoicewithcontainerandleaflist.contenttest.choicecontainer.choicecontainer.predict.DefaultReproduce;
import org.onosproject.yang.gen.v1.ytbchoicewithcontainerandleaflist.rev20160826.ytbchoicewithcontainerandleaflist.contenttest.choicecontainer.choicecontainer.predict.Reproduce;
import org.onosproject.yang.gen.v1.ytbchoicewithcontainerandleaflist.rev20160826.ytbchoicewithcontainerandleaflist.currentvalue.DefaultYtbAbsent;
import org.onosproject.yang.gen.v1.ytbsimpleaugment.rev20160826.ytbsimpleaugment.DefaultCont1;
import org.onosproject.yang.gen.v1.ytbsimpleaugment.rev20160826.ytbsimpleaugment.cont1.DefaultCont2;
import org.onosproject.yang.gen.v1.ytbsimpleaugment.rev20160826.ytbsimpleaugment.cont1.cont2.DefaultAugmentedCont2;
import org.onosproject.yang.gen.v1.ytbsimpleaugment.rev20160826.ytbsimpleaugment.cont1.cont2.augmentedcont2.Cont1s;
import org.onosproject.yang.gen.v1.ytbsimpleaugment.rev20160826.ytbsimpleaugment.cont1.cont2.augmentedcont2.DefaultCont1s;
import org.onosproject.yang.gen.v1.ytbsimplechoicecase.rev20160826.ytbsimplechoicecase.DefaultYtbFood;
import org.onosproject.yang.gen.v1.ytbsimplechoicecase.rev20160826.ytbsimplechoicecase.ytbfood.ytbsnack.DefaultYtbLateNight;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultModelObjectData;
import org.onosproject.yang.model.InnerModelObject;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;

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
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;

/**
 * Unit test cases for YANG tree builder for context switch for augment, RPC
 * and case.
 */
public class DataTreeContextSwitchTest {

    private static final String CHOC = "choc";
    private static final String VAL = "val";
    private static final String IND = "ind";
    private ResourceData rscData;
    private DefaultDataTreeBuilder treeBuilder;
    private ResourceId id;
    private List<NodeKey> keys;
    private String nameSpace;
    private SchemaId sid;
    private List<DataNode> dataNodes;
    private DataNode node;
    private ModelObjectId mid;
    private DefaultModelObjectData.Builder data;

    /**
     * Do the prior setup for each UT.
     */
    private void setUp() {
        processSchemaRegistry();
        DefaultYangModelRegistry registry = registry();
        treeBuilder = new DefaultDataTreeBuilder(registry);
    }

    /**
     * Unit test case to process a simple choice inside module. Here model
     * object id is null so resource identifier will have "/". Data node list
     * will contain container node which will have nodes for once of the case.
     */
    @Test
    public void processSimpleChoiceCase() {

        setUp();

        // As an application, creates the object.

        // Creates a choice snack with the case late night.
        DefaultYtbLateNight lateNight = new DefaultYtbLateNight();
        lateNight.chocolate(CHOC);

        // Creates container food with the created case.
        DefaultYtbFood food = new DefaultYtbFood();
        food.ytbSnack(lateNight);

        // Builds YANG tree in YTB.
        data = new DefaultModelObjectData.Builder();
        data.addModelObject(food);
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:simple:choice:case";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(1, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "YtbFood", nameSpace, SINGLE_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(1, is(child.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();

        validateDataNode(n.getValue(), "chocolate", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, CHOC);
    }

    /**
     * Unit test case to process a simple choice inside module. Here model
     * object id is having container class so resource identifier will have
     * "/" and container in it. data node list will have content of case node.
     */
    @Test
    public void processSimpleChoiceCaseModIdOne() {

        setUp();

        // As an application, creates the object.

        // Creates a choice snack with the case late night.
        DefaultYtbLateNight lateNight = new DefaultYtbLateNight();
        lateNight.chocolate(CHOC);

        // Builds YANG tree in YTB.
        data = new DefaultModelObjectData.Builder();
        data.addModelObject(lateNight);
        mid = ModelObjectId.builder().addChild(DefaultYtbFood.class).build();
        data.identifier(mid);
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:simple:choice:case";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("YtbFood", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "chocolate", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, CHOC);
    }

    /**
     * Unit test case for a module containing 2 choice nodes. model object id
     * is null so resource identifier will be "/". model object will contain
     * data for case1 and case 2. container has list which does not have a
     * key. in that case while adding it to parent data node it will
     * overwrite the data for previous instance of list. resource data will
     * have 3 model objects in list. 1 for container and other 2 for leaf
     * list of second choice node.
     */
    @Test
    public void processChoiceWithNodeAndLeafList() {
        setUp();

        // As an application, creates the object.

        // Creates reproduce container for list predict-1.
        Reproduce reproduce1 = new DefaultReproduce();
        reproduce1.yangAutoPrefixCatch((short) 90);

        // Assigns predict-1 with the container.
        Predict predict1 = new DefaultPredict();
        predict1.reproduce(reproduce1);

        // Creates reproduce container for list predict-2.
        Reproduce reproduce2 = new DefaultReproduce();
        reproduce2.yangAutoPrefixCatch((short) 100);

        // Assigns predict-2 with the container.
        Predict predict2 = new DefaultPredict();
        predict2.reproduce(reproduce2);

        List<Predict> predictList = new ArrayList<>();
        predictList.add(predict1);
        predictList.add(predict2);

        // Case container is added to the choice content-test.
        ChoiceContainer containerCase = new DefaultChoiceContainer();
        containerCase.predict(predictList);
        // Case container is added to the choice content-test.
        org.onosproject.yang.gen.v1.ytbchoicewithcontainerandleaflist.rev20160826
                .ytbchoicewithcontainerandleaflist.contenttest
                .DefaultChoiceContainer contentTest =
                new org.onosproject.yang.gen.v1.ytbchoicewithcontainerandleaflist
                        .rev20160826.ytbchoicewithcontainerandleaflist.contenttest
                        .DefaultChoiceContainer();
        contentTest.choiceContainer(containerCase);

        // Creates string list for leaf-list final.
        List<String> stringList = new ArrayList<>();
        stringList.add(VAL);
        stringList.add(IND);

        // For choice current value, the leaf list gets added as case.
        DefaultYtbAbsent currentValue = new DefaultYtbAbsent();
        currentValue.yangAutoPrefixFinal(stringList);

        // Builds YANG tree in YTB.
        data = new DefaultModelObjectData.Builder();
        data.addModelObject(contentTest).addModelObject(currentValue);
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:choice:with:container:and:leaf:list";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(1, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(3, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "choice-container", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);
        //Here only one instance of list will be added because config is
        // false. so there will be no difference in 2 instance of list node.
        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(1, is(child.size()));
        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();

        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "predict", nameSpace,
                         MULTI_INSTANCE_NODE,
                         true, null);
        child = ((InnerNode) n.getValue()).childNodes();
        assertThat(1, is(child.size()));

        it = child.entrySet().iterator();
        n = it.next();
        validateDataNode(n.getValue(), "reproduce", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        child = ((InnerNode) n.getValue()).childNodes();
        assertThat(1, is(child.size()));

        it = child.entrySet().iterator();
        n = it.next();
        validateDataNode(n.getValue(), "catch", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "100");

        node = dataNodes.get(1);
        validateDataNode(node, "final", nameSpace,
                         MULTI_INSTANCE_LEAF_VALUE_NODE,
                         true, VAL);

        node = dataNodes.get(2);
        validateDataNode(node, "final", nameSpace,
                         MULTI_INSTANCE_LEAF_VALUE_NODE,
                         true, IND);
    }

    /**
     * Unit test for a module containing a container and augment. Model
     * object id is null so resource identifier will have only "/". and as we
     * are augmenting one of the container. data node for container will have
     * augment data as child nodes.
     */
    @Test
    public void processSimpleAugment() {
        setUp();
        // As an application, creates the object.

        // Creates container cont1s with the leaf.
        org.onosproject.yang.gen.v1.ytbsimpleaugment.rev20160826
                .ytbsimpleaugment.cont1.cont2.augmentedcont2.cont1s
                .Cont1s cont1s1 = new org.onosproject.yang.gen.v1.ytbsimpleaugment
                .rev20160826.ytbsimpleaugment.cont1.cont2
                .augmentedcont2.cont1s.DefaultCont1s();

        // Appends the created container into another container.
        Cont1s cont1s = new DefaultCont1s();
        cont1s.cont1s(cont1s1);

        // Creates augment with the container and leaf.
        DefaultAugmentedCont2 augment = new DefaultAugmentedCont2();
        augment.cont1s(cont1s);
        augment.leaf4(500);

        // Creates for the node which will be getting augmented.
        // Creates cont2 where content will be augmented into.
        DefaultCont2 augCont2 = new DefaultCont2();
        augCont2.addAugmentation(augment);

        // Creates cont1 where cont2 is added.
        DefaultCont1 cont1 = new DefaultCont1();
        cont1.cont2(augCont2);

        // Builds YANG tree in YTB.
        data = new DefaultModelObjectData.Builder();
        data.addModelObject(cont1);
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:simple:augment";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(1, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "cont1", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);
        //Here only one instance of list will be added because config is
        // false. so there will be no difference in 2 instance of list node.
        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(1, is(child.size()));
        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();

        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "cont2", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);
        child = ((InnerNode) n.getValue()).childNodes();
        assertThat(2, is(child.size()));

        it = child.entrySet().iterator();
        n = it.next();
        validateDataNode(n.getValue(), "leaf4", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "500");

        n = it.next();
        validateDataNode(n.getValue(), "cont1s", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        child = ((InnerNode) n.getValue()).childNodes();
        assertThat(1, is(child.size()));

        it = child.entrySet().iterator();
        n = it.next();
        validateDataNode(n.getValue(), "cont1s", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);
    }

    /**
     * Unit test for a module containing a container and augment. Model
     * object id is having container so resource identifier will have "/".
     * and container node. as we are augmenting one of the container. data node
     * for container will have augment data as child nodes.
     */
    @Test
    public void processSimpleAugmentModIdC1() {
        setUp();
        // As an application, creates the object.

        // Creates container cont1s with the leaf.
        org.onosproject.yang.gen.v1.ytbsimpleaugment.rev20160826
                .ytbsimpleaugment.cont1.cont2.augmentedcont2.cont1s
                .Cont1s cont1s1 = new org.onosproject.yang.gen.v1.ytbsimpleaugment
                .rev20160826.ytbsimpleaugment.cont1.cont2
                .augmentedcont2.cont1s.DefaultCont1s();

        // Appends the created container into another container.
        Cont1s cont1s = new DefaultCont1s();
        cont1s.cont1s(cont1s1);

        // Creates augment with the container and leaf.
        DefaultAugmentedCont2 augment = new DefaultAugmentedCont2();
        augment.cont1s(cont1s);
        augment.leaf4(500);

        // Creates for the node which will be getting augmented.
        // Creates cont2 where content will be augmented into.
        DefaultCont2 augCont2 = new DefaultCont2();
        augCont2.addAugmentation(augment);

        // Builds YANG tree in YTB.
        data = new DefaultModelObjectData.Builder();
        data.addModelObject(augCont2);
        mid = ModelObjectId.builder().addChild(DefaultCont1.class).build();
        data.identifier(mid);
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:simple:augment";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("cont1", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "cont2", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);
        //Here only one instance of list will be added because config is
        // false. so there will be no difference in 2 instance of list node.
        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(2, is(child.size()));
        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();

        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "leaf4", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "500");

        n = it.next();
        validateDataNode(n.getValue(), "cont1s", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        child = ((InnerNode) n.getValue()).childNodes();
        assertThat(1, is(child.size()));

        it = child.entrySet().iterator();
        n = it.next();
        validateDataNode(n.getValue(), "cont1s", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);
    }

    /**
     * Unit test for a module containing a container and augment. Model
     * object id is having container so resource identifier will have "/".
     * and container node. as we are augmenting one of the container. data node
     * for container will have augment data as child nodes.
     */
    @Test
    public void processSimpleAugmentModIdC2() {
        setUp();
        // As an application, creates the object.

        // Creates container cont1s with the leaf.
        org.onosproject.yang.gen.v1.ytbsimpleaugment.rev20160826
                .ytbsimpleaugment.cont1.cont2.augmentedcont2.cont1s
                .Cont1s cont1s1 = new org.onosproject.yang.gen.v1.ytbsimpleaugment
                .rev20160826.ytbsimpleaugment.cont1.cont2
                .augmentedcont2.cont1s.DefaultCont1s();

        // Appends the created container into another container.
        Cont1s cont1s = new DefaultCont1s();
        cont1s.cont1s(cont1s1);

        // Creates augment with the container and leaf.
        DefaultAugmentedCont2 augment = new DefaultAugmentedCont2();
        augment.cont1s(cont1s);
        augment.leaf4(500);

        // Builds YANG tree in YTB.
        data = new DefaultModelObjectData.Builder();
        data.addModelObject(augment);
        mid = ModelObjectId.builder().addChild(DefaultCont1.class)
                .addChild(DefaultCont2.class).build();

        data.identifier(mid);
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:simple:augment";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(3, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("cont1", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = keys.get(2).schemaId();
        assertThat("cont2", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(2, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "leaf4", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "500");

        node = dataNodes.get(1);
        validateDataNode(node, "cont1s", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);
    }

    /**
     * Unit test for a module containing a container and augment. Model
     * object id is having container which added by augment so resource
     * identifier will have "/".
     * and container node. list of data nodes will contain the augmented
     * container's child node.
     */
    @Test
    public void processSimpleAugmentModIdAug() {
        setUp();
        // As an application, creates the object.

        // Creates container cont1s with the leaf.
        org.onosproject.yang.gen.v1.ytbsimpleaugment.rev20160826
                .ytbsimpleaugment.cont1.cont2.augmentedcont2.cont1s
                .DefaultCont1s cont1s1 = new org.onosproject.yang.gen.v1.ytbsimpleaugment
                .rev20160826.ytbsimpleaugment.cont1.cont2
                .augmentedcont2.cont1s.DefaultCont1s();

        // Builds YANG tree in YTB.
        data = new DefaultModelObjectData.Builder();
        data.addModelObject(cont1s1);
        mid = ModelObjectId.builder().addChild(DefaultCont1.class)
                .addChild(DefaultCont2.class)
                .addChild(DefaultCont1s.class).build();

        data.identifier(mid);
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:simple:augment";
        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(4, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("cont1", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = keys.get(2).schemaId();
        assertThat("cont2", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = keys.get(3).schemaId();
        assertThat("cont1s", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "cont1s", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);
    }


    /**
     * Unit test for inter file augment. Model object is null so resource id
     * will have "/" and data nodes will contain augment data as well.
     */
    @Test
    public void processInterFileAugmentList() {
        setUp();

        // As an application, creates the object.

        DefaultModKey modKey = new DefaultModKey();
        modKey.types(20);

        //add augment.
        DefaultAugmentedSchModKey aug = new DefaultAugmentedSchModKey();
        DefaultAugListModKey modAug = new DefaultAugListModKey();
        modAug.types(10);
        List<AugListModKey> modKeys = new ArrayList<>();
        modKeys.add(modAug);
        aug.augListModKey(modKeys);

        modKey.addAugmentation(aug);

        data = new DefaultModelObjectData.Builder();
        data.addModelObject(modKey);

        // Builds YANG tree in YTB.
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
        validateDataNode(node, "modKey", nameSpace,
                         MULTI_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(2, is(child.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();

        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "types", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "20");
        n = it.next();
        validateDataNode(n.getValue(), "aug-list-modKey", nameSpace,
                         MULTI_INSTANCE_NODE,
                         true, null);
    }

    /**
     * Basic unit test for second level nodes. in this ut we are testing for
     * a normal container in container case. model object id contain the
     * first level container class so resource id will contain it and
     * resource data will contain object of the second container.
     */
    @Test
    public void moduleWithModIdFirstLvlOne() {
        setUp();

        DefaultOnlyContainer con = new DefaultOnlyContainer();

        data = new DefaultModelObjectData.Builder();
        data.addModelObject(con);
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultFirstLevel.class).build());

        // Builds YANG tree in YTB.
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yrt:model:converter:model:data:to:resource:data";

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("first-level", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "only-container", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(0, is(child.size()));
    }

    /**
     * Basic unit test for second level nodes. in this ut we are testing for
     * a normal container in container case. model object id contain the
     * first level  and container class so resource id will contain it and
     * resource data will empty list
     */
    @Test
    public void moduleWithModIdFirstLvlOneL2() {
        setUp();

        data = new DefaultModelObjectData.Builder();
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultFirstLevel.class)
                                .addChild(DefaultOnlyContainer.class).build());

        // Builds YANG tree in YTB.
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yrt:model:converter:model:data:to:resource:data";

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(3, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("first-level", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = keys.get(2).schemaId();
        assertThat("only-container", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(0, is(dataNodes.size()));
    }


    /**
     * Basic unit test for second level nodes. in this ut we are testing for
     * a normal container in container case. model object id contain the
     * first level container class so resource id will contain it and
     * resource data will contain object of the second container. we have
     * augmented leaf added to it so those should also be part of data nodes.
     */
    @Test
    public void moduleWithModIdFirstLvlTwo() {
        setUp();

        DefaultContainerLeaf con = new DefaultContainerLeaf();
        con.leaf2("leaf2");

        DefaultAugmentedContainerLeaf aug = new DefaultAugmentedContainerLeaf();
        aug.leafAug(true);
        con.addAugmentation(aug);

        data = new DefaultModelObjectData.Builder();
        data.addModelObject(con);
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultFirstLevel.class).build());

        // Builds YANG tree in YTB.
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yrt:model:converter:model:data:to:resource:data";

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("first-level", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "container-leaf", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(2, is(child.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();

        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "leaf2", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "leaf2");
        n = it.next();
        validateDataNode(n.getValue(), "leaf-aug", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, null);
    }

    /**
     * Basic unit test for second level nodes. in this ut we are testing for
     * a normal container in container case. model object id contain the
     * first level container class so resource id will contain it and
     * resource data will contain object of the second container. each leaf
     * list instance should have its data node.
     */
    @Test
    public void moduleWithModIdFirstLvlThree() {
        setUp();

        List<LeafList2Enum> list = new ArrayList<>();
        list.add(LeafList2Enum.HUNDRED);
        list.add(LeafList2Enum.TEN);
        list.add(LeafList2Enum.THOUSAND);

        DefaultContainerLeafList con = new DefaultContainerLeafList();
        con.leafList2(list);

        data = new DefaultModelObjectData.Builder();
        data.addModelObject(con);
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultFirstLevel.class).build());

        // Builds YANG tree in YTB.
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yrt:model:converter:model:data:to:resource:data";

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("first-level", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "container-leaf-list", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(3, is(child.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();

        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "leaf-list2", nameSpace,
                         MULTI_INSTANCE_LEAF_VALUE_NODE,
                         true, "hundred");
        n = it.next();
        validateDataNode(n.getValue(), "leaf-list2", nameSpace,
                         MULTI_INSTANCE_LEAF_VALUE_NODE,
                         true, "ten");
        n = it.next();
        validateDataNode(n.getValue(), "leaf-list2", nameSpace,
                         MULTI_INSTANCE_LEAF_VALUE_NODE,
                         true, "thousand");
    }

    /**
     * Basic unit test for second level nodes. in this ut we are testing for
     * a normal container in container case. model object id contain the
     * first level container class so resource id will contain it and
     * resource data will contain object of the second list. each
     * list instance should have its data node.
     */
    @Test
    public void moduleWithModIdFirstLvlFour() {
        setUp();

        List<ListLeaf> list = new ArrayList<>();
        DefaultListLeaf listLeaf = new DefaultListLeaf();
        DefaultAugmentedListLeaf aug = new DefaultAugmentedListLeaf();
        aug.leafAug(UnionTypedef.fromString("12"));
        listLeaf.addAugmentation(aug);

        listLeaf.name("first");
        list.add(listLeaf);

        listLeaf = new DefaultListLeaf();
        aug = new DefaultAugmentedListLeaf();
        aug.leafAug(UnionTypedef.fromString("14"));
        listLeaf.addAugmentation(aug);

        listLeaf.name("second");
        list.add(listLeaf);

        DefaultContainerList con = new DefaultContainerList();
        con.listLeaf(list);

        data = new DefaultModelObjectData.Builder();
        data.addModelObject(con);
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultFirstLevel.class).build());

        // Builds YANG tree in YTB.
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yrt:model:converter:model:data:to:resource:data";

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("first-level", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "container-list", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(2, is(child.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();

        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "list-leaf", nameSpace,
                         MULTI_INSTANCE_NODE,
                         true, null);
        child = ((InnerNode) n.getValue()).childNodes();
        assertThat(2, is(child.size()));

        it = child.entrySet().iterator();
        n = it.next();
        validateDataNode(n.getValue(), "name", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "first");
        n = it.next();
        validateDataNode(n.getValue(), "leaf-aug", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "12");

        child = ((InnerNode) node).childNodes();
        assertThat(2, is(child.size()));

        it = child.entrySet().iterator();

        n = it.next();
        //second child
        n = it.next();
        validateDataNode(n.getValue(), "list-leaf", nameSpace,
                         MULTI_INSTANCE_NODE,
                         true, null);
        child = ((InnerNode) n.getValue()).childNodes();
        assertThat(2, is(child.size()));

        it = child.entrySet().iterator();
        n = it.next();
        validateDataNode(n.getValue(), "name", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "second");
        n = it.next();
        validateDataNode(n.getValue(), "leaf-aug", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "14");
    }

    /**
     * Basic unit test for second level nodes. in this ut we are testing for
     * a normal container in container case. model object id contain the
     * first level and container ,list class so resource id will contain it
     * and resource data will contain leafs.
     */
    @Test
    public void moduleWithModIdFirstLvlFourL2() {
        setUp();

        data = new DefaultModelObjectData.Builder();
        List<ListLeaf> list = new ArrayList<>();
        DefaultListLeaf listLeaf = new DefaultListLeaf();
        DefaultAugmentedListLeaf aug = new DefaultAugmentedListLeaf();
        aug.leafAug(UnionTypedef.fromString("12"));
        listLeaf.addAugmentation(aug);

        listLeaf.name("first");
        list.add(listLeaf);

        data.addModelObject(listLeaf);
        listLeaf = new DefaultListLeaf();
        aug = new DefaultAugmentedListLeaf();
        aug.leafAug(UnionTypedef.fromString("14"));
        listLeaf.addAugmentation(aug);

        listLeaf.name("second");
        list.add(listLeaf);

        data.addModelObject(listLeaf);
        DefaultContainerList con = new DefaultContainerList();
        con.listLeaf(list);

        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultFirstLevel.class)
                                .addChild(DefaultContainerList.class).build());

        // Builds YANG tree in YTB.
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yrt:model:converter:model:data:to:resource:data";

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(3, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("first-level", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = keys.get(2).schemaId();
        assertThat("container-list", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(2, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "list-leaf", nameSpace,
                         MULTI_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(2, is(child.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();

        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "name", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "first");
        n = it.next();
        validateDataNode(n.getValue(), "leaf-aug", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "12");

        node = dataNodes.get(1);
        validateDataNode(node, "list-leaf", nameSpace,
                         MULTI_INSTANCE_NODE,
                         true, null);
        child = ((InnerNode) node).childNodes();
        assertThat(2, is(child.size()));

        it = child.entrySet().iterator();
        n = it.next();
        validateDataNode(n.getValue(), "name", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "second");
        n = it.next();
        validateDataNode(n.getValue(), "leaf-aug", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "14");
    }

    /**
     * Basic unit test for second level nodes. in this ut we are testing for
     * a normal container in container case. model object id contain the
     * first level and container ,list class so resource id will contain it
     * and resource data will contain leafs.
     */
    @Test
    public void moduleWithModIdFirstLvlFourL3() {
        setUp();

        data = new DefaultModelObjectData.Builder();
        ListLeafKeys key = new ListLeafKeys();
        key.name("key leaf");
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultFirstLevel.class)
                                .addChild(DefaultContainerList.class)
                                .addChild(DefaultListLeaf.class, key).build());

        // Builds YANG tree in YTB.
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yrt:model:converter:model:data:to:resource:data";

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(4, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("first-level", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = keys.get(2).schemaId();
        assertThat("container-list", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        sid = keys.get(3).schemaId();
        assertThat("list-leaf", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));
        NodeKey nodeKey = keys.get(3);
        assertThat(true, is(nodeKey instanceof ListKey));

        ListKey listKey = (ListKey) nodeKey;
        List<KeyLeaf> keyLeaves = listKey.keyLeafs();

        assertThat(1, is(keyLeaves.size()));

        KeyLeaf leaf = keyLeaves.get(0);
        sid = leaf.leafSchema();
        assertThat("name", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        Object val = leaf.leafValAsString();
        assertThat(true, is(val.equals("key leaf")));

        dataNodes = rscData.dataNodes();
        assertThat(0, is(dataNodes.size()));
    }

    /**
     * Basic unit test for second level nodes. in this ut we are testing for
     * a normal container in container case. model object id contain the
     * first level container class so resource id will contain it and
     * resource data will contain object of the case's nodes contains as data
     * nodes. augmented info also should be present.
     */
    @Test
    public void moduleWithModIdFirstLvlFive() {
        setUp();

        DefaultLeafCase c1 = new DefaultLeafCase();
        c1.leaf3(120);

        DefaultAugmentedLeafCase aug1 = new DefaultAugmentedLeafCase();
        aug1.leafAug(BinaryTypedef.fromString("MTAxMQ=="));

        c1.addAugmentation(aug1);

        DefaultAugmentedSchLeafCase aug2 = new DefaultAugmentedSchLeafCase();
        aug2.leafInterAug(BigDecimal.valueOf(21102));

        c1.addAugmentation(aug2);

        DefaultContainerChoice con = new DefaultContainerChoice();
        con.choiceCase(c1);

        data = new DefaultModelObjectData.Builder();
        data.addModelObject(con);
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultFirstLevel.class).build());

        // Builds YANG tree in YTB.
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yrt:model:converter:model:data:to:resource:data";

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("first-level", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "container-choice", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(3, is(child.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();

        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "leaf3", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "120");
        n = it.next();
        validateDataNode(n.getValue(), "leaf-aug", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "MTAxMQ==");
        n = it.next();
        validateDataNode(n.getValue(), "leaf-inter-aug", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "21102");
    }

    /**
     * Basic unit test for second level nodes. in this ut we are testing for
     * a normal container in container case. model object id contain the
     * first level container class so resource id will contain it and
     * resource data will contain object of the augmented case's nodes.
     * augmented case will be added as child object to the parent class of
     * choice node.
     */
    @Test
    public void moduleWithModIdFirstLvlSix() {
        setUp();

        DefaultLeafInterAug c1 = new DefaultLeafInterAug();
        c1.leafInterAug(Uri.fromString("namespace"));

        DefaultContainerChoice con = new DefaultContainerChoice();
        con.choiceCase(c1);

        data = new DefaultModelObjectData.Builder();
        data.addModelObject(con);
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultFirstLevel.class).build());

        // Builds YANG tree in YTB.
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yrt:model:converter:model:data:to:resource:data";

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("first-level", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "container-choice", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(1, is(child.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();

        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "leaf-inter-aug", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "namespace");
    }


    /**
     * Basic unit test for second level nodes. in this ut we are testing for
     * a normal container in container case. model object id contain the
     * first level container class so resource id will contain it and
     * resource data will contain object of the cloned container node.
     */
    @Test
    public void moduleWithModIdFirstLvlSeven() {
        setUp();

        DefaultGroupContainer gp = new DefaultGroupContainer();
        gp.groupLeaf(Uri.fromString("namespace"));

        DefaultContainerGrouping con = new DefaultContainerGrouping();
        con.groupContainer(gp);

        data = new DefaultModelObjectData.Builder();
        data.addModelObject(con);
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultFirstLevel.class).build());

        // Builds YANG tree in YTB.
        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yrt:model:converter:model:data:to:resource:data";

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("first-level", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "container-grouping", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(1, is(child.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();

        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "group-container", nameSpace,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        child = ((InnerNode) n.getValue()).childNodes();
        assertThat(1, is(child.size()));

        it = child.entrySet().iterator();

        n = it.next();
        validateDataNode(n.getValue(), "group-leaf", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         true, "namespace");
    }

    /**
     * Unit test case for multi augments with the same augment name.
     */
    @Test
    public void processMultiAugWithSameName() {
        setUp();

        AugmentedSchValid valid = new DefaultAugmentedSchValid();
        valid.chTest(PrivateIp.class);
        byte b1 = 8;
        byte b2 = 110;
        valid.addToTest(b1);
        valid.addToTest(b2);
        TestedCont cont = new DefaultTestedCont();
        cont.presence("true");
        valid.testedCont(cont);
        UnlistedVal val = new DefaultUnlistedVal();
        val.presence("false");
        valid.addToUnlistedVal(val);
        AugCaseModKey modKey = new DefaultAugCaseModKey();
        modKey.types(12);
        valid.addToAugCaseModKey(modKey);
        Valid valid1 = new DefaultValid();
        valid1.addAugmentation((InnerModelObject) valid);

        data = new DefaultModelObjectData.Builder();
        data.addModelObject((InnerModelObject) valid1);

        rscData = treeBuilder.getResourceData(data.build());

        nameSpace = "yms:test:ytb:choice:with:container:and:leaf:list";

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(1, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(6, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "ch-test", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "private-ip");

        node = dataNodes.get(1);
        validateDataNode(node, "test", nameSpace,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "8");

        node = dataNodes.get(2);
        validateDataNode(node, "test", nameSpace,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, true, "110");

        node = dataNodes.get(3);
        validateDataNode(node, "unlisted-val", nameSpace, MULTI_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
        assertThat(1, is(child.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "presence", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "false");

        node = dataNodes.get(4);
        validateDataNode(node, "aug-case-modKey", nameSpace,
                         MULTI_INSTANCE_NODE, true, null);

        child = ((InnerNode) node).childNodes();
        assertThat(1, is(child.size()));

        it = child.entrySet().iterator();
        n = it.next();
        validateDataNode(n.getValue(), "types", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "12");

        node = dataNodes.get(5);
        validateDataNode(node, "tested-cont", nameSpace, SINGLE_INSTANCE_NODE,
                         true, null);

        child = ((InnerNode) node).childNodes();
        assertThat(1, is(child.size()));

        it = child.entrySet().iterator();
        n = it.next();
        validateDataNode(n.getValue(), "presence", nameSpace,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "true");
    }

    /**
     * Unit test for inter file augment. Model object is null so resource id
     * will have "/" and data nodes will contain augment data as well.
     */
    @Test
    public void processInterFileAugmentChoice() {

        //FIXME: when get filters are done
//        setUp();
//
//        AugChoiceModKey augChoiceModKey =
//                new org.onosproject.yang.gen.v1.yms.test.ytb.choice.with
//                        .container.and.leaf.yangautoprefixlist
//                        .rev20160826.augmentchoice.contenttest
//                        .augmentedschcontenttest.augchoicemodkey
//                        .DefaultAugChoiceModKey();
//        augChoiceModKey.types(30);
//        List<AugChoiceModKey> list = new ArrayList<>();
//        list.add(augChoiceModKey);
//
//        DefaultAugChoiceModKey aug1 = new DefaultAugChoiceModKey();
//        aug1.augChoiceModKey(list);
//
//        data = new DefaultModelObjectData.Builder();
//        data.addModelObject(aug1);
//
//        // Builds YANG tree in YTB.
//        rscData = treeBuilder.getResourceData(data.build());
//
//        nameSpace = "yms:test:ytb:choice:with:container:and:leaf:list";
//
//        id = rscData.resourceId();
//        keys = id.nodeKeys();
//        assertThat(1, is(keys.size()));
//
//        sid = keys.get(0).schemaId();
//        assertThat("/", is(sid.name()));
//        assertThat(null, is(sid.namespace()));
//
//        dataNodes = rscData.dataNodes();
//        assertThat(1, is(dataNodes.size()));
//
//        node = dataNodes.get(0);
//        validateDataNode(node, "aug-choice-modKey", nameSpace,
//                         MULTI_INSTANCE_NODE,
//                         true, null);
//
//        Map<NodeKey, DataNode> child = ((InnerNode) node).childNodes();
//        assertThat(1, is(child.size()));
//
//        Iterator<Map.Entry<NodeKey, DataNode>> it = child.entrySet().iterator();
//
//        Map.Entry<NodeKey, DataNode> n = it.next();
//        validateDataNode(n.getValue(), "types", nameSpace,
//                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
//                         true, "30");
    }

//TODO: check this in St test case.
//    /**
//     * Processes inter file augment with augmented node as list and the
//     * augment having list.
//     */
//    // @Test
//    public void processInterFileAugment() {
//        setUp();
//
//        // As an application, creates the object.
//        YrtIetfNetworkOpParam opParam = createObjectForInterFileAugment();
//        data = new DefaultModelObjectData.Builder();
//        data.addModelObject(opParam);
//
//        // Builds YANG tree in YTB.
//        rscData = treeBuilder.getResourceData(data.build());
//
//        nameSpace = "yms:test:ytb:simple:augment";
//        id = rscData.resourceId();
//        keys = id.nodeKeys();
//        assertThat(1, is(keys.size()));
//
//        sid = keys.get(0).schemaId();
//        assertThat("/", is(sid.name()));
//        assertThat(null, is(sid.namespace()));
//
//        dataNodes = rscData.dataNodes();
//        assertThat(1, is(dataNodes.size()));
//
//        node = dataNodes.get(0);
//        validateDataNode(node, "networks", nameSpace,
//                         SINGLE_INSTANCE_NODE,
//                         true, null);
//    }
//
//    /**
//     * Creates an application object for inter file augment.
//     *
//     * @return application object
//     */
//    private YrtIetfNetworkOpParam createObjectForInterFileAugment() {
//
//        // Creates leaf value for network-ref.
//        Uri nwkRef = new Uri(AUG_NW_REF_1);
//        NetworkId nwIdUri = new NetworkId(nwkRef);
//        Uri nwkRef2 = new Uri("network-ref-aug2");
//        NetworkId nwIdUri2 = new NetworkId(nwkRef2);
//
//        // Creates leaf value for node-ref
//        Uri nodeRef = new Uri(AUG_NODE_REF_1);
//        NodeId nodeId = new NodeId(nodeRef);
//
//        Uri nodeRef2 = new Uri("node-ref-aug2");
//        NodeId nodeId2 = new NodeId(nodeRef2);
//
//        // Creates support termination list with the above two contents.
//        SupportingTerminationPoint point1 =
//                new DefaultSupportingTerminationPoint();
//        point1.networkRef(nwIdUri);
//        point1.nodeRef(nodeId);
//        point1.tpRef(AUG_TP_REF_1);
//
//        SupportingTerminationPoint point2 =
//                new DefaultSupportingTerminationPoint();
//        point2.networkRef(nwIdUri2);
//        point2.nodeRef(nodeId2);
//        point2.tpRef("tp-ref-aug-2");
//
//        List<SupportingTerminationPoint> pointList = new ArrayList<>();
//        pointList.add(point1);
//        pointList.add(point2);
//
//        // Adds the list created to the termination point content1.
//        TerminationPoint tPoint1 = new DefaultTerminationPoint();
//        tPoint1.supportingTerminationPoint(pointList);
//        tPoint1.tpId(AUG_TP_ID_1);
//
//        // Creates leaf value for network-ref.
//        Uri nwkRef3 = new Uri(AUG_NW_REF_B1);
//        NetworkId nwIdUri3 = new NetworkId(nwkRef3);
//        Uri nwkRef4 = new Uri("network-ref-augb2");
//        NetworkId nwIdUri4 = new NetworkId(nwkRef4);
//
//        // Creates leaf value for node-ref
//        Uri nodeRef3 = new Uri(AUG_NODE_REF_B1);
//        NodeId nodeId3 = new NodeId(nodeRef3);
//
//        Uri nodeRef4 = new Uri("node-ref-augb2");
//        NodeId nodeId4 = new NodeId(nodeRef4);
//
//        // Creates support termination list with the above two contents.
//        SupportingTerminationPoint point3 =
//                new DefaultSupportingTerminationPoint();
//        point3.networkRef(nwIdUri3);
//        point3.nodeRef(nodeId3);
//        point3.tpRef(AUG_TP_REF_B1);
//        SupportingTerminationPoint point4 =
//                new DefaultSupportingTerminationPoint();
//        point4.networkRef(nwIdUri4);
//        point4.nodeRef(nodeId4);
//        point4.tpRef("tp-ref-aug-b2");
//
//        List<SupportingTerminationPoint> pointList2 = new ArrayList<>();
//        pointList2.add(point3);
//        pointList2.add(point4);
//
//        // Adds the list created to the termination point content2.
//        TerminationPoint tPoint2 = new DefaultTerminationPoint();
//        tPoint2.supportingTerminationPoint(pointList2);
//        tPoint2.tpId(AUG_TP_ID_B1);
//
//        List<TerminationPoint> terminationPointList = new ArrayList<>();
//        terminationPointList.add(tPoint1);
//        terminationPointList.add(tPoint2);
//
//        // Adds all the above contents to the augment.
//        DefaultAugmentedNdNode augment = new DefaultAugmentedNdNode();
//        augment.terminationPoint(terminationPointList);
//
//        // Creates leaf value for network-ref in augmented node(ietf-network).
//        Uri nwRef5 = new Uri(NW_REF);
//        NetworkId nwIdUri5 = new NetworkId(nwRef5);
//
//        //Creates leaf value for node-ref in augmented node(ietf-network).
//        Uri nodeRef5 = new Uri(NODE_REF);
//        NodeId nodeId5 = new NodeId(nodeRef5);
//
//        // Creates supporting node list content 1 with above contents.
//        SupportingNode supNode1 = new DefaultSupportingNode();
//        supNode1.nodeRef(nodeId5);
//        supNode1.networkRef(nwIdUri5);
//
//        // Creates leaf value for network-ref in augmented node(ietf-network).
//        Uri nwRef6 = new Uri(NW_REF_2);
//        NetworkId nwIdUri6 = new NetworkId(nwRef6);
//
//        //Creates leaf value for node-ref in augmented node(ietf-network).
//        Uri nodeRef6 = new Uri("node-ref2");
//        NodeId nodeId6 = new NodeId(nodeRef6);
//
//        // Creates supporting node list content 2 with above contents.
//        SupportingNode supNode2 = new DefaultSupportingNode();
//        supNode1.nodeRef(nodeId6);
//        supNode1.networkRef(nwIdUri6);
//
//        List<SupportingNode> supNodeList = new ArrayList<>();
//        supNodeList.add(supNode1);
//        supNodeList.add(supNode2);
//
//        // Creates leaf value for node-id in augmented node(ietf-network).
//        Uri nodeId1 = new Uri(NODE_REF_3);
//        NodeId nodeIdForId = new NodeId(nodeId1);
//
//        // Creates node list with content 1 by adding augment also.
//        DefaultNode node1 = new DefaultNode();
//        node1.addAugmentation(augment);
//        node1.supportingNode(supNodeList);
//        node1.nodeId(nodeIdForId);
//
//        // Creates an augment node without any values set to it.
//        DefaultAugmentedNdNode augmentedNdNode2 = new DefaultAugmentedNdNode();
//
//        // Creates leaf value for network-ref in augmented node(ietf-network).
//        Uri nwRef7 = new Uri(NW_REF_B);
//        NetworkId nwIdUri7 = new NetworkId(nwRef7);
//        //Creates leaf value for node-ref in augmented node(ietf-network).
//        Uri nodeRef7 = new Uri(NODE_REF_B);
//        NodeId nodeId7 = new NodeId(nodeRef7);
//
//        // Creates supporting node list content 1 with above contents.
//        SupportingNode supNode3 = new DefaultSupportingNode();
//        supNode3.nodeRef(nodeId7);
//        supNode3.networkRef(nwIdUri7);
//
//        // Creates leaf value for network-ref in augmented node(ietf-network).
//        Uri nwRef8 = new Uri(NW_REF_2B);
//        NetworkId nwIdUri8 = new NetworkId(nwRef8);
//
//        //Creates leaf value for node-ref in augmented node(ietf-network).
//        Uri nodeRef8 = new Uri(NODE_REF_2B);
//        NodeId nodeId8 = new NodeId(nodeRef8);
//
//        // Creates supporting node list content 1 with above contents.
//        SupportingNode supNode4 = new DefaultSupportingNode();
//        supNode3.nodeRef(nodeId8);
//        supNode3.networkRef(nwIdUri8);
//
//        List<SupportingNode> supNodeList2 = new ArrayList<>();
//        supNodeList2.add(supNode3);
//        supNodeList2.add(supNode4);
//
//        // Creates leaf value for node-id in augmented node(ietf-network).
//        Uri nodeIdLeaf = new Uri(NODE_REF_3B);
//        NodeId nodeIdForId2 = new NodeId(nodeIdLeaf);
//
//        // Creates node list with content 2 by adding empty augment also.
//        DefaultNode node2 = new DefaultNode();
//        node2.addAugmentation(augmentedNdNode2);
//        node2.supportingNode(supNodeList2);
//        node2.nodeId(nodeIdForId2);
//
//        // Adds both nodes into the list.
//        List<Node> nodeList = new LinkedList<>();
//        nodeList.add(node1);
//        nodeList.add(node2);
//
//        // Adds the list into the network list.
//        DefaultNetwork nwkList = new DefaultNetwork();
//        nwkList.node(nodeList);
//
//        List<Network> networkList = new ArrayList<>();
//        networkList.add(nwkList);
//
//        // Adds the network list into networks container.
//        Networks contNetworks = new DefaultNetworks();
//        contNetworks.network(networkList);
//
//        // Adds the container into the module.
//        YrtIetfNetworkOpParam opParam = new YrtIetfNetworkOpParam();
//        opParam.networks(contNetworks);
//        return opParam;
//    }
}
