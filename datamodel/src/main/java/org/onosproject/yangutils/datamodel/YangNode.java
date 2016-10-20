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
package org.onosproject.yangutils.datamodel;

import org.onosproject.yangutils.datamodel.exceptions.DataModelException;
import org.onosproject.yangutils.datamodel.utils.Parsable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.onosproject.yangutils.datamodel.TraversalType.CHILD;
import static org.onosproject.yangutils.datamodel.TraversalType.PARENT;
import static org.onosproject.yangutils.datamodel.TraversalType.SIBILING;
import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.cloneListOfLeaf;
import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.cloneListOfLeafList;
import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.updateClonedLeavesUnionEnumRef;

/**
 * Represents base class of a node in data model tree.
 */
public abstract class YangNode
        implements Cloneable, Serializable, YangSchemaNode,
        Comparable<YangNode> {

    private static final long serialVersionUID = 806201601L;

    /**
     * YANG schema node identifier.
     */
    private YangSchemaNodeIdentifier yangSchemaNodeIdentifier;

    /**
     * Type of node.
     */
    private YangNodeType nodeType;

    /**
     * Parent reference.
     */
    private YangNode parent;

    /**
     * First child reference.
     */
    private YangNode child;

    /**
     * Next sibling reference.
     */
    private YangNode nextSibling;

    /**
     * Previous sibling reference.
     */
    private YangNode previousSibling;

    /**
     * Priority of the node.
     */
    private int priority;

    /**
     * Flag if the node is for translation.
     */
    private boolean isToTranslate = true;

    private transient int lineNumber;
    private transient int charPosition;
    private String fileName;

    /**
     * Map of YANG context information. It is to be consumed by YMS.
     */
    private Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> ysnContextInfoMap;

    /**
     * Count of mandatory YANG schema nodes.
     */
    private int mandatoryChildCount;

    /**
     * Yang revision.
     */
    private YangRevision revision;

    /**
     * Map of default schema nodes.
     */
    private Map<YangSchemaNodeIdentifier, YangSchemaNode> defaultChildMap;

    /**
     * Flag to check whether any descendant node is augmented.
     */
    private boolean isDescendantNodeAugmented;

    /**
     * Referred schema node, only applicable during grouping.
     */
    private YangNode referredSchemaNode;

    /**
     * Returns the priority of the node.
     *
     * @return priority of the node
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the node.
     *
     * @param priority of the node
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Creates a YANG node object.
     */
    @SuppressWarnings("unused")
    private YangNode() {
    }

    /**
     * Creates a specific type of node.
     *
     * @param type              of YANG node
     * @param ysnContextInfoMap YSN context info map
     */
    protected YangNode(YangNodeType type,
                       Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> ysnContextInfoMap) {
        nodeType = type;
        this.ysnContextInfoMap = ysnContextInfoMap;
    }

    /**
     * Returns true if descendant node is augmented.
     *
     * @return true if descendant node is augmented
     */
    public boolean isDescendantNodeAugmented() {
        return isDescendantNodeAugmented;
    }

    /**
     * Sets true if descendant node is augmented.
     *
     * @param descendantNodeAugmented true if descendant node is augmented.
     */
    public void setDescendantNodeAugmented(boolean descendantNodeAugmented) {
        isDescendantNodeAugmented = descendantNodeAugmented;
    }

    /**
     * Returns the node type.
     *
     * @return node type
     */
    public YangNodeType getNodeType() {
        return nodeType;
    }

    /**
     * Sets the node type.
     *
     * @param nodeType type of node
     */
    private void setNodeType(YangNodeType nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * Returns the parent of node.
     *
     * @return parent of node
     */
    public YangNode getParent() {
        return parent;
    }

    /**
     * Sets the parent of node.
     *
     * @param parent node
     */
    public void setParent(YangNode parent) {
        this.parent = parent;
    }

    /**
     * Returns the first child of node.
     *
     * @return first child of node
     */
    public YangNode getChild() {
        return child;
    }

    /**
     * Sets the first instance of a child node.
     *
     * @param child is only child to be set
     */
    public void setChild(YangNode child) {
        this.child = child;
    }

    /**
     * Returns the next sibling of node.
     *
     * @return next sibling of node
     */
    public YangNode getNextSibling() {
        return nextSibling;
    }

    /**
     * Sets the next sibling of node.
     *
     * @param sibling YANG node
     */
    public void setNextSibling(YangNode sibling) {
        nextSibling = sibling;
    }

    /**
     * Returns the previous sibling.
     *
     * @return previous sibling node
     */
    public YangNode getPreviousSibling() {
        return previousSibling;
    }

    /**
     * Sets the previous sibling.
     *
     * @param previousSibling points to predecessor sibling
     */
    public void setPreviousSibling(YangNode previousSibling) {
        this.previousSibling = previousSibling;
    }

    /**
     * Adds a child node, the children sibling list will be sorted based on node
     * type.
     *
     * @param newChild refers to a child to be added
     * @throws DataModelException due to violation in data model rules
     */
    public void addChild(YangNode newChild)
            throws DataModelException {
        if (newChild.getNodeType() == null) {
            throw new DataModelException("Abstract node cannot be inserted " +
                                                 "into a tree " + getName() +
                                                 " in " + getLineNumber() +
                                                 " at " + getCharPosition() +
                                                 " in " + getFileName() + "\"");
        }

        if (newChild.getParent() == null) {
            newChild.setParent(this);
        } else if (newChild.getParent() != this) {
            throw new DataModelException("Node is already part of a tree " +
                                                 getName() + " in " +
                                                 getLineNumber() + " at " +
                                                 getCharPosition() + " in " +
                                                 getFileName() + "\"");
        }

        if (newChild.getChild() != null) {
            throw new DataModelException("Child to be added is not atomic, " +
                                                 "it already has a child " +
                                                 getName() + " in " +
                                                 getLineNumber() + " at " +
                                                 getCharPosition() + " in " +
                                                 getFileName() + "\"");
        }

        if (newChild.getNextSibling() != null) {
            throw new DataModelException("Child to be added is not atomic, " +
                                                 "it already has a next " +
                                                 "sibling " + getName() +
                                                 " in " + getLineNumber() +
                                                 " at " + getCharPosition() +
                                                 " in " + getFileName() + "\"");
        }

        if (newChild.getPreviousSibling() != null) {
            throw new DataModelException("Child to be added is not atomic, " +
                                                 "it already has a previous " +
                                                 "sibling " + getName() +
                                                 " in " + getLineNumber() +
                                                 " at " + getCharPosition() +
                                                 " in " + getFileName() + "\"");
        }

        /* First child to be added */
        if (getChild() == null) {
            setChild(newChild);
        } else {

            YangNode curNode;
            curNode = getChild();

            // Get the predecessor child of new child
            while (curNode.getNextSibling() != null) {
                curNode = curNode.getNextSibling();
            }

            // If the new node needs to be the last child
            if (curNode.getNextSibling() == null) {
                curNode.setNextSibling(newChild);
                newChild.setPreviousSibling(curNode);
            }
        }
    }

    /**
     * Processes addition of schema node child to parent map.
     *
     * @param name      name of the node
     * @param namespace namespace of the node
     */
    protected void processAdditionOfSchemaNodeToParentMap(String name,
                                                          YangNamespace namespace) {
        processAdditionOfSchemaNodeToMap(name, namespace, this, getParent());
    }

    /**
     * Processes addition of schema node child to parent map.
     *
     * @param name           name of the node
     * @param namespace      namespace of the node
     * @param yangSchemaNode YANG schema node
     */
    public void processAdditionOfSchemaNodeToCurNodeMap(String name,
                                                        YangNamespace namespace,
                                                        YangSchemaNode yangSchemaNode) {
        processAdditionOfSchemaNodeToMap(name, namespace, yangSchemaNode, this);
    }

    /**
     * Processes addition of schema node child to map.
     *
     * @param name                 name of the node
     * @param namespace            namespace of the node
     * @param yangSchemaNode       YANG schema node
     * @param childSchemaMapHolder child schema map holder
     */
    private void processAdditionOfSchemaNodeToMap(String name,
                                                  YangNamespace namespace,
                                                  YangSchemaNode yangSchemaNode,
                                                  YangNode childSchemaMapHolder) {
        // Addition of node to schema node map.
        // Create YANG schema node identifier with child node name.
        YangSchemaNodeIdentifier yangSchemaNodeIdentifier = new YangSchemaNodeIdentifier();
        yangSchemaNodeIdentifier.setName(name);
        yangSchemaNodeIdentifier.setNameSpace(namespace);
        // Create YANG schema node context info and set child node.
        YangSchemaNodeContextInfo yangSchemaNodeContextInfo = new YangSchemaNodeContextInfo();
        yangSchemaNodeContextInfo.setSchemaNode(yangSchemaNode);
        // Invoke parent method to add the created entry.
        try {
            childSchemaMapHolder.addToChildSchemaMap(yangSchemaNodeIdentifier,
                                                     yangSchemaNodeContextInfo);
        } catch (DataModelException e) {
            //TODO
        }
    }

    @Override
    public int compareTo(YangNode otherNode) {
        if (priority == otherNode.getPriority()) {
            return 0;
        }
        return ((Integer) otherNode.getPriority()).compareTo(priority);
    }

    /**
     * Clones the current node contents and create a new node.
     *
     * @param yangUses YANG uses
     * @return cloned node
     * @throws CloneNotSupportedException clone is not supported by the referred
     *                                    node
     */
    public YangNode clone(YangUses yangUses)
            throws CloneNotSupportedException {
        YangNode clonedNode = (YangNode) super.clone();
        clonedNode.referredSchemaNode = this;
        if (clonedNode instanceof YangLeavesHolder) {
            try {
                cloneListOfLeaf((YangLeavesHolder) clonedNode, yangUses);
                cloneListOfLeafList((YangLeavesHolder) clonedNode, yangUses);
            } catch (DataModelException e) {
                throw new CloneNotSupportedException(e.getMessage());
            }
        }

        clonedNode.setParent(null);
        clonedNode.setChild(null);
        clonedNode.setNextSibling(null);
        clonedNode.setPreviousSibling(null);
        clonedNode.yangSchemaNodeIdentifier =
                clonedNode.yangSchemaNodeIdentifier.clone();
        clonedNode.ysnContextInfoMap = new HashMap<>();
        return clonedNode;
    }

    /**
     * Clones the subtree from the specified source node to the mentioned target
     * node. The source and target root node cloning is carried out by the
     * caller.
     *
     * @param srcRootNode source node for sub tree cloning
     * @param dstRootNode destination node where the sub tree needs to be cloned
     * @param yangUses    YANG uses
     * @throws DataModelException data model error
     */
    public static void cloneSubTree(YangNode srcRootNode, YangNode dstRootNode,
                                    YangUses yangUses)
            throws DataModelException {

        YangNode nextNodeToClone = srcRootNode;
        TraversalType curTraversal;

        YangNode clonedTreeCurNode = dstRootNode;
        YangNode newNode = null;

        nextNodeToClone = nextNodeToClone.getChild();
        if (nextNodeToClone == null) {
            return;
        } else {
            /*
             * Root level cloning is taken care in the caller.
             */
            curTraversal = CHILD;
        }

        /*
         * Caller ensures the cloning of the root nodes
         */
        try {
            while (nextNodeToClone != srcRootNode) {
                if (nextNodeToClone == null) {
                    throw new DataModelException("Internal error: Cloning " +
                                                         "failed, source " +
                                                         "tree null pointer " +
                                                         "reached " +
                                                         nextNodeToClone.getName() +
                                                         " in " + nextNodeToClone.getLineNumber() +
                                                         " at " + nextNodeToClone.getCharPosition() +
                                                         " in " + nextNodeToClone.getFileName() + "\"");
                }
                if (curTraversal != PARENT) {
                    newNode = nextNodeToClone.clone(yangUses);
                    detectCollisionWhileCloning(clonedTreeCurNode, newNode,
                                                curTraversal);
                }

                if (curTraversal == CHILD) {

                    /*
                     * add the new node to the cloned tree.
                     */
                    clonedTreeCurNode.addChild(newNode);

                    /*
                     * update the cloned tree's traversal current node as the
                     * new node.
                     */
                    clonedTreeCurNode = newNode;
                } else if (curTraversal == SIBILING) {

                    clonedTreeCurNode.addNextSibling(newNode);
                    clonedTreeCurNode = newNode;
                } else {
                    if (clonedTreeCurNode instanceof YangLeavesHolder) {
                        updateClonedLeavesUnionEnumRef((YangLeavesHolder) clonedTreeCurNode);
                    }
                    clonedTreeCurNode = clonedTreeCurNode.getParent();
                }

                if (curTraversal != PARENT && nextNodeToClone.getChild() != null) {
                    curTraversal = CHILD;

                    /*
                     * update the traversal's current node.
                     */
                    nextNodeToClone = nextNodeToClone.getChild();

                } else if (nextNodeToClone.getNextSibling() != null) {

                    curTraversal = SIBILING;

                    nextNodeToClone = nextNodeToClone.getNextSibling();
                } else {
                    curTraversal = PARENT;
                    nextNodeToClone = nextNodeToClone.getParent();
                }
            }
        } catch (CloneNotSupportedException e) {
            throw new DataModelException("Failed to clone the tree " +
                                                 nextNodeToClone.getName() +
                                                 " in " + nextNodeToClone.getLineNumber() +
                                                 " at " + nextNodeToClone.getCharPosition() +
                                                 " in " + nextNodeToClone.getFileName() + "\"");
        }

    }

    /**
     * Detects collision when the grouping is deep copied to the uses's parent.
     *
     * @param currentNode parent/previous sibling node for the new node
     * @param newNode     node which has to be added
     * @param addAs       traversal type of the node
     * @throws DataModelException data model error
     */
    private static void detectCollisionWhileCloning(YangNode currentNode,
                                                    YangNode newNode,
                                                    TraversalType addAs)
            throws DataModelException {
        if (!(currentNode instanceof CollisionDetector)
                || !(newNode instanceof Parsable)) {
            throw new DataModelException("Node in data model tree does not " +
                                                 "support collision detection " +
                                                 newNode.getName() + " in " +
                                                 newNode.getLineNumber() + " at " +
                                                 newNode.getCharPosition() +
                                                 " in " + newNode.getFileName() + "\"");
        }

        CollisionDetector collisionDetector = (CollisionDetector) currentNode;
        Parsable parsable = (Parsable) newNode;
        if (addAs == TraversalType.CHILD) {
            collisionDetector.detectCollidingChild(newNode.getName(),
                                                   parsable.getYangConstructType());
        } else if (addAs == TraversalType.SIBILING) {
            currentNode = currentNode.getParent();
            if (!(currentNode instanceof CollisionDetector)) {
                throw new DataModelException("Node in data model tree does " +
                                                     "not support collision " +
                                                     "detection" + currentNode.getName() +
                                                     " in " + currentNode.getLineNumber() +
                                                     " at " + currentNode.getCharPosition() +
                                                     " in " + currentNode.getFileName() + "\"");
            }
            collisionDetector = (CollisionDetector) currentNode;
            collisionDetector.detectCollidingChild(newNode.getName(),
                                                   parsable.getYangConstructType());
        } else {
            throw new DataModelException("Error tree cloning " +
                                                 currentNode.getName() + " in" +
                                                 " " + currentNode.getLineNumber() +
                                                 " at " + currentNode.getCharPosition() +
                                                 " in " + currentNode.getFileName() + "\"");
        }

    }

    /**
     * /** Returns true if translation required.
     *
     * @return true if translation required
     */
    public boolean isToTranslate() {
        return isToTranslate;
    }

    /**
     * Sest true if translation required.
     *
     * @param toTranslate true if translation required.
     */
    public void setToTranslate(boolean toTranslate) {
        isToTranslate = toTranslate;
    }

    /**
     * Adds a new next sibling.
     *
     * @param newSibling new sibling to be added
     * @throws DataModelException data model error
     */
    private void addNextSibling(YangNode newSibling)
            throws DataModelException {

        if (newSibling.getNodeType() == null) {
            throw new DataModelException("Cloned abstract node cannot be " +
                                                 "inserted into a tree "
                                                 + getName() + " in " +
                                                 getLineNumber() + " at " +
                                                 getCharPosition()
                                                 + " in " + getFileName() + "\"");
        }

        if (newSibling.getParent() == null) {
            /**
             * Since the siblings needs to have a common parent, set the parent
             * as the current node's parent
             */
            newSibling.setParent(getParent());

        } else {
            throw new DataModelException("Node is already part of a tree, " +
                                                 "and cannot be added as a " +
                                                 "sibling " + getName() + " in " +
                                                 getLineNumber() + " at " +
                                                 getCharPosition() + " in " +
                                                 getFileName() + "\"");
        }

        if (newSibling.getPreviousSibling() == null) {
            newSibling.setPreviousSibling(this);
            setNextSibling(newSibling);
        } else {
            throw new DataModelException("New sibling to be added is not " +
                                                 "atomic, it already has a " +
                                                 "previous sibling " +
                                                 getName() + " in " +
                                                 getLineNumber() + " at " +
                                                 getCharPosition() + " in " +
                                                 getFileName() + "\"");
        }

        if (newSibling.getChild() != null) {
            throw new DataModelException("Sibling to be added is not atomic, " +
                                                 "it already has a child " +
                                                 getName() + " in " +
                                                 getLineNumber() + " at " +
                                                 getCharPosition() + " in " +
                                                 getFileName() + "\"");
        }

        if (newSibling.getNextSibling() != null) {
            throw new DataModelException("Sibling to be added is not atomic, " +
                                                 "it already has a next " +
                                                 "sibling " + getName() +
                                                 " in " + getLineNumber() +
                                                 " at " + getCharPosition() +
                                                 " in " + getFileName() + "\"");
        }
    }

    @Override
    public YangSchemaNodeContextInfo getChildSchema(YangSchemaNodeIdentifier dataNodeIdentifier)
            throws DataModelException {
        YangSchemaNodeContextInfo childSchemaContext =
                ysnContextInfoMap.get(dataNodeIdentifier);
        if (childSchemaContext == null) {
            throw new DataModelException("Requested " +
                                                 dataNodeIdentifier.getName() +
                                                 " is not child in " +
                                                 getName());
        }
        return childSchemaContext;
    }

    @Override
    public int getMandatoryChildCount()
            throws DataModelException {
        return mandatoryChildCount;
    }

    @Override
    public Map<YangSchemaNodeIdentifier, YangSchemaNode> getDefaultChild(YangSchemaNodeIdentifier dataNodeIdentifier) {
        return defaultChildMap;
    }

    @Override
    public boolean isNotificationPresent() throws DataModelException {
        throw new DataModelException("Method is called for node other than module/sub-module.");
    }

    /**
     * Adds child schema in child schema map, this is used to add the schema
     * to the map in case of leaf as a child.
     *
     * @param schemaNodeIdentifier      YANG schema node identifier
     * @param yangSchemaNodeContextInfo YANG data node context information
     * @throws DataModelException a violation in data model rule
     */
    public abstract void addToChildSchemaMap(YangSchemaNodeIdentifier schemaNodeIdentifier,
                                             YangSchemaNodeContextInfo yangSchemaNodeContextInfo)
            throws DataModelException;

    /**
     * Increments mandatory child count.
     */
    public abstract void incrementMandatoryChildCount();

    /**
     * Sets mandatory child count.
     *
     * @param mandatoryChildCount value of mandatory child count
     */
    public void setMandatoryChildCount(int mandatoryChildCount) {
        this.mandatoryChildCount = mandatoryChildCount;
    }

    /**
     * Adds default child information to map.
     *
     * @param yangSchemaNodeIdentifier YANG schema node identifier
     * @param yangSchemaNode           YANG schema node
     */
    public abstract void addToDefaultChildMap(YangSchemaNodeIdentifier yangSchemaNodeIdentifier,
                                              YangSchemaNode yangSchemaNode);

    /**
     * Returns default child map.
     *
     * @return default child map
     */
    public Map<YangSchemaNodeIdentifier, YangSchemaNode> getDefaultChildMap() {
        return defaultChildMap;
    }

    /**
     * Returns YANG schema node context info map.
     *
     * @return YANG schema node context info map
     */
    public Map<YangSchemaNodeIdentifier,
            YangSchemaNodeContextInfo> getYsnContextInfoMap() {
        return ysnContextInfoMap;
    }

    /**
     * Adds namespace for self, next sibling and first child. This is used
     * after obtaining namespace in case of submodule after performing
     * linking.
     */
    public void setNameSpaceAndAddToParentSchemaMap() {
        // Get parent namespace.
        if (getParent() != null) {
            // Get parent namespace and set namespace for self node.
            setNameSpace(getParent().getNameSpace());
            // Process addition of leaf to the child schema map of parent.
            processAdditionOfSchemaNodeToParentMap(getName(), getNameSpace());
        } else {
            // Module/Sub-module
            setNameSpace((YangNamespace) this);
        }
        /*
         * Check if node contains leaf/leaf-list, if yes add namespace for leaf
         * and leaf list.
         */
        if (this instanceof YangLeavesHolder) {
            ((YangLeavesHolder) this).setLeafNameSpaceAndAddToParentSchemaMap();
        }
    }

    /**
     * Sets YSN context info map.
     *
     * @param ysnContextInfoMap YSN context info map
     */
    public void setYsnContextInfoMap(Map<YangSchemaNodeIdentifier,
            YangSchemaNodeContextInfo> ysnContextInfoMap) {
        this.ysnContextInfoMap = ysnContextInfoMap;
    }

    /**
     * Adds to YSN context info map.
     *
     * @param yangSchemaNodeIdentifier  YANG schema node identifier
     * @param yangSchemaNodeContextInfo YANG schema node context info
     */
    public void addToYsnContextInfoMap(YangSchemaNodeIdentifier
                                               yangSchemaNodeIdentifier, YangSchemaNodeContextInfo
                                               yangSchemaNodeContextInfo) {
        getYsnContextInfoMap().put(yangSchemaNodeIdentifier, yangSchemaNodeContextInfo);
    }

    @Override
    public void isValueValid(String value)
            throws DataModelException {
        throw new DataModelException("Value validation asked for YANG node. "
                                             + getName() + " in " +
                                             getLineNumber() + " at " +
                                             getCharPosition()
                                             + " in " + getFileName() + "\"");
    }

    @Override
    public YangSchemaNodeIdentifier getYangSchemaNodeIdentifier() {
        return yangSchemaNodeIdentifier;
    }

    /**
     * Sets YANG schema node identifier.
     *
     * @param yangSchemaNodeIdentifier YANG schema node identifier
     */
    public void setYangSchemaNodeIdentifier(YangSchemaNodeIdentifier yangSchemaNodeIdentifier) {
        if (this.yangSchemaNodeIdentifier == null) {
            this.yangSchemaNodeIdentifier = new YangSchemaNodeIdentifier();
        }
        this.yangSchemaNodeIdentifier = yangSchemaNodeIdentifier;
    }

    @Override
    public String getName() {
        return yangSchemaNodeIdentifier.getName();
    }

    /**
     * Sets name of node.
     *
     * @param name name of the node
     */
    public void setName(String name) {
        if (yangSchemaNodeIdentifier == null) {
            yangSchemaNodeIdentifier = new YangSchemaNodeIdentifier();
        }
        yangSchemaNodeIdentifier.setName(name);
    }

    @Override
    public YangNamespace getNameSpace() {
        return yangSchemaNodeIdentifier.getNameSpace();
    }

    /**
     * Sets namespace of node.
     *
     * @param namespace namespace of the node
     */
    public void setNameSpace(YangNamespace namespace) {
        if (yangSchemaNodeIdentifier == null) {
            yangSchemaNodeIdentifier = new YangSchemaNodeIdentifier();
        }
        yangSchemaNodeIdentifier.setNameSpace(namespace);
    }

    /**
     * Returns YANG revision.
     *
     * @return YANG revision
     */
    public YangRevision getRevision() {
        return revision;
    }

    /**
     * Sets YANG revision.
     *
     * @param revision YANG revision
     */
    public void setRevision(YangRevision revision) {
        this.revision = revision;
    }

    @Override
    public YangSchemaNode getNotificationSchemaNode(String notificationNameInEnum)
            throws DataModelException {
        throw new DataModelException("Method called for schema node other " +
                                             "then module/sub-module");
    }

    @Override
    public YangSchemaNode getReferredSchema() {
        return referredSchemaNode;
    }

    /**
     * Returns true if op type info required for node.
     *
     * @return true if op type info required for node
     */
    public boolean isOpTypeReq() {
        return this instanceof RpcNotificationContainer ||
                !(this instanceof InvalidOpTypeHolder) &&
                        getParent().isOpTypeReq();
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public int getCharPosition() {
        return charPosition;
    }

    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public void setCharPosition(int charPositionInLine) {
        charPosition = charPositionInLine;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String name) {
        fileName = name;
    }
}
