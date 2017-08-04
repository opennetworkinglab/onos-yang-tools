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
import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangPatternRestriction;
import org.onosproject.yang.compiler.datamodel.YangRangeInterval;
import org.onosproject.yang.compiler.datamodel.YangRangeRestriction;
import org.onosproject.yang.compiler.datamodel.YangStringRestriction;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangTypeDef;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangInt16;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangInt32;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangUint64;
import org.onosproject.yang.compiler.linker.exceptions.LinkerException;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ListIterator;

import static java.math.BigInteger.valueOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.onosproject.yang.compiler.datamodel.YangNodeType.MODULE_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.RESOLVED;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DERIVED;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT16;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT32;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.STRING;

/**
 * Test cases for testing restriction resolution.
 */
public final class RestrictionResolutionTest {

    private final YangUtilsParserManager manager = new YangUtilsParserManager();

    /**
     * Checks length restriction in typedef.
     */
    @Test
    public void processLengthRestrictionInTypedef()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel(
                "src/test/resources/LengthRestrictionInTypedef.yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();

        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("hello"));
        assertThat(type.getDataType(), is(DERIVED));
        YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                .getDataTypeExtendedInfo();

        assertThat(info.getReferredTypeDef(),
                   Is.is((YangTypeDef) node.getChild()));

        assertThat(type.getResolvableStatus(), Is.is(RESOLVED));

        // Check for the effective built-in type.
        assertThat(info.getEffectiveBuiltInType(), is(STRING));

        // Check for the restriction.
        assertThat(info.getLengthRes(), is(nullValue()));
        assertThat(info.getRangeRes(), is(nullValue()));
        assertThat(info.getPatternResList(), is(nullValue()));
        assertThat(info.getResolvedExtendedInfo(), is(notNullValue()));

        // Check for the restriction value.
        YangStringRestriction strRes = (YangStringRestriction) info
                .getResolvedExtendedInfo();
        YangRangeRestriction lenRes = strRes.getLengthRestriction();

        ListIterator<YangRangeInterval> llIt = lenRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range = llIt.next();

        assertThat(((YangUint64) range.getStartValue()).getValue(),
                   is(valueOf(0)));
        assertThat(((YangUint64) range.getEndValue()).getValue(),
                   is(valueOf(100)));
    }

    /**
     * Checks length restriction in referred type.
     */
    @Test
    public void processLengthRestrictionInRefType()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel(
                "src/test/resources/LengthRestrictionInRefType.yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();

        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("hello"));
        assertThat(type.getDataType(), is(DERIVED));
        YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                .getDataTypeExtendedInfo();

        assertThat(info.getReferredTypeDef(),
                   is((YangTypeDef) node.getChild()));

        assertThat(type.getResolvableStatus(), Is.is(RESOLVED));
        // Check for the effective built-in type.
        assertThat(info.getEffectiveBuiltInType(), is(STRING));

        // Check for the restriction.
        assertThat(info.getLengthRes(), is(notNullValue()));
        assertThat(info.getRangeRes(), is(nullValue()));
        assertThat(info.getPatternResList(), is(nullValue()));
        assertThat(info.getResolvedExtendedInfo(), is(notNullValue()));

        // Check for the restriction value.
        YangStringRestriction strRes = (YangStringRestriction) info
                .getResolvedExtendedInfo();
        YangRangeRestriction lenRes = strRes.getLengthRestriction();

        ListIterator<YangRangeInterval> llIt = lenRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range = llIt.next();

        assertThat(((YangUint64) range.getStartValue()).getValue(),
                   is(valueOf(0)));
        assertThat(((YangUint64) range.getEndValue()).getValue(),
                   is(valueOf(100)));
    }

    /**
     * Checks length restriction in typedef and in type with stricter value.
     */
    @Test
    public void processLengthRestrictionInTypedefAndTypeValid()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel(
                "src/test/resources/LengthRestrictionInTypedefAndTypeValid." +
                        "yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();

        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("hello"));
        assertThat(type.getDataType(), is(DERIVED));

        YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                .getDataTypeExtendedInfo();

        assertThat(info.getReferredTypeDef(),
                   is((YangTypeDef) node.getChild()));

        assertThat(type.getResolvableStatus(), Is.is(RESOLVED));

        // Check for the effective built-in type.
        assertThat(info.getEffectiveBuiltInType(), is(STRING));

        // Check for the restriction.
        assertThat(info.getLengthRes(), is(notNullValue()));
        assertThat(info.getRangeRes(), is(nullValue()));
        assertThat(info.getPatternResList(), is(nullValue()));
        assertThat(info.getResolvedExtendedInfo(), is(notNullValue()));

        // Check for the restriction value.
        YangStringRestriction strRes = (YangStringRestriction) info
                .getResolvedExtendedInfo();
        YangRangeRestriction lenRes = strRes.getLengthRestriction();

        ListIterator<YangRangeInterval> llIt = lenRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range1 = llIt.next();

        assertThat(((YangUint64) range1.getStartValue()).getValue(),
                   is(valueOf(0)));
        assertThat(((YangUint64) range1.getEndValue()).getValue(),
                   is(valueOf(20)));

        YangRangeInterval range2 = llIt.next();

        assertThat(((YangUint64) range2.getStartValue()).getValue(),
                   is(valueOf(201)));
        assertThat(((YangUint64) range2.getEndValue()).getValue(),
                   is(valueOf(300)));
    }

    /**
     * Checks length restriction in typedef and in type with not stricter value.
     */
    @Test(expected = LinkerException.class)
    public void processLengthRestrictionInTypedefAndTypeInValid()
            throws IOException, DataModelException {
        manager.getDataModel("src/test/resources/LengthRestrictionInTypedef" +
                                     "AndTypeInValid.yang");
    }

    /**
     * Checks range restriction in typedef.
     */
    @Test
    public void processRangeRestrictionInTypedef()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel(
                "src/test/resources/RangeRestrictionInTypedef.yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();

        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("hello"));
        assertThat(type.getDataType(), is(DERIVED));

        YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                .getDataTypeExtendedInfo();

        assertThat(info.getReferredTypeDef(),
                   is((YangTypeDef) node.getChild()));

        assertThat(type.getResolvableStatus(), Is.is(RESOLVED));

        // Check for the effective built-in type.
        assertThat(info.getEffectiveBuiltInType(), is(INT32));

        // Check for the restriction.
        assertThat(info.getLengthRes(), is(nullValue()));
        assertThat(info.getRangeRes(), is(nullValue()));
        assertThat(info.getPatternResList(), is(nullValue()));
        assertThat(info.getResolvedExtendedInfo(), is(notNullValue()));

        // Check for the restriction value.
        YangRangeRestriction ranRes = (YangRangeRestriction) info
                .getResolvedExtendedInfo();

        ListIterator<YangRangeInterval> rlIt = ranRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range1 = rlIt.next();

        assertThat(((YangInt32) range1.getStartValue()).getValue(), is(1));
        assertThat(((YangInt32) range1.getEndValue()).getValue(), is(4));

        YangRangeInterval range2 = rlIt.next();

        assertThat(((YangInt32) range2.getStartValue()).getValue(), is(10));
        assertThat(((YangInt32) range2.getEndValue()).getValue(), is(20));
    }

    /**
     * Checks range restriction in referred typedef.
     */
    @Test
    public void processRangeRestrictionInRefTypedef()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel(
                "src/test/resources/RangeRestrictionInRefTypedef.yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        // check top typedef
        YangTypeDef typeDef1 = (YangTypeDef) yangNode.getChild();
        assertThat(typeDef1.getName(), is("Num3"));
        YangType type = typeDef1.getTypeList().iterator().next();
        assertThat(type.getDataType(), is(INT16));
        assertThat(type.getDataTypeName(), is("int16"));

        // Check for the restriction value.
        YangRangeRestriction ranRes = (YangRangeRestriction) type.
                getDataTypeExtendedInfo();
        ListIterator<YangRangeInterval> rlIt = ranRes
                .getAscendingRangeIntervals().listIterator();
        YangRangeInterval range1 = rlIt.next();
        assertThat((int) ((YangInt16) range1.getStartValue()).getValue(),
                   is(-32000));
        assertThat((int) ((YangInt16) range1.getEndValue()).getValue(), is(4));

        YangRangeInterval range2 = rlIt.next();
        assertThat((int) ((YangInt16) range2.getStartValue()).getValue(),
                   is(32767));
        assertThat((int) ((YangInt16) range2.getEndValue()).getValue(),
                   is(32767));

        // check referred typedef
        YangTypeDef refTypedef = (YangTypeDef) typeDef1.getNextSibling();
        assertThat(refTypedef.getName(), is("Num6"));
        YangType refType = refTypedef.getTypeList().iterator().next();
        assertThat(refType.getDataType(), is(DERIVED));
        assertThat(refType.getDataTypeName(), is("Num3"));
        YangDerivedInfo<YangRangeRestriction> info =
                (YangDerivedInfo<YangRangeRestriction>) refType
                        .getDataTypeExtendedInfo();

        // Check for the restriction value.
        ranRes = info.getResolvedExtendedInfo();
        rlIt = ranRes.getAscendingRangeIntervals().listIterator();
        range1 = rlIt.next();
        assertThat((int) ((YangInt16) range1.getStartValue()).getValue(),
                   is(-3));
        assertThat((int) ((YangInt16) range1.getEndValue()).getValue(), is(-3));

        range2 = rlIt.next();
        assertThat((int) ((YangInt16) range2.getStartValue()).getValue(),
                   is(-2));
        assertThat((int) ((YangInt16) range2.getEndValue()).getValue(), is(2));

        YangRangeInterval range3 = rlIt.next();
        assertThat((int) ((YangInt16) range3.getStartValue()).getValue(),
                   is(3));
        assertThat((int) ((YangInt16) range3.getEndValue()).getValue(), is(3));
    }

    /**
     * Checks invalid range restriction in referred typedef.
     */
    @Test(expected = LinkerException.class)
    public void processInvalidRangeRestrictionInRefTypedef()
            throws IOException, ParserException, DataModelException {
        manager.getDataModel("src/test/resources/RangeRestrictionInvalidIn" +
                                     "RefTypedef.yang");
    }

    /**
     * Checks range restriction in referred type.
     */
    @Test
    public void processRangeRestrictionInRefType()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel("src/test/resources/RangeRes" +
                                                     "trictionInRefType.yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();

        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("hello"));
        assertThat(type.getDataType(), is(DERIVED));

        YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                .getDataTypeExtendedInfo();

        assertThat(info.getReferredTypeDef(),
                   is((YangTypeDef) node.getChild()));

        assertThat(type.getResolvableStatus(), Is.is(RESOLVED));

        // Check for the effective built-in type.
        assertThat(info.getEffectiveBuiltInType(), is(INT32));

        // Check for the restriction.
        assertThat(info.getLengthRes(), is(nullValue()));
        assertThat(info.getRangeRes(), is(notNullValue()));
        assertThat(info.getPatternResList(), is(nullValue()));
        assertThat(info.getResolvedExtendedInfo(), is(notNullValue()));

        // Check for the restriction value.
        YangRangeRestriction ranRes = (YangRangeRestriction) info
                .getResolvedExtendedInfo();

        ListIterator<YangRangeInterval> rlIt = ranRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range1 = rlIt.next();

        assertThat(((YangInt32) range1.getStartValue()).getValue(), is(1));
        assertThat(((YangInt32) range1.getEndValue()).getValue(), is(4));

        YangRangeInterval range2 = rlIt.next();

        assertThat(((YangInt32) range2.getStartValue()).getValue(), is(10));
        assertThat(((YangInt32) range2.getEndValue()).getValue(), is(20));
    }

    /**
     * Checks range restriction in typedef and stricter in referred type.
     */
    @Test
    public void processRangeRestrictionInRefTypeAndTypedefValid()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel(
                "src/test/resources/RangeRestrictionInRefTypeAndTypedef" +
                        "Valid.yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();

        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("hello"));
        assertThat(type.getDataType(), is(DERIVED));

        YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                .getDataTypeExtendedInfo();

        assertThat(info.getReferredTypeDef(),
                   is((YangTypeDef) node.getChild()));

        assertThat(type.getResolvableStatus(), Is.is(RESOLVED));

        // Check for the effective built-in type.
        assertThat(info.getEffectiveBuiltInType(), is(INT32));

        // Check for the restriction.
        assertThat(info.getLengthRes(), is(nullValue()));
        assertThat(info.getRangeRes(), is(notNullValue()));
        assertThat(info.getPatternResList(), is(nullValue()));
        assertThat(info.getResolvedExtendedInfo(), is(notNullValue()));

        // Check for the restriction value.
        YangRangeRestriction ranRes = (YangRangeRestriction) info
                .getResolvedExtendedInfo();

        ListIterator<YangRangeInterval> rlIt = ranRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range1 = rlIt.next();

        assertThat(((YangInt32) range1.getStartValue()).getValue(), is(1));
        assertThat(((YangInt32) range1.getEndValue()).getValue(), is(4));

        YangRangeInterval range2 = rlIt.next();

        assertThat(((YangInt32) range2.getStartValue()).getValue(), is(10));
        assertThat(((YangInt32) range2.getEndValue()).getValue(), is(20));
    }

    /**
     * Checks range restriction in typedef and not stricter in referred type.
     */
    @Test(expected = LinkerException.class)
    public void processRangeRestrictionInRefTypeAndTypedefInValid()
            throws IOException, ParserException, DataModelException {
        manager.getDataModel("src/test/resources/RangeRestrictionInRefType" +
                                     "AndTypedefInValid.yang");
    }

    /**
     * Checks range restriction for string.
     */
    @Test(expected = ParserException.class)
    public void processRangeRestrictionInString()
            throws IOException, ParserException, DataModelException {
        manager.getDataModel("src/test/resources/RangeRestrictionInString." +
                                     "yang");
    }

    /**
     * Checks range restriction for string in referred type.
     */
    @Test(expected = LinkerException.class)
    public void processRangeRestrictionInStringInRefType()
            throws IOException, DataModelException {
        manager.getDataModel("src/test/resources/RangeRestrictionInStringIn" +
                                     "RefType.yang");
    }

    /**
     * Checks pattern restriction in typedef.
     */
    @Test
    public void processPatternRestrictionInTypedef()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel(
                "src/test/resources/PatternRestrictionInTypedef.yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();
        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("hello"));
        assertThat(type.getDataType(), is(DERIVED));

        YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                .getDataTypeExtendedInfo();
        YangTypeDef typeDef = info.getReferredTypeDef();

        assertThat(typeDef, is((YangTypeDef) node.getChild()));

        assertThat(type.getResolvableStatus(), Is.is(RESOLVED));


        // Check for the effective built-in type.
        assertThat(info.getEffectiveBuiltInType(), is(STRING));

        // Check for the restriction.
        assertThat(info.getLengthRes(), is(nullValue()));
        assertThat(info.getRangeRes(), is(nullValue()));
        assertThat(info.getPatternResList(), is(nullValue()));
        assertThat(info.getResolvedExtendedInfo(), is(notNullValue()));

        // Check for the restriction value.
        YangStringRestriction strRes = (YangStringRestriction) info
                .getResolvedExtendedInfo();
        ListIterator<YangPatternRestriction> patIt = strRes
                .getPatternResList().listIterator();
        YangPatternRestriction pattern1 = patIt.next();

        assertThat(pattern1.getPattern(), is("[a-zA-Z]"));
    }

    /**
     * Checks pattern restriction in referred type.
     */
    @Test
    public void processPatternRestrictionInRefType()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel(
                "src/test/resources/PatternRestrictionInRefType.yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();
        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("hello"));
        assertThat(type.getDataType(), is(DERIVED));
        YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                .getDataTypeExtendedInfo();

        assertThat(info.getReferredTypeDef(),
                   is((YangTypeDef) node.getChild()));

        assertThat(type.getResolvableStatus(), Is.is(RESOLVED));

        // Check for the effective built-in type.
        assertThat(info.getEffectiveBuiltInType(), is(STRING));

        // Check for the restriction.
        assertThat(info.getLengthRes(), is(nullValue()));
        assertThat(info.getRangeRes(), is(nullValue()));
        assertThat(info.getPatternResList(), is(notNullValue()));
        assertThat(info.getResolvedExtendedInfo(), is(notNullValue()));

        // Check for the restriction value.
        YangStringRestriction strRes = (YangStringRestriction) info
                .getResolvedExtendedInfo();
        ListIterator<YangPatternRestriction> patIt = strRes
                .getPatternResList().listIterator();
        YangPatternRestriction pattern1 = patIt.next();

        assertThat(pattern1.getPattern(), is("[a-zA-Z]"));
    }

    /**
     * Checks pattern restriction in referred type and typedef.
     */
    @Test
    public void processPatternRestrictionInRefTypeAndTypedef()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel(
                "src/test/resources/PatternRestrictionInRefTypeAndTypedef" +
                        ".yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();
        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("hello"));
        assertThat(type.getDataType(), is(DERIVED));
        YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                .getDataTypeExtendedInfo();

        assertThat(info.getReferredTypeDef(),
                   is((YangTypeDef) node.getChild()));

        assertThat(type.getResolvableStatus(), Is.is(RESOLVED));

        // Check for the effective built-in type.
        assertThat(info.getEffectiveBuiltInType(), is(STRING));

        // Check for the restriction.
        assertThat(info.getLengthRes(), is(nullValue()));
        assertThat(info.getRangeRes(), is(nullValue()));
        assertThat(info.getPatternResList(), is(notNullValue()));
        assertThat(info.getResolvedExtendedInfo(), is(notNullValue()));

        // Check for the restriction value.
        YangStringRestriction strRes = (YangStringRestriction) info
                .getResolvedExtendedInfo();
        ListIterator<YangPatternRestriction> patIt = strRes
                .getPatternResList().listIterator();
        YangPatternRestriction pattern1 = patIt.next();
        assertThat(pattern1.getPattern(), is("[a-zA-Z]"));

        YangPatternRestriction pattern2 = patIt.next();
        assertThat(pattern2.getPattern(), is("[0-9]"));
    }

    /**
     * Checks multiple pattern restriction in referred type and typedef.
     */
    @Test
    public void processMultiplePatternRestriction()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel(
                "src/test/resources/MultiplePatternRestrictionInRefTypeAnd" +
                        "Typedef.yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();
        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("hello"));
        assertThat(type.getDataType(), is(DERIVED));
        YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                .getDataTypeExtendedInfo();

        assertThat(info.getReferredTypeDef(),
                   is((YangTypeDef) node.getChild()));

        assertThat(type.getResolvableStatus(), Is.is(RESOLVED));

        // Check for the effective built-in type.
        assertThat(info.getEffectiveBuiltInType(), is(STRING));

        // Check for the restriction.
        assertThat(info.getLengthRes(), is(nullValue()));
        assertThat(info.getRangeRes(), is(nullValue()));
        assertThat(info.getPatternResList(), is(notNullValue()));
        assertThat(info.getResolvedExtendedInfo(), is(notNullValue()));

        // Check for the restriction value.
        YangStringRestriction strRes = (YangStringRestriction) info
                .getResolvedExtendedInfo();
        ListIterator<YangPatternRestriction> patIt = strRes
                .getPatternResList().listIterator();
        YangPatternRestriction pattern1 = patIt.next();
        assertThat(pattern1.getPattern(), is("[a-z]"));

        YangPatternRestriction pattern2 = patIt.next();
        assertThat(pattern2.getPattern(), is("[A-Z]"));

        YangPatternRestriction pattern3 = patIt.next();
        assertThat(pattern3.getPattern(), is("[0-9]"));

        YangPatternRestriction pattern4 = patIt.next();
        assertThat(pattern4.getPattern(), is("[\\n]"));
    }

    /**
     * Checks multiple pattern and length restriction in referred type and
     * typedef.
     */
    @Test
    public void processMultiplePatternAndLengthRestriction()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel(
                "src/test/resources/MultiplePatternAndLengthRestriction.yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();
        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("hello"));
        assertThat(type.getDataType(), is(DERIVED));

        YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                .getDataTypeExtendedInfo();

        assertThat(info.getReferredTypeDef(),
                   is((YangTypeDef) node.getChild()));

        assertThat(type.getResolvableStatus(), Is.is(RESOLVED));

        // Check for the effective built-in type.
        assertThat(info.getEffectiveBuiltInType(), is(STRING));

        // Check for the restriction.
        assertThat(info.getLengthRes(), is(notNullValue()));
        assertThat(info.getRangeRes(), is(nullValue()));
        assertThat(info.getPatternResList(), is(notNullValue()));
        assertThat(info.getResolvedExtendedInfo(), is(notNullValue()));

        // Check for the restriction value.
        YangStringRestriction strRes = (YangStringRestriction) info
                .getResolvedExtendedInfo();
        ListIterator<YangPatternRestriction> patIt = strRes
                .getPatternResList().listIterator();
        YangPatternRestriction pattern1 = patIt.next();
        assertThat(pattern1.getPattern(), is("[a-z]"));
        assertThat(pattern1.getDescription(),
                   is("\"pattern a-z description.\""));
        assertThat(pattern1.getReference(), is("\"a-z reference\""));

        YangPatternRestriction pattern2 = patIt.next();
        assertThat(pattern2.getPattern(), is("[A-Z]"));
        assertThat(pattern2.getDescription(),
                   is("\"pattern A-Z description.\""));
        assertThat(pattern2.getReference(), is("\"A-Z reference\""));

        YangPatternRestriction pattern3 = patIt.next();
        assertThat(pattern3.getPattern(), is("[0-9]"));
        assertThat(pattern3.getDescription(),
                   is("\"pattern 0-9 description.\""));
        assertThat(pattern3.getReference(), is("\"0-9 reference\""));

        YangPatternRestriction pattern4 = patIt.next();
        assertThat(pattern4.getPattern(), is("[\\n]"));
        assertThat(pattern4.getDescription(),
                   is("\"pattern \\n description.\""));
        assertThat(pattern4.getReference(), is("\"\\n reference\""));

        // Check for length restriction.
        YangRangeRestriction lenRes = strRes.getLengthRestriction();
        ListIterator<YangRangeInterval> lenIt = lenRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range1 = lenIt.next();

        assertThat(((YangUint64) range1.getStartValue()).getValue(),
                   is(valueOf(0)));
        assertThat(((YangUint64) range1.getEndValue()).getValue(),
                   is(valueOf(20)));

        YangRangeInterval range2 = lenIt.next();

        assertThat(((YangUint64) range2.getStartValue()).getValue(),
                   is(valueOf(201)));
        assertThat(((YangUint64) range2.getEndValue()).getValue(),
                   is(valueOf(300)));
    }

    /**
     * Checks multiple pattern and length restriction in referred type and
     * typedef.
     */
    @Test
    public void processMultiplePatternAndLengthRestrictionValid()
            throws IOException, ParserException, DataModelException {

        YangNode node = manager.getDataModel(
                "src/test/resources/MultiplePatternAndLengthRestric" +
                        "tionValid.yang");

        // Check whether the data model tree returned is of type module.
        assertThat(node instanceof YangModule, is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> it = yangNode.getListOfLeaf().listIterator();
        YangLeaf leaf = it.next();
        YangType<?> type = leaf.getDataType();

        assertThat(leaf.getName(), is("invalid-interval"));
        assertThat(type.getDataTypeName(), is("hello"));
        assertThat(type.getDataType(), is(DERIVED));
        YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                .getDataTypeExtendedInfo();

        assertThat(info.getReferredTypeDef(),
                   is((YangTypeDef) node.getChild()));

        assertThat(type.getResolvableStatus(), Is.is(RESOLVED));

        // Check for the effective built-in type.
        assertThat(info.getEffectiveBuiltInType(), is(STRING));

        // Check for the restriction.
        assertThat(info.getLengthRes(), is(notNullValue()));
        assertThat(info.getRangeRes(), is(nullValue()));
        assertThat(info.getPatternResList(), is(notNullValue()));
        assertThat(info.getResolvedExtendedInfo(), is(notNullValue()));

        // Check for the restriction value.
        YangStringRestriction strRes = (YangStringRestriction) info
                .getResolvedExtendedInfo();

        ListIterator<YangPatternRestriction> patIt = strRes
                .getPatternResList().listIterator();
        YangPatternRestriction pattern1 = patIt.next();
        assertThat(pattern1.getPattern(), is("[a-z]"));

        YangPatternRestriction pattern2 = patIt.next();
        assertThat(pattern2.getPattern(), is("[A-Z]"));

        YangPatternRestriction pattern3 = patIt.next();
        assertThat(pattern3.getPattern(), is("[0-9]"));

        YangPatternRestriction pattern4 = patIt.next();
        assertThat(pattern4.getPattern(), is("[\\n]"));

        // Check for length restriction.
        YangRangeRestriction lenRes = strRes.getLengthRestriction();
        ListIterator<YangRangeInterval> lenIt = lenRes
                .getAscendingRangeIntervals().listIterator();

        YangRangeInterval range1 = lenIt.next();

        assertThat(((YangUint64) range1.getStartValue()).getValue(),
                   is(valueOf(0)));
        assertThat(((YangUint64) range1.getEndValue()).getValue(),
                   is(valueOf(20)));

        YangRangeInterval range2 = lenIt.next();

        assertThat(((YangUint64) range2.getStartValue()).getValue(),
                   is(valueOf(100)));
        assertThat(((YangUint64) range2.getEndValue()).getValue(),
                   is(new BigInteger("18446744073709551615")));
    }

    /**
     * Checks multiple pattern and length restriction in referred type and
     * typedef invalid scenario.
     */
    @Test(expected = LinkerException.class)
    public void processMultiplePatternAndLengthRestrictionInValid()
            throws IOException, DataModelException {
        YangNode node = manager.getDataModel(
                "src/test/resources/MultiplePatternAndLengthRestriction" +
                        "InValid.yang");
    }
}
