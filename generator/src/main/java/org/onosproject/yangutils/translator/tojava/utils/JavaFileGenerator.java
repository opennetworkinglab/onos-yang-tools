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
import org.onosproject.yangutils.datamodel.YangAugmentableNode;
import org.onosproject.yangutils.datamodel.YangChoice;
import org.onosproject.yangutils.datamodel.YangDerivedInfo;
import org.onosproject.yangutils.datamodel.YangEnumeration;
import org.onosproject.yangutils.datamodel.YangLeavesHolder;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangType;
import org.onosproject.yangutils.datamodel.YangTypeDef;
import org.onosproject.yangutils.datamodel.YangUnion;
import org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yangutils.translator.tojava.JavaAttributeInfo;
import org.onosproject.yangutils.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yangutils.translator.tojava.JavaFileInfoContainer;
import org.onosproject.yangutils.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yangutils.translator.tojava.JavaImportData;
import org.onosproject.yangutils.translator.tojava.TempJavaCodeFragmentFilesContainer;
import org.onosproject.yangutils.translator.tojava.TempJavaEnumerationFragmentFiles;
import org.onosproject.yangutils.translator.tojava.TempJavaEventFragmentFiles;
import org.onosproject.yangutils.translator.tojava.TempJavaServiceFragmentFiles;
import org.onosproject.yangutils.translator.tojava.TempJavaTypeFragmentFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;
import static org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes.DERIVED;
import static org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes.IDENTITYREF;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.BUILDER_CLASS_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.BUILDER_INTERFACE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.DEFAULT_CLASS_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_ENUM_CLASS;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_EVENT_CLASS;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_EVENT_LISTENER_INTERFACE;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_EVENT_SUBJECT_CLASS;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_TYPEDEF_CLASS;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_UNION_CLASS;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.INTERFACE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.ADD_TO_LIST_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.ADD_TO_LIST_INTERFACE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.ATTRIBUTES_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.CONSTRUCTOR_FOR_TYPE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.CONSTRUCTOR_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.ENUM_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.EQUALS_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.EVENT_ENUM_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.EVENT_METHOD_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.EVENT_SUBJECT_ATTRIBUTE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.EVENT_SUBJECT_GETTER_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.EVENT_SUBJECT_SETTER_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.FROM_STRING_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.GETTER_FOR_CLASS_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.GETTER_FOR_INTERFACE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.HASH_CODE_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.LEAF_IDENTIFIER_ENUM_ATTRIBUTES_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.OF_STRING_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.RPC_INTERFACE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.SETTER_FOR_CLASS_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.SETTER_FOR_INTERFACE_MASK;
import static org.onosproject.yangutils.translator.tojava.GeneratedTempFileType.TO_STRING_IMPL_MASK;
import static org.onosproject.yangutils.translator.tojava.TempJavaFragmentFiles.getCurNodeAsAttributeInTarget;
import static org.onosproject.yangutils.translator.tojava.YangJavaModelUtils.isGetSetOfRootNodeRequired;
import static org.onosproject.yangutils.translator.tojava.utils.IndentationType.FOUR_SPACE;
import static org.onosproject.yangutils.translator.tojava.utils.JavaCodeSnippetGen.getEnumsValueAttribute;
import static org.onosproject.yangutils.translator.tojava.utils.JavaCodeSnippetGen.getEventEnumTypeStart;
import static org.onosproject.yangutils.translator.tojava.utils.JavaCodeSnippetGen.getOperationTypeEnum;
import static org.onosproject.yangutils.translator.tojava.utils.JavaCodeSnippetGen.getSetValueParaForUnionClass;
import static org.onosproject.yangutils.translator.tojava.utils.JavaFileGeneratorUtils.getDataFromTempFileHandle;
import static org.onosproject.yangutils.translator.tojava.utils.JavaFileGeneratorUtils.initiateJavaFileGeneration;
import static org.onosproject.yangutils.translator.tojava.utils.MethodBodyTypes.ENUM_METHOD_INT_VALUE;
import static org.onosproject.yangutils.translator.tojava.utils.MethodBodyTypes.ENUM_METHOD_STRING_VALUE;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.builderMethod;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.generateBuildMethodForSubTree;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getAddAugmentInfoMethodImpl;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getAddAugmentInfoMethodInterface;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getAugmentsDataMethodForService;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getBitSetEnumClassFromString;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getBitSetEnumClassToString;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getConstructorStart;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getEnumsConstructor;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getEnumsOfValueMethod;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getEqualsMethodClose;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getEqualsMethodOpen;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getFromStringMethodClose;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getFromStringMethodSignature;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getGetter;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getGetterString;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getHashCodeMethodClose;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getHashCodeMethodOpen;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getInterfaceLeafIdEnumSignature;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getRangeValidatorMethodForUnion;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getSetterForSelectLeaf;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getSetterString;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getToStringForEnumClass;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getToStringForType;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getToStringMethodClose;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getToStringMethodOpen;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getUnionToStringMethod;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getYangAugmentInfoImpl;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.getYangAugmentInfoInterface;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.isLeafValueSetInterface;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.isSelectLeafSetInterface;
import static org.onosproject.yangutils.translator.tojava.utils.MethodsGenerator.setSelectLeafSetInterface;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getInterfaceLeafIdEnumMethods;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getIsSelectLeafSet;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.getIsValueLeafSet;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.methodClose;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.signatureClose;
import static org.onosproject.yangutils.translator.tojava.utils.SubtreeFilteringMethodsGenerator.getAugmentableSubTreeFiltering;
import static org.onosproject.yangutils.translator.tojava.utils.SubtreeFilteringMethodsGenerator.getProcessChildNodeSubtreeFiltering;
import static org.onosproject.yangutils.translator.tojava.utils.SubtreeFilteringMethodsGenerator.getProcessLeafListSubtreeFiltering;
import static org.onosproject.yangutils.translator.tojava.utils.SubtreeFilteringMethodsGenerator.getProcessLeafSubtreeFiltering;
import static org.onosproject.yangutils.translator.tojava.utils.SubtreeFilteringMethodsGenerator.getProcessSubTreeFilteringEnd;
import static org.onosproject.yangutils.translator.tojava.utils.SubtreeFilteringMethodsGenerator.getProcessSubTreeForChoiceInterface;
import static org.onosproject.yangutils.translator.tojava.utils.SubtreeFilteringMethodsGenerator.getProcessSubtreeFilteringStart;
import static org.onosproject.yangutils.translator.tojava.utils.SubtreeFilteringMethodsGenerator.getProcessSubtreeFunctionBody;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorUtils.addDefaultConstructor;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorUtils.getBeanFiles;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorUtils.getTypeFiles;
import static org.onosproject.yangutils.utils.UtilConstants.BIG_INTEGER;
import static org.onosproject.yangutils.utils.UtilConstants.BUILDER;
import static org.onosproject.yangutils.utils.UtilConstants.BUILDER_CLASS;
import static org.onosproject.yangutils.utils.UtilConstants.BUILDER_INTERFACE;
import static org.onosproject.yangutils.utils.UtilConstants.CLOSE_CURLY_BRACKET;
import static org.onosproject.yangutils.utils.UtilConstants.COMMA;
import static org.onosproject.yangutils.utils.UtilConstants.DEFAULT;
import static org.onosproject.yangutils.utils.UtilConstants.DEFAULT_CAPS;
import static org.onosproject.yangutils.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.ENUM_CLASS;
import static org.onosproject.yangutils.utils.UtilConstants.EVENT_CLASS;
import static org.onosproject.yangutils.utils.UtilConstants.EVENT_LISTENER_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.EVENT_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.EVENT_SUBJECT_NAME_SUFFIX;
import static org.onosproject.yangutils.utils.UtilConstants.IMPL_CLASS;
import static org.onosproject.yangutils.utils.UtilConstants.INT;
import static org.onosproject.yangutils.utils.UtilConstants.INTERFACE;
import static org.onosproject.yangutils.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yangutils.utils.UtilConstants.OP_PARAM;
import static org.onosproject.yangutils.utils.UtilConstants.PRIVATE;
import static org.onosproject.yangutils.utils.UtilConstants.PROTECTED;
import static org.onosproject.yangutils.utils.UtilConstants.PUBLIC;
import static org.onosproject.yangutils.utils.UtilConstants.RPC_CLASS;
import static org.onosproject.yangutils.utils.UtilConstants.SEMI_COLON;
import static org.onosproject.yangutils.utils.UtilConstants.SERVICE_METHOD_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.TYPEDEF_CLASS;
import static org.onosproject.yangutils.utils.UtilConstants.UNION_CLASS;
import static org.onosproject.yangutils.utils.io.impl.JavaDocGen.JavaDocType.GETTER_METHOD;
import static org.onosproject.yangutils.utils.io.impl.JavaDocGen.getJavaDoc;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getCapitalCase;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.insertDataIntoJavaFile;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.replaceLast;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.trimAtLast;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.validateLineLength;

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

        // Generate ENUM interface
        if (curNode instanceof RpcNotificationContainer) {
            insertDataIntoJavaFile(file, getOperationTypeEnum());
        }
        List<String> methods = new ArrayList<>();

        //Add only for choice class
        if (curNode instanceof YangChoice) {
            insertDataIntoJavaFile(file, getProcessSubTreeForChoiceInterface(
                    curNode));
        }

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
            } catch (IOException e) {
                throw new IOException(getErrorMsg(className, INTERFACE));
            }
        }

        if (curNode instanceof YangAugmentableNode &&
                !(curNode instanceof YangChoice)) {
            methods.add(getYangAugmentInfoInterface());
        }

        if (leavesPresent) {
            methods.add(isLeafValueSetInterface());
            if (curNode.isOpTypeReq()) {
                methods.add(isSelectLeafSetInterface());
            }
        }
        for (String method : methods) {
            insertDataIntoJavaFile(file, method);
        }
        return file;
    }

    /**
     * Returns generated builder interface file for current node.
     *
     * @param file        file
     * @param curNode     current YANG node
     * @param attrPresent if any attribute is present or not
     * @return builder interface file
     * @throws IOException when fails to write in file
     */
    public static File generateBuilderInterfaceFile(File file, YangNode curNode,
                                                    boolean attrPresent)
            throws IOException {

        JavaFileInfoTranslator fileInfo =
                ((JavaFileInfoContainer) curNode).getJavaFileInfo();

        boolean leavesPresent;
        YangLeavesHolder leavesHolder;
        if (curNode instanceof YangLeavesHolder &&
                curNode.isOpTypeReq()) {
            leavesHolder = (YangLeavesHolder) curNode;
            leavesPresent = leavesPresent(leavesHolder);
        } else {
            leavesPresent = false;
        }

        String className = getCapitalCase(fileInfo.getJavaName());
        String path;
        if (curNode instanceof RpcNotificationContainer) {
            path = fileInfo.getPluginConfig().getCodeGenDir() +
                    fileInfo.getPackageFilePath();
        } else {
            path = fileInfo.getBaseCodeGenPath() +
                    fileInfo.getPackageFilePath();
        }

        initiateJavaFileGeneration(file, BUILDER_INTERFACE_MASK, null, curNode,
                                   className);
        List<String> methods = new ArrayList<>();
        if (attrPresent) {
            try {
                //Getter methods.
                methods.add(getDataFromTempFileHandle(
                        GETTER_FOR_INTERFACE_MASK,
                        getBeanFiles(curNode), path));

                //Setter methods.
                methods.add(getDataFromTempFileHandle(
                        SETTER_FOR_INTERFACE_MASK,
                        getBeanFiles(curNode), path));

                //Add to list method.
                insertDataIntoJavaFile(file, getDataFromTempFileHandle(
                        ADD_TO_LIST_INTERFACE_MASK, getBeanFiles(curNode), path));
            } catch (IOException e) {
                throw new IOException(getErrorMsg(className, BUILDER_INTERFACE));
            }
        }

        if (curNode instanceof YangAugmentableNode &&
                !(curNode instanceof YangChoice)) {
            methods.add(getAddAugmentInfoMethodInterface(className + BUILDER));
            methods.add(getYangAugmentInfoInterface());
        }

        if (leavesPresent) {
            methods.add(setSelectLeafSetInterface(className));
        }
        //Add build method to builder interface file.
        methods.add(((TempJavaCodeFragmentFilesContainer) curNode)
                            .getTempJavaCodeFragmentFiles()
                            .addBuildMethodForInterface());

        //Add getters and setters in builder interface.
        for (String method : methods) {
            insertDataIntoJavaFile(file, method);
        }

        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);
        return file;
    }

    /**
     * Returns generated builder class file for current node.
     *
     * @param file        file
     * @param curNode     current YANG node
     * @param attrPresent if any attribute is present or not
     * @return builder class file
     * @throws IOException when fails to write in file
     */
    public static File generateBuilderClassFile(File file, YangNode curNode,
                                                boolean attrPresent)
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

        String className = getCapitalCase(fileInfo.getJavaName());
        String path;
        if (curNode instanceof RpcNotificationContainer) {
            path = fileInfo.getPluginConfig().getCodeGenDir() +
                    fileInfo.getPackageFilePath();
        } else {
            path = fileInfo.getBaseCodeGenPath() +
                    fileInfo.getPackageFilePath();
        }

        initiateJavaFileGeneration(file, BUILDER_CLASS_MASK, null, curNode,
                                   className);
        List<String> methods = new ArrayList<>();
        if (attrPresent) {

            //Add attribute strings.
            try {
                insertDataIntoJavaFile(file, getDataFromTempFileHandle(
                        ATTRIBUTES_MASK, getBeanFiles(curNode), path));
            } catch (IOException e) {
                throw new IOException(getErrorMsg(className, BUILDER_CLASS));
            }
            try {
                //Getter methods.
                methods.add(getDataFromTempFileHandle(
                        GETTER_FOR_CLASS_MASK, getBeanFiles(curNode), path));
                // Setter methods.
                methods.add(getDataFromTempFileHandle(
                        SETTER_FOR_CLASS_MASK, getBeanFiles(curNode), path));

                //Add to list impl method.
                methods.add(getDataFromTempFileHandle(
                        ADD_TO_LIST_IMPL_MASK, getBeanFiles(curNode), path));

                insertDataIntoJavaFile(file, NEW_LINE);

                //Add operation attribute methods.
                if (leavesPresent && curNode.isOpTypeReq()) {
                    insertDataIntoJavaFile(file, NEW_LINE);
                    methods.add(getSetterForSelectLeaf(className));
                }
            } catch (IOException e) {
                throw new IOException(getErrorMsg(className, BUILDER_CLASS));
            }
        } else {
            insertDataIntoJavaFile(file, NEW_LINE);
        }

        if (curNode instanceof YangAugmentableNode) {
            methods.add(getAddAugmentInfoMethodImpl(className + BUILDER));
            methods.add(getYangAugmentInfoImpl());
        }

        // Add default constructor and build method impl.
        methods.add(((TempJavaCodeFragmentFilesContainer) curNode)
                            .getTempJavaCodeFragmentFiles()
                            .addBuildMethodImpl());
        if (curNode.isOpTypeReq()) {
            methods.add(generateBuildMethodForSubTree(curNode));
        }
        methods.add(addDefaultConstructor(curNode, PUBLIC, BUILDER));

        //Add methods in builder class.
        for (String method : methods) {
            insertDataIntoJavaFile(file, method);
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

        if (curNode instanceof YangAugmentableNode) {
            methods.add(getYangAugmentInfoImpl());
        }
        try {
            //Constructor.
            String constructor = getConstructorStart(className, rootNode) +
                    getDataFromTempFileHandle(
                            CONSTRUCTOR_IMPL_MASK, getBeanFiles(curNode), path)
                    + methodClose(FOUR_SPACE);
            methods.add(constructor);
            if (curNode.isOpTypeReq()) {
                String augmentableSubTreeFiltering = EMPTY_STRING;
                if (curNode instanceof YangAugmentableNode) {
                    // add is filter content match.
                    augmentableSubTreeFiltering = getAugmentableSubTreeFiltering();
                }
                methods.add(getProcessSubtreeFilteringStart(curNode) +
                                    getProcessSubtreeFunctionBody(curNode) +
                                    augmentableSubTreeFiltering +
                                    getProcessSubTreeFilteringEnd(name));

                if (curNode instanceof YangLeavesHolder) {
                    if (((YangLeavesHolder) curNode).getListOfLeaf() != null &&
                            !((YangLeavesHolder) curNode).getListOfLeaf().isEmpty()) {
                        methods.add(getProcessLeafSubtreeFiltering(curNode,
                                                                   path));
                    }
                    if (((YangLeavesHolder) curNode).getListOfLeafList() != null &&
                            !((YangLeavesHolder) curNode).getListOfLeafList().isEmpty()) {
                        methods.add(getProcessLeafListSubtreeFiltering(curNode,
                                                                       path));
                    }
                }

                if (curNode.getChild() != null) {
                    methods.add(getProcessChildNodeSubtreeFiltering(curNode,
                                                                    path));
                }
            }
        } catch (IOException e) {
            throw new IOException(getErrorMsg(className, IMPL_CLASS));
        }

        methods.add(addDefaultConstructor(curNode, PROTECTED, DEFAULT));

        methods.add(builderMethod(className));
        if (leavesPresent) {
            methods.add(getIsValueLeafSet());
            if (curNode.isOpTypeReq()) {
                methods.add(getIsSelectLeafSet());
            }
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
        YangTypeDef typeDef = (YangTypeDef) curNode;
        List<YangType<?>> types = typeDef.getTypeList();
        YangType type = types.get(0);
        YangDataTypes yangDataTypes = type.getDataType();

        initiateJavaFileGeneration(file, className, GENERATE_TYPEDEF_CLASS,
                                   imports, path);

        List<String> methods = new ArrayList<>();

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
            if (type.getDataType() == DERIVED &&
                    ((YangDerivedInfo) type.getDataTypeExtendedInfo())
                            .getEffectiveBuiltInType()
                            == IDENTITYREF) {
                yangDataTypes = IDENTITYREF;
            }

            if (type.getDataType() == IDENTITYREF) {
                yangDataTypes = IDENTITYREF;
            }

            if (yangDataTypes != IDENTITYREF) {
                methods.add(getFromStringMethodSignature(className) +
                                    getDataFromTempFileHandle(
                                            FROM_STRING_IMPL_MASK,
                                            javaGenInfo.getTempJavaCodeFragmentFiles()
                                                    .getTypeTempFiles(), path) +
                                    getFromStringMethodClose());
            }
        } catch (IOException e) {
            throw new IOException(getErrorMsg(className, TYPEDEF_CLASS));
        }

        for (String method : methods) {
            insertDataIntoJavaFile(file, method);
        }
        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);

        return validateLineLength(file);
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

        return validateLineLength(file);
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
            insertDataIntoJavaFile(file, JavaCodeSnippetGen
                    .addStaticAttributeIntRange(PRIVATE,
                                                tempFiles.getIntIndex() <
                                                        tempFiles.getUIntIndex()));
        }

        if (longConflict) {
            insertDataIntoJavaFile(file, JavaCodeSnippetGen
                    .addStaticAttributeLongRange(PRIVATE,
                                                 tempFiles.getLongIndex() <
                                                         tempFiles.getULongIndex()));
        }

        if (shortConflict) {
            insertDataIntoJavaFile(file, JavaCodeSnippetGen
                    .addStaticAttributeShortRange(PRIVATE,
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
            insertDataIntoJavaFile(file, getBitSetEnumClassFromString
                    (getCapitalCase(className)));
            insertDataIntoJavaFile(file, getBitSetEnumClassToString(
                    getCapitalCase(className), (YangEnumeration) curNode));
        } else {
            insertDataIntoJavaFile(file, getToStringForEnumClass());
        }

        insertDataIntoJavaFile(file, CLOSE_CURLY_BRACKET + NEW_LINE);

        return validateLineLength(file);
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
        JavaAttributeInfo rootAttribute =
                getCurNodeAsAttributeInTarget(curNode, curNode, false,
                                              tempFiles);

        try {

            if (isGetSetOfRootNodeRequired(curNode)) {
                //Getter methods.
                methods.add(getGetterString(rootAttribute,
                                            GENERATE_SERVICE_AND_MANAGER) +
                                    NEW_LINE);
                // Setter methods.
                methods.add(getSetterString(rootAttribute, className,
                                            GENERATE_SERVICE_AND_MANAGER) +
                                    NEW_LINE);
            }

            methods.add(getAugmentsDataMethodForService(curNode));

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

        return validateLineLength(file);
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
        validateLineLength(file);
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
        validateLineLength(file);
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
        validateLineLength(file);
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
        return holder.getListOfLeaf() != null &&
                !holder.getListOfLeaf().isEmpty();
    }

}
