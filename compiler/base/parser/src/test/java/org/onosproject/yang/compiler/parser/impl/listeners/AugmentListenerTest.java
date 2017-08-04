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
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeType;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;

import java.io.IOException;
import java.util.ListIterator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Test cases for testing augment listener functionality.
 */
public class AugmentListenerTest {

    private final YangUtilsParserManager manager = new YangUtilsParserManager();

    /**
     * Checks valid augment statement.
     */
    @Test
    public void processValidAugmentStatement() throws IOException, ParserException {

        YangNode node = manager.getDataModel("src/test/resources/ValidAugmentStatement.yang");

        assertThat(node instanceof YangModule, is(true));
        assertThat(node.getNodeType(), Is.is(YangNodeType.MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        YangAugment yangAugment = (YangAugment) yangNode.getChild();
        ListIterator<YangAtomicPath> absPathIterator = yangAugment.getTargetNode().listIterator();
        YangAtomicPath absPathIdentifier = absPathIterator.next();
        assertThat(absPathIdentifier.getNodeIdentifier().getPrefix(), is("if"));
        assertThat(absPathIdentifier.getNodeIdentifier().getName(), is("interfaces"));

        ListIterator<YangLeaf> leafIterator = yangAugment.getListOfLeaf().listIterator();
        YangLeaf leafInfo = leafIterator.next();

        assertThat(leafInfo.getName(), is("ds0ChannelNumber"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("ChannelNumber"));
        assertThat(leafInfo.getDataType().getDataType(), Is.is(YangDataTypes.DERIVED));
    }
}
