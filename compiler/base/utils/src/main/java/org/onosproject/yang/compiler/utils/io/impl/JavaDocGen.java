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

package org.onosproject.yang.compiler.utils.io.impl;

import static org.onosproject.yang.compiler.utils.UtilConstants.ADD_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.ENUM_ATTRIBUTE_JAVADOC;
import static org.onosproject.yang.compiler.utils.UtilConstants.ENUM_CLASS_JAVADOC;
import static org.onosproject.yang.compiler.utils.UtilConstants.EVENT_JAVA_DOC;
import static org.onosproject.yang.compiler.utils.UtilConstants.EVENT_LISTENER_JAVA_DOC;
import static org.onosproject.yang.compiler.utils.UtilConstants.FOUR_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.FROM_STRING_METHOD_NAME;
import static org.onosproject.yang.compiler.utils.UtilConstants.FROM_STRING_PARAM_NAME;
import static org.onosproject.yang.compiler.utils.UtilConstants.IMPL_CLASS_JAVA_DOC;
import static org.onosproject.yang.compiler.utils.UtilConstants.INPUT;
import static org.onosproject.yang.compiler.utils.UtilConstants.INTERFACE_JAVA_DOC;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_ADD_TO_LIST;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_CONSTRUCTOR;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_END_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_FIRST_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_FOR_VALIDATOR;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_FOR_VALIDATOR_RETURN;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_GETTERS;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_MANAGER_SETTERS;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_OF;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_REGISTER_RPC;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_RETURN;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_RPC;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_RPC_EXECUTER;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_RPC_EXTENDED_CMD;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_RPC_HANDLER;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_SETTERS;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_DOC_SETTERS_COMMON;
import static org.onosproject.yang.compiler.utils.UtilConstants.KEYS;
import static org.onosproject.yang.compiler.utils.UtilConstants.LIST;
import static org.onosproject.yang.compiler.utils.UtilConstants.MAP;
import static org.onosproject.yang.compiler.utils.UtilConstants.MAX_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.MIN_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE_ASTERISK;
import static org.onosproject.yang.compiler.utils.UtilConstants.OBJECT;
import static org.onosproject.yang.compiler.utils.UtilConstants.OF;
import static org.onosproject.yang.compiler.utils.UtilConstants.OP_PARAM_JAVA_DOC;
import static org.onosproject.yang.compiler.utils.UtilConstants.PACKAGE_INFO_JAVADOC;
import static org.onosproject.yang.compiler.utils.UtilConstants.PACKAGE_INFO_JAVADOC_OF_CHILD;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUEUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_INPUT_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_OUTPUT_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.SET;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.STRING_DATA_TYPE;
import static org.onosproject.yang.compiler.utils.UtilConstants.TO_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALIDATE_RANGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.VOID;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getSmallCase;

/**
 * Represents javadoc for the generated classes.
 */
public final class JavaDocGen {

    /**
     * Creates an instance of java doc gen.
     */
    private JavaDocGen() {
    }

    /**
     * Returns java docs.
     *
     * @param type               java doc type
     * @param name               name of the YangNode
     * @param isList             is list attribute
     * @param compilerAnnotation compiler annotations for user defined data type
     * @return javaDocs.
     */
    public static String getJavaDoc(JavaDocType type, String name, boolean isList,
                                    String compilerAnnotation) {

        name = getSmallCase(name);
        switch (type) {
            case DEFAULT_CLASS: {
                return generateForClass(name, isList);
            }
            case OPERATION_CLASS: {
                return generateForOpParamClass(name);
            }
            case INTERFACE: {
                return generateForInterface(name);
            }
            case PACKAGE_INFO: {
                return generateForPackage(name, isList);
            }
            case GETTER_METHOD: {
                return generateForGetters(name, isList, compilerAnnotation);
            }
            case TYPE_DEF_SETTER_METHOD: {
                return generateForTypeDefSetter(name);
            }
            case SETTER_METHOD: {
                return generateForSetters(name, isList, compilerAnnotation);
            }
            case MANAGER_SETTER_METHOD: {
                return generateForManagerSetters(name, isList, compilerAnnotation);
            }
            case OF_METHOD: {
                return generateForOf(name);
            }
            case DEFAULT_CONSTRUCTOR: {
                return generateForDefaultConstructors(name);
            }
            case TYPE_CONSTRUCTOR: {
                return generateForTypeConstructor(name);
            }
            case FROM_METHOD: {
                return generateForFromString(name);
            }
            case ENUM_CLASS: {
                return generateForEnum(name);
            }
            case ENUM_ATTRIBUTE: {
                return generateForEnumAttr(name);
            }
            case RPC_INTERFACE: {
                return generateForRpcService(name);
            }
            case RPC_MANAGER: {
                return generateForClass(name, false);
            }
            case EVENT: {
                return generateForEvent(name);
            }
            case EVENT_LISTENER: {
                return generateForEventListener(name);
            }
            case EVENT_SUBJECT_CLASS: {
                return generateForClass(name, false);
            }
            case ADD_TO_LIST: {
                return generateForAddToList(name, compilerAnnotation);
            }
            case RPC_HANDLER: {
                return generateForClass(JAVA_DOC_RPC_HANDLER);
            }
            case RPC_COMMAND: {
                return generateForClass(name);
            }
            case REGISTER_RPC: {
                return generateForClass(JAVA_DOC_REGISTER_RPC);
            }
            case RPC_EXECUTER: {
                return getJavaDocWithoutParam(JAVA_DOC_RPC_EXECUTER,
                                              EMPTY_STRING);
            }
            case RPC_EXTENDED_CMD: {
                return getJavaDocWithoutParam(JAVA_DOC_RPC_EXTENDED_CMD,
                                              EMPTY_STRING);
            }
            default: {
                return generateForConstructors(name);
            }
        }
    }

    /**
     * Generates javaDocs for enum's attributes.
     *
     * @param name attribute name
     * @return javaDocs
     */
    private static String generateForEnumAttr(String name) {
        return getJavaDocForClass(name, ENUM_ATTRIBUTE_JAVADOC,
                                  FOUR_SPACE_INDENTATION);
    }

    /**
     * Generates javaDocs for inner class enum's attributes.
     *
     * @param name attribute name
     * @return javaDocs
     */
    public static String enumJavaDocForInnerClass(String name) {
        return getJavaDocForClass(name, ENUM_ATTRIBUTE_JAVADOC,
                                  EIGHT_SPACE_INDENTATION);
    }

    /**
     * Generates javaDocs for rpc method.
     *
     * @param rpcName    name of the rpc
     * @param inputName  name of input
     * @param outputName name of output
     * @return javaDocs of rpc method
     */
    public static String generateJavaDocForRpc(String rpcName, String inputName,
                                               String outputName) {

        String javadoc = getJavaDocStartLine(rpcName, JAVA_DOC_RPC) +
                getJavaDocEmptyAsteriskLine();
        if (!inputName.equals(EMPTY_STRING)) {
            javadoc = javadoc + getInputString(inputName, rpcName);
        }
        if (!outputName.equals(VOID)) {
            javadoc = javadoc + getOutputString(getSmallCase(outputName),
                                                rpcName);
        }
        return javadoc + getJavaDocEndLine();
    }

    /**
     * Returns output string of rpc.
     *
     * @param outputName name of output
     * @param rpcName    name of rpc
     * @return javaDocs for output string of rpc
     */
    private static String getOutputString(String outputName, String rpcName) {
        return FOUR_SPACE_INDENTATION + JAVA_DOC_RETURN + outputName + SPACE +
                RPC_OUTPUT_STRING + rpcName + NEW_LINE;
    }

    /**
     * Returns input string of rpc.
     *
     * @param inputName name of input
     * @param rpcName   name of rpc
     * @return javaDocs for input string of rpc
     */
    private static String getInputString(String inputName, String rpcName) {
        if (inputName.isEmpty()) {
            return null;
        } else {
            return FOUR_SPACE_INDENTATION + JAVA_DOC_PARAM + inputName +
                    SPACE + RPC_INPUT_STRING + rpcName + NEW_LINE;
        }
    }

    /**
     * Generates javaDoc for the interface.
     *
     * @param interfaceName interface name
     * @return javaDocs
     */
    private static String generateForRpcService(String interfaceName) {
        return getJavaDocForClass(interfaceName, INTERFACE_JAVA_DOC, EMPTY_STRING);
    }

    /**
     * Generates javaDoc for the event.
     *
     * @param name event class name
     * @return javaDocs
     */
    private static String generateForEvent(String name) {
        return getJavaDocForClass(name, EVENT_JAVA_DOC, EMPTY_STRING);
    }

    /**
     * Generates javaDoc for the event listener.
     *
     * @param name event class name
     * @return javaDocs
     */
    private static String generateForEventListener(String name) {
        return getJavaDocForClass(name, EVENT_LISTENER_JAVA_DOC, EMPTY_STRING);
    }

    /**
     * Generates javaDocs for getter method.
     *
     * @param attribute          attribute
     * @param isList             is list attribute
     * @param compilerAnnotation compiler annotation
     * @return javaDocs
     */
    private static String generateForGetters(String attribute, boolean isList,
                                             String compilerAnnotation) {

        String getter = getJavaDocStartLine(attribute, JAVA_DOC_GETTERS) +
                getJavaDocEmptyAsteriskLine() +
                FOUR_SPACE_INDENTATION + JAVA_DOC_RETURN + attribute + SPACE;

        getter = getParamForAnnotation(getter, compilerAnnotation, isList) +
                attribute + NEW_LINE + getJavaDocEndLine();
        return getter;
    }

    /**
     * Generates javaDocs for setter method.
     *
     * @param attribute          attribute
     * @param isList             is list attribute
     * @param compilerAnnotation compiler annotation
     * @return javaDocs
     */
    private static String generateForSetters(String attribute, boolean isList,
                                             String compilerAnnotation) {

        String setter = getJavaDocStartLine(attribute, JAVA_DOC_SETTERS) +
                getJavaDocEmptyAsteriskLine() +
                FOUR_SPACE_INDENTATION + JAVA_DOC_PARAM + attribute + SPACE;
        setter = getParamForAnnotation(setter, compilerAnnotation, isList) +
                attribute + NEW_LINE + getJavaDocEndLine();
        return setter;
    }

    /**
     * Generates javaDocs for setter method.
     *
     * @param attribute          attribute
     * @param isList             is list attribute
     * @param compilerAnnotation compiler annotation
     * @return javaDocs
     */
    private static String generateForManagerSetters(String attribute, boolean isList,
                                                    String compilerAnnotation) {
        String setter = getJavaDocStartLine(attribute, JAVA_DOC_MANAGER_SETTERS) +
                getJavaDocEmptyAsteriskLine() +
                FOUR_SPACE_INDENTATION + JAVA_DOC_PARAM + attribute + SPACE;
        setter = getParamForAnnotation(setter, compilerAnnotation, isList) +
                attribute + NEW_LINE + getJavaDocEndLine();
        return setter;
    }

    private static String getParamForAnnotation(
            String setter, String compilerAnnotation, boolean isList) {
        String attributeParam;
        if (compilerAnnotation != null) {
            compilerAnnotation = compilerAnnotation.toLowerCase();
            compilerAnnotation = getCapitalCase(compilerAnnotation);
            switch (compilerAnnotation) {
                case QUEUE: {
                    attributeParam = QUEUE.toLowerCase() + SPACE + OF + SPACE;
                    setter = setter + attributeParam;
                    break;
                }
                case SET: {
                    attributeParam = SET.toLowerCase() + SPACE + OF + SPACE;
                    setter = setter + attributeParam;
                    break;
                }
                case LIST: {
                    attributeParam = LIST.toLowerCase() + SPACE + OF + SPACE;
                    setter = setter + attributeParam;
                    break;
                }
                case MAP:
                    attributeParam = MAP.toLowerCase() + SPACE + OF + SPACE;
                    setter = setter + attributeParam;
                    break;
                default: {

                }
            }
        } else if (isList) {
            attributeParam = LIST.toLowerCase() + SPACE + OF + SPACE;
            setter = setter + attributeParam;
        } else {
            setter = setter + VALUE + SPACE + OF + SPACE;
        }
        return setter;
    }

    /**
     * Generates javaDocs for of method.
     *
     * @param attribute attribute
     * @return javaDocs
     */
    private static String generateForOf(String attribute) {
        return getJavaDocStartLine(attribute, JAVA_DOC_OF) +
                getJavaDocEmptyAsteriskLine() +
                getJavaDocParamLine(attribute, VALUE) +
                getJavaDocReturnLine(attribute) +
                getJavaDocEndLine();
    }

    /**
     * Generates javaDocs for from method.
     *
     * @param attribute attribute
     * @return javaDocs
     */
    private static String generateForFromString(String attribute) {
        return getJavaDocStartLine(attribute, JAVA_DOC_OF
                + attribute + SPACE + FROM_STRING_METHOD_NAME + SPACE + INPUT +
                SPACE + STRING_DATA_TYPE + SPACE) +
                getJavaDocEmptyAsteriskLine() +
                getJavaDocParamLine(INPUT + SPACE + STRING_DATA_TYPE,
                                    FROM_STRING_PARAM_NAME) +
                getJavaDocReturnLine(attribute) +
                getJavaDocEndLine();
    }

    /**
     * Generates javaDocs for typedef setter method.
     *
     * @param attribute attribute
     * @return javaDocs
     */
    private static String generateForTypeDefSetter(String attribute) {
        return getJavaDocStartLine(attribute, JAVA_DOC_SETTERS_COMMON) +
                getJavaDocEmptyAsteriskLine() +
                getJavaDocParamLine(attribute, VALUE) +
                getJavaDocEndLine();
    }

    /**
     * Generates javaDocs for the impl class.
     *
     * @param className         class name
     * @param isForDefaultClass if javadoc is for default class
     * @return javaDocs
     */
    private static String generateForClass(String className, boolean isForDefaultClass) {
        return getJavaDocForDefaultClass(className, IMPL_CLASS_JAVA_DOC,
                                         EMPTY_STRING, isForDefaultClass);
    }

    /**
     * Generates javaDocs for the impl class.
     *
     * @param className class name
     * @return javaDocs javadocs in string
     */
    private static String generateForClass(String className) {
        return NEW_LINE + JAVA_DOC_FIRST_LINE + IMPL_CLASS_JAVA_DOC +
                className + PERIOD + NEW_LINE + JAVA_DOC_END_LINE;
    }

    private static String addFlagJavaDoc() {
        return " *\n" +
                " * <p>\n" +
                " * valueLeafFlags identify the leafs whose value are " +
                "explicitly set\n" +
                " * Applicable in protocol edit and query operation.\n" +
                " * </p>\n";
    }

    /**
     * Generates javaDocs for enum.
     *
     * @param className enum class name
     * @return javaDocs
     */
    private static String generateForEnum(String className) {
        return getJavaDocForClass(className, ENUM_CLASS_JAVADOC, EMPTY_STRING);
    }

    /**
     * Generates javaDocs for the op param class.
     *
     * @param className class name
     * @return javaDocs
     */
    private static String generateForOpParamClass(String className) {
        return getJavaDocForClass(className, OP_PARAM_JAVA_DOC,
                                  EMPTY_STRING);
    }

    /**
     * Generates javaDoc for the interface.
     *
     * @param interfaceName interface name
     * @return javaDocs
     */
    private static String generateForInterface(String interfaceName) {
        return getJavaDocForClass(interfaceName, INTERFACE_JAVA_DOC,
                                  EMPTY_STRING);
    }

    /**
     * Generates javaDocs for package-info.
     *
     * @param packageName package name
     * @param isChildNode is it child node
     * @return javaDocs
     */
    private static String generateForPackage(String packageName, boolean isChildNode) {
        if (isChildNode) {
            return getJavaDocForClass(
                    packageName + PACKAGE_INFO_JAVADOC_OF_CHILD,
                    PACKAGE_INFO_JAVADOC, EMPTY_STRING);
        }
        return getJavaDocForClass(packageName, PACKAGE_INFO_JAVADOC, EMPTY_STRING);
    }

    /**
     * Generates javaDocs for default constructor.
     *
     * @param className class name
     * @return javaDocs
     */
    private static String generateForDefaultConstructors(String className) {
        return getJavaDocStartLine(className, JAVA_DOC_CONSTRUCTOR) +
                getJavaDocEndLine();
    }

    /**
     * Generates javaDocs for constructor with parameters.
     *
     * @param className class name
     * @return javaDocs
     */
    private static String generateForConstructors(String className) {
        return getJavaDocStartLine(className, JAVA_DOC_CONSTRUCTOR) +
                getJavaDocEmptyAsteriskLine() +
                getJavaDocParamLine(className, OBJECT) + getJavaDocEndLine();
    }

    /**
     * Generates javaDocs for type constructor.
     *
     * @param attribute attribute string
     * @return javaDocs for type constructor
     */
    private static String generateForTypeConstructor(String attribute) {
        return getJavaDocStartLine(attribute,
                                   JAVA_DOC_CONSTRUCTOR) +
                getJavaDocEmptyAsteriskLine() +
                getJavaDocParamLine(attribute, attribute) +
                getJavaDocEndLine();
    }

    /**
     * Returns javadoc for validator method.
     *
     * @return javadoc for validator method
     */
    public static String generateForValidatorMethod() {
        return getJavaDocStartLine(VALIDATE_RANGE, JAVA_DOC_FOR_VALIDATOR) +
                getJavaDocEmptyAsteriskLine() +
                getJavaDocParamLine(MIN_RANGE, MIN_RANGE) +
                getJavaDocParamLine(MAX_RANGE, MIN_RANGE) +
                getJavaDocReturnLine(JAVA_DOC_FOR_VALIDATOR_RETURN) +
                getJavaDocEndLine();
    }

    /**
     * Generates javaDocs for type constructor.
     *
     * @param attribute attribute string
     * @return javaDocs for type constructor
     */
    public static String generateForGetMethodWithAttribute(String attribute) {
        attribute = getSmallCase(attribute);
        return getJavaDocStartLine(attribute, JAVA_DOC_GETTERS) +
                getJavaDocEmptyAsteriskLine() +
                getJavaDocParamLine(attribute, attribute) +
                getJavaDocReturnLine(attribute) +
                getJavaDocEndLine();
    }

    /**
     * Returns javaDocs for add to list method.
     *
     * @param attribute  attribute
     * @param annotation compile annotation
     * @return javaDocs
     */
    private static String generateForAddToList(String attribute, String annotation) {
        StringBuilder javadoc = new StringBuilder();
        javadoc.append(getJavaDocStartLine(attribute, JAVA_DOC_ADD_TO_LIST))
                .append(getJavaDocEmptyAsteriskLine());
        if (annotation != null) {
            annotation = annotation.toLowerCase();
            annotation = getCapitalCase(annotation);
            switch (annotation) {
                case MAP:
                    javadoc.append(getJavaDocParamLine(
                            attribute, attribute + KEYS)).append(getJavaDocParamLine(
                            attribute, attribute + VALUE_CAPS));
                    break;
                default:
                    javadoc.append(getJavaDocParamLine(
                            attribute, ADD_STRING + TO_CAPS));
                    break;
            }
        }
        javadoc.append(getJavaDocParamLine(attribute, ADD_STRING + TO_CAPS))
                .append(getJavaDocEndLine());
        return javadoc.toString();
    }

    /**
     * Returns class javadoc.
     *
     * @param name   name of class
     * @param type   type of javadoc
     * @param indent indentation
     * @return class javadoc
     */
    private static String getJavaDocForClass(String name, String type,
                                             String indent) {
        return NEW_LINE + indent + JAVA_DOC_FIRST_LINE + indent + type +
                getSmallCase(name) + PERIOD + NEW_LINE + indent + JAVA_DOC_END_LINE;
    }

    /**
     * Returns class javadoc.
     *
     * @param name              name of class
     * @param type              type of javadoc
     * @param indent            indentation
     * @param isForDefaultClass if javadoc is generated for default class
     * @return class javadoc
     */
    private static String getJavaDocForDefaultClass(String name, String type,
                                                    String indent, boolean isForDefaultClass) {
        String append = addFlagJavaDoc();
        if (!isForDefaultClass) {
            append = EMPTY_STRING;
        }
        return NEW_LINE + indent + JAVA_DOC_FIRST_LINE + indent + type +
                getSmallCase(name) + PERIOD + NEW_LINE + indent + append +
                JAVA_DOC_END_LINE;
    }

    /**
     * Returns javadoc start line.
     *
     * @param name    name of attribute
     * @param javaDoc type of javadoc
     * @return javadoc start line
     */
    private static String getJavaDocStartLine(String name, String javaDoc) {
        return FOUR_SPACE_INDENTATION + JAVA_DOC_FIRST_LINE +
                FOUR_SPACE_INDENTATION + javaDoc + getSmallCase(name) +
                PERIOD + NEW_LINE;
    }

    /**
     * Returns asterisk line.
     *
     * @return asterisk line
     */
    private static String getJavaDocEmptyAsteriskLine() {
        return FOUR_SPACE_INDENTATION + NEW_LINE_ASTERISK;
    }

    /**
     * Returns javadoc param line.
     *
     * @param name name of attribute
     * @return javadoc param line
     */
    private static String getJavaDocParamLine(String name, String paraName) {
        return FOUR_SPACE_INDENTATION + JAVA_DOC_PARAM +
                getSmallCase(paraName) + SPACE + VALUE + SPACE + OF + SPACE +
                getSmallCase(name) + NEW_LINE;
    }

    /**
     * Returns javadoc return line.
     *
     * @param name name of attribute
     * @return javadoc return line
     */
    private static String getJavaDocReturnLine(String name) {
        return FOUR_SPACE_INDENTATION + JAVA_DOC_RETURN + getSmallCase(name)
                + NEW_LINE;
    }

    /**
     * Returns javadoc end line.
     *
     * @return javadoc end line
     */
    private static String getJavaDocEndLine() {
        return FOUR_SPACE_INDENTATION + JAVA_DOC_END_LINE;
    }

    /**
     * Returns data in javadoc format.
     *
     * @param data  content of javadoc
     * @param space indendation for javadoc
     * @return data in javadoc format
     */
    public static String getJavaDocWithoutParam(String data, String space) {
        StringBuilder builder = new StringBuilder(NEW_LINE);
        builder.append(space).append(JAVA_DOC_FIRST_LINE).append(SPACE)
                .append(data).append(PERIOD).append(NEW_LINE)
                .append(space).append(JAVA_DOC_END_LINE);
        return builder.toString();
    }

    /**
     * Returns javadoc for RPC executer constructor.
     *
     * @return javadoc for RPC executer constructor
     */
    public static String getJavaDocForRpcExecuterConstructor() {
        return "\n        /**\n" +
                "         * Constructs a RPC executor for the given msg id, RPC command and\n" +
                "         * RPC input.\n" +
                "         *\n" +
                "         * @param msgId msgId of the RPC message to be executed\n" +
                "         * @param cmd RPC command to be executed\n" +
                "         * @param input input data to the RPC command\n" +
                "         */";
    }

    /**
     * Returns javadoc for RPC command constructor.
     *
     * @param rpcName name of RPC
     * @return javadoc for RPC command constructor
     */
    public static String getJavaDocForRpcCommandConstructor(String rpcName) {
        return "    /**\n" +
                "     * Constructs a " + rpcName + " command.\n" +
                "     * @param cfgService dynamic config service\n" +
                "     * @param modelConverter model converter for convertion\n" +
                "     * @param appService application service\n     */";
    }

    /**
     * Returns javadoc for RPC extended command constructor.
     *
     * @return javadoc for RPC extended command constructor
     */
    public static String getJavaDocForRpcExtendedCommandConstructor() {
        return "    /**\n" +
                "     * Creates an instance of RPC extended command.\n" +
                "     *\n" +
                "     * @param cmdId of RPC command\n" +
                "     */";
    }

    /**
     * Returns javadoc for RPC extended command execute method.
     *
     * @return javadoc for RPC extended command execute method
     */
    public static String getJavaDocForExtendedExecuteMethod() {
        return "    /**\n" +
                "     * Executes the RPC command.\n" +
                "     *\n" +
                "     * @param rpcInput input data to the RPC command\n" +
                "     * @param msgId of the RPC message to be executed\n" +
                "     */";
    }

    /**
     * Returns javadoc for execute method.
     *
     * @return javadoc for execute method
     */
    public static String getJavaDocForExecuteMethod() {
        return "    /**\n" +
                "     * Executes the RPC command.\n" +
                "     *\n" +
                "     * @param rpcInput input data to the RPC command\n" +
                "     * @param msgId msgId of the RPC message to be executed\n" +
                "     */";
    }

    /**
     * Returns javadoc for register RPC constructor.
     *
     * @return javadoc for register RPC constructor
     */
    public static String getJavadocForRegisterRpcConstructor() {
        return "    /**\n" +
                "     * Constructs a register rpc for the given cfg service, " +
                "model converter and\n" +
                "     * application service.\n" +
                "     *\n" +
                "     * @param cfgService dynamic config service\n" +
                "     * @param modelConverter model converter for convertion\n" +
                "     * @param appService application service\n" +
                "     */";
    }

    /**
     * JavaDocs types.
     */
    public enum JavaDocType {

        /**
         * For class.
         */
        DEFAULT_CLASS,

        /**
         * For interface.
         */
        INTERFACE,

        /**
         * For package-info.
         */
        PACKAGE_INFO,

        /**
         * For getters.
         */
        GETTER_METHOD,

        /**
         * For rpc service.
         */
        RPC_INTERFACE,

        /**
         * For rpc manager.
         */
        RPC_MANAGER,

        /**
         * For event.
         */
        EVENT,

        /**
         * For event listener.
         */
        EVENT_LISTENER,

        /**
         * For setters.
         */
        SETTER_METHOD,

        /**
         * For type def's setters.
         */
        TYPE_DEF_SETTER_METHOD,

        /**
         * For of method.
         */
        OF_METHOD,

        /**
         * For default constructor.
         */
        DEFAULT_CONSTRUCTOR,

        /**
         * For constructor.
         */
        CONSTRUCTOR,

        /**
         * For from method.
         */
        FROM_METHOD,

        /**
         * For type constructor.
         */
        TYPE_CONSTRUCTOR,

        /**
         * For build.
         */
        BUILD_METHOD,

        /**
         * For enum.
         */
        ENUM_CLASS,

        /**
         * For enum's attributes.
         */
        ENUM_ATTRIBUTE,

        /**
         * For manager setters.
         */
        MANAGER_SETTER_METHOD,

        /**
         * For event subject.
         */
        EVENT_SUBJECT_CLASS,

        /**
         * For operation.
         */
        OPERATION_CLASS,

        /**
         * For add to list.
         */
        ADD_TO_LIST,

        /**
         * For RPC handler.
         */
        RPC_HANDLER,

        /**
         * For RPC command.
         */
        RPC_COMMAND,

        /**
         * For register RPC.
         */
        REGISTER_RPC,

        /**
         * For RPC executer.
         */
        RPC_EXECUTER,

        /**
         * For RPC extended command.
         */
        RPC_EXTENDED_CMD
    }
}
