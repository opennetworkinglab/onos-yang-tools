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
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeType;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Test cases for unique listener.
 */
public class UniqueListenerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final YangUtilsParserManager manager = new YangUtilsParserManager();

    /**
     * Checks unique statement as sub-statement of list.
     */
    @Test
    public void processListSubStatementUnique() throws IOException, ParserException {

        YangNode node = manager.getDataModel("src/test/resources/ListSubStatementUnique.yang");

        assertThat((node instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        // Check whether the list is child of module
        YangList yangList = (YangList) yangNode.getChild();
        assertThat(yangList.getName(), is("valid"));
        List<YangLeaf> leaves = yangList.getListOfLeaf();
        YangLeaf leaf = leaves.iterator().next();

        List<List<YangAtomicPath>> uniLeaves = yangList.getPathList();
    }

    /**
     * Check multiple unique values.
     */
    @Test
    public void processMultipleUniqueValues() throws IOException, ParserException {

        YangNode node = manager.getDataModel("src/test/resources/MultipleUniqueValues.yang");

        assertThat((node instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        // Check whether the list is child of module
        YangList yangList = (YangList) yangNode.getChild();
        assertThat(yangList.getName(), is("valid"));

        Iterator<List<YangAtomicPath>> pathListIt;
        Iterator<YangAtomicPath> pathIt;
        List<YangAtomicPath> path;
        YangAtomicPath atPath;

        pathListIt = yangList.getPathList().iterator();
        path = pathListIt.next();
        assertThat(path.size(), is(1));
        pathIt = path.iterator();
        atPath = pathIt.next();
        assertThat(atPath.getNodeIdentifier().getName(), is("ospf"));
        path = pathListIt.next();
        assertThat(path.size(), is(1));
        pathIt = path.iterator();
        atPath = pathIt.next();
        assertThat(atPath.getNodeIdentifier().getName(), is("isis"));
    }


    /**
     * Check single unique along its atomic path.
     */
    @Test
    public void processParsingUniqueValues() throws IOException,
            ParserException {
        YangNode node = manager.getDataModel(
                "src/test/resources/UniqueError.yang");
        assertThat((node instanceof YangModule), is(true));

        //Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));

        //Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("TestUnique"));

        //Check whether the list is child of module.
        YangList yangList = (YangList) yangNode.getChild();
        assertThat(yangList.getName(), is("resources"));

        Iterator<List<YangAtomicPath>> pathListIt;
        Iterator<YangAtomicPath> pathIt;
        List<YangAtomicPath> path;
        YangAtomicPath atPath;

        pathListIt = yangList.getPathList().iterator();
        path = pathListIt.next();
        assertThat(path.size(), is(3));
        pathIt = path.iterator();
        atPath = pathIt.next();
        assertThat(atPath.getNodeIdentifier().getName(), is("animal"));
        atPath = pathIt.next();
        assertThat(atPath.getNodeIdentifier().getName(), is("mammal"));
        atPath = pathIt.next();
        assertThat(atPath.getNodeIdentifier().getName(), is("abc"));
    }

    /**
     * Check unique for its descendent.
     */
    @Test
    public void processParsingUniqueDescendent() throws IOException,
            ParserException {
        thrown.expect(ParserException.class);
        thrown.expectMessage("YANG file error : The descendant path must not" +
                                     " start with a slash(/)");
        YangNode node = manager.getDataModel(
                "src/test/resources/UniqueDescendent.yang");
    }

    /**
     * Check the error for wrong holder of unique.
     */
    @Test
    public void processWrongHolderOfUnique() throws IOException,
            ParserException {
        thrown.expect(ParserException.class);
        YangNode node = manager.getDataModel(
                "src/test/resources/WrongHolderOfUnique.yang");
    }
}