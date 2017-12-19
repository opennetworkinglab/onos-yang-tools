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
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.DefaultConfigurationSchedules;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.Operation;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.configurationschedules.DefaultTarget;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.configurationschedules.target.DefaultDataValue;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.schedules.Schedules;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.schedules.schedules.Schedule;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelp2pproperties.Config;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelsgrouping.tunnels.DefaultTunnel;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ResourceData;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.serializerhelper.ActnIetfNetAnydataTest.actnDataTree;
import static org.onosproject.yang.runtime.impl.serializerhelper.ActnIetfNetAnydataTest.validateDataNodeTree;

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
        context.getRegistry().registerAnydataSchema(
                DefaultDataValue.class, DefaultTunnel.class);
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
        DefaultTunnel tunnel = dataValue.anydata(DefaultTunnel.class);
        assertThat(tunnel.name().toString(), is("p2p"));

        Config config = tunnel.config();
        assertThat(config.name().toString(), is("p2p"));

        Schedules schedules = target.schedules();
        Schedule schedule = schedules.schedule().get(0);
        assertThat(((Long) schedule.scheduleId()).toString(), is("11"));
        assertThat(schedule.start().toString(), is("2016-09-12T23:20:50.52Z"));
        assertThat(schedule.scheduleDuration().toString(), is("PT108850373514M"));

        validateDataNodeTree(dataNode);
    }
}
