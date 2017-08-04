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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangContainer;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangPathPredicate;
import org.onosproject.yang.compiler.linker.exceptions.LinkerException;
import org.onosproject.yang.compiler.linker.impl.YangLinkerManager;
import org.onosproject.yang.compiler.linker.impl.YangLinkerUtils;
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
 * Test cases for path predicate linking in leaf-ref.
 */
public class PathPredicateLinkingTest {

    private final YangCompilerManager utilMgr =
            new YangCompilerManager();
    private final YangLinkerManager linkerMgr = new YangLinkerManager();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ListIterator<YangLeaf> leafItr;
    private YangLeaf ifName;
    private YangLeaf address;
    private YangLeaf name;
    private Iterator<YangAtomicPath> pathItr;
    private YangAtomicPath atomicPath;
    private Iterator<YangPathPredicate> predicateItr;
    private YangPathPredicate predicate;

    /**
     * Processes simple path predicate which gets linked within the same file
     * using relative path.
     *
     * @throws IOException IO file error
     */
    @Test
    public void processSimplePathPredicate() throws IOException {

        String searchDir = "src/test/resources/pathpredicate/simple";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangNode selfNode;

        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilMgr.getYangNodeSet());

        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
        Iterator<YangNode> nodeItr = utilMgr.getYangNodeSet().iterator();
        selfNode = nodeItr.next();

        // Gets the list node.
        YangList yangList = (YangList) selfNode.getChild();
        // Gets the container node.
        YangContainer container = (YangContainer) yangList.getNextSibling();

        ListIterator<YangLeaf> leafIterator;
        YangLeaf ifName;
        YangLeaf address;
        YangLeaf name;
        Iterator<YangAtomicPath> pathItr;
        YangAtomicPath atomicPath;
        Iterator<YangPathPredicate> predicateItr;
        YangPathPredicate predicate;

        leafIterator = container.getListOfLeaf().listIterator();
        ifName = leafIterator.next();
        address = leafIterator.next();

        // Gets the address leaf's leaf-ref type.
        YangLeafRef<?> leafRef2 = (YangLeafRef) address.getDataType()
                .getDataTypeExtendedInfo();
        pathItr = leafRef2.getAtomicPath().listIterator();
        atomicPath = pathItr.next();

        // Gets the path-predicate.
        predicateItr = atomicPath.getPathPredicatesList().listIterator();
        predicate = predicateItr.next();

        // Gets the left and right axis node in path-predicate.
        YangLeaf yangLeftLeaf = (YangLeaf) predicate.getLeftAxisNode();
        YangLeaf yangRightLeaf = (YangLeaf) predicate.getRightAxisNode();

        leafIterator = yangList.getListOfLeaf().listIterator();
        name = leafIterator.next();

        // Checks that right and left path-predicates are correct.
        assertThat(yangLeftLeaf, is(name));
        assertThat(yangRightLeaf, is(ifName));
    }

    /**
     * Processes simple inter file path predicate which gets linked to another
     * file using absolute path.
     *
     * @throws IOException IO file error
     */
    @Test
    public void processSimpleInterFilePathPredicate() throws IOException {

        String searchDir = "src/test/resources/pathpredicate/simpleinterfile";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangModule selfNode;

        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilMgr.getYangNodeSet());

        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
        Iterator<YangNode> nodeItr = utilMgr.getYangNodeSet().iterator();

        YangNode rootNode = nodeItr.next();
        YangModule refNode;
        if (rootNode.getName().equals("ietf-network")) {
            selfNode = (YangModule) rootNode;
            refNode = (YangModule) nodeItr.next();
        } else {
            refNode = (YangModule) rootNode;
            selfNode = (YangModule) nodeItr.next();
        }

        // Gets the container node.
        YangContainer container = (YangContainer) selfNode.getChild();
        // Gets the list node.
        YangList yangList = (YangList) refNode.getChild();

        ListIterator<YangLeaf> leafItr;
        YangLeaf ifName;
        YangLeaf address;
        YangLeaf name;
        Iterator<YangAtomicPath> pathItr;
        YangAtomicPath atomicPath;
        Iterator<YangPathPredicate> predicateItr;
        YangPathPredicate predicate;

        leafItr = container.getListOfLeaf().listIterator();
        ifName = leafItr.next();
        address = leafItr.next();

        // Gets the address leaf's leaf-ref type.
        YangLeafRef<?> leafRef2 = (YangLeafRef) address.getDataType()
                .getDataTypeExtendedInfo();
        pathItr = leafRef2.getAtomicPath().listIterator();
        atomicPath = pathItr.next();

        // Gets the path-predicate.
        predicateItr = atomicPath.getPathPredicatesList().listIterator();
        predicate = predicateItr.next();

        // Gets the left and right axis node in path-predicate.
        YangLeaf yangLeftLeaf = (YangLeaf) predicate.getLeftAxisNode();
        YangLeaf yangRightLeaf = (YangLeaf) predicate.getRightAxisNode();

        leafItr = yangList.getListOfLeaf().listIterator();
        name = leafItr.next();

        // Checks that right and left path-predicates are correct.
        assertThat(yangLeftLeaf, is(name));
        assertThat(yangRightLeaf, is(ifName));
    }

    /**
     * Processes inter file path predicate, where leaf-ref is present under
     * YANG augment.
     *
     * @throws IOException IO file error
     */
    @Test
    public void processInterFilePathPredicateFromAugment() throws IOException {

        String searchDir = "src/test/resources/pathpredicate/interfileaugment";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangModule selfNode;

        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilMgr.getYangNodeSet());

        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
        Iterator<YangNode> nodeItr = utilMgr.getYangNodeSet().iterator();

        YangNode rootNode = nodeItr.next();
        YangModule refNode;
        if (rootNode.getName().equals("ietf-network")) {
            selfNode = (YangModule) rootNode;
            refNode = (YangModule) nodeItr.next();
        } else {
            refNode = (YangModule) rootNode;
            selfNode = (YangModule) nodeItr.next();
        }

        // Gets the augment node.
        YangList list = (YangList) selfNode.getChild().getChild();

        // Gets the augment node.
        YangAugment augment = (YangAugment) refNode.getChild();

        ListIterator<YangLeaf> leafItr;
        YangLeaf test;
        YangLeaf networkId;
        YangLeaf networkRef;
        Iterator<YangAtomicPath> pathItr;
        YangAtomicPath atomicPath;
        Iterator<YangPathPredicate> predicateItr;
        YangPathPredicate predicate;

        leafItr = augment.getListOfLeaf().listIterator();
        test = leafItr.next();

        YangLeafRef<?> leafRef =
                (YangLeafRef) test.getDataType().getDataTypeExtendedInfo();
        pathItr = leafRef.getAtomicPath().listIterator();
        pathItr.next();
        atomicPath = pathItr.next();

        // Gets the path-predicate.
        predicateItr = atomicPath.getPathPredicatesList().listIterator();
        predicate = predicateItr.next();

        // Gets the left and right axis node in path-predicate.
        YangLeaf yangLeftLeaf = (YangLeaf) predicate.getLeftAxisNode();
        YangLeaf yangRightLeaf = (YangLeaf) predicate.getRightAxisNode();

        leafItr = list.getListOfLeaf().listIterator();
        networkId = leafItr.next();
        YangContainer reference = (YangContainer) list.getChild();
        leafItr = reference.getListOfLeaf().listIterator();
        networkRef = leafItr.next();

        // Checks that right and left path-predicates are correct.
        assertThat(yangLeftLeaf, is(networkId));
        assertThat(yangRightLeaf, is(networkRef));
    }

    /**
     * Processes an invalid scenario where the target leaf/leaf-list in
     * path-predicate is not found.
     *
     * @throws IOException IO file error
     */
    @Test
    public void processInvalidPathLink() throws IOException {
        thrown.expect(LinkerException.class);
        thrown.expectMessage(
                "YANG file error: There is no leaf/leaf-list in YANG node as " +
                        "mentioned in the path predicate of the leafref path " +
                        "../../interface[ifname = current()/../../ifname]" +
                        "/address/ip");

        String searchDir = "src/test/resources/pathpredicate/invalidlinking";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();

        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilMgr.getYangNodeSet());

        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
    }

    /**
     * Processes an invalid scenario where the right axis node doesn't come
     * under YANG list node.
     *
     * @throws IOException IO file error
     */
    @Test
    public void processInvalidPathLinkForList() throws IOException {
        thrown.expect(LinkerException.class);
        thrown.expectMessage(
                "YANG file error: Path predicates are only applicable for " +
                        "YANG list. The leafref path has path predicate for" +
                        " non-list node in the path ../../default-address" +
                        "[ifname = current()/../ifname]/ifname");

        String searchDir = "src/test/resources/pathpredicate/invalidlinking2";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangNode selfNode;

        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilMgr.getYangNodeSet());

        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
    }

    /**
     * Processes an invalid scenario where the node in path predicate is not
     * present in the traversal.
     *
     * @throws IOException IO file error
     */
    @Test
    public void processInvalidPathLinkForInvalidNode()
            throws IOException {
        thrown.expect(LinkerException.class);
        thrown.expectMessage(
                "YANG file error: The path predicate of the leafref has an " +
                        "invalid path in ../../interface[name = current()/" +
                        "../../address/ifname]/address/ip");

        String searchDir = "src/test/resources/pathpredicate/invalidlinking3";
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(searchDir)) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();

        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilMgr.getYangNodeSet());

        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
    }
}
