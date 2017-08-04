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
import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.translator.tojava.utils.JavaExtendsListHolder;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.onosproject.yang.compiler.translator.tojava.utils.IndentationType.FOUR_SPACE;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGenerator.generateRegisterRpc;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGenerator.generateRpcExtendedCommand;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGenerator.generateRpcHandler;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodClassTypes.CLASS_TYPE;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getParaMeterisiedConstructor;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.createNewInstance;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getDefaultDefinitionWithImpl;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getForLoopString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getOverRideString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getVariableDeclaration;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.methodClose;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.multiAttrMethodSignature;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.valueAssign;
import static org.onosproject.yang.compiler.utils.UtilConstants.ABSTRACT;
import static org.onosproject.yang.compiler.utils.UtilConstants.ADD_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLASS;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMA;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMAND;
import static org.onosproject.yang.compiler.utils.UtilConstants.CREATE_RPC_CMD_JAVADOC;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_RPC_HANDLER;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.EQUAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXECUTORS;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXECUTOR_SERVICE;
import static org.onosproject.yang.compiler.utils.UtilConstants.FOUR_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT;
import static org.onosproject.yang.compiler.utils.UtilConstants.INTEGER_WRAPPER;
import static org.onosproject.yang.compiler.utils.UtilConstants.MODEL_CONVERTER;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.OVERRIDE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.PRIVATE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PUBLIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGISTER_RPC;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGISTER_RPC_JAVADOC;
import static org.onosproject.yang.compiler.utils.UtilConstants.RESOURCE_ID;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_COMMAND;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_EXECUTER;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_EXECUTER_JAVADOC;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_EXTENDED_COMMAND;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_HANDLER;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_INPUT;
import static org.onosproject.yang.compiler.utils.UtilConstants.RUN;
import static org.onosproject.yang.compiler.utils.UtilConstants.RUNNABLE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SEMI_COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.SERVICE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SUPER;
import static org.onosproject.yang.compiler.utils.UtilConstants.THIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.TWELVE_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.VOID;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_RPC_SERVICE;
import static org.onosproject.yang.compiler.utils.io.impl.FileSystemUtil.closeFile;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavaDocForExtendedExecuteMethod;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavaDocForRpcExecuterConstructor;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavaDocForRpcExtendedCommandConstructor;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavaDocWithoutParam;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavadocForRegisterRpcConstructor;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getAbsolutePackagePath;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getSmallCase;

/**
 * Represents implementation of java service code fragments temporary
 * implementations. Maintains the temp files required specific for RPC
 * java snippet generation.
 */
public class TempJavaRpcFragmentFiles extends TempJavaFragmentFiles {

    /**
     * Java file handle for RPC handler file.
     */
    private File rpcHandlerFileHandler;

    /**
     * Java file handle for RPC extended command file.
     */
    private File rpcExtendedCommandFileHandler;

    /**
     * Java file handle for RPC register file.
     */
    private File rpcRegisterFileHandler;

    private static final String VAR_CMD_ID = "cmdId";
    private static final String VAR_RPC_INPUT = "rpcInput";
    private static final String VAR_EXECUTOR = "executor";
    private static final String VAR_RPC_HANDLER = "rpcHandler";
    private static final String VAR_CMD = "cmd";
    private static final String VAR_INPUT = "input";
    private static final String VAR_MSG_ID = "msgId";
    private static final String VAR_RPC_COMMANDS = "rpcCommands";
    private static final String VAR_RPC_COMMAND = "rpcCommand";
    private static final String VAR_CFG_SERVICE = "cfgService";
    private static final String VAR_MODEL_CONVERTER = "modelConverter";
    private static final String VAR_APP_SERVICE = "appService";
    private static final String EXECUTE = "execute";
    private static final String EXECUTE_RPC = "executeRpc";
    private static final String REGISTER_RPC_METHOD = "registerRpc";
    private static final String REGISTER_HANDLER = "registerHandler";
    private static final String CREATE_RPC_CMDS = "createRpcCommands";

    /**
     * Creates an instance of temporary java code fragment.
     *
     * @param javaFileInfo generated file information
     * @throws IOException when fails to create new file handle
     */
    TempJavaRpcFragmentFiles(JavaFileInfoTranslator javaFileInfo)
            throws IOException {
        setJavaExtendsListHolder(new JavaExtendsListHolder());
        setJavaImportData(new JavaImportData());
        setJavaFileInfo(javaFileInfo);
        setAbsoluteDirPath(getAbsolutePackagePath(
                getJavaFileInfo().getBaseCodeGenPath(),
                getJavaFileInfo().getPackageFilePath()));
    }

    /**
     * Constructs java code.
     *
     * @param fileType generated file type
     * @param curNode  current YANG node
     * @throws IOException when fails to generate java files
     */
    public void generateJavaFile(int fileType, YangNode curNode)
            throws IOException {

        generateRpcExtendedCommandFile(curNode);

        generateRpcHandlerFile(curNode);

        generateRpcRegisterFile(curNode);

        // close all the file handles
        freeTemporaryResources(false);
    }

    /**
     * Constructs RPC handler code.
     *
     * @param curNode current YANG node
     * @throws IOException when fails to generate java files
     */
    private void generateRpcHandlerFile(YangNode curNode)
            throws IOException {
        JavaImportData importData = ((JavaCodeGeneratorInfo)
                curNode).getTempJavaCodeFragmentFiles().getRpcFragmentFiles()
                .getJavaImportData();

        List<String> imports = importData.getImports(false);
        imports.add(importData.getExecutorServiceImport());
        imports.add(importData.getExecutorsImport());
        imports.add(importData.getImportForRpcHandler());
        imports.add(importData.getImportForRpcCommand());
        imports.add(importData.getImportForRpcInput());

        rpcHandlerFileHandler = getJavaFileHandle(DEFAULT_RPC_HANDLER);
        generateRpcHandler(rpcHandlerFileHandler, curNode, imports);
    }

    /**
     * Constructs RPC extended command code.
     *
     * @param curNode current YANG node
     * @throws IOException when fails to generate java files
     */
    private void generateRpcExtendedCommandFile(YangNode curNode)
            throws IOException {
        JavaImportData importData = ((JavaCodeGeneratorInfo)
                curNode).getTempJavaCodeFragmentFiles().getRpcFragmentFiles()
                .getJavaImportData();

        List<String> imports = importData.getImports(false);
        imports.add(importData.getImportForResourceId());
        imports.add(importData.getImportForRpcInput());
        imports.add(importData.getImportForRpcCommand());

        rpcExtendedCommandFileHandler = getJavaFileHandle(RPC_EXTENDED_COMMAND);
        generateRpcExtendedCommand(rpcExtendedCommandFileHandler, curNode,
                                   imports);
    }

    /**
     * Constructs register RPC code.
     *
     * @param curNode current YANG node
     * @throws IOException when fails to generate java files
     */
    private void generateRpcRegisterFile(YangNode curNode)
            throws IOException {
        JavaImportData importData = ((JavaCodeGeneratorInfo)
                curNode).getTempJavaCodeFragmentFiles().getRpcFragmentFiles()
                .getJavaImportData();

        // add imports of all RPC commands
        YangNode child = curNode.getChild();
        while (child != null) {
            if (child instanceof YangRpc) {
                JavaQualifiedTypeInfoTranslator cmdImport = new
                        JavaQualifiedTypeInfoTranslator();
                cmdImport.setForInterface(false);
                cmdImport.setPkgInfo(child.getJavaPackage());
                cmdImport.setClassInfo(getCapitalCase(getCamelCase(
                        child.getJavaClassNameOrBuiltInType(), null)) +
                                               COMMAND);
                importData.addImportInfo(cmdImport, REGISTER_RPC,
                                         curNode.getJavaPackage());
            }
            child = child.getNextSibling();
        }

        List<String> imports = importData.getImports(false);
        imports.add(importData.getImportForLinkedList());
        imports.add(importData.getImportForList());
        imports.add(importData.getImportForRpcCommand());
        imports.add(importData.getImportForRpcHandler());
        imports.add(importData.getImportForDynamicStoreService());
        imports.add(importData.getImportForModelConverter());

        rpcRegisterFileHandler = getJavaFileHandle(REGISTER_RPC);
        generateRegisterRpc(rpcRegisterFileHandler, curNode, imports);
    }

    /**
     * Removes all temporary file handles.
     *
     * @param errorOccurred flag indicating error
     * @throws IOException when failed to delete the temporary files
     */
    @Override
    public void freeTemporaryResources(boolean errorOccurred)
            throws IOException {
        closeFile(rpcHandlerFileHandler, errorOccurred);
        closeFile(rpcExtendedCommandFileHandler, errorOccurred);
        closeFile(rpcRegisterFileHandler, errorOccurred);
        super.freeTemporaryResources(errorOccurred);
    }

    /**
     * Returns RPC handler class contents.
     *
     * @return RPC handler class contents
     */
    public static String getRpcHandlerContents() {
        StringBuilder builder = new StringBuilder(NEW_LINE);

        // add executor attribute
        builder.append(FOUR_SPACE_INDENTATION).append(PRIVATE).append(SPACE)
                .append(EXECUTOR_SERVICE).append(SPACE).append(VAR_EXECUTOR)
                .append(SEMI_COLON).append(NEW_LINE)

                // add execute RPC method
                .append(getExecuteRpcMethod())

                // add RPC executer class
                .append(getRpcExecuterClass());
        return builder.toString();
    }

    /**
     * Returns RPC extended command class contents.
     *
     * @return RPC extended command class contents
     */
    public static String getRpcExtendedCommandContents() {
        StringBuilder builder = new StringBuilder(NEW_LINE)

                // add constructor with resource id parameter
                .append(getJavaDocForRpcExtendedCommandConstructor())
                .append(NEW_LINE).append(FOUR_SPACE_INDENTATION).append(PUBLIC)
                .append(SPACE).append(RPC_EXTENDED_COMMAND)
                .append(OPEN_PARENTHESIS).append(RESOURCE_ID).append(SPACE)
                .append(VAR_CMD_ID).append(CLOSE_PARENTHESIS).append(SPACE)
                .append(OPEN_CURLY_BRACKET).append(NEW_LINE)
                .append(EIGHT_SPACE_INDENTATION).append(SUPER)
                .append(OPEN_PARENTHESIS).append(VAR_CMD_ID)
                .append(CLOSE_PARENTHESIS).append(SEMI_COLON).append(NEW_LINE)
                .append(FOUR_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(NEW_LINE)

                //add execute method
                .append(NEW_LINE).append(getJavaDocForExtendedExecuteMethod())
                .append(NEW_LINE).append(FOUR_SPACE_INDENTATION).append(PUBLIC)
                .append(SPACE).append(ABSTRACT).append(SPACE).append(VOID)
                .append(SPACE).append(EXECUTE).append(OPEN_PARENTHESIS)
                .append(RPC_INPUT).append(SPACE).append(VAR_RPC_INPUT)
                .append(COMMA).append(SPACE).append(INT).append(SPACE)
                .append(VAR_MSG_ID).append(CLOSE_PARENTHESIS).append(SEMI_COLON)
                .append(NEW_LINE);

        return builder.toString();
    }

    /**
     * Returns register RPC class contents.
     *
     * @param node YANG module node
     * @return register RPC class contents
     */
    public static String getRegisterRpcContents(YangNode node) {
        StringBuilder builder = new StringBuilder(NEW_LINE);

        String appService = getCamelCase(node.getJavaClassNameOrBuiltInType(),
                                         null) + SERVICE;

        Map<String, String> param = new LinkedHashMap<>();
        param.put(VAR_CFG_SERVICE, YANG_RPC_SERVICE);
        param.put(VAR_MODEL_CONVERTER, MODEL_CONVERTER);
        param.put(VAR_APP_SERVICE, getCapitalCase(appService));

        // add attributes
        builder.append(getVariableDeclaration(VAR_RPC_COMMANDS, "List<RpcCommand>",
                                              FOUR_SPACE_INDENTATION,
                                              PRIVATE, null))
                .append(getVariableDeclaration(VAR_RPC_HANDLER, RPC_HANDLER,
                                               FOUR_SPACE_INDENTATION,
                                               PRIVATE, null))
                .append(getVariableDeclaration(VAR_CFG_SERVICE,
                                               YANG_RPC_SERVICE,
                                               FOUR_SPACE_INDENTATION,
                                               PRIVATE, null))
                .append(getVariableDeclaration(VAR_MODEL_CONVERTER,
                                               MODEL_CONVERTER,
                                               FOUR_SPACE_INDENTATION,
                                               PRIVATE, null))
                .append(getVariableDeclaration(VAR_APP_SERVICE, getCapitalCase(appService),
                                               FOUR_SPACE_INDENTATION,
                                               PRIVATE, null))
                .append(NEW_LINE)

                // add constructor
                .append(getJavadocForRegisterRpcConstructor()).append(NEW_LINE)

                .append(multiAttrMethodSignature(REGISTER_RPC, null, PUBLIC,
                                                 null, param, CLASS_TYPE,
                                                 FOUR_SPACE_INDENTATION))
                .append(valueAssign(THIS + PERIOD + VAR_RPC_COMMANDS, "new " +
                        "LinkedList<RpcCommand>()", EIGHT_SPACE_INDENTATION))
                .append(valueAssign("this." + VAR_RPC_HANDLER,
                                    "new DefaultRpcHandler()",
                                    EIGHT_SPACE_INDENTATION))
                .append(valueAssign(THIS + PERIOD + VAR_CFG_SERVICE,
                                    VAR_CFG_SERVICE, EIGHT_SPACE_INDENTATION))
                .append(valueAssign(THIS + PERIOD + VAR_MODEL_CONVERTER,
                                    VAR_MODEL_CONVERTER,
                                    EIGHT_SPACE_INDENTATION))
                .append(valueAssign(THIS + PERIOD + VAR_APP_SERVICE, VAR_APP_SERVICE,
                                    EIGHT_SPACE_INDENTATION))

                .append(FOUR_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(NEW_LINE)

                // add register rpc method
                .append(getRegisterRpcMethod())

                // add create rpc method
                .append(getCreateRpcCommandMethod(node));

        return builder.toString();
    }

    /**
     * Returns execute RPC method.
     *
     * @return execute RPC method
     */
    private static String getExecuteRpcMethod() {
        // execute method parameters
        Map<String, String> param = new LinkedHashMap<>();
        param.put(VAR_MSG_ID, INTEGER_WRAPPER);
        param.put(VAR_CMD, RPC_COMMAND);
        param.put(VAR_INPUT, RPC_INPUT);

        // constructor parameters for RPC executer
        List<String> parameters = new LinkedList<>();
        parameters.add(VAR_MSG_ID);
        parameters.add("(RpcExtendedCommand) cmd");
        parameters.add(VAR_INPUT);

        StringBuilder builder = new StringBuilder(getOverRideString());
        // method signature
        builder.append(multiAttrMethodSignature(EXECUTE_RPC, null, PUBLIC,
                                                VOID, param, CLASS_TYPE,
                                                FOUR_SPACE_INDENTATION))

                // method body
                .append(valueAssign(VAR_EXECUTOR, EXECUTORS +
                        ".newSingleThreadExecutor()", EIGHT_SPACE_INDENTATION))

                .append(EIGHT_SPACE_INDENTATION).append(VAR_EXECUTOR).append(PERIOD)
                .append(EXECUTE).append(OPEN_PARENTHESIS)
                .append(createNewInstance(RPC_EXECUTER, EMPTY_STRING,
                                          parameters))
                .append(CLOSE_PARENTHESIS).append(SEMI_COLON).append(NEW_LINE)
                .append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    /**
     * Returns contents of RPC executer class.
     *
     * @return contents of RPC executer class
     */
    public static String getRpcExecuterClass() {
        Map<String, String> param = new LinkedHashMap<>();
        param.put(VAR_MSG_ID, INTEGER_WRAPPER);
        param.put(VAR_CMD, RPC_EXTENDED_COMMAND);
        param.put(VAR_INPUT, RPC_INPUT);

        StringBuilder builder = new StringBuilder();
        builder.append(getJavaDocWithoutParam(RPC_EXECUTER_JAVADOC,
                                              FOUR_SPACE_INDENTATION))
                .append(FOUR_SPACE_INDENTATION).append(
                getDefaultDefinitionWithImpl(CLASS, RPC_EXECUTER, PUBLIC,
                                             RUNNABLE))

                // add attributes
                .append(getVariableDeclaration(VAR_MSG_ID, INTEGER_WRAPPER,
                                               EIGHT_SPACE_INDENTATION, null,
                                               null))
                .append(getVariableDeclaration(VAR_CMD, RPC_EXTENDED_COMMAND,
                                               EIGHT_SPACE_INDENTATION, null,
                                               null))
                .append(getVariableDeclaration(VAR_INPUT, RPC_INPUT,
                                               EIGHT_SPACE_INDENTATION, null,
                                               null))

                // add constructor
                .append(getJavaDocForRpcExecuterConstructor()).append(NEW_LINE)
                .append(getParaMeterisiedConstructor(RPC_EXECUTER, PUBLIC,
                                                     param,
                                                     EIGHT_SPACE_INDENTATION))
                .append(NEW_LINE)

                // add run method
                .append(getRunMethod())
                .append(methodClose(FOUR_SPACE));
        return builder.toString();
    }

    /**
     * Returns run method.
     *
     * @return run method
     */
    private static String getRunMethod() {
        StringBuilder builder = new StringBuilder();
        // method signature
        builder.append(EIGHT_SPACE_INDENTATION).append(OVERRIDE)
                .append(NEW_LINE).append(EIGHT_SPACE_INDENTATION)
                .append(PUBLIC).append(SPACE).append(VOID).append(SPACE)
                .append(RUN).append(OPEN_PARENTHESIS).append(CLOSE_PARENTHESIS)
                .append(SPACE).append(OPEN_CURLY_BRACKET).append(NEW_LINE)

                // method body
                .append(TWELVE_SPACE_INDENTATION).append(VAR_CMD).append(PERIOD)
                .append(EXECUTE).append(OPEN_PARENTHESIS).append(VAR_INPUT)
                .append(COMMA).append(SPACE).append(VAR_MSG_ID)
                .append(CLOSE_PARENTHESIS).append(SEMI_COLON).append(NEW_LINE)
                .append(EIGHT_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(NEW_LINE);
        return builder.toString();
    }

    /**
     * Returns register RPC method.
     *
     * @return register RPC method
     */
    private static String getRegisterRpcMethod() {
        StringBuilder builder = new StringBuilder();
        builder.append(getJavaDocWithoutParam(REGISTER_RPC_JAVADOC,
                                              FOUR_SPACE_INDENTATION))
                .append(FOUR_SPACE_INDENTATION).append(PUBLIC).append(SPACE)
                .append(VOID).append(SPACE).append(REGISTER_RPC_METHOD)
                .append(OPEN_PARENTHESIS).append(CLOSE_PARENTHESIS)
                .append(SPACE).append(OPEN_CURLY_BRACKET).append(NEW_LINE)

                .append(EIGHT_SPACE_INDENTATION).append(CREATE_RPC_CMDS)
                .append(OPEN_PARENTHESIS).append(CLOSE_PARENTHESIS)
                .append(SEMI_COLON).append(NEW_LINE)

                .append(getForLoopString(EIGHT_SPACE_INDENTATION, RPC_COMMAND,
                                         VAR_RPC_COMMAND, VAR_RPC_COMMANDS))
                .append(TWELVE_SPACE_INDENTATION).append(VAR_CFG_SERVICE)
                .append(PERIOD).append(REGISTER_HANDLER).append(OPEN_PARENTHESIS)
                .append(VAR_RPC_HANDLER).append(COMMA).append(SPACE)
                .append(VAR_RPC_COMMAND).append(CLOSE_PARENTHESIS)
                .append(SEMI_COLON).append(NEW_LINE)

                .append(EIGHT_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(NEW_LINE)

                .append(FOUR_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(NEW_LINE);
        return builder.toString();
    }

    /**
     * Returns create RPC command method.
     *
     * @return create RPC command method
     */
    private static String getCreateRpcCommandMethod(YangNode node) {
        StringBuilder builder = new StringBuilder();
        builder.append(getJavaDocWithoutParam(CREATE_RPC_CMD_JAVADOC,
                                              FOUR_SPACE_INDENTATION))
                .append(FOUR_SPACE_INDENTATION).append(PUBLIC).append(SPACE)
                .append(VOID).append(SPACE).append(CREATE_RPC_CMDS)
                .append(OPEN_PARENTHESIS).append(CLOSE_PARENTHESIS)
                .append(SPACE).append(OPEN_CURLY_BRACKET).append(NEW_LINE)
                .append(getCreateRpcCmdContents(node))
                .append(FOUR_SPACE_INDENTATION).append(CLOSE_CURLY_BRACKET)
                .append(NEW_LINE);
        return builder.toString();
    }

    /**
     * Returns create RPC command contents.
     *
     * @return create RPC command contents
     */
    private static String getCreateRpcCmdContents(YangNode node) {
        StringBuilder builder = new StringBuilder();
        YangNode child = node.getChild();
        while (child != null) {
            if (child instanceof YangRpc) {
                builder.append(getCreateRpcCommand(child));
            }
            child = child.getNextSibling();
        }
        return builder.toString();
    }

    /**
     * Returns create RPC command method.
     *
     * @param node YANG RPC node
     * @return create RPC command method
     */
    public static String getCreateRpcCommand(YangNode node) {
        StringBuilder builder = new StringBuilder();
        String cmdName = getSmallCase(node.getJavaClassNameOrBuiltInType());

        // creates RPC command
        builder.append(EIGHT_SPACE_INDENTATION).append(RPC_COMMAND).append(SPACE)
                .append(cmdName).append(SPACE).append(EQUAL).append(SPACE)
                .append(NEW).append(SPACE).append(getCapitalCase(
                node.getJavaClassNameOrBuiltInType())).append(COMMAND)
                .append(OPEN_PARENTHESIS).append(VAR_CFG_SERVICE).append(COMMA).append(SPACE)
                .append(VAR_MODEL_CONVERTER).append(COMMA)
                .append(SPACE).append(VAR_APP_SERVICE).append(CLOSE_PARENTHESIS)
                .append(SEMI_COLON).append(NEW_LINE)

                // adds RPC command to list
                .append(EIGHT_SPACE_INDENTATION).append(VAR_RPC_COMMANDS)
                .append(PERIOD).append(ADD_STRING).append(OPEN_PARENTHESIS)
                .append(cmdName).append(CLOSE_PARENTHESIS).append(SEMI_COLON)
                .append(NEW_LINE);
        return builder.toString();
    }
}
