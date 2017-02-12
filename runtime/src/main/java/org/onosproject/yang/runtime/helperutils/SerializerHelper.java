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

import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.LeafSchemaContext;
import org.onosproject.yang.model.ListSchemaContext;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.model.SingleInstanceNodeContext;
import org.onosproject.yang.runtime.YangSerializerContext;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.onosproject.yang.model.DataNode.Builder;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;

/**
 * Representation of serializer helper utilities, serializer can use them to
 * build the data node and resource identifier without obtaining the schema
 * context.
 */
public final class SerializerHelper {

    // Serializer helper formatted error string
    private static final String FMT_TOO_FEW =
            "Too few key parameters in %s. Expected %d; actual %d.";
    private static final String FMT_TOO_MANY =
            "Too many key parameters in %s. Expected %d; actual %d.";
    private static final String FMT_NOT_EXIST =
            "Schema node with name %s doesn't exist.";
    private static final String E_NAMESPACE =
            "NameSpace is mandatory to provide for first level node.";
    private static final String E_LEAFLIST =
            "Method is not allowed to pass multiple values for leaf-list.";

    // Name for first level child
    private static final String SLASH = "/";

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
        SchemaContext cont = context.getContext();
        SchemaId id = cont.getSchemaId();
        ExtResourceIdBldr rIdBdr = new ExtResourceIdBldr();
        rIdBdr.addBranchPointSchema(id.name(), id.namespace());
        // Adding the schema context to resource id app info.
        rIdBdr.appInfo(cont);
        return rIdBdr;
    }

    /**
     * Adds to resource identifier builder.
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
        try {
            SchemaContext child = getChildSchemaContext(
                    (SchemaContext) builder.appInfo(), name, namespace);
            if (child == null) {
                throw new IllegalArgumentException(
                        errorMsg(FMT_NOT_EXIST, name));
            }
            DataNode.Type type = child.getType();
            updateResourceId(builder, name, value, child, type);
            builder.appInfo(child);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return builder;
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
            List<String> value) throws IllegalArgumentException {
        SchemaContext child = getChildSchemaContext(
                (SchemaContext) builder.appInfo(), name, namespace);
        namespace = child.getSchemaId().namespace();
        builder.appInfo(child);
        DataNode.Type childType = child.getType();
        try {
            if (childType == MULTI_INSTANCE_LEAF_VALUE_NODE) {
                if (value.size() > 1) {
                    throw new IllegalArgumentException(errorMsg(E_LEAFLIST));
                }
                builder.addLeafListBranchPoint(name, namespace, value);
            } else if (childType == MULTI_INSTANCE_NODE) {
                Set<String> keyLeafs = ((ListSchemaContext) child)
                        .getKeyLeaf();
                int expectedCount = keyLeafs.size();

                try {
                    checkElementCount(name, expectedCount, value.size());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }

                //After validation adding the key nodes under the list node.
                Iterator<String> sklIter = keyLeafs.iterator();
                Iterator<String> kvlIter = value.iterator();
                String keyEleName;

                while (kvlIter.hasNext()) {
                    String val = kvlIter.next();
                    keyEleName = sklIter.next();
                    builder.addKeyLeaf(keyEleName, namespace, val);
                }
            } else {
                throw new IllegalArgumentException(errorMsg(FMT_NOT_EXIST,
                                                            name));
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return builder;
    }

    /**
     * Initializes a new data node builder using resource identifier builder.
     * <p>
     * This API can only be used when passed parameter resourceId builder is
     * prepared with the help of serializer helper utility.
     *
     * @param builder resource identifier builder
     * @return data node builder
     */
    public static Builder initializeDataNode(ResourceId.Builder builder) {

        if (!(builder instanceof ExtResourceIdBldr)) {
            throw new IllegalArgumentException("Invalid resourceId builder.");
        }
        SchemaContext node = (SchemaContext) builder.appInfo();
        HelperContext info = new HelperContext();
        info.setResourceIdBuilder(null);
        info.setParentResourceIdBldr((ExtResourceIdBldr) builder);
        SchemaId sId = node.getSchemaId();
        InnerNode.Builder dBldr = InnerNode.builder(sId.name(), sId.namespace());
        dBldr.appInfo(info);
        return dBldr;
    }

    /**
     * Initializes a new data node builder.
     *
     * @param context YANG serializer context
     * @return data node builder
     */
    public static Builder initializeDataNode(YangSerializerContext context) {

        SchemaContext node = context.getContext();
        SchemaId sId = node.getSchemaId();
        HelperContext info = new HelperContext();
        ExtResourceIdBldr rId = info.getResourceIdBuilder();
        rId.addBranchPointSchema(sId.name(), sId.namespace());
        rId.appInfo(node);
        info.setResourceIdBuilder(rId);
//        info.setSchemaContext(node);
        InnerNode.Builder dBlr = InnerNode.builder(sId.name(), sId.namespace());
        dBlr.type(SINGLE_INSTANCE_NODE);
        dBlr.appInfo(info);
        return dBlr;
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
        try {
            SchemaContext node;
            ExtResourceIdBldr rIdBldr;
            HelperContext nodeInfo;
            HelperContext info = (HelperContext) builder.appInfo();
            ExtResourceIdBldr curBldr = info.getResourceIdBuilder();
            boolean isCreate = false;
            if (curBldr != null) {
//                node = info.getSchemaContext();
                rIdBldr = info.getResourceIdBuilder();
                node = (SchemaContext) rIdBldr.appInfo();
                isCreate = true;
            } else {
                node = (SchemaContext) info.getParentResourceIdBldr().appInfo();
                rIdBldr = info.getParentResourceIdBldr();
            }
            SchemaContext childSchema = getChildSchemaContext(
                    node, name, namespace);
            DataNode.Type nodeType = childSchema.getType();
            if (type != null && !nodeType.equals(type)) {
                throw new IllegalArgumentException(errorMsg(FMT_NOT_EXIST, name));
            }

            updateResourceId(rIdBldr, name, value, childSchema, nodeType);
            if (isCreate) {
                switch (nodeType) {

                    case SINGLE_INSTANCE_NODE:
                    case MULTI_INSTANCE_NODE:
                        builder = builder.createChildBuilder(name, namespace)
                                .type(nodeType);
                        break;
                    case SINGLE_INSTANCE_LEAF_VALUE_NODE:
                    case MULTI_INSTANCE_LEAF_VALUE_NODE:
                        builder = builder.createChildBuilder(name, namespace, value)
                                .type(nodeType);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                errorMsg(FMT_NOT_EXIST, name));
                }

                nodeInfo = new HelperContext();
            } else {
                builder.type(nodeType);
                nodeInfo = info;
            }
//            nodeInfo.setSchemaContext(childSchema);
            nodeInfo.setResourceIdBuilder(rIdBldr);
            builder.appInfo(nodeInfo);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return builder;
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
        HelperContext info = (HelperContext) builder.appInfo();
        return info.getResourceIdBuilder().getResourceId();
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
        HelperContext info = (HelperContext) builder.appInfo();
        ExtResourceIdBldr rId = info.getResourceIdBuilder();
        SchemaContext cont = (SchemaContext) rId.appInfo();
        // Deleting the last key entry in resource id.
        if (cont.getType() == SINGLE_INSTANCE_LEAF_VALUE_NODE) {
            if (!((YangLeaf) cont).isKeyLeaf()) {
                rId.traveseToParent();
            }
        } else {
            rId.traveseToParent();
        }
        rId.appInfo(cont.getParentContext());
        return builder.exitNode();
    }

    /**
     * Returns child schema context for request name and namespace from given
     * resourceId builder.
     *
     * @param context   parent schema context
     * @param name      name of the child node
     * @param namespace namespace of the child node
     * @return schema context
     */
    private static SchemaContext getChildSchemaContext(
            SchemaContext context, String name, String namespace)
            throws IllegalArgumentException {
        SchemaContext child;
        SchemaId parentId = context.getSchemaId();
        if (namespace == null && parentId.name().equals(SLASH)) {
            throw new IllegalArgumentException(E_NAMESPACE);
        } else if (namespace == null) {
            namespace = parentId.namespace();
        }

        SchemaId id = new SchemaId(name, namespace);
        child = ((SingleInstanceNodeContext) context).getChildContext(id);
        return child;
    }

    /**
     * Checks the user supplied list of argument match's the expected value
     * or not.
     *
     * @param name     name of the parent list/leaf-list node
     * @param expected count suppose to be
     * @param actual   user supplied values count
     * @throws IllegalArgumentException when user requested multi instance node
     *                                  instance's count doesn't fit into the
     *                                  allowed instance limit
     */
    private static void checkElementCount(String name, int expected, int actual)
            throws IllegalArgumentException {
        if (expected < actual) {
            throw new IllegalArgumentException(
                    errorMsg(FMT_TOO_MANY, name, expected, actual));
        } else if (expected > actual) {
            throw new IllegalArgumentException(
                    errorMsg(FMT_TOO_FEW, name, expected, actual));
        }
    }

    /**
     * Updates running resource id for current provided builder.
     *
     * @param builder resource identifier builder
     * @param name    name of node
     * @param value   value of node
     * @param child   child schema context
     * @param type    type of data node
     */
    private static void updateResourceId(ResourceId.Builder builder, String name,
                                         String value, SchemaContext child,
                                         DataNode.Type type)
            throws IllegalArgumentException {

        Object valObject;
        switch (type) {
            case SINGLE_INSTANCE_LEAF_VALUE_NODE:
                if (((YangLeaf) child).isKeyLeaf()) {
                    valObject = ((LeafSchemaContext) child).fromString(value);
                    builder.addKeyLeaf(name, child.getSchemaId().namespace(),
                                       valObject);
                } else {
                    builder.addBranchPointSchema(name, child.getSchemaId()
                            .namespace());
                }
                break;
            case MULTI_INSTANCE_LEAF_VALUE_NODE:
                valObject = ((LeafSchemaContext) child).fromString(value);
                builder.addLeafListBranchPoint(name, child.getSchemaId()
                        .namespace(), valObject);
                break;
            case MULTI_INSTANCE_NODE:
            case SINGLE_INSTANCE_NODE:
                if (value == null) {
                    builder.addBranchPointSchema(name, child.getSchemaId()
                            .namespace());
                    break;
                }
            default:
                throw new IllegalArgumentException(
                        errorMsg(FMT_NOT_EXIST, name));
        }
        builder.appInfo(child);
    }

    /**
     * Returns the error string by filling the parameters in the given
     * formatted error string.
     *
     * @param fmt    error format string
     * @param params parameters to be filled in formatted string
     * @return error string
     */
    public static String errorMsg(String fmt, Object... params) {
        return String.format(fmt, params);
    }
}
