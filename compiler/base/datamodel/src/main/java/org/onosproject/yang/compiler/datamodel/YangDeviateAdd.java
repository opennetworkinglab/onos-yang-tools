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

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DEVIATE_ADD;

/*
 *  Reference RFC 6020.
 *
 *  The "deviate" statement defines how the device's implementation of
 *  the target node deviates from its original definition.  The argument
 *  is one of the strings "not-supported", "add", "replace", or "delete".
 *
 *  The argument "add" adds properties to the target node.  The
 *  properties to add are identified by substatements to the "deviate"
 *  statement.  If a property can only appear once, the property MUST NOT
 *  exist in the target node.
 *
 *  The deviate's sub-statements
 *
 *        +--------------+---------+-------------+------------------+
 *        | substatement | section | cardinality |data model mapping|
 *        +--------------+---------+-------------+------------------+
 *        | config       | 7.19.1  | 0..1        | YangConfig       |
 *        | default      | 7.6.4   | 0..1        | String           |
 *        | mandatory    | 7.6.5   | 0..1        | boolean          |
 *        | max-elements | 7.7.4   | 0..1        | YangMaxElement   |
 *        | min-elements | 7.7.3   | 0..1        | YangMinElement   |
 *        | must         | 7.5.3   | 0..n        | YangMust         |
 *        | type         | 7.4     | 0..1        | YangType         |
 *        | unique       | 7.8.3   | 0..n        | String           |
 *        | units        | 7.3.3   | 0..1        | String           |
 *        +--------------+---------+-------------+------------------+
 */

/**
 * Represents deviate add data represented in YANG.
 */
public class YangDeviateAdd implements YangConfig, Parsable,
        YangMustHolder, YangUniqueHolder, YangMaxElementHolder,
        YangMinElementHolder, YangUnits, YangMandatory, YangDefault,
        Serializable {

    private static final long serialVersionUID = 806201609L;

    /**
     * Textual units info.
     */
    private String units;

    /**
     * List of must statement constraints.
     */
    private List<YangMust> mustConstraintList;

    /**
     * List of unique atomic path list.
     */
    private List<List<YangAtomicPath>> pathList;

    /**
     * Default value in string, needs to be converted to the target object,
     * based on the type.
     */
    private String defaultValueInString;

    /**
     * Deviate's config data.
     */
    private boolean isConfig;

    /**
     * Mandatory constraint.
     */
    private boolean isMandatory;

    /**
     * Deviate's max element data.
     */
    private YangMaxElement maxElements;

    /**
     * Deviate's min element data.
     */
    private YangMinElement minElements;

    /**
     * List of unique leaves.
     */
    private List<YangLeaf> uniqueLeaves;

    /**
     * Creates a YANG deviate add object.
     */
    public YangDeviateAdd() {
        mustConstraintList = new LinkedList<>();
        pathList = new LinkedList<>();
        uniqueLeaves = new LinkedList<>();
    }

    @Override
    public boolean isConfig() {
        return isConfig;
    }

    @Override
    public void setConfig(boolean isConfig) {
        this.isConfig = isConfig;
    }

    @Override
    public YangConstructType getYangConstructType() {
        return DEVIATE_ADD;
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
    public List<YangMust> getListOfMust() {
        return mustConstraintList;
    }

    @Override
    public void setListOfMust(List<YangMust> mustConstraintList) {
        this.mustConstraintList = mustConstraintList;
    }

    @Override
    public void addMust(YangMust must) {
        mustConstraintList.add(must);
    }

    /**
     * Returns the max elements.
     *
     * @return the max elements
     */
    @Override
    public YangMaxElement getMaxElements() {
        return maxElements;
    }

    /**
     * Sets the max elements.
     *
     * @param max the max elements
     */
    @Override
    public void setMaxElements(YangMaxElement max) {
        this.maxElements = max;
    }

    /**
     * Returns the minimum elements.
     *
     * @return the minimum elements
     */
    @Override
    public YangMinElement getMinElements() {
        return minElements;
    }

    /**
     * Sets the minimum elements.
     *
     * @param minElements the minimum elements
     */
    @Override
    public void setMinElements(YangMinElement minElements) {
        this.minElements = minElements;
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
     * Adds unique path in data holder.
     *
     * @param path unique path
     */
    @Override
    public void addUnique(List<YangAtomicPath> path) {
        pathList.add(path);
    }

    /**
     * Sets the list of unique path.
     *
     * @param pathList unique path list
     */
    @Override
    public void setPathList(List<List<YangAtomicPath>> pathList) {
        this.pathList = pathList;
    }

    /**
     * Returns the list of unique path from data holder.
     *
     * @return unique path list
     */
    @Override
    public List<List<YangAtomicPath>> getPathList() {
        return pathList;
    }

    /**
     * Returns the list of unique leaves.
     *
     * @return unique leaves
     */
    @Override
    public List<YangLeaf> getUniqueLeaves() {
        return uniqueLeaves;
    }

    /**
     * Adds unique leaf to the unique leaves.
     *
     * @param uniqueLeaf YANG leaf
     */
    @Override
    public void addUniqueLeaf(YangLeaf uniqueLeaf) {
        uniqueLeaves.add(uniqueLeaf);
    }
}
