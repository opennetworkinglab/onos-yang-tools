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
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.BOOLEAN;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DECIMAL64;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.EMPTY;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT16;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT32;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT64;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT8;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT16;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT32;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT64;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT8;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER;
import static org.onosproject.yang.compiler.translator.tojava.utils.BracketType.OPEN_CLOSE_BRACKET;
import static org.onosproject.yang.compiler.translator.tojava.utils.BracketType.OPEN_CLOSE_BRACKET_WITH_VALUE;
import static org.onosproject.yang.compiler.translator.tojava.utils.BracketType.OPEN_CLOSE_BRACKET_WITH_VALUE_AND_RETURN_TYPE;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodClassTypes.CLASS_TYPE;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getYangDataStructure;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodClassTypes.INTERFACE_TYPE;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorUtils.getIdentityRefName;
import static org.onosproject.yang.compiler.utils.UtilConstants.ABSTRACT;
import static org.onosproject.yang.compiler.utils.UtilConstants.ADD;
import static org.onosproject.yang.compiler.utils.UtilConstants.ADD_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.AND;
import static org.onosproject.yang.compiler.utils.UtilConstants.APP_INSTANCE;
import static org.onosproject.yang.compiler.utils.UtilConstants.AT;
import static org.onosproject.yang.compiler.utils.UtilConstants.BIG_DECIMAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.BIG_INTEGER;
import static org.onosproject.yang.compiler.utils.UtilConstants.BITSET;
import static org.onosproject.yang.compiler.utils.UtilConstants.BOOLEAN_DATA_TYPE;
import static org.onosproject.yang.compiler.utils.UtilConstants.BOOLEAN_WRAPPER;
import static org.onosproject.yang.compiler.utils.UtilConstants.BYTE;
import static org.onosproject.yang.compiler.utils.UtilConstants.BYTE_WRAPPER;
import static org.onosproject.yang.compiler.utils.UtilConstants.CATCH;
import static org.onosproject.yang.compiler.utils.UtilConstants.CHECK_NOT_NULL_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMA;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMPARE_TO;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.DIAMOND_CLOSE_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.DIAMOND_OPEN_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.DOUBLE;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.ELSE;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.EQUAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.EQUALS_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXCEPTION;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXCEPTION_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXCEPTION_VAR;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXTEND;
import static org.onosproject.yang.compiler.utils.UtilConstants.FALSE;
import static org.onosproject.yang.compiler.utils.UtilConstants.FOR;
import static org.onosproject.yang.compiler.utils.UtilConstants.FOUR_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.FROM_STRING_METHOD_NAME;
import static org.onosproject.yang.compiler.utils.UtilConstants.GET;
import static org.onosproject.yang.compiler.utils.UtilConstants.IF;
import static org.onosproject.yang.compiler.utils.UtilConstants.IMPLEMENTS;
import static org.onosproject.yang.compiler.utils.UtilConstants.IMPORT;
import static org.onosproject.yang.compiler.utils.UtilConstants.IN;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT;
import static org.onosproject.yang.compiler.utils.UtilConstants.INTEGER_WRAPPER;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT_MAX_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT_MIN_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.KEYS;
import static org.onosproject.yang.compiler.utils.UtilConstants.LEAF_IDENTIFIER;
import static org.onosproject.yang.compiler.utils.UtilConstants.LIST;
import static org.onosproject.yang.compiler.utils.UtilConstants.LONG;
import static org.onosproject.yang.compiler.utils.UtilConstants.LONG_MAX_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.LONG_MIN_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.LONG_WRAPPER;
import static org.onosproject.yang.compiler.utils.UtilConstants.MAP;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.OBJECT;
import static org.onosproject.yang.compiler.utils.UtilConstants.OF;
import static org.onosproject.yang.compiler.utils.UtilConstants.OMIT_NULL_VALUE_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_CLOSE_BRACKET_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.OP_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.OVERRIDE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PARSE_BOOLEAN;
import static org.onosproject.yang.compiler.utils.UtilConstants.PARSE_BYTE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PARSE_INT;
import static org.onosproject.yang.compiler.utils.UtilConstants.PARSE_LONG;
import static org.onosproject.yang.compiler.utils.UtilConstants.PARSE_SHORT;
import static org.onosproject.yang.compiler.utils.UtilConstants.PATTERN;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUEUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUOTES;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUOTE_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.RETURN;
import static org.onosproject.yang.compiler.utils.UtilConstants.SEMI_COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.SET;
import static org.onosproject.yang.compiler.utils.UtilConstants.SET_METHOD_PREFIX;
import static org.onosproject.yang.compiler.utils.UtilConstants.SET_VALUE_PARA;
import static org.onosproject.yang.compiler.utils.UtilConstants.SHORT;
import static org.onosproject.yang.compiler.utils.UtilConstants.SHORT_MAX_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SHORT_MIN_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SHORT_WRAPPER;
import static org.onosproject.yang.compiler.utils.UtilConstants.SIXTEEN_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.STATIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.STRING_BUILDER;
import static org.onosproject.yang.compiler.utils.UtilConstants.STRING_BUILDER_VAR;
import static org.onosproject.yang.compiler.utils.UtilConstants.THIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.THROW_NEW;
import static org.onosproject.yang.compiler.utils.UtilConstants.TMP_VAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.TO_STRING_METHOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.TRY;
import static org.onosproject.yang.compiler.utils.UtilConstants.TWELVE_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.TWENTY_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.UINT8_MAX_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.UINT8_MIN_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.UINT_MAX_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.UINT_MIN_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.ULONG_MAX_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.ULONG_MIN_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALIDATE_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.VOID;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_UTILS_TODO;
import static org.onosproject.yang.compiler.utils.UtilConstants.ZERO;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.trimAtLast;

/**
 * Represents string generator for translator.
 */
public final class StringGenerator {

    private static final Set<YangDataTypes> PRIMITIVE_TYPES =
            new HashSet<>(Arrays.asList(INT8, INT16, INT32, INT64, UINT8,
                                        UINT16, UINT32, UINT64, DECIMAL64,
                                        BOOLEAN, EMPTY));

    // No instantiation.
    private StringGenerator() {
    }

    /**
     * Returns compare to string.
     *
     * @return compare to string
     */
    static String getCompareToString() {
        return PERIOD + COMPARE_TO;
    }

    /**
     * Returns lesser than and equals condition.
     *
     * @return lesser than and equals condition
     */
    static String getLesserThanCondition() {
        return SPACE + DIAMOND_OPEN_BRACKET + EQUAL + SPACE;
    }

    /**
     * Returns greater than and equals condition.
     *
     * @return greater than and equals condition
     */
    static String getGreaterThanCondition() {
        return SPACE + DIAMOND_CLOSE_BRACKET + EQUAL + SPACE;
    }

    /**
     * Returns && conditional string.
     *
     * @param cond1 condition one
     * @param cond2 condition two
     * @return && conditional string
     */
    static String ifAndAndCondition(String cond1, String cond2) {
        return cond1 + SPACE + AND + AND + SPACE + cond2;
    }

    /**
     * Returns equal equal conditional string.
     *
     * @param cond1 condition one
     * @param cond2 condition two
     * @return equal equal conditional string
     */
    static String ifEqualEqualCondition(String cond1, String cond2) {
        return cond1 + SPACE + EQUAL + EQUAL + SPACE + cond2;
    }

    /**
     * Returns new instance string.
     *
     * @param returnType return type
     * @param varName    variable name
     * @param space      spaces
     * @param value      value
     * @return new instance string
     */
    static String getNewInstance(String returnType, String varName,
                                 String space, String value) {
        return space + returnType + SPACE + varName + SPACE + EQUAL + SPACE +
                NEW + SPACE + returnType + getOpenCloseParaWithValue(value) +
                signatureClose();
    }

    /**
     * Returns return string.
     *
     * @param value value to be returned
     * @param space spaces
     * @return return string
     */
    static String getReturnString(String value, String space) {
        return space + RETURN + SPACE + value;
    }

    /**
     * Returns illegal argument exception string.
     *
     * @param space indentation
     * @return illegal argument exception string
     */
    static String getExceptionThrowString(String space) {
        return space + THROW_NEW + EXCEPTION_STRING;
    }

    /**
     * Returns new line string with spaces.
     *
     * @param space spaces
     * @return new line string with spaces
     */
    static String getNewLineAndSpace(String space) {
        return NEW_LINE + space;
    }

    /**
     * Returns method close string.
     *
     * @param type indentation type
     * @return method close string
     */
    public static String methodClose(IndentationType type) {
        switch (type) {
            case EIGHT_SPACE:
                return EIGHT_SPACE_INDENTATION + CLOSE_CURLY_BRACKET +
                        NEW_LINE;
            case TWELVE_SPACE:
                return TWELVE_SPACE_INDENTATION + CLOSE_CURLY_BRACKET +
                        NEW_LINE;
            case SIXTEEN_SPACE:
                return SIXTEEN_SPACE_INDENTATION + CLOSE_CURLY_BRACKET +
                        NEW_LINE;
            case TWENTY_SPACE:
                return TWENTY_SPACE_INDENTATION + CLOSE_CURLY_BRACKET +
                        NEW_LINE;
            case TWENTY_EIGHT_SPACE:
                return TWENTY_SPACE_INDENTATION + EIGHT_SPACE_INDENTATION +
                        CLOSE_CURLY_BRACKET + NEW_LINE;
            case TWENTY_FOUR_SPACE:
                return TWENTY_SPACE_INDENTATION + FOUR_SPACE_INDENTATION +
                        CLOSE_CURLY_BRACKET + NEW_LINE;
            case FOUR_SPACE:
                return FOUR_SPACE_INDENTATION + CLOSE_CURLY_BRACKET +
                        NEW_LINE;
            default:
                return CLOSE_CURLY_BRACKET + NEW_LINE;
        }
    }

    /**
     * Returns body of the method.
     *
     * @param type            type of method body
     * @param paraName        parameter name
     * @param methodName      method name
     * @param space           space to be given before body
     * @param prefix          prefix for internal method
     * @param paramType       parameter type
     * @param isBuilderSetter is for builder setter
     * @param setterVal       value to set in setter
     * @return body of the method
     */
    static String methodBody(MethodBodyTypes type, String paraName,
                             String methodName,
                             String space, String prefix,
                             String paramType, boolean isBuilderSetter, String setterVal) {
        StringBuilder builder = new StringBuilder();
        String body;
        switch (type) {
            case GETTER:
                return getReturnString(paraName, space);
            case SETTER:
                if (setterVal == null) {
                    setterVal = paraName;
                }
                body = space + THIS + PERIOD + paraName + SPACE + EQUAL + SPACE +
                        setterVal + signatureClose();
                builder.append(body);
                if (isBuilderSetter) {
                    body = getReturnString(THIS + signatureClose(), space);
                    builder.append(body);
                }
                return builder.toString();
            case CONSTRUCTOR:
                return space + paraName + SPACE +
                        EQUAL + SPACE + OBJECT + PERIOD +
                        prefix + methodName +
                        brackets(OPEN_CLOSE_BRACKET, null, null) +
                        signatureClose();
            case DEFAULT_CONSTRUCTOR:
                return EMPTY_STRING;
            case MANAGER_METHODS:
                body = space + YANG_UTILS_TODO + NEW_LINE;
                builder.append(body);
                if (paramType != null) {
                    body = getReturnString(parseTypeForReturnValue(paramType),
                                           space);
                    builder.append(body)
                            .append(signatureClose());
                }
                return builder.toString();
            case OF_METHOD:
                return getReturnString(
                        NEW + SPACE + paraName + getOpenCloseParaWithValue(
                                VALUE) + signatureClose(), space);
            case TO_STRING:
                return getToStringMethodsAddString(space, paraName) + paraName +
                        CLOSE_PARENTHESIS;
            case EQUALS_METHOD:
            default:
                return null;
        }
    }

    /**
     * Returns to string method's add string.
     *
     * @param space    indentation
     * @param paraName parameter name
     * @return to string method's add string
     */
    static String getToStringMethodsAddString(String space, String paraName) {
        return space + PERIOD + ADD_STRING + OPEN_PARENTHESIS +
                getQuotedString(paraName + EQUAL) + SPACE + ADD + SPACE;
    }

    /**
     * Returns end of line.
     *
     * @return end of line
     */
    static String signatureClose() {
        return SEMI_COLON + NEW_LINE;
    }


    /**
     * Returns value assignment.
     *
     * @param param       variable to which value to be assigned
     * @param value       value to be assigned
     * @param indentation indentation of the statement
     * @return value assignment
     */
    public static String valueAssign(String param, String value, String
            indentation) {
        return indentation + param + SPACE + EQUAL + SPACE + value +
                signatureClose();
    }

    /**
     * Returns method signature close for method class type.
     *
     * @param type method class type
     * @return method signature close for method class type
     */
    static String methodSignatureClose(MethodClassTypes type) {
        switch (type) {
            case INTERFACE_TYPE:
                return signatureClose();
            case CLASS_TYPE:
                return SPACE + OPEN_CURLY_BRACKET + NEW_LINE;
            default:
                return null;
        }
    }

    /**
     * Returns method param.
     *
     * @param type type of param
     * @param name name of param
     * @return method param
     */
    private static String methodParam(String type, String name) {
        return type + SPACE + name;
    }

    /**
     * Returns comma followed by a space.
     *
     * @return comma followed by a space
     */
    private static String commaWithSpace() {
        return COMMA + SPACE;
    }

    /**
     * Returns bracket string for the given type.
     *
     * @param type       bracket type
     * @param value      value to be added in brackets
     * @param returnType returns type to be added for value
     * @return bracket for the given type.
     */
    static String brackets(BracketType type, String value,
                           String returnType) {
        String ret = EMPTY_STRING;
        switch (type) {
            case OPEN_CLOSE_BRACKET:
                return OPEN_PARENTHESIS + CLOSE_PARENTHESIS;
            case OPEN_CLOSE_BRACKET_WITH_VALUE:
                return OPEN_PARENTHESIS + value + CLOSE_PARENTHESIS;
            case OPEN_CLOSE_BRACKET_WITH_VALUE_AND_RETURN_TYPE:
                if (returnType != null) {
                    ret = returnType + SPACE;
                }
                return OPEN_PARENTHESIS + ret + value +
                        CLOSE_PARENTHESIS;
            case OPEN_BRACKET_WITH_VALUE:
                ret = EMPTY_STRING;
                if (returnType != null) {
                    ret = returnType + SPACE;
                }
                return OPEN_PARENTHESIS + ret + value +
                        COMMA;
            case CLOSE_BRACKET_WITH_VALUE:
                ret = EMPTY_STRING;
                if (returnType != null) {
                    ret = returnType + SPACE;
                }
                return SPACE + ret + value +
                        CLOSE_PARENTHESIS;
            case OPEN_CLOSE_DIAMOND:
                return DIAMOND_OPEN_BRACKET + DIAMOND_CLOSE_BRACKET;
            case OPEN_CLOSE_DIAMOND_WITH_VALUE:
                return DIAMOND_OPEN_BRACKET + value + DIAMOND_CLOSE_BRACKET;
            default:
                return null;
        }
    }

    /**
     * Returns method signature for multi attribute methods.
     *
     * @param methodName       method name
     * @param prefix           prefix for method
     * @param modifier         modifier for method
     * @param methodReturnType method's return type
     * @param params           parameters
     * @param type             type of method
     * @param space            indentation of method
     * @return method signature for multi attribute methods
     */
    public static String multiAttrMethodSignature(String methodName, String
            prefix, String modifier, String methodReturnType,
                                                  Map<String, String> params,
                                                  MethodClassTypes type,
                                                  String space) {
        StringBuilder methodBuilder = new StringBuilder(space);
        String method = EMPTY_STRING;
        if (modifier != null) {
            method = modifier + SPACE;
        }
        methodBuilder.append(method);
        if (prefix == null) {
            prefix = EMPTY_STRING;
        }
        if (methodReturnType != null) {
            method = methodReturnType + SPACE + prefix + methodName;
        } else {
            method = prefix + methodName;
        }
        //Append method beginning.
        methodBuilder.append(method)
                .append(OPEN_PARENTHESIS);
        for (Map.Entry<String, String> param : params.entrySet()) {
            methodBuilder.append(methodParam(param.getValue(), param.getKey()));
            methodBuilder.append(commaWithSpace());
        }
        String para = methodBuilder.toString();
        String[] array = {SPACE, COMMA};
        para = trimAtLast(para, array);
        methodBuilder = new StringBuilder(para)
                .append(CLOSE_PARENTHESIS)
                .append(methodSignatureClose(type));

        return methodBuilder.toString();
    }

    /**
     * Returns method signature for interface and implementation classes.
     *
     * @param methodName       name of the method
     * @param prefix           prefix which needs to be added for method
     * @param modifier         modifier which needs to be added for method
     * @param paraVal          value which needs to be added as parameter
     * @param methodReturnType returns type to be added for method
     * @param paraReturnType   return type to be added for parameter
     * @param type             method class type
     * @return method signature for interface and implementation classes
     */
    public static String methodSignature(
            String methodName, String prefix, String modifier, String paraVal,
            String methodReturnType, String paraReturnType,
            MethodClassTypes type) {
        StringBuilder methodBuilder = new StringBuilder(FOUR_SPACE_INDENTATION);
        String method = EMPTY_STRING;
        if (modifier != null) {
            method = modifier + SPACE;
        }
        if (prefix == null) {
            prefix = EMPTY_STRING;
        }
        methodBuilder.append(method);
        if (methodReturnType != null) {
            method = methodReturnType + SPACE + prefix + methodName;
        } else {
            method = prefix + methodName;
        }
        //Append method beginning.
        methodBuilder.append(method);

        if (paraVal != null) {
            methodBuilder.append(brackets(
                    OPEN_CLOSE_BRACKET_WITH_VALUE_AND_RETURN_TYPE,
                    paraVal, paraReturnType));
        } else {
            methodBuilder.append(brackets(OPEN_CLOSE_BRACKET, null,
                                          null));
        }

        methodBuilder.append(methodSignatureClose(type));

        return methodBuilder.toString();
    }

    /**
     * Returns list attribute.
     *
     * @param attrType           attribute type
     * @param compilerAnnotation compiler annotations
     * @return list attribute
     */

    static String getListAttribute(String attrType,
                                   YangCompilerAnnotation compilerAnnotation) {
        String listAttr;
        YangDataStructure ds = getYangDataStructure(compilerAnnotation);
        if (ds != null) {
            switch (ds) {
                case QUEUE: {
                    listAttr = QUEUE + DIAMOND_OPEN_BRACKET + attrType +
                            DIAMOND_CLOSE_BRACKET;
                    break;
                }
                case SET: {
                    listAttr = SET + DIAMOND_OPEN_BRACKET + attrType +
                            DIAMOND_CLOSE_BRACKET;
                    break;
                }
                case LIST: {
                    listAttr = getListString() + attrType +
                            DIAMOND_CLOSE_BRACKET;
                    break;
                }
                case MAP:
                    listAttr = MAP + DIAMOND_OPEN_BRACKET + attrType + KEYS +
                            COMMA + attrType + DIAMOND_CLOSE_BRACKET;
                    break;
                default: {
                    listAttr = getListString() + attrType +
                            DIAMOND_CLOSE_BRACKET;
                }
            }
        } else {
            listAttr = getListString() + attrType + DIAMOND_CLOSE_BRACKET;
        }
        return listAttr;
    }

    /**
     * Returns getters for value and select leaf.
     *
     * @return getters for value and select leaf
     */
    static String getIsValueLeafSet() {
        return "\n" +
                "    @Override\n" +
                "    public boolean isLeafValueSet(LeafIdentifier leaf) {\n" +
                "        return valueLeafFlags.get(leaf.getLeafIndex());\n" +
                "    }\n" +
                "\n";
    }

    /**
     * Returns interface leaf identifier enum method.
     *
     * @return interface leaf identifier enum method
     */
    static String getInterfaceLeafIdEnumMethods() {
        return "\n\n        private int leafIndex;\n" +
                "\n" +
                "        public int getLeafIndex() {\n" +
                "            return leafIndex;\n" +
                "        }\n" +
                "\n" +
                "        LeafIdentifier(int value) {\n" +
                "            this.leafIndex = value;\n" +
                "        }\n" +
                "    }\n";
    }

    /**
     * Returns if condition string for typedef constructor.
     *
     * @param type     type of conflict
     * @param addFirst true int/long need to be added first
     * @param val      value to set
     * @return if condition string for typedef constructor
     */
    static String ifConditionForIntInTypeDefConstructor(ValidatorTypeForUnionTypes type,
                                                        boolean addFirst, String val) {
        String condition =
                EIGHT_SPACE_INDENTATION + IF + SPACE + OPEN_PARENTHESIS +
                        VALIDATE_RANGE + OPEN_PARENTHESIS;

        switch (type) {
            case INT_TYPE_CONFLICT:
                if (addFirst) {
                    condition = condition + INT_MIN_RANGE + COMMA + SPACE +
                            INT_MAX_RANGE + COMMA + SPACE + val;
                } else {
                    condition = condition + UINT_MIN_RANGE + COMMA + SPACE +
                            UINT_MAX_RANGE + COMMA + SPACE + val;
                }
                break;
            case LONG_TYPE_CONFLICT:
                if (addFirst) {
                    condition = condition + LONG_MIN_RANGE + COMMA + SPACE +
                            LONG_MAX_RANGE + COMMA + SPACE + val;
                } else {
                    condition = condition + ULONG_MIN_RANGE + COMMA + SPACE +
                            ULONG_MAX_RANGE + COMMA + SPACE + val;
                }
                break;
            case SHORT_TYPE_CONFLICT:
                if (addFirst) {
                    condition = condition + SHORT_MIN_RANGE + COMMA + SPACE +
                            SHORT_MAX_RANGE + COMMA + SPACE + val;
                } else {
                    condition = condition + UINT8_MIN_RANGE + COMMA + SPACE +
                            UINT8_MAX_RANGE + COMMA + SPACE + val;
                }
                break;
            default:
                return null;
        }

        return condition + CLOSE_PARENTHESIS + CLOSE_PARENTHESIS + SPACE +
                OPEN_CURLY_BRACKET + NEW_LINE;
    }

    /**
     * Returns from string method parsed string.
     *
     * @param targetDataType target data type
     * @param yangType       YANG type
     * @return parsed string
     */
    static String getParseFromStringMethod(String targetDataType,
                                           YangType<?> yangType) {

        YangDataTypes type = yangType.getDataType();
        switch (type) {
            case INT8:
                return BYTE_WRAPPER + PERIOD + PARSE_BYTE;

            case INT16:
                return SHORT_WRAPPER + PERIOD + PARSE_SHORT;

            case INT32:
                return INTEGER_WRAPPER + PERIOD + PARSE_INT;

            case INT64:
                return LONG_WRAPPER + PERIOD + PARSE_LONG;

            case UINT8:
                return SHORT_WRAPPER + PERIOD + PARSE_SHORT;

            case UINT16:
                return INTEGER_WRAPPER + PERIOD + PARSE_INT;

            case UINT32:
                return LONG_WRAPPER + PERIOD + PARSE_LONG;

            case UINT64:
                return NEW + SPACE + BIG_INTEGER;

            case DECIMAL64:
                return NEW + SPACE + BIG_DECIMAL;

            case INSTANCE_IDENTIFIER:
            case STRING:
                return EMPTY_STRING;
            case EMPTY:
            case BOOLEAN:
                return BOOLEAN_WRAPPER + PERIOD + PARSE_BOOLEAN;

            case ENUMERATION:
                return targetDataType + PERIOD + OF;

            case IDENTITYREF:
                return getCapitalCase(getCamelCase(
                        getIdentityRefName(yangType), null))
                        + PERIOD + FROM_STRING_METHOD_NAME;
            case DERIVED:
            case UNION:
                return targetDataType + PERIOD + FROM_STRING_METHOD_NAME;

            default:
                throw new TranslatorException(
                        "Given data type is not supported. " +
                                yangType.getDataTypeName() + " in " +
                                yangType.getLineNumber() + " at " +
                                yangType.getCharPosition() + " in " +
                                yangType.getFileName());
        }
    }

    /**
     * Returns sub string with catch statement for union's from string method.
     *
     * @param attrHolder attribute holder/count for from string
     * @return sub string with catch statement for union's from string method
     */
    static String getCatchSubString(String attrHolder) {
        StringBuilder builder = new StringBuilder();
        builder.append(CLOSE_CURLY_BRACKET).append(SPACE).append(CATCH)
                .append(SPACE).append(
                brackets(OPEN_CLOSE_BRACKET_WITH_VALUE_AND_RETURN_TYPE, EXCEPTION_VAR,
                         EXCEPTION)).append(SPACE).append(OPEN_CURLY_BRACKET)
                .append(NEW_LINE);
        if (attrHolder != null) {
            builder.append(getExceptionThrowString(TWELVE_SPACE_INDENTATION));
        }
        builder.append(EIGHT_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET);
        return builder.toString();
    }

    /**
     * Returns sub string with return statement for union's from string method.
     *
     * @return sub string with return statement for union's from string method
     */
    static String getReturnOfSubString() {
        return getReturnString(OF, TWELVE_SPACE_INDENTATION) +
                getOpenCloseParaWithValue(TMP_VAL) + signatureClose();
    }

    /**
     * Returns sub string with try statement for union's from string method.
     *
     * @return sub string with try statement for union's from string method
     */
    static String getTrySubString() {
        return TRY + SPACE + OPEN_CURLY_BRACKET;
    }

    /*
         * Returns omit null value string.
         *
         * @return omit null value string
         */
    static String getOmitNullValueString() {
        return TWELVE_SPACE_INDENTATION + PERIOD + OMIT_NULL_VALUE_STRING +
                NEW_LINE;
    }

    /**
     * Returns collection's iterator method.
     *
     * @param indentation indentation
     * @param loopVar     loop variable
     * @param collection  collection
     * @return collection's iterator method
     */
    static String getCollectionIteratorForLoopBegin(String indentation,
                                                    String loopVar,
                                                    String collection) {
        return indentation + FOR + SPACE + OPEN_PARENTHESIS + loopVar + SPACE +
                COLON + SPACE + collection + CLOSE_PARENTHESIS + SPACE +
                OPEN_CURLY_BRACKET + NEW_LINE;
    }

    /**
     * Returns if else condition's signature.
     *
     * @param indentation indentation
     * @param condition   conditions
     * @return if else condition's signature
     */
    static String getElseIfConditionBegin(String indentation,
                                          String condition) {
        return indentation + CLOSE_CURLY_BRACKET + ELSE +
                getIfConditionBegin(EMPTY_STRING, condition);
    }

    /**
     * Returns if condition's signature.
     *
     * @param indentation indentation
     * @param condition   conditions
     * @return if condition's signature
     */
    static String getIfConditionBegin(String indentation, String condition) {
        return indentation + IF + SPACE + getOpenCloseParaWithValue(condition) +
                methodSignatureClose(CLASS_TYPE);
    }

    /**
     * Returns true, if the data type is primitive; false otherwise.
     *
     * @param dataType data type
     * @return true if the data type is primitive; false otherwise
     */
    public static boolean isPrimitiveDataType(YangDataTypes dataType) {
        return PRIMITIVE_TYPES.contains(dataType);
    }

    /**
     * Returns list string.
     *
     * @return list string
     */
    private static String getListString() {
        return LIST + DIAMOND_OPEN_BRACKET;
    }

    /**
     * Returns override string.
     *
     * @return override string
     */
    public static String getOverRideString() {
        return NEW_LINE + FOUR_SPACE_INDENTATION + OVERRIDE + NEW_LINE;
    }

    /**
     * Returns value leaf flag setter.
     *
     * @param name        name of leaf
     * @param flag        flag to set values
     * @param indentation indentation
     * @param prefix      prefix of method
     * @return value leaf flag setter
     */
    static String getLeafFlagSetString(String name, String flag, String indentation, String prefix) {
        return indentation + flag + PERIOD + prefix +
                getOpenCloseParaWithValue(LEAF_IDENTIFIER + PERIOD + name
                        .toUpperCase() + ".getLeafIndex()");
    }

    /*Provides string to return for type.*/
    private static String parseTypeForReturnValue(String type) {
        switch (type) {
            case BYTE:
            case INT:
            case SHORT:
            case LONG:
            case DOUBLE:
                return ZERO;
            case BOOLEAN_DATA_TYPE:
                return FALSE;
            default:
                return null;
        }
    }

    /**
     * Returns check not null string.
     *
     * @param name attribute name
     * @return check not null string
     */
    static String getCheckNotNull(String name) {
        return EIGHT_SPACE_INDENTATION + CHECK_NOT_NULL_STRING +
                OPEN_PARENTHESIS + name + COMMA + SPACE + name +
                CLOSE_PARENTHESIS + SEMI_COLON + NEW_LINE;
    }

    /**
     * Returns definition close string.
     *
     * @return definition close string
     */
    private static String defCloseString() {
        return SPACE + OPEN_CURLY_BRACKET + NEW_LINE;
    }

    /**
     * Returns default class definition for java file when extends a
     * interface.
     *
     * @param classType class type
     * @param name      name of class
     * @param modifier  modifier for class
     * @param extend    extends class name
     * @return class definition
     */
    static String getDefaultDefinitionWithExtends(String classType,
                                                  String name, String
                                                          modifier,
                                                  String extend) {
        String mod = EMPTY_STRING;
        if (modifier != null) {
            mod = modifier + SPACE;
        }
        return mod + classType + SPACE + name + SPACE + EXTEND + SPACE
                + extend + defCloseString();
    }

    /**
     * Returns default class definition for java file when implements a
     * interface.
     *
     * @param classType class type
     * @param name      name of class
     * @param modifier  modifier for class
     * @param impl      implements class name
     * @return class definition
     */
    public static String getDefaultDefinitionWithImpl(String classType,
                                                      String name,
                                                      String modifier,
                                                      String impl) {
        String mod = EMPTY_STRING;
        if (modifier != null) {
            mod = modifier + SPACE;
        }
        return mod + classType + SPACE + name + SPACE + IMPLEMENTS +
                SPACE + impl + defCloseString();
    }

    /**
     * Returns default class definition for java file.
     *
     * @param classType class type
     * @param name      name of class
     * @param modifier  modifier for class
     * @return class definition
     */
    static String getDefaultDefinition(String classType,
                                       String name, String modifier) {
        String mod = EMPTY_STRING;
        if (modifier != null) {
            mod = modifier + SPACE;
        }
        return mod + classType + SPACE + name + defCloseString();
    }

    /**
     * Returns class definition for implements.
     *
     * @param classType class type
     * @param name      name of class
     * @param modifier  modifier
     * @param implClass implements class
     * @return class definition
     */
    static String getDefinitionWithImplements(String classType, String name,
                                              String modifier, String
                                                      implClass) {
        String mod = EMPTY_STRING;
        if (modifier != null) {
            mod = modifier + SPACE;
        }
        return mod + classType + SPACE + name + SPACE + IMPLEMENTS + SPACE +
                implClass + defCloseString();
    }

    /**
     * Returns class definition for implements.
     *
     * @param classType     class type
     * @param name          name of class
     * @param modifier      modifier
     * @param implClassList list of class which should be implemented
     * @return class definition
     */
    static String getDefinitionWithMultipleImplements(String classType,
                                                      String name,
                                                      String modifier,
                                                      List<String>
                                                              implClassList) {
        String mod = EMPTY_STRING;
        if (modifier != null) {
            mod = modifier + SPACE;
        }
        String classDef = mod + classType + SPACE + name + SPACE
                + IMPLEMENTS + SPACE;
        if (implClassList != null && !implClassList.isEmpty()) {
            Iterator<String> implIterator = implClassList.iterator();
            while (implIterator.hasNext()) {
                String implClass = implIterator.next();
                classDef = classDef + implClass;
                if (implIterator.hasNext()) {
                    classDef = classDef + COMMA + SPACE;
                }
            }
        }
        return classDef + defCloseString();
    }

    /**
     * Returns string for service class.
     *
     * @param name1 name of event listener class
     * @param type name of service
     * @param name2 name of event class
     * @return listener service string for service class
     */
    static String getEventExtendsString(String name1, String type,
                                        String name2) {
        StringBuilder builder = new StringBuilder();
        builder.append(type).append(DIAMOND_OPEN_BRACKET).append(name1)
                .append(COMMA).append(SPACE).append(name2).append(DIAMOND_CLOSE_BRACKET);
        return builder.toString();
    }

    /**
     * Returns import string.
     *
     * @param pkg package
     * @param cls class
     * @return import string
     */
    public static String getImportString(String pkg, String cls) {
        StringBuilder builder = new StringBuilder()
                .append(IMPORT).append(pkg).append(PERIOD).append(cls)
                .append(signatureClose());
        return builder.toString();
    }

    /**
     * Returns static modifier string.
     *
     * @param modifier modifier
     * @param keyWord keyword
     * @return static modifier string
     */
    static String getSpecificModifier(String modifier, String keyWord) {
        return new StringBuilder(modifier).append(SPACE).append(keyWord)
                .toString();
    }

    /**
     * Returns default name string.
     *
     * @param yangName class name
     * @return default name string
     */
    static String getDefaultName(String yangName) {
        return new StringBuilder(DEFAULT_CAPS).append(yangName).toString();
    }

    /**
     * Returns suffixed name string.
     *
     * @param yangName class name
     * @param suffix   suffix to append to name
     * @return suffixed name string
     */
    static String getSuffixedName(String yangName, String suffix) {
        return new StringBuilder(yangName).append(suffix).toString();
    }

    /**
     * Returns error msg string.
     *
     * @param msg      message
     * @param name     name of node
     * @param line     line number
     * @param position char position
     * @param fileName file name
     * @return error message string
     */
    static String getErrorMsg(String msg, String name, int line, int position,
                              String fileName) {
        return new StringBuilder().append(msg).append(name).append(IN)
                .append(line).append(AT).append(position).append(IN)
                .append(fileName).toString();
    }

    /**
     * Returns string builder attribute string.
     *
     * @param init  first param to be appended to string builder
     * @param space indentation space
     * @return string builder attribute
     */
    static String getStringBuilderAttr(String init, String space) {
        StringBuilder builder = new StringBuilder(space);
        builder.append(STRING_BUILDER).append(SPACE).append(STRING_BUILDER_VAR)
                .append(SPACE).append(EQUAL).append(SPACE).append(NEW)
                .append(SPACE).append(STRING_BUILDER).append(
                getOpenCloseParaWithValue(getQuotedString(init)))
                .append(signatureClose());
        return builder.toString();
    }

    /**
     * Returns quoted string.
     *
     * @param name name to be quoted
     * @return quoted string
     */
    static String getQuotedString(String name) {
        return QUOTES + name + QUOTES;
    }

    /**
     * Returns pattern quote string.
     *
     * @param type type for pattern is needed
     * @return pattern quote string
     */
    static String getPatternQuoteString(String type) {
        return PATTERN + PERIOD + QUOTE_STRING + getOpenCloseParaWithValue(
                getQuotedString(type));
    }

    /**
     * Returns bitset attribute.
     *
     * @param indentation indentation
     * @return bitset attribute
     */
    static String getBitSetAttr(String indentation) {
        StringBuilder builder = new StringBuilder(indentation);
        return builder.append(BITSET).append(SPACE).append(TMP_VAL)
                .append(SPACE).append(EQUAL).append(SPACE).append(NEW)
                .append(SPACE).append(BITSET).append(OPEN_CLOSE_BRACKET_STRING)
                .append(signatureClose()).toString();
    }

    /**
     * Returns for loop string.
     *
     * @param space indentation
     * @param type  data type
     * @param var   variable
     * @param data  data variable/collection
     * @return for loop string
     */
    public static String getForLoopString(String space, String type, String var,
                                          String data) {
        return space + FOR + SPACE + OPEN_PARENTHESIS + type + SPACE + var +
                SPACE + COLON + SPACE + data + CLOSE_PARENTHESIS +
                methodSignatureClose(CLASS_TYPE);
    }

    /**
     * Returns set value parameter's get string for union to string method.
     *
     * @param count count of type
     * @return get string
     */
    static String getSetValueParaCondition(int count) {
        return SET_VALUE_PARA + PERIOD + GET + getOpenCloseParaWithValue(
                count + EMPTY_STRING);
    }

    /**
     * Returns to string call.
     *
     * @param name name of attribute
     * @return to string call for attribute
     */
    static String getToStringCall(String name) {
        return name + PERIOD +
                TO_STRING_METHOD + OPEN_CLOSE_BRACKET_STRING;
    }

    /**
     * Returns value in brackets.
     *
     * @param name value
     * @return value in brackets
     */
    static String getOpenCloseParaWithValue(String name) {
        return brackets(OPEN_CLOSE_BRACKET_WITH_VALUE, name, null);
    }

    /**
     * Returns equals comparision.
     *
     * @param para1 param
     * @param para2 param
     * @return equals comparision
     */
    static String getTwoParaEqualsString(String para1, String para2) {
        return para1 + PERIOD + EQUALS_STRING + getOpenCloseParaWithValue(para2);
    }

    /**
     * Returns equal equal condition.
     *
     * @param para param
     * @param val  value
     * @return equal equal condition
     */
    static String getEqualEqualString(String para, String val) {
        return para + SPACE + EQUAL + EQUAL + SPACE + val;
    }

    /**
     * Returns app instance method call.
     *
     * @param name attr name
     * @return app instance method call
     */
    static String getAppInstanceAttrString(String name) {
        return APP_INSTANCE + PERIOD + name + OPEN_CLOSE_BRACKET_STRING;
    }

    /**
     * Returns qualified name.
     *
     * @param pkg package
     * @param cls class info
     * @return qualified name
     */
    static String getQualifiedString(String pkg, String cls) {
        return pkg + PERIOD + cls;
    }

    /**
     * Returns new instance string.
     *
     * @param className  class name
     * @param space      indentation
     * @param parameters parameters for constructor
     * @return new instance string
     */
    public static String createNewInstance(String className, String space,
                                           List<String> parameters) {
        StringBuilder builder = new StringBuilder();
        builder.append(space).append(NEW).append(SPACE).append(className)
                .append(OPEN_PARENTHESIS);

        if (parameters != null && !parameters.isEmpty()) {
            Iterator<String> iterator = parameters.iterator();
            while (iterator.hasNext()) {
                builder.append(iterator.next());
                if (iterator.hasNext()) {
                    builder.append(COMMA).append(SPACE);
                }
            }
        }

        builder.append(CLOSE_PARENTHESIS);
        return builder.toString();
    }

    /**
     * Returns variable declaration string.
     *
     * @param varName  name of variable
     * @param varType  type of variable
     * @param space    indentation of the statement
     * @param modifier modifier name
     * @param value    value to intialize the variable
     * @return return variable declaration string
     */
    public static String getVariableDeclaration(String varName, String varType,
                                                String space, String modifier,
                                                String value) {
        StringBuilder builder = new StringBuilder(space);
        if (modifier != null) {
            builder.append(modifier).append(SPACE);
        }
        builder.append(varType).append(SPACE).append(varName);

        // variable intialization
        if (value != null) {
            builder.append(SPACE).append(EQUAL).append(SPACE).append(value);
        }
        builder.append(SEMI_COLON).append(NEW_LINE);

        return builder.toString();
    }

    /**
     * Returns abstract class definition for java file when extends a
     * interface.
     *
     * @param classType class type
     * @param name      name of class
     * @param modifier  modifier for class
     * @param extend    extends class name
     * @return abstract class definition
     */
    static String getAbstractClassDefinitionWithExtends(String classType,
                                                        String name, String
                                                                modifier,
                                                        String extend) {
        String mod = EMPTY_STRING;
        if (modifier != null) {
            mod = modifier + SPACE;
        }
        return mod + ABSTRACT + SPACE + classType + SPACE + name + SPACE +
                EXTEND + SPACE
                + extend + defCloseString();
    }

    /**
     * Returns static import string.
     *
     * @param pkg package
     * @param cls class
     * @return static import string
     */
    public static String getStaticImportString(String pkg, String cls) {
        StringBuilder builder = new StringBuilder()
                .append(IMPORT).append(STATIC).append(SPACE)
                .append(pkg).append(PERIOD).append(cls).append(signatureClose());
        return builder.toString();
    }

    /**
     * Returns setter string for interface.
     *
     * @param name     class name
     * @param attrName attribute name
     * @param attrType attribute type
     * @param genType java file generation type
     * @return setter string
     */
    static String getSetterInterfaceString(String name,
                                           String attrName,
                                           String attrType,
                                           int genType) {
        if (genType == GENERATE_SERVICE_AND_MANAGER) {
            return methodSignature(getCapitalCase(attrName), SET_METHOD_PREFIX,
                                   null, attrName, VOID, attrType + OP_PARAM,
                                   INTERFACE_TYPE);
        }
        return methodSignature(attrName, EMPTY_STRING, null,
                               attrName, VOID, attrType, INTERFACE_TYPE);
    }
}
