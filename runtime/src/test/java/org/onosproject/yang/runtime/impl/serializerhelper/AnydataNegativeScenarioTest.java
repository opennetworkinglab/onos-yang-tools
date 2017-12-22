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

import org.onosproject.yang.gen.v11.listanydata.rev20160624.ListAnydataOpParam;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import static org.junit.Assert.assertEquals;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.INVAL_ANYDATA;

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
//    @Test
    public void addToDataTest() {
        boolean isExpOccurred = false;
//        context.getContext();
//        try {
//            context.getRegistry().registerAnydataSchema(Node.class, Node.class);
//        } catch (IllegalArgumentException e) {
//            isExpOccurred = true;
//            assertEquals(e.getMessage(), String.format(FMT_INV, Node.class));
//        }
        assertEquals(isExpOccurred, true);
    }

    /**
     * Test anydata add to data node negative test scenario when given
     * referenced node module is not registered.
     */
//    @Test
    public void addToData2Test() {
        boolean isExpOccurred = false;
        context.getContext();
        try {
//            context.getRegistry().registerAnydataSchema(Mydata.class,
//                                                        ListAnydataOpParam.class);
        } catch (IllegalArgumentException e) {
            isExpOccurred = true;
            assertEquals(e.getMessage(), String.format(
                    INVAL_ANYDATA, ListAnydataOpParam.class));
        }
        assertEquals(isExpOccurred, true);
    }


    /**
     * Test anydata add to data node negative test scenario when given
     * referenced node is not of type list/container.
     */
//    @Test
    public void addToData3Test() {
        boolean isExpOccurred = false;
        context.getContext();
//        try {
//            context.getRegistry().registerAnydataSchema(Mydata.class,
//                                                        ListAnydata.class);
//        } catch (IllegalArgumentException e) {
//            isExpOccurred = true;
//            assertEquals(e.getMessage(), String.format(
//                    INVAL_ANYDATA, ListAnydata.class));
//        }
        assertEquals(isExpOccurred, true);
    }

    /**
     * Test anydata add to data node negative test scenario when given
     * referenced node is not of type list/container.
     */
//    @Test
    public void addToData4Test() {
        boolean isExpOccurred = false;
//        context.getContext();
//        try {
//            context.getRegistry().registerAnydataSchema(Mydata.class,
//                                                        List52Keys.class);
//        } catch (IllegalArgumentException e) {
//            isExpOccurred = true;
//            assertEquals(e.getMessage(), String.format(
//                    INVAL_ANYDATA, List52Keys.class.getCanonicalName()));
//        }
        assertEquals(isExpOccurred, true);
    }
}
