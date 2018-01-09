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
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangAugmentableNode;
import org.onosproject.yang.compiler.datamodel.YangCase;
import org.onosproject.yang.compiler.datamodel.YangChoice;
import org.onosproject.yang.compiler.datamodel.YangInput;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangOutput;
import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.DefaultResourceData.Builder;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.LeafModelObject;
import org.onosproject.yang.model.LeafNode;
import org.onosproject.yang.model.ModelConverterException;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_NON_DATA_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.nonEmpty;
import static org.onosproject.yang.compiler.translator.tojava.YangJavaModelUtils.getAugmentClassName;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.RuntimeHelper.DEFAULT_CAPS;
import static org.onosproject.yang.runtime.RuntimeHelper.PERIOD;
import static org.onosproject.yang.runtime.RuntimeHelper.getCapitalCase;
import static org.onosproject.yang.runtime.impl.DataTreeBuilderHelper.getValNamespace;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.TRUE;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getAttributeOfObject;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getJavaName;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getLeafListObject;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getLeafObject;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.isTypeEmpty;

/**
 * Representation of data tree builder which generates YANG data tree from the
 * class objects which are provided from the applications and return it to the
 * protocol(s).
 */
class DefaultDataTreeBuilder {
    /*
     *
     *  ModelObjectId | List<ModelObject> |    ResourceId        | List<DN>
     * ---------------|-------------------|----------------------|---------
     *    Null        |    Null           | Form module          |  null
     *    Class       |    Null           | Form module to node  |  null
     *    Class +Leaf |    Null           | From module to leaf  |  null
     *    Leaf        |    Null           | From module to leaf  |  null
     *    Null        |    Class          | Form module          |  class
     *    Class       |    Class          | From module to node  |  class
     *    Class+leaf  |    Object         |  INVALID case        | -----
     */

    /**
     * Default YANG Model registry for getting the schema node with respect
     * to class packages.
     */
    private final DefaultYangModelRegistry reg;

    /**
     * Creates and instance of data tree builder.
     *
     * @param registry model registry
     */
    DefaultDataTreeBuilder(DefaultYangModelRegistry registry) {
        reg = registry;
    }

    /**
     * Returns resource data for given model object data. resource data
     * contains resource identifier for given model object identifier and
     * list of data nodes for given model objects.
     *
     * @param modelData model object data
     * @return resource data
     */
    ResourceData getResourceData(ModelObjectData modelData) {

        List<ModelObject> modelObjects = modelData.modelObjects();
        ModelObjectId id = modelData.identifier();

        Builder rscData = DefaultResourceData.builder();

        //Create resource identifier.
        ModIdToRscIdConverter converter = new ModIdToRscIdConverter(reg);
        rscData.resourceId(converter.fetchResourceId(id).build());
        YangSchemaNode lastIndexNode = converter.getLastIndexNode();

        //If module object list is empty or null then we just need to
        // create resource identifier.
        if (!nonEmpty(modelObjects)) {
            return rscData.build();
        }

        if (converter.isMoIdWithLeaf() && nonEmpty(modelObjects)) {
            throw new ModelConverterException(
                    "invalid module object data when model object identifier" +
                            " contains leaf node, no object should be added to" +
                            " model object list." + modelData);
        }

        if (id == null || !nonEmpty(id.atomicPaths())) {
            lastIndexNode = fetchModNode(modelObjects.get(0), converter);
        }

        //Create data nodes.
        DataTreeBuilderHelper helper = new DataTreeBuilderHelper(reg);
        YangSchemaNode curNode;
        for (ModelObject modObj : modelObjects) {
            //Do processing of data node conversion from model objects.
            if (modObj instanceof LeafModelObject) {
                //Process leaf object.
                processLeafObj(lastIndexNode, rscData, (LeafModelObject) modObj);
            } else {
                if (converter.isInputOrOutput()) {
                    curNode = handleRpcChild(modObj, (YangNode)
                            lastIndexNode, rscData);
                } else {
                    curNode = fetchCurNode(modObj, (YangNode) lastIndexNode);
                }
                if (curNode != null) {
                    processDataNodeConversion((YangNode) curNode, helper,
                                              rscData, modObj);
                } else {
                    throw new ModelConverterException(
                            "failed to convert model object in data node" +
                                    modObj);
                }
            }
        }

        return rscData.build();
    }

    /**
     * Process model object to data node conversion.
     *
     * @param curNode current root node
     * @param helper  data tree builder helper
     * @param rscData resource data builder
     * @param curObj  current root object
     */
    private void processDataNodeConversion(
            YangNode curNode, DataTreeBuilderHelper helper,
            Builder rscData, Object curObj) {
        if (curNode == null) {
            return;
        }
        DataTreeNodeInfo info = new DataTreeNodeInfo();
        //Set object in parent node info.
        setObjInParent(curNode, curObj, info);
        info.type(SINGLE_INSTANCE_NODE);
        switch (curNode.getYangSchemaNodeType()) {
            case YANG_SINGLE_INSTANCE_NODE:
            case YANG_MULTI_INSTANCE_NODE:
                processNodeObj(helper, curObj, rscData, curNode);
                break;
            case YANG_AUGMENT_NODE:
                processModelObjects(helper, curNode, rscData, curObj);
                break;
            case YANG_CHOICE_NODE:
                handleChoiceNode(curNode, info, helper, rscData);
                break;
            case YANG_NON_DATA_NODE:
                if (curNode instanceof YangCase) {
                    handleCaseNode(curNode, info, helper, rscData);
                }
                break;
            default:
                throw new ModelConverterException(
                        "Non processable schema node has arrived for adding " +
                                "it in data tree");
        }
    }

    /**
     * Returns input/output nodes.
     *
     * @param obj     model object for input/output nodes
     * @param parent  module node
     * @param rscData resource data builder
     * @return input/output node
     */
    private YangSchemaNode handleRpcChild(
            Object obj, YangNode parent, Builder rscData) {
        if (obj != null && parent != null) {
            //process all the node which are in data model tree.
            String name = obj.getClass().getName();
            YangNode child = parent.getChild();
            while (child != null && !(child instanceof YangRpc)) {
                child = child.getNextSibling();
            }
            while (child != null && child instanceof YangRpc &&
                    !name.contains(child.getJavaPackage() + "." +
                    child.getJavaClassNameOrBuiltInType().toLowerCase())) {
                child = child.getNextSibling();
            }
            if (child != null) {
                //Rpc should be part of resource identifier for input/output
                // nodes.
                ResourceId id = ResourceId.builder()
                        .addBranchPointSchema("/", null)
                        .addBranchPointSchema(child.getName(), child
                                .getNameSpace().getModuleNamespace()).build();
                rscData.resourceId(id);
                child = child.getChild();
                return getNode(child, name);
            }
        }
        // this could also be possible that we have received this module node
        // when model object is a node at third level of root node. for
        // example if we have a model object id which is null and model
        // object is an object of choice instance then this will be called
        // because choice object will be instance of case class which is an
        // third level node.
        return fetchCurNode(obj, parent);
    }

    /**
     * In last index node we will be having parent node of the current object
     * so we need to get the current node and start processing it for
     * conversion.
     *
     * @param obj    current object
     * @param parent parent node
     * @return current node
     */
    private YangSchemaNode fetchCurNode(Object obj, YangNode parent) {
        YangSchemaNode output;
        if (obj != null && parent != null) {
            //process all the node which are in data model tree.
            String name = obj.getClass().getName();
            YangNode child = parent.getChild();
            while (child != null) {

                if (child.getYangSchemaNodeType() == YANG_NON_DATA_NODE) {
                    child = child.getNextSibling();
                    continue;
                }
                //search if parent node has choice as child node.
                if (child instanceof YangChoice) {
                    output = findFromChoiceNode(name, parent);
                    //search if parent node has a case child node
                } else if (child instanceof YangCase) {
                    output = findFromCaseNode(name, parent);
                    //no need to process non data nodes.
                } else {
                    //search for normal nodes.
                    output = getNode(child, name);
                }
                if (output != null) {
                    return output;
                }
                child = child.getNextSibling();
            }
            return findIfAugmentable(name, parent);
        }
        return null;
    }


    /**
     * Find the node in augment. if current object is a object of augment
     * node or if object is a object of child node of augment node.
     *
     * @param name   class qualified name
     * @param parent parent node
     * @return current node for object
     */
    private YangSchemaNode findIfAugmentable(String name, YangNode parent) {
        List<YangAugment> augments = ((YangAugmentableNode) parent)
                .getAugmentedInfoList();
        YangNode child;
        YangSchemaNode output;
        if (nonEmpty(augments)) {
            //this is if we are having an augment class object.
            for (YangAugment augment : augments) {
                output = getNode(augment, name);
                if (output != null) {
                    return output;
                }
            }
            //this is if we have a child class object which is a
            // child node of augment node.
            for (YangAugment augment : augments) {
                child = augment.getChild();
                output = getNode(child, name);
                if (output != null) {
                    return output;
                }
            }
        }
        return null;
    }

    /**
     * Find in choice node if current object is an object of choice/case node.
     *
     * @param qName  qualified name
     * @param parent choice's paren node
     * @return choice node
     */
    private YangSchemaNode findFromChoiceNode(String qName, YangSchemaNode parent) {
        YangNode child = ((YangNode) parent).getChild();
        YangSchemaNode output;
        while (child != null) {
            if (child instanceof YangChoice) {
                output = findFromCaseNode(qName, child);
                if (output != null) {
                    return output;
                }
            }
            child = child.getNextSibling();
        }
        return null;
    }

    /**
     * Find in case if given object is an object of case node.
     *
     * @param qName  qualified name
     * @param parent case's parent node
     * @return case node
     */
    private YangSchemaNode findFromCaseNode(String qName, YangSchemaNode parent) {
        YangNode caseNode = ((YangNode) parent).getChild();
        String javaName;
        while (caseNode != null) {
            javaName = caseNode.getJavaPackage() + PERIOD +
                    DEFAULT_CAPS + getCapitalCase(getCamelCase(
                    caseNode.getName(), null));
            if (javaName.equals(qName)) {
                return caseNode;
            }
            caseNode = caseNode.getNextSibling();
        }
        return findIfAugmentable(qName, (YangNode) parent);
    }

    /**
     * Returns the node which has the same java package as the object's package.
     *
     * @param child child node
     * @param pkg   java package
     * @return child node
     */
    private YangSchemaNode getNode(YangNode child, String pkg) {
        String javaName;
        while (child != null) {
            if (child instanceof YangAugment) {
                javaName = child.getJavaPackage() + PERIOD + DEFAULT_CAPS +
                        getAugmentClassName((YangAugment) child,
                                            new YangPluginConfig());
            } else if (child instanceof YangInput ||
                    child instanceof YangOutput) {
                javaName = child.getJavaPackage() + PERIOD + DEFAULT_CAPS +
                        getCapitalCase(child.getJavaClassNameOrBuiltInType());
            } else {
                javaName = child.getJavaPackage() + PERIOD + DEFAULT_CAPS +
                        getCapitalCase(getCamelCase(child.getName(), null));
            }
            if (javaName.equals(pkg)) {
                return child;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    /**
     * Returns module node when model object identifier is null. model object
     * will be used here to get the module node.
     *
     * @param object    module object
     * @param converter identifier converter
     * @return module node
     */
    private YangSchemaNode fetchModNode(ModelObject object,
                                        ModIdToRscIdConverter converter) {
        if (object instanceof LeafModelObject) {
            LeafModelObject obj = (LeafModelObject) object;
            return converter.fetchModNodeFromLeaf(obj.leafIdentifier()
                                                          .getClass().getName());
        }
        return converter.fetchModuleNode(object.getClass().getName());
    }

    /**
     * Process leaf and leaf list information.
     *
     * @param holder  holder node
     * @param rscData resource data
     * @param lObj    leaf model object
     */
    private void processLeafObj(YangSchemaNode holder, Builder rscData,
                                LeafModelObject lObj) {
        //handle leaf nodes.
        YangLeavesHolder lHolder = (YangLeavesHolder) holder;
        if (lObj.leafIdentifier() != null) {
            String name = lObj.leafIdentifier().toString().toLowerCase();
            List<Object> values = lObj.values();
            // handle all leaf nodes and add their data nodes to resource data.
            List<YangLeaf> leaves = lHolder.getListOfLeaf();
            if (nonEmpty(leaves)) {
                updateLeafDataNode(name, leaves, values.get(0), rscData,
                                   holder, lObj);
            }
            // handle all leaf-list nodes and add their data nodes to
            // resource data.
            List<YangLeafList> leafLists = lHolder.getListOfLeafList();
            if (nonEmpty(leafLists)) {
                updateLeafListDataNode(name, leafLists, values, rscData,
                                       holder, lObj);
            }
        }
    }

    /**
     * Updates the processed leaf-list objects from the leaf-list type to the
     * data node.
     *
     * @param name      leaf-list name
     * @param leafLists YANG leaf-lists
     * @param values    leaf-list objects
     * @param rscData   data node
     * @param holder    leaf-list holder
     * @param lObj      leaf model object
     */
    private void updateLeafListDataNode(String name, List<YangLeafList> leafLists,
                                        List<Object> values, Builder rscData,
                                        YangSchemaNode holder, LeafModelObject lObj) {
        for (YangLeafList leafList : leafLists) {
            if (name.equals(leafList.getJavaAttributeName().toLowerCase())) {
                Set<Object> objects = getLeafListObject(holder, leafList,
                                                        lObj, values);
                if (!objects.isEmpty()) {
                    Object o = objects.iterator().next();
                    if (isTypeEmpty(leafList.getDataType())) {
                        objects.clear();
                        String empty = String.valueOf(o);
                        if (!empty.equals(TRUE)) {
                            break;
                        }
                        objects.add(null);
                    }
                    for (Object obj : objects) {
                        String valNamespace = getValNamespace(obj, leafList);
                        DataNode node = LeafNode
                                .builder(leafList.getName(), leafList
                                        .getNameSpace().getModuleNamespace())
                                .value(obj)
                                .type(MULTI_INSTANCE_LEAF_VALUE_NODE)
                                .valueNamespace(valNamespace).build();
                        rscData.addDataNode(node);
                    }
                }
                break;
            }
        }
    }

    /**
     * Updates the processed leaf object from the leaf type to the data node.
     *
     * @param lName    leaf name
     * @param leaves   YANG leaves
     * @param val      leaf object
     * @param rscData  data node
     * @param rootNode holder node
     * @param lObj     leaf model object
     */
    private void updateLeafDataNode(String lName, List<YangLeaf> leaves,
                                    Object val, Builder rscData,
                                    YangSchemaNode rootNode, LeafModelObject lObj) {
        for (YangLeaf leaf : leaves) {
            if (lName.equals(leaf.getJavaAttributeName().toLowerCase())) {
                Object obj = getLeafObject(rootNode, leaf, lObj,
                                           val, true);
                if (obj != null) {
                    if (isTypeEmpty(leaf.getDataType())) {
                        String empty = String.valueOf(obj);
                        if (!empty.equals(TRUE)) {
                            break;
                        }
                        obj = null;
                    }
                    String valNamespace = getValNamespace(obj, leaf);
                    DataNode node = LeafNode.builder(leaf.getName(), leaf
                            .getNameSpace().getModuleNamespace())
                            .value(obj)
                            .type(SINGLE_INSTANCE_LEAF_VALUE_NODE)
                            .valueNamespace(valNamespace).build();
                    rscData.addDataNode(node);
                    break;
                }
            }
        }
    }

    /**
     * Process single instance/multi instance nodes and build there data nodes.
     *
     * @param helper  data tree builder helper
     * @param modObj  model object
     * @param rscData resource data
     * @param curRoot current root node
     */
    private void processNodeObj(DataTreeBuilderHelper helper, Object modObj,
                                ResourceData.Builder rscData, YangSchemaNode curRoot) {
        DataNode.Builder builder = InnerNode.builder(
                curRoot.getName(), curRoot.getNameSpace()
                        .getModuleNamespace());
        //assign the type of the node.
        if (curRoot instanceof YangList) {
            builder.type(MULTI_INSTANCE_NODE);
        } else {
            builder.type(SINGLE_INSTANCE_NODE);
        }
        //process current node and get data tree.
        builder = helper.getDataTree(curRoot, builder, modObj);
        if (builder != null) {
            rscData.addDataNode(builder.build());
        }
    }

    /**
     * Does the processing to convert objects to data nodes.
     *
     * @param modYo         data node builder
     * @param lastIndexNode last index schema node
     * @param rscData       resource data
     * @param yangObj       object for node
     */
    private void processModelObjects(DataTreeBuilderHelper modYo, YangSchemaNode lastIndexNode,
                                     Builder rscData, Object
                                             yangObj) {
        //Process all the leaf nodes of root node.
        processRootLeafInfo(modYo, lastIndexNode, rscData, yangObj);

        //Process all the list nodes of root node.
        processRootLevelListNode(modYo, lastIndexNode, rscData,
                                 yangObj);
        //process all the single instance node of root nodes.
        processRootLevelSingleInNode(modYo, lastIndexNode, rscData,
                                     yangObj);
    }

    /**
     * Process leaf and leaf list information.
     *
     * @param modYo   module builder
     * @param holder  root node
     * @param rscData resource data
     */
    private void processRootLeafInfo(DataTreeBuilderHelper modYo,
                                     YangSchemaNode holder, Builder rscData,
                                     Object hObj) {
        //handle leaf nodes.
        YangLeavesHolder lHolder = (YangLeavesHolder) holder;
        if (holder instanceof YangCase) {
            if (((YangCase) holder).getParent().getParent() instanceof
                    RpcNotificationContainer) {
                modYo.setExtBuilder(null);
            }
        }
        // handle all leaf nodes and add their data nodes to resource data.
        List<YangLeaf> leaves = lHolder.getListOfLeaf();
        if (nonEmpty(leaves)) {
            updateLeaf(leaves, modYo, holder, hObj, rscData);
        }
        // handle all leaf list nodes and add their data nodes to resource data.
        List<YangLeafList> leafLists = lHolder.getListOfLeafList();
        if (nonEmpty(leafLists)) {
            updateLeafList(leafLists, modYo, holder, hObj, rscData);
        }
    }

    /**
     * Updates leaf values inside the holder node by taking the processed
     * leaf object and checking for empty type.
     *
     * @param leafLists list of leaf-list
     * @param modYo     data tree builder
     * @param holder    leaf-list holder
     * @param hObj      holder object
     * @param rscData   resource data
     */
    private void updateLeafList(List<YangLeafList> leafLists,
                                DataTreeBuilderHelper modYo, YangSchemaNode holder,
                                Object hObj, Builder rscData) {
        List<Object> obj;
        List<DataNode.Builder> nodes;
        for (YangLeafList leafList : leafLists) {
            try {
                obj = (List<Object>) getAttributeOfObject(
                        hObj, getJavaName(leafList));
                if (obj != null) {
                    Set<Object> objects = getLeafListObject(holder, leafList,
                                                            hObj, obj);
                    if (!objects.isEmpty()) {
                        Object o = objects.iterator().next();
                        if (isTypeEmpty(leafList.getDataType())) {
                            objects.clear();
                            String empty = String.valueOf(o);
                            if (!empty.equals(TRUE)) {
                                continue;
                            }
                            objects.add(null);
                        }
                        nodes = modYo.addLeafList(objects, leafList);
                        if (nodes != null) {
                            for (DataNode.Builder node : nodes) {
                                rscData.addDataNode(node.build());
                            }
                        }
                    }
                }
            } catch (NoSuchMethodException e) {
                throw new ModelConverterException(e);
            }
        }
    }

    /**
     * Updates leaf values inside the holder node by taking the processed
     * leaf object and checking for empty type.
     *
     * @param leaves  list of leaf
     * @param modYo   data tree builder
     * @param holder  leaf holder
     * @param hObj    holder object
     * @param rscData resource data
     */
    private void updateLeaf(List<YangLeaf> leaves, DataTreeBuilderHelper modYo,
                            YangSchemaNode holder, Object hObj, Builder rscData) {
        DataNode.Builder builder;
        for (YangLeaf leaf : leaves) {
            try {
                Object leafObj = getAttributeOfObject(
                        hObj, leaf.getJavaAttributeName());
                Object obj = getLeafObject(holder, leaf, hObj, leafObj,
                                           false);

                if (obj != null) {
                    if (isTypeEmpty(leaf.getDataType())) {
                        String empty = String.valueOf(obj);
                        if (!empty.equals(TRUE)) {
                            continue;
                        }
                        obj = null;
                    }
                    builder = modYo.createLeafNode(leaf, obj);
                    addDataNode(builder, rscData);
                }
            } catch (NoSuchMethodException e) {
                throw new ModelConverterException(
                        "Failed to create data node for leaf "
                                + leaf.getName(), e);
            }
        }
    }

    /**
     * Process all current root level list nodes. list node instances should be
     * added as a new instance in resource data.
     *
     * @param helper      data tree builder helper
     * @param curRootNode current root schema node
     * @param rscData     resource data
     * @param curRootObj  current root object
     */
    private void processRootLevelListNode(
            DataTreeBuilderHelper helper, YangSchemaNode curRootNode,
            Builder rscData, Object curRootObj) {

        YangNode curNode = ((YangNode) curRootNode).getChild();
        DataTreeNodeInfo parentInfo = new DataTreeNodeInfo();
        //Set object in parent node info.
        setObjInParent(curRootNode, curRootObj, parentInfo);
        parentInfo.type(SINGLE_INSTANCE_NODE);
        DataNode.Builder builder;

        Object childObj;
        while (curNode != null) {
            curNode = verifyAndGetList(curNode);
            if (curNode == null) {
                break;
            }
            //Get all the instance object for list node.
            List<Object> childObjList = (List<Object>) helper.getChildObject(
                    curNode, parentInfo);
            if (nonEmpty(childObjList)) {
                Iterator<Object> listItr = childObjList.iterator();
                while (listItr.hasNext()) {
                    childObj = listItr.next();
                    builder = InnerNode.builder(curNode.getName(), curNode
                            .getNameSpace().getModuleNamespace())
                            .type(MULTI_INSTANCE_NODE);
                    if (childObj != null) {
                        DataNode.Builder output = helper
                                .getDataTree(curNode, builder, childObj);
                        if (output != null) {
                            rscData.addDataNode(output.build());
                        }
                    }
                }
            }
            curNode = curNode.getNextSibling();
        }
    }

    /**
     * Sets parent object on parent data tree info.
     *
     * @param rootNode   current root node
     * @param rootObj    current root object
     * @param parentInfo parent data tree info
     */
    private void setObjInParent(YangSchemaNode rootNode, Object rootObj,
                                DataTreeNodeInfo parentInfo) {
        if (rootNode instanceof YangCase) {
            parentInfo.setCaseObject(rootObj);
        }
        if (rootNode instanceof YangAugment) {
            parentInfo.setAugmentObject(rootObj);
            parentInfo.setYangObject(rootObj);
        } else {
            parentInfo.setYangObject(rootObj);
        }
    }

    /**
     * Process root level single instance nodes.
     *
     * @param helper   data tree builder helper
     * @param rootNode current root node
     * @param rscData  resource data
     * @param rootObj  current root object
     */
    private void processRootLevelSingleInNode(
            DataTreeBuilderHelper helper, YangSchemaNode rootNode,
            Builder rscData, Object rootObj) {

        YangNode curNode = ((YangNode) rootNode).getChild();

        DataTreeNodeInfo parentInfo = new DataTreeNodeInfo();
        parentInfo.type(SINGLE_INSTANCE_NODE);
        setObjInParent(rootNode, rootObj, parentInfo);

        Object childObj = null;
        DataNode.Builder builder;
        while (curNode != null) {
            // we need to get current data node.
            curNode = verifyAndDoNotGetList(curNode, helper, parentInfo, rscData);
            if (curNode == null) {
                break;
            }
            if (curNode.getYangSchemaNodeType() != YANG_NON_DATA_NODE) {
                childObj = helper.getChildObject(
                        curNode, parentInfo);
            }

            if (childObj != null) {
                builder = InnerNode.builder(curNode.getName(), curNode
                        .getNameSpace().getModuleNamespace())
                        .type(SINGLE_INSTANCE_NODE);
                DataNode.Builder output = helper
                        .getDataTree(curNode, builder, childObj);
                if (output != null) {
                    rscData.addDataNode(output.build());
                }
            }
            curNode = curNode.getNextSibling();
        }
        // check for augments in current node.
        if (rootNode instanceof YangAugmentableNode) {
            List<YangAugment> augments = ((YangAugmentableNode) rootNode)
                    .getAugmentedInfoList();
            if (nonEmpty(augments)) {
                for (YangAugment augment : augments) {
                    childObj = helper.processAugmentNode(augment, parentInfo);
                    if (childObj != null) {
                        processModelObjects(helper, augment, rscData, childObj);
                    }
                }
            }
        }
    }

    /**
     * Check for the list node and its siblings. if found list then return
     * node else null.
     *
     * @param curNode current node
     * @return list node
     */
    private YangNode verifyAndGetList(YangNode curNode) {
        if (curNode == null) {
            return null;
        }
        if (!(curNode instanceof YangList)) {
            curNode = curNode.getNextSibling();
            curNode = verifyAndGetList(curNode);
        }
        return curNode;
    }

    /**
     * Verify if current node is a data node. handles choice ,case and
     * augment node itself and returns only when node is a data node.
     *
     * @param curNode current node
     * @param helper  data tree builder helper
     * @param info    parent info
     * @param rscData resource data
     * @return single instance node
     */
    private YangNode verifyAndDoNotGetList(YangNode curNode, DataTreeBuilderHelper
            helper, DataTreeNodeInfo info, Builder rscData) {
        if (curNode == null) {
            return null;
        }
        switch (curNode.getYangSchemaNodeType()) {
            case YANG_SINGLE_INSTANCE_NODE:
                return curNode;
            case YANG_MULTI_INSTANCE_NODE:
            case YANG_AUGMENT_NODE:
                break;
            case YANG_CHOICE_NODE:
                handleChoiceNode(curNode, info, helper, rscData);
                break;
            case YANG_NON_DATA_NODE:
                if (curNode instanceof YangCase) {
                    handleCaseNode(curNode, info, helper, rscData);
                }
                break;
            default:
                throw new ModelConverterException(
                        "Non processable schema node has arrived for adding " +
                                "it in data tree");
        }
        return processReCheckSibling(curNode, helper, info, rscData);
    }

    /**
     * In case of case , choice and augment we need to process then and then
     * need to check for their siblings so we can get a single instance node
     * to process and add in resource data.
     *
     * @param curNode current node
     * @param helper  data tree helper
     * @param info    data tree node info
     * @param rscData resource data
     * @return single instance node
     */
    private YangNode processReCheckSibling(
            YangNode curNode, DataTreeBuilderHelper helper, DataTreeNodeInfo info,
            Builder rscData) {
        curNode = curNode.getNextSibling();
        curNode = verifyAndDoNotGetList(curNode, helper, info, rscData);
        return curNode;
    }

    /**
     * Handles root level choice node.
     *
     * @param curNode current schema node
     * @param info    data node info
     * @param helper  data node builder
     * @param rscData resource data
     */
    private void handleChoiceNode(
            YangNode curNode, DataTreeNodeInfo info, DataTreeBuilderHelper helper,
            Builder rscData) {
        // get the choice object.
        Object childObj = helper.processChoiceNode(curNode, info);
        YangNode tempNode = curNode;
        YangNode caseNode;
        curNode = curNode.getChild();
        if (childObj != null) {
            while (curNode != null) {
                // process child case nodes.
                processCaseNode(curNode, info, helper, rscData);
                curNode = curNode.getNextSibling();
            }
            //process augment nodes because choice can have a augmented case
            // in it.
            List<YangAugment> augments = ((YangChoice) tempNode)
                    .getAugmentedInfoList();
            if (nonEmpty(augments)) {
                for (YangAugment augment : augments) {
                    caseNode = augment.getChild();
                    while (caseNode != null && caseNode instanceof YangCase) {
                        processCaseNode(caseNode, info, helper, rscData);
                        caseNode = caseNode.getNextSibling();
                    }
                }
            }
        }
    }

    /**
     * Process case node.
     * case node is non data node so contents of a case
     * node should be added to its choice's parent node. if there is current
     * root node is parent for choice node then data node for case child
     * schema's will be added to resource data.
     *
     * @param curNode current case node
     * @param info    parent tree info
     * @param helper  data tree helper
     * @param rscData resource data
     */
    private void processCaseNode(
            YangNode curNode, DataTreeNodeInfo info, DataTreeBuilderHelper helper,
            Builder rscData) {
        Object childObj = helper.processCaseNode(curNode, info);
        if (childObj != null) {
            processModelObjects(helper, curNode, rscData, childObj);
        }
    }

    /**
     * Handles case node when model object contains choice class. we need to
     * add all the content of case node as new data node in resource data
     * node list.
     *
     * @param curNode current node
     * @param info    data node builder info
     * @param yo      data node builder
     * @param rscData resource data
     */
    private void handleCaseNode(
            YangNode curNode, DataTreeNodeInfo info, DataTreeBuilderHelper yo,
            Builder rscData) {

        Object obj = info.getYangObject();
        if (obj != null) {
            processModelObjects(yo, curNode, rscData, obj);
        }
    }

    /**
     * Adds data node to resource data.
     *
     * @param node    data node
     * @param builder resource data builder
     */
    private void addDataNode(DataNode.Builder node,
                             Builder builder) {
        if (node != null) {
            builder.addDataNode(node.build());
        }
    }
}
