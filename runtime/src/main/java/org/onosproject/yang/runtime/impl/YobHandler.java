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

import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.runtime.YangModelRegistry;

import static org.onosproject.yang.compiler.datamodel.YangNodeType.ANYDATA_NODE;
import static org.onosproject.yang.runtime.impl.YobUtils.ANYDATA_SETTER;
import static org.onosproject.yang.runtime.impl.YobUtils.getClassLoader;
import static org.onosproject.yang.runtime.impl.YobUtils.getInstanceOfClass;
import static org.onosproject.yang.runtime.impl.YobUtils.getQualifiedDefaultClass;

/**
 * Represents a YANG object builder handler to process the data node content and
 * build yang object.
 */
abstract class YobHandler {

    /**
     * Creates a YANG builder object.
     *
     * @param schemaNode schema context
     * @param reg        YANG model registry
     * @return YOB workbench for the data node
     */
    YobWorkBench createObject(YangSchemaNode schemaNode,
                              DefaultYangModelRegistry reg) {
        if (schemaNode instanceof YangRpc) {
            // object should not be created for rpc node
            return new YobWorkBench(null, null, null,
                                    schemaNode);
        }

        YangSchemaNode node = schemaNode;
        String setterName;
        YangNode n = (YangNode) node;
        /**
         * if current node parent is anydata then it need to be added into
         * anydata map, so in setter for same in parent will be addAnydata
         */
        if (n.getParent() != null && (n.getParent().getNodeType() ==
                ANYDATA_NODE)) {
            setterName = ANYDATA_SETTER;
        } else {
            setterName = schemaNode.getJavaAttributeName();
        }

        while (node.getReferredSchema() != null) {
            node = node.getReferredSchema();
        }

        String qualName = getQualifiedDefaultClass(node);
        ClassLoader classLoader = getClassLoader(node, reg);
        Object builtObject = getInstanceOfClass(classLoader, qualName);
        return new YobWorkBench(classLoader, builtObject, setterName,
                                schemaNode);
    }


    /**
     * Sets the YANG built object in corresponding parent class method.
     *
     * @param dataNode data node
     * @param childWb  YOB work bench for data node
     * @param parentWb YOB work bench for parent node
     * @param reg      YANG model registry
     */
    void setInParent(DataNode dataNode,
                     YobWorkBench childWb,
                     YobWorkBench parentWb,
                     DefaultYangModelRegistry reg) {
        parentWb.setObject(childWb, dataNode, reg);
    }

    /**
     * Builds the object.
     *
     * @param curWorkbench YOB work bench
     * @param reg          YANG model registry
     */
    void buildObject(YobWorkBench curWorkbench,
                     YangModelRegistry reg) {
        curWorkbench.buildObject(reg);
    }
}
