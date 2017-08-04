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

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.PATTERN_DATA;

/*-
 *  Reference RFC 6020.
 *
 *  The pattern Statement
 *
 *  The "pattern" statement, which is an optional sub-statement to the
 *  "type" statement, takes as an argument a regular expression string.
 *  It is used to restrict the built-in type "string", or types derived
 *  from "string", to values that match the pattern.
 *
 *  If the type has multiple "pattern" statements, the expressions are
 *  ANDed together, i.e., all such expressions have to match.
 *
 *  If a pattern restriction is applied to an already pattern-restricted
 *  type, values must match all patterns in the base type, in addition to
 *  the new patterns.
 *  The pattern's sub-statements
 *
 *   +---------------+---------+-------------+
 *   | substatement  | section | cardinality |
 *   +---------------+---------+-------------+
 *   | description   | 7.19.3  | 0..1        |
 *   | error-app-tag | 7.5.4.2 | 0..1        |
 *   | error-message | 7.5.4.1 | 0..1        |
 *   | reference     | 7.19.4  | 0..1        |
 *   +---------------+---------+-------------+
 */

/**
 * Represents pattern restriction information. The regular expression restriction on string
 * data type.
 */
public class YangPatternRestriction extends DefaultLocationInfo
        implements Serializable, YangAppErrorHolder, YangReference, YangDesc,
        Parsable {

    private static final long serialVersionUID = 806201649L;

    /**
     * Pattern restriction.
     */
    private final String pattern;

    /**
     * YANG application error information.
     */
    private YangAppErrorInfo yangAppErrorInfo;

    /**
     * Textual reference.
     */
    private String reference;

    /**
     * Textual description.
     */
    private String description;

    /**
     * Creates a YANG pattern restriction object.
     *
     * @param p pattern
     */
    public YangPatternRestriction(String p) {
        pattern = p;
        yangAppErrorInfo = new YangAppErrorInfo();
    }

    /**
     * Returns the pattern restriction defined for the current type.
     *
     * @return pattern restriction
     */
    public String getPattern() {
        return pattern;
    }

    @Override
    public YangAppErrorInfo getAppErrorInfo() {
        return yangAppErrorInfo;
    }

    @Override
    public void setAppErrorInfo(YangAppErrorInfo yangAppErrorInfo) {
        this.yangAppErrorInfo = yangAppErrorInfo;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String d) {
        description = d;
    }

    @Override
    public String getReference() {
        return reference;
    }

    @Override
    public void setReference(String r) {
        reference = r;
    }

    @Override
    public YangConstructType getYangConstructType() {
        return PATTERN_DATA;
    }

    @Override
    public void validateDataOnEntry() throws DataModelException {
        // TODO auto-generated method stub
    }

    @Override
    public void validateDataOnExit() throws DataModelException {
        // TODO auto-generated method stub
    }
}
