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
import org.onosproject.yang.gen.v1.yrtietfinettypes.rev20130715.yrtietfinettypes.Uri;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.DefaultNetworks;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.NetworkId;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.NodeId;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.DefaultNetwork;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.network.DefaultNode;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.network.node.DefaultSupportingNode;
import org.onosproject.yang.gen.v1.yrtnetworktopology.rev20151208.yrtnetworktopology.LinkId;
import org.onosproject.yang.gen.v1.yrtnetworktopology.rev20151208.yrtnetworktopology.networks.network.augmentedndnetwork.DefaultLink;
import org.onosproject.yang.gen.v1.yrtnetworktopology.rev20151208.yrtnetworktopology.networks.network.augmentedndnetwork.link.DefaultSource;
import org.onosproject.yang.gen.v11.anytest.rev20160624.anytest.DefaultC1;
import org.onosproject.yang.gen.v11.anytest.rev20160624.anytest.c1.DefaultMydata2;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultModelObjectData.Builder;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;

import java.util.Iterator;
import java.util.List;

import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;
import static org.onosproject.yang.runtime.impl.TestUtils.IETFNS;
import static org.onosproject.yang.runtime.impl.TestUtils.TANY_NS;
import static org.onosproject.yang.runtime.impl.TestUtils.TOPONS;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;

/**
 * Unit test cases for resource id conversion from model object id.
 */
public class YtbAnydataTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ResourceData rscData;
    private DefaultDataTreeBuilder treeBuilder;
    private ResourceId id;
    private List<NodeKey> keys;
    private SchemaId sid;
    private ModelObjectId mid;
    private Builder data;
    DefaultYangModelRegistry reg;

    /**
     * Prior setup for each UT.
     */
    @Before
    public void setUp() {
        processSchemaRegistry();
        reg = registry();
        treeBuilder = new DefaultDataTreeBuilder(reg);
    }


    /**
     * Processes anydata with augmented node as child.
     */
    @Test
    public void processAnydataTest() {

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
        reg.registerAnydataSchema(id, id1);
        reg.registerAnydataSchema(id, id2);
        DefaultC1 c1 = new DefaultC1();
        DefaultMydata2 mydata2 = new DefaultMydata2();

        // link
        DefaultLink link = new DefaultLink();
        link.linkId(new LinkId(new Uri("link-id")));
        DefaultSource source = new DefaultSource();
        source.sourceNode(new NodeId(new Uri("source-node")));
        link.source(source);

        DefaultLink link1 = new DefaultLink();
        link1.linkId(new LinkId(new Uri("link-id1")));
        DefaultSource source1 = new DefaultSource();
        source1.sourceNode(new NodeId(new Uri("source-node1")));
        link1.source(source1);

        //node
        DefaultNode node = new DefaultNode();
        node.nodeId(new NodeId(new Uri("node1")));
        DefaultSupportingNode sn = new DefaultSupportingNode();
        sn.networkRef(new NetworkId(new Uri("network3")));
        sn.nodeRef(new NodeId(new Uri("network4")));
        node.addToSupportingNode(sn);
        mydata2.addAnydata(link);
        mydata2.addAnydata(link1);
        mydata2.addAnydata(node);

        c1.mydata2(mydata2);
        data = new Builder();
        data.addModelObject(c1);
        rscData = treeBuilder.getResourceData(data.build());

        List<DataNode> nodes = rscData.dataNodes();
        DataNode n = nodes.get(0);
        validateDataNode(n, "c1", TANY_NS, SINGLE_INSTANCE_NODE,
                         true, null);
        n = ((InnerNode) n).childNodes().values().iterator().next();
        validateDataNode(n, "mydata2", TANY_NS, SINGLE_INSTANCE_NODE,
                         true, null);
        Iterator<DataNode> it = ((InnerNode) n).childNodes().values().iterator();

        // node validation
        n = it.next();
        validateDataNode(n, "node", IETFNS, MULTI_INSTANCE_NODE,
                         true, null);

        Iterator<DataNode> it1 = ((InnerNode) n).childNodes().values().iterator();
        n = it1.next();
        validateDataNode(n, "node-id", IETFNS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "node1");
        n = it1.next();
        validateDataNode(n, "supporting-node", IETFNS, MULTI_INSTANCE_NODE,
                         true, null);

        Iterator<DataNode> it2 = ((InnerNode) n).childNodes().values()
                .iterator();
        n = it2.next();
        validateDataNode(n, "network-ref", IETFNS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "network3");
        n = it2.next();
        validateDataNode(n, "node-ref", IETFNS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "network4");

        //link validation
        n = it.next();
        validateDataNode(n, "link", TOPONS, MULTI_INSTANCE_NODE,
                         true, null);

        it1 = ((InnerNode) n).childNodes().values().iterator();
        n = it1.next();
        validateDataNode(n, "link-id", TOPONS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "link-id");
        n = it1.next();
        validateDataNode(n, "source", TOPONS, SINGLE_INSTANCE_NODE,
                         true, null);

        it2 = ((InnerNode) n).childNodes().values().iterator();
        n = it2.next();
        validateDataNode(n, "source-node", TOPONS,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "source-node");

        n = it.next();
        validateDataNode(n, "link", TOPONS, MULTI_INSTANCE_NODE,
                         true, null);

        it1 = ((InnerNode) n).childNodes().values().iterator();
        n = it1.next();
        validateDataNode(n, "link-id", TOPONS,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "link-id1");
        n = it1.next();
        validateDataNode(n, "source", TOPONS, SINGLE_INSTANCE_NODE,
                         true, null);

        it2 = ((InnerNode) n).childNodes().values().iterator();
        n = it2.next();
        validateDataNode(n, "source-node", TOPONS,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "source-node1");
    }
}
