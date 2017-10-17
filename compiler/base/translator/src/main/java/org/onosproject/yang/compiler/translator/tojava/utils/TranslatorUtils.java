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

package org.onosproject.yang.compiler.translator.tojava.utils;

import org.onosproject.yang.compiler.datamodel.LocationInfo;
import org.onosproject.yang.compiler.datamodel.YangIdentity;
import org.onosproject.yang.compiler.datamodel.YangIdentityRef;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yang.compiler.translator.tojava.TempJavaBeanFragmentFiles;
import org.onosproject.yang.compiler.translator.tojava.TempJavaCodeFragmentFilesContainer;
import org.onosproject.yang.compiler.translator.tojava.TempJavaTypeFragmentFiles;
import org.onosproject.yang.compiler.utils.UtilConstants;

import java.io.IOException;

import static org.onosproject.yang.compiler.utils.UtilConstants.AT;
import static org.onosproject.yang.compiler.utils.UtilConstants.IN;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_AUTO_PREFIX;

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

    /**
     * Returns the identity ref name with name conflict checking.
     *
     * @param yangType YANG type
     * @return identity ref name
     */
    static String getIdentityRefName(YangType<?> yangType) {
        YangIdentityRef ir = (YangIdentityRef) yangType
                .getDataTypeExtendedInfo();
        YangIdentity identity = ir.getReferredIdentity();
        String name = identity.getName();
        if (identity.isNameConflict()) {
            name = name + UtilConstants.IDENTITY;
        }
        return name;
    }

    /**
     * Returns enum YANG attribute name for given javaName.
     *
     * @param name enum javaName
     * @return YANG attribute name
     */
    public static String getEnumYangName(String name) {
        if (name.startsWith(YANG_AUTO_PREFIX)) {
            name = name.replaceFirst(YANG_AUTO_PREFIX, "");
        }
        return name;
    }
}
