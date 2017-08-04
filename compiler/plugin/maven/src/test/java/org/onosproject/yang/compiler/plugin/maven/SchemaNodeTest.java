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

package org.onosproject.yang.compiler.plugin.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNodeContextInfo;
import org.onosproject.yang.compiler.datamodel.YangSchemaNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.tool.YangCompilerManager;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.onosproject.yang.compiler.utils.io.impl.YangIoUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;

/**
 * Test cases for testing YANG schema node.
 */
public class SchemaNodeTest {

    private final YangCompilerManager utilManager =
            new YangCompilerManager();

    /**
     * Checks method to get schema node from map.
     *
     * @throws IOException            a violation in IO rule
     * @throws ParserException        a violation in parser rule
     * @throws MojoExecutionException a violation in mojo rule
     * @throws DataModelException     a violation in data model rule
     */
    @Test
    public void processSchemaNodeMap()
            throws IOException, ParserException,
            MojoExecutionException, DataModelException {

        YangIoUtils.deleteDirectory("target/schemaMap/");
        String searchDir = "src/test/resources/schemaMap";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();
        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir("target/schemaMap/");
        utilManager.translateToJava(yangPluginConfig);

        Iterator<YangNode> yangNodeIterator =
                utilManager.getYangNodeSet().iterator();
        YangNode rootNode = yangNodeIterator.next();

        // Validate the notification enum map
        assertThat(rootNode.getChild().getNextSibling(),
                   is(rootNode.getNotificationSchemaNode("TESTNOTIFICATION1")));

        // Validate the notification enum map shouldn't have container
        assertThat(rootNode.getNotificationSchemaNode("TESTCONTAINER"),
                   is(nullValue()));

        // Validation for RPC input/output node.
        YangNode yangRpcNode = rootNode.getChild().getNextSibling()
                .getNextSibling();
        YangSchemaNodeIdentifier yangInputNode = new YangSchemaNodeIdentifier();
        yangInputNode.setName("input");
        yangInputNode.setNameSpace(yangRpcNode.getNameSpace());
        assertThat(yangRpcNode.getChildSchema(yangInputNode).getSchemaNode(),
                   is(yangRpcNode.getChild()));

        YangSchemaNodeIdentifier yangOutputNode = new
                YangSchemaNodeIdentifier();
        yangOutputNode.setName("output");
        yangOutputNode.setNameSpace(yangRpcNode.getNameSpace());
        assertThat(yangRpcNode.getChildSchema(yangOutputNode).getSchemaNode(),
                   is(yangRpcNode.getChild().getNextSibling()));

        // Validate the input schema map
        YangSchemaNode yangInput = yangRpcNode.getChild();
        YangSchemaNodeIdentifier yangInputLeafNode = new
                YangSchemaNodeIdentifier();
        yangInputLeafNode.setName("image-name");
        yangInputLeafNode.setNameSpace(yangRpcNode.getNameSpace());
        assertThat(yangInput.getChildSchema(yangInputLeafNode),
                   is(notNullValue()));

        YangSchemaNode yangOutput = yangRpcNode.getChild().getNextSibling();
        YangSchemaNodeIdentifier yangOutputLeafNode = new
                YangSchemaNodeIdentifier();
        yangOutputLeafNode.setName("image-name");
        yangOutputLeafNode.setNameSpace(yangRpcNode.getNameSpace());
        assertThat(yangOutput.getChildSchema(yangOutputLeafNode),
                   is(notNullValue()));

        // Validate schema node
        assertThat(rootNode.getYsnContextInfoMap(), is(notNullValue()));
        Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> schemaMap =
                rootNode.getYsnContextInfoMap();
        YangSchemaNodeIdentifier yangSchemaNodeIdentifier =
                new YangSchemaNodeIdentifier();
        yangSchemaNodeIdentifier.setName("testcontainer");
        yangSchemaNodeIdentifier.setNameSpace(yangRpcNode.getNameSpace());
        assertThat(schemaMap.get(yangSchemaNodeIdentifier), is(notNullValue()));
        YangSchemaNodeContextInfo yangSchemaNodeContextInfo =
                schemaMap.get(yangSchemaNodeIdentifier);
        assertThat(yangSchemaNodeContextInfo.getSchemaNode(),
                   is(rootNode.getChild()));

        assertThat(rootNode.getChild().getYsnContextInfoMap(),
                   is(notNullValue()));
        Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> schemaMap2 =
                rootNode.getChild()
                        .getYsnContextInfoMap();
        yangSchemaNodeIdentifier.setName("testleaf");
        yangSchemaNodeIdentifier.setNameSpace(yangRpcNode.getNameSpace());
        assertThat(schemaMap2.get(yangSchemaNodeIdentifier),
                   is(notNullValue()));

        yangSchemaNodeIdentifier.setName("pretzel");
        yangSchemaNodeIdentifier.setNameSpace(yangRpcNode.getNameSpace());
        assertThat(schemaMap2.get(yangSchemaNodeIdentifier),
                   is(notNullValue()));

        assertThat(rootNode.getChild().getChild().getYsnContextInfoMap(),
                   is(notNullValue()));
        Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> schemaMap3 =
                rootNode.getChild().getChild()
                        .getYsnContextInfoMap();
        yangSchemaNodeIdentifier.setName("pretzel");
        yangSchemaNodeIdentifier.setNameSpace(yangRpcNode.getNameSpace());
        assertThat(schemaMap3.get(yangSchemaNodeIdentifier),
                   is(notNullValue()));
        YangSchemaNodeContextInfo yangSchemaNodeContextInfo3 =
                schemaMap3.get(yangSchemaNodeIdentifier);

        assertThat(rootNode.getChild().getChild().getChild()
                           .getYsnContextInfoMap(),
                   is(notNullValue()));
        Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> schemaMap4 =
                rootNode.getChild().getChild().getChild()
                        .getYsnContextInfoMap();
        yangSchemaNodeIdentifier.setName("pretzel");
        yangSchemaNodeIdentifier.setNameSpace(yangRpcNode.getNameSpace());
        assertThat(schemaMap4.get(yangSchemaNodeIdentifier),
                   is(notNullValue()));

        YangSchemaNodeContextInfo yangSchemaNodeContextInfo2 =
                schemaMap4.get(yangSchemaNodeIdentifier);
        List<YangLeaf> yangListOfLeaf = ((YangLeavesHolder) rootNode.getChild()
                .getChild().getChild()).getListOfLeaf();
        YangLeaf yangLeaf = yangListOfLeaf.get(0);
        assertThat(yangSchemaNodeContextInfo2.getSchemaNode(), is(yangLeaf));

        assertThat(yangSchemaNodeContextInfo3.getSchemaNode(), is(yangLeaf));
        assertThat(yangSchemaNodeContextInfo3.getContextSwitchedNode(),
                   is(rootNode.getChild().getChild().getChild()));

        YangIoUtils.deleteDirectory("target/schemaMap/");
    }

    /**
     * Checks that notification map shouldn't be present in other YANG node.
     *
     * @throws IOException            a violation in IO rule
     * @throws ParserException        a violation in parser rule
     * @throws MojoExecutionException a violation in mojo rule
     * @throws DataModelException     a violation in data model rule
     */
    @Test(expected = DataModelException.class)
    public void processNotificationEnumMapInvalidScenario()
            throws IOException,
            ParserException, MojoExecutionException,
            DataModelException {

        YangIoUtils.deleteDirectory("target/schemaMap/");
        String searchDir = "src/test/resources/schemaMap";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();
        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir("target/schemaMap/");
        utilManager.translateToJava(yangPluginConfig);

        Iterator<YangNode> yangNodeIterator =
                utilManager.getYangNodeSet().iterator();
        YangNode rootNode = yangNodeIterator.next();

        YangIoUtils.deleteDirectory("target/schemaMap/");

        rootNode.getChild().getNotificationSchemaNode("TESTNOTIFICATION1");
    }
}
