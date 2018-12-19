/*
 *  Copyright 2017-present Open Networking Foundation
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
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.runtime.YangSerializerContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

    // no instantiation
    private EncoderUtils() {
    }

    /**
     * Converts a data node to JSON data.
     *
     * @param dataNode given data node
     * @param context  jsonserializer context
     * @return JSON
     */
    public static ObjectNode convertDataNodeToJson(DataNode dataNode, YangSerializerContext context) {
        checkNotNull(dataNode, "data node cannot be null");

        JsonBuilder jsonBuilder = new DefaultJsonBuilder();
        jsonBuilder.initializeJson();
        DataNodeVisitor treeNodeListener = new DataNodeJsonVisitor(jsonBuilder, context);

        DataNodeSiblingPositionType siblingType = (dataNode.type() == MULTI_INSTANCE_NODE) ?
                FIRST_INSTANCE : NOT_MULTI_INSTANCE_NODE;
        walkDataNodeTree(treeNodeListener, dataNode, siblingType);

        jsonBuilder.finalizeJson(dataNode.type() == MULTI_INSTANCE_NODE);
        return jsonBuilder.getTreeNode();
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
        DataNodeSiblingPositionType currChildType;

        /*
         * Dynamic Config does not preserve the order of child nodes.
         * For cases where an array of objects gets fragmented by an internal key
         */
        List<DataNode> sortedChildList = sortChildrenList(childrenList);
        Iterator<DataNode> it = sortedChildList.iterator();
        DataNode currChild = it.next();
        DataNode nextChild;
        boolean lastChildNotProcessed = true;
        while (lastChildNotProcessed) {
            /*
             * Iterate through the children list. Invoke data node walker
             * for every child. If the child is a multi-instance node, we
             * need to determine if it is the first or last sibling and pass
             * this info the walker.
             */
            if (it.hasNext()) {
                nextChild = it.next();
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
            case LAST_INSTANCE:
            case NOT_MULTI_INSTANCE_NODE:
            case SINGLE_INSTANCE_IN_MULTI_NODE:
                /*
                 * If type of previous child is unknown or last instance,
                 * that means the current child is the first sibling. If
                 * the next child is null or has a different node name,
                 * then that means the current child is the only child.
                 */
                if (nextChild == null ||
                        !nextChild.key().schemaId().name().
                                equals(currChild.key().schemaId().name())) {
                    curChildSiblingType = SINGLE_INSTANCE_IN_MULTI_NODE;
                } else {
                    curChildSiblingType = FIRST_INSTANCE;
                }
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

    private static List<DataNode> sortChildrenList(Map<NodeKey, DataNode> childrenList) {

        List<DataNode> sortedList = new ArrayList<>();
        Map<String, List<DataNode>> groupedBucket = new HashMap<>();

        //Sort by name
        for (Entry<NodeKey, DataNode> nodeKeyDataNodeEntry : childrenList.entrySet()) {
            DataNode dataNode = nodeKeyDataNodeEntry.getValue();
            String nodeName = dataNode.key().schemaId().name();
            List<DataNode> group = groupedBucket.computeIfAbsent(nodeName, k -> new ArrayList<>());
            group.add(dataNode);
        }
        for (Entry<String, List<DataNode>> entry : groupedBucket.entrySet()) {
            sortedList.addAll(entry.getValue());
        }

        return sortedList.stream().sorted(Comparator.comparing(object -> object.key().schemaId().name()))
                .collect(Collectors.toList());
    }
}
