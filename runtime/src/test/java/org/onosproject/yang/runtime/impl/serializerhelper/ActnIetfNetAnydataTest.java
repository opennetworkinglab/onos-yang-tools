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
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.configurationschedules.target.DefaultDataValue;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelsgrouping.tunnels.DefaultTunnel;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import java.util.Iterator;

import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.ACTN_SCHD_NS;
import static org.onosproject.yang.runtime.impl.TestUtils.ACTN_TE;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateLeafDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.walkINTree;

/**
 * Tests the serializer helper methods.
 */
public class ActnIetfNetAnydataTest {


    private static final String[] EXPECTED = {
            "Entry Node is configuration-schedules.",
            "Entry Node is target.",
            "Entry Node is object.",
            "Exit Node is object.",
            "Entry Node is operation.",
            "Exit Node is operation.",
            "Entry Node is data-value.",
            "Entry Node is tunnel.",
            "Entry Node is name.",
            "Exit Node is name.",
            "Entry Node is config.",
            "Entry Node is name.",
            "Exit Node is name.",
            "Exit Node is config.",
            "Exit Node is tunnel.",
            "Exit Node is data-value.",
            "Entry Node is schedules.",
            "Entry Node is schedule.",
            "Entry Node is schedule-id.",
            "Exit Node is schedule-id.",
            "Entry Node is start.",
            "Exit Node is start.",
            "Entry Node is schedule-duration.",
            "Exit Node is schedule-duration.",
            "Exit Node is schedule.",
            "Exit Node is schedules.",
            "Exit Node is target.",
            "Exit Node is configuration-schedules."
    };

    /**
     * Test anydata add to data node builder.
     */
    @Test
    public void atcnDataNodeTest() {

        TestYangSerializerContext context = new TestYangSerializerContext();
        DataNode.Builder dBlr = initializeDataNode(context);
        context.getRegistry().registerAnydataSchema(
                DefaultDataValue.class, DefaultTunnel.class);
        DataNode n = actnDataTree(dBlr);
        validateDataNodeTree(n);
    }

    /**
     * Creates the data node tree for actn anydata test case.
     *
     * @param dBlr data node builder
     */
    public static DataNode actnDataTree(DataNode.Builder dBlr) {

        String value = null;
        // Adding container configuration-schedules
        dBlr = addDataNode(dBlr, "configuration-schedules", ACTN_SCHD_NS, value, null);
        // Adding list target
        dBlr = addDataNode(dBlr, "target", ACTN_SCHD_NS, value, null);
        value = "te-links";
        dBlr = addDataNode(dBlr, "object", ACTN_SCHD_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "configure";
        dBlr = addDataNode(dBlr, "operation", ACTN_SCHD_NS, value, null);
        dBlr = exitDataNode(dBlr);
        // Adding anydata container
        value = null;
        dBlr = addDataNode(dBlr, "data-value", ACTN_SCHD_NS, value, null);

        dBlr = addDataNode(dBlr, "tunnel", ACTN_TE, value, null);
        value = "p2p";
        dBlr = addDataNode(dBlr, "name", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = null;
        dBlr = addDataNode(dBlr, "config", null, value, null);
        value = "p2p";
        dBlr = addDataNode(dBlr, "name", null, value, null);
        dBlr = exitDataNode(dBlr);

        dBlr = exitDataNode(dBlr); // config
        dBlr = exitDataNode(dBlr); // tunnel
        dBlr = exitDataNode(dBlr); // data-value

        value = null;
        // Adding container schedules
        dBlr = addDataNode(dBlr, "schedules", ACTN_SCHD_NS, value, null);
        // Adding list schedules
        dBlr = addDataNode(dBlr, "schedule", null, value, null);
        value = "11";
        dBlr = addDataNode(dBlr, "schedule-id", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "2016-09-12T23:20:50.52Z";
        dBlr = addDataNode(dBlr, "start", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "PT108850373514M";
        dBlr = addDataNode(dBlr, "schedule-duration", null, value, null);
        dBlr = exitDataNode(dBlr);
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

        validateDataNode(n, "configuration-schedules", ACTN_SCHD_NS,
                         SINGLE_INSTANCE_NODE, true, null);
        Iterator<DataNode> it = ((InnerNode) n).childNodes().values().iterator();
        n = it.next();
        validateDataNode(n, "target", ACTN_SCHD_NS, MULTI_INSTANCE_NODE,
                         true, null);
        Iterator<KeyLeaf> keyIt = ((ListKey) n.key()).keyLeafs().iterator();
        validateLeafDataNode(keyIt.next(), "object", ACTN_SCHD_NS, "te-links");

        Iterator<DataNode> it1 = ((InnerNode) n).childNodes().values()
                .iterator();
        n = it1.next();
        validateDataNode(n, "object", ACTN_SCHD_NS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "te-links");
        n = it1.next();
        validateDataNode(n, "operation", ACTN_SCHD_NS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "configure");

        n = it1.next();
        validateDataNode(n, "data-value", ACTN_SCHD_NS, SINGLE_INSTANCE_NODE,
                         true, null);

        Iterator<DataNode> it3 = ((InnerNode) n).childNodes().values()
                .iterator();
        n = it3.next();
        validateDataNode(n, "tunnel", ACTN_TE, MULTI_INSTANCE_NODE,
                         true, null);
        keyIt = ((ListKey) n.key()).keyLeafs().iterator();
        validateLeafDataNode(keyIt.next(), "name", ACTN_TE, "p2p");

        it3 = ((InnerNode) n).childNodes().values().iterator();
        n = it3.next();
        validateDataNode(n, "name", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "p2p");
        n = it3.next();
        validateDataNode(n, "config", ACTN_TE, SINGLE_INSTANCE_NODE,
                         true, null);
        it3 = ((InnerNode) n).childNodes().values().iterator();
        n = it3.next();
        validateDataNode(n, "name", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "p2p");

        n = it1.next();
        validateDataNode(n, "schedules", ACTN_SCHD_NS, SINGLE_INSTANCE_NODE,
                         true, null);

        Iterator<DataNode> it2 = ((InnerNode) n).childNodes().values()
                .iterator();
        n = it2.next();
        validateDataNode(n, "schedule", ACTN_SCHD_NS, MULTI_INSTANCE_NODE,
                         true, null);
        keyIt = ((ListKey) n.key()).keyLeafs().iterator();
        validateLeafDataNode(keyIt.next(), "schedule-id", ACTN_SCHD_NS, "11");

        it1 = ((InnerNode) n).childNodes().values().iterator();
        n = it1.next();
        validateDataNode(n, "schedule-id", ACTN_SCHD_NS,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "11");
        n = it1.next();
        validateDataNode(n, "start", ACTN_SCHD_NS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "2016-09-12T23:20:50.52Z");

        n = it1.next();
        validateDataNode(n, "schedule-duration", ACTN_SCHD_NS,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, false, "PT108850373514M");

        walkINTree(node, EXPECTED);
    }
}
