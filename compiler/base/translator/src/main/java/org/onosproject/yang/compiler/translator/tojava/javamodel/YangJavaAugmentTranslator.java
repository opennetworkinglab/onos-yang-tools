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

import org.onosproject.yang.compiler.datamodel.YangChoice;
import org.onosproject.yang.compiler.datamodel.javadatamodel.YangJavaAugment;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGenerator;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yang.compiler.translator.tojava.TempJavaCodeFragmentFiles;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.IOException;

import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_INTERFACE_WITH_BUILDER;
import static org.onosproject.yang.compiler.translator.tojava.YangJavaModelUtils.generateCodeOfNode;

/**
 * Represents augment information extended to support java code generation.
 */
public class YangJavaAugmentTranslator
        extends YangJavaAugment
        implements JavaCodeGeneratorInfo, JavaCodeGenerator {

    private static final long serialVersionUID = 806201632L;

    /**
     * File handle to maintain temporary java code fragments as per the code snippet types.
     */
    private transient TempJavaCodeFragmentFiles tempFileHandle;

    /**
     * Creates a YANG java augment object.
     */
    public YangJavaAugmentTranslator() {
        super();
        setJavaFileInfo(new JavaFileInfoTranslator());
        getJavaFileInfo().setGeneratedFileTypes(GENERATE_INTERFACE_WITH_BUILDER);
    }

    /**
     * Returns the generated java file information.
     *
     * @return generated java file information
     */
    @Override
    public JavaFileInfoTranslator getJavaFileInfo() {

        if (javaFileInfo == null) {
            throw new TranslatorException("Missing java info in java datamodel node " +
                                                  getName() + " in " +
                                                  getLineNumber() + " at " +
                                                  getCharPosition()
                                                  + " in " + getFileName());
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

    /**
     * Returns the temporary file handle.
     *
     * @return temporary file handle
     */
    @Override
    public TempJavaCodeFragmentFiles getTempJavaCodeFragmentFiles() {
        return tempFileHandle;
    }

    /**
     * Sets temporary file handle.
     *
     * @param fileHandle temporary file handle
     */
    @Override
    public void setTempJavaCodeFragmentFiles(TempJavaCodeFragmentFiles fileHandle) {
        tempFileHandle = fileHandle;
    }

    /**
     * Prepare the information for java code generation corresponding to YANG augment info.
     *
     * @param yangPlugin YANG plugin config
     * @throws TranslatorException translator operation fail
     */
    @Override
    public void generateCodeEntry(YangPluginConfig yangPlugin) throws TranslatorException {
        try {
            generateCodeOfNode(this, yangPlugin);
        } catch (IOException e) {
            throw new TranslatorException("Failed to generate code for augmentable node " +
                                                  getName() + " in " +
                                                  getLineNumber() + " at " +
                                                  getCharPosition()
                                                  + " in " + getFileName() + " " + e.getLocalizedMessage());
        }
    }

    /**
     * Create a java file using the YANG augment info.
     *
     * @throws TranslatorException when failed to do translator operations
     */
    @Override
    public void generateCodeExit() throws TranslatorException {
        try {
            if (validateAugmentNode()) {
                getTempJavaCodeFragmentFiles().generateJavaFile(GENERATE_INTERFACE_WITH_BUILDER, this);
            }
        } catch (IOException e) {
            throw new TranslatorException("Failed to generate code for augmentable node " +
                                                  getName() + " in " +
                                                  getLineNumber() + " at " +
                                                  getCharPosition()
                                                  + " in " + getFileName() + " " + e.getLocalizedMessage());
        }
    }

    /**
     * Returns true if augment does not have choice as target node.
     *
     * @return true if augment does not have choice as target node
     */
    private boolean validateAugmentNode() {
        return !(getAugmentedNode() instanceof YangChoice);
    }
}
