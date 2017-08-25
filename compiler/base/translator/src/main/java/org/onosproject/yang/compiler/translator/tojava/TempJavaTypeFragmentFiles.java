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

import org.onosproject.yang.compiler.datamodel.YangIdentity;
import org.onosproject.yang.compiler.datamodel.YangIdentityRef;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangTypeDef;
import org.onosproject.yang.compiler.datamodel.YangTypeHolder;
import org.onosproject.yang.compiler.datamodel.YangUnion;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaTypeTranslator;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.onosproject.yang.compiler.datamodel.exceptions.ErrorMessages.getErrorMsg;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.BINARY;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.BITS;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT16;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT32;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.INT64;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT16;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT32;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.UINT8;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_TYPEDEF_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_UNION_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.CONSTRUCTOR_FOR_TYPE_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.FROM_STRING_IMPL_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.OF_STRING_IMPL_MASK;
import static org.onosproject.yang.compiler.translator.tojava.JavaAttributeInfo.getAttributeInfoForTheData;
import static org.onosproject.yang.compiler.translator.tojava.JavaQualifiedTypeInfoTranslator.getQualifiedInfoOfFromString;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGenerator.generateTypeDefClassFile;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGenerator.generateUnionClassFile;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getDerivedPkfInfo;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaIdentifierSyntax.createPackage;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getOfMethodStringAndJavaDoc;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getTypeConstructorStringAndJavaDoc;
import static org.onosproject.yang.compiler.translator.tojava.utils.ValidatorTypeForUnionTypes.INT_TYPE_CONFLICT;
import static org.onosproject.yang.compiler.translator.tojava.utils.ValidatorTypeForUnionTypes.LONG_TYPE_CONFLICT;
import static org.onosproject.yang.compiler.translator.tojava.utils.ValidatorTypeForUnionTypes.SHORT_TYPE_CONFLICT;
import static org.onosproject.yang.compiler.utils.UtilConstants.BASE64;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.HOLDER_TYPE_DEF;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_UTIL_PKG;
import static org.onosproject.yang.compiler.utils.UtilConstants.LAST;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.io.impl.FileSystemUtil.closeFile;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;

/**
 * Represents implementation of java data type code fragments temporary implementations. Maintains the temp files
 * required specific for user defined data type java snippet generation.
 */
//TODO: Update with multi type handler framework.
public class TempJavaTypeFragmentFiles
        extends TempJavaFragmentFiles {

    /**
     * File name for of string method.
     */
    private static final String OF_STRING_METHOD_FILE_NAME = "OfString";

    /**
     * File name for construction for special type like union, typedef.
     */
    private static final String CONSTRUCTOR_FOR_TYPE_FILE_NAME = "ConstructorForType";

    /**
     * Integer index in type list.
     */
    private int intIndex = 0;

    /**
     * UInt index in type list.
     */
    private int uIntIndex = 0;

    /**
     * long index in type list.
     */
    private int longIndex = 0;

    /**
     * ULong index in type list.
     */
    private int uLongIndex = 0;

    /**
     * short index in type list.
     */
    private int shortIndex = 0;

    /**
     * Uint8 index in type list.
     */
    private int uInt8Index = 0;

    /**
     * Temporary file handle for of string method of class.
     */
    private File ofStringImplTempFileHandle;

    /**
     * Temporary file handle for constructor for type class.
     */
    private File constructorForTypeTempFileHandle;

    /**
     * Java file handle for typedef class file.
     */
    private File typedefClassJavaFileHandle;

    /**
     * Java file handle for type class like union, typedef file.
     */
    private File typeClassJavaFileHandle;

    /**
     * Java attribute for int.
     */
    private JavaAttributeInfo intAttribute;

    /**
     * Java attribute for long.
     */
    private JavaAttributeInfo longAttribute;

    /**
     * Java attribute for short.
     */
    private JavaAttributeInfo shortAttribute;

    /**
     * Java attribute for uint8.
     */
    private JavaAttributeInfo uInt8Attribute;

    /**
     * Java attribute for uInt.
     */
    private JavaAttributeInfo uIntAttribute;

    /**
     * Java attribute for uLong.
     */
    private JavaAttributeInfo uLongAttribute;

    private List<YangType<?>> local = new ArrayList<>();

    private List<JavaAttributeInfo> attrs = new ArrayList<>();

    /**
     * Creates an instance of temporary java code fragment.
     *
     * @param javaFileInfo generated java file info
     * @throws IOException when fails to create new file handle
     */
    TempJavaTypeFragmentFiles(JavaFileInfoTranslator javaFileInfo)
            throws IOException {

        super(javaFileInfo);

        /*
         * Initialize getterImpl, attributes, hash code, equals and to strings
         * when generation file type matches to typeDef class mask.
         */
        addGeneratedTempFile(OF_STRING_IMPL_MASK | CONSTRUCTOR_FOR_TYPE_MASK |
                                     FROM_STRING_IMPL_MASK);

        ofStringImplTempFileHandle = getTemporaryFileHandle(
                OF_STRING_METHOD_FILE_NAME);
        constructorForTypeTempFileHandle = getTemporaryFileHandle(
                CONSTRUCTOR_FOR_TYPE_FILE_NAME);
    }

    /**
     * Returns type class constructor method's temporary file handle.
     *
     * @return type class constructor method's temporary file handle
     */

    public File getConstructorForTypeTempFileHandle() {
        return constructorForTypeTempFileHandle;
    }

    /**
     * Returns java file handle for typedef class file.
     *
     * @return java file handle for typedef class file
     */
    private File getTypedefClassJavaFileHandle() {
        return typedefClassJavaFileHandle;
    }

    /**
     * Sets the java file handle for typedef class file.
     *
     * @param typedefClassJavaFileHandle java file handle
     */
    private void setTypedefClassJavaFileHandle(File typedefClassJavaFileHandle) {
        this.typedefClassJavaFileHandle = typedefClassJavaFileHandle;
    }

    /**
     * Returns java file handle for type class file.
     *
     * @return java file handle for type class file
     */
    private File getTypeClassJavaFileHandle() {
        return typeClassJavaFileHandle;
    }

    /**
     * Sets the java file handle for type class file.
     *
     * @param typeClassJavaFileHandle type file handle
     */
    private void setTypeClassJavaFileHandle(File typeClassJavaFileHandle) {
        this.typeClassJavaFileHandle = typeClassJavaFileHandle;
    }

    /**
     * Returns of string method's temporary file handle.
     *
     * @return of string method's temporary file handle
     */

    public File getOfStringImplTempFileHandle() {
        return ofStringImplTempFileHandle;
    }

    private void verifyUnionTypes(List<YangType<?>> typeList,
                                  YangTypeHolder yangTypeHolder) {
        String msg;
        YangUnion union = (YangUnion) yangTypeHolder;
        for (YangType<?> yangType : typeList) {
            YangDataTypes type = yangType.getDataType();
            switch (type) {
                case EMPTY:
                    msg = "Union member derived type must not be one of the " +
                            "type whose built-in types is \"empty\"";
                    break;
                case LEAFREF:
                    msg = "Union member derived type must not be one of the " +
                            "type whose built-in types is \"leafref\"";
                    break;
                default:
                    msg = null;
            }
            if (msg != null) {
                throw new TranslatorException(getErrorMsg(
                        msg, union.getName(), union.getLineNumber(), union
                                .getCharPosition(), union.getFileName()));
            }
        }
    }

    /**
     * Adds all the type in the current data model node as part of the generated temporary file.
     *
     * @param yangTypeHolder YANG java data model node which has type info, eg union / typedef
     * @param config         plugin configurations for naming conventions
     * @throws IOException IO operation fail
     */
    void addTypeInfoToTempFiles(YangTypeHolder yangTypeHolder, YangPluginConfig config)
            throws IOException {
        List<YangType<?>> typeList = yangTypeHolder.getTypeList();
        if (yangTypeHolder instanceof YangUnion) {
            verifyUnionTypes(typeList, yangTypeHolder);
        }
        addIdentityToImport(typeList, config);
        if (typeList != null) {
            List<YangType<?>> types = validateTypes(typeList);
            for (YangType<?> type : types) {
                if (!(type instanceof YangJavaTypeTranslator)) {
                    throw new TranslatorException(
                            "Type does not have Java info " + type
                                    .getDataTypeName() + " in " + type
                                    .getLineNumber() + " at " + type
                                    .getCharPosition() + " in " +
                                    type.getFileName());
                }
                JavaAttributeInfo javaAttributeInfo = getAttributeForType(type,
                                                                          config);
                if (yangTypeHolder instanceof YangTypeDef) {
                    javaAttributeInfo.setCurHolderOrCount(HOLDER_TYPE_DEF);
                } else if (types.indexOf(type) == types.size() - 1) {
                    javaAttributeInfo.setCurHolderOrCount(LAST);
                }
                if (type.getDataType() == BITS) {
                    addBitsHandler(javaAttributeInfo, type, this);
                }
                if (type.getDataType() == BINARY) {
                    JavaQualifiedTypeInfoTranslator info = new
                            JavaQualifiedTypeInfoTranslator();
                    info.setClassInfo(BASE64);
                    info.setPkgInfo(JAVA_UTIL_PKG);
                    getJavaImportData().addImportInfo(
                            info, getGeneratedJavaClassName(), getJavaFileInfo()
                                    .getPackage());
                }
                addJavaSnippetInfoToApplicableTempFiles(javaAttributeInfo,
                                                        config, types);
            }
            addTypeConstructor();
            addMethodsInConflictCase(config);
            for (JavaAttributeInfo attr : attrs) {
                super.addJavaSnippetInfoToApplicableTempFiles(attr, config);
            }
        }
    }

    //TODO: Union with two identities code generation has to be changed.
    private void addIdentityToImport(List<YangType<?>> types,
                                     YangPluginConfig config) {
        for (YangType<?> type : types) {
            Object ex = type.getDataTypeExtendedInfo();
            if (ex != null && ex instanceof YangIdentityRef) {
                YangIdentity base = ((YangIdentityRef) ex).getReferredIdentity();
                getAttributeForType(type, config);
                List<YangIdentity> idList = base.getExtendList();
                for (YangIdentity id : idList) {
                    JavaQualifiedTypeInfoTranslator derPkgInfo =
                            getDerivedPkfInfo(id);
                    getIsQualifiedAccessOrAddToImportList(derPkgInfo);
                }
            }
        }
    }

    /**
     * Returns java attribute.
     *
     * @param type   YANG type
     * @param config plugin configurations
     * @return java attribute
     */
    private JavaAttributeInfo getAttributeForType(YangType type,
                                                  YangPluginConfig config) {
        YangJavaTypeTranslator javaType = (YangJavaTypeTranslator) type;
        javaType.updateJavaQualifiedInfo(config.getConflictResolver());
        String typeName = getCamelCase(javaType.getDataTypeName(), config
                .getConflictResolver());
        return getAttributeInfoForTheData(
                javaType.getJavaQualifiedInfo(), typeName, javaType,
                getIsQualifiedAccessOrAddToImportList(
                        javaType.getJavaQualifiedInfo()), false);
    }

    /**
     * Adds the new attribute info to the target generated temporary files for union class.
     *
     * @param attr   the attribute info that needs to be added to temporary files
     * @param config plugin configurations
     * @param types  type list
     * @throws IOException IO operation fail
     */
    private void addJavaSnippetInfoToApplicableTempFiles(
            JavaAttributeInfo attr, YangPluginConfig config, List<YangType<?>> types)
            throws IOException {

        YangDataTypes attrType = attr.getAttributeType().getDataType();

        if (attrType == INT16 || attrType == UINT8) {
            boolean isShortConflict = validateForConflictingShortTypes(types);
            attr.setShortConflict(isShortConflict);
            updateAttributeCondition(attr);
            if (!isShortConflict) {
                addMethodsWhenNoConflictingTypes(attr, config, types);
            }
        } else if (attrType == INT32 || attrType == UINT16) {
            boolean isIntConflict = validateForConflictingIntTypes(types);
            attr.setIntConflict(isIntConflict);
            updateAttributeCondition(attr);
            if (!isIntConflict) {
                addMethodsWhenNoConflictingTypes(attr, config, types);
            }
        } else if (attrType == INT64 || attrType == UINT32) {
            boolean isLongConflict = validateForConflictingLongTypes(types);
            attr.setLongConflict(isLongConflict);
            updateAttributeCondition(attr);
            if (!isLongConflict) {
                addMethodsWhenNoConflictingTypes(attr, config, types);
            }
        } else {
            addMethodsWhenNoConflictingTypes(attr, config, types);
        }
        String attrHolder = attr.getCurHolderOrCount();
        if (attrHolder != null && !attrHolder.equals(HOLDER_TYPE_DEF)) {
            attrs.add(attr);
        } else {
            super.addJavaSnippetInfoToApplicableTempFiles(attr, config);
        }
    }

    private List<YangType<?>> validateTypes(List<YangType<?>> types) {
        String curType;
        List<String> preType = new ArrayList<>();
        for (YangType type : types) {
            curType = type.getDataTypeName();
            if (!preType.contains(curType)) {
                preType.add(curType);
                local.add(type);
            }
        }
        return local;
    }

    /**
     * Adds of method and constructor when there is no conflicting types.
     *
     * @param javaAttributeInfo java attribute info
     * @param pluginConfig      plugin configurations
     * @param types             YANG type
     * @throws IOException when fails to do IO operations
     */
    private void addMethodsWhenNoConflictingTypes(JavaAttributeInfo javaAttributeInfo,
                                                  YangPluginConfig pluginConfig, List<YangType<?>> types)
            throws IOException {
        if ((getGeneratedTempFiles() & OF_STRING_IMPL_MASK) != 0) {
            addOfStringMethod(javaAttributeInfo, pluginConfig);
        }

        if ((getGeneratedTempFiles() & CONSTRUCTOR_FOR_TYPE_MASK) != 0) {
            addTypeConstructor(javaAttributeInfo, types.indexOf(javaAttributeInfo.getAttributeType()));
        }
    }

    /**
     * Adds of, getter and from string method in conflict cases.
     *
     * @param pluginConfig plugin configurations
     * @throws IOException when fails to do IO operations
     */
    private void addMethodsInConflictCase(YangPluginConfig pluginConfig)
            throws IOException {
        JavaAttributeInfo attr = getIntAttribute();
        if (attr != null) {
            attr = getUIntAttribute();
        }
        if (attr != null) {
            if (attr.isIntConflict()) {
                if (getIntIndex() < getUIntIndex()) {
                    getIntAttribute().setCurHolderOrCount(
                            getUIntAttribute().getCurHolderOrCount());
                    appendToFile(getOfStringImplTempFileHandle(), getOfMethodStringAndJavaDoc(
                            getIntAttribute(), getGeneratedJavaClassName())
                            + NEW_LINE);
                    addGetterImpl(getIntAttribute());

                    addFromStringMethod(getIntAttribute(), pluginConfig);
                } else {
                    getUIntAttribute().setCurHolderOrCount(
                            getIntAttribute().getCurHolderOrCount());
                    appendToFile(getOfStringImplTempFileHandle(),
                                 getOfMethodStringAndJavaDoc(getUIntAttribute(),
                                                             getGeneratedJavaClassName())
                                         + NEW_LINE);
                    addGetterImpl(getUIntAttribute());
                    addFromStringMethod(getUIntAttribute(), pluginConfig);
                }
            }
        }
        attr = getLongAttribute();
        if (attr != null) {
            attr = getULongAttribute();
        }
        if (attr != null) {
            if (attr.isLongConflict()) {
                if (getLongIndex() < getULongIndex()) {
                    getLongAttribute().setCurHolderOrCount(
                            getULongAttribute().getCurHolderOrCount());
                    appendToFile(getOfStringImplTempFileHandle(),
                                 getOfMethodStringAndJavaDoc(getLongAttribute(),
                                                             getGeneratedJavaClassName())
                                         + NEW_LINE);
                    addGetterImpl(getLongAttribute());
                    addFromStringMethod(getLongAttribute(), pluginConfig);
                } else {
                    getULongAttribute().setCurHolderOrCount(
                            getLongAttribute().getCurHolderOrCount());
                    appendToFile(getOfStringImplTempFileHandle(),
                                 getOfMethodStringAndJavaDoc(getULongAttribute(),
                                                             getGeneratedJavaClassName())
                                         + NEW_LINE);
                    addGetterImpl(getULongAttribute());
                    addFromStringMethod(getULongAttribute(), pluginConfig);
                }
            }
        }

        attr = getShortAttribute();
        if (attr != null) {
            attr = getUInt8Attribute();
        }
        if (attr != null) {
            if (attr.isShortConflict()) {
                if (getShortIndex() < getUInt8Index()) {
                    getShortAttribute().setCurHolderOrCount(
                            getUInt8Attribute().getCurHolderOrCount());
                    appendToFile(getOfStringImplTempFileHandle(),
                                 getOfMethodStringAndJavaDoc(getShortAttribute(),
                                                             getGeneratedJavaClassName())
                                         + NEW_LINE);
                    addGetterImpl(getShortAttribute());
                    addFromStringMethod(getShortAttribute(), pluginConfig);
                } else {
                    getUInt8Attribute().setCurHolderOrCount(
                            getShortAttribute().getCurHolderOrCount());
                    appendToFile(getOfStringImplTempFileHandle(),
                                 getOfMethodStringAndJavaDoc(getUInt8Attribute(),
                                                             getGeneratedJavaClassName())
                                         + NEW_LINE);
                    addGetterImpl(getUInt8Attribute());
                    addFromStringMethod(getUInt8Attribute(), pluginConfig);
                }
            }
        }
    }

    /**
     * Adds from string method for conflict case.
     *
     * @param newAttrInfo  new attribute
     * @param pluginConfig plugin configurations
     * @throws IOException when fails to do IO operations
     */
    private void addFromStringMethod(JavaAttributeInfo newAttrInfo, YangPluginConfig pluginConfig)
            throws IOException {

        JavaQualifiedTypeInfoTranslator qualifiedInfoOfFromString =
                getQualifiedInfoOfFromString(newAttrInfo, pluginConfig.getConflictResolver());
            /*
             * Create a new java attribute info with qualified information of
             * wrapper classes.
             */
        JavaAttributeInfo fromStringAttributeInfo =
                getAttributeInfoForTheData(qualifiedInfoOfFromString,
                                           newAttrInfo.getAttributeName(),
                                           newAttrInfo.getAttributeType(),
                                           getIsQualifiedAccessOrAddToImportList(
                                                   qualifiedInfoOfFromString), false);

        addFromStringMethod(newAttrInfo, fromStringAttributeInfo);
    }

    /**
     * Adds type constructor.
     *
     * @param attr  attribute info
     * @param count count of types
     * @throws IOException when fails to append to temporary file
     */
    private void addTypeConstructor(JavaAttributeInfo attr, int count)
            throws IOException {
        appendToFile(getConstructorForTypeTempFileHandle(),
                     getTypeConstructorStringAndJavaDoc(
                             attr, getGeneratedJavaClassName(), getGeneratedJavaFiles(), count)
                             + NEW_LINE);
    }

    /**
     * Adds type constructor.
     *
     * @throws IOException when fails to append to temporary file
     */
    private void addTypeConstructor()
            throws IOException {
        JavaAttributeInfo attr = getIntAttribute();
        if (attr != null) {
            attr = getUIntAttribute();
        }
        boolean index = getIntIndex() < getUIntIndex();
        int count;
        if (index) {
            count = getIntIndex();
        } else {
            count = getUIntIndex();
        }
        if (attr != null) {
            if (attr.isIntConflict()) {
                appendToFile(getConstructorForTypeTempFileHandle(),
                             getTypeConstructorStringAndJavaDoc(
                                     getIntAttribute(),
                                     getUIntAttribute(), getGeneratedJavaClassName(),
                                     INT_TYPE_CONFLICT,
                                     index, count) + NEW_LINE);
            }
        }
        attr = getLongAttribute();
        if (attr != null) {
            attr = getULongAttribute();
        }
        index = getLongIndex() < getULongIndex();
        if (index) {
            count = getLongIndex();
        } else {
            count = getULongIndex();
        }
        if (attr != null) {
            if (attr.isLongConflict()) {
                appendToFile(getConstructorForTypeTempFileHandle(),
                             getTypeConstructorStringAndJavaDoc(
                                     getLongAttribute(),
                                     getULongAttribute(), getGeneratedJavaClassName(),
                                     LONG_TYPE_CONFLICT,
                                     index, count) + NEW_LINE);
            }
        }
        attr = getShortAttribute();
        if (attr != null) {
            attr = getUInt8Attribute();
        }
        index = getShortIndex() < getUInt8Index();
        if (index) {
            count = getShortIndex();
        } else {
            count = getUInt8Index();
        }
        if (attr != null) {
            if (attr.isShortConflict()) {
                appendToFile(getConstructorForTypeTempFileHandle(),
                             getTypeConstructorStringAndJavaDoc(
                                     getShortAttribute(),
                                     getUInt8Attribute(), getGeneratedJavaClassName(),
                                     SHORT_TYPE_CONFLICT,
                                     index, count) + NEW_LINE);
            }
        }
    }

    /**
     * Adds of string for type.
     *
     * @param attr         attribute info
     * @param pluginConfig plugin configurations
     * @throws IOException when fails to append to temporary file
     */
    private void addOfStringMethod(JavaAttributeInfo attr, YangPluginConfig pluginConfig)
            throws IOException {
        appendToFile(getOfStringImplTempFileHandle(),
                     getOfMethodStringAndJavaDoc(attr,
                                                 getGeneratedJavaClassName())
                             + NEW_LINE);
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

        if ((getGeneratedJavaFiles() & GENERATE_TYPEDEF_CLASS) != 0 ||
                (getGeneratedJavaFiles() & GENERATE_UNION_CLASS) != 0) {
            closeFile(typeClassJavaFileHandle, isErrorOccurred);
        }

        if ((getGeneratedTempFiles() & CONSTRUCTOR_FOR_TYPE_MASK) != 0) {
            closeFile(constructorForTypeTempFileHandle, true);
        }
        if ((getGeneratedTempFiles() & OF_STRING_IMPL_MASK) != 0) {
            closeFile(ofStringImplTempFileHandle, true);
        }
        if ((getGeneratedTempFiles() & FROM_STRING_IMPL_MASK) != 0) {
            closeFile(getFromStringImplTempFileHandle(), true);
        }

        super.freeTemporaryResources(isErrorOccurred);
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
        List<String> imports = new ArrayList<>();
        if (isAttributePresent()) {
            imports = getJavaImportData().getImports(true);
        }

        createPackage(curNode);
        imports.add(getJavaImportData().getImportForHashAndEquals());

        /*
         * Creates type def class file.
         */
        if ((fileType & GENERATE_TYPEDEF_CLASS) != 0) {
            setTypedefClassJavaFileHandle(getJavaFileHandle(
                    getJavaClassName(EMPTY_STRING)));
            generateTypeDefClassFile(getTypedefClassJavaFileHandle(), curNode, imports);
        }
        String bit = getJavaImportData().getImportForToBitSet();
        if (!imports.contains(bit)) {
            imports.add(bit);
        }
        /*
         * Creates type class file.
         */
        if ((fileType & GENERATE_UNION_CLASS) != 0) {
            setTypeClassJavaFileHandle(getJavaFileHandle(
                    getJavaClassName(EMPTY_STRING)));
            generateUnionClassFile(getTypeClassJavaFileHandle(), curNode, imports);
        }

        /*
         * Close all the file handles.
         */
        freeTemporaryResources(false);
    }

    /**
     * Returns short type index from type list.
     *
     * @return short type index from type list
     */
    public int getShortIndex() {
        return shortIndex;
    }

    /**
     * Sets short type index from type list.
     *
     * @param shortIndex short type index from type list.
     */
    private void setShortIndex(int shortIndex) {
        this.shortIndex = shortIndex;
    }

    /**
     * Returns uInt type index from type list.
     *
     * @return uInt type index from type list
     */
    public int getUInt8Index() {
        return uInt8Index;
    }

    /**
     * Sets uInt8 type index from type list.
     *
     * @param uInt8Index uInt8 type index from type list.
     */
    private void setUInt8Index(int uInt8Index) {
        this.uInt8Index = uInt8Index;
    }

    /**
     * Returns int type index from type list.
     *
     * @return int type index from type list
     */
    public int getIntIndex() {
        return intIndex;
    }

    /**
     * Sets int type index from type list.
     *
     * @param intIndex int type index from type list.
     */
    private void setIntIndex(int intIndex) {
        this.intIndex = intIndex;
    }

    /**
     * Returns uInt type index from type list.
     *
     * @return uInt type index from type list
     */
    public int getUIntIndex() {
        return uIntIndex;
    }

    /**
     * Sets uInt type index from type list.
     *
     * @param uIntIndex uInt type index from type list.
     */
    private void setUIntIndex(int uIntIndex) {
        this.uIntIndex = uIntIndex;
    }

    /**
     * Returns long type index from type list.
     *
     * @return long type index from type list
     */
    public int getLongIndex() {
        return longIndex;
    }

    /**
     * Sets long type index from type list.
     *
     * @param longIndex long type index from type list.
     */
    private void setLongIndex(int longIndex) {
        this.longIndex = longIndex;
    }

    /**
     * Returns uLong type index from type list.
     *
     * @return uLong type index from type list
     */
    public int getULongIndex() {
        return uLongIndex;
    }

    /**
     * Sets uLong type index from type list.
     *
     * @param uLongIndex uLong type index from type list.
     */
    private void setULongIndex(int uLongIndex) {
        this.uLongIndex = uLongIndex;
    }

    /**
     * Validates conflict for int and uInt.
     *
     * @param typeList type list
     * @return true if conflict is there
     */
    private boolean validateForConflictingIntTypes(List<YangType<?>> typeList) {
        boolean isIntPresent = false;
        boolean isUIntPresent = false;
        for (YangType type : typeList) {
            if (type.getDataType().equals(INT32)) {
                setIntIndex(typeList.indexOf(type));
                isIntPresent = true;
            }
            if (type.getDataType().equals(UINT16)) {
                setUIntIndex(typeList.indexOf(type));
                isUIntPresent = true;
            }
        }

        return isIntPresent && isUIntPresent;
    }

    /**
     * Validates conflict for int and uInt.
     *
     * @param typeList type list
     * @return true if conflict is there
     */
    private boolean validateForConflictingShortTypes(List<YangType<?>> typeList) {
        boolean isShortPresent = false;
        boolean isUInt8Present = false;
        for (YangType type : typeList) {
            if (type.getDataType().equals(INT16)) {
                setShortIndex(typeList.indexOf(type));
                isShortPresent = true;
            }
            if (type.getDataType().equals(UINT8)) {
                setUInt8Index(typeList.indexOf(type));
                isUInt8Present = true;
            }
        }

        return isShortPresent && isUInt8Present;
    }

    /**
     * Validates conflict for long and uLong.
     *
     * @param typeList type list
     * @return true if conflict is there
     */
    private boolean validateForConflictingLongTypes(List<YangType<?>> typeList) {
        boolean isLongPresent = false;
        boolean isULongPresent = false;
        for (YangType type : typeList) {
            if (type.getDataType().equals(INT64)) {
                setLongIndex(typeList.indexOf(type));
                isLongPresent = true;
            }
            if (type.getDataType().equals(UINT32)) {
                setULongIndex(typeList.indexOf(type));
                isULongPresent = true;
            }
        }

        return isLongPresent && isULongPresent;
    }

    /**
     * Updates attribute info in case of conflicts.
     *
     * @param javaAttributeInfo java attribute info
     */
    private void updateAttributeCondition(JavaAttributeInfo javaAttributeInfo) {

        if (javaAttributeInfo.isIntConflict()) {
            if (javaAttributeInfo.getAttributeType().getDataType() == UINT16) {
                setUIntAttribute(javaAttributeInfo);
            } else if (javaAttributeInfo.getAttributeType().getDataType() == INT32) {
                setIntAttribute(javaAttributeInfo);
            }
        }
        if (javaAttributeInfo.isLongConflict()) {
            if (javaAttributeInfo.getAttributeType().getDataType() == UINT32) {
                setULongAttribute(javaAttributeInfo);
            } else if (javaAttributeInfo.getAttributeType().getDataType() == INT64) {
                setLongAttribute(javaAttributeInfo);
            }
        }
        if (javaAttributeInfo.isShortConflict()) {
            if (javaAttributeInfo.getAttributeType().getDataType() == UINT8) {
                setUInt8Attribute(javaAttributeInfo);
            } else if (javaAttributeInfo.getAttributeType().getDataType() == INT16) {
                setShortAttribute(javaAttributeInfo);
            }
        }
    }

    /**
     * Returns attribute for int.
     *
     * @return attribute for int
     */
    public JavaAttributeInfo getIntAttribute() {
        return intAttribute;
    }

    /**
     * Sets attribute for int.
     *
     * @param intAttribute attribute for int
     */
    private void setIntAttribute(JavaAttributeInfo intAttribute) {
        this.intAttribute = intAttribute;
    }

    /**
     * Returns attribute for long.
     *
     * @return attribute for long
     */
    public JavaAttributeInfo getLongAttribute() {
        return longAttribute;
    }

    /**
     * Sets attribute for long.
     *
     * @param longAttribute attribute for long
     */
    private void setLongAttribute(JavaAttributeInfo longAttribute) {
        this.longAttribute = longAttribute;
    }

    /**
     * Returns attribute for uInt.
     *
     * @return attribute for uInt
     */
    public JavaAttributeInfo getUIntAttribute() {
        return uIntAttribute;
    }

    /**
     * Sets attribute for uInt.
     *
     * @param uIntAttribute attribute for uInt
     */
    private void setUIntAttribute(JavaAttributeInfo uIntAttribute) {
        this.uIntAttribute = uIntAttribute;
    }

    /**
     * Returns attribute for uLong.
     *
     * @return attribute for uLong
     */
    public JavaAttributeInfo getULongAttribute() {
        return uLongAttribute;
    }

    /**
     * Sets attribute for uLong.
     *
     * @param uLongAttribute attribute for uLong
     */
    private void setULongAttribute(JavaAttributeInfo uLongAttribute) {
        this.uLongAttribute = uLongAttribute;
    }

    /**
     * Returns attribute for uInt8.
     *
     * @return attribute for uInt8
     */
    public JavaAttributeInfo getUInt8Attribute() {
        return uInt8Attribute;
    }

    /**
     * Sets attribute for uInt8.
     *
     * @param uInt8Attribute attribute for uInt8
     */
    private void setUInt8Attribute(JavaAttributeInfo uInt8Attribute) {
        this.uInt8Attribute = uInt8Attribute;
    }

    /**
     * Returns attribute for short.
     *
     * @return attribute for short
     */
    public JavaAttributeInfo getShortAttribute() {
        return shortAttribute;
    }

    /**
     * Sets attribute for short.
     *
     * @param shortAttribute attribute for short
     */
    private void setShortAttribute(JavaAttributeInfo shortAttribute) {
        this.shortAttribute = shortAttribute;
    }
}
