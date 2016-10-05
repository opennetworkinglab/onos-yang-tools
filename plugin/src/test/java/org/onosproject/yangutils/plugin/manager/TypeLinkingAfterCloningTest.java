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
import org.onosproject.yangutils.datamodel.YangContainer;
import org.onosproject.yangutils.datamodel.YangDerivedInfo;
import org.onosproject.yangutils.datamodel.YangIdentityRef;
import org.onosproject.yangutils.datamodel.YangLeaf;
import org.onosproject.yangutils.datamodel.YangLeafList;
import org.onosproject.yangutils.datamodel.YangLeafRef;
import org.onosproject.yangutils.datamodel.YangList;
import org.onosproject.yangutils.datamodel.YangModule;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangType;
import org.onosproject.yangutils.datamodel.YangTypeDef;
import org.onosproject.yangutils.datamodel.YangUnion;
import org.onosproject.yangutils.linker.impl.YangLinkerManager;
import org.onosproject.yangutils.parser.exceptions.ParserException;

import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yangutils.datamodel.YangNodeType.MODULE_NODE;
import static org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes.DERIVED;
import static org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes.STRING;
import static org.onosproject.yangutils.linker.impl.YangLinkerUtils.updateFilePriority;
import static org.onosproject.yangutils.utils.io.impl.YangFileScanner.getYangFiles;

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

    private final YangUtilManager utilMgr = new YangUtilManager();
    private final YangLinkerManager linkerMgr = new YangLinkerManager();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
     * @throws IOException io error when finding file
     */
    @Test
    public void processLeafRefAfterCloning() throws IOException {

        String searchDir = "src/test/resources/typelinkingaftercloning" +
                "/leafref/intrafile";
        utilMgr.createYangFileInfoSet(getYangFiles(searchDir));
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangNode selfNode;

        // Create YANG node set
        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());

        // Add references to import list.
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        updateFilePriority(utilMgr.getYangNodeSet());

        // Carry out inter-file linking.
        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());

        Iterator<YangNode> nodeItr = utilMgr.getYangNodeSet().iterator();

        selfNode = nodeItr.next();

        ListIterator<YangLeaf> leafItr;
        YangLeaf leafInfo;
        ListIterator<YangLeafList> leafListItr;
        YangLeafList leafListInfo;
        YangLeafRef leafRef;

        // Checks whether the data model tree returned is of type module.
        assertThat((selfNode instanceof YangModule), is(true));

        // Checks whether the node type is set properly to module.
        assertThat(selfNode.getNodeType(), is(MODULE_NODE));

        assertThat(getInCrtName(MODULE, OPEN_ROAD), selfNode.getName(),
                   is(OPEN_ROAD));

        YangList list = (YangList) selfNode.getChild().getNextSibling()
                .getNextSibling();

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
     * Processed invalid scenario where a leaf-ref is present in union.
     *
     * @throws IOException io error when finding file
     */
    @Test
    public void processInvalidLeafRef() throws IOException {
        thrown.expect(ParserException.class);
        thrown.expectMessage("Union member type must not be one of the " +
                                     "built-in types \"empty\" or " +
                                     "\"leafref\"node-id_union");
        String searchDir = "src/test/resources/typelinkingaftercloning" +
                "/leafref/invalid";
        utilMgr.createYangFileInfoSet(getYangFiles(searchDir));
        utilMgr.parseYangFileInfoSet();
    }

    /**
     * Processes simple identity-ref after it gets cloned from grouping.
     *
     * @throws IOException io error when finding file
     */
    @Test
    public void processIdentityRefAfterCloning() throws IOException {

        String searchDir = "src/test/resources/typelinkingaftercloning" +
                "/identityref";
        utilMgr.createYangFileInfoSet(getYangFiles(searchDir));
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangNode selfNode;

        // Create YANG node set
        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());

        // Add references to import list.
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());

        updateFilePriority(utilMgr.getYangNodeSet());

        // Carry out inter-file linking.
        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());

        Iterator<YangNode> nodeItr = utilMgr.getYangNodeSet().iterator();

        selfNode = nodeItr.next();

        ListIterator<YangLeaf> leafItr;
        YangLeaf leafInfo;
        ListIterator<YangLeafList> leafListItr;
        YangLeafList leafListInfo;
        YangIdentityRef identityRef;
        YangUnion union;
        Iterator<YangType<?>> unionTypeItr;
        YangType type;

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
        identityRef = (YangIdentityRef) type.getDataTypeExtendedInfo();

        // Checks the effective type for the leaf.
        assertThat(getInCrtLeafType(LEAF, FACILITY),
                   identityRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        leafInfo = leafItr.next();

        // Checks whether the information in the leaf is correct under list.
        assertThat(getInCrtName(LEAF, NODE_ID), leafInfo.getName(),
                   is(NODE_ID));
        identityRef = (YangIdentityRef) leafInfo.getDataType()
                .getDataTypeExtendedInfo();

        // Checks the effective type for the leaf.
        assertThat(getInCrtLeafType(LEAF, NODE_ID),
                   identityRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        leafListItr = list.getListOfLeafList().listIterator();
        leafListInfo = leafListItr.next();

        // Checks if the information in the leaf-list is correct under list.
        assertThat(getInCrtName(LEAF_LIST, NODE_REF), leafListInfo.getName(),
                   is(NODE_REF));

        identityRef = (YangIdentityRef) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();

        // Checks the effective type for the leaf-list.
        assertThat(getInCrtLeafType(LEAF, NODE_REF),
                   identityRef.getBaseIdentity().getName(),
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
        identityRef = (YangIdentityRef) type.getDataTypeExtendedInfo();

        // Checks the effective type for the leaf-list.
        assertThat(getInCrtLeafType(LEAF_LIST, FACILITY),
                   identityRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        leafListInfo = leafListItr.next();

        // Checks the leaf-list information is correct.
        assertThat(getInCrtName(LEAF_LIST, NODE_REF), leafListInfo.getName(),
                   is(NODE_REF));
        identityRef = (YangIdentityRef) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();

        // Checks the effective type for the leaf.
        assertThat(getInCrtLeafType(LEAF_LIST, NODE_REF),
                   identityRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        leafItr = container.getListOfLeaf().listIterator();
        leafInfo = leafItr.next();

        // Checks the leaf information is correct.
        assertThat(getInCrtName(LEAF, NODE_ID), leafInfo.getName(),
                   is(NODE_ID));
        identityRef = (YangIdentityRef) leafListInfo.getDataType()
                .getDataTypeExtendedInfo();

        assertThat(getInCrtLeafType(LEAF, NODE_ID),
                   identityRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

    }

    /**
     * Processes union having different recursive level with identity-ref.
     *
     * @throws IOException io error when finding file
     */
    @Test
    public void processUnionAfterCloning() throws IOException {

        String searchDir = "src/test/resources/typelinkingaftercloning/union";
        utilMgr.createYangFileInfoSet(getYangFiles(searchDir));
        utilMgr.parseYangFileInfoSet();
        utilMgr.createYangNodeSet();
        YangNode selfNode;

        // Create YANG node set
        linkerMgr.createYangNodeSet(utilMgr.getYangNodeSet());

        // Add references to import list.
        linkerMgr.addRefToYangFilesImportList(utilMgr.getYangNodeSet());
        updateFilePriority(utilMgr.getYangNodeSet());

        // Carry out inter-file linking.
        linkerMgr.processInterFileLinking(utilMgr.getYangNodeSet());
        Iterator<YangNode> nodeItr = utilMgr.getYangNodeSet().iterator();
        selfNode = nodeItr.next();

        YangIdentityRef identityRef;
        YangUnion union;
        Iterator<YangType<?>> unionTypeItr;
        YangType type;
        YangUnion union2;
        Iterator<YangType<?>> unionTypeItr2;
        YangType type2;
        YangUnion union3;
        Iterator<YangType<?>> unionTypeItr3;
        YangType type3;
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
        identityRef = (YangIdentityRef) type3.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                THIRD, FIRST, USABILITY_SYS_LOG, LEAF, FACILITY),
                   identityRef.getBaseIdentity().getName(),
                   is(USABILITY_SYS_LOG));

        // Checks the first identity-ref in second level union.
        type2 = unionTypeItr2.next();
        identityRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                SECOND, FIRST, FACILITY_SYS_LOG, LEAF, FACILITY),
                   identityRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        // Checks the first identity-ref in first level union.
        type = unionTypeItr.next();
        identityRef = (YangIdentityRef) type.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                FIRST, FIRST, AVAILABILITY_SYS_LOG, LEAF, FACILITY),
                   identityRef.getBaseIdentity().getName(),
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
        identityRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                SECOND, FIRST, AVAILABILITY_SYS_LOG, TYPEDEF, CORRECT),
                   identityRef.getBaseIdentity().getName(),
                   is(AVAILABILITY_SYS_LOG));

        // Checks the second identity-ref in second level union.
        type2 = unionTypeItr2.next();
        identityRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                SECOND, SECOND, AVAILABILITY_SYS_LOG, TYPEDEF, CORRECT),
                   identityRef.getBaseIdentity().getName(),
                   is(AVAILABILITY_SYS_LOG));

        // Checks the first identity-ref in first level union.
        type = unionTypeItr.next();
        identityRef = (YangIdentityRef) type.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                FIRST, FIRST, USABILITY_SYS_LOG, TYPEDEF, CORRECT),
                   identityRef.getBaseIdentity().getName(),
                   is(USABILITY_SYS_LOG));

        YangContainer container = (YangContainer) list.getChild()
                .getNextSibling().getNextSibling();


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
        identityRef = (YangIdentityRef) type3.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                THIRD, FIRST, USABILITY_SYS_LOG, LEAF_LIST, FACILITY),
                   identityRef.getBaseIdentity().getName(),
                   is(USABILITY_SYS_LOG));

        // Checks the first identity-ref in second level union.
        type2 = unionTypeItr2.next();
        identityRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                SECOND, FIRST, FACILITY_SYS_LOG, LEAF_LIST, FACILITY),
                   identityRef.getBaseIdentity().getName(),
                   is(FACILITY_SYS_LOG));

        // Checks the first identity-ref in first level union.
        type = unionTypeItr.next();
        identityRef = (YangIdentityRef) type.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                FIRST, FIRST, AVAILABILITY_SYS_LOG, LEAF_LIST, FACILITY),
                   identityRef.getBaseIdentity().getName(),
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
        identityRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                SECOND, FIRST, AVAILABILITY_SYS_LOG, TYPEDEF, CORRECT),
                   identityRef.getBaseIdentity().getName(),
                   is(AVAILABILITY_SYS_LOG));

        // Checks the second identity-ref in second level union.
        type2 = unionTypeItr2.next();
        identityRef = (YangIdentityRef) type2.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                SECOND, SECOND, AVAILABILITY_SYS_LOG, TYPEDEF, CORRECT),
                   identityRef.getBaseIdentity().getName(),
                   is(AVAILABILITY_SYS_LOG));

        // Checks the first identity-ref in first level union.
        type = unionTypeItr.next();
        identityRef = (YangIdentityRef) type.getDataTypeExtendedInfo();
        assertThat(getInCrtUnionWithIdRef(
                FIRST, FIRST, USABILITY_SYS_LOG, TYPEDEF, CORRECT),
                   identityRef.getBaseIdentity().getName(),
                   is(USABILITY_SYS_LOG));
    }
}
