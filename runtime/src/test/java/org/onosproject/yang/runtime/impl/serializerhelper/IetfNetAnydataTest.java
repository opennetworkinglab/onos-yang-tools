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

package org.onosproject.yang.runtime.impl.serializerhelper;

import org.junit.Test;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.DefaultNetworks;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.DefaultNetwork;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.network.DefaultNode;
import org.onosproject.yang.gen.v1.yrtnetworktopology.rev20151208.yrtnetworktopology.networks.network.augmentedndnetwork.DefaultLink;
import org.onosproject.yang.gen.v11.anytest.rev20160624.anytest.DefaultC1;
import org.onosproject.yang.gen.v11.anytest.rev20160624.anytest.c1.DefaultMydata2;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.HelperContext;
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import java.util.Iterator;
import java.util.Map;

import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.IETFNS;
import static org.onosproject.yang.runtime.impl.TestUtils.TANY_NS;
import static org.onosproject.yang.runtime.impl.TestUtils.TOPONS;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateLeafDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.walkINTree;

/**
 * Tests the serializer helper methods.
 */
public class IetfNetAnydataTest {

    TestYangSerializerContext context = new TestYangSerializerContext();

    /*
     * Reference for data node info.
     */
    HelperContext info;

    /*
     * Reference for data node builder.
     */
    DataNode.Builder dBlr;

    /*
     * Reference for resource id.
     */
    ResourceId id;

    /*
     * Reference for the value.
     */
    String value;

    /*
     * Reference for string array to used for resource id testing.
     */
    String[] nA;
    String[] nsA;
    String[] valA;

    private static final String[] EXPECTED = {
            "Entry Node is /.",
            "Entry Node is c1.",
            "Entry Node is mydata2.",
            "Entry Node is link.",
            "Entry Node is link-id.",
            "Exit Node is link-id.",
            "Entry Node is source.",
            "Entry Node is source-node.",
            "Exit Node is source-node.",
            "Exit Node is source.",
            "Exit Node is link.",
            "Entry Node is node.",
            "Entry Node is node-id.",
            "Exit Node is node-id.",
            "Entry Node is supporting-node.",
            "Entry Node is network-ref.",
            "Exit Node is network-ref.",
            "Entry Node is node-ref.",
            "Exit Node is node-ref.",
            "Exit Node is supporting-node.",
            "Exit Node is node.",
            "Exit Node is mydata2.",
            "Exit Node is c1.",
            "Exit Node is /."
    };

    /**
     * Test anydata add to data node builder.
     */
    @Test
    public void addToDataTest() {

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
        dBlr = addDataNode(dBlr, "link", TOPONS, value, null);
        value = "link-id";
        dBlr = addDataNode(dBlr, "link-id", TOPONS, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "source", TOPONS, value, null);
        value = "source-node";
        dBlr = addDataNode(dBlr, "source-node", TOPONS, value, null);
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

        // Validating the data node.
        DataNode node = dBlr.build();
        validateDataNode(node, "/", null, SINGLE_INSTANCE_NODE, true, null);

        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        Iterator<Map.Entry<NodeKey, DataNode>> iter = childMap.entrySet()
                .iterator();
        DataNode n = iter.next().getValue();
        validateDataNode(n, "c1", TANY_NS, SINGLE_INSTANCE_NODE,
                         true, null);
        n = ((InnerNode) n).childNodes().values().iterator().next();
        validateDataNode(n, "mydata2", TANY_NS, SINGLE_INSTANCE_NODE,
                         true, null);
        Iterator<DataNode> it = ((InnerNode) n).childNodes().values().iterator();

        //link validation
        n = it.next();
        validateDataNode(n, "link", TOPONS, MULTI_INSTANCE_NODE,
                         true, null);
        Iterator<KeyLeaf> keyIt = ((ListKey) n.key()).keyLeafs().iterator();

        validateLeafDataNode(keyIt.next(), "link-id", TOPONS, "link-id");
        Iterator<DataNode> it1 = ((InnerNode) n).childNodes().values().iterator();
        n = it1.next();
        validateDataNode(n, "link-id", TOPONS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "link-id");
        n = it1.next();
        validateDataNode(n, "source", TOPONS, SINGLE_INSTANCE_NODE,
                         true, null);

        Iterator<DataNode> it2 = ((InnerNode) n).childNodes().values().iterator();
        n = it2.next();
        validateDataNode(n, "source-node", TOPONS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "source-node");

        // node validation
        n = it.next();
        validateDataNode(n, "node", IETFNS, MULTI_INSTANCE_NODE,
                         true, null);
        keyIt = ((ListKey) n.key()).keyLeafs().iterator();

        validateLeafDataNode(keyIt.next(), "node-id", IETFNS, "node1");

        it1 = ((InnerNode) n).childNodes().values().iterator();
        n = it1.next();
        validateDataNode(n, "node-id", IETFNS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "node1");
        n = it1.next();
        validateDataNode(n, "supporting-node", IETFNS, MULTI_INSTANCE_NODE,
                         true, null);

        keyIt = ((ListKey) n.key()).keyLeafs().iterator();

        validateLeafDataNode(keyIt.next(), "network-ref", IETFNS, "network3");

        it2 = ((InnerNode) n).childNodes().values().iterator();
        n = it2.next();
        validateDataNode(n, "network-ref", IETFNS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "network3");
        n = it2.next();
        validateDataNode(n, "node-ref", IETFNS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "network4");

        walkINTree(dBlr.build(), EXPECTED);
    }
}
