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

import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangContainer;
import org.onosproject.yang.compiler.datamodel.YangFeature;
import org.onosproject.yang.compiler.datamodel.YangIfFeature;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeType;
import org.onosproject.yang.compiler.datamodel.YangPathArgType;
import org.onosproject.yang.compiler.datamodel.YangPathOperator;
import org.onosproject.yang.compiler.datamodel.YangPathPredicate;
import org.onosproject.yang.compiler.datamodel.YangRelativePath;
import org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.linker.exceptions.LinkerException;
import org.onosproject.yang.compiler.linker.impl.YangLinkerManager;
import org.onosproject.yang.compiler.linker.impl.YangLinkerUtils;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.tool.YangCompilerManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.onosproject.yang.compiler.datamodel.YangNodeType.MODULE_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.LEAFREF;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;

/**
 * Test cases for testing leafref intra file linking.
 */
public class IntraFileLeafrefLinkingTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final YangCompilerManager utilManager =
            new YangCompilerManager();
    private final YangLinkerManager yangLinkerManager = new YangLinkerManager();

    /**
     * Checks self resolution when grouping and uses are siblings.
     * Grouping followed by uses.
     */
    @Test
    public void processSelfResolutionWhenLeafrefRefersAnotherDerivedType()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafreftoderivedtype";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("ietf-network")) {
            selfNode = rootNode;
        }
        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("ietf-network"));

        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;

        //YangGrouping grouping = (YangGrouping) yangNode.getChild().getNextSibling();
        leafIterator = yangNode.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct under grouping.
        assertThat(leafInfo.getName(), is("network-ref"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) (leafInfo.getDataType().getDataTypeExtendedInfo());

        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.DERIVED));
    }

    /**
     * Checks self resolution when leafref refers to many other leafref.
     */
    @Test
    public void processSelfResolutionWhenLeafrefReferToMultipleTypedef()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafreftomultitypedef";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("Test")) {
            selfNode = rootNode;
        }
        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("Test"));

        YangContainer containerInModule = (YangContainer) yangNode.getChild().getNextSibling();
        YangContainer containerInList = (YangContainer) containerInModule.getChild().getChild();

        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;

        leafIterator = containerInList.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct under grouping.
        assertThat(leafInfo.getName(), is("remove"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) (leafInfo.getDataType().getDataTypeExtendedInfo());

        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.DERIVED));
    }

    /**
     * Checks self resolution when leafref refers to many other leaf with derived type
     * which in turn referring to another leaf.
     */
    @Test
    public void processSelfResolutionWhenLeafrefReferToDerivedTypeReferringToLeafWithLeafref()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafreftotypedefwithleafref";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("Test")) {
            selfNode = rootNode;
        }
        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("Test"));

        YangContainer containerInModule = (YangContainer) yangNode.getChild().getNextSibling();
        YangContainer containerInList = (YangContainer) containerInModule.getChild().getChild();

        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;

        leafIterator = containerInList.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct under grouping.
        assertThat(leafInfo.getName(), is("remove"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) leafInfo.getDataType().getDataTypeExtendedInfo();

        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.ENUMERATION));
    }

    /**
     * Checks self resolution when leafref under module refers to leaf in container with relative path.
     */
    @Test
    public void processSelfResolutionWhenLeafrefReferToContainerLeafRelPath()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/relativepath/simpleleafref";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("ietf-network")) {
            selfNode = rootNode;
        }
        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("ietf-network"));

        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;

        leafIterator = yangNode.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafInfo.getName(), is("network-ref"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) (leafInfo.getDataType().getDataTypeExtendedInfo());

        // Check whether leafref type got resolved.
        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.UINT8));
    }

    /**
     * Checks self resolution when leafref under module refers to grouping rpc with input as name.
     * Rpc has input child also. So here the node search must be done by taking input node using relative path.
     */
    @Ignore
    public void processSelfResolutionWhenLeafrefInModuleReferToGroupingWithInputInRpcRelPath()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/relativepath/leafreftoinputwithgroupinginrpc";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("ietf-network")) {
            selfNode = rootNode;
        }
        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("ietf-network"));

        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;

        leafIterator = yangNode.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafInfo.getName(), is("network-ref"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) (leafInfo.getDataType().getDataTypeExtendedInfo());

        // Check whether leafref type got resolved.
        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.UINT8));
    }

    /**
     * Checks self resolution when leafref under module refers to invalid root node with relative path.
     */
    @Test
    public void processSelfResolutionWhenLeafrefInModuleReferToInvalidRootNodeRelPath()
            throws IOException, ParserException {

        thrown.expect(LinkerException.class);
        thrown.expectMessage(
                "YANG file error: The target node, in the leafref path ../../../define/network-id, is invalid.");
        String searchDir = "src/test/resources/leafreflinker/intrafile/relativepath/invalidrelativeancestoraccess";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());
    }

    /**
     * Checks self resolution when leafref under module refers to invalid node.
     * Inter file linking also has to be done to know the error message with relative path.
     */
    @Test
    public void processSelfResolutionWhenLeafrefInModuleReferToInvalidNodeRelPath()
            throws IOException, ParserException {

        thrown.expect(LinkerException.class);
        thrown.expectMessage(
                "YANG file error: Unable to find base leaf/leaf-list for given leafref path ../define/network-id");
        String searchDir = "src/test/resources/leafreflinker/intrafile/relativepath/invalidnode";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        //Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());
    }

    /**
     * Checks self resolution when leafref of leaf-list under module refers to leaf in container with relative path.
     */
    @Test
    public void processSelfResolutionWhenLeafrefInTypedefReferToContainerRelPath()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/relativepath/leafrefintypedef";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("ietf-network")) {
            selfNode = rootNode;
        }
        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("ietf-network"));
        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;
        YangContainer yangContainer = (YangContainer) yangNode.getChild();
        leafIterator = yangContainer.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct under grouping.
        assertThat(leafInfo.getName(), is("network-id"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));

        YangLeafRef leafref = (YangLeafRef) (leafInfo.getDataType().getDataTypeExtendedInfo());

        // Check whether leafref type got resolved.
        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.UINT8));
    }

    /**
     * Checks self resolution when leafref refers to many other leafref with relative path.
     */
    @Test
    public void processSelfResolutionWhenLeafrefReferToMultipleLeafrefRelPath()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/relativepath/leafreftomultileafref";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("Test")) {
            selfNode = rootNode;
        }
        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("Test"));

        YangContainer containerInModule = (YangContainer) yangNode.getChild().getNextSibling();
        YangContainer containerInList = (YangContainer) containerInModule.getChild().getChild();

        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;

        leafIterator = containerInList.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct under grouping.
        assertThat(leafInfo.getName(), is("remove"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) (leafInfo.getDataType().getDataTypeExtendedInfo());

        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.ENUMERATION));
    }

    /**
     * Checks self resolution when leafref refers to many other leaf with derived type
     * which in turn referring to another leaf with relative type.
     */
    @Test
    public void processSelfResolutionWhenLeafrefReferToDerivedTypeReferringToLeafWithLeafrefRelType()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/relativepath/leafreftotypedef";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("Test")) {
            selfNode = rootNode;
        }
        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("Test"));

        YangContainer containerInModule = (YangContainer) yangNode.getChild().getNextSibling();
        YangContainer containerInList = (YangContainer) containerInModule.getChild().getChild();

        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;

        leafIterator = containerInList.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct under grouping.
        assertThat(leafInfo.getName(), is("remove"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) leafInfo.getDataType().getDataTypeExtendedInfo();

        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.ENUMERATION));
    }

    /**
     * Checks the valid scenerios of path argument having proper setters.
     */
    @Test
    public void processPathArgumentStatement()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/relativepath/pathlistener";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("PathListener")) {
            selfNode = rootNode;
        }
        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("PathListener"));
        YangList listInModule = (YangList) yangNode.getChild();

        YangContainer containerInModule = (YangContainer) yangNode.getChild().getNextSibling();
        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;

        YangLeaf leafNameInList = listInModule.getListOfLeaf().listIterator().next();

        leafIterator = containerInModule.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct under grouping.
        assertThat(leafInfo.getName(), is("ifname"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) leafInfo.getDataType().getDataTypeExtendedInfo();
        assertThat(leafref.getPathType(), Is.is(YangPathArgType.ABSOLUTE_PATH));

        YangRelativePath relativePathForName = leafref.getRelativePath();
        assertThat(relativePathForName.getAncestorNodeCount(), is(2));
        List<YangAtomicPath> absPathForName = relativePathForName.getAtomicPathList();
        Iterator<YangAtomicPath> absPathIteratorForName = absPathForName.listIterator();
        YangAtomicPath abspathForName = absPathIteratorForName.next();
        assertThat(abspathForName.getNodeIdentifier().getName(), is("interface"));
        assertThat(abspathForName.getNodeIdentifier().getPrefix(), is("test"));
        YangAtomicPath abspath1 = absPathIteratorForName.next();
        assertThat(abspath1.getNodeIdentifier().getName(), is("name"));
        assertThat(abspath1.getNodeIdentifier().getPrefix(), is("test"));

        YangLeaf leafInfo1 = leafIterator.next();
        // Check whether the information in the leaf is correct under grouping.
        assertThat(leafInfo1.getName(), is("status"));
        assertThat(leafInfo1.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo1.getDataType().getDataType(), is(YangDataTypes.LEAFREF));

        YangLeafRef leafref1 = (YangLeafRef) leafInfo1.getDataType().getDataTypeExtendedInfo();
        assertThat(leafref1.getPathType(), is(YangPathArgType.ABSOLUTE_PATH));

        List<YangAtomicPath> absolutePathList = leafref1.getAtomicPath();
        Iterator<YangAtomicPath> absPathIterator = absolutePathList.listIterator();
        YangAtomicPath abspath = absPathIterator.next();
        assertThat(abspath.getNodeIdentifier().getName(), is("interface"));
        assertThat(abspath.getNodeIdentifier().getPrefix(), is("test"));

        List<YangPathPredicate> pathPredicateList = abspath.getPathPredicatesList();
        Iterator<YangPathPredicate> pathPredicate = pathPredicateList.listIterator();
        YangPathPredicate pathPredicate1 = pathPredicate.next();
        assertThat(pathPredicate1.getNodeId().getName(), is("name"));
        assertThat(pathPredicate1.getNodeId().getPrefix(), nullValue());
        assertThat(pathPredicate1.getRelPath().getAncestorNodeCount(), is(1));
        assertThat(pathPredicate1.getPathOp(), is(YangPathOperator.EQUALTO));
        assertThat(pathPredicate1.getRelPath().getAtomicPathList().listIterator().next().getNodeIdentifier()
                           .getName(), is("ifname"));
        //TODO : Fill the path predicates
//        assertThat(pathPredicate1.getLeftAxisNode(), is(leafNameInList));
//        assertThat(pathPredicate1.getRightAxisNode(), is(leafInfo));
    }

    /**
     * Checks inter file resolution when leafref refers to multiple leafrefs through many files.
     */
    @Test
    public void processInterFileLeafrefRefersToMultipleLeafrefInMultipleFiles()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/interfile" +
                "/interfileleafrefreferstomultipleleafrefinmultiplefiles";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        YangNode refNode1 = null;
        YangNode refNode2 = null;
        YangNode selfNode = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        for (YangNode rootNode : utilManager.getYangNodeSet()) {
            if (rootNode.getName().equals("ietf-network-topology")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("ietf-network")) {
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
        assertThat(yangNode.getName(), is("ietf-network-topology"));

        YangList list = (YangList) yangNode.getChild().getChild();
        ListIterator<YangLeaf> leafIterator = list.getListOfLeaf().listIterator();
        YangLeaf leafInfo = leafIterator.next();

        assertThat(leafInfo.getName(), is("link-tp"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(LEAFREF));

        YangLeafRef leafref = (YangLeafRef) leafInfo.getDataType().getDataTypeExtendedInfo();

        YangLeaf leafInfo2 = (YangLeaf) leafref.getReferredLeafOrLeafList();
        assertThat(leafref.getReferredLeafOrLeafList(), is(leafInfo2));
        assertThat(leafref.getResolvableStatus(), Is.is(ResolvableStatus.RESOLVED));

        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.STRING));
    }


    /**
     * Checks addition of if-feature list to leafref.
     */
    @Test
    public void processSelfFileLinkingWithFeatureReferredByLeafref()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/iffeatuinleafref/simpleleafrefwithiffeature";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("syslog")) {
            selfNode = rootNode;
        }
        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("syslog"));

        List<YangFeature> featureList = yangNode.getFeatureList();
        YangFeature feature = featureList.iterator().next();
        assertThat(feature.getName(), is("local-storage"));

        YangContainer container = (YangContainer) yangNode.getChild();
        assertThat(container.getName(), is("speed"));

        List<YangLeaf> listOfLeaf = container.getListOfLeaf();
        YangLeaf leaf = listOfLeaf.iterator().next();
        assertThat(leaf.getName(), is("local-storage-limit"));

        List<YangIfFeature> ifFeatureList = leaf.getIfFeatureList();
        YangIfFeature ifFeature = ifFeatureList.iterator().next();
        assertThat(ifFeature.getName().getName(), is("local-storage"));
        assertThat(ifFeature.getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeaf> listOfLeafInModule = yangNode.getListOfLeaf().listIterator();
        YangLeaf yangLeaf = listOfLeafInModule.next();
        assertThat(yangLeaf.getName(), is("storage-value"));

        YangLeafRef leafRef = (YangLeafRef) yangLeaf.getDataType().getDataTypeExtendedInfo();

        assertThat(leafRef.getEffectiveDataType().getDataType(), is(YangDataTypes.UINT64));

        List<YangIfFeature> ifFeatureListInLeafref = leafRef.getIfFeatureList();
        YangIfFeature ifFeatureInLeafref = ifFeatureListInLeafref.iterator().next();
        assertThat(ifFeatureInLeafref.getName().getName(), is("local-storage"));
        assertThat(ifFeatureInLeafref.getResolvableStatus(), is(ResolvableStatus.RESOLVED));
    }

    /**
     * Checks addition of if-feature list to leafref when referred leaf is again having leafref in it.
     */
    @Test
    public void processSelfFileLinkingWithFeatureReferredByMultiLeafref()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/iffeatuinleafref/featurebymultileafref";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("syslog")) {
            selfNode = rootNode;
        }
        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("syslog"));

        List<YangFeature> featureList = yangNode.getFeatureList();
        YangFeature feature = featureList.iterator().next();
        assertThat(feature.getName(), is("local-storage"));

        YangContainer container = (YangContainer) yangNode.getChild();
        assertThat(container.getName(), is("speed"));

        List<YangLeaf> listOfLeaf = container.getListOfLeaf();
        YangLeaf leaf = listOfLeaf.iterator().next();
        assertThat(leaf.getName(), is("local-storage-limit"));

        List<YangIfFeature> ifFeatureList = leaf.getIfFeatureList();
        YangIfFeature ifFeature = ifFeatureList.iterator().next();
        assertThat(ifFeature.getName().getName(), is("local-storage"));
        assertThat(ifFeature.getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeaf> listOfLeafInModule = yangNode.getListOfLeaf().listIterator();
        YangLeaf yangLeaf = listOfLeafInModule.next();
        assertThat(yangLeaf.getName(), is("storage-value"));

        YangLeafRef leafRef = (YangLeafRef) yangLeaf.getDataType().getDataTypeExtendedInfo();

        assertThat(leafRef.getEffectiveDataType().getDataType(), is(YangDataTypes.UINT64));

        List<YangIfFeature> ifFeatureListInLeafref = leafRef.getIfFeatureList();
        YangIfFeature ifFeatureInLeafref = ifFeatureListInLeafref.iterator().next();

        assertThat(ifFeatureInLeafref.getName().getName(), is("main-storage"));
        assertThat(ifFeatureInLeafref.getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        YangIfFeature ifFeatureInLeafref1 = ifFeatureListInLeafref.iterator().next();

        assertThat(ifFeatureInLeafref1.getName().getName(), is("main-storage"));
        assertThat(ifFeatureInLeafref1.getResolvableStatus(), is(ResolvableStatus.RESOLVED));
    }

    /**
     * Checks self resolution when leafref in grouping is copied to augment.
     */
    @Test
    public void processSelfResolutionWhenLeafrefInGroupingIsUnderAugment()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafrefInAugment";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("topology")) {
            selfNode = rootNode;
        }

        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("topology"));

        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;

        YangAugment augment = (YangAugment) yangNode.getChild().getNextSibling();

        YangList list = (YangList) augment.getChild().getChild().getChild().getChild().getChild();

        leafIterator = list.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafInfo.getName(), is("src-tp-ref"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) (leafInfo.getDataType().getDataTypeExtendedInfo());

        // Check whether leafref type got resolved.
        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.UINT8));
    }

    /**
     * Checks self resolution when leafref under grouping's uses.
     */
    @Test
    public void processSelfResolutionWhenLeafrefUnderGroupingUses()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafrefinusesundergrouping";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        YangNode selfNode = null;
        YangNode refNode = null;

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("ietf-network")) {
            selfNode = rootNode;
            refNode = yangNodeIterator.next();
        } else {
            refNode = rootNode;
            selfNode = yangNodeIterator.next();
        }

        // Check whether the data model tree returned is of type module.
        assertThat(selfNode instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("ietf-network"));

        // Check whether the module name is set correctly.
        YangModule yangNode1 = (YangModule) refNode;
        assertThat(yangNode1.getName(), is("network"));

        YangContainer yangContainer = (YangContainer) yangNode.getChild().getNextSibling().getNextSibling();
        assertThat(yangContainer.getName(), is("fine"));

        YangContainer yangContainer1 = (YangContainer) yangContainer.getChild().getNextSibling();
        assertThat(yangContainer1.getName(), is("hi"));

        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;

        leafIterator = yangContainer1.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafInfo.getName(), is("network-id-ref"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) (leafInfo.getDataType().getDataTypeExtendedInfo());

        // Check whether leafref type got resolved.
        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.DERIVED));
    }

    /**
     * Checks self resolution when leafref under typedef refers to the node where it is used.
     */
    @Test
    public void processSelfResolutionWhenLeafrefInTypedefIsUsedInSameReferredNode()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafrefintypedefwithsamereferpath";

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

        YangLinkerUtils.updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        Iterator<YangNode> yangNodeIterator = utilManager.getYangNodeSet().iterator();

        YangNode rootNode = yangNodeIterator.next();

        if (rootNode.getName().equals("typedef")) {
            selfNode = rootNode;
        }

        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("typedef"));

        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;

        YangContainer yangContainer = (YangContainer) yangNode.getChild().getNextSibling();

        leafIterator = yangContainer.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafInfo.getName(), is("reference"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) (leafInfo.getDataType().getDataTypeExtendedInfo());

        // Check whether leafref type got resolved.
        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.UINT8));
    }
}
