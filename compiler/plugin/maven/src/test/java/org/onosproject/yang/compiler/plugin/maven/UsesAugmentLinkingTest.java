/*
 * Copyright 2017-present Open Networking Foundation
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
import org.onosproject.yang.compiler.datamodel.YangAugmentableNode;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.tool.YangCompilerManager;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.System.getProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.RESOLVED;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.deleteDirectory;

/**
 * Unit test case for uses augment linker and translator.
 */
public class UsesAugmentLinkingTest {

    private static final String DIR = "target/usesaugment/";
    private static final String COMP = getProperty("user.dir") +
            File.separator + DIR;
    private final YangCompilerManager utilMgr = new YangCompilerManager();
    private final String path = "src/test/resources/usesaugment/";

    /**
     * Processes a simple uses augment linker.
     *
     * @throws IOException if IO exception occurs
     */
    @Test
    public void processSimpleUsesAug() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(path + "simpleusesaug/")) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        utilMgr.resolveDependenciesUsingLinker();

        YangNode node = utilMgr.getYangNodeSet().iterator().next();

        YangNode cont1 = node.getChild();
        YangNode groupUses = cont1.getChild();
        YangNode cont2aug = groupUses.getChild();
        assertThat(cont2aug instanceof YangAugment, is(true));

        YangAugment aug1 = (YangAugment) cont2aug;
        ResolvableStatus status = aug1.getResolvableStatus();
        assertThat(status, is(RESOLVED));
        YangNode target = aug1.getAugmentedNode();

        YangNode cont2 = groupUses.getNextSibling();
        List<YangAugment> augList = ((YangAugmentableNode) cont2)
                .getAugmentedInfoList();

        assertThat(augList.get(0), is(aug1));
        assertThat(cont2, is(target));
    }

    /**
     * Processes multiple uses augment linker.
     *
     * @throws IOException if IO exception occurs
     */
    @Test
    public void processMultiUsesAug() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(path + "multiusesaug/")) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        utilMgr.resolveDependenciesUsingLinker();

        YangNode node = utilMgr.getYangNodeSet().iterator().next();

        YangNode cont1 = node.getChild();
        YangNode groupUses = cont1.getChild();
        YangNode cont6aug = groupUses.getChild();
        assertThat(cont6aug instanceof YangAugment, is(true));

        YangAugment aug1 = (YangAugment) cont6aug;
        ResolvableStatus status = aug1.getResolvableStatus();
        assertThat(status, is(RESOLVED));
        YangNode target = aug1.getAugmentedNode();

        YangNode cont2 = groupUses.getNextSibling();
        YangNode group2Uses = cont2.getChild();
        YangNode cont3aug = group2Uses.getChild();
        assertThat(cont3aug instanceof YangAugment, is(true));

        YangAugment aug2 = (YangAugment) cont3aug;
        ResolvableStatus status2 = aug2.getResolvableStatus();
        assertThat(status2, is(RESOLVED));
        YangNode target2 = aug2.getAugmentedNode();

        YangNode cont3 = group2Uses.getNextSibling();
        List<YangAugment> augList = ((YangAugmentableNode) cont3)
                .getAugmentedInfoList();
        assertThat(augList.get(0), is(aug2));
        assertThat(cont3, is(target2));

        YangNode cont4 = aug2.getChild();
        YangNode cont5 = cont4.getChild();
        YangNode group3uses = cont5.getChild();
        YangNode cont6 = group3uses.getNextSibling();
        YangNode cont6aug2 = group3uses.getChild();

        assertThat(cont6aug2 instanceof YangAugment, is(true));
        YangAugment aug3 = (YangAugment) cont6aug2;
        ResolvableStatus status3 = aug3.getResolvableStatus();
        assertThat(status3, is(RESOLVED));
        YangNode target3 = aug3.getAugmentedNode();

        List<YangAugment> augList1 = ((YangAugmentableNode) cont6)
                .getAugmentedInfoList();
        assertThat(augList1.get(0), is(aug1));
        assertThat(cont6, is(target));

        assertThat(augList1.get(1), is(cont6aug2));
        assertThat(cont6, is(target3));
    }

    /**
     * Processes the inter file uses augment linker.
     *
     * @throws IOException if IO exception occurs
     */
    @Test
    public void processInterFileUsesAug() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(path + "interfileusesaug/")) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        utilMgr.resolveDependenciesUsingLinker();

        YangNode selfNode = null;
        for (YangNode rootNode : utilMgr.getYangNodeSet()) {
            if (rootNode.getName().equals("module1")) {
                selfNode = rootNode;
            }
        }
        YangNode not = selfNode.getChild();
        YangNode groupUses = not.getChild();
        YangNode cont6aug = groupUses.getChild();
        assertThat(cont6aug instanceof YangAugment, is(true));

        YangAugment aug1 = (YangAugment) cont6aug;
        ResolvableStatus status = aug1.getResolvableStatus();
        assertThat(status, is(RESOLVED));
        YangNode target = aug1.getAugmentedNode();

        YangNode cont2 = groupUses.getNextSibling().getNextSibling();
        YangNode group2Uses = cont2.getChild();
        YangNode cont3aug = group2Uses.getChild();
        assertThat(cont3aug instanceof YangAugment, is(true));

        YangAugment aug2 = (YangAugment) cont3aug;
        ResolvableStatus status2 = aug2.getResolvableStatus();
        assertThat(status2, is(RESOLVED));
        YangNode target2 = aug2.getAugmentedNode();

        YangNode intaug = aug2.getNextSibling();
        assertThat(intaug instanceof YangAugment, is(true));

        YangAugment aug3 = (YangAugment) intaug;
        ResolvableStatus status3 = aug3.getResolvableStatus();
        assertThat(status3, is(RESOLVED));
        YangNode target3 = aug3.getAugmentedNode();

        YangNode phyAug = aug3.getNextSibling();
        assertThat(phyAug instanceof YangAugment, is(true));

        YangAugment aug4 = (YangAugment) phyAug;
        ResolvableStatus status4 = aug4.getResolvableStatus();
        assertThat(status4, is(RESOLVED));
        YangNode target4 = aug4.getAugmentedNode();

        YangNode cont3 = group2Uses.getNextSibling();
        List<YangAugment> augList = ((YangAugmentableNode) cont3)
                .getAugmentedInfoList();
        assertThat(augList.get(0), is(aug2));
        assertThat(cont3, is(target2));

        YangNode intChoice = cont3.getNextSibling();
        List<YangAugment> augList2 = ((YangAugmentableNode) intChoice)
                .getAugmentedInfoList();
        assertThat(augList2.get(0), is(aug3));
        assertThat(intChoice, is(target3));

        YangNode phy = intChoice.getChild();
        List<YangAugment> augList3 = ((YangAugmentableNode) phy)
                .getAugmentedInfoList();
        assertThat(augList3.get(0), is(aug4));
        assertThat(phy, is(target4));

        YangNode cont4 = aug2.getChild();
        YangNode cont5 = cont4.getChild();
        YangNode group3uses = cont5.getChild();
        YangNode cont6 = group3uses.getNextSibling();
        YangNode cont6aug2 = group3uses.getChild();

        assertThat(cont6aug2 instanceof YangAugment, is(true));
        YangAugment aug5 = (YangAugment) cont6aug2;
        ResolvableStatus status5 = aug5.getResolvableStatus();
        assertThat(status5, is(RESOLVED));
        YangNode target5 = aug5.getAugmentedNode();

        List<YangAugment> augList5 = ((YangAugmentableNode) cont6)
                .getAugmentedInfoList();
        assertThat(augList5.get(0), is(aug1));
        assertThat(cont6, is(target));

        assertThat(augList5.get(1), is(cont6aug2));
        assertThat(cont6, is(target5));
    }

    /**
     * Processes uses augment with typedef.
     *
     * @throws IOException if IO exception occurs
     */
    @Test
    public void processTypedefInUsesAug() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(path + "typedef/")) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        utilMgr.resolveDependenciesUsingLinker();

        YangNode selfNode = null;
        for (YangNode rootNode : utilMgr.getYangNodeSet()) {
            if (rootNode.getName().equals("subscription")) {
                selfNode = rootNode;
            }
        }

        YangNode rpc = selfNode.getChild();
        YangNode in = rpc.getChild();
        YangNode usesSubPol = in.getChild();
        YangNode usesSubPolNm = usesSubPol.getNextSibling();
        YangNode event = usesSubPolNm.getChild();

        assertThat(event instanceof YangAugment, is(true));
        YangAugment aug1 = (YangAugment) event;
        ResolvableStatus status1 = aug1.getResolvableStatus();
        assertThat(status1, is(RESOLVED));
        YangNode target1 = aug1.getAugmentedNode();

        YangNode targetCh = usesSubPolNm.getNextSibling();
        YangNode eventCase = targetCh.getChild();

        List<YangAugment> augList1 = ((YangAugmentableNode) eventCase)
                .getAugmentedInfoList();
        assertThat(augList1.get(0), is(aug1));
        assertThat(eventCase, is(target1));

        YangNode out = in.getNextSibling();
        YangNode usesSubRes = out.getChild();
        YangNode result = usesSubRes.getChild();

        assertThat(result instanceof YangAugment, is(true));
        YangAugment aug2 = (YangAugment) result;
        ResolvableStatus status2 = aug2.getResolvableStatus();
        assertThat(status2, is(RESOLVED));
        YangNode target2 = aug2.getAugmentedNode();

        YangNode noSuccess = result.getNextSibling();

        assertThat(noSuccess instanceof YangAugment, is(true));
        YangAugment aug3 = (YangAugment) noSuccess;
        ResolvableStatus status3 = aug3.getResolvableStatus();
        assertThat(status3, is(RESOLVED));
        YangNode target3 = aug3.getAugmentedNode();

        YangNode resultCh = usesSubRes.getNextSibling();

        List<YangAugment> augList2 = ((YangAugmentableNode) resultCh)
                .getAugmentedInfoList();
        assertThat(augList2.get(0), is(aug2));
        assertThat(resultCh, is(target2));

        YangNode noSuccessCase = resultCh.getChild();

        List<YangAugment> augList3 = ((YangAugmentableNode) noSuccessCase)
                .getAugmentedInfoList();
        assertThat(augList3.get(0), is(aug3));
        assertThat(noSuccessCase, is(target3));

        YangNode subs = rpc.getNextSibling();
        YangNode usesRec = subs.getChild().getChild().getNextSibling()
                .getNextSibling();
        YangNode reciever = usesRec.getChild();

        assertThat(reciever instanceof YangAugment, is(true));
        YangAugment aug4 = (YangAugment) reciever;
        ResolvableStatus status4 = aug4.getResolvableStatus();
        assertThat(status4, is(RESOLVED));
        YangNode target4 = aug4.getAugmentedNode();

        YangNode recievers = usesRec.getNextSibling().getNextSibling()
                .getNextSibling().getNextSibling();
        YangNode recieverList = recievers.getChild();

        List<YangAugment> augList4 = ((YangAugmentableNode) recieverList)
                .getAugmentedInfoList();
        assertThat(augList4.get(0), is(aug4));
        assertThat(recieverList, is(target4));
    }

    /**
     * Processes simple uses augment translator.
     *
     * @throws IOException            if IO exception occurs
     * @throws ParserException        if parser exception occurs
     * @throws MojoExecutionException if mojo execution exception occurs
     */
    @Test
    public void processSimpleUsesTranslator() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = path + "simpleusesaug/";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        utilMgr.resolveDependenciesUsingLinker();

        YangPluginConfig config = new YangPluginConfig();
        config.setCodeGenDir(DIR);
        utilMgr.translateToJava(config);
        YangPluginConfig.compileCode(COMP);
        deleteDirectory(DIR);
    }

    /**
     * Processes multiple uses augment translator.
     *
     * @throws IOException            if IO exception occurs
     * @throws ParserException        if parser exception occurs
     * @throws MojoExecutionException if mojo execution exception occurs
     */
    @Test
    public void processMultiUsesTranslator() throws IOException,
            ParserException,
            MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = path + "multiusesaug/";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        utilMgr.resolveDependenciesUsingLinker();

        YangPluginConfig config = new YangPluginConfig();
        config.setCodeGenDir(DIR);
        utilMgr.translateToJava(config);
        YangPluginConfig.compileCode(COMP);
        deleteDirectory(DIR);
    }

    /**
     * Processes inter uses augment translator.
     *
     * @throws IOException            if IO exception occurs
     * @throws ParserException        if parser exception occurs
     * @throws MojoExecutionException if mojo execution exception occurs
     */
    @Test
    public void processInterFileUsesTranslator() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = path + "interfileusesaug/";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        utilMgr.resolveDependenciesUsingLinker();

        YangPluginConfig config = new YangPluginConfig();
        config.setCodeGenDir(DIR);
        utilMgr.translateToJava(config);
        YangPluginConfig.compileCode(COMP);
        deleteDirectory(DIR);
    }

    /**
     * Processes typedef uses augment translator.
     *
     * @throws IOException            if IO exception occurs
     * @throws ParserException        if parser exception occurs
     * @throws MojoExecutionException if mojo execution exception occurs
     */
    @Test
    public void processTypedefUsesTranslator() throws IOException,
            ParserException, MojoExecutionException {
        deleteDirectory(DIR);
        String searchDir = path + "typedef/";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        utilMgr.resolveDependenciesUsingLinker();

        YangPluginConfig config = new YangPluginConfig();
        config.setCodeGenDir(DIR);
        utilMgr.translateToJava(config);
        YangPluginConfig.compileCode(COMP);
        deleteDirectory(DIR);
    }
}
