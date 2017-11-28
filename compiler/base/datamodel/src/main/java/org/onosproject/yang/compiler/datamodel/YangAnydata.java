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

package org.onosproject.yang.compiler.datamodel;

import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.datamodel.utils.YangConstructType;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.model.SingleInstanceNodeContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.E_ID;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.FMT_NOT_EXIST;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.detectCollidingChildUtil;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.errorMsg;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.getNodeIdFromSchemaId;

/*-
 * Reference RFC 7950.
 *
 * The "anydata" statement defines an interior node in the schema tree.
 * It takes one argument, which is an identifier, followed by a block of
 * substatements that holds detailed anydata information.
 *
 * The "anydata" statement is used to represent an unknown set of nodes
 * that can be modeled with YANG, except anyxml, but for which the data
 * model is not known at module design time.  It is possible, though not
 * required, for the data model for anydata content to become known
 * through protocol signaling or other means that are outside the scope
 * of this document.
 *
 * An example of where anydata can be useful is a list of received
 * notifications where the specific notifications are not known at
 * design time.
 *
 * An anydata node exists in zero or one instance in the data tree.
 * An implementation may or may not know the data model used to model a
 * specific instance of an anydata node.
 *
 * Since the use of anydata limits the manipulation of the content, the
 * "anydata" statement SHOULD NOT be used to define configuration data.
.*
 * The anydata's Substatements
 *
 *               +--------------+---------+-------------+
 *               | substatement | section | cardinality |
 *               +--------------+---------+-------------+
 *               | config       | 7.21.1  | 0..1        |
 *               | description  | 7.21.3  | 0..1        |
 *               | if-feature   | 7.20.2  | 0..n        |
 *               | mandatory    | 7.6.5   | 0..1        |
 *               | must         | 7.5.3   | 0..n        |
 *               | reference    | 7.21.4  | 0..1        |
 *               | status       | 7.21.2  | 0..1        |
 *               | when         | 7.21.5  | 0..1        |
 *               +--------------+---------+-------------+
 */

/**
 * Represents data model node to maintain information defined in YANG anydata.
 */
public abstract class YangAnydata
        extends YangNode
        implements YangConfig, YangIfFeatureHolder, YangMandatory,
        YangMustHolder, YangWhenHolder, YangCommonInfo, Parsable,
        CollisionDetector, YangIsFilterContentNodes,
        SingleInstanceNodeContext, SchemaDataNode {

    private static final long serialVersionUID = -4962764560367658905L;

    /**
     * If anydata maintains config data.
     */
    private boolean isConfig;

    /**
     * Description of anydata.
     */
    private String description;

    /**
     * List of if-feature.
     */
    private List<YangIfFeature> ifFeatureList;

    /**
     * If anydata maintains mandatory data.
     */
    private boolean isMandatory;

    /**
     * List of must statement constraints.
     */
    private List<YangMust> mustConstraintList;

    /**
     * Reference of the module.
     */
    private String reference;

    /**
     * Status of the node.
     */
    private YangStatusType status = YangStatusType.CURRENT;

    /**
     * When data of the node.
     */
    private YangWhen when;

    /**
     * Create a anydata node.
     */
    public YangAnydata() {
        super(YangNodeType.ANYDATA_NODE, new HashMap<>(),
              DataNode.Type.SINGLE_INSTANCE_NODE);
        mustConstraintList = new LinkedList<>();
        ifFeatureList = new LinkedList<>();
    }

    @Override
    public void addToChildSchemaMap(YangSchemaNodeIdentifier schemaNodeIdentifier,
                                    YangSchemaNodeContextInfo yangSchemaNodeContextInfo) {
        getYsnContextInfoMap().put(schemaNodeIdentifier, yangSchemaNodeContextInfo);
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
        return YangSchemaNodeType.YANG_ANYDATA_NODE;
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
     * Returns the config flag.
     *
     * @return the isConfig
     */
    @Override
    public boolean isConfig() {
        return isConfig;
    }

    /**
     * Sets the config flag.
     *
     * @param isConfig the config flag
     */
    @Override
    public void setConfig(boolean isConfig) {
        this.isConfig = isConfig;
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
     * Sets the description.
     *
     * @param description set the description
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
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
     * Returns the type of the data.
     *
     * @return returns ANYDATA_DATA
     */
    @Override
    public YangConstructType getYangConstructType() {
        return YangConstructType.ANYDATA_DATA;
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
        // TODO runtime validation is required.
    }

    @Override
    public void detectCollidingChild(String identifierName,
                                     YangConstructType dataType)
            throws DataModelException {
        // Asks helper to detect colliding child.
        detectCollidingChildUtil(identifierName, dataType, this);
    }

    @Override
    public void detectSelfCollision(String identifierName,
                                    YangConstructType dataType)
            throws DataModelException {
        if (getName().equals(identifierName)) {
            throw new DataModelException(
                    "YANG file error: Duplicate input identifier detected," +
                            " same as container \"" +
                            getName() + " in " +
                            getLineNumber() + " at " +
                            getCharPosition() +
                            " in " + getFileName() + "\"");
        }
    }

    @Override
    public List<YangIfFeature> getIfFeatureList() {
        return ifFeatureList;
    }

    @Override
    public void addIfFeatureList(YangIfFeature ifFeature) {
        ifFeatureList.add(ifFeature);
    }

    @Override
    public void setIfFeatureList(List<YangIfFeature> ifFeatureList) {
        this.ifFeatureList = ifFeatureList;
    }

    @Override
    public List<YangMust> getListOfMust() {
        return mustConstraintList;
    }

    @Override
    public void setListOfMust(List<YangMust> mustConstraintList) {
        this.mustConstraintList = mustConstraintList;
    }

    @Override
    public boolean isMandatory() {
        return isMandatory;
    }

    @Override
    public void setMandatory(boolean isReq) {
        isMandatory = isReq;
    }

    @Override
    public void addMust(YangMust must) {
        mustConstraintList.add(must);
    }

    @Override
    public SchemaContext getChildContext(SchemaId schemaId) {
        checkNotNull(schemaId, E_ID);
        YangSchemaNodeIdentifier id = getNodeIdFromSchemaId(
                schemaId, getNameSpace().getModuleNamespace());
        try {
            YangSchemaNode node = getChildSchema(id).getSchemaNode();
            if (node instanceof SchemaDataNode) {
                return node;
            } else {
                throw new IllegalArgumentException(errorMsg(FMT_NOT_EXIST,
                                                            schemaId.name(),
                                                            getName()));
            }
        } catch (DataModelException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public YangSchemaNode addSchema(YangSchemaNode containedSchema) throws
            IllegalArgumentException {
        YangNode nodeToClone = (YangNode) containedSchema;
        try {
            cloneSubTree(nodeToClone.getParent(), this, null,
                         false, nodeToClone);
        } catch (DataModelException e) {
            throw new IllegalArgumentException(e);
        }
        YangNode child = getChild();
        // Contained Schema Name
        String name = containedSchema.getName();
        while (child.getName() != name) {
            child = child.getNextSibling();
        }
        return child;
    }
}
