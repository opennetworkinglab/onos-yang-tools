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

package org.onosproject.yang.compiler.translator.tojava;

import org.onosproject.yang.compiler.datamodel.RpcNotificationContainer;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangInput;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeType;
import org.onosproject.yang.compiler.datamodel.YangOutput;
import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.utils.JavaExtendsListHolder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.RPC_INTERFACE_MASK;
import static org.onosproject.yang.compiler.translator.tojava.JavaAttributeInfo.getAttributeInfoForTheData;
import static org.onosproject.yang.compiler.translator.tojava.JavaQualifiedTypeInfoTranslator.getQualifiedTypeInfoOfCurNode;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGenerator.generateServiceInterfaceFile;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaIdentifierSyntax.createPackage;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getRpcServiceMethod;
import static org.onosproject.yang.compiler.utils.UtilConstants.HYPHEN;
import static org.onosproject.yang.compiler.utils.UtilConstants.INPUT;
import static org.onosproject.yang.compiler.utils.UtilConstants.MODEL_OBJECT_PKG;
import static org.onosproject.yang.compiler.utils.UtilConstants.OUTPUT;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_INPUT_VAR_NAME;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_SERVICE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SERVICE;
import static org.onosproject.yang.compiler.utils.io.impl.FileSystemUtil.closeFile;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.generateJavaDocForRpc;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getAbsolutePackagePath;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;

/**
 * Represents implementation of java service code fragments temporary
 * implementations. Maintains the temp files required specific for service
 * and manager java snippet generation.
 */
public class TempJavaServiceFragmentFiles extends TempJavaFragmentFiles {

    /**
     * File name for rpc method.
     */
    private static final String RPC_INTERFACE_FILE_NAME = "Rpc";

    /**
     * Temporary file handle for rpc interface.
     */
    private final File rpcInterfaceTempFileHandle;

    /**
     * Java file handle for rpc interface file.
     */
    private File serviceJavaFileHandle;

    private static final String RPC_INPUT = "RpcInput";
    private static final String RPC_OUTPUT = "RpcOutput";

    /**
     * Creates an instance of temporary java code fragment.
     *
     * @param javaFileInfo generated file information
     * @throws IOException when fails to create new file handle
     */
    TempJavaServiceFragmentFiles(JavaFileInfoTranslator javaFileInfo)
            throws IOException {
        setJavaExtendsListHolder(new JavaExtendsListHolder());
        setJavaImportData(new JavaImportData());
        setJavaFileInfo(javaFileInfo);
        setAbsoluteDirPath(getAbsolutePackagePath(
                getJavaFileInfo().getBaseCodeGenPath(),
                getJavaFileInfo().getPackageFilePath()));
        addGeneratedTempFile(RPC_INTERFACE_MASK);
        rpcInterfaceTempFileHandle = getTemporaryFileHandle(RPC_INTERFACE_FILE_NAME);
    }

    /**
     * Returns rpc method's temporary file handle.
     *
     * @return temporary file handle
     */
    public File getRpcInterfaceTempFileHandle() {
        return rpcInterfaceTempFileHandle;
    }

    /**
     * Constructs java code exit.
     *
     * @param fileType generated file type
     * @param curNode  current YANG node
     * @throws IOException when fails to generate java files
     */
    @Override
    public void generateJavaFile(int fileType, YangNode curNode)
            throws IOException {

        JavaQualifiedTypeInfoTranslator typeInfo =
                new JavaQualifiedTypeInfoTranslator();
        typeInfo.setClassInfo(RPC_SERVICE);
        typeInfo.setPkgInfo(MODEL_OBJECT_PKG);
        typeInfo.setForInterface(true);
        ((JavaCodeGeneratorInfo) curNode)
                .getTempJavaCodeFragmentFiles().getServiceTempFiles()
                .getJavaImportData().addImportInfo(typeInfo,
                                                   getJavaClassName(SERVICE),
                                                   getJavaFileInfo().getPackage());
        boolean rpcInput = false;
        boolean rpcOutput = false;

        YangNode subNode = curNode.getChild();
        while (subNode != null) {
            if (subNode.getNodeType() == YangNodeType.RPC_NODE) {
                YangNode childNode1 = subNode.getChild();
                while (childNode1 != null) {
                    if (childNode1.getNodeType() == YangNodeType.INPUT_NODE) {
                        rpcInput = true;
                    } else {
                        rpcOutput = true;
                    }
                    childNode1 = childNode1.getNextSibling();
                }
            }
            subNode = subNode.getNextSibling();
            if (rpcInput & rpcOutput) {
                break;
            }
        }

        JavaQualifiedTypeInfoTranslator typeInfo1 =
                new JavaQualifiedTypeInfoTranslator();
        typeInfo1.setClassInfo(RPC_INPUT);
        typeInfo1.setPkgInfo(MODEL_OBJECT_PKG);
        typeInfo1.setForInterface(true);
        ((JavaCodeGeneratorInfo) curNode)
                .getTempJavaCodeFragmentFiles().getServiceTempFiles()
                .getJavaImportData().addImportInfo(typeInfo1,
                                                   getJavaClassName(SERVICE),
                                                   getJavaFileInfo().getPackage());

        JavaQualifiedTypeInfoTranslator typeInfo2 =
                new JavaQualifiedTypeInfoTranslator();
        typeInfo2.setClassInfo(RPC_OUTPUT);
        typeInfo2.setPkgInfo(MODEL_OBJECT_PKG);
        typeInfo2.setForInterface(true);
        ((JavaCodeGeneratorInfo) curNode)
                .getTempJavaCodeFragmentFiles().getServiceTempFiles()
                .getJavaImportData().addImportInfo(typeInfo2,
                                                   getJavaClassName(SERVICE),
                                                   getJavaFileInfo().getPackage());

        List<String> imports = ((JavaCodeGeneratorInfo) curNode)
                .getTempJavaCodeFragmentFiles().getServiceTempFiles()
                .getJavaImportData().getImports(true);

        createPackage(curNode);
        /*boolean notification = false;
        if (curNode instanceof YangJavaModuleTranslator) {
            if (!((YangJavaModuleTranslator) curNode).getNotificationNodes()
                    .isEmpty()) {
                notification = true;
            }
        } else if (curNode instanceof YangJavaSubModuleTranslator) {
            if (!((YangJavaSubModuleTranslator) curNode).getNotificationNodes()
                    .isEmpty()) {
                notification = true;
            }
        }

        if (notification) {
            addListenersImport(curNode, imports, ADD);
        } */

        serviceJavaFileHandle = getJavaFileHandle(getJavaClassName(SERVICE));
        generateServiceInterfaceFile(serviceJavaFileHandle, curNode, imports);

        // Close all the file handles.
        freeTemporaryResources(false);
    }

    /**
     * Adds rpc string information to applicable temp file.
     *
     * @param inAttr  RPCs input node attribute info
     * @param outAttr RPCs output node attribute info
     * @param rpcName name of the rpc function
     * @throws IOException IO operation fail
     */
    private void addRpcString(JavaAttributeInfo inAttr, JavaAttributeInfo outAttr,
                              String rpcName) throws IOException {
        String rpcInput = RPC_INPUT;
        String rpcOutput = RPC_OUTPUT;
        String rpcIn = RPC_INPUT_VAR_NAME;
        appendToFile(rpcInterfaceTempFileHandle,
                     generateJavaDocForRpc(rpcName, rpcIn, rpcOutput) +
                             getRpcServiceMethod(rpcName, rpcInput,
                                                 rpcOutput));
    }

    /**
     * Adds the JAVA rpc snippet information.
     *
     * @param inAttr  RPCs input node attribute info
     * @param outAttr RPCs output node attribute info
     * @param rpcName name of the rpc function
     * @throws IOException IO operation fail
     */
    public void addJavaSnippetInfoToApplicableTempFiles(JavaAttributeInfo inAttr,
                                                        JavaAttributeInfo outAttr,
                                                        String rpcName)
            throws IOException {
        addRpcString(inAttr, outAttr, rpcName);
    }

    /**
     * Creates an attribute info object corresponding to a data model node and
     * return it.
     *
     * @param childNode  child data model node(input / output) for which the java code generation
     *                   is being handled
     * @param parentNode parent node (module / sub-module) in which the child node is an attribute
     * @param rpcName    rpc name
     * @return AttributeInfo attribute details required to add in temporary
     * files
     */
    public JavaAttributeInfo getChildNodeAsAttributeInParentService(
            YangNode childNode, YangNode parentNode, String rpcName) {

        JavaFileInfoTranslator fileInfo = ((JavaFileInfoContainer) childNode)
                .getJavaFileInfo();
        String childNodeName = fileInfo.getJavaName();
        if (childNodeName == null) {
            if (childNode instanceof YangInput) {
                childNodeName = rpcName + HYPHEN + INPUT;
            } else {
                childNodeName = rpcName + HYPHEN + OUTPUT;
            }
            childNodeName = getCamelCase(childNodeName, null);
        }
        /*
         * Get the import info corresponding to the attribute for import in
         * generated java files or qualified access
         */
        JavaQualifiedTypeInfoTranslator qualifiedTypeInfo =
                getQualifiedTypeInfoOfCurNode(childNode,
                                              getCapitalCase(childNodeName));
        if (!(parentNode instanceof TempJavaCodeFragmentFilesContainer)) {
            throw new TranslatorException("Parent node does not have file info");
        }

        boolean isQualified = addImportToService(qualifiedTypeInfo, parentNode);
        return getAttributeInfoForTheData(qualifiedTypeInfo, childNodeName,
                                          null, isQualified, false);
    }

    /**
     * Adds to service class import list.
     *
     * @param importInfo import info
     * @return true or false
     */
    private boolean addImportToService(
            JavaQualifiedTypeInfoTranslator importInfo, YangNode curNode) {
        JavaFileInfoTranslator fileInfo = ((JavaFileInfoContainer) curNode)
                .getJavaFileInfo();
        String name = fileInfo.getJavaName();
        String clsInfo = importInfo.getClassInfo();

        StringBuilder className = new StringBuilder()
                .append(getCapitalCase(name))
                .append(SERVICE);
        return clsInfo.contentEquals(SERVICE) || clsInfo.contentEquals(className) ||
                getJavaImportData().addImportInfo(importInfo, className.toString(),
                                                  fileInfo.getPackage());
    }

    /**
     * Adds augmented rpc methods to service temp file.
     *
     * @param module root node
     * @throws IOException when fails to do IO operations
     */
    public void addAugmentedRpcMethod(RpcNotificationContainer module)
            throws IOException {
        JavaAttributeInfo in = null;
        JavaAttributeInfo out = null;
        YangNode rpcChild;
        YangRpc rpc;
        String rpcName;
        YangInput input;

        for (YangAugment info : module.getAugmentList()) {
            input = (YangInput) info.getAugmentedNode();

            if (input != null) {
                rpc = (YangRpc) input.getParent();
                if (!validateForIntraFile(module, (RpcNotificationContainer) rpc
                        .getParent())) {
                    rpcChild = rpc.getChild();

                    rpcName = getCamelCase(rpc.getName(), null);
                    while (rpcChild != null) {
                        if (rpcChild instanceof YangInput) {
                            in = getChildNodeAsAttributeInParentService(
                                    rpcChild, (YangNode) module, rpcName);
                        }
                        if (rpcChild instanceof YangOutput) {
                            out = getChildNodeAsAttributeInParentService(
                                    rpcChild, (YangNode) module, rpcName);
                        }
                        rpcChild = rpcChild.getNextSibling();
                    }
                    addJavaSnippetInfoToApplicableTempFiles(in, out, rpcName);
                }
            }
        }
    }

    private boolean validateForIntraFile(RpcNotificationContainer parent,
                                         RpcNotificationContainer curModule) {
        return parent.getPrefix().equals(curModule.getPrefix());
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
        closeFile(serviceJavaFileHandle, errorOccurred);
        closeFile(rpcInterfaceTempFileHandle);
        closeFile(getGetterInterfaceTempFileHandle());
        closeFile(getSetterInterfaceTempFileHandle());
        closeFile(getSetterImplTempFileHandle());
        super.freeTemporaryResources(errorOccurred);
    }
}
