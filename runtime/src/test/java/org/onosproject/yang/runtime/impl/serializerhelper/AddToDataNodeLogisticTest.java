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
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.HelperContext;
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import java.util.Iterator;
import java.util.Map;

import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.getResourceId;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.LMNG;
import static org.onosproject.yang.runtime.impl.TestUtils.LMNG_N;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateLeafDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateResourceId;
import static org.onosproject.yang.runtime.impl.TestUtils.walkINTree;

/**
 * Tests the serializer helper methods.
 */
public class AddToDataNodeLogisticTest {

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

            "Entry Node is Customs-supervisor.",
            "Exit Node is Customs-supervisor.",

            "Entry Node is Merchandiser-supervisor.",
            "Exit Node is Merchandiser-supervisor.",

            "Entry Node is Material-supervisor.",
            "Entry Node is name.",
            "Exit Node is name.",
            "Entry Node is departmentId.",
            "Exit Node is departmentId.",
            "Exit Node is Material-supervisor.",

            "Entry Node is Material-supervisor.",
            "Entry Node is name.",
            "Exit Node is name.",
            "Entry Node is departmentId.",
            "Exit Node is departmentId.",
            "Exit Node is Material-supervisor.",

            "Entry Node is Material-supervisor.",
            "Entry Node is name.",
            "Exit Node is name.",
            "Entry Node is departmentId.",
            "Exit Node is departmentId.",
            "Exit Node is Material-supervisor.",

            "Entry Node is Purchasing-supervisor.",

            "Entry Node is purchasing-specialist.",
            "Exit Node is purchasing-specialist.",

            "Entry Node is support.",
            "Exit Node is support.",

            "Entry Node is support.",
            "Exit Node is support.",

            "Entry Node is support.",
            "Exit Node is support.",

            "Exit Node is Purchasing-supervisor.",

            "Entry Node is Warehouse-supervisor.",
            "Exit Node is Warehouse-supervisor.",

            "Entry Node is Warehouse-supervisor.",
            "Exit Node is Warehouse-supervisor.",

            "Entry Node is Warehouse-supervisor.",
            "Exit Node is Warehouse-supervisor.",

            "Entry Node is Warehouse-supervisor.",
            "Exit Node is Warehouse-supervisor.",

            "Entry Node is Trading-supervisor.",
            "Exit Node is Trading-supervisor.",

            "Entry Node is Employee-id.",
            "Exit Node is Employee-id.",

            "Entry Node is Employee-id.",
            "Exit Node is Employee-id.",

            "Entry Node is Employee-id.",
            "Exit Node is Employee-id.",

            "Entry Node is Employee-id.",
            "Exit Node is Employee-id.",
            "Exit Node is /."
    };

    /**
     * Test add to data node builder logistic manager module.
     */
    @Test
    public void addToDataTest() {

        dBlr = getLogisticModuleDataNode();

        walkINTree(dBlr.build(), EXPECTED);
        // Validating the data node.
        DataNode node = dBlr.build();
        validateDataNode(node, "/", null, SINGLE_INSTANCE_NODE, true, null);

        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();
        value = "1";
        validateDataNode(n.getValue(), "Customs-supervisor", LMNG,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, value);

        n = it.next();
        value = "1";
        validateDataNode(n.getValue(), "Merchandiser-supervisor", LMNG,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, value);

        n = it.next();
        value = null;
        validateDataNode(n.getValue(), "Material-supervisor", LMNG,
                         MULTI_INSTANCE_NODE, true, value);

        Iterator<KeyLeaf> keyIt = ((ListKey) n.getKey()).keyLeafs().iterator();
        validateLeafDataNode(keyIt.next(), "name", LMNG, "abc");

        Iterator<Map.Entry<NodeKey, DataNode>> it1;
        it1 = ((InnerNode) n.getValue()).childNodes().entrySet().iterator();
        validateDataNode(it1.next().getValue(), "name", LMNG,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "abc");
        validateDataNode(it1.next().getValue(), "departmentId", LMNG,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "dep-1");

        n = it.next();
        value = null;
        validateDataNode(n.getValue(), "Material-supervisor", LMNG,
                         MULTI_INSTANCE_NODE, true, value);

        keyIt = ((ListKey) n.getKey()).keyLeafs().iterator();
        validateLeafDataNode(keyIt.next(), "name", LMNG, "abc1");
        it1 = ((InnerNode) n.getValue()).childNodes().entrySet().iterator();
        validateDataNode(it1.next().getValue(), "name", LMNG,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "abc1");
        validateDataNode(it1.next().getValue(), "departmentId", LMNG,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "dep-1");

        n = it.next();
        value = null;
        validateDataNode(n.getValue(), "Material-supervisor", LMNG,
                         MULTI_INSTANCE_NODE, true, value);

        keyIt = ((ListKey) n.getKey()).keyLeafs().iterator();
        validateLeafDataNode(keyIt.next(), "name", LMNG, "abc2");
        it1 = ((InnerNode) n.getValue()).childNodes().entrySet().iterator();
        validateDataNode(it1.next().getValue(), "name", LMNG,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "abc2");
        validateDataNode(it1.next().getValue(), "departmentId", LMNG,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "dep-1");

        n = it.next();
        value = null;
        validateDataNode(n.getValue(), "Purchasing-supervisor", LMNG,
                         SINGLE_INSTANCE_NODE, true, value);

        it1 = ((InnerNode) n.getValue()).childNodes().entrySet().iterator();
        validateDataNode(it1.next().getValue(), "purchasing-specialist", LMNG,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "xyz");
        validateDataNode(it1.next().getValue(), "support", LMNG,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, "helpdesk");
        validateDataNode(it1.next().getValue(), "support", LMNG,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, "helpdesk1");
        validateDataNode(it1.next().getValue(), "support", LMNG,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, "helpdesk2");

        n = it.next();
        value = "1";
        validateDataNode(n.getValue(), "Warehouse-supervisor", LMNG,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, value);

        n = it.next();
        value = "2";
        validateDataNode(n.getValue(), "Warehouse-supervisor", LMNG,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, value);

        n = it.next();
        value = "3";
        validateDataNode(n.getValue(), "Warehouse-supervisor", LMNG,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, value);

        n = it.next();
        value = "4";
        validateDataNode(n.getValue(), "Warehouse-supervisor", LMNG,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, value);

        n = it.next();
        value = "1";
        validateDataNode(n.getValue(), "Trading-supervisor", LMNG,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, value);

        n = it.next();
        value = "1";
        validateDataNode(n.getValue(), "Employee-id", LMNG,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, value);

        n = it.next();
        value = "2";
        validateDataNode(n.getValue(), "Employee-id", LMNG,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, value);

        n = it.next();
        value = "3";
        validateDataNode(n.getValue(), "Employee-id", LMNG,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, value);

        n = it.next();
        value = "4";
        validateDataNode(n.getValue(), "Employee-id", LMNG,
                         MULTI_INSTANCE_LEAF_VALUE_NODE, false, value);
    }

    /**
     * Returns the logistic module data tree.
     */
    public DataNode.Builder getLogisticModuleDataNode() {
        dBlr = initializeDataNode(context);

        value = "1";
        dBlr = addDataNode(dBlr, "Customs-supervisor", LMNG_N, value, null);
        id = getResourceId(dBlr);
        nA = new String[]{"/", "Customs-supervisor"};
        nsA = new String[]{null, LMNG};
        valA = new String[]{"1"};
        validateResourceId(nA, nsA, valA, id);
        dBlr = exitDataNode(dBlr);

        dBlr = addDataNode(dBlr, "Merchandiser-supervisor", LMNG, value, null);
        info = (HelperContext) dBlr.appInfo();
        id = getResourceId(dBlr);
        nA = new String[]{"/", "Merchandiser-supervisor"};
        nsA = new String[]{null, LMNG};
        valA = new String[]{"1"};
        validateResourceId(nA, nsA, valA, id);

        dBlr = exitDataNode(dBlr);

        value = null;
        dBlr = addDataNode(dBlr, "Material-supervisor", LMNG_N, value, null);

        value = "abc";
        dBlr = addDataNode(dBlr, "name", LMNG_N, value, null);
        dBlr = exitDataNode(dBlr);

        value = "dep-1";
        dBlr = addDataNode(dBlr, "departmentId", null, value, null);
        info = (HelperContext) dBlr.appInfo();
        id = getResourceId(dBlr);
        nA = new String[]{"/", "Material-supervisor", "name", "departmentId"};
        nsA = new String[]{null, LMNG, LMNG, LMNG};
        valA = new String[]{"abc", "dep-1"};
        validateResourceId(nA, nsA, valA, id);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

        id = getResourceId(dBlr);
        nA = new String[]{"/"};
        nsA = new String[]{null};
        valA = new String[]{};
        validateResourceId(nA, nsA, valA, id);

        value = null;
        dBlr = addDataNode(dBlr, "Material-supervisor", LMNG, value, null);

        value = "abc1";
        dBlr = addDataNode(dBlr, "name", LMNG_N, value, null);
        dBlr = exitDataNode(dBlr);

        value = "dep-1";
        dBlr = addDataNode(dBlr, "departmentId", null, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

        value = null;
        dBlr = addDataNode(dBlr, "Material-supervisor", LMNG_N, value, null);

        value = "abc2";
        dBlr = addDataNode(dBlr, "name", LMNG, value, null);
        dBlr = exitDataNode(dBlr);

        value = "dep-1";
        dBlr = addDataNode(dBlr, "departmentId", null, value, null);
        id = getResourceId(dBlr);
        nA = new String[]{"/", "Material-supervisor", "name", "departmentId"};
        nsA = new String[]{null, LMNG, LMNG, LMNG};
        valA = new String[]{"abc2", "dep-1"};
        validateResourceId(nA, nsA, valA, id);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

        value = null;
        dBlr = addDataNode(dBlr, "Purchasing-supervisor", LMNG_N, value, null);

        value = "xyz";
        dBlr = addDataNode(dBlr, "purchasing-specialist", LMNG, value, null);
        dBlr = exitDataNode(dBlr);

        value = "helpdesk";
        dBlr = addDataNode(dBlr, "support", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "helpdesk1";
        dBlr = addDataNode(dBlr, "support", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "helpdesk2";
        dBlr = addDataNode(dBlr, "support", null, value, null);
        id = getResourceId(dBlr);
        nA = new String[]{"/", "Purchasing-supervisor", "support"};
        nsA = new String[]{null, LMNG, LMNG};
        valA = new String[]{"helpdesk2"};
        validateResourceId(nA, nsA, valA, id);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

        value = "1";
        dBlr = addDataNode(dBlr, "Warehouse-supervisor", LMNG, value, null);
        dBlr = exitDataNode(dBlr);

        value = "2";
        dBlr = addDataNode(dBlr, "Warehouse-supervisor", LMNG_N, value, null);
        dBlr = exitDataNode(dBlr);

        value = "3";
        dBlr = addDataNode(dBlr, "Warehouse-supervisor", LMNG, value, null);
        dBlr = exitDataNode(dBlr);

        value = "4";
        dBlr = addDataNode(dBlr, "Warehouse-supervisor", LMNG_N, value, null);
        dBlr = exitDataNode(dBlr);

        value = "1";
        dBlr = addDataNode(dBlr, "Trading-supervisor", LMNG, value, null);
        dBlr = exitDataNode(dBlr);

        value = "1";
        dBlr = addDataNode(dBlr, "Employee-id", LMNG, value, null);
        dBlr = exitDataNode(dBlr);

        value = "2";
        dBlr = addDataNode(dBlr, "Employee-id", LMNG_N, value, null);
        dBlr = exitDataNode(dBlr);

        value = "3";
        dBlr = addDataNode(dBlr, "Employee-id", LMNG_N, value, null);
        dBlr = exitDataNode(dBlr);

        value = "4";
        dBlr = addDataNode(dBlr, "Employee-id", LMNG, value, null);
        dBlr = exitDataNode(dBlr);
        return dBlr;
    }
}
