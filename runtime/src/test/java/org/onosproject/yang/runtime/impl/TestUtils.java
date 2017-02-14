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
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.LeafNode;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.model.SchemaId;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;

public final class TestUtils {

    /**
     * Restricts creation of test utils instance.
     */
    private TestUtils() {
    }

    /**
     * Checks the schema context values of given leaf node.
     */
    public static void checkLeafSchemaContext(String name, String namespace,
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
    public static void checkLeafListSchemaContext(String name, String namespace,
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
    public static void checkSchemaContext(String name, String namespace,
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


    /**
     * Validates the root level node schema context.
     *
     * @param context schema context
     */
    public static void checkRootLevelContext(SchemaContext context) {
        SchemaId id = context.getSchemaId();
        assertEquals(id.name(), "/");
        assertEquals(id.namespace(), null);
        assertNull(context.getParentContext());
        assertEquals(context.getType(), SINGLE_INSTANCE_NODE);
    }

    /**
     * Validate the resource id builder.
     */
    public static void validateResourceId(String[] nA, String[] nsA,
                                          String[] valA, ResourceId rBlrEx) {
        SchemaId sId;
        Object val = null;
        List<NodeKey> keys = rBlrEx.nodeKeys();
        int i = 0;
        int j = 0;
        for (NodeKey k : keys) {
            sId = k.schemaId();
            assertEquals(sId.name(), nA[i]);
            assertEquals(sId.namespace(), nsA[i]);
            i++;
            if (k instanceof ListKey) {
                List<KeyLeaf> kLeaf = ((ListKey) k).keyLeafs();
                for (KeyLeaf kl : kLeaf) {
                    sId = kl.leafSchema();
                    assertEquals(sId.name(), nA[i]);
                    assertEquals(sId.namespace(), nsA[i]);
                    assertEquals(kl.leafValAsString(), valA[j]);
                    i++;
                    j++;
                }
            } else if (k instanceof LeafListKey) {
                val = ((LeafListKey) k).value().toString();
            }
            if (val != null) {
                assertEquals(val, valA[j]);
                j++;
            }
        }
    }

    /**
     * Validates the give data node content.
     *
     * @param node    data node
     * @param n       name
     * @param ns      namespace
     * @param type    data node type
     * @param isChild denotes the given node has child or not
     * @param value   value of leaf
     */
    public static void validateDataNode(DataNode node, String n, String ns,
                                        DataNode.Type type, boolean isChild,
                                        String value) {
        NodeKey k = node.key();
        SchemaId id = k.schemaId();
        assertEquals(id.name(), n);
        assertEquals(id.namespace(), ns);
        if (node instanceof InnerNode) {
            assertEquals(((InnerNode) node).type(), type);
            if (isChild) {
                assertNotNull(((InnerNode) node).childNodes());
            } else {
                assertNull(((InnerNode) node).childNodes());
            }
        } else {
            assertEquals(((LeafNode) node).type(), type);
            assertEquals(((LeafNode) node).value().toString(), value);
        }
    }

    /**
     * Validates the give key leaf content.
     *
     * @param key key leaf
     * @param n   name
     * @param ns  namespace
     * @param v   value of leaf node
     */
    public static void validateLeafDataNode(KeyLeaf key, String n, String ns,
                                            String v) {
        SchemaId id = key.leafSchema();
        assertEquals(id.name(), n);
        assertEquals(id.namespace(), ns);
        assertEquals(key.leafValue().toString(), v);
    }
}
