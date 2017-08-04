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
import org.onosproject.yang.gen.v1.yrtietftetopology.rev20160317.yrtietftetopology.telinkconfig.bundlestacklevel.bundle.DefaultBundledLinks;
import org.onosproject.yang.gen.v1.ymstopology.rev20140101.ymstopology.DefaultNode;
import org.onosproject.yang.gen.v1.ymstopology.rev20140101.ymstopology.node.choice1.Case1a;
import org.onosproject.yang.gen.v1.ymstopology.rev20140101.ymstopology.node.choice1.Case1b;
import org.onosproject.yang.gen.v1.ymstopology.rev20140101.ymstopology.node.choice1.case1b.choice1b.Case1Bi;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.addToResourceId;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeResourceId;

/**
 * Tests the YANG object building for the YANG data nodes.
 */
public class YobChoiceTest {

    private static final String NAME_SPACE = "urn:topo";
    private static final String NW_NS = "urn:ietf:params:xml:ns:yang:yrt-ietf-network";
    private static final String TE_NS = "urn:ietf:params:xml:ns:yang:yrt-ietf-te-topology";
    private static final String NW_TOPO_NS =
            "urn:ietf:params:xml:ns:yang:yrt-ietf-network-topology";
    TestYangSerializerContext context = new TestYangSerializerContext();
    private DataNode.Builder dBlr;
    private String value;
    private ResourceId.Builder rIdBlr;

    public DataNode buildDataNodeForCaseInChoice() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "node", NAME_SPACE, value, null);

        value = "id";
        dBlr = addDataNode(dBlr, "node-id", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str1";
        dBlr = addDataNode(dBlr, "leaf1a1", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str2";
        dBlr = addDataNode(dBlr, "leaf1a2", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        return dBlr.build();
    }

    public DataNode buildDataNodeForRecursiveChoice() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "node", NAME_SPACE, value, null);

        value = "id";
        dBlr = addDataNode(dBlr, "node-id", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str1";
        dBlr = addDataNode(dBlr, "leaf1bia", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str2";
        dBlr = addDataNode(dBlr, "leaf1bib", NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        return dBlr.build();
    }

    private ResourceId.Builder buildRIdForNodeInsideChoiceCase() {
        rIdBlr = initializeResourceId(context);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "networks", NW_NS, value);
        rIdBlr = addToResourceId(rIdBlr, "network", NW_NS, value);
        value = "network-id";
        rIdBlr = addToResourceId(rIdBlr, "network-id", NW_NS, value);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "link", NW_TOPO_NS, value);
        value = "link-id";
        rIdBlr = addToResourceId(rIdBlr, "link-id", NW_TOPO_NS, value);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "te", TE_NS, value);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "config", TE_NS, value);
        return rIdBlr;
    }

    public DataNode buildDnForChoiceCaseWithRid() {
        ResourceId.Builder rIdBdlr = buildRIdForNodeInsideChoiceCase();
        dBlr = initializeDataNode(rIdBdlr);
        value = null;
        dBlr = addDataNode(dBlr, "bundled-links", TE_NS, value, null);
        return dBlr.build();
    }

    @Test
    public void caseInChoice() {
        DataNode dataNode = buildDataNodeForCaseInChoice();
        ResourceData data = DefaultResourceData.builder().addDataNode(dataNode)
                .build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultNode node = ((DefaultNode) modelObject);
        assertThat(node.nodeId(), is("id"));
        assertThat(((Case1a) node.choice1()).leaf1A1(), is("str1"));
        assertThat(((Case1a) node.choice1()).leaf1A2(), is("str2"));
    }

    @Test
    public void recursiveChoice() {
        DataNode dataNode = buildDataNodeForRecursiveChoice();
        ResourceData data = DefaultResourceData.builder().addDataNode(dataNode)
                .build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultNode node = ((DefaultNode) modelObject);
        assertThat(node.nodeId(), is("id"));
        assertThat(((Case1Bi) ((Case1b) node.choice1()).choice1b()).leaf1Bia(),
                   is("str1"));
        assertThat(((Case1Bi) ((Case1b) node.choice1()).choice1b()).leaf1Bib(),
                   is("str2"));
    }

    @Test
    public void testChoiceCaseWithResourceId() {
        ResourceId.Builder rIdBdlr = buildRIdForNodeInsideChoiceCase();
        DataNode dataNode = buildDnForChoiceCaseWithRid();
        ResourceData data = DefaultResourceData.builder().addDataNode(dataNode)
                .resourceId(rIdBdlr.build()).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        assertThat(modelObject instanceof DefaultBundledLinks, is(true));
    }
}
