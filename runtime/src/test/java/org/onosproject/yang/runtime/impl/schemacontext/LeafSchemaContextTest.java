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
import org.onosproject.yang.compiler.datamodel.DefaultYangNamespace;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.runtime.impl.DefaultYangModelRegistry;

import static org.onosproject.yang.runtime.impl.TestUtils.checkLeafListSchemaContext;
import static org.onosproject.yang.runtime.impl.TestUtils.checkLeafSchemaContext;
import static org.onosproject.yang.runtime.impl.TestUtils.checkSchemaContext;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;

/**
 * Tests the default schema context methods.
 */
public class LeafSchemaContextTest {

    private static final String FOODNS = "yrt:food";
    private DefaultYangModelRegistry registry;

    /**
     * Do the prior setup for each UT.
     */
    private void setUp() {
        processSchemaRegistry();
        registry = registry();
    }

    /**
     * Checks leaf, leaf-list, choice-case data node parent context.
     */
    @Test
    public void leafSchemaContTest() throws DataModelException {
        setUp();
        SchemaId id = new SchemaId("food", FOODNS);
        YangNode child = (YangNode) registry.getChildContext(id);
        checkSchemaContext("food", FOODNS, "/", null,
                           DataNode.Type.SINGLE_INSTANCE_NODE, child);

        YangSchemaNodeIdentifier rId = new YangSchemaNodeIdentifier();
        rId.setName("pretzel");
        rId.setNameSpace(new DefaultYangNamespace(FOODNS));
        YangSchemaNode leaf1 = child.getChildSchema(rId).getSchemaNode();
        checkLeafSchemaContext("pretzel", FOODNS, "food", FOODNS,
                               (YangLeaf) leaf1);

        rId.setName("redbull");
        leaf1 = child.getChildSchema(rId).getSchemaNode();
        checkLeafSchemaContext("redbull", FOODNS, "food", FOODNS,
                               (YangLeaf) leaf1);

        rId.setName("kingfisher");
        leaf1 = child.getChildSchema(rId).getSchemaNode();
        checkLeafSchemaContext("kingfisher", FOODNS, "food", FOODNS,
                               (YangLeaf) leaf1);

        id = new SchemaId("bool", FOODNS);
        YangLeaf leaf = (YangLeaf) registry.getChildContext(id);
        checkLeafSchemaContext("bool", FOODNS, "/", null,
                               leaf);

        id = new SchemaId("boolean", FOODNS);
        YangLeafList leafList = (YangLeafList) registry.getChildContext(id);
        checkLeafListSchemaContext("boolean", FOODNS, "/", null,
                                   leafList);
    }
}
