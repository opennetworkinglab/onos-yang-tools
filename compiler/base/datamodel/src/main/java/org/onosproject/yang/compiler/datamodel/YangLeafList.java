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
import org.onosproject.yang.model.DataNode;
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
 *  Reference:RFC 6020.
 *  Where the "leaf" statement is used to define a simple scalar variable
 *  of a particular type, the "leaf-list" statement is used to define an
 *  array of a particular type.  The "leaf-list" statement takes one
 *  argument, which is an identifier, followed by a block of
 *  sub-statements that holds detailed leaf-list information.
 *
 *  The values in a leaf-list MUST be unique.
 *
 * The leaf-list's sub-statements
 *
 *                +--------------+---------+-------------+------------------+
 *                | substatement | section | cardinality |data model mapping|
 *                +--------------+---------+-------------+------------------+
 *                | config       | 7.19.1  | 0..1        | -boolean         |
 *                | description  | 7.19.3  | 0..1        | -string          |
 *                | if-feature   | 7.18.2  | 0..n        | -YangIfFeature   |
 *                | max-elements | 7.7.4   | 0..1        | -int             |
 *                | min-elements | 7.7.3   | 0..1        | -int             |
 *                | must         | 7.5.3   | 0..n        | -YangMust        |
 *                | ordered-by   | 7.7.5   | 0..1        | -TODO            |
 *                | reference    | 7.19.4  | 0..1        | -string          |
 *                | status       | 7.19.2  | 0..1        | -YangStatus      |
 *                | type         | 7.4     | 1           | -YangType        |
 *                | units        | 7.3.3   | 0..1        | -string          |
 *                | when         | 7.19.5  | 0..1        | -YangWhen        |
 *                +--------------+---------+-------------+------------------+
 */

/**
 * Represents leaf-list data represented in YANG.
 */
public abstract class YangLeafList extends DefaultLocationInfo
        implements YangCommonInfo, Parsable, Cloneable, Serializable,
        YangMustHolder, YangWhenHolder, YangIfFeatureHolder, YangSchemaNode,
        YangConfig, YangUnits, YangMaxElementHolder, YangMinElementHolder,
        SchemaDataNode, LeafSchemaContext, DefaultDenyWriteExtension,
        DefaultDenyAllExtension {

    private static final long serialVersionUID = 806201637L;

    /**
     * Name of leaf-list.
     */
    private YangSchemaNodeIdentifier yangSchemaNodeIdentifier;

    /**
     * If the leaf-list is a config parameter.
     */
    private boolean isConfig;

    /**
     * Description of leaf-list.
     */
    private String description;

    /**
     * Reference:RFC 6020.
     * <p>
     * The "max-elements" statement, which is optional, takes as an argument a
     * positive integer or the string "unbounded", which puts a constraint on
     * valid list entries. A valid leaf-list or list always has at most
     * max-elements entries.
     * <p>
     * If no "max-elements" statement is present, it defaults to "unbounded".
     */
    private YangMaxElement maxElement;

    /**
     * Reference:RFC 6020.
     * <p>
     * The "min-elements" statement, which is optional, takes as an argument a
     * non-negative integer that puts a constraint on valid list entries. A
     * valid leaf-list or list MUST have at least min-elements entries.
     * <p>
     * If no "min-elements" statement is present, it defaults to zero.
     * <p>
     * The behavior of the constraint depends on the type of the leaf-list's or
     * list's closest ancestor node in the schema tree that is not a non-
     * presence container:
     * <p>
     * o If this ancestor is a case node, the constraint is enforced if any
     * other node from the case exists.
     * <p>
     * o Otherwise, it is enforced if the ancestor node exists.
     */
    private YangMinElement minElements;

    /**
     * The textual reference to this leaf-list.
     */
    private String reference;

    /**
     * Status of the leaf-list in the YANG definition.
     */
    private YangStatusType status = CURRENT;

    /**
     * Textual units.
     */
    private String units;

    /**
     * Data type of leaf-list.
     */
    private YangType<?> dataType;

    /**
     * YANG Node in which the leaf is contained.
     */
    private YangLeavesHolder containedIn;

    /**
     * List of must statement constraints.
     */
    private List<YangMust> mustConstraintList;

    /**
     * When data of the leaf.
     */
    private YangWhen when;

    /**
     * List of if-feature.
     */
    private List<YangIfFeature> ifFeatureList;

    /**
     * Referred schema leaf list.
     */
    private YangLeafList referredLeafList;

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
     * Creates a YANG leaf-list.
     */
    public YangLeafList() {
        mustConstraintList = new LinkedList<>();
        ifFeatureList = new LinkedList<>();
        setMinElements(new YangMinElement());
        setMaxElements(new YangMaxElement());
    }

    /**
     * Returns the config flag.
     *
     * @return the config flag
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
     * Returns the maximum elements number.
     *
     * @return the maximum elements number
     */
    @Override
    public YangMaxElement getMaxElements() {
        return maxElement;
    }

    /**
     * Sets the maximum elements number.
     *
     * @param maxElement maximum elements number
     */
    @Override
    public void setMaxElements(YangMaxElement maxElement) {
        this.maxElement = maxElement;
    }

    /**
     * Returns the minimum elements number.
     *
     * @return the minimum elements number
     */
    @Override
    public YangMinElement getMinElements() {
        return minElements;
    }

    /**
     * Sets the minimum elements number.
     *
     * @param minElements the minimum elements number
     */
    @Override
    public void setMinElements(YangMinElement minElements) {
        this.minElements = minElements;
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
    public YangLeafList clone()
            throws CloneNotSupportedException {
        YangLeafList cll = (YangLeafList) super.clone();
        cll.yangSchemaNodeIdentifier = yangSchemaNodeIdentifier.clone();
        return cll;
    }

    /**
     * Returns the cloned leaf-list.
     *
     * @return returns cloned leaf-list
     * @throws CloneNotSupportedException if the object's class does not
     *                                    support the interface
     */
    public YangLeafList cloneForDeviation()
            throws CloneNotSupportedException {
        YangLeafList cll = (YangLeafList) super.clone();
        return cll;
    }

    /**
     * Returns the type of the parsed data.
     *
     * @return returns LEAF_LIST_DATA
     */
    @Override
    public YangConstructType getYangConstructType() {
        return YangConstructType.LEAF_LIST_DATA;
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
    public YangSchemaNodeContextInfo getChildSchema(
            YangSchemaNodeIdentifier dataNodeIdentifier)
            throws DataModelException {
        throw new DataModelException("leaf cannot have any child schema nodes" +
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
        throw new DataModelException("leaf list can't have child " + getName() + " in " +
                                             getLineNumber() + " at " +
                                             getCharPosition() +
                                             " in " + getFileName() + "\"");
    }

    @Override
    public Map<YangSchemaNodeIdentifier, YangSchemaNode> getDefaultChild(YangSchemaNodeIdentifier dataNodeIdentifier) {
        // Returns null as there is no child to leaf list.
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
        return YangSchemaNodeType.YANG_MULTI_INSTANCE_LEAF_NODE;
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
     * Retrieve the name of leaf list.
     *
     * @return leaf list name
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
        throw new DataModelException("Method called for schema node other then module/sub-module");
    }

    @Override
    public YangLeafList getReferredSchema() {
        return referredLeafList;
    }

    /**
     * Sets referred schema leaf-list. This is only applicable for grouping.
     *
     * @param leafList referred schema leaf-list
     */
    public void setReferredSchemaLeafList(YangLeafList leafList) {
        referredLeafList = leafList;
    }

    @Override
    public DataNode.Type getType() {
        return DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE;
    }

    @Override
    public SchemaId getSchemaId() {
        SchemaId schemaId = new SchemaId(
                this.getName(), this.getNameSpace().getModuleNamespace());
        return schemaId;
    }

    @Override
    public SchemaContext getParentContext() {
        return parentContext;
    }

    /**
     * Sets the parent context for current context.
     *
     * @param schemaContext schema context
     */
    public void setParentContext(SchemaContext schemaContext) {
        this.parentContext = schemaContext;
    }

    @Override
    public void setRootContext(SchemaContext context) {
        parentContext = context;
    }

    @Override
    public LeafObjectType getLeafObjectType() {
        return getLeafTypeByDataType(dataType, dataType.getDataType());
    }

    @Override
    public <T extends LeafRestriction> T getLeafRestrictions() {
        // TODO implementation.
        return null;
    }

    @Override
    public Object fromString(String value) {
        return LeafContextUtil.getObject(dataType, value, dataType.getDataType());
    }

    @Override
    public YangNamespace getValueNamespace(String value) {
        return LeafContextUtil.getValueNamespace(dataType, value,
                                                 dataType.getDataType());
    }

    @Override
    public LeafType getLeafType(String v) {
        return LeafContextUtil.getLeafType(dataType, v, dataType.getDataType());
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
