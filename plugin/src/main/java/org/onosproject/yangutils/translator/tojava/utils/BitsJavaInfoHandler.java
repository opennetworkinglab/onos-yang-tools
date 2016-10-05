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

import org.onosproject.yangutils.datamodel.YangBit;
import org.onosproject.yangutils.datamodel.YangBits;
import org.onosproject.yangutils.datamodel.YangEnum;
import org.onosproject.yangutils.datamodel.YangEnumeration;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangType;
import org.onosproject.yangutils.datamodel.exceptions.DataModelException;
import org.onosproject.yangutils.translator.tojava.JavaAttributeInfo;
import org.onosproject.yangutils.translator.tojava.JavaFileInfoContainer;
import org.onosproject.yangutils.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yangutils.translator.tojava.TempJavaCodeFragmentFiles;
import org.onosproject.yangutils.translator.tojava.javamodel.YangJavaEnumerationTranslator;

import java.io.IOException;
import java.util.Map;

import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_ENUM_CLASS;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getCapitalCase;

/**
 * Represents bits YANG type info.
 */
public class BitsJavaInfoHandler {

    private JavaAttributeInfo attr;
    private YangType<?> yangType;

    /**
     * Creates an instance of bits java info handler.
     *
     * @param attr     java attribute
     * @param yangType YANG type
     */
    public BitsJavaInfoHandler(JavaAttributeInfo attr, YangType<?> yangType) {
        this.attr = attr;
        this.yangType = yangType;
    }

    /**
     * Returns bits type enum file.
     *
     * @param attr    attribute
     * @param type    data type
     * @param curNode current node
     * @throws IOException when fails to do IO operations
     */
    static void generateBitsFile(JavaAttributeInfo attr, YangType type,
                                 YangNode curNode) throws IOException {
        JavaFileInfoTranslator fileInfo = ((JavaFileInfoContainer) curNode)
                .getJavaFileInfo();
        String className = fileInfo.getJavaName() +
                getCapitalCase(attr.getAttributeName());
        JavaFileInfoTranslator attrInfo = new JavaFileInfoTranslator();
        attrInfo.setJavaName(className);
        attrInfo.setPackage(fileInfo.getPackage());
        attrInfo.setBaseCodeGenPath(fileInfo.getBaseCodeGenPath());
        attrInfo.setGeneratedFileTypes(GENERATE_ENUM_CLASS);
        attrInfo.setPackageFilePath(fileInfo.getPackageFilePath());
        attrInfo.setPluginConfig(fileInfo.getPluginConfig());
        TempJavaCodeFragmentFiles codeFile = new TempJavaCodeFragmentFiles(attrInfo);
        YangJavaEnumerationTranslator enumeration = new YangJavaEnumerationTranslator() {
            @Override
            public String getJavaPackage() {
                return attr.getImportInfo().getPkgInfo();
            }

            @Override
            public String getJavaClassNameOrBuiltInType() {
                return className;
            }

            @Override
            public String getJavaAttributeName() {
                return className;
            }
        };

        enumeration.setName(className);
        enumeration.setJavaFileInfo(attrInfo);
        enumeration.setTempJavaCodeFragmentFiles(codeFile);
        YangBits yangBits = (YangBits) type.getDataTypeExtendedInfo();
        Integer key;
        YangBit bit;
        String bitName;
        for (Map.Entry<Integer, YangBit> entry : yangBits.getBitPositionMap()
                .entrySet()) {
            key = entry.getKey();
            bit = entry.getValue();
            if (bit != null) {
                bitName = bit.getBitName();
                createAndAddEnum(bitName, key, enumeration);
            }
        }

        codeFile.getEnumTempFiles()
                .addEnumAttributeToTempFiles(enumeration, fileInfo.getPluginConfig());
        codeFile.getEnumTempFiles().setEnumClass(false);
        codeFile.generateJavaFile(GENERATE_ENUM_CLASS, enumeration);
    }

    private static void createAndAddEnum(String name, int value,
                                         YangEnumeration enumeration) {
        YangEnum yangEnum = new YangEnum();
        yangEnum.setNamedValue(name);
        yangEnum.setValue(value);
        try {
            enumeration.addEnumInfo(yangEnum);
        } catch (DataModelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns java attribute for bits.
     *
     * @return java attribute for bits
     */
    public JavaAttributeInfo getAttr() {
        return attr;
    }

    /**
     * Returns YANG type for bits.
     *
     * @return yang type for bits
     */
    public YangType<?> getYangType() {
        return yangType;
    }
}
