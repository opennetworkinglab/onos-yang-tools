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

import org.onosproject.yang.compiler.datamodel.YangAppDataStructure;
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangCompilerAnnotation;
import org.onosproject.yang.compiler.datamodel.YangDataStructure;
import org.onosproject.yang.compiler.datamodel.YangIdentity;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.YangRevision;
import org.onosproject.yang.compiler.datamodel.YangSubModule;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.JavaAttributeInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoContainer;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yang.compiler.translator.tojava.JavaImportData;
import org.onosproject.yang.compiler.translator.tojava.JavaQualifiedTypeInfoTranslator;
import org.onosproject.yang.compiler.translator.tojava.TempJavaBeanFragmentFiles;
import org.onosproject.yang.compiler.translator.tojava.TempJavaEnumerationFragmentFiles;
import org.onosproject.yang.compiler.translator.tojava.TempJavaEventFragmentFiles;
import org.onosproject.yang.compiler.translator.tojava.TempJavaFragmentFiles;
import org.onosproject.yang.compiler.translator.tojava.TempJavaServiceFragmentFiles;
import org.onosproject.yang.compiler.translator.tojava.TempJavaTypeFragmentFiles;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.IDENTITYREF;
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
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.ADD_TO_LIST_IMPL_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.ADD_TO_LIST_INTERFACE_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.ATTRIBUTES_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.CONSTRUCTOR_FOR_TYPE_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.CONSTRUCTOR_IMPL_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.EDIT_CONTENT_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.ENUM_IMPL_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.EQUALS_IMPL_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.EVENT_ENUM_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.EVENT_METHOD_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.EVENT_SUBJECT_ATTRIBUTE_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.EVENT_SUBJECT_GETTER_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.EVENT_SUBJECT_SETTER_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.FROM_STRING_IMPL_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.GETTER_FOR_CLASS_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.GETTER_FOR_INTERFACE_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.HASH_CODE_IMPL_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.LEAF_IDENTIFIER_ENUM_ATTRIBUTES_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.OF_STRING_IMPL_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.RPC_INTERFACE_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.SETTER_FOR_CLASS_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.SETTER_FOR_INTERFACE_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.TO_STRING_IMPL_MASK;
import static org.onosproject.yang.compiler.translator.tojava.JavaQualifiedTypeInfoTranslator.getQualifiedTypeInfoOfCurNode;
import static org.onosproject.yang.compiler.translator.tojava.YangJavaModelUtils.getNodesPackage;
import static org.onosproject.yang.compiler.translator.tojava.utils.ClassDefinitionGenerator.generateClassDefinition;
import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.FOUR_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaIdentifierSyntax.getRootPackage;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodClassTypes.CLASS_TYPE;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodClassTypes.INTERFACE_TYPE;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getIfConditionBegin;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getOverRideString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getReturnString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getTwoParaEqualsString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.methodClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.methodSignature;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.multiAttrMethodSignature;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.signatureClose;
import static org.onosproject.yang.compiler.utils.UtilConstants.ADD_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.AUGMENTED;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLASS_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMPARE_TO;
import static org.onosproject.yang.compiler.utils.UtilConstants.DIAMOND_CLOSE_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.DIAMOND_OPEN_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.ERROR_MSG_FOR_GEN_CODE;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXTEND;
import static org.onosproject.yang.compiler.utils.UtilConstants.FOUR_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.IDENTITY;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT;
import static org.onosproject.yang.compiler.utils.UtilConstants.KEYS;
import static org.onosproject.yang.compiler.utils.UtilConstants.LEAFREF;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEG_ONE;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PACKAGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.PUBLIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUESTION_MARK;
import static org.onosproject.yang.compiler.utils.UtilConstants.SEMI_COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.SET_METHOD_PREFIX;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.TO_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.VOID;
import static org.onosproject.yang.compiler.utils.UtilConstants.ZERO;
import static org.onosproject.yang.compiler.utils.io.impl.CopyrightHeader.parseCopyrightHeader;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.DEFAULT_CLASS;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.ENUM_CLASS;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.EVENT;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.EVENT_LISTENER;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.EVENT_SUBJECT_CLASS;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.INTERFACE;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.REGISTER_RPC;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.RPC_COMMAND;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.RPC_EXTENDED_CMD;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.RPC_HANDLER;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.RPC_INTERFACE;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavaDoc;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getJavaPackageFromPackagePath;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.insertDataIntoJavaFile;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.parsePkg;

/**
 * Represents utilities for java file generator.
 */
public final class JavaFileGeneratorUtils {

    /**
     * Creates an instance of java file generator util.
     */
    private JavaFileGeneratorUtils() {
    }

    /**
     * Returns a file object for generated file.
     *
     * @param filePath  file package path
     * @param fileName  file name
     * @param extension file extension
     * @param handler   cached file handle
     * @return file object
     */
    public static File getFileObject(String filePath, String fileName,
                                     String extension,
                                     JavaFileInfoTranslator handler) {
        return new File(handler.getBaseCodeGenPath() + filePath + SLASH +
                                fileName + extension);
    }

    /**
     * Returns data stored in temporary files.
     *
     * @param tempFiles  temporary file types
     * @param tempHandle temp java fragment files
     * @param path       absolute path
     * @return data stored in temporary files
     * @throws IOException when failed to get the data from temporary file handle
     */
    static String getDataFromTempFileHandle(
            int tempFiles, TempJavaFragmentFiles tempHandle, String path)
            throws IOException {

        TempJavaTypeFragmentFiles typeHandle = null;
        if (tempHandle instanceof TempJavaTypeFragmentFiles) {
            typeHandle = (TempJavaTypeFragmentFiles) tempHandle;
        }

        TempJavaBeanFragmentFiles beanHandle = null;
        if (tempHandle instanceof TempJavaBeanFragmentFiles) {
            beanHandle = (TempJavaBeanFragmentFiles) tempHandle;
        }

        TempJavaServiceFragmentFiles serviceHandle = null;
        if (tempHandle instanceof TempJavaServiceFragmentFiles) {
            serviceHandle = (TempJavaServiceFragmentFiles) tempHandle;
        }

        TempJavaEventFragmentFiles eventHandle = null;
        if (tempHandle instanceof TempJavaEventFragmentFiles) {
            eventHandle = (TempJavaEventFragmentFiles) tempHandle;
        }

        if ((tempFiles & ATTRIBUTES_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getAttributesTempFileHandle(),
                    path);
        }
        if ((tempFiles & GETTER_FOR_INTERFACE_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getGetterInterfaceTempFileHandle(),
                    path);
        }
        if ((tempFiles & SETTER_FOR_INTERFACE_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getSetterInterfaceTempFileHandle(),
                    path);
        }
        if ((tempFiles & GETTER_FOR_CLASS_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getGetterImplTempFileHandle(),
                    path);
        }
        if ((tempFiles & SETTER_FOR_CLASS_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getSetterImplTempFileHandle(),
                    path);
        }
        if ((tempFiles & ADD_TO_LIST_INTERFACE_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getAddToListInterfaceTempFileHandle(),
                    path);
        }
        if ((tempFiles & ADD_TO_LIST_IMPL_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getAddToListImplTempFileHandle(),
                    path);
        }
        if ((tempFiles & EDIT_CONTENT_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getEditContentTempFileHandle(),
                    path);
        }
        if ((tempFiles & LEAF_IDENTIFIER_ENUM_ATTRIBUTES_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getLeafIdAttributeTempFileHandle(),
                    path);
        }
        if ((tempFiles & CONSTRUCTOR_IMPL_MASK) != 0) {
            if (beanHandle == null) {
                throw new TranslatorException("Required constructor info is" +
                                                      " missing.");
            }
            return beanHandle.getTemporaryDataFromFileHandle(
                    beanHandle.getConstructorImplTempFileHandle(),
                    path);
        }
        if ((tempFiles & HASH_CODE_IMPL_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getHashCodeImplTempFileHandle(),
                    path);
        }
        if ((tempFiles & EQUALS_IMPL_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getEqualsImplTempFileHandle(),
                    path);
        }
        if ((tempFiles & TO_STRING_IMPL_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getToStringImplTempFileHandle(),
                    path);
        }
        if ((tempFiles & OF_STRING_IMPL_MASK) != 0) {
            if (typeHandle == null) {
                throw new TranslatorException("Required of string implementation" +
                                                      " info is missing.");
            }
            return typeHandle.getTemporaryDataFromFileHandle(
                    typeHandle.getOfStringImplTempFileHandle(), path);
        }
        if ((tempFiles & CONSTRUCTOR_FOR_TYPE_MASK) != 0) {
            if (typeHandle == null) {
                throw new TranslatorException("Required constructor implementation" +
                                                      " info is missing.");
            }
            return typeHandle.getTemporaryDataFromFileHandle(
                    typeHandle.getConstructorForTypeTempFileHandle(),
                    path);
        }
        if ((tempFiles & FROM_STRING_IMPL_MASK) != 0) {
            return tempHandle.getTemporaryDataFromFileHandle(
                    tempHandle.getFromStringImplTempFileHandle(),
                    path);
        }
        if ((tempFiles & ENUM_IMPL_MASK) != 0) {
            if (!(tempHandle instanceof TempJavaEnumerationFragmentFiles)) {
                throw new TranslatorException("Required enum info is missing.");
            }
            TempJavaEnumerationFragmentFiles enumHandle =
                    (TempJavaEnumerationFragmentFiles) tempHandle;
            return enumHandle.getTemporaryDataFromFileHandle(
                    enumHandle.getEnumClassTempFileHandle(), path);
        }
        if ((tempFiles & RPC_INTERFACE_MASK) != 0) {
            if (serviceHandle == null) {
                throw new TranslatorException("Required rpc interface info is" +
                                                      " missing.");
            }
            return serviceHandle.getTemporaryDataFromFileHandle(
                    serviceHandle.getRpcInterfaceTempFileHandle(),
                    path);
        }
        if ((tempFiles & EVENT_ENUM_MASK) != 0) {
            if (eventHandle == null) {
                throw new TranslatorException(
                        "Required event enum implementation info is missing.");
            }
            return eventHandle.getTemporaryDataFromFileHandle(
                    eventHandle.getEventEnumTempFileHandle(),
                    path);
        }
        if ((tempFiles & EVENT_METHOD_MASK) != 0) {
            if (eventHandle == null) {
                throw new TranslatorException(
                        "Required event method implementation info is missing.");
            }
            return eventHandle.getTemporaryDataFromFileHandle(
                    eventHandle.getEventMethodTempFileHandle(),
                    path);
        }
        if ((tempFiles & EVENT_SUBJECT_GETTER_MASK) != 0) {
            if (eventHandle == null) {
                throw new TranslatorException(
                        "Required event subject getter implementation info is" +
                                " missing.");
            }
            return eventHandle.getTemporaryDataFromFileHandle(
                    eventHandle.getEventSubjectGetterTempFileHandle(),
                    path);
        }
        if ((tempFiles & EVENT_SUBJECT_SETTER_MASK) != 0) {
            if (eventHandle == null) {
                throw new TranslatorException(
                        "Required event subject setter implementation info is" +
                                " missing.");
            }
            return eventHandle.getTemporaryDataFromFileHandle(
                    eventHandle.getEventSubjectSetterTempFileHandle(),
                    path);
        }
        if ((tempFiles & EVENT_SUBJECT_ATTRIBUTE_MASK) != 0) {
            if (eventHandle == null) {
                throw new TranslatorException(
                        "Required event subject attribute implementation info is" +
                                " missing.");
            }
            return eventHandle.getTemporaryDataFromFileHandle(
                    eventHandle.getEventSubjectAttributeTempFileHandle(),
                    path);
        }
        return null;
    }

    /**
     * Initiates generation of file based on generated file type.
     *
     * @param file      generated file
     * @param className generated file class name
     * @param genType   generated file type
     * @param imports   imports for the file
     * @param pkg       generated file package
     * @throws IOException when fails to generate a file
     */
    public static void initiateJavaFileGeneration(File file, String className,
                                                  int genType, List<String> imports,
                                                  String pkg)
            throws IOException {

        if (file.exists()) {
            throw new IOException(" file " + file.getName() + " is already generated for "
                                          + className + " @ " + pkg + "\n" +
                                          ERROR_MSG_FOR_GEN_CODE);
        }

        try {
            appendContents(file, className, genType, imports, pkg);
        } catch (IOException e) {
            throw new IOException("Failed to append contents in " + file.getName() +
                                          " class file.", e);
        }
    }

    /**
     * Initiates generation of file based on generated file type.
     *
     * @param file    generated file
     * @param genType generated file type
     * @param imports imports for the file
     * @param curNode current YANG node
     * @param name    class name
     * @throws IOException when fails to generate a file
     */
    public static void initiateJavaFileGeneration(File file, int genType,
                                                  List<String> imports,
                                                  YangNode curNode, String name)
            throws IOException {

        if (file.exists()) {
            throw new IOException(" file " + file.getName() +
                                          " is already generated for: " + name + "\n" +
                                          ERROR_MSG_FOR_GEN_CODE);
        }
        try {
            appendContents(file, genType, imports, curNode, name);
        } catch (IOException e) {
            throw new IOException("Failed to append contents in " + file.getName() +
                                          " class file.", e);
        }
    }

    /**
     * Appends all the contents into a generated java file.
     *
     * @param file        generated file
     * @param genType     generated file type
     * @param importsList list of java imports
     * @param curNode     current YANG node
     * @param className   class name
     * @throws IOException when fails to do IO operations
     */
    private static void appendContents(File file, int genType,
                                       List<String> importsList, YangNode curNode,
                                       String className)
            throws IOException {

        JavaFileInfoTranslator javaFileInfo = ((JavaFileInfoContainer) curNode)
                .getJavaFileInfo();

        String name = javaFileInfo.getJavaName();
        String path = javaFileInfo.getBaseCodeGenPath() + javaFileInfo
                .getPackageFilePath();

        String pkgString;
        if (genType == GENERATE_EVENT_CLASS ||
                genType == GENERATE_EVENT_LISTENER_INTERFACE ||
                genType == GENERATE_EVENT_SUBJECT_CLASS) {
            pkgString = parsePackageString((path + PERIOD + name)
                                                   .toLowerCase(), importsList);
        } else {
            pkgString = parsePackageString(path, importsList);
        }
        switch (genType) {
            case INTERFACE_MASK:
                appendHeaderContents(file, pkgString, importsList);
                write(file, genType, INTERFACE, curNode, className, false);
                break;
            case GENERATE_KEY_CLASS:
                appendHeaderContents(file, pkgString, importsList);
                write(file, genType, DEFAULT_CLASS, curNode, className, false);
                break;
            case DEFAULT_CLASS_MASK:
                appendHeaderContents(file, pkgString, importsList);
                write(file, genType, DEFAULT_CLASS, curNode, className,
                      curNode.isOpTypeReq());
                break;
            case GENERATE_SERVICE_AND_MANAGER:
                appendHeaderContents(file, pkgString, importsList);
                write(file, genType, RPC_INTERFACE, curNode, className, false);
                break;
            case GENERATE_EVENT_CLASS:
                appendHeaderContents(file, pkgString, importsList);
                write(file, genType, EVENT, curNode, className, false);
                break;
            case GENERATE_EVENT_LISTENER_INTERFACE:
                appendHeaderContents(file, pkgString, importsList);
                write(file, genType, EVENT_LISTENER, curNode, className, false);
                break;
            case GENERATE_EVENT_SUBJECT_CLASS:
                appendHeaderContents(file, pkgString, importsList);
                write(file, genType, EVENT_SUBJECT_CLASS, curNode, className, false);
                break;
            case GENERATE_IDENTITY_CLASS:
                appendHeaderContents(file, pkgString, importsList);
                write(file, genType, EVENT_SUBJECT_CLASS, curNode, className, false);
                break;
            case GENERATE_RPC_HANDLER_CLASS:
                appendHeaderContents(file, pkgString, importsList);
                write(file, genType, RPC_HANDLER, curNode, className, false);
                break;
            case GENERATE_RPC_REGISTER_CLASS:
                appendHeaderContents(file, pkgString, importsList);
                write(file, genType, REGISTER_RPC, curNode, className, false);
                break;
            case GENERATE_RPC_COMMAND_CLASS:
                appendHeaderContents(file, pkgString, importsList);
                write(file, genType, RPC_COMMAND, curNode, className, false);
                break;
            case GENERATE_RPC_EXTENDED_COMMAND_CLASS:
                appendHeaderContents(file, pkgString, importsList);
                write(file, genType, RPC_EXTENDED_CMD, curNode, className, false);
            default:
                break;
        }
    }

    /**
     * Appends all the contents into a generated java file.
     *
     * @param file        generated file
     * @param fileName    generated file name
     * @param genType     generated file type
     * @param importsList list of java imports
     * @param pkg         generated file package
     * @throws IOException when fails to append contents
     */
    private static void appendContents(File file, String fileName, int genType,
                                       List<String> importsList, String pkg)
            throws IOException {

        String pkgString = parsePackageString(pkg, importsList);

        switch (genType) {
            case GENERATE_TYPEDEF_CLASS:
                appendHeaderContents(file, pkgString, importsList);
                write(file, fileName, genType, DEFAULT_CLASS);
                break;
            case GENERATE_UNION_CLASS:
                appendHeaderContents(file, pkgString, importsList);
                write(file, fileName, genType, DEFAULT_CLASS);
                break;
            case GENERATE_ENUM_CLASS:
                appendHeaderContents(file, pkgString, importsList);
                write(file, fileName, genType, ENUM_CLASS);
                break;
            default:
                break;
        }
    }

    /**
     * Removes base directory path from package and generates package string for file.
     *
     * @param javaPkg     generated java package
     * @param importsList list of imports
     * @return package string
     */
    private static String parsePackageString(String javaPkg,
                                             List<String> importsList) {

        javaPkg = parsePkg(getJavaPackageFromPackagePath(javaPkg));
        if (importsList != null) {
            if (!importsList.isEmpty()) {
                return PACKAGE + SPACE + javaPkg + SEMI_COLON + NEW_LINE;
            } else {
                return PACKAGE + SPACE + javaPkg + SEMI_COLON;
            }
        } else {
            return PACKAGE + SPACE + javaPkg + SEMI_COLON;
        }
    }

    /**
     * Appends other contents to interface, impl and typedef classes.
     * for example : ONOS copyright, imports and package.
     *
     * @param file        generated file
     * @param pkg         generated package
     * @param importsList list of imports
     * @throws IOException when fails to append contents
     */
    private static void appendHeaderContents(File file, String pkg,
                                             List<String> importsList)
            throws IOException {

        insertDataIntoJavaFile(file, parseCopyrightHeader());
        insertDataIntoJavaFile(file, pkg);

        /*
         * TODO: add the file header using comments for snippet of yang file.
         * JavaCodeSnippetGen.getFileHeaderComment
         */

        if (importsList != null) {
            insertDataIntoJavaFile(file, NEW_LINE);
            for (String imports : importsList) {
                insertDataIntoJavaFile(file, imports);
            }
        }
    }

    /**
     * Writes data to the specific generated file.
     *
     * @param file        generated file
     * @param genType     generated file type
     * @param javaDocType java doc type
     * @param curNode     current YANG node
     * @param fileName    file name
     * @param isForClass  is for default class
     * @throws IOException when fails to write into a file
     */
    private static void write(File file, int genType, JavaDocType javaDocType,
                              YangNode curNode, String fileName, boolean isForClass)
            throws IOException {
        insertDataIntoJavaFile(file, getJavaDoc(
                javaDocType, curNode.getJavaClassNameOrBuiltInType(), isForClass,
                null));
        insertDataIntoJavaFile(file, generateClassDefinition(genType,
                                                             fileName, curNode));
    }

    /**
     * Writes data to the specific generated file.
     *
     * @param file        generated file
     * @param fileName    file name
     * @param genType     generated file type
     * @param javaDocType java doc type
     * @throws IOException when fails to write into a file
     */
    private static void write(File file, String fileName, int genType,
                              JavaDocType javaDocType) throws IOException {
        insertDataIntoJavaFile(file, getJavaDoc(javaDocType, fileName, false,
                                                null));
        insertDataIntoJavaFile(file, generateClassDefinition(genType, fileName));
    }

    /**
     * Returns set of node identifiers.
     *
     * @param parent parent node
     * @return set of node identifiers
     */
    static List<YangAtomicPath> getSetOfNodeIdentifiers(YangNode parent) {

        List<YangAtomicPath> targets = new ArrayList<>();
        YangNodeIdentifier nodeId;
        List<YangAugment> augments = getListOfAugments(parent);
        for (YangAugment augment : augments) {
            nodeId = augment.getTargetNode().get(0).getNodeIdentifier();
            augment.setSetterMethodName(getAugmentSetterName(augment));
            if (validateNodeIdentifierInSet(nodeId, targets)) {
                targets.add(augment.getTargetNode().get(0));
            }
        }
        return targets;
    }

    /* Returns list of augments.*/
    private static List<YangAugment> getListOfAugments(YangNode parent) {
        List<YangAugment> augments = new ArrayList<>();
        YangNode child = parent.getChild();
        while (child != null) {
            if (child instanceof YangAugment) {
                augments.add((YangAugment) child);
            }
            child = child.getNextSibling();
        }
        return augments;
    }

    /* Returns setter method name for augment.*/
    private static String getAugmentSetterName(YangAugment augment) {
        YangAtomicPath atomicPath = augment.getTargetNode().get(0);
        YangNode augmentedNode = atomicPath.getResolvedNode();
        String setterName = SET_METHOD_PREFIX + AUGMENTED
                + getCapitalCase(((JavaFileInfoContainer) augmentedNode.getParent())
                                         .getJavaFileInfo().getJavaName())
                + getCapitalCase(((JavaFileInfoContainer) augmentedNode)
                                         .getJavaFileInfo().getJavaName());
        return setterName;
    }

    /*Validates the set for duplicate names of node identifiers.*/
    private static boolean validateNodeIdentifierInSet(
            YangNodeIdentifier nodeId, List<YangAtomicPath> targets) {
        boolean isPresent = true;
        for (YangAtomicPath target : targets) {
            if (target.getNodeIdentifier().getName().equals(nodeId.getName())) {
                if (target.getNodeIdentifier().getPrefix() != null) {
                    isPresent = !target.getNodeIdentifier().getPrefix()
                            .equals(nodeId.getPrefix());
                } else {
                    isPresent = nodeId.getPrefix() != null;
                }
            }
        }
        return isPresent;
    }

    /**
     * Returns qualified type info of augmented node.
     *
     * @param augmentedNode augmented node
     * @param curNodeName   current node name
     * @param pluginConfig  plugin configurations
     * @return qualified type info of augmented node
     */
    private static JavaQualifiedTypeInfoTranslator getQTypeInfoOfNode(
            YangNode augmentedNode, String curNodeName, YangPluginConfig pluginConfig) {
        JavaQualifiedTypeInfoTranslator javaQualifiedTypeInfo =
                getQualifiedTypeInfoOfCurNode(augmentedNode,
                                              curNodeName);
        if (javaQualifiedTypeInfo.getPkgInfo() == null) {
            javaQualifiedTypeInfo.setPkgInfo(getNodesPackage(augmentedNode,
                                                             pluginConfig));
        }
        return javaQualifiedTypeInfo;
    }

    /**
     * Validates if augmented node is imported in parent node.
     *
     * @param javaQualifiedTypeInfo qualified type info
     * @param importData            import data
     * @return true if present in imports
     */
    private static boolean validateQualifiedInfoOfAugmentedNode(
            JavaQualifiedTypeInfoTranslator javaQualifiedTypeInfo,
            JavaImportData importData) {
        for (JavaQualifiedTypeInfoTranslator curImportInfo : importData
                .getImportSet()) {
            if (curImportInfo.getClassInfo()
                    .contentEquals(javaQualifiedTypeInfo.getClassInfo())) {
                return curImportInfo.getPkgInfo()
                        .contentEquals(javaQualifiedTypeInfo.getPkgInfo());
            }
        }
        return true;
    }

    /**
     * Return augmented class name for data methods in manager and service.
     *
     * @param augmentedNode augmented node
     * @param parent        parent node
     * @return augmented class name for data methods in manager and service
     */
    static String getAugmentedClassNameForDataMethods(YangNode augmentedNode,
                                                      YangNode parent) {
        String curNodeName;
        JavaQualifiedTypeInfoTranslator javaQualifiedTypeInfo;
        JavaFileInfoTranslator parentInfo = ((JavaFileInfoContainer) parent)
                .getJavaFileInfo();
        YangPluginConfig pluginConfig = parentInfo.getPluginConfig();
        TempJavaServiceFragmentFiles tempJavaServiceFragmentFiles = (
                (JavaCodeGeneratorInfo) parent).getTempJavaCodeFragmentFiles()
                .getServiceTempFiles();
        curNodeName = getCurNodeName(augmentedNode, pluginConfig);

        javaQualifiedTypeInfo = getQTypeInfoOfNode(augmentedNode,
                                                   getCapitalCase(curNodeName),
                                                   parentInfo.getPluginConfig());
        if (validateQualifiedInfoOfAugmentedNode(javaQualifiedTypeInfo,
                                                 tempJavaServiceFragmentFiles
                                                         .getJavaImportData())) {
            return javaQualifiedTypeInfo.getClassInfo();
        } else {
            return javaQualifiedTypeInfo.getPkgInfo() + PERIOD +
                    javaQualifiedTypeInfo.getClassInfo();
        }
    }

    //Returns class name of current node
    static String getCurNodeName(YangNode node, YangPluginConfig config) {
        if (((JavaFileInfoContainer) node).getJavaFileInfo()
                .getJavaName() != null) {
            return getCapitalCase(((JavaFileInfoContainer) node)
                                          .getJavaFileInfo()
                                          .getJavaName());
        } else {
            return getCapitalCase(getCamelCase(node.getName(), config
                    .getConflictResolver()));
        }
    }

    /**
     * Checks if the type name is leafref and returns the effective type name.
     *
     * @param attributeName name of the current type
     * @param attributeType effective type
     * @return name of the effective type
     */
    public static String isTypeNameLeafref(String attributeName,
                                           YangType<?> attributeType) {
        if (attributeName.equalsIgnoreCase(LEAFREF)) {
            YangLeafRef leafRef = (YangLeafRef) attributeType.getDataTypeExtendedInfo();
            if (leafRef != null && !leafRef.isInGrouping()) {
                return attributeType.getDataTypeName();
            }
        }
        return attributeName;
        // TODO handle union scenario, having multiple leafref.
    }

    /**
     * Checks if the type is leafref and returns the effective type.
     *
     * @param attributeType current type
     * @return effective type
     */
    public static YangType isTypeLeafref(YangType<?> attributeType) {
        if (attributeType.getDataType() == YangDataTypes.LEAFREF) {
            YangLeafRef leafRef = (YangLeafRef) attributeType
                    .getDataTypeExtendedInfo();
            if (!leafRef.isInGrouping()) {
                return leafRef.getEffectiveDataType();
            }
        }
        return attributeType;
    }

    /**
     * Returns package info of derived identity.
     *
     * @param id YANG identity.
     * @return derived package info.
     */
    public static JavaQualifiedTypeInfoTranslator getDerivedPkfInfo(YangIdentity id) {
        String pkg = getDerivedPackage(id);
        String name;
        if (id.isNameConflict()) {
            name = getCapitalCase(
                    getCamelCase(id.getName() + IDENTITY, null));
        } else {
            name = getCapitalCase(
                    getCamelCase(id.getName(), null));
        }
        JavaQualifiedTypeInfoTranslator derPkgInfo =
                new JavaQualifiedTypeInfoTranslator();
        derPkgInfo.setClassInfo(name);
        derPkgInfo.setPkgInfo(pkg);
        return derPkgInfo;
    }

    /**
     * Returns Identity name of the Identity.
     *
     * @param id YANG identity.
     * @return YANG identity name.
     */
    public static String getIdName(YangIdentity id) {
        String idName;
        if (id.isNameConflict()) {
            idName = getCapitalCase(
                    getCamelCase(id.getName() + IDENTITY, null));
        } else {
            idName = getCapitalCase(
                    getCamelCase(id.getName(), null));
        }
        return idName;
    }

    /**
     * Returns add to list method interface.
     *
     * @param attr      java attribute
     * @param className name of the class
     * @return add to list method interface
     */
    public static String getAddToListMethodInterface(JavaAttributeInfo attr,
                                                     String className) {

        String methodName = ADD_STRING + TO_CAPS + getCapitalCase(
                attr.getAttributeName());
        String retType = getReturnType(attr);
        YangDataStructure struct = getYangDataStructure(attr.getCompilerAnnotation());
        if (struct != null) {
            switch (struct) {
                case MAP:
                    Map<String, String> param = new LinkedHashMap<>();
                    param.put(attr.getAttributeName() + KEYS, retType + KEYS);
                    param.put(attr.getAttributeName() + VALUE_CAPS, retType);
                    return multiAttrMethodSignature(methodName, null, null,
                                                    VOID, param,
                                                    INTERFACE_TYPE,
                                                    FOUR_SPACE_INDENTATION);
                default:
                    return methodSignature(methodName, null, null, ADD_STRING + TO_CAPS,
                                           VOID, retType,
                                           INTERFACE_TYPE);
            }
        }
        return methodSignature(methodName, null, null, ADD_STRING + TO_CAPS,
                               VOID, getReturnType(attr),
                               INTERFACE_TYPE);
    }

    /**
     * Returns YANG data structure from java attribute.
     *
     * @param annotation compiler annotation
     * @return YANG data structure from java attribute
     */
    public static YangDataStructure getYangDataStructure(
            YangCompilerAnnotation annotation) {
        if (annotation != null) {
            YangAppDataStructure data = annotation.getYangAppDataStructure();
            if (data != null) {
                return data.getDataStructure();
            }
        }
        return null;
    }

    /**
     * Returns return type for attribute.
     *
     * @param attr attribute info
     * @return return type
     */
    static String getReturnType(JavaAttributeInfo attr) {
        StringBuilder builder = new StringBuilder();

        if (attr.isQualifiedName() &&
                attr.getImportInfo().getPkgInfo() != null) {
            builder.append(attr.getImportInfo().getPkgInfo()).append(PERIOD);
        }
        builder.append(attr.getImportInfo().getClassInfo());

        if (attr.getAttributeType() != null &&
                attr.getAttributeType().getDataType() == IDENTITYREF) {
            return CLASS_STRING + DIAMOND_OPEN_BRACKET +
                    QUESTION_MARK + SPACE + EXTEND + SPACE +
                    builder.toString() + DIAMOND_CLOSE_BRACKET;
        }
        return builder.toString();
    }

    /**
     * Returns compare to method for key class.
     *
     * @param attrs     attribute list
     * @param className class name
     * @return compare to method
     */
    public static String getCompareToForKeyClass(
            List<JavaAttributeInfo> attrs, String className) {

        StringBuilder builder = new StringBuilder(getOverRideString());
        builder.append(methodSignature(COMPARE_TO, null, PUBLIC, VALUE, INT,
                                       className, CLASS_TYPE));
        String cond;
        String attrName;
        String para;
        StringBuilder space = new StringBuilder();
        List<String> spaces = new ArrayList<>();
        int count = 1;
        for (JavaAttributeInfo attr : attrs) {
            attrName = attr.getAttributeName();
            para = VALUE + PERIOD + attrName;
            cond = getTwoParaEqualsString(attrName, para);
            if (count == 1) {
                space.append(EIGHT_SPACE_INDENTATION);
            } else {
                space.append(FOUR_SPACE_INDENTATION);
            }
            spaces.add(space.toString());
            count++;
            builder.append(getIfConditionBegin(space.toString(), cond));
        }
        space.append(FOUR_SPACE_INDENTATION);
        builder.append(getReturnString(ZERO, space.toString()))
                .append(signatureClose());
        for (int i = spaces.size() - 1; i >= 0; i--) {
            builder.append(spaces.get(i)).append(CLOSE_CURLY_BRACKET)
                    .append(NEW_LINE);
        }
        builder.append(getReturnString(NEG_ONE, EIGHT_SPACE_INDENTATION))
                .append(signatureClose()).append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    /**
     * Gets the derived package of YangIdentity.
     *
     * @param id YANG Identity.
     * @return package of identity.
     */
    public static String getDerivedPackage(YangIdentity id) {
        String derPkg;
        String version;
        String moduleName;
        YangRevision revision;
        String nodeName;

        YangNode node = id.getParent();
        if (node instanceof YangModule) {
            YangModule module = (YangModule) node;
            version = module.getVersion();
            moduleName = module.getModuleName();
            revision = module.getRevision();
            nodeName = module.getName();
        } else {
            YangSubModule subModule = (YangSubModule) node;
            version = subModule.getVersion();
            moduleName = subModule.getModuleName();
            revision = subModule.getRevision();
            nodeName = subModule.getName();
        }
        String modulePkg = getRootPackage(version, moduleName, revision, null);
        String modJava = getCamelCase(nodeName, null);
        derPkg = modulePkg + PERIOD + modJava.toLowerCase();
        return derPkg;
    }
}