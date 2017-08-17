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
package org.onosproject.yang.compiler.datamodel;

import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus;
import org.onosproject.yang.compiler.datamodel.utils.YangConstructType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.detectCollidingChildUtil;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.getParentSchemaContext;

/*-
 * Reference RFC 6020.
 *
 * The "augment" statement allows a module or submodule to add to the
 *  schema tree defined in an external module, or the current module and
 *  its submodules, and to add to the nodes from a grouping in a "uses"
 *  statement.  The argument is a string that identifies a node in the
 *  schema tree.  This node is called the augment's target node.  The
 *  target node MUST be either a container, list, choice, case, input,
 *  output, or notification node.  It is augmented with the nodes defined
 *  in the sub-statements that follow the "augment" statement.
 *
 *  The argument string is a schema node identifier.
 *  If the "augment" statement is on the top level in a module or
 *  submodule, the absolute form of a schema node identifier
 *  MUST be used.  If the "augment" statement is a sub-statement to the
 *  "uses" statement, the descendant form MUST be used.
 *
 *  If the target node is a container, list, case, input, output, or
 *  notification node, the "container", "leaf", "list", "leaf-list",
 *  "uses", and "choice" statements can be used within the "augment"
 *  statement.
 *
 *  If the target node is a choice node, the "case" statement, or a case
 *  shorthand statement can be used within the "augment" statement.
 *
 *   If the target node is in another module, then nodes added by the
 *  augmentation MUST NOT be mandatory nodes.
 *
 *  The "augment" statement MUST NOT add multiple nodes with the same
 *  name from the same module to the target node.
 *  The augment's sub-statements
 *
 *                +--------------+---------+-------------+------------------+
 *                | substatement | section | cardinality |data model mapping|
 *                +--------------+---------+-------------+------------------+
 *                | anyxml       | 7.10    | 0..n        |-not supported    |
 *                | case         | 7.9.2   | 0..n        |-child nodes      |
 *                | choice       | 7.9     | 0..n        |-child nodes      |
 *                | container    | 7.5     | 0..n        |-child nodes      |
 *                | description  | 7.19.3  | 0..1        |-string           |
 *                | if-feature   | 7.18.2  | 0..n        |-YangIfFeature    |
 *                | leaf         | 7.6     | 0..n        |-YangLeaf         |
 *                | leaf-list    | 7.7     | 0..n        |-YangLeafList     |
 *                | list         | 7.8     | 0..n        |-child nodes      |
 *                | reference    | 7.19.4  | 0..1        |-String           |
 *                | status       | 7.19.2  | 0..1        |-YangStatus       |
 *                | uses         | 7.12    | 0..n        |-child nodes      |
 *                | when         | 7.19.5  | 0..1        |-YangWhen         |
 *                +--------------+---------+-------------+------------------+
 */

/**
 * Representation of data model node to maintain information defined in YANG
 * augment.
 */
public abstract class YangAugment
        extends YangNode
        implements YangLeavesHolder, YangCommonInfo, Parsable,
        CollisionDetector, Resolvable, YangXPathResolver, YangWhenHolder,
        YangIfFeatureHolder {

    private static final long serialVersionUID = 806201602L;

    /**
     * Description of augment.
     */
    private String description;

    /**
     * List of leaves.
     */
    private List<YangLeaf> listOfLeaf;

    /**
     * List of leaf-lists.
     */
    private List<YangLeafList> listOfLeafList;

    /**
     * List of node identifiers.
     */
    private List<YangAtomicPath> targetNode;

    /**
     * Reference of the YANG augment.
     */
    private String reference;

    /**
     * Status of the node.
     */
    private YangStatusType status;

    /**
     * Resolved augmented node.
     */
    private YangNode augmentedNode;

    /**
     * Status of resolution. If completely resolved enum value is "RESOLVED",
     * if not enum value is "UNRESOLVED", in case
     * reference of grouping/typedef is added to uses/type but it's not
     * resolved value of enum should be
     * "INTRA_FILE_RESOLVED".
     */
    private ResolvableStatus resolvableStatus;

    /**
     * When data of the node.
     */
    private YangWhen when;

    /**
     * List of if-feature.
     */
    private List<YangIfFeature> ifFeatureList;

    /**
     * Name of augment when prefix is removed if it is in the same file. For
     * linking purpose the name with same prefix of the file is removed and
     * maintained.
     */
    private String prefixRemovedName;

    /**
     * Name of augment's setter method.
     */
    private String setterMethodName;

    /**
     * Logical node of YANG augment.
     */
    private YangAugment logicalNode;

    /**
     * Create a YANG augment node.
     */
    public YangAugment() {
        super(YangNodeType.AUGMENT_NODE, new HashMap<>());
        listOfLeaf = new LinkedList<>();
        listOfLeafList = new LinkedList<>();
        targetNode = new LinkedList<>();
        ifFeatureList = new LinkedList<>();
        resolvableStatus = ResolvableStatus.UNRESOLVED;
    }

    @Override
    public void addToChildSchemaMap(YangSchemaNodeIdentifier schemaNodeIdentifier,
            YangSchemaNodeContextInfo ctxInfo) throws DataModelException {
        getYsnContextInfoMap().put(schemaNodeIdentifier, ctxInfo);
        YangSchemaNodeContextInfo ctxInfo1 = new YangSchemaNodeContextInfo();
        ctxInfo1.setSchemaNode(ctxInfo.getSchemaNode());
        ctxInfo1.setContextSwitchedNode(this);
        if (getAugmentedNode() != null) {
            if (getAugmentedNode() instanceof YangChoice) {
                ctxInfo1.setContextSwitchedNode(
                        ctxInfo.getContextSwitchedNode());
            }
            getAugmentedNode().addToChildSchemaMap(schemaNodeIdentifier,
                                                   ctxInfo1);
        }
    }

    @Override
    public void setNameSpaceAndAddToParentSchemaMap() {
        // Get parent namespace and set namespace for self node.
        YangNode parent = getParent();
        if (parent instanceof YangUses) {
            parent = parent.getParent();
        }
        setNameSpace(parent.getNameSpace());
        /*
         * Check if node contains leaf/leaf-list, if yes add namespace for leaf
         * and leaf list.
         */
        setLeafNameSpaceAndAddToParentSchemaMap();
    }

    @Override
    public void incrementMandatoryChildCount() {
        // TODO
    }

    @Override
    public void addToDefaultChildMap(
            YangSchemaNodeIdentifier yangSchemaNodeIdentifier,
            YangSchemaNode yangSchemaNode) {
        // TODO
    }

    @Override
    public YangSchemaNodeType getYangSchemaNodeType() {
        /*
         * Augment node to switch the name space in YMS
         */
        return YangSchemaNodeType.YANG_AUGMENT_NODE;
    }

    /**
     * Returns the augmented node.
     *
     * @return the augmented node
     */
    public List<YangAtomicPath> getTargetNode() {
        return targetNode;
    }

    /**
     * Sets the augmented node.
     *
     * @param nodeIdentifiers the augmented node
     */
    public void setTargetNode(List<YangAtomicPath> nodeIdentifiers) {
        targetNode = nodeIdentifiers;
    }

    /**
     * Returns the name of augment after removing the prefix, in each atomic
     * content, which is equal to the root prefix.
     *
     * @return prefix removed augment name
     */
    public String getPrefixRemovedName() {
        return prefixRemovedName;
    }

    /**
     * Sets the name of augment after removing the prefix, in each atomic
     * content, which is equal to the root prefix.
     *
     * @param prefixRemovedName augment name
     */
    public void setPrefixRemovedName(String prefixRemovedName) {
        this.prefixRemovedName = prefixRemovedName;
    }

    /**
     * Returns the when.
     *
     * @return the when
     */
    @Override
    public YangWhen getWhen() {
        return when;
    }

    /**
     * Sets the when.
     *
     * @param when the when to set
     */
    @Override
    public void setWhen(YangWhen when) {
        this.when = when;
    }

    /**
     * Returns the description.
     *
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Set the description.
     *
     * @param description set the description
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void detectCollidingChild(String identifierName,
                                     YangConstructType dataType)
            throws DataModelException {
        // Detect colliding child.
        detectCollidingChildUtil(identifierName, dataType, this);
    }

    @Override
    public void detectSelfCollision(String identifierName,
                                    YangConstructType dataType)
            throws DataModelException {
        if (getName().equals(identifierName)) {
            throw new DataModelException(
                    "YANG file error: Duplicate input identifier detected, " +
                            "same as input \"" +
                            getName() + " in " +
                            getLineNumber() + " at " +
                            getCharPosition() +
                            " in " + getFileName() + "\"");
        }
    }

    /**
     * Returns the list of leaves.
     *
     * @return the list of leaves
     */
    @Override
    public List<YangLeaf> getListOfLeaf() {
        return listOfLeaf;
    }

    /**
     * Sets the list of leaves.
     *
     * @param leafsList the list of leaf to set
     */
    @Override
    public void setListOfLeaf(List<YangLeaf> leafsList) {
        listOfLeaf = leafsList;
    }

    /**
     * Adds a leaf.
     *
     * @param leaf the leaf to be added
     */
    @Override
    public void addLeaf(YangLeaf leaf) {
        listOfLeaf.add(leaf);
    }

    /**
     * Removes a leaf.
     *
     * @param leaf the leaf to be removed
     */
    @Override
    public void removeLeaf(YangLeaf leaf) {
        listOfLeaf.remove(leaf);
    }

    /**
     * Returns the list of leaf-list.
     *
     * @return the list of leaf-list
     */
    @Override
    public List<YangLeafList> getListOfLeafList() {
        return listOfLeafList;
    }

    /**
     * Sets the list of leaf-list.
     *
     * @param listOfLeafList the list of leaf-list to set
     */
    @Override
    public void setListOfLeafList(List<YangLeafList> listOfLeafList) {
        this.listOfLeafList = listOfLeafList;
    }

    /**
     * Adds a leaf-list.
     *
     * @param leafList the leaf-list to be added
     */
    @Override
    public void addLeafList(YangLeafList leafList) {
        listOfLeafList.add(leafList);
    }

    /**
     * Removes a leaf-list.
     *
     * @param leafList the leaf-list to be removed
     */
    @Override
    public void removeLeafList(YangLeafList leafList) {
        listOfLeafList.remove(leafList);
    }

    @Override
    public void setLeafNameSpaceAndAddToParentSchemaMap() {
        // Add namespace for all leafs.
        for (YangLeaf yangLeaf : getListOfLeaf()) {
            yangLeaf.setLeafNameSpaceAndAddToParentSchemaMap(getNameSpace());
        }
        // Add namespace for all leaf list.
        for (YangLeafList yangLeafList : getListOfLeafList()) {
            yangLeafList
                    .setLeafNameSpaceAndAddToParentSchemaMap(getNameSpace());
        }
    }

    @Override
    public void setLeafParentContext() {
        // Add parent context for all leafs.
        for (YangLeaf yangLeaf : getListOfLeaf()) {
            yangLeaf.setParentContext(getParentSchemaContext(this));
        }
        // Add parent context for all leaf list.
        for (YangLeafList yangLeafList : getListOfLeafList()) {
            yangLeafList.setParentContext(getParentSchemaContext(this));
        }
    }

    /**
     * Returns the textual reference.
     *
     * @return the reference
     */
    @Override
    public String getReference() {
        return reference;
    }

    /**
     * Sets the textual reference.
     *
     * @param reference the reference to set
     */
    @Override
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Returns the status.
     *
     * @return the status
     */
    @Override
    public YangStatusType getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    @Override
    public void setStatus(YangStatusType status) {
        this.status = status;
    }

    /**
     * Returns the type of the data as belongs-to.
     *
     * @return returns AUGMENT_DATA
     */
    @Override
    public YangConstructType getYangConstructType() {
        return YangConstructType.AUGMENT_DATA;
    }

    /**
     * Validates the data on entering the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnEntry()
            throws DataModelException {
        // TODO auto-generated method stub, to be implemented by parser
    }

    /**
     * Validates the data on exiting the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnExit()
            throws DataModelException {
        // TODO auto-generated method stub, to be implemented by parser
    }

    /**
     * Returns augmented node.
     *
     * @return augmented node
     */
    public YangNode getAugmentedNode() {
        return augmentedNode;
    }

    /**
     * Sets augmented node.
     *
     * @param augmentedNode augmented node
     */
    public void setAugmentedNode(YangNode augmentedNode) {
        this.augmentedNode = augmentedNode;
    }

    @Override
    public List<YangIfFeature> getIfFeatureList() {
        return ifFeatureList;
    }

    @Override
    public void setIfFeatureList(List<YangIfFeature> ifFeatureList) {
        this.ifFeatureList = ifFeatureList;
    }

    @Override
    public void addIfFeatureList(YangIfFeature ifFeature) {
        if (getIfFeatureList() == null) {
            setIfFeatureList(new LinkedList<>());
        }
        getIfFeatureList().add(ifFeature);
    }

    @Override
    public ResolvableStatus getResolvableStatus() {
        return resolvableStatus;
    }

    @Override
    public void setResolvableStatus(ResolvableStatus resolvableStatus) {
        this.resolvableStatus = resolvableStatus;
    }

    @Override
    public Object resolve()
            throws DataModelException {
        // Resolving of target node is being done in XPathLinker.
        return null;
    }

    /**
     * Returns setter method name for augment in application.
     *
     * @return setter method name for augment in application
     */
    public String getSetterMethodName() {
        return setterMethodName;
    }

    /**
     * Sets setter method name for augment in application.
     *
     * @param name setter method name for augment in application
     */
    public void setSetterMethodName(String name) {
        setterMethodName = name;
    }

    /**
     * Returns the logical YANG augment node.
     *
     * @return logical YANG augment
     */
    public YangAugment getLogicalNode() {
        return logicalNode;
    }

    /**
     * Sets the logical YANG augment node.
     *
     * @param logicalNode logical YANG augment
     */
    public void setLogicalNode(YangAugment logicalNode) {
        this.logicalNode = logicalNode;
    }
}
