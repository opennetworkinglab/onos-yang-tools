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

import org.onosproject.yang.runtime.YangSerializer;
import org.onosproject.yang.runtime.YangSerializerRegistry;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents YANG serializer registry implementation.
 */
public class DefaultYangSerializerRegistry implements YangSerializerRegistry {

    private static final Logger log = getLogger(DefaultYangModelRegistry.class);
    private static final String NOT_REGISTERED = "Serializer is not " +
            "registered.";
    private final ConcurrentMap<String, YangSerializer> serializerMap;

    /**
     * Creates a new YANG serializer registry.
     */
    public DefaultYangSerializerRegistry() {
        serializerMap = new ConcurrentHashMap<>();
    }

    @Override
    public void registerSerializer(YangSerializer serializer) {
        if (serializerMap.containsKey(serializer.supportsFormat())) {
            log.info("Overwriting the registered {} data format.",
                     serializer.supportsFormat());
        }
        serializerMap.put(serializer.supportsFormat(), serializer);
    }

    @Override
    public void unregisterSerializer(YangSerializer serializer)
            throws IllegalArgumentException {
        if (!serializerMap.remove(serializer.supportsFormat(), serializer)) {
            throw new IllegalArgumentException(NOT_REGISTERED);
        }
    }

    @Override
    public Set<YangSerializer> getSerializers() {
        return new HashSet<>(serializerMap.values());
    }

    /**
     * Returns a registered YANG serializer for a given data format. Returns
     * null if no serializer is registered for a given data format.
     *
     * @param dataFormat data format
     * @return YANG serializer
     */
    public YangSerializer getSerializer(String dataFormat) {
        return serializerMap.get(dataFormat);
    }
}
