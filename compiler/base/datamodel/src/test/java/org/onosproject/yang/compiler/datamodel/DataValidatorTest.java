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

import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.DataTypeException;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangInt8;

import static org.junit.Assert.fail;
import static org.onosproject.yang.compiler.datamodel.CheckValidationTest.dataValidation;
import static org.onosproject.yang.compiler.datamodel.CheckValidationTest.rangeCheck;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.BINARY;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.BITS;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.BOOLEAN;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DECIMAL64;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.EMPTY;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT16;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT32;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT64;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT8;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT16;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT32;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT64;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT8;

/**
 * Unit tests for data type validations.Creates and validates different data
 * types against their data types covering different scenarios.
 */
public class DataValidatorTest {

    /*
     * Creating nodes of type INT8 and testing the data validation.
     */
    @Test
    public void negativeIntTest1() throws DataModelException {

        YangType<?> newNode = new YangType<>();
        newNode.setDataType(INT8);
        dataValidation(newNode, "-129");
        dataValidation(newNode, "128");
        dataValidation(newNode, " ");
    }

    /*
     * Creating nodes of type INT16 and testing the data validation.
     */
    @Test
    public void negativeIntTest2() throws DataModelException {

        YangType<?> newNode = new YangType<>();
        newNode.setDataType(INT16);
        dataValidation(newNode, "-32769");
        dataValidation(newNode, "32768");
        dataValidation(newNode, " ");
    }

    /*
     * Creating nodes of type INT32 and testing the data validation.
     */
    @Test
    public void negativeIntTest3() throws DataModelException {

        YangType<?> newNode = new YangType<>();
        newNode.setDataType(INT32);
        dataValidation(newNode, "-2147483649");
        dataValidation(newNode, "2147483648");
        dataValidation(newNode, " ");
    }

    /**
     * Creating nodes of type INT64 and testing the data validation.
     */
    @Test
    public void negativeIntTest4() throws DataModelException {

        YangType<?> newNode = new YangType<>();
        newNode.setDataType(INT64);
        dataValidation(newNode, "-9223372036854775809");
        dataValidation(newNode, "9223372036854775808");
        dataValidation(newNode, " ");
    }

    /**
     * Creating nodes of type UINT8 and testing the data validation.
     */
    @Test
    public void negativeUintTest1() throws DataModelException {

        YangType<?> newNode = new YangType<>();
        newNode.setDataType(UINT8);
        rangeCheck(newNode, "-1");
        rangeCheck(newNode, "256");
        dataValidation(newNode, " ");
    }

    /**
     * Creating nodes of type UINT16 and testing the data validation.
     */
    @Test
    public void negativeUintTest2() throws DataModelException {

        YangType<?> newNode = new YangType<>();
        newNode.setDataType(UINT16);
        rangeCheck(newNode, "-2");
        rangeCheck(newNode, "65536");
        dataValidation(newNode, " ");
    }

    /**
     * Creating nodes of type UINT32 and testing the data validation.
     */
    @Test
    public void negativeUintTest3() throws DataModelException {

        YangType<?> newNode = new YangType<>();
        newNode.setDataType(UINT32);
        rangeCheck(newNode, "-3");
        rangeCheck(newNode, "4294967298");
        dataValidation(newNode, " ");
    }

    /**
     * Creating nodes of type UINT64 and testing the data validation.
     */
    @Test
    public void negativeUintTest4() throws DataModelException {

        YangType<?> newNode = new YangType<>();
        newNode.setDataType(UINT64);
        rangeCheck(newNode, "-9223372036854775809");
        rangeCheck(newNode, "18446744073709551616");
        dataValidation(newNode, " ");
    }

    /**
     * Creating nodes of type BOOLEAN and testing the data validation.
     */
    @Test
    public void negativeBool() throws DataModelException {
        YangType<?> newNode = new YangType<>();
        newNode.setDataType(BOOLEAN);
        dataValidation(newNode, " ");
        dataValidation(newNode, "10");
        dataValidation(newNode, "ABCD");
    }

    /**
     * Creating nodes of type DECIMAL64 and testing the data validation.
     */
    @Test
    public void negativeDec64() throws DataModelException {

        YangType<YangDecimal64> newNode = new YangType<>();
        newNode.setDataType(DECIMAL64);
        YangDecimal64 decimal64Node = new YangDecimal64();
        newNode.setDataTypeExtendedInfo(decimal64Node);
        rangeCheck(newNode, "-932337203685477580.9");
        rangeCheck(newNode, "932337203685477580.8");
        dataValidation(newNode, " ");
    }

    /**
     * Creating nodes of type BIT and testing valid and invalid values.
     */
    @Test
    public void positiveBits() throws DataModelException {

        YangType<YangBits> newNode = new YangType<>();
        newNode.setDataType(BITS);
        YangBits bitNode = new YangBits();
        YangBit yb1 = new YangBit();
        yb1.setBitName("test1");
        bitNode.addBitInfo(yb1);
        newNode.setDataTypeExtendedInfo(bitNode);
        try {
            newNode.isValidValue("test1");
        } catch (DataModelException e) {
            fail("'test1' is valid. Unexpected " + e.getMessage());
        }
        try {
            newNode.isValidValue("test2");
            fail("Expecting exception.");
        } catch (DataTypeException e) {
        }
        try {
            newNode.isValidValue(null);
        } catch (DataModelException e) {
            fail("null is valid. Unexpected " + e.getMessage());
        }
    }

    /**
     * Creating nodes of type BIT and testing the data validation.
     */
    @Test
    public void negativeBits() throws DataModelException {

        YangType<YangBits> newNode = new YangType<>();
        newNode.setDataType(BITS);
        YangBits bitNode = new YangBits();
        newNode.setDataTypeExtendedInfo(bitNode);
        dataValidation(newNode, " ");
        dataValidation(newNode, "0");
        dataValidation(newNode, "default");
        dataValidation(newNode, "1");
    }

    /**
     * Creating nodes of type EMPTY and testing the data validation.
     */
    @Test
    public void negativeEmpty() throws DataModelException {

        YangType<?> newNode = new YangType<>();
        newNode.setDataType(EMPTY);
        dataValidation(newNode, "0");
        dataValidation(newNode, "ABCD");
    }

    /**
     * Creating nodes of type BINARY and testing the data validation.
     */
    @Test
    public void negativeBin() throws DataModelException {

        YangType<?> newNode = new YangType<>();
        newNode.setDataType(BINARY);
        dataValidation(newNode, "");
    }

    /**
     * Creating nodes of type ENUMERATION and testing the data validation.
     */
    @Test
    public void negativeEnum() throws DataModelException {

        YangType<YangEnumeration> newNode = new YangType<>();
        newNode.setDataType(YangDataTypes.ENUMERATION);
        YangEnum enum1 = new YangEnum();
        enum1.setNamedValue("sample1");
        enum1.setValue(10);
        YangEnum enum2 = new YangEnum();
        enum2.setNamedValue("sample2");
        enum2.setValue(20);
        YangEnumeration yEnumeration = new YangEnumeration() {
            @Override
            public String getJavaPackage() {
                return null;
            }

            @Override
            public String getJavaClassNameOrBuiltInType() {
                return null;
            }

            @Override
            public String getJavaAttributeName() {
                return null;
            }
        };
        yEnumeration.addEnumInfo(enum1);
        yEnumeration.addEnumInfo(enum2);
        newNode.setDataTypeExtendedInfo(yEnumeration);
        dataValidation(newNode, " ");
        dataValidation(newNode, "123");
    }

    /**
     * Creating nodes of type UNION and testing the data validation.
     */
    @Test
    public void negativeUnion() throws DataModelException {

        YangType<YangUnion> newNode = new YangType<>();
        newNode.setDataType(YangDataTypes.UNION);
        YangUnion union = new YangUnion() {
            @Override
            public String getJavaPackage() {
                return null;
            }

            @Override
            public String getJavaClassNameOrBuiltInType() {
                return null;
            }

            @Override
            public String getJavaAttributeName() {
                return null;
            }
        };
        YangType<YangInt8> typeInt1 = new YangType<>();
        typeInt1.setDataType(INT8);
        YangType<YangDecimal64> typeInt2 = new YangType<>();
        typeInt2.setDataType(BOOLEAN);
        union.addType(typeInt1);
        union.addType(typeInt2);
        newNode.setDataTypeExtendedInfo(union);
        dataValidation(newNode, "abcd");
        dataValidation(newNode, "-129");
    }
}
