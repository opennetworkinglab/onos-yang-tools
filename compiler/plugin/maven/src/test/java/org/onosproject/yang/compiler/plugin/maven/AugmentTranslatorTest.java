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

package org.onosproject.yang.compiler.plugin.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.tool.impl.YangCompilerManager;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.deleteDirectory;

/**
 * Unit test case for augment translator.
 */
public class AugmentTranslatorTest {

    private final YangCompilerManager utilManager = new YangCompilerManager();
    private static final String DIR = "target/augmentTranslator/";
    private static final String COMP = System.getProperty("user.dir") + File
            .separator + DIR;

    /**
     * Checks augment translation should not result in any exception.
     *
     * @throws MojoExecutionException
     */
    @Test
    public void processAugmentTranslator() throws IOException, ParserException, MojoExecutionException {

        deleteDirectory(DIR);
        String searchDir = "src/test/resources/augmentTranslator";

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
        YangPluginConfig.compileCode(COMP);
        deleteDirectory(DIR);
    }

    /**
     * Checks augment translation should not result in any exception.
     * compiler not added because it contains a notification which depends on
     * onos api.
     *
     * @throws MojoExecutionException
     */
    @Test
    public void processRpcAugmentIntraTranslator() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = "src/test/resources/rpcAugment/intra";

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
        deleteDirectory(DIR);
    }

    /**
     * Checks augment translation should not result in any exception.
     *
     * @throws MojoExecutionException when fails to mojo operations
     */
    @Test
    public void processRpcAugmentInterTranslator() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = "src/test/resources/rpcAugment/inter";

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
        YangPluginConfig.compileCode(COMP);
        deleteDirectory(DIR);
    }

    /**
     * Checks augment translation should not result in any exception.
     *
     * @throws MojoExecutionException when fails
     */
    @Test
    public void processChoiceAugmentInterTranslator() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = "src/test/resources/choiceAugment";

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
        YangPluginConfig.compileCode(COMP);
        deleteDirectory(DIR);
    }
}
