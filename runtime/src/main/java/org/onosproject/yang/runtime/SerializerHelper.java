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

import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.LeafNode;
import org.onosproject.yang.model.LeafSchemaContext;
import org.onosproject.yang.model.LeafType;
import org.onosproject.yang.model.ListSchemaContext;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.model.SingleInstanceNodeContext;
import org.onosproject.yang.model.YangNamespace;
import org.onosproject.yang.runtime.impl.DefaultYangModelRegistry;

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
    private static final String E_RESID = "Invalid resourceId builder.";

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
        ResourceId.Builder rIdBdr = ResourceId.builder();
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
            SchemaContext parentCont = (SchemaContext) builder.appInfo();
            SchemaContext child = getChildSchemaContext(
                    parentCont, name, namespace);
            if (child == null) {
                throw new IllegalArgumentException(
                        errorMsg(FMT_NOT_EXIST, name));
            }
            DataNode.Type type = child.getType();
            updateResourceId(builder, name, value, child, type);
            if (type == SINGLE_INSTANCE_LEAF_VALUE_NODE &&
                    ((YangLeaf) child).isKeyLeaf()) {
                builder.appInfo(parentCont);
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }
        return builder;
    }

    /**
     * Adds to resource identifier builder, this API will be used by
     * applications which are not aware about the schema name association
     * with key's value.
     * <p>
     * Builder and name are mandatory inputs, In case namespace is null,
     * namespace of last key in the key list of resource identifier builder will
     * be used. Value should only be provided for leaf-list/list.
     * <p>
     * In case of list its mandatory to pass either all key values of list or
     * non of them (wild card to support get operation).
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
        Object valObject;
        SchemaContext child = getChildSchemaContext(
                (SchemaContext) builder.appInfo(), name, namespace);
        namespace = child.getSchemaId().namespace();
        DataNode.Type childType = child.getType();
        try {
            if (childType == MULTI_INSTANCE_LEAF_VALUE_NODE) {
                if (value.size() > 1) {
                    throw new IllegalArgumentException(errorMsg(E_LEAFLIST));
                }
                valObject = ((LeafSchemaContext) child).fromString(
                        value.get(0));
                builder.addLeafListBranchPoint(name, namespace, valObject);
            } else if (childType == MULTI_INSTANCE_NODE) {
                // Adding list node.
                String v = null;
                builder = addToResourceId(builder, name, namespace, v);
                if (value != null && value.size() != 0) {
                    Set<String> keyLeafs = ((ListSchemaContext) child)
                            .getKeyLeaf();
                    try {
                        checkElementCount(name, keyLeafs.size(), value.size());
                    } catch (IllegalArgumentException e) {
                        throw e;
                    }

                    // After validation adding the key nodes under the list node.
                    Iterator<String> sklIter = keyLeafs.iterator();
                    Iterator<String> kvlIter = value.iterator();
                    String keyEleName;

                    while (kvlIter.hasNext()) {
                        String val = kvlIter.next();
                        keyEleName = sklIter.next();
                        SchemaContext keyChild = getChildSchemaContext(
                                (SchemaContext) builder.appInfo(), keyEleName,
                                namespace);
                        valObject = ((LeafSchemaContext) keyChild).fromString(val);
                        builder.addKeyLeaf(keyEleName, namespace, valObject);
                    }
                }
            } else {
                throw new IllegalArgumentException(
                        errorMsg(FMT_NOT_EXIST, name));
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }

        builder.appInfo(child);
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

        ExtResourceIdBldr rIdBldr = new ExtResourceIdBldr();
        rIdBldr = rIdBldr.copyBuilder(rIdBldr, builder.build());
        rIdBldr.appInfo(builder.appInfo());
        SchemaContext node = (SchemaContext) builder.appInfo();
        HelperContext info = new HelperContext();
        info.setResourceIdBuilder(null);
        info.setParentResourceIdBldr(rIdBldr);
        SchemaId sId = node.getSchemaId();
        // Creating a dummy node
        InnerNode.Builder dBldr = InnerNode.builder(
                sId.name(), sId.namespace()).type(node.getType());
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
        return addDataNode(builder, name, namespace, value, null, type);
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
     * @param builder      data node builder
     * @param name         name of data node
     * @param namespace    namespace of data node
     * @param value        value of data node
     * @param valNamespace value's namespace, either module name of namespace,
     *                     null indicates its same as leaf
     * @param type         type of data node
     * @return data node builder with added information
     * @throws IllegalArgumentException when given input is not as per the
     *                                  schema context
     * @throws IllegalStateException    when a key is added under a atomic child
     */
    public static Builder addDataNode(Builder builder,
                                      String name, String namespace,
                                      String value, String valNamespace,
                                      DataNode.Type type) {
        try {
            Object valObject;
            SchemaContext node;
            ExtResourceIdBldr rIdBldr;
            HelperContext nodeInfo;
            boolean initWithRId = false;
            HelperContext info = (HelperContext) builder.appInfo();
            ExtResourceIdBldr curBldr = info.getResourceIdBuilder();
            LeafSchemaContext schema;
            LeafType lType;

            if (curBldr != null) {
                rIdBldr = info.getResourceIdBuilder();
                node = (SchemaContext) rIdBldr.appInfo();
                nodeInfo = new HelperContext();
                initWithRId = true;
            } else {
                // If data node is initialized by resource id.
                node = (SchemaContext) info.getParentResourceIdBldr().appInfo();
                rIdBldr = info.getParentResourceIdBldr();
                nodeInfo = info;
            }

            SchemaContext childSchema = getChildSchemaContext(node, name,
                                                              namespace);
            DataNode.Type nodeType = childSchema.getType();

            if (type != null && !nodeType.equals(type)) {
                throw new IllegalArgumentException(
                        errorMsg(FMT_NOT_EXIST, name));
            }

            // Updating the namespace
            namespace = childSchema.getSchemaId().namespace();
            updateResourceId(rIdBldr, name, value, childSchema, nodeType);

            if (!initWithRId) {
                /*
                 * Adding first data node in case of if data node initialized
                 * with resource id builder.
                 */
                // TODO check based on type, handle leaf without value scenario
                // also handle list without key leaf scenario.
                switch (nodeType) {

                    case SINGLE_INSTANCE_LEAF_VALUE_NODE:
                        if (((YangLeaf) childSchema).isKeyLeaf()) {
                            throw new IllegalArgumentException(E_RESID);
                        }
                        schema = (LeafSchemaContext) childSchema;

                        lType = schema.getLeafType(value);
                        if (!lType.equals(LeafType.IDENTITYREF) && valNamespace != null) {
                            value = valNamespace + ":" + value;
                            valNamespace = null;
                        }

                        valObject = getLeaf(value, schema);
                        valNamespace = getValidValNamespace(value, schema,
                                                            valNamespace);
                        builder = LeafNode.builder(name, namespace)
                                .type(nodeType).value(valObject)
                                .valueNamespace(valNamespace).leafType(lType);
                        break;
                    case MULTI_INSTANCE_LEAF_VALUE_NODE:
                        schema = (LeafSchemaContext) childSchema;
                        lType = schema.getLeafType(value);
                        if (!lType.equals(LeafType.IDENTITYREF) && valNamespace != null) {
                            value = valNamespace + ":" + value;
                            valNamespace = null;
                        }
                        valObject = getLeafList(value, schema);
                        valNamespace = getValidValNamespace(value, schema,
                                                            valNamespace);
                        builder = LeafNode.builder(name, namespace)
                                .type(nodeType).value(valObject)
                                .valueNamespace(valNamespace).leafType(lType);
                        builder = builder.addLeafListValue(valObject);
                        break;
                    default:
                    /*
                     * Can't update the node key in dummy data node as
                     * keybuilder will be initialized only once when
                     * InnerNode.builder call is made with name and namespace.
                     */
                        builder = InnerNode.builder(name, namespace).type(nodeType);
                        break;
                }
            } else {
                switch (nodeType) {
                    case SINGLE_INSTANCE_LEAF_VALUE_NODE:
                        schema = (LeafSchemaContext) childSchema;
                        lType = schema.getLeafType(value);
                        if (!lType.equals(LeafType.IDENTITYREF) && valNamespace != null) {
                            value = valNamespace + ":" + value;
                            valNamespace = null;
                        }
                        valObject = getLeaf(value, schema);
                        valNamespace = getValidValNamespace(value, schema,
                                                            valNamespace);
                        if (((YangLeaf) childSchema).isKeyLeaf()) {
                            builder = builder.addKeyLeaf(
                                    name, namespace, valObject);
                        }
                        builder = builder.createChildBuilder(
                                name, namespace, valObject, valNamespace)
                                .type(nodeType).leafType(lType);
                        break;
                    case MULTI_INSTANCE_LEAF_VALUE_NODE:
                        schema = (LeafSchemaContext) childSchema;
                        lType = schema.getLeafType(value);
                        if (!lType.equals(LeafType.IDENTITYREF) && valNamespace != null) {
                            value = valNamespace + ":" + value;
                            valNamespace = null;
                        }
                        valObject = getLeafList(value, schema);
                        valNamespace = getValidValNamespace(value, schema,
                                                            valNamespace);
                        builder = builder.createChildBuilder(
                                name, namespace, valObject, valNamespace)
                                .type(nodeType).leafType(lType);
                        builder = builder.addLeafListValue(valObject);
                        break;
                    default:
                        builder = builder.createChildBuilder(name, namespace)
                                .type(nodeType);
                }
            }

            nodeInfo.setResourceIdBuilder(rIdBldr);
            builder.appInfo(nodeInfo);
        } catch (IllegalArgumentException e) {
            throw e;
        }
        return builder;
    }

    /**
     * Returns the corresponding datatype value object for given leaf-list
     * value.
     *
     * @param val value in string
     * @param ctx leaf schema context
     * @return object of value
     * @throws IllegalArgumentException a violation of data type rules
     */
    private static Object getLeafList(String val, LeafSchemaContext ctx)
            throws IllegalArgumentException {
        try {
            ((YangLeafList) ctx).getDataType().isValidValue(val);
        } catch (DataModelException e) {
            throw new IllegalArgumentException(e);
        }
        return ctx.fromString(val);
    }

    /**
     * Returns the corresponding datatype value object for given leaf value.
     *
     * @param val value in string
     * @param ctx leaf schema context
     * @return object of value
     * @throws IllegalArgumentException a violation of data type rules
     */
    private static Object getLeaf(String val, LeafSchemaContext ctx)
            throws IllegalArgumentException {
        try {
            ((YangLeaf) ctx).getDataType().isValidValue(val);
        } catch (DataModelException e) {
            throw new IllegalArgumentException(e);
        }
        return ctx.fromString(val);
    }


    /**
     * Returns valid value namespace which is module's namespace.
     *
     * @param val    value in string
     * @param ctx    leaf schema context
     * @param actual valNamespace either module name of namespace
     * @return validated value module's namespace
     * @throws IllegalArgumentException if input namespace is invalid
     */
    private static String getValidValNamespace(String val, LeafSchemaContext ctx,
                                               String actual)
            throws IllegalArgumentException {
        YangNamespace expected = ctx.getValueNamespace(val);
        if (actual == null) {
            if (expected == null ||
                    expected.getModuleNamespace().equals(ctx.getSchemaId().namespace())) {
                return null;
            }
        } else if (actual.equals(expected.getModuleName()) ||
                actual.equals(expected.getModuleNamespace())) {
            return expected.getModuleNamespace();
        }
        throw new IllegalArgumentException("Invalid input for value namespace");
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
        if (info.getParentResourceIdBldr() != null) {
            return info.getParentResourceIdBldr().getResourceId();
        }
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
     * @throws IllegalArgumentException on argument error
     */
    public static SchemaContext getChildSchemaContext(
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
        if (child == null) {
            throw new IllegalArgumentException(errorMsg(FMT_NOT_EXIST, name));
        }
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
    private static void updateResourceId(
            ResourceId.Builder builder, String name, String value,
            SchemaContext child, DataNode.Type type)
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

    /**
     * Returns the yang module name for given namespace.
     *
     * @param c  YANG serializer context
     * @param ns namespace of the module
     * @return namespace of the module
     */
    public static String getModuleNameFromNameSpace(YangSerializerContext c,
                                                    String ns) {

        YangSchemaNode schemaNode = ((DefaultYangModelRegistry) c.getContext())
                .getForNameSpace(ns, false);
        if (schemaNode != null) {
            return schemaNode.getName();
        }
        return null;
    }
}
