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

package org.onosproject.yangutils.plugin.manager;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yangutils.datamodel.YangAtomicPath;
import org.onosproject.yangutils.datamodel.YangAugment;
import org.onosproject.yangutils.datamodel.YangContainer;
import org.onosproject.yangutils.datamodel.YangLeaf;
import org.onosproject.yangutils.datamodel.YangLeafRef;
import org.onosproject.yangutils.datamodel.YangList;
import org.onosproject.yangutils.datamodel.YangModule;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangPathPredicate;
import org.onosproject.yangutils.linker.exceptions.LinkerException;
import org.onosproject.yangutils.linker.impl.YangLinkerManager;

import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yangutils.linker.impl.YangLinkerUtils.updateFilePriority;
import static org.onosproject.yangutils.utils.io.impl.YangFileScanner.getYangFiles;

/**
 * Test cases for path predicate linking in leaf-ref.
 */
public class PathPredicateLinkingTest {

    private static final String DIR = "src/test/resources/pathpredicate/";

    private final YangUtilManager utilMgr = new YangUtilManager();
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
     * @throws IOException if violates IO operation
     */
    @Test
    public void processSimplePathPredicate() throws IOException {

        utilMgr.createYangFileInfoSet(getYangFiles(DIR + "simple"));
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangNode selfNode;

        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        updateFilePriority(utilMgr.getYangNodeSet());

        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
        Iterator<YangNode> nodeItr = utilMgr.getYangNodeSet().iterator();
        selfNode = nodeItr.next();

        // Gets the list node.
        YangList yangList = (YangList) selfNode.getChild();
        // Gets the container node.
        YangContainer container = (YangContainer) yangList.getNextSibling();

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
     * Processes simple inter file path predicate which gets linked to another
     * file using absolute path.
     *
     * @throws IOException if violates IO operation
     */
    @Test
    public void processSimpleInterFilePathPredicate() throws IOException {

        utilMgr.createYangFileInfoSet(getYangFiles(DIR + "simpleinterfile"));
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangModule selfNode;

        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        updateFilePriority(utilMgr.getYangNodeSet());

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
     * @throws IOException if violates IO operation
     */
    @Test
    public void processInterFilePathPredicateFromAugment() throws IOException {

        utilMgr.createYangFileInfoSet(getYangFiles(DIR + "interfileaugment"));
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangModule selfNode;

        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        updateFilePriority(utilMgr.getYangNodeSet());

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

        YangLeaf test;
        YangLeaf networkId;
        YangLeaf networkRef;

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
     * @throws IOException if violates IO operation
     */
    @Test
    public void processInvalidPathLink() throws IOException {
        thrown.expect(LinkerException.class);
        thrown.expectMessage(
                "YANG file error: There is no leaf/leaf-list in YANG node as " +
                        "mentioned in the path predicate of the leafref path " +
                        "../../interface[ifname = current()/../../ifname]" +
                        "/address/ip");

        utilMgr.createYangFileInfoSet(getYangFiles(DIR + "invalidlinking"));
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();

        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        updateFilePriority(utilMgr.getYangNodeSet());

        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
    }

    /**
     * Processes an invalid scenario where the right axis node doesn't come
     * under YANG list node.
     *
     * @throws IOException if violates IO operation
     */
    @Test
    public void processInvalidPathLinkForList() throws IOException {
        thrown.expect(LinkerException.class);
        thrown.expectMessage(
                "YANG file error: Path predicates are only applicable for " +
                        "YANG list. The leafref path has path predicate for" +
                        " non-list node in the path ../../default-address" +
                        "[ifname = current()/../ifname]/ifname");

        utilMgr.createYangFileInfoSet(getYangFiles(DIR + "invalidlinking2"));
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();

        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        updateFilePriority(utilMgr.getYangNodeSet());

        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
    }

    /**
     * Processes an invalid scenario where the node in path predicate is not
     * present in the traversal.
     *
     * @throws IOException if violates IO operation
     */
    @Test
    public void processInvalidPathLinkForInvalidNode()
            throws IOException {
        thrown.expect(LinkerException.class);
        thrown.expectMessage(
                "YANG file error: The path predicate of the leafref has an " +
                        "invalid path in ../../interface[name = current()/" +
                        "../../address/ifname]/address/ip");

        utilMgr.createYangFileInfoSet(getYangFiles(DIR + "invalidlinking3"));
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();

        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        updateFilePriority(utilMgr.getYangNodeSet());

        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
    }
}
