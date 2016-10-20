/*
 * Copyright 2016-present Open Networking Laboratory
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

package org.onosproject.yangutils.linker.impl;

import org.onosproject.yangutils.datamodel.TraversalType;
import org.onosproject.yangutils.datamodel.YangAtomicPath;
import org.onosproject.yangutils.datamodel.YangAugment;
import org.onosproject.yangutils.datamodel.YangAugmentableNode;
import org.onosproject.yangutils.datamodel.YangBase;
import org.onosproject.yangutils.datamodel.YangCase;
import org.onosproject.yangutils.datamodel.YangChoice;
import org.onosproject.yangutils.datamodel.YangGrouping;
import org.onosproject.yangutils.datamodel.YangIdentityRef;
import org.onosproject.yangutils.datamodel.YangIfFeature;
import org.onosproject.yangutils.datamodel.YangImport;
import org.onosproject.yangutils.datamodel.YangInclude;
import org.onosproject.yangutils.datamodel.YangLeaf;
import org.onosproject.yangutils.datamodel.YangLeafList;
import org.onosproject.yangutils.datamodel.YangLeafRef;
import org.onosproject.yangutils.datamodel.YangLeavesHolder;
import org.onosproject.yangutils.datamodel.YangList;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangNodeIdentifier;
import org.onosproject.yangutils.datamodel.YangPathPredicate;
import org.onosproject.yangutils.datamodel.YangReferenceResolver;
import org.onosproject.yangutils.datamodel.YangRelativePath;
import org.onosproject.yangutils.datamodel.YangType;
import org.onosproject.yangutils.datamodel.YangTypeDef;
import org.onosproject.yangutils.datamodel.YangUses;
import org.onosproject.yangutils.datamodel.exceptions.DataModelException;
import org.onosproject.yangutils.datamodel.utils.ResolvableStatus;
import org.onosproject.yangutils.datamodel.utils.YangConstructType;
import org.onosproject.yangutils.linker.exceptions.LinkerException;
import org.onosproject.yangutils.translator.exception.TranslatorException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.onosproject.yangutils.datamodel.TraversalType.CHILD;
import static org.onosproject.yangutils.datamodel.TraversalType.PARENT;
import static org.onosproject.yangutils.datamodel.TraversalType.ROOT;
import static org.onosproject.yangutils.datamodel.TraversalType.SIBILING;
import static org.onosproject.yangutils.datamodel.exceptions.ErrorMessages.COLLISION_DETECTION;
import static org.onosproject.yangutils.datamodel.exceptions.ErrorMessages.FAILED_TO_ADD_CASE;
import static org.onosproject.yangutils.datamodel.exceptions.ErrorMessages.TARGET_NODE;
import static org.onosproject.yangutils.datamodel.exceptions.ErrorMessages.TARGET_NODE_LEAF_INFO;
import static org.onosproject.yangutils.datamodel.exceptions.ErrorMessages.getErrorMsg;
import static org.onosproject.yangutils.datamodel.exceptions.ErrorMessages.getErrorMsgCollision;
import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.addResolutionInfo;
import static org.onosproject.yangutils.datamodel.utils.GeneratedLanguage.JAVA_GENERATION;
import static org.onosproject.yangutils.datamodel.utils.YangConstructType.getYangConstructType;
import static org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes.DERIVED;
import static org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes.IDENTITYREF;
import static org.onosproject.yangutils.translator.tojava.YangDataModelFactory.getYangCaseNode;
import static org.onosproject.yangutils.utils.UtilConstants.BASE_LINKER_ERROR;
import static org.onosproject.yangutils.utils.UtilConstants.COLON;
import static org.onosproject.yangutils.utils.UtilConstants.COMMA;
import static org.onosproject.yangutils.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.FEATURE_LINKER_ERROR;
import static org.onosproject.yangutils.utils.UtilConstants.GROUPING_LINKER_ERROR;
import static org.onosproject.yangutils.utils.UtilConstants.IDENTITYREF_LINKER_ERROR;
import static org.onosproject.yangutils.utils.UtilConstants.INVALID_TREE;
import static org.onosproject.yangutils.utils.UtilConstants.IS_INVALID;
import static org.onosproject.yangutils.utils.UtilConstants.LEAFREF_ERROR;
import static org.onosproject.yangutils.utils.UtilConstants.LEAFREF_LINKER_ERROR;
import static org.onosproject.yangutils.utils.UtilConstants.TYPEDEF_LINKER_ERROR;

/**
 * Represent utilities for YANG linker.
 */
public final class YangLinkerUtils {

    private static final int IDENTIFIER_LENGTH = 64;
    private static final Pattern IDENTIFIER_PATTERN =
            Pattern.compile("[a-zA-Z_][a-zA-Z0-9_.-]*");
    private static final String XML = "xml";
    private static final String INVALID_PATH_PRE =
            "YANG file error: The path predicate of the leafref has an " +
                    "invalid path in ";
    private static final String EMPTY_PATH_LIST_ERR =
            "YANG file error : The atomic path list cannot be empty of the " +
                    "leafref in the path ";
    private static final String TGT_LEAF_ERR =
            "YANG file error: There is no leaf/leaf-list in YANG node as " +
                    "mentioned in the path predicate of the leafref path ";
    private static final String LEAF_REF_LIST_ERR =
            "YANG file error: Path predicates are only applicable for YANG " +
                    "list. The leafref path has path predicate for non-list " +
                    "node in the path ";

    // No instantiation.
    private YangLinkerUtils() {
    }

    /**
     * Detects collision between target nodes leaf/leaf-list or child node with augmented leaf/leaf-list or child node.
     *
     * @param targetNode target node
     * @param augment    augment node
     */
    private static void detectCollision(YangNode targetNode, YangAugment augment) {
        YangNode targetNodesChild = targetNode.getChild();
        YangNode augmentsChild = augment.getChild();
        if (targetNode instanceof YangChoice) {
            addCaseNodeToChoiceTarget(augment);
        } else {
            detectCollisionInLeaveHolders(targetNode, augment);
            while (augmentsChild != null) {
                detectCollisionInChildNodes(targetNodesChild, augmentsChild);
                augmentsChild = augmentsChild.getNextSibling();
            }
        }
    }

    /*Detects collision between leaves/leaf-lists*/
    private static void detectCollisionInLeaveHolders(YangNode targetNode, YangAugment augment) {
        YangLeavesHolder targetNodesLeavesHolder = (YangLeavesHolder) targetNode;
        if (augment.getListOfLeaf() != null && augment.getListOfLeaf().isEmpty() &&
                targetNodesLeavesHolder.getListOfLeaf() != null) {
            for (YangLeaf leaf : augment.getListOfLeaf()) {
                for (YangLeaf targetLeaf : targetNodesLeavesHolder.getListOfLeaf()) {
                    detectCollision(targetLeaf.getName(), leaf.getName(),
                                    leaf.getLineNumber(),
                                    leaf.getCharPosition(),
                                    leaf.getFileName(), TARGET_NODE_LEAF_INFO);
                }
            }
        }
        if (augment.getListOfLeafList() != null &&
                augment.getListOfLeafList().isEmpty() &&
                targetNodesLeavesHolder.getListOfLeafList() != null) {
            for (YangLeafList leafList : augment.getListOfLeafList()) {
                for (YangLeafList targetLeafList : targetNodesLeavesHolder.getListOfLeafList()) {
                    detectCollision(targetLeafList.getName(), leafList.getName(),
                                    leafList.getLineNumber(),
                                    leafList.getCharPosition(),
                                    leafList.getFileName(), TARGET_NODE_LEAF_INFO);
                }
            }
        }
    }


    private static void detectCollision(String first, String second,
                                        int line, int position, String
                                                fileName, String type) {
        if (first.equals(second)) {
            throw new LinkerException(getErrorMsgCollision(
                    COLLISION_DETECTION, second, line, position, type,
                    fileName));
        }
    }

    /*Detects collision for child nodes.*/
    private static void detectCollisionInChildNodes(YangNode targetNodesChild,
                                                    YangNode augmentsChild) {
        while (augmentsChild != null) {
            while (targetNodesChild != null) {
                if (targetNodesChild.getName().equals(augmentsChild.getName())) {
                    detectCollision(targetNodesChild.getName(), augmentsChild.getName(),
                                    augmentsChild.getLineNumber(),
                                    augmentsChild.getCharPosition(),
                                    augmentsChild.getFileName(), TARGET_NODE);
                }
                targetNodesChild = targetNodesChild.getNextSibling();
            }
            augmentsChild = augmentsChild.getNextSibling();
        }
    }

    /**
     * Adds a case node in augment when augmenting a choice node.
     *
     * @param augment augment node
     */
    private static void addCaseNodeToChoiceTarget(YangAugment augment) {
        try {
            YangNode child = augment.getChild();
            List<YangNode> childNodes = new ArrayList<>();
            while (child != null) {
                childNodes.add(child);
                child = child.getNextSibling();
            }
            augment.setChild(null);

            for (YangNode node : childNodes) {
                Map<YangNode, List<YangNode>> map = new LinkedHashMap<>();
                node.setNextSibling(null);
                node.setPreviousSibling(null);
                node.setParent(null);
                YangCase javaCase = getYangCaseNode(JAVA_GENERATION);
                javaCase.setName(node.getName());
                //Break the tree to from a new tree.
                traverseAndBreak(node, map);
                augment.addChild(javaCase);
                node.setParent(javaCase);
                javaCase.addChild(node);
                //Connect each node to its correct parent again.
                connectTree(map);
            }
            if (augment.getListOfLeaf() != null) {
                for (YangLeaf leaf : augment.getListOfLeaf()) {
                    YangCase javaCase = getYangCaseNode(JAVA_GENERATION);
                    javaCase.setName(leaf.getName());
                    javaCase.addLeaf(leaf);
                    augment.addChild(javaCase);

                }
                augment.getListOfLeaf().clear();
            }
            if (augment.getListOfLeafList() != null) {
                for (YangLeafList leafList : augment.getListOfLeafList()) {
                    YangCase javaCase = getYangCaseNode(JAVA_GENERATION);
                    javaCase.setName(leafList.getName());
                    javaCase.addLeafList(leafList);
                    augment.addChild(javaCase);
                }
                augment.getListOfLeafList().clear();
            }

        } catch (DataModelException e) {
            throw new TranslatorException(
                    getErrorMsg(FAILED_TO_ADD_CASE, augment.getName(),
                                augment.getLineNumber(), augment.getCharPosition(),
                                augment.getFileName()));
        }
    }

    private static void connectTree(Map<YangNode, List<YangNode>> map)
            throws DataModelException {
        ArrayList<YangNode> keys = new ArrayList<>(map.keySet());
        int size = keys.size();
        for (int i = size - 1; i >= 0; i--) {
            YangNode curNode = keys.get(i);
            List<YangNode> nodes = map.get(curNode);
            if (nodes != null) {
                for (YangNode node : nodes) {
                    curNode.addChild(node);
                }
            }
        }
        map.clear();
    }

    private static void processHierarchyChild(YangNode node,
                                              Map<YangNode, List<YangNode>> map) {
        YangNode child = node.getChild();
        if (child != null) {
            List<YangNode> nodes = new ArrayList<>();
            while (child != null) {
                nodes.add(child);
                child.setParent(null);
                child = child.getNextSibling();
                if (child != null) {
                    child.getPreviousSibling().setNextSibling(null);
                    child.setPreviousSibling(null);
                }
            }
            map.put(node, nodes);
        }
        node.setChild(null);
    }

    private static void traverseAndBreak(YangNode rootNode,
                                         Map<YangNode, List<YangNode>> map) {

        YangNode curNode = rootNode;
        TraversalType curTraversal = ROOT;
        while (curNode != null) {
            if (curTraversal != PARENT && curNode.getChild() != null) {
                curTraversal = CHILD;
                curNode = curNode.getChild();
            } else if (curNode.getNextSibling() != null) {
                curTraversal = SIBILING;
                curNode = curNode.getNextSibling();
            } else {
                curTraversal = PARENT;
                curNode = curNode.getParent();
                if (curNode != null) {
                    processHierarchyChild(curNode, map);
                }
            }
        }
    }

    /**
     * Returns error messages.
     *
     * @param resolvable resolvable entity
     * @return error message
     */
    static String getErrorInfoForLinker(Object resolvable) {
        if (resolvable instanceof YangType) {
            return TYPEDEF_LINKER_ERROR;
        }
        if (resolvable instanceof YangUses) {
            return GROUPING_LINKER_ERROR;
        }
        if (resolvable instanceof YangIfFeature) {
            return FEATURE_LINKER_ERROR;
        }
        if (resolvable instanceof YangBase) {
            return BASE_LINKER_ERROR;
        }
        if (resolvable instanceof YangIdentityRef) {
            return IDENTITYREF_LINKER_ERROR;
        }
        return LEAFREF_LINKER_ERROR;
    }

    /**
     * Returns leafref's error message.
     *
     * @param leafref leaf ref
     * @return error message
     */
    static String getLeafRefErrorInfo(YangLeafRef leafref) {
        return getErrorMsg(
                LEAFREF_ERROR + leafref.getPath() + COMMA + IS_INVALID, EMPTY_STRING,
                leafref.getLineNumber(), leafref.getCharPosition(), leafref
                        .getFileName());
    }

    /**
     * Detects collision between target nodes and its all leaf/leaf-list or child node with augmented leaf/leaf-list or
     * child node.
     *
     * @param targetNode target node
     * @param augment    augment node
     */
    static void detectCollisionForAugmentedNode(YangNode targetNode, YangAugment augment) {
        // Detect collision for target node and augment node.
        detectCollision(targetNode, augment);
        List<YangAugment> yangAugmentedInfo = ((YangAugmentableNode) targetNode).getAugmentedInfoList();
        // Detect collision for target augment node and current augment node.
        for (YangAugment info : yangAugmentedInfo) {
            detectCollision(info, augment);
        }
    }

    /**
     * Returns list of path names that are needed from augment.
     *
     * @param augment            instance of YANG augment
     * @param remainingAncestors ancestor count to move in augment path
     * @return list of path names needed in leafref
     */
    static List<String> getPathWithAugment(YangAugment augment, int remainingAncestors) {
        List<String> listOfPathName = new ArrayList<>();
        for (YangAtomicPath atomicPath : augment.getTargetNode()) {
            if (atomicPath.getNodeIdentifier().getPrefix() != null &&
                    !atomicPath.getNodeIdentifier().getPrefix().equals(EMPTY_STRING)) {
                listOfPathName.add(atomicPath.getNodeIdentifier().getPrefix()
                                           + COLON + atomicPath.getNodeIdentifier().getName());
            } else {
                listOfPathName.add(atomicPath.getNodeIdentifier().getName());
            }
        }
        for (int countOfAncestor = 0; countOfAncestor < remainingAncestors; countOfAncestor++) {
            listOfPathName.remove(listOfPathName.size() - 1);
        }
        return listOfPathName;
    }

    /**
     * Skips the invalid nodes which cannot have data from YANG.
     *
     * @param curParent current parent
     * @param leafRef   YANG leaf-ref
     * @return parent node which can hold data
     * @throws LinkerException if linker rules are violated
     */
    public static YangNode skipInvalidDataNodes(YangNode curParent,
                                                YangLeafRef leafRef)
            throws LinkerException {

        YangNode node = curParent;
        while (node instanceof YangChoice ||
                node instanceof YangCase) {

            if (node.getParent() == null) {
                throw new LinkerException(getLeafRefErrorInfo(leafRef));
            }
            node = node.getParent();
        }
        return node;
    }

    /**
     * Checks and return valid node identifier.
     *
     * @param nodeIdentifierString string from yang file
     * @param yangConstruct        yang construct for creating error message
     * @return valid node identifier
     */
    static YangNodeIdentifier getValidNodeIdentifier(String nodeIdentifierString,
                                                     YangConstructType yangConstruct) {
        String[] tmpData = nodeIdentifierString.split(Pattern.quote(COLON));
        if (tmpData.length == 1) {
            YangNodeIdentifier nodeIdentifier = new YangNodeIdentifier();
            nodeIdentifier.setName(getValidIdentifier(tmpData[0], yangConstruct));
            return nodeIdentifier;
        } else if (tmpData.length == 2) {
            YangNodeIdentifier nodeIdentifier = new YangNodeIdentifier();
            nodeIdentifier.setPrefix(getValidIdentifier(tmpData[0], yangConstruct));
            nodeIdentifier.setName(getValidIdentifier(tmpData[1], yangConstruct));
            return nodeIdentifier;
        } else {
            throw new LinkerException("YANG file error : " +
                                              getYangConstructType(yangConstruct) + " name " + nodeIdentifierString +
                                              " is not valid.");
        }
    }

    /**
     * Validates identifier and returns concatenated string if string contains plus symbol.
     *
     * @param identifier    string from yang file
     * @param yangConstruct yang construct for creating error message=
     * @return concatenated string after removing double quotes
     */
    public static String getValidIdentifier(String identifier, YangConstructType yangConstruct) {

        if (identifier.length() > IDENTIFIER_LENGTH) {
            throw new LinkerException("YANG file error : " +
                                              getYangConstructType(yangConstruct) + " name " + identifier + " is " +
                                              "greater than 64 characters.");
        } else if (!IDENTIFIER_PATTERN.matcher(identifier).matches()) {
            throw new LinkerException("YANG file error : " +
                                              getYangConstructType(yangConstruct) + " name " + identifier + " is not " +
                                              "valid.");
        } else if (identifier.toLowerCase().startsWith(XML)) {
            throw new LinkerException("YANG file error : " +
                                              getYangConstructType(yangConstruct) + " identifier " + identifier +
                                              " must not start with (('X'|'x') ('M'|'m') ('L'|'l')).");
        } else {
            return identifier;
        }
    }

    /**
     * Updates the priority for all the input files.
     *
     * @param yangNodeSet set of YANG files info
     */
    public static void updateFilePriority(Set<YangNode> yangNodeSet) {
        for (YangNode yangNode : yangNodeSet) {
            updateFilePriorityOfNode(yangNode);
        }
    }

    /**
     * Updates priority of the node.
     *
     * @param yangNode YANG node information
     */
    private static void updateFilePriorityOfNode(YangNode yangNode) {
        int curNodePriority = yangNode.getPriority();
        if (yangNode instanceof YangReferenceResolver) {
            List<YangImport> yangImportList = ((YangReferenceResolver) yangNode).getImportList();
            Iterator<YangImport> importInfoIterator = yangImportList.iterator();
            // Run through the imported list to update priority.
            while (importInfoIterator.hasNext()) {
                YangImport yangImport = importInfoIterator.next();
                YangNode importedNode = yangImport.getImportedNode();
                if (curNodePriority >= importedNode.getPriority()) {
                    importedNode.setPriority(curNodePriority + 1);
                    updateFilePriorityOfNode(importedNode);
                }
            }

            List<YangInclude> yangIncludeList = ((YangReferenceResolver) yangNode).getIncludeList();
            Iterator<YangInclude> includeInfoIterator = yangIncludeList.iterator();
            // Run through the imported list to update priority.
            while (includeInfoIterator.hasNext()) {
                YangInclude yangInclude = includeInfoIterator.next();
                YangNode includedNode = yangInclude.getIncludedNode();
                if (curNodePriority >= includedNode.getPriority()) {
                    includedNode.setPriority(curNodePriority + 1);
                    updateFilePriorityOfNode(includedNode);
                }
            }
        }
    }

    /**
     * Add the unresolved data under the root leve grouping to be resolved, since it will be used in interfile uses.
     *
     * @param referenceResolver module / sub-module
     */
    public static void resolveGroupingInDefinationScope(YangReferenceResolver referenceResolver) {
        YangNode potentialInterFileGrouping = ((YangNode) referenceResolver).getChild();

        while (potentialInterFileGrouping != null) {
            if (potentialInterFileGrouping instanceof YangGrouping) {
                addGroupingResolvableEntitiesToResolutionList((YangGrouping) potentialInterFileGrouping);
            }

            potentialInterFileGrouping = potentialInterFileGrouping.getNextSibling();
        }
    }

    /**
     * Add the interfile grouping resolvable entities to reesolution list.
     *
     * @param interFileGrouping interfile grouping
     */
    private static void addGroupingResolvableEntitiesToResolutionList(YangGrouping interFileGrouping) {
        YangNode curNode = interFileGrouping;
        TraversalType curTraversal = ROOT;
        addResolvableLeavesToResolutionList((YangLeavesHolder) curNode);
        curTraversal = CHILD;
        curNode = interFileGrouping.getChild();
        if (curNode == null) {
            return;
        }
        while (curNode != interFileGrouping) {
            if (curTraversal != PARENT) {
                if (curNode instanceof YangGrouping || curNode instanceof YangUses) {
                    if (curNode.getNextSibling() != null) {
                        curTraversal = SIBILING;
                        curNode = curNode.getNextSibling();
                    } else {
                        curTraversal = PARENT;
                        curNode = curNode.getParent();
                    }
                    continue;
                }

                if (curNode instanceof YangLeavesHolder) {
                    addResolvableLeavesToResolutionList((YangLeavesHolder) curNode);
                } else if (curNode instanceof YangTypeDef) {
                    List<YangType<?>> typeList = ((YangTypeDef) curNode).getTypeList();
                    if (!typeList.isEmpty()) {
                        YangType<?> type = typeList.get(0);
                        if (type.getDataType() == DERIVED) {
                            if (type.getResolvableStatus() != ResolvableStatus.RESOLVED) {

                                type.setTypeForInterFileGroupingResolution(true);

                                // Add resolution information to the list
                                YangResolutionInfoImpl resolutionInfo =
                                        new YangResolutionInfoImpl<YangType>(type, curNode, type.getLineNumber(),
                                                                             type.getCharPosition());
                                try {
                                    addResolutionInfo(resolutionInfo);
                                } catch (DataModelException e) {
                                    String errorInfo = "Error in file: " + curNode.getName() + " in " +
                                            curNode.getFileName() + " at " +
                                            "line: " + e.getLineNumber() +
                                            " at position: " + e.getCharPositionInLine()
                                            + e.getLocalizedMessage();
                                    throw new LinkerException("Failed to add type info in grouping to resolution "
                                                                      + errorInfo);
                                }
                            }
                        }
                    }
                }

            }
            if (curTraversal != PARENT && curNode.getChild() != null) {
                curTraversal = CHILD;
                curNode = curNode.getChild();
            } else if (curNode.getNextSibling() != null) {

                curTraversal = SIBILING;
                curNode = curNode.getNextSibling();
            } else {
                curTraversal = PARENT;
                curNode = curNode.getParent();
            }
        }
    }

    /**
     * Add resolvable leaves type info to resolution list.
     *
     * @param leavesHolder leaves holder node
     */
    private static void addResolvableLeavesToResolutionList(YangLeavesHolder leavesHolder) {
        if (leavesHolder.getListOfLeaf() != null && !leavesHolder.getListOfLeaf().isEmpty()) {
            for (YangLeaf leaf : leavesHolder.getListOfLeaf()) {
                YangType type = leaf.getDataType();
                if (type.getDataType() == DERIVED) {

                    type.setTypeForInterFileGroupingResolution(true);

                    // Add resolution information to the list
                    YangResolutionInfoImpl resolutionInfo =
                            new YangResolutionInfoImpl<>(type, (YangNode) leavesHolder,
                                                         type.getLineNumber(), type.getCharPosition());
                    try {
                        addResolutionInfo(resolutionInfo);
                    } catch (DataModelException e) {
                        throw new LinkerException("Failed to add leaf type info in grouping, to resolution ");
                    }
                } else if (type.getDataType() == IDENTITYREF) {
                    YangIdentityRef identityRef = (YangIdentityRef) type.getDataTypeExtendedInfo();

                    identityRef.setIdentityForInterFileGroupingResolution(true);

                    // Add resolution information to the list
                    YangResolutionInfoImpl resolutionInfo =
                            new YangResolutionInfoImpl<YangIdentityRef>(identityRef, (YangNode) leavesHolder,
                                                                        identityRef.getLineNumber(), identityRef.getCharPosition());
                    try {
                        addResolutionInfo(resolutionInfo);
                    } catch (DataModelException e) {
                        throw new LinkerException("Failed to add leaf identity ref info in grouping, to resolution ");
                    }
                }
            }
        }

        if (leavesHolder.getListOfLeafList() != null && !leavesHolder.getListOfLeafList().isEmpty()) {
            for (YangLeafList leafList : leavesHolder.getListOfLeafList()) {
                YangType type = leafList.getDataType();
                if (type.getDataType() == DERIVED) {

                    type.setTypeForInterFileGroupingResolution(true);

                    // Add resolution information to the list
                    YangResolutionInfoImpl resolutionInfo =
                            new YangResolutionInfoImpl<YangType>(type, (YangNode) leavesHolder,
                                                                 type.getLineNumber(), type.getCharPosition());
                    try {
                        addResolutionInfo(resolutionInfo);
                    } catch (DataModelException e) {
                        throw new LinkerException("Failed to add leaf type info in grouping, to resolution ");
                    }
                } else if (type.getDataType() == IDENTITYREF) {
                    YangIdentityRef identityRef = (YangIdentityRef) type.getDataTypeExtendedInfo();

                    identityRef.setIdentityForInterFileGroupingResolution(true);
                    // Add resolution information to the list
                    YangResolutionInfoImpl resolutionInfo =
                            new YangResolutionInfoImpl<YangIdentityRef>(identityRef, (YangNode) leavesHolder,
                                                                        identityRef.getLineNumber(), identityRef.getCharPosition());
                    try {
                        addResolutionInfo(resolutionInfo);
                    } catch (DataModelException e) {
                        throw new LinkerException("Failed to add leaf identity ref info in grouping, to resolution ");
                    }
                }
            }
        }
    }

    /**
     * Fills the path predicates of the leaf-ref with right axis node and
     * left axis node, after linking the nodes.
     *
     * @param leafRef YANG leaf-ref
     * @throws DataModelException if there is a data model error
     */
    public static void fillPathPredicates(YangLeafRef<?> leafRef)
            throws DataModelException {

        List<YangAtomicPath> atomics = leafRef.getAtomicPath();
        if (atomics != null) {
            for (YangAtomicPath atom : atomics) {
                List<YangPathPredicate> predicates =
                        atom.getPathPredicatesList();

                if (predicates != null) {
                    for (YangPathPredicate predicate : predicates) {
                        setLeftAxisNode(leafRef, atom, predicate);
                        setRightAxisNode(leafRef, predicate);
                    }
                }
            }
        }
    }

    /**
     * Sets the left axis node in the YANG path predicate after finding it
     * under the YANG list node.
     *
     * @param leafRef   YANG leaf-ref
     * @param atom      atomic path content
     * @param predicate predicate in the atomic path
     * @throws DataModelException if there is a data model error
     */
    private static void setLeftAxisNode(YangLeafRef<?> leafRef,
                                        YangAtomicPath atom,
                                        YangPathPredicate predicate)
            throws DataModelException {
        YangNode resolvedNode = atom.getResolvedNode();
        if (!(resolvedNode instanceof YangList)) {
            throw getDataModelExc(LEAF_REF_LIST_ERR, leafRef);
        }

        YangNodeIdentifier leftAxisName = predicate.getNodeId();
        Object target = getTarget(leftAxisName, resolvedNode, leafRef);
        predicate.setLeftAxisNode(target);
    }

    /**
     * Returns the target leaf/leaf-list from the provided YANG node.
     *
     * @param leftAxisName name of node
     * @param node         node having target
     * @param leafRef      YANG leaf-ref
     * @return target leaf/leaf-list
     * @throws DataModelException if there is a data model error
     */
    private static Object getTarget(YangNodeIdentifier leftAxisName,
                                    YangNode node, YangLeafRef leafRef)
            throws DataModelException {

        YangLeaf leaf = getLeaf(leftAxisName, (YangLeavesHolder) node);
        if (leaf != null) {
            return leaf;
        }
        YangLeafList leafList = getLeafList(leftAxisName,
                                            (YangLeavesHolder) node);
        if (leafList == null) {
            throw getDataModelExc(TGT_LEAF_ERR, leafRef);
        }
        return leafList;
    }

    /**
     * Returns the leaf by searching it in the node by the leaf name. Returns
     * null when the name doesn't match.
     *
     * @param name   leaf name
     * @param holder holder of leaf
     * @return YANG leaf
     */
    private static YangLeaf getLeaf(YangNodeIdentifier name,
                                    YangLeavesHolder holder) {

        List<YangLeaf> listOfLeaf = holder.getListOfLeaf();
        if (listOfLeaf != null) {
            for (YangLeaf yangLeaf : listOfLeaf) {
                if (yangLeaf.getName().equals(name.getName())) {
                    return yangLeaf;
                }
            }
        }
        return null;
    }

    /**
     * Returns the leaf-list by searching it in the node by the leaf-list name.
     * Returns null when the name doesn't match.
     *
     * @param name   leaf-list name
     * @param holder holder of leaf-list
     * @return YANG leaf-list
     */
    private static YangLeafList getLeafList(YangNodeIdentifier name,
                                            YangLeavesHolder holder) {

        List<YangLeafList> listOfLeafList = holder.getListOfLeafList();
        if (listOfLeafList != null) {
            for (YangLeafList yangLeafList : listOfLeafList) {
                if (yangLeafList.getName().equals(name.getName())) {
                    return yangLeafList;
                }
            }
        }
        return null;
    }

    /**
     * Returns the root node from which the path with the atomic node names
     * has to be traversed through. With the ancestor count the nodes are
     * moved upward.
     *
     * @param count   ancestor count
     * @param node    current leaf-ref parent
     * @param leafRef YANG leaf-ref
     * @return root node from ancestor count
     * @throws DataModelException if there is a data model error
     */
    private static YangNode getRootNode(int count, YangNode node,
                                        YangLeafRef leafRef)
            throws DataModelException {

        YangNode curParent = node;
        int curCount = 0;
        while (curCount < count) {
            curCount = curCount + 1;
            if (curCount != 1) {
                if (curParent.getParent() == null) {
                    throw getDataModelExc(INVALID_TREE, leafRef);
                }
                curParent = curParent.getParent();
            }
            curParent = skipInvalidDataNodes(curParent, leafRef);
            if (curParent instanceof YangAugment) {
                YangAugment augment = (YangAugment) curParent;
                curParent = augment.getAugmentedNode();
                curCount = curCount + 1;
            }
        }
        return curParent;
    }

    /**
     * Returns the last node by traversing through the atomic node id by
     * leaving the last target leaf/leaf-list.
     *
     * @param curNode current node
     * @param relPath relative path
     * @param leafRef YANG leaf-ref
     * @return last YANG node
     * @throws DataModelException if there is a data model error
     */
    private static Object getLastNode(YangNode curNode,
                                      YangRelativePath relPath,
                                      YangLeafRef leafRef)
            throws DataModelException {

        YangNode node = curNode;
        List<YangAtomicPath> atomics = new ArrayList<>();
        atomics.addAll(relPath.getAtomicPathList());

        if (atomics.isEmpty()) {
            throw getDataModelExc(EMPTY_PATH_LIST_ERR, leafRef);
        }

        YangAtomicPath pathTgt = atomics.get(atomics.size() - 1);
        if (atomics.size() == 1) {
            return getTarget(pathTgt.getNodeIdentifier(), node, leafRef);
        }

        atomics.remove(atomics.size() - 1);
        for (YangAtomicPath atomicPath : atomics) {
            node = getNode(node.getChild(), atomicPath.getNodeIdentifier());
            if (node == null) {
                throw getDataModelExc(INVALID_PATH_PRE, leafRef);
            }
        }
        return getTarget(pathTgt.getNodeIdentifier(), node, leafRef);
    }

    /**
     * Returns the node from the parent node by matching it with the atomic
     * name. If no child node matches the name then it returns null.
     *
     * @param curNode    current node
     * @param identifier atomic name
     * @return node to be traversed
     */
    private static YangNode getNode(YangNode curNode,
                                    YangNodeIdentifier identifier) {
        YangNode node = curNode;
        while (node != null) {
            if (node.getName().equals(identifier.getName())) {
                return node;
            }
            node = node.getNextSibling();
        }
        return null;
    }

    /**
     * Sets the right axis node in the YANG path predicate after finding it
     * from the relative path.
     *
     * @param leafRef   YANG leaf-ref
     * @param predicate YANG path predicate
     * @throws DataModelException if there is a data model error
     */
    private static void setRightAxisNode(YangLeafRef leafRef,
                                         YangPathPredicate predicate)
            throws DataModelException {

        YangNode parentNode = leafRef.getParentNode();
        YangRelativePath relPath = predicate.getRelPath();
        int ancestor = relPath.getAncestorNodeCount();

        YangNode rootNode = getRootNode(ancestor, parentNode, leafRef);
        Object target = getLastNode(rootNode, relPath, leafRef);
        if (target == null) {
            throw getDataModelExc(INVALID_PATH_PRE, leafRef);
        }
        predicate.setRightAxisNode(target);
    }

    /**
     * Returns data model error messages for leaf-ref with the path.
     *
     * @param msg     error message
     * @param leafRef YANG leaf-ref
     * @return data model exception
     */
    private static DataModelException getDataModelExc(String msg,
                                                      YangLeafRef leafRef) {
        DataModelException exc = new DataModelException(
                msg + leafRef.getPath());
        exc.setCharPosition(leafRef.getCharPosition());
        exc.setLine(leafRef.getLineNumber());
        return exc;
    }
}
