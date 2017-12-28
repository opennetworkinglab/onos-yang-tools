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

import org.onosproject.yang.compiler.datamodel.InvalidOpTypeHolder;
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangCompilerAnnotation;
import org.onosproject.yang.compiler.datamodel.YangDataStructure;
import org.onosproject.yang.compiler.datamodel.YangEnum;
import org.onosproject.yang.compiler.datamodel.YangEnumeration;
import org.onosproject.yang.compiler.datamodel.YangIdentity;
import org.onosproject.yang.compiler.datamodel.YangIdentityRef;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.translator.tojava.JavaAttributeInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoContainer;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.IDENTITYREF;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_EVENT_SUBJECT_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_TYPEDEF_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_UNION_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.utils.BracketType.OPEN_CLOSE_BRACKET;
import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.EIGHT_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.FOUR_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.TWELVE_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getAugmentedClassNameForDataMethods;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getCurNodeName;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getIdName;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getReturnType;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getSetOfNodeIdentifiers;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getYangDataStructure;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaIdentifierSyntax.getEnumJavaAttribute;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodBodyTypes.GETTER;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodBodyTypes.MANAGER_METHODS;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodBodyTypes.SETTER;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodBodyTypes.TO_STRING;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodClassTypes.CLASS_TYPE;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodClassTypes.INTERFACE_TYPE;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.brackets;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getBitSetAttr;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getCatchSubString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getCompareToString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getElseIfConditionBegin;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getExceptionThrowString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getForLoopString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getGreaterThanCondition;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getIfConditionBegin;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getLeafFlagSetString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getLesserThanCondition;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getListAttribute;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getNewInstance;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getNewLineAndSpace;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getOpenCloseParaWithValue;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getOverRideString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getParseFromStringMethod;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getPatternQuoteString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getQuotedString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getReturnOfSubString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getReturnString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getSetValueParaCondition;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getSetterInterfaceString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getStringBuilderAttr;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getToStringCall;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getTrySubString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getTwoParaEqualsString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.ifAndAndCondition;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.ifConditionForIntInTypeDefConstructor;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.ifEqualEqualCondition;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.methodBody;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.methodClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.methodSignature;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.methodSignatureClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.multiAttrMethodSignature;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.signatureClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.valueAssign;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorUtils.getEnumYangName;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorUtils.getIdentityRefName;
import static org.onosproject.yang.compiler.utils.UtilConstants.ADD;
import static org.onosproject.yang.compiler.utils.UtilConstants.ADD_AUGMENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.ADD_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.AND;
import static org.onosproject.yang.compiler.utils.UtilConstants.APPEND;
import static org.onosproject.yang.compiler.utils.UtilConstants.ARRAY_LIST_INIT;
import static org.onosproject.yang.compiler.utils.UtilConstants.AUGMENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.AUGMENTATIONS;
import static org.onosproject.yang.compiler.utils.UtilConstants.AUGMENTED;
import static org.onosproject.yang.compiler.utils.UtilConstants.BASE64;
import static org.onosproject.yang.compiler.utils.UtilConstants.BIG_INTEGER;
import static org.onosproject.yang.compiler.utils.UtilConstants.BITS;
import static org.onosproject.yang.compiler.utils.UtilConstants.BIT_SET;
import static org.onosproject.yang.compiler.utils.UtilConstants.BOOLEAN_DATA_TYPE;
import static org.onosproject.yang.compiler.utils.UtilConstants.CAMEL_CLASS;
import static org.onosproject.yang.compiler.utils.UtilConstants.CASE;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLASS;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLASS_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMA;
import static org.onosproject.yang.compiler.utils.UtilConstants.DECODE;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.ELSE;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.ENCODE_TO_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.ENUM;
import static org.onosproject.yang.compiler.utils.UtilConstants.EQUAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.EQUALS_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXTEND;
import static org.onosproject.yang.compiler.utils.UtilConstants.FALSE;
import static org.onosproject.yang.compiler.utils.UtilConstants.FOR;
import static org.onosproject.yang.compiler.utils.UtilConstants.FOR_TYPE_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.FOUR_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.FROM_STRING_METHOD_NAME;
import static org.onosproject.yang.compiler.utils.UtilConstants.FROM_STRING_PARAM_NAME;
import static org.onosproject.yang.compiler.utils.UtilConstants.GET;
import static org.onosproject.yang.compiler.utils.UtilConstants.GET_DECODER;
import static org.onosproject.yang.compiler.utils.UtilConstants.GET_ENCODER;
import static org.onosproject.yang.compiler.utils.UtilConstants.GET_METHOD_PREFIX;
import static org.onosproject.yang.compiler.utils.UtilConstants.HASH;
import static org.onosproject.yang.compiler.utils.UtilConstants.HASH_CODE_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.IDENTITY;
import static org.onosproject.yang.compiler.utils.UtilConstants.IF;
import static org.onosproject.yang.compiler.utils.UtilConstants.IMPLEMENTS;
import static org.onosproject.yang.compiler.utils.UtilConstants.INSTANCE_OF;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT;
import static org.onosproject.yang.compiler.utils.UtilConstants.IS_EMPTY;
import static org.onosproject.yang.compiler.utils.UtilConstants.KEYS;
import static org.onosproject.yang.compiler.utils.UtilConstants.LEAF;
import static org.onosproject.yang.compiler.utils.UtilConstants.LEAF_IDENTIFIER;
import static org.onosproject.yang.compiler.utils.UtilConstants.LEFT_ANGULAR_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.LINKED_HASH_MAP;
import static org.onosproject.yang.compiler.utils.UtilConstants.LINKED_HASH_SET;
import static org.onosproject.yang.compiler.utils.UtilConstants.LONG;
import static org.onosproject.yang.compiler.utils.UtilConstants.MAP;
import static org.onosproject.yang.compiler.utils.UtilConstants.MAX_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.MIN_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.MODEL_LEAF_IDENTIFIER;
import static org.onosproject.yang.compiler.utils.UtilConstants.MODEL_OBJECT;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_STRING_JOINER_OBJECT;
import static org.onosproject.yang.compiler.utils.UtilConstants.NOT;
import static org.onosproject.yang.compiler.utils.UtilConstants.NULL;
import static org.onosproject.yang.compiler.utils.UtilConstants.OBJ;
import static org.onosproject.yang.compiler.utils.UtilConstants.OBJECT_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.OF;
import static org.onosproject.yang.compiler.utils.UtilConstants.OF_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.ONE;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_CLOSE_BRACKET_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPERATION_TYPE_ATTRIBUTE;
import static org.onosproject.yang.compiler.utils.UtilConstants.OP_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.OTHER;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.PRIORITY_QUEUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PRIVATE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PUBLIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.PUT;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUESTION_MARK;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUOTES;
import static org.onosproject.yang.compiler.utils.UtilConstants.REMOVE_AUGMENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.RETURN;
import static org.onosproject.yang.compiler.utils.UtilConstants.RIGHT_ANGULAR_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_INPUT_VAR_NAME;
import static org.onosproject.yang.compiler.utils.UtilConstants.SCHEMA_NAME;
import static org.onosproject.yang.compiler.utils.UtilConstants.SEMI_COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.SET_METHOD_PREFIX;
import static org.onosproject.yang.compiler.utils.UtilConstants.SET_VALUE_PARA;
import static org.onosproject.yang.compiler.utils.UtilConstants.SIXTEEN_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPLIT_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.SQUARE_BRACKETS;
import static org.onosproject.yang.compiler.utils.UtilConstants.STATIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.STRING_BUILDER_VAR;
import static org.onosproject.yang.compiler.utils.UtilConstants.STRING_DATA_TYPE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SUFFIX_S;
import static org.onosproject.yang.compiler.utils.UtilConstants.SWITCH;
import static org.onosproject.yang.compiler.utils.UtilConstants.TEMPLATE_T;
import static org.onosproject.yang.compiler.utils.UtilConstants.THIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.TMP_VAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.TO_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.TO_STRING_METHOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.TRIM_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.TRUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.TWELVE_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALIDATE_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE_LEAF;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE_LEAF_SET;
import static org.onosproject.yang.compiler.utils.UtilConstants.VARIABLE_C;
import static org.onosproject.yang.compiler.utils.UtilConstants.VOID;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.CONSTRUCTOR;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.DEFAULT_CONSTRUCTOR;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.FROM_METHOD;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.GETTER_METHOD;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.MANAGER_SETTER_METHOD;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.OF_METHOD;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.SETTER_METHOD;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.TYPE_CONSTRUCTOR;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.generateForGetMethodWithAttribute;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.generateForValidatorMethod;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavaDoc;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getSmallCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.trimAtLast;

/**
 * Represents generator for methods of generated files based on the file type.
 */
public final class MethodsGenerator {
    private static final String BIT_NAMES_VAR = "bitNames";
    private static final String BIT_NAME_VAR = "bitName";

    /**
     * Creates an instance of method generator.
     */
    private MethodsGenerator() {
    }

    /**
     * Returns getter string.
     *
     * @param attr    attribute info
     * @param genType generated java files
     * @return getter string
     */
    public static String getGetterString(JavaAttributeInfo attr, int genType) {
        String returnType = getReturnType(attr);
        String attributeName = attr.getAttributeName();
        String appDataStructure = null;
        StringBuilder builder = new StringBuilder();
        if (attr.getCompilerAnnotation() != null) {
            YangDataStructure data = getYangDataStructure(
                    attr.getCompilerAnnotation());
            if (data != null) {
                appDataStructure = data.name();
            }
        }
        if (genType == GENERATE_SERVICE_AND_MANAGER) {
            builder.append(generateForGetMethodWithAttribute(returnType))
                    .append(getGetterForInterface(
                            attributeName, returnType, attr.isListAttr(),
                            genType, attr.getCompilerAnnotation()));

            return builder.toString();
        }

        builder.append(getJavaDoc(GETTER_METHOD, attributeName, attr
                .isListAttr(), appDataStructure))
                .append(getGetterForInterface(
                        attributeName, returnType, attr.isListAttr(),
                        genType, attr.getCompilerAnnotation()));

        return builder.toString();
    }

    /**
     * Returns setter string.
     *
     * @param attr      attribute info
     * @param className java class name
     * @param genType   generated java files
     * @return setter string
     */
    public static String getSetterString(JavaAttributeInfo attr,
                                         String className, int genType) {

        String attrType = getReturnType(attr);
        String attributeName = attr.getAttributeName();
        JavaDocType type;
        StringBuilder builder = new StringBuilder();
        if (genType == GENERATE_SERVICE_AND_MANAGER) {
            type = MANAGER_SETTER_METHOD;
        } else {
            type = SETTER_METHOD;
        }

        String appDataStructure = null;
        YangDataStructure ds = getYangDataStructure(
                attr.getCompilerAnnotation());
        if (ds != null) {
            appDataStructure = ds.name();
        }
        builder.append(getJavaDoc(type, attributeName, attr.isListAttr(),
                                  appDataStructure))
                .append(getSetterForInterface(attributeName, attrType, className,
                                              attr.isListAttr(), genType,
                                              attr.getCompilerAnnotation()));
        return builder.toString();
    }

    /**
     * Returns constructor method string.
     *
     * @param name class name
     * @return constructor string
     */
    private static String getConstructorString(String name) {
        return getJavaDoc(CONSTRUCTOR, name, false, null);
    }

    /**
     * Returns default constructor method string.
     *
     * @param name         class name
     * @param modifierType modifier type
     * @return default constructor string
     */
    public static String getDefaultConstructorString(String name,
                                                     String modifierType) {
        return getJavaDoc(DEFAULT_CONSTRUCTOR, name, false, null)
                + getDefaultConstructor(name, modifierType) + NEW_LINE;
    }

    /**
     * Returns add augmentation method string.
     *
     * @return add augmentation string
     */
    public static String getAddAugmentationString() {
        return getOverRideString() +
                getAddAugmentation();
    }

    /**
     * Returns remove augmentation method string.
     *
     * @return remove augmentation string
     */
    public static String getRemoveAugmentationString() {
        return getOverRideString() +
                getRemoveAugmentation();
    }

    /**
     * Returns augmentations method string.
     *
     * @return augmentations string
     */
    public static String getAugmentationsString() {
        return getOverRideString() +
                getAugmentations();
    }

    /**
     * Returns augmentation method string.
     *
     * @return augmentation string
     */
    public static String getAugmentationString() {
        return getOverRideString() +
                getAugmentation();
    }

    /**
     * Returns the getter method strings for class file.
     *
     * @param attr               attribute info
     * @param generatedJavaFiles for the type of java file being generated
     * @return getter method for class
     */
    public static String getGetterForClass(JavaAttributeInfo attr,
                                           int generatedJavaFiles) {

        String attrQualifiedType = getReturnType(attr);
        String attributeName = attr.getAttributeName();

        if (!attr.isListAttr()) {
            return getGetter(attrQualifiedType, attributeName,
                             generatedJavaFiles);
        }
        String attrParam = getListAttribute(attrQualifiedType,
                                            attr.getCompilerAnnotation());
        return getGetter(attrParam, attributeName, generatedJavaFiles);
    }

    /**
     * Returns getter for attribute.
     *
     * @param type    return type
     * @param name    attribute name
     * @param genType generated java files
     * @return getter for attribute
     */
    static String getGetter(String type, String name, int genType) {
        StringBuilder builder = new StringBuilder();
        if (genType == GENERATE_SERVICE_AND_MANAGER) {
            //Append method signature.
            builder.append(methodSignature(getCapitalCase(name), GET,
                                           PUBLIC, null, type, null,
                                           CLASS_TYPE))
                    //Append method body.
                    .append(methodBody(MANAGER_METHODS, null, null,
                                       EIGHT_SPACE_INDENTATION, null,
                                       type, false, null))
                    .append(signatureClose())
                    //Append method close.
                    .append(methodClose(FOUR_SPACE));
            return builder.toString();
        }
        builder.append(methodSignature(name, EMPTY_STRING,
                                       PUBLIC, null, type, null,
                                       CLASS_TYPE))
                //Append method body.
                .append(methodBody(GETTER, name, name,
                                   EIGHT_SPACE_INDENTATION, null,
                                   type, false, null))
                .append(signatureClose())
                //Append method close.
                .append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    /**
     * Returns the setter method strings for class file.
     *
     * @param attr               attribute info
     * @param generatedJavaFiles generated java files
     * @return setter method for class
     */
    public static String getSetterForClass(JavaAttributeInfo attr,
                                           int generatedJavaFiles) {
        String attrQualifiedType = getReturnType(attr);
        String attributeName = attr.getAttributeName();
        boolean isTypeNull = false;
        if (attr.getAttributeType() == null) {
            isTypeNull = true;
        }
        if (!attr.isListAttr()) {
            return getSetter(attributeName, attrQualifiedType,
                             generatedJavaFiles, isTypeNull);
        }
        String attrParam = getListAttribute(attrQualifiedType,
                                            attr.getCompilerAnnotation());
        return getSetter(attributeName, attrParam,
                         generatedJavaFiles, isTypeNull);
    }

    /**
     * Returns setter for attribute.
     *
     * @param name       attribute name
     * @param type       return type
     * @param isTypeNull if attribute type is null
     * @return setter for attribute
     */
    private static String getSetter(String name, String type,
                                    int genType,
                                    boolean isTypeNull) {
        StringBuilder builder = new StringBuilder();
        if (genType == GENERATE_SERVICE_AND_MANAGER) {
            //Append method signature.
            builder.append(methodSignature(getCapitalCase(name),
                                           SET_METHOD_PREFIX,
                                           PUBLIC, name, VOID, type,
                                           CLASS_TYPE))
                    //Append method body.
                    .append(methodBody(MANAGER_METHODS, null, null,
                                       EIGHT_SPACE_INDENTATION, null, null,
                                       false, null))
                    .append(methodClose(FOUR_SPACE));
            return builder.toString();
        }
        if (genType == GENERATE_EVENT_SUBJECT_CLASS) {
            builder.append(methodSignature(name, EMPTY_STRING, PUBLIC, name, VOID,
                                           type, CLASS_TYPE))

                    //Append method body.
                    .append(methodBody(SETTER, name, name,
                                       EIGHT_SPACE_INDENTATION, null, null,
                                       false, null))
                    .append(methodClose(FOUR_SPACE));
            return builder.toString();
        }
        //Append method signature.
        builder.append(methodSignature(name, EMPTY_STRING, PUBLIC, name,
                                       VOID, type, CLASS_TYPE));

        if (!isTypeNull &&
                genType != GENERATE_TYPEDEF_CLASS && genType != GENERATE_UNION_CLASS) {
            builder.append(getLeafFlagSetString(name, VALUE_LEAF, EIGHT_SPACE_INDENTATION,
                                                SET_METHOD_PREFIX)).append(signatureClose());
        } else {
            builder.append(EMPTY_STRING);
        }
        //Append method body.
        builder.append(methodBody(SETTER, name, name,
                                  EIGHT_SPACE_INDENTATION, null, null,
                                  false, null))
                //Append method close.
                .append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    /**
     * Returns the setter method strings for class file.
     *
     * @param attr attribute info
     * @return setter method for class
     */
    static String getSetterForTypeDefClass(JavaAttributeInfo attr) {

        String attrQualifiedType = getReturnType(attr);
        String attributeName = attr.getAttributeName();
        return getTypeDefSetter(attrQualifiedType, attributeName);
    }

    /**
     * Returns type def's setter for attribute.
     *
     * @param type data type
     * @param name attribute name
     * @return setter for type def's attribute
     */
    private static String getTypeDefSetter(String type, String name) {
        return methodSignature(getCapitalCase(name), SET_METHOD_PREFIX, PUBLIC,
                               name, VOID, type, CLASS_TYPE) +
                methodBody(SETTER, name, name, EIGHT_SPACE_INDENTATION, null,
                           null, false, null) + methodClose(FOUR_SPACE);
    }

    /**
     * Returns the getter method strings for interface file.
     *
     * @param yangName   name of the attribute
     * @param returnType return type of attribute
     * @param isList     is list attribute
     * @param genType    generated java files
     * @param annotation compiler annotation
     * @return getter method for interface
     */
    static String getGetterForInterface(String yangName, String returnType,
                                        boolean isList,
                                        int genType,
                                        YangCompilerAnnotation annotation) {
        if (!isList) {
            return getGetterInterfaceString(returnType, yangName,
                                            genType);
        }
        String listAttr = getListAttribute(returnType, annotation);
        return getGetterInterfaceString(listAttr, yangName, genType);
    }

    /**
     * Returns getter for attribute in interface.
     *
     * @param returnType return type
     * @param yangName   attribute name
     * @return getter for interface
     */
    private static String getGetterInterfaceString(String returnType,
                                                   String yangName,
                                                   int genType) {
        switch (genType) {
            case GENERATE_SERVICE_AND_MANAGER:
                return getGetMethodWithArgument(returnType, yangName);
            default:
                return methodSignature(yangName, EMPTY_STRING, null,
                                       null, returnType, null, INTERFACE_TYPE);
        }
    }

    /**
     * Returns the setter method strings for interface file.
     *
     * @param attrName   name of the attribute
     * @param attrType   return type of attribute
     * @param className  name of the java class being generated
     * @param isList     is list attribute
     * @param genType    generated java files
     * @param annotation compiler annotations
     * @return setter method for interface
     */
    static String getSetterForInterface(String attrName, String attrType,
                                        String className,
                                        boolean isList, int genType,
                                        YangCompilerAnnotation annotation) {
        if (!isList) {
            return getSetterInterfaceString(className, attrName, attrType,
                                            genType);
        }

        String listAttr = getListAttribute(attrType, annotation);
        return getSetterInterfaceString(className, attrName, listAttr, genType);
    }

    /**
     * Returns the constructor strings for class file.
     *
     * @param attr    attribute info
     * @param genType generated java files
     * @return constructor for class
     */
    public static String getConstructor(JavaAttributeInfo attr, int genType) {
        String attrName = attr.getAttributeName();
        String attrCaps = getCapitalCase(attrName);
        switch (genType) {
            case GENERATE_SERVICE_AND_MANAGER:
                return methodBody(MethodBodyTypes.CONSTRUCTOR, attrName,
                                  attrCaps, EIGHT_SPACE_INDENTATION, GET, null,
                                  false, null);
            default:
                return methodBody(MethodBodyTypes.DEFAULT_CONSTRUCTOR, null,
                                  attrName, EIGHT_SPACE_INDENTATION, EMPTY_STRING,
                                  null, false, null);
        }
    }

    /**
     * Returns the rpc strings for service interface.
     *
     * @param rpcName name of the rpc
     * @param input   name of input
     * @param output  name of output
     * @return rpc method string
     */
    public static String getRpcServiceMethod(String rpcName, String input,
                                             String output) {
        String inputVal = null;
        if (input != null) {
            inputVal = RPC_INPUT_VAR_NAME;
        }
        return methodSignature(rpcName, EMPTY_STRING, null,
                               inputVal, output, input, INTERFACE_TYPE) +
                NEW_LINE;
    }

    /**
     * Returns the Default constructor strings for class file.
     *
     * @param name         name of the class
     * @param modifierType modifier type for default constructor
     * @return Default constructor for class
     */
    private static String getDefaultConstructor(String name,
                                                String modifierType) {
        return methodSignature(name, EMPTY_STRING, modifierType, null,
                               null, null, CLASS_TYPE) +
                methodClose(FOUR_SPACE);
    }

    /**
     * Returns the add augmentation strings for class file.
     *
     * @return add augmentation method for class
     */
    private static String getAddAugmentation() {
        return methodSignature(ADD_AUGMENTATION, EMPTY_STRING, PUBLIC, OBJ,
                               VOID, MODEL_OBJECT, CLASS_TYPE) +
                methodClose(FOUR_SPACE);
    }

    /**
     * Returns the remove augmentation strings for class file.
     *
     * @return remove augmentation method for class
     */
    private static String getRemoveAugmentation() {
        return methodSignature(REMOVE_AUGMENTATION, EMPTY_STRING, PUBLIC,
                               OBJ, VOID, MODEL_OBJECT, CLASS_TYPE) +
                methodClose(FOUR_SPACE);
    }

    /**
     * Returns the augmentations method for class file.
     *
     * @return augmentations method for class
     */
    private static String getAugmentations() {
        String methodReturnType = MAP + LEFT_ANGULAR_BRACKET + CAMEL_CLASS +
                LEFT_ANGULAR_BRACKET + QUESTION_MARK + SPACE + EXTEND +
                SPACE + MODEL_OBJECT + RIGHT_ANGULAR_BRACKET + COMMA +
                SPACE + MODEL_OBJECT + RIGHT_ANGULAR_BRACKET;
        return methodSignature(AUGMENTATIONS, EMPTY_STRING, PUBLIC, null,
                               methodReturnType, null, CLASS_TYPE) + NEW_LINE +
                getReturnString(NULL, EIGHT_SPACE_INDENTATION) + SEMI_COLON +
                NEW_LINE + methodClose(FOUR_SPACE);
    }

    /**
     * Returns the augmentation strings for class file.
     *
     * @return augmentation method for class
     */
    private static String getAugmentation() {
        String methodReturnType = LEFT_ANGULAR_BRACKET + TEMPLATE_T + SPACE +
                EXTEND + SPACE + MODEL_OBJECT + RIGHT_ANGULAR_BRACKET + SPACE +
                TEMPLATE_T;
        String paraReturntype = CAMEL_CLASS + LEFT_ANGULAR_BRACKET + TEMPLATE_T +
                RIGHT_ANGULAR_BRACKET;
        return methodSignature(AUGMENTATION, EMPTY_STRING, PUBLIC, VARIABLE_C,
                               methodReturnType,
                               paraReturntype, CLASS_TYPE) + NEW_LINE +
                getReturnString(NULL, EIGHT_SPACE_INDENTATION) + SEMI_COLON +
                NEW_LINE + methodClose(FOUR_SPACE);
    }

    /**
     * Returns to string method's open strings.
     *
     * @return string method's open string
     */
    static String getToStringMethodOpen() {
        StringBuilder builder = new StringBuilder(getOverRideString());
        builder.append(methodSignature(TO_STRING_METHOD, EMPTY_STRING,
                                       PUBLIC, null, STRING_DATA_TYPE, null,
                                       CLASS_TYPE));
        builder.append(getReturnString(NEW_STRING_JOINER_OBJECT,
                                       EIGHT_SPACE_INDENTATION)).append(NEW_LINE);
        return builder.toString();
    }

    /**
     * Returns to string method's close string.
     *
     * @return to string method close string
     */
    static String getToStringMethodClose() {
        return TWELVE_SPACE_INDENTATION + PERIOD + TO_STRING_METHOD +
                brackets(OPEN_CLOSE_BRACKET, null, null) + signatureClose() +
                methodClose(FOUR_SPACE) + NEW_LINE;
    }

    /**
     * Returns to string method for class.
     *
     * @param attr java attribute
     * @return to string method for class
     */
    public static String getToStringMethod(JavaAttributeInfo attr) {
        return methodBody(TO_STRING, attr.getAttributeName(), null,
                          TWELVE_SPACE_INDENTATION, null, null, false, null);
    }

    /**
     * Returns from string method's open string.
     *
     * @param className name of the class
     * @return from string method's open string
     */
    static String getFromStringMethodSignature(String className) {
        return getJavaDoc(FROM_METHOD, className, false, null) +
                methodSignature(FROM_STRING_METHOD_NAME, EMPTY_STRING, PUBLIC +
                                        SPACE + STATIC, FROM_STRING_PARAM_NAME,
                                className, STRING_DATA_TYPE,
                                CLASS_TYPE);
    }

    /**
     * Return from string method's close string.
     *
     * @return from string method's close string
     */
    static String getFromStringMethodClose() {
        return methodClose(FOUR_SPACE);
    }

    /**
     * Return from string method's body string.
     *
     * @param attr     attribute info
     * @param fromAttr attribute info for the from string wrapper
     *                 type
     * @return from string method's body string
     */
    public static String getFromStringMethod(JavaAttributeInfo attr,
                                             JavaAttributeInfo fromAttr) {

        return EIGHT_SPACE_INDENTATION + getTrySubString() +
                getNewLineAndSpace(TWELVE_SPACE_INDENTATION) +
                getParsedSubString(attr, fromAttr) +
                getReturnOfSubString() + EIGHT_SPACE_INDENTATION +
                getCatchSubString(attr.getCurHolderOrCount());
    }

    /**
     * Returns sub string with parsed statement for union's from string method.
     *
     * @param attr attribute info
     * @return sub string with parsed statement for union's from string method
     */
    private static String getParsedSubString(JavaAttributeInfo attr,
                                             JavaAttributeInfo fromStringAttr) {

        String targetDataType = getReturnType(attr);
        YangDataTypes types = fromStringAttr.getAttributeType()
                .getDataType();
        StringBuilder method = new StringBuilder();
        switch (types) {
            case BITS:
                return targetDataType + SPACE + TMP_VAL + SPACE + EQUAL +
                        SPACE + getCapitalCase(attr.getAttributeName()) +
                        PERIOD + FROM_STRING_METHOD_NAME +
                        getOpenCloseParaWithValue(FROM_STRING_PARAM_NAME) +
                        signatureClose();
            case BINARY:
                return method.append(targetDataType).append(SPACE).append(TMP_VAL)
                        .append(SPACE).append(EQUAL).append(SPACE).append(
                                geStringConverterForBinary(FROM_STRING_PARAM_NAME))
                        .append(signatureClose()).toString();
            default:
                return targetDataType + SPACE + TMP_VAL + SPACE + EQUAL +
                        SPACE + getParseFromStringMethod(
                        targetDataType, fromStringAttr.getAttributeType()) +
                        getOpenCloseParaWithValue(FROM_STRING_PARAM_NAME) +
                        signatureClose();
        }
    }

    /**
     * Returns from string converter for binary type.
     *
     * @param var variable name
     * @return to string method body
     */
    private static String geStringConverterForBinary(String var) {
        StringBuilder builder = new StringBuilder();
        return builder.append(BASE64).append(PERIOD)
                .append(GET_DECODER).append(OPEN_CLOSE_BRACKET_STRING).append(PERIOD)
                .append(DECODE).append(getOpenCloseParaWithValue(var)).toString();
    }

    /**
     * Returns to string converter for binary type.
     *
     * @param var variable name
     * @return to string method body
     */
    private static String getToStringForBinary(String var) {
        StringBuilder builder = new StringBuilder();
        return builder.append(BASE64).append(PERIOD)
                .append(GET_ENCODER).append(OPEN_CLOSE_BRACKET_STRING)
                .append(PERIOD).append(ENCODE_TO_STRING)
                .append(getOpenCloseParaWithValue(var)).toString();
    }

    /**
     * Returns hash code method open strings.
     *
     * @return hash code method open string
     */
    static String getHashCodeMethodOpen() {
        String line;
        StringBuilder builder = new StringBuilder(getOverRideString());
        builder.append(methodSignature(HASH_CODE_STRING, EMPTY_STRING, PUBLIC,
                                       null, INT, null, CLASS_TYPE));
        // FIXME this can end up generating Objects.hash against arrays
        line = getReturnString(OBJECT_STRING + SUFFIX_S + PERIOD + HASH +
                                       OPEN_PARENTHESIS, EIGHT_SPACE_INDENTATION);
        builder.append(line);
        return builder.toString();
    }

    /**
     * Returns hash code methods close string.
     *
     * @param hashcodeString hash code string
     * @return to hash code method close string
     */
    static String getHashCodeMethodClose(String hashcodeString) {
        String[] array = {SPACE, COMMA};
        hashcodeString = trimAtLast(hashcodeString, array);
        return hashcodeString + CLOSE_PARENTHESIS + signatureClose() +
                methodClose(FOUR_SPACE);
    }

    /**
     * Returns hash code method for class.
     *
     * @param attr attribute info
     * @return hash code method
     */
    public static String getHashCodeMethod(JavaAttributeInfo attr) {
        return attr.getAttributeName() + COMMA + SPACE;
    }

    /**
     * Returns equals method open strings.
     *
     * @param className class name
     * @return equals method open string
     */
    static String getEqualsMethodOpen(String className) {
        return getOverRideString() +
                methodSignature(EQUALS_STRING, EMPTY_STRING, PUBLIC, OBJ,
                                BOOLEAN_DATA_TYPE, OBJECT_STRING,
                                CLASS_TYPE) +
                getEqualsMethodsCommonIfCondition() +
                getEqualsMethodsSpecificIfCondition(className);
    }

    /**
     * Returns equal methods if condition string.
     *
     * @return if condition string
     */
    private static String getEqualsMethodsCommonIfCondition() {
        return getIfConditionBegin(EIGHT_SPACE_INDENTATION, THIS + SPACE +
                EQUAL + EQUAL + SPACE + OBJ) + getReturnString(
                TRUE, TWELVE_SPACE_INDENTATION) + signatureClose()
                + methodClose(EIGHT_SPACE);
    }

    /**
     * Returns if condition for specific class object in equals method.
     *
     * @param className class name
     * @return if condition string
     */
    private static String getEqualsMethodsSpecificIfCondition(String className) {
        return EIGHT_SPACE_INDENTATION + IF + SPACE + OPEN_PARENTHESIS + OBJ +
                INSTANCE_OF + className + CLOSE_PARENTHESIS + SPACE +
                OPEN_CURLY_BRACKET + NEW_LINE + TWELVE_SPACE_INDENTATION +
                className + SPACE + OTHER + SPACE + EQUAL + SPACE +
                OPEN_PARENTHESIS + className + CLOSE_PARENTHESIS + SPACE + OBJ +
                SEMI_COLON + NEW_LINE + TWELVE_SPACE_INDENTATION + RETURN +
                NEW_LINE;
    }

    /**
     * Returns equals methods close string.
     *
     * @param equalMethodString equal method string
     * @return equals method close string
     */
    static String getEqualsMethodClose(String equalMethodString) {
        String[] array = {NEW_LINE, AND, AND, SPACE};
        equalMethodString = trimAtLast(equalMethodString, array) +
                signatureClose();
        return equalMethodString + methodClose(EIGHT_SPACE) +
                getReturnString(FALSE, EIGHT_SPACE_INDENTATION) +
                signatureClose() + methodClose(FOUR_SPACE);
    }

    /**
     * Returns equals method for class.
     *
     * @param attr attribute info
     * @return equals method
     */
    public static String getEqualsMethod(JavaAttributeInfo attr) {
        String attributeName = attr.getAttributeName();
        if (attributeName.contains(OPERATION_TYPE_ATTRIBUTE)) {
            return SIXTEEN_SPACE_INDENTATION + "Objects" + NEW_LINE +
                   SIXTEEN_SPACE_INDENTATION + ".deepEquals(" + attributeName + "," + NEW_LINE +
                   SIXTEEN_SPACE_INDENTATION + SPACE + OTHER + "." + attributeName + ") &&";
        }
        return SIXTEEN_SPACE_INDENTATION +
                "Objects.deepEquals(" + attributeName + ", " +
                                OTHER + "." + attributeName + ") &&";
    }

    /**
     * Returns of method's string and java doc for special type.
     *
     * @param attr      attribute info
     * @param className class name
     * @return of method's string and java doc for special type
     */

    public static String getOfMethodStringAndJavaDoc(JavaAttributeInfo attr,
                                                     String className) {
        String attrType = getReturnType(attr);
        String attrName = attr.getAttributeName();

        return getJavaDoc(OF_METHOD, className + FOR_TYPE_STRING + attrName,
                          false, null) + getOfMethodString(attrType, className);
    }

    /**
     * Returns of method's string.
     *
     * @param type data type
     * @param name class name
     * @return of method's string
     */
    private static String getOfMethodString(String type, String name) {
        return methodSignature(OF, EMPTY_STRING, PUBLIC + SPACE + STATIC,
                               VALUE, name, type, CLASS_TYPE) +
                methodBody(MethodBodyTypes.OF_METHOD, name, null,
                           EIGHT_SPACE_INDENTATION, EMPTY_STRING, null, false, null) +
                methodClose(FOUR_SPACE);
    }

    /**
     * Returns string and java doc for constructor of type class.
     *
     * @param attr      attribute info
     * @param className class name
     * @param genType   generate file type
     * @param count     type count
     * @return string and java doc for constructor of type class
     */
    public static String getTypeConstructorStringAndJavaDoc(
            JavaAttributeInfo attr, String className, int genType, int count) {
        String attrType = getReturnType(attr);
        String attrName = attr.getAttributeName();
        return getJavaDoc(TYPE_CONSTRUCTOR, attrName, false, null) +
                getTypeConstructorString(attrType, attrName, className,
                                         genType, count);
    }

    /**
     * Returns string and java doc for constructor of type class.
     *
     * @param attr1    first attribute info
     * @param attr2    second attribute info
     * @param genType  class name
     * @param type     conflict validate type
     * @param addFirst whether int came first or uInt came first
     * @param count    type count
     * @return string and java doc for constructor of type class
     */
    public static String getTypeConstructorStringAndJavaDoc(
            JavaAttributeInfo attr1, JavaAttributeInfo attr2, String genType,
            ValidatorTypeForUnionTypes type, boolean addFirst, int count) {

        String attrType = getReturnType(attr1);
        String attrName1 = "";
        String attrName2 = "";
        if (attr1 != null) {
            if (addFirst) {
                attrName1 = attr1.getAttributeName();
            } else {
                attrName2 = attr1.getAttributeName();
            }
        }
        if (attr2 != null) {
            if (addFirst) {
                attrName2 = attr2.getAttributeName();
            } else {
                attrName1 = attr2.getAttributeName();
            }
        }

        String appDataStructure = null;
        if (attr1 != null && attr1.getCompilerAnnotation() != null) {
            appDataStructure =
                    attr1.getCompilerAnnotation().getYangAppDataStructure()
                            .getDataStructure().name();
        }
        String doc;
        if (attrName1.isEmpty()) {
            doc = attrName2;
        } else {
            doc = attrName1;
        }
        return getJavaDoc(TYPE_CONSTRUCTOR, doc, false, appDataStructure) +
                getTypeConstructorString(attrType, attrName1,
                                         attrName2, genType,
                                         type, addFirst, count);
    }

    /**
     * Returns type constructor string.
     *
     * @param type      data type
     * @param name      attribute name
     * @param className class name
     * @param genType   generated file type
     * @param count     type count
     * @return type constructor string
     */
    private static String getTypeConstructorString(String type, String name,
                                                   String className, int genType, int count) {
        StringBuilder builder = new StringBuilder();
        builder.append(
                methodSignature(className, EMPTY_STRING, PUBLIC, name,
                                null, type, CLASS_TYPE));
        if (genType == GENERATE_UNION_CLASS) {
            builder.append(EIGHT_SPACE_INDENTATION).append(SET_VALUE_PARA)
                    .append(PERIOD).append(SET_METHOD_PREFIX).append(
                    getOpenCloseParaWithValue(count + EMPTY_STRING))
                    .append(signatureClose());
        }
        builder.append(methodBody(SETTER, name, null, EIGHT_SPACE_INDENTATION,
                                  EMPTY_STRING, null, false, null))
                .append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    /**
     * Returns type constructor string.
     *
     * @param type      data type
     * @param attr1     attribute attr1
     * @param className class attr1
     * @param count     type count
     * @return type constructor string
     */
    private static String getTypeConstructorString(
            String type, String attr1, String attr2, String className,
            ValidatorTypeForUnionTypes validatorType, boolean addInt, int count) {
        StringBuilder constructor = new StringBuilder(
                methodSignature(className, EMPTY_STRING, PUBLIC, attr1,
                                null, type, CLASS_TYPE))
                .append(EIGHT_SPACE_INDENTATION).append(SET_VALUE_PARA)
                .append(PERIOD).append(SET_METHOD_PREFIX).append(
                        getOpenCloseParaWithValue(count + EMPTY_STRING))
                .append(signatureClose())
                .append(ifConditionForIntInTypeDefConstructor(validatorType,
                                                              addInt, attr1))
                .append(methodBody(SETTER, attr1, null,
                                   TWELVE_SPACE_INDENTATION, EMPTY_STRING,
                                   null, false, attr1));
        String str = EIGHT_SPACE_INDENTATION + CLOSE_CURLY_BRACKET +
                ELSE + OPEN_CURLY_BRACKET + NEW_LINE;
        constructor.append(str)
                .append(methodBody(SETTER, attr2, null,
                                   TWELVE_SPACE_INDENTATION, EMPTY_STRING,
                                   null, false, attr1))
                .append(methodClose(FOUR_SPACE))
                .append(methodClose(EIGHT_SPACE));

        return constructor.toString();
    }

    /**
     * Returns enum's constructor.
     *
     * @param className enum's class name
     * @return enum's constructor
     */
    static String getEnumsConstructor(String className) {
        StringBuilder builder = new StringBuilder(
                getJavaDoc(TYPE_CONSTRUCTOR, className, false, null));
        String clsName = getSmallCase(className);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(clsName, INT);
        map.put(SCHEMA_NAME, STRING_DATA_TYPE);
        builder.append(multiAttrMethodSignature(className, EMPTY_STRING,
                                                EMPTY_STRING, null,
                                                map, CLASS_TYPE, FOUR_SPACE_INDENTATION))
                .append(methodBody(SETTER, clsName, EMPTY_STRING,
                                   EIGHT_SPACE_INDENTATION, EMPTY_STRING,
                                   EMPTY_STRING, false, null))
                .append(methodBody(SETTER, SCHEMA_NAME, EMPTY_STRING,
                                   EIGHT_SPACE_INDENTATION, EMPTY_STRING,
                                   EMPTY_STRING, false, null))
                .append(methodClose(FOUR_SPACE));

        return builder.toString();
    }

    /**
     * Returns of method for enum class.
     *
     * @param className class name
     * @param enumeration in yang
     * @param type      method body type
     * @return of method
     */
    static String getEnumsOfValueMethod(String className,
                                        YangEnumeration enumeration,
                                        MethodBodyTypes type) {
        String name = getCapitalCase(className);
        StringBuilder builder = new StringBuilder(getJavaDoc(
                OF_METHOD, name + SPACE + FOR,
                false, null));
        //Switch statement.
        String sw = EIGHT_SPACE_INDENTATION + SWITCH + SPACE +
                getOpenCloseParaWithValue(VALUE) +
                methodSignatureClose(CLASS_TYPE);
        String str;
        switch (type) {
            case ENUM_METHOD_INT_VALUE:
                builder.append(getEnumValueMethodSignature(name, INT))
                        .append(sw);
                for (YangEnum yangEnum : enumeration.getEnumSet()) {
                    str = getEnumJavaAttribute(yangEnum.getNamedValue())
                            .toUpperCase();
                    builder.append(getEnumValueMethodCases(
                            EMPTY_STRING + yangEnum.getValue(), str, name));
                }
                break;
            case ENUM_METHOD_STRING_VALUE:
                builder.append(getEnumValueMethodSignature(name,
                                                           STRING_DATA_TYPE))
                        .append(sw);
                for (YangEnum yangEnum : enumeration.getEnumSet()) {
                    str = getEnumJavaAttribute(yangEnum.getNamedValue())
                            .toUpperCase();
                    builder.append(getEnumValueMethodCases(
                            QUOTES + getEnumYangName(yangEnum.getNamedValue()) + QUOTES, str,
                            name));
                }
                break;
            default:
                return null;
        }

        String method = TWELVE_SPACE_INDENTATION + "default" + SPACE + COLON +
                NEW_LINE;
        builder.append(method)
                .append(getExceptionThrowString(SIXTEEN_SPACE_INDENTATION))
                .append(methodClose(EIGHT_SPACE))
                .append(methodClose(FOUR_SPACE));

        return builder.toString();
    }

    /**
     * Returns enum value method signature.
     *
     * @param name method name
     * @param type param type
     * @return enum value method signature
     */
    private static String getEnumValueMethodSignature(String name, String type) {
        return methodSignature(OF, EMPTY_STRING, PUBLIC + SPACE +
                STATIC, VALUE, name, type, CLASS_TYPE);
    }

    /**
     * Returns enum value method's cases.
     *
     * @param caseType case type
     * @param value    return value
     * @param name     name of class
     * @return enum value method's cases
     */
    private static String getEnumValueMethodCases(String caseType, String value,
                                                  String name) {
        return TWELVE_SPACE_INDENTATION + CASE + SPACE + caseType +
                COLON + NEW_LINE + getReturnString(name,
                                                   SIXTEEN_SPACE_INDENTATION) +
                PERIOD + value + signatureClose();
    }

    /**
     * Returns augmented data getter and setter methods for service class.
     *
     * @param parent parent node
     * @return augmented data getter and setter methods for service class
     */
    static String getAugmentsDataMethodForService(YangNode parent) {
        List<YangAtomicPath> targets = getSetOfNodeIdentifiers(parent);
        if (targets.isEmpty()) {
            return EMPTY_STRING;
        }
        YangNode first = targets.get(0).getResolvedNode();
        //If target path is for notification then no need to generate get/set
        // for that augment in service class.
        if (first instanceof InvalidOpTypeHolder) {
            return EMPTY_STRING;
        }
        YangNode augmentedNode;
        String curNodeName;
        String method;
        StringBuilder methods = new StringBuilder();
        String parentName;
        String returnType;
        YangNode methodNode;
        YangPluginConfig pluginConfig =
                ((JavaFileInfoContainer) parent).getJavaFileInfo()
                        .getPluginConfig();
        for (YangAtomicPath nodeId : targets) {
            augmentedNode = nodeId.getResolvedNode().getParent();
            methodNode = nodeId.getResolvedNode();
            if (((JavaFileInfoContainer) methodNode).getJavaFileInfo()
                    .getJavaName() != null) {
                curNodeName =
                        ((JavaFileInfoContainer) methodNode).getJavaFileInfo()
                                .getJavaName();
            } else {
                curNodeName = getCapitalCase(
                        getCamelCase(methodNode.getName(),
                                     pluginConfig.getConflictResolver()));
            }
            returnType =
                    getAugmentedClassNameForDataMethods(augmentedNode, parent);
            parentName = getCurNodeName(augmentedNode,
                                        pluginConfig);
            method = generateForGetMethodWithAttribute(returnType) +
                    getGetMethodWithArgument(returnType, AUGMENTED +
                            parentName + getCapitalCase(curNodeName)) + NEW_LINE;
            methods.append(method);

            method = getJavaDoc(MANAGER_SETTER_METHOD,
                                AUGMENTED + getCapitalCase(parentName) +
                                        getCapitalCase(curNodeName), false,
                                null) +
                    getSetterForInterface(getSmallCase(AUGMENTED) + parentName +
                                                  getCapitalCase(curNodeName),
                                          returnType, parentName, false,
                                          GENERATE_SERVICE_AND_MANAGER, null) +
                    NEW_LINE;
            methods.append(method);
        }
        return methods.toString();
    }

    /**
     * Returns validator method for range in union class.
     *
     * @param type type
     * @return validator method for range in union class
     */
    static String getRangeValidatorMethodForUnion(String type) {
        String newType;
        if (type.contentEquals(BIG_INTEGER)) {
            newType = LONG;
        } else {
            newType = INT;
        }
        StringBuilder builder = new StringBuilder(generateForValidatorMethod());
        String var = getSmallCase(BIG_INTEGER);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(MIN_RANGE, type);
        map.put(MAX_RANGE, type);
        map.put(VALUE, newType);
        builder.append(multiAttrMethodSignature(VALIDATE_RANGE, EMPTY_STRING,
                                                PRIVATE, BOOLEAN_DATA_TYPE, map,
                                                CLASS_TYPE, FOUR_SPACE_INDENTATION));
        if (type.contentEquals(BIG_INTEGER)) {
            //Create new instance of big integer.
            builder.append(getNewInstance(BIG_INTEGER, var, EIGHT_SPACE_INDENTATION,
                                          QUOTES + SPACE + QUOTES + SPACE +
                                                  ADD + SPACE + VALUE))
                    //Add return string.
                    .append(getReturnString(var, EIGHT_SPACE_INDENTATION))
                    //Add compareTo string
                    .append(getCompareToString())
                    //Add && condition.
                    .append(ifAndAndCondition(
                            //Add == condition
                            ifEqualEqualCondition(
                                    getOpenCloseParaWithValue(MIN_RANGE), ONE),
                            var))
                    //Add compareTo string.
                    .append(getCompareToString())
                    //Add == condition.
                    .append(ifEqualEqualCondition(
                            getOpenCloseParaWithValue(MAX_RANGE), ONE))
                    .append(signatureClose());
        } else {
            builder.append(getReturnString(VALUE, EIGHT_SPACE_INDENTATION))
                    .append(getGreaterThanCondition())
                    .append(ifAndAndCondition(MIN_RANGE, VALUE))
                    .append(getLesserThanCondition())
                    .append(MAX_RANGE)
                    .append(signatureClose());
        }
        builder.append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    //Get method with arguments.
    private static String getGetMethodWithArgument(String returnType,
                                                   String yangName) {
        return methodSignature(getCapitalCase(yangName), GET_METHOD_PREFIX,
                               null, getSmallCase(returnType),
                               returnType, returnType + OP_PARAM,
                               INTERFACE_TYPE);
    }

    /**
     * Returns add to list method impl.
     *
     * @param attr java attribute
     * @param name class name
     * @return add to list method impl
     */
    public static String getAddToListMethodImpl(JavaAttributeInfo attr,
                                                String name) {
        String attrName = attr.getAttributeName();
        String retString = getOverRideString();
        String methodName = ADD_STRING + TO_CAPS + getCapitalCase(attrName);
        StringBuilder builder = new StringBuilder(retString);
        String retType = getReturnType(attr);
        YangDataStructure struct = getYangDataStructure(attr.getCompilerAnnotation());
        if (struct != null) {
            switch (struct) {
                case MAP:
                    Map<String, String> param = new LinkedHashMap<>();
                    param.put(attr.getAttributeName() + KEYS, retType + KEYS);
                    param.put(attr.getAttributeName() + VALUE_CAPS, retType);
                    builder.append(multiAttrMethodSignature(methodName,
                                                            null, PUBLIC,
                                                            VOID, param,
                                                            CLASS_TYPE,
                                                            FOUR_SPACE_INDENTATION))
                            .append(getIfConditionForAddToListMethod(attr));
                    retString = EIGHT_SPACE_INDENTATION + attrName + PERIOD +
                            PUT + getOpenCloseParaWithValue(
                            attrName + KEYS + COMMA + SPACE + attrName +
                                    VALUE_CAPS);
                    break;
                default:
                    builder.append(methodSignature(methodName,
                                                   null, PUBLIC,
                                                   ADD_STRING + TO_CAPS,
                                                   VOID, retType,
                                                   CLASS_TYPE))
                            .append(getIfConditionForAddToListMethod(attr));
                    retString = EIGHT_SPACE_INDENTATION + attrName + PERIOD + ADD_STRING +
                            OPEN_PARENTHESIS + ADD_STRING + TO_CAPS + CLOSE_PARENTHESIS;
            }
        } else {
            builder.append(methodSignature(ADD_STRING + TO_CAPS +
                                                   getCapitalCase(attrName),
                                           null, PUBLIC, ADD_STRING + TO_CAPS,
                                           VOID, retType,
                                           CLASS_TYPE))
                    .append(getIfConditionForAddToListMethod(attr));
            retString = EIGHT_SPACE_INDENTATION + attrName + PERIOD + ADD_STRING +
                    OPEN_PARENTHESIS + ADD_STRING + TO_CAPS + CLOSE_PARENTHESIS;
        }
        builder.append(retString)
                .append(signatureClose())
                .append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    // Returns if condition for add to list method.

    private static String getIfConditionForAddToListMethod(JavaAttributeInfo attr) {
        String name = attr.getAttributeName();
        String type;
        YangDataStructure struct = getYangDataStructure(attr.getCompilerAnnotation());
        if (struct != null) {
            switch (struct) {
                case QUEUE:
                    type = PRIORITY_QUEUE;
                    break;

                case LIST:
                    type = ARRAY_LIST_INIT;
                    break;

                case MAP:
                    type = LINKED_HASH_MAP;
                    break;

                case SET:
                    type = LINKED_HASH_SET;
                    break;

                default:
                    type = ARRAY_LIST_INIT;
                    break;
            }
        } else {
            type = ARRAY_LIST_INIT;
        }
        return getIfConditionBegin(EIGHT_SPACE_INDENTATION, name + SPACE + EQUAL +
                EQUAL + SPACE + NULL) + TWELVE_SPACE_INDENTATION +
                name + SPACE + EQUAL + SPACE +
                NEW + SPACE + type + signatureClose() + methodClose(
                EIGHT_SPACE);
    }

    /**
     * Returns is value set interface.
     *
     * @return is value set interface
     */
    static String isLeafValueSetInterface() {
        String method = "\n    /**\n" +
                "     * Checks if the leaf value is set.\n" +
                "     *\n" +
                "     * @param leaf leaf whose value status needs to checked\n" +
                "     * @return result of leaf value set in object\n" +
                "     */\n";
        return method + methodSignature(VALUE_LEAF_SET, EMPTY_STRING, null,
                                        LEAF, BOOLEAN_DATA_TYPE,
                                        LEAF_IDENTIFIER, INTERFACE_TYPE);
    }

    /**
     * Returns leaf identifier interface enum signature.
     *
     * @param name name of node
     * @return leaf identifier interface enum signature
     */
    static String getInterfaceLeafIdEnumSignature(String name) {
        String start = "\n    /**\n" +
                "     * Identify the leaf of " + name + PERIOD + NEW_LINE +
                "     */\n";
        return start + FOUR_SPACE_INDENTATION + PUBLIC + SPACE + ENUM + SPACE +
                LEAF_IDENTIFIER + SPACE + IMPLEMENTS + SPACE +
                MODEL_LEAF_IDENTIFIER + OPEN_CURLY_BRACKET + NEW_LINE;
    }

    /**
     * Generates fromString code for bits.
     *
     * @return generated fromString code for bits.
     */
    private static String getFromStringForBits(String bitClassName) {
     /* generate code will look like this.
       public static BitSet fromString(String valInString) {
            BitSet tmpVal = new BitSet();
            String[] bitNames = valInString.trim().split(Pattern.quote(" "));
            for (String bitName : bitNames) {
                Bits bits = of(bitName);
                if (bits != null) {
                    tmpVal.set(bits.bits());
                }
            }
            if (tmpVal.isEmpty()) {
                throw new NoSuchElementException("no such element found in bits");
            }
            return tmpVal;
        }*/
        StringBuilder sBuild = new StringBuilder();
        sBuild.append(methodSignature(FROM_STRING_METHOD_NAME, null,
                                      PUBLIC + SPACE + STATIC,
                                      FROM_STRING_PARAM_NAME,
                                      BIT_SET, STRING_DATA_TYPE, CLASS_TYPE))
                .append(EIGHT_SPACE_INDENTATION)
                .append(getBitSetAttr(EMPTY_STRING));
        // Split the input string and check each bit name falls in configured yang file
        sBuild.append(EIGHT_SPACE_INDENTATION).append(STRING_DATA_TYPE)
                .append(SQUARE_BRACKETS).append(SPACE).append(BIT_NAMES_VAR)
                .append(SPACE).append(EQUAL).append(SPACE).append(FROM_STRING_PARAM_NAME)
                .append(PERIOD).append(TRIM_STRING).append(OPEN_CLOSE_BRACKET_STRING)
                .append(PERIOD).append(SPLIT_STRING).append(getOpenCloseParaWithValue(
                getPatternQuoteString(SPACE))).append(signatureClose()).append(
                getForLoopString(EIGHT_SPACE_INDENTATION, STRING_DATA_TYPE, BIT_NAME_VAR,
                                 BIT_NAMES_VAR));

        String small = getSmallCase(bitClassName);
        sBuild.append(TWELVE_SPACE_INDENTATION).append(bitClassName).append(
                SPACE).append(small).append(SPACE).append(EQUAL).append(
                SPACE).append(OF).append(
                getOpenCloseParaWithValue(BIT_NAME_VAR)).append(signatureClose());
        String condition = small + SPACE + NOT + EQUAL + SPACE + NULL;
        sBuild.append(getIfConditionBegin(TWELVE_SPACE_INDENTATION, condition))
                .append(SIXTEEN_SPACE_INDENTATION)
                .append(TMP_VAL).append(PERIOD).append(SET_METHOD_PREFIX)
                .append(OPEN_PARENTHESIS)
                .append(small).append(PERIOD).append(small).append(
                OPEN_CLOSE_BRACKET_STRING).append(CLOSE_PARENTHESIS)
                .append(signatureClose()).append(methodClose(TWELVE_SPACE))
                .append(methodClose(EIGHT_SPACE));

        condition = TMP_VAL + PERIOD + IS_EMPTY;
        sBuild.append(getIfConditionBegin(EIGHT_SPACE_INDENTATION, condition));
        sBuild.append(getExceptionThrowString(TWELVE_SPACE_INDENTATION))
                .append(methodClose(EIGHT_SPACE))
                .append(getReturnString(TMP_VAL, EIGHT_SPACE_INDENTATION))
                .append(signatureClose()).append(methodClose(FOUR_SPACE));
        return sBuild.toString();
    }


    /**
     * Returns to string method for typedef.
     *
     * @param attr attribute name
     * @param type in yang
     * @return to string method for typedef
     */
    static String getToStringForType(String attr, YangType type) {
        StringBuilder builder = new StringBuilder(getOverRideString())
                .append(methodSignature(TO_STRING_METHOD, null, PUBLIC, null,
                                        STRING_DATA_TYPE, null, CLASS_TYPE));
        if (type.getDataType() == IDENTITYREF) {
            builder.append(getToStringForSpecialType(type, attr))
                    .append(getReturnString(null, EIGHT_SPACE_INDENTATION))
                    .append(signatureClose())
                    .append(methodClose(FOUR_SPACE));
        } else {
            builder.append(getReturnString(
                    getToStringForSpecialType(type, attr), EIGHT_SPACE_INDENTATION))
                    .append(signatureClose()).append(methodClose(FOUR_SPACE));
        }
        return builder.toString();
    }

    /**
     * Returns to string method body for type class.
     *
     * @param type type of attribute
     * @param name @return to string method body for typedef class
     */
    private static String getToStringForSpecialType(YangType type, String name) {
        switch (type.getDataType()) {
            case INT8:
            case INT16:
            case INT32:
            case INT64:
            case UINT8:
            case UINT16:
            case UINT32:
                return STRING_DATA_TYPE + PERIOD + VALUE + OF_CAPS +
                        getOpenCloseParaWithValue(name);

            case BINARY:
                return getToStringCall(getToStringForBinary(name));

            case BITS:
                return getCapitalCase(name) + PERIOD +
                        TO_STRING_METHOD + getOpenCloseParaWithValue(name);

            case BOOLEAN:
            case EMPTY:
                return name + SPACE + QUESTION_MARK + SPACE + getQuotedString(TRUE)
                        + SPACE + COLON + SPACE + getQuotedString(FALSE);

            case LEAFREF:
                YangLeafRef<?> lri = (YangLeafRef<?>) type.getDataTypeExtendedInfo();
                YangType<?> rt = lri.isInGrouping() ? null : lri
                        .getEffectiveDataType();
                return rt == null ? getToStringCall(name) :
                        getToStringForSpecialType(rt, name);

            case IDENTITYREF:
                return getIdRefToString(type, name, "");
            case ENUMERATION:
            case INSTANCE_IDENTIFIER:
            case UINT64:
            case DECIMAL64:
            case DERIVED:
            case UNION:
                return getToStringCall(name);

            default:
                return name;
        }
    }

    /**
     * Returns indented toString method of identiref.
     *
     * @param type   type of attribute.
     * @param name   @return to string method body for typedef class
     * @param indent number of indent spaces.
     * @return returns tostring method.
     */
    private static String getIdRefToString(YangType type, String name, String
            indent) {
        YangIdentityRef ir = (YangIdentityRef) type
                .getDataTypeExtendedInfo();
        StringBuilder builder = new StringBuilder();
        String idName = getCamelCase(getIdentityRefName(type), null);
        YangIdentity identity = ir.getReferredIdentity();
        //condition for comparision
        String cond = getTwoParaEqualsString(name, getCapitalCase(idName) + PERIOD + CLASS);
        //return value in toString method
        String returnVal = getCapitalCase(idName) + PERIOD + getCamelCase(
                identity.getName(), null) + TO_CAPS +
                STRING_DATA_TYPE + OPEN_CLOSE_BRACKET_STRING;
        List<YangIdentity> idList = identity.getExtendList();
        //adding present identity's tostring method
        builder.append(getIfConditionBegin(EIGHT_SPACE_INDENTATION + indent,
                                           cond))
                .append(getReturnString(returnVal, TWELVE_SPACE_INDENTATION +
                        indent))
                .append(signatureClose());
        //adding derived identities tostring method
        for (YangIdentity id : idList) {
            idName = getIdName(id);
            cond = getTwoParaEqualsString(name, getCapitalCase(idName) +
                    PERIOD + CLASS);

            returnVal = getCapitalCase(idName) + PERIOD + getCamelCase(id.getName(), null) + TO_CAPS +
                    STRING_DATA_TYPE + OPEN_CLOSE_BRACKET_STRING;

            builder.append(getElseIfConditionBegin(
                    EIGHT_SPACE_INDENTATION + indent, cond))
                    .append(getReturnString(returnVal,
                                            TWELVE_SPACE_INDENTATION + indent))
                    .append(signatureClose());
        }
        //passing indents for if-else statements in toString methods
        if (indent.equals(FOUR_SPACE_INDENTATION)) {
            builder.append(methodClose(TWELVE_SPACE));
        } else {
            builder.append(methodClose(EIGHT_SPACE));
        }
        return builder.toString();
    }

    /**
     * Returns union class's to string method.
     *
     * @param types list of types
     * @return union class's to string method
     */
    static String getUnionToStringMethod(List<YangType<?>> types) {
        StringBuilder builder = new StringBuilder(getOverRideString());
        builder.append(methodSignature(TO_STRING_METHOD, null, PUBLIC, null,
                                       STRING_DATA_TYPE, null, CLASS_TYPE));
        for (YangType type : types) {
            builder.append(getIfConditionBegin(
                    EIGHT_SPACE_INDENTATION, getSetValueParaCondition(
                            types.indexOf(type))));
            String retVal = getToStringForSpecialType(
                    type, getCamelCase(type.getDataTypeName(), null));
            if (type.getDataType() != IDENTITYREF) {
                builder.append(getReturnString(retVal, TWELVE_SPACE_INDENTATION))
                        .append(signatureClose());
            } else {
                retVal = getIdRefToString(type,
                                          getCamelCase(type.getDataTypeName(), null),
                                          FOUR_SPACE_INDENTATION);
                builder.append(retVal);
            }
            builder.append(methodClose(EIGHT_SPACE));
        }
        builder.append(getReturnString(NULL, EIGHT_SPACE_INDENTATION)).append(signatureClose())
                .append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    /**
     * Returns from string method for bits class.
     *
     * @param className bits class name
     * @return from string method for bits class
     */
    static String getBitSetEnumClassFromString(String className) {
        return getJavaDoc(FROM_METHOD, className, false, null) +
                getFromStringForBits(className);
    }

    /**
     * Returns to string method for bits type.
     *
     * @param className   class name
     * @param enumeration enumeration
     * @return to string method
     */
    static String getBitSetEnumClassToString(String className,
                                             YangEnumeration enumeration) {

        StringBuilder builder = new StringBuilder();
        builder.append(methodSignature(TO_STRING_METHOD, null,
                                       PUBLIC + SPACE + STATIC, BITS,
                                       STRING_DATA_TYPE, BIT_SET, CLASS_TYPE))
                .append(getStringBuilderAttr(EMPTY_STRING, EIGHT_SPACE_INDENTATION));
        String condition;
        String name;
        for (YangEnum yangEnum : enumeration.getEnumSet()) {
            name = yangEnum.getNamedValue();
            condition = BITS + PERIOD + GET + OPEN_PARENTHESIS +
                    className + PERIOD + getEnumJavaAttribute(name).toUpperCase()
                    + PERIOD + getSmallCase(className)
                    + OPEN_CLOSE_BRACKET_STRING + CLOSE_PARENTHESIS;

            builder.append(getIfConditionBegin(
                    EIGHT_SPACE_INDENTATION, condition))
                    .append(TWELVE_SPACE_INDENTATION).append(STRING_BUILDER_VAR).append(
                    PERIOD).append(APPEND).append(OPEN_PARENTHESIS)
                    .append(getQuotedString(name)).append(CLOSE_PARENTHESIS)
                    .append(signatureClose())
                    .append(TWELVE_SPACE_INDENTATION).append(STRING_BUILDER_VAR).append(
                    PERIOD).append(APPEND).append(OPEN_PARENTHESIS)
                    .append(getQuotedString(SPACE)).append(CLOSE_PARENTHESIS)
                    .append(signatureClose()).append(methodClose(EIGHT_SPACE));
        }
        builder.append(getReturnString(STRING_BUILDER_VAR, EIGHT_SPACE_INDENTATION))
                .append(PERIOD).append(TO_STRING_METHOD)
                .append(OPEN_CLOSE_BRACKET_STRING).append(signatureClose())
                .append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    /**
     * Returns to string method for enum class.
     *
     * @return to string method for enum class
     */
    static String getToStringForEnumClass() {
        StringBuilder builder = new StringBuilder(getOverRideString());
        builder.append(methodSignature(TO_STRING_METHOD, EMPTY_STRING,
                                       PUBLIC, null, STRING_DATA_TYPE, null,
                                       CLASS_TYPE));
        builder.append(getReturnString(SCHEMA_NAME, EIGHT_SPACE_INDENTATION))
                .append(signatureClose()).append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    /**
     * To string method for identity.
     *
     * @param name name of identity
     * @return to string method
     */
    public static String getToStringMethodForIdentity(String name) {
        StringBuilder builder = new StringBuilder(NEW_LINE);
        builder.append(getJavaDoc(GETTER_METHOD, name, false, null));
        String returnVal = getQuotedString(name);
        String methodName = getCamelCase(name, null) + TO_CAPS + STRING_DATA_TYPE;
        builder.append(methodSignature(methodName, null, PUBLIC + SPACE + STATIC,
                                       null, STRING_DATA_TYPE, null, CLASS_TYPE))
                .append(getReturnString(returnVal, EIGHT_SPACE_INDENTATION))
                .append(signatureClose()).append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    /**
     * Returns from string method for identity class.
     *
     * @param name       name of identity
     * @param schemaName schema name
     * @param idList     list of derived identities
     * @return from string method
     */
    public static String getFromStringMethodForIdentity(String name,
                                                        String schemaName,
                                                        List<YangIdentity> idList) {
        StringBuilder builder = new StringBuilder(NEW_LINE);
        builder.append(getJavaDoc(FROM_METHOD, name, false, null));
        String caps = getCapitalCase(name);
        String returnVal = caps + PERIOD + CLASS;
        String cond = getTwoParaEqualsString(FROM_STRING_PARAM_NAME,
                                             getQuotedString(schemaName));
        builder.append(methodSignature(FROM_STRING_METHOD_NAME, null,
                                       PUBLIC + SPACE + STATIC,
                                       FROM_STRING_PARAM_NAME, CLASS_STRING,
                                       STRING_DATA_TYPE, CLASS_TYPE))
                .append(getIfConditionBegin(EIGHT_SPACE_INDENTATION, cond))
                .append(getReturnString(returnVal, TWELVE_SPACE_INDENTATION))
                .append(signatureClose());
        if (idList != null) {
            for (YangIdentity id : idList) {
                cond = getTwoParaEqualsString(FROM_STRING_PARAM_NAME,
                                              getQuotedString(id.getName()));
                if (id.isNameConflict()) {
                    name = getCamelCase(id.getName() + IDENTITY, null);
                } else {
                    name = getCamelCase(id.getName(), null);
                }
                caps = getCapitalCase(name);
                returnVal = caps + PERIOD + CLASS;
                builder.append(getElseIfConditionBegin(
                        EIGHT_SPACE_INDENTATION, cond))
                        .append(getReturnString(returnVal, TWELVE_SPACE_INDENTATION))
                        .append(signatureClose());
            }
        }
        builder.append(methodClose(EIGHT_SPACE))
                .append(getExceptionThrowString(EIGHT_SPACE_INDENTATION))
                .append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    /**
     * Returns parameterisied constructor string.
     *
     * @param name         class name
     * @param modifierType modifier type
     * @param params       parameters for constructor
     * @param space        indentation for constructor
     * @return parameterisied constructor method string
     */
    public static String getParaMeterisiedConstructor(String name,
                                                      String modifierType,
                                                      Map<String, String> params,
                                                      String space) {
        StringBuilder builder = new StringBuilder();
        builder.append(multiAttrMethodSignature(name, null, modifierType, null,
                                                params, CLASS_TYPE, space));
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.append(valueAssign(THIS + PERIOD + entry.getKey(),
                                       entry.getKey(), space + FOUR_SPACE_INDENTATION));
        }

        builder.append(space).append(CLOSE_CURLY_BRACKET).append(NEW_LINE);
        return builder.toString();
    }
}
