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

import java.util.SortedSet;
import java.util.TreeSet;

import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_NON_DATA_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.ENUMERATION_DATA;

/*
 * The enumeration built-in type represents values from a set of
 *  assigned names.
 */

/**
 * Represents the enumeration data type information.
 */
public abstract class YangEnumeration
        extends YangNode
        implements Parsable, CollisionDetector {

    private static final long serialVersionUID = 806201606L;

    // Enumeration info set.
    private SortedSet<YangEnum> enumSet;

    /**
     * Creates an enumeration object.
     */
    public YangEnumeration() {
        super(YangNodeType.ENUMERATION_NODE, null);
        setEnumSet(new TreeSet<>());
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
        return YANG_NON_DATA_NODE;
    }

    /**
     * Returns the ENUM set.
     *
     * @return the ENUM set
     */
    public SortedSet<YangEnum> getEnumSet() {
        return enumSet;
    }

    /**
     * Sets the ENUM set.
     *
     * @param enumSet the ENUM set to set
     */
    private void setEnumSet(SortedSet<YangEnum> enumSet) {
        this.enumSet = enumSet;
    }

    /**
     * Adds ENUM information.
     *
     * @param enumInfo the ENUM information to be added
     * @throws DataModelException due to violation in data model rules
     */
    public void addEnumInfo(YangEnum enumInfo)
            throws DataModelException {
        if (!getEnumSet().add(enumInfo)) {
            throw new DataModelException("YANG ENUM already exists " +
                    getName() + " in " +
                    getLineNumber() + " at " +
                    getCharPosition() +
                    " in " + getFileName() + "\"");
        }
    }

    /**
     * Returns the type of the data.
     *
     * @return returns ENUMERATION_DATA
     */
    @Override
    public YangConstructType getYangConstructType() {
        return ENUMERATION_DATA;
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
    public void detectCollidingChild(String identifierName, YangConstructType dataType)
            throws DataModelException {
        /*
        Do nothing, since it is not part of the schema tree, it is only type of an existing node in schema tree.
         */
    }

    @Override
    public void detectSelfCollision(String identifierName, YangConstructType dataType)
            throws DataModelException {
        /*
        Do nothing, since it is not part of the schema tree, it is only type of an existing node in schema tree.
         */
    }
}
