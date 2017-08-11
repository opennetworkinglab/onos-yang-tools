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

import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.javadatamodel.YangJavaTypeDef;
import org.onosproject.yang.compiler.translator.exception.InvalidNodeForTranslatorException;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGenerator;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yang.compiler.translator.tojava.TempJavaCodeFragmentFiles;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.IOException;

import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DERIVED;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.LEAFREF;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_TYPEDEF_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.YangJavaModelUtils.generateCodeOfNode;
import static org.onosproject.yang.compiler.translator.tojava.YangJavaModelUtils.generateJava;

/**
 * Represents type define information extended to support java code generation.
 */
public class YangJavaTypeDefTranslator
        extends YangJavaTypeDef
        implements JavaCodeGeneratorInfo, JavaCodeGenerator {

    private static final long serialVersionUID = 806201620L;

    /**
     * File handle to maintain temporary java code fragments as per the code
     * snippet types.
     */
    private transient TempJavaCodeFragmentFiles tempFileHandle;

    /**
     * Creates a YANG java typedef object.
     */
    public YangJavaTypeDefTranslator() {
        super();
        setJavaFileInfo(new JavaFileInfoTranslator());
        getJavaFileInfo().setGeneratedFileTypes(GENERATE_TYPEDEF_CLASS);
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
     * Prepare the information for java code generation corresponding to YANG
     * typedef info.
     *
     * @param yangPlugin YANG plugin config
     * @throws TranslatorException when fails to translate
     */
    @Override
    public void generateCodeEntry(YangPluginConfig yangPlugin) throws TranslatorException {
        if (getReferredSchema() != null) {
            throw new InvalidNodeForTranslatorException();
        }
        // TODO update the below exception in all related places, remove file
        // name and other information.
        YangType typeInTypeDef = this.getTypeDefBaseType();
        InvalidNodeForTranslatorException exception = new InvalidNodeForTranslatorException();
        exception.setFileName(this.getFileName());
        exception.setCharPosition(this.getCharPosition());
        exception.setLine(this.getLineNumber());
        if (typeInTypeDef.getDataType() == DERIVED) {
            YangDerivedInfo derivedInfo = (YangDerivedInfo) typeInTypeDef.getDataTypeExtendedInfo();
            if (derivedInfo.getEffectiveBuiltInType() == LEAFREF) {
                throw exception;
            }
        } else if (typeInTypeDef.getDataType() == LEAFREF) {
            throw exception;
        }
        try {
            generateCodeOfNode(this, yangPlugin);
        } catch (IOException e) {
            throw new TranslatorException(
                    "Failed to prepare generate code entry for typedef node " + getName()
                            + " in " + getLineNumber() +
                            " at " + getCharPosition() +
                            " in " + getFileName(), e);
        }
    }

    /**
     * Create a java file using the YANG typedef info.
     *
     * @throws TranslatorException when fails to translate
     */
    @Override
    public void generateCodeExit() throws TranslatorException {
        try {
            generateJava(GENERATE_TYPEDEF_CLASS, this);
        } catch (IOException e) {
            throw new TranslatorException(
                    "Failed to prepare generate code for typedef node " + getName()
                            + " in " + getLineNumber() +
                            " at " + getCharPosition() +
                            " in " + getFileName(), e);
        }
    }
}
