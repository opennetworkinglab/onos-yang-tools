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

package org.onosproject.yang.compiler.datamodel.utils;

import org.onosproject.yang.compiler.datamodel.CollisionDetector;
import org.onosproject.yang.compiler.datamodel.DefaultYangNamespace;
import org.onosproject.yang.compiler.datamodel.SchemaDataNode;
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangBase;
import org.onosproject.yang.compiler.datamodel.YangCompilerAnnotation;
import org.onosproject.yang.compiler.datamodel.YangConfig;
import org.onosproject.yang.compiler.datamodel.YangDefault;
import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangDeviateAdd;
import org.onosproject.yang.compiler.datamodel.YangDeviateDelete;
import org.onosproject.yang.compiler.datamodel.YangDeviateReplace;
import org.onosproject.yang.compiler.datamodel.YangDeviation;
import org.onosproject.yang.compiler.datamodel.YangEntityToResolveInfo;
import org.onosproject.yang.compiler.datamodel.YangEntityToResolveInfoImpl;
import org.onosproject.yang.compiler.datamodel.YangEnumeration;
import org.onosproject.yang.compiler.datamodel.YangIdentityRef;
import org.onosproject.yang.compiler.datamodel.YangIfFeature;
import org.onosproject.yang.compiler.datamodel.YangImport;
import org.onosproject.yang.compiler.datamodel.YangInput;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangMandatory;
import org.onosproject.yang.compiler.datamodel.YangMaxElementHolder;
import org.onosproject.yang.compiler.datamodel.YangMinElementHolder;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangMust;
import org.onosproject.yang.compiler.datamodel.YangMustHolder;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNotification;
import org.onosproject.yang.compiler.datamodel.YangOutput;
import org.onosproject.yang.compiler.datamodel.YangReferenceResolver;
import org.onosproject.yang.compiler.datamodel.YangResolutionInfo;
import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangUnion;
import org.onosproject.yang.compiler.datamodel.YangUniqueHolder;
import org.onosproject.yang.compiler.datamodel.YangUnits;
import org.onosproject.yang.compiler.datamodel.YangUses;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.model.LeafType;
import org.onosproject.yang.model.SchemaId;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_AUGMENT;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_BASE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_COMPILER_ANNOTATION;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_DERIVED_DATA_TYPE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_DEVIATION;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_IDENTITYREF;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_IF_FEATURE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_LEAFREF;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_USES;
import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_AUGMENT_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DERIVED;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.EMPTY;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.ENUMERATION;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UNION;
import static org.onosproject.yang.model.LeafType.BIG_DECIMAL;
import static org.onosproject.yang.model.LeafType.BIG_INTEGER;
import static org.onosproject.yang.model.LeafType.BOOLEAN;
import static org.onosproject.yang.model.LeafType.BYTE;
import static org.onosproject.yang.model.LeafType.BYTE_ARRAY;
import static org.onosproject.yang.model.LeafType.INT;
import static org.onosproject.yang.model.LeafType.LONG;
import static org.onosproject.yang.model.LeafType.SHORT;
import static org.onosproject.yang.model.LeafType.STRING;

/**
 * Represents utilities for data model tree.
 */
public final class DataModelUtils {
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    private static final String SLASH = File.separator;
    public static final String FMT_NOT_EXIST =
            "Requested %s is not child in %s.";
    private static final String E_DATATYPE = "Data type not supported.";
    public static final String E_ID = "Schema id should not be null.";

    /**
     * Creates a new data model tree utility.
     */
    private DataModelUtils() {
    }

    /**
     * Detects the colliding identifier name in a given YANG node and its child.
     *
     * @param identifierName name for which collision detection is to be checked
     * @param dataType       type of YANG node asking for detecting collision
     * @param node           instance of calling node
     * @throws DataModelException a violation of data model rules
     */
    public static void detectCollidingChildUtil(String identifierName, YangConstructType dataType, YangNode node)
            throws DataModelException {
        if (dataType == YangConstructType.USES_DATA || dataType == YangConstructType.GROUPING_DATA) {
            detectCollidingForUsesGrouping(identifierName, dataType, node);
        } else {
            if (node instanceof YangLeavesHolder) {
                YangLeavesHolder leavesHolder = (YangLeavesHolder) node;
                detectCollidingLeaf(leavesHolder.getListOfLeaf(), identifierName);
                detectCollidingLeafList(leavesHolder.getListOfLeafList(), identifierName);
            }
            node = node.getChild();
            while (node != null) {
                Parsable parsable = (Parsable) node;
                if (node instanceof CollisionDetector
                        && parsable.getYangConstructType() != YangConstructType.USES_DATA
                        && parsable.getYangConstructType() != YangConstructType.GROUPING_DATA) {
                    ((CollisionDetector) node).detectSelfCollision(identifierName, dataType);
                }
                node = node.getNextSibling();
            }
        }
    }

    /**
     * Detects colliding of uses and grouping only with uses and grouping respectively.
     *
     * @param identifierName name for which collision detection is to be checked
     * @param dataType       type of YANG node asking for detecting collision
     * @param node           node instance of calling node
     * @throws DataModelException a violation of data model rules
     */
    private static void detectCollidingForUsesGrouping(String identifierName, YangConstructType dataType, YangNode node)
            throws DataModelException {

        node = node.getChild();
        while (node != null) {
            Parsable parsable = (Parsable) node;
            if (node instanceof CollisionDetector
                    && parsable.getYangConstructType() == dataType) {
                ((CollisionDetector) node).detectSelfCollision(identifierName, dataType);
            }
            node = node.getNextSibling();
        }
    }

    /**
     * Detects the colliding identifier name in a given leaf node.
     *
     * @param listOfLeaf     List of leaves to detect collision
     * @param identifierName name for which collision detection is to be checked
     * @throws DataModelException a violation of data model rules
     */
    private static void detectCollidingLeaf(List<YangLeaf> listOfLeaf, String identifierName)
            throws DataModelException {

        if (listOfLeaf == null) {
            return;
        }
        for (YangLeaf leaf : listOfLeaf) {
            if (leaf.getName().equals(identifierName)) {
                throw new DataModelException("YANG file error: Duplicate input identifier detected, same as leaf \"" +
                                                     leaf.getName() + " in " +
                                                     leaf.getLineNumber() + " at " +
                                                     leaf.getCharPosition() +
                                                     " in " + leaf.getFileName() + "\"");
            }
        }
    }

    /**
     * Detects the colliding identifier name in a given leaf-list node.
     *
     * @param listOfLeafList list of leaf-lists to detect collision
     * @param identifierName name for which collision detection is to be checked
     * @throws DataModelException a violation of data model rules
     */
    private static void detectCollidingLeafList(List<YangLeafList> listOfLeafList, String identifierName)
            throws DataModelException {

        if (listOfLeafList == null) {
            return;
        }
        for (YangLeafList leafList : listOfLeafList) {
            if (leafList.getName().equals(identifierName)) {
                throw new DataModelException("YANG file error: Duplicate input identifier detected, same as leaf " +
                                                     "list \"" + leafList.getName() + " in " +
                                                     leafList.getLineNumber() + " at " +
                                                     leafList.getCharPosition() +
                                                     " in " + leafList.getFileName() + "\"");
            }
        }
    }

    /**
     * Add a resolution information.
     *
     * @param resolutionInfo information about the YANG construct which has to be resolved
     * @throws DataModelException a violation of data model rules
     */
    public static void addResolutionInfo(YangResolutionInfo resolutionInfo)
            throws DataModelException {

        /* get the module node to add maintain the list of nested reference */
        YangNode curNode = resolutionInfo.getEntityToResolveInfo()
                .getHolderOfEntityToResolve();
        while (!(curNode instanceof YangReferenceResolver)) {
            curNode = curNode.getParent();
            if (curNode == null) {
                throw new DataModelException("Internal datamodel error: Datamodel tree is not correct");
            }
        }
        YangReferenceResolver resolutionNode = (YangReferenceResolver) curNode;
        YangEntityToResolveInfo entityToResolveInfo = resolutionInfo.getEntityToResolveInfo();
        if (entityToResolveInfo.getEntityToResolve()
                instanceof YangType) {
            resolutionNode.addToResolutionList(resolutionInfo,
                                               YANG_DERIVED_DATA_TYPE);
        } else if (entityToResolveInfo.getEntityToResolve()
                instanceof YangUses) {
            resolutionNode.addToResolutionList(resolutionInfo, YANG_USES);
        } else if (entityToResolveInfo.getEntityToResolve()
                instanceof YangAugment) {
            resolutionNode.addToResolutionList(resolutionInfo, YANG_AUGMENT);
        } else if (entityToResolveInfo.getEntityToResolve()
                instanceof YangIfFeature) {
            resolutionNode.addToResolutionList(resolutionInfo, YANG_IF_FEATURE);
        } else if (entityToResolveInfo.getEntityToResolve()
                instanceof YangLeafRef) {
            resolutionNode.addToResolutionList(resolutionInfo, YANG_LEAFREF);
        } else if (entityToResolveInfo.getEntityToResolve()
                instanceof YangBase) {
            resolutionNode.addToResolutionList(resolutionInfo, YANG_BASE);
        } else if (entityToResolveInfo.getEntityToResolve()
                instanceof YangIdentityRef) {
            resolutionNode.addToResolutionList(resolutionInfo, YANG_IDENTITYREF);
        } else if (entityToResolveInfo.getEntityToResolve()
                instanceof YangCompilerAnnotation) {
            resolutionNode.addToResolutionList(resolutionInfo,
                                               YANG_COMPILER_ANNOTATION);
        } else if (entityToResolveInfo.getEntityToResolve()
                instanceof YangDeviation) {
            resolutionNode.addToResolutionList(resolutionInfo, YANG_DEVIATION);
        }
    }

    /**
     * Resolve linking for a resolution list.
     *
     * @param resolutionList    resolution list for which linking to be done
     * @param dataModelRootNode module/sub-module node
     * @throws DataModelException a violation of data model rules
     */
    public static void resolveLinkingForResolutionList(List<YangResolutionInfo> resolutionList,
                                                       YangReferenceResolver dataModelRootNode)
            throws DataModelException {

        for (YangResolutionInfo resolutionInfo : resolutionList) {
            resolutionInfo.resolveLinkingForResolutionInfo(dataModelRootNode);
        }
    }

    /**
     * Links type/uses referring to typedef/uses of inter YANG file.
     *
     * @param resolutionList    resolution list for which linking to be done
     * @param dataModelRootNode module/sub-module node
     * @throws DataModelException a violation of data model rules
     */
    public static void linkInterFileReferences(List<YangResolutionInfo> resolutionList,
                                               YangReferenceResolver dataModelRootNode)
            throws DataModelException {
        /*
         * Run through the resolution list, find type/uses referring to inter
         * file typedef/grouping, ask for linking.
         */
        if (resolutionList != null) {
            for (YangResolutionInfo resolutionInfo : resolutionList) {
                resolutionInfo.linkInterFile(dataModelRootNode);
            }
        }
    }

    /**
     * Checks if there is any rpc defined in the module or sub-module.
     *
     * @param rootNode root node of the data model
     * @return status of rpc's existence
     */
    public static boolean isRpcChildNodePresent(YangNode rootNode) {
        YangNode childNode = rootNode.getChild();
        while (childNode != null) {
            if (childNode instanceof YangRpc) {
                return true;
            }
            childNode = childNode.getNextSibling();
        }
        return false;
    }

    /**
     * Checks if there is any rpc/notification defined in the module or
     * sub-module.
     *
     * @param rootNode root node of the data model
     * @return status of rpc/notification existence
     */
    public static boolean isRpcNotificationPresent(YangNode rootNode) {
        YangNode childNode = rootNode.getChild();
        while (childNode != null) {
            if (childNode instanceof YangRpc ||
                    childNode instanceof YangNotification) {
                return true;
            }
            childNode = childNode.getNextSibling();
        }
        return false;
    }

    /**
     * Returns referred node in a given set.
     *
     * @param yangNodeSet YANG node set
     * @param refNodeName name of the node which is referred
     * @return referred node's reference
     */
    public static YangNode findReferredNode(Set<YangNode> yangNodeSet, String refNodeName) {
        /*
         * Run through the YANG files to see which YANG file matches the
         * referred node name.
         */
        for (YangNode yangNode : yangNodeSet) {
            if (yangNode.getName().equals(refNodeName)) {
                return yangNode;
            }
        }
        return null;
    }

    /**
     * Returns the contained data model parent node.
     *
     * @param currentNode current node which parent contained node is required
     * @return parent node in which the current node is an attribute
     */
    public static YangNode getParentNodeInGenCode(YangNode currentNode) {

        /*
         * TODO: recursive parent lookup to support choice/augment/uses. TODO:
         * need to check if this needs to be updated for
         * choice/case/augment/grouping
         */
        return currentNode.getParent();
    }

    /**
     * Returns de-serializes YANG data-model nodes.
     *
     * @param serializedFileInfo serialized File Info
     * @return de-serializes YANG data-model nodes
     * @throws IOException when fails do IO operations
     */
    public static Set<YangNode> deSerializeDataModel(String serializedFileInfo)
            throws IOException {

        Set<YangNode> nodes;
        try {
            FileInputStream fileInputStream = new FileInputStream(serializedFileInfo);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            nodes = (Set<YangNode>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new IOException(serializedFileInfo + " not found.");
        }
        return nodes;
    }

    /**
     * Adds the list of leaf present under a node to resolution list, after
     * cloning. Under the cloned node, with cloned leaf, attributes are set
     * and added to resolution list.
     *
     * @param clonedNode  holder node
     * @param yangUses    YANG uses
     * @param isDeviation flag to identify cloning is for deviation
     * @throws CloneNotSupportedException clone not supported error
     * @throws DataModelException         data model error
     */
    public static void cloneListOfLeaf(YangLeavesHolder clonedNode,
                                       YangUses yangUses,
                                       boolean isDeviation)
            throws CloneNotSupportedException, DataModelException {

        List<YangLeaf> leaves = clonedNode.getListOfLeaf();
        if (nonEmpty(leaves)) {
            List<YangLeaf> clonedLeaves = new LinkedList<>();
            for (YangLeaf leaf : leaves) {
                YangLeaf clonedLeaf;
                if (!isDeviation) {
                    clonedLeaf = leaf.clone();
                    addUnresolvedType(yangUses, clonedLeaf,
                                      (YangNode) clonedNode);
                } else {
                    clonedLeaf = leaf.cloneForDeviation();
                }
                clonedLeaf.setReferredLeaf(leaf);
                clonedLeaf.setContainedIn(clonedNode);
                clonedLeaves.add(clonedLeaf);
            }
            clonedNode.setListOfLeaf(clonedLeaves);
        }
    }

    /**
     * Adds all the unresolved type under leaf/leaf-list to the resolution
     * list, after cloning. This makes the resolution to happen after cloning
     * of the grouping. Adds resolution with cloned node holder under which
     * cloned type is present.
     *
     * @param yangUses   YANG uses
     * @param clonedObj  cloned type object
     * @param clonedNode holder node
     * @throws DataModelException data model error
     */
    public static void addUnresolvedType(
            YangUses yangUses, Object clonedObj,
            YangNode clonedNode) throws DataModelException {

        List<YangEntityToResolveInfoImpl> infoList;
        if (yangUses != null && yangUses.getCurrentGroupingDepth() == 0) {
            infoList = getTypesToBeResolved(clonedObj, clonedNode, yangUses);
            if (nonEmpty(infoList)) {
                yangUses.addEntityToResolve(infoList);
            }
        }
    }

    /**
     * Returns true if collection object is non-null and non-empty; false
     * otherwise.
     *
     * @param c collection object
     * @return true if object is non-null and non-empty; false otherwise
     */
    public static boolean nonEmpty(Collection<?> c) {
        return c != null && !c.isEmpty();
    }

    /**
     * Adds the list of leaf-list present under a node to resolution list,
     * after cloning. Under the cloned node, with cloned leaf-list,
     * attributes are set and added to resolution list.
     *
     * @param clonedNode  cloned holder
     * @param yangUses    YANG uses
     * @param isDeviation flag to identify cloning is for deviation
     * @throws CloneNotSupportedException clone not supported error
     * @throws DataModelException         data model error
     */
    public static void cloneListOfLeafList(
            YangLeavesHolder clonedNode, YangUses yangUses, boolean isDeviation)
            throws CloneNotSupportedException, DataModelException {

        List<YangLeafList> listOfLeafList = clonedNode.getListOfLeafList();
        if (nonEmpty(listOfLeafList)) {
            List<YangLeafList> clonedList = new LinkedList<>();
            for (YangLeafList leafList : listOfLeafList) {
                YangLeafList clonedLeafList;
                if (!isDeviation) {
                    clonedLeafList = leafList.clone();
                    addUnresolvedType(yangUses, clonedLeafList,
                                      (YangNode) clonedNode);
                } else {
                    clonedLeafList = leafList.cloneForDeviation();
                }
                clonedLeafList.setReferredSchemaLeafList(leafList);
                clonedLeafList.setContainedIn(clonedNode);
                clonedList.add(clonedLeafList);
            }
            clonedNode.setListOfLeafList(clonedList);
        }
    }

    /**
     * Returns types that has to be resolved for a single leaf/leaf-list.
     * Identifies the object to be leaf/leaf-list and assigns respective
     * parameters to resolve the types under leaf/leaf-list.
     *
     * @param clonedObj  leaf/leaf-list object
     * @param holderNode holder node
     * @param yangUses   YANG uses
     * @return list of resolvable entities in a leaf/leaf-list
     * @throws DataModelException data model error
     */
    private static List<YangEntityToResolveInfoImpl> getTypesToBeResolved(
            Object clonedObj, YangNode holderNode,
            YangUses yangUses) throws DataModelException {

        YangType type;
        if (clonedObj instanceof YangLeaf) {
            YangLeaf clonedLeaf = (YangLeaf) clonedObj;
            type = clonedLeaf.getDataType();
            return getUnresolvedTypeList(type.getDataType(), type, holderNode,
                                         yangUses, true);
        }
        YangLeafList clonedLeafList = (YangLeafList) clonedObj;
        type = clonedLeafList.getDataType();
        return getUnresolvedTypeList(type.getDataType(), type, holderNode,
                                     yangUses, false);
    }

    /**
     * Returns list of resolvable entities from the type of leaf/leaf-list.
     * If the type is leaf-ref, derived or union with type resolution
     * required, it has to be resolved from the place where it is cloned. So,
     * the resolution list added with these entities. When a type require no
     * resolution then null is returned, so it will never be added to
     * resolution list.
     *
     * @param dataTypes data type of type
     * @param type      type of leaf/leaf-list
     * @param holder    holder node of type
     * @param yangUses  YANG uses
     * @param isLeaf    leaf or leaf-list
     * @return list of resolvable entities for a leaf/leaf-list.
     * @throws DataModelException data model error
     */
    private static List<YangEntityToResolveInfoImpl> getUnresolvedTypeList(
            YangDataTypes dataTypes, YangType type, YangNode holder,
            YangUses yangUses, boolean isLeaf) throws DataModelException {

        List<YangEntityToResolveInfoImpl> infoList = new ArrayList<>();
        YangEntityToResolveInfoImpl entity = null;
        List<YangEntityToResolveInfoImpl> entityList = null;

        switch (dataTypes) {
            case LEAFREF:
                entity = getLeafRefResolvableEntity(type, yangUses, holder);
                break;

            case DERIVED:
                entity = getDerivedResolvableEntity(type, holder, isLeaf);
                break;

            case UNION:
                entityList = getUnionResolvableEntity(type, isLeaf);
                break;

            default:
                return null;
        }
        infoList.add(entity);
        if (nonEmpty(entityList)) {
            infoList.addAll(entityList);
        }
        return infoList;
    }

    /**
     * Returns resolvable entity when the type is leaf-ref. It changes the
     * prefixes from grouping to uses, then changes the parent node to the
     * cloned node, sets needed information to entity such as line number,
     * position number and holder.
     *
     * @param type     YANG type of leaf-ref
     * @param yangUses YANG uses
     * @param holder   cloned holder
     * @return entity to resolve for leaf-ref
     * @throws DataModelException data model error
     */
    private static YangEntityToResolveInfoImpl getLeafRefResolvableEntity(
            YangType type, YangUses yangUses, YangNode holder)
            throws DataModelException {

        YangEntityToResolveInfoImpl<YangLeafRef> leafRefInfo =
                new YangEntityToResolveInfoImpl<>();
        YangLeafRef leafRef = (YangLeafRef) type.getDataTypeExtendedInfo();

        // Conversion of prefixes in absolute path while cloning them.
        convertThePrefixesDuringChange(leafRef, yangUses);
        leafRef.setParentNode(holder);
        leafRefInfo.setEntityToResolve(leafRef);

        return setInformationInEntity(
                leafRefInfo, holder, leafRef.getCharPosition(),
                leafRef.getLineNumber());
    }

    /**
     * Returns resolvable entity when the type is derived. It sets needed
     * information to entity such as line number,position number and holder.
     * Returns null when identity is for inter grouping.
     *
     * @param type   derived YANG type
     * @param holder holder node
     * @param isLeaf leaf or leaf-list
     * @return entity to resolve for derived type
     */
    private static YangEntityToResolveInfoImpl getDerivedResolvableEntity(
            YangType<?> type, YangNode holder, boolean isLeaf) {

        YangEntityToResolveInfoImpl<YangType<?>> derivedInfo =
                new YangEntityToResolveInfoImpl<>();
        if (type.isTypeForInterFileGroupingResolution()) {
            return null;
        }
        if (!isLeaf && type.isTypeNotResolvedTillRootNode()) {
            return null;
        }

        derivedInfo.setEntityToResolve(type);
        return setInformationInEntity(
                derivedInfo, holder, type.getCharPosition(),
                type.getLineNumber());
    }

    /**
     * Sets the information needed for adding the entity into resolution
     * list, such as line number, position number and cloned holder node.
     *
     * @param entity  resolvable entity
     * @param holder  cloned holder node
     * @param charPos character position
     * @param lineNum line number
     * @return resolvable entity after setting info
     */
    private static YangEntityToResolveInfoImpl<?> setInformationInEntity(
            YangEntityToResolveInfoImpl<?> entity, YangNode holder,
            int charPos, int lineNum) {

        entity.setHolderOfEntityToResolve(holder);
        entity.setCharPosition(charPos);
        entity.setLineNumber(lineNum);
        return entity;
    }

    /**
     * Returns resolvable entity under union. When types under union have
     * identity-ref, derived and union, the function call is done recursively
     * to get resolvable entity and adds it to list.
     *
     * @param type   union YANG type
     * @param isLeaf leaf or leaf-list
     * @return resolvable entity list after setting info
     * @throws DataModelException data model error
     */
    private static List<YangEntityToResolveInfoImpl> getUnionResolvableEntity(
            YangType type, boolean isLeaf) throws DataModelException {

        YangUnion union = (YangUnion) type.getDataTypeExtendedInfo();
        List<YangType<?>> typeList = union.getTypeList();
        List<YangEntityToResolveInfoImpl> unionList = new ArrayList<>();
        List<YangEntityToResolveInfoImpl> entity;

        for (YangType unionType : typeList) {
            entity = getUnresolvedTypeList(unionType.getDataType(),
                                           unionType, union, null, isLeaf);
            if (nonEmpty(entity)) {
                unionList.addAll(entity);
            }
        }
        return unionList;
    }

    /**
     * Converts the prefixes in all the nodes of the leafref with respect to the uses node.
     *
     * @param leafrefForCloning leafref that is to be cloned
     * @param yangUses          instance of YANG uses where cloning is done
     * @throws DataModelException data model error
     */
    private static void convertThePrefixesDuringChange(YangLeafRef leafrefForCloning, YangUses yangUses)
            throws DataModelException {
        List<YangAtomicPath> atomicPathList = leafrefForCloning.getAtomicPath();
        if (atomicPathList != null && !atomicPathList.isEmpty()) {
            Iterator<YangAtomicPath> atomicPathIterator = atomicPathList.listIterator();
            while (atomicPathIterator.hasNext()) {
                YangAtomicPath atomicPath = atomicPathIterator.next();
                Map<String, String> prefixesAndItsImportNameNode = leafrefForCloning.getPrefixAndNode();
                String prefixInPath = atomicPath.getNodeIdentifier().getPrefix();
                String importedNodeName = prefixesAndItsImportNameNode.get(prefixInPath);
                assignCurrentLeafedWithNewPrefixes(importedNodeName, atomicPath, yangUses);
            }
        }
    }

    /**
     * Assigns leafref with new prefixes while cloning.
     *
     * @param importedNodeName imported node name from grouping
     * @param atomicPath       atomic path in leafref
     * @param node             instance of YANG uses where cloning is done
     * @throws DataModelException data model error
     */
    private static void assignCurrentLeafedWithNewPrefixes(String importedNodeName, YangAtomicPath atomicPath,
                                                           YangNode node)
            throws DataModelException {
        while (!(node instanceof YangReferenceResolver)) {
            node = node.getParent();
            if (node == null) {
                throw new DataModelException("Internal datamodel error: Datamodel tree is not correct");
            }
        }
        if (node instanceof YangModule) {
            List<YangImport> importInUsesList = ((YangModule) node).getImportList();
            if (importInUsesList != null && !importInUsesList.isEmpty()) {
                Iterator<YangImport> importInUsesListIterator = importInUsesList.listIterator();
                while (importInUsesListIterator.hasNext()) {
                    YangImport importInUsesNode = importInUsesListIterator.next();
                    if (importInUsesNode.getModuleName().equals(importedNodeName)) {
                        atomicPath.getNodeIdentifier().setPrefix(importInUsesNode.getPrefixId());
                    }
                }
            }
        }
    }

    /**
     * Clones the union or enum leaves. If there is any cloned leaves whose type is union/enum then the corresponding
     * type info needs to be updated to the cloned new type node.
     *
     * @param leavesHolder cloned leaves holder, for whom the leaves reference needs to be updated
     * @throws DataModelException when fails to do data model operations
     */
    public static void updateClonedLeavesUnionEnumRef(YangLeavesHolder leavesHolder)
            throws DataModelException {
        List<YangLeaf> currentListOfLeaves = leavesHolder.getListOfLeaf();
        if (currentListOfLeaves != null) {
            for (YangLeaf leaf : currentListOfLeaves) {
                if (leaf.getDataType().getDataType() == ENUMERATION
                        || leaf.getDataType().getDataType() == UNION) {
                    try {
                        YangType<?> clonedType = leaf.getDataType().clone();
                        updateClonedTypeRef(clonedType, leavesHolder);
                        leaf.setDataType(clonedType);
                    } catch (DataModelException e) {
                        throw e;
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                        throw new DataModelException("Could not clone Type node " +
                                                             leaf.getDataType().getDataTypeName() + " in " +
                                                             leaf.getDataType().getLineNumber() + " at " +
                                                             leaf.getDataType().getCharPosition() +
                                                             " in " + leaf.getDataType().getFileName() + "\"");
                    }
                }
            }
        }

        List<YangLeafList> currentListOfLeafList = leavesHolder.getListOfLeafList();
        if (currentListOfLeafList != null) {
            for (YangLeafList leafList : currentListOfLeafList) {
                if (leafList.getDataType().getDataType() == ENUMERATION
                        || leafList.getDataType().getDataType() == UNION) {
                    try {
                        YangType<?> clonedType = leafList.getDataType().clone();
                        updateClonedTypeRef(clonedType, leavesHolder);
                        leafList.setDataType(clonedType);
                    } catch (DataModelException e) {
                        throw e;
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                        throw new DataModelException("Could not clone Type node " +
                                                             leafList.getDataType().getDataTypeName() + " in " +
                                                             leafList.getDataType().getLineNumber() + " at " +
                                                             leafList.getDataType().getCharPosition() +
                                                             " in " + leafList.getDataType().getFileName() + "\"");
                    }
                }
            }
        }
    }

    /**
     * Updates the types extended info pointer to point to the cloned type node.
     *
     * @param dataType     data type, whose extended info needs to be pointed to the cloned type
     * @param leavesHolder the leaves holder having the cloned type
     */
    private static void updateClonedTypeRef(YangType dataType, YangLeavesHolder leavesHolder)
            throws DataModelException {
        if (!(leavesHolder instanceof YangNode)) {
            throw new DataModelException("Data model error: cloned leaves holder is not a node " +
                                                 " in " +
                                                 leavesHolder.getLineNumber() + " at " +
                                                 leavesHolder.getCharPosition() +
                                                 " in " + leavesHolder.getFileName() + "\"");
        }
        YangNode potentialTypeNode = ((YangNode) leavesHolder).getChild();
        while (potentialTypeNode != null) {
            String dataTypeName = null;
            if (dataType.getDataType() == ENUMERATION) {
                YangEnumeration enumNode = (YangEnumeration) dataType.getDataTypeExtendedInfo();
                dataTypeName = enumNode.getName();
            } else if (dataType.getDataType() == UNION) {
                YangUnion unionNode = (YangUnion) dataType.getDataTypeExtendedInfo();
                dataTypeName = unionNode.getName();
            }
            if (potentialTypeNode.getName().contentEquals(dataTypeName)) {
                dataType.setDataTypeExtendedInfo(potentialTypeNode);
                return;
            }
            potentialTypeNode = potentialTypeNode.getNextSibling();
        }

        throw new DataModelException("Data model error: cloned leaves type is not found " +
                                             dataType.getDataTypeName() + " in " +
                                             dataType.getLineNumber() + " at " +
                                             dataType.getCharPosition() +
                                             " in " + dataType.getFileName() + "\"");
    }

    /**
     * Parses jar file and returns list of serialized file names.
     *
     * @param jarFile   jar file to be parsed
     * @param directory directory where to search
     * @return list of serialized files
     * @throws IOException when fails to do IO operations
     */
    public static List<YangNode> parseJarFile(String jarFile, String directory)
            throws IOException {

        List<YangNode> nodes = new ArrayList<>();
        JarFile jar = new JarFile(jarFile);
        Enumeration<?> enumEntries = jar.entries();

        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();
            if (file.getName().endsWith(".ser")) {

                if (file.getName().contains(SLASH)) {
                    String[] strArray = file.getName().split(SLASH);
                    String tempPath = "";
                    for (int i = 0; i < strArray.length - 1; i++) {
                        tempPath = SLASH + tempPath + SLASH + strArray[i];
                    }
                    File dir = new File(directory + tempPath);
                    dir.mkdirs();
                }
                File serializedFile = new File(directory + SLASH + file.getName());
                if (file.isDirectory()) {
                    serializedFile.mkdirs();
                    continue;
                }
                InputStream inputStream = jar.getInputStream(file);

                FileOutputStream fileOutputStream = new FileOutputStream(serializedFile);
                while (inputStream.available() > 0) {
                    fileOutputStream.write(inputStream.read());
                }
                fileOutputStream.close();
                inputStream.close();
                nodes.addAll(deSerializeDataModel(serializedFile.toString()));
            }
        }
        jar.close();
        return nodes;
    }

    /**
     * Parses jar file and returns list of serialized file names.
     *
     * @param jarFile   jar file to be parsed
     * @param directory directory where to search
     * @return list of serialized files
     * @throws IOException when fails to do IO operations
     */
    public static File parseDepSchemaPath(String jarFile, String directory)
            throws IOException {
        JarFile jar = new JarFile(jarFile);
        Enumeration<?> enumEntries = jar.entries();
        File serializedFile = null;
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();
            if (file.getName().endsWith(".ser")) {

                if (file.getName().contains(SLASH)) {
                    String[] strArray = file.getName().split(SLASH);
                    String tempPath = "";
                    for (int i = 0; i < strArray.length - 1; i++) {
                        tempPath = SLASH + tempPath + SLASH + strArray[i];
                    }
                    File dir = new File(directory + tempPath);
                    dir.mkdirs();
                }
                serializedFile = new File(directory + SLASH + file.getName());
                if (file.isDirectory()) {
                    serializedFile.mkdirs();
                    continue;
                }
                InputStream inputStream = jar.getInputStream(file);

                FileOutputStream fileOutputStream = new FileOutputStream(serializedFile);
                while (inputStream.available() > 0) {
                    fileOutputStream.write(inputStream.read());
                }
                fileOutputStream.close();
                inputStream.close();
            }
        }
        jar.close();
        return serializedFile;
    }

    /**
     * Validates the requested data-type resolve type in empty or not.
     *
     * @param dataType the data type
     * @return true, for empty resolved data-type; false otherwise
     */
    public static boolean validateEmptyDataType(YangType dataType) {
        switch (dataType.getDataType()) {
            case DERIVED:
                return ((YangDerivedInfo) dataType.getDataTypeExtendedInfo())
                        .getEffectiveBuiltInType().equals(EMPTY);

            case LEAFREF:
                YangType type = ((YangLeafRef) dataType
                        .getDataTypeExtendedInfo())
                        .getEffectiveDataType();
                if (type.getDataType() == DERIVED) {
                    return ((YangDerivedInfo) type.getDataTypeExtendedInfo())
                            .getEffectiveBuiltInType().equals(EMPTY);
                }
                return ((YangLeafRef) dataType.getDataTypeExtendedInfo())
                        .getEffectiveDataType().getDataType().equals(EMPTY);

            case UNION:
                return ((YangUnion) dataType.getDataTypeExtendedInfo())
                        .getTypeList().contains(EMPTY);
            default:
                return dataType.getDataType().equals(EMPTY);
        }
    }

    /**
     * Validates whether multiple deviation statement's Xpath is referring
     * to same module.
     *
     * @param node YANG data model node
     * @throws DataModelException if deviations referring to multiple module
     */
    public static void validateMultipleDeviationStatement(
            YangReferenceResolver node) throws DataModelException {
        List<YangResolutionInfo> deviationList = node
                .getUnresolvedResolutionList(YANG_DEVIATION);
        String prefix = null;
        if (!deviationList.isEmpty()) {
            YangDeviation firstDeviation = (YangDeviation) deviationList.get(0)
                    .getEntityToResolveInfo().getEntityToResolve();
            YangAtomicPath atomic = firstDeviation.getTargetNode().get(0);
            prefix = atomic.getNodeIdentifier().getPrefix();
        }

        Iterator<YangResolutionInfo> deviationIterator = deviationList.iterator();
        while (deviationIterator.hasNext()) {
            YangDeviation deviation = (YangDeviation) deviationIterator.next()
                    .getEntityToResolveInfo().getEntityToResolve();
            List<YangAtomicPath> targetNode = deviation.getTargetNode();
            YangAtomicPath atomicPath = targetNode.get(0);
            if (!atomicPath.getNodeIdentifier().getPrefix().equals(prefix)) {
                throw new DataModelException("YANG FILE ERROR : Deviations " +
                                                     "of multiple module is" +
                                                     " currently not " +
                                                     "supported.");
            }
        }
    }

    /**
     * Removes node from data model tree.
     *
     * @param node YANG data model node
     */
    public static void deleteUnsupportedNodeFromTree(YangNode node) {
        // unlink from parent
        YangNode parentNode = node.getParent();
        if (parentNode.getChild().equals(node)) {
            parentNode.setChild(node.getNextSibling());
        }

        //unlink from siblings
        YangNode previousSibling = node.getPreviousSibling();
        YangNode nextSibling = node.getNextSibling();
        if (nextSibling != null && previousSibling != null) {
            previousSibling.setNextSibling(nextSibling);
            nextSibling.setPreviousSibling(previousSibling);
        } else if (nextSibling != null) {
            nextSibling.setPreviousSibling(null);
        } else if (previousSibling != null) {
            previousSibling.setNextSibling(null);
        }
        node.setParent(null);
        node.setPreviousSibling(null);
        node.setNextSibling(null);
        node.setChild(null);
    }

    /**
     * Removes leaf/leaf-list from data model tree.
     *
     * @param node     YANG data model node
     * @param leafName name of leaf to be removed
     */
    public static void deleteUnsupportedLeafOrLeafList(YangLeavesHolder node,
                                                       String leafName) {
        List<YangLeaf> leaves = node.getListOfLeaf();
        if (leaves != null && !leaves.isEmpty()) {
            for (YangLeaf leaf : leaves) {
                if (leaf.getName().equals(leafName)) {
                    node.removeLeaf(leaf);
                    return;
                }
            }
        }

        List<YangLeafList> leafList = node.getListOfLeafList();
        if (leafList != null && !leafList.isEmpty()) {
            for (YangLeafList leaf : leafList) {
                if (leaf.getName().equals(leafName)) {
                    node.removeLeafList(leaf);
                    return;
                }
            }
        }
    }

    /**
     * Updates the target data model with deviate delete sub statements.
     *
     * @param targetNode    target node of deviation
     * @param deviateDelete YANG deviate delete data model node
     * @throws DataModelException if deviations referring to multiple module
     */
    public static void updateDeviateDeleteToTargetNode(YangSchemaNode targetNode,
                                                       YangDeviateDelete deviateDelete)
            throws DataModelException {

        // delete must statement
        if (targetNode instanceof YangMustHolder
                && !deviateDelete.getListOfMust().isEmpty()) {
            deviateDelete.setListOfMust(new LinkedList<>());
        }

        // delete unique statement
        if (targetNode instanceof YangUniqueHolder
                && !deviateDelete.getUniqueList().isEmpty()) {
            deviateDelete.setUniqueList(new LinkedList<>());
        }

        // delete units statement
        if (targetNode instanceof YangUnits) {
            ((YangUnits) targetNode).setUnits(null);
        }

        // delete default statement
        if (targetNode instanceof YangDefault) {
            ((YangDefault) targetNode)
                    .setDefaultValueInString(null);
        }
    }

    /**
     * Updates the target data model with deviate add sub statements.
     *
     * @param targetNode target node of deviation
     * @param deviateAdd YANG deviate add data model node
     * @throws DataModelException if deviations referring to multiple module
     */
    public static void updateDeviateAddToTargetNode(YangSchemaNode targetNode,
                                                    YangDeviateAdd deviateAdd)
            throws DataModelException {
        // update must statement
        if (targetNode instanceof YangMustHolder
                && !deviateAdd.getListOfMust().isEmpty()) {
            Iterator<YangMust> mustList = deviateAdd.getListOfMust().listIterator();
            while (mustList.hasNext()) {
                ((YangMustHolder) targetNode).addMust(mustList.next());
            }
        }

        // update unique statement
        if (targetNode instanceof YangUniqueHolder
                && !deviateAdd.getUniqueList().isEmpty()) {
            Iterator<String> uniqueList = deviateAdd.getUniqueList()
                    .listIterator();
            while (uniqueList.hasNext()) {
                ((YangUniqueHolder) targetNode).addUnique(uniqueList.next());
            }
        }

        // update config statement
        if (targetNode instanceof YangConfig) {
            ((YangConfig) targetNode).setConfig(deviateAdd.isConfig());
        }

        // update units statement
        if (targetNode instanceof YangUnits) {
            ((YangUnits) targetNode).setUnits(deviateAdd.getUnits());
        }

        // update default statement
        if (targetNode instanceof YangDefault) {
            ((YangDefault) targetNode)
                    .setDefaultValueInString(deviateAdd.getDefaultValueInString());
        }

        // update mandatory statement
        if (targetNode instanceof YangMandatory) {
            ((YangMandatory) targetNode).setMandatory(deviateAdd.isMandatory());
        }

        // update minelement statement
        if (targetNode instanceof YangMinElementHolder) {
            ((YangMinElementHolder) targetNode)
                    .setMinElements(deviateAdd.getMinElements());
        }

        // update max-element statement
        if (targetNode instanceof YangMaxElementHolder) {
            ((YangMaxElementHolder) targetNode)
                    .setMaxElements(deviateAdd.getMaxElements());
        }
    }

    /**
     * Replaces the substatements of deviate replace to target node.
     *
     * @param targetNode     target node of deviation
     * @param deviateReplace YANG deviate replace data model node
     */
    public static void updateDeviateReplaceToTargetNode(YangSchemaNode targetNode,
                                                        YangDeviateReplace deviateReplace) {

        if (targetNode instanceof YangLeaf
                && deviateReplace.getDataType() != null) {
            ((YangLeaf) targetNode).setDataType(deviateReplace.getDataType());
        }

        if (targetNode instanceof YangLeafList
                && deviateReplace.getDataType() != null) {
            ((YangLeafList) targetNode).setDataType(deviateReplace
                                                            .getDataType());
        }

        // update config statement
        if (targetNode instanceof YangConfig) {
            ((YangConfig) targetNode).setConfig(deviateReplace.isConfig());
        }

        // update units statement
        if (targetNode instanceof YangUnits) {
            ((YangUnits) targetNode).setUnits(deviateReplace.getUnits());
        }

        // update default statement
        if (targetNode instanceof YangDefault) {
            ((YangDefault) targetNode)
                    .setDefaultValueInString(deviateReplace.getDefaultValueInString());
        }

        // update mandatory statement
        if (targetNode instanceof YangMandatory) {
            ((YangMandatory) targetNode).setMandatory(deviateReplace.isMandatory());
        }

        // update minelement statement
        if (targetNode instanceof YangMinElementHolder) {
            ((YangMinElementHolder) targetNode)
                    .setMinElements(deviateReplace.getMinElements());
        }

        // update max-element statement
        if (targetNode instanceof YangMaxElementHolder) {
            ((YangMaxElementHolder) targetNode)
                    .setMaxElements(deviateReplace.getMaxElements());
        }
    }

    /**
     * Searches for leaf/leaf-list in given leaf holder node.
     *
     * @param target leaf holder
     * @param name   leaf/leaf-list name
     * @return leaf/leaf-list node
     */
    public static YangSchemaNode findLeafNode(YangLeavesHolder target,
                                              String name) {
        List<YangLeaf> leaves = target.getListOfLeaf();
        if (leaves != null && !leaves.isEmpty()) {
            for (YangLeaf leaf : leaves) {
                if (leaf.getName().equals(name)) {
                    return leaf;
                }
            }
        }

        List<YangLeafList> listOfleafList = target.getListOfLeafList();
        if (listOfleafList != null && !listOfleafList.isEmpty()) {
            for (YangLeafList leafList : listOfleafList) {
                if (leafList.getName().equals(name)) {
                    return leafList;
                }
            }
        }
        return null;
    }

    /**
     * Searches for input in given RPC node.
     *
     * @param rpc YANG RPC node
     * @return input node
     */
    public static YangNode findRpcInput(YangNode rpc) {
        YangNode child = rpc.getChild();
        while (child != null) {
            if (!(child instanceof YangInput)) {
                child = child.getNextSibling();
                continue;
            }
            return child;
        }
        return null;
    }

    /**
     * Searches for output in given RPC node.
     *
     * @param rpc YANG RPC node
     * @return output node
     */
    public static YangNode findRpcOutput(YangNode rpc) {
        YangNode child = rpc.getChild();
        while (child != null) {
            if (!(child instanceof YangOutput)) {
                child = child.getNextSibling();
                continue;
            }
            return child;
        }
        return null;
    }

    /**
     * Returns the parent data node schema context for given yang node.
     *
     * @param node yang node
     * @return yang node
     */
    public static YangNode getParentSchemaContext(YangNode node) {
        while (!(node instanceof SchemaDataNode) && node != null) {
            if (node.getYangSchemaNodeType() == YANG_AUGMENT_NODE) {
                node = ((YangAugment) node).getAugmentedNode();
                continue;
            }
            node = node.getParent();
        }
        return node;
    }

    /**
     * Returns the yang schema node identifier from provided schema id.
     *
     * @param schemaId schema id
     * @param ns       namespace of parent node
     * @return yang schema node identifier
     */
    public static YangSchemaNodeIdentifier getNodeIdFromSchemaId(
            SchemaId schemaId, String ns) {
        String namespace = schemaId.namespace();

        if (namespace == null) {
            namespace = ns;
        }
        DefaultYangNamespace nameSpace = new DefaultYangNamespace(namespace);
        YangSchemaNodeIdentifier id = new YangSchemaNodeIdentifier();
        id.setName(schemaId.name());
        id.setNameSpace(nameSpace);
        return id;
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
     * Returns the yang leaf type for corresponding supplied data type.
     *
     * @param type     YANG type
     * @param dataType YANG data type
     * @return leaf type
     */
    public static LeafType getLeafTypeByDataType(YangType type,
                                                 YangDataTypes dataType) {

        switch (dataType) {
            case BITS:
            case ENUMERATION:
            case IDENTITYREF:
            case STRING:
                return STRING;
            case BOOLEAN:
            case EMPTY:
                return BOOLEAN;
            case DECIMAL64:
                return BIG_DECIMAL;
            case INT8:
                return BYTE;
            case INT16:
            case UINT8:
                return SHORT;
            case INT32:
            case UINT16:
                return INT;
            case INT64:
            case UINT32:
                return LONG;
            case UINT64:
                return BIG_INTEGER;
            case BINARY:
                return BYTE_ARRAY;
            case DERIVED:
                return getLeafTypeByDataType(type, ((YangDerivedInfo) type
                        .getDataTypeExtendedInfo()).getEffectiveBuiltInType());
            case LEAFREF:
                return getLeafTypeByDataType(type, ((YangLeafRef) type
                        .getDataTypeExtendedInfo()).getEffectiveDataType()
                        .getDataType());
            case INSTANCE_IDENTIFIER:
                return STRING;
            case UNION:
                return LeafType.UNION;

            default:
                throw new IllegalArgumentException(E_DATATYPE);
        }
    }
}
