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

package org.onosproject.yang.runtime.utils;

import org.onosproject.yang.YangModel;
import org.onosproject.yang.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.onosproject.yang.compiler.plugin.utils.YangApacheUtils.getYangModel;
import static org.onosproject.yang.compiler.utils.UtilConstants.OP_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.SERVICE;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;

/**
 * Represents utility for runtime. These utilities can be used by application
 * to get YANG model for their application and also it can be used by runtime
 * to get the YANG node for given YANG model.
 */
public final class RuntimeHelper {

    /**
     * Returns YANG model for given generated class.
     *
     * @param aClass generated class for module node
     * @return YANG model
     */
    public static YangModel getModel(Class<?> aClass) {
        return getYangModel(aClass);
    }

    /**
     * Returns YANG node for given YANG model.
     *
     * @param model YANG model
     * @return YANG node for given model
     */
    public static Set<YangNode> getNodes(YangModel model) {
        Set<YangNode> nodes = new HashSet<>();
        Iterator<YangModule> it = model.getYangModules().iterator();
        YangModule module;
        InputStream is;
        if (it.hasNext()) {
            module = it.next();
            is = module.getMetadata();
            ObjectInputStream os;
            try {
                os = new ObjectInputStream(is);
                nodes.addAll((Set<YangNode>) os.readObject());
                os.close();
                is.close();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("failed to deserialize model" + e
                        .getLocalizedMessage());
            }
        }
        return nodes;
    }


    /**
     * Returns schema node's generated interface class name.
     *
     * @param schemaNode schema node
     * @return schema node's generated interface class name
     */
    public static String getInterfaceClassName(YangSchemaNode schemaNode) {
        return schemaNode.getJavaPackage() + PERIOD +
                getCapitalCase(schemaNode.getJavaClassNameOrBuiltInType());
    }

    /**
     * Returns schema node's generated op param class name.
     *
     * @param schemaNode schema node
     * @return schema node's generated op param class name
     */
    public static String getOpParamClassName(YangSchemaNode schemaNode) {
        return getInterfaceClassName(schemaNode) + OP_PARAM;
    }

    /**
     * Returns schema node's generated service class name.
     *
     * @param schemaNode schema node
     * @return schema node's generated service class name
     */
    public static String getServiceName(YangSchemaNode schemaNode) {
        return getInterfaceClassName(schemaNode) + SERVICE;
    }
}
