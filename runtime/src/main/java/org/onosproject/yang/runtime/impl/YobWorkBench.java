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

import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNodeContextInfo;
import org.onosproject.yang.compiler.datamodel.YangSchemaNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerModelObject;
import org.onosproject.yang.model.ModelConverterException;
import org.onosproject.yang.runtime.YangModelRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_AUGMENT_NODE;
import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_CHOICE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.impl.YobConstants.ADD_AUGMENT_METHOD;
import static org.onosproject.yang.runtime.impl.YobConstants.ADD_TO;
import static org.onosproject.yang.runtime.impl.YobConstants.E_FAIL_TO_GET_FIELD;
import static org.onosproject.yang.runtime.impl.YobConstants.E_FAIL_TO_GET_METHOD;
import static org.onosproject.yang.runtime.impl.YobConstants.E_FAIL_TO_INVOKE_METHOD;
import static org.onosproject.yang.runtime.impl.YobConstants.E_HAS_NO_CHILD;
import static org.onosproject.yang.runtime.impl.YobConstants.L_FAIL_TO_GET_FIELD;
import static org.onosproject.yang.runtime.impl.YobConstants.L_FAIL_TO_GET_METHOD;
import static org.onosproject.yang.runtime.impl.YobConstants.L_FAIL_TO_INVOKE_METHOD;
import static org.onosproject.yang.runtime.impl.YobUtils.ANYDATA_SETTER;
import static org.onosproject.yang.runtime.impl.YobUtils.getCapitalCase;
import static org.onosproject.yang.runtime.impl.YobUtils.getInstanceOfClass;
import static org.onosproject.yang.runtime.impl.YobUtils.getQualifiedDefaultClass;

/**
 * Represents the YANG object builder's work bench corresponding to a YANG data
 * tree node.
 */
class YobWorkBench {

    private static final Logger log =
            LoggerFactory.getLogger(YobWorkBench.class);

    /**
     * Class loader to be used to load the class.
     */
    private ClassLoader classLoader;

    /**
     * Map of the non schema descendant objects.
     */
    private Map<YangSchemaNodeIdentifier, YobWorkBench> attributeMap =
            new HashMap<>();

    /**
     * Built object.
     */
    private Object builtObject;

    /**
     * Setter method to be used in parent.
     */
    private String setterInParent;

    /**
     * YANG schema node.
     */
    private YangSchemaNode schemaNode;

    /**
     * Creates an instance of YANG object builder.
     *
     * @param classLoader    class loader
     * @param builtObject    YANG object built
     * @param setterInParent setter method in parent
     * @param schemaNode     YANG schema node
     */
    YobWorkBench(ClassLoader classLoader,
                 Object builtObject, String setterInParent,
                 YangSchemaNode schemaNode) {
        this.classLoader = classLoader;
        this.setterInParent = setterInParent;
        this.builtObject = builtObject;
        this.schemaNode = schemaNode;
    }

    /**
     * Returns the class loader.
     *
     * @return class loader
     */
    ClassLoader classLoader() {
        return classLoader;
    }

    /**
     * Sets the class loader.
     *
     * @param loader class loader
     */
    void classLoader(ClassLoader loader) {
        classLoader = loader;
    }

    /**
     * Returns the setter method name.
     *
     * @return setter method name
     */
    String setterInParent() {
        return setterInParent;
    }

    /**
     * Sets the setter method name.
     *
     * @param name setter method name
     */
    void setterInParent(String name) {
        setterInParent = name;
    }

    /**
     * Returns the YANG scheme node.
     *
     * @return YANG schema node
     */
    YangSchemaNode schemaNode() {
        return schemaNode;
    }

    /**
     * Sets the YANG schema node.
     *
     * @param node YANG schema node
     */
    void schemaNode(YangSchemaNode node) {
        schemaNode = node;
    }

    /**
     * Returns the attribute map.
     *
     * @return attribute map
     */
    Map<YangSchemaNodeIdentifier, YobWorkBench> attributeMap() {
        return attributeMap;
    }

    /**
     * Sets the attribute map.
     *
     * @param attributeMap map of the non schema descendant objects
     */
    void attributeMap(Map<YangSchemaNodeIdentifier, YobWorkBench>
                              attributeMap) {
        this.attributeMap = attributeMap;
    }

    /**
     * Returns the built object.
     *
     * @return built object
     */
    Object getBuiltObject() {
        return builtObject;
    }

    /**
     * Sets the built object.
     *
     * @param obj built object
     */
    void setBuiltObject(Object obj) {
        builtObject = obj;
    }

    /**
     * Sets the model object of data node in parent object.
     *
     * @param curWb    YOB work bench for data node
     * @param dataNode data node
     * @param reg      YANG model registry
     */
    void setObject(YobWorkBench curWb, DataNode dataNode,
                   DefaultYangModelRegistry reg) {
        Object parentObj = getParentObject(reg, curWb.schemaNode());
        setObjectInParent(parentObj, curWb.setterInParent(),
                          curWb.getBuiltObject(), dataNode.type());
    }

    /**
     * Sets the model object of data node in parent object.
     *
     * @param parentObj parent object
     * @param setter    setter method name
     * @param curObj    current object
     * @param type      data node type
     */
    private static void setObjectInParent(Object parentObj, String setter,
                                          Object curObj, DataNode.Type type) {
        Class<?> parentClass = parentObj.getClass();
        String parentClassName = parentClass.getName();
        try {
            Class<?> classType = null;
            if (setter.equals(ANYDATA_SETTER)) {
                Method method = parentClass.getSuperclass()
                        .getDeclaredMethod(setter, InnerModelObject.class);
                method.invoke(parentObj, curObj);
                return;
            }
            Field fieldName = parentClass.getDeclaredField(setter);
            if (fieldName != null) {
                classType = fieldName.getType();
            }

            Method method;
            if (type == MULTI_INSTANCE_NODE) {
                if (fieldName != null) {
                    ParameterizedType genericTypes =
                            (ParameterizedType) fieldName.getGenericType();
                    classType = (Class<?>) genericTypes.getActualTypeArguments()[0];
                }
                method = parentClass
                        .getDeclaredMethod(ADD_TO + getCapitalCase(setter), classType);
            } else {
                method = parentClass.getDeclaredMethod(setter, classType);
            }

            method.invoke(parentObj, curObj);
        } catch (NoSuchFieldException e) {
            log.error(L_FAIL_TO_GET_FIELD, parentClassName);
            throw new ModelConverterException(E_FAIL_TO_GET_FIELD + parentClassName, e);
        } catch (NoSuchMethodException e) {
            log.error(L_FAIL_TO_GET_METHOD, parentClassName);
            throw new ModelConverterException(E_FAIL_TO_GET_METHOD + parentClassName, e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error(L_FAIL_TO_INVOKE_METHOD, parentClassName);
            throw new ModelConverterException(E_FAIL_TO_INVOKE_METHOD + parentClassName, e);
        }
    }

    /**
     * Returns the parent builder object in which the child object can be set.
     *
     * @param reg        YANG model registry
     * @param schemaNode descendant schema node for whom the builder is
     *                   required
     * @return parent builder object
     */
    Object getParentObject(DefaultYangModelRegistry reg,
                           YangSchemaNode schemaNode) {

        YangSchemaNodeIdentifier targetNode =
                schemaNode.getYangSchemaNodeIdentifier();

        YobWorkBench curWorkBench = this;
        YangSchemaNode nonSchemaHolder;
        do {

            //Current Schema node context
            YangSchemaNodeContextInfo schemaContext;
            YangSchemaNode parentSchema = null;
            try {
                //Find the new schema context node.
                parentSchema = curWorkBench.schemaNode();
                schemaContext = parentSchema.getChildSchema(targetNode);

            } catch (DataModelException e) {
                throw new ModelConverterException(parentSchema.getName() +
                                                          E_HAS_NO_CHILD +
                                                          targetNode.getName(), e);
            }

            nonSchemaHolder = schemaContext.getContextSwitchedNode();

            //If the descendant schema node is in switched context
            if (nonSchemaHolder != null) {

                YangSchemaNodeIdentifier nonSchemaIdentifier =
                        nonSchemaHolder.getYangSchemaNodeIdentifier();

                //check if the descendant builder container is already available
                YobWorkBench childWorkBench =
                        curWorkBench.attributeMap.get(nonSchemaIdentifier);

                if (childWorkBench == null) {
                    YobWorkBench newWorkBench = getNewChildWorkBench(
                            schemaContext, targetNode, curWorkBench, reg);

                    curWorkBench.attributeMap.put(nonSchemaIdentifier,
                                                  newWorkBench);
                    curWorkBench = newWorkBench;
                } else {
                    curWorkBench = childWorkBench;
                }
            }

        } while (nonSchemaHolder != null);
        return curWorkBench.getBuiltObject();
    }

    /**
     * Creates a new builder container object corresponding to a context
     * switch schema node.
     *
     * @param childContext schema context of immediate child
     * @param targetNode   final node whose parent builder is
     *                     required
     * @param curWorkBench current context builder container
     * @param registry     model registry
     * @return new builder container object corresponding to a context
     * switch schema node
     */
    static YobWorkBench getNewChildWorkBench(
            YangSchemaNodeContextInfo childContext,
            YangSchemaNodeIdentifier targetNode, YobWorkBench curWorkBench,
            DefaultYangModelRegistry registry) {

        YangSchemaNode ctxSwitchedNode = childContext.getContextSwitchedNode();
        String name;

         /* This is the first child trying to set its object in the
         current context. */
        String setterInParent = ctxSwitchedNode.getJavaAttributeName();

        /* If current switched context is choice, then case class needs to be
         used. */
        if (ctxSwitchedNode.getYangSchemaNodeType() == YANG_CHOICE_NODE) {
            try {
                childContext = ctxSwitchedNode.getChildSchema(targetNode);
                ctxSwitchedNode = childContext.getContextSwitchedNode();
                name = getQualifiedDefaultClass(
                        childContext.getContextSwitchedNode());

            } catch (DataModelException e) {
                throw new ModelConverterException(ctxSwitchedNode.getName() +
                                                          E_HAS_NO_CHILD +
                                                          targetNode.getName(), e);
            }
        } else if (ctxSwitchedNode.getYangSchemaNodeType() ==
                YANG_AUGMENT_NODE) {
            name = getQualifiedDefaultClass(ctxSwitchedNode);
            setterInParent = YobUtils.getQualifiedinterface(ctxSwitchedNode);
        } else {
            name = getQualifiedDefaultClass(childContext.getSchemaNode());
        }

        ClassLoader newClassesLoader = YobUtils.getTargetClassLoader(
                curWorkBench.classLoader, childContext, registry);

        Object obj = getInstanceOfClass(newClassesLoader, name);
        return new YobWorkBench(newClassesLoader, obj, setterInParent,
                                ctxSwitchedNode);
    }

    static void addInAugmentation(Object builder,
                                  Object instance) {
        Class<?> builderClass = builder.getClass();
        Class<?> baseClass = builderClass.getSuperclass();
        try {
            Method method = baseClass.getDeclaredMethod(ADD_AUGMENT_METHOD,
                                                        InnerModelObject.class);
            method.invoke(builder, instance);
        } catch (NoSuchMethodException e) {
            log.error(L_FAIL_TO_GET_METHOD, ADD_AUGMENT_METHOD);
            throw new ModelConverterException(E_FAIL_TO_GET_METHOD + ADD_AUGMENT_METHOD, e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error(L_FAIL_TO_INVOKE_METHOD, ADD_AUGMENT_METHOD);
            throw new ModelConverterException(E_FAIL_TO_INVOKE_METHOD + ADD_AUGMENT_METHOD, e);
        }
    }

    /**
     * Builds the non schema objects and maintain it in the contained schema
     * node.
     *
     * @param reg YANG model registry
     */
    void buildNonSchemaAttributes(YangModelRegistry reg) {

        for (Map.Entry<YangSchemaNodeIdentifier, YobWorkBench> entry :
                attributeMap.entrySet()) {
            YobWorkBench childWorkBench = entry.getValue();
            YangSchemaNode childSchema = childWorkBench.schemaNode();
            childWorkBench.buildObject(reg);

            if (childSchema.getYangSchemaNodeType() == YANG_AUGMENT_NODE) {
                addInAugmentation(builtObject,
                                  childWorkBench.getBuiltObject());
                continue;
            }

            setObjectInParent(builtObject, childWorkBench.setterInParent,
                              childWorkBench.getBuiltObject(), SINGLE_INSTANCE_NODE);
        }
    }

    /**
     * Set the operation type attribute and build the object from the builder
     * object, by invoking the build method.
     *
     * @param reg YANG model registry
     */
    void buildObject(YangModelRegistry reg) {
        buildNonSchemaAttributes(reg);
    }
}
