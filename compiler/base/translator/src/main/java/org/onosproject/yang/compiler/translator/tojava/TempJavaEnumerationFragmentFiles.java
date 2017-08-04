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

import org.onosproject.yang.compiler.datamodel.YangEnum;
import org.onosproject.yang.compiler.datamodel.YangEnumeration;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaTypeTranslator;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT32;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.ENUM_IMPL_MASK;
import static org.onosproject.yang.compiler.translator.tojava.JavaAttributeInfo.getAttributeInfoForTheData;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaCodeSnippetGen.generateEnumAttributeStringWithSchemaName;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGenerator.generateEnumClassFile;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaIdentifierSyntax.createPackage;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.INT;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_FOR_FIRST_DIGIT;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_AUTO_PREFIX;
import static org.onosproject.yang.compiler.utils.io.impl.FileSystemUtil.closeFile;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getPrefixForIdentifier;

/**
 * Represents implementation of java code fragments temporary implementations. Maintains the temp files required
 * specific for enumeration java snippet generation.
 */
public class TempJavaEnumerationFragmentFiles
        extends TempJavaFragmentFiles {

    /**
     * File name for temporary enum class.
     */
    private static final String ENUM_CLASS_TEMP_FILE_NAME = "EnumClass";

    /**
     * Temporary file handle for enum class file.
     */
    private final File enumClassTempFileHandle;

    /**
     * Java file handle for enum class.
     */
    private File enumClassJavaFileHandle;
    private boolean isEnumClass;

    /**
     * Creates an instance of temporary java code fragment.
     *
     * @param javaFileInfo generated java file info
     * @throws IOException when fails to create new file handle
     */
    public TempJavaEnumerationFragmentFiles(JavaFileInfoTranslator javaFileInfo)
            throws IOException {

        super(javaFileInfo);
        /*
         * Initialize enum when generation file type matches to enum class mask.
         */
        addGeneratedTempFile(ENUM_IMPL_MASK);
        enumClassTempFileHandle = getTemporaryFileHandle(ENUM_CLASS_TEMP_FILE_NAME);
    }

    /**
     * Returns temporary file handle for enum class file.
     *
     * @return temporary file handle for enum class file
     */
    public File getEnumClassTempFileHandle() {
        return enumClassTempFileHandle;
    }

    /**
     * Adds enum class attributes to temporary file.
     *
     * @param yangEnum YANG enum
     * @throws IOException when fails to do IO operations.
     */
    private void addAttributesForEnumClass(YangEnum yangEnum)
            throws IOException {
        appendToFile(enumClassTempFileHandle,
                     generateEnumAttributeStringWithSchemaName(yangEnum.getNamedValue(),
                                                               yangEnum.getValue()));
    }

    /**
     * Adds enum attributes to temporary files.
     *
     * @param curNode current YANG node
     * @param config  plugin configurations
     * @throws IOException when fails to do IO operations
     */
    public void addEnumAttributeToTempFiles(YangNode curNode,
                                            YangPluginConfig config)
            throws IOException {

        addJavaSnippetInfoToApplicableTempFiles(getJavaAttributeForEnum(config),
                                                config);
        if (curNode instanceof YangEnumeration) {
            YangEnumeration enumeration = (YangEnumeration) curNode;
            for (YangEnum curEnum : enumeration.getEnumSet()) {
                String enumName = curEnum.getNamedValue();
                String prefix;
                if (enumName.matches(REGEX_FOR_FIRST_DIGIT)) {
                    prefix = getPrefixForIdentifier(
                            config.getConflictResolver());
                    if (prefix != null) {
                        curEnum.setNamedValue(prefix + enumName);
                    } else {
                        curEnum.setNamedValue(YANG_AUTO_PREFIX + enumName);
                    }
                }
                addJavaSnippetInfoToApplicableTempFiles(curEnum);
            }
        } else {
            throw new TranslatorException(
                    "current node should be of enumeration type. " +
                            curNode.getName() + " in " + curNode.getLineNumber() +
                            " at " + curNode.getCharPosition() + " in " + curNode
                            .getFileName());
        }
    }

    /**
     * Returns java attribute for enum class.
     *
     * @param config plugin configurations
     * @return java attribute
     */
    private JavaAttributeInfo getJavaAttributeForEnum(YangPluginConfig config) {
        YangJavaTypeTranslator javaType = new YangJavaTypeTranslator();
        javaType.setDataType(INT32);
        javaType.setDataTypeName(INT);
        javaType.updateJavaQualifiedInfo(config.getConflictResolver());
        return getAttributeInfoForTheData(
                javaType.getJavaQualifiedInfo(),
                javaType.getDataTypeName(), javaType,
                getIsQualifiedAccessOrAddToImportList(javaType.getJavaQualifiedInfo()),
                false);
    }

    /**
     * Adds the new attribute info to the target generated temporary files.
     *
     * @param yangEnum @throws IOException IO operation fail
     */
    private void addJavaSnippetInfoToApplicableTempFiles(YangEnum yangEnum)
            throws IOException {
        addAttributesForEnumClass(yangEnum);
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

        List<String> imports = this.getJavaImportData().getImports(true);
        createPackage(curNode);
        enumClassJavaFileHandle = getJavaFileHandle(getJavaClassName(EMPTY_STRING));
        generateEnumClassFile(enumClassJavaFileHandle, curNode, imports);
        freeTemporaryResources(false);
    }

    /**
     * Removes all temporary file handles.
     *
     * @param isErrorOccurred flag to tell translator that error has occurred while file generation
     * @throws IOException when failed to delete the temporary files
     */
    @Override
    public void freeTemporaryResources(boolean isErrorOccurred)
            throws IOException {
        closeFile(enumClassJavaFileHandle, isErrorOccurred);
        closeFile(enumClassTempFileHandle, true);
        if (isEnumClass) {
            super.freeTemporaryResources(isErrorOccurred);
        }
    }

    public boolean isEnumClass() {
        return isEnumClass;
    }

    /**
     * Sets  true if free super resources is required.
     *
     * @param enumClass true if free super resources is required
     */
    public void setEnumClass(boolean enumClass) {
        isEnumClass = enumClass;
    }
}
