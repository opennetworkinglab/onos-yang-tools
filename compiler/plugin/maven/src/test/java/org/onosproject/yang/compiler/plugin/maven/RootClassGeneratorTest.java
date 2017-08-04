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
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.tool.YangCompilerManager;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.onosproject.yang.compiler.utils.io.impl.YangIoUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;

/**
 * Unit test case for root node's code generation.
 */
public class RootClassGeneratorTest {

    private final YangCompilerManager utilManager =
            new YangCompilerManager();

    @Test
    public void rootClassGenTest() throws IOException, ParserException, MojoExecutionException {
        YangIoUtils.deleteDirectory("target/manager/");
        String searchDir = "src/test/resources/manager/singleChild";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir("target/manager/");
        utilManager.translateToJava(yangPluginConfig);
        String dir1 = System.getProperty("user.dir") + File.separator + "target/manager/";
        YangPluginConfig.compileCode(dir1);
        String path = System.getProperty("user.dir") + "/target/manager/" +
                "org/onosproject/yang/gen/v1/test5/rev20160704" +
                "/Test5.java";
        assertThat(true, is((new File(path)).exists()));

        path = System.getProperty("user.dir") + "/target/manager/" +
                "org/onosproject/yang/gen/v1/test7/rev20160704" +
                "/Test7.java";
        assertThat(true, is((new File(path)).exists()));
        YangIoUtils.deleteDirectory("target/manager/");
    }

    @Test
    public void rootClassGenwithoutRevTest() throws IOException, ParserException, MojoExecutionException {
        YangIoUtils.deleteDirectory("target/manager/");
        String searchDir = "src/test/resources/manager/genwithoutrev";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir("target/manager/");
        utilManager.translateToJava(yangPluginConfig);
        String dir1 = System.getProperty("user.dir") + File.separator + "target/manager/";
        YangPluginConfig.compileCode(dir1);
        String path = System.getProperty("user.dir") + "/target/manager/" +
                "org/onosproject/yang/gen/v1/test5/Test5.java";

        assertThat(true, is((new File(path)).exists()));
        YangIoUtils.deleteDirectory("target/manager/");
    }

    @Test
    public void rootClassMethodGenTest() throws IOException, ParserException, MojoExecutionException {
        YangIoUtils.deleteDirectory("target/manager/");
        String searchDir = "src/test/resources/manager/MultiChild";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir("target/manager/");
        utilManager.translateToJava(yangPluginConfig);
        String dir1 = System.getProperty("user.dir") + File.separator + "target/manager/";
        YangPluginConfig.compileCode(dir1);
        String path = System.getProperty("user.dir") + "/target/manager/" +
                "org/onosproject/yang/gen/v1/test5/rev20160704" +
                "/Test5.java";
        assertThat(true, is((new File(path)).exists()));

        path = System.getProperty("user.dir") + "/target/manager/" +
                "org/onosproject/yang/gen/v1/test7/rev20160704" +
                "/Test7.java";
        assertThat(true, is((new File(path)).exists()));

        path = System.getProperty("user.dir") + "/target/manager/" +
                "org/onosproject/yang/gen/v1/test8/rev20160704" +
                "/Test8.java";
        assertThat(true, is((new File(path)).exists()));

        YangIoUtils.deleteDirectory("target/manager/");
    }
}
