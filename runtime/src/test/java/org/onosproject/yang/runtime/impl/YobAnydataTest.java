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
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.DefaultNetworks;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.DefaultNetwork;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.network.DefaultNode;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.network.Node;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.network.node.SupportingNode;
import org.onosproject.yang.gen.v1.yrtnetworktopology.rev20151208.yrtnetworktopology.networks.network.augmentedndnetwork.DefaultLink;
import org.onosproject.yang.gen.v1.yrtnetworktopology.rev20151208.yrtnetworktopology.networks.network.augmentedndnetwork.Link;
import org.onosproject.yang.gen.v11.anytest.rev20160624.anytest.DefaultC1;
import org.onosproject.yang.gen.v11.anytest.rev20160624.anytest.c1.DefaultMydata2;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.InnerModelObject;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.ResourceData;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.IETFNS;
import static org.onosproject.yang.runtime.impl.TestUtils.TANY_NS;

/**
 * Tests the YANG object building for the YANG data nodes based on the non
 * schema augmented nodes.
 */
public class YobAnydataTest {
    private static final String NW_TOPO_NAME_SPACE = "urn:ietf:params:xml:ns:yang:yrt-ietf-network-topology";
    TestYangSerializerContext context = new TestYangSerializerContext();
    DataNode.Builder dBlr;
    String value;

    public DataNode buildDnForAnydata() {
        dBlr = initializeDataNode(context);
        value = null;
        // Adding container c1
        dBlr = addDataNode(dBlr, "c1", TANY_NS, value, null);
        // Adding anydata container
        dBlr = addDataNode(dBlr, "mydata2", TANY_NS, value, null);
        ModelObjectId id = new ModelObjectId.Builder()
                .addChild(DefaultC1.class).addChild(DefaultMydata2.class)
                .build();
        ModelObjectId id1 = new ModelObjectId.Builder()
                .addChild(DefaultNetworks.class)
                .addChild(DefaultNetwork.class, null)
                .addChild(DefaultNode.class, null)
                .build();
        ModelObjectId id2 = new ModelObjectId.Builder()
                .addChild(DefaultNetworks.class)
                .addChild(DefaultNetwork.class, null)
                .addChild(DefaultLink.class, null)
                .build();
        context.getRegistry().registerAnydataSchema(id, id1);
        context.getRegistry().registerAnydataSchema(id, id2);

        // Adding list inside anydata container
        dBlr = addDataNode(dBlr, "link", NW_TOPO_NAME_SPACE, value, null);
        value = "link-id";
        dBlr = addDataNode(dBlr, "link-id", NW_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "source", NW_TOPO_NAME_SPACE, value, null);
        value = "source-node";
        dBlr = addDataNode(dBlr, "source-node", NW_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // exit source
        dBlr = exitDataNode(dBlr); // exit link

        // Adding list inside anydata container
        value = null;
        dBlr = addDataNode(dBlr, "node", IETFNS, value, null);
        // Adding key element node-id
        value = "node1";
        dBlr = addDataNode(dBlr, "node-id", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = null;
        // Adding list inside list
        dBlr = addDataNode(dBlr, "supporting-node", null, value, null);
        // Adding key element network-ref
        value = "network3";
        dBlr = addDataNode(dBlr, "network-ref", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "network4";
        // Adding key element node-ref
        dBlr = addDataNode(dBlr, "node-ref", null, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        return dBlr.build();
    }


    @Test
    public void anydataTest() {
        DataNode dataNode = buildDnForAnydata();
        ResourceData data = DefaultResourceData.builder()
                .addDataNode(dataNode).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(context.getRegistry());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultC1 c1 = ((DefaultC1) modelObject);
        DefaultMydata2 mydata2 = ((DefaultMydata2) c1.mydata2());

        List<InnerModelObject> link = mydata2.anydata(DefaultLink.class);
        assertThat(((Link) link.get(0)).linkId().toString(), is("link-id"));
        assertThat(((Link) link.get(0)).source().sourceNode().toString(), is("source-node"));

        List<InnerModelObject> node = mydata2.anydata(DefaultNode.class);
        assertThat(((Node) node.get(0)).nodeId().toString(), is("node1"));
        SupportingNode snode = ((Node) node.get(0)).supportingNode().get(0);
        assertThat(snode.networkRef().toString(), is("network3"));
        assertThat(snode.nodeRef().toString(), is("network4"));
    }
}
