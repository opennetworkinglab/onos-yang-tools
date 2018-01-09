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

import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangAugmentableNode;
import org.onosproject.yang.compiler.datamodel.YangInput;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangOutput;
import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNodeType;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.model.AtomicPath;
import org.onosproject.yang.model.ModelConverterException;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.MultiInstanceLeaf;
import org.onosproject.yang.model.MultiInstanceNode;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SingleInstanceLeaf;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_AUGMENT_NODE;
import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_NON_DATA_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.nonEmpty;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.runtime.RuntimeHelper.DEFAULT_CAPS;
import static org.onosproject.yang.runtime.RuntimeHelper.PERIOD;
import static org.onosproject.yang.runtime.RuntimeHelper.getCapitalCase;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.fetchPackage;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getAttributeOfObject;
import static org.onosproject.yang.runtime.impl.ModelConverterUtil.getObjFromType;

/**
 * Converts model object identifier to resource identifier.
 */
class ModIdToRscIdConverter {

    /**
     * Model registry.
     */
    private final DefaultYangModelRegistry reg;
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
     * Flag is true if we have found module node using input/output packages.
     */
    private boolean isInputOrOutput;

    /**
     * Creates an instance of converter.
     *
     * @param registry model registry
     */
    ModIdToRscIdConverter(DefaultYangModelRegistry registry) {
        reg = registry;
    }


    /**
     * Fetch resource identifier builder from model object identifier.
     *
     * @param id model object identifier
     * @return resource identifier builder from model object identifier
     */
    ResourceId.Builder fetchResourceId(ModelObjectId id) {

        ResourceId.Builder rid = ResourceId.builder().addBranchPointSchema("/", null);
        if (id == null || id.atomicPaths().isEmpty()) {
            return rid;
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
                return rid;
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
        return reg.getForRegClassName(array[0]);
    }

    /**
     * Takes the first element in model object id and then uses it to fetch
     * the module schema fetchNode from model registry.
     *
     * @param pkg package for child node
     * @return first matching module schema
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
        YangNode node = (YangNode) reg.getForRegClassQualifiedName(modPkg.toString(),
                                                                   true);
        if (node != null) {
            modNode = node;
            //in this case we should update the lastIndexNode for object to
            // data fetchNode conversion. because we need to create the data fetchNode
            // with the input fetchNode's data
            node = node.getChild();
            while (node != null) {
                YangSchemaNodeType type = node.getYangSchemaNodeType();
                if (type != YANG_NON_DATA_NODE && type != YANG_AUGMENT_NODE &&
                        node.getJavaAttributeName().toLowerCase()
                                .equals(strArray[i])) {
                    //last index fetchNode will be input fetchNode.
                    lastIndexNode = node.getChild();
                    break;
                }
                node = node.getNextSibling();
            }
            if (lastIndexNode instanceof YangInput ||
                    lastIndexNode instanceof YangOutput) {
                isInputOrOutput = true;
            }
        } else {
            modPkg.append(PERIOD);
            //In this case this package will be of module fetchNode.
            modPkg.append(strArray[i]);
            modNode = reg.getForRegClassQualifiedName(modPkg.toString(), false);
        }
        return modNode;
    }

    /**
     * Converts model object identifier to resource identifier.
     *
     * @param id      model object identifier
     * @param builder resource id builder
     * @return resource identifier builder
     */
    private ResourceId.Builder convertToResourceId(ModelObjectId id, YangSchemaNode
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
            try {
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
                        Object keysObj = mil.key();
                        if (keysObj != null) {
                            Set<String> keys = list.getKeyLeaf();
                            for (String key : keys) {
                                Object obj = getKeyObject(keysObj, key, list);
                                builder.addKeyLeaf(key, list.getNameSpace()
                                        .getModuleNamespace(), obj);
                            }
                        }
                    }
                } else {
                    throw new ModelConverterException("invalid model object id." + id);
                }
                preNode = curNode;
            } catch (Exception e) {
                throw new ModelConverterException("Encountered an Exception processing " + path, e);
            }
        }
        if (!isMoIdWithLeaf) {
            // last node with respect to the last class in model object
            // identifier. model object will be an object for last index node.
            lastIndexNode = curNode;
        }
        builder.appInfo(curNode);
        return builder;
    }

    private void handleLeafInRid(YangSchemaNode preNode, ModelObjectId id,
                                 ResourceId.Builder builder, AtomicPath path) {
        //check leaf nodes in previous nodes.
        String pkg = fetchPackage(path);
        YangSchemaNode curNode = fetchLeaf(preNode, pkg, false);
        if (curNode == null) {
            if (preNode instanceof YangAugmentableNode) {
                List<YangAugment> augments = ((YangAugmentableNode) preNode)
                        .getAugmentedInfoList();
                for (YangAugment augment : augments) {
                    curNode = fetchLeaf(augment, pkg, false);
                    if (curNode != null) {
                        break;
                    }
                }
            }
        }
        if (curNode == null) {
            throw new ModelConverterException("invalid model object id." + id);
        }
        isMoIdWithLeaf = true;
        if (curNode instanceof YangLeaf) {
            //leaf should be added as a branch point schema
            builder.addBranchPointSchema(curNode.getName(), curNode
                    .getNameSpace().getModuleNamespace());
        } else {
            // leaf list should be added as leaf list branch point
            // schema with its value added to it.
            YangType<?> type = ((YangLeafList) curNode).getDataType();
            Object val = ((MultiInstanceLeaf) path).value();
            val = getObjFromType(preNode, path, curNode, "value", val, type);
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
            throw new ModelConverterException("invalid key value in model id for list" +
                                                      "." + keys.getClass().getName(), e);
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
            if (node.getYangSchemaNodeType() != YANG_NON_DATA_NODE) {
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
            } else {
                // check in next sibling node
                node = node.getNextSibling();
            }
        }
        return null;
    }

    private YangSchemaNode fetchLeaf(YangSchemaNode node, String name,
                                     boolean isSchemaName) {
        YangLeavesHolder holder = (YangLeavesHolder) node;
        List<YangLeaf> leaves = holder.getListOfLeaf();
        String lName;
        // check if the names is equal to any of the leaf/leaf-list nodes.
        if (nonEmpty(leaves)) {
            for (YangLeaf leaf : leaves) {
                lName = leaf.getName();
                if (!isSchemaName) {
                    lName = leaf.getJavaAttributeName().toLowerCase();
                }
                if (lName.equals(name)) {
                    return leaf;
                }
            }
        }
        List<YangLeafList> leafLists = holder.getListOfLeafList();
        if (nonEmpty(leafLists)) {
            for (YangLeafList ll : leafLists) {
                lName = ll.getName();
                if (!isSchemaName) {
                    lName = ll.getJavaAttributeName().toLowerCase();
                }
                if (lName.equals(name)) {
                    return ll;
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

    /**
     * Returns true if module node is found using input/output packages.
     *
     * @return true if module node is found using input/output packages
     */
    boolean isInputOrOutput() {
        return isInputOrOutput;
    }

    /**
     * Returns the key leaf's processed object to be present in the resource id.
     *
     * @param keysObj list of keys object
     * @param key     leaf key object
     * @param list    YANG list
     * @return processed object
     */
    private Object getKeyObject(Object keysObj, String key, YangList list) {
        Object keyObj = getKeyValue(keysObj, key);
        YangSchemaNode leaf = fetchLeaf(list, key, true);

        if (leaf == null) {
            List<YangAugment> augment = list.getAugmentedInfoList();
            for (YangAugment a : augment) {
                leaf = fetchLeaf(a, key, true);
                if (leaf != null) {
                    break;
                }
            }
        }
        if (leaf == null) {
            throw new ModelConverterException(
                    "The specified key " + key + " is not present in the " +
                            "YANG schema node.");
        }
        YangType<?> type;
        if (leaf instanceof YangLeaf) {
            type = ((YangLeaf) leaf).getDataType();
        } else {
            type = ((YangLeafList) leaf).getDataType();
        }
        return getObjFromType(list, keysObj, leaf, key, keyObj, type);
    }
}
