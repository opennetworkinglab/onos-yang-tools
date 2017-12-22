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
import org.onosproject.yang.gen.v1.actnietftetypes.rev20170310.actnietftetypes.LspEncodingOduk;
import org.onosproject.yang.gen.v1.actnietftetypes.rev20170310.actnietftetypes.LspProtUnprotected;
import org.onosproject.yang.gen.v1.actnietftetypes.rev20170310.actnietftetypes.PathSignalingRsvpte;
import org.onosproject.yang.gen.v1.actnietftetypes.rev20170310.actnietftetypes.StateUp;
import org.onosproject.yang.gen.v1.actnietftetypes.rev20170310.actnietftetypes.TeGlobalId;
import org.onosproject.yang.gen.v1.actnietftetypes.rev20170310.actnietftetypes.TeTopologyId;
import org.onosproject.yang.gen.v1.actnietftetypes.rev20170310.actnietftetypes.TunnelP2p;
import org.onosproject.yang.gen.v1.yrtietfinettypes.rev20130715.yrtietfinettypes.IpAddress;
import org.onosproject.yang.gen.v1.yrtietfinettypes.rev20130715.yrtietfinettypes.Ipv4Address;
import org.onosproject.yang.gen.v1.yrtietfinettypes.rev20130715.yrtietfinettypes.ipaddress.IpAddressUnion;
import org.onosproject.yang.gen.v1.yrtietftemplstypes.rev20170310.yrtietftemplstypes.BandwidthKbps;
import org.onosproject.yang.gen.v1.yrtietftemplstypes.rev20170310.yrtietftemplstypes.TeBandwidthType;
import org.onosproject.yang.gen.v1.yrtietftransporttypes.rev20161025.yrtietftransporttypes.ClientSignal10GbElan;
import org.onosproject.yang.gen.v1.yrtietftransporttypes.rev20161025.yrtietftransporttypes.ClientSignalOdu2e;
import org.onosproject.yang.gen.v1.yrtietfyangtypes.rev20130715.yrtietfyangtypes.DateAndTime;
import org.onosproject.yang.gen.v1.yrtietfyangtypes.rev20130715.yrtietfyangtypes.Xpath10;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.DefaultConfigurationSchedules;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.Operation;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.configurationschedules.DefaultTarget;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.configurationschedules.target.DefaultDataValue;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.schedules.DefaultSchedules;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.schedules.schedules.DefaultSchedule;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.DefaultTe;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tetunnelbandwidthtop.Bandwidth;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tetunnelbandwidthtop.DefaultBandwidth;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelp2pproperties.DefaultConfig;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelp2pproperties.DefaultP2PprimaryPaths;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelp2pproperties.p2pprimarypaths.DefaultP2PprimaryPath;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelsgrouping.DefaultTunnels;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelsgrouping.tunnels.DefaultTunnel;
import org.onosproject.yang.gen.v11.yrtietfotntunnel.rev20170311.yrtietfotntunnel.otntunnelendpoint.DefaultDstTributarySlots;
import org.onosproject.yang.gen.v11.yrtietfotntunnel.rev20170311.yrtietfotntunnel.otntunnelendpoint.DefaultSrcTributarySlots;
import org.onosproject.yang.gen.v11.yrtietfotntunnel.rev20170311.yrtietfotntunnel.te.tunnels.tunnel.config.DefaultAugmentedTeConfig;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultModelObjectData.Builder;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.ResourceData;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import static org.onosproject.yang.gen.v1.yrtietftemplstypes.rev20170310.yrtietftemplstypes.tebandwidthtype.TeBandwidthTypeEnum.SPECIFIED;
import static org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.operation.OperationEnum.CONFIGURE;
import static org.onosproject.yang.gen.v11.yrtietfotntunnel.rev20170311.yrtietfotntunnel.otntunnelendpoint.PayloadTreatmentEnum.TRANSPORT;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;
import static org.onosproject.yang.runtime.impl.TestUtils.ACTN_SCHD_NS;
import static org.onosproject.yang.runtime.impl.TestUtils.ACTN_TE;
import static org.onosproject.yang.runtime.impl.TestUtils.OTN_TUNN;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateLeafDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.walkINTree;

/**
 * Unit test cases for resource id conversion from model object id.
 */
public class YtbAtcnAnydataTest {

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
            "Entry Node is src-client-signal.",
            "Exit Node is src-client-signal.",
            "Entry Node is src-tpn.",
            "Exit Node is src-tpn.",
            "Entry Node is src-tributary-slot-count.",
            "Exit Node is src-tributary-slot-count.",
            "Entry Node is dst-client-signal.",
            "Exit Node is dst-client-signal.",
            "Entry Node is dst-tpn.",
            "Exit Node is dst-tpn.",
            "Entry Node is dst-tributary-slot-count.",
            "Exit Node is dst-tributary-slot-count.",
            "Entry Node is src-tributary-slots.",
            "Entry Node is values.",
            "Exit Node is values.",
            "Exit Node is src-tributary-slots.",
            "Entry Node is dst-tributary-slots.",
            "Entry Node is values.",
            "Exit Node is values.",
            "Exit Node is dst-tributary-slots.",
            "Exit Node is config.",
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
            "Entry Node is bandwidth.",
            "Entry Node is config.",
            "Entry Node is specification-type.",
            "Exit Node is specification-type.",
            "Entry Node is set-bandwidth.",
            "Exit Node is set-bandwidth.",
            "Exit Node is config.",
            "Exit Node is bandwidth.",
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ResourceData rscData;
    private DefaultDataTreeBuilder treeBuilder;
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
                .addChild(DefaultConfigurationSchedules.class)
                .addChild(DefaultTarget.class, null)
                .addChild(DefaultDataValue.class)
                .build();
        ModelObjectId id1 = new ModelObjectId.Builder()
                .addChild(DefaultTe.class)
                .addChild(DefaultTunnels.class)
                .addChild(DefaultTunnel.class, null)
                .build();
        reg.registerAnydataSchema(id, id1);
        DefaultConfigurationSchedules c1 = new DefaultConfigurationSchedules();
        DefaultTarget target = new DefaultTarget();
        target.object(new Xpath10("te-links"));
        target.operation(new Operation(CONFIGURE));

        DefaultTunnel tunnel = new DefaultTunnel();
        tunnel.name("p2p");

        DefaultConfig config = new DefaultConfig();
        config.name("p2p");
        config.type(TunnelP2p.class);
        config.identifier(19899);
        config.description("OTN tunnel segment within the Huawei OTN Domain");
        config.encoding(LspEncodingOduk.class);
        config.protectionType(LspProtUnprotected.class);
        config.adminStatus(StateUp.class);
        config.providerId(new TeGlobalId(200));
        config.clientId(new TeGlobalId(1000));
        config.teTopologyId(new TeTopologyId("11"));
        config.source(new IpAddress(
                new IpAddressUnion(new Ipv4Address("0.67.0.76"))));
        config.destination(new IpAddress(
                new IpAddressUnion(new Ipv4Address("0.67.0.77"))));
        config.setupPriority(((short) 7));
        config.holdPriority(((short) 6));
        config.signalingType(PathSignalingRsvpte.class);

        DefaultAugmentedTeConfig cong = new DefaultAugmentedTeConfig();
        cong.payloadTreatment(TRANSPORT);
        cong.srcTpn(1);
        cong.srcClientSignal(ClientSignal10GbElan.class);
        cong.srcTributarySlotCount(1);
        DefaultSrcTributarySlots srcTributarySlots = new
                DefaultSrcTributarySlots();
        srcTributarySlots.addToValues((short) 1);
        cong.srcTributarySlots(srcTributarySlots);
        cong.dstTpn(13);
        cong.dstTributarySlotCount(1);
        DefaultDstTributarySlots dstTributarySlots = new
                DefaultDstTributarySlots();
        dstTributarySlots.addToValues((short) 1);
        cong.dstTributarySlots(dstTributarySlots);
        cong.dstTributarySlots().addToValues((short) 1);
        cong.dstClientSignal(ClientSignalOdu2e.class);
        config.addAugmentation(cong);

        Bandwidth bandwidth = new DefaultBandwidth();
        org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte
                .tetunnelbandwidthtop.bandwidth.DefaultConfig confg = new org
                .onosproject.yang.gen.v11.actnietfte.rev20170310
                .actnietfte.tetunnelbandwidthtop.bandwidth.DefaultConfig();
        confg.specificationType(new TeBandwidthType(SPECIFIED));
        BigInteger i = new BigInteger("10000000000");
        confg.setBandwidth(new BandwidthKbps(i));
        bandwidth.config(confg);
        tunnel.bandwidth(bandwidth);

        DefaultP2PprimaryPath p2PprimaryPath = new DefaultP2PprimaryPath();
        p2PprimaryPath.name("Primary path");
        org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte
                .p2pprimarypathproperties.DefaultConfig cc =
                new org.onosproject.yang.gen.v11.actnietfte.rev20170310
                        .actnietfte.p2pprimarypathproperties.DefaultConfig();

        cc.name("Primary path");
        cc.useCspf(true);
        cc.namedExplicitPath("OTN-ERO-L0L1Service_lq_02_2b032409-8ec8-4b63" +
                                     "-ab9e-6fa9365913f0");
        cc.namedPathConstraint("OTN-PATH-CONSTRAINT-L0L1Service" +
                                       "_lq_02_2b032409-8ec8-4b63-ab9e" +
                                       "-6fa9365913f0");
        p2PprimaryPath.config(cc);

        DefaultP2PprimaryPaths p2PprimaryPaths = new DefaultP2PprimaryPaths();
        p2PprimaryPaths.addToP2PprimaryPath(p2PprimaryPath);
        tunnel.p2PprimaryPaths(p2PprimaryPaths);

        tunnel.config(config);
        DefaultDataValue dataValue = new DefaultDataValue();
        dataValue.addAnydata(tunnel);

        DefaultSchedules schedules = new DefaultSchedules();
        DefaultSchedule schedule = new DefaultSchedule();
        schedule.scheduleDuration("PT108850373514M");
        schedule.start(DateAndTime.of("2016-09-12T23:20:50.52Z"));

        long val = 11;
        schedule.scheduleId(val);
        schedules.addToSchedule(schedule);
        target.schedules(schedules);

        target.dataValue(dataValue);
        c1.addToTarget(target);
        data = new Builder();
        data.addModelObject(c1);
        rscData = treeBuilder.getResourceData(data.build());

        List<DataNode> nodes = rscData.dataNodes();
        DataNode n = nodes.get(0);

        validateDataNodeTree(n);
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

        Iterator<DataNode> it4 = ((InnerNode) n).childNodes().values()
                .iterator();
        n = it4.next();
        validateDataNode(n, "tunnel", ACTN_TE, MULTI_INSTANCE_NODE,
                         true, null);
        keyIt = ((ListKey) n.key()).keyLeafs().iterator();
        validateLeafDataNode(keyIt.next(), "name", ACTN_TE, "p2p");

        it4 = ((InnerNode) n).childNodes().values().iterator();
        n = it4.next();
        validateDataNode(n, "name", ACTN_TE, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "p2p");
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
        validateDataNode(n, "src-client-signal", OTN_TUNN, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "client-signal-10GbE-LAN");
        n = it3.next();
        validateDataNode(n, "src-tpn", OTN_TUNN, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "1");
        n = it3.next();
        validateDataNode(n, "src-tributary-slot-count", OTN_TUNN, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "1");

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
                         false, "OTN-PATH-CONSTRAINT-L0L1Service_lq_02_2b032409-8ec8-4b63-ab9e-6fa9365913f0");

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

        walkINTree(node, EXPECTED);
    }
}
