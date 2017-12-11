/*
 * Copyright 2016-present Open Networking Foundation
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

package org.onosproject.yang.model;

import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;

/**
 * Unit test case to verify data tree operations.
 */
public class DataTreeOperationTest {

    private static final String PARENT = "parent";
    private static final String PARENT_NAMESPACE = "parent";

    private static final String C1 = "c1";
    private static final String C1_NAMESPACE = "parent/c1";

    private static final String C2 = "c2";
    private static final String C2_NAMESPACE = "parent/c2";

    private static final String C3 = "c3";
    private static final String C3_NAMESPACE = "parent/c1/c3";

    private static final String L1 = "l1";
    private static final String L1_NAMESPACE = "parent/c1/l1";

    private static final String L2 = "l2";
    private static final String L2_NAMESPACE = "parent/c2/l2";

    private static final String C4 = "c4";
    private static final String C4_NAMESPACE = "parent/c1/c3/c4";

    private static final String L3 = "l3";
    private static final String L3_NAMESPACE = "parent/c2/l3";

    private static final String LIST = "list";
    private static final String LIST_NAMESPACE = "parent/list";

    private static final String KL1 = "kl1";
    private static final String KL1_NAMESPACE = "parent/kl1";

    private static final String KL2 = "kl2";
    private static final String KL2_NAMESPACE = "parent/kl2";


    /**
     * Creates a data tree.
     *
     * @return data tree
     */
    private DataNode createDataTree() {

        /*
         * parent
         * |------C1
         * |       |-----C3
         * |       |-----l1
         * |
         * |------C2
         * |      |-----l2
         */

        return InnerNode.builder(PARENT, PARENT_NAMESPACE)
                //Parent
                .type(SINGLE_INSTANCE_NODE)
                //C1
                .createChildBuilder(C1, C1_NAMESPACE)
                .type(SINGLE_INSTANCE_NODE)

                //C1's child nodes C3
                .createChildBuilder(C3, C3_NAMESPACE)
                .type(SINGLE_INSTANCE_NODE)

                //build c3 and traverse back to c1
                .exitNode()

                //C1's child leaf L1
                .createChildBuilder(L1, L1_NAMESPACE, 10, null)
                .type(SINGLE_INSTANCE_LEAF_VALUE_NODE)

                //Builder l1 and traverse back to c1
                .exitNode()

                //build c1 and add it to parent and traverse back to parent node
                .exitNode()

                //create c2 parent's child node
                .createChildBuilder(C2, C2_NAMESPACE)
                .type(SINGLE_INSTANCE_NODE)
                //C2's leaf l2

                .createChildBuilder(L2, L2_NAMESPACE, "string", null)
                .type(MULTI_INSTANCE_LEAF_VALUE_NODE)

                //build l2 and add it to c2 and traverse back to c2.
                .exitNode()

                //build c2 and traverse back to parent node
                .exitNode()
                //build parent node
                .build();
    }

    /**
     * Unit test case for creating a data tree.
     */
    @Test
    public void testCreate() {

        /*
         * parent
         * |------C1
         * |       |-----C3
         * |       |-----l1
         * |
         * |------C2
         * |      |-----l2
         */

        DataNode node = createDataTree();

        //validate parent.
        validateNode(node, PARENT, PARENT_NAMESPACE, 2);

        //Validate c1
        Iterator<Map.Entry<NodeKey, DataNode>> itp = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();
        node = itp.next().getValue();
        validateNode(node, C1, C1_NAMESPACE, 2);

        Iterator<Map.Entry<NodeKey, DataNode>> itc1 = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();

        //validate c3
        node = itc1.next().getValue();
        validateNode(node, C3, C3_NAMESPACE, 0);

        //Validate c2
        node = itp.next().getValue();
        validateNode(node, C2, C2_NAMESPACE, 1);
    }

    /**
     * Unit test case to add a new child to current data tree.
     */
    @Test
    public void testAddChildNode() {

        /*
         * parent
         * |------C1
         * |       |-----C3
         * |       |      |----c4
         * |       |-----l1
         * |
         * |------C2
         * |       |-----l2
         */

        DataNode node = createDataTree();
        /*
         * RSC path == /parent/c1/c3.
         * adding c4 to c3 node.
         */
        ResourceId id = ResourceId.builder()
                .addBranchPointSchema(PARENT, PARENT_NAMESPACE)
                .addBranchPointSchema(C1, C1_NAMESPACE)
                .addBranchPointSchema(C3, C3_NAMESPACE).build();

        List<NodeKey> keys = id.nodeKeys();

        node.copyBuilder()

                //Reach to c1 by fetching it from the map.
                .getChildBuilder(keys.get(1))

                // now you have c1's builder and get c3 from c1's map and
                // then get its builder.
                .getChildBuilder(keys.get(2))

                //add c4 in c3.
                .createChildBuilder(C4, C4_NAMESPACE).type(SINGLE_INSTANCE_NODE)

                //build c3 and return to c1.
                .exitNode()

                //build c1 and return to parent.
                .exitNode()

                //build parent node.
                .build();

        //validate parent.
        validateNode(node, PARENT, PARENT_NAMESPACE, 2);

        //Validate c1
        Iterator<Map.Entry<NodeKey, DataNode>> itp = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();
        node = itp.next().getValue();
        validateNode(node, C1, C1_NAMESPACE, 2);

        Iterator<Map.Entry<NodeKey, DataNode>> itc1 = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();

        //validate c3
        node = itc1.next().getValue();
        validateNode(node, C3, C3_NAMESPACE, 1);

        Iterator<Map.Entry<NodeKey, DataNode>> itc3 = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();

        //validate c3
        node = itc3.next().getValue();
        validateNode(node, C4, C4_NAMESPACE, 0);
    }

    /**
     * Unit test case to add a leaf node to current data tree.
     */
    @Test
    public void testAddLeafNode() {

        /*
         * parent
         * |------C1
         * |       |-----C3
         * |       |-----l1
         * |
         * |------C2
         * |       |----l2
         * |       |----l3
         */
        DataNode node = createDataTree();
        /*
         * RSC path == /parent/c1/c3.
         * adding c4 to c3 node.
         */
        ResourceId id = ResourceId.builder()
                .addBranchPointSchema(PARENT, PARENT_NAMESPACE)
                .addBranchPointSchema(C2, C2_NAMESPACE).build();

        List<NodeKey> keys = id.nodeKeys();

        node.copyBuilder()

                //Reach to c2 by fetching it from the map.
                .getChildBuilder(keys.get(1))
                //add l3 in c2.
                .createChildBuilder(L3, L3_NAMESPACE, 15, null)
                .type(MULTI_INSTANCE_LEAF_VALUE_NODE)
                .addLeafListValue(16)

                //build l3 and return to c2.
                .exitNode()

                //build c2 and return to parent.
                .exitNode()

                //build parent node.
                .build();

        //validate parent.
        validateNode(node, PARENT, PARENT_NAMESPACE, 2);

        //Validate c1
        Iterator<Map.Entry<NodeKey, DataNode>> itp = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();
        node = itp.next().getValue();
        validateNode(node, C1, C1_NAMESPACE, 2);

        //Validate c1
        node = itp.next().getValue();
        validateNode(node, C2, C2_NAMESPACE, 2);

        Iterator<Map.Entry<NodeKey, DataNode>> itc2 = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();

        //validate l2
        node = itc2.next().getValue();
        validateNode(node, L2, L2_NAMESPACE, 0);

        //validate l3
        node = itc2.next().getValue();
        validateNode(node, L3, L3_NAMESPACE, 0);

        //validate for leaf list key
        assertThat(16, is(((LeafListKey) node.key()).value()));
    }

    /**
     * Unit test case for adding a list node in current data tree.
     */
    @Test
    public void testAddListNode() {
         /*
         * parent
         * |------C1
         * |       |-----C3
         * |             |-------list1
         * |       |-----l1
         * |
         * |------C2
         * |       |----l2
         */
        DataNode node = createDataTree();
        /*
         * RSC path == /parent/c1/c3.
         * adding c4 to c3 node.
         */
        ResourceId id = ResourceId.builder()
                .addBranchPointSchema(PARENT, PARENT_NAMESPACE)
                .addBranchPointSchema(C1, C1_NAMESPACE)
                .addBranchPointSchema(C3, C3_NAMESPACE).build();

        List<NodeKey> keys = id.nodeKeys();

        node.copyBuilder()

                //Reach to c1 by fetching it from the map.
                .getChildBuilder(keys.get(1))

                //reach to c3
                .getChildBuilder(keys.get(2))

                //add list in c3.
                .createChildBuilder(LIST, LIST_NAMESPACE)
                .type(MULTI_INSTANCE_NODE)

                //Add key leaf1
                .addKeyLeaf(KL1, KL1_NAMESPACE, 15)

                //add key leaf 3
                .addKeyLeaf(KL2, KL2_NAMESPACE, 16)

                //build list and return to c3.
                .exitNode()

                //build c3 and return to c1.
                .exitNode()

                //build c1 and return to parent.
                .exitNode()

                //build parent node.
                .build();

        //validate parent.
        validateNode(node, PARENT, PARENT_NAMESPACE, 2);

        //Validate c1
        Iterator<Map.Entry<NodeKey, DataNode>> itp = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();
        node = itp.next().getValue();
        validateNode(node, C1, C1_NAMESPACE, 2);

        Iterator<Map.Entry<NodeKey, DataNode>> itc1 = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();

        //validate c2
        node = itc1.next().getValue();
        validateNode(node, C3, C3_NAMESPACE, 1);

        //validate c3
        Iterator<Map.Entry<NodeKey, DataNode>> itc2 = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();

        //validate l2
        node = itc2.next().getValue();
        validateNode(node, LIST, LIST_NAMESPACE, 0);

        //validate for leaf list key
        assertThat(2, is(((ListKey) node.key()).keyLeafs().size()));
    }

    /**
     * Unit test case to remove one leaf node from data tree.
     */
    @Test
    public void testRemoveLeafNode() {

        DataNode node = createDataTree();
        /*
         * RSC path == /parent/c1/c3.
         * adding c4 to c3 node.
         */
        ResourceId id = ResourceId.builder()
                .addBranchPointSchema(PARENT, PARENT_NAMESPACE)
                .addBranchPointSchema(C1, C1_NAMESPACE)
                .addBranchPointSchema(L1, L1_NAMESPACE).build();

        List<NodeKey> keys = id.nodeKeys();

        node.copyBuilder()

                // copy c1
                .getChildBuilder(keys.get(1))

                //delete l1 from c1
                .deleteChild(keys.get(2))

                //traverse back to parent node and build c1
                .exitNode()

                //build parent node
                .build();

        //validate parent.
        validateNode(node, PARENT, PARENT_NAMESPACE, 2);

        //Validate c1
        Iterator<Map.Entry<NodeKey, DataNode>> itp = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();
        node = itp.next().getValue();
        validateNode(node, C1, C1_NAMESPACE, 1);

        Iterator<Map.Entry<NodeKey, DataNode>> itc1 = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();

        //validate c3
        node = itc1.next().getValue();
        validateNode(node, C3, C3_NAMESPACE, 0);
    }

    /**
     * Unit test case to remove one child node from data tree.
     */
    @Test
    public void testRemoveChildNode() {

        DataNode node = createDataTree();
        /*
         * RSC path == /parent/c1/c3.
         * adding c4 to c3 node.
         */
        ResourceId id = ResourceId.builder()
                .addBranchPointSchema(PARENT, PARENT_NAMESPACE)
                .addBranchPointSchema(C1, C1_NAMESPACE).build();

        List<NodeKey> keys = id.nodeKeys();

        node.copyBuilder()

                //delete l1 from c1
                .deleteChild(keys.get(1))

                //build parent node
                .build();

        //validate parent.
        validateNode(node, PARENT, PARENT_NAMESPACE, 1);

        //Validate c1
        Iterator<Map.Entry<NodeKey, DataNode>> itp = ((InnerNode) node)
                .childNodes()
                .entrySet()
                .iterator();
        node = itp.next().getValue();

        validateNode(node, C2, C2_NAMESPACE, 1);
    }

    /**
     * Validates each node.
     *
     * @param node      data node
     * @param name      name of node
     * @param namespace namespace of node
     * @param size      number of children
     */
    private void validateNode(DataNode node, String name, String namespace,
                              int size) {

        String nodeName = node.key().schemaId().name();
        String nodeNamespace = node.key().schemaId().namespace();

        //validate parent node.
        assertThat(true, is(nodeName.equals(name)));
        assertThat(true, is(nodeNamespace.equals(namespace)));

        if (node instanceof InnerNode) {
            InnerNode in = (InnerNode) node;
            Map<NodeKey, DataNode> children = in.childNodes();
            assertThat(true, is(children.size() == size));
        }
    }
}