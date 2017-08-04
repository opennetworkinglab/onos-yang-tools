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
import org.onosproject.yang.compiler.datamodel.utils.FractionDigits;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.datamodel.utils.YangConstructType;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.DataTypeException;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangBuiltInDataTypeInfo;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ListIterator;

import static org.onosproject.yang.compiler.datamodel.exceptions.ErrorMessages.getErrorMsg;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DECIMAL64_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DECIMAL64;

/**
 * Represents YANG decimal 64.
 */
public class YangDecimal64<T> extends DefaultLocationInfo
        implements YangBuiltInDataTypeInfo<YangDecimal64>, Parsable, Serializable,
        Comparable<YangDecimal64> {

    private static final long serialVersionUID = 8006201668L;

    /**
     * YANG's min keyword.
     */
    private static final String MIN_KEYWORD = "min";

    /**
     * YANG's max keyword.
     */
    private static final String MAX_KEYWORD = "max";

    /**
     * Valid minimum value of YANG's fraction-digits.
     */
    public static final int MIN_FRACTION_DIGITS_VALUE = 1;

    /**
     * Valid maximum value of YANG's fraction-digits.
     */
    public static final int MAX_FRACTION_DIGITS_VALUE = 18;

    /**
     * Valid minimum value of YANG's decimal64.
     */
    private static final BigDecimal MIN_VALUE =
            BigDecimal.valueOf(-922337203685477580.8);

    /**
     * Valid maximum value of YANG's decimal64.
     */
    private static final BigDecimal MAX_VALUE =
            BigDecimal.valueOf(922337203685477580.7);

    private static final int MIN_FRACTION_DIGIT_RANGE = 1;
    private static final int MAX_FRACTION_DIGIT_RANGE = 18;
    private static final int ZERO = 0;


    // Decimal64 value
    private BigDecimal value;

    // fraction-digits
    private int fractionDigit;

    /**
     * Additional information about range restriction.
     */
    private T rangeRestrictedExtendedInfo;

    /**
     * Creates an instance of YANG decimal64.
     */
    public YangDecimal64() {
    }

    /**
     * Creates an instance of YANG decimal64.
     *
     * @param value of decimal64
     */
    public YangDecimal64(BigDecimal value) {
        setValue(value);
    }

    /**
     * Creates an instance of YANG decimal64.
     *
     * @param valueInString of decimal64 in string
     */
    YangDecimal64(String valueInString) {
        if (valueInString.matches(MIN_KEYWORD)) {
            value = MIN_VALUE;
        } else if (valueInString.matches(MAX_KEYWORD)) {
            value = MAX_VALUE;
        } else {
            try {
                value = new BigDecimal(valueInString);
            } catch (Exception e) {
                throw new DataTypeException(
                        "YANG file error : Input value \"" + valueInString + "\"" +
                                " is not a valid decimal64.");
            }
        }

        if (value.doubleValue() < MIN_VALUE.doubleValue()) {
            throw new DataTypeException("YANG file error : " + valueInString +
                                                " is less than minimum value "
                                                + MIN_VALUE + ".");
        } else if (value.doubleValue() > MAX_VALUE.doubleValue()) {
            throw new DataTypeException("YANG file error : " + valueInString +
                                                " is greater than maximum value "
                                                + MAX_VALUE + ".");
        }
    }

    /**
     * Returns decimal64 value.
     *
     * @return value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the decimal64 value.
     *
     * @param value of decimal64
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Returns fraction digit.
     *
     * @return the fractionDigit
     */
    public int getFractionDigit() {
        return fractionDigit;
    }

    /**
     * Sets fraction digit.
     *
     * @param fractionDigit fraction digits.
     */
    public void setFractionDigit(int fractionDigit) {
        this.fractionDigit = fractionDigit;
    }

    /**
     * Returns additional information about range restriction.
     *
     * @return resolved range restricted extended information
     */
    public T getRangeRestrictedExtendedInfo() {
        return rangeRestrictedExtendedInfo;
    }

    /**
     * Sets additional information about range restriction.
     *
     * @param resolvedExtendedInfo resolved range restricted extended information
     */
    public void setRangeRestrictedExtendedInfo(T resolvedExtendedInfo) {
        rangeRestrictedExtendedInfo = resolvedExtendedInfo;
    }

    /**
     * Returns object of YANG decimal64.
     *
     * @param value of decimal64
     * @return YANG decimal64 object
     */
    public static YangDecimal64 of(BigDecimal value) {
        return new YangDecimal64(value);
    }

    @Override
    public YangDataTypes getYangType() {
        return DECIMAL64;
    }

    @Override
    public YangConstructType getYangConstructType() {
        return DECIMAL64_DATA;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * Returns the object of YANG decimal64 from input string.
     *
     * @param valInString input String
     * @return Object of YANG decimal64
     */
    static YangDecimal64 fromString(String valInString) {
        return new YangDecimal64(valInString);
    }

    /**
     * Checks whether specific fraction-digit in its range.
     *
     * @return true if fraction-digit is in its range otherwise false
     */
    public boolean isValidFractionDigit() {
        return fractionDigit >= MIN_FRACTION_DIGIT_RANGE &&
                fractionDigit <= MAX_FRACTION_DIGIT_RANGE;
    }


    /**
     * Checks whether value is in correct decimal64 value range.
     *
     * @throws DataModelException a violation of data model rules
     */
    public void validateDecimal64() throws DataModelException {
        YangRangeRestriction rangeRestriction =
                (YangRangeRestriction) getRangeRestrictedExtendedInfo();
        if (rangeRestriction != null) {
            // Check whether value is within provided range value
            ListIterator<YangRangeInterval> rangeListIterator =
                    rangeRestriction.getAscendingRangeIntervals().listIterator();

            boolean isMatched = false;
            while (rangeListIterator.hasNext()) {
                YangRangeInterval rangeInterval = rangeListIterator.next();
                rangeInterval.setCharPosition(getCharPosition());
                rangeInterval.setLineNumber(getLineNumber());
                rangeInterval.setFileName(getFileName());
                BigDecimal startValue = ((YangDecimal64) rangeInterval.getStartValue()).getValue();
                BigDecimal endValue = ((YangDecimal64) rangeInterval.getEndValue()).getValue();
                if (value.compareTo(startValue) >= ZERO &&
                        value.compareTo(endValue) <= ZERO) {
                    isMatched = true;
                    break;
                }
            }
            // If range is not matched then throw error
            if (!isMatched) {
                throw new DataModelException(getErrorMsg(
                        "YANG file error : decimal64 validation failed.", "decimal64",
                        getLineNumber(), getCharPosition(), getFileName() + "\""));
            }
        } else {
            // Check value is in fraction-digits decimal64 value range
            if (!FractionDigits.isValueInDecimal64Range(value, getFractionDigit())) {
                throw new DataModelException(
                        "YANG file error : value is not in decimal64 range.");
            }
        }
    }

    /**
     * Validate range restriction values based on fraction-digits decimal64 range value.
     *
     * @throws DataModelException a violation of data model rules
     */
    public void validateRange() throws DataModelException {
        YangRangeRestriction rangeRestriction =
                (YangRangeRestriction) getRangeRestrictedExtendedInfo();
        if (rangeRestriction == null) {
            // No need to validate. Range is optional.
            return;
        }
        ListIterator<YangRangeInterval> rangeListIterator =
                rangeRestriction.getAscendingRangeIntervals().listIterator();
        while (rangeListIterator.hasNext()) {
            YangRangeInterval rangeInterval = rangeListIterator.next();
            rangeInterval.setCharPosition(getCharPosition());
            rangeInterval.setLineNumber(getLineNumber());
            rangeInterval.setFileName(getFileName());
            if (!FractionDigits.isValueInDecimal64Range(
                    ((YangDecimal64) rangeInterval.getStartValue()).getValue(),
                    getFractionDigit())) {
                throw new DataModelException(getErrorMsg(
                        "YANG file error : decimal64 validation failed.", "decimal64",
                        getLineNumber(), getCharPosition(), getFileName() + "\""));
            }
            if (!FractionDigits.isValueInDecimal64Range(
                    ((YangDecimal64) rangeInterval.getEndValue()).getValue(),
                    getFractionDigit())) {
                throw new DataModelException(getErrorMsg(
                        "YANG file error : decimal64 validation failed.", "decimal64",
                        getLineNumber(), getCharPosition(), getFileName() + "\""));
            }
        }
    }

    @Override
    public int compareTo(YangDecimal64 o) {
        return Double.compare(value.doubleValue(), o.value.doubleValue());
    }

    /**
     * Returns decimal64 default range restriction based on fraction-digits.
     * If range restriction is not provided then this default range value will be applicable.
     *
     * @return range restriction
     * @throws DataModelException a violation of data model rules
     */
    public YangRangeRestriction getDefaultRangeRestriction() throws DataModelException {
        YangRangeRestriction refRangeRestriction = new YangRangeRestriction(null);
        YangRangeInterval rangeInterval = new YangRangeInterval<>();
        rangeInterval.setCharPosition(getCharPosition());
        rangeInterval.setLineNumber(getLineNumber());
        rangeInterval.setFileName(getFileName());
        FractionDigits.Range range = FractionDigits.getRange(fractionDigit);
        rangeInterval.setStartValue(new YangDecimal64(new BigDecimal((range.getMin()))));
        rangeInterval.setEndValue(new YangDecimal64(new BigDecimal((range.getMax()))));
        refRangeRestriction.addRangeRestrictionInterval(rangeInterval);
        return refRangeRestriction;
    }

    /**
     * Validates the data on entering the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnEntry() throws DataModelException {
        // TODO auto-generated method stub, to be implemented by parser
    }

    /**
     * Validates the data on exiting the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnExit() throws DataModelException {
        // TODO auto-generated method stub, to be implemented by parser
    }
}
