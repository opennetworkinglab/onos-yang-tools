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

import org.onosproject.yang.compiler.datamodel.RpcNotificationContainer;
import org.onosproject.yang.compiler.datamodel.YangIdentity;
import org.onosproject.yang.compiler.datamodel.YangIdentityRef;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNodeContextInfo;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerModelObject;
import org.onosproject.yang.model.KeyInfo;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafIdentifier;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.LeafModelObject;
import org.onosproject.yang.model.LeafNode;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelConverterException;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.MultiInstanceObject;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.model.SchemaId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_AUGMENT_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.nonEmpty;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.runtime.SerializerHelper.getChildSchemaContext;
import static org.onosproject.yang.runtime.impl.YobConstants.DEFAULT;
import static org.onosproject.yang.runtime.impl.YobConstants.E_DATA_TYPE_NOT_SUPPORT;
import static org.onosproject.yang.runtime.impl.YobConstants.E_FAIL_TO_CREATE_OBJ;
import static org.onosproject.yang.runtime.impl.YobConstants.E_FAIL_TO_LOAD_CLASS;
import static org.onosproject.yang.runtime.impl.YobConstants.E_FAIL_TO_LOAD_CONSTRUCTOR;
import static org.onosproject.yang.runtime.impl.YobConstants.E_FAIL_TO_LOAD_LEAF_IDENTIFIER_CLASS;
import static org.onosproject.yang.runtime.impl.YobConstants.E_INVALID_IDENTITY_DATA;
import static org.onosproject.yang.runtime.impl.YobConstants.E_REFLECTION_FAIL_TO_CREATE_OBJ;
import static org.onosproject.yang.runtime.impl.YobConstants.FROM_STRING;
import static org.onosproject.yang.runtime.impl.YobConstants.L_FAIL_TO_CREATE_OBJ;
import static org.onosproject.yang.runtime.impl.YobConstants.L_FAIL_TO_LOAD_CLASS;
import static org.onosproject.yang.runtime.impl.YobConstants.L_REFLECTION_FAIL_TO_CREATE_OBJ;
import static org.onosproject.yang.runtime.impl.YobConstants.OF;
import static org.onosproject.yang.runtime.impl.YobConstants.OP_PARAM;
import static org.onosproject.yang.runtime.impl.YobConstants.PERIOD;

/**
 * Utils to support object creation.
 */
final class YobUtils {

    public static final String FORWARD_SLASH = "/";
    private static final Logger log = LoggerFactory.getLogger(YobUtils.class);
    private static final String ENUM_LEAF_IDENTIFIER = "$LeafIdentifier";
    static final String ANYDATA_SETTER = "addAnydata";

    // no instantiation
    private YobUtils() {
    }

    /**
     * Sets data from string value in parent method.
     *
     * @param type         refers to YANG data type
     * @param value        value argument is used to set the value in method
     * @param parentSetter Invokes the underlying method represented
     *                     by this parentSetter
     * @param parentObj    the parentObject is to invoke the underlying method
     * @param schemaNode   schema information
     * @param parentSchema schema information of parent
     * @throws InvocationTargetException if failed to invoke method
     * @throws IllegalAccessException    if member cannot be accessed
     * @throws NoSuchMethodException     if method is not found
     */
    static void setDataFromStringValue(YangDataTypes type,
                                       Object value,
                                       Method parentSetter,
                                       Object parentObj,
                                       YangSchemaNode schemaNode,
                                       YangSchemaNode parentSchema)
            throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException {
        switch (type) {
            case INT8:
            case UINT8:
            case INT16:
            case UINT16:
            case INT32:
            case UINT32:
            case INT64:
            case UINT64:
            case BOOLEAN:
            case STRING:
            case DECIMAL64:
            case INSTANCE_IDENTIFIER:
                parentSetter.invoke(parentObj, value);
                break;

            case BINARY:
                byte[] data = Base64.getDecoder().decode((String) value);
                parentSetter.invoke(parentObj, data);
                break;

            case BITS:
                parseBitSetTypeInfo(parentSetter, parentObj, value,
                                    schemaNode, parentSchema);
                break;

            case DERIVED:
                parseDerivedTypeInfo(parentSetter, parentObj, value,
                                     false, schemaNode);
                break;

            case IDENTITYREF:
                parseIdentityRefInfo(parentSetter, parentObj, value,
                                     schemaNode);
                break;

            case UNION:
                parseDerivedTypeInfo(parentSetter, parentObj, value,
                                     false, schemaNode);
                break;

            case LEAFREF:
                parseLeafRefTypeInfo(parentSetter, parentObj, value,
                                     schemaNode);
                break;

            case ENUMERATION:
                parseDerivedTypeInfo(parentSetter, parentObj, value.toString(),
                                     true, schemaNode);
                break;

            case EMPTY:
                if (value == null) {
                    parentSetter.invoke(parentObj, true);
                }
                break;

            default:
                log.error(E_DATA_TYPE_NOT_SUPPORT);
        }
    }

    /**
     * To set data into parent setter method from string value for derived type.
     *
     * @param parentSetter the parent setter method to be invoked
     * @param parentObj    the parent build object on which to invoke the
     *                     method
     * @param value        value to be set in method
     * @param isEnum       flag to check whether type is enum or derived
     * @param leaf         schema node
     * @throws InvocationTargetException if failed to invoke method
     * @throws IllegalAccessException    if member cannot be accessed
     * @throws NoSuchMethodException     if the required method is not found
     */
    static void parseDerivedTypeInfo(Method parentSetter,
                                     Object parentObj,
                                     Object value,
                                     boolean isEnum,
                                     YangSchemaNode leaf)
            throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException {
        String val;
        if (value == null) {
            val = "true";
        } else {
            val = value.toString();
        }
        Class<?> childSetClass = null;
        Constructor<?> childConstructor = null;
        Object childValue = null;
        Object childObject = null;
        Method childMethod = null;
        while (leaf.getReferredSchema() != null) {
            leaf = leaf.getReferredSchema();
        }

        String qualifiedClassName = leaf.getJavaPackage() + PERIOD +
                getCapitalCase(leaf.getJavaClassNameOrBuiltInType());

        ClassLoader classLoader = parentObj.getClass().getClassLoader();
        try {
            childSetClass = classLoader.loadClass(qualifiedClassName);
        } catch (ClassNotFoundException e) {
            log.error(L_FAIL_TO_LOAD_CLASS, qualifiedClassName);
        }

        if (!isEnum) {
            if (childSetClass != null) {
                childConstructor = childSetClass.getDeclaredConstructor();
            }

            if (childConstructor != null) {
                childConstructor.setAccessible(true);
            }

            try {
                if (childConstructor != null) {
                    childObject = childConstructor.newInstance();
                }
            } catch (InstantiationException e) {
                log.error(E_FAIL_TO_LOAD_CONSTRUCTOR, qualifiedClassName);
            }
            if (childSetClass != null) {
                childMethod = childSetClass
                        .getDeclaredMethod(FROM_STRING, String.class);
            }
        } else {
            if (childSetClass != null) {
                childMethod = childSetClass.getDeclaredMethod(OF, String.class);
            }
        }
        if (childMethod != null) {
            childValue = childMethod.invoke(childObject, val);
        }
        parentSetter.invoke(parentObj, childValue);
    }

    /**
     * To set data into parent setter method from string value for bits type.
     *
     * @param parentSetterMethod the parent setter method to be invoked
     * @param parentObject       the parent build object on which to invoke the
     *                           method
     * @param leafValue          value to be set in method
     * @param leaf               schema information
     * @param parentSchema       schema information of parent
     * @throws InvocationTargetException if failed to invoke method
     * @throws IllegalAccessException    if member cannot be accessed
     * @throws NoSuchMethodException     if the required method is not found
     */
    static void parseBitSetTypeInfo(Method parentSetterMethod,
                                    Object parentObject,
                                    Object leafValue,
                                    YangSchemaNode leaf,
                                    YangSchemaNode parentSchema)
            throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException {
        Class<?> childSetClass = null;
        Object childValue = null;
        Object childObject = null;
        Method childMethod = null;

        while (leaf.getReferredSchema() != null) {
            leaf = leaf.getReferredSchema();
        }

        String pName = parentSchema.getJavaClassNameOrBuiltInType()
                .toLowerCase() + PERIOD;

        String qualifiedClassName = parentSchema.getJavaPackage() + PERIOD +
                pName + getCapitalCase(leaf.getJavaAttributeName());

        ClassLoader classLoader = parentObject.getClass().getClassLoader();

        try {
            childSetClass = classLoader.loadClass(qualifiedClassName);
        } catch (ClassNotFoundException e) {
            log.error(L_FAIL_TO_LOAD_CLASS, qualifiedClassName);
        }

        if (childSetClass != null) {
            childMethod = childSetClass.getDeclaredMethod(FROM_STRING, String.class);
        }
        if (childMethod != null) {
            childValue = childMethod.invoke(childObject, leafValue);
        }

        parentSetterMethod.invoke(parentObject, childValue);

    }

    /**
     * To set data into parent setter method from string value for leafref type.
     *
     * @param parentSetter the parent setter method to be invoked
     * @param parentObject the parent build object on which to invoke
     *                     the method
     * @param leafValue    leaf value to be set
     * @param schemaNode   schema information
     * @throws InvocationTargetException if method could not be invoked
     * @throws IllegalAccessException    if method could not be accessed
     * @throws NoSuchMethodException     if method does not exist
     */
    static void parseLeafRefTypeInfo(Method parentSetter, Object parentObject,
                                     Object leafValue,
                                     YangSchemaNode schemaNode)
            throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException {
        while (schemaNode.getReferredSchema() != null) {
            schemaNode = schemaNode.getReferredSchema();
        }

        YangLeafRef leafRef;
        if (schemaNode instanceof YangLeaf) {
            leafRef = (YangLeafRef) ((YangLeaf) schemaNode)
                    .getDataType().getDataTypeExtendedInfo();
        } else {
            leafRef = (YangLeafRef) ((YangLeafList) schemaNode)
                    .getDataType().getDataTypeExtendedInfo();
        }

        YangType type = leafRef.getEffectiveDataType();
        Object refLeaf = leafRef.getReferredLeafOrLeafList();
        YangLeavesHolder parent;
        if (refLeaf instanceof YangLeaf) {
            parent = ((YangLeaf) refLeaf).getContainedIn();
        } else {
            parent = ((YangLeafList) refLeaf).getContainedIn();
        }
        setDataFromStringValue(type.getDataType(), leafValue, parentSetter,
                               parentObject, (YangSchemaNode) refLeaf,
                               (YangSchemaNode) parent);

    }

    /**
     * Returns class loader.
     *
     * @param schemaNode schema information
     * @param reg        YANG model registry
     * @return class loader
     */
    static ClassLoader getClassLoader(YangSchemaNode schemaNode,
                                      DefaultYangModelRegistry reg) {

        YangSchemaNode curNode = schemaNode;
        while (!(curNode instanceof RpcNotificationContainer)) {
            curNode = ((YangNode) curNode).getParent();
        }

        Class<?> regClass = reg.getRegisteredClass(curNode);
        return regClass.getClassLoader();
    }

    /**
     * Returns the class loader to be used for the switched context schema node.
     *
     * @param curLoader current context class loader
     * @param context   switched context
     * @param reg       schema registry
     * @return class loader to be used for the switched context schema node
     */
    static ClassLoader getTargetClassLoader(ClassLoader curLoader,
                                            YangSchemaNodeContextInfo context,
                                            DefaultYangModelRegistry
                                                    reg) {
        YangSchemaNode augment = context.getContextSwitchedNode();
        if (augment.getYangSchemaNodeType() == YANG_AUGMENT_NODE) {
            YangSchemaNode parent = ((YangNode) augment).getParent();
            while (((YangNode) parent).getParent() != null) {
                parent = ((YangNode) parent).getParent();
            }
            Class<?> moduleClass = reg.getRegisteredClass(parent);
            if (moduleClass == null) {
                throw new ModelConverterException(E_FAIL_TO_LOAD_CLASS + parent
                        .getJavaClassNameOrBuiltInType());
            }
            return moduleClass.getClassLoader();
        }
        return curLoader;
    }

    /**
     * Returns the qualified default / op param class.
     *
     * @param schemaNode schema node of the required class
     * @return qualified default / op param class name
     */
    static String getQualifiedDefaultClass(YangSchemaNode schemaNode) {
        String packageName = schemaNode.getJavaPackage();
        String className = getCapitalCase(
                schemaNode.getJavaClassNameOrBuiltInType());

        if (schemaNode instanceof RpcNotificationContainer) {
            return packageName + PERIOD + className + OP_PARAM;
        }

        return packageName + PERIOD + DEFAULT + className;
    }

    /**
     * Returns the qualified interface name.
     *
     * @param schemaNode schema node of the required class
     * @return qualified interface name
     */
    static String getQualifiedinterface(YangSchemaNode schemaNode) {
        String packageName = schemaNode.getJavaPackage();
        String className = getCapitalCase(
                schemaNode.getJavaClassNameOrBuiltInType());

        return packageName + PERIOD + className;
    }

    /**
     * Returns the capital cased first letter of the given string.
     *
     * @param name string to be capital cased
     * @return capital cased string
     */
    static String getCapitalCase(String name) {
        return name.substring(0, 1).toUpperCase() +
                name.substring(1);
    }

    /**
     * To set data into parent setter method from string value for identity ref.
     *
     * @param parentSetterMethod the parent setter method to be invoked
     * @param parentObject       the parent build object on which to invoke
     *                           the method
     * @param leafValue          leaf value to be set
     * @param schemaNode         schema information
     * @throws InvocationTargetException if method could not be invoked
     * @throws IllegalAccessException    if method could not be accessed
     * @throws NoSuchMethodException     if method does not exist
     */
    static void parseIdentityRefInfo(Method parentSetterMethod,
                                     Object parentObject,
                                     Object leafValue,
                                     YangSchemaNode schemaNode)
            throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException {
        Class<?> childSetClass = null;
        Object childValue = null;
        Method childMethod = null;

        while (schemaNode.getReferredSchema() != null) {
            schemaNode = schemaNode.getReferredSchema();
        }

        String qualifiedClassName;
        YangType type;
        if (schemaNode instanceof YangLeaf) {
            type = ((YangLeaf) schemaNode).getDataType();
        } else {
            type = ((YangLeafList) schemaNode).getDataType();
        }

        YangIdentityRef identityRef = null;
        YangIdentity derivedId;
        if (type.getDataType() == YangDataTypes.LEAFREF && schemaNode
                .getJavaPackage().equals(YobConstants.JAVA_LANG)) {
            YangLeafRef leafref = ((YangLeafRef) type.getDataTypeExtendedInfo());
            YangType effectiveType = leafref.getEffectiveDataType();
            if (effectiveType.getDataType() == YangDataTypes.IDENTITYREF) {
                identityRef = ((YangIdentityRef) effectiveType
                        .getDataTypeExtendedInfo());
            }
        } else {
            identityRef = ((YangIdentityRef) type.getDataTypeExtendedInfo());
        }

        derivedId = getDerivedIdentity(leafValue.toString(), identityRef
                .getReferredIdentity());
        if (derivedId == null) {
            throw new ModelConverterException(E_INVALID_IDENTITY_DATA +
                                                      leafValue.toString());
        }
        qualifiedClassName = derivedId.getJavaPackage() + PERIOD +
                getCapitalCase(derivedId.getJavaClassNameOrBuiltInType());
        ClassLoader classLoader = parentObject.getClass().getClassLoader();
        try {
            childSetClass = classLoader.loadClass(qualifiedClassName);
        } catch (ClassNotFoundException e) {
            log.error(L_FAIL_TO_LOAD_CLASS, qualifiedClassName);
        }

        if (childSetClass != null) {
            childMethod = childSetClass
                    .getDeclaredMethod(FROM_STRING, String.class);
        }

        if (childMethod != null) {
            childValue = childMethod.invoke(null, leafValue);
        }

        parentSetterMethod.invoke(parentObject, childValue);
    }

    /**
     * Returns schema node for given data node.
     *
     * @param dataNode data node
     * @param context  schema context
     * @return child schema node
     */
    static YangSchemaNode getChildSchemaNode(DataNode dataNode,
                                             SchemaContext context) {
        SchemaId schemaId = dataNode.key().schemaId();
        SchemaContext schemaContext = getChildSchemaContext(context,
                                                            schemaId.name(),
                                                            schemaId.namespace());
        return ((YangSchemaNode) schemaContext);
    }

    /**
     * Builds leaf model object.
     *
     * @param dataNode   data node
     * @param leafHolder schema context
     * @param reg        YANG model registry
     * @return leaf model object
     */
    static ModelObject buildLeafModelObject(DataNode dataNode,
                                            YangSchemaNode leafHolder,
                                            DefaultYangModelRegistry reg) {
        if (leafHolder == null) {
            YangSchemaNode schemaNode = ((YangSchemaNode) reg
                    .getChildContext(dataNode.key().schemaId()));
            if (schemaNode instanceof YangLeaf) {
                leafHolder = ((YangSchemaNode) ((YangLeaf) schemaNode)
                        .getContainedIn());
            } else {
                leafHolder = ((YangSchemaNode) ((YangLeafList) schemaNode)
                        .getContainedIn());
            }
        }
        LeafModelObject leafObj = new LeafModelObject();
        leafObj.addValue(((LeafNode) dataNode).value());
        LeafIdentifier leafId = getLeafIdentifier(dataNode.key().schemaId(),
                                                  leafHolder, reg);
        leafObj.leafIdentifier(leafId);
        return leafObj;
    }

    /**
     * Returns leaf identifier of the leaf.
     *
     * @param id         schema id class loader
     * @param leafHolder parent of leaf
     * @param reg        YANG model registry
     * @return leaf identifier of the leaf
     */
    static LeafIdentifier getLeafIdentifier(SchemaId id,
                                            YangSchemaNode leafHolder,
                                            DefaultYangModelRegistry reg) {
        String qualName = getQualifiedDefaultClass(leafHolder);
        ClassLoader classLoader = getClassLoader(leafHolder, reg);
        try {
            Class<InnerModelObject> cls = (Class<InnerModelObject>) classLoader
                    .loadClass(qualName);
            Class<?>[] intfs = cls.getInterfaces();
            Class<?> intf = null;
            for (Class<?> in : intfs) {
                if (in.getName()
                        .equals(getJavaQualifiedInterFaceName(leafHolder))) {
                    intf = in;
                    break;
                }
            }
            String leafId;
            if (intf != null) {
                leafId = intf.getName() + ENUM_LEAF_IDENTIFIER;
            } else {
                throw new ModelConverterException(E_FAIL_TO_LOAD_LEAF_IDENTIFIER_CLASS);
            }

            Class<Enum> leafIdentifier =
                    (Class<Enum>) cls.getClassLoader().loadClass(leafId);
            Enum[] enumConst = leafIdentifier.getEnumConstants();
            for (Enum e : enumConst) {
                if (e.name().equalsIgnoreCase(id.name())) {
                    return ((LeafIdentifier) e);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new ModelConverterException(E_FAIL_TO_LOAD_CLASS, e);
        }
        return null;
    }

    /**
     * Returns class.
     *
     * @param loader class loader
     * @param name   class name
     * @return java class
     */
    static Class<?> fetchClassForNode(ClassLoader loader, String name) {
        try {
            return loader.loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new ModelConverterException(E_FAIL_TO_LOAD_CLASS + name, e);
        }
    }

    /**
     * Returns instance of class.
     *
     * @param loader class loader
     * @param name   class name
     * @return instance of class
     */
    static Object getInstanceOfClass(ClassLoader loader, String name) {
        try {
            Class<?> defaultClass = loader.loadClass(name);
            return defaultClass.newInstance();
        } catch (ClassNotFoundException e) {
            log.error(L_FAIL_TO_LOAD_CLASS, name);
            throw new ModelConverterException(E_FAIL_TO_LOAD_CLASS + name, e);
        } catch (NullPointerException e) {
            log.error(L_REFLECTION_FAIL_TO_CREATE_OBJ, name);
            throw new ModelConverterException(E_REFLECTION_FAIL_TO_CREATE_OBJ +
                                                      name, e);
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(L_FAIL_TO_CREATE_OBJ, name);
            throw new ModelConverterException(E_FAIL_TO_CREATE_OBJ + name, e);
        }
    }

    /**
     * Returns java class name.
     *
     * @param node YANG node
     * @return java class name
     */
    static String getJavaQualifiedInterFaceName(YangSchemaNode node) {
        if (node != null) {
            return node.getJavaPackage() + PERIOD
                    + getCapitalCase(node.getJavaClassNameOrBuiltInType());
        }
        return null;
    }

    /**
     * Returns key class name.
     *
     * @param node schema node
     * @return key class name
     */
    static String getKeyClassName(YangSchemaNode node) {
        if (node != null) {
            return getJavaQualifiedInterFaceName(node) + "Keys";
        }
        return null;
    }

    /**
     * Returns key leaf schema.
     *
     * @param keyleaf key leaf
     * @param node to look in
     * @return node YANG schema node
     */
    static YangLeaf getKeyLeafSchema(KeyLeaf keyleaf, YangSchemaNode
            node) {
        YangList list = ((YangList) node);
        List<YangLeaf> keyLeaves = list.getListOfLeaf();
        Iterator<YangLeaf> it = keyLeaves.iterator();
        while (it.hasNext()) {
            YangLeaf leaf = it.next();
            if (leaf.getName().equals(keyleaf.leafSchema().name())) {
                return leaf;
            }
        }
        return null;
    }


    /**
     * Adds list node key to model object id builder.
     *
     * @param midb model object identifier
     * @param reg  model registry
     * @param node YANG node
     * @param key  node key
     * @return model object id builder
     * @param <T> list class type
     * @param <K> key type
     */
    static <T extends InnerModelObject & MultiInstanceObject<K>,
            K extends KeyInfo<T>> ModelObjectId.Builder handleListKey(
            ModelObjectId.Builder midb, DefaultYangModelRegistry reg,
            YangSchemaNode node, NodeKey key) {
        ListKey listKey = (ListKey) key;
        List<KeyLeaf> keyLeaves = listKey.keyLeafs();
        String keyClassName;
        Class<KeyInfo> keyClass;
        Object value;
        String javaName = null;
        Method setter;
        if (nonEmpty(keyLeaves)) {
            String qualName = getQualifiedDefaultClass(node);
            ClassLoader classLoader = getClassLoader(node, reg);
            Class<T> listClass = ((Class<T>) fetchClassForNode(classLoader,
                                                               qualName));
            K keyObj;
            if (listClass != null) {
                keyClassName = getKeyClassName(node);
                try {
                    keyClass = (Class<KeyInfo>) listClass.getClassLoader()
                            .loadClass(keyClassName);
                    keyObj = (K) keyClass.newInstance();
                    for (KeyLeaf leaf : keyLeaves) {
                        YangLeaf leafSchema = getKeyLeafSchema(leaf, node);
                        YangDataTypes datatype = leafSchema.getDataType()
                                .getDataType();
                        javaName = getCamelCase(leaf.leafSchema().name(), null);
                        Field leafName = keyClass.getDeclaredField(javaName);
                        setter = keyClass.getDeclaredMethod(javaName,
                                                            leafName.getType());
                        value = leaf.leafValue();
                        setDataFromStringValue(datatype,
                                               value, setter, keyObj,
                                               leafSchema, node);
                        midb = midb.addChild(listClass, keyObj);
                    }
                } catch (NoSuchMethodException e) {
                    throw new ModelConverterException(
                            "Failed to load setter method for " +
                                    javaName + " in key class"
                                    + keyClassName, e);
                } catch (InvocationTargetException e) {
                    throw new ModelConverterException(
                            "Failed to invoke setter method for " +
                                    javaName + " in key class"
                                    + keyClassName, e);
                } catch (ClassNotFoundException e) {
                    throw new ModelConverterException("Failed to load key class"
                                                              + keyClassName, e);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new ModelConverterException("Failed Instantiation of key class"
                                                              + keyClassName, e);
                } catch (NoSuchFieldException e) {
                    throw new ModelConverterException("Field " + javaName + " not found", e);
                }
            }
        }
        return midb;
    }

    /**
     * Adds leaf list node key to model object id builder.
     *
     * @param midb       model object identifier
     * @param reg        model registry
     * @param schemaNode leaf schema node
     * @param key        leaf list node key
     * @return model object id builder
     */
    static ModelObjectId.Builder handleLeafListKey(
            ModelObjectId.Builder midb, DefaultYangModelRegistry reg,
            YangSchemaNode schemaNode, LeafListKey key) {
        Class<?> intf = null;
        YangSchemaNode parentSchema = ((YangSchemaNode) ((YangLeafList)
                schemaNode).getContainedIn());
        String qualName = getQualifiedDefaultClass(parentSchema);
        ClassLoader classLoader = getClassLoader(parentSchema, reg);
        Class<?> parentClass = fetchClassForNode(classLoader, qualName);
        Class<?>[] interfaces = parentClass.getInterfaces();
        for (Class<?> in : interfaces) {
            if (in.getName().equals(getJavaQualifiedInterFaceName(parentSchema))) {
                intf = in;
                break;
            }
        }
        String leafName;
        if (intf != null) {
            leafName = intf.getName() + ENUM_LEAF_IDENTIFIER;
            try {
                Class<Enum> leafId =
                        (Class<Enum>) parentClass.getClassLoader()
                                .loadClass(leafName);
                Enum[] enumConst = leafId.getEnumConstants();
                for (Enum e : enumConst) {
                    if (e.name().equalsIgnoreCase(key.schemaId().name())) {
                        midb = midb.addChild(((LeafIdentifier) e), key.value());
                        return midb;
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new ModelConverterException("Failed to load leaf identifier class." +
                                                          leafName, e);
            }
        }
        return midb;
    }


    /**
     * Adds node key for container node or single instance nodes to model
     * object id builder.
     *
     * @param midb model object identifier builder
     * @param reg  model registry
     * @param node YANG node
     * @param key  node key
     * @return model object builder with single instance node
     */
    static ModelObjectId.Builder handleNodeKey(
            ModelObjectId.Builder midb, DefaultYangModelRegistry reg,
            YangSchemaNode node, NodeKey key) {
        if (node != null) {
            if (!(node instanceof YangLeaf)) {
                String qualName = getQualifiedDefaultClass(node);
                ClassLoader classLoader = getClassLoader(node, reg);
                Class<InnerModelObject> nodeClass = (Class<InnerModelObject>)
                        fetchClassForNode(classLoader, qualName);
                if (nodeClass != null) {
                    midb = midb.addChild(nodeClass);
                }
            } else {
                midb = handleLeafInfo(midb, reg, key, node);
            }
        }
        return midb;
    }

    /**
     * Adds node key for leaf node to model object id builder.
     *
     * @param midb       model object identifier builder
     * @param reg        model registry
     * @param key        leaf node key
     * @param schemaNode YANG schema node
     * @return model object builder with single instance node
     */
    static ModelObjectId.Builder handleLeafInfo(
            ModelObjectId.Builder midb, DefaultYangModelRegistry reg,
            NodeKey key, YangSchemaNode schemaNode) {
        YangSchemaNode parentSchema = ((YangSchemaNode) ((YangLeaf) schemaNode)
                .getContainedIn());
        String qualName = getQualifiedDefaultClass(parentSchema);
        ClassLoader classLoader = getClassLoader(parentSchema, reg);
        Class<InnerModelObject> nodeClass = (Class<InnerModelObject>)
                fetchClassForNode(classLoader, qualName);
        Class<?>[] interfaces = nodeClass.getInterfaces();
        for (Class<?> intf : interfaces) {
            String leafId = intf.getName() + ENUM_LEAF_IDENTIFIER;
            try {
                Class<Enum> leafIdentifier =
                        (Class<Enum>) nodeClass.getClassLoader()
                                .loadClass(leafId);
                Enum[] enumConst = leafIdentifier.getEnumConstants();
                for (Enum e : enumConst) {
                    if (e.name().equalsIgnoreCase(key.schemaId().name())) {
                        midb = midb.addChild(((LeafIdentifier) e));
                        return midb;
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new ModelConverterException(E_FAIL_TO_LOAD_LEAF_IDENTIFIER_CLASS, e);
            }
        }
        return midb;
    }

    /**
     * Returns derived identity based on the leaf value.
     *
     * @param value      leaf value
     * @param referredId referred identity of leaf
     * @return derived identity
     */
    static YangIdentity getDerivedIdentity(String value,
                                           YangIdentity referredId) {
        if (referredId.getJavaClassNameOrBuiltInType().equalsIgnoreCase(value)) {
            return referredId;
        }

        List<YangIdentity> extendList = referredId.getExtendList();
        if (extendList != null && !extendList.isEmpty()) {
            for (YangIdentity identity : extendList) {
                if (identity.getYangSchemaNodeIdentifier().getName()
                        .equalsIgnoreCase(value)) {
                    return identity;
                }
            }
        }
        return null;
    }
}
