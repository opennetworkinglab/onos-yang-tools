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

import org.onosproject.yangutils.datamodel.LocationInfo;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangSchemaNode;
import org.onosproject.yangutils.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yangutils.translator.tojava.TempJavaBeanFragmentFiles;
import org.onosproject.yangutils.translator.tojava.TempJavaCodeFragmentFilesContainer;
import org.onosproject.yangutils.translator.tojava.TempJavaTypeFragmentFiles;

import java.io.IOException;

import static org.onosproject.yangutils.utils.UtilConstants.AT;
import static org.onosproject.yangutils.utils.UtilConstants.IN;

/**
 * Represents common translator utilities.
 */
public final class TranslatorUtils {

    // No instantiation
    private TranslatorUtils() {
    }

    /**
     * Returns translator error message string with location information for
     * YANG schema node with localized message.
     *
     * @param errorType    type of translator error
     * @param node         YANG schema node
     * @param localizedMsg localized message
     * @return translator error message
     */
    public static String getErrorMsg(TranslatorErrorType errorType,
                                     YangSchemaNode node,
                                     String localizedMsg) {
        return getErrorMsg(errorType, node) + localizedMsg;
    }

    /**
     * Returns translator error message string with location information for
     * YANG schema node.
     *
     * @param errorType type of translator error
     * @param node      YANG schema node
     * @return translator error message
     */
    public static String getErrorMsg(TranslatorErrorType errorType,
                                     YangSchemaNode node) {
        return errorType.prefix() + IN + node.getName() + getLocationMsg(node);
    }

    /**
     * Returns translator error message string with location information for
     * JAVA code generator info.
     *
     * @param errorType type of translator error
     * @param location  node with location info
     * @return translator error message
     */
    public static String getErrorMsgForCodeGenerator(TranslatorErrorType errorType,
                                                     LocationInfo location) {
        return errorType.prefix() + getLocationMsg(location);
    }

    /**
     * Returns location message string.
     *
     * @param location location info node
     * @return location message string
     */
    private static String getLocationMsg(LocationInfo location) {
        return AT + location.getLineNumber() + AT +
                location.getCharPosition() + IN + location.getFileName();
    }

    /**
     * Returns bean temp files for YANG node.
     *
     * @param curNode current YANG node
     * @return bean files
     */
    public static TempJavaBeanFragmentFiles getBeanFiles(YangNode curNode) {
        return ((TempJavaCodeFragmentFilesContainer) curNode)
                .getTempJavaCodeFragmentFiles().getBeanTempFiles();
    }

    /**
     * Returns bean temp files for JAVA code generator info.
     *
     * @param info JAVA code generator info
     * @return bean files
     */
    public static TempJavaBeanFragmentFiles getBeanFiles(JavaCodeGeneratorInfo
                                                                 info) {
        return info.getTempJavaCodeFragmentFiles().getBeanTempFiles();
    }

    /**
     * Returns type temp files for YANG node.
     *
     * @param curNode current YANG node
     * @return type files
     */
    public static TempJavaTypeFragmentFiles getTypeFiles(YangNode curNode) {
        return ((TempJavaCodeFragmentFilesContainer) curNode)
                .getTempJavaCodeFragmentFiles().getTypeTempFiles();
    }

    /**
     * Adds default constructor to a given YANG node.
     *
     * @param node     YANG node
     * @param modifier modifier for constructor.
     * @param toAppend string which need to be appended with the class name
     * @return default constructor for class
     * @throws IOException when fails to append to file
     */
    static String addDefaultConstructor(YangNode node, String modifier,
                                        String toAppend)
            throws IOException {
        return ((TempJavaCodeFragmentFilesContainer) node)
                .getTempJavaCodeFragmentFiles()
                .addDefaultConstructor(modifier, toAppend);
        /*
         * TODO update addDefaultConstructor, it doesn't need YANG node as an
         * input.
         */
    }
}
