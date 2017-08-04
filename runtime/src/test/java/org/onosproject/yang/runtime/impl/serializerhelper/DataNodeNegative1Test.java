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
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import static org.junit.Assert.assertEquals;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;

/**
 * Tests the serializer helper methods.
 */
public class DataNodeNegative1Test {

    TestYangSerializerContext context = new TestYangSerializerContext();

    /*
     * Reference for data node builder.
     */
    DataNode.Builder dBlr;

    /*
     * Reference for resource id.
     */
    ResourceId id;

    /*
     * Reference for the value.
     */
    String value;

    private static final String E_NAMESPACE =
            "NameSpace is mandatory to provide for first level node.";

    /**
     * Test adding a node null namespace negative scenario.
     */
    @Test
    public void negativeTest() {

        dBlr = initializeDataNode(context);
        value = "1";
        boolean isExpOccurred = false;
        try {
            dBlr = addDataNode(dBlr, "l1", null, value, null);
        } catch (IllegalArgumentException e) {
            isExpOccurred = true;
            assertEquals(e.getMessage(), E_NAMESPACE);
        }
        assertEquals(isExpOccurred, true);
    }
}
