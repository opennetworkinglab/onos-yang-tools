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

import org.hamcrest.core.Is;
import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeType;
import org.onosproject.yang.compiler.datamodel.YangPatternRestriction;
import org.onosproject.yang.compiler.datamodel.YangStringRestriction;
import org.onosproject.yang.compiler.datamodel.YangTypeDef;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Test cases for pattern restriction listener.
 */
public class PatternRestrictionListenerTest {

    private final YangUtilsParserManager manager = new YangUtilsParserManager();

    /**
     * Checks valid pattern statement as sub-statement of leaf statement.
     */
    @Test
    public void processValidPatternStatement() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/ValidPatternStatement.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), Is.is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(leaf.getDataType().getDataTypeName(), is("string"));
        assertThat(leaf.getDataType().getDataType(), is(YangDataTypes.STRING));
        YangStringRestriction strRes = (YangStringRestriction) leaf
                .getDataType().getDataTypeExtendedInfo();
        ListIterator<YangPatternRestriction> patIt = strRes
                .getPatternResList().listIterator();
        assertThat(patIt.next().getPattern(), is("[a-zA-Z]"));

        leaf = it.next();

        assertThat(leaf.getName(), is("ipv4-address"));
        assertThat(leaf.getDataType().getDataTypeName(), is("string"));
        assertThat(leaf.getDataType().getDataType(), is(YangDataTypes.STRING));
        strRes = (YangStringRestriction) leaf.getDataType()
                .getDataTypeExtendedInfo();
        patIt = strRes.getPatternResList().listIterator();
        assertThat(patIt.next().getPattern(), is(
                "(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}" +
                        "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])" +
                        "(%[\\p{N}\\p{L}]+)?"));
    }

    /**
     * Checks valid pattern statement as sub-statement of leaf-list.
     */
    @Test
    public void processPatternStatementInsideLeafList() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/PatternStatementInsideLeafList.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeafList> it = yangNode.getListOfLeafList()
                .listIterator();
        YangLeafList ll = it.next();

        assertThat(ll.getName(), is("invalid-interval"));
        assertThat(ll.getDataType().getDataTypeName(), is("string"));
        assertThat(ll.getDataType().getDataType(), is(YangDataTypes.STRING));
        YangStringRestriction strRes = (YangStringRestriction) ll
                .getDataType().getDataTypeExtendedInfo();
        ListIterator<YangPatternRestriction> patIt = strRes.getPatternResList()
                .listIterator();
        assertThat(patIt.next().getPattern(), is("[a-zA-Z]"));
    }

    /**
     * Checks valid pattern statement as sub-statement of typedef.
     */
    @Test
    public void processPatternStatementInsideTypeDef() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/PatternStatementInsideTypeDef.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        YangTypeDef typedef = (YangTypeDef) yangNode.getChild();
        YangStringRestriction strRes = (YangStringRestriction) typedef
                .getTypeDefBaseType().getDataTypeExtendedInfo();

        List<YangPatternRestriction> patRes = strRes.getPatternResList();
        assertThat(patRes.listIterator().next().getPattern(), is("[a-zA-Z]"));
    }

    /**
     * Checks valid multiple pattern statements.
     */
    @Test
    public void processMultiplePatternStatement() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/MultiplePatternStatement.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeafList> it = yangNode.getListOfLeafList()
                .listIterator();
        YangLeafList ll = it.next();

        assertThat(ll.getName(), is("invalid-interval"));
        assertThat(ll.getDataType().getDataTypeName(), is("string"));
        assertThat(ll.getDataType().getDataType(), is(YangDataTypes.STRING));
        YangStringRestriction strRes = (YangStringRestriction) ll.getDataType()
                .getDataTypeExtendedInfo();
        ListIterator<YangPatternRestriction> patIt = strRes.getPatternResList()
                .listIterator();
        assertThat(patIt.next().getPattern(), is("[a-zA-Z]"));
    }

    /**
     * Checks valid pattern statement with plus symbol in pattern.
     */
    @Test
    public void processPatternStatementWithPlus() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/PatternStatementWithPlus.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeafList> it = yangNode.getListOfLeafList()
                .listIterator();
        YangLeafList ll = it.next();

        assertThat(ll.getName(), is("invalid-interval"));
        assertThat(ll.getDataType().getDataTypeName(), is("string"));
        assertThat(ll.getDataType().getDataType(), is(YangDataTypes.STRING));
        YangStringRestriction strRes = (YangStringRestriction) ll.getDataType()
                .getDataTypeExtendedInfo();
        ListIterator<YangPatternRestriction> patIt = strRes.getPatternResList()
                .listIterator();
        //FIXME: + should not be remove from the end.
        //assertThat(patternListIterator.next(), is("-[0-9]+|[0-9]+"));
    }

    /**
     * Checks valid pattern substatement.
     */
    @Test
    public void processPatternSubStatements() throws IOException, ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/PatternSubStatements.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(leaf.getDataType().getDataTypeName(), is("string"));
        assertThat(leaf.getDataType().getDataType(), is(YangDataTypes.STRING));
        YangStringRestriction strRes = (YangStringRestriction) leaf
                .getDataType().getDataTypeExtendedInfo();
        ListIterator<YangPatternRestriction> patIt = strRes.getPatternResList()
                .listIterator();
        YangPatternRestriction pat = patIt.next();
        assertThat(pat.getDescription(), is("\"pattern description\""));
        assertThat(pat.getReference(), is("\"pattern reference\""));
        assertThat(pat.getPattern(), is("[a-zA-Z]"));
    }

    /**
     * Checks invalid pattern sub-statement.
     */
    @Test(expected = ParserException.class)
    public void processInvalidPatternSubStatements() throws IOException, ParserException {
        YangNode node = manager.getDataModel(
                "src/test/resources/InvalidPatternSubStatements.yang");
    }
}
