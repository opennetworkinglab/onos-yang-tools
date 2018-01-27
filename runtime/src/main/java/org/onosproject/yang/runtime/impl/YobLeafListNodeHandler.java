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

import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.javadatamodel.JavaQualifiedTypeInfoContainer;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.LeafNode;
import org.onosproject.yang.model.ModelConverterException;
import org.onosproject.yang.runtime.YangModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.IDENTITYREF;
import static org.onosproject.yang.runtime.impl.YobConstants.ADD_TO;
import static org.onosproject.yang.runtime.impl.YobConstants.E_FAIL_TO_INVOKE_METHOD;
import static org.onosproject.yang.runtime.impl.YobConstants.L_FAIL_TO_INVOKE_METHOD;
import static org.onosproject.yang.runtime.impl.YobUtils.getCapitalCase;
import static org.onosproject.yang.runtime.impl.YobUtils.getChildSchemaNode;
import static org.onosproject.yang.runtime.impl.YobUtils.setDataFromStringValue;

/**
 * Represents a multi instance leaf node handler in YANG object builder.
 */
class YobLeafListNodeHandler extends YobHandler {

    private static final Logger log =
            LoggerFactory.getLogger(YobLeafListNodeHandler.class);

    @Override
    YobWorkBench createObject(YangSchemaNode schemaNode,
                                     DefaultYangModelRegistry registry) {
        // For multi instance leaf no need to create an object.
        return null;
    }

    /**
     * Builds the object.
     *
     * @param curWorkbench current work bench
     * @param registry YANG schema registry
     */
    @Override
    void buildObject(YobWorkBench curWorkbench,
                            YangModelRegistry registry) {
        // For multi instance leaf no need to build an object.
    }

    @Override
    void setInParent(DataNode leafNode,
                            YobWorkBench curWb,
                            YobWorkBench parentWb,
                            DefaultYangModelRegistry reg) {
        Class<?> parentClass = null;
        try {
            YangSchemaNode schemaNode = getChildSchemaNode(leafNode,
                                                           curWb.schemaNode());
            YangSchemaNode referredSchema = schemaNode;
            while (referredSchema.getReferredSchema() != null) {
                referredSchema = referredSchema.getReferredSchema();
            }

            String setterInParent = referredSchema.getJavaAttributeName();
            Object parentObj = curWb.getParentObject(reg, schemaNode);
            parentClass = parentObj.getClass();

            Field leafName = parentClass
                    .getDeclaredField(setterInParent);
            ParameterizedType genericListType =
                    (ParameterizedType) leafName.getGenericType();
            Class<?> genericListClass;
            if (((YangLeafList) referredSchema)
                    .getDataType().getDataType() == IDENTITYREF) {
                ParameterizedType type = (ParameterizedType)
                        genericListType.getActualTypeArguments()[0];
                genericListClass = Class.class;
            } else {
                genericListClass = (Class<?>) genericListType.getActualTypeArguments()[0];
            }

            Method setterMethod = parentClass.getDeclaredMethod(
                    ADD_TO + getCapitalCase(setterInParent), genericListClass);

            JavaQualifiedTypeInfoContainer javaQualifiedType =
                    (JavaQualifiedTypeInfoContainer) referredSchema;
            YangType<?> yangType =
                    ((YangLeafList) javaQualifiedType).getDataType();
            setDataFromStringValue(yangType.getDataType(),
                                   ((LeafNode) leafNode).value(), setterMethod,
                                   parentObj, referredSchema,
                                   curWb.schemaNode());
        } catch (NoSuchMethodException | InvocationTargetException |
                IllegalAccessException | NoSuchFieldException e) {
            log.error(L_FAIL_TO_INVOKE_METHOD, parentClass.getName());
            throw new ModelConverterException(E_FAIL_TO_INVOKE_METHOD + parentClass
                    .getName(), e);
        }
    }
}
