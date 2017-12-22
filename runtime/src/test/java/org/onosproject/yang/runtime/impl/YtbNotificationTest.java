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
import org.onosproject.yang.gen.v1.event.event.DefaultEvent;
import org.onosproject.yang.gen.v1.event.event.event.DefaultC;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultModelObjectData.Builder;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;

import java.util.Iterator;
import java.util.List;

import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;
import static org.onosproject.yang.runtime.impl.TestUtils.NOTIF_NS;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;

/**
 * Unit test cases for resource id conversion from model object id.
 */
public class YtbNotificationTest {

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
     * Processes notification with container node as child.
     */
    @Test
    public void processNotificationTest() {

        DefaultC c = new DefaultC();
        c.eventClass("xyz");
        DefaultEvent event = new DefaultEvent();
        event.c(c);
        data = new Builder();
        data.addModelObject(event);
        rscData = treeBuilder.getResourceData(data.build());

        List<DataNode> nodes = rscData.dataNodes();
        DataNode n = nodes.get(0);
        validateDataNode(n, "event", NOTIF_NS,
                         SINGLE_INSTANCE_NODE, true, null);
        Iterator<DataNode> it = ((InnerNode) n).childNodes().values().iterator();
        n = it.next();
        validateDataNode(n, "c", NOTIF_NS, SINGLE_INSTANCE_NODE,
                         true, null);

        Iterator<DataNode> it1 = ((InnerNode) n).childNodes().values()
                .iterator();
        n = it1.next();
        validateDataNode(n, "event-class", NOTIF_NS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "xyz");
    }
}
