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

import org.onosproject.yang.compiler.datamodel.CollisionDetector;
import org.onosproject.yang.compiler.datamodel.DefaultLocationInfo;
import org.onosproject.yang.compiler.datamodel.Resolvable;
import org.onosproject.yang.compiler.datamodel.RpcNotificationContainer;
import org.onosproject.yang.compiler.datamodel.TraversalType;
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangAugmentableNode;
import org.onosproject.yang.compiler.datamodel.YangBase;
import org.onosproject.yang.compiler.datamodel.YangCompilerAnnotation;
import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangDeviateAdd;
import org.onosproject.yang.compiler.datamodel.YangDeviateDelete;
import org.onosproject.yang.compiler.datamodel.YangDeviateReplace;
import org.onosproject.yang.compiler.datamodel.YangDeviation;
import org.onosproject.yang.compiler.datamodel.YangDeviationHolder;
import org.onosproject.yang.compiler.datamodel.YangEntityToResolveInfoImpl;
import org.onosproject.yang.compiler.datamodel.YangFeature;
import org.onosproject.yang.compiler.datamodel.YangFeatureHolder;
import org.onosproject.yang.compiler.datamodel.YangGrouping;
import org.onosproject.yang.compiler.datamodel.YangIdentity;
import org.onosproject.yang.compiler.datamodel.YangIdentityRef;
import org.onosproject.yang.compiler.datamodel.YangIfFeature;
import org.onosproject.yang.compiler.datamodel.YangImport;
import org.onosproject.yang.compiler.datamodel.YangInclude;
import org.onosproject.yang.compiler.datamodel.YangInput;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.YangReferenceResolver;
import org.onosproject.yang.compiler.datamodel.YangRelativePath;
import org.onosproject.yang.compiler.datamodel.YangResolutionInfo;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangTypeDef;
import org.onosproject.yang.compiler.datamodel.YangUses;
import org.onosproject.yang.compiler.datamodel.YangXPathResolver;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.linker.exceptions.LinkerException;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_LEAFREF;
import static org.onosproject.yang.compiler.datamodel.TraversalType.CHILD;
import static org.onosproject.yang.compiler.datamodel.TraversalType.PARENT;
import static org.onosproject.yang.compiler.datamodel.TraversalType.ROOT;
import static org.onosproject.yang.compiler.datamodel.TraversalType.SIBLING;
import static org.onosproject.yang.compiler.datamodel.YangNode.cloneGroupingTree;
import static org.onosproject.yang.compiler.datamodel.YangPathArgType.ABSOLUTE_PATH;
import static org.onosproject.yang.compiler.datamodel.YangPathArgType.RELATIVE_PATH;
import static org.onosproject.yang.compiler.datamodel.exceptions.ErrorMessages.getErrorMsg;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.addResolutionInfo;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.deleteUnsupportedLeafOrLeafList;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.deleteUnsupportedNodeFromTree;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.findLeafNode;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.updateDeviateAddToTargetNode;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.updateDeviateDeleteToTargetNode;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.updateDeviateReplaceToTargetNode;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.INTER_FILE_LINKED;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.INTRA_FILE_RESOLVED;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.LINKED;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.RESOLVED;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.UNDEFINED;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.UNRESOLVED;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.LEAF_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.LEAF_LIST_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.PATH_DATA;
import static org.onosproject.yang.compiler.linker.impl.XpathLinkingTypes.AUGMENT_LINKING;
import static org.onosproject.yang.compiler.linker.impl.XpathLinkingTypes.DEVIATION_LINKING;
import static org.onosproject.yang.compiler.linker.impl.XpathLinkingTypes.LEAF_REF_LINKING;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.detectCollisionForAugment;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.fillPathPredicates;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.getErrorInfoForLinker;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.getLeafRefErrorInfo;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.getPathWithAugment;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.getValidNodeIdentifier;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.skipInvalidDataNodes;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.FAILED_TO_FIND_ANNOTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.FAILED_TO_FIND_DEVIATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.FAILED_TO_FIND_LEAD_INFO_HOLDER;
import static org.onosproject.yang.compiler.utils.UtilConstants.FAILED_TO_LINK;
import static org.onosproject.yang.compiler.utils.UtilConstants.INVALID_ENTITY;
import static org.onosproject.yang.compiler.utils.UtilConstants.INVALID_LINKER_STATE;
import static org.onosproject.yang.compiler.utils.UtilConstants.INVALID_RESOLVED_ENTITY;
import static org.onosproject.yang.compiler.utils.UtilConstants.INVALID_TARGET;
import static org.onosproject.yang.compiler.utils.UtilConstants.INVALID_TREE;
import static org.onosproject.yang.compiler.utils.UtilConstants.LEAFREF;
import static org.onosproject.yang.compiler.utils.UtilConstants.LINKER_ERROR;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH_FOR_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.UNRESOLVABLE;

/**
 * Represents implementation of resolution object which will be resolved by
 * linker.
 *
 * @param <T> type of resolution entity uses / type
 */
public class YangResolutionInfoImpl<T> extends DefaultLocationInfo
        implements YangResolutionInfo<T>, Serializable {

    private static final long serialVersionUID = 806201658L;

    /**
     * Information about the entity that needs to be resolved.
     */
    private YangEntityToResolveInfoImpl<T> entityToResolveInfo;

    /**
     * Current module/sub-module reference, will be used in inter-file/
     * inter-jar scenario to get the import/include list.
     */
    private YangReferenceResolver curRefResolver;

    /**
     * Stack for type/uses is maintained for hierarchical references, this is
     * used during resolution.
     */
    private Stack<YangEntityToResolveInfoImpl<T>> partialResolvedStack;

    /**
     * It is private to ensure the overloaded method be invoked to create an
     * object.
     */
    @SuppressWarnings("unused")
    private YangResolutionInfoImpl() {
    }

    /**
     * Creates a resolution information object with all the inputs.
     *
     * @param dataNode           current parsable data node
     * @param holderNode         parent YANG node
     * @param lineNumber         error line number
     * @param charPositionInLine error character position in line
     */
    public YangResolutionInfoImpl(T dataNode, YangNode holderNode, int lineNumber,
                                  int charPositionInLine) {
        entityToResolveInfo = new YangEntityToResolveInfoImpl<>();
        entityToResolveInfo.setEntityToResolve(dataNode);
        entityToResolveInfo.setHolderOfEntityToResolve(holderNode);
        setLineNumber(lineNumber);
        setCharPosition(charPositionInLine);
        partialResolvedStack = new Stack<>();
    }

    @Override
    public void resolveLinkingForResolutionInfo(YangReferenceResolver dataModelRootNode)
            throws DataModelException {

        curRefResolver = dataModelRootNode;
    /*
         * Current node to resolve, it can be a YANG type, YANG uses or YANG if-feature or
         * YANG leafref or YANG base or YANG identityref.
         */
        T entityToResolve = entityToResolveInfo.getEntityToResolve();

        // Check if linking is already done
        if (entityToResolve instanceof Resolvable) {
            Resolvable resolvable = (Resolvable) entityToResolve;
            if (resolvable.getResolvableStatus() == RESOLVED) {
                /*
                 * entity is already resolved, so nothing to do
                 */
                return;
            }
        } else {
            throw new DataModelException(LINKER_ERROR);
        }
        // Push the initial entity to resolve in stack.
        addInPartialResolvedStack(entityToResolveInfo);
        linkAndResolvePartialResolvedStack();
        addDerivedRefTypeToRefTypeResolutionList();
    }

    /**
     * Resolves linking with ancestors.
     *
     * @throws DataModelException a violation of data model rules
     */
    private void linkAndResolvePartialResolvedStack()
            throws DataModelException {

        while (!partialResolvedStack.isEmpty()) {
            /*
             * Current node to resolve, it can be a YANG type or YANG uses or
             * YANG if-feature or YANG leafref or YANG base or YANG identityref.
             */
            T entityToResolve = getCurEntityToResolveFromStack();
            if (!(entityToResolve instanceof Resolvable)) {
                throw new DataModelException(LINKER_ERROR);
            }
            // Check if linking is already done
            Resolvable resolvable = (Resolvable) entityToResolve;
            switch (resolvable.getResolvableStatus()) {
                case RESOLVED:
                        /*
                         * If the entity is already resolved in the stack, then pop
                         * it and continue with the remaining stack elements to
                         * resolve
                         */
                    partialResolvedStack.pop();
                    break;

                case LINKED:
                        /*
                         * If the top of the stack is already linked then resolve
                         * the references and pop the entity and continue with
                         * remaining stack elements to resolve.
                         */
                    resolveTopOfStack();
                    partialResolvedStack.pop();
                    break;

                case INTRA_FILE_RESOLVED:
                        /*
                         * Pop the top of the stack.
                         */
                    partialResolvedStack.pop();
                    break;

                case UNRESOLVED:
                    linkTopOfStackReferenceUpdateStack();

                    if (resolvable.getResolvableStatus() == UNRESOLVED) {
                        // If current entity is still not resolved, then
                        // linking/resolution has failed.
                        DataModelException ex = new DataModelException(
                                getErrorInfoForLinker(resolvable));
                        ex.setLine(getLineNumber());
                        ex.setCharPosition(getCharPosition());
                        throw ex;
                    }
                    break;

                default:
                    throw new DataModelException(INVALID_LINKER_STATE);
            }
        }
    }

    /**
     * Adds leaf-ref to the resolution list, with different context if
     * leaf-ref is defined under derived type. Leaf-ref must be resolved from
     * where the typedef is referenced.
     */
    private void addDerivedRefTypeToRefTypeResolutionList()
            throws DataModelException {

        YangNode refNode = entityToResolveInfo.getHolderOfEntityToResolve();
        YangDerivedInfo info = getValidResolvableType();

        if (info == null) {
            return;
        }

        YangType<T> type =
                (YangType<T>) entityToResolveInfo.getEntityToResolve();

        T extType = (T) info.getReferredTypeDef().getTypeDefBaseType()
                .getDataTypeExtendedInfo();

        while (extType instanceof YangDerivedInfo) {
            info = (YangDerivedInfo) extType;
            extType = (T) info.getReferredTypeDef().getTypeDefBaseType()
                    .getDataTypeExtendedInfo();
        }
        /*
         * Backup the leaf-ref info from derived type and deletes the derived
         * type info. Copies the backed up leaf-ref data to the actual type in
         * replacement of derived type. Adds to the resolution list in this
         * context.
         */
        addRefTypeInfo(extType, type, refNode);
    }

    /**
     * Returns the derived info if the holder is typedef, the entity is type
     * and the effective type is leaf-ref; null otherwise.
     *
     * @return derived info
     */
    private YangDerivedInfo<?> getValidResolvableType() {

        YangNode refNode = entityToResolveInfo.getHolderOfEntityToResolve();
        T entity = entityToResolveInfo.getEntityToResolve();

        if (!(refNode instanceof YangTypeDef) && entity instanceof YangType) {
            YangType<?> type = (YangType) entity;
            YangDerivedInfo<?> info =
                    (YangDerivedInfo) type.getDataTypeExtendedInfo();
            YangDataTypes dataType = info.getEffectiveBuiltInType();
            if ((type.getResolvableStatus() == RESOLVED) &&
                    (dataType == YangDataTypes.LEAFREF)) {
                return info;
            }
        }
        return null;
    }

    /**
     * Adds resolvable type (leaf-ref) info to resolution list.
     *
     * @param extType resolvable type
     * @param type    YANG type
     * @param holder  holder node
     * @throws DataModelException if there is a data model error
     */
    private void addRefTypeInfo(T extType, YangType<T> type, YangNode holder)
            throws DataModelException {

        type.resetYangType();
        type.setResolvableStatus(RESOLVED);
        type.setDataType(YangDataTypes.LEAFREF);
        type.setDataTypeName(LEAFREF);
        type.setDataTypeExtendedInfo(extType);

        YangLeafRef leafRef = (YangLeafRef) extType;
        (leafRef).setResolvableStatus(UNRESOLVED);
        leafRef.setParentNode(holder);

        YangResolutionInfoImpl info = new YangResolutionInfoImpl<>(
                leafRef, holder, getLineNumber(), getCharPosition());
        curRefResolver.addToResolutionList(info, YANG_LEAFREF);
        curRefResolver.resolveSelfFileLinking(YANG_LEAFREF);
    }

    /**
     * Resolves the current entity in the stack.
     */
    private void resolveTopOfStack()
            throws DataModelException {
        T entity = getCurEntityToResolveFromStack();
        List<T> entityToResolve = (List<T>) ((Resolvable) entity).resolve();
        if (entityToResolve != null && !entityToResolve.isEmpty()) {
            for (T anEntityToResolve : entityToResolve) {
                addUnresolvedEntitiesToResolutionList(anEntityToResolve);
            }
        }
        if (((Resolvable) entity).getResolvableStatus() != INTRA_FILE_RESOLVED &&
                ((Resolvable) entity).getResolvableStatus() != UNDEFINED) {
            // Sets the resolution status in inside the type/uses/if-feature/leafref.
            ((Resolvable) entity).setResolvableStatus(RESOLVED);
        }
    }

    /**
     * Adds the unresolved entities to the resolution list.
     *
     * @param entityToResolve entity to resolve
     * @throws DataModelException a violation of data model rules
     */
    private void addUnresolvedEntitiesToResolutionList(T entityToResolve)
            throws DataModelException {
        if (entityToResolve instanceof YangEntityToResolveInfoImpl) {
            YangEntityToResolveInfoImpl entityToResolveInfo
                    = (YangEntityToResolveInfoImpl) entityToResolve;
            if (entityToResolveInfo.getEntityToResolve() instanceof YangLeafRef) {
                YangLeafRef leafref = (YangLeafRef) entityToResolveInfo
                        .getEntityToResolve();
                YangNode parentNodeOfLeafref = entityToResolveInfo
                        .getHolderOfEntityToResolve();
                leafref.setParentNode(parentNodeOfLeafref);
                if (leafref.getResolvableStatus() == UNRESOLVED) {
                    leafref.setResolvableStatus(INTRA_FILE_RESOLVED);
                }
            }

            // Add resolution information to the list.
            YangResolutionInfoImpl resolutionInfoImpl = new YangResolutionInfoImpl<>(
                    entityToResolveInfo.getEntityToResolve(),
                    entityToResolveInfo.getHolderOfEntityToResolve(),
                    entityToResolveInfo.getLineNumber(),
                    entityToResolveInfo.getCharPosition());
            addResolutionInfo(resolutionInfoImpl);
        }
    }

    /**
     * Resolves linking for a node child and siblings.
     *
     * @throws DataModelException data model error
     */
    private void linkTopOfStackReferenceUpdateStack()
            throws DataModelException {
        T entity = getCurEntityToResolveFromStack();
        if (entity instanceof YangLeafRef) {
            ((Resolvable) entity).setResolvableStatus(INTRA_FILE_RESOLVED);
            return;
        }
        /*
         * Check if self file reference is there, this will not check for the
         * scenario when prefix is not present and type/uses is present in
         * sub-module from include list.
         */
        if (!isCandidateForSelfFileReference()) {
            ((Resolvable) entity).setResolvableStatus(INTRA_FILE_RESOLVED);
            return;
        }

        /*
         * Try to resolve the top of the stack and update partial resolved stack
         * if there is recursive references
         */
        YangNode ancestorRefNode = partialResolvedStack.peek()
                .getHolderOfEntityToResolve();

        if (entity instanceof YangIfFeature) {
            resolveSelfFileLinkingForIfFeature(ancestorRefNode);
            return;
        }
        if (entity instanceof YangIdentityRef || entity instanceof YangBase) {
            resolveSelfFileLinkingForBaseAndIdentityref();
            return;
        }
        YangType type = null;
        if (entity instanceof YangType) {
            type = (YangType) entity;
        }
            /*
             * Traverse up in the ancestor tree to check if the referred node is
             * defined
             */
        while (ancestorRefNode != null) {
                /*
                 * Check for the referred node defined in a ancestor scope
                 */
            YangNode curRefNode = ancestorRefNode.getChild();
            if (isReferredNodeInSiblingListProcessed(curRefNode)) {
                return;
            }
            ancestorRefNode = ancestorRefNode.getParent();
            if (type != null && ancestorRefNode != null) {
                if (ancestorRefNode.getParent() == null) {
                    type.setTypeNotResolvedTillRootNode(true);
                }
            }
        }

        /*
         * In case prefix is not present or it's self prefix it's a candidate for inter-file
         * resolution via include list.
         */
        if (getRefPrefix() == null ||
                getRefPrefix().contentEquals(curRefResolver.getPrefix())) {
            ((Resolvable) entity).setResolvableStatus(INTRA_FILE_RESOLVED);
        }
    }

    /**
     * Resolves self file linking for base/identityref.
     *
     * @throws DataModelException a violation of data model rules
     */
    private void resolveSelfFileLinkingForBaseAndIdentityref()
            throws DataModelException {

        boolean refIdentity = false;
        String nodeName = null;
        T entity = getCurEntityToResolveFromStack();
        if (entity instanceof YangIdentityRef) {
            nodeName = ((YangIdentityRef) entity).getName();
        } else if (entity instanceof YangBase) {
            nodeName = ((YangBase) entity).getBaseIdentifier().getName();
        }
        if (curRefResolver instanceof RpcNotificationContainer) {
            // Sends list of nodes for finding the target identity.
            refIdentity = isIdentityReferenceFound(nodeName, (YangNode) curRefResolver);
        }
        if (refIdentity) {
            return;
        }

        /*
         * In case prefix is not present or it's self prefix it's a candidate for inter-file
         * resolution via include list.
         */
        if (getRefPrefix() == null || getRefPrefix()
                .contentEquals(curRefResolver.getPrefix())) {
            ((Resolvable) entity).setResolvableStatus(INTRA_FILE_RESOLVED);
        }
    }

    /**
     * Resolves self file linking for if-feature.
     *
     * @param ancestorRefNode if-feature holder node
     * @throws DataModelException DataModelException a violation of data model
     *                            rules
     */
    private void resolveSelfFileLinkingForIfFeature(YangNode ancestorRefNode)
            throws DataModelException {

        YangFeatureHolder featureHolder = getFeatureHolder(ancestorRefNode);
        YangNode curRefNode = (YangNode) featureHolder;
        if (isReferredNode(curRefNode)) {

            // Adds reference link of entity to the node under resolution.
            addReferredEntityLink(curRefNode, LINKED);

            /*
             * resolve the reference and update the partial resolution stack
             * with any further recursive references
             */
            addUnresolvedRecursiveReferenceToStack(curRefNode);
            return;
        }

        /*
         * In case prefix is not present or it's self prefix it's a candidate for inter-file
         * resolution via include list.
         */
        if (getRefPrefix() == null || getRefPrefix()
                .contentEquals(curRefResolver.getPrefix())) {
            ((Resolvable) getCurEntityToResolveFromStack())
                    .setResolvableStatus(INTRA_FILE_RESOLVED);
        }
    }

    /**
     * Returns the status of the referred identity found for base/identityref.
     *
     * @param nodeName        the name of the base node
     *                        identifier/identityref node identifier
     * @param ancestorRefNode the parent node of base/identityref
     * @return status of referred base/identityref
     * @throws DataModelException a violation of data model rules
     */
    private boolean isIdentityReferenceFound(String nodeName, YangNode ancestorRefNode)
            throws DataModelException {

        // When child is not present return.
        if (ancestorRefNode.getChild() == null) {
            return false;
        }

        ancestorRefNode = ancestorRefNode.getChild();

        // Checks all the siblings under the node and returns the matched node.
        YangNode nodeFound = isReferredNodeInSiblingProcessedForIdentity(ancestorRefNode,
                                                                         nodeName);

        if (nodeFound != null) {
            // Adds reference link of entity to the node under resolution.
            addReferredEntityLink(nodeFound, LINKED);

            /*
             * resolve the reference and update the partial resolution stack with any further recursive references
             */
            addUnresolvedRecursiveReferenceToStack(nodeFound);
            return true;
        }

        return false;
    }

    /**
     * Adds the unresolved constructs to stack which has to be resolved for leafref.
     *
     * @param leavesInfo      YANG leaf or leaf list which holds the type
     * @param ancestorRefNode holder of the YANG leaf or leaf list
     */
    private void addUnResolvedLeafRefTypeToStack(T leavesInfo, YangNode ancestorRefNode) {

        YangType refType;
        T extendedInfo;
        if (leavesInfo instanceof YangLeaf) {
            YangLeaf leaf = (YangLeaf) leavesInfo;
            refType = leaf.getDataType();
        } else {
            YangLeafList leafList = (YangLeafList) leavesInfo;
            refType = leafList.getDataType();
        }
        extendedInfo = (T) refType.getDataTypeExtendedInfo();
        addUnResolvedTypeDataToStack(refType, ancestorRefNode, extendedInfo);
    }

    //Adds unresolved type info to stack.
    private void addUnResolvedTypeDataToStack(YangType refType, YangNode
            ancestorRefNode, T extendedInfo) {
        YangEntityToResolveInfoImpl<YangLeafRef<?>> unResolvedLeafRef =
                new YangEntityToResolveInfoImpl<>();
        YangEntityToResolveInfoImpl<YangType<?>> unResolvedTypeDef =
                new YangEntityToResolveInfoImpl<>();
        if (refType.getDataType() == YangDataTypes.LEAFREF) {
            unResolvedLeafRef.setEntityToResolve((YangLeafRef<?>) extendedInfo);
            unResolvedLeafRef.setHolderOfEntityToResolve(ancestorRefNode);
            addInPartialResolvedStack((YangEntityToResolveInfoImpl<T>) unResolvedLeafRef);
        } else if (refType.getDataType() == YangDataTypes.DERIVED) {
            unResolvedTypeDef.setEntityToResolve(refType);
            unResolvedTypeDef.setHolderOfEntityToResolve(ancestorRefNode);
            addInPartialResolvedStack((YangEntityToResolveInfoImpl<T>) unResolvedTypeDef);
        }
    }

    /**
     * Returns feature holder(module/sub-module node) .
     *
     * @param ancestorRefNode if-feature holder node
     */
    private YangFeatureHolder getFeatureHolder(YangNode ancestorRefNode) {
        while (ancestorRefNode != null) {
            if (ancestorRefNode instanceof YangFeatureHolder) {
                return (YangFeatureHolder) ancestorRefNode;
            }
            ancestorRefNode = ancestorRefNode.getParent();
        }
        return null;
    }

    /**
     * Checks if the reference in self file or in external file.
     *
     * @return true if self file reference, false otherwise
     * @throws DataModelException a violation of data model rules
     */
    private boolean isCandidateForSelfFileReference()
            throws DataModelException {
        String prefix = getRefPrefix();
        return prefix == null || prefix.contentEquals(curRefResolver.getPrefix());
    }

    /**
     * Checks for the referred parent node for the base/identity.
     *
     * @param refNode potential referred node
     * @return the referred parent node of base/identity.
     * @throws DataModelException data model errors
     */
    private YangNode isReferredNodeInSiblingProcessedForIdentity(YangNode refNode,
                                                                 String refName)
            throws DataModelException {

        while (refNode != null) {
            if (refNode instanceof YangIdentity) {
                // Check if the potential referred node is the actual referred node
                if (isReferredNodeForIdentity(refNode, refName)) {
                    return refNode;
                }
            }
            refNode = refNode.getNextSibling();
        }
        return null;
    }

    /**
     * Checks if the current reference node name and the name in the base/identityref base are equal.
     *
     * @param curRefNode the node where the reference is pointed
     * @param name       name of the base in the base/identityref base
     * @return status of the match between the name
     * @throws DataModelException a violation of data model rules
     */
    private boolean isReferredNodeForIdentity(YangNode curRefNode, String name)
            throws DataModelException {
        T entity = getCurEntityToResolveFromStack();
        if (entity instanceof YangIdentityRef || entity instanceof YangBase) {

            //Check if name of node name matches with the current reference node.
            return curRefNode.getName().contentEquals(name);
        } else {
            throw new DataModelException(getErrorMsg(
                    INVALID_ENTITY, curRefNode.getName(), curRefNode.getLineNumber(),
                    curRefNode.getCharPosition(), curRefNode.getFileName()));
        }
    }

    /**
     * Checks for the referred node defined in a ancestor scope.
     *
     * @param refNode potential referred node
     * @return status of resolution and updating the partial resolved stack with
     * the any recursive references
     * @throws DataModelException a violation of data model rules
     */
    private boolean isReferredNodeInSiblingListProcessed(YangNode refNode)
            throws DataModelException {
        while (refNode != null) {

            // Check if the potential referred node is the actual referred node
            if (isReferredNode(refNode)) {

                // Adds reference link of entity to the node under resolution.
                addReferredEntityLink(refNode, LINKED);

                /*
                 * resolve the reference and update the partial resolution stack
                 * with any further recursive references
                 */
                addUnresolvedRecursiveReferenceToStack(refNode);

                /*
                 * return true, since the reference is linked and any recursive
                 * unresolved references is added to the stack
                 */
                return true;
            }

            refNode = refNode.getNextSibling();
        }
        return false;
    }

    /**
     * Checks if the potential referred node is the actual referred node.
     *
     * @param refNode typedef/grouping node
     * @return true if node is of resolve type otherwise false
     * @throws DataModelException a violation of data model rules
     */
    private boolean isReferredNode(YangNode refNode)
            throws DataModelException {
        T entity = getCurEntityToResolveFromStack();
        if (entity instanceof YangType) {
            if (refNode instanceof YangTypeDef) {
                return isNodeNameSameAsResolutionInfoName(refNode);
            }
        } else if (entity instanceof YangUses) {
            if (refNode instanceof YangGrouping) {
                return isNodeNameSameAsResolutionInfoName(refNode);
            }
        } else if (entity instanceof YangIfFeature) {
            if (refNode instanceof YangFeatureHolder) {
                return isNodeNameSameAsResolutionInfoName(refNode);
            }
        } else if (entity instanceof YangBase || entity instanceof YangIdentityRef) {
            if (refNode instanceof YangIdentity) {
                return isNodeNameSameAsResolutionInfoName(refNode);
            }
        } else {
            throw new DataModelException(getErrorMsg(
                    LINKER_ERROR, refNode.getName(), refNode.getLineNumber(),
                    refNode.getCharPosition(), refNode.getFileName()));
        }
        return false;
    }

    /**
     * Checks if node name is same as name in resolution info, i.e. name of
     * typedef/grouping is same as name of type/uses.
     *
     * @param node typedef/grouping node
     * @return true if node name is same as name in resolution info, otherwise
     * false
     * @throws DataModelException a violation of data model rules
     */

    private boolean isNodeNameSameAsResolutionInfoName(YangNode node)
            throws DataModelException {
        T entity = getCurEntityToResolveFromStack();
        if (entity instanceof YangType) {
            return node.getName().contentEquals(((YangType<?>) entity)
                                                        .getDataTypeName());
        }
        if (entity instanceof YangUses) {
            return node.getName().contentEquals(((YangUses) entity).getName());
        }
        if (entity instanceof YangIfFeature) {
            return isFeatureDefinedInNode(node);
        }
        if (entity instanceof YangBase) {
            return node.getName().contentEquals(((YangBase) entity)
                                                        .getBaseIdentifier()
                                                        .getName());
        }
        if (entity instanceof YangIdentityRef) {
            return node.getName().contentEquals(((YangIdentityRef) entity)
                                                        .getName());
        }
        throw new DataModelException(getErrorMsg(
                INVALID_RESOLVED_ENTITY, node.getName(), node.getLineNumber(),
                node.getCharPosition(), node.getFileName()));
    }

    private boolean isFeatureDefinedInNode(YangNode node) {
        T entity = getCurEntityToResolveFromStack();
        YangNodeIdentifier ifFeature = ((YangIfFeature) entity).getName();
        List<YangFeature> featureList = ((YangFeatureHolder) node)
                .getFeatureList();
        if (featureList != null && !featureList.isEmpty()) {
            Iterator<YangFeature> iterator = featureList.iterator();
            while (iterator.hasNext()) {
                YangFeature feature = iterator.next();
                if (ifFeature.getName().equals(feature.getName())) {
                    ((YangIfFeature) entity).setReferredFeature(feature);
                    ((YangIfFeature) entity).setReferredFeatureHolder(node);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds reference of grouping/typedef in uses/type.
     *
     * @param refNode      grouping/typedef node being referred
     * @param linkedStatus linked status if success.
     * @throws DataModelException a violation of data model rules
     */
    private void addReferredEntityLink(YangNode refNode, ResolvableStatus linkedStatus)
            throws DataModelException {
        T entity = getCurEntityToResolveFromStack();
        if (entity instanceof YangType) {
            YangDerivedInfo<?> derivedInfo =
                    (YangDerivedInfo<?>) ((YangType<?>) entity)
                            .getDataTypeExtendedInfo();
            derivedInfo.setReferredTypeDef((YangTypeDef) refNode);
        } else if (entity instanceof YangUses) {
            ((YangUses) entity).setRefGroup((YangGrouping) refNode);
        } else if (entity instanceof YangBase) {
            ((YangBase) entity).setReferredIdentity((YangIdentity) refNode);
            addToIdentityExtendList(((YangIdentity) ((YangBase) entity)
                    .getParentIdentity()), (YangIdentity) refNode);
        } else if (entity instanceof YangIdentityRef) {
            ((YangIdentityRef) entity).setReferredIdentity((YangIdentity) refNode);
        } else if (!(entity instanceof YangIfFeature) &&
                !(entity instanceof YangLeafRef)) {
            throw new DataModelException(getErrorMsg(
                    LINKER_ERROR, refNode.getName(), refNode.getLineNumber(),
                    refNode.getCharPosition(), refNode.getFileName()));
        }
        // Sets the resolution status in inside the type/uses.
        ((Resolvable) entity).setResolvableStatus(linkedStatus);
    }

    private void addToIdentityExtendList(YangIdentity baseIdentity, YangIdentity
            referredIdentity) {
        YangIdentity referredId = referredIdentity;
        referredId.addToExtendList(baseIdentity);
    }

    /**
     * Checks if type/grouping has further reference to typedef/ unresolved
     * uses. Add it to the partial resolve stack and return the status of
     * addition to stack.
     *
     * @param refNode grouping/typedef node
     * @throws DataModelException a violation of data model rules
     */
    private void addUnresolvedRecursiveReferenceToStack(YangNode refNode)
            throws DataModelException {
        T entity = getCurEntityToResolveFromStack();
        if (entity instanceof YangType) {

            //Checks if typedef type is derived
            if (((YangTypeDef) refNode).getTypeDefBaseType()
                    .getDataType() == YangDataTypes.DERIVED) {
                addEntityToStack((T) ((YangTypeDef) refNode).getTypeDefBaseType(),
                                 refNode);
            }
        } else if (entity instanceof YangUses) {
            /*
             * Search if the grouping has any un resolved uses child, if so
             * return true, else return false.
             */
            addUnResolvedUsesToStack(refNode);
        } else if (entity instanceof YangIfFeature) {
            addUnResolvedIfFeatureToStack(refNode);
        } else if (entity instanceof YangLeafRef) {
            // do nothing , referred node is already set
            throw new DataModelException(getErrorMsg(
                    INVALID_RESOLVED_ENTITY, refNode.getName(), refNode.getLineNumber(),
                    refNode.getCharPosition(), refNode.getFileName()));
        } else if (entity instanceof YangBase || entity instanceof YangIdentityRef) {

            //Search if the identity has any un resolved base,
            // if so return true, else return false.
            addUnResolvedBaseToStack(refNode);
        } else {
            throw new DataModelException(getErrorMsg(
                    LINKER_ERROR, refNode.getName(), refNode.getLineNumber(),
                    refNode.getCharPosition(), refNode.getFileName()));
        }
    }

    /**
     * Returns if there is any unresolved uses in grouping.
     *
     * @param node grouping/typedef node
     */
    private void addUnResolvedUsesToStack(YangNode node) {

        //Search the grouping node's children for presence of uses node.
        TraversalType curTraversal = ROOT;
        YangNode curNode = node.getChild();
        while (curNode != null) {
            if (curNode.getName().equals(node.getName())) {
                // if we have traversed all the child nodes, then exit from loop
                return;
            }

            // if child nodes has uses, then add it to resolution stack
            if (curNode instanceof YangUses) {
                addEntityToStack((T) curNode, node);
            }

            // Traversing all the child nodes of grouping
            if (curTraversal != PARENT && curNode.getChild() != null) {
                curTraversal = CHILD;
                curNode = curNode.getChild();
            } else if (curNode.getNextSibling() != null) {
                curTraversal = SIBLING;
                curNode = curNode.getNextSibling();
            } else {
                curTraversal = PARENT;
                curNode = curNode.getParent();
            }
        }
    }

    /**
     * Returns if there is any unresolved if-feature in feature.
     *
     * @param node module/submodule node
     */
    private void addUnResolvedIfFeatureToStack(YangNode node) {
        YangFeature refFeature = ((YangIfFeature) getCurEntityToResolveFromStack())
                .getReferredFeature();
        List<YangIfFeature> ifFeatureList = refFeature.getIfFeatureList();
        if (ifFeatureList != null && !ifFeatureList.isEmpty()) {
            Iterator<YangIfFeature> ifFeatureIterator = ifFeatureList.iterator();
            while (ifFeatureIterator.hasNext()) {
                addEntityToStack((T) ifFeatureIterator.next(), node);
            }
        }
    }

    /**
     * Returns if there is any unresolved base in identity.
     *
     * @param node module/submodule node
     */
    private void addUnResolvedBaseToStack(YangNode node) {

        YangIdentity curNode = (YangIdentity) node;
        if (curNode.getBaseNode() != null) {
            if (curNode.getBaseNode().getResolvableStatus() != RESOLVED) {
                addEntityToStack((T) curNode.getBaseNode(), node);
            }
        }
    }

    private void addEntityToStack(T entity, YangNode holder) {
        YangEntityToResolveInfoImpl<T> unResolvedEntityInfo =
                new YangEntityToResolveInfoImpl<>();
        unResolvedEntityInfo.setEntityToResolve(entity);
        unResolvedEntityInfo.setHolderOfEntityToResolve(holder);
        addInPartialResolvedStack(unResolvedEntityInfo);
    }

    /**
     * Sets stack of YANG type with partially resolved YANG construct hierarchy.
     *
     * @param partialResolvedInfo partial resolved YANG construct stack
     */
    private void addInPartialResolvedStack(
            YangEntityToResolveInfoImpl<T> partialResolvedInfo) {
        partialResolvedStack.push(partialResolvedInfo);
    }

    /**
     * Retrieves the next entity in the stack that needs to be resolved. It is
     * assumed that the caller ensures that the stack is not empty.
     *
     * @return next entity in the stack that needs to be resolved
     */
    private T getCurEntityToResolveFromStack() {
        return partialResolvedStack.peek().getEntityToResolve();
    }

    @Override
    public YangEntityToResolveInfoImpl<T> getEntityToResolveInfo() {
        return entityToResolveInfo;
    }

    @Override
    public void linkInterFile(YangReferenceResolver dataModelRootNode)
            throws DataModelException {

        curRefResolver = dataModelRootNode;

        // Current node to resolve, it can be a YANG type or YANG uses.
        T entityToResolve = entityToResolveInfo.getEntityToResolve();

        // Check if linking is already done
        if (entityToResolve instanceof Resolvable) {
            Resolvable resolvable = (Resolvable) entityToResolve;
            if (resolvable.getResolvableStatus() == RESOLVED) {
                return;
            }
        } else {
            throw new DataModelException(UNRESOLVABLE);
        }

        if (entityToResolve instanceof YangXPathResolver &&
                !(entityToResolve instanceof YangLeafRef)) {
            //Process x-path linking.
            processXPathLinking(entityToResolve, dataModelRootNode);
        } else {
            // Push the initial entity to resolve in stack.
            addInPartialResolvedStack(entityToResolveInfo);
            // Inter file linking and resolution.
            linkInterFileAndResolve();
            addDerivedRefTypeToRefTypeResolutionList();
        }
    }

    /**
     * Process x-path linking for augment and leaf-ref.
     *
     * @param entityToResolve entity to resolve
     * @param root            root node
     * @throws DataModelException if there is a data model error
     */
    private void processXPathLinking(T entityToResolve,
                                     YangReferenceResolver root)
            throws DataModelException {

        YangXpathLinker<T> xPathLinker = new YangXpathLinker<T>();
        YangNode node = entityToResolveInfo.getHolderOfEntityToResolve();
        if (entityToResolve instanceof YangAugment &&
                !(node instanceof YangUses)) {
            YangNode targetNode;
            YangAugment augment = (YangAugment) entityToResolve;
            targetNode = xPathLinker
                    .processXpathLinking(augment.getTargetNode(), (YangNode)
                            root, AUGMENT_LINKING);
            if (targetNode != null) {
                if (targetNode instanceof YangAugmentableNode) {
                    detectCollisionForAugment(targetNode, augment,
                                              (YangNode) root);
                    ((YangAugmentableNode) targetNode).addAugmentation(augment);
                    augment.setAugmentedNode(targetNode);
                    setAugmentedFlagInAncestors(targetNode);
                    Resolvable resolvable = (Resolvable) entityToResolve;
                    resolvable.setResolvableStatus(RESOLVED);
                    if (targetNode instanceof YangInput) {
                        xPathLinker.addInModuleIfInput(augment, (YangNode) root);
                    }
                } else {
                    throw new LinkerException(getErrorMsg(
                            INVALID_TARGET + targetNode.getNodeType(),
                            augment.getName(), augment.getLineNumber(),
                            augment.getCharPosition(), augment.getFileName()));
                }
            } else {
                throw new LinkerException(getErrorMsg(
                        FAILED_TO_LINK, augment.getName(), augment
                                .getLineNumber(), augment.getCharPosition(),
                        augment.getFileName()));
            }
        } else if (entityToResolve instanceof YangCompilerAnnotation) {
            YangNode targetNode;
            YangCompilerAnnotation ca = (YangCompilerAnnotation) entityToResolve;
            targetNode = xPathLinker.processXpathLinking(ca.getAtomicPathList(),
                                                         (YangNode) root,
                                                         AUGMENT_LINKING);
            if (targetNode != null) {
                if (targetNode instanceof YangList) {
                    ((YangList) targetNode).setCompilerAnnotation(
                            (YangCompilerAnnotation) entityToResolve);
                    Resolvable resolvable = (Resolvable) entityToResolve;
                    resolvable.setResolvableStatus(RESOLVED);
                } else {
                    throw new LinkerException(getErrorMsg(
                            INVALID_TARGET + targetNode.getNodeType(), ca.getPath(),
                            ca.getLineNumber(), ca.getCharPosition(), ca.getFileName()));
                }
            } else {
                throw new LinkerException(getErrorMsg(
                        FAILED_TO_FIND_ANNOTATION, ca.getPath(), ca.getLineNumber(),
                        ca.getCharPosition(), ca.getFileName()));
            }
        } else if (entityToResolve instanceof YangLeafRef) {
            YangLeafRef leafRef = (YangLeafRef) entityToResolve;
            Object target = xPathLinker.processLeafRefXpathLinking(
                    leafRef.getAtomicPath(), (YangNode) root, leafRef, LEAF_REF_LINKING);
            if (target != null) {
                YangLeaf leaf;
                YangLeafList leafList;
                leafRef.setReferredLeafOrLeafList(target);
                if (target instanceof YangLeaf) {
                    leaf = (YangLeaf) target;
                    leafRef.setResolvableStatus(INTER_FILE_LINKED);
                    addUnResolvedLeafRefTypeToStack((T) leaf, entityToResolveInfo
                            .getHolderOfEntityToResolve());
                } else {
                    leafList = (YangLeafList) target;
                    leafRef.setResolvableStatus(INTER_FILE_LINKED);
                    addUnResolvedLeafRefTypeToStack(
                            (T) leafList, entityToResolveInfo.getHolderOfEntityToResolve());
                }
                fillPathPredicates(leafRef);
            } else {
                LinkerException ex = new LinkerException(
                        FAILED_TO_FIND_LEAD_INFO_HOLDER + leafRef.getPath());
                ex.setCharPosition(leafRef.getCharPosition());
                ex.setLine(leafRef.getLineNumber());
                ex.setFileName(leafRef.getFileName());
                throw ex;
            }
        } else if (entityToResolve instanceof YangDeviation) {
            YangDeviation deviation = (YangDeviation) entityToResolve;
            List<YangAtomicPath> path = deviation.getTargetNode();
            YangAtomicPath targetPath = path.get(path.size() - 1);
            YangSchemaNode target = findDeviationTarget(entityToResolve, root,
                                                        xPathLinker);

            if (deviation.isDeviateNotSupported()) {
                resolveDeviationNotSupported(target, targetPath);
            } else {
                List<YangDeviateAdd> deviateAddList = deviation.getDeviateAdd();

                if (deviateAddList != null && !deviateAddList.isEmpty()) {
                    resolveDeviateAdd(deviateAddList, target, targetPath);
                }

                List<YangDeviateDelete> deviateDeleteList = deviation
                        .getDeviateDelete();
                if (deviateDeleteList != null && !deviateDeleteList.isEmpty()) {
                    resolveDeviateDelete(deviateDeleteList, target, targetPath);
                }

                List<YangDeviateReplace> deviateReplaceList = deviation
                        .getDeviateReplace();
                if (deviateReplaceList != null && !deviateReplaceList.isEmpty()) {
                    resolveDeviateReplace(deviateReplaceList, target, targetPath);
                }
            }

            Resolvable resolvable = (Resolvable) entityToResolve;
            resolvable.setResolvableStatus(RESOLVED);
        } else if (entityToResolve instanceof YangAugment) {
            resolveUsesAugment(entityToResolve, node);
        }
    }

    private void resolveUsesAugment(T entity, YangNode node) {
        YangXpathLinker<T> linker = new YangXpathLinker<T>();
        YangAugment aug = (YangAugment) entity;
        YangNode tgt = linker.processUsesAugLinking(aug.getTargetNode(),
                                                    (YangUses) node);
        if (tgt != null) {
            if (tgt instanceof YangAugmentableNode) {
                //TODO: collision detection
                ((YangAugmentableNode) tgt).addAugmentation(aug);
                aug.setAugmentedNode(tgt);
                setAugmentedFlagInAncestors(tgt);
                Resolvable resolvable = (Resolvable) entity;
                resolvable.setResolvableStatus(RESOLVED);
                if (tgt instanceof YangInput) {
                    linker.addInModuleIfInput(aug, node);
                }
            } else {
                throw new LinkerException(getErrorMsg(
                        INVALID_TARGET + tgt.getNodeType(),
                        aug.getName(), aug.getLineNumber(),
                        aug.getCharPosition(), aug.getFileName()));
            }
        }
    }

    /**
     * Adds deviate add sub-statements to deviation target node.
     *
     * @param deviateAddList list of deviate add
     * @param target         deviation target node
     * @param targetPath     deviation target's last node
     * @throws DataModelException if there is a data model error
     */
    private void resolveDeviateAdd(List<YangDeviateAdd> deviateAddList,
                                   YangSchemaNode target,
                                   YangAtomicPath targetPath)
            throws DataModelException {
        for (YangDeviateAdd deviate : deviateAddList) {
            if (target.getName().equals(targetPath.getNodeIdentifier().getName())) {
                updateDeviateAddToTargetNode(target, deviate);
            } else {
                YangSchemaNode leaf = findLeafNode((YangLeavesHolder) target, targetPath
                        .getNodeIdentifier().getName());
                updateDeviateAddToTargetNode(leaf, deviate);
            }
        }
    }

    /**
     * Removes deviate delete sub-statements from deviation target node.
     *
     * @param deviateDeleteList list of deviate delete
     * @param target            deviation target node
     * @param targetPath        deviation target's last node
     * @throws DataModelException if there is a data model error
     */
    private void resolveDeviateDelete(List<YangDeviateDelete>
                                              deviateDeleteList,
                                      YangSchemaNode target,
                                      YangAtomicPath targetPath)
            throws DataModelException {
        for (YangDeviateDelete deviate : deviateDeleteList) {
            if (target.getName().equals(targetPath.getNodeIdentifier().getName())) {
                updateDeviateDeleteToTargetNode(target, deviate);
            } else {
                YangSchemaNode leaf = findLeafNode((YangLeavesHolder) target, targetPath
                        .getNodeIdentifier().getName());
                updateDeviateDeleteToTargetNode(leaf, deviate);
            }
        }
    }

    /**
     * Replaces deviate replace sub-statements from deviation target node.
     *
     * @param deviateReplaceList list of deviate replace
     * @param target             deviation target node
     * @param targetPath         deviation target's last node
     * @throws DataModelException if there is a data model error
     */
    private void resolveDeviateReplace(List<YangDeviateReplace>
                                               deviateReplaceList,
                                       YangSchemaNode target,
                                       YangAtomicPath targetPath)
            throws DataModelException {
        for (YangDeviateReplace deviate : deviateReplaceList) {
            if (target.getName().equals(targetPath.getNodeIdentifier().getName())) {
                updateDeviateReplaceToTargetNode(target, deviate);
            } else {
                YangSchemaNode leaf = findLeafNode((YangLeavesHolder) target, targetPath
                        .getNodeIdentifier().getName());
                updateDeviateReplaceToTargetNode(leaf, deviate);
            }
        }
    }

    /**
     * Removes deviation target node from cloned data model.
     *
     * @param target     deviation target node
     * @param targetPath deviation target's last node
     */
    private void resolveDeviationNotSupported(YangSchemaNode target,
                                              YangAtomicPath targetPath) {
        if (target.getName().equals(targetPath.getNodeIdentifier().getName())) {
            deleteUnsupportedNodeFromTree((YangNode) target);
        } else {
            deleteUnsupportedLeafOrLeafList((YangLeavesHolder) target,
                                            targetPath.getNodeIdentifier().getName());
        }
    }

    /**
     * Returns the cloned node of deviation target node.
     *
     * @param entityToResolve entity to resolve
     * @param root            root node
     * @param xPathLinker     xpath Linker
     * @throws DataModelException if there is a data model error
     */
    private YangSchemaNode findDeviationTarget(T entityToResolve,
                                               YangReferenceResolver root,
                                               YangXpathLinker<T> xPathLinker)
            throws DataModelException {

        YangNode targetNode;
        YangDeviation deviation = (YangDeviation) entityToResolve;
        List<YangAtomicPath> path = deviation.getTargetNode();
        targetNode = xPathLinker.processXpathLinking(path, (YangNode) root,
                                                     DEVIATION_LINKING);

        if (targetNode != null) {
            YangNode clonedNode = cloneDeviatedModuleNode(targetNode, deviation);
            return xPathLinker.parsePath(clonedNode);
        } else {
            throw new LinkerException(getErrorMsg(
                    FAILED_TO_FIND_DEVIATION, deviation.getName(),
                    deviation.getLineNumber(), deviation.getCharPosition(),
                    deviation.getFileName()));
        }
    }

    /**
     * Returns the cloned node of deviation target node.
     *
     * @param targetNode deviation target node
     * @param deviation  YANG deviation node
     * @throws DataModelException if there is a data model error
     */
    private YangNode cloneDeviatedModuleNode(YangNode targetNode,
                                             YangDeviation deviation)
            throws DataModelException {

        // get Root node of target schema
        while (targetNode.getParent() != null) {
            targetNode = targetNode.getParent();
        }
        YangNode srcNode = targetNode;
        YangNode dstNode = deviation.getParent();

        if (((YangDeviationHolder) dstNode).isDeviatedNodeCloned()) {
            // Target Node is already cloned, no need to clone again
            return dstNode;
        }

        // clone leaf and leaf-list of root level
        YangLeavesHolder destLeafHolder = (YangLeavesHolder) dstNode;
        YangLeavesHolder srcLeafHolder = (YangLeavesHolder) srcNode;
        if (srcLeafHolder.getListOfLeaf() != null) {
            for (YangLeaf leaf : srcLeafHolder.getListOfLeaf()) {
                YangLeaf clonedLeaf;
                try {
                    ((CollisionDetector) dstNode)
                            .detectCollidingChild(leaf.getName(), LEAF_DATA);
                    clonedLeaf = leaf.cloneForDeviation();
                    clonedLeaf.setReferredLeaf(leaf);
                } catch (CloneNotSupportedException | DataModelException e) {
                    throw new DataModelException(e.getMessage());
                }

                clonedLeaf.setContainedIn(destLeafHolder);
                destLeafHolder.addLeaf(clonedLeaf);
            }
        }
        if (srcLeafHolder.getListOfLeafList() != null) {
            for (YangLeafList leafList : srcLeafHolder.getListOfLeafList()) {
                YangLeafList clonedLeafList;
                try {
                    ((CollisionDetector) destLeafHolder)
                            .detectCollidingChild(leafList.getName(), LEAF_LIST_DATA);
                    clonedLeafList = leafList.cloneForDeviation();
                    clonedLeafList.setReferredSchemaLeafList(leafList);
                } catch (CloneNotSupportedException | DataModelException e) {
                    throw new DataModelException(e.getMessage());
                }
                clonedLeafList.setContainedIn(destLeafHolder);
                destLeafHolder.addLeafList(clonedLeafList);
            }
        }

        // clone subtree
        cloneGroupingTree(srcNode, dstNode, null, true);

        /*
         * Cloning of deviated module is done, set isDeviatedNodeCloned
         * flag as true.
         */
        ((YangDeviationHolder) dstNode).setDeviatedNodeCloned(true);
        return dstNode;
    }

    /**
     * Returns the referenced prefix of entity under resolution.
     *
     * @return referenced prefix of entity under resolution
     * @throws DataModelException a violation in data model rule
     */
    private String getRefPrefix()
            throws DataModelException {
        T entity = getCurEntityToResolveFromStack();
        if (entity instanceof YangType) {
            return ((YangType<?>) entity).getPrefix();
        }
        if (entity instanceof YangUses) {
            return ((YangUses) entity).getPrefix();
        }
        if (entity instanceof YangIfFeature) {
            return ((YangIfFeature) entity).getPrefix();
        }
        if (entity instanceof YangBase) {
            return ((YangBase) entity).getBaseIdentifier()
                    .getPrefix();
        }
        if (entity instanceof YangIdentityRef) {
            return ((YangIdentityRef) entity).getPrefix();
        }
        throw new DataModelException(LINKER_ERROR);
    }

    /**
     * Performs inter file linking and resolution.
     *
     * @throws DataModelException a violation in data model rule
     */
    private void linkInterFileAndResolve()
            throws DataModelException {

        while (!partialResolvedStack.isEmpty()) {

            // Current node to resolve, it can be a YANG type or YANG uses.
            T entityToResolve = getCurEntityToResolveFromStack();
            // Check if linking is already done
            if (entityToResolve instanceof Resolvable) {

                Resolvable resolvable = (Resolvable) entityToResolve;
                switch (resolvable.getResolvableStatus()) {
                    case RESOLVED:
                        /*
                         * If the entity is already resolved in the stack, then pop
                         * it and continue with the remaining stack elements to
                         * resolve
                         */
                        partialResolvedStack.pop();
                        break;

                    case INTER_FILE_LINKED:
                        /*
                         * If the top of the stack is already linked then resolve
                         * the references and pop the entity and continue with
                         * remaining stack elements to resolve
                         */
                        resolveTopOfStack();
                        partialResolvedStack.pop();
                        break;

                    case INTRA_FILE_RESOLVED:
                        /*
                         * If the top of the stack is intra file resolved then check
                         * if top of stack is linked, if not link it using
                         * import/include list and push the linked referred entity
                         * to the stack, otherwise only push it to the stack.
                         */
                        linkInterFileTopOfStackRefUpdateStack();
                        break;

                    case UNDEFINED:
                        /*
                         * In case of if-feature resolution, if referred "feature" is not
                         * defined then the resolvable status will be undefined.
                         */
                        partialResolvedStack.pop();
                        break;

                    default:
                        throw new DataModelException(INVALID_LINKER_STATE);
                }
            } else {
                throw new DataModelException(INVALID_RESOLVED_ENTITY);
            }
        }
    }

    /**
     * Links the top of the stack if it's inter-file and update stack.
     *
     * @throws DataModelException data model error
     */
    private void linkInterFileTopOfStackRefUpdateStack()
            throws DataModelException {
        T entity = getCurEntityToResolveFromStack();
        if (entity instanceof YangLeafRef) {
            // When leafref path comes with relative path, it will be converted to absolute path.
            setAbsolutePathFromRelativePathInLeafref(entity);
            processXPathLinking(entity, curRefResolver);
            return;
        }
        /*
         * Obtain the referred node of top of stack entity under resolution
         */
        T refNode = getRefNode();

        /*
         * Check for null for scenario when it's not linked and inter-file
         * linking is required.
         */
        if (refNode == null) {

            /*
             * Check if prefix is null or not, to identify whether to search in
             * import list or include list.
             */
            if (getRefPrefix() != null && !getRefPrefix()
                    .contentEquals(curRefResolver.getPrefix())) {
                if (resolveWithImport()) {
                    return;
                }
            } else {
                if (resolveWithInclude()) {
                    return;
                }
            }

            if (entity instanceof YangIfFeature) {
                ((YangIfFeature) entity).setResolvableStatus(UNDEFINED);
                return;
            }
            // If current entity is still not resolved, then
            // linking/resolution has failed.

            DataModelException ex = new DataModelException(
                    getErrorInfoForLinker(entity));
            ex.setLine(getLineNumber());
            ex.setCharPosition(getCharPosition());
            throw ex;
        } else {
            ((Resolvable) entity).setResolvableStatus(INTER_FILE_LINKED);
            addUnresolvedRecursiveReferenceToStack((YangNode) refNode);
        }
    }

    /**
     * Sets the leafref with absolute path from the relative path.
     *
     * @param resolutionInfo information about the YANG construct which has to be resolved
     * @throws DataModelException a violation of data model rules
     */
    private void setAbsolutePathFromRelativePathInLeafref(T resolutionInfo)
            throws DataModelException {
        if (resolutionInfo instanceof YangLeafRef) {

            YangNode leafParent = ((YangLeafRef) resolutionInfo)
                    .getParentNode();
            YangLeafRef leafref = (YangLeafRef) resolutionInfo;

            // Checks if the leafref has relative path in it.
            if (leafref.getPathType() == RELATIVE_PATH) {
                YangRelativePath relativePath = leafref.getRelativePath();
                List<YangAtomicPath> absoluteInRelative = relativePath.getAtomicPathList();
                int ancestorCount = relativePath.getAncestorNodeCount();

                // Gets the root node from the ancestor count.
                T nodeOrAugmentList =
                        getRootNodeWithAncestorCountForLeafref(ancestorCount, leafParent,
                                                               leafref);
                if (nodeOrAugmentList instanceof YangNode) {
                    StringBuilder name = new StringBuilder();
                    StringBuilder prefix = new StringBuilder();
                    YangNode rootNode = (YangNode) nodeOrAugmentList;
                    // Forms a new absolute path from the relative path
                    while (!(rootNode instanceof YangReferenceResolver)) {
                        name.append(rootNode.getName());
                        prefix.append(SLASH_FOR_STRING).append(name.reverse());
                        name.delete(0, name.length());
                        rootNode = rootNode.getParent();
                        if (rootNode == null) {
                            throw new DataModelException(INVALID_TREE);
                        }
                    }
                    prefix.reverse();
                    fillAbsolutePathValuesInLeafref(leafref, prefix.toString(),
                                                    absoluteInRelative);
                } else {
                    List<String> listOfAugment = (List<String>) nodeOrAugmentList;
                    Iterator<String> listOfAugmentIterator = listOfAugment.listIterator();
                    StringBuilder augment = new StringBuilder(EMPTY_STRING);
                    while (listOfAugmentIterator.hasNext()) {
                        augment.append(SLASH_FOR_STRING)
                                .append(listOfAugmentIterator.next());
                    }
                    fillAbsolutePathValuesInLeafref(leafref, augment.toString(),
                                                    absoluteInRelative);
                }
            }
        }
    }

    /**
     * Fills the absolute path values in the leafref from relative path.
     *
     * @param leafref  instance of YANG leafref
     * @param path     path name which has to be prefixed to relative path
     * @param relative atomic paths in relative
     * @throws DataModelException a violation of data model rules
     */
    private void fillAbsolutePathValuesInLeafref(YangLeafRef leafref, String path,
                                                 List<YangAtomicPath> relative)
            throws DataModelException {
        leafref.setPathType(ABSOLUTE_PATH);
        String[] pathName = new String[0];
        if (path != null && !path.equals(EMPTY_STRING)) {
            pathName = path.split(SLASH_FOR_STRING);
        }
        List<YangAtomicPath> finalListForAbsolute = new LinkedList<>();
        for (String value : pathName) {
            if (value != null && !value.isEmpty() && !value.equals(EMPTY_STRING)) {
                YangNodeIdentifier nodeId = getValidNodeIdentifier(value, PATH_DATA);
                YangAtomicPath atomicPath = new YangAtomicPath();
                atomicPath.setNodeIdentifier(nodeId);
                finalListForAbsolute.add(atomicPath);
            }
        }
        if (relative != null && !relative.isEmpty()) {
            Iterator<YangAtomicPath> pathIt = relative.listIterator();
            while (pathIt.hasNext()) {
                YangAtomicPath yangAtomicPath = pathIt.next();
                finalListForAbsolute.add(yangAtomicPath);
            }
            leafref.setAtomicPath(finalListForAbsolute);
        } else {
            DataModelException ex = new DataModelException(getLeafRefErrorInfo(leafref));
            ex.setCharPosition(leafref.getCharPosition());
            ex.setLine(leafref.getLineNumber());
            ex.setFileName(leafref.getFileName());
            throw ex;
        }
    }

    /**
     * Returns the root parent with respect to the ancestor count from leafref.
     *
     * @param ancestorCount count of node where parent node can be reached
     * @param curParent     current parent node
     * @param leafref       instance of YANG leafref
     * @return node where the ancestor count stops or augment path name list
     * @throws DataModelException a violation of data model rules
     */
    private T getRootNodeWithAncestorCountForLeafref(
            int ancestorCount, YangNode curParent, YangLeafRef leafref)
            throws DataModelException {

        int curParentCount = 1;
        curParent = skipInvalidDataNodes(curParent, leafref);
        if (curParent instanceof YangAugment) {
            YangAugment augment = (YangAugment) curParent;
            Object valueInAugment = getPathWithAugment(augment,
                                                       ancestorCount - curParentCount);
            return (T) valueInAugment;
        } else {
            while (curParentCount < ancestorCount) {
                YangNode currentSkippedParent = skipInvalidDataNodes(curParent, leafref);
                if (currentSkippedParent == curParent) {
                    if (curParent.getParent() == null) {
                        throw new DataModelException(getLeafRefErrorInfo(leafref));
                    }
                    curParent = curParent.getParent();
                } else {
                    curParent = currentSkippedParent;
                    continue;
                }
                curParentCount = curParentCount + 1;
                if (curParent instanceof YangAugment) {
                    YangAugment augment = (YangAugment) curParent;
                    Object valueInAugment = getPathWithAugment(
                            augment, ancestorCount - curParentCount);
                    return (T) valueInAugment;
                }
            }
        }
        return (T) curParent;
    }

    /**
     * Finds and resolves with include list.
     *
     * @return true if resolved, false otherwise
     * @throws DataModelException a violation in data model rule
     */
    private boolean resolveWithInclude() throws DataModelException {
        /*
         * Run through all the nodes in include list and search for referred
         * typedef/grouping at the root level.
         */
        for (YangInclude yangInclude : curRefResolver.getIncludeList()) {
            YangNode inc = yangInclude.getIncludedNode();
            YangNode linkedNode = getLinkedNode(inc);
            if (linkedNode == null) {
                linkedNode = getFromIncludeList(inc);
            }
            if (linkedNode != null) {
                return addUnResolvedRefToStack(linkedNode);
            }
        }
        // If referred node can't be found return false.
        return false;
    }

    /**
     * Finds and resolves with import list.
     *
     * @return true if resolved, false otherwise
     * @throws DataModelException a violation in data model rule
     */
    private boolean resolveWithImport() throws DataModelException {

        // Run through import list to find the referred typedef/grouping.
        for (YangImport yangImport : curRefResolver.getImportList()) {
            /*
             * Match the prefix attached to entity under resolution with the
             * imported/included module/sub-module's prefix. If found, search
             * for the referred typedef/grouping at the root level.
             */
            if (yangImport.getPrefixId().contentEquals(getRefPrefix())) {
                YangNode inc = yangImport.getImportedNode();
                YangNode linkedNode = getLinkedNode(inc);
                if (linkedNode == null) {
                    linkedNode = getFromIncludeList(inc);
                }
                if (linkedNode != null) {
                    return addUnResolvedRefToStack(linkedNode);
                }
                /*
                 * If referred node can't be found at root level break for loop,
                 * and return false.
                 */
                break;
            }
        }
        // If referred node can't be found return false.
        return false;
    }

    /**
     * Returns the referred node, by finding in the list of included nodes,
     * inside the given node.
     *
     * @param node YANG node
     * @return referred node inside included node
     */
    private YangNode getFromIncludeList(YangNode node) {
        List<YangInclude> incList = ((YangReferenceResolver) node)
                .getIncludeList();
        YangNode refNode = null;
        if (incList != null && !incList.isEmpty()) {
            for (YangInclude inc : incList) {
                refNode = getLinkedNode(inc.getIncludedNode());
                if (refNode != null) {
                    break;
                }
            }
        }
        return refNode;
    }

    //Add unresolved constructs to stack.
    private boolean addUnResolvedRefToStack(YangNode linkedNode)
            throws DataModelException {
        // Add the link to external entity.
        addReferredEntityLink(linkedNode, INTER_FILE_LINKED);

        // Add the type/uses of referred typedef/grouping to the stack.
        addUnresolvedRecursiveReferenceToStack(linkedNode);
        return true;
    }

    //Returns linked node from entity of stack.
    private YangNode getLinkedNode(YangNode node) {
        T entity = getCurEntityToResolveFromStack();
        if (entity instanceof YangType) {
            return findRefTypedef(node);
        }
        if (entity instanceof YangUses) {
            return findRefGrouping(node);
        }
        if (entity instanceof YangIfFeature) {
            return findRefFeature(node);
        }
        if (entity instanceof YangBase) {
            return findRefIdentity(node);
        }
        if (entity instanceof YangIdentityRef) {
            return findRefIdentityRef(node);
        }
        return null;
    }

    /**
     * Returns referred typedef/grouping node.
     *
     * @return referred typedef/grouping node
     * @throws DataModelException a violation in data model rule
     */
    private T getRefNode() throws DataModelException {
        T entity = getCurEntityToResolveFromStack();
        if (entity instanceof YangType) {
            YangDerivedInfo<?> derivedInfo = (YangDerivedInfo<?>)
                    ((YangType<?>) entity).getDataTypeExtendedInfo();
            return (T) derivedInfo.getReferredTypeDef();
        }
        if (entity instanceof YangUses) {
            return (T) ((YangUses) entity).getRefGroup();
        }
        if (entity instanceof YangIfFeature) {
            return (T) ((YangIfFeature) entity).getReferredFeatureHolder();
        }
        if (entity instanceof YangLeafRef) {
            return (T) ((YangLeafRef) entity).getReferredLeafOrLeafList();
        }
        if (entity instanceof YangBase) {
            return (T) ((YangBase) entity).getReferredIdentity();
        }
        if (entity instanceof YangIdentityRef) {
            return (T) ((YangIdentityRef) entity).getReferredIdentity();
        }
        throw new DataModelException(LINKER_ERROR);
    }

    /**
     * Finds the referred grouping node at the root level of imported/included node.
     *
     * @param refNode module/sub-module node
     * @return referred grouping
     */
    private YangNode findRefGrouping(YangNode refNode) {
        YangNode tmpNode = refNode.getChild();
        while (tmpNode != null) {
            if (tmpNode instanceof YangGrouping) {
                if (tmpNode.getName()
                        .equals(((YangUses) getCurEntityToResolveFromStack())
                                        .getName())) {
                    return tmpNode;
                }
            }
            tmpNode = tmpNode.getNextSibling();
        }
        return null;
    }

    /**
     * Finds the referred feature node at the root level of imported/included node.
     *
     * @param refNode module/sub-module node
     * @return referred feature
     */
    private YangNode findRefFeature(YangNode refNode) {
        T entity = getCurEntityToResolveFromStack();
        YangNodeIdentifier ifFeature = ((YangIfFeature) entity).getName();
        List<YangFeature> featureList = ((YangFeatureHolder) refNode)
                .getFeatureList();
        if (featureList != null && !featureList.isEmpty()) {
            for (YangFeature feature : featureList) {
                if (ifFeature.getName().equals(feature.getName())) {
                    ((YangIfFeature) entity).setReferredFeature(feature);
                    return refNode;
                }
            }
        }
        return null;
    }

    /**
     * Finds the referred typedef node at the root level of imported/included node.
     *
     * @param refNode module/sub-module node
     * @return referred typedef
     */
    private YangNode findRefTypedef(YangNode refNode) {
        YangNode tmpNode = refNode.getChild();
        while (tmpNode != null) {
            if (tmpNode instanceof YangTypeDef) {
                if (tmpNode.getName()
                        .equals(((YangType) getCurEntityToResolveFromStack())
                                        .getDataTypeName())) {
                    return tmpNode;
                }
            }
            tmpNode = tmpNode.getNextSibling();
        }
        return null;
    }

    /**
     * Finds the referred identity node at the root level of imported/included node.
     *
     * @param refNode module/sub-module node
     * @return referred identity
     */
    private YangNode findRefIdentity(YangNode refNode) {
        YangNode tmpNode = refNode.getChild();
        while (tmpNode != null) {
            if (tmpNode instanceof YangIdentity) {
                if (tmpNode.getName()
                        .equals(((YangBase) getCurEntityToResolveFromStack())
                                        .getBaseIdentifier().getName())) {
                    return tmpNode;
                }
            }
            tmpNode = tmpNode.getNextSibling();
        }
        return null;
    }

    /**
     * Finds the referred identity node at the root level of imported/included node.
     *
     * @param refNode module/sub-module node
     * @return referred identity
     */
    private YangNode findRefIdentityRef(YangNode refNode) {
        YangNode tmpNode = refNode.getChild();
        while (tmpNode != null) {
            if (tmpNode instanceof YangIdentity) {
                if (tmpNode.getName()
                        .equals(((YangIdentityRef) getCurEntityToResolveFromStack())
                                        .getBaseIdentity().getName())) {
                    return tmpNode;
                }
            }
            tmpNode = tmpNode.getNextSibling();
        }
        return null;
    }

    /**
     * Sets descendant node augmented flag in ancestors.
     *
     * @param targetNode augmented YANG node
     */
    private void setAugmentedFlagInAncestors(YangNode targetNode) {
        targetNode = targetNode.getParent();
        while (targetNode != null) {
            targetNode.setDescendantNodeAugmented(true);
            targetNode = targetNode.getParent();
        }
    }
}