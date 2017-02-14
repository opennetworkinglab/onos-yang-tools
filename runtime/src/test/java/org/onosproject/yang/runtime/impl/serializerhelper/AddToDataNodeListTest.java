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

package org.onosproject.yang.runtime.impl.serializerhelper;

import org.junit.Test;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.helperutils.HelperContext;
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.getResourceId;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateLeafDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateResourceId;

/**
 * Tests the serializer helper methods.
 */
public class AddToDataNodeListTest {

    public static final String LNS = "yrt:list";

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

    /**
     * Test add to data node builder.
     */
    @Test
    public void addToDataListTest() throws IOException {

        Object ob;
        dBlr = initializeDataNode(context);

        dBlr = addDataNode(dBlr, "l1", LNS, value, null);
        value = "1";
        dBlr = addDataNode(dBlr, "k1", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "2";
        dBlr = addDataNode(dBlr, "k2", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "c1", null, value, null);
        value = "0";
        dBlr = addDataNode(dBlr, "l1", null, value, null);

        info = (HelperContext) dBlr.appInfo();
        id = getResourceId(dBlr);
        dBlr = exitDataNode(dBlr);

        ResourceId id1 = getResourceId(dBlr);
        dBlr = exitDataNode(dBlr);

        value = "3";
        dBlr = addDataNode(dBlr, "k3", null, value, null);

        info = (HelperContext) dBlr.appInfo();
        ResourceId id2 = getResourceId(dBlr);

        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

        // Checking leaf list
        value = "1";
        dBlr = addDataNode(dBlr, "leaf1", LNS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "2";
        dBlr = addDataNode(dBlr, "leaf1", LNS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "3";
        dBlr = addDataNode(dBlr, "leaf1", LNS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "4";
        dBlr = addDataNode(dBlr, "leaf1", LNS, value, null);
        ResourceId id3 = getResourceId(dBlr);
        dBlr = exitDataNode(dBlr);

        //Tree validation
        nA = new String[]{"/", "l1", "k1", "k2", "k3", "c1", "l1", ""};
        nsA = new String[]{null, LNS, LNS, LNS, LNS, LNS, LNS, ""};
        valA = new String[]{"1", "2", "3", "0", ""};
        validateResourceId(nA, nsA, valA, id);

        nA = new String[]{"/", "l1", "k1", "k2", "k3", "c1", ""};
        nsA = new String[]{null, LNS, LNS, LNS, LNS, LNS, ""};
        valA = new String[]{"1", "2", "3", ""};
        validateResourceId(nA, nsA, valA, id1);

        nA = new String[]{"/", "l1", "k1", "k2", "k3", ""};
        nsA = new String[]{null, LNS, LNS, LNS, LNS, ""};
        valA = new String[]{"1", "2", "3", ""};
        validateResourceId(nA, nsA, valA, id2);

        nA = new String[]{"/", "leaf1", ""};
        nsA = new String[]{null, LNS, ""};
        valA = new String[]{"4", "2", "3", "0", ""};
        validateResourceId(nA, nsA, valA, id3);

        DataNode node = dBlr.build();
        validateDataNode(node, "/", null, SINGLE_INSTANCE_NODE, true, null);

        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "l1", LNS, MULTI_INSTANCE_NODE,
                         true, null);

        Iterator<KeyLeaf> keyIt = ((ListKey) n.getKey()).keyLeafs().iterator();

        validateLeafDataNode(keyIt.next(), "k1", LNS, "1");
        validateLeafDataNode(keyIt.next(), "k2", LNS, "2");
        validateLeafDataNode(keyIt.next(), "k3", LNS, "3");

        Iterator<Map.Entry<NodeKey, DataNode>> it1;
        it1 = ((InnerNode) n.getValue()).childNodes().entrySet().iterator();
        validateDataNode(it1.next().getValue(), "k1", LNS,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "1");
        validateDataNode(it1.next().getValue(), "k2", LNS,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "2");
        DataNode n1 = it1.next().getValue();
        validateDataNode(n1, "c1", LNS,
                         SINGLE_INSTANCE_NODE, true, null);
        validateDataNode(it1.next().getValue(), "k3", LNS,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "3");

        Iterator<Map.Entry<NodeKey, DataNode>> it2;
        it2 = ((InnerNode) n1).childNodes().entrySet().iterator();
        validateDataNode(it2.next().getValue(), "l1", LNS,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "0");
        validateDataNode(it.next().getValue(), "leaf1", LNS,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, "1");
        validateDataNode(it.next().getValue(), "leaf1", LNS,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, "2");
        validateDataNode(it.next().getValue(), "leaf1", LNS,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, "3");
        validateDataNode(it.next().getValue(), "leaf1", LNS,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, "4");
    }
}
