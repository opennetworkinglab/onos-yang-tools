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

import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ModelConverterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.impl.YobConstants.E_DATA_NODE_TYPE_IS_NOT_SUPPORT;

/**
 * Represents an YANG object builder factory to create different types
 * of YANG data tree node.
 */
final class YobHandlerFactory {

    private static final Logger log =
            LoggerFactory.getLogger(YobHandlerFactory.class);

    /**
     * Map of YANG object builder handler.
     */
    private static final Map<DataNode.Type, YobHandler> HANDLER_MAP = new
            HashMap<>();

    /**
     * Creates instance of YobHandlerFactory.
     */
    private YobHandlerFactory() {
        HANDLER_MAP.put(SINGLE_INSTANCE_NODE, new YobInnerNodeHandler());
        HANDLER_MAP.put(MULTI_INSTANCE_NODE, new YobInnerNodeHandler());
        HANDLER_MAP.put(SINGLE_INSTANCE_LEAF_VALUE_NODE,
                        new YobLeafNodeHandler());
        HANDLER_MAP.put(MULTI_INSTANCE_LEAF_VALUE_NODE,
                        new YobLeafListNodeHandler());
    }

    /**
     * Returns the corresponding YOB handler for current context.
     *
     * @param type data node type
     * @return handler to create the object
     * @throws ModelConverterException if the data node type is not supported in YOB
     */
    YobHandler getYobHandlerForContext(DataNode.Type type) {
        YobHandler yobHandler = HANDLER_MAP.get(type);
        if (yobHandler == null) {
            log.error(E_DATA_NODE_TYPE_IS_NOT_SUPPORT);
            throw new ModelConverterException(E_DATA_NODE_TYPE_IS_NOT_SUPPORT);
        }
        return yobHandler;
    }

    /**
     * Returns the YANG object builder factory instance.
     *
     * @return YANG object builder factory instance
     */
    static YobHandlerFactory instance() {
        return LazyHolder.INSTANCE;
    }

    /*
     * Bill Pugh Singleton pattern. INSTANCE won't be instantiated until the
     * LazyHolder class is loaded via a call to the instance() method below.
     */
    static class LazyHolder {
        private static final YobHandlerFactory INSTANCE =
                new YobHandlerFactory();
    }
}
