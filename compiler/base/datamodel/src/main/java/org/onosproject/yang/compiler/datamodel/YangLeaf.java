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
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.LeafContextUtil;
import org.onosproject.yang.model.DataNode.Type;
import org.onosproject.yang.model.LeafObjectType;
import org.onosproject.yang.model.LeafRestriction;
import org.onosproject.yang.model.LeafSchemaContext;
import org.onosproject.yang.model.LeafType;
import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.model.YangNamespace;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.onosproject.yang.compiler.datamodel.YangStatusType.CURRENT;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.E_INVALID;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.getLeafTypeByDataType;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.validateEmptyDataType;

/*
 * Reference:RFC 6020.
 *  The "leaf" statement is used to define a leaf node in the schema
 *  tree.  It takes one argument, which is an identifier, followed by a
 *  block of sub-statements that holds detailed leaf information.
 *
 *  A leaf node has a value, but no child nodes in the data tree.
 *  Conceptually, the value in the data tree is always in the canonical
 *  form.
 *
 *  A leaf node exists in zero or one instances in the data tree.
 *
 *  The "leaf" statement is used to define a scalar variable of a
 *  particular built-in or derived type.
 *
 * The leaf's sub-statements
 *
 *       +--------------+---------+-------------+------------------+
 *       | substatement | section | cardinality |data model mapping|
 *       +--------------+---------+-------------+------------------+
 *       | config       | 7.19.1  | 0..1        | - boolean        |
 *       | default      | 7.6.4   | 0..1        | - string         |
 *       | description  | 7.19.3  | 0..1        | - string         |
 *       | if-feature   | 7.18.2  | 0..n        | - YangIfFeature  |
 *       | mandatory    | 7.6.5   | 0..1        | - boolean        |
 *       | must         | 7.5.3   | 0..n        | - YangMust       |
 *       | reference    | 7.19.4  | 0..1        | - string         |
 *       | status       | 7.19.2  | 0..1        | - YangStatus     |
 *       | type         | 7.6.3   | 1           | - YangType       |
 *       | units        | 7.3.3   | 0..1        | - String         |
 *       | when         | 7.19.5  | 0..1        | - YangWhen       |
 *       +--------------+---------+-------------+------------------+
 */

/**
 * Represents leaf data represented in YANG.
 */
public abstract class YangLeaf extends DefaultLocationInfo
        implements YangCommonInfo, Parsable, Cloneable, Serializable,
        YangMustHolder, YangIfFeatureHolder, YangWhenHolder, YangSchemaNode,
        YangConfig, YangUnits, YangDefault, YangMandatory, LeafSchemaContext,
        SchemaDataNode, DefaultDenyWriteExtension, DefaultDenyAllExtension {

    private static final long serialVersionUID = 806201635L;

    /**
     * YANG schema node identifier.
     */
    private YangSchemaNodeIdentifier yangSchemaNodeIdentifier;

    /**
     * If the leaf is a config parameter.
     */
    private boolean isConfig;

    /**
     * description of leaf.
     */
    private String description;

    /**
     * If mandatory leaf.
     */
    private boolean isMandatory;

    /**
     * The textual reference to this leaf.
     */
    private String reference;

    /**
     * Status of leaf in YANG definition.
     */
    private YangStatusType status = CURRENT;

    /**
     * Textual units info.
     */
    private String units;

    /**
     * Data type of the leaf.
     */
    private YangType<?> dataType;

    /**
     * Default value in string, needs to be converted to the target object,
     * based on the type.
     */
    private String defaultValueInString;

    /**
     * When data of the leaf.
     */
    private YangWhen when;

    /**
     * YANG Node in which the leaf is contained.
     */
    private YangLeavesHolder containedIn;

    /**
     * List of must statement constraints.
     */
    private List<YangMust> mustConstraintList;

    /**
     * List of if-feature.
     */
    private List<YangIfFeature> ifFeatureList;

    /**
     * Referred schema leaf.
     */
    private YangLeaf referredLeaf;

    private boolean isKeyLeaf;

    /**
     * Parent context of data node.
     */
    private SchemaContext parentContext;

    /**
     * References the extension default-deny-write.
     */
    private boolean defaultDenyWrite;

    /**
     * References the extension default-deny-all.
     */
    private boolean defaultDenyAll;

    /**
     * Creates a YANG leaf.
     */
    public YangLeaf() {
        mustConstraintList = new LinkedList<>();
        ifFeatureList = new LinkedList<>();
    }

    /**
     * Returns the config flag.
     *
     * @return if config flag
     */
    @Override
    public boolean isConfig() {
        return isConfig;
    }

    /**
     * Sets the config flag.
     *
     * @param isConfig the flag value to set
     */
    @Override
    public void setConfig(boolean isConfig) {
        this.isConfig = isConfig;
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
     * Sets the description.
     *
     * @param description set the description
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns if the leaf is mandatory.
     *
     * @return if leaf is mandatory
     */
    @Override
    public boolean isMandatory() {
        return isMandatory;
    }

    /**
     * Sets if the leaf is mandatory.
     *
     * @param isReq if the leaf is mandatory
     */
    @Override
    public void setMandatory(boolean isReq) {
        isMandatory = isReq;
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
     * Returns the units.
     *
     * @return the units
     */
    @Override
    public String getUnits() {
        return units;
    }

    /**
     * Sets the units.
     *
     * @param units the units to set
     */
    @Override
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * Returns the default value.
     *
     * @return the default value
     */
    @Override
    public String getDefaultValueInString() {
        return defaultValueInString;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValueInString the default value
     */
    @Override
    public void setDefaultValueInString(String defaultValueInString) {
        this.defaultValueInString = defaultValueInString;
    }

    /**
     * Returns the data type.
     *
     * @return the data type
     */
    public YangType<?> getDataType() {
        return dataType;
    }

    /**
     * Sets the data type.
     *
     * @param dataType the data type to set
     */
    public void setDataType(YangType<?> dataType) {
        this.dataType = dataType;
    }

    @Override
    public boolean isEmptyDataType() {
        return validateEmptyDataType(dataType);
    }

    /**
     * Retrieves the YANG node in which the leaf is defined.
     *
     * @return the YANG node in which the leaf is defined
     */
    public YangLeavesHolder getContainedIn() {
        return containedIn;
    }

    /**
     * Assigns the YANG node in which the leaf is defined.
     *
     * @param containedIn the YANG node in which the leaf is defined
     */
    public void setContainedIn(YangLeavesHolder containedIn) {
        this.containedIn = containedIn;
    }

    @Override
    public YangLeaf clone()
            throws CloneNotSupportedException {
        YangLeaf cl = (YangLeaf) super.clone();
        cl.yangSchemaNodeIdentifier = yangSchemaNodeIdentifier.clone();
        return cl;
    }

    /**
     * Returns the cloned leaf.
     *
     * @return returns cloned leaf
     * @throws CloneNotSupportedException if the object's class does not
     *                                    support the interface
     */
    public YangLeaf cloneForDeviation()
            throws CloneNotSupportedException {
        YangLeaf cl = (YangLeaf) super.clone();
        return cl;
    }

    @Override
    public boolean getDefaultDenyWrite() {
        return defaultDenyWrite;
    }

    @Override
    public void setDefaultDenyWrite(boolean defaultDenyWrite) {
        this.defaultDenyWrite = defaultDenyWrite;
    }

    @Override
    public boolean getDefaultDenyAll() {
        return defaultDenyAll;
    }

    @Override
    public void setDefaultDenyAll(boolean defaultDenyAll) {
        this.defaultDenyAll = defaultDenyAll;
    }

    /**
     * Returns the type of the parsed data.
     *
     * @return returns LEAF_DATA
     */
    @Override
    public YangConstructType getYangConstructType() {
        return YangConstructType.LEAF_DATA;
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
        if (defaultValueInString != null && !defaultValueInString.isEmpty()
                && dataType != null) {
            dataType.isValidValue(defaultValueInString);
        }
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
    public void addMust(YangMust must) {
        if (getListOfMust() == null) {
            setListOfMust(new LinkedList<>());
        }
        getListOfMust().add(must);
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
    public YangSchemaNodeContextInfo getChildSchema(
            YangSchemaNodeIdentifier dataNodeIdentifier)
            throws DataModelException {
        throw new DataModelException("leaf cannot have any child schema nodes " +
                                             getName() + " in " +
                                             getLineNumber() + " at " +
                                             getCharPosition() +
                                             " in " + getFileName() + "\"");
    }

    @Override
    public void isValueValid(String value)
            throws DataModelException {
        getDataType().isValidValue(value);
    }

    @Override
    public int getMandatoryChildCount()
            throws DataModelException {
        throw new DataModelException("Leaf can't have child. " +
                                             getName() + " in " +
                                             getLineNumber() + " at " +
                                             getCharPosition() +
                                             " in " + getFileName() + "\"");
    }

    @Override
    public Map<YangSchemaNodeIdentifier, YangSchemaNode> getDefaultChild(YangSchemaNodeIdentifier dataNodeIdentifier) {
        // Returns null as there is no child to leaf.
        return null;
    }

    @Override
    public boolean isNotificationPresent() throws DataModelException {
        throw new DataModelException("Method is called for node other than module/sub-module.");
    }

    @Override
    public boolean isRpcPresent() throws DataModelException {
        throw new DataModelException("Method is called for node other than module/sub-module.");
    }

    @Override
    public YangSchemaNodeType getYangSchemaNodeType() {
        return YangSchemaNodeType.YANG_SINGLE_INSTANCE_LEAF_NODE;
    }

    /**
     * Sets leaf namespace and add itself to parent child schema map.
     *
     * @param nameSpace namespace
     */
    public void setLeafNameSpaceAndAddToParentSchemaMap(YangNamespace nameSpace) {
        setNameSpace(nameSpace);
        // Process addition of leaf to schema node map.
        ((YangNode) getContainedIn()).processAdditionOfSchemaNodeToCurNodeMap(getName(), getNameSpace(), this);
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
    public void setYangSchemaNodeIdentifier(YangSchemaNodeIdentifier
                                                    yangSchemaNodeIdentifier) {
        if (this.yangSchemaNodeIdentifier == null) {
            this.yangSchemaNodeIdentifier = new YangSchemaNodeIdentifier();
        }
        this.yangSchemaNodeIdentifier = yangSchemaNodeIdentifier;
    }

    /**
     * Retrieve the name of leaf.
     *
     * @return leaf name
     */
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

    @Override
    public YangSchemaNode getNotificationSchemaNode(String notificationNameInEnum)
            throws DataModelException {
        throw new DataModelException("Method called for schema node other " +
                                             "then module/sub-module");
    }

    @Override
    public YangLeaf getReferredSchema() {
        return referredLeaf;
    }

    /**
     * Sets referred schema leaf. This is only applicable for grouping.
     *
     * @param leaf referred schema leaf
     */
    public void setReferredLeaf(YangLeaf leaf) {
        referredLeaf = leaf;
    }

    /**
     * Returns true if its a key leaf.
     *
     * @return true if its a key leaf
     */
    public boolean isKeyLeaf() {
        return isKeyLeaf;
    }

    /**
     * Sets true if its a key leaf.
     *
     * @param keyLeaf true if its a key leaf
     */
    public void setKeyLeaf(boolean keyLeaf) {
        isKeyLeaf = keyLeaf;
    }

    @Override
    public Type getType() {
        return Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
    }

    @Override
    public SchemaId getSchemaId() {
        SchemaId schemaId = new SchemaId(getName(), getNameSpace()
                .getModuleNamespace());
        return schemaId;
    }


    @Override
    public LeafObjectType getLeafObjectType() {
        return getLeafTypeByDataType(dataType, dataType.getDataType());
    }

    @Override
    public <T extends LeafRestriction> T getLeafRestrictions() {
        //TODO implementation
        return null;
    }

    @Override
    public Object fromString(String value) {
        return LeafContextUtil.getObject(dataType, value, dataType.getDataType());
    }

    @Override
    public LeafType getLeafType(String v) {
        return LeafContextUtil.getLeafType(dataType, v, dataType.getDataType());
    }

    @Override
    public YangNamespace getValueNamespace(String value) {
        return LeafContextUtil.getValueNamespace(dataType, value,
                                                 dataType.getDataType());
    }

    @Override
    public SchemaContext getParentContext() {
        return parentContext;
    }

    /**
     * Sets the parent context for current context.
     *
     * @param sc schema context
     */
    public void setParentContext(SchemaContext sc) {
        parentContext = sc;
    }

    @Override
    public void setRootContext(SchemaContext context) {
        parentContext = context;
    }

    @Override
    public Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> getYsnContextInfoMap() {
        throw new IllegalArgumentException(E_INVALID);
    }

    @Override
    public YangSchemaNode addSchema(YangSchemaNode containedSchema)
            throws IllegalArgumentException {
        throw new IllegalArgumentException(E_INVALID);
    }
}
