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
import org.onosproject.yang.gen.v1.check.check.DefaultList52;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.DefaultNetworks;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.DefaultNetwork;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.network.DefaultNode;
import org.onosproject.yang.gen.v11.anytest.rev20160624.AnyTestOpParam;
import org.onosproject.yang.gen.v11.anytest.rev20160624.anytest.DefaultC1;
import org.onosproject.yang.gen.v11.anytest.rev20160624.anytest.c1.DefaultMydata2;
import org.onosproject.yang.gen.v11.listanydata.rev20160624.listanydata.DefaultL1;
import org.onosproject.yang.gen.v11.listanydata.rev20160624.listanydata.l1.DefaultMydata;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import static org.junit.Assert.assertEquals;
import static org.onosproject.yang.runtime.impl.UtilsConstants.FMT_INV;

/**
 * Tests the serializer helper methods.
 */
public class AnydataNegativeScenarioTest {

    TestYangSerializerContext context = new TestYangSerializerContext();

    /*
     * Reference for data node builder.
     */
    DataNode.Builder dBlr;

    /**
     * Test anydata add to data node negative test scenario when given
     * referenced node is not of type anydata.
     */
    @Test
    public void addToDataTest() {
        boolean isExpOccurred = false;
        context.getContext();
        ModelObjectId id1 = null;
        try {
            id1 = new ModelObjectId.Builder()
                    .addChild(DefaultNetworks.class)
                    .addChild(DefaultNetwork.class, null)
                    .addChild(DefaultNode.class, null)
                    .build();
            context.getRegistry().registerAnydataSchema(id1, id1);
        } catch (IllegalArgumentException e) {
            isExpOccurred = true;
            assertEquals(e.getMessage(), String.format(FMT_INV, id1));
        }
        assertEquals(isExpOccurred, true);
    }

    /**
     * Test anydata add to data node negative test scenario when given
     * referenced node module is not registered.
     */
    @Test
    public void addToData2Test() {
        boolean isExpOccurred = false;
        context.getContext();
        ModelObjectId id1 = null;
        try {
            ModelObjectId id = new ModelObjectId.Builder()
                    .addChild(DefaultC1.class).addChild(DefaultMydata2.class)
                    .build();
            id1 = new ModelObjectId.Builder()
                    .addChild(AnyTestOpParam.class)
                    .build();
            context.getRegistry().registerAnydataSchema(id, id1);
        } catch (IllegalArgumentException e) {
            isExpOccurred = true;
            assertEquals(e.getMessage(), String.format(
                    FMT_INV, id1));
        }
        assertEquals(isExpOccurred, true);
    }


    /**
     * Test anydata add to data node negative test scenario when given
     * referenced node is not of type list/container.
     */
    @Test
    public void addToData3Test() {
        boolean isExpOccurred = false;
        context.getContext();
        ModelObjectId id = null;
        try {
            id = new ModelObjectId.Builder()
                    .addChild(AnyTestOpParam.class)
                    .build();
            ModelObjectId id1 = new ModelObjectId.Builder()
                    .addChild(DefaultL1.class, null)
                    .addChild(DefaultMydata.class)
                    .build();
            context.getRegistry().registerAnydataSchema(id1, id);
        } catch (IllegalArgumentException e) {
            isExpOccurred = true;
            assertEquals(e.getMessage(), String.format(FMT_INV, id));
        }
        assertEquals(isExpOccurred, true);
    }

    /**
     * Test anydata add to data node negative test scenario when given
     * referenced node is not of type list/container.
     */
    @Test
    public void addToData4Test() {
        boolean isExpOccurred = false;
        context.getContext();
        ModelObjectId id = null;
        try {
            id = new ModelObjectId.Builder()
                    .addChild(DefaultList52.class)
                    .build();
            ModelObjectId id1 = new ModelObjectId.Builder()
                    .addChild(DefaultL1.class, null)
                    .addChild(DefaultMydata.class)
                    .build();
            context.getRegistry().registerAnydataSchema(id1, id);
        } catch (IllegalArgumentException e) {
            isExpOccurred = true;
            assertEquals(e.getMessage(), String.format(
                    FMT_INV, id));
        }
        assertEquals(isExpOccurred, true);
    }

    /**
     * Test anydata add to data node negative test scenario when given
     * referenced identifer is invalid for anydata.
     */
    @Test
    public void addToData5Test() {
        boolean isExpOccurred = false;
        context.getContext();
        ModelObjectId id = null;
        try {
            id = new ModelObjectId.Builder()
                    .addChild(DefaultC1.class).addChild(DefaultMydata.class)
                    .build();
            ModelObjectId id1 = new ModelObjectId.Builder()
                    .addChild(AnyTestOpParam.class)
                    .build();
            context.getRegistry().registerAnydataSchema(id, id1);
        } catch (IllegalArgumentException e) {
            isExpOccurred = true;
            assertEquals(e.getMessage(), String.format(
                    FMT_INV, id));
        }
        assertEquals(isExpOccurred, true);
    }
}
