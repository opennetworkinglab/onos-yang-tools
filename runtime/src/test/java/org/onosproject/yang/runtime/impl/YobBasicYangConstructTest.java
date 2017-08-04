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
import org.onosproject.yang.gen.v1.sample.sample.DefaultL1;
import org.onosproject.yang.gen.v1.sample.sample.DefaultTop;
import org.onosproject.yang.gen.v1.sample.sample.l1.C1;
import org.onosproject.yang.gen.v1.sample.sample.top.DefaultYangAutoPrefixInterface;
import org.onosproject.yang.gen.v1.sample.sample.top.YangAutoPrefixInterface;
import org.onosproject.yang.gen.v1.sample.sample.top.yangautoprefixinterface.Address;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.LeafModelObject;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.onosproject.yang.gen.v1.sample.Sample.LeafIdentifier.LEAF5;
import static org.onosproject.yang.gen.v1.sample.Sample.LeafIdentifier.LEAF6;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.addToResourceId;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeResourceId;

/**
 * Tests the YANG object building for the YANG data nodes.
 */
public class YobBasicYangConstructTest {
    private static final String NAME_SPACE = "samplenamespace";
    TestYangSerializerContext context = new TestYangSerializerContext();
    private DataNode.Builder dBlr;
    private String value;
    private ResourceId.Builder rIdBlr;

    private DataNode buildDataNode() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "top", NAME_SPACE, value, null);
        value = "100";
        dBlr = addDataNode(dBlr, "mtu", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = "1123";
        dBlr = addDataNode(dBlr, "color", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "interface", null, value, null);
        value = "name";
        dBlr = addDataNode(dBlr, "name", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "address", null, value, null);
        value = "name";
        dBlr = addDataNode(dBlr, "name", null, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // exit address
        dBlr = exitDataNode(dBlr); // exit interface
        dBlr = exitDataNode(dBlr); // exit top

        value = null;
        dBlr = addDataNode(dBlr, "l1", NAME_SPACE, value, null);
        dBlr = addDataNode(dBlr, "c1", NAME_SPACE, value, null);
        value = "leaf1_value";
        dBlr = addDataNode(dBlr, "leaf1", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = "leaf2_value";
        dBlr = addDataNode(dBlr, "leaf2", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // exit container c1
        value = "leaf3_value";
        dBlr = addDataNode(dBlr, "leaf3", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = "leaf4_value";
        dBlr = addDataNode(dBlr, "leaf4", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // exit list l1
        return dBlr.build();
    }

    private DataNode buildDnForModuleLevelLeaf() {
        dBlr = initializeDataNode(context);
        value = "abc";
        dBlr = addDataNode(dBlr, "leaf5", NAME_SPACE, value, null);
        return dBlr.build();
    }

    private DataNode buildDnForModuleLevelLeafList() {
        dBlr = initializeDataNode(context);
        value = "def";
        dBlr = addDataNode(dBlr, "leaf6", NAME_SPACE, value, null);
        return dBlr.build();
    }

    private DataNode buildDataNodeWithResourceIdForL1() {
        ResourceId.Builder builder = buildResourceId();
        dBlr = initializeDataNode(builder);
        dBlr = addDataNode(dBlr, "c1", NAME_SPACE, value, null);
        value = "leaf1_value";
        dBlr = addDataNode(dBlr, "leaf1", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = "leaf2_value";
        dBlr = addDataNode(dBlr, "leaf2", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        return dBlr.build();
    }

    private ResourceId.Builder buildResourceIdForContainerTop() {
        rIdBlr = initializeResourceId(context);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "top", NAME_SPACE, value);
        return rIdBlr;
    }

    private DataNode buildDataNodeWithResourceIdForLeafL3() {
        ResourceId.Builder builder = buildResourceId();
        dBlr = initializeDataNode(builder);
        value = "leaf3_value";
        dBlr = addDataNode(dBlr, "leaf3", NAME_SPACE, value, null);
        return dBlr.build();
    }

    private ResourceId.Builder buildResourceId() {
        rIdBlr = initializeResourceId(context);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "l1", NAME_SPACE, value);
        return rIdBlr;
    }

    @Test
    public void testBasicYangConstruct() throws IOException {
        DataNode dataNode = buildDataNode();
        ResourceData data = DefaultResourceData.builder().addDataNode(dataNode).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultTop top = ((DefaultTop) modelObject);
        assertThat(top.mtu(), is("100"));
        assertThat(top.color().get(0), is("1123"));

        List<YangAutoPrefixInterface> intfList = top.yangAutoPrefixInterface();
        YangAutoPrefixInterface intf = intfList.get(0);
        assertThat(intf.name(), is("name"));

        Address address = intf.address();
        assertThat(address.name(), is("name"));

        modelObject = modelObjectList.get(1);
        DefaultL1 l1 = ((DefaultL1) modelObject);
        C1 c1 = l1.c1();
        assertThat(c1.leaf1(), is("leaf1_value"));
        assertThat(c1.leaf2().get(0), is("leaf2_value"));
        assertThat(l1.leaf3(), is("leaf3_value"));
        assertThat(l1.leaf4().get(0), is("leaf4_value"));
    }

    @Test
    public void testModuleLevelLeaf() throws IOException {
        DataNode dataNode = buildDnForModuleLevelLeaf();
        ResourceData data = DefaultResourceData.builder().addDataNode(dataNode)
                .build();

        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        LeafModelObject obj = ((LeafModelObject) modelObject);
        assertThat(obj.leafIdentifier(), is(LEAF5));
        assertThat(obj.values().get(0), is("abc"));
    }

    @Test
    public void testModuleLevelLeafList() throws IOException {
        DataNode dataNode = buildDnForModuleLevelLeafList();
        ResourceData data = DefaultResourceData.builder().addDataNode(dataNode)
                .build();

        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        LeafModelObject obj = ((LeafModelObject) modelObject);
        assertThat(obj.leafIdentifier(), is(LEAF6));
        assertThat(obj.values().get(0), is("def"));
    }

    @Test
    public void testWithResourceId() {
        DataNode dataNode = buildDataNodeWithResourceIdForL1();
        ResourceId.Builder rIdbuilder = buildResourceId();
        ResourceData data = DefaultResourceData.builder().addDataNode(dataNode)
                .addDataNode(buildDataNodeWithResourceIdForLeafL3())
                .resourceId(rIdbuilder.build()).build();

        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        C1 c1 = ((C1) modelObject);
        assertThat(c1.leaf1(), is("leaf1_value"));
        assertThat(c1.leaf2().get(0), is("leaf2_value"));
    }

    @Test
    public void testWithRIdForContainerTop() {
        ResourceId.Builder builder = buildResourceIdForContainerTop();
        dBlr = initializeDataNode(builder);
        value = "mtu";
        dBlr = addDataNode(dBlr, "mtu", NAME_SPACE, value, null);
        ResourceData.Builder dataBdlr = DefaultResourceData.builder()
                .addDataNode(dBlr.build());
        dBlr = initializeDataNode(builder);
        value = "color";
        dBlr = addDataNode(dBlr, "color", NAME_SPACE, value, null);
        dataBdlr = dataBdlr.addDataNode(dBlr.build());
        dBlr = initializeDataNode(builder);
        value = null;
        dBlr = addDataNode(dBlr, "interface", NAME_SPACE, value, null);
        value = "name";
        dBlr = addDataNode(dBlr, "name", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        dataBdlr = dataBdlr.addDataNode(dBlr.build()).resourceId(builder.build());
        DefaultYobBuilder yobBuilder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = yobBuilder.getYangObject(dataBdlr.build());
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        LeafModelObject mtu = ((LeafModelObject) modelObject);
        assertThat(mtu.values().get(0), is("mtu"));
        LeafModelObject color = ((LeafModelObject) modelObjectList.get(1));
        assertThat(color.values().get(0), is("color"));
        DefaultYangAutoPrefixInterface intf = ((DefaultYangAutoPrefixInterface)
                modelObjectList.get(2));
        assertThat(intf.name(), is("name"));
    }
}
