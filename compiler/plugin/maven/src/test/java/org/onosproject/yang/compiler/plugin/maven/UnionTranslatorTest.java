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
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;
import org.onosproject.yang.compiler.tool.YangCompilerManager;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorUtil;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.onosproject.yang.compiler.utils.io.impl.YangIoUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;

/**
 * Unit tests for union translator.
 */
public final class UnionTranslatorTest {

    private final YangUtilsParserManager manager = new YangUtilsParserManager();
    private static final String DIR = "target/unionTranslator/";
    private static final String DIR1 = System.getProperty("user.dir") + File
            .separator + DIR;

    YangCompilerManager utilManager = new YangCompilerManager();

    /**
     * Checks union translation should not result in any exception.
     */
    @Test
    public void processUnionTranslator()
            throws IOException, ParserException {
        YangIoUtils.deleteDirectory(DIR);
        YangNode node = manager.getDataModel("src/test/resources/UnionTranslator.yang");

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);

        JavaCodeGeneratorUtil.generateJavaCode(node, yangPluginConfig);
        YangPluginConfig.compileCode(DIR1);
        YangIoUtils.deleteDirectory(DIR);
    }

    /**
     * Unit test case to test conflicting types.
     *
     * @throws IOException            when fails to do IO operations
     * @throws MojoExecutionException when fails to do mojo operations
     */
    @Test
    public void processUnionIntUintConflictingTypes() throws IOException, MojoExecutionException {
        YangIoUtils.deleteDirectory(DIR);
        String searchDir = "src/test/resources/unionTranslator/intuint";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);

        utilManager.translateToJava(yangPluginConfig);
        YangPluginConfig.compileCode(DIR1);
        YangIoUtils.deleteDirectory(DIR);
    }

    /**
     * Unit test case to test conflicting types.
     *
     * @throws IOException            when fails to do IO operations
     * @throws MojoExecutionException when fails to do mojo operations
     */
    @Test
    public void processUnionUintIntConflictingTypes() throws IOException,
            MojoExecutionException {
        YangIoUtils.deleteDirectory(DIR);
        String searchDir = "src/test/resources/unionTranslator/uintint";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);

        utilManager.translateToJava(yangPluginConfig);
        YangPluginConfig.compileCode(DIR1);
        YangIoUtils.deleteDirectory(DIR);
    }

    /**
     * Unit test case to test conflicting types.
     *
     * @throws IOException            when fails to do IO operations
     * @throws MojoExecutionException when fails to do mojo operations
     */
    @Test
    public void processUnionLongUlongConflictingTypes() throws IOException,
            MojoExecutionException {
        YangIoUtils.deleteDirectory(DIR);
        String searchDir = "src/test/resources/unionTranslator/longulong";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);

        utilManager.translateToJava(yangPluginConfig);
        YangPluginConfig.compileCode(DIR1);
        YangIoUtils.deleteDirectory(DIR);
    }

    /**
     * Unit test case to test conflicting types.
     *
     * @throws IOException            when fails to do IO operations
     * @throws MojoExecutionException when fails to do mojo operations
     */
    @Test
    public void processUnionUlongLongConflictingTypes() throws IOException,
            MojoExecutionException {
        YangIoUtils.deleteDirectory(DIR);
        String searchDir = "src/test/resources/unionTranslator/ulonglong";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);

        utilManager.translateToJava(yangPluginConfig);
        YangPluginConfig.compileCode(DIR1);
        YangIoUtils.deleteDirectory(DIR);
    }

    /**
     * Unit test case to test conflicting types.
     *
     * @throws IOException            when fails to do IO operations
     * @throws MojoExecutionException when fails to do mojo operations
     */
    @Test
    public void processUnionIntUintUlongLongConflictingTypes() throws IOException,
            MojoExecutionException {
        YangIoUtils.deleteDirectory(DIR);
        String searchDir = "src/test/resources/unionTranslator/intuintulonglong";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);

        utilManager.translateToJava(yangPluginConfig);
        YangPluginConfig.compileCode(DIR1);
        YangIoUtils.deleteDirectory(DIR);
    }

    /**
     * Unit test case to test conflicting types.
     *
     * @throws IOException            when fails to do IO operations
     * @throws MojoExecutionException when fails to do mojo operations
     */
    @Test
    public void processUnionIntUintUlongLongStringConflictingTypes() throws IOException,
            MojoExecutionException {
        YangIoUtils.deleteDirectory(DIR);
        String searchDir = "src/test/resources/unionTranslator/intuintulonglongstring";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);

        utilManager.translateToJava(yangPluginConfig);
        YangPluginConfig.compileCode(DIR1);
        YangIoUtils.deleteDirectory(DIR);
    }

    /**
     * Unit test case to test conflicting types.
     *
     * @throws IOException            when fails to do IO operations
     * @throws MojoExecutionException when fails to do mojo operations
     */
    @Test
    public void processUnionIntUintStringConflictingTypes() throws IOException,
            MojoExecutionException {
        YangIoUtils.deleteDirectory(DIR);
        String searchDir = "src/test/resources/unionTranslator/intuintstring";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);

        utilManager.translateToJava(yangPluginConfig);
        YangPluginConfig.compileCode(DIR1);
        YangIoUtils.deleteDirectory(DIR);
    }

    /**
     * Unit test case to test Union with binary type.
     *
     * @throws IOException            when fails to do IO operations
     * @throws MojoExecutionException when fails to do mojo operations
     */
    @Test
    public void processUnionWithBinaryTypes() throws IOException,
            MojoExecutionException {
        YangIoUtils.deleteDirectory(DIR);
        String searchDir = "src/test/resources/unionTranslator/unionwithbinary";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);

        utilManager.translateToJava(yangPluginConfig);
        YangPluginConfig.compileCode(DIR1);
        YangIoUtils.deleteDirectory(DIR);
    }

    // TODO enhance the test cases, after having a framework of translator test.
}
