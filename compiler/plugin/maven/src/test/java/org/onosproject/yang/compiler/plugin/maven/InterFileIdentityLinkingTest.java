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
import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangIdentity;
import org.onosproject.yang.compiler.datamodel.YangIdentityRef;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangTypeDef;
import org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.linker.exceptions.LinkerException;
import org.onosproject.yang.compiler.linker.impl.YangLinkerManager;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.tool.YangCompilerManager;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.onosproject.yang.compiler.utils.io.impl.YangIoUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.datamodel.YangNodeType.MODULE_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.IDENTITYREF;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.updateFilePriority;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;

/**
 * Test cases for testing inter file linking for identity.
 */
public class InterFileIdentityLinkingTest {

    private final YangCompilerManager utilManager =
            new YangCompilerManager();
    private final YangLinkerManager yangLinkerManager = new YangLinkerManager();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Checks inter file feature linking with imported file.
     */
    @Test
    public void processIdentityInImportedFile()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/interfileidentityimport";
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

        updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        for (YangNode rootNode : utilManager.getYangNodeSet()) {
            if (rootNode.getName().equals("IdentityIntraFile")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("IdentityInModule")) {
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
        assertThat(yangNode.getName(), is("IdentityIntraFile"));

        YangIdentity yangIdentity = (YangIdentity) yangNode.getChild();
        assertThat(yangIdentity.getName(), is("ipv4-address-family"));
        assertThat(yangIdentity.getBaseNode().getBaseIdentifier().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        yangIdentity = (YangIdentity) yangNode.getChild().getNextSibling();
        assertThat(yangIdentity.getName(), is("ipv6-address-family"));
        assertThat(yangIdentity.getBaseNode().getBaseIdentifier().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeaf> leafIterator = yangNode.getListOfLeaf().listIterator();
        YangLeaf leafInfo = leafIterator.next();

        assertThat(leafInfo.getName(), is("tunnel"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("identityref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.IDENTITYREF));
        YangIdentityRef yangIdentityRef = (YangIdentityRef) leafInfo.getDataType().getDataTypeExtendedInfo();
        assertThat(yangIdentityRef.getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getBaseIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeafList> leafListIterator = yangNode.getListOfLeafList().listIterator();
        YangLeafList leafListInfo = leafListIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafListInfo.getName(), is("network-ref"));
        assertThat(leafListInfo.getDataType().getDataTypeName(), is("identityref"));
        assertThat(leafListInfo.getDataType().getDataType(), is(YangDataTypes.IDENTITYREF));
        yangIdentityRef = (YangIdentityRef) (leafListInfo.getDataType().getDataTypeExtendedInfo());
        assertThat(yangIdentityRef.getBaseIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getResolvableStatus(), is(ResolvableStatus.RESOLVED));
    }

    @Test
    public void processTranslator() throws IOException, ParserException, MojoExecutionException {

        YangIoUtils.deleteDirectory("target/identityTranslator/");
        String searchDir = "src/test/resources/interfileidentityimport";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir("target/identityTranslator/");
        utilManager.translateToJava(yangPluginConfig);
        YangPluginConfig.compileCode(System.getProperty("user.dir") + File
                .separator + "target/identityTranslator/");
        YangIoUtils.deleteDirectory("target/identityTranslator/");
    }

    /**
     * Checks inter file feature linking with included file.
     */
    @Test
    public void processIdentityInIncludedFile()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/interfileidentityinlude";
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

        updateFilePriority(utilManager.getYangNodeSet());

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

        YangIdentity yangIdentity = (YangIdentity) yangNode.getChild();
        assertThat(yangIdentity.getName(), is("ipv4-address-family"));
        assertThat(yangIdentity.getBaseNode().getBaseIdentifier().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        yangIdentity = (YangIdentity) yangNode.getChild().getNextSibling();
        assertThat(yangIdentity.getName(), is("ipv6-address-family"));
        assertThat(yangIdentity.getBaseNode().getBaseIdentifier().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeaf> leafIterator = yangNode.getListOfLeaf().listIterator();
        YangLeaf leafInfo = leafIterator.next();

        assertThat(leafInfo.getName(), is("tunnel"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("identityref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.IDENTITYREF));
        YangIdentityRef yangIdentityRef = (YangIdentityRef) leafInfo.getDataType().getDataTypeExtendedInfo();
        assertThat(yangIdentityRef.getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getBaseIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeafList> leafListIterator = yangNode.getListOfLeafList().listIterator();
        YangLeafList leafListInfo = leafListIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafListInfo.getName(), is("network-ref"));
        assertThat(leafListInfo.getDataType().getDataTypeName(), is("identityref"));
        assertThat(leafListInfo.getDataType().getDataType(), is(YangDataTypes.IDENTITYREF));
        yangIdentityRef = (YangIdentityRef) (leafListInfo.getDataType().getDataTypeExtendedInfo());
        assertThat(yangIdentityRef.getBaseIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getResolvableStatus(), is(ResolvableStatus.RESOLVED));
    }

    /**
     * Checks inter file feature linking with imported file with dependency.
     */
    @Test
    public void processIdentityInImportedFileWithDependency()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/interfileidentityimportdependency";
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

        updateFilePriority(utilManager.getYangNodeSet());

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

        YangIdentity yangIdentity = (YangIdentity) yangNode.getChild();
        assertThat(yangIdentity.getName(), is("ipv4-address-family"));
        assertThat(yangIdentity.getBaseNode().getBaseIdentifier().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        yangIdentity = (YangIdentity) yangNode.getChild().getNextSibling();
        assertThat(yangIdentity.getName(), is("ipv6-address-family"));
        assertThat(yangIdentity.getBaseNode().getBaseIdentifier().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeaf> leafIterator = yangNode.getListOfLeaf().listIterator();
        YangLeaf leafInfo = leafIterator.next();

        assertThat(leafInfo.getName(), is("tunnel"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("identityref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.IDENTITYREF));
        YangIdentityRef yangIdentityRef = (YangIdentityRef) leafInfo.getDataType().getDataTypeExtendedInfo();
        assertThat(yangIdentityRef.getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getBaseIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeafList> leafListIterator = yangNode.getListOfLeafList().listIterator();
        YangLeafList leafListInfo = leafListIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafListInfo.getName(), is("network-ref"));
        assertThat(leafListInfo.getDataType().getDataTypeName(), is("identityref"));
        assertThat(leafListInfo.getDataType().getDataType(), is(YangDataTypes.IDENTITYREF));
        yangIdentityRef = (YangIdentityRef) (leafListInfo.getDataType().getDataTypeExtendedInfo());
        assertThat(yangIdentityRef.getBaseIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getResolvableStatus(), is(ResolvableStatus.RESOLVED));
    }

    /**
     * Checks inter file feature linking with included file with dependency.
     */
    @Test
    public void processIdentityInIncludedFileWithDependency()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/interfileidentityincludedependency";
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

        updateFilePriority(utilManager.getYangNodeSet());

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

        YangIdentity yangIdentity = (YangIdentity) yangNode.getChild();
        assertThat(yangIdentity.getName(), is("ipv4-address-family"));
        assertThat(yangIdentity.getBaseNode().getBaseIdentifier().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        yangIdentity = (YangIdentity) yangNode.getChild().getNextSibling();
        assertThat(yangIdentity.getName(), is("ipv6-address-family"));
        assertThat(yangIdentity.getBaseNode().getBaseIdentifier().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeaf> leafIterator = yangNode.getListOfLeaf().listIterator();
        YangLeaf leafInfo = leafIterator.next();

        assertThat(leafInfo.getName(), is("tunnel"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("identityref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.IDENTITYREF));
        YangIdentityRef yangIdentityRef = (YangIdentityRef) leafInfo.getDataType().getDataTypeExtendedInfo();
        assertThat(yangIdentityRef.getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getBaseIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeafList> leafListIterator = yangNode.getListOfLeafList().listIterator();
        YangLeafList leafListInfo = leafListIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafListInfo.getName(), is("network-ref"));
        assertThat(leafListInfo.getDataType().getDataTypeName(), is("identityref"));
        assertThat(leafListInfo.getDataType().getDataType(), is(YangDataTypes.IDENTITYREF));
        yangIdentityRef = (YangIdentityRef) (leafListInfo.getDataType().getDataTypeExtendedInfo());
        assertThat(yangIdentityRef.getBaseIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getResolvableStatus(), is(ResolvableStatus.RESOLVED));
    }

    /**
     * Checks inter file feature linking with imported file with dependency
     * feature undefined.
     */
    @Test
    public void processIdentityInImportedFileWithDependencyUndefined()
            throws IOException, LinkerException, MojoExecutionException {
        thrown.expect(LinkerException.class);
        thrown.expectMessage("YANG file error: Unable to find base identity for given base");

        String searchDir = "src/test/resources/interfileidentityimportdependencyUndefined";
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

        updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());
    }

    /**
     * Checks inter file feature linking with included file with dependency
     * feature undefined.
     */
    @Test
    public void processIdentityInIncludedFileWithDependencyUndefined()
            throws IOException, LinkerException, MojoExecutionException {
        thrown.expect(LinkerException.class);
        thrown.expectMessage("YANG file error: Unable to find base identity for given base");

        String searchDir = "src/test/resources/interfileidentityincludedependencyUndefined";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilManager.createYangFileInfoSet(paths);
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();

        // Create YANG node set
        yangLinkerManager.createYangNodeSet(utilManager.getYangNodeSet());

        // Carry out linking of sub module with module.
        yangLinkerManager.linkSubModulesToParentModule(utilManager.getYangNodeSet());

        // Add references to import list.
        yangLinkerManager.addRefToYangFilesImportList(utilManager.getYangNodeSet());

        // Add references to include list.
        yangLinkerManager.addRefToYangFilesIncludeList(utilManager.getYangNodeSet());

        // Update the priority for all the files.
        updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());
    }

    /**
     * Checks inter file feature linking with imported file.
     */
    @Test
    public void processIdentityTypedefUnresolvedInImportedFile()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/interfileidentitytypedef";
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

        updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        for (YangNode rootNode : utilManager.getYangNodeSet()) {
            if (rootNode.getName().equals("IdentityIntraFile")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("IdentityInModule")) {
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
        assertThat(yangNode.getName(), is("IdentityIntraFile"));

        YangIdentity yangIdentity = (YangIdentity) yangNode.getChild();
        assertThat(yangIdentity.getName(), is("ipv4-address-family"));
        assertThat(yangIdentity.getBaseNode().getBaseIdentifier().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        yangIdentity = (YangIdentity) yangNode.getChild().getNextSibling();
        assertThat(yangIdentity.getName(), is("ipv6-address-family"));
        assertThat(yangIdentity.getBaseNode().getBaseIdentifier().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeaf> leafIterator = yangNode.getListOfLeaf().listIterator();
        YangLeaf leafInfo = leafIterator.next();

        assertThat(leafInfo.getName(), is("tunnel"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("identityref"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.IDENTITYREF));
        YangIdentityRef yangIdentityRef = (YangIdentityRef) leafInfo.getDataType().getDataTypeExtendedInfo();
        assertThat(yangIdentityRef.getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getBaseIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentityRef.getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeafList> leafListIterator = yangNode.getListOfLeafList().listIterator();
        YangLeafList leafListInfo = leafListIterator.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafListInfo.getName(), is("network-ref"));
        assertThat(leafListInfo.getDataType().getDataTypeName(), is("identityref"));
        assertThat(leafListInfo.getDataType().getDataType(), is(YangDataTypes.IDENTITYREF));
        yangIdentityRef = (YangIdentityRef) (leafListInfo.getDataType().getDataTypeExtendedInfo());
        // Check whether leafref type got resolved.
        assertThat(yangIdentityRef.getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        YangTypeDef typedef = (YangTypeDef) yangNode.getChild().getNextSibling().getNextSibling();
        assertThat(typedef.getName(), is("type15"));

        YangType type = typedef.getTypeList().iterator().next();
        assertThat(type.getDataType(), is(YangDataTypes.IDENTITYREF));
        assertThat(type.getDataTypeName(), is("identityref"));

        YangIdentityRef identityRef = (YangIdentityRef) type.getDataTypeExtendedInfo();
        assertThat(identityRef.getName(), is("ref-address-family"));
        assertThat(identityRef.getBaseIdentity().getName(), is("ref-address-family"));
        assertThat(identityRef.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));
    }

    /**
     * Checks inter file feature linking with imported file.
     */
    @Test
    public void processIdentityTypedefInImportedFile()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/interfileidentitytypedef";
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

        updateFilePriority(utilManager.getYangNodeSet());

        // Carry out inter-file linking.
        yangLinkerManager.processInterFileLinking(utilManager.getYangNodeSet());

        for (YangNode rootNode : utilManager.getYangNodeSet()) {
            if (rootNode.getName().equals("IdentityTypedef")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("IdentityInModule")) {
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
        assertThat(yangNode.getName(), is("IdentityTypedef"));

        YangIdentity yangIdentity = (YangIdentity) yangNode.getChild();
        assertThat(yangIdentity.getName(), is("ipv4-address-family"));
        assertThat(yangIdentity.getBaseNode().getBaseIdentifier().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        yangIdentity = (YangIdentity) yangNode.getChild().getNextSibling();
        assertThat(yangIdentity.getName(), is("ipv6-address-family"));
        assertThat(yangIdentity.getBaseNode().getBaseIdentifier().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getReferredIdentity().getName(), is("ref-address-family"));
        assertThat(yangIdentity.getBaseNode().getResolvableStatus(), is(ResolvableStatus.RESOLVED));

        YangTypeDef typedef = (YangTypeDef) yangNode.getChild().getNextSibling().getNextSibling();
        assertThat(typedef.getName(), is("type15"));

        YangType type = typedef.getTypeList().iterator().next();
        assertThat(type.getDataType(), is(YangDataTypes.IDENTITYREF));
        assertThat(type.getDataTypeName(), is("identityref"));

        YangIdentityRef identityRef = (YangIdentityRef) type.getDataTypeExtendedInfo();
        assertThat(identityRef.getName(), is("ref-address-family"));
        assertThat(identityRef.getBaseIdentity().getName(), is("ref-address-family"));

        ListIterator<YangLeaf> leafIterator = yangNode.getListOfLeaf().listIterator();
        YangLeaf leafInfo = leafIterator.next();

        assertThat(leafInfo.getName(), is("tunnel"));
        YangDerivedInfo info = (YangDerivedInfo) leafInfo.getDataType()
                .getDataTypeExtendedInfo();
        assertThat(info.getEffectiveBuiltInType(), is(IDENTITYREF));
        YangType type1 = info.getReferredTypeDef().getTypeList().get(0);
        YangIdentityRef idRef1 =
                (YangIdentityRef) type1.getDataTypeExtendedInfo();
        assertThat(idRef1.getResolvableStatus(),
                   is(ResolvableStatus.RESOLVED));

        ListIterator<YangLeafList> itr =
                yangNode.getListOfLeafList().listIterator();
        YangLeafList leafListInfo = itr.next();

        // Check whether the information in the leaf is correct.
        assertThat(leafListInfo.getName(), is("network-ref"));
        info = (YangDerivedInfo) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();
        assertThat(info.getEffectiveBuiltInType(), is(IDENTITYREF));
        type1 = info.getReferredTypeDef().getTypeList().get(0);
        idRef1 = (YangIdentityRef) type1.getDataTypeExtendedInfo();
        assertThat(idRef1.getResolvableStatus(), is(ResolvableStatus.RESOLVED));
    }
}
