/*
 * Copyright 2017-present Open Networking Foundation
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
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.DataTypeException;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangUint16;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangUint32;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangUint64;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangUint8;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Utility class to check error messages.
 */
final class CheckValidationTest {
    private static final String E_MESS = "Exception has not occurred for " +
            "invalid value with type ";
    private CheckValidationTest() {
    }

    /**
     * Tests the value against the respective YANG node data type.
     *
     * @param node  associated with YANG node data type
     * @param value associated with value of YANG node
     */
    static void dataValidation(YangType node, String value)
            throws DataModelException {
        boolean expOccurred = false;
        YangDataTypes type = node.getDataType();
        try {
            node.isValidValue(value);
        } catch (DataTypeException e) {
            expOccurred = true;
            assertEquals(e.getMessage(), getErrorString(value, type));
        }
        assertEquals(E_MESS + type, expOccurred, true);
    }

    /**
     * Tests the range of the value against the respective YANG node data type.
     *
     * @param node  associated with YANG node data type
     * @param value associated with value of YANG node
     */
    static void rangeCheck(YangType node, String value)
            throws DataModelException {
        boolean expOccurred = false;
        YangDataTypes type = node.getDataType();
        try {
            node.isValidValue(value);
        } catch (DataTypeException e) {
            expOccurred = true;
            assertEquals(e.getMessage(), getRangeError(value, type));
        }
        assertEquals(E_MESS + type, expOccurred, true);
    }

    /**
     * Returns the error message for the corresponding range of value of the
     * respective data type.
     *
     * @param value associated with value of YANG node
     * @param type  of the YANG node
     * @return the error message associated with the validation
     */
    private static String getRangeError(String value, YangDataTypes type) {
        StringBuilder msg = new StringBuilder();
        switch (type) {
            case UINT8:
                if (Integer.valueOf(value) < YangUint8.MIN_VALUE) {
                    msg.append("YANG file error : ")
                            .append(value)
                            .append(" is lesser than minimum value ")
                            .append(YangUint8.MIN_VALUE).append(".");
                } else if (Integer.valueOf(value) > YangUint8.MAX_VALUE) {
                    msg.append("YANG file error : ")
                            .append(value)
                            .append(" is greater than maximum value ")
                            .append(YangUint8.MAX_VALUE).append(".");
                }
                break;
            case UINT16:
                if (Integer.valueOf(value) < YangUint16.MIN_VALUE) {
                    msg.append("YANG file error : ")
                            .append(value)
                            .append(" is lesser than minimum value ")
                            .append(YangUint16.MIN_VALUE).append(".");
                } else if (Integer.valueOf(value) > YangUint16.MAX_VALUE) {
                    msg.append("YANG file error : ")
                            .append(value)
                            .append(" is greater than maximum value ")
                            .append(YangUint16.MAX_VALUE).append(".");
                }
                break;
            case UINT32:
                if (Long.parseLong(value) < YangUint32.MIN_VALUE) {
                    msg.append("YANG file error : ")
                            .append(value)
                            .append(" is lesser than minimum value ")
                            .append(YangUint32.MIN_VALUE).append(".");
                } else if (Long.parseLong(value) > YangUint32.MAX_VALUE) {
                    msg.append("YANG file error : ")
                            .append(value)
                            .append(" is greater than maximum value ")
                            .append(YangUint32.MAX_VALUE).append(".");
                }
                break;
            case UINT64:
                BigInteger val = new BigInteger(value);
                if (val.compareTo(YangUint64.MIN_VALUE) == -1) {
                    msg.append("YANG file error : ")
                            .append(value)
                            .append(" is lesser than minimum value ")
                            .append(YangUint64.MIN_VALUE).append(".");
                } else if (val.compareTo(YangUint64.MAX_VALUE) == 1) {
                    msg.append("YANG file error : ")
                            .append(value)
                            .append(" is greater than maximum value ")
                            .append(YangUint64.MAX_VALUE).append(".");
                }
                break;
            case DECIMAL64:
                BigDecimal decVal = new BigDecimal(value);
                BigDecimal min = new BigDecimal("-9.2233720368547763E+17");
                BigDecimal max = new BigDecimal("9.2233720368547763E+17");

                if (decVal.compareTo(min) == -1) {
                    msg.append("YANG file error : ")
                            .append(value)
                            .append(" is less than minimum value ")
                            .append(min).append(".");
                } else if (decVal.compareTo(max) == 1) {
                    msg.append("YANG file error : ")
                            .append(value)
                            .append(" is greater than maximum value ")
                            .append(max).append(".");
                }
                break;
            default:
                return null;
        }
        return msg.toString();
    }

    /**
     * Utility function to get the error string of the respective data type.
     *
     * @param value    is associated with value of the YANG node value
     * @param dataType is associated with YANG data type
     * @return the error string
     */

    private static String getErrorString(String value, YangDataTypes dataType) {
        StringBuilder msg = new StringBuilder();
        switch (dataType) {
            case UINT8:
            case UINT16:
            case UINT32:
            case UINT64:
            case INT8:
            case INT16:
            case INT32:
            case INT64:
            case DECIMAL64:
                msg.append("YANG file error : Input value ").append("\"")
                        .append(value).append("\"")
                        .append(" is not a valid ")
                        .append(dataType.toString().toLowerCase())
                        .append(".");
                break;
            case BITS:
            case ENUMERATION:
            case BINARY:
            case STRING:
            case BOOLEAN:
            case UNION:
                msg.append("YANG file error : Input value ").append("\"")
                        .append(value).append("\"")
                        .append(" is not a valid ").append(dataType);
                break;
            case EMPTY:
                msg.append("YANG file error : Input value ").append("\"")
                        .append(value).append("\"")
                        .append(" is not allowed for a data type ")
                        .append(dataType);
                break;
            default:
                return null;
        }
        return msg.toString();
    }
}