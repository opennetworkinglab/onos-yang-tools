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

import java.util.List;

/**
 * Service for encoding and decoding between internal and external model
 * representations.
 */
public interface YangRuntimeService {

    /**
     * Decodes the external representation of a configuration model from the
     * specified composite stream into an in-memory representation.
     * <p>
     * Resource identifier stream "URI" will get decoded to resource identifier
     * and resource data stream will get decoded to data node.
     * <p>
     * Protocols like NETCONF may opt only to have resource data without
     * resource identifier, which implies the data node construction from
     * logical root resource ("/").
     * <p>
     * Also protocols like NETCONF will have decorations around the input stream
     * which will be reported back to protocol in output. Produced
     * annotations will be in order of preorder traversal.
     *
     * @param external      composite input stream carrying external
     *                      representation of configuration data
     * @param dataFormat    data format of the provided external representation
     * @param nodeAnnotated true if annotations are expected, false otherwise
     * @return in-memory representation of configuration data with decorated
     * node information
     * @throws YangRuntimeException when fails to perform decode operation
     */
    // FIXME: This needs to be revised for symmetry with encode and mutability
    // FIXME: Entities exchanged across this boundary ought not to be mutable
    DecodedOutput decode(CompositeStream external, String dataFormat,
                         boolean nodeAnnotated);

    /**
     * Encodes the internal in-memory representation of a configuration model
     * to an external representation consumable from the resulting input stream.
     * <p>
     * Resource identifier in composite data will get encoded to resource
     * identifier stream and data node will be encoded to resource data
     * stream.
     * <p>
     * Logical root node "/" will be removed during encoding and will not be
     * part of either resource identifier or data node.
     * <p>
     * Protocols like NETCONF may opt only to have data node with
     * resource identifier as null in order to only get complete output in
     * form of body without URI.
     * <p>
     * Also protocols like NETCONF would like to provide additional
     * decorations for the node. These decoration should be in preorder
     * traversal order.
     *
     * @param internal   in-memory representation of configuration data
     * @param dataFormat expected data format of the external representation
     * @param info       decorated node information
     * @return input stream carrying external representation of
     * configuration data
     * @throws YangRuntimeException when fails to perform encode operation
     */
    // FIXME: Entities exchanged across this boundary ought not to be mutable
    CompositeStream encode(CompositeData internal, String dataFormat,
                           List<AnnotatedNodeInfo> info);
}
