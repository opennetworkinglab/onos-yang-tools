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

package org.onosproject.yangutils.datamodel.utils;

import org.onosproject.yangutils.datamodel.CollisionDetector;
import org.onosproject.yangutils.datamodel.ResolvableType;
import org.onosproject.yangutils.datamodel.YangAtomicPath;
import org.onosproject.yangutils.datamodel.YangAugment;
import org.onosproject.yangutils.datamodel.YangBase;
import org.onosproject.yangutils.datamodel.YangCompilerAnnotation;
import org.onosproject.yangutils.datamodel.YangEntityToResolveInfoImpl;
import org.onosproject.yangutils.datamodel.YangEnumeration;
import org.onosproject.yangutils.datamodel.YangIdentityRef;
import org.onosproject.yangutils.datamodel.YangIfFeature;
import org.onosproject.yangutils.datamodel.YangImport;
import org.onosproject.yangutils.datamodel.YangLeaf;
import org.onosproject.yangutils.datamodel.YangLeafList;
import org.onosproject.yangutils.datamodel.YangLeafRef;
import org.onosproject.yangutils.datamodel.YangLeavesHolder;
import org.onosproject.yangutils.datamodel.YangModule;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangReferenceResolver;
import org.onosproject.yangutils.datamodel.YangResolutionInfo;
import org.onosproject.yangutils.datamodel.YangRpc;
import org.onosproject.yangutils.datamodel.YangType;
import org.onosproject.yangutils.datamodel.YangUnion;
import org.onosproject.yangutils.datamodel.YangUses;
import org.onosproject.yangutils.datamodel.exceptions.DataModelException;
import org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes;

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

/**
 * Represents utilities for data model tree.
 */
public final class DataModelUtils {
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    private static final String SLASH = File.separator;

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

        if (resolutionInfo.getEntityToResolveInfo()
                .getEntityToResolve() instanceof YangType) {
            resolutionNode.addToResolutionList(resolutionInfo,
                    ResolvableType.YANG_DERIVED_DATA_TYPE);
        } else if (resolutionInfo.getEntityToResolveInfo()
                .getEntityToResolve() instanceof YangUses) {
            resolutionNode.addToResolutionList(resolutionInfo,
                    ResolvableType.YANG_USES);
        } else if (resolutionInfo.getEntityToResolveInfo()
                .getEntityToResolve() instanceof YangAugment) {
            resolutionNode.addToResolutionList(resolutionInfo,
                    ResolvableType.YANG_AUGMENT);
        } else if (resolutionInfo.getEntityToResolveInfo()
                .getEntityToResolve() instanceof YangIfFeature) {
            resolutionNode.addToResolutionList(resolutionInfo,
                    ResolvableType.YANG_IF_FEATURE);
        } else if (resolutionInfo.getEntityToResolveInfo()
                .getEntityToResolve() instanceof YangLeafRef) {
            resolutionNode.addToResolutionList(resolutionInfo,
                    ResolvableType.YANG_LEAFREF);
        } else if (resolutionInfo.getEntityToResolveInfo().getEntityToResolve() instanceof YangBase) {
            resolutionNode.addToResolutionList(resolutionInfo, ResolvableType.YANG_BASE);
        } else if (resolutionInfo.getEntityToResolveInfo().getEntityToResolve() instanceof YangIdentityRef) {
            resolutionNode.addToResolutionList(resolutionInfo, ResolvableType.YANG_IDENTITYREF);
        } else if (resolutionInfo.getEntityToResolveInfo()
                .getEntityToResolve() instanceof YangCompilerAnnotation) {
            resolutionNode.addToResolutionList(resolutionInfo,
                    ResolvableType.YANG_COMPILER_ANNOTATION);
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
     * @param clonedNode holder node
     * @param yangUses   YANG uses
     * @throws CloneNotSupportedException clone not supported error
     * @throws DataModelException         data model error
     */
    public static void cloneListOfLeaf(
            YangLeavesHolder clonedNode, YangUses yangUses)
            throws CloneNotSupportedException, DataModelException {

        List<YangLeaf> leaves = clonedNode.getListOfLeaf();
        if (nonEmpty(leaves)) {
            List<YangLeaf> clonedLeaves = new LinkedList<>();
            for (YangLeaf leaf : leaves) {
                YangLeaf clonedLeaf = leaf.clone();
                clonedLeaf.setReferredLeaf(leaf);
                addUnresolvedType(yangUses, clonedLeaf, (YangNode) clonedNode);
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
     * @param clonedNode cloned holder
     * @param yangUses   YANG uses
     * @throws CloneNotSupportedException clone not supported error
     * @throws DataModelException         data model error
     */
    public static void cloneListOfLeafList(
            YangLeavesHolder clonedNode, YangUses yangUses)
            throws CloneNotSupportedException, DataModelException {

        List<YangLeafList> listOfLeafList = clonedNode.getListOfLeafList();
        if (nonEmpty(listOfLeafList)) {
            List<YangLeafList> clonedList = new LinkedList<>();
            for (YangLeafList leafList : listOfLeafList) {
                YangLeafList clonedLeafList = leafList.clone();
                clonedLeafList.setReferredSchemaLeafList(leafList);
                addUnresolvedType(yangUses, clonedLeafList,
                                  (YangNode) clonedNode);
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
                if (leaf.getDataType().getDataType() == YangDataTypes.ENUMERATION
                        || leaf.getDataType().getDataType() == YangDataTypes.UNION) {
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
                if (leafList.getDataType().getDataType() == YangDataTypes.ENUMERATION
                        || leafList.getDataType().getDataType() == YangDataTypes.UNION) {
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
            if (dataType.getDataType() == YangDataTypes.ENUMERATION) {
                YangEnumeration enumNode = (YangEnumeration) dataType.getDataTypeExtendedInfo();
                dataTypeName = enumNode.getName();
            } else if (dataType.getDataType() == YangDataTypes.UNION) {
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
}
