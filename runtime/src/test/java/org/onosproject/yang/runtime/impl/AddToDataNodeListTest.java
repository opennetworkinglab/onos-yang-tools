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

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.helperutils.HelperContext;

import static org.onosproject.yang.runtime.helperutils.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.getResourceId;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateResourceId;

/**
 * Tests the serializer helper methods.
 */

@FixMethodOrder(MethodSorters.DEFAULT)
public class AddToDataNodeListTest {

    public static final String LNS = "yrt:list";

    TestYangSerializerContext context = new TestYangSerializerContext();

    /*
     * Reference for data node info.
     */
    HelperContext info;

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

    /*
     * Reference for string array to used for resource id testing.
     */
    String[] nA;
    String[] nsA;
    String[] valA;

    /**
     * Test add to data node builder.
     */
    @Test
    public void addToDataListTest() {

        dBlr = initializeDataNode(context);

        dBlr = addDataNode(dBlr, "l1", LNS, value, null);
        value = "1";
        dBlr = addDataNode(dBlr, "k1", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = "2";
        dBlr = addDataNode(dBlr, "k2", null, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "c1", null, value, null);
        value = "0";
        dBlr = addDataNode(dBlr, "l1", null, value, null);

        info = (HelperContext) dBlr.appInfo();
        id = getResourceId(dBlr);
        dBlr = exitDataNode(dBlr);

        ResourceId id1 = getResourceId(dBlr);
        dBlr = exitDataNode(dBlr);

        value = "3";
        dBlr = addDataNode(dBlr, "k3", null, value, null);

        info = (HelperContext) dBlr.appInfo();
        ResourceId id2 = getResourceId(dBlr);

        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

//        // Checking leaf list
//        value = "1";
//        dBlr = addDataNode(dBlr, "leaf1", LNS, value, null);
//        value = "2";
//        dBlr = addDataNode(dBlr, "leaf1", LNS, value, null);
//        value = "3";
//        dBlr = addDataNode(dBlr, "leaf1", LNS, value, null);
//        value = "4";
//        dBlr = addDataNode(dBlr, "leaf1", LNS, value, null);

        //Tree validation
        nA = new String[]{"/", "l1", "k1", "k2", "k3", "c1", "l1", ""};
        nsA = new String[]{null, LNS, LNS, LNS, LNS, LNS, LNS, ""};
        valA = new String[]{"1", "2", "3", "0", ""};
        validateResourceId(nA, nsA, valA, id);

        nA = new String[]{"/", "l1", "k1", "k2", "k3", "c1", ""};
        nsA = new String[]{null, LNS, LNS, LNS, LNS, LNS, ""};
        valA = new String[]{"1", "2", "3", ""};
        validateResourceId(nA, nsA, valA, id1);

        nA = new String[]{"/", "l1", "k1", "k2", "k3", ""};
        nsA = new String[]{null, LNS, LNS, LNS, LNS, ""};
        valA = new String[]{"1", "2", "3", ""};
        validateResourceId(nA, nsA, valA, id2);
        return;
    }
}
