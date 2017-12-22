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
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.DefaultConfigurationSchedules;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.configurationschedules.DefaultTarget;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.configurationschedules.target.DefaultDataValue;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.DefaultTe;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelsgrouping.DefaultTunnels;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelsgrouping.tunnels.DefaultTunnel;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelObjectId;
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
import static org.onosproject.yang.runtime.impl.TestUtils.OTN_TUNN;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateLeafDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.walkINTree;

/**
 * Tests the serializer helper methods.
 */
public class ActnIetfNetAnydataTest {


    private static final String[] EXPECTED = {
            "Entry Node is tunnel.",
            "Entry Node is name.",
            "Exit Node is name.",
            "Entry Node is config.",
            "Entry Node is name.",
            "Exit Node is name.",
            "Entry Node is type.",
            "Exit Node is type.",
            "Entry Node is identifier.",
            "Exit Node is identifier.",
            "Entry Node is description.",
            "Exit Node is description.",
            "Entry Node is encoding.",
            "Exit Node is encoding.",
            "Entry Node is protection-type.",
            "Exit Node is protection-type.",
            "Entry Node is admin-status.",
            "Exit Node is admin-status.",
            "Entry Node is provider-id.",
            "Exit Node is provider-id.",
            "Entry Node is client-id.",
            "Exit Node is client-id.",
            "Entry Node is te-topology-id.",
            "Exit Node is te-topology-id.",
            "Entry Node is source.",
            "Exit Node is source.",
            "Entry Node is destination.",
            "Exit Node is destination.",
            "Entry Node is setup-priority.",
            "Exit Node is setup-priority.",
            "Entry Node is hold-priority.",
            "Exit Node is hold-priority.",
            "Entry Node is signaling-type.",
            "Exit Node is signaling-type.",
            "Entry Node is payload-treatment.",
            "Exit Node is payload-treatment.",
            "Entry Node is src-tpn.",
            "Exit Node is src-tpn.",
            "Entry Node is src-tributary-slot-count.",
            "Exit Node is src-tributary-slot-count.",
            "Entry Node is src-tributary-slots.",
            "Entry Node is values.",
            "Exit Node is values.",
            "Exit Node is src-tributary-slots.",
            "Entry Node is src-client-signal.",
            "Exit Node is src-client-signal.",
            "Entry Node is dst-client-signal.",
            "Exit Node is dst-client-signal.",
            "Entry Node is dst-tpn.",
            "Exit Node is dst-tpn.",
            "Entry Node is dst-tributary-slot-count.",
            "Exit Node is dst-tributary-slot-count.",
            "Entry Node is dst-tributary-slots.",
            "Entry Node is values.",
            "Exit Node is values.",
            "Exit Node is dst-tributary-slots.",
            "Exit Node is config.",
            "Entry Node is bandwidth.",
            "Entry Node is config.",
            "Entry Node is specification-type.",
            "Exit Node is specification-type.",
            "Entry Node is set-bandwidth.",
            "Exit Node is set-bandwidth.",
            "Exit Node is config.",
            "Exit Node is bandwidth.",
            "Entry Node is p2p-primary-paths.",
            "Entry Node is p2p-primary-path.",
            "Entry Node is name.",
            "Exit Node is name.",
            "Entry Node is config.",
            "Entry Node is name.",
            "Exit Node is name.",
            "Entry Node is use-cspf.",
            "Exit Node is use-cspf.",
            "Entry Node is named-explicit-path.",
            "Exit Node is named-explicit-path.",
            "Entry Node is named-path-constraint.",
            "Exit Node is named-path-constraint.",
            "Exit Node is config.",
            "Exit Node is p2p-primary-path.",
            "Exit Node is p2p-primary-paths.",
            "Exit Node is tunnel."
    };

    /**
     * Test anydata add to data node builder.
     */
    @Test
    public void atcnDataNodeTest() {

        TestYangSerializerContext context = new TestYangSerializerContext();
        DataNode.Builder dBlr = initializeDataNode(context);
        ModelObjectId id = new ModelObjectId.Builder()
                .addChild(DefaultConfigurationSchedules.class)
                .addChild(DefaultTarget.class, null)
                .addChild(DefaultDataValue.class)
                .build();
        ModelObjectId id1 = new ModelObjectId.Builder()
                .addChild(DefaultTe.class)
                .addChild(DefaultTunnels.class)
                .addChild(DefaultTunnel.class, null)
                .build();
        context.getRegistry().registerAnydataSchema(id, id1);
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

        dBlr = getTunnelBuilder(dBlr, "p2p");
        dBlr = getTunnelBuilder(dBlr, "p2p1");

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
     * Returns the data tree builder for tunnel.
     *
     * @param dBlr  data tree builder
     * @param value value
     * @return data tree builder
     */
    private static DataNode.Builder getTunnelBuilder(DataNode.Builder dBlr, String value) {
        String val = null;
        dBlr = addDataNode(dBlr, "tunnel", ACTN_TE, val, null);
        dBlr = addDataNode(dBlr, "name", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = null;
        dBlr = addDataNode(dBlr, "config", null, value, null);
        value = "p2p";
        dBlr = addDataNode(dBlr, "name", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "tunnel-p2p";
        dBlr = addDataNode(dBlr, "type", null, value, "actn-ietf-te-types",
                           null);
        dBlr = exitDataNode(dBlr);
        value = "19899";
        dBlr = addDataNode(dBlr, "identifier", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "OTN tunnel segment within the Huawei OTN Domain";
        dBlr = addDataNode(dBlr, "description", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "lsp-encoding-oduk";
        dBlr = addDataNode(dBlr, "encoding", null, value,
                           "actn-ietf-te-types", null);
        dBlr = exitDataNode(dBlr);
        value = "lsp-prot-unprotected";
        dBlr = addDataNode(dBlr, "protection-type", null, value,
                           "actn-ietf-te-types", null);
        dBlr = exitDataNode(dBlr);
        value = "state-up";
        dBlr = addDataNode(dBlr, "admin-status", null, value,
                           "actn-ietf-te-types", null);
        dBlr = exitDataNode(dBlr);
        value = "200";
        dBlr = addDataNode(dBlr, "provider-id", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "1000";
        dBlr = addDataNode(dBlr, "client-id", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "11";
        dBlr = addDataNode(dBlr, "te-topology-id", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "0.67.0.76";
        dBlr = addDataNode(dBlr, "source", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "0.67.0.77";
        dBlr = addDataNode(dBlr, "destination", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "7";
        dBlr = addDataNode(dBlr, "setup-priority", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "6";
        dBlr = addDataNode(dBlr, "hold-priority", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "path-signaling-rsvpte";
        dBlr = addDataNode(dBlr, "signaling-type", null, value,
                           "actn-ietf-te-types", null);
        dBlr = exitDataNode(dBlr);

        value = "transport";
        dBlr = addDataNode(dBlr, "payload-treatment", OTN_TUNN, value,
                           null, null);
        dBlr = exitDataNode(dBlr);
        value = "1";
        dBlr = addDataNode(dBlr, "src-tpn", OTN_TUNN, value, null);
        dBlr = exitDataNode(dBlr);
        value = "1";
        dBlr = addDataNode(dBlr, "src-tributary-slot-count", OTN_TUNN, value, null);
        dBlr = exitDataNode(dBlr);

        value = null;
        dBlr = addDataNode(dBlr, "src-tributary-slots", OTN_TUNN, value, null);
        value = "1";
        dBlr = addDataNode(dBlr, "values", null, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // src-tributary-slots
        value = "client-signal-10GbE-LAN";
        dBlr = addDataNode(dBlr, "src-client-signal", OTN_TUNN, value,
                           "yrt-ietf-transport-types", null);
        dBlr = exitDataNode(dBlr);
        value = "client-signal-ODU2e";
        dBlr = addDataNode(dBlr, "dst-client-signal", OTN_TUNN, value,
                           "yrt-ietf-transport-types", null);
        dBlr = exitDataNode(dBlr);
        value = "13";
        dBlr = addDataNode(dBlr, "dst-tpn", OTN_TUNN, value, null);
        dBlr = exitDataNode(dBlr);
        value = "1";
        dBlr = addDataNode(dBlr, "dst-tributary-slot-count", OTN_TUNN, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "dst-tributary-slots", OTN_TUNN, value, null);
        value = "1";
        dBlr = addDataNode(dBlr, "values", null, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // dst-tributary-slots

        dBlr = exitDataNode(dBlr); // config

        value = null;
        dBlr = addDataNode(dBlr, "bandwidth", null, value, null);
        dBlr = addDataNode(dBlr, "config", null, value, null);

        value = "SPECIFIED";
        dBlr = addDataNode(dBlr, "specification-type", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "10000000000";
        dBlr = addDataNode(dBlr, "set-bandwidth", null, value, null);
        dBlr = exitDataNode(dBlr);

        dBlr = exitDataNode(dBlr); // config
        dBlr = exitDataNode(dBlr); // bandwidth

        value = null;
        dBlr = addDataNode(dBlr, "p2p-primary-paths", null, value, null);
        dBlr = addDataNode(dBlr, "p2p-primary-path", null, value, null);

        value = "Primary path";
        dBlr = addDataNode(dBlr, "name", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "config", null, value, null);

        value = "Primary path";
        dBlr = addDataNode(dBlr, "name", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "true";
        dBlr = addDataNode(dBlr, "use-cspf", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "OTN-ERO-L0L1Service_lq_02_2b032409-8ec8-4b63-ab9e-6fa9365913f0";
        dBlr = addDataNode(dBlr, "named-explicit-path", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "OTN-PATH-CONSTRAINT-L0L1Service_lq_02_2b032409-8ec8-4b63-ab9e-6fa9365913f0";
        dBlr = addDataNode(dBlr, "named-path-constraint", null, value, null);
        dBlr = exitDataNode(dBlr);

        dBlr = exitDataNode(dBlr); //config
        dBlr = exitDataNode(dBlr); // p2p-primary-path
        dBlr = exitDataNode(dBlr); // p2p-primary-paths

        dBlr = exitDataNode(dBlr); // tunnel
        return dBlr;
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

        Iterator<DataNode> it5 = ((InnerNode) n).childNodes().values()
                .iterator();
        tunnelValidator(it5.next(), "p2p");

        n = it5.next();
        tunnelValidator(n, "p2p1");

        n = it1.next();
        validateDataNode(n, "schedules", ACTN_SCHD_NS, SINGLE_INSTANCE_NODE,
                         true, null);
        it1 = ((InnerNode) n).childNodes().values().iterator();
        n = it1.next();
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
    }

    /**
     * Validates the tunnel data tree.
     *
     * @param n1  data node n1
     * @param key tunnel instance key
     */
    private static void tunnelValidator(DataNode n1, String key) {
        DataNode n = n1;
        validateDataNode(n, "tunnel", ACTN_TE, MULTI_INSTANCE_NODE,
                         true, null);
        Iterator<KeyLeaf> keyIt = ((ListKey) n.key()).keyLeafs()
                .iterator();
        validateLeafDataNode(keyIt.next(), "name", ACTN_TE, key);

        Iterator<DataNode> it4 = ((InnerNode) n).childNodes().values().iterator();
        n = it4.next();
        validateDataNode(n, "name", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, key);
        n = it4.next();
        validateDataNode(n, "config", ACTN_TE, SINGLE_INSTANCE_NODE,
                         true, null);
        Iterator<DataNode> it3 = ((InnerNode) n).childNodes().values().iterator();
        n = it3.next();
        validateDataNode(n, "name", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "p2p");
        n = it3.next();
        validateDataNode(n, "type", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "tunnel-p2p");
        n = it3.next();
        validateDataNode(n, "identifier", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "19899");
        n = it3.next();
        validateDataNode(n, "description", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "OTN tunnel segment within the Huawei OTN Domain");
        n = it3.next();
        validateDataNode(n, "encoding", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "lsp-encoding-oduk");
        n = it3.next();
        validateDataNode(n, "protection-type", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "lsp-prot-unprotected");
        n = it3.next();
        validateDataNode(n, "admin-status", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "state-up");
        n = it3.next();
        validateDataNode(n, "provider-id", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "200");
        n = it3.next();
        validateDataNode(n, "client-id", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "1000");
        n = it3.next();
        validateDataNode(n, "te-topology-id", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "11");
        n = it3.next();
        validateDataNode(n, "source", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "0.67.0.76");
        n = it3.next();
        validateDataNode(n, "destination", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "0.67.0.77");
        n = it3.next();
        validateDataNode(n, "setup-priority", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "7");

        n = it3.next();
        validateDataNode(n, "hold-priority", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "6");
        n = it3.next();
        validateDataNode(n, "signaling-type", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "path-signaling-rsvpte");
        n = it3.next();
        validateDataNode(n, "payload-treatment", OTN_TUNN,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "transport");
        n = it3.next();
        validateDataNode(n, "src-tpn", OTN_TUNN, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "1");
        n = it3.next();
        validateDataNode(n, "src-tributary-slot-count", OTN_TUNN, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "1");
        it3.next();
        n = it3.next();
        validateDataNode(n, "src-client-signal", OTN_TUNN, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "client-signal-10GbE-LAN");
        n = it3.next();
        validateDataNode(n, "dst-client-signal", OTN_TUNN, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "client-signal-ODU2e");

        n = it3.next();
        validateDataNode(n, "dst-tpn", OTN_TUNN, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "13");
        n = it3.next();
        validateDataNode(n, "dst-tributary-slot-count", OTN_TUNN, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "1");
        n = it4.next();
        validateDataNode(n, "bandwidth", ACTN_TE, SINGLE_INSTANCE_NODE,
                         true, null);

        it3 = ((InnerNode) n).childNodes().values().iterator();
        n = it3.next();
        validateDataNode(n, "config", ACTN_TE, SINGLE_INSTANCE_NODE,
                         true, null);
        it3 = ((InnerNode) n).childNodes().values().iterator();
        n = it3.next();
        validateDataNode(n, "specification-type", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "SPECIFIED");
        n = it3.next();
        validateDataNode(n, "set-bandwidth", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "10000000000");

        n = it4.next();
        validateDataNode(n, "p2p-primary-paths", ACTN_TE, SINGLE_INSTANCE_NODE,
                         true, null);
        it3 = ((InnerNode) n).childNodes().values().iterator();
        n = it3.next();
        validateDataNode(n, "p2p-primary-path", ACTN_TE, MULTI_INSTANCE_NODE,
                         true, null);
        it3 = ((InnerNode) n).childNodes().values().iterator();
        n = it3.next();
        validateDataNode(n, "name", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "Primary path");
        n = it3.next();
        validateDataNode(n, "config", ACTN_TE, SINGLE_INSTANCE_NODE,
                         true, null);
        it3 = ((InnerNode) n).childNodes().values().iterator();
        n = it3.next();
        validateDataNode(n, "name", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "Primary path");
        n = it3.next();
        validateDataNode(n, "use-cspf", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "true");
        n = it3.next();
        validateDataNode(n, "named-explicit-path", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "OTN-ERO-L0L1Service_lq_02_2b032409-8ec8-4b63-ab9e-6fa9365913f0");
        n = it3.next();
        validateDataNode(n, "named-path-constraint", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false,
                         "OTN-PATH-CONSTRAINT-L0L1Service_lq_02_2b032409-8ec8-4b63-ab9e-6fa9365913f0");

        walkINTree(n1, EXPECTED);
    }
}
