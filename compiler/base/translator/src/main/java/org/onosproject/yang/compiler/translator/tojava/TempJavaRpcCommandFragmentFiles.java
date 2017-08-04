/*
 * Copyright 2017-present Open Networking Foundation
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

package org.onosproject.yang.compiler.translator.tojava;

import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.utils.JavaExtendsListHolder;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.findRpcInput;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.findRpcOutput;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGenerator.generateRpcCommand;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaIdentifierSyntax.createPackage;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodClassTypes.CLASS_TYPE;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getImportString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getOverRideString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getVariableDeclaration;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.multiAttrMethodSignature;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.valueAssign;
import static org.onosproject.yang.compiler.utils.UtilConstants.ADD_BRANCH_POINT_SCHEMA;
import static org.onosproject.yang.compiler.utils.UtilConstants.BUILD;
import static org.onosproject.yang.compiler.utils.UtilConstants.BUILDER;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMA;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMAND;
import static org.onosproject.yang.compiler.utils.UtilConstants.CREATE_DATA_NODE;
import static org.onosproject.yang.compiler.utils.UtilConstants.CREATE_MODEL;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_MODEL_OBJECT_DATA;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_RESOURCE_DATA;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.EQUAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.FINAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.FOUR_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT;
import static org.onosproject.yang.compiler.utils.UtilConstants.MODEL_CONVERTER;
import static org.onosproject.yang.compiler.utils.UtilConstants.MODEL_OBJECT_DATA;
import static org.onosproject.yang.compiler.utils.UtilConstants.MODEL_PKG;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.NULL;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.PRIVATE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PUBLIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUOTES;
import static org.onosproject.yang.compiler.utils.UtilConstants.RESOURCE_DATA;
import static org.onosproject.yang.compiler.utils.UtilConstants.RESOURCE_ID;
import static org.onosproject.yang.compiler.utils.UtilConstants.RETURN;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_EXTENDED_COMMAND;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_INPUT;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_OUTPUT;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_SUCCESS;
import static org.onosproject.yang.compiler.utils.UtilConstants.SEMI_COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.SERVICE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SIXTEEN_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH_FOR_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.STATIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.STRING_DATA_TYPE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SUPER;
import static org.onosproject.yang.compiler.utils.UtilConstants.THIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.VOID;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_RPC_SERVICE;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavaDocForExecuteMethod;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavaDocForRpcCommandConstructor;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getAbsolutePackagePath;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;

/**
 * Represents implementation of java code fragments temporary
 * implementations. Maintains the temp files required specific for RPC command
 * java snippet generation.
 */
public class TempJavaRpcCommandFragmentFiles extends TempJavaFragmentFiles {

    /**
     * Temporary file handle for RPC command class file.
     */
    private File rpcCommandClassTempFileHandle;

    private static final String VAR_RPC_INPUT = "rpcInput";
    private static final String VAR_INPUT = "input";
    private static final String VAR_OUTPUT = "output";
    private static final String VAR_MSG_ID = "msgId";
    private static final String VAR_CFG_SERVICE = "cfgService";
    private static final String VAR_MODEL_CONVERTER = "modelConverter";
    private static final String EXECUTE = "execute";
    private static final String GET_RESOURCE_ID = "getResourceId";
    private static final String VAR_INPUT_OBJECT = "inputObject";
    private static final String VAR_OUTPUT_OBJECT = "outputObject";
    private static final String RPC_RESPONSE = "rpcResponse";
    private static final String VAR_INPUT_MO = "inputMo";
    private static final String VAR_OUTPUT_MO = "outputMo";
    private static final String VAR_INPUT_DATA = "inputData";
    private static final String VAR_OUTPUT_DATA = "outputData";
    private static final String BUILDER_METHOD = "builder";
    private static final String ADD_DATA_NODE = "addDataNode";
    private static final String ADD_MODEL_OBJECT = "addModelObject";
    private static final String VAR_RESOURCE_ID = "resourceId";
    private static final String VAR_APP_SERVICE = "appService";
    private static final String STR_CONST_RPC_NAME = "RPC_NAME";
    private static final String STR_CONST_RPC_NAMESPACE = "RPC_NAMESPACE";
    private static final String STR_CONST_SLASH = "SLASH";

    @Override
    public void generateJavaFile(int fileType, YangNode curNode) throws IOException {
        JavaImportData importData = ((JavaCodeGeneratorInfo) curNode)
                .getTempJavaCodeFragmentFiles().getRpcCommandTempFiles()
                .getJavaImportData();
        String parentPkg = curNode.getParent().getJavaPackage();
        String parentClassName = getCapitalCase(getCamelCase(
                curNode.getParent().getJavaClassNameOrBuiltInType(), null));
        String className = getCapitalCase(getCamelCase(
                curNode.getJavaClassNameOrBuiltInType(), null)) + COMMAND;
        createPackage(curNode);

        // add RPC input import
        YangNode inputNode = findRpcInput(curNode);
        if (inputNode != null) {
            JavaQualifiedTypeInfoTranslator inputImport = new
                    JavaQualifiedTypeInfoTranslator();
            inputImport.setForInterface(false);
            inputImport.setPkgInfo(inputNode.getJavaPackage());
            inputImport.setClassInfo(getCapitalCase(getCamelCase(
                    inputNode.getJavaClassNameOrBuiltInType(), null)));
            importData.addImportInfo(inputImport, className,
                                     curNode.getJavaPackage());
        }

        // add RPC output import
        YangNode outputNode = findRpcOutput(curNode);
        if (outputNode != null) {
            JavaQualifiedTypeInfoTranslator outputImport = new
                    JavaQualifiedTypeInfoTranslator();
            outputImport.setForInterface(false);
            outputImport.setPkgInfo(outputNode.getJavaPackage());
            outputImport.setClassInfo(getCapitalCase(getCamelCase(
                    outputNode.getJavaClassNameOrBuiltInType(), null)));

            JavaQualifiedTypeInfoTranslator defaultOutput = new
                    JavaQualifiedTypeInfoTranslator();
            defaultOutput.setForInterface(false);
            defaultOutput.setPkgInfo(outputNode.getJavaPackage());
            defaultOutput.setClassInfo(DEFAULT_CAPS + getCapitalCase(
                    getCamelCase(outputNode.getJavaClassNameOrBuiltInType(),
                                 null)));
            importData.addImportInfo(defaultOutput, className,
                                     curNode.getJavaPackage());

        }

        // add application service import
        JavaQualifiedTypeInfoTranslator importInfo = new
                JavaQualifiedTypeInfoTranslator();
        importInfo.setForInterface(false);
        importInfo.setPkgInfo(parentPkg);
        importInfo.setClassInfo(parentClassName + SERVICE);
        importData.addImportInfo(importInfo, className, curNode
                .getJavaPackage());

        // add RPC extended command import
        JavaQualifiedTypeInfoTranslator extendedCmdImport = new
                JavaQualifiedTypeInfoTranslator();
        extendedCmdImport.setForInterface(false);
        extendedCmdImport.setPkgInfo(parentPkg);
        extendedCmdImport.setClassInfo(RPC_EXTENDED_COMMAND);

        importData.addImportInfo(extendedCmdImport, className,
                                 curNode.getJavaPackage());

        List<String> imports = importData.getImports(false);
        imports.add(importData.getImportForModelConverter());
        imports.add(importData.getImportForResourceId());
        imports.add(importData.getImportForRpcInput());
        imports.add(importData.getImportForRpcOutput());
        imports.add(importData.getImportForDynamicStoreService());
        if (inputNode != null || outputNode != null) {
            imports.add(getImportString(MODEL_PKG, RESOURCE_DATA));
            imports.add(getImportString(MODEL_PKG, MODEL_OBJECT_DATA));
        }
        if (inputNode != null) {
            imports.add(getImportString(MODEL_PKG, DEFAULT_RESOURCE_DATA));
        }
        if (outputNode != null) {
            imports.add(getImportString(MODEL_PKG, DEFAULT_MODEL_OBJECT_DATA));
        }
        imports.add(importData.getImportForRpcSuccess());
        try {
            rpcCommandClassTempFileHandle = getJavaFileHandle(className);
            generateRpcCommand(rpcCommandClassTempFileHandle, curNode, imports);
        } catch (IOException e) {
            throw new TranslatorException(
                    "Failed to generate code for RPC command " + curNode.getName());
        }
    }

    /**
     * Creates an instance of temporary java code fragment.
     *
     * @param javaFileInfo generated file information
     * @throws IOException when fails to create new file handle
     */
    TempJavaRpcCommandFragmentFiles(JavaFileInfoTranslator javaFileInfo)
            throws IOException {
        super(javaFileInfo);
        setJavaExtendsListHolder(new JavaExtendsListHolder());
        setJavaImportData(new JavaImportData());
        setJavaFileInfo(javaFileInfo);
        setAbsoluteDirPath(getAbsolutePackagePath(
                getJavaFileInfo().getBaseCodeGenPath(),
                getJavaFileInfo().getPackageFilePath()));
    }

    /**
     * Removes all temporary file handles.
     *
     * @param isErrorOccurred flag to tell translator that error has occurred
     *                        while code generation
     * @throws IOException when failed to delete the temporary files
     */
    @Override
    public void freeTemporaryResources(boolean isErrorOccurred)
            throws IOException {
        super.freeTemporaryResources(isErrorOccurred);
    }

    /**
     * Returns RPC commands contents.
     *
     * @param node YANG RPC node
     * @return RPC commands contents
     */
    public static String getRpcCommandContents(YangNode node) {
        String appService = node.getParent()
                .getJavaClassNameOrBuiltInType() + SERVICE;
        String className = getCapitalCase(getCamelCase(
                node.getJavaClassNameOrBuiltInType(), null)) + COMMAND;

        // parameters for constructors
        Map<String, String> param = new LinkedHashMap<>();
        param.put(VAR_CFG_SERVICE, YANG_RPC_SERVICE);
        param.put(VAR_MODEL_CONVERTER, MODEL_CONVERTER);
        param.put(VAR_APP_SERVICE, getCapitalCase(appService));

        String modifier = PRIVATE + SPACE + STATIC + SPACE + FINAL;
        String nameSpace = QUOTES + node.getParent().getNameSpace()
                .getModuleNamespace() + QUOTES;
        String slashValue = QUOTES + SLASH_FOR_STRING + QUOTES;
        String rpcName = QUOTES + node.getName() + QUOTES;

        // add attributes
        StringBuilder builder = new StringBuilder();
        builder.append(getVariableDeclaration(VAR_MODEL_CONVERTER, MODEL_CONVERTER,
                                              FOUR_SPACE_INDENTATION,
                                              PRIVATE, null))
                .append(getVariableDeclaration(VAR_APP_SERVICE, getCapitalCase(appService),
                                               FOUR_SPACE_INDENTATION,
                                               PRIVATE, null))
                .append(getVariableDeclaration(VAR_CFG_SERVICE,
                                               YANG_RPC_SERVICE,
                                               FOUR_SPACE_INDENTATION,
                                               PRIVATE, null))
                .append(getVariableDeclaration(STR_CONST_RPC_NAME,
                                               STRING_DATA_TYPE,
                                               FOUR_SPACE_INDENTATION,
                                               modifier, rpcName))
                .append(getVariableDeclaration(STR_CONST_RPC_NAMESPACE,
                                               STRING_DATA_TYPE,
                                               FOUR_SPACE_INDENTATION,
                                               modifier, nameSpace))
                .append(getVariableDeclaration(STR_CONST_SLASH,
                                               STRING_DATA_TYPE,
                                               FOUR_SPACE_INDENTATION,
                                               modifier, slashValue))
                .append(NEW_LINE)

                // add constructor
                .append(getJavaDocForRpcCommandConstructor(
                        node.getJavaClassNameOrBuiltInType()))
                .append(NEW_LINE)
                .append(multiAttrMethodSignature(className, null, PUBLIC,
                                                 null, param, CLASS_TYPE,
                                                 FOUR_SPACE_INDENTATION))
                .append(EIGHT_SPACE_INDENTATION).append(SUPER)
                .append(OPEN_PARENTHESIS).append(GET_RESOURCE_ID)
                .append(OPEN_PARENTHESIS).append(CLOSE_PARENTHESIS)
                .append(CLOSE_PARENTHESIS).append(SEMI_COLON).append(NEW_LINE)
                .append(valueAssign(THIS + PERIOD + VAR_CFG_SERVICE,
                                    VAR_CFG_SERVICE, EIGHT_SPACE_INDENTATION))
                .append(valueAssign(THIS + PERIOD + VAR_MODEL_CONVERTER,
                                    VAR_MODEL_CONVERTER,
                                    EIGHT_SPACE_INDENTATION))
                .append(valueAssign(THIS + PERIOD + VAR_APP_SERVICE,
                                    VAR_APP_SERVICE, EIGHT_SPACE_INDENTATION))

                .append(FOUR_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(NEW_LINE)

                //add execute method
                .append(getRpcCommandExecuteMethod(node))

                //add get resource id method
                .append(getResourceIdMethod());

        // add execute method with msgid
        return builder.toString();
    }

    /**
     * Returns RPC command execute method.
     *
     * @param node Yang RPC node
     * @return RPC command execute method
     */
    private static String getRpcCommandExecuteMethod(YangNode node) {
        StringBuilder builder = new StringBuilder(getOverRideString())

                // execute method with only RPC input
                .append(FOUR_SPACE_INDENTATION).append(PUBLIC).append(SPACE)
                .append(VOID).append(SPACE).append(EXECUTE)
                .append(OPEN_PARENTHESIS).append(RPC_INPUT)
                .append(SPACE).append(VAR_RPC_INPUT).append(CLOSE_PARENTHESIS)
                .append(SPACE).append(OPEN_CURLY_BRACKET).append(NEW_LINE)
                .append(FOUR_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(NEW_LINE)

                // execute method with RPC input and msg id
                .append(NEW_LINE).append(getJavaDocForExecuteMethod())
                .append(NEW_LINE).append(FOUR_SPACE_INDENTATION).append(PUBLIC)
                .append(SPACE).append(VOID).append(SPACE).append(EXECUTE)
                .append(OPEN_PARENTHESIS).append(RPC_INPUT).append(SPACE)
                .append(VAR_RPC_INPUT).append(COMMA).append(SPACE).append(INT)
                .append(SPACE).append(VAR_MSG_ID).append(CLOSE_PARENTHESIS)
                .append(SPACE).append(OPEN_CURLY_BRACKET).append(NEW_LINE)

                .append(getExecuteMethodContents(node))
                .append(FOUR_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(NEW_LINE);

        return builder.toString();
    }

    /**
     * Returns get resource id method.
     *
     * @return get resource id method
     */
    private static String getResourceIdMethod() {
        StringBuilder builder = new StringBuilder(NEW_LINE);

        builder.append(FOUR_SPACE_INDENTATION).append(PRIVATE).append(SPACE)
                .append(STATIC).append(SPACE).append(RESOURCE_ID).append(SPACE)
                .append(GET_RESOURCE_ID).append(OPEN_PARENTHESIS)
                .append(CLOSE_PARENTHESIS)
                .append(SPACE).append(OPEN_CURLY_BRACKET).append(NEW_LINE)

                // constructing resource id for RPC
                .append(EIGHT_SPACE_INDENTATION).append(RETURN).append(SPACE)
                .append(NEW).append(SPACE).append(RESOURCE_ID).append(PERIOD)
                .append(BUILDER).append(OPEN_PARENTHESIS)
                .append(CLOSE_PARENTHESIS).append(PERIOD)
                .append(ADD_BRANCH_POINT_SCHEMA).append(OPEN_PARENTHESIS)
                .append(STR_CONST_SLASH).append(COMMA).append(SPACE)
                .append(NULL).append(CLOSE_PARENTHESIS).append(NEW_LINE)
                .append(SIXTEEN_SPACE_INDENTATION).append(PERIOD)
                .append(ADD_BRANCH_POINT_SCHEMA).append(OPEN_PARENTHESIS)
                .append(STR_CONST_RPC_NAME).append(COMMA).append(SPACE)
                .append(STR_CONST_RPC_NAMESPACE).append(CLOSE_PARENTHESIS)
                .append(PERIOD).append(BUILD).append(OPEN_PARENTHESIS)
                .append(CLOSE_PARENTHESIS).append(SEMI_COLON).append(NEW_LINE)
                .append(FOUR_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(NEW_LINE);

        return builder.toString();
    }

    /**
     * Returns execute method contents.
     *
     * @param node YANG RPC node
     * @return execute method contents
     */
    private static String getExecuteMethodContents(YangNode node) {
        StringBuilder builder = new StringBuilder();
        builder.append(invokeCreateModelString(node))
                .append(invokeRpcString(node))
                .append(invokeCreateDataNodeString(node))
                .append(createRpcOutputString(node))
                .append(invokeRpcResponseString());
        return builder.toString();
    }

    /**
     * Returns create model for input data node statement.
     *
     * @param node YANG RPC node
     * @return create model for input data node statement
     */
    private static String invokeCreateModelString(YangNode node) {
        StringBuilder builder = new StringBuilder();
        YangNode inputNode = findRpcInput(node);
        if (inputNode != null) {
            // build resource data
            builder.append(buildResourceData())
                    .append(EIGHT_SPACE_INDENTATION).append(MODEL_OBJECT_DATA).append(SPACE)
                    .append(VAR_INPUT_MO).append(SPACE).append(EQUAL).append(SPACE)
                    .append(VAR_MODEL_CONVERTER).append(PERIOD)
                    .append(CREATE_MODEL).append(OPEN_PARENTHESIS)
                    .append(VAR_INPUT_DATA).append(CLOSE_PARENTHESIS)
                    .append(SEMI_COLON).append(NEW_LINE);
        }
        return builder.toString();
    }

    /**
     * Returns creating data node for output object.
     *
     * @param node YANG RPC node
     * @return creating data node for output object
     */
    private static String invokeCreateDataNodeString(YangNode node) {
        StringBuilder builder = new StringBuilder();
        YangNode outputNode = findRpcOutput(node);
        if (outputNode != null) {
            // create model object data
            builder.append(buildModelObjectData());
            builder.append(EIGHT_SPACE_INDENTATION).append(RESOURCE_DATA)
                    .append(SPACE).append(VAR_OUTPUT_DATA).append(SPACE)
                    .append(EQUAL).append(SPACE).append(VAR_MODEL_CONVERTER)
                    .append(PERIOD).append(CREATE_DATA_NODE)
                    .append(OPEN_PARENTHESIS).append(VAR_OUTPUT_MO)
                    .append(CLOSE_PARENTHESIS).append(SEMI_COLON)
                    .append(NEW_LINE);
        }
        return builder.toString();
    }

    /**
     * Returns execute method contents.
     *
     * @param node YANG RPC node
     * @return execute method contents
     */
    private static String invokeRpcString(YangNode node) {
        StringBuilder builder = new StringBuilder();
        String rpc = getCamelCase(node.getJavaClassNameOrBuiltInType(), null);
        YangNode inputNode = findRpcInput(node);
        YangNode outputNode = findRpcOutput(node);
        String outputName = null;
        if (outputNode != null) {
            outputName = getCapitalCase(getCamelCase(
                    outputNode.getJavaClassNameOrBuiltInType(), null));
        }

        if (inputNode != null && outputNode != null) {
            builder.append(getInputObject(inputNode));
            builder.append(EIGHT_SPACE_INDENTATION).append(DEFAULT_CAPS)
                    .append(outputName).append(SPACE).append(VAR_OUTPUT_OBJECT)
                    .append(SPACE).append(EQUAL).append(SPACE).append(OPEN_PARENTHESIS)
                    .append(DEFAULT_CAPS).append(outputName).append(CLOSE_PARENTHESIS)
                    .append(SPACE).append(VAR_APP_SERVICE).append(PERIOD).append(rpc)
                    .append(OPEN_PARENTHESIS).append(VAR_INPUT_OBJECT).append(CLOSE_PARENTHESIS)
                    .append(SEMI_COLON).append(NEW_LINE);
        } else if (inputNode != null && outputNode == null) {
            builder.append(getInputObject(inputNode));
            builder.append(EIGHT_SPACE_INDENTATION).append(VAR_APP_SERVICE)
                    .append(PERIOD).append(rpc).append(OPEN_PARENTHESIS)
                    .append(VAR_INPUT_OBJECT).append(CLOSE_PARENTHESIS)
                    .append(SEMI_COLON).append(NEW_LINE);
        } else if (inputNode == null && outputNode != null) {
            builder.append(EIGHT_SPACE_INDENTATION).append(DEFAULT_CAPS)
                    .append(outputName).append(SPACE).append(VAR_OUTPUT_OBJECT)
                    .append(SPACE).append(EQUAL).append(SPACE).append(OPEN_PARENTHESIS)
                    .append(DEFAULT_CAPS).append(outputName).append(CLOSE_PARENTHESIS)
                    .append(SPACE).append(VAR_APP_SERVICE).append(PERIOD).append(rpc)
                    .append(OPEN_PARENTHESIS).append(CLOSE_PARENTHESIS).append(SEMI_COLON)
                    .append(NEW_LINE);
        } else {
            builder.append(EIGHT_SPACE_INDENTATION).append(VAR_APP_SERVICE)
                    .append(PERIOD).append(rpc).append(OPEN_PARENTHESIS)
                    .append(CLOSE_PARENTHESIS).append(SEMI_COLON)
                    .append(NEW_LINE);
        }
        return builder.toString();
    }

    /**
     * Returns creating RPC output statement.
     *
     * @param node YANG RPC node
     * @return creating RPC output statement
     */
    private static String createRpcOutputString(YangNode node) {
        YangNode outputNode = findRpcOutput(node);
        String dataNode = "outputData.dataNodes().get(0)";
        if (outputNode == null) {
            dataNode = NULL;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(EIGHT_SPACE_INDENTATION).append(RPC_OUTPUT).append(SPACE)
                .append(VAR_OUTPUT).append(SPACE).append(EQUAL).append(SPACE)
                .append(NEW).append(SPACE).append(RPC_OUTPUT)
                .append(OPEN_PARENTHESIS).append(RPC_SUCCESS).append(COMMA)
                .append(SPACE).append(dataNode).append(CLOSE_PARENTHESIS)
                .append(SEMI_COLON).append(NEW_LINE);
        return builder.toString();
    }

    /**
     * Returns invoking rpc response statement.
     *
     * @return invoking rpc response statement
     */
    private static String invokeRpcResponseString() {
        StringBuilder builder = new StringBuilder();
        builder.append(EIGHT_SPACE_INDENTATION).append(VAR_CFG_SERVICE)
                .append(PERIOD).append(RPC_RESPONSE).append(OPEN_PARENTHESIS)
                .append(VAR_MSG_ID).append(COMMA).append(SPACE)
                .append(VAR_OUTPUT).append(CLOSE_PARENTHESIS).append(SEMI_COLON)
                .append(NEW_LINE);
        return builder.toString();
    }


    /**
     * Returns create resource data string.
     *
     * @return create resource data string
     */
    private static String buildResourceData() {
        StringBuilder builder = new StringBuilder();
        builder.append(EIGHT_SPACE_INDENTATION).append(RESOURCE_DATA)
                .append(SPACE).append(VAR_INPUT_DATA).append(SPACE).append(EQUAL)
                .append(SPACE).append(DEFAULT_RESOURCE_DATA).append(PERIOD)
                .append(BUILDER_METHOD).append(OPEN_PARENTHESIS)
                .append(CLOSE_PARENTHESIS).append(NEW_LINE)
                .append(SIXTEEN_SPACE_INDENTATION).append(PERIOD)
                .append(VAR_RESOURCE_ID).append(OPEN_PARENTHESIS)
                .append(GET_RESOURCE_ID).append(OPEN_PARENTHESIS)
                .append(CLOSE_PARENTHESIS).append(CLOSE_PARENTHESIS)
                .append(NEW_LINE).append(SIXTEEN_SPACE_INDENTATION).append(PERIOD)
                .append(ADD_DATA_NODE).append(OPEN_PARENTHESIS)
                .append(VAR_RPC_INPUT).append(PERIOD).append(VAR_INPUT)
                .append(OPEN_PARENTHESIS).append(CLOSE_PARENTHESIS)
                .append(CLOSE_PARENTHESIS).append(PERIOD)
                .append(BUILD).append(OPEN_PARENTHESIS).append(CLOSE_PARENTHESIS)
                .append(SEMI_COLON).append(NEW_LINE);
        return builder.toString();
    }


    /**
     * Returns input object string.
     *
     * @param inputNode YANG RPC input node
     * @return input object string
     */
    private static String getInputObject(YangNode inputNode) {
        StringBuilder builder = new StringBuilder();
        if (inputNode != null) {
            String inputName = getCapitalCase(getCamelCase(
                    inputNode.getJavaClassNameOrBuiltInType(), null));
            String modelObject = "inputMo.modelObjects().get(0)";
            return builder.append(EIGHT_SPACE_INDENTATION).append(inputName)
                    .append(SPACE).append(VAR_INPUT_OBJECT)
                    .append(SPACE).append(EQUAL).append(SPACE)
                    .append(OPEN_PARENTHESIS).append(OPEN_PARENTHESIS)
                    .append(inputName).append(CLOSE_PARENTHESIS).append(SPACE)
                    .append(modelObject).append(CLOSE_PARENTHESIS)
                    .append(SEMI_COLON).append(NEW_LINE).toString();
        }
        return null;
    }

    /**
     * Returns model object data creation string.
     *
     * @return model object data string
     */
    private static String buildModelObjectData() {
        StringBuilder builder = new StringBuilder();
        return builder.append(EIGHT_SPACE_INDENTATION).append(MODEL_OBJECT_DATA)
                .append(SPACE).append(VAR_OUTPUT_MO).append(SPACE).append(EQUAL)
                .append(SPACE).append(DEFAULT_MODEL_OBJECT_DATA).append(PERIOD)
                .append(BUILDER_METHOD).append(OPEN_PARENTHESIS)
                .append(CLOSE_PARENTHESIS).append(NEW_LINE)
                .append(SIXTEEN_SPACE_INDENTATION).append(PERIOD)
                .append(ADD_MODEL_OBJECT).append(OPEN_PARENTHESIS)
                .append(VAR_OUTPUT_OBJECT).append(CLOSE_PARENTHESIS)
                .append(PERIOD).append(BUILD).append(OPEN_PARENTHESIS)
                .append(CLOSE_PARENTHESIS).append(SEMI_COLON)
                .append(NEW_LINE).toString();
    }

}
