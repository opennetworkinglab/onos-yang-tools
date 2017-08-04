/*
 *  Copyright 2017-present Open Networking Foundation
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

package org.onosproject.yang.serializers.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.SerializerHelper;
import org.onosproject.yang.runtime.YangSerializerContext;


/**
 * Utilities for parsing URI and JSON strings.
 */
public final class DecoderUtils {

    // no instantiation
    private DecoderUtils() {
    }

    /**
     * Converts JSON data to a data node. This method should be used when
     * the URI corresponding to the JSON body is null (Thus the caller can
     * only provide a serializer context rather than a resource ID).
     *
     * @param rootNode given JSON data
     * @param context  YANG serializer context corresponding
     *                 to the target data node
     * @return data node
     */
    public static DataNode convertJsonToDataNode(ObjectNode rootNode,
                                                 YangSerializerContext context) {
        if (rootNode == null || context == null) {
            return null;
        }

        DataNode.Builder dataNodeBuilder = SerializerHelper.
                initializeDataNode(context);

        DefaultJsonWalker jsonWalker = new DefaultJsonWalker(dataNodeBuilder);
        // FIXME: Handle scenario wherein there are multiple data nodes are
        // there at root level.
        jsonWalker.walkJsonNode(null, rootNode);
        // returning the updated data node builder
        return jsonWalker.rootBuilder().build();
    }

    /**
     * Converts JSON data to a data node. This method should be used when
     * the JSON body has a valid URI associated with it (so that the caller
     * can convert the URI to a resource ID).
     *
     * @param rootNode   given JSON data
     * @param ridBuilder resource ID builder corresponding
     *                   to the target data node
     * @return data node
     */
    public static DataNode convertJsonToDataNode(ObjectNode rootNode,
                                                 ResourceId.Builder ridBuilder) {
        if (rootNode == null || ridBuilder == null) {
            return null;
        }

        DataNode.Builder dataNodeBuilder = SerializerHelper.
                initializeDataNode(ridBuilder);

        DefaultJsonWalker jsonWalker = new DefaultJsonWalker(dataNodeBuilder);
        // FIXME: Handle scenario wherein there are multiple data nodes are
        // there at root level.
        jsonWalker.walkJsonNode(null, rootNode);
        // returning the updated data node builder
        return jsonWalker.rootBuilder().build();
    }
}
