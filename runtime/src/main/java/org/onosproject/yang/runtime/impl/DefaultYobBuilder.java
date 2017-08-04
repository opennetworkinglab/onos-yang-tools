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

package org.onosproject.yang.runtime.impl;

import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultModelObjectData;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;

import java.util.Iterator;
import java.util.List;

import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.nonEmpty;
import static org.onosproject.yang.runtime.DefaultDataNodeWalker.walk;
import static org.onosproject.yang.runtime.SerializerHelper.getChildSchemaContext;
import static org.onosproject.yang.runtime.impl.YobUtils.FORWARD_SLASH;
import static org.onosproject.yang.runtime.impl.YobUtils.handleLeafListKey;
import static org.onosproject.yang.runtime.impl.YobUtils.handleListKey;
import static org.onosproject.yang.runtime.impl.YobUtils.handleNodeKey;

/**
 * Represents implementation to build and obtain YANG objects from data node.
 */
public class DefaultYobBuilder {
    private final DefaultYangModelRegistry registry;
    private YangSchemaNode lastIndexNode;

    /**
     * Creates an instance of YOB builder.
     *
     * @param reg YANG model registry
     */
    public DefaultYobBuilder(DefaultYangModelRegistry reg) {
        registry = reg;
    }

    /**
     * Returns the YANG object.
     *
     * @param data resource data
     * @return model object identifier and YANG object
     */
    public ModelObjectData getYangObject(ResourceData data) {
        DefaultModelObjectData.Builder builder = DefaultModelObjectData.builder();
        ModelObjectId id = null;
        if (data.resourceId() != null) {
            id = convertRscIdToMoId(data.resourceId());
        }

        List<DataNode> dataNodes = data.dataNodes();
        if (nonEmpty(dataNodes)) {
            for (DataNode dataNode : dataNodes) {
                YobListener listener = new YobListener(lastIndexNode, registry);
                walk(listener, dataNode);
                List<ModelObject> objList = listener.modelObjectList();
                if (objList != null) {
                    for (ModelObject obj : objList) {
                        builder.addModelObject(obj);
                    }
                }
            }
        }
        return builder.identifier(id).build();
    }

    /**
     * Converts resource identifier to model object identifier.
     *
     * @param id resource identifier
     * @return model object identifier
     */
    private ModelObjectId convertRscIdToMoId(ResourceId id) {
        ModelObjectId.Builder midb = ModelObjectId.builder();

        if (id != null) {
            List<NodeKey> nodeKeys = id.nodeKeys();
            NodeKey key;
            SchemaId sId;
            if (nonEmpty(nodeKeys)) {
                Iterator<NodeKey> it = nodeKeys.iterator();
                while (it.hasNext()) {
                    key = it.next();
                    sId = key.schemaId();
                    if (sId.name().equals(FORWARD_SLASH)) {
                        continue;
                    }

                    YangSchemaNode schemaNode;
                    if (lastIndexNode == null) {
                        schemaNode = ((YangSchemaNode) registry.getChildContext(sId));
                    } else {
                        schemaNode = ((YangSchemaNode)
                                getChildSchemaContext(lastIndexNode, sId.name(),
                                                      sId.namespace()));
                    }

                    if (schemaNode instanceof YangRpc) {
                        // RPC resource id need not be converted to model
                        // object id.
                        lastIndexNode = schemaNode;
                        return null;
                    }

                    if (key instanceof ListKey) {
                        midb = handleListKey(midb, registry, schemaNode, key);
                    } else if (key instanceof LeafListKey) {
                        LeafListKey llKey = (LeafListKey) key;
                        midb = handleLeafListKey(midb, registry, schemaNode,
                                                 llKey);
                    } else {
                        midb = handleNodeKey(midb, registry, schemaNode, key);
                    }
                    if (!(schemaNode instanceof YangLeaf) && !(schemaNode
                            instanceof YangLeafList)) {
                        lastIndexNode = schemaNode;
                    }
                }
            }
        }
        return midb.build();
    }
}
