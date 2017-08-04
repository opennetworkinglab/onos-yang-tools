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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.linker.impl.YangLinkerManager;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.tool.YangCompilerManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.updateFilePriority;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;

/**
 * Test cases for testing the unique statement validation after linking.
 */
public class UniqueLinkingTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();


    private final YangCompilerManager utilMgr = new YangCompilerManager();
    private final YangLinkerManager linkerMgr = new YangLinkerManager();

    /**
     * Checks grouping linking of unique.
     */
    @Test
    public void processGroupingLinkingForUnique()
            throws IOException, ParserException, MojoExecutionException {

        String searchDir = "src/test/resources/groupingforunique";

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        Set<YangNode> yangSet = utilMgr.getYangNodeSet();

        YangNode refNode = null;
        YangNode selfNode = null;

        linkerMgr.createYangNodeSet(yangSet);
        linkerMgr.addRefToYangFilesImportList(yangSet);
        updateFilePriority(yangSet);
        linkerMgr.processInterFileLinking(yangSet);
        linkerMgr.processUniqueLinking(yangSet);

        Iterator<YangNode> yangNodeIterator = utilMgr.getYangNodeSet().iterator();
        YangNode rootNode = yangNodeIterator.next();

        List<YangLeaf> leaves;
        Iterator<YangLeaf> it;
        YangNode list = rootNode.getChild();
        assertThat(list instanceof YangList, is(true));
        leaves = ((YangList) list).getUniqueLeaves();
        assertThat(leaves.size(), is(1));
        it = leaves.iterator();
        YangLeaf groupingLeaf = it.next();
        assertThat(groupingLeaf.getName(), is("groupingleaf"));

        Iterator<List<YangAtomicPath>> pathListIt;
        Iterator<YangAtomicPath> pathIt;
        List<YangAtomicPath> path;
        YangAtomicPath atPath;

        pathListIt = ((YangList) list).getPathList().iterator();
        path = pathListIt.next();
        assertThat(path.size(), is(1));
        pathIt = path.iterator();
        atPath = pathIt.next();
        assertThat(atPath.getNodeIdentifier().getName(), is("groupingleaf"));
    }
}
