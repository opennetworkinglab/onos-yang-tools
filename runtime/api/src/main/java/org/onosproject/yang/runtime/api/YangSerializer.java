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

package org.onosproject.yang.runtime.api;

import org.onosproject.yang.model.DataNode;

import java.io.InputStream;

/**
 * Abstraction of entity capable of encoding and decoding arbitrary
 * {@link DataNode} structures, which are in-memory representations of
 * YANG models, to and from various external representations, e.g. XML, JSON.
 * <p>
 * This interface is not intended for use by applications, but rather only
 * by the YANG runtime implementation.
 * </p>
 */
public interface YangSerializer {

    /**
     * Returns the data format supported by this  serializer.
     *
     * @return supported data format
     */
    DataFormat supportsFormat();

    /**
     * Decodes the external representation of a configuration model from the
     * specified input stream and into an in-memory representation.
     *
     * @param external input stream carrying external representation of
     *                 configuration data
     * @param context  serialization context containing information required
     *                 for the serializer, e.g. access to model schemas
     * @return in-memory representation of configuration data
     */
    DataNode decode(InputStream external, YangSerializerContext context);

    /**
     * Encodes the internal in-memory representation of a configuration model
     * to an external representation consumable from the resulting input stream.
     *
     * @param internal in-memory representation of configuration data
     * @param context  serialization context containing information required
     *                 for the serializer, e.g. access to model schemas
     * @return input stream carrying external representation of
     * configuration data
     */
    InputStream encode(DataNode internal, YangSerializerContext context);

}
