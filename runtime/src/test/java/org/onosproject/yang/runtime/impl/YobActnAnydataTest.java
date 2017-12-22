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
import org.onosproject.yang.gen.v1.actnietftetypes.rev20170310.actnietftetypes.LspEncodingOduk;
import org.onosproject.yang.gen.v1.actnietftetypes.rev20170310.actnietftetypes.LspProtUnprotected;
import org.onosproject.yang.gen.v1.actnietftetypes.rev20170310.actnietftetypes.PathSignalingRsvpte;
import org.onosproject.yang.gen.v1.actnietftetypes.rev20170310.actnietftetypes.StateUp;
import org.onosproject.yang.gen.v1.actnietftetypes.rev20170310.actnietftetypes.TunnelP2p;
import org.onosproject.yang.gen.v1.yrtietftransporttypes.rev20161025.yrtietftransporttypes.ClientSignal10GbElan;
import org.onosproject.yang.gen.v1.yrtietftransporttypes.rev20161025.yrtietftransporttypes.ClientSignalOdu2e;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.DefaultConfigurationSchedules;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.Operation;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.configurationschedules.DefaultTarget;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.configurationschedules.target.DefaultDataValue;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.schedules.Schedules;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.schedules.schedules.Schedule;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.DefaultTe;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tetunnelbandwidthtop.Bandwidth;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelp2pproperties.Config;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelp2pproperties.p2pprimarypaths.P2PprimaryPath;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelsgrouping.DefaultTunnels;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelsgrouping.tunnels.DefaultTunnel;
import org.onosproject.yang.gen.v11.yrtietfotntunnel.rev20170311.yrtietfotntunnel.te.tunnels.tunnel.config.DefaultAugmentedTeConfig;
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
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.serializerhelper.ActnIetfNetAnydataTest.actnDataTree;

/**
 * Tests the YANG object building for the YANG data nodes based on the non
 * schema augmented nodes.
 */
public class YobActnAnydataTest {
    private static final String NW_TOPO_NAME_SPACE = "urn:ietf:params:xml:ns:yang:yrt-ietf-network-topology";
    TestYangSerializerContext context = new TestYangSerializerContext();

    @Test
    public void anydataTest() {
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
        DataNode dataNode = actnDataTree(dBlr);
        ResourceData data = DefaultResourceData.builder()
                .addDataNode(dataNode).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(context.getRegistry());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultConfigurationSchedules c1 = ((DefaultConfigurationSchedules) modelObject);
        DefaultTarget target = ((DefaultTarget) c1.target().get(0));
        assertThat(target.object().toString(), is("te-links"));

        Operation obj = target.operation();
        assertThat(obj.enumeration().toString(), is("configure"));

        DefaultDataValue dataValue = (DefaultDataValue) target.dataValue();
        List<InnerModelObject> tunnel = dataValue.anydata(DefaultTunnel.class);
        assertThat(((DefaultTunnel) tunnel.get(0)).name().toString(), is("p2p"));

        Config config = ((DefaultTunnel) tunnel.get(0)).config();
        assertThat(config.name().toString(), is("p2p"));
        assertThat(config.type().toString(), is(TunnelP2p.class.toString()));
        assertThat(config.identifier(), is(19899));
        assertThat(config.description().toString(),
                   is("OTN tunnel segment within the Huawei OTN Domain"));
        assertThat(config.encoding().toString(), is(LspEncodingOduk.class.toString()));
        assertThat(config.protectionType().toString(), is(LspProtUnprotected.class.toString()));
        assertThat(config.adminStatus().toString(), is(StateUp.class.toString()));

        assertThat(config.providerId().toString(), is("200"));
        assertThat(config.clientId().toString(), is("1000"));
        assertThat(config.teTopologyId().toString(), is("11"));
        assertThat(config.source().toString(), is("0.67.0.76"));
        assertThat(config.destination().toString(), is("0.67.0.77"));
        assertThat(config.setupPriority(), is(((short) 7)));
        assertThat(config.holdPriority(), is(((short) 6)));
        assertThat(config.signalingType().toString(), is(PathSignalingRsvpte.class.toString()));
        DefaultAugmentedTeConfig cong = config.augmentation(DefaultAugmentedTeConfig.class);
        assertThat(cong.payloadTreatment().toString(), is("transport"));
        assertThat(cong.srcTpn(), is(1));
        assertThat(cong.srcTributarySlotCount(), is(1));
        assertThat(cong.srcTributarySlots().values().get(0), is(((short) 1)));

        assertThat(cong.srcClientSignal().toString(), is(ClientSignal10GbElan.class.toString()));
        assertThat(cong.dstClientSignal().toString(), is(ClientSignalOdu2e.class.toString()));
        assertThat(cong.dstTpn(), is(13));
        assertThat(cong.dstTributarySlotCount(), is(1));
        assertThat(cong.dstTributarySlots().values().get(0), is(((short) 1)));

        Bandwidth bandwidth = ((DefaultTunnel) tunnel.get(0)).bandwidth();
        assertThat(bandwidth.config().specificationType().toString(), is("SPECIFIED"));
        assertThat(bandwidth.config().setBandwidth().toString(), is("10000000000"));

        P2PprimaryPath path = ((DefaultTunnel) tunnel.get(0)).p2PprimaryPaths()
                .p2PprimaryPath().get(0);
        assertThat(path.name(), is("Primary path"));
        assertThat(path.config().name(), is("Primary path"));
        assertThat(path.config().useCspf(), is(true));
        assertThat(path.config().namedExplicitPath().toString(),
                   is("OTN-ERO-L0L1Service_lq_02_2b032409-8ec8-4b63-ab9e-6fa9365913f0"));
        assertThat(path.config().namedPathConstraint().toString(),
                   is("OTN-PATH-CONSTRAINT-L0L1Service_lq_02_2b032409-8ec8-4b63-ab9e-6fa9365913f0"));

        Schedules schedules = target.schedules();
        Schedule schedule = schedules.schedule().get(0);
        assertThat(((Long) schedule.scheduleId()).toString(), is("11"));
        assertThat(schedule.start().toString(), is("2016-09-12T23:20:50.52Z"));
        assertThat(schedule.scheduleDuration().toString(), is("PT108850373514M"));
    }
}
