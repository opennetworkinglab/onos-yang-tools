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

import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.runtime.CompositeData;
import org.onosproject.yang.runtime.CompositeStream;
import org.onosproject.yang.runtime.DefaultYangSerializerContext;
import org.onosproject.yang.runtime.RuntimeContext;
import org.onosproject.yang.runtime.YangRuntimeException;
import org.onosproject.yang.runtime.YangRuntimeService;
import org.onosproject.yang.runtime.YangSerializer;
import org.onosproject.yang.runtime.YangSerializerContext;
import org.onosproject.yang.runtime.YangSerializerRegistry;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents YANG runtime codec service implementation.
 */
public class DefaultYangRuntimeHandler implements YangRuntimeService {

    private static final Logger log = getLogger(DefaultYangModelRegistry.class);
    private static final String DF = "Data format ";
    private static final String NR = " is not registered.";
    private YangSerializerRegistry registry;
    private SchemaContext rootContext;

    /**
     * Creates a new YANG runtime manager.
     *
     * @param r serializer registry
     * @param c root's schema context
     */
    public DefaultYangRuntimeHandler(YangSerializerRegistry r,
                                     SchemaContext c) {
        registry = r;
        rootContext = c;
    }

    @Override
    public CompositeData decode(CompositeStream external, RuntimeContext c) {
        YangSerializer ys = getRegisteredSerializer(c.getDataFormat());
        YangSerializerContext sc =
                new DefaultYangSerializerContext(rootContext,
                                                 c.getProtocolAnnotations());
        return ys.decode(external, sc);
    }

    @Override
    public CompositeStream encode(CompositeData internal, RuntimeContext c) {
        YangSerializer ys = getRegisteredSerializer(c.getDataFormat());
        YangSerializerContext sc =
                new DefaultYangSerializerContext(rootContext,
                                                 c.getProtocolAnnotations());
        return ys.encode(internal, sc);
    }

    /**
     * Returns serializer for a given data format.
     *
     * @param df data format
     * @return YANG serializer
     */
    private YangSerializer getRegisteredSerializer(String df) {
        YangSerializer s =
                ((DefaultYangSerializerRegistry) registry).getSerializer(df);
        if (s == null) {
            log.info(DF + " {} " + NR, df);
            throw new YangRuntimeException(DF + df + NR);
        }
        return s;
    }
}
