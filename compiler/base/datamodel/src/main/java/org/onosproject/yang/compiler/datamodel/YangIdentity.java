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
import org.onosproject.yang.compiler.datamodel.utils.YangConstructType;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import static org.onosproject.yang.compiler.utils.UtilConstants.IDENTITY;

/*-
 * Reference RFC 6020.
 *
 *  The "identity" statement is used to define a new globally unique,
 *  abstract, and untyped identity.  Its only purpose is to denote its
 *  name, semantics, and existence.  An identity can either be defined
 *  from scratch or derived from a base identity.  The identity's
 *  argument is an identifier that is the name of the identity.  It is
 *  followed by a block of substatements that holds detailed identity
 *  information.
 *
 *  The identity's Substatements
 *
 *                +--------------+---------+-------------+-----------------------+
 *                | substatement | section | cardinality |  data model mapping   |
 *                +--------------+---------+-------------+-----------------------+
 *                | base         | 7.16.2  | 0..1        |  -YangNodeIdentifier  |
 *                | description  | 7.19.3  | 0..1        |  -string              |
 *                | reference    | 7.19.4  | 0..1        |  -string              |
 *                | status       | 7.19.2  | 0..1        |  -YangStatus          |
 *                +--------------+---------+-------------+-----------------------+
 */

/**
 * Represents data model node to maintain information defined in YANG identity.
 */
public abstract class YangIdentity
        extends YangNode
        implements YangCommonInfo, Parsable, Serializable,
        YangTranslatorOperatorNode, ConflictResolveNode {

    private static final long serialVersionUID = 806201691L;

    //Base node of identity.
    private YangBase baseNode;

    //Status of YANG identity.
    private YangStatusType status;

    //Description of YANG identity.
    private String description;

    //YANG reference of the identity.
    private String reference;

    //Status if identity is added to all the parent.
    private boolean isAddedToAllParent;

    /*
     * Identity extend list to contain list of all the direct/indirect derived
     * identities.
     */
    private List<YangIdentity> extendList;

    /**
     * Flag to distinguish name conflict between typedef and identity.
     */
    private boolean nameConflict;

    //Creates a identity type of node.
    public YangIdentity() {
        super(YangNodeType.IDENTITY_NODE, null);
        extendList = new LinkedList<>();
    }

    @Override
    public void addToChildSchemaMap(YangSchemaNodeIdentifier schemaNodeIdentifier,
                                    YangSchemaNodeContextInfo yangSchemaNodeContextInfo)
            throws DataModelException {
        // Do nothing.
    }

    @Override
    public void incrementMandatoryChildCount() {
        // TODO
    }

    @Override
    public void addToDefaultChildMap(YangSchemaNodeIdentifier yangSchemaNodeIdentifier, YangSchemaNode yangSchemaNode) {
        // TODO
    }

    @Override
    public YangSchemaNodeType getYangSchemaNodeType() {
        return YangSchemaNodeType.YANG_NON_DATA_NODE;
    }

    @Override
    public YangStatusType getStatus() {
        return status;
    }

    @Override
    public void setStatus(YangStatusType status) {
        this.status = status;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getReference() {
        return reference;
    }

    @Override
    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public YangConstructType getYangConstructType() {
        return YangConstructType.IDENTITY_DATA;
    }

    @Override
    public void validateDataOnEntry()
            throws DataModelException {
    }

    @Override
    public void validateDataOnExit()
            throws DataModelException {
    }

    /**
     * Returns base node of identity.
     *
     * @return the base node of identity
     */
    public YangBase getBaseNode() {
        return baseNode;
    }

    /**
     * Sets the base node.
     *
     * @param baseNode the base node to set
     */
    public void setBaseNode(YangBase baseNode) {
        this.baseNode = baseNode;
    }

    /**
     * Returns the list of derived identities which extends the identity.
     *
     * @return the list of derived identities which extends the identity
     */
    public List<YangIdentity> getExtendList() {
        return extendList;
    }

    /**
     * Sets the list of derived identity.
     *
     * @param extendList the list of derived identities
     */
    public void setExtendList(List<YangIdentity> extendList) {
        this.extendList = extendList;
    }

    /**
     * Adds a derived identity.
     *
     * @param identity derived identity
     */
    public void addToExtendList(YangIdentity identity) {
        extendList.add(identity);
    }

    @Override
    public String getSuffix() {
        return IDENTITY;
    }

    @Override
    public boolean isNameConflict() {
        return nameConflict;
    }

    @Override
    public void setConflictFlag() {
        nameConflict = true;
    }

    /**
     * Returns if the identity is added to all the parent nodes.
     *
     * @return returns true if added; false otherwise
     */
    public boolean isAddedToAllParent() {
        return isAddedToAllParent;
    }

    /**
     * Sets if the identity is added to all the parent nodes.
     *
     * @param addedToAllParent sets true if added; false otherwise
     */
    public void setAddedToAllParent(boolean addedToAllParent) {
        isAddedToAllParent = addedToAllParent;
    }
}
