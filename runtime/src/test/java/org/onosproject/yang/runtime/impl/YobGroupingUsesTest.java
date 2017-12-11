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
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.DefaultNetworks;
import org.onosproject.yang.gen.v1.yrtietfschedule.rev20160301.yrtietfschedule.schedules.schedules.Schedule;
import org.onosproject.yang.gen.v1.yrtietfte.rev20170310.yrtietfte.DefaultTe;
import org.onosproject.yang.gen.v1.yrtietfte.rev20170310.yrtietfte.tunnelp2pproperties.DefaultState;
import org.onosproject.yang.gen.v1.yrtietftetopology.rev20160317.yrtietftetopology.networks.DefaultAugmentedNwNetworks;
import org.onosproject.yang.gen.v1.yrtietftetopology.rev20160317.yrtietftetopology.tetopologiesaugment.te.templates.LinkTemplate;
import org.onosproject.yang.gen.v1.ytbrpc.rev20160826.ytbrpc.content.DefaultContentInput;
import org.onosproject.yang.gen.v1.ytbrpc.rev20160826.ytbrpc.content.DefaultContentOutput;
import org.onosproject.yang.gen.v1.ytbrpc.rev20160826.ytbrpc.content.contentinput.In;
import org.onosproject.yang.gen.v1.ytbrpc.rev20160826.ytbrpc.content.contentinput.InTypedef;
import org.onosproject.yang.gen.v1.ytbrpc.rev20160826.ytbrpc.content.contentoutput.outch.First;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.addToResourceId;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeResourceId;

/**
 * Tests the YANG object building for the YANG data tree based on the non
 * schema augmented nodes.
 */
public class YobGroupingUsesTest {

    private static final String NW_NS = "urn:ietf:params:xml:ns:yang:yrt-ietf-network";
    private static final String TE_NS = "urn:ietf:params:xml:ns:yang:yrt-ietf-te-topology";
    private static final String TE = "urn:ietf:params:xml:ns:yang:ietf-te";
    private static final String RPC_NS = "yms:test:ytb:ytb:rpc";
    private static final String DURATION_TIME =
            "P9243983843671636195325300973389215743Y441498414397233640755" +
                    "748678783585602855222287242483601612720MT42429291980" +
                    "9198503801168838636847720848117391695063559947287623" +
                    "02419062212H5803675020235950787S";
    private static final String REPEAT_INTERVAL =
            "R006733702120616055381223229783008865740416144969234/P988414" +
                    "1350640084815248711164804331178923236350109520293255" +
                    "5007727609859241946405817769469072177M30614037188965" +
                    "5752702322778693919891230979781747844670759083567190" +
                    "85127201004965309450WT393H91819305489488863421529096" +
                    "58695920717372841701557104867M992396486515481883800S";
    TestYangSerializerContext context = new TestYangSerializerContext();
    private DataNode.Builder dBlr;
    private String value;
    private ResourceId.Builder rIdBlr;


    public DataNode buildDataNodeForInterFileGrouping() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "networks", NW_NS, value, null);
        dBlr = addDataNode(dBlr, "te", TE_NS, value, null);
        dBlr = addDataNode(dBlr, "templates", TE_NS, value, null);
        dBlr = addDataNode(dBlr, "link-template", TE_NS, value, null);
        value = "name";
        dBlr = addDataNode(dBlr, "name", TE_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "te-link-attributes", TE_NS, value, null);
        dBlr = addDataNode(dBlr, "schedules", TE_NS, value, null);
        dBlr = addDataNode(dBlr, "schedule", TE_NS, value, null);
        value = "100";
        dBlr = addDataNode(dBlr, "schedule-id", TE_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "start";
        String time = "2000-06-12T06:23:21.52Z";
        dBlr = addDataNode(dBlr, "start", TE_NS, time, null);
        dBlr = exitDataNode(dBlr);
        dBlr = addDataNode(dBlr, "schedule-duration", TE_NS, DURATION_TIME, null);
        dBlr = exitDataNode(dBlr);
        value = "repeat-interval";
        dBlr = addDataNode(dBlr, "repeat-interval", TE_NS, REPEAT_INTERVAL, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // schedule
        dBlr = exitDataNode(dBlr); // schedules
        dBlr = exitDataNode(dBlr); // te-link-attributes
        dBlr = exitDataNode(dBlr); // link-template
        dBlr = exitDataNode(dBlr); // templates
        dBlr = exitDataNode(dBlr); // te
        dBlr = exitDataNode(dBlr); // networks
        return dBlr.build();
    }

    @Test
    public void testInterFileGrouping() {
        DataNode dataNode = buildDataNodeForInterFileGrouping();
        ResourceData data = DefaultResourceData.builder().addDataNode(dataNode).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultNetworks networks = ((DefaultNetworks) modelObject);
        DefaultAugmentedNwNetworks augNws = networks
                .augmentation(DefaultAugmentedNwNetworks.class);
        LinkTemplate linkTmp = augNws.te().templates().linkTemplate().get(0);
        assertThat(linkTmp.name().toString(), is("name"));
        Schedule sh = linkTmp.teLinkAttributes().schedules().schedule().get(0);
        assertThat(sh.scheduleId(), is(100L));
        assertThat(sh.start().toString(), is("2000-06-12T06:23:21.52Z"));
        assertThat(sh.scheduleDuration(), is(DURATION_TIME));
        assertThat(sh.repeatInterval(), is(REPEAT_INTERVAL));
    }

    private DataNode buildDataNodeWithIdentityRef() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "te", TE, value, null);
        dBlr = addDataNode(dBlr, "tunnels", TE, value, null);
        dBlr = addDataNode(dBlr, "tunnel", TE, value, null);
        value = "name";
        dBlr = addDataNode(dBlr, "name", TE, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "state", TE, value, null);
        value = "statename";
        dBlr = addDataNode(dBlr, "name", TE, value, null);
        dBlr = exitDataNode(dBlr);
        value = "tunnel-p2p";
        dBlr = addDataNode(dBlr, "type", TE, value,
                           "yrt-ietf-te-types",
                           null);
        dBlr = exitDataNode(dBlr); // tunnel-p2p
        dBlr = exitDataNode(dBlr); // state
        dBlr = exitDataNode(dBlr); // tunnel
        dBlr = exitDataNode(dBlr); // tunnels
        dBlr = exitDataNode(dBlr); // te
        return dBlr.build();
    }

    private ResourceData.Builder buildDataNodeWithInput() {
        value = null;
        rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "content", RPC_NS, value);
        dBlr = initializeDataNode(rIdBlr);
        dBlr = addDataNode(dBlr, "input", RPC_NS, value, null);
        dBlr = addDataNode(dBlr, "in", RPC_NS, value, null);
        value = "name";
        dBlr = addDataNode(dBlr, "con-in", RPC_NS, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        return DefaultResourceData.builder().addDataNode(dBlr.build())
                .resourceId(rIdBlr.build());
    }

    private ResourceData.Builder buildDataNodeWithOutput() {
        value = null;
        rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "content", RPC_NS, value);
        dBlr = initializeDataNode(rIdBlr);
        dBlr = addDataNode(dBlr, "output", RPC_NS, value, null);
        value = "8";
        dBlr = addDataNode(dBlr, "call", RPC_NS, value, null);
        dBlr = exitDataNode(dBlr);
        return DefaultResourceData.builder().addDataNode(dBlr.build())
                .resourceId(rIdBlr.build());
    }

    /**
     * Unit test for rpc input.
     */
    @Test
    public void testRpcInput() {
        ResourceData.Builder data = buildDataNodeWithInput();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data.build());

        List<ModelObject> objects = modelObjectData.modelObjects();
        ModelObject obj = objects.get(0);
        DefaultContentInput in = ((DefaultContentInput) obj);
        In input = in.in();
        InTypedef leaf = input.conIn();
        assertThat(leaf.string(), is("name"));
    }

    /**
     * Unit test for rpc output.
     */
    @Test
    public void testRpcOutput() {
        ResourceData.Builder data = buildDataNodeWithOutput();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data.build());
        List<ModelObject> objects = modelObjectData.modelObjects();
        ModelObject obj = objects.get(0);
        DefaultContentOutput out = ((DefaultContentOutput) obj);
        First first = (First) out.outCh();
        List<Short> call = first.call();
        assertThat(call.get(0), is((short) 8));
    }

    /**
     * Unit test for identity-ref.
     */
    @Test
    public void testIdentityRef() {
        DataNode dataNode = buildDataNodeWithIdentityRef();
        ResourceData data = DefaultResourceData.builder().
                addDataNode(dataNode).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultTe te = ((DefaultTe) modelObject);
        DefaultState state = ((DefaultState) te.tunnels()
                .tunnel().get(0).state());
        assertThat(state.name().toString(), is("statename"));
        assertThat(state.type().getSimpleName().toString(), is("TunnelP2p"));
    }
}
