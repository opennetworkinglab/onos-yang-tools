/*
 * Copyright 2016-present Open Networking Foundation
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

package org.onosproject.yang.compiler.linker.impl;

import org.onosproject.yang.compiler.datamodel.LeafRefInvalidHolder;
import org.onosproject.yang.compiler.datamodel.RpcNotificationContainer;
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangGrouping;
import org.onosproject.yang.compiler.datamodel.YangImport;
import org.onosproject.yang.compiler.datamodel.YangInclude;
import org.onosproject.yang.compiler.datamodel.YangInput;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.YangOutput;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangSubModule;
import org.onosproject.yang.compiler.datamodel.YangUses;
import org.onosproject.yang.compiler.linker.exceptions.LinkerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.onosproject.yang.compiler.datamodel.exceptions.ErrorMessages.getErrorMsg;
import static org.onosproject.yang.compiler.linker.impl.PrefixResolverType.INTER_TO_INTER;
import static org.onosproject.yang.compiler.linker.impl.PrefixResolverType.INTER_TO_INTRA;
import static org.onosproject.yang.compiler.linker.impl.PrefixResolverType.INTRA_TO_INTER;
import static org.onosproject.yang.compiler.linker.impl.PrefixResolverType.NO_PREFIX_CHANGE_FOR_INTER;
import static org.onosproject.yang.compiler.linker.impl.PrefixResolverType.NO_PREFIX_CHANGE_FOR_INTRA;
import static org.onosproject.yang.compiler.linker.impl.XpathLinkingTypes.AUGMENT_LINKING;
import static org.onosproject.yang.compiler.linker.impl.XpathLinkingTypes.USES_AUGMENT_LINKING;
import static org.onosproject.yang.compiler.utils.UtilConstants.COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.ERROR_MSG_FOR_AUGMENT_LINKING;
import static org.onosproject.yang.compiler.utils.UtilConstants.FAILED_TO_FIND_LEAD_INFO_HOLDER;
import static org.onosproject.yang.compiler.utils.UtilConstants.INPUT;
import static org.onosproject.yang.compiler.utils.UtilConstants.IS_INVALID;
import static org.onosproject.yang.compiler.utils.UtilConstants.LEAFREF_ERROR;
import static org.onosproject.yang.compiler.utils.UtilConstants.LEAF_HOLDER_ERROR;
import static org.onosproject.yang.compiler.utils.UtilConstants.OUTPUT;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH_FOR_STRING;

/**
 * Represents x-path linking.
 *
 * @param <T> x-path linking can be done for target node or for target leaf/leaf-list
 */
public class YangXpathLinker<T> {

    private List<YangAtomicPath> absPaths;
    private YangNode rootNode;
    private Map<YangAtomicPath, PrefixResolverType> prefixResolverTypes;
    private String curPrefix;
    private String constructsParentsPrefix;
    private XpathLinkingTypes linkingType;

    /**
     * Creates an instance of x-path linker.
     */
    public YangXpathLinker() {
        absPaths = new ArrayList<>();
    }

    /**
     * Returns list of augment nodes.
     *
     * @param node root node
     * @return list of augment nodes
     */
    public List<YangAugment> getListOfYangAugment(YangNode node) {
        node = node.getChild();
        List<YangAugment> augments = new ArrayList<>();
        while (node != null) {
            if (node instanceof YangAugment) {
                augments.add((YangAugment) node);
            }
            node = node.getNextSibling();
        }
        return augments;
    }

    /**
     * Process absolute node path for target leaf.
     *
     * @param atomicPaths atomic path node list
     * @param root        root node
     * @param leafref     instance of YANG leafref
     * @param curLinking  x path linking type
     * @return linked target node
     */
    T processLeafRefXpathLinking(List<YangAtomicPath> atomicPaths, YangNode root,
                                 YangLeafRef leafref, XpathLinkingTypes curLinking) {

        YangNode targetNode;
        rootNode = root;
        prefixResolverTypes = new HashMap<>();
        linkingType = curLinking;
        parsePrefixResolverList(atomicPaths);
        YangAtomicPath leafRefPath = atomicPaths.get(atomicPaths.size() - 1);

        // When leaf-ref path contains only one absolute path.
        if (atomicPaths.size() == 1) {
            targetNode = getTargetNodeWhenPathSizeIsOne(atomicPaths);
        } else {
            for (YangAtomicPath atomicPath : atomicPaths) {
                if (atomicPath != leafRefPath) {
                    absPaths.add(atomicPath);
                }
            }
            targetNode = parseData(root);
        }
        if (targetNode == null) {
            targetNode = searchInSubModule(root);
        }

        // Invalid path presence in the node list is checked.
        validateInvalidNodesInThePath(leafref);

        if (targetNode != null) {
            YangLeaf targetLeaf = searchReferredLeaf(targetNode, leafRefPath
                    .getNodeIdentifier().getName());
            if (targetLeaf == null) {
                YangLeafList targetLeafList = searchReferredLeafList(
                        targetNode, leafRefPath.getNodeIdentifier().getName());
                if (targetLeafList != null) {
                    return (T) targetLeafList;
                } else {
                    LinkerException ex = new LinkerException(
                            FAILED_TO_FIND_LEAD_INFO_HOLDER + leafref.getPath());
                    ex.setCharPosition(leafref.getCharPosition());
                    ex.setLine(leafref.getLineNumber());
                    ex.setFileName(leafref.getFileName());
                    throw ex;
                }
            }
            return (T) targetLeaf;
        }
        return null;
    }

    /**
     * Validates the nodes in the path for any invalid node.
     *
     * @param leafref instance of YANG leafref
     */
    private void validateInvalidNodesInThePath(YangLeafRef leafref) {
        for (YangAtomicPath absolutePath : (Iterable<YangAtomicPath>) leafref
                .getAtomicPath()) {
            YangNode nodeInPath = absolutePath.getResolvedNode();

            if (nodeInPath instanceof LeafRefInvalidHolder) {
                LinkerException ex = new LinkerException(
                        LEAFREF_ERROR + leafref.getPath() + IS_INVALID);
                ex.setCharPosition(leafref.getCharPosition());
                ex.setLine(leafref.getLineNumber());
                ex.setFileName(leafref.getFileName());
                throw ex;
            }
        }
    }

    /**
     * Returns target node when leaf-ref has only one absolute path in list.
     *
     * @param paths absolute paths
     * @return target node
     */
    private YangNode getTargetNodeWhenPathSizeIsOne(List<YangAtomicPath> paths) {
        if (paths.get(0).getNodeIdentifier().getPrefix() != null &&
                !paths.get(0).getNodeIdentifier().getPrefix().equals(
                        getRootsPrefix(rootNode))) {
            return getImportedNode(rootNode, paths.get(0).getNodeIdentifier());
        }
        return rootNode;
    }

    /**
     * Process absolute node path linking for augment.
     *
     * @param paths      absolute path node list
     * @param root       root node
     * @param curLinking x path linker type
     * @return linked target node
     */
    public YangNode processXpathLinking(List<YangAtomicPath> paths,
                                        YangNode root, XpathLinkingTypes curLinking) {
        absPaths = paths;
        rootNode = root;
        prefixResolverTypes = new HashMap<>();
        linkingType = curLinking;
        parsePrefixResolverList(paths);
        YangNode targetNode = parseData(root);
        if (targetNode == null) {
            targetNode = searchInSubModule(root);
        }
        return targetNode;
    }

    /**
     * Processes uses augment linking.
     *
     * @param paths YANG atomic path list
     * @param uses  YANG uses
     * @return augmented node
     */
    public YangNode processUsesAugLinking(List<YangAtomicPath> paths,
                                          YangUses uses) {
        absPaths = paths;
        rootNode = uses;
        linkingType = USES_AUGMENT_LINKING;
        return parseUsesAugData(uses.getParent());
    }

    /**
     * Searches for the referred leaf in target node.
     *
     * @param targetNode target node
     * @param leafName   leaf name
     * @return target leaf
     */
    private YangLeaf searchReferredLeaf(YangNode targetNode, String leafName) {
        if (!(targetNode instanceof YangLeavesHolder)) {
            throw new LinkerException(getErrorMsg(
                    LEAF_HOLDER_ERROR, targetNode.getName(), targetNode
                            .getLineNumber(), targetNode.getCharPosition(),
                    targetNode.getFileName()));
        }
        YangLeavesHolder holder = (YangLeavesHolder) targetNode;
        List<YangLeaf> leaves = holder.getListOfLeaf();
        if (leaves != null && !leaves.isEmpty()) {
            for (YangLeaf leaf : leaves) {
                if (leaf.getName().equals(leafName)) {
                    return leaf;
                }
            }
        }
        return null;
    }

    /**
     * Searches for the referred leaf-list in target node.
     *
     * @param targetNode target node
     * @param name       leaf-list name
     * @return target leaf-list
     */
    private YangLeafList searchReferredLeafList(YangNode targetNode, String name) {
        if (!(targetNode instanceof YangLeavesHolder)) {
            throw new LinkerException(getErrorMsg(
                    LEAF_HOLDER_ERROR, targetNode.getName(), targetNode
                            .getLineNumber(), targetNode.getCharPosition(),
                    targetNode.getFileName()));
        }
        YangLeavesHolder holder = (YangLeavesHolder) targetNode;
        List<YangLeafList> leavesList = holder.getListOfLeafList();
        if (leavesList != null && !leavesList.isEmpty()) {
            for (YangLeafList leafList : leavesList) {
                if (leafList.getName().equals(name)) {
                    return leafList;
                }
            }
        }
        return null;
    }

    /**
     * Process linking using for node identifier for inter/intra file.
     *
     * @param root root node
     * @return linked target node
     */
    private YangNode parseData(YangNode root) {
        String rootPrefix = getRootsPrefix(root);
        constructsParentsPrefix = rootPrefix;
        Iterator<YangAtomicPath> pathIterator = absPaths.iterator();
        YangAtomicPath path = pathIterator.next();
        if (path.getNodeIdentifier().getPrefix() != null
                && !path.getNodeIdentifier().getPrefix().equals(rootPrefix)) {
            return parsePath(getImportedNode(root, path.getNodeIdentifier()));
        } else {
            return parsePath(root);
        }
    }

    /**
     * Process linking of target node in root node.
     *
     * @param root root node
     * @return linked target node
     */
    public YangNode parsePath(YangNode root) {
        YangNode tempNode = root;
        Stack<YangNode> linkerStack = new Stack<>();
        Iterator<YangAtomicPath> pathIterator = absPaths.iterator();
        YangAtomicPath tempPath = pathIterator.next();
        YangNodeIdentifier nodeId;
        curPrefix = tempPath.getNodeIdentifier().getPrefix();
        int index = 0;
        YangNode tempAugment;
        do {
            nodeId = tempPath.getNodeIdentifier();
            if (tempPath.getNodeIdentifier().getPrefix() == null) {
                tempAugment = resolveIntraFileAugment(tempPath, root);
            } else {
                if (!tempPath.getNodeIdentifier().getPrefix().equals(curPrefix)) {
                    curPrefix = tempPath.getNodeIdentifier().getPrefix();
                    root = getImportedNode(rootNode, tempPath.getNodeIdentifier());
                }
                tempAugment = resolveInterFileAugment(tempPath, root, index);
            }
            if (tempAugment != null) {
                linkerStack.push(tempNode);
                tempNode = tempAugment;
            }

            tempNode = searchTargetNode(tempNode, nodeId);

            if (tempNode == null && !linkerStack.isEmpty()) {
                tempNode = linkerStack.peek();
                linkerStack.pop();
                tempNode = searchTargetNode(tempNode, nodeId);
            }

            if (tempNode != null) {
                tempPath.setResolvedNode(tempNode);
                validateTempPathNode(tempNode);
            }

            if (index == absPaths.size() - 1) {
                break;
            }
            tempPath = pathIterator.next();
            index++;
        } while (validate(tempNode, index));
        return tempNode;
    }

    /**
     * Parses uses-augment data to finding the augmented node.
     *
     * @param node YANG uses parent
     * @return augmented node
     */
    private YangNode parseUsesAugData(YangNode node) {
        YangNode temp = node;
        YangNode tgt;
        Iterator<YangAtomicPath> it = absPaths.iterator();
        YangAtomicPath path;
        YangNodeIdentifier id;
        StringBuilder builder = new StringBuilder();
        while (it.hasNext()) {
            path = it.next();
            id = path.getNodeIdentifier();
            tgt = searchTargetNode(temp, id);
            if (tgt == null) {
                temp = getAugmentNode(temp, builder.toString());
                if (temp != null) {
                    tgt = searchTargetNode(temp, id);
                }
            }
            temp = tgt;
            builder.append(id.getName());
            builder.append("/");
        }
        return temp;
    }

    /**
     * Returns the augment node, that contains the node to be searched.
     *
     * @param temp parent YANG node
     * @param id   node id to be searched
     * @return YANG augment node
     */
    private YangAugment getAugmentNode(YangNode temp, String id) {
        String augId = getAugNodeId(id);
        YangNode parent = temp.getParent();
        List<YangUses> usesList = getUsesNode(parent);
        for (YangUses uses : usesList) {
            List<YangAugment> augList = getAugList(uses);
            if (!augList.isEmpty()) {
                for (YangAugment aug : augList) {
                    if (aug.getPrefixRemovedName().equals(augId)) {
                        return aug;
                    }
                }
            }
        }
        // TODO: Has to be more specific error message.
        throw new LinkerException("Invalid augment path");
    }

    /**
     * Gets the list of augments from the uses.
     *
     * @param uses YANG uses
     * @return list of YANG augment
     */
    private List<YangAugment> getAugList(YangUses uses) {
        List<YangAugment> augList = new LinkedList<>();
        YangNode child = uses.getChild();
        while (child != null) {
            if (child instanceof YangAugment) {
                augList.add((YangAugment) child);
            }
            child = child.getNextSibling();
        }
        return augList;
    }

    /**
     * Returns a proper YANG augment id to be searched.
     *
     * @param id YANG augment id
     * @return formatted id
     */
    private String getAugNodeId(String id) {
        int ind = id.indexOf(SLASH_FOR_STRING);
        try {
            id = id.substring(ind + 1, id.length() - 1);
        } catch (StringIndexOutOfBoundsException e) {
            throw new LinkerException("Invalid augment path: " + id);
        }
        StringBuilder str = new StringBuilder();
        String[] arr;

        arr = id.split(SLASH_FOR_STRING);
        for (String a : arr) {
            str.append(SLASH_FOR_STRING);
            str.append(a);
        }
        return str.toString();
    }

    /**
     * Returns the YANG uses node list.
     *
     * @param node parent node
     * @return YANG uses list
     */
    private List<YangUses> getUsesNode(YangNode node) {
        YangNode curNode = node.getChild();
        List<YangUses> usesList = new LinkedList<>();
        while (curNode != null) {
            if (curNode instanceof YangUses) {
                usesList.add((YangUses) curNode);
            }
            curNode = curNode.getNextSibling();
        }
        return usesList;
    }

    /**
     * Validates temp path nodes for augment linking.
     *
     * @param node temp path node
     */
    private void validateTempPathNode(YangNode node) {

        if (linkingType != AUGMENT_LINKING) {
            return;
        }
        if (node instanceof YangGrouping) {
            LinkerException ex = new LinkerException(
                    ERROR_MSG_FOR_AUGMENT_LINKING +
                            getAugmentNodeIdentifier(
                                    absPaths.get(absPaths.size() - 1).getNodeIdentifier(),
                                    absPaths,
                                    rootNode));
            ex.setFileName(rootNode.getFileName());
            throw ex;
        }
    }

    /**
     * Resolves intra file augment linking.
     *
     * @param tempPath temporary absolute path
     * @param root     root node
     * @return linked target node
     */
    private YangNode resolveIntraFileAugment(YangAtomicPath tempPath, YangNode root) {
        YangNode tempAugment;
        if (curPrefix != tempPath.getNodeIdentifier().getPrefix()) {
            root = getIncludedNode(rootNode, tempPath.getNodeIdentifier().getName());
            if (root == null) {
                root = getIncludedNode(rootNode, getAugmentNodeIdentifier(
                        tempPath.getNodeIdentifier(), absPaths, rootNode));
                if (root == null) {
                    root = rootNode;
                }
            }
        } else {
            if (curPrefix != null) {
                root = getImportedNode(root, tempPath.getNodeIdentifier());
            }
        }

        curPrefix = tempPath.getNodeIdentifier().getPrefix();
        tempAugment = getAugment(tempPath.getNodeIdentifier(), root, absPaths);
        if (tempAugment == null) {
            tempAugment = getAugment(tempPath.getNodeIdentifier(), rootNode,
                                     absPaths);
        }
        return tempAugment;
    }

    /**
     * Resolves inter file augment linking.
     *
     * @param tempPath temporary absolute path
     * @param root     root node
     * @param size     node size
     * @return linked target node
     */
    private YangNode resolveInterFileAugment(YangAtomicPath tempPath,
                                             YangNode root, int size) {
        YangNode tempAugment = getAugment(tempPath.getNodeIdentifier(), root, absPaths);
        if (tempAugment == null) {
            return resolveInterToInterFileAugment(root, size);
        }
        return tempAugment;
    }

    /**
     * Resolves augment when prefix changed from inter file to inter file.
     * it may be possible that the prefix used in imported module is different
     * the given list of node identifiers.
     *
     * @param root root node
     * @param size node size
     * @return target node
     */
    private YangNode resolveInterToInterFileAugment(YangNode root, int size) {
        List<YangAugment> augments = getListOfYangAugment(root);
        int index;
        List<YangAtomicPath> paths = new ArrayList<>();
        for (YangAugment augment : augments) {
            index = 0;

            for (YangAtomicPath path : augment.getTargetNode()) {

                if (!searchForAugmentInImportedNode(path.getNodeIdentifier(),
                                                    index)) {
                    paths.clear();
                    break;
                }
                paths.add(path);
                index++;
            }
            if (!paths.isEmpty() && paths.size() == size) {
                return augment;
            } else {
                paths.clear();
            }
        }
        return null;
    }

    /**
     * Searches for the augment node in imported module when prefix has changed from
     * inter file to inter file.
     *
     * @param nodeId node id
     * @param index  index
     * @return true if found
     */
    private boolean searchForAugmentInImportedNode(YangNodeIdentifier nodeId,
                                                   int index) {
        if (index == absPaths.size()) {
            return false;
        }
        YangNodeIdentifier tempNodeId = absPaths.get(index).getNodeIdentifier();
        return nodeId.getName().equals(tempNodeId.getName());
    }

    /**
     * Returns augment node.
     *
     * @param tempNodeId temporary absolute path id
     * @param root       root node
     * @return linked target node
     */
    private YangNode getAugment(YangNodeIdentifier tempNodeId, YangNode root,
                                List<YangAtomicPath> absPaths) {
        String augmentName = getAugmentNodeIdentifier(tempNodeId, absPaths, root);
        if (augmentName != null) {
            return searchAugmentNode(root, augmentName);
        }
        return null;
    }

    /**
     * Process linking using import list.
     *
     * @param root   root node
     * @param nodeId node identifier
     * @return linked target node
     */
    private YangNode getImportedNode(YangNode root, YangNodeIdentifier nodeId) {

        List<YangImport> importList;

        if (root instanceof YangModule) {
            importList = ((YangModule) root).getImportList();
        } else {
            importList = ((YangSubModule) root).getImportList();
        }

        for (YangImport imported : importList) {
            if (imported.getPrefixId().equals(nodeId.getPrefix())) {
                return imported.getImportedNode();
            }
        }

        if (nodeId.getName() != null && nodeId.getPrefix()
                .equals(constructsParentsPrefix)) {
            return rootNode;
        }
        return root;
    }

    /**
     * Searches in sub-module node.
     *
     * @param root root node
     * @return target linked node
     */
    private YangNode searchInSubModule(YangNode root) {
        List<YangInclude> includeList;
        YangNode tempNode;
        if (root instanceof YangModule) {
            includeList = ((YangModule) root).getIncludeList();
        } else {
            includeList = ((YangSubModule) root).getIncludeList();
        }

        for (YangInclude included : includeList) {
            tempNode = parseData(included.getIncludedNode());
            if (tempNode != null) {
                return tempNode;
            }
        }
        return null;
    }

    /**
     * Process linking using include list.
     *
     * @param root         root node
     * @param tempPathName temporary path node name
     * @return linked target node
     */
    private YangNode getIncludedNode(YangNode root, String tempPathName) {

        List<YangInclude> includeList;

        if (root instanceof YangModule) {
            includeList = ((YangModule) root).getIncludeList();
        } else {
            includeList = ((YangSubModule) root).getIncludeList();
        }

        for (YangInclude included : includeList) {
            if (verifyChildNode(included.getIncludedNode(), tempPathName)) {
                return included.getIncludedNode();
            }
        }

        return null;
    }

    /**
     * Verifies for child nodes in sub module.
     *
     * @param node submodule node
     * @param name name of child node
     * @return true if child node found
     */
    private boolean verifyChildNode(YangNode node, String name) {
        node = node.getChild();
        while (node != null) {
            if (node.getName().equals(name)) {
                return true;
            }
            node = node.getNextSibling();
        }
        return false;
    }


    /**
     * Returns augment's node id.
     *
     * @param nodeId   node identifier
     * @param absPaths absolute paths
     * @param root     root node
     * @return augment's node id
     */
    private String getAugmentNodeIdentifier(
            YangNodeIdentifier nodeId, List<YangAtomicPath> absPaths, YangNode root) {
        Iterator<YangAtomicPath> nodeIdIterator = absPaths.iterator();
        YangAtomicPath tempNodeId;
        StringBuilder builder = new StringBuilder();
        String name;
        String prefix;
        String id;
        PrefixResolverType type;
        while (nodeIdIterator.hasNext()) {
            tempNodeId = nodeIdIterator.next();
            name = tempNodeId.getNodeIdentifier().getName();
            prefix = tempNodeId.getNodeIdentifier().getPrefix();
            if (!tempNodeId.getNodeIdentifier().equals(nodeId)) {
                type = prefixResolverTypes.get(tempNodeId);
                switch (type) {
                    case INTER_TO_INTRA:
                        id = SLASH_FOR_STRING + name;
                        break;
                    case INTRA_TO_INTER:
                        if (!getRootsPrefix(root).equals(prefix)) {
                            id = SLASH_FOR_STRING + prefix + COLON + name;
                        } else {
                            id = SLASH_FOR_STRING + name;
                        }
                        break;
                    case INTER_TO_INTER:
                        id = SLASH_FOR_STRING + prefix + COLON + name;
                        break;
                    case NO_PREFIX_CHANGE_FOR_INTRA:
                        id = SLASH_FOR_STRING + name;
                        break;
                    case NO_PREFIX_CHANGE_FOR_INTER:
                        if (!getRootsPrefix(root).equals(prefix)) {
                            id = SLASH_FOR_STRING + prefix + COLON + name;
                        } else {
                            id = SLASH_FOR_STRING + name;
                        }
                        break;
                    default:
                        id = SLASH_FOR_STRING + name;
                        break;
                }
                builder.append(id);
            } else {
                return builder.toString();
            }
        }
        return null;
    }

    /**
     * Searches augment node in root node by name of the augment. For intra
     * file augment, target augment name without prefix is taken and checked.
     *
     * @param root    root node
     * @param augName current augment name
     * @return target augment node
     */
    private YangNode searchAugmentNode(YangNode root, String augName) {
        YangNode node = root;
        node = node.getChild();
        while (node != null) {
            if (node instanceof YangAugment) {
                String name = ((YangAugment) node).getPrefixRemovedName();
                if (node.getName().equals(augName) || name.equals(augName)) {
                    return node;
                }
            }
            node = node.getNextSibling();
        }
        return null;
    }

    /**
     * Validates for target node if target node found or not.
     *
     * @param tempNode temporary node
     * @param index    current index of list
     * @return false if target node found
     */
    private boolean validate(YangSchemaNode tempNode, int index) {

        int size = absPaths.size();
        if (tempNode != null && index != size) {
            return true;
        } else if (tempNode != null) {
            return false;
            // this is your target node.
        } else if (index != size) {
            return true;
            // this could be in submodule as well.
        }
        return false;
    }

    /**
     * Searches target node in root node.
     *
     * @param node      root node
     * @param curNodeId YANG node identifier
     * @return linked target node
     */
    private YangNode searchTargetNode(YangNode node,
                                      YangNodeIdentifier curNodeId) {

        if (linkingType == XpathLinkingTypes.DEVIATION_LINKING &&
                node instanceof YangLeavesHolder) {
            YangNode targetNode = searchTargetLeaf(node, curNodeId);
            if (targetNode != null) {
                return targetNode;
            }
        }

        if (node != null) {
            node = node.getChild();
        }
        while (node != null) {
            if (node instanceof YangInput) {
                if (curNodeId.getName().equalsIgnoreCase(INPUT)) {
                    return node;
                }
            } else if (node instanceof YangOutput) {
                if (curNodeId.getName().equalsIgnoreCase(OUTPUT)) {
                    return node;
                }
            }
            if (node.getName().equals(curNodeId.getName()) &&
                    !(node instanceof YangUses)) {
                return node;
            }

            if (linkingType == XpathLinkingTypes.DEVIATION_LINKING &&
                    node instanceof YangLeavesHolder) {
                YangNode targetNode = searchTargetLeaf(node, curNodeId);
                if (targetNode != null) {
                    return targetNode;
                }
            }
            node = node.getNextSibling();
        }
        return null;
    }

    /**
     * Searches target leaf in root node.
     *
     * @param node      root node
     * @param curNodeId YANG node identifier
     * @return linked target leaf node holder
     */
    private YangNode searchTargetLeaf(YangNode node,
                                      YangNodeIdentifier curNodeId) {
        YangLeavesHolder holder = (YangLeavesHolder) node;
        List<YangLeafList> leavesList = holder.getListOfLeafList();
        if (leavesList != null && !leavesList.isEmpty()) {
            for (YangLeafList leafList : leavesList) {
                if (leafList.getName().equals(curNodeId.getName())) {
                    return node;
                }
            }
        }

        List<YangLeaf> leaves = holder.getListOfLeaf();
        if (leaves != null && !leaves.isEmpty()) {
            for (YangLeaf leaf : leaves) {
                if (leaf.getName().equals(curNodeId.getName())) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Returns root prefix.
     *
     * @param root root node
     * @return root prefix
     */
    private String getRootsPrefix(YangNode root) {
        if (root instanceof YangModule) {
            return ((YangModule) root).getPrefix();
        } else {
            return ((YangSubModule) root).getPrefix();
        }
    }

    /**
     * Resolves prefix and provides prefix resolver list.
     *
     * @param absolutePaths absolute paths
     */
    private void parsePrefixResolverList(List<YangAtomicPath> absolutePaths) {
        Iterator<YangAtomicPath> pathIterator = absolutePaths.iterator();
        YangAtomicPath absPath;
        String prePrefix;
        String curPrefix = null;
        String rootPrefix = getRootsPrefix(rootNode);
        while (pathIterator.hasNext()) {
            prePrefix = curPrefix;
            absPath = pathIterator.next();
            curPrefix = absPath.getNodeIdentifier().getPrefix();
            if (curPrefix != null && !(curPrefix.equals(rootPrefix))) {
                if (!curPrefix.equals(prePrefix)) {
                    if (prePrefix != null) {
                        prefixResolverTypes.put(absPath, INTER_TO_INTER);
                    } else {
                        prefixResolverTypes.put(absPath, INTRA_TO_INTER);
                    }
                } else {
                    prefixResolverTypes.put(absPath, NO_PREFIX_CHANGE_FOR_INTER);
                }
            } else {
                if (prePrefix != null) {
                    prefixResolverTypes.put(absPath, INTER_TO_INTRA);
                } else {
                    prefixResolverTypes.put(absPath, NO_PREFIX_CHANGE_FOR_INTRA);
                }
            }
        }
    }

    /**
     * Adds augment to rpc augmented list of input.
     *
     * @param augment  augment
     * @param rootNode root node
     */
    void addInModuleIfInput(YangAugment augment,
                            YangNode rootNode) {
        ((RpcNotificationContainer) rootNode).addToAugmentList(augment);
    }
}