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
import org.onosproject.yang.gen.v1.yrtietfyangtypes.rev20130715.yrtietfyangtypes.DateAndTime;
import org.onosproject.yang.gen.v1.yrtietfyangtypes.rev20130715.yrtietfyangtypes.Xpath10;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.DefaultConfigurationSchedules;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.Operation;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.configurationschedules.DefaultTarget;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.configurationschedules.target.DefaultDataValue;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.schedules.DefaultSchedules;
import org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.schedules.schedules.DefaultSchedule;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelp2pproperties.DefaultConfig;
import org.onosproject.yang.gen.v11.actnietfte.rev20170310.actnietfte.tunnelsgrouping.tunnels.DefaultTunnel;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultModelObjectData.Builder;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;

import java.util.List;

import static org.onosproject.yang.gen.v11.actnietfschedule.rev20170306.actnietfschedule.operation.OperationEnum.CONFIGURE;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;
import static org.onosproject.yang.runtime.impl.serializerhelper.ActnIetfNetAnydataTest.validateDataNodeTree;

/**
 * Unit test cases for resource id conversion from model object id.
 */
public class YtbAtcnAnydataTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ResourceData rscData;
    private DefaultDataTreeBuilder treeBuilder;
    private ResourceId id;
    private List<NodeKey> keys;
    private SchemaId sid;
    private ModelObjectId mid;
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

        reg.registerAnydataSchema(
                DefaultDataValue.class, DefaultTunnel.class);
        DefaultConfigurationSchedules c1 = new DefaultConfigurationSchedules();
        DefaultTarget target = new DefaultTarget();
        target.object(new Xpath10("te-links"));
        target.operation(new Operation(CONFIGURE));

        DefaultTunnel tunnel = new DefaultTunnel();
        tunnel.name("p2p");

        DefaultConfig config = new DefaultConfig();
        config.name("p2p");

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
}
