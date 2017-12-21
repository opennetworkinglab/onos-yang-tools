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
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.HelperContext;
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.getResourceId;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateResourceId;
import static org.onosproject.yang.runtime.impl.TestUtils.walkINTree;

/**
 * Tests the serializer helper data validation.
 */
public class CheckEnumValidationTest {

    public static final String LNS = "ydt.enumtest";
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

    private static final String[] EXPECTED = {
            "Entry Node is /.",
            "Entry Node is enumList.",
            "Entry Node is enumleaf.",
            "Exit Node is enumleaf.",
            "Exit Node is enumList.",
            "Exit Node is /."
    };

    @Test
    public void addDataNodeToBuilder() {
        dBlr = initializeDataNode(context);
        dBlr = addDataNode(dBlr, "enumList", LNS, value, null);
        value = "ten";
        dBlr = addDataNode(dBlr, "enumleaf", LNS, value, null);

        // resource ID validation
        id = getResourceId(dBlr);
        nA = new String[]{"/", "enumList", "enumleaf"};
        nsA = new String[]{null, LNS, LNS};
        valA = new String[]{"ten"};
        validateResourceId(nA, nsA, valA, id);

        dBlr = dBlr.exitNode();
        dBlr = dBlr.exitNode();

        // Validating the data node.
        DataNode node = dBlr.build();
        node = ((InnerNode) node).childNodes().entrySet().iterator().next()
                .getValue();

        validateDataNode(node, "enumList", LNS, SINGLE_INSTANCE_NODE,
                         true, null);

        node = ((InnerNode) node).childNodes().entrySet().iterator().next()
                .getValue();

        validateDataNode(node, "enumleaf", LNS,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "ten");
        walkINTree(dBlr.build(), EXPECTED);
    }
}
