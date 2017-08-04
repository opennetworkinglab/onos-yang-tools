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
import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeType;
import org.onosproject.yang.compiler.datamodel.YangRangeInterval;
import org.onosproject.yang.compiler.datamodel.YangRangeRestriction;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangTypeDef;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangInt32;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangUint8;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;

import java.io.IOException;
import java.util.ListIterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DERIVED;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT32;

/**
 * Test cases for range restriction listener.
 */
public class RangeRestrictionListenerTest {

    private final YangUtilsParserManager manager = new YangUtilsParserManager();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Checks valid range statement as sub-statement of leaf statement.
     */
    @Test
    public void processValidRangeStatement() throws IOException, ParserException {

        YangNode node = manager.getDataModel("src/test/resources/ValidRange" +
                                                     "Statement.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();

        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("int32"));
        assertThat(type.getDataType(), is(INT32));
        YangRangeRestriction ranRes = (YangRangeRestriction) type
                .getDataTypeExtendedInfo();

        ListIterator<YangRangeInterval> rlIt = ranRes
                .getAscendingRangeIntervals().listIterator();
        YangRangeInterval range = rlIt.next();
        assertThat(((YangInt32) range.getStartValue()).getValue(), is(1));
        assertThat(((YangInt32) range.getEndValue()).getValue(), is(4));
    }

    /**
     * Checks valid range statement as sub-statement of leaf-list.
     */
    @Test
    public void processRangeStatementInsideLeafList() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/RangeStatementInsideLeafList.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeafList> it = yangNode.getListOfLeafList()
                .listIterator();
        YangLeafList ll = it.next();

        YangType<?> type = ll.getDataType();

        assertThat(ll.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("int32"));
        assertThat(type.getDataType(), is(INT32));
        YangRangeRestriction ranRes = (YangRangeRestriction) type
                .getDataTypeExtendedInfo();

        ListIterator<YangRangeInterval> rlIt = ranRes
                .getAscendingRangeIntervals().listIterator();
        YangRangeInterval range = rlIt.next();

        assertThat(((YangInt32) range.getStartValue()).getValue(), is(1));
        assertThat(((YangInt32) range.getEndValue()).getValue(), is(4));
    }

    /**
     * Checks valid range statement with one interval.
     */
    @Test
    public void processRangeWithOneInterval() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/RangeWithOneInterval.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeafList> it = yangNode.getListOfLeafList()
                .listIterator();
        YangLeafList ll = it.next();

        YangType<?> type = ll.getDataType();

        assertThat(ll.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("int32"));
        assertThat(type.getDataType(), is(INT32));
        YangRangeRestriction ranRes = (YangRangeRestriction) type
                .getDataTypeExtendedInfo();

        ListIterator<YangRangeInterval> rlIt = ranRes
                .getAscendingRangeIntervals().listIterator();
        YangRangeInterval range = rlIt.next();

        assertThat(((YangInt32) range.getStartValue()).getValue(), is(1));
        assertThat(((YangInt32) range.getEndValue()).getValue(), is(1));
    }

    /**
     * Checks valid range statement with min and max.
     */
    @Test
    public void processRangeWithMinMax() throws IOException, ParserException {

        YangNode node = manager.getDataModel("src/test/resources/RangeWithM" +
                                                     "inMax.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeafList> it = yangNode.getListOfLeafList()
                .listIterator();
        YangLeafList ll = it.next();

        YangType<?> type = ll.getDataType();

        assertThat(ll.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("int32"));
        assertThat(type.getDataType(), is(INT32));
        YangRangeRestriction ranRes = (YangRangeRestriction) type
                .getDataTypeExtendedInfo();

        ListIterator<YangRangeInterval> rlIt = ranRes
                .getAscendingRangeIntervals().listIterator();
        YangRangeInterval range = rlIt.next();

        assertThat(((YangInt32) range.getStartValue()).getValue(),
                   is(-2147483648));
        assertThat(((YangInt32) range.getEndValue()).getValue(),
                   is(2147483647));
    }

    /**
     * Checks valid range statement with invalid integer pattern.
     */
    @Test
    public void processRangeWithInvalidIntegerPattern() throws IOException, ParserException {
        thrown.expect(ParserException.class);
        thrown.expectMessage("YANG file error : Input value \"a\" is not " +
                                     "a valid int32.");
        manager.getDataModel("src/test/resources/RangeWithInval" +
                                     "idIntegerPattern.yang");
    }

    /**
     * Checks valid range statement with description.
     */
    @Test
    public void processRangeSubStatements() throws IOException, ParserException {

        YangNode node = manager.getDataModel("src/test/resources/" +
                                                     "RangeSubStatements.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();

        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("int32"));
        assertThat(type.getDataType(), is(INT32));
        YangRangeRestriction ranRes = (YangRangeRestriction) type
                .getDataTypeExtendedInfo();

        assertThat(ranRes.getDescription(), is("\"range description\""));
        assertThat(ranRes.getReference(), is("\"range reference\""));

        ListIterator<YangRangeInterval> rlIt = ranRes
                .getAscendingRangeIntervals().listIterator();
        YangRangeInterval range = rlIt.next();
        assertThat(((YangInt32) range.getStartValue()).getValue(), is(1));
        assertThat(((YangInt32) range.getEndValue()).getValue(), is(4));
        assertThat(((YangInt32) range.getEndValue()).getValue(), is(4));
    }

    /**
     * Checks whether space can be allowed when range statement is present.
     */
    @Test
    public void processRangeStatementWithSpace() throws IOException, ParserException {

        YangNode node = manager.getDataModel("src/test/resources/RangeSta" +
                                                     "tementWithSpace.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();

        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("int32"));
        assertThat(type.getDataType(), is(INT32));
        YangRangeRestriction ranRes = (YangRangeRestriction) type
                .getDataTypeExtendedInfo();

        ListIterator<YangRangeInterval> rlIt = ranRes
                .getAscendingRangeIntervals().listIterator();
        YangRangeInterval range = rlIt.next();
        assertThat(((YangInt32) range.getStartValue()).getValue(), is(1));
        assertThat(((YangInt32) range.getEndValue()).getValue(), is(4));
    }

    /**
     * Checks range statement in typedef with description and reference.
     *
     * @throws IOException     when IO operation fails
     * @throws ParserException when parsing fails
     */
    @Test
    public void processRangeStatementInTypeDef() throws IOException, ParserException {
        YangNode node = manager.getDataModel("src/test/resources/Range" +
                                                     "WithTypedef.yang");
        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();

        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataType(), is(DERIVED));
        YangDerivedInfo info = (YangDerivedInfo) type
                .getDataTypeExtendedInfo();

        YangRangeRestriction ranRes1 = info.getRangeRes();

        assertThat(ranRes1.getDescription(), is("\"range description\""));
        assertThat(ranRes1.getReference(), is("\"range reference\""));

        ListIterator<YangRangeInterval> rlIt1 = ranRes1
                .getAscendingRangeIntervals().listIterator();
        YangRangeInterval range1 = rlIt1.next();
        assertThat(((YangUint8) range1.getStartValue()).getValue(),
                   is((short) 2));
        assertThat(((YangUint8) range1.getEndValue()).getValue(),
                   is((short) 100));

        YangTypeDef typeDef = (YangTypeDef) yangNode.getChild();
        YangRangeRestriction ranRes = (YangRangeRestriction) typeDef
                .getTypeDefBaseType().getDataTypeExtendedInfo();
        assertThat(ranRes.getDescription(),
                   is("\"typedef description\""));
        assertThat(ranRes.getReference(), is("\"typedef reference\""));

        ListIterator<YangRangeInterval> rlIt2 = ranRes
                .getAscendingRangeIntervals().listIterator();
        YangRangeInterval range2 = rlIt2.next();
        assertThat(((YangUint8) range2.getStartValue()).getValue(),
                   is((short) 1));
        assertThat(((YangUint8) range2.getEndValue()).getValue(),
                   is((short) (100)));
    }

}
