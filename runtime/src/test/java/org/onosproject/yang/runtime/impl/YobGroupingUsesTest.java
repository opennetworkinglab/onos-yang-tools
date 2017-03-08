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

package org.onosproject.yang.runtime.impl;

import org.junit.Test;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.network.rev20151208.yrtietfnetwork.DefaultNetworks;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.schedule.rev20160301.yrtietfschedule.schedules.schedules.Schedule;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.te.topology.rev20160317.yrtietftetopology.networks.DefaultAugmentedNwNetworks;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.te.topology.rev20160317.yrtietftetopology.tetopologiesaugment.te.templates.LinkTemplate;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.runtime.DefaultResourceData;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.initializeDataNode;

/**
 * Tests the YANG object building for the YANG data tree based on the non
 * schema augmented nodes.
 */
public class YobGroupingUsesTest {
    TestYangSerializerContext context = new TestYangSerializerContext();
    private static final String NW_NS = "urn:ietf:params:xml:ns:yang:yrt-ietf-network";
    private static final String TE_NS = "urn:ietf:params:xml:ns:yang:yrt-ietf-te-topology";
    private DataNode.Builder dBlr;
    private String value;


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
        dBlr = addDataNode(dBlr, "start", TE_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "schedule-duration";
        dBlr = addDataNode(dBlr, "schedule-duration", TE_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "repeat-interval";
        dBlr = addDataNode(dBlr, "repeat-interval", TE_NS, value, null);
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
        assertThat(sh.start().toString(), is("start"));
        assertThat(sh.scheduleDuration(), is("schedule-duration"));
        assertThat(sh.repeatInterval(), is("repeat-interval"));
    }
}
