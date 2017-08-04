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
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;

import java.util.LinkedList;
import java.util.List;

import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_NON_DATA_NODE;
import static org.onosproject.yang.compiler.datamodel.exceptions.ErrorMessages.getErrorMsg;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.UNION_DATA;

/*
 * Reference RFC 6020.
 *
 * The union built-in type represents a value that corresponds to one of
 * its member types.
 *
 * When the type is "union", the "type" statement (Section 7.4) MUST be
 * present.  It is used to repeatedly specify each member type of the
 * union.  It takes as an argument a string that is the name of a member
 * type.
 *
 * A member type can be of any built-in or derived type, except it MUST
 * NOT be one of the built-in types "empty" or "leafref".
 *
 * When a string representing a union data type is validated, the string
 * is validated against each member type, in the order they are
 * specified in the "type" statement, until a match is found.
 *
 * Any default value or "units" property defined in the member types is
 * not inherited by the union type.
 */

/**
 * Represents data model node to maintain information defined in YANG union.
 */
public abstract class YangUnion
        extends YangNode
        implements Parsable, YangTypeHolder, CollisionDetector {

    private static final long serialVersionUID = 806201616L;

    // List of YANG type.
    private List<YangType<?>> typeList;

    // Current child union number.
    private transient int childUnionNumber;

    /**
     * Creates a YANG union node.
     */
    public YangUnion() {
        super(YangNodeType.UNION_NODE, null);
        typeList = new LinkedList<>();
        childUnionNumber = 1;
    }

    @Override
    public void addToChildSchemaMap(YangSchemaNodeIdentifier schemaNodeIdentifier,
                                    YangSchemaNodeContextInfo yangSchemaNodeContextInfo)
            throws DataModelException {
        // Do nothing.
    }

    @Override
    public void incrementMandatoryChildCount() {
        // Do nothing, as leaf can't come directly or indirectly below this construct.
        // TODO
    }

    @Override
    public void addToDefaultChildMap(YangSchemaNodeIdentifier yangSchemaNodeIdentifier, YangSchemaNode yangSchemaNode) {
        // Do nothing, as leaf can't come directly or indirectly below this construct.
        // TODO
    }

    @Override
    public YangSchemaNodeType getYangSchemaNodeType() {
        return YANG_NON_DATA_NODE;
    }

    @Override
    public List<YangType<?>> getTypeList() {
        return typeList;
    }


    /**
     * Returns running child union number.
     *
     * @return running child union number
     */
    public int getChildUnionNumber() {
        return childUnionNumber;
    }

    /**
     * Sets the running child union number.
     *
     * @param childUnionNumber running child union number
     */
    public void setChildUnionNumber(int childUnionNumber) {
        this.childUnionNumber = childUnionNumber;
    }

    /**
     * Adds YANG type to type list.
     *
     * @param yangType YANG type to be added to list
     * @throws DataModelException union member type must not be one of the
     *                            built-in types "empty" or "leafref"
     */
    public void addType(YangType<?> yangType)
            throws DataModelException {
        YangDataTypes type = yangType.getDataType();
        String msg = "Union member type must not be one of the built-in types" +
                " \"empty\" or \"leafref\"";
        switch (type) {
            case EMPTY:
            case LEAFREF:
                throw new DataModelException(getErrorMsg(
                        msg, getName(), getLineNumber(), getCharPosition(),
                        getFileName()));

            default:
                typeList.add(yangType);
        }
    }

    @Override
    public YangConstructType getYangConstructType() {
        return UNION_DATA;
    }

    /**
     * Validates the data on entering the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnEntry()
            throws DataModelException {
        //TODO: implement the method.
    }

    /**
     * Validates the data on exiting the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnExit()
            throws DataModelException {
        //TODO: implement the method.
    }

    @Override
    public void detectCollidingChild(String identifierName, YangConstructType dataType)
            throws DataModelException {
        // Do nothing
    }

    @Override
    public void detectSelfCollision(String identifierName, YangConstructType dataType)
            throws DataModelException {
        // Do nothing
    }
}
