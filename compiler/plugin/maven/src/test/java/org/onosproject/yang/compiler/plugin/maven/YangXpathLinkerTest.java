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
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangReferenceResolver;
import org.onosproject.yang.compiler.datamodel.YangResolutionInfo;
import org.onosproject.yang.compiler.linker.impl.YangLinkerManager;
import org.onosproject.yang.compiler.linker.impl.YangLinkerUtils;
import org.onosproject.yang.compiler.linker.impl.YangXpathLinker;
import org.onosproject.yang.compiler.tool.YangCompilerManager;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_AUGMENT;
import static org.onosproject.yang.compiler.linker.impl.XpathLinkingTypes.AUGMENT_LINKING;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.deleteDirectory;

/**
 * Unit test cases for x-path linker.
 */
public class YangXpathLinkerTest {

    private static final String INTRA_FILE_PATH = "src/test/resources/xPathLinker/IntraFile/";
    private static final String INTER_FILE_PATH = "src/test/resources/xPathLinker/InterFile/";
    private static final String CASE_FILE_PATH = "src/test/resources/xPathLinker/Case/";

    private final YangCompilerManager utilManager =
            new YangCompilerManager();
    private YangXpathLinker<?> linker = new YangXpathLinker();
    private YangLinkerManager linkerManager = new YangLinkerManager();

    /**
     * Unit test case for intra file linking for single level container.
     *
     * @throws IOException            when fails to do IO operations
     * @throws MojoExecutionException
     */
    @Test
    public void processIntraFileLinkingSingleLevel() throws IOException, MojoExecutionException {

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTRA_FILE_PATH + "IntraSingle/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            YangReferenceResolver ref = (YangReferenceResolver) node;
            List<YangResolutionInfo> infos = ref.getUnresolvedResolutionList(YANG_AUGMENT);
            YangResolutionInfo info = infos.get(0);

            YangAugment augment = (YangAugment) info
                    .getEntityToResolveInfo().getEntityToResolve();
            targetNodeName = augment.getTargetNode().get(
                    augment.getTargetNode().size() - 1).getNodeIdentifier()
                    .getName();
            targetNode = augment.getAugmentedNode();
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for intra file linking for multiple level container.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processIntraFileLinkingMultipleLevel() throws IOException {

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTRA_FILE_PATH + "IntraMulti/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = linker.processXpathLinking(
                        augment.getTargetNode(), node, AUGMENT_LINKING);
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for intra file linking for single level augment.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processIntraFileLinkingInAugmentSingleLevel() throws IOException {

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTRA_FILE_PATH + "IntraSingleAugment/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = linker.processXpathLinking(augment.getTargetNode(),
                                                        node, AUGMENT_LINKING);
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for intra file linking for multiple level augment
     * without prefix.
     *
     * @throws IOException if fails to do IO operations
     */
    @Test
    public void processIntraFileMultiLevelWithoutPrefix() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTRA_FILE_PATH +
                                                "IntraMultiAugment/withoutprefix/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());
        linkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangNode target = null;
        String name = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);
            for (YangAugment augment : augments) {
                name = augment.getTargetNode()
                        .get(augment.getTargetNode().size() - 1)
                        .getNodeIdentifier().getName();
                target = linker.processXpathLinking(augment.getTargetNode(),
                                                    node, AUGMENT_LINKING);
            }
        }
        assertThat(true, is(target.getName().equals(name)));
    }

    /**
     * Unit test case for intra file linking for multiple level augment with
     * prefix.
     *
     * @throws IOException if fails to do IO operations
     */
    @Test
    public void processIntraFileWithPrefix() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTRA_FILE_PATH +
                                                "IntraMultiAugment/withprefix/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());
        linkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangNode target = null;
        String name = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);
            for (YangAugment augment : augments) {
                name = augment.getTargetNode()
                        .get(augment.getTargetNode().size() - 1)
                        .getNodeIdentifier().getName();
                target = linker.processXpathLinking(augment.getTargetNode(),
                                                    node, AUGMENT_LINKING);
            }
        }
        assertThat(true, is(target.getName().equals(name)));
    }

    /**
     * Unit test case for intra file linking for multiple level augment with
     * partial prefix.
     *
     * @throws IOException if fails to do IO operations
     */
    @Test
    public void processIntraFileWithPartialPrefix() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTRA_FILE_PATH +
                                                "IntraMultiAugment/withpartialprefix/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());
        linkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangNode target = null;
        String name = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);
            for (YangAugment augment : augments) {
                name = augment.getTargetNode()
                        .get(augment.getTargetNode().size() - 1)
                        .getNodeIdentifier().getName();
                target = linker.processXpathLinking(augment.getTargetNode(),
                                                    node, AUGMENT_LINKING);
            }
        }
        assertThat(true, is(target.getName().equals(name)));
    }

    /**
     * Unit test case for intra file linking for multiple level submodule.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processIntraFileLinkingInSubModuleSingleLevel() throws IOException {

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTRA_FILE_PATH +
                                                "IntraSingleSubModule/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.linkSubModulesToParentModule(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = linker.processXpathLinking(augment.getTargetNode(), node, AUGMENT_LINKING);
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for intra file linking for multiple level submodule.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processIntraFileLinkingInSubModuleMultiLevel() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTRA_FILE_PATH +
                                                "IntraMultiSubModule/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.linkSubModulesToParentModule(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = linker.processXpathLinking(
                        augment.getTargetNode(), node, AUGMENT_LINKING);
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for intra file linking for single level uses.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processIntraFileLinkingInUsesSingleLevel() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTRA_FILE_PATH +
                                                "IntraSingleUses/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());
        linkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = linker.processXpathLinking(
                        augment.getTargetNode(), node, AUGMENT_LINKING);
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for intra file linking for multi level uses.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processIntraFileLinkingInUsesMultiLevel() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTRA_FILE_PATH +
                                                "IntraMultiUses/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());
        linkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = linker.processXpathLinking(
                        augment.getTargetNode(), node, AUGMENT_LINKING);
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for inter file linking for single level container.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processInterFileLinkingSingleLevel() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTER_FILE_PATH +
                                                "InterSingle/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = linker.processXpathLinking(
                        augment.getTargetNode(), node, AUGMENT_LINKING);
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for inter file linking for multi level container.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processInterFileLinkingMultipleLevel() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTER_FILE_PATH +
                                                "InterMulti/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = linker.processXpathLinking(
                        augment.getTargetNode(), node, AUGMENT_LINKING);
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for inter file linking for single level augment.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processInterFileLinkingInAugmentSingleLevel() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTER_FILE_PATH +
                                                "InterSingleAugment/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = linker.processXpathLinking(
                        augment.getTargetNode(), node, AUGMENT_LINKING);
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for inter file linking for multi level augment.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processInterFileLinkingInAugmentMultiLevel() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTER_FILE_PATH +
                                                "InterMultiAugment/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = linker.processXpathLinking(
                        augment.getTargetNode(), node, AUGMENT_LINKING);
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for multipler inter file linking for single level augment.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processMultiInterFileLinkingInAugmentSingleLevel() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTER_FILE_PATH +
                                                "InterMultiFileAugment/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = linker.processXpathLinking(
                        augment.getTargetNode(), node, AUGMENT_LINKING);
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for multiple inter file linking for multi level augment.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processMultiInterFileLinkingInAugmentMultiLevel() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTER_FILE_PATH +
                                                "InterMultiFileAugmentMulti/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());
        linkerManager.linkSubModulesToParentModule(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());
        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());
        linkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = augment.getAugmentedNode();
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for inter file linking for single level submodule.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processInterFileLinkingInSubModuleSingleLevel() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTER_FILE_PATH +
                                                "InterSingleSubModule/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.linkSubModulesToParentModule(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());
        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());
        linkerManager.processInterFileLinking(utilManager.getYangNodeSet());
        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = augment.getAugmentedNode();
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for inter file linking for multi level submodule.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processInterFileLinkingInSubModuleMultiLevel() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTER_FILE_PATH +
                                                "InterMultiSubModule/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.linkSubModulesToParentModule(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());
        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());
        linkerManager.processInterFileLinking(utilManager.getYangNodeSet());
        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = augment.getAugmentedNode();
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for inter file linking for multi level uses inside augment.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processInterFileLinkingInUsesInAugment() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTER_FILE_PATH +
                                                "InterSingleUses/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.linkSubModulesToParentModule(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());
        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());
        linkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1)
                        .getNodeIdentifier().getName();
                targetNode = augment.getAugmentedNode();
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for inter file linking for multi level uses.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processInterFileLinkingInUsesMultiLevel() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(INTER_FILE_PATH +
                                                "InterMultiUses/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());
        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());
        linkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1).getNodeIdentifier()
                        .getName();
                targetNode = linker.processXpathLinking(
                        augment.getTargetNode(), node, AUGMENT_LINKING);
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for inter file linking for multi level uses inside augment.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processInterFileLinkingInMultipleSubmodules() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(CASE_FILE_PATH +
                                                "submodule/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.linkSubModulesToParentModule(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());
        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        linkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(
                        augment.getTargetNode().size() - 1)
                        .getNodeIdentifier().getName();
                targetNode = augment.getAugmentedNode();
            }
        }

        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }

    /**
     * Unit test case for inter file linking for multi level uses inside augment.
     *
     * @throws IOException when fails to do IO operations
     */
    @Test
    public void processInterFileLinkingInMultipleUses() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(CASE_FILE_PATH +
                                                "uses/")) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        deleteDirectory("target/xpath/");
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        linkerManager.createYangNodeSet(utilManager.getYangNodeSet());
        linkerManager.linkSubModulesToParentModule(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());
        linkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());
        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());
        linkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangNode targetNode = null;
        String targetNodeName = null;

        for (YangNode node : utilManager.getYangNodeSet()) {
            List<YangAugment> augments = linker.getListOfYangAugment(node);

            for (YangAugment augment : augments) {
                targetNodeName = augment.getTargetNode().get(augment.getTargetNode().size() - 1)
                        .getNodeIdentifier().getName();
                targetNode = augment.getAugmentedNode();
            }
        }

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir("target/xpath/");
        utilManager.translateToJava(yangPluginConfig);
        String dir = System.getProperty("user.dir") + File.separator + "target/xpath/";
        YangPluginConfig.compileCode(dir);
        deleteDirectory("target/xpath/");
        assertThat(true, is(targetNode.getName().equals(targetNodeName)));
    }
}
