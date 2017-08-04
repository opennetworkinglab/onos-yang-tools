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

package org.onosproject.yang.compiler.parser.impl.listeners;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeType;
import org.onosproject.yang.compiler.datamodel.YangRangeInterval;
import org.onosproject.yang.compiler.datamodel.YangRangeRestriction;
import org.onosproject.yang.compiler.datamodel.YangStringRestriction;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangTypeDef;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangUint64;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ListIterator;

import static java.math.BigInteger.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.BINARY;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.STRING;

/**
 * Test cases for length restriction listener.
 */
public class LengthRestrictionListenerTest {

    private final YangUtilsParserManager manager = new YangUtilsParserManager();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Checks valid length statement as sub-statement of leaf statement.
     */
    @Test
    public void processValidLengthStatement() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/ValidLengthStatement.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();
        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("string"));
        assertThat(type.getDataType(), is(STRING));
        YangStringRestriction strRes = (YangStringRestriction) type
                .getDataTypeExtendedInfo();
        YangRangeRestriction lenRes = strRes.getLengthRestriction();

        ListIterator<YangRangeInterval> lenIt = lenRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range = lenIt.next();

        assertThat(((YangUint64) range.getStartValue()).getValue(),
                   is(valueOf(0)));
        assertThat(((YangUint64) range.getEndValue()).getValue(),
                   is(valueOf(100)));
    }

    /**
     * Checks valid length statement as sub-statement of leaf-list.
     */
    @Test
    public void processLengthStatementInsideLeafList() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/LengthStatementInsideLeafList.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeafList> it = yangNode.getListOfLeafList()
                .listIterator();
        YangLeafList ll = it.next();
        YangType<?> type = ll.getDataType();
        assertThat(ll.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("string"));
        assertThat(type.getDataType(), is(STRING));
        YangStringRestriction strRes = (YangStringRestriction) type
                .getDataTypeExtendedInfo();
        YangRangeRestriction lenRes = strRes.getLengthRestriction();

        ListIterator<YangRangeInterval> llIt = lenRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range = llIt.next();
        assertThat(((YangUint64) range.getStartValue()).getValue(),
                   is(valueOf(1)));
        assertThat(((YangUint64) range.getEndValue()).getValue(),
                   is(valueOf(100)));
    }

    /**
     * Checks valid length statement as sub-statement of typedef.
     */
    @Test
    public void processLengthStatementInsideTypeDef() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/LengthStatementInsideTypeDef.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        YangTypeDef typedef = (YangTypeDef) yangNode.getChild();
        YangStringRestriction strRes = (YangStringRestriction) typedef
                .getTypeDefBaseType().getDataTypeExtendedInfo();

        YangRangeRestriction lenRes = strRes.getLengthRestriction();
        assertThat(lenRes.getDescription(),
                   is("\"length-description typedef\""));
        assertThat(lenRes.getReference(), is("\"reference typedef\""));
        ListIterator<YangRangeInterval> it = lenRes.getAscendingRangeIntervals()
                .listIterator();
        YangRangeInterval rangeInterval = it.next();
        assertThat(((YangUint64) rangeInterval.getStartValue()).getValue(),
                   is(valueOf(1)));
        assertThat(((YangUint64) rangeInterval.getEndValue()).getValue(),
                   is(valueOf(100)));
    }

    /**
     * Checks valid length statement as sub-statement of binary statement.
     */
    @Test
    public void processValidBinaryLengthStatement() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/ValidBinaryLengthStatement.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();
        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("message"));
        assertThat(type.getDataTypeName(), is("binary"));
        assertThat(type.getDataType(), is(BINARY));
        YangRangeRestriction lenRes = (YangRangeRestriction) type
                .getDataTypeExtendedInfo();

        assertThat(lenRes.getDescription(), is("\"binary description\""));
        assertThat(lenRes.getReference(), is("\"binary reference\""));

        ListIterator<YangRangeInterval> llIt = lenRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range = llIt.next();

        assertThat(((YangUint64) range.getStartValue()).getValue(),
                   is(valueOf(4)));
        assertThat(((YangUint64) range.getEndValue()).getValue(),
                   is(valueOf(4)));
    }

    /**
     * Checks length statement with invalid type.
     */
    @Test
    public void processLengthWithInvalidType() throws IOException, ParserException {
        thrown.expect(ParserException.class);
        thrown.expectMessage("YANG file error : Length can only be used to" +
                                     " restrict the built-in type string/" +
                                     "binary or types derived from string/" +
                                     "binary");
        manager.getDataModel("src/test/resources/LengthWithInvalidType.yang");
    }

    /**
     * Checks length statement with only start interval.
     */
    @Test
    public void processLengthWithOneInterval() throws IOException, ParserException {


        YangNode node = manager.getDataModel(
                "src/test/resources/LengthWithOneInterval.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeafList> it = yangNode.getListOfLeafList()
                .listIterator();
        YangLeafList ll = it.next();
        YangType<?> type = ll.getDataType();

        assertThat(ll.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("string"));
        assertThat(type.getDataType(), is(STRING));
        YangStringRestriction strRes = (YangStringRestriction) type
                .getDataTypeExtendedInfo();
        YangRangeRestriction lenRes = strRes.getLengthRestriction();

        ListIterator<YangRangeInterval> llIt = lenRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range = llIt.next();
        assertThat(((YangUint64) range.getStartValue()).getValue(),
                   is(valueOf(1)));
        assertThat(((YangUint64) range.getEndValue()).getValue(),
                   is(valueOf(1)));
    }

    /**
     * Checks length statement with min and max.
     */
    @Test
    public void processLengthWithMinMax() throws IOException, ParserException {


        YangNode node = manager.getDataModel(
                "src/test/resources/LengthWithMinMax.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeafList> it = yangNode.getListOfLeafList()
                .listIterator();
        YangLeafList ll = it.next();
        YangType<?> type = ll.getDataType();

        assertThat(ll.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("string"));
        assertThat(type.getDataType(), is(STRING));
        YangStringRestriction strRes = (YangStringRestriction) type
                .getDataTypeExtendedInfo();
        YangRangeRestriction lenRes = strRes.getLengthRestriction();

        ListIterator<YangRangeInterval> llIt = lenRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range = llIt.next();
        assertThat(((YangUint64) range.getStartValue()).getValue(),
                   is(valueOf(0)));
        assertThat(((YangUint64) range.getEndValue()).getValue(),
                   is(new BigInteger("18446744073709551615")));
    }

    /**
     * Checks length statement with invalid integer pattern.
     */
    @Test
    public void processLengthWithInvalidIntegerPattern() throws IOException, ParserException {
        thrown.expect(ParserException.class);
        thrown.expectMessage("YANG file error : Input value \"a\" is not a" +
                                     " valid uint64.");
        manager.getDataModel("src/test/resources/LengthWithInvalidI" +
                                     "ntegerPattern.yang");
    }

    /**
     * Checks length statement with invalid interval.
     */
    @Test
    public void processLengthWithInvalidInterval() throws IOException, ParserException {
        thrown.expect(ParserException.class);
        thrown.expectMessage("YANG file error : 18446744073709551617 is gre" +
                                     "ater than maximum value " +
                                     "18446744073709551615.");
        manager.getDataModel("src/test/resources/LengthWithInvalidInter" +
                                     "val.yang");
    }

    /**
     * Checks valid length sub-statements.
     */
    @Test
    public void processLengthSubStatements() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/LengthSubStatements.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();
        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("string"));
        assertThat(type.getDataType(), is(STRING));
        YangStringRestriction strRes = (YangStringRestriction) type
                .getDataTypeExtendedInfo();
        YangRangeRestriction lenRes = strRes.getLengthRestriction();

        assertThat(lenRes.getDescription(), is("\"length description\""));
        assertThat(lenRes.getReference(), is("\"length reference\""));

        ListIterator<YangRangeInterval> llIt = lenRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range = llIt.next();

        assertThat(((YangUint64) range.getStartValue()).getValue(),
                   is(valueOf(0)));
        assertThat(((YangUint64) range.getEndValue()).getValue(),
                   is(valueOf(100)));
    }

    /**
     * Checks whether space can be allowed when length statement is present.
     */
    @Test
    public void processLengthStatementWithSpace() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/LengthStatementWithSpace.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();
        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("string"));
        assertThat(type.getDataType(), is(STRING));
        YangStringRestriction strRes = (YangStringRestriction) type
                .getDataTypeExtendedInfo();
        YangRangeRestriction lenRes = strRes.getLengthRestriction();

        ListIterator<YangRangeInterval> llIt = lenRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range = llIt.next();

        assertThat(((YangUint64) range.getStartValue()).getValue(),
                   is(valueOf(0)));
        assertThat(((YangUint64) range.getEndValue()).getValue(),
                   is(valueOf(100)));
    }
}
