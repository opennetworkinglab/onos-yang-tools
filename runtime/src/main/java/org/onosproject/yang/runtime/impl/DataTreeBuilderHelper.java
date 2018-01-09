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
import org.onosproject.yang.compiler.datamodel.TraversalType;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangAugmentableNode;
import org.onosproject.yang.compiler.datamodel.YangCase;
import org.onosproject.yang.compiler.datamodel.YangChoice;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.model.Anydata;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.LeafNode;
import org.onosproject.yang.model.LeafSchemaContext;
import org.onosproject.yang.model.LeafType;
import org.onosproject.yang.model.ModelConverterException;
import org.onosproject.yang.model.YangNamespace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.onosproject.yang.compiler.datamodel.TraversalType.CHILD;
import static org.onosproject.yang.compiler.datamodel.TraversalType.PARENT;
import static org.onosproject.yang.compiler.datamodel.TraversalType.ROOT;
import static org.onosproject.yang.compiler.datamodel.TraversalType.SIBLING;
import static org.onosproject.yang.compiler.datamodel.YangNodeType.ANYDATA_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.nonEmpty;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.RuntimeHelper.DEFAULT_CAPS;
import static org.onosproject.yang.runtime.RuntimeHelper.PERIOD;
import static org.onosproject.yang.runtime.RuntimeHelper.getCapitalCase;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getAttributeOfObject;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getAugmentObject;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getClassLoaderForAugment;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getInterfaceClassFromImplClass;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getJavaName;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getLeafListObject;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getLeafObject;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getParentObjectOfNode;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.isAugmentNode;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.isMultiInstanceNode;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.isNodeProcessCompleted;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.isNonProcessableNode;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.isTypeEmpty;
import static org.onosproject.yang.runtime.impl.YobConstants.E_FAIL_TO_LOAD_CLASS;


/**
 * Implements traversal of YANG node and its corresponding object, resulting
 * in building of the data tree.
 */
public class DataTreeBuilderHelper {

    private static final String TRUE = "true";
    private static final String AUGMENTATIONS = "augmentations";
    private static final String FALSE = "false";

    /**
     * Default YANG model registry.
     */
    private final DefaultYangModelRegistry reg;

    /**
     * Current instance of the data node builder where the tree is built.
     */
    private DataNode.Builder extBuilder;

    /**
     * YANG root object that is required for walking along with the YANG node.
     */
    private Object rootObj;

    /**
     * YANG root node that is required for walking along with the YANG object.
     */
    private YangSchemaNode rootSchema;

    /**
     * Yang node for which exit builder is being processed.
     */
    private YangSchemaNode exitBuilderSchema;

    /**
     * Creates an instance of data tree builder helper.
     *
     * @param reg default model registry
     */
    public DataTreeBuilderHelper(DefaultYangModelRegistry reg) {
        this.reg = reg;
    }

    /**
     * Sets exit builder.
     *
     * @param extBuilder data node builder
     */
    public void setExtBuilder(DataNode.Builder extBuilder) {
        this.extBuilder = extBuilder;
    }

    /**
     * Creates data tree from the root object, by traversing through YANG data
     * model node, and simultaneously checking the object nodes presence and
     * walking the object.
     *
     * @param curSchema current root node schema
     * @param builder   data node builder
     * @param obj       current root object
     * @return data node builder
     */
    DataNode.Builder getDataTree(YangSchemaNode curSchema, DataNode.Builder builder,
                                 Object obj) {
        extBuilder = builder;
        rootObj = obj;
        rootSchema = curSchema;
        YangNode curNode = (YangNode) curSchema;
        TraversalType curTraversal = ROOT;
        DataTreeNodeInfo listNodeInfo = null;
        DataTreeNodeInfo augmentNodeInfo = null;

        do {
            /*
             * Processes the node, if it is being visited for the first time in
             * the schema, also if the schema node is being retraced in a multi
             * instance node.
             */
            if (curTraversal != PARENT || isMultiInstanceNode(curNode)) {

                if (curTraversal == PARENT && isMultiInstanceNode(curNode)) {
                    /*
                     * If the schema is being retraced for a multi-instance
                     * node, it has already entered for this multi-instance
                     * node. Now this re-processes the same schema node for
                     * any additional list object.
                     */
                    listNodeInfo = getCurNodeInfoAndTraverseBack(curNode);
                }

                if (curTraversal == ROOT && !isAugmentNode(curNode)) {
                    /*
                     * In case of RPC output, the root node is augmentative,
                     * so when the root traversal is coming for augment this
                     * flow is skipped. This adds only the root node in the
                     * data tree.
                     */
                    processApplicationRootNode();
                } else {
                    /*
                     * Gets the object corresponding to current schema node.
                     * If object exists, this adds the corresponding data node
                     * to the tree and returns the object. Else returns null.
                     */
                    Object processedObject = processCurSchemaNode(curNode,
                                                                  listNodeInfo);
                    /*
                     * Clears the list info of processed node. The next time
                     * list info is taken newly and accordingly.
                     */
                    listNodeInfo = null;
                    if (processedObject == null && !isAugmentNode(curNode)) {
                        /*
                         * Checks the presence of next sibling of the node, by
                         * breaking the complete chain of the current node,
                         * when the object value is not present, or when the
                         * list entries are completely retraced. The augment
                         * may have sibling, so this doesn't process for
                         * augment.
                         */
                        ModelConverterTraversalInfo traverseInfo =
                                getProcessableInfo(curNode);
                        curNode = traverseInfo.getYangNode();
                        curTraversal = traverseInfo.getTraverseType();
                        continue;
                        /*
                         * Irrespective of root or parent, sets the traversal
                         * type as parent, when augment node doesn't have any
                         * value. So, the other sibling augments can be
                         * processed, if present.
                         */
                    } else if (processedObject == null &&
                            isAugmentNode(curNode)) {
                        curTraversal = PARENT;
                        /*
                         * The second content in the list will be having
                         * parent traversal, in such case it cannot go to its
                         * child in the flow, so it is made as child
                         * traversal and proceeded to continue.
                         */
                    } else if (curTraversal == PARENT &&
                            isMultiInstanceNode(curNode)) {
                        curTraversal = CHILD;
                    }
                }
            }
            /*
             * Checks for the sibling augment when the first augment node is
             * getting completed. From the current augment node the previous
             * node info is taken for augment and the traversal is changed to
             * child, so as to check for the presence of sibling augment.
             */
            if (curTraversal == PARENT && isAugmentNode(curNode)) {
                curNode = ((YangAugment) curNode).getAugmentedNode();
                augmentNodeInfo = getParentInfo();
                curTraversal = CHILD;
            }
            /*
             * Creates an augment iterator for the first time or takes the
             * previous augment iterator for more than one time, whenever an
             * augmentative node arrives. If augment is present it goes back
             * for processing. If its null, the augmentative nodes process is
             * continued.
             */
            if (curTraversal != PARENT &&
                    curNode instanceof YangAugmentableNode) {
                YangNode augmentNode = getAugmentInsideSchemaNode(
                        curNode, augmentNodeInfo);
                if (augmentNode != null) {
                    curNode = augmentNode;
                    continue;
                }
            }
            /*
             * Processes the child, after processing the node. If complete
             * child depth is over, it takes up sibling and processes it.
             * Once child and sibling is over, it is traversed back to the
             * parent, without processing. In multi instance case, before
             * going to parent or schema sibling, its own list sibling is
             * processed. Skips the processing of RPC,notification and
             * augment, as these nodes are dealt in a different flow.
             */
            if (curTraversal != PARENT && curNode.getChild() != null) {
                augmentNodeInfo = null;
                listNodeInfo = null;
                curTraversal = CHILD;
                curNode = curNode.getChild();
                if (isNonProcessableNode(curNode)) {
                    ModelConverterTraversalInfo traverseInfo = getProcessableInfo(curNode);
                    curNode = traverseInfo.getYangNode();
                    curTraversal = traverseInfo.getTraverseType();
                }
            } else if (curNode.getNextSibling() != null) {
                if (isNodeProcessCompleted(curNode, curTraversal)) {
                    break;
                }
                if (isMultiInstanceNode(curNode)) {
                    listNodeInfo = getCurNodeInfoAndTraverseBack(curNode);
                    augmentNodeInfo = null;
                    continue;
                }
                curTraversal = SIBLING;
                augmentNodeInfo = null;
                traverseToParent(curNode);
                //here if current node does have a sibling node but current
                // node is equal to the root schema then it will fail because
                // our current root object does not have info about its sibling.
                if (!curNode.equals(rootSchema)) {
                    if (curNode instanceof YangAugment) {
                        curTraversal = PARENT;
                        continue;
                    }
                    if (isNonProcessableNode(curNode)) {
                        ModelConverterTraversalInfo traverseInfo = getProcessableInfo(curNode);
                        curNode = traverseInfo.getYangNode();
                        curTraversal = traverseInfo.getTraverseType();
                    } else {
                        curNode = curNode.getNextSibling();
                    }
                }
            } else {
                if (isNodeProcessCompleted(curNode, curTraversal)) {
                    break;
                }
                if (isMultiInstanceNode(curNode)) {
                    listNodeInfo = getCurNodeInfoAndTraverseBack(curNode);
                    augmentNodeInfo = null;
                    continue;
                }
                curTraversal = PARENT;
                traverseToParent(curNode);
                curNode = getParentSchemaNode(curNode);
            }
        } while (curNode != null && !curNode.equals(curSchema));
        return extBuilder;
    }

    /**
     * Returns parent schema node of current node.
     *
     * @param curNode current schema node
     * @return parent schema node
     */
    private YangNode getParentSchemaNode(YangNode curNode) {
        if (curNode instanceof YangAugment) {
            /*
             * If curNode is augment, either next augment or augmented node
             * has to be processed. So traversal type is changed to parent,
             * but node is not changed.
             */
            return curNode;
        }
        if (!curNode.equals(rootSchema)) {
            return curNode.getParent();
        }
        return curNode;
    }

    /**
     * Processes root YANG node and adds it as a child to the data tree
     * builder which is created earlier.
     */
    private void processApplicationRootNode() {
        DataTreeNodeInfo nodeInfo = new DataTreeNodeInfo();
        nodeInfo.setYangObject(rootObj);
        if (rootSchema instanceof YangList) {
            nodeInfo.type(MULTI_INSTANCE_NODE);
        } else {
            nodeInfo.type(SINGLE_INSTANCE_NODE);
        }
        extBuilder.appInfo(nodeInfo);
        exitBuilderSchema = rootSchema;
        processLeaves((YangNode) rootSchema, nodeInfo);
        processLeavesList((YangNode) rootSchema, nodeInfo);
    }

    /**
     * Traverses to parent, based on the schema node that requires to be
     * traversed. Skips traversal of parent for choice and case node, as they
     * don't get added to the data tree.
     *
     * @param curNode current YANG node
     */
    private void traverseToParent(YangNode curNode) {
        if (curNode instanceof YangCase || curNode instanceof YangChoice
                || curNode instanceof YangAugment) {
            return;
        }
        if (!curNode.equals(rootSchema)) {
            extBuilder = extBuilder.exitNode();
        }
    }

    /**
     * Returns the current data tree builder info of the data node builder, and
     * then traverses back to parent. In case of multi instance node the
     * previous node info is used for iterating through the list.
     *
     * @param curNode current YANG node
     * @return current data tree builder info
     */
    private DataTreeNodeInfo getCurNodeInfoAndTraverseBack(YangNode curNode) {
        DataTreeNodeInfo appInfo = getParentInfo();
        if (!curNode.equals(rootSchema)) {
            extBuilder = extBuilder.exitNode();
        }
        return appInfo;
    }

    /**
     * Returns augment node for an augmented node. From the list of augment
     * nodes it has, one of the nodes is taken and provided linearly. If the
     * node is not augmented or the all the augment nodes are processed, then
     * it returns null.
     *
     * @param curNode         current YANG node
     * @param augmentNodeInfo previous augment node info
     * @return YANG augment node
     */
    private YangNode getAugmentInsideSchemaNode(YangNode curNode,
                                                DataTreeNodeInfo augmentNodeInfo) {
        if (augmentNodeInfo == null) {
            List<YangAugment> augmentList = ((YangAugmentableNode) curNode)
                    .getAugmentedInfoList();
            if (nonEmpty(augmentList)) {
                DataTreeNodeInfo parentNodeInfo = getParentInfo();
                Iterator<YangAugment> augmentItr = augmentList.listIterator();
                parentNodeInfo.setAugmentIterator(augmentItr);
                return augmentItr.next();
            }
        } else if (augmentNodeInfo.getAugmentIterator() != null) {
            if (augmentNodeInfo.getAugmentIterator().hasNext()) {
                return augmentNodeInfo.getAugmentIterator().next();
            }
        }
        return null;
    }

    /**
     * Processes the current YANG node and if necessary adds it to the data tree
     * builder by extracting the information from the corresponding class object.
     *
     * @param curNode      current YANG node
     * @param listNodeInfo previous node info for list
     * @return object of the schema node
     */
    private Object processCurSchemaNode(YangNode curNode,
                                        DataTreeNodeInfo listNodeInfo) {
        DataTreeNodeInfo curNodeInfo = new DataTreeNodeInfo();
        Object nodeObj = null;
        DataTreeNodeInfo parentNodeInfo = getParentInfo();
        if (curNode instanceof YangAugment) {
            YangNode augmented = ((YangAugment) curNode).getAugmentedNode();
            String name;
            if (augmented instanceof YangCase) {
                name = augmented.getJavaAttributeName();
                Object obj;
                try {
                    obj = getAttributeOfObject(
                            parentNodeInfo.getYangObject(), name);
                } catch (NoSuchMethodException e) {
                    throw new ModelConverterException(
                            "Not processable case node with augment in " +
                                    "data tree", e);
                }
                parentNodeInfo = new DataTreeNodeInfo();
                parentNodeInfo.setYangObject(obj);
                parentNodeInfo.type(SINGLE_INSTANCE_NODE);
            }
        }

        switch (curNode.getYangSchemaNodeType()) {
            case YANG_ANYDATA_NODE:
            case YANG_SINGLE_INSTANCE_NODE:
                curNodeInfo.type(SINGLE_INSTANCE_NODE);
                nodeObj = processSingleInstanceNode(curNode, curNodeInfo,
                                                    parentNodeInfo);
                break;
            case YANG_MULTI_INSTANCE_NODE:
                curNodeInfo.type(MULTI_INSTANCE_NODE);
                nodeObj = processMultiInstanceNode(
                        curNode, curNodeInfo, listNodeInfo, parentNodeInfo);
                break;
            case YANG_CHOICE_NODE:
                nodeObj = processChoiceNode(curNode, parentNodeInfo);
                break;
            case YANG_NON_DATA_NODE:
                if (curNode instanceof YangCase) {
                    nodeObj = processCaseNode(curNode, parentNodeInfo);
                }
                break;
            case YANG_AUGMENT_NODE:
                nodeObj = processAugmentNode(curNode, parentNodeInfo);
                break;
            default:
                throw new ModelConverterException(
                        "Non processable schema node has arrived for adding " +
                                "it in data tree");
        }
        // Processes leaf/leaf-list only when object has value, else it skips.
        if (nodeObj != null) {
            processLeaves(curNode, parentNodeInfo);
            processLeavesList(curNode, parentNodeInfo);
        }
        return nodeObj;
    }

    /**
     * Processes single instance node which is added to the data tree.
     *
     * @param curNode        current YANG node
     * @param curNodeInfo    current data tree node info
     * @param parentNodeInfo parent data tree node info
     * @return object of the current node
     */
    private Object processSingleInstanceNode(YangNode curNode,
                                             DataTreeNodeInfo curNodeInfo,
                                             DataTreeNodeInfo parentNodeInfo) {
        Object childObj = getChildObject(curNode, parentNodeInfo);
        if (childObj != null) {
            curNodeInfo.setYangObject(childObj);
            curNodeInfo.type(SINGLE_INSTANCE_NODE);
            processChildNode(curNode, curNodeInfo);
        }
        return childObj;
    }

    /**
     * Processes multi instance node which has to be added to the data tree.
     * For the first instance in the list, iterator is created and added to
     * the list. For second instance or more the iterator from first instance
     * is taken and iterated through to get the object of parent.
     *
     * @param curNode        current list node
     * @param curNodeInfo    current node info for list
     * @param listNodeInfo   previous instance node info of list
     * @param parentNodeInfo parent node info of list
     * @return object of the current instance
     */
    private Object processMultiInstanceNode(YangNode curNode,
                                            DataTreeNodeInfo curNodeInfo,
                                            DataTreeNodeInfo listNodeInfo,
                                            DataTreeNodeInfo parentNodeInfo) {
        Object childObj = null;
        /*
         * When YANG list comes to this flow for first time, its data node
         * will be null. When it comes for the second or more content, then
         * the list would have been already set for that node. According to
         * set or not set this flow will be proceeded.
         */
        if (listNodeInfo == null) {
            List<Object> childObjList = (List<Object>) getChildObject(
                    curNode, parentNodeInfo);
            if (nonEmpty(childObjList)) {
                Iterator<Object> listItr = childObjList.iterator();
                if (!listItr.hasNext()) {
                    return null;
                    //TODO: Handle the subtree filtering with no list entries.
                }
                childObj = listItr.next();
                /*
                 * For that node the iterator is set. So the next time for
                 * the list this iterator will be taken.
                 */
                curNodeInfo.setListIterator(listItr);
            }
        } else {
            /*
             * If the list value comes for second or more time, that list
             * node will be having data tree builder info, where iterator can be
             * retrieved and check if any more contents are present. If
             * present those will be processed.
             */
            curNodeInfo.setListIterator(listNodeInfo.getListIterator());
            if (listNodeInfo.getListIterator().hasNext()) {
                childObj = listNodeInfo.getListIterator().next();
            }
        }
        if (childObj != null) {
            curNodeInfo.setYangObject(childObj);
            processChildNode(curNode, curNodeInfo);
        }
        return childObj;
    }

    /**
     * Processes choice node which adds a map to the parent node info of
     * choice name and the case object. The object taken for choice node is
     * of case object with choice name. Also, this Skips the addition of choice
     * to data tree.
     *
     * @param curNode        current choice node
     * @param parentNodeInfo parent data tree info
     * @return object of the choice node
     */
    Object processChoiceNode(YangNode curNode,
                             DataTreeNodeInfo parentNodeInfo) {
        /*
         * Retrieves the parent data tree info, to take the object of parent,
         * so as
         * to check the child attribute from the object.
         */
        Object childObj = getChildObject(curNode, parentNodeInfo);
        if (childObj != null) {
            Map<String, Object> choiceCaseMap = parentNodeInfo
                    .getChoiceCaseMap();
            if (choiceCaseMap == null) {
                choiceCaseMap = new HashMap<>();
                parentNodeInfo.setChoiceCaseMap(choiceCaseMap);
            }
            choiceCaseMap.put(curNode.getName(), childObj);
        }
        return childObj;
    }

    /**
     * Processes case node from the map contents that is filled by choice
     * nodes. Object of choice is taken when choice name and case class name
     * matches. When the case node is not present in the map it returns null.
     *
     * @param curNode        current case node
     * @param parentNodeInfo choice parent node info
     * @return object of the case node
     */
    Object processCaseNode(YangNode curNode,
                           DataTreeNodeInfo parentNodeInfo) {
        Object childObj = null;
        if (parentNodeInfo.getChoiceCaseMap() != null) {
            childObj = getCaseObjectFromChoice(parentNodeInfo,
                                               curNode);
        }
        if (childObj != null) {
            /*
             * Sets the case object in parent info, so that rest of the case
             * children can use it as parent. Case is not added in data tree.
             */
            parentNodeInfo.setCaseObject(childObj);
        }
        return childObj;
    }

    /**
     * Processes augment node, which is not added in the data tree, but binds
     * itself to the parent data tree node info, so rest of its child nodes can
     * use for adding themselves to the data tree. If there is no augment node
     * added in map or if the augment module is not registered,
     * then it returns null.
     *
     * @param curNode        current augment node
     * @param parentNodeInfo augment parent node info
     * @return object of the augment node
     */
    Object processAugmentNode(YangNode curNode,
                              DataTreeNodeInfo parentNodeInfo) {
        String className = curNode.getJavaClassNameOrBuiltInType();
        String pkgName = curNode.getJavaPackage();
        Object parentObj = getParentObjectOfNode(parentNodeInfo,
                                                 curNode.getParent());

        YangNode augmented = ((YangAugment) curNode).getAugmentedNode();
        if (augmented instanceof YangChoice) {
            String name = augmented.getJavaAttributeName();
            try {
                return getAttributeOfObject(
                        parentNodeInfo.getYangObject(), name);
            } catch (NoSuchMethodException e) {
                throw new ModelConverterException(
                        "Not processable case node with augment in " +
                                "data tree", e);
            }
        }

        Map augmentMap;
        try {
            augmentMap = (Map) getAugmentObject(parentObj,
                                                AUGMENTATIONS);
            if (augmentMap != null && !augmentMap.isEmpty()) {
            /*
             * Gets the registered module class. Loads the class and gets the
             * augment class.
             */
                curNode = getRootNode(curNode);
                Class moduleClass = getClassLoaderForAugment(curNode, reg);
                if (moduleClass == null) {
                    return null;
                }
                Class augmentClass = moduleClass.getClassLoader().loadClass(
                        pkgName + PERIOD + DEFAULT_CAPS + className);
                Object childObj = augmentMap.get(augmentClass);
                parentNodeInfo.setAugmentObject(childObj);
                return childObj;
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new ModelConverterException(e);
        }
        return null;
    }

    /**
     * Returns the root node from the current node.
     *
     * @param curNode current YANG node
     * @return root node
     */
    private YangNode getRootNode(YangNode curNode) {
        while (curNode.getParent() != null) {
            curNode = curNode.getParent();
        }
        return curNode;
    }

    /**
     * Returns the data tree info from the parent node, so that its own bounded
     * object can be taken out.
     *
     * @return parent node data tree node info
     */
    private DataTreeNodeInfo getParentInfo() {
        return (DataTreeNodeInfo) extBuilder.appInfo();
    }

    /**
     * Returns the child object from the parent object. Uses java name of the
     * current node to search the attribute in the parent object.
     *
     * @param curNode        current YANG node
     * @param parentNodeInfo parent data tree node info
     * @return object of the child node
     */
    Object getChildObject(YangNode curNode,
                          DataTreeNodeInfo parentNodeInfo) {
        // check current node parent linking status if current node
        if (curNode.getParent() != null && curNode.getParent()
                .getNodeType() == ANYDATA_NODE) {
            return getAnydataChildObject(curNode, parentNodeInfo);
        }
        String nodeJavaName = curNode.getJavaAttributeName();
        Object parentObj = getParentObjectOfNode(parentNodeInfo,
                                                 curNode.getParent());
        try {
            return getAttributeOfObject(parentObj, nodeJavaName);
        } catch (NoSuchMethodException e) {
            throw new ModelConverterException(e);
        }
    }

    /**
     * Returns the child object from the parent object for anydata. Uses java
     * name of the current node to search the attribute in the parent object.
     *
     * @param curNode current YANG node
     * @param info    parent data tree node info
     * @return object of the child node
     */
    private Object getAnydataChildObject(YangNode curNode,
                                         DataTreeNodeInfo info) {
        // Getting the curNode anydata parent object
        Anydata parentObj = (Anydata) getParentObjectOfNode(
                info, curNode.getParent());
        YangSchemaNode node = reg.getForNameSpace(
                curNode.getNameSpace().getModuleNamespace(), false);
        // Getting the module class
        Class<?> moduleClass = reg.getRegisteredClass(node);
        if (moduleClass == null) {
            throw new ModelConverterException(E_FAIL_TO_LOAD_CLASS + node
                    .getJavaClassNameOrBuiltInType());
        }

        // Forming the default class name for the curNode object creation.
        String className = curNode.getJavaPackage() + PERIOD + DEFAULT_CAPS +
                getCapitalCase(curNode.getJavaAttributeName());
        Class childClass;
        try {
            childClass = moduleClass.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new ModelConverterException(E_FAIL_TO_LOAD_CLASS + className, e);
        }
        if (curNode.getType().equals(SINGLE_INSTANCE_NODE)) {
            return parentObj.anydata(childClass).get(0);
        }
        return parentObj.anydata(childClass);
    }

    /**
     * Adds the child node to the data tree by taking operation type from the
     * object. Also, binds the object to the data node through data tree node
     * info.
     *
     * @param curNode     current YANG node
     * @param curNodeInfo current data tree info
     */
    private void processChildNode(YangNode curNode, DataTreeNodeInfo curNodeInfo) {
        if (extBuilder == null) {
            InnerNode.Builder builder = InnerNode.builder(
                    curNode.getName(), curNode.getNameSpace().getModuleNamespace());
            if (curNode instanceof YangList) {
                builder.type(MULTI_INSTANCE_NODE);
            } else {
                builder.type(SINGLE_INSTANCE_NODE);
            }
            extBuilder = builder;
        } else {
            extBuilder = extBuilder.createChildBuilder(
                    curNode.getName(), curNode.getNameSpace()
                            .getModuleNamespace()).type(curNodeInfo.type());
        }
        exitBuilderSchema = curNode;
        extBuilder.appInfo(curNodeInfo);
    }

    /**
     * Processes every leaf in a YANG node. Iterates through the leaf, takes
     * value from the leaf and adds it to the data tree with value. If value is
     * not present, and select leaf is set, adds it to the data tree without
     * value.
     *
     * @param yangNode leaves holder node
     * @param info     data node info
     */
    private void processLeaves(YangNode yangNode, DataTreeNodeInfo info) {
        if (yangNode instanceof YangLeavesHolder) {
            List<YangLeaf> leavesList = ((YangLeavesHolder) yangNode)
                    .getListOfLeaf();
            if (leavesList != null) {
                for (YangLeaf yangLeaf : leavesList) {
                    DataTreeNodeInfo parentInfo = getParentInfo();
                    if (yangNode instanceof YangAugment) {
                        if (info != null) {
                            parentInfo = info;
                        }
                    }
                    if (parentInfo == null) {
                        parentInfo = info;
                    }
                    Object parentObj = getParentObjectOfNode(parentInfo,
                                                             yangNode);
                    Object leafType;
                    try {
                        leafType = getAttributeOfObject(parentObj,
                                                        getJavaName(yangLeaf));
                    } catch (NoSuchMethodException e) {
                        throw new ModelConverterException(e);
                    }
                    Object obj = getLeafObject(yangNode, yangLeaf, parentObj,
                                               leafType, false);
                    if (obj != null) {
                        if (isTypeEmpty(yangLeaf.getDataType())) {
                            String empty = String.valueOf(obj);
                            if (!empty.equals(TRUE)) {
                                continue;
                            }
                            obj = null;
                        }
                        createLeafNode(yangLeaf, obj);
                    }
                }
            }
        }
    }

    /**
     * Processes every leaf-list in a YANG node for adding the value in data
     * tree.
     *
     * @param yangNode list of leaf-list holder node
     * @param info     data node info
     */
    private void processLeavesList(YangNode yangNode, DataTreeNodeInfo info) {
        if (yangNode instanceof YangLeavesHolder) {
            List<YangLeafList> listOfLeafList =
                    ((YangLeavesHolder) yangNode).getListOfLeafList();

            if (listOfLeafList != null) {
                for (YangLeafList yangLeafList : listOfLeafList) {
                    addToBuilder(yangNode, yangLeafList, info);
                }
            }
        }
    }

    /**
     * Processes the list of objects of the leaf list and adds the leaf list
     * value to the builder.
     *
     * @param yangNode YANG node
     * @param leafList YANG leaf list
     * @param info     data node info
     */
    private List<DataNode.Builder> addToBuilder(
            YangNode yangNode, YangLeafList leafList,
            DataTreeNodeInfo info) {
        DataTreeNodeInfo dnbNodeInfo = getParentInfo();
        if (yangNode instanceof YangAugment) {
            if (info != null) {
                dnbNodeInfo = info;
            }
        }
        if (dnbNodeInfo == null) {
            dnbNodeInfo = info;
        }
        Object parentObj = getParentObjectOfNode(dnbNodeInfo, yangNode);
        List<Object> obj;
        try {
            obj = (List<Object>) getAttributeOfObject(parentObj,
                                                      getJavaName(leafList));
        } catch (NoSuchMethodException e) {
            throw new ModelConverterException(e);
        }
        if (obj != null) {
            Set<Object> objects = getLeafListObject(yangNode, leafList,
                                                    parentObj, obj);
            if (!objects.isEmpty()) {
                Object o = objects.iterator().next();
                if (isTypeEmpty(leafList.getDataType())) {
                    objects.clear();
                    String empty = String.valueOf(o);
                    if (!empty.equals(TRUE)) {
                        return null;
                    }
                    objects.add(null);
                }
                return addLeafList(objects, leafList);
            }
        }
        return null;
    }

    /**
     * Adds set of leaf list values in the builder and traverses back to the
     * holder.
     *
     * @param leafListVal set of values
     * @param leafList    YANG leaf list
     * @return node builders for leaf list or null
     */
    List<DataNode.Builder> addLeafList(Set<Object> leafListVal,
                                       YangLeafList leafList) {
        LeafType ltype;
        if (extBuilder != null) {
            for (Object val : leafListVal) {
                if (val != null) {
                    ltype = leafList.getLeafType(val.toString());
                } else {
                    ltype = leafList.getLeafType(null);
                }
                String valNamespace = getValNamespace(val, leafList);
                DataNode.Builder leaf = extBuilder.createChildBuilder(
                        leafList.getName(), leafList.getNameSpace()
                                .getModuleNamespace(), val, valNamespace)
                        .leafType(ltype);
                leaf.type(MULTI_INSTANCE_LEAF_VALUE_NODE);
                leaf.addLeafListValue(val);
                extBuilder = leaf.exitNode();
            }
            return null;
        }
        //In case of root node leaf lists.
        List<DataNode.Builder> builders = new ArrayList<>();
        for (Object val : leafListVal) {
            if (val != null) {
                ltype = leafList.getLeafType(val.toString());
            } else {
                ltype = leafList.getLeafType(null);
            }
            String valNamespace = getValNamespace(val, leafList);
            DataNode.Builder leaf = LeafNode.builder(
                    leafList.getName(), leafList.getNameSpace()
                            .getModuleNamespace()).value(val)
                    .valueNamespace(valNamespace).leafType(ltype);
            leaf.type(MULTI_INSTANCE_LEAF_VALUE_NODE);
            leaf.addLeafListValue(val);
            builders.add(leaf);
        }
        return builders;
    }

    /**
     * Returns case object from the map that is bound to the parent node
     * info. For any case node, only when the key and value is matched the
     * object of the case is provided. If a match is not found, null is
     * returned.
     *
     * @param parentNodeInfo parent data tree node info
     * @param caseNode       case schema node
     * @return object of the case node
     */
    private Object getCaseObjectFromChoice(DataTreeNodeInfo parentNodeInfo,
                                           YangSchemaNode caseNode) {
        String javaName = getCapitalCase(
                caseNode.getJavaClassNameOrBuiltInType());
        YangNode parent = ((YangNode) caseNode).getParent();
        String choiceName;
        if (parent instanceof YangAugment) {
            choiceName = ((YangAugment) parent).getAugmentedNode().getName();
        } else {
            choiceName = ((YangNode) caseNode).getParent().getName();
        }
        Map<String, Object> mapObj = parentNodeInfo.getChoiceCaseMap();
        Object caseObj = mapObj.get(choiceName);
        Class<?> interfaceClass = getInterfaceClassFromImplClass(caseObj);
        return interfaceClass.getSimpleName().equals(javaName) ? caseObj : null;
    }

    /**
     * Creates data leaf node.
     *
     * @param yangLeaf YANG leaf
     * @param val      value for the leaf
     * @return datanode builder created
     */
    DataNode.Builder createLeafNode(YangLeaf yangLeaf, Object val) {
        String valNamespace = getValNamespace(val, yangLeaf);
        LeafType ltype;
        if (val != null) {
            ltype = yangLeaf.getLeafType(val.toString());
        } else {
            ltype = yangLeaf.getLeafType(null);
        }
        if (extBuilder != null) {
            //Add leaf to key leaves.
            if (yangLeaf.isKeyLeaf()) {
                extBuilder.addKeyLeaf(yangLeaf.getName(), yangLeaf
                        .getNameSpace().getModuleNamespace(), val);
            }
            //build leaf node and add to parent node.
            DataNode.Builder leaf = extBuilder.createChildBuilder(
                    yangLeaf.getName(), yangLeaf.getNameSpace()
                            .getModuleNamespace(), val, valNamespace)
                    .leafType(ltype);
            leaf.type(SINGLE_INSTANCE_LEAF_VALUE_NODE);

            extBuilder = leaf.exitNode();
            return leaf;
        }
        return LeafNode.builder(yangLeaf.getName(), yangLeaf.getNameSpace()
                .getModuleNamespace())
                .type(SINGLE_INSTANCE_LEAF_VALUE_NODE)
                .value(val).valueNamespace(valNamespace).leafType(ltype);
    }

    /**
     * Returns the node info which can be processed, by eliminating the nodes
     * which need not to be processed at normal conditions such as RPC,
     * notification and augment.
     *
     * @param curNode current node
     * @return info of node which needs processing
     */
    private ModelConverterTraversalInfo getProcessableInfo(YangNode curNode) {
        if (curNode.getNextSibling() != null) {
            YangNode sibling = curNode.getNextSibling();
            while (isNonProcessableNode(sibling)) {
                sibling = sibling.getNextSibling();
            }
            if (sibling != null) {
                return new ModelConverterTraversalInfo(sibling, SIBLING);
            }
        }
        YangNode parent = curNode.getParent();
        if (!(parent instanceof RpcNotificationContainer)) {
            return new ModelConverterTraversalInfo(curNode.getParent(), PARENT);
        }
        return new ModelConverterTraversalInfo((YangNode) exitBuilderSchema, PARENT);
    }

    public static String getValNamespace(Object val, LeafSchemaContext lsc) {
        String valNamespace = null;
        if (val != null) {
            YangNamespace yn = lsc.getValueNamespace(val.toString());
            if (yn != null) {
                valNamespace = yn.getModuleNamespace();
            }
        }
        return valNamespace;
    }
}
