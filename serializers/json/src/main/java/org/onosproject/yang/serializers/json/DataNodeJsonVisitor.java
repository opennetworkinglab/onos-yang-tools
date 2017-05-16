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

import com.fasterxml.jackson.databind.node.JsonNodeType;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.LeafNode;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.runtime.YangSerializerContext;

import static org.onosproject.yang.runtime.SerializerHelper.getModuleNameFromNameSpace;
import static org.onosproject.yang.serializers.json.DataNodeSiblingPositionType.FIRST_INSTANCE;
import static org.onosproject.yang.serializers.json.DataNodeSiblingPositionType.LAST_INSTANCE;
import static org.onosproject.yang.serializers.json.DataNodeSiblingPositionType.SINGLE_INSTANCE_IN_MULTI_NODE;

/**
 * Representation implementation of DataNode visitor, which traverse data tree.
 */
public class DataNodeJsonVisitor implements DataNodeVisitor {
    private static final String COLON = ":";

    private JsonBuilder jsonBuilder;

    private YangSerializerContext jsonSerializerContext;

    /**
     * Creates an instance of data node JSON visitor.
     *
     * @param jb json builder
     * @param context yang serializer context
     */
    public DataNodeJsonVisitor(JsonBuilder jb, YangSerializerContext context) {
        jsonBuilder = jb;
        jsonSerializerContext = context;
    }

    @Override
    public void enterDataNode(DataNode dataNode,
                              DataNodeSiblingPositionType siblingType) {
        String nodeName = getNodeNameWithModuleName(dataNode.key().schemaId());
        switch (dataNode.type()) {
            case SINGLE_INSTANCE_NODE:
                jsonBuilder.addNodeTopHalf(nodeName, JsonNodeType.OBJECT);
                break;
            case MULTI_INSTANCE_NODE:
                if (siblingType == FIRST_INSTANCE ||
                        siblingType == SINGLE_INSTANCE_IN_MULTI_NODE) {
                    jsonBuilder.addNodeTopHalf(nodeName, JsonNodeType.ARRAY);
                }
                jsonBuilder.addNodeTopHalf("", JsonNodeType.OBJECT);
                break;
            case SINGLE_INSTANCE_LEAF_VALUE_NODE:
                LeafNode sLeafNode = (LeafNode) dataNode;
                jsonBuilder.addNodeWithValueTopHalf(nodeName,
                                                    sLeafNode.asString());
                break;
            case MULTI_INSTANCE_LEAF_VALUE_NODE:
                if (siblingType == FIRST_INSTANCE ||
                        siblingType == SINGLE_INSTANCE_IN_MULTI_NODE) {
                    jsonBuilder.addNodeTopHalf(nodeName, JsonNodeType.ARRAY);
                }
                LeafNode mLeafNode = (LeafNode) dataNode;
                jsonBuilder.addValueToLeafListNode(mLeafNode.asString());
                break;
            default:
                break;
        }
    }

    private String getNodeNameWithModuleName(SchemaId schemaId) {
        String nodeName = schemaId.name();
        String nameSpace = schemaId.namespace();
        String moduleName = getModuleNameFromNameSpace(jsonSerializerContext,
                                                       nameSpace);

        StringBuilder builder = new StringBuilder();

        if (nameSpace != null) {
            builder.append(moduleName);
            builder.append(COLON);
        }

        builder.append(nodeName);

        return builder.toString();
    }

    @Override
    public void exitDataNode(DataNode dataNode,
                             DataNodeSiblingPositionType siblingType) {
        switch (dataNode.type()) {
            case SINGLE_INSTANCE_NODE:
                jsonBuilder.addNodeBottomHalf(JsonNodeType.OBJECT);
                break;
            case MULTI_INSTANCE_NODE:
                jsonBuilder.addNodeBottomHalf(JsonNodeType.OBJECT);
                if (siblingType == LAST_INSTANCE ||
                        siblingType == SINGLE_INSTANCE_IN_MULTI_NODE) {
                    jsonBuilder.addNodeBottomHalf(JsonNodeType.ARRAY);
                }
                break;
            case SINGLE_INSTANCE_LEAF_VALUE_NODE:
                jsonBuilder.addNodeBottomHalf(JsonNodeType.STRING);
                break;
            case MULTI_INSTANCE_LEAF_VALUE_NODE:
                jsonBuilder.addNodeBottomHalf(JsonNodeType.STRING);
                if (siblingType == LAST_INSTANCE ||
                        siblingType == SINGLE_INSTANCE_IN_MULTI_NODE) {
                    jsonBuilder.addNodeBottomHalf(JsonNodeType.ARRAY);
                }
                break;
            default:
                break;
        }
    }
}
