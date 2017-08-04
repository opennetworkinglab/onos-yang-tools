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

package org.onosproject.yang.serializers.xml;


import org.onosproject.yang.model.DataNode;

import java.util.HashMap;
import java.util.Map;

import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;

/**
 * Represents an xml serializer handle factory to create different types of
 * data node.
 */
public final class SerializerHandlerFactory {

    /**
     * Map of xml codec handler.
     */
    private final Map<DataNode.Type, XmlSerializerHandler> handlerMap;

    /**
     * Creates a new codec handler factory.
     */
    private SerializerHandlerFactory() {
        handlerMap = new HashMap<>();
        handlerMap.put(SINGLE_INSTANCE_NODE,
                       new XmlSerializerNodeHandler());
        handlerMap.put(MULTI_INSTANCE_NODE,
                       new XmlSerializerNodeHandler());
        handlerMap.put(SINGLE_INSTANCE_LEAF_VALUE_NODE,
                       new XmlSerializerLeafHandler());
        handlerMap.put(MULTI_INSTANCE_LEAF_VALUE_NODE,
                       new XmlSerializerLeafHandler());
    }

    /**
     * Returns serializer instance handler node instance.
     *
     * @param node data node
     * @return returns serializer handler node instance
     */
    public XmlSerializerHandler getSerializerHandlerForContext(
            DataNode node) {
        XmlSerializerHandler handler = handlerMap.get(node.type());
        if (handler == null) {
            throw new XmlSerializerException("Unsupported node type " + node
                    .type());
        }
        return handler;
    }

    /*
     * Bill Pugh Singleton pattern. INSTANCE won't be instantiated until the
     * LazyHolder class is loaded via a call to the instance() method below.
     */
    private static class LazyHolder {
        private static final SerializerHandlerFactory INSTANCE =
                new SerializerHandlerFactory();
    }

    /**
     * Returns a reference to the Singleton Codec Handler factory.
     *
     * @return the singleton codec handler factory
     */
    public static SerializerHandlerFactory instance() {
        return LazyHolder.INSTANCE;
    }
}
