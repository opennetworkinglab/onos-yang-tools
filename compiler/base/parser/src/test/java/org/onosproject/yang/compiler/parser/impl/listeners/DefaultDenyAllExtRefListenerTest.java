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

import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangContainer;
import org.onosproject.yang.compiler.datamodel.YangExtension;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeType;
import org.onosproject.yang.compiler.datamodel.YangNotification;
import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for testing default-deny-all extension reference listener.
 */
public class DefaultDenyAllExtRefListenerTest {

    private final YangUtilsParserManager manager = new YangUtilsParserManager();

    /**
     * Checks extension reference statement as sub-statement of module.
     */
    @Test
    public void processDefaultDenyAllExtensionRefStatement() throws IOException, ParserException {

        YangNode node = manager
                .getDataModel("src/test/resources/DefaultDenyAllExtensionRefTest.yang");

        assertThat((node instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));

        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("default-deny-all-extension-ref-test"));

        YangExtension extension = yangNode.getExtensionList().iterator().next();
        assertThat(extension.getName(), is("default-deny-all"));
        assertThat(extension.getDescription(), is("\"Used to indicate that the data model node\n       "
                + "controls a very sensitive security system parameter.\""));

        YangContainer yangContainer = (YangContainer) yangNode.getChild();
        assertThat(yangContainer.getName(), is("test"));
        assertNotNull(yangContainer.getDefaultDenyAll());

        assertEquals(2, yangContainer.getListOfLeaf().size());
        YangLeaf leaf1 = yangContainer.getListOfLeaf().get(0);
        assertThat(leaf1.getName(), is("test1"));
        assertFalse(leaf1.getDefaultDenyAll());

        YangLeaf leaf2 = yangContainer.getListOfLeaf().get(1);
        assertThat(leaf2.getName(), is("test2"));
        assertTrue(leaf2.getDefaultDenyAll());

        assertEquals(1, yangContainer.getListOfLeafList().size());
        YangLeafList leaffList3 = yangContainer.getListOfLeafList().get(0);
        assertThat(leaffList3.getName(), is("test3"));
        assertTrue(leaffList3.getDefaultDenyAll());

        YangList list4 = (YangList) yangContainer.getChild();
        assertThat(list4.getName(), is("test4"));
        assertTrue(list4.getDefaultDenyAll());

        YangRpc testrpc = (YangRpc) yangContainer.getNextSibling();
        assertThat(testrpc.getName(), is("testrpc"));
        assertTrue(testrpc.getDefaultDenyAll());

        YangNotification testNotification =
                            (YangNotification) testrpc.getNextSibling();
        assertThat(testNotification.getName(), is("testnotif"));
        assertTrue(testNotification.getDefaultDenyAll());
    }
}
