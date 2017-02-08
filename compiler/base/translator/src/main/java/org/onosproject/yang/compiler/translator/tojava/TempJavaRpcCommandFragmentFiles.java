/*
 * Copyright 2017-present Open Networking Laboratory
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
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodClassTypes.CLASS_TYPE;
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
import static org.onosproject.yang.compiler.utils.UtilConstants.DATA_NODE;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_CAPS;
import static org.onosproject.yang.compiler.utils.UtilConstants.DYNAMIC_CONFIG_SERVICE;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.EQUAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.FOUR_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT;
import static org.onosproject.yang.compiler.utils.UtilConstants.MODEL_CONVERTER;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.NULL;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.PRIVATE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PUBLIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUOTES;
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
import static org.onosproject.yang.compiler.utils.UtilConstants.SUPER;
import static org.onosproject.yang.compiler.utils.UtilConstants.THIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.VOID;
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
    private static final String VAR_STORE = "store";
    private static final String VAR_STORE_SERVICE = "storeService";
    private static final String VAR_MODEL_CONVERTER = "modelConverter";
    private static final String EXECUTE = "execute";
    private static final String GET_RESOURCE_ID = "getResourceId";
    private static final String VAR_INPUT_OBJECT = "inputObject";
    private static final String VAR_OUTPUT_OBJECT = "outputObject";
    private static final String RPC_RESPONSE = "rpcResponse";
    private static final String VAR_DATA_NODE = "dataNode";

    @Override
    public void generateJavaFile(int fileType, YangNode curNode) {
        JavaImportData importData = ((JavaCodeGeneratorInfo) curNode)
                .getTempJavaCodeFragmentFiles().getRpcCommandTempFiles()
                .getJavaImportData();
        String parentPkg = curNode.getParent().getJavaPackage();
        String parentClassName = getCapitalCase(getCamelCase(
                curNode.getParent().getJavaClassNameOrBuiltInType(), null));
        String className = getCapitalCase(getCamelCase(
                curNode.getJavaClassNameOrBuiltInType(), null) + COMMAND);

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
            importData.addImportInfo(outputImport, className,
                                     curNode.getJavaPackage());

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
        imports.add(importData.getImportForDataNode());
        imports.add(importData.getImportForRpcSuccess());

        try {
            rpcCommandClassTempFileHandle = getJavaFileHandle(className);
            generateRpcCommand(rpcCommandClassTempFileHandle, curNode, imports);
        } catch (IOException e) {
            throw new TranslatorException(
                    "Failed to generate code for RPC command" + curNode.getName());
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
     * @return RPC commands contents
     */
    public static String getRpcCommandContents(YangNode curNode) {
        String appService = curNode.getParent()
                .getJavaClassNameOrBuiltInType() + SERVICE;
        String className = getCapitalCase(getCamelCase(
                curNode.getJavaClassNameOrBuiltInType(), null)) + COMMAND;

        // parameters for constructors
        Map<String, String> param = new LinkedHashMap<>();
        param.put(VAR_STORE, DYNAMIC_CONFIG_SERVICE);
        param.put(VAR_MODEL_CONVERTER, MODEL_CONVERTER);
        param.put(appService, getCapitalCase(appService));

        StringBuilder builder = new StringBuilder();
        // add attributes
        builder.append(getVariableDeclaration(VAR_MODEL_CONVERTER, MODEL_CONVERTER,
                                              FOUR_SPACE_INDENTATION, PRIVATE))
                .append(getVariableDeclaration(appService, getCapitalCase(appService),
                                               FOUR_SPACE_INDENTATION, PRIVATE))
                .append(getVariableDeclaration(VAR_STORE_SERVICE, DYNAMIC_CONFIG_SERVICE,
                                               FOUR_SPACE_INDENTATION, PRIVATE))
                .append(NEW_LINE)

                // add constructor
                .append(getJavaDocForRpcCommandConstructor(
                        curNode.getJavaClassNameOrBuiltInType()))
                .append(NEW_LINE)
                .append(multiAttrMethodSignature(className, null, PUBLIC,
                                                 null, param, CLASS_TYPE,
                                                 FOUR_SPACE_INDENTATION))
                .append(EIGHT_SPACE_INDENTATION).append(SUPER)
                .append(OPEN_PARENTHESIS).append(GET_RESOURCE_ID)
                .append(OPEN_PARENTHESIS).append(CLOSE_PARENTHESIS)
                .append(CLOSE_PARENTHESIS).append(SEMI_COLON).append(NEW_LINE)
                .append(valueAssign(THIS + PERIOD + VAR_STORE_SERVICE, VAR_STORE,
                                    EIGHT_SPACE_INDENTATION))
                .append(valueAssign(THIS + PERIOD + VAR_MODEL_CONVERTER,
                                    VAR_MODEL_CONVERTER,
                                    EIGHT_SPACE_INDENTATION))
                .append(valueAssign(THIS + PERIOD + appService, appService,
                                    EIGHT_SPACE_INDENTATION))

                .append(FOUR_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(NEW_LINE)

                //add execute method
                .append(getRpcCommandExecuteMethod(appService, curNode))

                //add get resource id method
                .append(getResourceIdMethod(curNode));

        // add execute method with msgid
        return builder.toString();
    }

    /**
     * Returns RPC command execute method.
     *
     * @return RPC command execute method
     */
    private static String getRpcCommandExecuteMethod(String appService,
                                                     YangNode node) {
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

                .append(getExecuteMethodContents(node, appService))
                .append(FOUR_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(NEW_LINE);

        return builder.toString();
    }

    /**
     * Returns get resource id method.
     *
     * @return get resource id method
     */
    private static String getResourceIdMethod(YangNode node) {
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
                .append(QUOTES).append(SLASH_FOR_STRING).append(QUOTES)
                .append(COMMA).append(SPACE).append(NULL)
                .append(CLOSE_PARENTHESIS).append(NEW_LINE)
                .append(SIXTEEN_SPACE_INDENTATION).append(PERIOD)
                .append(ADD_BRANCH_POINT_SCHEMA).append(OPEN_PARENTHESIS)
                .append(QUOTES).append(node.getName()).append(QUOTES)
                .append(COMMA).append(SPACE).append(QUOTES).append(
                node.getParent().getNameSpace().getModuleNamespace())
                .append(QUOTES).append(CLOSE_PARENTHESIS).append(PERIOD)
                .append(BUILD).append(OPEN_PARENTHESIS).append(CLOSE_PARENTHESIS)
                .append(SEMI_COLON).append(NEW_LINE).append(FOUR_SPACE_INDENTATION)
                .append(CLOSE_CURLY_BRACKET).append(NEW_LINE);

        return builder.toString();
    }

    /**
     * Returns execute method contents.
     *
     * @return execute method contents
     */
    private static String getExecuteMethodContents(YangNode node, String
            appService) {
        StringBuilder builder = new StringBuilder();
        builder.append(invokeCreateModelString(node))
                .append(invokeRpcString(node, appService))
                .append(invokeCreateDataNodeString(node))
                .append(createRpcOutputString(node))
                .append(invokeRpcResponseString());
        return builder.toString();
    }

    /**
     * Returns create model for input data node statement.
     *
     * @return create model for input data node statement
     */
    private static String invokeCreateModelString(YangNode node) {
        StringBuilder builder = new StringBuilder();
        YangNode inputNode = findRpcInput(node);
        if (inputNode != null) {
            String inputName = getCapitalCase(getCamelCase(
                    inputNode.getJavaClassNameOrBuiltInType(), null));
            builder.append(EIGHT_SPACE_INDENTATION).append(inputName).append(SPACE)
                    .append(VAR_INPUT_OBJECT).append(SPACE).append(EQUAL).append(SPACE)
                    .append(VAR_MODEL_CONVERTER).append(PERIOD)
                    .append(CREATE_MODEL).append(OPEN_PARENTHESIS)
                    .append(VAR_RPC_INPUT).append(PERIOD).append(VAR_INPUT)
                    .append(OPEN_PARENTHESIS).append(CLOSE_PARENTHESIS)
                    .append(CLOSE_PARENTHESIS).append(SEMI_COLON).append(NEW_LINE);
        }
        return builder.toString();
    }

    /**
     * Returns creating data node for output object.
     *
     * @return creating data node for output object
     */
    private static String invokeCreateDataNodeString(YangNode node) {
        StringBuilder builder = new StringBuilder();
        YangNode outputNode = findRpcOutput(node);
        if (outputNode != null) {
            String outputName = getCapitalCase(getCamelCase(
                    outputNode.getJavaClassNameOrBuiltInType(), null));
            builder.append(EIGHT_SPACE_INDENTATION).append(DATA_NODE).append(SPACE)
                    .append(VAR_DATA_NODE).append(SPACE).append(EQUAL)
                    .append(SPACE).append(VAR_MODEL_CONVERTER).append(PERIOD)
                    .append(CREATE_DATA_NODE).append(OPEN_PARENTHESIS)
                    .append(OPEN_PARENTHESIS).append(DEFAULT_CAPS)
                    .append(outputName).append(CLOSE_PARENTHESIS).append(SPACE)
                    .append(VAR_OUTPUT_OBJECT).append(CLOSE_PARENTHESIS)
                    .append(SEMI_COLON).append(NEW_LINE);
        }
        return builder.toString();
    }

    /**
     * Returns execute method contents.
     *
     * @param node       YANG RPC node
     * @param appService application service name
     * @return execute method contents
     */
    private static String invokeRpcString(YangNode node, String appService) {
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
            builder.append(EIGHT_SPACE_INDENTATION).append(outputName)
                    .append(SPACE).append(VAR_OUTPUT_OBJECT).append(SPACE)
                    .append(EQUAL).append(SPACE).append(appService)
                    .append(PERIOD).append(rpc).append(OPEN_PARENTHESIS)
                    .append(VAR_INPUT_OBJECT).append(CLOSE_PARENTHESIS)
                    .append(SEMI_COLON).append(NEW_LINE);
        } else if (inputNode != null && outputNode == null) {
            builder.append(EIGHT_SPACE_INDENTATION).append(appService)
                    .append(PERIOD).append(rpc).append(OPEN_PARENTHESIS)
                    .append(VAR_INPUT_OBJECT).append(CLOSE_PARENTHESIS)
                    .append(SEMI_COLON).append(NEW_LINE);
        } else if (inputNode == null && outputNode != null) {
            builder.append(EIGHT_SPACE_INDENTATION).append(outputName)
                    .append(SPACE).append(VAR_OUTPUT_OBJECT).append(SPACE)
                    .append(EQUAL).append(SPACE).append(appService)
                    .append(PERIOD).append(rpc).append(OPEN_PARENTHESIS)
                    .append(CLOSE_PARENTHESIS).append(SEMI_COLON)
                    .append(NEW_LINE);
        } else {
            builder.append(EIGHT_SPACE_INDENTATION).append(appService)
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
        String dataNode = VAR_DATA_NODE;
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
        builder.append(EIGHT_SPACE_INDENTATION).append(VAR_STORE_SERVICE)
                .append(PERIOD).append(RPC_RESPONSE).append(OPEN_PARENTHESIS)
                .append(VAR_MSG_ID).append(COMMA).append(SPACE)
                .append(VAR_OUTPUT).append(CLOSE_PARENTHESIS).append(SEMI_COLON)
                .append(NEW_LINE);
        return builder.toString();
    }

}
