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

package org.onosproject.yang.runtime;

import java.util.Set;

/**
 * Registry of YANG serializers available as part of YANG runtime.
 */
public interface YangSerializerRegistry {

    /**
     * Registers a new serializer. Overwrites, if serializer for a given data
     * format is already registered.
     *
     * @param serializer serializer to be registered
     */
    void registerSerializer(YangSerializer serializer);

    /**
     * Unregisters the specified serializer.
     *
     * @param serializer serializer to be unregistered
     * @throws IllegalArgumentException when input serializer is not registered
     */
    void unregisterSerializer(YangSerializer serializer);

    /**
     * Returns collection of all registered serializers.
     *
     * @return collection of serializers
     */
    Set<YangSerializer> getSerializers();
}
