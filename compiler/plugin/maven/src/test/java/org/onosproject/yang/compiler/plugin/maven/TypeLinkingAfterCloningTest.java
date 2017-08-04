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
import org.onosproject.yang.compiler.datamodel.YangContainer;
import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangIdentityRef;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangTypeDef;
import org.onosproject.yang.compiler.datamodel.YangUnion;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.linker.impl.YangLinkerManager;
import org.onosproject.yang.compiler.linker.impl.YangLinkerUtils;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
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
import static org.onosproject.yang.compiler.datamodel.YangNodeType.MODULE_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DERIVED;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.IDENTITYREF;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.STRING;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;

/**
 * Test cases for type linking after cloning happens grouping.
 */
public class TypeLinkingAfterCloningTest {

    private static final String MODULE = "module";
    private static final String OPEN_ROAD = "org-open-road-m-device";
    private static final String NODE_ID = "node-id";
    private static final String LEAF = "leaf";
    private static final String LEAF_LIST = "leaf-list";
    private static final String NODE_REF = "node-ref";
    private static final String FACILITY = "facility";
    private static final String FACILITY_SYS_LOG = "syslog-facility";
    private static final String USABILITY_SYS_LOG = "syslog-usability";
    private static final String AVAILABILITY_SYS_LOG = "syslog-availability";
    private static final String THIRD = "third";
    private static final String SECOND = "second";
    private static final String FIRST = "first";
    private static final String TYPEDEF = "typedef";
    private static final String CORRECT = "correct";
    private static final String UNI = "with-uni";
    private static final String UNION = "union";
    private static final String BASE1 = "id2";
    private static final String BASE2 = "id1";
    private static final String DIR =
            "src/test/resources/typelinkingaftercloning/";
    private final YangCompilerManager utilMgr =
            new YangCompilerManager();
    private final YangLinkerManager linkerMgr = new YangLinkerManager();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ListIterator<YangLeaf> leafItr;
    private YangLeaf leafInfo;
    private ListIterator<YangLeafList> leafListItr;
    private YangLeafList leafListInfo;
    private YangIdentityRef idRef;
    private YangUnion union;
    private Iterator<YangType<?>> unionTypeItr;
    private YangType type;
    private YangDerivedInfo derInfo;
    private YangType type2;
    private YangType type3;
    private YangType type1;
    private YangDerivedInfo derInfo1;
    private YangTypeDef typedef1;

    /**
     * Returns the error message as the node name incorrect, when assert fails.
     *
     * @param node     YANG node
     * @param nodeName node name
     * @return error message as the name is incorrect
     */
    private static String getInCrtName(String node, Object nodeName) {
        return getCapitalCase(node) + "'s name " + nodeName + " is incorrect.";
    }

    /**
     * Returns the capital cased first letter of the given string.
     *
     * @param name string to be capital cased
     * @return capital cased string
     */
    private static String getCapitalCase(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * Returns the error message as the node type incorrect, when assert fails.
     *
     * @param node     YANG node
     * @param nodeName node name
     * @return error message as the type is incorrect
     */
    private static String getInCrtLeafType(String node, String nodeName) {
        return "The " + node + " " + nodeName + " has incorrect data type.";
    }

    /**
     * Returns the error message, stating the union and identity-ref level in
     * the type, has not resolved to the referred identity.
     *
     * @param unionLvl union level in node
     * @param idLvl    identity-ref level in node
     * @param baseName referred base
     * @param node     YANG node having type
     * @param nodeName node name
     * @return error message for incorrect identity-ref in union.
     */
    public static String getInCrtUnionWithIdRef(
            String unionLvl, String idLvl, String baseName, String node,
            String nodeName) {
        return "The " + idLvl + " direct occurrence identity-ref in " +
                unionLvl + " level union, of " + node + " " + nodeName +
                " is not " + baseName;
    }

    /**
     * Processes leaf-ref after its cloned to uses from grouping.
     *
     * @throws IOException if violates IO operation
     */
    @Test
    public void processLeafRefAfterCloning() throws IOException {

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(DIR + "leafref/intrafile")) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangNode selfNode;

        // Create YANG node set
        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());

        // Add references to import list.
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilMgr.getYangNodeSet());

        // Carry out inter-file linking.
        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());

        Iterator<YangNode> nodeItr = utilMgr.getYangNodeSet().iterator();

        selfNode = nodeItr.next();

        // Checks whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Checks whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        assertThat(getInCrtName(MODULE, OPEN_ROAD), selfNode.getName(),
                   is(OPEN_ROAD));

        YangList list = (YangList) selfNode.getChild().getNextSibling()
                .getNextSibling();

        YangLeafRef leafRef;

        leafItr = list.getListOfLeaf().listIterator();
        leafInfo = leafItr.next();

        // Checks whether the information in the leaf is correct under list.
        assertThat(getInCrtName(LEAF, NODE_ID), leafInfo.getName(),
                   is(NODE_ID));
        leafRef = (YangLeafRef) leafInfo.getDataType()
                .getDataTypeExtendedInfo();

        // Checks the effective type for the leaf.
        assertThat(getInCrtLeafType(LEAF, NODE_ID),
                   leafRef.getEffectiveDataType().getDataType(), is(STRING));

        leafListItr = list.getListOfLeafList().listIterator();
        leafListInfo = leafListItr.next();

        // Checks whether the information in the leaf-list is correct.
        assertThat(getInCrtName(LEAF_LIST, NODE_REF), leafListInfo.getName(),
                   is(NODE_REF));
        leafRef = (YangLeafRef) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();

        assertThat(getInCrtLeafType(LEAF_LIST, NODE_REF),
                   leafRef.getEffectiveDataType().getDataType(), is(DERIVED));

        // Checks whether the information under cloned container is correct.
        YangContainer container = (YangContainer) list.getChild()
                .getNextSibling();

        leafItr = container.getListOfLeaf().listIterator();
        leafInfo = leafItr.next();

        // Checks whether the information in the leaf is correct under cont.
        assertThat(getInCrtName(LEAF, NODE_ID), leafInfo.getName(),
                   is(NODE_ID));
        leafRef = (YangLeafRef) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();
        assertThat(getInCrtLeafType(LEAF, NODE_ID),
                   leafRef.getEffectiveDataType().getDataType(), is(DERIVED));

        leafListItr = container.getListOfLeafList().listIterator();
        leafListInfo = leafListItr.next();

        // Checks whether the information in the leaf-list is correct.
        assertThat(getInCrtName(LEAF_LIST, NODE_REF), leafListInfo.getName(),
                   is(NODE_REF));
        leafRef = (YangLeafRef) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();
        assertThat(getInCrtLeafType(LEAF_LIST, NODE_REF),
                   leafRef.getEffectiveDataType().getDataType(),
                   is(STRING));
    }

    /**
     * Processes invalid scenario where a leaf-ref is present in union.
     *
     * @throws IOException io error when finding file
     */
    @Test
    public void processInvalidLeafRef() throws IOException {

        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(DIR + "leafref/invalid")) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        thrown.expect(ParserException.class);
        thrown.expectMessage("Union member type must not be one of the " +
                                     "built-in types \"empty\" or " +
                                     "\"leafref\"node-id_union");
        utilMgr.parseYangFileInfoSet();
    }

    /**
     * Processes simple identity-ref after it gets cloned from grouping.
     *
     * @throws IOException if violates IO operation
     */
    @Test
    public void processIdentityRefBeforeCloning() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(DIR + "identityref")) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangNode selfNode;

        // Create YANG node set
        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());

        // Add references to import list.
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        YangLinkerUtils.updateFilePriority(utilMgr.getYangNodeSet());

        // Carry out inter-file linking.
        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());

        Iterator<YangNode> nodeItr = utilMgr.getYangNodeSet().iterator();

        selfNode = nodeItr.next();

        // Checks whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Checks whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        assertThat(getInCrtName(MODULE, OPEN_ROAD), selfNode.getName(),
                   is(OPEN_ROAD));

        YangList list = (YangList) selfNode.getChild().getNextSibling()
                .getNextSibling().getNextSibling();

        leafItr = list.getListOfLeaf().listIterator();
        leafInfo = leafItr.next();

        // Checks if the leaf has identity-ref in union.
        assertThat(getInCrtName(LEAF, FACILITY), leafInfo.getName(),
                   is(FACILITY));
        union = (YangUnion) leafInfo.getDataType().getDataTypeExtendedInfo();
        unionTypeItr = union.getTypeList().listIterator();
        type = unionTypeItr.next();
        idRef = (YangIdentityRef) type.getDataTypeExtendedInfo();

        // Checks the effective type for the leaf.
        assertThat(getInCrtLeafType(LEAF, FACILITY),
                   idRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        leafInfo = leafItr.next();

        // Checks whether the information in the leaf is correct under list.
        assertThat(getInCrtName(LEAF, NODE_ID), leafInfo.getName(),
                   is(NODE_ID));
        idRef = (YangIdentityRef) leafInfo.getDataType()
                .getDataTypeExtendedInfo();

        // Checks the effective type for the leaf.
        assertThat(getInCrtLeafType(LEAF, NODE_ID),
                   idRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        leafListItr = list.getListOfLeafList().listIterator();
        leafListInfo = leafListItr.next();

        // Checks if the information in the leaf-list is correct under list.
        assertThat(getInCrtName(LEAF_LIST, NODE_REF), leafListInfo.getName(),
                   is(NODE_REF));

        derInfo = (YangDerivedInfo) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();

        type = derInfo.getReferredTypeDef().getTypeList().get(0);
        idRef = (YangIdentityRef) type.getDataTypeExtendedInfo();

        // Checks the effective type for the leaf-list.
        assertThat(getInCrtLeafType(LEAF, NODE_REF),
                   idRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        YangContainer container = (YangContainer) list.getChild()
                .getNextSibling().getNextSibling();

        leafListItr = container.getListOfLeafList().listIterator();
        leafListInfo = leafListItr.next();

        // Checks the leaf-list information is correct.
        assertThat(getInCrtName(LEAF_LIST, FACILITY), leafListInfo.getName(),
                   is(FACILITY));
        union = (YangUnion) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();
        unionTypeItr = union.getTypeList().listIterator();
        type = unionTypeItr.next();
        idRef = (YangIdentityRef) type.getDataTypeExtendedInfo();

        // Checks the effective type for the leaf-list.
        assertThat(getInCrtLeafType(LEAF_LIST, FACILITY),
                   idRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        leafListInfo = leafListItr.next();

        // Checks the leaf-list information is correct.
        assertThat(getInCrtName(LEAF_LIST, NODE_REF), leafListInfo.getName(),
                   is(NODE_REF));
        idRef = (YangIdentityRef) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();

        // Checks the effective type for the leaf.
        assertThat(getInCrtLeafType(LEAF_LIST, NODE_REF),
                   idRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        leafItr = container.getListOfLeaf().listIterator();
        leafInfo = leafItr.next();

        // Checks the leaf information is correct.
        assertThat(getInCrtName(LEAF, NODE_ID), leafInfo.getName(),
                   is(NODE_ID));
        idRef = (YangIdentityRef) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();

        assertThat(getInCrtLeafType(LEAF, NODE_ID),
                   idRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));
    }

    /**
     * Processes union having different recursive level with identity-ref.
     *
     * @throws IOException if violates IO operation
     */
    @Test
    public void processUnionAfterCloning() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(DIR + "union")) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangNode selfNode;

        // Create YANG node set
        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());

        // Add references to import list.
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());
        YangLinkerUtils.updateFilePriority(utilMgr.getYangNodeSet());

        // Carry out inter-file linking.
        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
        Iterator<YangNode> nodeItr = utilMgr.getYangNodeSet().iterator();
        selfNode = nodeItr.next();

        YangUnion union2;
        YangUnion union3;
        Iterator<YangType<?>> unionTypeItr2;
        Iterator<YangType<?>> unionTypeItr3;
        YangDerivedInfo derivedInfo;
        YangTypeDef typeDef;
        Iterator<YangType<?>> typeDefItr;

        // Checks whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Checks whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        assertThat(getInCrtName(MODULE, OPEN_ROAD), selfNode.getName(),
                   is(OPEN_ROAD));

        YangList list = (YangList) selfNode.getChild().getNextSibling()
                .getNextSibling().getNextSibling().getNextSibling()
                .getNextSibling().getNextSibling();

        validateList1(list);
        validateList2(list);
    }

    private void validateList1(YangList list) {
        YangUnion union2;
        Iterator<YangType<?>> unionTypeItr2;
        YangUnion union3;
        Iterator<YangType<?>> unionTypeItr3;
        YangDerivedInfo derivedInfo;
        YangTypeDef typeDef;
        Iterator<YangType<?>> typeDefItr;
        Iterator<YangLeaf> leafItr = list.getListOfLeaf().listIterator();
        YangLeaf leafInfo = leafItr.next();

        // Checks if the leaf has identity-ref in union.
        assertThat(getInCrtName(LEAF, FACILITY), leafInfo.getName(),
                   is(FACILITY));

        // Gets the first level union and the list of type in it.
        union = (YangUnion) leafInfo.getDataType().getDataTypeExtendedInfo();
        unionTypeItr = union.getTypeList().listIterator();
        type = unionTypeItr.next();

        // Gets the second level union and types in it.
        union2 = (YangUnion) type.getDataTypeExtendedInfo();
        unionTypeItr2 = union2.getTypeList().listIterator();
        type2 = unionTypeItr2.next();

        // Gets the third level union and types in it.
        union3 = (YangUnion) type2.getDataTypeExtendedInfo();
        unionTypeItr3 = union3.getTypeList().listIterator();
        type3 = unionTypeItr3.next();

        // Checks the first identity-ref in third level union.
        idRef = (YangIdentityRef) type3.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                THIRD, FIRST, USABILITY_SYS_LOG, LEAF, FACILITY),
                   idRef.getBaseIdentity().getName(),
                   is(USABILITY_SYS_LOG));

        // Checks the first identity-ref in second level union.
        type2 = unionTypeItr2.next();
        idRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                SECOND, FIRST, FACILITY_SYS_LOG, LEAF, FACILITY),
                   idRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        // Checks the first identity-ref in first level union.
        type = unionTypeItr.next();
        derInfo = (YangDerivedInfo) type.getDataTypeExtendedInfo();
        type = derInfo.getReferredTypeDef().getTypeList().get(0);
        idRef = (YangIdentityRef) type.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                FIRST, FIRST, AVAILABILITY_SYS_LOG, LEAF, FACILITY),
                   idRef.getBaseIdentity().getName(),
                   is(AVAILABILITY_SYS_LOG));

        // Checks derived type in third level union.
        type3 = unionTypeItr3.next();
        derivedInfo = (YangDerivedInfo) type3.getDataTypeExtendedInfo();
        typeDef = derivedInfo.getReferredTypeDef();
        typeDefItr = typeDef.getTypeList().listIterator();
        type = typeDefItr.next();

        // Gets the first level union and the list of type in it.
        union = (YangUnion) type.getDataTypeExtendedInfo();
        unionTypeItr = union.getTypeList().listIterator();
        type = unionTypeItr.next();

        // Gets the first level union and the list of type in it.
        union2 = (YangUnion) type.getDataTypeExtendedInfo();
        unionTypeItr2 = union2.getTypeList().listIterator();
        type2 = unionTypeItr2.next();

        // Checks the first identity-ref in second level union.
        idRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                SECOND, FIRST, AVAILABILITY_SYS_LOG, TYPEDEF, CORRECT),
                   idRef.getBaseIdentity().getName(),
                   is(AVAILABILITY_SYS_LOG));

        // Checks the second identity-ref in second level union.
        type2 = unionTypeItr2.next();
        derInfo = (YangDerivedInfo) type2.getDataTypeExtendedInfo();
        type2 = derInfo.getReferredTypeDef().getTypeList().get(0);
        idRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                SECOND, SECOND, AVAILABILITY_SYS_LOG, TYPEDEF, CORRECT),
                   idRef.getBaseIdentity().getName(),
                   is(AVAILABILITY_SYS_LOG));

        // Checks the first identity-ref in first level union.
        type = unionTypeItr.next();
        idRef = (YangIdentityRef) type.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                FIRST, FIRST, USABILITY_SYS_LOG, TYPEDEF, CORRECT),
                   idRef.getBaseIdentity().getName(),
                   is(USABILITY_SYS_LOG));
    }

    private void validateList2(YangList list) {
        YangUnion union2;
        Iterator<YangType<?>> unionTypeItr2;
        YangUnion union3;
        Iterator<YangType<?>> unionTypeItr3;
        YangDerivedInfo derivedInfo;
        YangTypeDef typeDef;
        Iterator<YangType<?>> typeDefItr;
        YangContainer container = (YangContainer) list.getChild().getNextSibling().getNextSibling();

        Iterator<YangLeafList> leafListItr = container.getListOfLeafList()
                .listIterator();
        YangLeafList leafListInfo = leafListItr.next();

        // Checks if the leaf-list has identity-ref in union.
        assertThat(getInCrtName(LEAF_LIST, FACILITY), leafListInfo.getName(),
                   is(FACILITY));

        // Gets the first level union and the list of type in it.
        union = (YangUnion) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();
        unionTypeItr = union.getTypeList().listIterator();
        type = unionTypeItr.next();

        // Gets the second level union and types in it.
        union2 = (YangUnion) type.getDataTypeExtendedInfo();
        unionTypeItr2 = union2.getTypeList().listIterator();
        type2 = unionTypeItr2.next();

        // Gets the third level union and types in it.
        union3 = (YangUnion) type2.getDataTypeExtendedInfo();
        unionTypeItr3 = union3.getTypeList().listIterator();
        type3 = unionTypeItr3.next();

        // Checks the first identity-ref in third level union.
        idRef = (YangIdentityRef) type3.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                THIRD, FIRST, USABILITY_SYS_LOG, LEAF_LIST, FACILITY),
                   idRef.getBaseIdentity().getName(),
                   is(USABILITY_SYS_LOG));

        // Checks the first identity-ref in second level union.
        type2 = unionTypeItr2.next();
        idRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                SECOND, FIRST, FACILITY_SYS_LOG, LEAF_LIST, FACILITY),
                   idRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        // Checks the first identity-ref in first level union.
        type = unionTypeItr.next();
        derInfo = (YangDerivedInfo) type.getDataTypeExtendedInfo();
        type = derInfo.getReferredTypeDef().getTypeList().get(0);
        idRef = (YangIdentityRef) type.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                FIRST, FIRST, AVAILABILITY_SYS_LOG, LEAF_LIST, FACILITY),
                   idRef.getBaseIdentity().getName(),
                   is(AVAILABILITY_SYS_LOG));

        // Checks derived type in third level union.
        type3 = unionTypeItr3.next();
        derivedInfo = (YangDerivedInfo) type3.getDataTypeExtendedInfo();
        typeDef = derivedInfo.getReferredTypeDef();
        typeDefItr = typeDef.getTypeList().listIterator();
        type = typeDefItr.next();

        // Gets the first level union and the list of type in it.
        union = (YangUnion) type.getDataTypeExtendedInfo();
        unionTypeItr = union.getTypeList().listIterator();
        type = unionTypeItr.next();

        // Gets the first level union and the list of type in it.
        union2 = (YangUnion) type.getDataTypeExtendedInfo();
        unionTypeItr2 = union2.getTypeList().listIterator();
        type2 = unionTypeItr2.next();

        // Checks the first identity-ref in second level union.
        idRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                SECOND, FIRST, AVAILABILITY_SYS_LOG, TYPEDEF, CORRECT),
                   idRef.getBaseIdentity().getName(),
                   is(AVAILABILITY_SYS_LOG));

        // Checks the second identity-ref in second level union.
        type2 = unionTypeItr2.next();
        derInfo = (YangDerivedInfo) type2.getDataTypeExtendedInfo();
        type2 = derInfo.getReferredTypeDef().getTypeList().get(0);
        idRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                SECOND, SECOND, AVAILABILITY_SYS_LOG, TYPEDEF, CORRECT),
                   idRef.getBaseIdentity().getName(),
                   is(AVAILABILITY_SYS_LOG));

        // Checks the first identity-ref in first level union.
        type = unionTypeItr.next();
        idRef = (YangIdentityRef) type.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                FIRST, FIRST, USABILITY_SYS_LOG, TYPEDEF, CORRECT),
                   idRef.getBaseIdentity().getName(),
                   is(USABILITY_SYS_LOG));
    }

    /**
     * Processes identity-ref when present under typedef, during intra and
     * inter file linking.
     *
     * @throws IOException if violates IO operation
     */
    @Test
    public void processIdentityRefWithTypeDef() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(DIR + "idreftypedef")) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangNode selfNode;

        // Create YANG node set
        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());

        // Add references to import list.
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());
        YangLinkerUtils.updateFilePriority(utilMgr.getYangNodeSet());

        // Carry out inter-file linking.
        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
        Iterator<YangNode> nodeItr = utilMgr.getYangNodeSet().iterator();
        YangNode rootNode = nodeItr.next();

        if (rootNode.getName().equals("IdRefInTypeDef1")) {
            selfNode = rootNode;
        } else {
            selfNode = nodeItr.next();
        }

        YangDerivedInfo derInfo2;
        YangTypeDef typedef2;
        YangDerivedInfo derInfo3;
        YangTypeDef typedef3;

        YangModule module = (YangModule) selfNode;
        leafItr = module.getListOfLeaf().listIterator();

        // Gets the first leaf, which has three typedef with effective id-ref.
        leafInfo = leafItr.next();
        assertThat(getInCrtName(LEAF, LEAF), leafInfo.getName(), is(LEAF));
        assertThat(getInCrtLeafType(LEAF, LEAF),
                   leafInfo.getDataType().getDataType(), is(DERIVED));

        // Traverses through the three typedef in it.
        derInfo1 = (YangDerivedInfo) leafInfo.getDataType()
                .getDataTypeExtendedInfo();
        typedef1 = derInfo1.getReferredTypeDef();
        type1 = typedef1.getTypeList().get(0);
        derInfo2 = (YangDerivedInfo) type1.getDataTypeExtendedInfo();
        typedef2 = derInfo2.getReferredTypeDef();
        type2 = typedef2.getTypeList().get(0);
        derInfo3 = (YangDerivedInfo) type2.getDataTypeExtendedInfo();
        typedef3 = derInfo3.getReferredTypeDef();
        type3 = typedef3.getTypeList().get(0);
        idRef = (YangIdentityRef) type3.getDataTypeExtendedInfo();

        assertThat(getInCrtLeafType(TYPEDEF, typedef1.getName()),
                   derInfo1.getEffectiveBuiltInType(), is(IDENTITYREF));
        assertThat(getInCrtLeafType(TYPEDEF, typedef3.getName()),
                   idRef.getBaseIdentity().getName(), is(BASE1));

        leafListItr = module.getListOfLeafList().listIterator();

        // Gets the first leaf, which has two typedef with effective id-ref.
        leafListInfo = leafListItr.next();
        assertThat(getInCrtName(LEAF_LIST, LEAF_LIST), leafListInfo.getName(),
                   is(LEAF_LIST));
        assertThat(getInCrtLeafType(LEAF_LIST, LEAF_LIST),
                   leafListInfo.getDataType().getDataType(), is(DERIVED));

        // Traverses through the two typedef in it.
        derInfo1 = (YangDerivedInfo) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();
        typedef1 = derInfo1.getReferredTypeDef();
        type1 = typedef1.getTypeList().get(0);
        derInfo2 = (YangDerivedInfo) type1.getDataTypeExtendedInfo();
        typedef2 = derInfo2.getReferredTypeDef();
        type2 = typedef2.getTypeList().get(0);
        idRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();

        assertThat(getInCrtLeafType(TYPEDEF, typedef1.getName()),
                   derInfo1.getEffectiveBuiltInType(), is(IDENTITYREF));
        assertThat(getInCrtLeafType(TYPEDEF, typedef3.getName()),
                   idRef.getBaseIdentity().getName(), is(BASE1));

        // Gets the leaf with union having typedef referred from other file.
        leafInfo = leafItr.next();
        assertThat(getInCrtName(LEAF, UNI), leafInfo.getName(), is(UNI));
        assertThat(getInCrtLeafType(LEAF, UNI),
                   leafInfo.getDataType().getDataType(),
                   is(YangDataTypes.UNION));

        union = (YangUnion) leafInfo.getDataType().getDataTypeExtendedInfo();
        type1 = union.getTypeList().get(0);
        idRef = (YangIdentityRef) type1.getDataTypeExtendedInfo();

        assertThat(getInCrtLeafType(UNION, "first type"),
                   idRef.getBaseIdentity().getName(), is(BASE1));

        type1 = union.getTypeList().get(1);
        derInfo1 = (YangDerivedInfo) type1.getDataTypeExtendedInfo();
        typedef1 = derInfo1.getReferredTypeDef();
        type2 = typedef1.getTypeList().get(0);
        idRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtLeafType(UNION, "second type"),
                   idRef.getBaseIdentity().getName(), is("id3"));
    }

    /**
     * Processes identity-ref when present in grouping used by inter file uses.
     *
     * @throws IOException if violates IO operation
     */
    @Test
    public void processIdentityRefInGrouping() throws IOException {
        Set<Path> paths = new HashSet<>();
        for (String file : getYangFiles(DIR + "idrefingrouping")) {
            paths.add(Paths.get(file));
        }

        utilMgr.createYangFileInfoSet(paths);
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangNode selfNode;

        // Create YANG node set
        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());

        // Add references to import list.
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());
        YangLinkerUtils.updateFilePriority(utilMgr.getYangNodeSet());

        // Carry out inter-file linking.
        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
        Iterator<YangNode> nodeItr = utilMgr.getYangNodeSet().iterator();
        YangNode rootNode = nodeItr.next();

        if (rootNode.getName().equals("IdRefInGrouping2")) {
            selfNode = rootNode;
        } else {
            selfNode = nodeItr.next();
        }

        YangModule module = (YangModule) selfNode;
        YangContainer cont = (YangContainer) module.getChild();

        leafItr = cont.getListOfLeaf().listIterator();

        // Gets the first leaf, which has three typedef with effective id-ref.
        leafInfo = leafItr.next();
        assertThat(getInCrtName(LEAF, LEAF), leafInfo.getName(), is(LEAF));
        assertThat(getInCrtLeafType(LEAF, LEAF),
                   leafInfo.getDataType().getDataType(), is(IDENTITYREF));

        idRef = (YangIdentityRef) leafInfo.getDataType()
                .getDataTypeExtendedInfo();
        assertThat(getInCrtLeafType(LEAF, LEAF),
                   idRef.getBaseIdentity().getName(), is(BASE1));

        leafListItr = cont.getListOfLeafList().listIterator();

        // Gets the first leaf, which has two typedef with effective id-ref.
        leafListInfo = leafListItr.next();
        assertThat(getInCrtName(LEAF_LIST, LEAF_LIST), leafListInfo.getName(),
                   is(LEAF_LIST));
        assertThat(getInCrtLeafType(LEAF_LIST, LEAF_LIST),
                   leafListInfo.getDataType().getDataType(), is(DERIVED));

        // Traverses through the two typedef in it.
        derInfo1 = (YangDerivedInfo) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();
        typedef1 = derInfo1.getReferredTypeDef();
        type1 = typedef1.getTypeList().get(0);
        idRef = (YangIdentityRef) type1.getDataTypeExtendedInfo();

        assertThat(getInCrtLeafType(TYPEDEF, typedef1.getName()),
                   derInfo1.getEffectiveBuiltInType(), is(IDENTITYREF));
        assertThat(getInCrtLeafType(TYPEDEF, typedef1.getName()),
                   idRef.getBaseIdentity().getName(), is(BASE2));

        YangContainer cont2 = (YangContainer) cont.getChild().getNextSibling();
        leafItr = cont2.getListOfLeaf().listIterator();
        leafInfo = leafItr.next();

        assertThat(getInCrtName(LEAF, LEAF), leafInfo.getName(), is(LEAF));
        assertThat(getInCrtLeafType(LEAF, LEAF),
                   leafInfo.getDataType().getDataType(), is(IDENTITYREF));
        idRef = (YangIdentityRef) leafInfo.getDataType()
                .getDataTypeExtendedInfo();
        assertThat(getInCrtLeafType(LEAF, LEAF),
                   idRef.getBaseIdentity().getName(), is(BASE2));
    }
}
