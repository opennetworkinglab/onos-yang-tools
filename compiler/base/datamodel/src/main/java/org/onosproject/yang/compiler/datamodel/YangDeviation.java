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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_NON_DATA_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.detectCollidingChildUtil;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DEVIATION_DATA;

/**
 * Represents deviation data represented in YANG.
 */
public class YangDeviation extends YangNode implements Parsable, YangDesc,
        YangReference, YangXPathResolver, Resolvable, CollisionDetector {

    private static final long serialVersionUID = 806201605L;

    /**
     * List of node identifiers.
     */
    private List<YangAtomicPath> targetNode;

    /**
     * Description of augment.
     */
    private String description;

    /**
     * Reference of the YANG augment.
     */
    private String reference;

    /**
     * Represents deviate-not-supported statement.
     */
    private boolean isDeviateNotSupported;

    /**
     * Represents deviate add statement.
     */
    private List<YangDeviateAdd> deviateAddList;

    /**
     * Represents deviate delete statement.
     */
    private List<YangDeviateDelete> deviateDeleteList;

    /**
     * Represents deviate replace statement.
     */
    private List<YangDeviateReplace> deviateReplaceList;

    /**
     * Status of resolution.
     */
    private ResolvableStatus resolvableStatus;

    /**
     * Creates a specific type of node.
     *
     * @param type              of YANG node
     * @param ysnContextInfoMap YSN context info map
     */
    public YangDeviation(YangNodeType type, Map<YangSchemaNodeIdentifier,
            YangSchemaNodeContextInfo> ysnContextInfoMap) {
        super(type, ysnContextInfoMap);
        targetNode = new LinkedList<>();
        resolvableStatus = ResolvableStatus.UNRESOLVED;
        deviateAddList = new LinkedList<>();
        deviateDeleteList = new LinkedList<>();
        deviateReplaceList = new LinkedList<>();
    }

    @Override
    public YangSchemaNodeType getYangSchemaNodeType() {
        return YANG_NON_DATA_NODE;
    }

    @Override
    public String getJavaPackage() {
        // do nothing
        return null;
    }

    @Override
    public String getJavaClassNameOrBuiltInType() {
        // do nothing
        return null;
    }

    @Override
    public String getJavaAttributeName() {
        // do nothing
        return null;
    }

    @Override
    public void addToChildSchemaMap(YangSchemaNodeIdentifier schemaNodeIdentifier,
                                    YangSchemaNodeContextInfo yangSchemaNodeContextInfo)
            throws DataModelException {
        // do nothing
    }

    @Override
    public void incrementMandatoryChildCount() {
        // do nothing
    }

    @Override
    public void addToDefaultChildMap(YangSchemaNodeIdentifier yangSchemaNodeIdentifier,
                                     YangSchemaNode yangSchemaNode) {
        // do nothing
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
    public YangConstructType getYangConstructType() {
        return DEVIATION_DATA;
    }

    @Override
    public void validateDataOnEntry() throws DataModelException {
        // do nothing
    }

    @Override
    public void validateDataOnExit() throws DataModelException {
        // do nothing
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
    public Object resolve() throws DataModelException {
        // Resolving of target node is being done in XPathLinker.
        return null;
    }

    /**
     * Returns the isDeviateNotSupported flag.
     *
     * @return if isDeviateNotSupported flag
     */
    public boolean isDeviateNotSupported() {
        return isDeviateNotSupported;
    }

    /**
     * Sets the isDeviateNotSupported flag.
     *
     * @param deviateNotSupported the flag value to set
     */
    public void setDeviateNotSupported(boolean deviateNotSupported) {
        isDeviateNotSupported = deviateNotSupported;
    }

    @Override
    public void detectCollidingChild(String identifierName,
                                     YangConstructType dataType)
            throws DataModelException {
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
     * Returns the list of deviate-add.
     *
     * @return the list of deviate-add
     */
    public List<YangDeviateAdd> getDeviateAdd() {
        return deviateAddList;
    }

    /**
     * Sets the list of deviate-add.
     *
     * @param deviateAdd the list of deviate-add to set
     */
    public void setDeviateAdd(List<YangDeviateAdd> deviateAdd) {
        this.deviateAddList = deviateAdd;
    }

    /**
     * Returns the list of deviate delete.
     *
     * @return the list of deviate delete
     */
    public List<YangDeviateDelete> getDeviateDelete() {
        return deviateDeleteList;
    }

    /**
     * Sets the list of deviate delete.
     *
     * @param deviateDelete the list of deviate delete to set
     */
    public void setDeviateDelete(List<YangDeviateDelete> deviateDelete) {
        this.deviateDeleteList = deviateDelete;
    }

    /**
     * Returns the list of deviate replace.
     *
     * @return the list of deviate replace
     */
    public List<YangDeviateReplace> getDeviateReplace() {
        return deviateReplaceList;
    }

    /**
     * Sets the list of deviate replace.
     *
     * @param deviateReplace the list of deviate-replace to set
     */
    public void setDeviateReplace(List<YangDeviateReplace> deviateReplace) {
        this.deviateReplaceList = deviateReplace;
    }

    /**
     * Adds a deviate-add.
     *
     * @param deviate the deviate-add to be added
     */
    public void addDeviateAdd(YangDeviateAdd deviate) {
        deviateAddList.add(deviate);
    }

    /**
     * Adds a deviate delete.
     *
     * @param deviate the deviate delete to be added
     */
    public void addDeviatedelete(YangDeviateDelete deviate) {
        deviateDeleteList.add(deviate);
    }

    /**
     * Adds a deviate replace.
     *
     * @param deviate the deviate replace to be added
     */
    public void addDeviateReplace(YangDeviateReplace deviate) {
        deviateReplaceList.add(deviate);
    }
}
