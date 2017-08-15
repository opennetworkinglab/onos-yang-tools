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

import org.onosproject.yang.compiler.datamodel.RpcNotificationContainer;
import org.onosproject.yang.compiler.datamodel.YangCase;
import org.onosproject.yang.compiler.datamodel.YangIdentity;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoContainer;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yang.compiler.translator.tojava.JavaQualifiedTypeInfoTranslator;
import org.onosproject.yang.compiler.translator.tojava.TempJavaCodeFragmentFilesContainer;

import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.DEFAULT_CLASS_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_ENUM_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_EVENT_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_EVENT_LISTENER_INTERFACE;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_EVENT_SUBJECT_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_IDENTITY_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_KEY_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_RPC_COMMAND_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_RPC_EXTENDED_COMMAND_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_RPC_HANDLER_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_RPC_REGISTER_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_TYPEDEF_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_UNION_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.INTERFACE_MASK;
import static org.onosproject.yang.compiler.translator.tojava.utils.BracketType.OPEN_CLOSE_DIAMOND_WITH_VALUE;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.brackets;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getAbstractClassDefinitionWithExtends;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getDefaultDefinition;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getDefaultDefinitionWithExtends;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getDefaultDefinitionWithImpl;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getDefaultName;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getDefinitionWithImplements;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getErrorMsg;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getEventExtendsString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getSpecificModifier;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getSuffixedName;
import static org.onosproject.yang.compiler.utils.UtilConstants.ABSTRACT;
import static org.onosproject.yang.compiler.utils.UtilConstants.ABSTRACT_EVENT;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLASS;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMA;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMAND;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_RPC_HANDLER;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.ENUM;
import static org.onosproject.yang.compiler.utils.UtilConstants.ERROR_MSG_JAVA_IDENTITY;
import static org.onosproject.yang.compiler.utils.UtilConstants.EVENT_LISTENER_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.EVENT_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.EVENT_TYPE;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXTEND;
import static org.onosproject.yang.compiler.utils.UtilConstants.FINAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.IMPLEMENTS;
import static org.onosproject.yang.compiler.utils.UtilConstants.INTERFACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.KEYS;
import static org.onosproject.yang.compiler.utils.UtilConstants.KEY_INFO;
import static org.onosproject.yang.compiler.utils.UtilConstants.LISTENER_SERVICE;
import static org.onosproject.yang.compiler.utils.UtilConstants.MODEL_OBJECT;
import static org.onosproject.yang.compiler.utils.UtilConstants.MULTI_INSTANCE_OBJECT;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.OP_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.PUBLIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_FOR_ANY_STRING_ENDING_WITH_SERVICE;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGISTER_RPC;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_COMMAND;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_EXTENDED_COMMAND;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_HANDLER;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_SERVICE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SERVICE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SUBJECT;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.trimAtLast;

/**
 * Represents generator for class definition of generated files.
 */
final class ClassDefinitionGenerator {

    /**
     * Creates an instance of class definition generator.
     */
    private ClassDefinitionGenerator() {
    }

    /**
     * Based on the file type and the YANG name of the file, generate the class
     * / interface definition start.
     *
     * @param genFileTypes generated file type
     * @param yangName     class name
     * @return class definition
     */
    static String generateClassDefinition(int genFileTypes, String yangName) {

        /*
         * Based on the file type and the YANG name of the file, generate the
         * class / interface definition start.
         */
        switch (genFileTypes) {
            case GENERATE_TYPEDEF_CLASS:
            case GENERATE_UNION_CLASS:
                return getTypeClassDefinition(yangName);
            case GENERATE_ENUM_CLASS:
                return getEnumClassDefinition(yangName);
            default:
                return null;
        }
    }

    /**
     * Based on the file type and the YANG name of the file, generate the class
     * / interface definition start.
     *
     * @param genFileTypes generated file type
     * @param yangName     class name
     * @param curNode      current YANG node
     * @return class definition
     */
    static String generateClassDefinition(int genFileTypes, String yangName,
                                          YangNode curNode) {
        /*
         * Based on the file type and the YANG name of the file, generate the
         * class / interface definition start.
         */
        switch (genFileTypes) {
            case INTERFACE_MASK:
                return getInterfaceDefinition(yangName, curNode);
            case DEFAULT_CLASS_MASK:
                return getImplClassDefinition(yangName, curNode);
            case GENERATE_SERVICE_AND_MANAGER:
                return getRpcInterfaceDefinition(yangName, curNode);
            case GENERATE_EVENT_CLASS:
                String eventName = yangName + SUBJECT;
                return getEventDefinition(yangName, eventName);
            case GENERATE_EVENT_LISTENER_INTERFACE:
                return getEventListenerDefinition(yangName);
            case GENERATE_KEY_CLASS:
                return getKeyClassDefinition(yangName, curNode);
            case GENERATE_EVENT_SUBJECT_CLASS:
                return getClassDefinition(yangName);
            case GENERATE_IDENTITY_CLASS:
                return getIdentityClassDefinition(yangName, curNode);
            case GENERATE_RPC_HANDLER_CLASS:
                return getRpcHandlerClassDefinition();
            case GENERATE_RPC_REGISTER_CLASS:
                return getRpcRegisterClassDefination();
            case GENERATE_RPC_COMMAND_CLASS:
                return getRpcCommandClassDefination(curNode);
            case GENERATE_RPC_EXTENDED_COMMAND_CLASS:
                return getRpcExtendedCommandClassDefination();
            default:
                return null;
        }
    }

    /**
     * Returns enum file class definition.
     *
     * @param yangName class name
     * @return enum file class definition
     */
    private static String getEnumClassDefinition(String yangName) {
        return getDefaultDefinition(ENUM, yangName, PUBLIC);
    }

    /**
     * Returns interface file class definition.
     *
     * @param yangName file name
     * @return definition
     */
    private static String getInterfaceDefinition(String yangName,
                                                 YangNode curNode) {

        String clsDef = getClassDefinitionForWhenExtended(curNode, yangName,
                                                          INTERFACE_MASK);
        if (clsDef != null) {
            return clsDef;
        }
        return getDefaultDefinition(INTERFACE, yangName, PUBLIC);
    }

    /**
     * Returns impl file class definition.
     *
     * @param yangName file name
     * @return definition
     */
    private static String getImplClassDefinition(String yangName,
                                                 YangNode curNode) {
        String clsDef = getClassDefinitionForWhenExtended(
                curNode, yangName, DEFAULT_CLASS_MASK);
        if (clsDef != null) {
            return clsDef;
        }
        return getDefaultDefinitionWithImpl(CLASS, getDefaultName(yangName),
                                            PUBLIC, yangName);
    }

    /**
     * Returns impl file class definition.
     *
     * @param yangName file name
     * @return definition
     */
    private static String getClassDefinition(String yangName) {
        return getDefaultDefinition(CLASS, yangName, PUBLIC);
    }

    /**
     * Returns impl file class definition.
     *
     * @param yangName file name
     * @param node     YANG list node
     * @return definition
     */
    private static String getKeyClassDefinition(String yangName,
                                                YangNode node) {
        String keyInfo = KEY_INFO +
                brackets(OPEN_CLOSE_DIAMOND_WITH_VALUE,
                         DEFAULT_CAPS + getCapitalCase(getCamelCase(
                                 node.getJavaClassNameOrBuiltInType(), null)),
                         null);
        return getDefinitionWithImplements(CLASS, yangName, PUBLIC,
                                           keyInfo);
    }

    /**
     * Returns implementation file identity class definition.
     *
     * @param yangName file name
     * @return identity class definition
     */
    private static String getIdentityClassDefinition(String yangName, YangNode curNode) {
        String error = getErrorMsg(ERROR_MSG_JAVA_IDENTITY, curNode.getName(),
                                   curNode.getLineNumber(), curNode
                                           .getCharPosition(), curNode
                                           .getFileName());
        if (!(curNode instanceof YangIdentity)) {
            throw new TranslatorException(error);
        }
        YangIdentity identity = (YangIdentity) curNode;
        String mod = getSpecificModifier(PUBLIC, ABSTRACT);
        if (identity.getBaseNode() != null) {
            YangIdentity baseIdentity = identity.getBaseNode().getReferredIdentity();
            if (baseIdentity == null) {
                throw new TranslatorException(error);
            }

            JavaFileInfoTranslator fileInfo = ((JavaFileInfoContainer) baseIdentity)
                    .getJavaFileInfo();
            return getDefaultDefinitionWithExtends(
                    CLASS, yangName, mod, getCapitalCase(fileInfo.getJavaName()));
        }
        return getDefaultDefinition(CLASS, yangName, mod);
    }

    /**
     * Returns type file class definition.
     *
     * @param yangName file name
     * @return definition
     */
    private static String getTypeClassDefinition(String yangName) {
        return getDefaultDefinition(CLASS, yangName,
                                    getSpecificModifier(PUBLIC, FINAL));
    }

    /**
     * Returns RPC file interface definition.
     *
     * @param yangName file name
     * @param curNode  current YANG node
     * @return definition
     */
    private static String getRpcInterfaceDefinition(String yangName, YangNode curNode) {
        /*JavaExtendsListHolder holder = ((TempJavaCodeFragmentFilesContainer)
                curNode)
                .getTempJavaCodeFragmentFiles().getServiceTempFiles()
                .getJavaExtendsListHolder();
        if (holder.getExtendsList() != null && !holder.getExtendsList()
                .isEmpty()) {
            curNode = curNode.getChild();
            while (curNode != null) {
                if (curNode instanceof YangNotification) {
                    return getRpcInterfaceDefinitionWhenItExtends(yangName);
                }
                curNode = curNode.getNextSibling();
            }
        }*/
        if (yangName.matches(REGEX_FOR_ANY_STRING_ENDING_WITH_SERVICE)) {
            return getDefaultDefinitionWithExtends(INTERFACE, yangName,
                                                   PUBLIC, RPC_SERVICE);
        }
        String name = getSuffixedName(
                yangName.substring(0, yangName.length() - 7), SERVICE);
        return getDefaultDefinitionWithImpl(CLASS, yangName, PUBLIC, name);
    }

    /* Provides class definition when RPC interface needs to extends any event.*/
    private static String getRpcInterfaceDefinitionWhenItExtends(String yangName) {

        StringBuilder newString = new StringBuilder(yangName);
        newString.replace(yangName.lastIndexOf(SERVICE), yangName
                .lastIndexOf(SERVICE) + 7, EMPTY_STRING);
        return getDefaultDefinitionWithExtends(
                INTERFACE, yangName, PUBLIC, getEventExtendsString(
                        getSuffixedName(newString.toString(), EVENT_STRING),
                        LISTENER_SERVICE, getSuffixedName(newString.toString(),
                                                          EVENT_LISTENER_STRING)));
    }

    /**
     * Returns event class definition.
     *
     * @param javaName file name
     * @return definition
     */
    private static String getEventDefinition(String javaName, String eventName) {
        return getDefaultDefinitionWithExtends(
                CLASS, javaName, PUBLIC, getEventExtendsString(
                        getSuffixedName(javaName, EVENT_TYPE), ABSTRACT_EVENT,
                        eventName));
    }

    /**
     * Returns event listener interface definition.
     *
     * @param javaName file name
     * @return definition
     */
    private static String getEventListenerDefinition(String javaName) {

        String name = javaName.substring(0, javaName.length() - 8);
        return getDefaultDefinitionWithExtends(
                INTERFACE, javaName, PUBLIC, EVENT_LISTENER_STRING +
                        brackets(OPEN_CLOSE_DIAMOND_WITH_VALUE, name, null));
    }

    /**
     * Returns class definition when class is extending another class.
     *
     * @param curNode      current node
     * @param yangName     name
     * @param genFileTypes gen file type
     * @return class definition
     */
    private static String getClassDefinitionForWhenExtended(
            YangNode curNode, String yangName, int genFileTypes) {
        JavaExtendsListHolder holder = ((TempJavaCodeFragmentFilesContainer) curNode)
                .getTempJavaCodeFragmentFiles().getBeanTempFiles()
                .getJavaExtendsListHolder();
        StringBuilder def = new StringBuilder();
        if (holder.getExtendsList() != null && !holder.getExtendsList().isEmpty()) {
            def.append(PUBLIC).append(SPACE);
            switch (genFileTypes) {
                case INTERFACE_MASK:
                    def.append(INTERFACE).append(SPACE).append(yangName)
                            .append(SPACE).append(EXTEND).append(SPACE);
                    def = new StringBuilder(getDefinitionString(def.toString(),
                                                                holder));
                    break;
                case DEFAULT_CLASS_MASK:

                    // class definition
                    if (curNode instanceof RpcNotificationContainer) {
                        def.append(CLASS).append(SPACE).append(yangName)
                                .append(OP_PARAM).append(SPACE).append(EXTEND)
                                .append(SPACE);
                    } else {
                        def.append(CLASS).append(SPACE).append(DEFAULT_CAPS)
                                .append(yangName).append(SPACE).append(EXTEND)
                                .append(SPACE);
                    }

                    // append with extendList
                    if (curNode instanceof YangCase) {
                        def = new StringBuilder(getDefinitionStringForCase(def.toString(),
                                                                           holder));
                    } else {
                        def = new StringBuilder(getDefinitionString(def.toString(),
                                                                    holder));
                    }

                    // append implements
                    if (curNode instanceof YangList) {
                        String multiInstanceObj = MULTI_INSTANCE_OBJECT +
                                brackets(OPEN_CLOSE_DIAMOND_WITH_VALUE,
                                         getCapitalCase(getCamelCase(yangName,
                                                                     null)) +
                                                 KEYS, null);
                        def.append(NEW_LINE).append(EIGHT_SPACE_INDENTATION)
                                .append(IMPLEMENTS).append(SPACE)
                                .append(yangName).append(COMMA).append(SPACE)
                                .append(multiInstanceObj);

                    } else {
                        def.append(IMPLEMENTS).append(SPACE)
                                .append(yangName);
                    }
                    break;
                default:
                    return null;
            }
            return def.append(SPACE).append(OPEN_CURLY_BRACKET)
                    .append(NEW_LINE).toString();
        }
        return null;
    }

    /**
     * Returns updated class definition.
     *
     * @param def    current definition
     * @param holder extend list holder
     * @return updated class definition
     */
    private static String getDefinitionString(String def,
                                              JavaExtendsListHolder holder) {
        StringBuilder builder = new StringBuilder(def);
        String str;
        for (JavaQualifiedTypeInfoTranslator info : holder.getExtendsList()) {
            if (!holder.getExtendedClassStore().get(info)) {
                str = info.getClassInfo() + COMMA + SPACE;
            } else {
                str = info.getPkgInfo() + PERIOD + info.getClassInfo() +
                        COMMA + SPACE;
            }
            builder.append(str);
        }
        def = builder.toString();
        return trimAtLast(def, COMMA);
    }

    /**
     * Returns updated class definition for case.
     *
     * @param def    current definition
     * @param holder extend list holder
     * @return updated class definition
     */
    private static String getDefinitionStringForCase(String def,
                                                     JavaExtendsListHolder holder) {
        StringBuilder builder = new StringBuilder(def);
        for (JavaQualifiedTypeInfoTranslator info : holder.getExtendsList()) {
            if (!info.getClassInfo().equals(MODEL_OBJECT)) {
                continue;
            }
            builder.append(info.getClassInfo() + COMMA + SPACE);
        }
        def = builder.toString();
        return trimAtLast(def, COMMA);
    }

    /**
     * Returns RPC handler class definition.
     *
     * @return RPC handler class definition
     */
    private static String getRpcHandlerClassDefinition() {
        return getDefinitionWithImplements(CLASS, DEFAULT_RPC_HANDLER,
                                           PUBLIC, RPC_HANDLER);
    }

    /**
     * Returns register RPC class definition.
     *
     * @return register RPC class definition
     */
    private static String getRpcRegisterClassDefination() {
        return getDefaultDefinition(CLASS, REGISTER_RPC, PUBLIC);
    }

    /**
     * Returns RPC command class definition.
     *
     * @return RPC command class definition
     */
    private static String getRpcCommandClassDefination(YangNode yangNode) {
        String className = getCapitalCase(getCamelCase(
                yangNode.getJavaClassNameOrBuiltInType(), null)) + COMMAND;
        return getDefaultDefinitionWithExtends(CLASS, className,
                                               PUBLIC, RPC_EXTENDED_COMMAND);
    }

    /**
     * Returns extended RPC command class definition.
     *
     * @return extended RPC command class definition
     */
    private static String getRpcExtendedCommandClassDefination() {
        return getAbstractClassDefinitionWithExtends(CLASS, RPC_EXTENDED_COMMAND,
                                                     PUBLIC, RPC_COMMAND);
    }
}
