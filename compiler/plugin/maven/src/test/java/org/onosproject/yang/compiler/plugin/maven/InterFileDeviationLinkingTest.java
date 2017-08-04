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

import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangContainer;
import org.onosproject.yang.compiler.datamodel.YangDeviateAdd;
import org.onosproject.yang.compiler.datamodel.YangDeviateDelete;
import org.onosproject.yang.compiler.datamodel.YangDeviateReplace;
import org.onosproject.yang.compiler.datamodel.YangDeviation;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.linker.exceptions.LinkerException;
import org.onosproject.yang.compiler.linker.impl.YangLinkerManager;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.tool.YangCompilerManager;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoContainer;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.datamodel.YangNodeType.MODULE_NODE;
import static org.onosproject.yang.compiler.datamodel.YangStatusType.CURRENT;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.UNRESOLVED;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT8;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT16;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.updateFilePriority;
import static org.onosproject.yang.compiler.utils.io.YangPluginConfig.compileCode;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.deleteDirectory;


/**
 * Unit test case for deviation linking.
 */
public class InterFileDeviationLinkingTest {

    private static final String DIR = "target/deviationTest/";
    private final YangCompilerManager utilManager =
            new YangCompilerManager();
    private final YangLinkerManager yangLinkerManager = new YangLinkerManager();
    private static final String COMP = System.getProperty("user.dir") + File
            .separator + DIR;

    /**
     * Checks deviation not supported statement linking.
     */
    @Test
    public void processDeviationNotSupportedLinking() throws IOException,
            ParserException {

        String searchDir = "src/test/resources/deviationLinking";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode refNode = null;
        YangNode selfNode = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        updateFilePriority(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        while (yangNodeIterator.hasNext()) {
            YangNode rootNode = yangNodeIterator.next();
            if (rootNode.getName().equals("deviation-module")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("Test2")) {
                refNode = rootNode;
            }
        }

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the deviation module info is set correctly after
        // parsing.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("deviation-module"));

        // Check whether the container is child of module
        YangDeviation deviation = (YangDeviation) yangNode.getChild();
        assertThat(deviation.getName(), is("/t:ospf"));
        assertThat(deviation.getDescription(), is("\"desc\""));
        assertThat(deviation.getReference(), is("\"ref\""));
        assertThat(deviation.isDeviateNotSupported(), is(true));
        List<YangAtomicPath> targetNode = deviation.getTargetNode();
        assertThat(targetNode.get(0).getNodeIdentifier().getName(), is("ospf"));
        assertThat(targetNode.get(0).getNodeIdentifier().getPrefix(), is("t"));
        assertThat(deviation.getResolvableStatus(), is(UNRESOLVED));

        // Check whether the base module - test information is set correctly.
        YangModule yangRefNode = (YangModule) refNode;
        assertThat(yangRefNode.getName(), is("Test2"));

        YangNode ospfNode = yangRefNode.getChild();
        assertThat(ospfNode.getName(), is("ospf"));

        YangNode testValid = ospfNode.getNextSibling();
        assertThat(testValid.getName(), is("valid"));

        assertThat(testValid.getNextSibling(), nullValue());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());
        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        utilManager.translateToJava(yangPluginConfig);

        compileCode(COMP);
        deleteDirectory(DIR);

        // Check whether the data model tree returned is of type module.
        assertThat(selfNode instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        yangRefNode = (YangModule) refNode;
        assertThat(yangRefNode.getName(), is("Test2"));

        YangNode childNode2 = yangRefNode.getChild();
        assertThat(childNode2.getName(), is("ospf"));

        testValid = childNode2.getNextSibling();
        assertThat(testValid.getName(), is("valid"));

        // Check whether the module name is set correctly.
        yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("deviation-module"));

        YangNode deviationValid = yangNode.getChild().getNextSibling()
                .getNextSibling();
        assertThat(deviationValid.getName(), is("valid"));

        List<YangLeaf> lisfOfLeaf = ((YangLeavesHolder) deviationValid).getListOfLeaf();
        assertThat(lisfOfLeaf.isEmpty(), is(true));
        assertThat(deviationValid.getNextSibling(), nullValue());

        assertThat(testValid.getYangSchemaNodeIdentifier(),
                   is(deviationValid.getYangSchemaNodeIdentifier()));

        JavaFileInfoTranslator deviateJavaFile = ((JavaFileInfoContainer)
                deviationValid).getJavaFileInfo();

        JavaFileInfoTranslator testJavaFile = ((JavaFileInfoContainer)
                testValid).getJavaFileInfo();
        assertThat(testJavaFile, is(deviateJavaFile));
    }

    /**
     * Checks deviate add statement linking.
     */
    @Test
    public void processDeviationAddLinking() throws IOException,
            ParserException {

        String searchDir = "src/test/resources/deviationLinking";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode refNode = null;
        YangNode selfNode = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        updateFilePriority(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        while (yangNodeIterator.hasNext()) {
            YangNode rootNode = yangNodeIterator.next();
            if (rootNode.getName().equals("deviation-module2")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("Test2")) {
                refNode = rootNode;
            }
        }

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the deviation module info is set correctly after
        // parsing.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("deviation-module2"));

        // Check whether the container is child of module
        YangDeviation deviation = (YangDeviation) yangNode.getChild();
        assertThat(deviation.getName(), is("/t:ospf"));
        assertThat(deviation.getDescription(), is("\"desc\""));
        assertThat(deviation.getReference(), is("\"ref\""));

        List<YangAtomicPath> targetNode = deviation.getTargetNode();
        assertThat(targetNode.get(0).getNodeIdentifier().getName(), is("ospf"));
        assertThat(targetNode.get(0).getNodeIdentifier().getPrefix(), is("t"));

        YangDeviateAdd deviateAdd = deviation.getDeviateAdd().get(0);
        assertThat(deviateAdd.getListOfMust().get(0).getConstraint(),
                   is("/base:system"));
        assertThat(deviateAdd.isConfig(), is(false));
        assertThat(deviation.getResolvableStatus(), is(UNRESOLVED));

        // Check whether the base module - test information is set correctly.
        YangModule yangRefNode = (YangModule) refNode;
        assertThat(yangRefNode.getName(), is("Test2"));

        YangNode ospfNode = yangRefNode.getChild();
        assertThat(ospfNode.getName(), is("ospf"));
        assertThat(((YangContainer) ospfNode).getListOfMust().isEmpty(), is(true));
        assertThat(((YangContainer) ospfNode).isConfig(), is(true));

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        utilManager.translateToJava(yangPluginConfig);

        compileCode(COMP);
        deleteDirectory(DIR);

        // Check whether the data model tree returned is of type module.
        assertThat(selfNode instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("deviation-module2"));

        YangContainer deviationValid = ((YangContainer) yangNode.getChild()
                .getNextSibling()
                .getNextSibling());
        assertThat(deviationValid.getName(), is("ospf"));
        assertThat(deviationValid.getListOfMust().get(0).getConstraint(),
                   is("/base:system"));
        assertThat(deviationValid.isConfig(), is(false));
    }

    /**
     * Checks deviate delete statement linking.
     */
    @Test
    public void processDeviationDeleteLinking() throws IOException,
            ParserException {

        String searchDir = "src/test/resources/deviationLinking";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode refNode = null;
        YangNode selfNode = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        updateFilePriority(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        while (yangNodeIterator.hasNext()) {
            YangNode rootNode = yangNodeIterator.next();
            if (rootNode.getName().equals("deviation-module4")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("Test2")) {
                refNode = rootNode;
            }
        }

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the deviation module info is set correctly after
        // parsing.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("deviation-module4"));

        // Check whether the container is child of module
        YangDeviation deviation = (YangDeviation) yangNode.getChild();
        assertThat(deviation.getName(), is("/t:ospf/t:process-id"));
        assertThat(deviation.getDescription(), is("\"desc\""));
        assertThat(deviation.getReference(), is("\"ref\""));

        List<YangAtomicPath> targetNode = deviation.getTargetNode();
        assertThat(targetNode.get(0).getNodeIdentifier().getName(), is("ospf"));
        assertThat(targetNode.get(0).getNodeIdentifier().getPrefix(), is("t"));
        assertThat(targetNode.get(1).getNodeIdentifier().getName(), is("process-id"));
        assertThat(targetNode.get(1).getNodeIdentifier().getPrefix(), is("t"));

        YangDeviateDelete delete = deviation.getDeviateDelete().get(0);
        assertThat(delete.getUnits(), is("\"units\""));
        assertThat(delete.getDefaultValueInString(), is("defaultValue"));
        assertThat(deviation.getResolvableStatus(), is(UNRESOLVED));

        // Check whether the base module - test information is set correctly.
        YangModule yangRefNode = (YangModule) refNode;
        assertThat(yangRefNode.getName(), is("Test2"));

        YangNode ospfNode = yangRefNode.getChild();
        assertThat(ospfNode.getName(), is("ospf"));
        YangLeaf leaf = ((YangLeavesHolder) ospfNode).getListOfLeaf().get(0);
        assertThat(leaf.getName(), is("process-id"));
        assertThat(leaf.getDataType().getDataType(), is(UINT16));
        assertThat(leaf.getUnits(), is("\"seconds\""));
        assertThat(leaf.getStatus(), is(CURRENT));
        assertThat(leaf.getReference(), is("\"RFC 6020\""));
        assertThat(leaf.getDefaultValueInString(), is("1"));

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        utilManager.translateToJava(yangPluginConfig);

        compileCode(COMP);
        deleteDirectory(DIR);

        // Check whether the data model tree returned is of type module.
        assertThat(selfNode instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("deviation-module4"));

        YangContainer ospf = (YangContainer) yangNode.getChild()
                .getNextSibling();
        assertThat(ospf.getName(), is("ospf"));

        leaf = ospf.getListOfLeaf().get(0);
        assertThat(leaf.getName(), is("process-id"));
        assertThat(leaf.getDataType().getDataType(), is(UINT16));
        assertThat(leaf.getUnits(), nullValue());
        assertThat(leaf.getDefaultValueInString(), nullValue());
    }

    /**
     * Checks deviate replace statement linking.
     */
    @Test
    public void processDeviationReplaceLinking() throws IOException,
            ParserException {

        String searchDir = "src/test/resources/deviationLinking";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode refNode = null;
        YangNode selfNode = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        updateFilePriority(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        while (yangNodeIterator.hasNext()) {
            YangNode rootNode = yangNodeIterator.next();
            if (rootNode.getName().equals("deviation-module3")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("Test2")) {
                refNode = rootNode;
            }
        }

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the deviation module info is set correctly after
        // parsing.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("deviation-module3"));

        // Check whether the container is child of module
        YangDeviation deviation = (YangDeviation) yangNode.getChild();
        assertThat(deviation.getName(), is("/t:ospf/t:process-id"));
        assertThat(deviation.getDescription(), is("\"desc\""));
        assertThat(deviation.getReference(), is("\"ref\""));

        List<YangAtomicPath> targetNode = deviation.getTargetNode();
        assertThat(targetNode.get(0).getNodeIdentifier().getName(), is("ospf"));
        assertThat(targetNode.get(0).getNodeIdentifier().getPrefix(), is("t"));
        assertThat(targetNode.get(1).getNodeIdentifier().getName(), is("process-id"));
        assertThat(targetNode.get(1).getNodeIdentifier().getPrefix(), is("t"));

        YangDeviateReplace replace = deviation.getDeviateReplace().get(0);
        assertThat(replace.getDataType().getDataType(), is(INT8));
        assertThat(replace.getUnits(), is("\"units\""));
        assertThat(replace.getDefaultValueInString(), is("0"));
        assertThat(replace.isConfig(), is(true));
        assertThat(replace.isMandatory(), is(true));
        assertThat(replace.getMinElements().getMinElement(), is(0));
        assertThat(replace.getMaxElements().getMaxElement(), is(3));
        assertThat(deviation.getResolvableStatus(), is(UNRESOLVED));

        // Check whether the base module - test information is set correctly.
        YangModule yangRefNode = (YangModule) refNode;
        assertThat(yangRefNode.getName(), is("Test2"));

        YangNode ospfNode = yangRefNode.getChild();
        assertThat(ospfNode.getName(), is("ospf"));
        YangLeaf leaf = ((YangLeavesHolder) ospfNode).getListOfLeaf().get(0);
        assertThat(leaf.getName(), is("process-id"));
        assertThat(leaf.getDataType().getDataType(), is(UINT16));
        assertThat(leaf.getUnits(), is("\"seconds\""));
        assertThat(leaf.getStatus(), is(CURRENT));
        assertThat(leaf.getReference(), is("\"RFC 6020\""));

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        utilManager.translateToJava(yangPluginConfig);

        compileCode(COMP);
        deleteDirectory(DIR);

        // Check whether the data model tree returned is of type module.
        assertThat(selfNode instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("deviation-module3"));

        YangContainer ospf = (YangContainer) yangNode.getChild()
                .getNextSibling();
        assertThat(ospf.getName(), is("ospf"));

        leaf = ospf.getListOfLeaf().get(0);
        assertThat(leaf.getName(), is("process-id"));
        assertThat(leaf.getDataType().getDataType(), is(INT8));
        assertThat(leaf.getUnits(), is("\"units\""));
        assertThat(leaf.getDefaultValueInString(), is("0"));
        assertThat(leaf.isConfig(), is(true));
        assertThat(leaf.isMandatory(), is(true));
        assertThat(leaf.getStatus(), is(CURRENT));
        assertThat(leaf.getReference(), is("\"RFC 6020\""));
    }

    /**
     * Checks whether exception is thrown when deviation target is invalid.
     */
    @Test(expected = LinkerException.class)
    public void processDeviationInvalidTarget() throws IOException,
            ParserException {

        String searchDir = "src/test/resources/InvalidDeviation";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        YangNode selfNode = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        updateFilePriority(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        while (yangNodeIterator.hasNext()) {
            YangNode rootNode = yangNodeIterator.next();
            if (rootNode.getName().equals("deviation-module5")) {
                selfNode = rootNode;
            }
        }

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the deviation module info is set correctly after
        // parsing.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("deviation-module5"));

        // Check whether the container is child of module
        YangDeviation deviation = (YangDeviation) yangNode.getChild();
        assertThat(deviation.getName(), is("/t:ospf/t:intf-id"));
        assertThat(deviation.getDescription(), is("\"desc\""));
        assertThat(deviation.getReference(), is("\"ref\""));

        List<YangAtomicPath> targetNode = deviation.getTargetNode();
        assertThat(targetNode.get(0).getNodeIdentifier().getName(), is("ospf"));
        assertThat(targetNode.get(0).getNodeIdentifier().getPrefix(), is("t"));
        assertThat(targetNode.get(1).getNodeIdentifier().getName(), is("intf-id"));
        assertThat(targetNode.get(1).getNodeIdentifier().getPrefix(), is("t"));

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        utilManager.translateToJava(yangPluginConfig);
    }
}
