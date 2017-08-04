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

package org.onosproject.yang.compiler.translator.tojava.javamodel;

import org.onosproject.yang.compiler.datamodel.RpcNotificationContainer;
import org.onosproject.yang.compiler.datamodel.YangInput;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangOutput;
import org.onosproject.yang.compiler.datamodel.javadatamodel.YangJavaRpc;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.JavaAttributeInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGenerator;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yang.compiler.translator.tojava.TempJavaCodeFragmentFiles;
import org.onosproject.yang.compiler.translator.tojava.TempJavaCodeFragmentFilesContainer;
import org.onosproject.yang.compiler.translator.tojava.TempJavaServiceFragmentFiles;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.IOException;

import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.getParentNodeInGenCode;
import static org.onosproject.yang.compiler.translator.tojava.YangJavaModelUtils.updatePackageInfo;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorErrorType.FAIL_AT_EXIT;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorErrorType.INVALID_CHILD_NODE;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorErrorType.INVALID_PARENT_NODE;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorUtils.getErrorMsg;

/**
 * Represents rpc information extended to support java code generation.
 */
public class YangJavaRpcTranslator
        extends YangJavaRpc
        implements JavaCodeGenerator, JavaCodeGeneratorInfo {

    private static final long serialVersionUID = 806201622L;

    /**
     * Temporary file for code generation.
     */
    private transient TempJavaCodeFragmentFiles tempJavaCodeFragmentFiles;

    /**
     * Creates an instance of YANG java rpc.
     */
    public YangJavaRpcTranslator() {
        setJavaFileInfo(new JavaFileInfoTranslator());
        //getJavaFileInfo().setGeneratedFileTypes(GENERATE_RPC_COMMAND_CLASS);
    }

    /**
     * Returns the generated java file information.
     *
     * @return generated java file information
     */
    @Override
    public JavaFileInfoTranslator getJavaFileInfo() {

        if (javaFileInfo == null) {
            throw new TranslatorException("missing java info in java datamodel node " +
                                                  getName());
        }
        return (JavaFileInfoTranslator) javaFileInfo;
    }

    /**
     * Sets the java file info object.
     *
     * @param javaInfo java file info object
     */
    @Override
    public void setJavaFileInfo(JavaFileInfoTranslator javaInfo) {
        javaFileInfo = javaInfo;
    }

    @Override
    public TempJavaCodeFragmentFiles getTempJavaCodeFragmentFiles() {
        return tempJavaCodeFragmentFiles;
    }

    @Override
    public void setTempJavaCodeFragmentFiles(TempJavaCodeFragmentFiles fileHandle) {
        tempJavaCodeFragmentFiles = fileHandle;
    }

    /**
     * Prepares the information for java code generation corresponding to YANG
     * RPC info.
     *
     * @param yangPlugin YANG plugin config
     * @throws TranslatorException translator operations fails
     */
    @Override
    public void generateCodeEntry(YangPluginConfig yangPlugin)
            throws TranslatorException {

        // TODO: code generation for each RPC
        /*try {
            generateCodeOfNode(this, yangPlugin);
        } catch (IOException e) {
            throw new TranslatorException(
                    "Failed to prepare generate code entry for RPC node " +
                            getName() + " in " +
                            getLineNumber() + " at " +
                            getCharPosition()
                            + " in " + getFileName() + " " + e.getLocalizedMessage());
        }*/

        // Add package information for RPC and create corresponding folder.
        updatePackageInfo(this, yangPlugin);
    }

    /**
     * Creates a java file using the YANG RPC info.
     *
     * @throws TranslatorException translator operations fails
     */
    @Override
    public void generateCodeExit()
            throws TranslatorException {
        // Get the parent module/sub-module.
        YangNode parent = getParentNodeInGenCode(this);

        // Parent should be holder of rpc or notification.
        if (!(parent instanceof RpcNotificationContainer)) {
            throw new TranslatorException(getErrorMsg(INVALID_PARENT_NODE,
                                                      this));
        }

        /*
         * Create attribute info for input and output of rpc and add it to the
         * parent import list.
         */
        TempJavaServiceFragmentFiles tempJavaFragmentFiles =
                ((TempJavaCodeFragmentFilesContainer) getParent())
                        .getTempJavaCodeFragmentFiles()
                        .getServiceTempFiles();
        JavaAttributeInfo javaAttributeInfoOfInput = null;
        JavaAttributeInfo javaAttributeInfoOfOutput = null;

        // Get the child input and output node and obtain create java attribute
        // info.
        YangNode yangNode = getChild();
        while (yangNode != null) {
            if (yangNode instanceof YangInput) {
                javaAttributeInfoOfInput = tempJavaFragmentFiles
                        .getChildNodeAsAttributeInParentService(yangNode,
                                                                getParent(),
                                                                getJavaClassNameOrBuiltInType());
            } else if (yangNode instanceof YangOutput) {
                javaAttributeInfoOfOutput = tempJavaFragmentFiles
                        .getChildNodeAsAttributeInParentService(yangNode,
                                                                getParent(),
                                                                getJavaClassNameOrBuiltInType());
            } else {
                throw new TranslatorException(getErrorMsg(INVALID_CHILD_NODE,
                                                          this));
            }
            yangNode = yangNode.getNextSibling();
        }

        /*
         * Add the rpc information to the parent's service temp file.
         */
        try {

            ((TempJavaCodeFragmentFilesContainer) parent)
                    .getTempJavaCodeFragmentFiles().getServiceTempFiles()
                    .addJavaSnippetInfoToApplicableTempFiles(
                            javaAttributeInfoOfInput, javaAttributeInfoOfOutput,
                            getJavaClassNameOrBuiltInType());
        } catch (IOException e) {
            throw new TranslatorException(getErrorMsg(FAIL_AT_EXIT, this,
                                                      e.getLocalizedMessage()));
        }

        // generate RPC command file
        /*try {
            generateJava(GENERATE_RPC_COMMAND_CLASS, this);
        } catch (IOException e) {
            throw new TranslatorException("Failed to generate code for RPC node " +
                                                  getName() + " in " +
                                                  getLineNumber() + " at " +
                                                  getCharPosition()
                                                  + " in " + getFileName() + " " + e.getLocalizedMessage());
        }*/
    }
}
