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
import org.onosproject.yang.compiler.datamodel.YangContainer;
import org.onosproject.yang.compiler.datamodel.YangFeature;
import org.onosproject.yang.compiler.datamodel.YangIfFeature;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus;
import org.onosproject.yang.compiler.linker.impl.YangLinkerManager;
import org.onosproject.yang.compiler.linker.impl.YangLinkerUtils;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.tool.YangCompilerManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.datamodel.YangNodeType.MODULE_NODE;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;

/**
 * Test cases for testing inter file linking.
 */
public class InterFileIfFeatureLinkingTest {

    private final YangCompilerManager utilManager =
            new YangCompilerManager();
    private final YangLinkerManager yangLinkerManager = new YangLinkerManager();

    /**
     * Checks inter file feature linking with imported file.
     */
    @Test
    public void processFeatureInImportedFile()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/interfilefeatureimport";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode selfNode = null;
        YangNode refNode1 = null;
        YangNode refNode2 = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        for (YangNode rootNode : utilManager.getYangNodeSet()) {
            if (rootNode.getName().equals("syslog1")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("syslog2")) {
                refNode1 = rootNode;
            } else {
                refNode2 = rootNode;
            }
        }
        // Check whether the data model tree returned is of type module.
        assertThat(selfNode instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("syslog1"));

        ListIterator<YangFeature> featureIterator = yangNode.getFeatureList().listIterator();
        YangFeature feature = featureIterator.next();
        assertThat(feature.getName(), is("frr-te"));

        YangIfFeature ifFeature = feature.getIfFeatureList().iterator().next();
        assertThat(ifFeature.getName().getName(), is("p2mp-te"));
        assertThat(ifFeature.getName().getPrefix(), is("sys2"));
        assertThat(ifFeature.getResolvableStatus(), Is.is(ResolvableStatus.RESOLVED));

        YangContainer container = (YangContainer) selfNode.getChild();
        assertThat(container.getName(), is("speed"));
        YangLeaf leaf = container.getListOfLeaf().iterator().next();
        assertThat(leaf.getName(), is("local-storage-limit"));

        List<YangIfFeature> ifFeatureList = leaf.getIfFeatureList();
        ifFeature = ifFeatureList.iterator().next();
        assertThat(ifFeature.getName().getName(), is("frr-te"));
        assertThat(ifFeature.getResolvableStatus(), Is.is(ResolvableStatus.RESOLVED));
    }

    /**
     * Checks inter file feature linking with included file.
     */
    @Test
    public void processFeatureInIncludedFile()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/interfilefeatureinclude";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode selfNode = null;
        YangNode refNode1 = null;
        YangNode refNode2 = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Carry out linking of sub module with module.
        yangLinkerManager.linkSubModulesToParentModule(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        // Add references to include list.
        yangLinkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        for (YangNode rootNode : utilManager.getYangNodeSet()) {
            if (rootNode.getName().equals("syslog3")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("syslog4")) {
                refNode1 = rootNode;
            } else {
                refNode2 = rootNode;
            }
        }

        // Check whether the data model tree returned is of type module.
        assertThat(selfNode instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("syslog3"));

        ListIterator<YangFeature> featureIterator = yangNode.getFeatureList().listIterator();
        YangFeature feature = featureIterator.next();
        assertThat(feature.getName(), is("frr-te"));

        YangIfFeature ifFeature = feature.getIfFeatureList().iterator().next();
        assertThat(ifFeature.getName().getName(), is("p2mp-te"));
        assertThat(ifFeature.getResolvableStatus(), Is.is(ResolvableStatus.RESOLVED));

        YangContainer container = (YangContainer) selfNode.getChild();
        assertThat(container.getName(), is("speed"));
        YangLeaf leaf = container.getListOfLeaf().iterator().next();
        assertThat(leaf.getName(), is("local-storage-limit"));

        List<YangIfFeature> ifFeatureList = leaf.getIfFeatureList();
        ifFeature = ifFeatureList.iterator().next();
        assertThat(ifFeature.getName().getName(), is("frr-te"));
        assertThat(ifFeature.getResolvableStatus(), Is.is(ResolvableStatus.RESOLVED));
    }

    /**
     * Checks inter file feature linking with imported file with dependency.
     */
    @Test
    public void processFeatureInImportedFileWithDependency()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/interfilefeatureimportdependency";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode selfNode = null;
        YangNode refNode1 = null;
        YangNode refNode2 = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        // Update the priority for all the files.
        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        for (YangNode rootNode : utilManager.getYangNodeSet()) {
            if (rootNode.getName().equals("syslog1")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("syslog2")) {
                refNode1 = rootNode;
            } else {
                refNode2 = rootNode;
            }
        }

        // Check whether the data model tree returned is of type module.
        assertThat(selfNode instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("syslog1"));

        ListIterator<YangFeature> featureIterator = yangNode.getFeatureList().listIterator();
        YangFeature feature = featureIterator.next();
        assertThat(feature.getName(), is("frr-te"));

        YangIfFeature ifFeature = feature.getIfFeatureList().iterator().next();
        assertThat(ifFeature.getName().getName(), is("p2mp-te"));
        assertThat(ifFeature.getName().getPrefix(), is("sys2"));
        assertThat(ifFeature.getResolvableStatus(), Is.is(ResolvableStatus.RESOLVED));

        YangContainer container = (YangContainer) selfNode.getChild();
        assertThat(container.getName(), is("speed"));
        YangLeaf leaf = container.getListOfLeaf().iterator().next();
        assertThat(leaf.getName(), is("local-storage-limit"));

        List<YangIfFeature> ifFeatureList = leaf.getIfFeatureList();
        ifFeature = ifFeatureList.iterator().next();
        assertThat(ifFeature.getName().getName(), is("frr-te"));
        assertThat(ifFeature.getResolvableStatus(), Is.is(ResolvableStatus.RESOLVED));
    }

    /**
     * Checks inter file feature linking with included file with dependency.
     */
    @Test
    public void processFeatureInIncludedFileWithDependency()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/interfilefeatureincludedependency";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode selfNode = null;
        YangNode refNode1 = null;
        YangNode refNode2 = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Carry out linking of sub module with module.
        yangLinkerManager.linkSubModulesToParentModule(utilManager.getYangNodeSet());

        // Add references to include list.
        yangLinkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        for (YangNode rootNode : utilManager.getYangNodeSet()) {
            if (rootNode.getName().equals("syslog1")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("syslog2")) {
                refNode1 = rootNode;
            } else {
                refNode2 = rootNode;
            }
        }

        // Check whether the data model tree returned is of type module.
        assertThat(selfNode instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("syslog1"));

        ListIterator<YangFeature> featureIterator = yangNode.getFeatureList().listIterator();
        YangFeature feature = featureIterator.next();
        assertThat(feature.getName(), is("frr-te"));

        YangIfFeature ifFeature = feature.getIfFeatureList().iterator().next();
        assertThat(ifFeature.getName().getName(), is("p2mp-te"));
        assertThat(ifFeature.getResolvableStatus(), Is.is(ResolvableStatus.RESOLVED));

        YangContainer container = (YangContainer) selfNode.getChild();
        assertThat(container.getName(), is("speed"));
        YangLeaf leaf = container.getListOfLeaf().iterator().next();
        assertThat(leaf.getName(), is("local-storage-limit"));

        List<YangIfFeature> ifFeatureList = leaf.getIfFeatureList();
        ifFeature = ifFeatureList.iterator().next();
        assertThat(ifFeature.getName().getName(), is("frr-te"));
        assertThat(ifFeature.getResolvableStatus(), Is.is(ResolvableStatus.RESOLVED));
    }

    /**
     * Checks inter file feature linking with imported file with dependency
     * feature undefined.
     */
    @Test
    public void processFeatureInImportedFileWithDependencyUndefined()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/interfilefeatureimportdependencyUndefined";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode selfNode = null;
        YangNode refNode1 = null;
        YangNode refNode2 = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        for (YangNode rootNode : utilManager.getYangNodeSet()) {
            if (rootNode.getName().equals("syslog1")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("syslog2")) {
                refNode1 = rootNode;
            } else {
                refNode2 = rootNode;
            }
        }

        // Check whether the data model tree returned is of type module.
        assertThat(selfNode instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("syslog1"));

        ListIterator<YangFeature> featureIterator = yangNode.getFeatureList().listIterator();
        YangFeature feature = featureIterator.next();
        assertThat(feature.getName(), is("frr-te"));

        YangIfFeature ifFeature = feature.getIfFeatureList().iterator().next();
        assertThat(ifFeature.getName().getName(), is("p2mp-te"));
        assertThat(ifFeature.getName().getPrefix(), is("sys2"));
        assertThat(ifFeature.getResolvableStatus(), Is.is(ResolvableStatus.INTRA_FILE_RESOLVED));

        YangContainer container = (YangContainer) selfNode.getChild();
        assertThat(container.getName(), is("speed"));
        YangLeaf leaf = container.getListOfLeaf().iterator().next();
        assertThat(leaf.getName(), is("local-storage-limit"));

        List<YangIfFeature> ifFeatureList = leaf.getIfFeatureList();
        ifFeature = ifFeatureList.iterator().next();
        assertThat(ifFeature.getName().getName(), is("frr-te"));
        assertThat(ifFeature.getResolvableStatus(), Is.is(ResolvableStatus.INTRA_FILE_RESOLVED));
    }

    /**
     * Checks inter file feature linking with included file with dependency
     * feature undefined.
     */
    @Test
    public void processFeatureInIncludedFileWithDependencyUndefined()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/interfilefeatureincludedependencyUndefined";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode selfNode = null;
        YangNode refNode1 = null;
        YangNode refNode2 = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Carry out linking of sub module with module.
        yangLinkerManager.linkSubModulesToParentModule(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        // Add references to include list.
        yangLinkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        for (YangNode rootNode : utilManager.getYangNodeSet()) {
            if (rootNode.getName().equals("syslog1")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("syslog2")) {
                refNode1 = rootNode;
            } else {
                refNode2 = rootNode;
            }
        }

        // Check whether the data model tree returned is of type module.
        assertThat(selfNode instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("syslog1"));

        ListIterator<YangFeature> featureIterator = yangNode.getFeatureList().listIterator();
        YangFeature feature = featureIterator.next();
        assertThat(feature.getName(), is("frr-te"));

        YangIfFeature ifFeature = feature.getIfFeatureList().iterator().next();
        assertThat(ifFeature.getName().getName(), is("p2mp-te"));
        assertThat(ifFeature.getResolvableStatus(), Is.is(ResolvableStatus.INTRA_FILE_RESOLVED));

        YangContainer container = (YangContainer) selfNode.getChild();
        assertThat(container.getName(), is("speed"));
        YangLeaf leaf = container.getListOfLeaf().iterator().next();
        assertThat(leaf.getName(), is("local-storage-limit"));

        List<YangIfFeature> ifFeatureList = leaf.getIfFeatureList();
        ifFeature = ifFeatureList.iterator().next();
        assertThat(ifFeature.getName().getName(), is("frr-te"));
        assertThat(ifFeature.getResolvableStatus(), Is.is(ResolvableStatus.INTRA_FILE_RESOLVED));
    }
}

