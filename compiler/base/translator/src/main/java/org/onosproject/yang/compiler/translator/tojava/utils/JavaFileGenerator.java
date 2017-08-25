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
import org.onosproject.yang.compiler.datamodel.YangDataStructure;
import org.onosproject.yang.compiler.datamodel.YangEnumeration;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangTypeDef;
import org.onosproject.yang.compiler.datamodel.YangUnion;
import org.onosproject.yang.compiler.translator.tojava.JavaAttributeInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoContainer;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yang.compiler.translator.tojava.JavaImportData;
import org.onosproject.yang.compiler.translator.tojava.JavaQualifiedTypeInfoTranslator;
import org.onosproject.yang.compiler.translator.tojava.TempJavaCodeFragmentFilesContainer;
import org.onosproject.yang.compiler.translator.tojava.TempJavaEnumerationFragmentFiles;
import org.onosproject.yang.compiler.translator.tojava.TempJavaEventFragmentFiles;
import org.onosproject.yang.compiler.translator.tojava.TempJavaServiceFragmentFiles;
import org.onosproject.yang.compiler.translator.tojava.TempJavaTypeFragmentFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.sort;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.IDENTITYREF;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.DEFAULT_CLASS_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_ENUM_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_EVENT_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_EVENT_LISTENER_INTERFACE;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_EVENT_SUBJECT_CLASS;
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
import static org.onosproject.yang.compiler.translator.tojava.TempJavaFragmentFiles.getListOfAttributesForKey;
import static org.onosproject.yang.compiler.translator.tojava.TempJavaRpcCommandFragmentFiles.getRpcCommandContents;
import static org.onosproject.yang.compiler.translator.tojava.TempJavaRpcFragmentFiles.getRegisterRpcContents;
import static org.onosproject.yang.compiler.translator.tojava.TempJavaRpcFragmentFiles.getRpcExtendedCommandContents;
import static org.onosproject.yang.compiler.translator.tojava.TempJavaRpcFragmentFiles.getRpcHandlerContents;
import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.FOUR_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaCodeSnippetGen.addStaticAttributeIntRange;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaCodeSnippetGen.addStaticAttributeLongRange;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaCodeSnippetGen.addStaticAttributeShortRange;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaCodeSnippetGen.getEnumsValueAttribute;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaCodeSnippetGen.getEventEnumTypeStart;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaCodeSnippetGen.getJavaAttributeDefinition;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaCodeSnippetGen.getSetValueParaForUnionClass;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getDataFromTempFileHandle;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.initiateJavaFileGeneration;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodBodyTypes.ENUM_METHOD_INT_VALUE;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodBodyTypes.ENUM_METHOD_STRING_VALUE;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getAddAugmentationString;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getAugmentationString;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getAugmentationsString;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getBitSetEnumClassFromString;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getBitSetEnumClassToString;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getEnumsConstructor;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getEnumsOfValueMethod;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getEqualsMethod;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getEqualsMethodClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getEqualsMethodOpen;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getFromStringMethodClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getFromStringMethodSignature;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getGetter;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getGetterForClass;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getHashCodeMethod;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getHashCodeMethodClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getHashCodeMethodOpen;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getInterfaceLeafIdEnumSignature;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getRangeValidatorMethodForUnion;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getRemoveAugmentationString;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getSetterForClass;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getToStringForEnumClass;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getToStringForType;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getToStringMethodClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getToStringMethodOpen;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getUnionToStringMethod;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getYangDataStructure;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.isLeafValueSetInterface;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getImportString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getInterfaceLeafIdEnumMethods;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getIsValueLeafSet;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.methodClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.signatureClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorUtils.addDefaultConstructor;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorUtils.getBeanFiles;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorUtils.getTypeFiles;
import static org.onosproject.yang.compiler.utils.UtilConstants.BIG_INTEGER;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLASS_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMA;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMAND;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_RPC_HANDLER;
import static org.onosproject.yang.compiler.utils.UtilConstants.DIAMOND_CLOSE_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.DIAMOND_OPEN_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.ENUM_CLASS;
import static org.onosproject.yang.compiler.utils.UtilConstants.EVENT_CLASS;
import static org.onosproject.yang.compiler.utils.UtilConstants.EVENT_LISTENER_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.EVENT_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.EVENT_SUBJECT_NAME_SUFFIX;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXTEND;
import static org.onosproject.yang.compiler.utils.UtilConstants.IMPL_CLASS;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT;
import static org.onosproject.yang.compiler.utils.UtilConstants.INTERFACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_UTIL_OBJECTS_IMPORT_CLASS;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_UTIL_PKG;
import static org.onosproject.yang.compiler.utils.UtilConstants.KEYS;
import static org.onosproject.yang.compiler.utils.UtilConstants.KEY_INFO;
import static org.onosproject.yang.compiler.utils.UtilConstants.MODEL_PKG;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.OP_PARAM;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.PRIVATE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PROTECTED;
import static org.onosproject.yang.compiler.utils.UtilConstants.PUBLIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUESTION_MARK;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGISTER_RPC;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_CLASS;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_EXTENDED_COMMAND;
import static org.onosproject.yang.compiler.utils.UtilConstants.SEMI_COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.SERVICE_METHOD_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.TYPEDEF_CLASS;
import static org.onosproject.yang.compiler.utils.UtilConstants.UNION_CLASS;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.GETTER_METHOD;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.MANAGER_SETTER_METHOD;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavaDoc;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.formatFile;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.insertDataIntoJavaFile;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.replaceLast;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.trimAtLast;

/**
 * Representation of java file generator.
 */
public final class JavaFileGenerator {

    // No instantiation
    private JavaFileGenerator() {
    }

    /**
     * Returns generated interface file for current node.
     *
     * @param file        file
     * @param imports     imports for the file
     * @param curNode     current YANG node
     * @param attrPresent if any attribute is present or not
     * @return interface file
     * @throws IOException when fails to write in file
     */
    public static File generateInterfaceFile(File file, List<String> imports,
                                             YangNode curNode,
                                             boolean attrPresent)
            throws IOException {

        JavaFileInfoTranslator fileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();

        String path;
        if (curNode instanceof RpcNotificationContainer) {
            path = fileInfo.getPluginConfig().getCodeGenDir() +
                    fileInfo.getPackageFilePath();
        } else {
            path = fileInfo.getBaseCodeGenPath() +
                    fileInfo.getPackageFilePath();
        }

        String className = getCapitalCase(fileInfo.getJavaName());

        boolean leavesPresent;
        YangLeavesHolder leavesHolder;
        if (curNode instanceof YangLeavesHolder) {
            leavesHolder = (YangLeavesHolder) curNode;
            leavesPresent = leavesPresent(leavesHolder);
        } else {
            leavesPresent = false;
        }

        initiateJavaFileGeneration(file, INTERFACE_MASK, imports, curNode,
                                   className);

        List<String> methods = new ArrayList<>();

        if (attrPresent) {
            // Add getter methods to interface file.
            try {
                //Leaf identifier enum.
                if (leavesPresent) {
                    insertDataIntoJavaFile(file, getInterfaceLeafIdEnumSignature(
                            className) + trimAtLast(replaceLast(
                            getDataFromTempFileHandle(
                                    LEAF_IDENTIFIER_ENUM_ATTRIBUTES_MASK,
                                    getBeanFiles(curNode), path),
                            COMMA, SEMI_COLON), NEW_LINE) +
                            getInterfaceLeafIdEnumMethods());
                }

                insertDataIntoJavaFile(file, NEW_LINE);
                //Getter methods.
                insertDataIntoJavaFile(file, getDataFromTempFileHandle(
                        GETTER_FOR_INTERFACE_MASK, getBeanFiles(curNode), path));

                insertDataIntoJavaFile(file, getDataFromTempFileHandle(
                        SETTER_FOR_INTERFACE_MASK, getBeanFiles(curNode), path));
                insertDataIntoJavaFile(file, getDataFromTempFileHandle(
                        ADD_TO_LIST_INTERFACE_MASK, getBeanFiles(curNode), path));
            } catch (IOException e) {
                throw new IOException(getErrorMsg(className, INTERFACE));
            }
        }

        if (leavesPresent) {
            methods.add(isLeafValueSetInterface());
        }
        for (String method : methods) {
            insertDataIntoJavaFile(file, method);
        }
        return file;
    }

    /**
     * Returns generated key class file for current list node.
     *
     * @param file    file
     * @param curNode current YANG node
     * @return key class file
     * @throws IOException when fails to write in file
     */
    public static File generateKeyClassFile(File file, YangNode curNode)
            throws IOException {

        JavaFileInfoTranslator fileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();

        String className = getCapitalCase(fileInfo.getJavaName()) + KEYS;
        List<String> imports = new ArrayList<>();

        YangDataStructure data = getYangDataStructure(
                ((YangList) curNode).getCompilerAnnotation());
        if (((YangList) curNode).isConfig() &&
                data == YangDataStructure.MAP) {
            YangNode parent = curNode.getParent();
            JavaImportData importData = ((JavaCodeGeneratorInfo) parent)
                    .getTempJavaCodeFragmentFiles().getBeanTempFiles()
                    .getJavaImportData();
            JavaQualifiedTypeInfoTranslator info =
                    new JavaQualifiedTypeInfoTranslator();
            info.setClassInfo(className);
            info.setPkgInfo(fileInfo.getPackage());
            importData.addImportInfo(info, parent.getJavaClassNameOrBuiltInType(),
                                     parent.getJavaPackage());
        }
        List<JavaAttributeInfo> attrs = getListOfAttributesForKey(curNode);

        Iterator<JavaAttributeInfo> attrIt = attrs.iterator();
        JavaAttributeInfo attr;
        JavaQualifiedTypeInfoTranslator typeInfo;
        while (attrIt.hasNext()) {
            attr = attrIt.next();
            typeInfo = attr.getImportInfo();
            if (!typeInfo.getClassInfo().equals(className) &&
                    attr.getImportInfo().getPkgInfo() != null) {
                imports.add(getImportString(attr.getImportInfo().getPkgInfo(),
                                            attr.getImportInfo().getClassInfo()));
            } else {
                attr.setIsQualifiedAccess(true);
            }
        }

        imports.add(getImportString(MODEL_PKG, KEY_INFO));
        if (!attrs.isEmpty()) {
            imports.add(getImportString(JAVA_UTIL_PKG,
                                        JAVA_UTIL_OBJECTS_IMPORT_CLASS));
        }

        initiateJavaFileGeneration(file, GENERATE_KEY_CLASS, imports, curNode,
                                   className);
        String pkg = null;
        attrIt = attrs.iterator();
        while (attrIt.hasNext()) {
            attr = attrIt.next();
            if (attr.isQualifiedName()) {
                pkg = attr.getImportInfo().getPkgInfo();
            }
            String attrType = attr.getImportInfo().getClassInfo();
            if (attr.getAttributeType() != null &&
                    attr.getAttributeType().getDataType() == IDENTITYREF) {
                String type = attrType;
                if (pkg != null) {
                    type = pkg + PERIOD + attrType;
                }
                attrType = CLASS_STRING + DIAMOND_OPEN_BRACKET +
                        QUESTION_MARK + SPACE + EXTEND + SPACE + type +
                        DIAMOND_CLOSE_BRACKET;
                insertDataIntoJavaFile(file, getJavaAttributeDefinition(
                        null, attrType, attr.getAttributeName(),
                        false, PROTECTED, null));
            } else {
                insertDataIntoJavaFile(file, getJavaAttributeDefinition(
                        pkg, attr.getImportInfo().getClassInfo(),
                        attr.getAttributeName(), false, PROTECTED, null));
            }
        }

        attrIt = attrs.iterator();
        while (attrIt.hasNext()) {
            attr = attrIt.next();
            //add getter methods
            insertDataIntoJavaFile(file, getJavaDoc(GETTER_METHOD, attr
                    .getAttributeName(), false, null));
            insertDataIntoJavaFile(file, getGetterForClass(
                    attr, GENERATE_KEY_CLASS));
            insertDataIntoJavaFile(file, NEW_LINE);
            //Add setter methods.
            insertDataIntoJavaFile(file, getJavaDoc(MANAGER_SETTER_METHOD, attr
                    .getAttributeName(), false, null));
            insertDataIntoJavaFile(file, getSetterForClass(
                    attr, GENERATE_EVENT_SUBJECT_CLASS));
            insertDataIntoJavaFile(file, NEW_LINE);
        }
        if (!attrs.isEmpty()) {
            //add hashcode and equals method.
            insertDataIntoJavaFile(file, getHashCodeMethodOpen());
            StringBuilder builder = new StringBuilder();
            for (JavaAttributeInfo att : attrs) {
                builder.append(getHashCodeMethod(att));
            }
            insertDataIntoJavaFile(file, getHashCodeMethodClose(builder.toString()));

            insertDataIntoJavaFile(file, getEqualsMethodOpen(className));
            StringBuilder builder2 = new StringBuilder();
            for (JavaAttributeInfo att : attrs) {
                builder2.append(getEqualsMethod(att)).append(NEW_LINE);
            }
            insertDataIntoJavaFile(file, getEqualsMethodClose(builder2.toString()));
        }
        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET);
        return file;
    }

    /**
     * Returns generated default class file for current node.
     *
     * @param file        file
     * @param curNode     current YANG node
     * @param attrPresent if any attribute is present or not
     * @param imports     list of imports
     * @return impl class file
     * @throws IOException when fails to write in file
     */
    public static File generateDefaultClassFile(File file, YangNode curNode,
                                                boolean attrPresent,
                                                List<String> imports)
            throws IOException {

        JavaFileInfoTranslator fileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();
        boolean leavesPresent;
        YangLeavesHolder leavesHolder;
        if (curNode instanceof YangLeavesHolder) {
            leavesHolder = (YangLeavesHolder) curNode;
            leavesPresent = leavesPresent(leavesHolder);
        } else {
            leavesPresent = false;
        }

        boolean rootNode = false;

        String className = getCapitalCase(fileInfo.getJavaName());
        String opParamClassName = className;
        String name = DEFAULT_CAPS + className;
        String path;
        if (curNode instanceof RpcNotificationContainer) {
            opParamClassName = className + OP_PARAM;
            name = opParamClassName;
            rootNode = true;
            path = fileInfo.getPluginConfig().getCodeGenDir() +
                    fileInfo.getPackageFilePath();
        } else {
            path = fileInfo.getBaseCodeGenPath() +
                    fileInfo.getPackageFilePath();
        }

        initiateJavaFileGeneration(file, DEFAULT_CLASS_MASK, imports, curNode,
                                   className);

        List<String> methods = new ArrayList<>();
        if (attrPresent) {
            addDefaultClassAttributeInfo(file, curNode, className,
                                         opParamClassName, path, methods,
                                         rootNode);
        } else {
            insertDataIntoJavaFile(file, NEW_LINE);
        }

        methods.add(addDefaultConstructor(curNode, PUBLIC, DEFAULT_CAPS));

        if (leavesPresent) {
            methods.add(getIsValueLeafSet());
        }

        if (curNode instanceof RpcNotificationContainer) {
            methods.add(getAddAugmentationString());
            methods.add(getRemoveAugmentationString());
            methods.add(getAugmentationsString());
            methods.add(getAugmentationString());
        }

        // Add methods in impl class.
        for (String method : methods) {
            insertDataIntoJavaFile(file, method);
        }

        return file;
    }

    /**
     * Add methods related to attributes.
     *
     * @param file             file
     * @param curNode          current YANG node
     * @param className        name of the class
     * @param opParamClassName op param class name
     * @param path             file path
     * @param methods          list of methods string
     * @param rootNode         flag indicating whether node is root node
     * @throws IOException a violation in IO rule
     */
    private static void addDefaultClassAttributeInfo(File file, YangNode curNode,
                                                     String className,
                                                     String opParamClassName,
                                                     String path, List<String> methods,
                                                     boolean rootNode)
            throws IOException {

        //Add attribute strings.
        try {
            insertDataIntoJavaFile(file, getDataFromTempFileHandle(
                    ATTRIBUTES_MASK, getBeanFiles(curNode), path));
        } catch (IOException e) {
            throw new IOException(getErrorMsg(className, IMPL_CLASS));
        }

        try {
            //Getter methods.
            methods.add(getDataFromTempFileHandle(
                    GETTER_FOR_CLASS_MASK, getBeanFiles(curNode), path));

            //Getter methods.
            methods.add(getDataFromTempFileHandle(
                    SETTER_FOR_CLASS_MASK, getBeanFiles(curNode), path));

            //Add to list impl method.
            methods.add(getDataFromTempFileHandle(
                    ADD_TO_LIST_IMPL_MASK, getBeanFiles(curNode), path));

            // Hash code method.
            methods.add(getHashCodeMethodClose(
                    getHashCodeMethodOpen() + getDataFromTempFileHandle(
                            HASH_CODE_IMPL_MASK, getBeanFiles(curNode), path)
                            .replace(NEW_LINE, EMPTY_STRING)));

            //Equals method.
            if (rootNode) {
                methods.add(getEqualsMethodClose(
                        getEqualsMethodOpen(opParamClassName) +
                                getDataFromTempFileHandle(
                                        EQUALS_IMPL_MASK,
                                        getBeanFiles(curNode), path)));
            } else {
                methods.add(getEqualsMethodClose(
                        getEqualsMethodOpen(DEFAULT_CAPS + className) +
                                getDataFromTempFileHandle(EQUALS_IMPL_MASK,
                                                          getBeanFiles(curNode),
                                                          path)));
            }
            // To string method.
            methods.add(getToStringMethodOpen() + getDataFromTempFileHandle(
                    TO_STRING_IMPL_MASK, getBeanFiles(curNode), path) +
                                getToStringMethodClose());
        } catch (IOException e) {
            throw new IOException(getErrorMsg(className, IMPL_CLASS));
        }
    }

    /**
     * Generates class file for type def.
     *
     * @param file    generated file
     * @param curNode current YANG node
     * @param imports imports for file
     * @return type def class file
     * @throws IOException when fails to generate class file
     */
    public static File generateTypeDefClassFile(File file, YangNode curNode,
                                                List<String> imports)
            throws IOException {

        JavaFileInfoTranslator fileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();
        // import
        String className = getCapitalCase(fileInfo.getJavaName());
        String path = fileInfo.getBaseCodeGenPath() +
                fileInfo.getPackageFilePath();
        initiateJavaFileGeneration(file, className, GENERATE_TYPEDEF_CLASS,
                                   imports, path);

        List<String> methods = new ArrayList<>();
        insertDataIntoJavaFile(file, NEW_LINE);
        //Add attribute strings.
        try {
            insertDataIntoJavaFile(file, getDataFromTempFileHandle(
                    ATTRIBUTES_MASK, getTypeFiles(curNode), path));
        } catch (IOException e) {
            throw new IOException(getErrorMsg(className, TYPEDEF_CLASS));
        }

        //Default constructor.
        methods.add(addDefaultConstructor(curNode, PRIVATE, EMPTY_STRING));

        try {

            //Type constructor.
            methods.add(getDataFromTempFileHandle(
                    CONSTRUCTOR_FOR_TYPE_MASK, getTypeFiles(curNode), path));

            //Of method.
            methods.add(getDataFromTempFileHandle(
                    OF_STRING_IMPL_MASK, getTypeFiles(curNode), path));

            //Getter methods.
            methods.add(getDataFromTempFileHandle(
                    GETTER_FOR_CLASS_MASK, getTypeFiles(curNode), path));

            //Setter methods.
            methods.add(getDataFromTempFileHandle(
                    SETTER_FOR_CLASS_MASK, getTypeFiles(curNode), path));

            // Hash code method.
            methods.add(getHashCodeMethodClose(
                    getHashCodeMethodOpen() + getDataFromTempFileHandle(
                            HASH_CODE_IMPL_MASK, getTypeFiles(curNode), path)
                            .replace(NEW_LINE, EMPTY_STRING)));

            //Equals method.
            methods.add(getEqualsMethodClose(
                    getEqualsMethodOpen(className + EMPTY_STRING) +
                            getDataFromTempFileHandle(EQUALS_IMPL_MASK,
                                                      getTypeFiles(curNode), path)));

            // To string method.
            addTypedefToString(curNode, methods);

            JavaCodeGeneratorInfo javaGenInfo = (JavaCodeGeneratorInfo) curNode;

            //From string method.
            methods.add(getFromStringMethodSignature(className) +
                                getDataFromTempFileHandle(
                                        FROM_STRING_IMPL_MASK,
                                        javaGenInfo.getTempJavaCodeFragmentFiles()
                                                .getTypeTempFiles(), path) +
                                getFromStringMethodClose());
        } catch (IOException e) {
            throw new IOException(getErrorMsg(className, TYPEDEF_CLASS));
        }

        for (String method : methods) {
            insertDataIntoJavaFile(file, method);
        }
        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);

        return formatFile(file);
    }

    /**
     * Adds typedef to string method.
     *
     * @param curNode current node
     * @param methods list of methods string
     */
    private static void addTypedefToString(YangNode curNode,
                                           List<String> methods) {
        //To string method.

        List<YangType<?>> types = ((YangTypeDef) curNode).getTypeList();
        YangType type = types.get(0);
        methods.add(getToStringForType(getCamelCase(type.getDataTypeName(),
                                                    null), type));
    }

    /**
     * Generates class file for union type.
     *
     * @param file    generated file
     * @param curNode current YANG node
     * @param imports imports for file
     * @return type def class file
     * @throws IOException when fails to generate class file
     */
    public static File generateUnionClassFile(File file, YangNode curNode,
                                              List<String> imports)
            throws IOException {

        JavaFileInfoTranslator fileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();

        String className = getCapitalCase(fileInfo.getJavaName());
        String path = fileInfo.getBaseCodeGenPath() +
                fileInfo.getPackageFilePath();

        TempJavaTypeFragmentFiles tempFiles =
                ((JavaCodeGeneratorInfo) curNode).getTempJavaCodeFragmentFiles()
                        .getTypeTempFiles();

        boolean intConflict = false;
        boolean longConflict = false;
        boolean shortConflict = false;
        JavaAttributeInfo intAttr = tempFiles.getIntAttribute();
        if (intAttr == null) {
            intAttr = tempFiles.getUIntAttribute();
        }

        JavaAttributeInfo longAttr =
                tempFiles.getLongAttribute();
        if (longAttr == null) {
            longAttr = tempFiles.getULongAttribute();
        }

        JavaAttributeInfo shortAttr =
                tempFiles.getShortAttribute();
        if (shortAttr == null) {
            shortAttr = tempFiles.getUInt8Attribute();
        }

        if (intAttr != null) {
            intConflict = intAttr.isIntConflict();
        }
        if (longAttr != null) {
            longConflict = longAttr.isLongConflict();
        }
        if (shortAttr != null) {
            shortConflict = shortAttr.isShortConflict();
        }
        if (longConflict) {
            String impt = tempFiles.getJavaImportData().getBigIntegerImport();
            if (!imports.contains(impt)) {
                imports.add(impt);
                sort(imports);
            }
        }

        initiateJavaFileGeneration(file, className, GENERATE_UNION_CLASS,
                                   imports, path);

        List<String> methods = new ArrayList<>();

        // Add attribute strings.
        try {
            addUnionClassAttributeInfo(file, curNode, intConflict,
                                       longConflict, shortConflict, path, tempFiles);
        } catch (IOException e) {
            throw new IOException(getErrorMsg(className, UNION_CLASS));
        }

        //Default constructor.
        methods.add(addDefaultConstructor(curNode, PRIVATE, EMPTY_STRING));

        try {

            //Type constructor.
            methods.add(getDataFromTempFileHandle(
                    CONSTRUCTOR_FOR_TYPE_MASK, getTypeFiles(curNode), path));

            // Of string method.
            methods.add(getDataFromTempFileHandle(
                    OF_STRING_IMPL_MASK, getTypeFiles(curNode), path));

            //Getter methods.
            methods.add(getDataFromTempFileHandle(
                    GETTER_FOR_CLASS_MASK, getTypeFiles(curNode), path));

            //Setter methods.
            methods.add(getDataFromTempFileHandle(
                    SETTER_FOR_CLASS_MASK, getTypeFiles(curNode), path));

            //Hash code method.
            methods.add(getHashCodeMethodClose(
                    getHashCodeMethodOpen() +
                            getDataFromTempFileHandle(
                                    HASH_CODE_IMPL_MASK, getTypeFiles(curNode),
                                    path).replace(NEW_LINE, EMPTY_STRING)));

            //Equals method.
            methods.add(getEqualsMethodClose(
                    getEqualsMethodOpen(className) +
                            getDataFromTempFileHandle(
                                    EQUALS_IMPL_MASK, getTypeFiles(curNode),
                                    path)));

            //To string method.
            methods.add(getUnionToStringMethod(
                    ((YangUnion) curNode).getTypeList()));

            //From string method.
            methods.add(getFromStringMethodSignature(className) +
                                getDataFromTempFileHandle(
                                        FROM_STRING_IMPL_MASK,
                                        getTypeFiles(curNode), path) +
                                getFromStringMethodClose());

            if (intConflict) {
                methods.add(getRangeValidatorMethodForUnion(INT));
            }
            if (longConflict) {
                methods.add(getRangeValidatorMethodForUnion(BIG_INTEGER));
            }
            if (shortConflict && !intConflict) {
                methods.add(getRangeValidatorMethodForUnion(INT));
            }
        } catch (IOException e) {
            throw new IOException(getErrorMsg(className, UNION_CLASS));
        }

        for (String method : methods) {
            insertDataIntoJavaFile(file, method);
        }
        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);

        return formatFile(file);
    }

    /**
     * Adds union class attribute information.
     *
     * @param file          generated file
     * @param curNode       current YANG node
     * @param intConflict   flag to indicate if there is a conflict in int
     * @param longConflict  flag to indicate if there is a conflict in long
     * @param shortConflict flag to indicate if there is a conflict in short
     * @param path          file path
     * @param tempFiles     temp java type fragment files
     * @throws IOException a violation in IO rule
     */
    private static void addUnionClassAttributeInfo(File file,
                                                   YangNode curNode,
                                                   boolean intConflict,
                                                   boolean longConflict,
                                                   boolean shortConflict,
                                                   String path,
                                                   TempJavaTypeFragmentFiles tempFiles)
            throws IOException {
        if (intConflict) {
            insertDataIntoJavaFile(file,
                                   addStaticAttributeIntRange(PRIVATE,
                                                              tempFiles.getIntIndex() <
                                                                      tempFiles.getUIntIndex()));
        }

        if (longConflict) {
            insertDataIntoJavaFile(file,
                                   addStaticAttributeLongRange(PRIVATE,
                                                               tempFiles.getLongIndex() <
                                                                       tempFiles.getULongIndex()));
        }

        if (shortConflict) {
            insertDataIntoJavaFile(file,
                                   addStaticAttributeShortRange(PRIVATE,
                                                                tempFiles.getShortIndex() <
                                                                        tempFiles.getUInt8Index()));
        }

        insertDataIntoJavaFile(file, getDataFromTempFileHandle(
                ATTRIBUTES_MASK, getTypeFiles(curNode), path));
        insertDataIntoJavaFile(file, getSetValueParaForUnionClass());
    }

    /**
     * Generates class file for type enum.
     *
     * @param file    generated file
     * @param curNode current YANG node
     * @param imports import list
     * @return class file for type enum
     * @throws IOException when fails to generate class file
     */
    public static File generateEnumClassFile(File file, YangNode curNode, List<String> imports)
            throws IOException {

        JavaFileInfoTranslator fileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();

        String className = fileInfo.getJavaName();
        String path = fileInfo.getBaseCodeGenPath() +
                fileInfo.getPackageFilePath();
        TempJavaEnumerationFragmentFiles enumFile = ((JavaCodeGeneratorInfo)
                curNode).getTempJavaCodeFragmentFiles().getEnumTempFiles();
        JavaImportData data = enumFile.getJavaImportData();
        if (!enumFile.isEnumClass()) {
            imports.add(data.getImportForToBitSet());
            imports.add(data.getImportForPattern());
        }

        initiateJavaFileGeneration(file, getCapitalCase(className),
                                   GENERATE_ENUM_CLASS, imports, path);

        //Add attribute strings.
        try {
            JavaCodeGeneratorInfo javaGenInfo = (JavaCodeGeneratorInfo) curNode;
            String[] remove = {COMMA, NEW_LINE};
            insertDataIntoJavaFile(file, trimAtLast(getDataFromTempFileHandle(
                    ENUM_IMPL_MASK, javaGenInfo.getTempJavaCodeFragmentFiles()
                            .getEnumTempFiles(), path), remove) +
                    signatureClose());
        } catch (IOException e) {
            throw new IOException(getErrorMsg(getCapitalCase(className),
                                              ENUM_CLASS));
        }

        insertDataIntoJavaFile(file, NEW_LINE);
        // Add an attribute to get the enum's values.
        insertDataIntoJavaFile(file, getEnumsValueAttribute(className));

        // Add a constructor for enum.
        //TODO: generate javadoc for method.
        insertDataIntoJavaFile(file, getEnumsConstructor(getCapitalCase(className)) +
                NEW_LINE);

        insertDataIntoJavaFile(file,
                               getEnumsOfValueMethod(className,
                                                     (YangEnumeration) curNode,
                                                     ENUM_METHOD_INT_VALUE));
        insertDataIntoJavaFile(file,
                               getEnumsOfValueMethod(className,
                                                     (YangEnumeration) curNode,
                                                     ENUM_METHOD_STRING_VALUE));

        // Add a getter method for enum.
        insertDataIntoJavaFile(file, getJavaDoc(GETTER_METHOD, className, false,
                                                null) +
                getGetter(INT, className, GENERATE_ENUM_CLASS) + NEW_LINE);

        if (!enumFile.isEnumClass()) {
            insertDataIntoJavaFile(file, getBitSetEnumClassFromString(getCapitalCase(className)));
            insertDataIntoJavaFile(file, getBitSetEnumClassToString(getCapitalCase(className),
                                                                    (YangEnumeration) curNode));
        } else {
            insertDataIntoJavaFile(file, getToStringForEnumClass());
        }

        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);

        return formatFile(file);
    }

    /**
     * Generates interface file for rpc.
     *
     * @param file    generated file
     * @param curNode current YANG node
     * @param imports imports for file
     * @return rpc class file
     * @throws IOException when fails to generate class file
     */
    public static File generateServiceInterfaceFile(File file, YangNode curNode,
                                                    List<String> imports)
            throws IOException {

        JavaFileInfoTranslator fileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();

        TempJavaServiceFragmentFiles tempFiles =
                ((JavaCodeGeneratorInfo) curNode)
                        .getTempJavaCodeFragmentFiles().getServiceTempFiles();
        String className = getCapitalCase(fileInfo.getJavaName()) +
                SERVICE_METHOD_STRING;
        String path = fileInfo.getBaseCodeGenPath() +
                fileInfo.getPackageFilePath();
        initiateJavaFileGeneration(file, GENERATE_SERVICE_AND_MANAGER, imports,
                                   curNode, className);

        List<String> methods = new ArrayList<>();

        try {
            if (((JavaCodeGeneratorInfo) curNode).getTempJavaCodeFragmentFiles()
                    .getServiceTempFiles() != null) {
                JavaCodeGeneratorInfo javaGenInfo =
                        (JavaCodeGeneratorInfo) curNode;

                // Rpc methods
                methods.add(getDataFromTempFileHandle(
                        RPC_INTERFACE_MASK,
                        javaGenInfo.getTempJavaCodeFragmentFiles()
                                .getServiceTempFiles(), path));
            }
        } catch (IOException e) {
            throw new IOException(getErrorMsg(className, RPC_CLASS));
        }

        for (String method : methods) {
            insertDataIntoJavaFile(file, method);
        }
        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);

        return formatFile(file);
    }

    /**
     * Generates event file.
     *
     * @param file    generated file
     * @param curNode current YANG node
     * @param imports imports for file
     * @throws IOException when fails to generate class file
     */
    public static void generateEventFile(File file, YangNode curNode,
                                         List<String> imports)
            throws IOException {

        String className =
                getCapitalCase(((JavaFileInfoContainer) curNode).getJavaFileInfo()
                                       .getJavaName()) + EVENT_STRING;

        TempJavaEventFragmentFiles tempFiles =
                ((TempJavaCodeFragmentFilesContainer) curNode)
                        .getTempJavaCodeFragmentFiles().getEventFragmentFiles();

        String path = ((JavaFileInfoContainer) curNode).getJavaFileInfo()
                .getBaseCodeGenPath() +
                ((JavaFileInfoContainer) curNode).getJavaFileInfo()
                        .getPackageFilePath();
        initiateJavaFileGeneration(file, GENERATE_EVENT_CLASS, imports, curNode,
                                   className);
        try {
            insertDataIntoJavaFile(file, getEventEnumTypeStart() +
                    trimAtLast(getDataFromTempFileHandle(EVENT_ENUM_MASK,
                                                         tempFiles, path),
                               COMMA) + methodClose(FOUR_SPACE));

            insertDataIntoJavaFile(file,
                                   getDataFromTempFileHandle(EVENT_METHOD_MASK,
                                                             tempFiles, path));
        } catch (IOException e) {
            throw new IOException(getErrorMsg(className, EVENT_CLASS));
        }

        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);
        formatFile(file);
    }

    /**
     * Generates event listener file.
     *
     * @param file    generated file
     * @param curNode current YANG node
     * @param imports imports for file
     * @throws IOException when fails to generate class file
     */
    public static void generateEventListenerFile(File file, YangNode curNode,
                                                 List<String> imports)
            throws IOException {

        String className =
                getCapitalCase(((JavaFileInfoContainer) curNode).getJavaFileInfo()
                                       .getJavaName()) + EVENT_LISTENER_STRING;

        initiateJavaFileGeneration(file, GENERATE_EVENT_LISTENER_INTERFACE,
                                   imports, curNode, className);
        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);
        formatFile(file);
    }

    /**
     * Generates event subject's file.
     *
     * @param file    file handle
     * @param curNode current YANG node
     * @throws IOException when fails to do IO exceptions
     */
    public static void generateEventSubjectFile(File file, YangNode curNode)
            throws IOException {

        String className =
                getCapitalCase(((JavaFileInfoContainer) curNode).getJavaFileInfo()
                                       .getJavaName()) + EVENT_SUBJECT_NAME_SUFFIX;

        initiateJavaFileGeneration(file, GENERATE_EVENT_SUBJECT_CLASS, null,
                                   curNode, className);

        String path = ((JavaFileInfoContainer) curNode).getJavaFileInfo()
                .getBaseCodeGenPath() +
                ((JavaFileInfoContainer) curNode).getJavaFileInfo()
                        .getPackageFilePath();

        TempJavaEventFragmentFiles tempFiles =
                ((TempJavaCodeFragmentFilesContainer) curNode)
                        .getTempJavaCodeFragmentFiles().getEventFragmentFiles();

        insertDataIntoJavaFile(file, NEW_LINE);
        try {
            insertDataIntoJavaFile(
                    file, getDataFromTempFileHandle(EVENT_SUBJECT_ATTRIBUTE_MASK,
                                                    tempFiles, path));

            insertDataIntoJavaFile(
                    file, getDataFromTempFileHandle(EVENT_SUBJECT_GETTER_MASK,
                                                    tempFiles, path));

            insertDataIntoJavaFile(
                    file, getDataFromTempFileHandle(EVENT_SUBJECT_SETTER_MASK,
                                                    tempFiles, path));
        } catch (IOException e) {
            throw new IOException(getErrorMsg(className, EVENT_CLASS));
        }

        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);
        formatFile(file);
    }

    /**
     * Returns error message string.
     *
     * @param className name of the class
     * @param fileType  type of file
     * @return error message string
     */
    private static String getErrorMsg(String className, String fileType) {
        return "No data found in temporary java code fragment files for " +
                className + " while " + fileType + " file generation.";
    }

    /**
     * Checks whether leaf is present in YANG leaves holder.
     *
     * @param holder holder of YANG leaves
     * @return true if leaves are present, false otherwise
     */
    private static boolean leavesPresent(YangLeavesHolder holder) {
        return (holder.getListOfLeaf() != null &&
                !holder.getListOfLeaf().isEmpty()) ||
                (holder.getListOfLeafList() != null &&
                        !holder.getListOfLeafList().isEmpty());
    }

    /**
     * Generates RPC handler file.
     *
     * @param file    generated file
     * @param curNode current YANG node
     * @param imports imports for file
     * @return rpc class file
     * @throws IOException when fails to generate class file
     */
    public static File generateRpcHandler(File file, YangNode curNode,
                                          List<String> imports)
            throws IOException {
        initiateJavaFileGeneration(file, GENERATE_RPC_HANDLER_CLASS, imports,
                                   curNode, DEFAULT_RPC_HANDLER);

        insertDataIntoJavaFile(file, getRpcHandlerContents());

        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);

        return formatFile(file);
    }

    /**
     * Generates RPC extended command file.
     *
     * @param file    generated file
     * @param curNode current YANG node
     * @param imports imports for file
     * @return RPC extended command file
     * @throws IOException when fails to generate class file
     */
    public static File generateRpcExtendedCommand(File file, YangNode curNode,
                                                  List<String> imports)
            throws IOException {
        initiateJavaFileGeneration(file, GENERATE_RPC_EXTENDED_COMMAND_CLASS, imports,
                                   curNode, RPC_EXTENDED_COMMAND);

        insertDataIntoJavaFile(file, getRpcExtendedCommandContents());

        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);

        return formatFile(file);
    }

    /**
     * Generates RPC command file.
     *
     * @param file    generated file
     * @param curNode current YANG node
     * @param imports imports for file
     * @return RPC command file
     * @throws IOException when fails to generate class file
     */
    public static File generateRpcCommand(File file, YangNode curNode,
                                          List<String> imports)
            throws IOException {
        String className = getCapitalCase(getCamelCase(
                curNode.getJavaClassNameOrBuiltInType(), null)) + COMMAND;
        initiateJavaFileGeneration(file, GENERATE_RPC_COMMAND_CLASS, imports,
                                   curNode, className);

        insertDataIntoJavaFile(file, getRpcCommandContents(curNode));

        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);

        return formatFile(file);
    }

    /**
     * Generates register RPC file.
     *
     * @param file    generated file
     * @param curNode current YANG node
     * @param imports imports for file
     * @return register RPC file
     * @throws IOException when fails to generate class file
     */
    public static File generateRegisterRpc(File file, YangNode curNode,
                                           List<String> imports)
            throws IOException {
        initiateJavaFileGeneration(file, GENERATE_RPC_REGISTER_CLASS, imports,
                                   curNode, REGISTER_RPC);

        insertDataIntoJavaFile(file, getRegisterRpcContents(curNode));

        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);

        return formatFile(file);
    }
}
