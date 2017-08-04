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

import org.onosproject.yang.compiler.datamodel.javadatamodel.YangJavaUnion;
import org.onosproject.yang.compiler.translator.exception.InvalidNodeForTranslatorException;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGenerator;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yang.compiler.translator.tojava.TempJavaCodeFragmentFiles;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.IOException;

import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_UNION_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.YangJavaModelUtils.generateCodeOfNode;
import static org.onosproject.yang.compiler.translator.tojava.YangJavaModelUtils.generateJava;

/**
 * Represents union information extended to support java code generation.
 */
public class YangJavaUnionTranslator
        extends YangJavaUnion
        implements JavaCodeGeneratorInfo, JavaCodeGenerator {

    private static final long serialVersionUID = 806201619L;

    /**
     * File handle to maintain temporary java code fragments as per the code
     * snippet types.
     */
    private transient TempJavaCodeFragmentFiles tempFileHandle;

    /**
     * Creates an instance of YANG java union.
     */
    public YangJavaUnionTranslator() {
        super();
        setJavaFileInfo(new JavaFileInfoTranslator());
        getJavaFileInfo().setGeneratedFileTypes(GENERATE_UNION_CLASS);
    }

    /**
     * Returns the generated java file information.
     *
     * @return generated java file information
     */
    @Override
    public JavaFileInfoTranslator getJavaFileInfo() {
        if (javaFileInfo == null) {
            throw new RuntimeException("Missing java info in java datamodel node " + getName() + " in " +
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
     * Prepare the information for java code generation corresponding to YANG
     * union info.
     *
     * @param yangPlugin YANG plugin config
     * @throws TranslatorException when fails to translate
     */
    @Override
    public void generateCodeEntry(YangPluginConfig yangPlugin) throws TranslatorException {
        try {
            if (getReferredSchema() != null) {
                throw new InvalidNodeForTranslatorException();
            }
            generateCodeOfNode(this, yangPlugin);
        } catch (IOException e) {
            throw new TranslatorException(
                    "Failed to prepare generate code entry for union node " + getName() + " in " +
                            getLineNumber() + " at " +
                            getCharPosition()
                            + " in " + getFileName() + " " + e.getLocalizedMessage());
        }
    }

    /**
     * Creates a java file using the YANG union info.
     *
     * @throws TranslatorException when fails to translate
     */
    @Override
    public void generateCodeExit() throws TranslatorException {
        try {
            generateJava(GENERATE_UNION_CLASS, this);
        } catch (IOException e) {
            throw new TranslatorException("Failed to generate code for union node " + getName() + " in " +
                                                  getLineNumber() + " at " +
                                                  getCharPosition()
                                                  + " in " + getFileName() + " " + e.getLocalizedMessage());
        }
    }
}
