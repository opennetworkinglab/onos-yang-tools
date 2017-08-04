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

import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.RpcContext;
import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.model.SchemaContextProvider;
import org.onosproject.yang.model.SchemaId;
import org.slf4j.Logger;

import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.SERVICE;
import static org.onosproject.yang.runtime.RuntimeHelper.getCapitalCase;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents Schema Context Provider implementation.
 */
public class DefaultSchemaContextProvider implements SchemaContextProvider {
    private static final Logger log =
            getLogger(DefaultSchemaContextProvider.class);
    public DefaultYangModelRegistry reg;

    /**
     * Creates an instance of Default Schema Context Provider.
     *
     * @param registry an instance of Default Yanf Model Registry.
     */
    public DefaultSchemaContextProvider(DefaultYangModelRegistry registry) {
        reg = registry;
    }

    @Override
    public SchemaContext getSchemaContext(ResourceId id) {
        return null;
    }

    @Override
    public RpcContext getRpcContext(ResourceId id) {
        SchemaId schemaId = id.nodeKeys().get(1).schemaId();
        String rpcname;

        SchemaContext childContext = reg.getChildContext(schemaId);
        if (childContext == null) {
            throw new IllegalArgumentException("Module is not registered or " +
                                                       "RPC name doesn't " +
                                                       "exist in module.");
        }
        YangSchemaNode childNode = (YangSchemaNode) childContext;
        rpcname = childNode.getJavaAttributeName();

        YangSchemaNode moduleNode = (YangSchemaNode) childNode.getNameSpace();
        String pkg = moduleNode.getJavaPackage();
        String moduleClassName = moduleNode.getJavaClassNameOrBuiltInType();
        String moduleQlName = pkg + PERIOD + getCapitalCase(moduleClassName);

        Class<?> moduleClass = reg.getRegisteredClass(moduleNode);
        ClassLoader classLoader = moduleClass.getClassLoader();
        Class rpcServiceIntf = null;
        try {
            rpcServiceIntf = classLoader.loadClass(moduleQlName + SERVICE);
        } catch (ClassNotFoundException e) {
            log.error("Not able to load service interface class");
        }
        return new RpcContext(rpcname, rpcServiceIntf);
    }
}
