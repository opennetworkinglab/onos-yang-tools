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
import org.onosproject.yang.compiler.datamodel.YangDeviateAdd;
import org.onosproject.yang.compiler.datamodel.YangDeviateDelete;
import org.onosproject.yang.compiler.datamodel.YangDeviateReplace;
import org.onosproject.yang.compiler.datamodel.YangDeviation;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.datamodel.YangNodeType.MODULE_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.UNRESOLVED;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT8;

/**
 * Test cases for testing deviation listener.
 */
public class DeviationListenerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final YangUtilsParserManager manager = new YangUtilsParserManager();

    /**
     * Checks deviation statement as sub-statement of module.
     */
    @Test
    public void processDeviationNotSupported() throws IOException,
            ParserException {

        YangNode node = manager.getDataModel("src/test/resources/" +
                                                     "ValidDeviationNotSupported.yang");

        assertThat((node instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        // Check whether the container is child of module
        YangDeviation deviation = (YangDeviation) yangNode.getChild();
        assertThat(deviation.getName(), is("/base:system/base:daytime"));
        assertThat(deviation.getDescription(), is("\"desc\""));
        assertThat(deviation.getReference(), is("\"ref\""));
        assertThat(deviation.isDeviateNotSupported(), is(true));
        List<YangAtomicPath> targetNode = deviation.getTargetNode();
        assertThat(targetNode.get(0).getNodeIdentifier().getName(),
                   is("system"));
        assertThat(targetNode.get(1).getNodeIdentifier().getName(),
                   is("daytime"));
        assertThat(targetNode.get(0).getNodeIdentifier().getPrefix(),
                   is("base"));
        assertThat(deviation.getResolvableStatus(), is(UNRESOLVED));
    }

    /**
     * Checks deviation add statement as sub-statement of module.
     */
    @Test
    public void processDeviationAddStatement() throws IOException,
            ParserException {

        YangNode node = manager.getDataModel("src/test/resources/" +
                                                     "ValidDeviateAdd.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        YangDeviation deviation = (YangDeviation) yangNode.getChild();
        assertThat(deviation.getName(), is("/base:system/base:user/base:type"));
        assertThat(deviation.isDeviateNotSupported(), is(false));
        List<YangAtomicPath> targetNode = deviation.getTargetNode();
        assertThat(targetNode.get(0).getNodeIdentifier().getName(),
                   is("system"));
        assertThat(targetNode.get(0).getNodeIdentifier().getPrefix(),
                   is("base"));
        assertThat(targetNode.get(1).getNodeIdentifier().getName(),
                   is("user"));
        assertThat(targetNode.get(2).getNodeIdentifier().getName(),
                   is("type"));

        YangDeviateAdd deviateAdd = deviation.getDeviateAdd().get(0);
        assertThat(deviateAdd.getUnits(), is("\"units\""));
        assertThat(deviateAdd.getListOfMust().get(0).getConstraint(),
                   is("/base:system"));
        assertThat(deviateAdd.getDefaultValueInString(), is("admin"));
        assertThat(deviateAdd.isConfig(), is(true));
        assertThat(deviateAdd.isMandatory(), is(true));
        assertThat(deviateAdd.getMinElements().getMinElement(), is(0));
        assertThat(deviateAdd.getMaxElements().getMaxElement(), is(12343));
        assertThat(deviation.getResolvableStatus(), is(UNRESOLVED));
    }

    /**
     * Checks deviation delete statement as sub-statement of module.
     */
    @Test
    public void processDeviationDeleteStatement() throws IOException,
            ParserException {

        YangNode node = manager.getDataModel("src/test/resources/" +
                                                     "ValidDeviateDelete.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        YangDeviation deviation = (YangDeviation) yangNode.getChild();
        assertThat(deviation.getName(), is("/base:system"));
        assertThat(deviation.isDeviateNotSupported(), is(false));
        List<YangAtomicPath> targetNode = deviation.getTargetNode();
        assertThat(targetNode.get(0).getNodeIdentifier().getName(),
                   is("system"));
        assertThat(targetNode.get(0).getNodeIdentifier().getPrefix(),
                   is("base"));

        YangDeviateDelete deviateDelete = deviation.getDeviateDelete().get(0);
        assertThat(deviateDelete.getUnits(), is("\"units\""));
        assertThat(deviateDelete.getListOfMust().get(0).getConstraint(),
                   is("daytime or time"));
        assertThat(deviateDelete.getDefaultValueInString(), is("defaultValue"));
        assertThat(deviation.getResolvableStatus(), is(UNRESOLVED));
    }

    /**
     * Checks deviation replace statement as sub-statement of module.
     */
    @Test
    public void processDeviationReplaceStatement() throws IOException,
            ParserException {

        YangNode node = manager.getDataModel("src/test/resources/" +
                                                     "ValidDeviateReplace.yang");

        assertThat((node instanceof YangModule), is(true));
        assertThat(node.getNodeType(), is(MODULE_NODE));
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        YangDeviation deviation = (YangDeviation) yangNode.getChild();
        assertThat(deviation.getName(), is("/base:system/base:name-server"));
        assertThat(deviation.isDeviateNotSupported(), is(false));
        List<YangAtomicPath> targetNode = deviation.getTargetNode();
        assertThat(targetNode.get(0).getNodeIdentifier().getName(),
                   is("system"));
        assertThat(targetNode.get(0).getNodeIdentifier().getPrefix(),
                   is("base"));
        assertThat(targetNode.get(1).getNodeIdentifier().getName(),
                   is("name-server"));

        YangDeviateReplace replace = deviation.getDeviateReplace().get(0);
        assertThat(replace.getDataType().getDataType(), is(INT8));
        assertThat(replace.getUnits(), is("\"units\""));
        assertThat(replace.getDefaultValueInString(), is("0"));
        assertThat(replace.isConfig(), is(true));
        assertThat(replace.isMandatory(), is(true));
        assertThat(replace.getMinElements().getMinElement(), is(0));
        assertThat(replace.getMaxElements().getMaxElement(), is(3));
        assertThat(deviation.getResolvableStatus(), is(UNRESOLVED));
    }

    /**
     * Checks deviation unsupported statement and deviate add as
     * sub-statement of module.
     */
    @Test
    public void processInvalidDeviateStatement() throws
            IOException, ParserException {
        String error = "YANG file error: Either deviate-not-supported-stmt or" +
                " deviate-replace should be present in deviation" +
                " /base:system/base:daytime";
        thrown.expect(ParserException.class);
        thrown.expectMessage(error);
        YangNode node = manager.getDataModel("src/test/resources/InvalidDeviateStatement.yang");
    }
}

