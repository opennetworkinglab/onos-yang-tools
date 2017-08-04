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

import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.compiler.datamodel.YangContainer;
import org.onosproject.yang.compiler.datamodel.YangInput;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeType;
import org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.linker.exceptions.LinkerException;
import org.onosproject.yang.compiler.linker.impl.YangLinkerManager;
import org.onosproject.yang.compiler.linker.impl.YangLinkerUtils;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;
import org.onosproject.yang.compiler.tool.YangCompilerManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;

/**
 * Test cases for testing leafref intra file linking.
 */
public class IntraFileLeafrefLinking2Test {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final YangCompilerManager utilManager =
            new YangCompilerManager();
    private final YangLinkerManager yangLinkerManager = new YangLinkerManager();
    private final YangUtilsParserManager manager = new YangUtilsParserManager();

    /**
     * Checks self resolution when leafref under module refers to leaf in container.
     */
    @Test
    public void processSelfResolutionWhenLeafrefReferToContainerLeaf()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/simpleleafref";

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

        if (rootNode.getName().equals("SelfResolutionWhenLeafrefReferToContainerLeaf")) {
            selfNode = rootNode;
        }

        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("SelfResolutionWhenLeafrefReferToContainerLeaf"));

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
                   Is.is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.UINT8));
    }

    /**
     * Checks self resolution when leafref under module refers to leaf in input of rpc.
     */
    @Test
    public void processSelfResolutionWhenLeafrefInModuleReferToLeafInInputOfRpc()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafrefwithrpc";

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

        if (rootNode.getName().equals("SelfResolutionWhenLeafrefInModuleReferToLeafInInputOfRpc")) {
            selfNode = rootNode;
        }

        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("SelfResolutionWhenLeafrefInModuleReferToLeafInInputOfRpc"));

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
     * Rpc has input child also. So here the node search must be done by taking input node.
     * TODO: When path has RPC's input but grouping & typedef with the same name occurs.
     */
    @Ignore
    public void processSelfResolutionWhenLeafrefInModuleReferToGroupingWithInputInRpc()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafrefwithrpcandgrouping";

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

        if (rootNode.getName().equals("SelfResolutionWhenLeafrefInModuleReferToGroupingWithInputInRpc")) {
            selfNode = rootNode;
        }

        // Check whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("SelfResolutionWhenLeafrefInModuleReferToGroupingWithInputInRpc"));

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
     * Checks self resolution when leafref under module refers to grouping under module.
     * Grouping/typedef cannot be referred.
     */
    @Test
    public void processSelfResolutionWhenLeafrefInModuleReferToGrouping()
            throws IOException, ParserException {
/*
        thrown.expect(LinkerException.class);
        thrown.expectMessage(
                "YANG file error: The target node, in the leafref path /networks/network-id, is invalid.");
*/
        String searchDir = "src/test/resources/leafreflinker/intrafile/invalidscenerioforgrouping";

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
     * Checks self resolution error scenerio where leafref is without path.
     */
    @Test
    public void processSelfResolutionWhenLeafrefDoesntHavePath()
            throws IOException, ParserException {

        thrown.expect(ParserException.class);
        thrown.expectMessage(
                "YANG file error : a type leafref must have one path statement.");
        YangNode node = manager
                .getDataModel("src/test/resources/SelfResolutionWhenLeafrefDoesntHavePath.yang");
    }

    /**
     * Checks self resolution when leafref under module refers to invalid node.
     */
    @Test
    public void processSelfResolutionWhenLeafrefInModuleReferToInvalidNode()
            throws IOException, ParserException {

        thrown.expect(LinkerException.class);
        thrown.expectMessage(
                "YANG file error: Unable to find base leaf/leaf-list for given leafref path /define/network-id");
        String searchDir = "src/test/resources/leafreflinker/intrafile/invalidsceneriowithinvalidnode";

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
     * Inter file linking also has to be done to know the error message.
     */
    @Test
    public void processSelfResolutionWhenLeafrefIsInDeepTreeAndLeafIsInModuleWithReferredTypeUnion()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafreflinking";

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

        YangContainer containerParent = (YangContainer) yangNode.getChild().getChild().getChild();
        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;

        leafIterator = containerParent.getListOfLeaf().listIterator();
        leafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafInfo.getName(), is("name"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) (leafInfo.getDataType().getDataTypeExtendedInfo());

        // Check whether leafref type got resolved.
        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.UNION));
    }

    /**
     * Checks self resolution when leafref of leaf-list under module refers to leaf in container.
     */
    @Test
    public void processSelfResolutionWhenLeafrefReferToContainerLeafList()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafrefreferingtoleaflist";

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

        ListIterator<YangLeafList> leafListIterator;
        YangLeafList leafListInfo;

        leafListIterator = yangNode.getListOfLeafList().listIterator();
        leafListInfo = leafListIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafListInfo.getName(), is("network-ref"));
        assertThat(leafListInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafListInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) (leafListInfo.getDataType().getDataTypeExtendedInfo());

        // Check whether leafref type got resolved.
        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.UINT8));
    }

    /**
     * Checks self resolution when leafref of leaf-list under module refers to leaf-list in input of rpc.
     */
    @Test
    public void processSelfResolutionWhenLeafrefInModuleReferToLeafListInInputOfRpc()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafreftoinputinrpc";

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

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("ietf-network"));

        ListIterator<YangLeafList> leafListIterator;
        YangLeafList leafListInfo;

        leafListIterator = yangNode.getListOfLeafList().listIterator();
        leafListInfo = leafListIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafListInfo.getName(), is("network-ref"));
        assertThat(leafListInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafListInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) (leafListInfo.getDataType().getDataTypeExtendedInfo());

        // Check whether leafref type got resolved.
        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.UINT8));
    }

    /**
     * Checks self resolution when leafref of leaf-list under module refers to invalid node.
     * Inter file linking also has to be done to know the error message.
     */
    @Test
    public void processSelfResolutionWhenLeafrefIsInDeepTreeAndLeafListIsInModuleWithReferredTypeEnumeration()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafrefwithrefleafderived";

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

        // Check whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) selfNode;

        assertThat(yangNode.getName(), is("Test"));

        YangContainer containerParent = (YangContainer) yangNode.getChild().getChild().getChild();
        ListIterator<YangLeafList> leafListListIterator;
        YangLeafList leafListInfo;

        leafListListIterator = containerParent.getListOfLeafList().listIterator();
        leafListInfo = leafListListIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafListInfo.getName(), is("name"));
        assertThat(leafListInfo.getDataType().getDataTypeName(), is("leafref"));
        assertThat(leafListInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));
        YangLeafRef leafref = (YangLeafRef) (leafListInfo.getDataType().getDataTypeExtendedInfo());

        // Check whether leafref type got resolved.
        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.ENUMERATION));
    }

    /**
     * Checks the error scenerio when the referred node is not a leaf or leaf-list.
     */
    @Test
    public void processSelfResolutionWhenLeafrefDoesNotReferToLeafOrLeafList()
            throws IOException, ParserException {

        thrown.expect(LinkerException.class);
        thrown.expectMessage(
                "YANG file error: Unable to find base leaf/leaf-list for given leafref path /networks");
        String searchDir = "src/test/resources/leafreflinker/intrafile/invalidsceneriowithnorefleaf";

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
     * Checks self resolution when leafref of leaf-list under module refers to leaf in container.
     */
    @Test
    public void processSelfResolutionWhenLeafrefInTypedefReferToContainer()
            throws IOException, ParserException {
        String searchDir = "src/test/resources/leafreflinker/intrafile/leafrefintypedef";

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

        YangContainer yangContainer = (YangContainer) yangNode.getChild();
        ListIterator<YangLeaf> leafIterator;
        YangLeaf leafInfo;
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
     * Checks self resolution when leafref of leaf-list under module refers to leaf-list in input of rpc.
     */
    @Test
    public void processSelfResolutionWhenLeafrefInTypedefModuleReferToLeafListInInputOfRpc()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafreftorpcinputleaflist";

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

        YangInput yangInput = (YangInput) yangNode.getChild().getChild();

        ListIterator<YangLeafList> leafListIterator;
        YangLeafList yangLeafListInfo;
        leafListIterator = yangInput.getListOfLeafList().listIterator();
        yangLeafListInfo = leafListIterator.next();

        // Check whether the information in the leaf is correct under grouping.
        assertThat(yangLeafListInfo.getName(), is("network-id"));
        assertThat(yangLeafListInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));

        YangLeafRef leafref = (YangLeafRef) (yangLeafListInfo.getDataType().getDataTypeExtendedInfo());

        // Check whether leafref type got resolved.
        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.UINT8));
    }

    /**
     * Checks self resolution when leafref of leaf-list under module refers to invalid node.
     * Inter file linking also has to be done to know the error message.
     */
    @Test
    public void processSelfResolutionWhenLeafrefInTypedefIsInDeepTreeAndLeafListIsInModuleWithReferredTypeEnumeration()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafreftoleafrefwithtypedef";

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

        YangContainer yangContainer = (YangContainer) yangNode.getChild().getChild().getChild().getNextSibling();

        ListIterator<YangLeaf> leafIterator;
        YangLeaf yangLeafInfo;
        leafIterator = yangContainer.getListOfLeaf().listIterator();
        yangLeafInfo = leafIterator.next();

        // Check whether the information in the leaf is correct under grouping.
        assertThat(yangLeafInfo.getName(), is("interval"));
        assertThat(yangLeafInfo.getDataType().getDataType(), is(YangDataTypes.LEAFREF));

        YangLeafRef leafref = (YangLeafRef) (yangLeafInfo.getDataType().getDataTypeExtendedInfo());

        // Check whether leafref type got resolved.
        assertThat(leafref.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        // Check the effective type for the leaf.
        assertThat(leafref.getEffectiveDataType().getDataType(),
                   is(YangDataTypes.ENUMERATION));
    }

    /**
     * Checks self resolution when grouping and uses are siblings.
     * Grouping followed by uses.
     */
    @Test
    public void processSelfResolutionWhenLeafrefRefersAnotherLeafref()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafreftoleafref";

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
                   is(YangDataTypes.UINT8));
    }

    /**
     * Checks self resolution when leafref refers to many other leafref.
     */
    @Test
    public void processSelfResolutionWhenLeafrefReferToMultipleLeafref()
            throws IOException, ParserException {

        String searchDir = "src/test/resources/leafreflinker/intrafile/leafreftomultileafref";

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

}
