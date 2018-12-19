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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.runtime.SerializerHelper;

import java.util.Iterator;
import java.util.Map;

import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.serializers.utils.SerializersUtil.getLatterSegment;
import static org.onosproject.yang.serializers.utils.SerializersUtil.getPreSegment;

/**
 * Represents implementation of JSON walk, which walks the JSON object node.
 */
public class DefaultJsonWalker implements JsonWalker {
    private static final String COLON = ":";

    private DataNode.Builder dataNodeBuilder;

    /**
     * Creates an instance of JSON object node walker.
     *
     * @param db data node builder
     */
    public DefaultJsonWalker(DataNode.Builder db) {
        dataNodeBuilder = db;
    }

    /**
     * Returns the root data node builder.
     *
     * @return data node builder
     */
    public DataNode.Builder rootBuilder() {
        return dataNodeBuilder;
    }

    @Override
    public void walkJsonNode(String fieldName, JsonNode jsonNode) {
        if (!jsonNode.isContainerNode()) {
            //the node has no children, so add it as leaf node to the data tree.
            addLeafNodeToDataTree(fieldName, jsonNode);
            // this is to avoid exit node for top level node
            if (dataNodeBuilder.parent() != null) {
                dataNodeBuilder = SerializerHelper.exitDataNode(dataNodeBuilder);
            }
            return;
        }

        /*
         * For an array node, there are 2 cases:
         *
         * 1. It is a leaflist node
         * 2. It is a multi-instance node.
         */
        if (jsonNode.isArray()) {
            // Let's deal with the leaflist case first.
            if (isJsonNodeLeafList((ArrayNode) jsonNode)) {
                addLeafListNodeToDataTree(fieldName, (ArrayNode) jsonNode);
                //Don't move up, as addLeafListNodeToDataTree() does it.
                //SerializerHelper.exitDataNode(dataNodeBuilder);
                return;
            }

            /*
             * This is a multi-instance node. Each element in the
             * array is an instance of multi-instance node in the data tree.
             */
            Iterator<JsonNode> elements = jsonNode.elements();
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                addMultiInstanceNodeToDataTree(fieldName);

                // Recursively build the subtree of element
                walkJsonNode(null, element);

                // We are done with this array element.
                // this is to avoid exit node for top level node
                if (dataNodeBuilder.parent() != null) {
                    dataNodeBuilder = SerializerHelper.exitDataNode(dataNodeBuilder);
                }
            }

            // We are done with this array node.
            // Don't move up, as we are already at the parent node.
            //SerializerHelper.exitDataNode(dataNodeBuilder);
            return;
        }

        /*
         * If we reach here, then this node is an object node. An object node
         * has a set of name-value pairs. ("value" can be object node.)
         */
        if (fieldName != null) {
            // If fieldName is null, then the caller does not want to
            // add the node into the data tree. Rather, it just want to add
            // the children nodes to the current node of the data tree.
            addSingleInstanceNodeToDataTree(fieldName);
        }

        Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
        while (fields.hasNext()) {
            //get the children entry of the node
            Map.Entry<String, JsonNode> currentChild = fields.next();
            String key = currentChild.getKey();
            JsonNode value = currentChild.getValue();
            walkJsonNode(key, value);
            // NOTE: Don't move up, because walkJsonNode will do so.
            // SerializerHelper.exitDataNode(dataNodeBuilder);
        }

        if (fieldName != null && dataNodeBuilder.parent() != null) {
            // move up since we finish creating a container node.
            dataNodeBuilder = SerializerHelper.exitDataNode(dataNodeBuilder);
        }
    }

    private void addDataNode(String fieldName, String value, String valNamespace,
                             DataNode.Type nodeType) {
        String nodeName = getLatterSegment(fieldName, COLON);
        String namespace = getPreSegment(fieldName, COLON);

        dataNodeBuilder = SerializerHelper.addDataNode(dataNodeBuilder,
                                                       nodeName, namespace,
                                                       value, valNamespace,
                                                       nodeType);
    }

    private void addNoneLeafDataNode(String fieldName, DataNode.Type nodeType) {
        addDataNode(fieldName, null, null, nodeType);
    }

    private void addLeafDataNode(String fieldName, String value, DataNode.Type nodeType) {
        String valNamespace = null;
        String actVal;
        if (value != null) {
            actVal = getLatterSegment(value, COLON);
            valNamespace = getPreSegment(value, COLON);
        } else {
            actVal = value;
        }
        addDataNode(fieldName, actVal, valNamespace, nodeType);
    }

    private void addSingleInstanceNodeToDataTree(String fieldName) {
        addNoneLeafDataNode(fieldName, SINGLE_INSTANCE_NODE);
    }

    private void addMultiInstanceNodeToDataTree(String fieldName) {
        addNoneLeafDataNode(fieldName, MULTI_INSTANCE_NODE);
    }

    private void addLeafNodeToDataTree(String fieldName, JsonNode jsonNode) {
        String value = jsonNode.asText();
        addLeafDataNode(fieldName, value,
                        SINGLE_INSTANCE_LEAF_VALUE_NODE);
    }

    private void addLeafListNodeToDataTree(String fieldName,
                                           ArrayNode jsonNode) {
        Iterator<JsonNode> elements = jsonNode.elements();
        while (elements.hasNext()) {
            JsonNode element = elements.next();
            JsonNodeType eleType = element.getNodeType();

            if (eleType == JsonNodeType.STRING || eleType == JsonNodeType.NUMBER ||
                    eleType == JsonNodeType.BOOLEAN) {
                addLeafDataNode(fieldName, element.asText(),
                                MULTI_INSTANCE_LEAF_VALUE_NODE);
                dataNodeBuilder = SerializerHelper.exitDataNode(dataNodeBuilder);
            }
        }
    }

    private boolean isJsonNodeLeafList(ArrayNode jsonNode) {
        if (!jsonNode.isArray()) {
            return false;
        }
        Iterator<JsonNode> elements = jsonNode.elements();
        while (elements.hasNext()) {
            JsonNode element = elements.next();
            JsonNodeType eleType = element.getNodeType();
            if (eleType != JsonNodeType.STRING && eleType != JsonNodeType.NUMBER &&
                    eleType != JsonNodeType.BOOLEAN) {
                return false;
            }
        }
        return true;
    }
}