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

package org.onosproject.yangutils.translator.tojava.utils;

import org.onosproject.yangutils.datamodel.RpcNotificationContainer;
import org.onosproject.yangutils.datamodel.YangAugment;
import org.onosproject.yangutils.datamodel.YangChoice;
import org.onosproject.yangutils.datamodel.YangLeafRef;
import org.onosproject.yangutils.datamodel.YangLeavesHolder;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangType;
import org.onosproject.yangutils.translator.tojava.JavaAttributeInfo;
import org.onosproject.yangutils.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yangutils.translator.tojava.JavaFileInfoContainer;
import org.onosproject.yangutils.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yangutils.translator.tojava.JavaQualifiedTypeInfoTranslator;
import org.onosproject.yangutils.translator.tojava.TempJavaCodeFragmentFilesContainer;
import org.onosproject.yangutils.utils.io.YangPluginConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes.LEAFREF;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.FILTER_CONTENT_MATCH_FOR_LEAF_LIST_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.FILTER_CONTENT_MATCH_FOR_LEAF_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.FILTER_CONTENT_MATCH_FOR_NODES_MASK;
import static org.onosproject.yangutils.translator.tojava.YangJavaModelUtils.getNodesPackage;
import static org.onosproject.yangutils.translator.tojava.utils.IndentationType.EIGHT_SPACE;
import static org.onosproject.yangutils.translator.tojava.utils.IndentationType.FOUR_SPACE;
import static org.onosproject.yangutils.translator.tojava.utils.IndentationType.SIXTEEN_SPACE;
import static org.onosproject.yangutils.translator.tojava.utils.IndentationType.TWELVE_SPACE;
import static org.onosproject.yangutils.translator.tojava.utils.IndentationType.TWENTY_FOUR_SPACE;
import static org.onosproject.yangutils.translator.tojava.utils.IndentationType.TWENTY_SPACE;
import static org.onosproject.yangutils.translator.tojava.utils.JavaFileGeneratorUtils.getDataFromTempFileHandle;
import static org.onosproject.yangutils.translator.tojava.utils.MethodClassTypes.CLASS_TYPE;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getAppInstanceAttrString;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getChoiceChildNodes;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getCollectionIteratorForLoopBegin;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getElseIfConditionBegin;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getEqualEqualString;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getIfConditionBegin;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getLeafFlagSetString;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getOpenCloseParaWithValue;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getReturnString;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getTwoParaEqualsString;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.methodClose;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.multiAttrMethodSignature;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.signatureClose;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.valueAssign;
import static org.onosproject.yangutils.utils.UtilConstants.ADD_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.AND_OPERATION;
import static org.onosproject.yangutils.utils.UtilConstants.APP_INSTANCE;
import static org.onosproject.yangutils.utils.UtilConstants.BOOLEAN_DATA_TYPE;
import static org.onosproject.yangutils.utils.UtilConstants.BOOLEAN_WRAPPER;
import static org.onosproject.yangutils.utils.UtilConstants.BREAK;
import static org.onosproject.yangutils.utils.UtilConstants.BUILD;
import static org.onosproject.yangutils.utils.UtilConstants.BUILDER;
import static org.onosproject.yangutils.utils.UtilConstants.BUILD_FOR_FILTER;
import static org.onosproject.yangutils.utils.UtilConstants.CHOICE_STF_METHOD_NAME;
import static org.onosproject.yangutils.utils.UtilConstants.CLOSE_CURLY_BRACKET;
import static org.onosproject.yangutils.utils.UtilConstants.CLOSE_PARENTHESIS;
import static org.onosproject.yangutils.utils.UtilConstants.COMMA;
import static org.onosproject.yangutils.utils.UtilConstants.DEFAULT_CAPS;
import static org.onosproject.yangutils.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yangutils.utils.UtilConstants.ELSE;
import static org.onosproject.yangutils.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.EQUAL;
import static org.onosproject.yangutils.utils.UtilConstants.EQUALS_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.FALSE;
import static org.onosproject.yangutils.utils.UtilConstants.FOUR_SPACE_INDENTATION;
import static org.onosproject.yangutils.utils.UtilConstants.GET;
import static org.onosproject.yangutils.utils.UtilConstants.IF;
import static org.onosproject.yangutils.utils.UtilConstants.INSTANCE_OF;
import static org.onosproject.yangutils.utils.UtilConstants.IS_ANY_SELECT_OR_CONTAINMENT_NODE_FLAG;
import static org.onosproject.yangutils.utils.UtilConstants.IS_EMPTY;
import static org.onosproject.yangutils.utils.UtilConstants.IS_SELECT_ALL_SCHEMA_CHILD_FLAG;
import static org.onosproject.yangutils.utils.UtilConstants.NEW;
import static org.onosproject.yangutils.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yangutils.utils.UtilConstants.NOT;
import static org.onosproject.yangutils.utils.UtilConstants.NULL;
import static org.onosproject.yangutils.utils.UtilConstants.OPEN_CLOSE_BRACKET_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.OPEN_CURLY_BRACKET;
import static org.onosproject.yangutils.utils.UtilConstants.OPEN_PARENTHESIS;
import static org.onosproject.yangutils.utils.UtilConstants.OP_PARAM;
import static org.onosproject.yangutils.utils.UtilConstants.OR_OPERATION;
import static org.onosproject.yangutils.utils.UtilConstants.PERIOD;
import static org.onosproject.yangutils.utils.UtilConstants.PRIVATE;
import static org.onosproject.yangutils.utils.UtilConstants.PROCESS_CHILD_NODE_STF_PARAM;
import static org.onosproject.yangutils.utils.UtilConstants.PROCESS_SUBTREE_FILTERING;
import static org.onosproject.yangutils.utils.UtilConstants.PUBLIC;
import static org.onosproject.yangutils.utils.UtilConstants.RETURN;
import static org.onosproject.yangutils.utils.UtilConstants.SELECT_ALL_CHILD_SCHEMA_PARAM;
import static org.onosproject.yangutils.utils.UtilConstants.SELECT_LEAF;
import static org.onosproject.yangutils.utils.UtilConstants.SELECT_OR_CONTAINMENT_NODE_PARAM;
import static org.onosproject.yangutils.utils.UtilConstants.SEMI_COLON;
import static org.onosproject.yangutils.utils.UtilConstants.SIXTEEN_SPACE_INDENTATION;
import static org.onosproject.yangutils.utils.UtilConstants.SPACE;
import static org.onosproject.yangutils.utils.UtilConstants.STF_BUILDER_PARAM;
import static org.onosproject.yangutils.utils.UtilConstants.SUBTREE_FILTERING_RESULT_BUILDER;
import static org.onosproject.yangutils.utils.UtilConstants.THIRTY_TWO_SPACE_INDENTATION;
import static org.onosproject.yangutils.utils.UtilConstants.TO;
import static org.onosproject.yangutils.utils.UtilConstants.TRUE;
import static org.onosproject.yangutils.utils.UtilConstants.TWELVE_SPACE_INDENTATION;
import static org.onosproject.yangutils.utils.UtilConstants.TWENTY_EIGHT_SPACE_INDENTATION;
import static org.onosproject.yangutils.utils.UtilConstants.TWENTY_FOUR_SPACE_INDENTATION;
import static org.onosproject.yangutils.utils.UtilConstants.TWENTY_SPACE_INDENTATION;
import static org.onosproject.yangutils.utils.UtilConstants.VALUE_LEAF;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getCapitalCase;

/**
 * Represents generator for subtree filtering methods of generated files
 * based on the file type.
 */
//TODO: improve class to use string generator.
public final class SubtreeFilteringMethodsGenerator {

    /**
     * private to make it a util.
     */
    private SubtreeFilteringMethodsGenerator() {
    }

    /**
     * Returns is filter content match for leaf.
     *
     * @param javaAttributeInfo java attribute
     * @param type              data type
     * @return is filter content match for leaf
     */
    public static String getSubtreeFilteringForLeaf(
            JavaAttributeInfo javaAttributeInfo, YangType<?> type) {
        String attrQualifiedType;
        String attributeName = javaAttributeInfo.getAttributeName();
        attrQualifiedType = getIfFilterContentMatchMethodImpl(attributeName,
                                                              type);
        /* if (valueLeafFlags.get(LeafIdentifier.LEAF.getLeafIndex())) {
         * if (appInstance.leaf() != leaf()) {
         * return false;
         * } else {
         * subTreeFilteringResultBuilder.leaf(appInstance.leaf());
         * }
         * } else if (selectLeafFlags.get(LeafIdentifier.LEAF.getLeafIndex()) ||
         * isSelectAllSchemaChild) {
         * isAnySelectOrContainmentNode = true;
         * subTreeFilteringResultBuilder.leaf(appInstance.leaf());
         * }*/
        return getIfConditionBegin(EIGHT_SPACE_INDENTATION, getLeafFlagSetString(
                attributeName, VALUE_LEAF, EMPTY_STRING, GET)) +
                getIfConditionBegin(TWELVE_SPACE_INDENTATION, attrQualifiedType) +
                getReturnString(FALSE, SIXTEEN_SPACE_INDENTATION) +
                signatureClose() +
                TWELVE_SPACE_INDENTATION + CLOSE_CURLY_BRACKET + SPACE + ELSE +
                SPACE + OPEN_CURLY_BRACKET + NEW_LINE +
                getSubTreeBuilderCallString(SIXTEEN_SPACE_INDENTATION, attributeName,
                                            TWELVE_SPACE) +
                EIGHT_SPACE_INDENTATION + CLOSE_CURLY_BRACKET + SPACE + ELSE +
                getIfConditionBegin(SPACE, getLeafFlagSetString(
                        attributeName, SELECT_LEAF, EMPTY_STRING, GET) + SPACE +
                        OR_OPERATION + SPACE + IS_SELECT_ALL_SCHEMA_CHILD_FLAG) +
                valueAssign(IS_ANY_SELECT_OR_CONTAINMENT_NODE_FLAG, TRUE,
                            TWELVE_SPACE_INDENTATION) +
                getSubTreeBuilderCallString(TWELVE_SPACE_INDENTATION, attributeName,
                                            EIGHT_SPACE);
    }

    private static String getSubTreeBuilderCallString(String indent, String
            name, IndentationType type) {
        return indent + SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + name +
                getOpenCloseParaWithValue(APP_INSTANCE + PERIOD + name +
                                                  OPEN_CLOSE_BRACKET_STRING) +
                signatureClose() + methodClose(type);
    }

    private static String getAttrTypeForFcmWhenPrimitiveDataType(
            String attributeName) {
        return/* TODO: Need to check if we can expose the value leaf flag in
         interface.
                NOT + APP_INSTANCE + PERIOD + GET_VALUE_LEAF_FLAGS +
                OPEN_PARENTHESIS + CLOSE_PARENTHESIS +
                GET_METHOD_PREFIX + OPEN_PARENTHESIS + LEAF_IDENTIFIER +
                PERIOD + attributeName.toUpperCase() + PERIOD
                + GET_LEAF_INDEX + OR_OPERATION +*/
                APP_INSTANCE + PERIOD + attributeName + OPEN_PARENTHESIS +
                        CLOSE_PARENTHESIS + SPACE + NOT + EQUAL + SPACE +
                        attributeName + OPEN_PARENTHESIS +
                        CLOSE_PARENTHESIS;
    }

    private static String attrTypeForFcmWhenNonPrimitiveDataTypes(String name) {
        /*
         * appInstance.name() == null || name().equals(appInstance.name())
         */
        return getEqualEqualString(StringGenerator.getAppInstanceAttrString(name), NULL) +
                SPACE + OR_OPERATION + SPACE + NOT + OPEN_PARENTHESIS +
                getTwoParaEqualsString(name + OPEN_CLOSE_BRACKET_STRING,
                                       StringGenerator.getAppInstanceAttrString(name))
                + CLOSE_PARENTHESIS;
    }

    private static String getIfFilterContentMatchMethodImpl(
            String name, YangType dataType) {
        String attrQualifiedType;
        if (dataType.getDataType().isPrimitiveDataType()) {
            attrQualifiedType = getAttrTypeForFcmWhenPrimitiveDataType(name);
        } else if (dataType.getDataType() == LEAFREF) {

            // When leafref in grouping.
            if (((YangLeafRef) dataType.getDataTypeExtendedInfo())
                    .isInGrouping()) {
                attrQualifiedType = attrTypeForFcmWhenNonPrimitiveDataTypes(name);
            } else {
                YangType type = ((YangLeafRef) dataType.getDataTypeExtendedInfo())
                        .getEffectiveDataType();
                if (type.getDataType().isPrimitiveDataType()) {
                    attrQualifiedType = getAttrTypeForFcmWhenPrimitiveDataType(name);
                } else {
                    attrQualifiedType = attrTypeForFcmWhenNonPrimitiveDataTypes(
                            name);
                }
            }
        } else {
            attrQualifiedType = attrTypeForFcmWhenNonPrimitiveDataTypes(name);
        }
        return attrQualifiedType;
    }

    /**
     * Returns is filter content match for leaf.
     *
     * @param javaAttributeInfo java attribute
     * @return is filter content match for leaf
     */
    public static String getSubtreeFilteringForLeafList(
            JavaAttributeInfo javaAttributeInfo) {
        return getSubtreeFilteringForList(javaAttributeInfo, true, null);
    }

    /**
     * Returns is filter content match for child node.
     *
     * @param curNode      current node
     * @param pluginConfig plugin configurations
     * @param path         path of temp file
     * @return is filter content match for child node
     */
    static String getProcessChildNodeSubtreeFiltering(YangNode curNode,
                                                      YangPluginConfig
                                                              pluginConfig,
                                                      String path)
            throws IOException {
        JavaFileInfoTranslator javaFileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();
        String instance = APP_INSTANCE;
        String name = getCapitalCase(javaFileInfo.getJavaName());
        String builderNamePrefix = getCapitalCase(javaFileInfo.getJavaName());

        Map<String, String> param = new HashMap<>();
        param.put(PROCESS_CHILD_NODE_STF_PARAM, BOOLEAN_DATA_TYPE);
        param.put(instance, name);
        param.put(STF_BUILDER_PARAM, builderNamePrefix + BUILDER);
        param.put(SELECT_OR_CONTAINMENT_NODE_PARAM, BOOLEAN_WRAPPER);
        param.put(SELECT_ALL_CHILD_SCHEMA_PARAM, BOOLEAN_WRAPPER);


        String method = FOUR_SPACE_INDENTATION + PRIVATE + SPACE + BOOLEAN_DATA_TYPE +
                SPACE + PROCESS_CHILD_NODE_STF_PARAM +
                OPEN_PARENTHESIS + name + SPACE + instance + COMMA +
                SPACE + builderNamePrefix + BUILDER + SPACE +
                STF_BUILDER_PARAM + COMMA +
                " Boolean " +
                "isAnySelectOrContainmentNode, " + "boolean " +
                SELECT_ALL_CHILD_SCHEMA_PARAM + CLOSE_PARENTHESIS +
                SPACE + OPEN_CURLY_BRACKET + NEW_LINE;

        method +=
                getDataFromTempFileHandle(FILTER_CONTENT_MATCH_FOR_NODES_MASK,
                                          ((TempJavaCodeFragmentFilesContainer) curNode)
                                                  .getTempJavaCodeFragmentFiles()
                                                  .getBeanTempFiles(), path);

        method +=
                EIGHT_SPACE_INDENTATION + RETURN + SPACE + TRUE + SEMI_COLON +
                        NEW_LINE + FOUR_SPACE_INDENTATION +
                        CLOSE_CURLY_BRACKET + NEW_LINE + NEW_LINE;
        YangNode child = curNode.getChild();
        while (child != null) {
            if (child instanceof YangChoice) {
                method += getChoiceInstanceForPstMethod(child, name);
            }
            child = child.getNextSibling();
        }

        return method;
    }

    /**
     * Returns is filter content match for leaf list.
     *
     * @param curNode      current node
     * @param pluginConfig plugin configurations
     * @param path         path of temp file
     * @return is filter content match for leaf list
     */
    static String getProcessLeafListSubtreeFiltering(YangNode curNode,
                                                     YangPluginConfig
                                                             pluginConfig,
                                                     String path)
            throws IOException {
        JavaFileInfoTranslator javaFileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();
        String name = getCapitalCase(javaFileInfo.getJavaName());
        String builderNamePrefix = getCapitalCase(javaFileInfo.getJavaName());

        String processSubtreeFilteringMethod =
                FOUR_SPACE_INDENTATION + PRIVATE + SPACE + BOOLEAN_DATA_TYPE +
                        SPACE + "processLeafListSubTreeFiltering" +
                        OPEN_PARENTHESIS + name + SPACE + APP_INSTANCE + COMMA +
                        SPACE + builderNamePrefix + BUILDER + SPACE +
                        "subTreeFilteringResultBuilder" + COMMA +
                        " Boolean " +
                        "isAnySelectOrContainmentNode, " + "boolean " +
                        "isSelectAllSchemaChild" + CLOSE_PARENTHESIS +
                        SPACE + OPEN_CURLY_BRACKET + NEW_LINE;

        processSubtreeFilteringMethod += getDataFromTempFileHandle(
                FILTER_CONTENT_MATCH_FOR_LEAF_LIST_MASK,
                ((TempJavaCodeFragmentFilesContainer) curNode)
                        .getTempJavaCodeFragmentFiles()
                        .getBeanTempFiles(), path);

        processSubtreeFilteringMethod +=
                EIGHT_SPACE_INDENTATION + RETURN + SPACE + TRUE + SEMI_COLON +
                        NEW_LINE + FOUR_SPACE_INDENTATION +
                        CLOSE_CURLY_BRACKET + NEW_LINE + NEW_LINE;

        return processSubtreeFilteringMethod;
    }

    /**
     * Returns is filter content match for leaf.
     *
     * @param curNode      current node
     * @param pluginConfig plugin configurations
     * @param path         path of temp file
     * @return is filter content match for leaf
     */
    static String getProcessLeafSubtreeFiltering(YangNode curNode,
                                                 YangPluginConfig pluginConfig,
                                                 String path)
            throws IOException {
        JavaFileInfoTranslator javaFileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();
        String name = getCapitalCase(javaFileInfo.getJavaName());
        String builderNamePrefix = getCapitalCase(javaFileInfo.getJavaName());

        String processSubtreeFilteringMethod =
                FOUR_SPACE_INDENTATION + PRIVATE + SPACE + BOOLEAN_DATA_TYPE +
                        SPACE + "processLeafSubtreeFiltering" +
                        OPEN_PARENTHESIS + name + SPACE + APP_INSTANCE + COMMA +
                        SPACE + builderNamePrefix + BUILDER + SPACE +
                        "subTreeFilteringResultBuilder" + COMMA +
                        " Boolean " +
                        "isAnySelectOrContainmentNode, " + "boolean " +
                        "isSelectAllSchemaChild" + CLOSE_PARENTHESIS +
                        SPACE + OPEN_CURLY_BRACKET + NEW_LINE;

        processSubtreeFilteringMethod +=
                getDataFromTempFileHandle(FILTER_CONTENT_MATCH_FOR_LEAF_MASK,
                                          ((TempJavaCodeFragmentFilesContainer) curNode)
                                                  .getTempJavaCodeFragmentFiles()
                                                  .getBeanTempFiles(), path);

        processSubtreeFilteringMethod +=
                EIGHT_SPACE_INDENTATION + RETURN + SPACE + TRUE + SEMI_COLON +
                        NEW_LINE + FOUR_SPACE_INDENTATION +
                        CLOSE_CURLY_BRACKET + NEW_LINE + NEW_LINE;

        return processSubtreeFilteringMethod;
    }

    /**
     * Returns is filter content match for leaf.
     *
     * @param curNode current node
     * @return is filter content match for leaf
     */
    static String getProcessSubtreeFilteringStart(YangNode curNode) {
        JavaFileInfoTranslator javaFileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();
        String instance = APP_INSTANCE;
        String name = getCapitalCase(javaFileInfo.getJavaName());
        String builderNamePrefix = getCapitalCase(javaFileInfo.getJavaName());
        if (curNode instanceof RpcNotificationContainer) {
            name = getCapitalCase(javaFileInfo.getJavaName()) + OP_PARAM;
        } else {
            name = DEFAULT_CAPS + name;
        }
        String javadoc = "   /**\n" +
                "     * Checks if the passed " + name +
                " maps the content match query condition.\n" +
                "     *\n" +
                "     * @param " + instance + SPACE +
                instance + SPACE + "being passed to check" +
                " for" +
                " content match\n" +
                "     * @param isSelectAllSchemaChild is select all schema child\n" +
                "     * @return match result\n" +
                "     */\n";

        String processSubtreeFilteringMethod =
                javadoc + FOUR_SPACE_INDENTATION +
                        PUBLIC + SPACE + name + SPACE +
                        PROCESS_SUBTREE_FILTERING + OPEN_PARENTHESIS + name +
                        SPACE + instance + COMMA + SPACE + BOOLEAN_DATA_TYPE +
                        SPACE + "isSelectAllSchemaChild" +
                        CLOSE_PARENTHESIS + SPACE + OPEN_CURLY_BRACKET +
                        NEW_LINE + EIGHT_SPACE_INDENTATION +
                        builderNamePrefix + BUILDER + SPACE +
                        SUBTREE_FILTERING_RESULT_BUILDER + SPACE + EQUAL +
                        SPACE + NEW + SPACE + builderNamePrefix + BUILDER +
                        OPEN_PARENTHESIS + CLOSE_PARENTHESIS + SEMI_COLON +
                        NEW_LINE + EIGHT_SPACE_INDENTATION + "Boolean" + SPACE +
                        IS_ANY_SELECT_OR_CONTAINMENT_NODE_FLAG + SPACE +
                        EQUAL + SPACE + FALSE + SEMI_COLON + NEW_LINE;
        return processSubtreeFilteringMethod;
    }

    /**
     * Get the body for process subtree filtering.
     *
     * @param curNode node for which the code is being generated
     * @return body of subtree filtering
     */
    static String getProcessSubtreeFunctionBody(YangNode curNode) {

        String method = "";

        if (curNode instanceof YangLeavesHolder) {
            if (((YangLeavesHolder) curNode).getListOfLeaf() != null
                    &&
                    !((YangLeavesHolder) curNode).getListOfLeaf().isEmpty()) {
                method +=
                        getIfConditionBegin(EIGHT_SPACE_INDENTATION, NOT +
                                "processLeafSubtreeFiltering(appInstance, " +
                                "subTreeFilteringResultBuilder, " +
                                "isAnySelectOrContainmentNode, " +
                                "isSelectAllSchemaChild)");

                method += TWELVE_SPACE_INDENTATION + RETURN + SPACE + NULL +
                        SEMI_COLON + NEW_LINE;

                method += methodClose(EIGHT_SPACE);
            }
        }

        if (curNode instanceof YangLeavesHolder) {
            if (((YangLeavesHolder) curNode).getListOfLeafList() != null
                    &&
                    !((YangLeavesHolder) curNode).getListOfLeafList()
                            .isEmpty()) {
                method +=
                        getIfConditionBegin(EIGHT_SPACE_INDENTATION, NOT +
                                "processLeafListSubTreeFiltering(appInstance," +
                                " subTreeFilteringResultBuilder, " +
                                "isAnySelectOrContainmentNode, " +
                                "isSelectAllSchemaChild)");

                method += TWELVE_SPACE_INDENTATION + RETURN + SPACE + NULL +
                        SEMI_COLON + NEW_LINE;

                method += methodClose(EIGHT_SPACE);
            }
        }

        if (curNode.getChild() != null) {

            method +=
                    getIfConditionBegin(EIGHT_SPACE_INDENTATION, NOT +
                            "processChildNodesSubTreeFiltering(appInstance, " +
                            "subTreeFilteringResultBuilder, " +
                            "isAnySelectOrContainmentNode, " +
                            "isSelectAllSchemaChild)");

            method += TWELVE_SPACE_INDENTATION + RETURN + SPACE + NULL +
                    SEMI_COLON + NEW_LINE;

            method += methodClose(EIGHT_SPACE);
        }

        return method;
    }

    /**
     * Returns is filter content match for node.
     *
     * @param attr attribute info
     * @param node YANG node
     * @return is filter content match for node
     */
    public static String getSubtreeFilteringForNode(JavaAttributeInfo attr,
                                                    YangNode node) {
        boolean isList = attr.isListAttr();
        if (isList) {
            return getSubtreeFilteringForList(attr, false, node);
        } else {
            return getSubtreeFilteringForChildNode(attr, node);
        }
    }

    /**
     * Returns is filter content match close.
     *
     * @param name    name of class
     * @param curNode current node
     * @return is filter content match close
     */
    static String getProcessSubTreeFilteringEnd(String name, YangNode curNode) {
        String method = getIfConditionBegin(EIGHT_SPACE_INDENTATION,
                                            NOT + IS_SELECT_ALL_SCHEMA_CHILD_FLAG +
                                                    SPACE + AND_OPERATION + SPACE +
                                                    NOT +
                                                    IS_ANY_SELECT_OR_CONTAINMENT_NODE_FLAG);

        method += TWELVE_SPACE_INDENTATION + RETURN + SPACE +
                PROCESS_SUBTREE_FILTERING + OPEN_PARENTHESIS + APP_INSTANCE +
                COMMA + SPACE + TRUE + CLOSE_PARENTHESIS + SEMI_COLON +
                NEW_LINE;

        method += methodClose(EIGHT_SPACE);
        String build = BUILD;
        if (curNode instanceof YangAugment) {
            build = BUILD_FOR_FILTER;
        }
        method += EIGHT_SPACE_INDENTATION + RETURN + SPACE + getOpenCloseParaWithValue(
                name) + SPACE +
                SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + build +
                OPEN_CLOSE_BRACKET_STRING + SEMI_COLON + NEW_LINE +
                FOUR_SPACE_INDENTATION + CLOSE_CURLY_BRACKET + NEW_LINE;

        return method;
    }

    /**
     * Returns filter content match for child nodes.
     *
     * @param javaAttributeInfo attribute to be added
     * @param node              YANG node
     * @return filter content match for child nodes
     */
    private static String getSubtreeFilteringForChildNode(
            JavaAttributeInfo javaAttributeInfo, YangNode node) {
        String name = javaAttributeInfo.getAttributeName();
        String clsInfo = javaAttributeInfo.getImportInfo()
                .getClassInfo();
        String type = DEFAULT_CAPS + javaAttributeInfo.getImportInfo()
                .getClassInfo();
        if (javaAttributeInfo.isQualifiedName()) {
            type = javaAttributeInfo.getImportInfo().getPkgInfo() + PERIOD +
                    type;
            clsInfo = javaAttributeInfo.getImportInfo().getPkgInfo() + PERIOD +
                    clsInfo;
        }
        String classCast = getOpenCloseParaWithValue(type) + SPACE;
        String cast = getOpenCloseParaWithValue(classCast + name);
        String resultString = cast +
                PERIOD + PROCESS_SUBTREE_FILTERING + OPEN_PARENTHESIS
                + classCast +
                APP_INSTANCE + PERIOD + name + OPEN_CLOSE_BRACKET_STRING
                + COMMA + SPACE + FALSE + CLOSE_PARENTHESIS + SEMI_COLON +
                NEW_LINE;

        if (node != null && node instanceof YangChoice) {
            resultString = getReturnStringInCaseOfChoice(node);
        }
        String method =
                getIfConditionBegin(EIGHT_SPACE_INDENTATION, name + "() != " +
                        "null  || isSelectAllSchemaChild");

        method += TWELVE_SPACE_INDENTATION +
                IS_ANY_SELECT_OR_CONTAINMENT_NODE_FLAG + SPACE + EQUAL + SPACE +
                TRUE + SEMI_COLON + NEW_LINE;

        method += TWELVE_SPACE_INDENTATION + IF + SPACE + OPEN_PARENTHESIS +
                APP_INSTANCE + PERIOD + name + OPEN_PARENTHESIS +
                CLOSE_PARENTHESIS + SPACE + NOT
                + EQUAL + SPACE + NULL + CLOSE_PARENTHESIS + SPACE +
                OPEN_CURLY_BRACKET + NEW_LINE;

        method += SIXTEEN_SPACE_INDENTATION + clsInfo + SPACE + "result" +
                SEMI_COLON + NEW_LINE;

        method +=
                getIfConditionBegin(SIXTEEN_SPACE_INDENTATION,
                                    "isSelectAllSchemaChild");

        method += TWENTY_SPACE_INDENTATION + "result" + SPACE + EQUAL + SPACE +
                APP_INSTANCE + PERIOD + name + OPEN_PARENTHESIS +
                CLOSE_PARENTHESIS + SEMI_COLON + NEW_LINE;

        method += SIXTEEN_SPACE_INDENTATION + CLOSE_CURLY_BRACKET + SPACE +
                ELSE + SPACE +
                OPEN_CURLY_BRACKET + NEW_LINE;

        method += TWENTY_SPACE_INDENTATION + "result = " + resultString;

        method += SIXTEEN_SPACE_INDENTATION + CLOSE_CURLY_BRACKET + NEW_LINE;

        method += SIXTEEN_SPACE_INDENTATION + "if (result != null) {" +
                NEW_LINE;

        method += TWENTY_SPACE_INDENTATION + SUBTREE_FILTERING_RESULT_BUILDER +
                PERIOD + name + OPEN_PARENTHESIS + "result" +
                CLOSE_PARENTHESIS + SEMI_COLON + NEW_LINE +
                SIXTEEN_SPACE_INDENTATION + CLOSE_CURLY_BRACKET + NEW_LINE;

        //if app instance is not null
        method += methodClose(TWELVE_SPACE);

        //if query instance is not null
        method += methodClose(TWELVE_SPACE);

        return method;
    }

    /**
     * Returns filter content match for list types.
     *
     * @param javaAttributeInfo attribute information
     * @param isLeafList        if for leaf list
     * @param node              YANG node
     * @return filter content match for list types
     */
    private static String getSubtreeFilteringForList(
            JavaAttributeInfo javaAttributeInfo, boolean isLeafList,
            YangNode node) {
        String capitalCaseName =
                getCapitalCase(javaAttributeInfo.getAttributeName());
        String name = javaAttributeInfo.getAttributeName();
        String type = javaAttributeInfo.getImportInfo()
                .getClassInfo();
        String clsInfo = DEFAULT_CAPS + type;
        if (javaAttributeInfo.isQualifiedName()) {
            type = javaAttributeInfo.getImportInfo().getPkgInfo() + PERIOD +
                    type;
            clsInfo = javaAttributeInfo.getImportInfo().getPkgInfo() + PERIOD +
                    clsInfo;
        }

        String classCast = getOpenCloseParaWithValue(clsInfo) + SPACE;
        String cast = getOpenCloseParaWithValue(classCast + name);
        String resultString = cast + PERIOD +
                PROCESS_SUBTREE_FILTERING + OPEN_PARENTHESIS + classCast +
                name + "2" + COMMA + SPACE + FALSE + CLOSE_PARENTHESIS + SEMI_COLON +
                NEW_LINE;

        if (node != null && node instanceof YangChoice) {
            resultString = getReturnStringInCaseOfChoice(node);
        }

        /*
         * If select all schema child
         */
        String method =
                getIfConditionBegin(EIGHT_SPACE_INDENTATION,
                                    IS_SELECT_ALL_SCHEMA_CHILD_FLAG);

        method = method + getCollectionIteratorForLoopBegin(TWELVE_SPACE_INDENTATION,
                                                            type + SPACE + name,
                                                            APP_INSTANCE + PERIOD +
                                                                    name +
                                                                    OPEN_CLOSE_BRACKET_STRING);

        method = method + SIXTEEN_SPACE_INDENTATION +
                SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + ADD_STRING +
                getCapitalCase(TO) + capitalCaseName + OPEN_PARENTHESIS +
                name + CLOSE_PARENTHESIS + SEMI_COLON + NEW_LINE;

        method += methodClose(TWELVE_SPACE); // Close collection Iteration loop

        //If need to explicitly participate in query
        method += getElseIfConditionBegin(EIGHT_SPACE_INDENTATION,
                                          name + OPEN_CLOSE_BRACKET_STRING +
                                                  SPACE + NOT + EQUAL +
                                                  SPACE + NULL);

        if (!isLeafList) {
            method += TWELVE_SPACE_INDENTATION +
                    IS_ANY_SELECT_OR_CONTAINMENT_NODE_FLAG + SPACE + EQUAL +
                    SPACE + TRUE + SEMI_COLON + NEW_LINE;
        }

        //If there is any parameter in the query condition
        method +=
                getIfConditionBegin(TWELVE_SPACE_INDENTATION, NOT + name +
                        OPEN_CLOSE_BRACKET_STRING + PERIOD + IS_EMPTY);

        if (isLeafList) {
            /*
             * If there is no app instance to perform content match
             */
            method +=
                    getIfConditionBegin(SIXTEEN_SPACE_INDENTATION,
                                        APP_INSTANCE + PERIOD + name +
                                                OPEN_CLOSE_BRACKET_STRING +
                                                SPACE + EQUAL + EQUAL + SPACE +
                                                NULL + SPACE + OR_OPERATION
                                                + SPACE + APP_INSTANCE +
                                                PERIOD + name +
                                                OPEN_CLOSE_BRACKET_STRING +
                                                PERIOD + IS_EMPTY);

            method += TWENTY_SPACE_INDENTATION + RETURN + SPACE + FALSE +
                    SEMI_COLON + NEW_LINE;

            method += methodClose(SIXTEEN_SPACE);

            // for instance iterator
            method += getCollectionIteratorForLoopBegin(
                    SIXTEEN_SPACE_INDENTATION, type + SPACE + name,
                    name + OPEN_CLOSE_BRACKET_STRING);

            method += TWENTY_SPACE_INDENTATION + BOOLEAN_DATA_TYPE + SPACE +
                    "flag" + SPACE + EQUAL + SPACE + FALSE + SEMI_COLON +
                    NEW_LINE;

            // for app instance iterator
            method +=
                    getCollectionIteratorForLoopBegin(TWENTY_SPACE_INDENTATION,
                                                      type + SPACE + name +
                                                              "2",
                                                      APP_INSTANCE + PERIOD +
                                                              name +
                                                              OPEN_CLOSE_BRACKET_STRING);

            //the content match leaf list attribute value matches
            method +=
                    getIfConditionBegin(TWENTY_FOUR_SPACE_INDENTATION,
                                        name + PERIOD + EQUALS_STRING
                                                + OPEN_PARENTHESIS + name +
                                                "2" + CLOSE_PARENTHESIS);

            method += TWENTY_EIGHT_SPACE_INDENTATION + "flag" + SPACE + EQUAL +
                    SPACE + TRUE + SEMI_COLON + NEW_LINE;

            method += TWENTY_EIGHT_SPACE_INDENTATION +
                    SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + ADD_STRING +
                    getCapitalCase(TO) + capitalCaseName + OPEN_PARENTHESIS +
                    name + "2" + CLOSE_PARENTHESIS +
                    SEMI_COLON + NEW_LINE + TWENTY_EIGHT_SPACE_INDENTATION +
                    BREAK + SEMI_COLON + NEW_LINE;

            //the content match leaf list attribute value matches
            method += methodClose(TWENTY_FOUR_SPACE);

            // for app instance iterator
            method += methodClose(TWENTY_SPACE);

            //if the content match failed
            method +=
                    getIfConditionBegin(TWENTY_SPACE_INDENTATION, "!flag");

            method += TWENTY_FOUR_SPACE_INDENTATION + RETURN + SPACE + FALSE +
                    SEMI_COLON + NEW_LINE;

            method +=
                    methodClose(TWENTY_SPACE); // if flag == false

            method += methodClose(SIXTEEN_SPACE); // for instance iterator
        } else {

            /*if there is any app instance entry*/
            method +=
                    getIfConditionBegin(SIXTEEN_SPACE_INDENTATION,
                                        APP_INSTANCE + PERIOD + name +
                                                OPEN_CLOSE_BRACKET_STRING +
                                                SPACE + NOT + EQUAL + SPACE +
                                                NULL + SPACE + AND_OPERATION +
                                                SPACE + NOT + APP_INSTANCE +
                                                PERIOD + name +
                                                OPEN_CLOSE_BRACKET_STRING +
                                                PERIOD + IS_EMPTY);

            /*
             * loop all the query condition instance(s)
             */
            method +=
                    getCollectionIteratorForLoopBegin(TWENTY_SPACE_INDENTATION,
                                                      type + SPACE + name,
                                                      name +
                                                              OPEN_CLOSE_BRACKET_STRING);

            //loop all the app instance(s)
            method += getCollectionIteratorForLoopBegin(
                    TWENTY_FOUR_SPACE_INDENTATION, type + SPACE + name + "2",
                    APP_INSTANCE + PERIOD + name +
                            OPEN_CLOSE_BRACKET_STRING);

            method += TWENTY_EIGHT_SPACE_INDENTATION + type + SPACE +
                    "result = " + resultString;

            method += TWENTY_EIGHT_SPACE_INDENTATION + "if (result != null) {" +
                    NEW_LINE;

            method += THIRTY_TWO_SPACE_INDENTATION +
                    SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + ADD_STRING +
                    getCapitalCase(TO) + capitalCaseName + OPEN_PARENTHESIS +
                    "result" + CLOSE_PARENTHESIS +
                    SEMI_COLON + NEW_LINE + TWENTY_EIGHT_SPACE_INDENTATION +
                    CLOSE_CURLY_BRACKET + NEW_LINE;

            //loop all the app instance(s)
            method +=
                    methodClose(TWENTY_FOUR_SPACE);

            //loop all the query condition instance(s)
            method += methodClose(TWENTY_SPACE);

            //if there is any app instance entry
            method += methodClose(SIXTEEN_SPACE);
        }

        method += TWELVE_SPACE_INDENTATION + "} else {" + NEW_LINE;

        if (isLeafList) {
            method += SIXTEEN_SPACE_INDENTATION +
                    IS_ANY_SELECT_OR_CONTAINMENT_NODE_FLAG + SPACE +
                    EQUAL + SPACE + TRUE + SEMI_COLON + NEW_LINE;
        }

        method +=
                getIfConditionBegin(SIXTEEN_SPACE_INDENTATION,
                                    APP_INSTANCE + PERIOD + name
                                            + OPEN_CLOSE_BRACKET_STRING +
                                            SPACE + NOT + EQUAL + SPACE +
                                            NULL + SPACE + AND_OPERATION +
                                            SPACE + NOT + APP_INSTANCE +
                                            PERIOD + name +
                                            OPEN_CLOSE_BRACKET_STRING +
                                            PERIOD + IS_EMPTY);

        method = method +
                getCollectionIteratorForLoopBegin(TWENTY_SPACE_INDENTATION,
                                                  type + SPACE + name,
                                                  APP_INSTANCE + PERIOD +
                                                          name +
                                                          OPEN_CLOSE_BRACKET_STRING);

        method = method + TWENTY_FOUR_SPACE_INDENTATION +
                SUBTREE_FILTERING_RESULT_BUILDER + PERIOD + ADD_STRING
                + getCapitalCase(TO) + capitalCaseName + OPEN_PARENTHESIS +
                name + CLOSE_PARENTHESIS + SEMI_COLON + NEW_LINE;

        method += methodClose(TWENTY_SPACE);// Close collection Iteration loop

        method +=
                methodClose(SIXTEEN_SPACE); // close  if condition

        method +=
                methodClose(TWELVE_SPACE); // close  else condition

        method += methodClose(EIGHT_SPACE); // close  else if condition

        return method;
    }

    //Returns method string for op params augmented syntax
    static String getAugmentableSubTreeFiltering() {
        return "        for (Object augmentInfo : this.yangAugmentedInfoMap()" +
                ".values()) {\n" +
                "            Object appInstanceInfo = appInstance.yangAugmentedInfo(" +
                "augmentInfo.getClass());\n" +
                "            if (appInstanceInfo == null) {\n" +
                "                subTreeFilteringResultBuilder.addYangAugmentedInfo(" +
                "augmentInfo, augmentInfo.getClass());\n" +
                "            } else {\n" +
                "                Object processSubtreeFiltering;\n" +
                "                try {\n" +
                "                    Class<?> augmentedClass = augmentInfo" +
                ".getClass();\n" +
                "                    processSubtreeFiltering = augmentInfo.getClass()" +
                ".getMethod(\"processSubtreeFiltering\", augmentedClass).invoke(" +
                "augmentInfo, appInstanceInfo);\n" +
                "                    if (processSubtreeFiltering != null) {\n" +
                "                        subTreeFilteringResultBuilder" +
                ".addYangAugmentedInfo(processSubtreeFiltering, processSubtreeFiltering.getClass());\n" +
                "                    }\n" +
                "                } catch (NoSuchMethodException |" +
                " InvocationTargetException | IllegalAccessException e) {\n" +
                "                    continue;\n" +
                "                }\n" +
                "            }\n" +
                "        }\n";
    }

    private static String getMethodBodyForChoicePstMethod(
            YangNode node, YangPluginConfig config, YangNode choiceParent,
            String choice) {
        StringBuilder builder = new StringBuilder();
        JavaCodeGeneratorInfo info = (JavaCodeGeneratorInfo) choiceParent;
        JavaFileInfoTranslator pInfo = info.getJavaFileInfo();

        JavaQualifiedTypeInfoTranslator qInfo = getQualifiedInfo(node, config);

        String castVar = qInfo.getClassInfo();
        boolean qualify = info.getTempJavaCodeFragmentFiles().getBeanTempFiles()
                .getJavaImportData().addImportInfo(qInfo, pInfo.getJavaName(),
                                                   pInfo.getPackage());
        if (qualify) {
            castVar = StringGenerator.getQualifiedString(qInfo.getPkgInfo(),
                                                         qInfo.getClassInfo());
        }
        String classCast = getOpenCloseParaWithValue(castVar) + SPACE;
        String cast = getOpenCloseParaWithValue(classCast + choice);
        String retString = cast + PERIOD + PROCESS_SUBTREE_FILTERING +
                getOpenCloseParaWithValue(classCast + getAppInstanceAttrString
                        (choice) + COMMA + SPACE + FALSE);
        String cond = choice + INSTANCE_OF + castVar;
        builder.append(getIfConditionBegin(EIGHT_SPACE_INDENTATION, cond))
                .append(getReturnString(retString, TWELVE_SPACE_INDENTATION))
                .append(signatureClose()).append(methodClose(EIGHT_SPACE));
        return builder.toString();
    }

    /**
     * Returns choice instance for PST method.
     *
     * @param choice    choice node
     * @param className class name
     * @return choice instance for pst method
     */
    private static String getChoiceInstanceForPstMethod(YangNode choice,
                                                        String className) {
        /*
         * private Choice1 getChoice1ResultOfProcessSubTree(Choice1 choice1, Test appInstance) {
         *     if (choice1 instanceof DefaultCase1) {
         *         return ((DefaultCase1) choice1).processSubtreeFiltering(
         *          appInstance.choice1(), false);
         * }
         * return null;
         * }
         */

        JavaFileInfoTranslator info = ((JavaFileInfoContainer) choice).getJavaFileInfo();
        String name = info.getJavaName();
        String caps = getCapitalCase(name);
        StringBuilder builder = new StringBuilder();
        String methodName = caps + CHOICE_STF_METHOD_NAME;
        Map<String, String> param = new LinkedHashMap<>();
        param.put(name, caps);
        param.put(APP_INSTANCE, className);

        builder.append(multiAttrMethodSignature(methodName, GET, PRIVATE, caps,
                                                param, CLASS_TYPE));

        for (YangNode cases : getChoiceChildNodes((YangChoice) choice)) {
            builder.append(getMethodBodyForChoicePstMethod(cases, info.getPluginConfig(),
                                                           choice.getParent(), name));
        }
        builder.append(getReturnString(NULL, FOUR_SPACE_INDENTATION))
                .append(signatureClose()).append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    private static String getReturnStringInCaseOfChoice(YangNode choice) {
        JavaFileInfoTranslator info = ((JavaFileInfoContainer) choice)
                .getJavaFileInfo();
        String name = info.getJavaName();
        String caps = getCapitalCase(name);
        String methodName = caps + CHOICE_STF_METHOD_NAME;
        return GET + methodName + getOpenCloseParaWithValue(
                name + COMMA + SPACE + APP_INSTANCE) + signatureClose();
    }

    static JavaQualifiedTypeInfoTranslator getQualifiedInfo(
            YangNode node, YangPluginConfig config) {
        JavaFileInfoTranslator fileInfo = ((JavaCodeGeneratorInfo) node)
                .getJavaFileInfo();
        String name = fileInfo.getJavaName();
        String pkg = fileInfo.getPackage();
        if (config == null) {
            config = new YangPluginConfig();
        }
        if (name == null) {
            name = getCamelCase(node.getName(), config.getConflictResolver());
            pkg = getNodesPackage(node, config);
        }

        name = DEFAULT_CAPS + getCapitalCase(name);
        JavaQualifiedTypeInfoTranslator qInfo = new
                JavaQualifiedTypeInfoTranslator();
        qInfo.setClassInfo(name);
        qInfo.setPkgInfo(pkg);
        return qInfo;
    }

}
