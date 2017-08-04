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

import org.onosproject.yang.compiler.datamodel.YangCompilerAnnotation;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.javadatamodel.JavaQualifiedTypeInfo;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;

import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.isTypeLeafref;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.isTypeNameLeafref;

/**
 * Represents the attribute info corresponding to class/interface generated.
 */
public final class JavaAttributeInfo {

    /**
     * The data type info of attribute.
     */
    private YangType<?> attrType;

    /**
     * Name of the attribute.
     */
    private String name;

    /**
     * If the added attribute is a list of info.
     */
    private boolean isListAttr;

    /**
     * If the added attribute has to be accessed in a fully qualified manner.
     */
    private boolean isQualifiedName;

    /**
     * The class info will be used to set the attribute type and package info
     * will be use for qualified name.
     */
    private JavaQualifiedTypeInfoTranslator importInfo;

    /**
     * Compiler annotation attribute info.
     */
    private YangCompilerAnnotation compilerAnnotation;

    /**
     * If conflict occurs.
     */
    private boolean isIntConflict;

    /**
     * If conflict occurs.
     */
    private boolean isLongConflict;

    /**
     * If conflict occurs.
     */
    private boolean isShortConflict;

    /**
     * current holder or count in type list of attribute for from string method.
     */
    private String curHolderOrCount;

    /**
     * Creates a java attribute info object.
     */
    private JavaAttributeInfo() {
    }

    /**
     * Creates object of java attribute info.
     *
     * @param attrType        YANG type
     * @param name            attribute name
     * @param isListAttr      is list attribute
     * @param isQualifiedName is qualified name
     */
    public JavaAttributeInfo(YangType<?> attrType, String name, boolean isListAttr, boolean isQualifiedName) {
        this.attrType = attrType;
        this.name = name;
        this.isListAttr = isListAttr;
        this.isQualifiedName = isQualifiedName;
    }

    /**
     * Returns the data type info of attribute.
     *
     * @return the data type info of attribute
     */
    public YangType<?> getAttributeType() {
        return attrType;
    }

    /**
     * Sets the data type info of attribute.
     *
     * @param type the data type info of attribute
     */
    public void setAttributeType(YangType<?> type) {
        attrType = type;
    }

    /**
     * Returns name of the attribute.
     *
     * @return name of the attribute
     */
    public String getAttributeName() {

        if (name == null) {
            throw new TranslatorException("Expected java attribute name is null");
        }
        return name;
    }

    /**
     * Sets name of the attribute.
     *
     * @param attrName name of the attribute
     */
    public void setAttributeName(String attrName) {
        name = attrName;
    }

    /**
     * Returns if the added attribute is a list of info.
     *
     * @return the if the added attribute is a list of info
     */
    public boolean isListAttr() {
        return isListAttr;
    }

    /**
     * Sets if the added attribute is a list of info.
     *
     * @param isList if the added attribute is a list of info
     */
    private void setListAttr(boolean isList) {
        isListAttr = isList;
    }

    /**
     * Returns if the added attribute has to be accessed in a fully qualified
     * manner.
     *
     * @return the if the added attribute has to be accessed in a fully
     * qualified manner.
     */
    public boolean isQualifiedName() {
        return isQualifiedName;
    }

    /**
     * Sets if the added attribute has to be accessed in a fully qualified
     * manner.
     *
     * @param isQualified if the added attribute has to be accessed in a fully
     *                    qualified manner
     */
    public void setIsQualifiedAccess(boolean isQualified) {
        isQualifiedName = isQualified;
    }

    /**
     * Returns the import info for the attribute type. It will be null, if the type
     * is basic built-in java type.
     *
     * @return import info
     */
    public JavaQualifiedTypeInfoTranslator getImportInfo() {
        return importInfo;
    }

    /**
     * Sets the import info for the attribute type.
     *
     * @param importInfo import info for the attribute type
     */
    public void setImportInfo(JavaQualifiedTypeInfoTranslator importInfo) {
        this.importInfo = importInfo;
    }

    /**
     * Returns the compiler annotation.
     *
     * @return compiler annotation info
     */
    public YangCompilerAnnotation getCompilerAnnotation() {
        return compilerAnnotation;
    }

    /**
     * Sets the compiler annotation.
     *
     * @param compilerAnnotation the compiler annotation to set
     */
    public void setCompilerAnnotation(YangCompilerAnnotation compilerAnnotation) {
        this.compilerAnnotation = compilerAnnotation;
    }

    /**
     * Returns true if conflict between int and uint.
     *
     * @return true if conflict between int and uInt
     */
    public boolean isIntConflict() {
        return isIntConflict;
    }

    /**
     * Sets true if conflict between int and uInt.
     *
     * @param intConflict true if conflict between int and uInt
     */
    void setIntConflict(boolean intConflict) {
        isIntConflict = intConflict;
    }

    /**
     * Returns true if conflict between long and uLong.
     *
     * @return true if conflict between long and uLong
     */
    public boolean isLongConflict() {
        return isLongConflict;
    }

    /**
     * Sets true if conflict between long and uLong.
     *
     * @param longConflict true if conflict between long and uLong
     */
    void setLongConflict(boolean longConflict) {
        isLongConflict = longConflict;
    }

    /**
     * Returns true if conflict between short and uint8.
     *
     * @return true if conflict between short and uint8
     */
    public boolean isShortConflict() {
        return isShortConflict;
    }

    /**
     * Sets true if conflict between short and uint8.
     *
     * @param shortConflict true if conflict between short and uint8
     */
    public void setShortConflict(boolean shortConflict) {
        isShortConflict = shortConflict;
    }

    /**
     * Returns java attribute info.
     *
     * @param importInfo        java qualified type info
     * @param attributeName     attribute name
     * @param attributeType     attribute type
     * @param isQualifiedAccess is the attribute a qualified access
     * @param isListAttribute   is list attribute
     * @return java attribute info.
     */
    public static JavaAttributeInfo getAttributeInfoForTheData(JavaQualifiedTypeInfo importInfo,
                                                               String attributeName,
                                                               YangType<?> attributeType, boolean isQualifiedAccess,
                                                               boolean isListAttribute) {

        if (attributeType != null) {
            attributeType = isTypeLeafref(attributeType);
        }
        attributeName = isTypeNameLeafref(attributeName, attributeType);
        JavaAttributeInfo newAttr = new JavaAttributeInfo();
        newAttr.setImportInfo((JavaQualifiedTypeInfoTranslator) importInfo);
        newAttr.setAttributeName(attributeName);
        newAttr.setAttributeType(attributeType);
        newAttr.setIsQualifiedAccess(isQualifiedAccess);
        newAttr.setListAttr(isListAttribute);

        return newAttr;
    }

    /**
     * Returns java attribute info.
     *
     * @param importInfo         java qualified type info
     * @param attributeName      attribute name
     * @param attributeType      attribute type
     * @param isQualifiedAccess  is the attribute a qualified access
     * @param isListAttribute    is list attribute
     * @param compilerAnnotation compiler annotation
     * @return java attribute info.
     */
    static JavaAttributeInfo getAttributeInfoForTheData(JavaQualifiedTypeInfoTranslator importInfo,
                                                        String attributeName, YangType<?> attributeType,
                                                        boolean isQualifiedAccess, boolean isListAttribute,
                                                        YangCompilerAnnotation compilerAnnotation) {
        JavaAttributeInfo newAttr = getAttributeInfoForTheData(importInfo, attributeName, attributeType,
                                                               isQualifiedAccess, isListAttribute);

        newAttr.setCompilerAnnotation(compilerAnnotation);

        return newAttr;
    }

    /**
     * Returns current holder or count in type list of attribute for from string method.
     *
     * @return current holder or count in type list of attribute for from string method
     */
    public String getCurHolderOrCount() {
        return curHolderOrCount;
    }

    /**
     * Sets current holder or count in type list of attribute for from string method.
     *
     * @param curHolderOrCount current holder or count in type list of attribute for from string method
     */
    public void setCurHolderOrCount(String curHolderOrCount) {
        this.curHolderOrCount = curHolderOrCount;
    }
}
