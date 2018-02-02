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
import org.onosproject.yang.compiler.datamodel.YangContainer;
import org.onosproject.yang.compiler.datamodel.YangIdentity;
import org.onosproject.yang.compiler.datamodel.YangIdentityRef;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.linker.impl.YangLinkerManager;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.tool.YangCompilerManager;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.datamodel.YangNodeType.MODULE_NODE;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.updateFilePriority;
import static org.onosproject.yang.compiler.utils.io.YangPluginConfig.compileCode;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.deleteDirectory;


/**
 * Translator test case for identity.
 */
public class IdentityTranslatorTest {

    private static final String DIR = "target/identity/";
    private static final String COMP = System.getProperty("user.dir")
            + File.separator + DIR;
    private static final String RSC = "src/test/resources/";
    private final YangLinkerManager linkMgr = new YangLinkerManager();
    private final YangCompilerManager util = new YangCompilerManager();

    /**
     * Checks translation should not result in any exception.
     *
     * @throws MojoExecutionException if mojo exception occur
     */
    @Test
    public void processTranslator() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = RSC + "identityTranslator";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        util.createYangFileInfoSet(paths);
        util.parseYangFileInfoSet();
        util.createYangNodeSet();
        util.resolveDependenciesUsingLinker();

        YangPluginConfig config = new YangPluginConfig();

        config.setCodeGenDir(DIR);
        util.translateToJava(config);
        compileCode(COMP);
        deleteDirectory(DIR);
    }

    /**
     * Checks translation should not result in any exception.
     *
     * @throws MojoExecutionException if mojo exception occur
     */
    @Test
    public void processMultipleLevelIdentity() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = RSC + "multipleIdentity";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        util.createYangFileInfoSet(paths);
        util.parseYangFileInfoSet();
        util.createYangNodeSet();

        YangNode selfNode = null;

        linkMgr.createYangNodeSet(util.getYangNodeSet());
        linkMgr.addRefToYangFilesImportList(util.getYangNodeSet());
        updateFilePriority(util.getYangNodeSet());

        linkMgr.processInterFileLinking(util.getYangNodeSet());
        linkMgr.processIdentityExtendList(util.getYangNodeSet());

        for (YangNode rootNode : util.getYangNodeSet()) {
            if (rootNode.getName().equals("test")) {
                selfNode = rootNode;
            }
        }

        assertThat(selfNode instanceof YangModule, is(true));
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));
        YangModule yangNode = (YangModule) selfNode;
        assertThat(yangNode.getName(), is("test"));

        YangIdentity id = ((YangIdentity) yangNode.getChild());
        assertThat(id.getName(), is("identity3"));

        assertThat(id.getExtendList().get(0).getName(), is("identity2"));
        assertThat(id.getExtendList().get(1).getName(), is("identity1"));

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        util.translateToJava(yangPluginConfig);
        compileCode(COMP);
        deleteDirectory(DIR);
    }

    /**
     * Processes the extend list of an identity in self-file linking.
     *
     * @throws MojoExecutionException if mojo exception occur
     */
    @Test
    public void processSelfFileIdExtendList1() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = RSC + "identityextend/self/test1";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        util.createYangFileInfoSet(paths);
        util.parseYangFileInfoSet();
        util.createYangNodeSet();

        YangNode selfNode = null;
        linkMgr.createYangNodeSet(util.getYangNodeSet());
        linkMgr.addRefToYangFilesImportList(util.getYangNodeSet());
        updateFilePriority(util.getYangNodeSet());

        linkMgr.processInterFileLinking(util.getYangNodeSet());
        linkMgr.processIdentityExtendList(util.getYangNodeSet());

        for (YangNode rootNode : util.getYangNodeSet()) {
            if (rootNode.getName().equals("self-id1")) {
                selfNode = rootNode;
            }
        }

        YangModule node = (YangModule) selfNode;
        YangIdentity id3 = (YangIdentity) node.getChild();
        YangIdentity id8 = (YangIdentity) id3.getNextSibling();
        YangIdentity id2 = (YangIdentity) id8.getNextSibling();
        YangIdentity id1 = (YangIdentity) id2.getNextSibling();
        YangIdentity id4 = (YangIdentity) id1.getNextSibling();
        YangIdentity id5 = (YangIdentity) id4.getNextSibling();
        YangIdentity id6 = (YangIdentity) id5.getNextSibling();
        YangIdentity id7 = (YangIdentity) id6.getNextSibling();

        validateExtendListContent(id3, new LinkedList<>(
                asList(id4, id5, id6, id7)));
        validateExtendListContent(id8, new LinkedList<>());
        validateExtendListContent(id2, new LinkedList<>(
                asList(id3, id4, id5, id6, id7, id8)));
        validateExtendListContent(id1, new LinkedList<>(
                asList(id2, id3, id4, id5, id6, id7, id8)));
        validateExtendListContent(id4, new LinkedList<>(asList(id5, id6, id7)));
        validateExtendListContent(id5, new LinkedList<>());
        validateExtendListContent(id6, new LinkedList<>(asList(id7)));
        validateExtendListContent(id7, new LinkedList<>());

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        util.translateToJava(yangPluginConfig);
        compileCode(COMP);
        deleteDirectory(DIR);
    }

    /**
     * Processes the extend list of an identity in self-file linking.
     *
     * @throws MojoExecutionException if mojo exception occur
     */
    @Test
    public void processSelfFileIdExtendList2() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = RSC + "identityextend/self/test2";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        util.createYangFileInfoSet(paths);
        util.parseYangFileInfoSet();
        util.createYangNodeSet();

        YangNode selfNode = null;
        linkMgr.createYangNodeSet(util.getYangNodeSet());
        linkMgr.addRefToYangFilesImportList(util.getYangNodeSet());
        updateFilePriority(util.getYangNodeSet());

        linkMgr.processInterFileLinking(util.getYangNodeSet());
        linkMgr.processIdentityExtendList(util.getYangNodeSet());

        for (YangNode rootNode : util.getYangNodeSet()) {
            if (rootNode.getName().equals("self-id2")) {
                selfNode = rootNode;
            }
        }

        YangModule node = (YangModule) selfNode;
        YangIdentity id1 = (YangIdentity) node.getChild();
        YangIdentity id2 = (YangIdentity) id1.getNextSibling();
        YangIdentity id5 = (YangIdentity) id2.getNextSibling();
        YangIdentity id3 = (YangIdentity) id5.getNextSibling();
        YangIdentity id6 = (YangIdentity) id3.getNextSibling();
        YangIdentity id7 = (YangIdentity) id6.getNextSibling();
        YangIdentity id8 = (YangIdentity) id7.getNextSibling();
        YangIdentity id4 = (YangIdentity) id8.getNextSibling();
        YangIdentity id9 = (YangIdentity) id4.getNextSibling();

        validateExtendListContent(id1, new LinkedList<>(
                asList(id2, id3, id4, id5, id6, id7, id8, id9)));
        validateExtendListContent(id2, new LinkedList<>(
                asList(id3, id4, id6, id9)));
        validateExtendListContent(id5, new LinkedList<>(asList(id7, id8)));
        validateExtendListContent(id3, new LinkedList<>(asList(id4, id9)));
        validateExtendListContent(id6, new LinkedList<>());
        validateExtendListContent(id7, new LinkedList<>());
        validateExtendListContent(id8, new LinkedList<>());
        validateExtendListContent(id4, new LinkedList<>());
        validateExtendListContent(id9, new LinkedList<>());

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        util.translateToJava(yangPluginConfig);
        compileCode(COMP);
        deleteDirectory(DIR);
    }

    /**
     * Processes the extend list of an identity in inter-file linking.
     *
     * @throws MojoExecutionException if mojo exception occur
     */
    @Test
    public void processInterFileIdExtendList1() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = RSC + "identityextend/inter/test1";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        util.createYangFileInfoSet(paths);
        util.parseYangFileInfoSet();
        util.createYangNodeSet();

        YangNode selfNode = null;
        linkMgr.createYangNodeSet(util.getYangNodeSet());
        linkMgr.addRefToYangFilesImportList(util.getYangNodeSet());
        updateFilePriority(util.getYangNodeSet());

        linkMgr.processInterFileLinking(util.getYangNodeSet());
        linkMgr.processIdentityExtendList(util.getYangNodeSet());
        YangNode refNode1 = null;
        YangNode refNode2 = null;

        for (YangNode rootNode : util.getYangNodeSet()) {
            if (rootNode.getName().equals("file-test1-a")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("file-test1-b")) {
                refNode1 = rootNode;
            } else {
                refNode2 = rootNode;
            }
        }

        YangModule node = (YangModule) selfNode;
        YangIdentity id1 = (YangIdentity) node.getChild();
        YangIdentity id2 = (YangIdentity) id1.getNextSibling();
        YangIdentity id3 = (YangIdentity) id2.getNextSibling();

        YangModule nodeb = (YangModule) refNode1;
        YangIdentity id4 = (YangIdentity) nodeb.getChild();
        YangIdentity id5 = (YangIdentity) id4.getNextSibling();
        YangIdentity id6 = (YangIdentity) id5.getNextSibling();
        YangIdentity id7 = (YangIdentity) id6.getNextSibling();

        YangModule nodec = (YangModule) refNode2;
        YangIdentity id8 = (YangIdentity) nodec.getChild();
        YangIdentity id9 = (YangIdentity) id8.getNextSibling();
        YangIdentity id10 = (YangIdentity) id9.getNextSibling();
        YangIdentity id11 = (YangIdentity) id10.getNextSibling();

        validateExtendListContent(id1, new LinkedList<>(asList(
                id2, id3, id4, id5, id6, id7, id8, id9, id10, id11)));
        validateExtendListContent(id2, new LinkedList<>(
                asList(id4, id5, id10, id11)));
        validateExtendListContent(id3, new LinkedList<>());
        validateExtendListContent(id4, new LinkedList<>(asList(id10, id11)));
        validateExtendListContent(id5, new LinkedList<>());
        validateExtendListContent(id6, new LinkedList<>());
        validateExtendListContent(id7, new LinkedList<>());
        validateExtendListContent(id8, new LinkedList<>());
        validateExtendListContent(id9, new LinkedList<>());
        validateExtendListContent(id10, new LinkedList<>());
        validateExtendListContent(id11, new LinkedList<>());

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        util.translateToJava(yangPluginConfig);
        compileCode(COMP);
        deleteDirectory(DIR);
    }

    /**
     * Processes the extend list of an identity in inter-file linking.
     *
     * @throws MojoExecutionException if mojo exception occur
     */
    @Test
    public void processInterFileIdExtendList2() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = RSC + "identityextend/inter/test2";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        util.createYangFileInfoSet(paths);
        util.parseYangFileInfoSet();
        util.createYangNodeSet();

        YangNode selfNode = null;
        linkMgr.createYangNodeSet(util.getYangNodeSet());
        linkMgr.addRefToYangFilesImportList(util.getYangNodeSet());
        updateFilePriority(util.getYangNodeSet());

        linkMgr.processInterFileLinking(util.getYangNodeSet());
        linkMgr.processIdentityExtendList(util.getYangNodeSet());
        YangNode refNode1 = null;
        YangNode refNode2 = null;

        for (YangNode rootNode : util.getYangNodeSet()) {
            if (rootNode.getName().equals("file-test2-a")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("file-test2-b")) {
                refNode1 = rootNode;
            } else {
                refNode2 = rootNode;
            }
        }

        YangModule node = (YangModule) selfNode;
        YangIdentity id2 = (YangIdentity) node.getChild();
        YangIdentity id3 = (YangIdentity) id2.getNextSibling();
        YangIdentity id4 = (YangIdentity) id3.getNextSibling();

        YangModule nodeb = (YangModule) refNode1;
        YangIdentity id1 = (YangIdentity) nodeb.getChild();
        YangIdentity id5 = (YangIdentity) id1.getNextSibling();
        YangIdentity id6 = (YangIdentity) id5.getNextSibling();

        YangModule nodec = (YangModule) refNode2;
        YangIdentity id7 = (YangIdentity) nodec.getChild();
        YangIdentity id8 = (YangIdentity) id7.getNextSibling();
        YangIdentity id9 = (YangIdentity) id8.getNextSibling();
        YangIdentity id10 = (YangIdentity) id9.getNextSibling();
        YangIdentity id11 = (YangIdentity) id10.getNextSibling();
        YangIdentity id12 = (YangIdentity) id11.getNextSibling();
        YangIdentity id13 = (YangIdentity) id12.getNextSibling();
        YangIdentity id14 = (YangIdentity) id13.getNextSibling();
        YangIdentity id15 = (YangIdentity) id14.getNextSibling();
        YangIdentity id16 = (YangIdentity) id15.getNextSibling();
        YangIdentity id17 = (YangIdentity) id16.getNextSibling();
        YangIdentity id18 = (YangIdentity) id17.getNextSibling();

        validateExtendListContent(id1, new LinkedList<>(asList(
                id2, id3, id4, id5, id6, id7, id8, id9, id10, id11, id12,
                id13, id14, id15, id16, id17, id18)));
        validateExtendListContent(id2, new LinkedList<>(asList(id3, id4)));
        validateExtendListContent(id3, new LinkedList<>());
        validateExtendListContent(id4, new LinkedList<>());
        validateExtendListContent(id5, new LinkedList<>());
        validateExtendListContent(id6, new LinkedList<>(asList(
                id7, id8, id9, id10, id11, id12, id13, id14, id15, id16, id17,
                id18)));
        validateExtendListContent(id7, new LinkedList<>(asList(
                id11, id15, id16, id17, id18)));
        validateExtendListContent(id8, new LinkedList<>(asList(id12)));
        validateExtendListContent(id9, new LinkedList<>(asList(id13)));
        validateExtendListContent(id10, new LinkedList<>(asList(id14)));
        validateExtendListContent(id11, new LinkedList<>(asList(
                id15, id16, id17, id18)));
        validateExtendListContent(id12, new LinkedList<>());
        validateExtendListContent(id13, new LinkedList<>());
        validateExtendListContent(id14, new LinkedList<>());
        validateExtendListContent(id15, new LinkedList<>());
        validateExtendListContent(id16, new LinkedList<>());
        validateExtendListContent(id17, new LinkedList<>());
        validateExtendListContent(id18, new LinkedList<>());

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        util.translateToJava(yangPluginConfig);
        compileCode(COMP);
        deleteDirectory(DIR);
    }

    /**
     * Processes the extend list of an identity in inter-file linking.
     *
     * @throws MojoExecutionException if mojo exception occur
     */
    @Test
    public void processInterFileIdExtendListIssue() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = RSC + "identityextend/inter/issue";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        util.createYangFileInfoSet(paths);
        util.parseYangFileInfoSet();
        util.createYangNodeSet();

        YangNode selfNode = null;
        linkMgr.createYangNodeSet(util.getYangNodeSet());
        linkMgr.addRefToYangFilesImportList(util.getYangNodeSet());
        updateFilePriority(util.getYangNodeSet());

        linkMgr.processInterFileLinking(util.getYangNodeSet());
        linkMgr.processIdentityExtendList(util.getYangNodeSet());
        YangNode refNode2 = null;

        for (YangNode rootNode : util.getYangNodeSet()) {
            if (rootNode.getName().equals("filea")) {
                selfNode = rootNode;
            } else if (rootNode.getName().equals("filec")) {
                refNode2 = rootNode;
            }
        }

        YangModule node = (YangModule) selfNode;
        YangContainer container = (YangContainer) node.getChild();
        Iterator<YangLeaf> it = container.getListOfLeaf().iterator();
        YangLeaf leaf = it.next();
        YangIdentityRef ref = (YangIdentityRef) leaf.getDataType()
                .getDataTypeExtendedInfo();

        YangIdentity intType = ref.getReferredIdentity();

        YangModule nodec = (YangModule) refNode2;
        YangIdentity jaT = (YangIdentity) nodec.getChild();
        YangIdentity gtp = (YangIdentity) jaT.getNextSibling();
        YangIdentity pdnLoop1 = (YangIdentity) gtp.getNextSibling();
        YangIdentity pdnLoop2 = (YangIdentity) pdnLoop1.getNextSibling();
        YangIdentity opChGr = (YangIdentity) pdnLoop2.getNextSibling();
        YangIdentity home = (YangIdentity) opChGr.getNextSibling();

        validateExtendListContent(intType, new LinkedList<>(asList(
                jaT, gtp, pdnLoop1, pdnLoop2, opChGr, home)));
        validateExtendListContent(jaT, new LinkedList<>(asList(
                gtp, pdnLoop1, pdnLoop2, opChGr, home)));
        validateExtendListContent(gtp, new LinkedList<>());
        validateExtendListContent(pdnLoop1, new LinkedList<>());
        validateExtendListContent(pdnLoop2, new LinkedList<>());
        validateExtendListContent(opChGr, new LinkedList<>());
        validateExtendListContent(home, new LinkedList<>());

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        util.translateToJava(yangPluginConfig);
        compileCode(COMP);
        deleteDirectory(DIR);
    }

    /**
     * Validates the extend list content in an identity.
     *
     * @param id     YANG identity
     * @param idList extend list to validate
     */
    private void validateExtendListContent(YangIdentity id,
                                           List<YangIdentity> idList) {
        List<YangIdentity> list = id.getExtendList();
        assertThat(list.size(), is(idList.size()));
        if (!idList.isEmpty()) {
            for (YangIdentity ext : idList) {
                assertThat(getErrMsg(ext, id), list.contains(ext), is(true));
            }
        }
    }

    /**
     * Returns the error message for extend-list failure.
     *
     * @param ext extend list identity
     * @param id  holder identity
     * @return error message
     */
    private String getErrMsg(YangIdentity ext, YangIdentity id) {
        return "Identity " + ext.getName() + " does not exist in the extend-" +
                "list of " + id.getName();
    }
}
