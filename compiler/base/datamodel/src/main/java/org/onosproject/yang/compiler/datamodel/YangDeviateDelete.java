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

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DEVIATE_DELETE;

/*
 *  Reference 6020.
 *
 *  The "deviate" statement defines how the device's implementation of
 *  the target node deviates from its original definition.  The argument
 *  is one of the strings "not-supported", "add", "replace", or "delete".
 *
 *  The argument "delete" deletes properties from the target node.  The
 *  properties to delete are identified by substatements to the "delete"
 *  statement.  The substatement's keyword MUST match a corresponding
 *  keyword in the target node, and the argument's string MUST be equal
 *  to the corresponding keyword's argument string in the target node.
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
 * Represents deviate delete data represented in YANG.
 */
public class YangDeviateDelete implements Parsable, YangMustHolder,
        YangUniqueHolder, YangDefault, YangUnits, Serializable {

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
     * List of unique leaves.
     */
    private List<YangLeaf> uniqueLeaves;

    /**
     * Creates a YANG deviate delete object.
     */
    public YangDeviateDelete() {
        mustConstraintList = new LinkedList<>();
        pathList = new LinkedList<>();
        uniqueLeaves = new LinkedList<>();
    }

    @Override
    public YangConstructType getYangConstructType() {
        return DEVIATE_DELETE;
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
     * Adds a unique path to the list.
     *
     * @param path unique path
     */
    @Override
    public void addUnique(List<YangAtomicPath> path) {
        pathList.add(path);
    }

    /**
     * Sets the list of unique path list.
     *
     * @param pathList the list of unique path list
     */
    public void setPathList(List<List<YangAtomicPath>> pathList) {
        this.pathList = pathList;
    }

    /**
     * Returns the list of unique field names.
     *
     * @return the list of unique field names
     */
    public List<List<YangAtomicPath>> getPathList() {
        return pathList;
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
     * Returns the list of unique leaves.
     *
     * @return the list of unique leves
     */
    @Override
    public List<YangLeaf> getUniqueLeaves() {
        return uniqueLeaves;
    }

    /**
     * Adds a unique lead to unique leaves.
     *
     * @param uniqueLeaf YANG leaf
     */
    @Override
    public void addUniqueLeaf(YangLeaf uniqueLeaf) {
        uniqueLeaves.add(uniqueLeaf);
    }
}
