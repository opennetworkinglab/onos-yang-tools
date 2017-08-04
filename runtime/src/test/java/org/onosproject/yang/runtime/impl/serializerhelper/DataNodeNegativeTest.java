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
public class DataNodeNegativeTest {

    public static final String LNS = "yrt:list";

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

    private static final String FMT_NOT_EXIST =
            "Schema node with name %s doesn't exist.";

    /**
     * Test adding a node with value negative scenario.
     */
    @Test
    public void negative1Test() {

        dBlr = initializeDataNode(context);
        value = "1";
        boolean isExpOccurred = false;
        try {
            dBlr = addDataNode(dBlr, "l1", LNS, value, null);
        } catch (IllegalArgumentException e) {
            isExpOccurred = true;
            assertEquals(e.getMessage(), String.format(FMT_NOT_EXIST, "l1"));
        }
        assertEquals(isExpOccurred, true);
    }
}
