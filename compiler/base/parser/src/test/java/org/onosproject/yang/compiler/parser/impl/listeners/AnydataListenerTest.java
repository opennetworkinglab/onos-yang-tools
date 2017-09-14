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

package org.onosproject.yang.compiler.parser.impl.listeners;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.compiler.datamodel.YangAnydata;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeType;
import org.onosproject.yang.compiler.datamodel.YangStatusType;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;

import java.io.IOException;
import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Test cases for testing anydata listener.
 */
public class AnydataListenerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final YangUtilsParserManager manager = new YangUtilsParserManager();

    /**
     * Checks anydata statement as sub-statement of module.
     */
    @Test
    public void processModuleSubStatementAnydata() throws IOException,
            ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/ModuleSubStatementAnydata.yang");

        assertThat((node instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        // Check whether the anydata is child of module
        YangAnydata anyData = (YangAnydata) yangNode.getChild();
        assertThat(anyData.getName(), is("valid"));
    }

    /**
     * Checks if anydata identifier in module is duplicate.
     */
    @Test(expected = ParserException.class)
    public void processModuleDuplicateAnydata() throws IOException,
            ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/ModuleDuplicateAnydata.yang");
    }

    /**
     * Checks if anydata identifier in list is duplicate.
     */
    @Test(expected = ParserException.class)
    public void processListDuplicateAnydata() throws IOException,
            ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/ListDuplicateAnydata.yang");
    }

    /**
     * Checks if anydata identifier collides with list at same level.
     */
    @Test(expected = ParserException.class)
    public void processDuplicateAnydataAndList() throws IOException,
            ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/DuplicateAnydataAndList.yang");
    }

    /**
     * Checks anydata statement as sub-statement of list.
     */
    @Test
    public void processListSubStatementAnydata() throws IOException,
            ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/ListSubStatementAnydata.yang");

        assertThat((node instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        // Check whether the list is child of module
        YangList yangList1 = (YangList) yangNode.getChild();
        assertThat(yangList1.getName(), is("ospf"));

        Iterator<String> keyList = yangList1.getKeyList().iterator();
        assertThat(keyList.next(), is("process-id"));

        // Check whether the list is child of list
        YangAnydata yangAnydata = (YangAnydata) yangList1.getChild();
        assertThat(yangAnydata.getName(), is("interface"));
    }

    /**
     * Checks anydata with all its sub-statements.
     */
    @Test
    public void processAnydataSubStatements() throws IOException,
            ParserException {

        YangNode node = manager.getDataModel(
                "src/test/resources/AnydataSubStatements.yang");

        assertThat((node instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        // Check whether the anydata is child of module
        YangAnydata yangAnydata = (YangAnydata) yangNode.getChild();

        // Check whether anydata properties as set correctly.
        assertThat(yangAnydata.getName(), is("ospf"));

        assertThat(yangAnydata.isConfig(), is(true));
        assertThat(yangAnydata.getDescription(), is("\"anydata description\""));
        assertThat(yangAnydata.getStatus(), is(YangStatusType.CURRENT));
        assertThat(yangAnydata.getReference(), is("\"anydata reference\""));
    }

    /**
     * Checks cardinality of sub-statements of anydata.
     */
    @Test
    public void processAnydataSubStatementCardinality() throws IOException,
            ParserException {
        thrown.expect(ParserException.class);
        thrown.expectMessage("YANG file error: \"reference\" is defined more" +
                                     " than once in \"anydata valid\".");
        YangNode node = manager.getDataModel(
                "src/test/resources/AnydataSubStatementCardinality.yang");
    }

    /**
     * Checks anydata as root node.
     */
    @Test
    public void processAnydataRootNode() throws IOException, ParserException {
        thrown.expect(ParserException.class);
        thrown.expectMessage("no viable alternative at input 'anydata'");
        YangNode node = manager.getDataModel(
                "src/test/resources/AnydataRootNode.yang");
    }

    /**
     * Checks invalid identifier for anydata statement.
     */
    @Test
    public void processAnydataInvalidIdentifier() throws IOException,
            ParserException {
        thrown.expect(ParserException.class);
        thrown.expectMessage(
                "YANG file error : anydata name 1valid is not valid.");
        YangNode node = manager.getDataModel(
                "src/test/resources/AnydataInvalidIdentifier.yang");
    }

    /**
     * Checks invalid identifier for anydata statement.
     */
    @Test
    public void processAnydataInvalidVersionTest() throws IOException,
            ParserException {
        thrown.expect(ParserException.class);
        thrown.expectMessage(
                "YANG file error : anydata with name valid at line number 5 " +
                        "in src/test/resources/AnydataInvalidVersion.yang is feature of YANG version 1.1");
        YangNode node = manager.getDataModel(
                "src/test/resources/AnydataInvalidVersion.yang");
    }
}
