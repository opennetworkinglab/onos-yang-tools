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
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.LeafNode;
import org.onosproject.yang.model.ModelConverterException;
import org.onosproject.yang.runtime.YangModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.onosproject.yang.runtime.impl.ModelConverterUtil.isTypeEmpty;
import static org.onosproject.yang.runtime.impl.YobConstants.E_FAIL_TO_INVOKE_METHOD;
import static org.onosproject.yang.runtime.impl.YobConstants.L_FAIL_TO_INVOKE_METHOD;
import static org.onosproject.yang.runtime.impl.YobUtils.getChildSchemaNode;
import static org.onosproject.yang.runtime.impl.YobUtils.setDataFromStringValue;

/**
 * Represents a single instance leaf node handler in YANG object builder.
 */
class YobLeafNodeHandler extends YobHandler {

    private static final Logger log =
            LoggerFactory.getLogger(YobLeafNodeHandler.class);

    @Override
    YobWorkBench createObject(YangSchemaNode schemaNode,
                                     DefaultYangModelRegistry registry) {
        // For single instance leaf no need to create an object.
        return null;
    }

    /**
     * Builds the object.
     *
     * @param curWorkbench current workbench
     * @param registry YANG schema registry
     */
    @Override
    void buildObject(YobWorkBench curWorkbench,
                            YangModelRegistry registry) {
        // For single instance leaf no need to build an object.
    }

    @Override
    void setInParent(DataNode leafNode,
                            YobWorkBench curWb,
                            YobWorkBench parentWb,
                            DefaultYangModelRegistry registry) {
        Class<?> parentClass = null;
        try {
            YangSchemaNode schemaNode = getChildSchemaNode(leafNode, curWb
                    .schemaNode());
            YangSchemaNode referredSchema = schemaNode;
            while (referredSchema.getReferredSchema() != null) {
                referredSchema = referredSchema.getReferredSchema();
            }

            String setterInParent = referredSchema.getJavaAttributeName();
            Object parentObj = curWb.getParentObject(registry, schemaNode);
            parentClass = parentObj.getClass();
            YangType<?> type = ((YangLeaf) referredSchema).getDataType();
            YangDataTypes dataType = type.getDataType();
            if (((LeafNode) leafNode).value() != null || isTypeEmpty(type)) {
                Field leafName = parentClass.getDeclaredField(setterInParent);
                Method setterMethod = parentClass.getDeclaredMethod(
                        setterInParent, leafName.getType());
                setDataFromStringValue(dataType, ((LeafNode) leafNode).value(),
                                       setterMethod, parentObj, referredSchema,
                                       curWb.schemaNode());
            }
        } catch (NoSuchMethodException | InvocationTargetException |
                IllegalAccessException | NoSuchFieldException e) {
            log.error(L_FAIL_TO_INVOKE_METHOD, parentClass.getName());
            throw new ModelConverterException(E_FAIL_TO_INVOKE_METHOD + parentClass
                    .getName(), e);
        }
    }
}
