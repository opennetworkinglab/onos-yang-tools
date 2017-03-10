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
package org.onosproject.yang.runtime.impl;

import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangAugmentableNode;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.model.AtomicPath;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.MultiInstanceLeaf;
import org.onosproject.yang.model.MultiInstanceNode;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SingleInstanceLeaf;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_NON_DATA_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.nonEmpty;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.runtime.helperutils.RuntimeHelper.DEFAULT_CAPS;
import static org.onosproject.yang.runtime.helperutils.RuntimeHelper.PERIOD;
import static org.onosproject.yang.runtime.helperutils.RuntimeHelper.getCapitalCase;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.fetchPackage;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getAttributeOfObject;

/**
 * Converts model object identifier to resource identifier.
 */
class ModIdToRscIdConverter {

    /**
     * Schema node with respect to the last atomic path in model object
     * identifier. in case of leaf node as last atomic path last index node
     * will be leaf's parent node.
     */
    private YangSchemaNode lastIndexNode;

    /**
     * Flag to know if model object identifier contains leaf identifier.
     */
    private boolean isMoIdWithLeaf;

    /**
     * Flag is true if rpc is added in branch point schema of resource
     * identifier.
     */
    private boolean isRpcAdded = true;

    /**
     * Model registry.
     */
    private final DefaultYangModelRegistry reg;

    /**
     * Creates an instance of converter.
     *
     * @param registry model registry
     */
    ModIdToRscIdConverter(DefaultYangModelRegistry registry) {
        reg = registry;
    }


    /**
     * Fetch resource identifier from model object identifier.
     *
     * @param id model object identifier
     * @return resource identifier from model object identifier
     */
    ResourceId fetchResourceId(ModelObjectId id) {

        ResourceId.Builder rid = ResourceId.builder().addBranchPointSchema("/", null);
        if (id == null || id.atomicPaths().isEmpty()) {
            return rid.build();
        }

        List<AtomicPath> paths = id.atomicPaths();
        AtomicPath path = paths.get(0);

        //If first element in model id contains only leaf/leaf-list then it
        // will be for module so in that case resource identifier will be
        // till module's leaf only.
        if (path instanceof SingleInstanceLeaf ||
                path instanceof MultiInstanceLeaf) {
            isMoIdWithLeaf = true;
            Object identifier;
            if (path instanceof SingleInstanceLeaf) {
                identifier = ((SingleInstanceLeaf) path).leafIdentifier();
            } else {
                identifier = ((MultiInstanceLeaf) path).leafIdentifier();
            }
            lastIndexNode = fetchModNodeFromLeaf(identifier.getClass().getName());
            if (lastIndexNode != null) {
                handleLeafInRid(lastIndexNode, id, rid, path);
                return rid.build();
            }
        }

        return convertToResourceId(id, fetchModuleNode(fetchPackage(path)),
                                   rid);
    }

    /**
     * Returns module node from leaf package.
     *
     * @param pkg leaf identifier package
     * @return module node from leaf package
     */
    YangSchemaNode fetchModNodeFromLeaf(String pkg) {
        String[] array = pkg.split(Pattern.quote("$"));
        return reg.getForInterfaceFileName(array[0]);
    }

    /**
     * Takes the first element in model object id and then uses it to fetch
     * the module schema fetchNode from model registry.
     *
     * @param pkg package for child node
     */
    YangSchemaNode fetchModuleNode(String pkg) {
        YangSchemaNode modNode;
        StringBuilder modPkg = new StringBuilder();

        //In other case we need to find the package
        // of module fetchNode from the given fetchNode's package.
        String[] strArray = pkg.split(Pattern.quote(PERIOD));
        int i = 0;
        while (i <= strArray.length - 3) {
            modPkg.append(strArray[i]).append(PERIOD);
            i++;
        }

        //If path contains input fetchNode class then in that case if we add
        // current modePkg will be the correct package for module fetchNode
        // because the next string will be rpc name in received from fetch
        // package method.
        modPkg.deleteCharAt(modPkg.lastIndexOf(PERIOD));
        YangNode node = (YangNode) reg.getForInterfaceFilePkg(modPkg.toString());
        if (node != null) {
            modNode = node;
            //in this case we should update the lastIndexNode for object to
            // data fetchNode conversion. because we need to create the data fetchNode
            // with the input fetchNode's data
            node = node.getChild();
            while (node != null) {
                if (node.getJavaAttributeName().toLowerCase()
                        .equals(strArray[i])) {
                    //last index fetchNode will be input fetchNode.
                    lastIndexNode = node.getChild();
                    break;
                }
                node = node.getNextSibling();
            }
        } else {
            modPkg.append(PERIOD);
            //In this case this package will be of module fetchNode.
            modPkg.append(strArray[i]);
            modNode = reg.getForInterfaceFilePkg(modPkg.toString());
        }
        return modNode;
    }

    /**
     * Converts model object identifier to resource identifier.
     *
     * @param id      model object identifier
     * @param builder resource id builder
     * @return resource identifier
     */
    private ResourceId convertToResourceId(ModelObjectId id, YangSchemaNode
            modNode, ResourceId.Builder builder) {
        List<AtomicPath> paths = id.atomicPaths();
        Iterator<AtomicPath> it = paths.iterator();
        AtomicPath path;
        String pkg;
        YangSchemaNode curNode = modNode;
        YangSchemaNode preNode = null;
        YangNode tempNode;
        while (it.hasNext()) {
            path = it.next();
            //Get the java package for given atomic path. this package will
            // be java package for schema node
            pkg = fetchPackage(path);
            if (curNode instanceof YangAugmentableNode) {
                tempNode = fetchFromAugment((YangNode) curNode, pkg, builder);
                if (tempNode != null) {
                    curNode = tempNode;
                } else {
                    //fetch the node for which model object identifier
                    // contains the atomic path.
                    curNode = fetchNode(((YangNode) curNode).getChild(), pkg, builder);
                }
            } else {
                curNode = fetchNode(((YangNode) curNode).getChild(), pkg, builder);
            }
            //if the current node is null and atomic path list contains
            // another node, then there is possibility that its a leaf node.
            if (curNode == null && paths.indexOf(path) == paths.size() - 1) {
                //check leaf nodes in previous nodes.
                handleLeafInRid(preNode, id, builder, path);
            } else if (curNode != null) {

                builder.addBranchPointSchema(curNode.getName(), curNode
                        .getNameSpace().getModuleNamespace());
                //list node can have key leaf in it. so resource identifier
                // should have key leaves also.
                if (curNode instanceof YangList) {
                    YangList list = (YangList) curNode;
                    MultiInstanceNode mil = (MultiInstanceNode) path;
                    Object keyObj = mil.key();
                    Set<String> keys = list.getKeyLeaf();
                    for (String key : keys) {
                        builder.addKeyLeaf(key, list.getNameSpace()
                                .getModuleNamespace(), getKeyValue(keyObj, key));
                    }
                }
            } else {
                throw new ModelConvertorException("invalid model object id." + id);
            }
            preNode = curNode;
        }
        if (!isMoIdWithLeaf) {
            // last node with respect to the last class in model object
            // identifier. model object will be an object for last index node.
            lastIndexNode = curNode;
        }
        return builder.build();
    }

    private void handleLeafInRid(YangSchemaNode preNode, ModelObjectId id,
                                 ResourceId.Builder builder, AtomicPath path) {
        //check leaf nodes in previous nodes.
        YangSchemaNode curNode = fetchLeaf(preNode, fetchPackage(path));
        if (curNode == null) {
            throw new ModelConvertorException("invalid model object id." + id);
        }
        isMoIdWithLeaf = true;
        if (curNode instanceof YangLeaf) {
            //leaf should be added as a branch point schema
            builder.addBranchPointSchema(curNode.getName(), curNode
                    .getNameSpace().getModuleNamespace());
        } else {
            // leaf list should be added as leaf list branch point
            // schema with its value added to it.
            Object val = ((MultiInstanceLeaf) path).value();
            builder.addLeafListBranchPoint(curNode.getName(), curNode
                    .getNameSpace().getModuleNamespace(), val);
        }
    }

    private String getJavaPkg(YangNode node) {
        return node.getJavaPackage() + PERIOD + DEFAULT_CAPS +
                getCapitalCase(node.getJavaClassNameOrBuiltInType());
    }

    /**
     * Returns augment node in case node is augmented.
     *
     * @param curNode current node
     * @param pkg     java package
     * @return augment node
     */
    private YangNode fetchFromAugment(YangNode curNode, String pkg,
                                      ResourceId.Builder builder) {
        YangNode tempNode;
        if (curNode != null) {
            if (curNode instanceof YangAugmentableNode) {
                List<YangAugment> augments = ((YangAugmentableNode) curNode)
                        .getAugmentedInfoList();
                if (nonEmpty(augments)) {
                    //fetch augment node from augment list and returns it.
                    for (YangAugment augment : augments) {
                        //process augment's child nodes.
                        tempNode = fetchNode(augment.getChild(), pkg, builder);
                        if (tempNode != null) {
                            return tempNode;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns key value from the key class object.
     *
     * @param keys    key class object
     * @param keyName key name
     * @return key value
     */
    private Object getKeyValue(Object keys, String keyName) {
        try {
            return getAttributeOfObject(keys, getCamelCase(keyName, null));
        } catch (NoSuchMethodException e) {
            throw new ModelConvertorException("invalid key value in model id for list" +
                                                      "." + keys.getClass().getName());
        }
    }

    /**
     * Returns YANG fetchNode for given package.
     *
     * @param node    YANG fetchNode
     * @param pkg     package
     * @param builder resource identifier builder
     * @return YANG fetchNode
     */
    private YangNode fetchNode(YangNode node, String pkg,
                               ResourceId.Builder builder) {
        String java;
        while (node != null) {
            //compare the java package with the package found in model object
            // identifier.
            if (node.getYangSchemaNodeType() == YANG_NON_DATA_NODE) {
                while (node != null &&
                        node.getYangSchemaNodeType() == YANG_NON_DATA_NODE) {
                    node = node.getNextSibling();
                }
            }
            if (node != null) {
                java = getJavaPkg(node);
                if (java.equals(pkg)) {
                    return node;
                } else if (node instanceof YangRpc) {
                    // in case of a input node rpc also needs to be added to
                    // resource identifier
                    if (isRpcAdded) {
                        isRpcAdded = false;
                        builder.addBranchPointSchema(node.getName(), node.getNameSpace()
                                .getModuleNamespace());
                    }
                    // node will become input node
                    node = node.getChild();
                } else {
                    // check in next sibling node
                    node = node.getNextSibling();
                }
            }
        }
        return null;
    }

    private YangSchemaNode fetchLeaf(YangSchemaNode node, String name) {
        YangLeavesHolder holder = (YangLeavesHolder) node;
        List<YangLeaf> leaves = holder.getListOfLeaf();
        // check if the names is equal to any of the leaf/leaf-list nodes.
        if (nonEmpty(leaves)) {
            for (YangLeaf leaf : leaves) {
                if (leaf.getJavaAttributeName().toLowerCase()
                        .equals(name)) {
                    return leaf;
                }
            }
        }
        List<YangLeafList> leafLists = holder.getListOfLeafList();
        if (nonEmpty(leafLists)) {
            for (YangLeafList leaf : leafLists) {
                if (leaf.getJavaAttributeName().toLowerCase()
                        .equals(name)) {
                    return leaf;
                }
            }
        }
        return null;
    }

    /**
     * Returns last index node for the last index atomic path of model object
     * identifier.
     *
     * @return schema node
     */
    YangSchemaNode getLastIndexNode() {
        return lastIndexNode;
    }

    /**
     * Returns true if model object identifier contains leaf.
     *
     * @return true if model object identifier contains leaf
     */
    boolean isMoIdWithLeaf() {
        return isMoIdWithLeaf;
    }
}
