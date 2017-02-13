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

import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.SchemaId;

import static org.junit.Assert.assertEquals;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;

public final class TestUtils {

    /**
     * Restricts creation of test utils instance.
     */
    private TestUtils() {
    }

    /**
     * Checks the schema context values of given leaf node.
     */
    static void checkLeafSchemaContext(String name, String namespace,
                                       String pname, String pnamespace,
                                       YangLeaf child) {
        SchemaId id = child.getSchemaId();
        assertEquals(id.name(), name);
        assertEquals(id.namespace(), namespace);

        id = child.getParentContext().getSchemaId();
        assertEquals(id.name(), pname);
        assertEquals(id.namespace(), pnamespace);
        assertEquals(child.getType(), SINGLE_INSTANCE_LEAF_VALUE_NODE);
    }

    /**
     * Checks the schema context values of given leaf list node.
     */
    static void checkLeafListSchemaContext(String name, String namespace,
                                           String pname, String pnamespace,
                                           YangLeafList child) {
        SchemaId id = child.getSchemaId();
        assertEquals(id.name(), name);
        assertEquals(id.namespace(), namespace);

        id = child.getParentContext().getSchemaId();
        assertEquals(id.name(), pname);
        assertEquals(id.namespace(), pnamespace);
        assertEquals(child.getType(), MULTI_INSTANCE_LEAF_VALUE_NODE);
    }

    /**
     * Checks the schema context values of given node.
     */
    static void checkSchemaContext(String name, String namespace,
                                   String pname, String pnamespace,
                                   DataNode.Type type, YangNode child) {
        SchemaId id = child.getSchemaId();
        assertEquals(id.name(), name);
        assertEquals(id.namespace(), namespace);

        id = child.getParentContext().getSchemaId();
        assertEquals(id.name(), pname);
        assertEquals(id.namespace(), pnamespace);
        assertEquals(child.getType(), type);
    }
}
