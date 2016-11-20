/*
 * Copyright 2016-present Open Networking Laboratory
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

import org.onosproject.yang.DataNode;

import java.io.InputStream;

/**
 * Service for encoding and decoding between internal and external model
 * representations.
 */
public interface YangRuntimeService {

    /**
     * Decodes the external representation of a configuration model from the
     * specified input stream and into an in-memory representation.
     *
     * @param external input stream carrying external representation of
     *                 configuration data
     * @param format   data format of the provided external representation
     * @return in-memory representation of configuration data
     */
    DataNode decode(InputStream external, DataFormat format);

    /**
     * Encodes the internal in-memory representation of a configuration model
     * to an external representation consumable from the resulting input stream.
     *
     * @param internal in-memory representation of configuration data
     * @param format   expected data format of the external representation
     * @return input stream carrying external representation of
     * configuration data
     */
    InputStream encode(DataNode internal, DataFormat format);

}
