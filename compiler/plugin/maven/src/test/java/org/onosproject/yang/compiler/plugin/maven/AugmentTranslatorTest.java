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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.compiler.api.YangCompilerException;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.tool.YangCompilerManager;
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

    private static final String DIR = "target/augmentTranslator/";
    private static final String COMP = System.getProperty("user.dir") + File
            .separator + DIR;
    private final YangCompilerManager utilManager = new YangCompilerManager();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
        deleteDirectory(DIR);
    }

    /**
     * Checks the augment statements linking with prefix change from inter to.
     * inter
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fails
     */
    @Test
    public void processActnAugmentInterTranslator() throws IOException,
            ParserException,
            MojoExecutionException {

        deleteDirectory(DIR);
        String searchDir = "src/test/resources/actnInterAugments";

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
     * Checks the derived identities with referred base types in inter modules.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fails
     */
    @Test
    public void processIdentityRefTranslator() throws IOException,
            ParserException,
            MojoExecutionException {

        deleteDirectory(DIR);
        String searchDir = "src/test/resources/DerivedIdentity";

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
     * Checks collision detection of an augment linked to a target having
     * same node in the same level.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fails
     */
    @Test
    public void processSingleAugToTgt() throws IOException,
            ParserException, MojoExecutionException {

        deleteDirectory(DIR);
        String dir = "src/test/resources/augmentcollision/singleaugtotgt";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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
     * Checks collision detection of two augments from two different modules
     * linked to a target having same node in the same level.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fails
     */
    @Test
    public void processTwoAugToTgt() throws IOException,
            ParserException, MojoExecutionException {

        deleteDirectory(DIR);
        String dir = "src/test/resources/augmentcollision/twoaugtotgt";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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
     * Checks collision detection of two augments from module and its
     * sub-module linked to a target having same node in the same level.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fails
     */
    @Test
    public void processSingleSubModToTgt() throws IOException,
            ParserException, MojoExecutionException {

        thrown.expect(YangCompilerException.class);
        thrown.expectMessage(
                "YANG File Error: Identifier collision detected in target " +
                        "node as \"val in 12 at 8");

        deleteDirectory(DIR);
        String dir = "src/test/resources/augmentcollision/singlesubmodtotgt";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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
     * Checks collision detection of two augments from same namespace
     * sub-modules linked to a target having same node in the same level.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fails
     */
    @Test
    public void processTwoSubModToTgt() throws IOException,
            ParserException, MojoExecutionException {

        thrown.expect(YangCompilerException.class);
        thrown.expectMessage(
                "YANG File Error: Identifier collision detected in target " +
                        "node as \"val in 12 at 8");

        deleteDirectory(DIR);
        String dir = "src/test/resources/augmentcollision/twosubmodtotgt";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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
     * Checks collision detection of an augment from the same module linked
     * to a target having same node in the same level.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fails
     */
    @Test
    public void processSelfAug() throws IOException,
            ParserException, MojoExecutionException {

        thrown.expect(YangCompilerException.class);
        thrown.expectMessage(
                "YANG File Error: Identifier collision detected in target " +
                        "node as \"val in 16 at 8");

        deleteDirectory(DIR);
        String dir = "src/test/resources/augmentcollision/selfaug";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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
     * Checks collision detection of two augments from the same module linked
     * to a target having same node in the same level.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fails
     */
    @Test
    public void processSelfAugWithAug() throws IOException,
            ParserException, MojoExecutionException {

        thrown.expect(YangCompilerException.class);
        thrown.expectMessage(
                "YANG File Error: Identifier collision detected in target " +
                        "node as \"val in 24 at 8");

        deleteDirectory(DIR);
        String dir = "src/test/resources/augmentcollision/selfaugwithaug";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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
     * Checks collision detection of augments which are inter connected in
     * multiple files.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fails
     */
    @Test
    public void processAugToAug() throws IOException,
            ParserException, MojoExecutionException {

        deleteDirectory(DIR);
        String dir = "src/test/resources/augmentcollision/augtoaug";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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
     * Checks multiple augment handling which has the same name for open
     * config YANG files.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fails
     */
    @Test
    public void processOpenConfigAugment() throws IOException,
            ParserException, MojoExecutionException {

        deleteDirectory(DIR);
        String dir = "src/test/resources/augwithsamename/oc/";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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
     * Checks multiple augment handling which has the same name.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fail
     */
    @Test
    public void processSameAugName() throws IOException,
            ParserException, MojoExecutionException {

        deleteDirectory(DIR);
        String dir = "src/test/resources/augwithsamename/multiaugwithsamename/";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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
     * Checks leaf-ref linking with augmented nodes in predicate for open
     * config YANG files.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fail
     */
    @Test
    public void processLeafRefAugment() throws IOException,
            ParserException, MojoExecutionException {

        deleteDirectory(DIR);
        String dir = "src/test/resources/leafreflinker/oc-leafref";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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
     * Checks union with more that two identities for proper code generation.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fail
     */
    @Test
    public void processUnionIdentity() throws IOException,
            ParserException, MojoExecutionException {

        deleteDirectory(DIR);
        String dir = "src/test/resources/unionTranslator/unionidentity";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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
     * Checks identity linked in sub-module of imported module.
     *
     * @throws IOException            if any error occurs during IO on files
     * @throws ParserException        if any error occurs during parsing
     * @throws MojoExecutionException if any mojo operation fail
     */
    @Test
    public void processSubmoduleId() throws IOException,
            ParserException, MojoExecutionException {

        deleteDirectory(DIR);
        String dir = "src/test/resources/identityinsubmod";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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

    @Test
    public void processMultipleAugment() throws IOException,
            ParserException, MojoExecutionException {

        deleteDirectory(DIR);
        String dir = "src/test/resources/multipleaugment";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(dir)) {
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
