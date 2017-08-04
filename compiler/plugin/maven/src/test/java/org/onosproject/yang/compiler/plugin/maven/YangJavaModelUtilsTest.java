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
import org.hamcrest.core.Is;
import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.tool.YangCompilerManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.onosproject.yang.compiler.translator.tojava.YangJavaModelUtils.isRootNodesCodeGenRequired;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;

/**
 * Unit test case for java model utils.
 */
public class YangJavaModelUtilsTest {

    private final YangCompilerManager utilManager = new YangCompilerManager();

    @Test
    public void isRootNodeContainsOnlyAugmentTest()
            throws IOException, ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/onlyaugment";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(true, Is.is(isRootNodesCodeGenRequired(node)));
            }
            if (node.getName().equals("test6")) {
                assertThat(true, Is.is(isRootNodesCodeGenRequired(node)));
            }
        }
    }

    @Test
    public void isRootNodeCodeGenRequiredOnlyLeafTest()
            throws IOException, ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/onlyleaf";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(true, Is.is(isRootNodesCodeGenRequired(node)));
            }
        }
    }

    @Test
    public void isRootNodeCodeGenRequiredOnlyLeafListTest()
            throws IOException, ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/onlyleaflist";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(true, Is.is(isRootNodesCodeGenRequired(node)));
            }
        }
    }

    @Test
    public void isRootNodeCodeGenRequiredOnlyGroupingTest()
            throws IOException, ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/onlygrouping";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(false, Is.is(isRootNodesCodeGenRequired(node)));
            }
            if (node.getName().equals("test6")) {
                assertThat(false, Is.is(isRootNodesCodeGenRequired(node)));
            }
        }
    }


    @Test
    public void isRootNodeCodeGenRequiredOnlyTypeDefTest()
            throws IOException, ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/onlytypdef";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(false, Is.is(isRootNodesCodeGenRequired(node)));
            }
            if (node.getName().equals("test6")) {
                assertThat(false, Is.is(isRootNodesCodeGenRequired(node)));
            }
        }
    }

    @Test
    public void isRootNodeCodeGenRequiredNoGenTest()
            throws IOException, ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/nogen";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(false, Is.is(isRootNodesCodeGenRequired(node)));
            }
        }
    }

    @Test
    public void isRootNodeCodeGenRequiredMixedTest()
            throws IOException, ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/mixed";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(true, Is.is(isRootNodesCodeGenRequired(node)));
            }
            if (node.getName().equals("test6")) {
                assertThat(true, Is.is(isRootNodesCodeGenRequired(node)));
            }
        }
    }

    @Test
    public void isRootNodeCodeGenRequiredTypedefGroupingTest()
            throws IOException, ParserException, MojoExecutionException {
        String searchDir = "src/test/resources/rootNode/typedefgrouping";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        for (YangNode node : utilManager.getYangNodeSet()) {
            if (node.getName().equals("test5")) {
                assertThat(false, Is.is(isRootNodesCodeGenRequired(node)));
            }
        }
    }
}
