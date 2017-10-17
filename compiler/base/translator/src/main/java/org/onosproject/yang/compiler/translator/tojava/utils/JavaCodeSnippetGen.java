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

package org.onosproject.yang.compiler.translator.tojava.utils;

import org.onosproject.yang.compiler.datamodel.YangCompilerAnnotation;
import org.onosproject.yang.compiler.datamodel.YangDataStructure;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaQualifiedTypeInfoTranslator;
import org.onosproject.yang.compiler.translator.tojava.TempJavaServiceFragmentFiles;
import org.onosproject.yang.compiler.utils.UtilConstants.Operation;

import java.util.List;

import static java.util.Collections.sort;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getYangDataStructure;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaIdentifierSyntax.getEnumJavaAttribute;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getDefaultDefinition;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getImportString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getOpenCloseParaWithValue;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.signatureClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorUtils.getEnumYangName;
import static org.onosproject.yang.compiler.utils.UtilConstants.BIT_SET;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMA;
import static org.onosproject.yang.compiler.utils.UtilConstants.DIAMOND_CLOSE_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.DIAMOND_OPEN_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.ENUM;
import static org.onosproject.yang.compiler.utils.UtilConstants.EQUAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.FOUR_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT_MAX_RANGE_ATTR;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT_MIN_RANGE_ATTR;
import static org.onosproject.yang.compiler.utils.UtilConstants.KEYS;
import static org.onosproject.yang.compiler.utils.UtilConstants.LIST;
import static org.onosproject.yang.compiler.utils.UtilConstants.LONG_MAX_RANGE_ATTR;
import static org.onosproject.yang.compiler.utils.UtilConstants.LONG_MIN_RANGE_ATTR;
import static org.onosproject.yang.compiler.utils.UtilConstants.MAP;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_CLOSE_BRACKET_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.PRIVATE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PUBLIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUEUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUOTES;
import static org.onosproject.yang.compiler.utils.UtilConstants.SCHEMA_NAME;
import static org.onosproject.yang.compiler.utils.UtilConstants.SEMI_COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.SET;
import static org.onosproject.yang.compiler.utils.UtilConstants.SET_VALUE_PARA;
import static org.onosproject.yang.compiler.utils.UtilConstants.SHORT_MAX_RANGE_ATTR;
import static org.onosproject.yang.compiler.utils.UtilConstants.SHORT_MIN_RANGE_ATTR;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.STRING_DATA_TYPE;
import static org.onosproject.yang.compiler.utils.UtilConstants.TYPE;
import static org.onosproject.yang.compiler.utils.UtilConstants.UINT8_MAX_RANGE_ATTR;
import static org.onosproject.yang.compiler.utils.UtilConstants.UINT8_MIN_RANGE_ATTR;
import static org.onosproject.yang.compiler.utils.UtilConstants.UINT_MAX_RANGE_ATTR;
import static org.onosproject.yang.compiler.utils.UtilConstants.UINT_MIN_RANGE_ATTR;
import static org.onosproject.yang.compiler.utils.UtilConstants.ULONG_MAX_RANGE_ATTR;
import static org.onosproject.yang.compiler.utils.UtilConstants.ULONG_MIN_RANGE_ATTR;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE_LEAF;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.ENUM_ATTRIBUTE;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.enumJavaDocForInnerClass;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavaDoc;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.trimAtLast;

/**
 * Represents utility class to generate the java snippet.
 */
public final class JavaCodeSnippetGen {

    // No instantiation.
    private JavaCodeSnippetGen() {
    }

    /**
     * Returns the java file header comment.
     *
     * @return the java file header comment
     */
    public static String getFileHeaderComment() {

        /*
         * TODO return the file header.
         */
        return null;
    }

    /**
     * Returns the textual java code information corresponding to the import
     * list.
     *
     * @param importInfo import info
     * @return the textual java code information corresponding to the import
     * list
     */
    static String getImportText(JavaQualifiedTypeInfoTranslator importInfo) {
        return getImportString(importInfo.getPkgInfo(), importInfo
                .getClassInfo());
    }

    /**
     * Returns the textual java code for attribute definition in class.
     *
     * @param typePkg    Package of the attribute type
     * @param attrType   java attribute type
     * @param attrName   name of the attribute
     * @param isList     is list attribute
     * @param accessType attribute access type
     * @param annotation compiler annotation
     * @return the textual java code for attribute definition in class
     */
    public static String getJavaAttributeDefinition(String typePkg,
                                                    String attrType,
                                                    String attrName,
                                                    boolean isList,
                                                    String accessType,
                                                    YangCompilerAnnotation annotation) {
        StringBuilder attrDef = new StringBuilder(FOUR_SPACE_INDENTATION);
        attrDef.append(accessType).append(SPACE);

        if (!isList) {
            if (typePkg != null) {
                attrDef.append(typePkg).append(PERIOD);
            }

            attrDef.append(attrType).append(SPACE)
                    .append(attrName);
            //Initialize select leaf/value leaf/ augment map attribute.
            if (attrName.equals(VALUE_LEAF)) {
                attrDef.append(SPACE).append(EQUAL).append(SPACE).append(NEW)
                        .append(SPACE).append(BIT_SET)
                        .append(OPEN_CLOSE_BRACKET_STRING);
            }

            attrDef.append(signatureClose());
        } else {
            StringBuilder type = new StringBuilder();
            if (typePkg != null) {
                type.append(typePkg).append(PERIOD);
            }

            type.append(attrType);

            // Add starting definition.
            addAttrStartDef(annotation, attrDef, type.toString());

            // Add ending definition.
            addAttrEndDef(attrDef, attrName);
        }
        return attrDef.toString();
    }

    /**
     * Adds starting attribute definition.
     *
     * @param annotation compiler annotation
     * @param attrDef    JAVA attribute definition
     * @param type       attr type
     */
    private static void addAttrStartDef(YangCompilerAnnotation annotation,
                                        StringBuilder attrDef, String type) {
        YangDataStructure ds = getYangDataStructure(annotation);
        if (ds != null) {
            switch (ds) {
                case QUEUE: {
                    attrDef.append(QUEUE)
                            .append(DIAMOND_OPEN_BRACKET);
                    break;
                }
                case SET: {
                    attrDef.append(SET)
                            .append(DIAMOND_OPEN_BRACKET);
                    break;
                }
                case MAP:
                    attrDef.append(MAP).append(DIAMOND_OPEN_BRACKET)
                            .append(type).append(KEYS).append(COMMA);
                    break;
                default: {
                    attrDef.append(LIST)
                            .append(DIAMOND_OPEN_BRACKET);
                }
            }
        } else {
            attrDef.append(LIST).append(DIAMOND_OPEN_BRACKET);
        }
        attrDef.append(type);
    }

    /**
     * Adds ending attribute definition.
     *
     * @param attrDef  JAVA attribute definition
     * @param attrName name of attribute
     */
    private static void addAttrEndDef(StringBuilder attrDef, String attrName) {
        attrDef.append(DIAMOND_CLOSE_BRACKET).append(SPACE)
                .append(attrName).append(signatureClose());
    }

    /**
     * Returns string for enum's attribute.
     *
     * @param name  name of attribute
     * @param value value of the enum
     * @return string for enum's attribute
     */
    public static String generateEnumAttributeString(String name, int value) {
        String enumName = getEnumJavaAttribute(name);
        return enumJavaDocForInnerClass(name) + EIGHT_SPACE_INDENTATION +
                enumName.toUpperCase() + getOpenCloseParaWithValue(
                value + EMPTY_STRING) + COMMA + NEW_LINE;
    }

    /**
     * Returns string for enum's attribute for enum class.
     *
     * @param name  name of attribute
     * @param value value of the enum
     * @return string for enum's attribute
     */
    public static String generateEnumAttributeStringWithSchemaName(
            String name, int value) {
        String enumName = getEnumJavaAttribute(name);
        String str = value + COMMA + SPACE + QUOTES + getEnumYangName(name) + QUOTES;
        return getJavaDoc(ENUM_ATTRIBUTE, name, false, null) +
                FOUR_SPACE_INDENTATION + enumName.toUpperCase() +
                getOpenCloseParaWithValue(str) + COMMA + NEW_LINE;
    }

    /**
     * Returns sorted import list.
     *
     * @param imports import list
     * @return sorted import list
     */
    public static List<String> sortImports(List<String> imports) {
        sort(imports);
        return imports;
    }

    /**
     * Returns event enum start.
     *
     * @return event enum start
     */
    static String getEventEnumTypeStart() {
        return NEW_LINE + FOUR_SPACE_INDENTATION +
                getDefaultDefinition(ENUM, TYPE, PUBLIC);
    }

    /**
     * Adds listener's imports.
     *
     * @param curNode   currentYangNode.
     * @param imports   import list
     * @param operation add or remove
     */
    public static void addListenersImport(YangNode curNode,
                                          List<String> imports,
                                          Operation operation) {
        String thisImport;
        TempJavaServiceFragmentFiles tempFiles =
                ((JavaCodeGeneratorInfo) curNode).getTempJavaCodeFragmentFiles()
                        .getServiceTempFiles();
        thisImport = tempFiles.getJavaImportData().getListenerServiceImport();
        performOperationOnImports(imports, thisImport, operation);
    }

    /**
     * Performs given operations on import list.
     *
     * @param imports   list of imports
     * @param curImport current import
     * @param operation ADD or REMOVE
     * @return import list
     */
    private static List<String> performOperationOnImports(List<String> imports,
                                                          String curImport,
                                                          Operation operation) {
        switch (operation) {
            case ADD:
                imports.add(curImport);
                break;
            case REMOVE:
                imports.remove(curImport);
                break;
            default:
                throw new TranslatorException("Invalid operation type");
        }
        sortImports(imports);
        return imports;
    }

    /**
     * Returns integer attribute for enum's class to get the values.
     *
     * @param className enum's class name
     * @return enum's attribute
     */
    static String getEnumsValueAttribute(String className) {
        return getJavaAttributeDefinition(null, INT, className,
                                          false, PRIVATE, null) +
                getJavaAttributeDefinition(null, STRING_DATA_TYPE, SCHEMA_NAME,
                                           false, PRIVATE, null) + NEW_LINE;
    }

    /**
     * Adds attribute for int ranges.
     *
     * @param modifier modifier for attribute
     * @param addFirst true if int need to be added fist.
     * @return attribute for int ranges
     */
    static String addStaticAttributeIntRange(String modifier,
                                             boolean addFirst) {
        if (addFirst) {
            return getTypeConflictAttributeStrings(modifier,
                                                   INT_MIN_RANGE_ATTR,
                                                   INT_MAX_RANGE_ATTR);
        }
        return getTypeConflictAttributeStrings(modifier,
                                               UINT_MIN_RANGE_ATTR,
                                               UINT_MAX_RANGE_ATTR);
    }

    /**
     * Adds attribute for long ranges.
     *
     * @param modifier modifier for attribute
     * @param addFirst if need to be added first
     * @return attribute for long ranges
     */
    static String addStaticAttributeLongRange(String modifier,
                                              boolean addFirst) {
        if (addFirst) {
            return getTypeConflictAttributeStrings(modifier,
                                                   LONG_MIN_RANGE_ATTR,
                                                   LONG_MAX_RANGE_ATTR);
        }
        return getTypeConflictAttributeStrings(modifier,
                                               ULONG_MIN_RANGE_ATTR,
                                               ULONG_MAX_RANGE_ATTR);
    }

    /**
     * Adds attribute for long ranges.
     *
     * @param modifier modifier for attribute
     * @param addFirst if need to be added first
     * @return attribute for long ranges
     */
    static String addStaticAttributeShortRange(String modifier,
                                               boolean addFirst) {
        if (addFirst) {
            return getTypeConflictAttributeStrings(modifier,
                                                   SHORT_MIN_RANGE_ATTR,
                                                   SHORT_MAX_RANGE_ATTR);
        }
        return getTypeConflictAttributeStrings(modifier,
                                               UINT8_MIN_RANGE_ATTR,
                                               UINT8_MAX_RANGE_ATTR);
    }

    /**
     * Returns attribute for conflicting type in union.
     *
     * @param modifier modifier
     * @param attr1    attribute one
     * @param att2     attribute two
     * @return attribute for conflicting type in union
     */
    private static String getTypeConflictAttributeStrings(String modifier,
                                                          String attr1,
                                                          String att2) {
        return FOUR_SPACE_INDENTATION + modifier + SPACE +
                attr1 + FOUR_SPACE_INDENTATION + modifier +
                SPACE + att2 + NEW_LINE;
    }

    /**
     * Returns set value parameter for union class.
     *
     * @return set value parameter for union class.
     */
    static String getSetValueParaForUnionClass() {
        String[] array = {NEW_LINE, SEMI_COLON};
        return new StringBuilder().append(trimAtLast(
                getJavaAttributeDefinition(null, BIT_SET, SET_VALUE_PARA,
                                           false, PRIVATE, null), array))
                .append(SPACE).append(EQUAL).append(SPACE).append(NEW)
                .append(SPACE).append(BIT_SET).append(OPEN_CLOSE_BRACKET_STRING)
                .append(signatureClose()).toString();
    }
}
