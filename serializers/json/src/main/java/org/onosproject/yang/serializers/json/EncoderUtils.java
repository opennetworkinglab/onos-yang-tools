/*
 *  Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang.serializers.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Splitter;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceId;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.serializers.json.DataNodeSiblingPositionType.FIRST_INSTANCE;
import static org.onosproject.yang.serializers.json.DataNodeSiblingPositionType.LAST_INSTANCE;
import static org.onosproject.yang.serializers.json.DataNodeSiblingPositionType.MIDDLE_INSTANCE;
import static org.onosproject.yang.serializers.json.DataNodeSiblingPositionType.NOT_MULTI_INSTANCE_NODE;
import static org.onosproject.yang.serializers.json.DataNodeSiblingPositionType.SINGLE_INSTANCE_IN_MULTI_NODE;
import static org.onosproject.yang.serializers.json.DataNodeSiblingPositionType.UNKNOWN_TYPE;

/**
 * Utilities for converting Data Nodes into JSON format.
 */
public final class EncoderUtils {
    private static final Splitter SLASH_SPLITTER = Splitter.on('/');
    private static final Splitter COMMA_SPLITTER = Splitter.on(',');
    private static final String EQUAL = "=";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String SLASH = "/";

    // no instantiation
    private EncoderUtils() {
    }

    /**
     * Converts a resource identifier to URI string.
     *
     * @param rid resource identifier
     * @return URI
     */
    public static String convertRidToUri(ResourceId rid) {
        if (rid == null) {
            return null;
        }

        StringBuilder uriBuilder = new StringBuilder();
        List<NodeKey> nodeKeyList = rid.nodeKeys();
        String curNameSpace = null;
        for (NodeKey key : nodeKeyList) {
            curNameSpace = addNodeKeyToUri(key, curNameSpace, uriBuilder);
        }
        return uriBuilder.toString();
    }

    private static String addNodeKeyToUri(NodeKey key,
                                          String curNameSpace,
                                          StringBuilder uriBuilder) {
        String newNameSpace;
        if (key instanceof LeafListKey) {
            newNameSpace = addLeafListNodeToUri((LeafListKey) key, curNameSpace, uriBuilder);
        } else if (key instanceof ListKey) {
            newNameSpace = addListNodeToUri((ListKey) key, curNameSpace, uriBuilder);
        } else {
            uriBuilder.append(SLASH);
            newNameSpace = addNodeNameToUri(key, curNameSpace, uriBuilder);
        }
        return newNameSpace;
    }

    private static String addLeafListNodeToUri(LeafListKey key,
                                               String curNameSpace,
                                               StringBuilder uriBuilder) {

        String newNameSpace = addNodeNameToUri(key, curNameSpace, uriBuilder);
        uriBuilder.append(EQUAL);
        uriBuilder.append(key.asString());
        return newNameSpace;
    }

    private static String addListNodeToUri(ListKey key,
                                           String curNameSpace,
                                           StringBuilder uriBuilder) {
        String newNameSpace = addNodeNameToUri(key, curNameSpace, uriBuilder);
        uriBuilder.append(EQUAL);
        String prefix = "";
        for (KeyLeaf keyLeaf : key.keyLeafs()) {
            uriBuilder.append(prefix);
            prefix = COMMA;
            uriBuilder.append(keyLeaf.leafValue().toString());
        }

        return newNameSpace;
    }

    private static String addNodeNameToUri(NodeKey key,
                                           String curNameSpace,
                                           StringBuilder uriBuilder) {
        String nodeName = key.schemaId().name();
        String newNameSpace = key.schemaId().namespace();

        uriBuilder.append(nodeName);

        if (newNameSpace == null) {
            return curNameSpace;
        }

        if (!newNameSpace.equals(curNameSpace)) {
            uriBuilder.append(COLON);
            uriBuilder.append(newNameSpace);
        }

        return newNameSpace;
    }

    /**
     * Converts a data node to JSON data.
     *
     * @param dataNode given data node
     * @return JSON
     */
    public static ObjectNode convertDataNodeToJson(DataNode dataNode) {
        checkNotNull(dataNode, "data node cannot be null");

        JsonBuilder jsonBuilder = new DefaultJsonBuilder();
        DataNodeVisitor treeNodeListener = new DataNodeJsonVisitor(jsonBuilder);

        DataNodeSiblingPositionType siblingType = NOT_MULTI_INSTANCE_NODE;
        walkDataNodeTree(treeNodeListener, dataNode, siblingType);

        ObjectNode resultData = jsonBuilder.getTreeNode();
        return resultData;
    }

    private static void walkDataNodeTree(DataNodeVisitor dataNodeVisitor,
                                         DataNode dataNode,
                                         DataNodeSiblingPositionType siblingType) {
        checkNotNull(dataNode, "data tree cannot be null");
        checkNotNull(dataNodeVisitor, "dataNodeVisitor cannot be null");

        // depth-first walk of the data node tree
        dataNodeVisitor.enterDataNode(dataNode, siblingType);

        if (dataNode.type() == SINGLE_INSTANCE_NODE ||
                dataNode.type() == MULTI_INSTANCE_NODE) {
            // Walk through every child on the children list
            walkChildNodeList(dataNodeVisitor, dataNode);
        }

        dataNodeVisitor.exitDataNode(dataNode, siblingType);
    }

    private static void walkChildNodeList(DataNodeVisitor dataNodeVisitor,
                                          DataNode dataNode) {
        if (dataNode.type() != SINGLE_INSTANCE_NODE &&
                dataNode.type() != MULTI_INSTANCE_NODE) {
            // Only inner nodes could have children.
            return;
        }

        Map<NodeKey, DataNode> childrenList = ((InnerNode) dataNode).childNodes();
        if (childrenList == null || childrenList.isEmpty()) {
            // the children list is either not yet created or empty.
            return;
        }

        /*
         * We now have a none empty children list to walk through.
         */

        DataNodeSiblingPositionType prevChildType = UNKNOWN_TYPE;
        DataNodeSiblingPositionType currChildType = UNKNOWN_TYPE;

        Iterator it = childrenList.entrySet().iterator();
        DataNode currChild = ((Map.Entry<NodeKey, DataNode>) it.next()).getValue();
        DataNode nextChild = null;
        boolean lastChildNotProcessed = true;
        while (lastChildNotProcessed) {
            /*
             * Iterate through the children list. Invoke data node walker
             * for every child. If the child is a multi-instance node, we
             * need to determine if it is the first or last sibling and pass
             * this info the walker.
             */
            if (it.hasNext()) {
                nextChild = ((Map.Entry<NodeKey, DataNode>) it.next()).getValue();
            } else {
                /*
                 * Current child is the last child.
                 * So mark this iteration as the last one.
                 */
                lastChildNotProcessed = false;
                nextChild = null;
            }
            currChildType = getCurrentChildSiblingType(currChild,
                                                       nextChild,
                                                       prevChildType);
            walkDataNodeTree(dataNodeVisitor, currChild, currChildType);
            prevChildType = currChildType;
            currChild = nextChild;
        }
    }

    private static DataNodeSiblingPositionType getCurrentChildSiblingType(DataNode currChild,
                                                                          DataNode nextChild,
                                                                          DataNodeSiblingPositionType prevChildType) {
        if (currChild.type() != MULTI_INSTANCE_NODE &&
                currChild.type() != MULTI_INSTANCE_LEAF_VALUE_NODE) {
            return NOT_MULTI_INSTANCE_NODE;
        }

        DataNodeSiblingPositionType curChildSiblingType = UNKNOWN_TYPE;
        switch (prevChildType) {
            case UNKNOWN_TYPE:
                /*
                 * If type of previous child is unknown, that means
                 * the current child is the first sibling. If the next
                 * child is null, then that means the current child is
                 * the only child.
                 */
                if (nextChild == null) {
                    curChildSiblingType = SINGLE_INSTANCE_IN_MULTI_NODE;
                } else {
                    curChildSiblingType = FIRST_INSTANCE;
                }
                break;
            case NOT_MULTI_INSTANCE_NODE:
            case LAST_INSTANCE:
                curChildSiblingType = FIRST_INSTANCE;
                break;
            case FIRST_INSTANCE:
            case MIDDLE_INSTANCE:
                /*
                 * If we still have a next child and the next child's name
                 * is the same name as the current node, then the current
                 * node is not the last sibling yet.
                 */
                if (nextChild != null &&
                        nextChild.key().schemaId().name().
                                equals(currChild.key().schemaId().name())) {
                    curChildSiblingType = MIDDLE_INSTANCE;
                } else {
                    curChildSiblingType = LAST_INSTANCE;
                }
                break;
            default:
                curChildSiblingType = UNKNOWN_TYPE;
        }

        return curChildSiblingType;
    }
}
