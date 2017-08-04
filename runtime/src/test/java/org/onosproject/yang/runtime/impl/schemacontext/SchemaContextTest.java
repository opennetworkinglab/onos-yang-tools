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

package org.onosproject.yang.runtime.impl.schemacontext;

import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangAugmentableNode;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.runtime.impl.DefaultYangModelRegistry;

import java.util.List;

import static org.onosproject.yang.runtime.impl.TestUtils.IETFNS;
import static org.onosproject.yang.runtime.impl.TestUtils.TOPONS;
import static org.onosproject.yang.runtime.impl.TestUtils.checkLeafSchemaContext;
import static org.onosproject.yang.runtime.impl.TestUtils.checkSchemaContext;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;

/**
 * Tests the default schema context methods.
 */
public class SchemaContextTest {

    private DefaultYangModelRegistry registry;

    /**
     * Do the prior setup for each UT.
     */
    private void setUp() {
        processSchemaRegistry();
        registry = registry();
    }

    /**
     * Validates the getting schema context by schema Id scenario.
     */
    @Test
    public void schemaContextBySchemaIdTest() {
        setUp();
        SchemaId id = new SchemaId("networks", IETFNS);
        YangNode child = (YangNode) registry.getChildContext(id);
        checkSchemaContext("networks", IETFNS, "/", null,
                           DataNode.Type.SINGLE_INSTANCE_NODE, child);

        // Validating networks parent context.
        child = child.getChild();
        checkSchemaContext("network", IETFNS, "networks", IETFNS,
                           DataNode.Type.MULTI_INSTANCE_NODE, child);
        List<YangAugment> augInfo = ((YangAugmentableNode) child)
                .getAugmentedInfoList();
        YangAugment augNode = augInfo.get(0);

        // Checking the augmented node parent context.
        checkSchemaContext("link", TOPONS, "network", IETFNS,
                           DataNode.Type.MULTI_INSTANCE_NODE,
                           augNode.getChild());

        List<YangLeaf> leafs = ((YangLeavesHolder) augNode.getChild())
                .getListOfLeaf();
        checkLeafSchemaContext("link-id", TOPONS, "link", TOPONS,
                               leafs.get(0));

        // Validating network-types parent context.
        child = child.getChild();
        checkSchemaContext("network-types", IETFNS, "network", IETFNS,
                           DataNode.Type.SINGLE_INSTANCE_NODE, child);

        child = child.getParent();
        leafs = ((YangLeavesHolder) child).getListOfLeaf();
        for (YangLeaf leaf : leafs) {
            checkLeafSchemaContext("network-id", IETFNS, "network", IETFNS,
                                   leaf);
        }

        child = child.getParent().getNextSibling();
        checkSchemaContext("networks-state", IETFNS, "/", null,
                           DataNode.Type.SINGLE_INSTANCE_NODE, child);
        child = child.getChild();

        checkSchemaContext("network", IETFNS, "networks-state", IETFNS,
                           DataNode.Type.MULTI_INSTANCE_NODE, child);
        leafs = ((YangLeavesHolder) child).getListOfLeaf();
        checkLeafSchemaContext("network-ref", IETFNS, "network", IETFNS,
                               leafs.get(1));
    }
}
