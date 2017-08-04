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
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.HelperContext;
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.getResourceId;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.IETFNAME;
import static org.onosproject.yang.runtime.impl.TestUtils.IETFNS;
import static org.onosproject.yang.runtime.impl.TestUtils.validateResourceId;
import static org.onosproject.yang.runtime.impl.TestUtils.walkINTree;

/**
 * Tests the serializer helper methods.
 */
public class AddToDataNodeIetfNetTest {

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
            "Entry Node is networks.",
            "Entry Node is network.",
            "Entry Node is network-id.",
            "Exit Node is network-id.",
            "Entry Node is supporting-network.",
            "Entry Node is network-ref.",
            "Exit Node is network-ref.",
            "Exit Node is supporting-network.",
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
            "Exit Node is network.",
            "Exit Node is networks.",
            "Entry Node is networks-state.",
            "Entry Node is network.",
            "Entry Node is network-ref.",
            "Exit Node is network-ref.",
            "Entry Node is server-provided.",
            "Exit Node is server-provided.",
            "Exit Node is network.",
            "Exit Node is networks-state.",
            "Exit Node is /.",
    };
    /**
     * Test add to data node builder.
     */
    @Test
    public void addToDataTest() {

        dBlr = initializeDataNode(context);
        // Adding container
        dBlr = addDataNode(dBlr, "networks", IETFNAME, value, null);
        // Adding list inside container
        dBlr = addDataNode(dBlr, "network", null, value, null);
        // Adding key element network Id
        value = "network1";
        dBlr = addDataNode(dBlr, "network-id", null, value, null);

        info = (HelperContext) dBlr.appInfo();
        id = getResourceId(dBlr);

        // Traverse back to parent
        dBlr = exitDataNode(dBlr);
        //Tree validation
        info = (HelperContext) dBlr.appInfo();
        id = getResourceId(dBlr);
        value = null;
        // Adding list inside list
        dBlr = addDataNode(dBlr, "supporting-network", null, value, null);
        // Adding key element network-ref
        value = "network2";
        dBlr = addDataNode(dBlr, "network-ref", null, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

        value = null;
        // Adding list inside list
        dBlr = addDataNode(dBlr, "node", null, value, null);
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

        value = null;
        // Adding container
        dBlr = addDataNode(dBlr, "networks-state", IETFNAME, value, null);
        // Adding list inside container
        dBlr = addDataNode(dBlr, "network", null, value, null);
        // Adding key element network-ref
        value = "network5";
        dBlr = addDataNode(dBlr, "network-ref", null, value, null);
        dBlr = exitDataNode(dBlr);
        // Adding leaf server-provided
        value = "true";
        dBlr = addDataNode(dBlr, "server-provided", null, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

        //Tree validation
        nA = new String[]{"/", "networks", "network", "network-id", ""};
        nsA = new String[]{null, IETFNS, IETFNS, IETFNS, ""};
        valA = new String[]{"network1", ""};
        validateResourceId(nA, nsA, valA, id);
        DataNode node = dBlr.build();
        walkINTree(dBlr.build(), EXPECTED);
    }
}
