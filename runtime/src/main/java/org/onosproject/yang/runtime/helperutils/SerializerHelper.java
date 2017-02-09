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

package org.onosproject.yang.runtime.helperutils;

import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.YangSerializerContext;

import java.util.List;

import static org.onosproject.yang.model.DataNode.Builder;

/**
 * Representation of serializer helper utilities, serializer can use them to
 * build the data node and resource identifier without obtaining the schema
 * context.
 */
public final class SerializerHelper {

    // Forbid construction.
    private SerializerHelper() {
    }

    /**
     * Initializes resource identifier builder with YANG serializer context
     * information.
     *
     * @param context YANG serializer context
     * @return resource identifier builder
     */
    public static ResourceId.Builder initializeResourceId(
            YangSerializerContext context) {
        // TODO implementation
        return null;
    }

    /**
     * Adds to resource identifier builder. To add a top level logical
     * resource identifier ("/"), name as "/" should be provided.
     * <p>
     * Builder and name are mandatory inputs, In case namespace is null,
     * namespace of last key in the keylist of resource identifier builder will
     * be used. Value should only be provided for leaf-list/list.
     * <p>
     * This API will also carry out necessary schema related validations.
     *
     * @param builder   resource identifier builder
     * @param name      name of node
     * @param namespace namespace of node
     * @param value     value of node
     * @return resource identifier builder
     * @throws IllegalArgumentException when given input is not as per the
     *                                  schema context
     * @throws IllegalStateException    when a key is added under a atomic child
     */
    public static ResourceId.Builder addToResourceId(
            ResourceId.Builder builder, String name, String namespace,
            String value) {
        // TODO implementation
        return null;
    }

    /**
     * Adds to resource identifier builder, this API will be used by
     * applications which are not aware about the schema name association
     * with key's value.
     * <p>
     * Builder and name are mandatory inputs, In case namespace is null,
     * namespace of last key in the keylist of resource identifier builder will
     * be used. Value should only be provided for leaf-list/list.
     * <p>
     * This API will also carry out necessary schema related validations.
     *
     * @param builder   resource identifier builder
     * @param name      name of node
     * @param namespace namespace of node
     * @param value     ordered list of values
     * @return resource identifier builder
     * @throws IllegalArgumentException when given input is not as per the
     *                                  schema context
     * @throws IllegalStateException    when a key is added under a atomic child
     */
    public static ResourceId.Builder addToResourceId(
            ResourceId.Builder builder, String name, String namespace,
            List<String> value) {
        // TODO implementation
        return null;
    }

    /**
     * Initializes a new data node builder.
     *
     * @param builder resource identifier builder
     * @return data node builder
     */
    public static Builder initializeDataNode(ResourceId.Builder builder) {
        // TODO implementation
        return null;
    }

    /**
     * Adds a data node to a given data node builder.
     * <p>
     * Name and builder is mandatory inputs. If namespace is not provided
     * parents namespace will be added for data node. Value should be
     * provided for leaf/leaf-list. In case of leaf-list it's expected that this
     * API is called for each leaf-list instance. Callers aware about the node
     * type can opt to provide data node type, implementation will carry out
     * validations based on input type and obtained type.
     * <p>
     * This API will also carry out necessary schema related validations.
     *
     * @param builder   data node builder
     * @param name      name of data node
     * @param namespace namespace of data node
     * @param value     value of data node
     * @param type      type of data node
     * @return data node builder with added information
     * @throws IllegalArgumentException when given input is not as per the
     *                                  schema context
     * @throws IllegalStateException    when a key is added under a atomic child
     */
    public static Builder addDataNode(Builder builder,
                                      String name, String namespace,
                                      String value, DataNode.Type type) {
        // TODO implementation
        return null;
    }

    /**
     * Exits a given data node builder. It builds current data node,
     * adds it to parent's data node builder and returns parent builder.
     * <p>
     * In case current data node is topmost node (which was created using
     * last key of resource identifier), current data node will not be
     * built and null will be returned, in such case caller is expected to
     * build data node from builder.
     * <p>
     * This API will also carry out necessary exit time validations, for
     * an example validation about all key leafs presence for a list.
     *
     * @param builder data node builder
     * @return parent builder
     */
    public static Builder exitDataNode(Builder builder) {
        // TODO implementation
        return null;
    }

    /**
     * Returns resource identifier for a given data node. This API will
     * be used by serializer to obtain the resource identifier in the
     * scenario when an annotation is associated with a given data node.
     *
     * @param builder data node builder
     * @return resource identifier of the data node
     */
    public static ResourceId getResourceId(Builder builder) {
        // TODO implementation
        return null;
    }
}
