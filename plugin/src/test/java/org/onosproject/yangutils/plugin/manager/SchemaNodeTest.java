/*
 * Copyright 2016-present Open Networking Laboratory
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

package org.onosproject.yangutils.plugin.manager;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.onosproject.yangutils.datamodel.YangLeaf;
import org.onosproject.yangutils.datamodel.YangLeavesHolder;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangSchemaNodeContextInfo;
import org.onosproject.yangutils.datamodel.YangSchemaNodeIdentifier;
import org.onosproject.yangutils.parser.exceptions.ParserException;
import org.onosproject.yangutils.utils.io.YangPluginConfig;
import org.onosproject.yangutils.utils.io.impl.YangFileScanner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.deleteDirectory;

/**
 * Test cases for testing YANG schema node.
 */
public class SchemaNodeTest {

    private final YangUtilManager utilManager = new YangUtilManager();

    /**
     * Checks method to get schema node from map.
     *
     * @throws MojoExecutionException
     */
    @Test
    public void processSchemaNodeMap() throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/schemaMap";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();
        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir("target/schemaMap/");
        utilManager.translateToJava(yangPluginConfig);

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();
        YangNode rootNode = yangNodeIterator.next();

        assertThat(rootNode.getYsnContextInfoMap(), is(notNullValue()));
        Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> schemaMap = rootNode.getYsnContextInfoMap();
        YangSchemaNodeIdentifier yangSchemaNodeIdentifier = new YangSchemaNodeIdentifier();
        yangSchemaNodeIdentifier.setName("testcontainer");
        yangSchemaNodeIdentifier.setNamespace("http://huawei.com");
        assertThat(schemaMap.get(yangSchemaNodeIdentifier), is(notNullValue()));
        YangSchemaNodeContextInfo yangSchemaNodeContextInfo = schemaMap.get(yangSchemaNodeIdentifier);
        assertThat(yangSchemaNodeContextInfo.getSchemaNode(), is(rootNode.getChild()));

        assertThat(rootNode.getChild().getYsnContextInfoMap(), is(notNullValue()));
        Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> schemaMap2 = rootNode.getChild()
                .getYsnContextInfoMap();
        yangSchemaNodeIdentifier.setName("testleaf");
        yangSchemaNodeIdentifier.setNamespace("http://huawei.com");
        assertThat(schemaMap2.get(yangSchemaNodeIdentifier), is(notNullValue()));

        yangSchemaNodeIdentifier.setName("pretzel");
        yangSchemaNodeIdentifier.setNamespace("http://huawei.com");
        assertThat(schemaMap2.get(yangSchemaNodeIdentifier), is(notNullValue()));

        assertThat(rootNode.getChild().getChild().getYsnContextInfoMap(), is(notNullValue()));
        Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> schemaMap3 = rootNode.getChild().getChild()
                .getYsnContextInfoMap();
        yangSchemaNodeIdentifier.setName("pretzel");
        yangSchemaNodeIdentifier.setNamespace("http://huawei.com");
        assertThat(schemaMap3.get(yangSchemaNodeIdentifier), is(notNullValue()));
        YangSchemaNodeContextInfo yangSchemaNodeContextInfo3 = schemaMap3.get(yangSchemaNodeIdentifier);

        assertThat(rootNode.getChild().getChild().getChild().getYsnContextInfoMap(), is(notNullValue()));
        Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> schemaMap4 = rootNode.getChild().getChild().getChild()
                .getYsnContextInfoMap();
        yangSchemaNodeIdentifier.setName("pretzel");
        yangSchemaNodeIdentifier.setNamespace("http://huawei.com");
        assertThat(schemaMap4.get(yangSchemaNodeIdentifier), is(notNullValue()));

        YangSchemaNodeContextInfo yangSchemaNodeContextInfo2 = schemaMap4.get(yangSchemaNodeIdentifier);
        List<YangLeaf> yangListOfLeaf = ((YangLeavesHolder) rootNode.getChild().getChild().getChild()).getListOfLeaf();
        YangLeaf yangLeaf = yangListOfLeaf.get(0);
        assertThat(yangSchemaNodeContextInfo2.getSchemaNode(), is(yangLeaf));

        assertThat(yangSchemaNodeContextInfo3.getSchemaNode(), is(yangLeaf));
        assertThat(yangSchemaNodeContextInfo3.getContextSwitchedNode(), is(rootNode.getChild().getChild().getChild()));

        deleteDirectory("target/schemaMap/");
    }
}
