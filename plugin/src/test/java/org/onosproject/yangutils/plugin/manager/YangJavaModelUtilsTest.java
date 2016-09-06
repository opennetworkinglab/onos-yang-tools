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

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.parser.exceptions.ParserException;
import org.onosproject.yangutils.plugin.manager.YangUtilManager;
import org.onosproject.yangutils.utils.io.impl.YangFileScanner;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yangutils.translator.tojava.YangJavaModelUtils.isGetSetOfRootNodeRequired;
import static org.onosproject.yangutils.translator.tojava.YangJavaModelUtils.isRootNodesCodeGenRequired;

/**
 * Unit test case for java model utils.
 */
public class YangJavaModelUtilsTest {


    private final YangUtilManager utilManager = new YangUtilManager();

    @Test
    public void isRootNodeContainsOnlyAugmentTest() throws IOException,
            ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/onlyaugment";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(false, is(isGetSetOfRootNodeRequired(node)));
                assertThat(true, is(isRootNodesCodeGenRequired(node)));
            }
            if (node.getName().equals("test6")) {
                assertThat(true, is(isGetSetOfRootNodeRequired(node)));
                assertThat(true, is(isRootNodesCodeGenRequired(node)));
            }
        }
    }

    @Test
    public void isRootNodeCodeGenRequiredOnlyLeafTest() throws IOException
            , ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/onlyleaf";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(true, is(isGetSetOfRootNodeRequired(node)));
                assertThat(true, is(isRootNodesCodeGenRequired(node)));
            }
        }
    }

    @Test
    public void isRootNodeCodeGenRequiredOnlyLeafListTest() throws IOException
            , ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/onlyleaflist";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(true, is(isGetSetOfRootNodeRequired(node)));
                assertThat(true, is(isRootNodesCodeGenRequired(node)));
            }
        }
    }

    @Test
    public void isRootNodeCodeGenRequiredOnlyGroupingTest() throws IOException
            , ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/onlygrouping";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(true, is(isGetSetOfRootNodeRequired(node)));
                assertThat(false, is(isRootNodesCodeGenRequired(node)));
            }
            if (node.getName().equals("test6")) {
                assertThat(true, is(isGetSetOfRootNodeRequired(node)));
                assertThat(false, is(isRootNodesCodeGenRequired(node)));
            }
        }
    }


    @Test
    public void isRootNodeCodeGenRequiredOnlyTypeDefTest() throws IOException
            , ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/onlytypdef";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(false, is(isGetSetOfRootNodeRequired(node)));
                assertThat(false, is(isRootNodesCodeGenRequired(node)));
            }
            if (node.getName().equals("test6")) {
                assertThat(true, is(isGetSetOfRootNodeRequired(node)));
                assertThat(false, is(isRootNodesCodeGenRequired(node)));
            }
        }
    }

    @Test
    public void isRootNodeCodeGenRequiredNoGenTest() throws IOException
            , ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/nogen";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(false, is(isGetSetOfRootNodeRequired(node)));
                assertThat(false, is(isRootNodesCodeGenRequired(node)));
            }
        }

    }

    @Test
    public void isRootNodeCodeGenRequiredMixedTest() throws IOException
            , ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/mixed";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(false, is(isGetSetOfRootNodeRequired(node)));
                assertThat(true, is(isRootNodesCodeGenRequired(node)));
            }
            if (node.getName().equals("test6")) {
                assertThat(true, is(isGetSetOfRootNodeRequired(node)));
                assertThat(true, is(isRootNodesCodeGenRequired(node)));
            }
        }

    }

    @Test
    public void isRootNodeCodeGenRequiredTypedefGroupingTest() throws IOException
            , ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/typedefgrouping";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(true, is(isGetSetOfRootNodeRequired(node)));
                assertThat(false, is(isRootNodesCodeGenRequired(node)));
            }
        }

    }
}
