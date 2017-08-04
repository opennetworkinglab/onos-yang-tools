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

package org.onosproject.yang.compiler.datamodel.utils;

import org.onosproject.yang.compiler.datamodel.YangRangeInterval;
import org.onosproject.yang.compiler.datamodel.YangRangeRestriction;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangBuiltInDataTypeInfo;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;

import static java.util.regex.Pattern.quote;
import static org.onosproject.yang.compiler.datamodel.BuiltInTypeObjectFactory.getDataObjectFromString;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.LENGTH_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.RANGE_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.getYangConstructType;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT64;

/**
 * Represents restriction resolver which provide common utility used by parser
 * and during linking for restriction resolution.
 */
public final class RestrictionResolver {

    private static final String PIPE = "|";
    private static final String ADD = "+";
    private static final String EMPTY_STRING = "";
    private static final String INTERVAL = "..";
    private static final int MAX_RANGE_BOUNDARY = 2;
    private static final int MIN_RANGE_BOUNDARY = 1;
    private static final String MIN_KEYWORD = "min";
    private static final String MAX_KEYWORD = "max";
    private static final String SPACE = " ";
    private static final String QUOTE = "\"";

    /**
     * Creates a restriction resolver.
     */
    private RestrictionResolver() {
    }

    /**
     * Processes the range restriction for parser and linker.
     *
     * @param refRr    range restriction of referred typedef
     * @param line     error line number
     * @param position error character position in line
     * @param hasRefR  whether has referred restriction
     * @param curRr    caller type's range restriction
     * @param type     effective type, when called from linker
     * @param fileName file name
     * @return YANG range restriction
     * @throws DataModelException a violation in data model rule
     */
    public static YangRangeRestriction processRangeRestriction(YangRangeRestriction refRr,
                                                               int line, int position,
                                                               boolean hasRefR,
                                                               YangRangeRestriction curRr,
                                                               YangDataTypes type,
                                                               String fileName)
            throws DataModelException {
        return getRestriction(refRr, line, position, hasRefR, curRr, fileName,
                              type, RANGE_DATA);
    }

    /**
     * Processes the length restriction for parser and linker.
     *
     * @param refLr    length restriction of referred typedef
     * @param line     error line number
     * @param position error character position in line
     * @param hasRefR  whether has referred restriction
     * @param curLr    caller type's length restriction
     * @param fileName file name
     * @return YANG range restriction
     * @throws DataModelException a violation in data model rule
     */
    public static YangRangeRestriction processLengthRes(YangRangeRestriction refLr,
                                                        int line, int position,
                                                        boolean hasRefR,
                                                        YangRangeRestriction curLr,
                                                        String fileName)
            throws DataModelException {
        return getRestriction(refLr, line, position, hasRefR, curLr, fileName,
                              UINT64, LENGTH_DATA);
    }

    /**
     * Processes the range/length restriction for parser and linker.
     *
     * @param refR     range/length restriction of referred typedef
     * @param line     error line number
     * @param position error character position in line
     * @param hasRefR  whether has referred restriction
     * @param curR     caller type's range restriction
     * @param type     effective type, when called from linker
     * @param fileName file name
     * @param conType  construct type
     * @return YANG range restriction
     * @throws DataModelException a violation in data model rule
     */
    private static YangRangeRestriction getRestriction(YangRangeRestriction refR,
                                                       int line, int position,
                                                       boolean hasRefR,
                                                       YangRangeRestriction curR,
                                                       String fileName, YangDataTypes type,
                                                       YangConstructType conType)
            throws DataModelException {
        YangBuiltInDataTypeInfo<?> startValue;
        YangBuiltInDataTypeInfo<?> endValue;

        String rangeArg = removeQuotesAndHandleConcat(curR.getRangeValue());
        String[] rangeArguments = rangeArg.trim().split(quote(PIPE));

        for (String rangePart : rangeArguments) {
            String startInterval;
            String endInterval;
            YangRangeInterval rangeInterval = new YangRangeInterval();
            rangeInterval.setCharPosition(position);
            rangeInterval.setLineNumber(line);
            rangeInterval.setFileName(fileName);
            String[] rangeBoundary = rangePart.trim().split(quote(INTERVAL));

            if (rangeBoundary.length > MAX_RANGE_BOUNDARY) {
                DataModelException ex = new DataModelException(
                        "YANG file error : " + getYangConstructType(conType) +
                                SPACE + rangeArg + " is not valid.");
                ex.setLine(line);
                ex.setCharPosition(position);
                ex.setFileName(fileName);
                throw ex;
            }

            if (rangeBoundary.length == MIN_RANGE_BOUNDARY) {
                startInterval = rangeBoundary[0].trim();
                endInterval = rangeBoundary[0].trim();
            } else {
                startInterval = rangeBoundary[0].trim();
                endInterval = rangeBoundary[1].trim();
            }

            try {
                if (hasRefR && startInterval.equals(MIN_KEYWORD) &&
                        refR.getMinRestrictedValue() != null) {
                    startValue = refR.getMinRestrictedValue();
                } else if (hasRefR && startInterval.equals(MAX_KEYWORD) &&
                        refR.getMaxRestrictedValue() != null) {
                    startValue = refR.getMaxRestrictedValue();
                } else {
                    startValue = getDataObjectFromString(startInterval, type);
                }
                if (hasRefR && endInterval.equals(MIN_KEYWORD) &&
                        refR.getMinRestrictedValue() != null) {
                    endValue = refR.getMinRestrictedValue();
                } else if (hasRefR && endInterval.equals(MAX_KEYWORD) &&
                        refR.getMaxRestrictedValue() != null) {
                    endValue = refR.getMaxRestrictedValue();
                } else {
                    endValue = getDataObjectFromString(endInterval, type);
                }
            } catch (Exception e) {
                DataModelException ex = new DataModelException(e.getMessage());
                ex.setLine(line);
                ex.setCharPosition(position);
                ex.setFileName(fileName);
                throw ex;
            }
            rangeInterval.setStartValue(startValue);
            rangeInterval.setEndValue(endValue);
            try {
                curR.addRangeRestrictionInterval(rangeInterval);
            } catch (DataModelException ex) {
                ex.setLine(line);
                ex.setCharPosition(position);
                ex.setFileName(fileName);
                throw ex;
            }
        }
        return curR;
    }

    /**
     * Removes doubles quotes and concatenates if string has plus symbol.
     *
     * @param yangStringData string from yang file
     * @return concatenated string after removing double quotes
     */
    private static String removeQuotesAndHandleConcat(String yangStringData) {
        yangStringData = yangStringData.replace(QUOTE, EMPTY_STRING);
        String[] tmpData = yangStringData.split(quote(ADD));
        StringBuilder builder = new StringBuilder();
        for (String yangString : tmpData) {
            builder.append(yangString);
        }
        return builder.toString();
    }
}
