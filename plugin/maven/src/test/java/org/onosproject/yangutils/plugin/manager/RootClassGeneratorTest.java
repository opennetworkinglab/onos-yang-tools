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
import org.onosproject.yangutils.parser.exceptions.ParserException;
import org.onosproject.yangutils.utils.io.YangPluginConfig;
import org.onosproject.yangutils.utils.io.impl.YangFileScanner;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yangutils.utils.io.YangPluginConfig.compileCode;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.deleteDirectory;

/**
 * Unit test case for root node's code generation.
 */
public class RootClassGeneratorTest {

    private final YangUtilManager utilManager = new YangUtilManager();

    @Test
    public void rootClassGenTest() throws IOException, ParserException, MojoExecutionException {
        deleteDirectory("target/manager/");
        String searchDir = "src/test/resources/manager/singleChild";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir("target/manager/");
        utilManager.translateToJava(yangPluginConfig);
        String dir1 = System.getProperty("user.dir") + File.separator + "target/manager/";
        compileCode(dir1);
        String path = System.getProperty("user.dir") + "/target/manager/" +
                "org/onosproject/yang/gen/v1/single/test5/test/rev20160704" +
                "/Test5.java";
        assertThat(true, is((new File(path)).exists()));

        path = System.getProperty("user.dir") + "/target/manager/" +
                "org/onosproject/yang/gen/v1/single/test5/test/rev20160704" +
                "/Test7.java";
        assertThat(true, is((new File(path)).exists()));
        deleteDirectory("target/manager/");
    }

    @Test
    public void rootClassGenwithoutRevTest() throws IOException, ParserException, MojoExecutionException {
        deleteDirectory("target/manager/");
        String searchDir = "src/test/resources/manager/genwithoutrev";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir("target/manager/");
        utilManager.translateToJava(yangPluginConfig);
        String dir1 = System.getProperty("user.dir") + File.separator + "target/manager/";
        compileCode(dir1);
        String path = System.getProperty("user.dir") + "/target/manager/" +
                "org/onosproject/yang/gen/v1/test5/test/Test5.java";

        assertThat(true, is((new File(path)).exists()));
        deleteDirectory("target/manager/");
    }

    @Test
    public void rootClassMethodGenTest() throws IOException, ParserException, MojoExecutionException {
        deleteDirectory("target/manager/");
        String searchDir = "src/test/resources/manager/MultiChild";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir("target/manager/");
        utilManager.translateToJava(yangPluginConfig);
        String dir1 = System.getProperty("user.dir") + File.separator + "target/manager/";
        compileCode(dir1);
        String path = System.getProperty("user.dir") + "/target/manager/" +
                "org/onosproject/yang/gen/v1/multi/test5/test/rev20160704" +
                "/Test5.java";
        assertThat(true, is((new File(path)).exists()));

        path = System.getProperty("user.dir") + "/target/manager/" +
                "org/onosproject/yang/gen/v1/multi/test5/test/rev20160704" +
                "/Test7.java";
        assertThat(true, is((new File(path)).exists()));

        path = System.getProperty("user.dir") + "/target/manager/" +
                "org/onosproject/yang/gen/v1/multi/test8/test/rev20160704" +
                "/Test8.java";
        assertThat(true, is((new File(path)).exists()));

        deleteDirectory("target/manager/");
    }
}
