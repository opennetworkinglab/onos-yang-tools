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
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import java.util.Iterator;

import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.NOTIF_NS;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.walkINTree;

/**
 * Tests the serializer helper methods.
 */
public class YangNotificationTest {


    private static final String[] EXPECTED = {
            "Entry Node is /.",
            "Entry Node is event.",
            "Entry Node is c.",
            "Entry Node is event-class.",
            "Exit Node is event-class.",
            "Exit Node is c.",
            "Exit Node is event.",
            "Exit Node is /."
    };

    /**
     * Test notification add to data node builder.
     */
    @Test
    public void notificationTest() {

        TestYangSerializerContext context = new TestYangSerializerContext();
        DataNode.Builder dBlr = initializeDataNode(context);
        DataNode n = notificationTree(dBlr);
        validateDataNodeTree(n);
    }

    /**
     * Creates the data node tree for notification test case.
     *
     * @param dBlr data node builder
     */
    public static DataNode notificationTree(DataNode.Builder dBlr) {

        String value = null;
        // Adding notfication container configuration-schedules
        dBlr = addDataNode(dBlr, "event", NOTIF_NS, value, null);
        // Adding c container
        dBlr = addDataNode(dBlr, "c", NOTIF_NS, value, null);
        value = "xyz";
        dBlr = addDataNode(dBlr, "event-class", NOTIF_NS, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

        return dBlr.build();
    }

    /**
     * Validates the given data node sub-tree.
     *
     * @param node data node which needs to be validated
     */
    public static void validateDataNodeTree(DataNode node) {
        // Validating the data node.
        DataNode n = node;
        validateDataNode(n, "/", null,
                         SINGLE_INSTANCE_NODE, true, null);
        Iterator<DataNode> it = ((InnerNode) n).childNodes().values()
                .iterator();
        n = it.next();
        validateDataNode(n, "event", NOTIF_NS,
                         SINGLE_INSTANCE_NODE, true, null);
        it = ((InnerNode) n).childNodes().values().iterator();
        n = it.next();
        validateDataNode(n, "c", NOTIF_NS, SINGLE_INSTANCE_NODE,
                         true, null);

        Iterator<DataNode> it1 = ((InnerNode) n).childNodes().values()
                .iterator();
        n = it1.next();
        validateDataNode(n, "event-class", NOTIF_NS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "xyz");

        walkINTree(node, EXPECTED);
    }
}
