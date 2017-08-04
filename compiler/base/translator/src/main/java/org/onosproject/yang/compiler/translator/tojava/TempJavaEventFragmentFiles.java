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

import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.translator.tojava.utils.JavaExtendsListHolder;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_EVENT_SUBJECT_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.EVENT_ENUM_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.EVENT_METHOD_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.EVENT_SUBJECT_ATTRIBUTE_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.EVENT_SUBJECT_GETTER_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedTempFileType.EVENT_SUBJECT_SETTER_MASK;
import static org.onosproject.yang.compiler.translator.tojava.JavaAttributeInfo.getAttributeInfoForTheData;
import static org.onosproject.yang.compiler.translator.tojava.JavaQualifiedTypeInfoTranslator.getQualifiedTypeInfoOfCurNode;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGenerator.generateEventFile;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGenerator.generateEventListenerFile;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGenerator.generateEventSubjectFile;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGeneratorUtils.getFileObject;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaIdentifierSyntax.getEnumJavaAttribute;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getGetterForClass;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getSetterForClass;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMA;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.EVENT_LISTENER;
import static org.onosproject.yang.compiler.utils.UtilConstants.EVENT_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH;
import static org.onosproject.yang.compiler.utils.io.impl.FileSystemUtil.closeFile;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.GETTER_METHOD;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.MANAGER_SETTER_METHOD;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.enumJavaDocForInnerClass;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavaDoc;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getAbsolutePackagePath;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;

/**
 * Represent temporary java fragments for event files.
 */
public class TempJavaEventFragmentFiles
        extends TempJavaFragmentFiles {
    /**
     * File name for generated class file for special type like union, typedef suffix.
     */
    private static final String EVENT_SUBJECT_NAME_SUFFIX = "EventSubject";

    /**
     * File name for event enum temp file.
     */
    private static final String EVENT_ENUM_FILE_NAME = "EventEnum";

    /**
     * File name for event method temp file.
     */
    private static final String EVENT_METHOD_FILE_NAME = "EventMethod";

    /**
     * File name for event subject attribute temp file.
     */
    private static final String EVENT_SUBJECT_ATTRIBUTE_FILE_NAME
            = "EventSubjectAttribute";

    /**
     * File name for event subject getter temp file.
     */
    private static final String EVENT_SUBJECT_GETTER_FILE_NAME
            = "EventSubjectGetter";

    /**
     * File name for event subject setter temp file.
     */
    private static final String EVENT_SUBJECT_SETTER_FILE_NAME
            = "EventSubjectSetter";

    private static final String JAVA_FILE_EXTENSION = ".java";
    /**
     * Java file handle for event subject file.
     */
    private File eventSubjectJavaFileHandle;

    /**
     * Java file handle for event listener file.
     */
    private File eventListenerJavaFileHandle;

    /**
     * Java file handle for event file.
     */
    private File eventJavaFileHandle;

    /**
     * Java file handle for event enum impl file.
     */
    private final File eventEnumTempFileHandle;
    /**
     * Java file handle for event method impl file.
     */
    private final File eventMethodTempFileHandle;
    /**
     * Java file handle for event subject attribute file.
     */
    private final File eventSubjectAttributeTempFileHandle;
    /**
     * Java file handle for event subject getter impl file.
     */
    private final File eventSubjectGetterTempFileHandle;
    /**
     * Java file handle for event subject setter impl file.
     */
    private final File eventSubjectSetterTempFileHandle;

    /**
     * Creates an instance of temporary java code fragment.
     *
     * @param javaFileInfo generated file information
     * @throws IOException when fails to create new file handle
     */
    TempJavaEventFragmentFiles(JavaFileInfoTranslator javaFileInfo)
            throws IOException {
        setJavaExtendsListHolder(new JavaExtendsListHolder());
        setJavaImportData(new JavaImportData());
        setJavaFileInfo(javaFileInfo);
        setAbsoluteDirPath(getAbsolutePackagePath(
                getJavaFileInfo().getBaseCodeGenPath(),
                getJavaFileInfo().getPackageFilePath()));

        addGeneratedTempFile(EVENT_ENUM_MASK);
        addGeneratedTempFile(EVENT_METHOD_MASK);
        addGeneratedTempFile(EVENT_SUBJECT_ATTRIBUTE_MASK);
        addGeneratedTempFile(EVENT_SUBJECT_GETTER_MASK);
        addGeneratedTempFile(EVENT_SUBJECT_SETTER_MASK);

        eventEnumTempFileHandle = getTemporaryFileHandle(EVENT_ENUM_FILE_NAME);
        eventMethodTempFileHandle = getTemporaryFileHandle(EVENT_METHOD_FILE_NAME);
        eventSubjectAttributeTempFileHandle = getTemporaryFileHandle(EVENT_SUBJECT_ATTRIBUTE_FILE_NAME);
        eventSubjectGetterTempFileHandle = getTemporaryFileHandle(EVENT_SUBJECT_GETTER_FILE_NAME);
        eventSubjectSetterTempFileHandle = getTemporaryFileHandle(EVENT_SUBJECT_SETTER_FILE_NAME);
    }

    /**
     * Returns event enum temp file.
     *
     * @return event enum temp file
     */
    public File getEventEnumTempFileHandle() {
        return eventEnumTempFileHandle;
    }

    /**
     * Returns event method temp file.
     *
     * @return event method temp file
     */
    public File getEventMethodTempFileHandle() {
        return eventMethodTempFileHandle;
    }

    /**
     * Returns event subject attribute temp file.
     *
     * @return event subject attribute temp file
     */
    public File getEventSubjectAttributeTempFileHandle() {
        return eventSubjectAttributeTempFileHandle;
    }

    /**
     * Returns event subject getter temp file.
     *
     * @return event subject getter temp file
     */
    public File getEventSubjectGetterTempFileHandle() {
        return eventSubjectGetterTempFileHandle;
    }

    /**
     * Returns event subject setter temp file.
     *
     * @return event subject setter temp file
     */
    public File getEventSubjectSetterTempFileHandle() {
        return eventSubjectSetterTempFileHandle;
    }

    /*Adds event method contents to event file.*/
    private static String getEventFileContents(String eventClassname, String classname) {
        return "\n" +
                "    /**\n" +
                "     * Creates " + classname + " event with type and subject.\n" +
                "     *\n" +
                "     * @param type event type\n" +
                "     * @param subject subject " + classname + "\n" +
                "     */\n" +
                "    public " + eventClassname + "(Type type, " +
                getCapitalCase(classname) + " subject) {\n" +
                "        super(type, subject);\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * Creates " + classname + " event with type, subject and time.\n" +
                "     *\n" +
                "     * @param type event type\n" +
                "     * @param subject subject " + classname + "\n" +
                "     * @param time time of event\n" +
                "     */\n" +
                "    public " + eventClassname + "(Type type, " + getCapitalCase(classname)
                + " subject, long time) {\n" +
                "        super(type, subject, time);\n" +
                "    }\n" +
                "\n";
    }

    public void generateJavaFile(int fileType, YangNode curNode)
            throws IOException {
        generateEventJavaFile(curNode);
        generateEventListenerJavaFile(curNode);
        generateEventSubjectJavaFile(curNode);

        // Close all the file handles.
        freeTemporaryResources(false);
    }

    /**
     * Constructs java code exit.
     *
     * @param curNode current YANG node
     * @throws IOException when fails to generate java files
     */
    private void generateEventJavaFile(YangNode curNode)
            throws IOException {

        List<String> imports = new ArrayList<>();

        imports.add(getJavaImportData().getAbstractEventsImport());
        String curNodeInfo = getCapitalCase(((JavaFileInfoContainer) curNode)
                                                    .getJavaFileInfo().getJavaName());
        String nodeName = curNodeInfo + EVENT_STRING;

        addEnumMethod(nodeName, curNodeInfo + EVENT_SUBJECT_NAME_SUFFIX);

        //Creates event interface file.
        eventJavaFileHandle = getJavaFileHandle(curNode, curNodeInfo +
                EVENT_STRING);
        generateEventFile(eventJavaFileHandle, curNode, imports);
    }

    /**
     * Constructs java code exit.
     *
     * @param curNode current YANG node
     * @throws IOException when fails to generate java files
     */
    private void generateEventListenerJavaFile(YangNode curNode)
            throws IOException {

        List<String> imports = new ArrayList<>();

        imports.add(getJavaImportData().getEventListenerImport());
        String curNodeInfo = getCapitalCase(((JavaFileInfoContainer) curNode)
                                                    .getJavaFileInfo().getJavaName());

        // Creates event listener interface file.
        eventListenerJavaFileHandle =
                getJavaFileHandle(curNode, curNodeInfo + EVENT_LISTENER);
        generateEventListenerFile(eventListenerJavaFileHandle, curNode, imports);
    }

    /**
     * Constructs java code exit.
     *
     * @param curNode current YANG node
     * @throws IOException when fails to generate java files
     */
    private void generateEventSubjectJavaFile(YangNode curNode)
            throws IOException {

        String curNodeInfo = getCapitalCase(((JavaFileInfoContainer) curNode)
                                                    .getJavaFileInfo().getJavaName());

        //Creates event interface file.
        eventSubjectJavaFileHandle = getJavaFileHandle(curNode, curNodeInfo +
                EVENT_SUBJECT_NAME_SUFFIX);
        generateEventSubjectFile(eventSubjectJavaFileHandle, curNode);
    }

    /**
     * Adds java snippet for events to event subject file.
     *
     * @param curNode      current node
     * @param pluginConfig plugin configurations
     * @throws IOException when fails to do IO operations
     */
    void addJavaSnippetOfEvent(YangNode curNode, YangPluginConfig pluginConfig)
            throws IOException {

        String currentInfo = getCamelCase(curNode.getName(), pluginConfig
                .getConflictResolver());
        String notificationName = curNode.getName();

        JavaQualifiedTypeInfoTranslator qualifiedTypeInfo
                = getQualifiedTypeInfoOfCurNode(curNode, getCapitalCase(currentInfo));

        JavaAttributeInfo javaAttributeInfo
                = getAttributeInfoForTheData(qualifiedTypeInfo, currentInfo,
                                             null, false, false);

        /*Adds java info for event in respective temp files.*/
        addEventEnum(notificationName);
        addEventSubjectAttribute(javaAttributeInfo);
        addEventSubjectGetter(javaAttributeInfo);
        addEventSubjectSetter(javaAttributeInfo);
    }

    /*Adds event to enum temp file.*/
    private void addEventEnum(String notificationName)
            throws IOException {
        appendToFile(getEventEnumTempFileHandle(), enumJavaDocForInnerClass(
                notificationName) + EIGHT_SPACE_INDENTATION
                + getEnumJavaAttribute(notificationName).toUpperCase() +
                COMMA + NEW_LINE);
    }

    /*Adds event method in event class*/
    private void addEnumMethod(String eventClassname, String className)
            throws IOException {
        appendToFile(getEventMethodTempFileHandle(),
                     getEventFileContents(eventClassname, className));
    }

    /*Adds events to event subject file.*/
    private void addEventSubjectAttribute(JavaAttributeInfo attr)
            throws IOException {
        appendToFile(getEventSubjectAttributeTempFileHandle(),
                     parseAttribute(attr));
    }

    /*Adds getter method for event in event subject class.*/
    private void addEventSubjectGetter(JavaAttributeInfo attr)
            throws IOException {
        String appDataStructure = null;
        if (attr.getCompilerAnnotation() != null) {
            appDataStructure = attr.getCompilerAnnotation()
                    .getYangAppDataStructure().getDataStructure().name();
        }
        appendToFile(getEventSubjectGetterTempFileHandle(), getJavaDoc(
                GETTER_METHOD, attr.getAttributeName(), false,
                appDataStructure) + getGetterForClass(
                attr, GENERATE_EVENT_SUBJECT_CLASS) + NEW_LINE);
    }

    /*Adds setter method for event in event subject class.*/
    private void addEventSubjectSetter(JavaAttributeInfo attr)
            throws IOException {
        String appDataStructure = null;
        if (attr.getCompilerAnnotation() != null) {
            appDataStructure = attr.getCompilerAnnotation()
                    .getYangAppDataStructure().getDataStructure().name();
        }
        appendToFile(getEventSubjectSetterTempFileHandle(), getJavaDoc(
                MANAGER_SETTER_METHOD, attr.getAttributeName(),
                false, appDataStructure) + getSetterForClass(
                attr, GENERATE_EVENT_SUBJECT_CLASS) + NEW_LINE);
    }

    /**
     * Returns a temporary file handle for the event's file type.
     *
     * @param name file name
     * @return temporary file handle
     */
    private File getJavaFileHandle(YangNode curNode, String name) {

        JavaFileInfoTranslator parentInfo = ((JavaFileInfoContainer) curNode)
                .getJavaFileInfo();
        return getFileObject(getDirPath(parentInfo), name, JAVA_FILE_EXTENSION,
                             parentInfo);
    }

    /**
     * Returns the directory path.
     *
     * @return directory path
     */
    private String getDirPath(JavaFileInfoTranslator parentInfo) {
        return (parentInfo.getPackageFilePath() + SLASH +
                parentInfo.getJavaName()).toLowerCase();
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

        closeFile(eventJavaFileHandle, isErrorOccurred);
        closeFile(eventListenerJavaFileHandle, isErrorOccurred);
        closeFile(eventSubjectJavaFileHandle, isErrorOccurred);

        closeFile(eventEnumTempFileHandle, true);
        closeFile(eventSubjectAttributeTempFileHandle, true);
        closeFile(eventMethodTempFileHandle, true);
        closeFile(eventSubjectGetterTempFileHandle, true);
        closeFile(eventSubjectSetterTempFileHandle, true);

        super.freeTemporaryResources(isErrorOccurred);
    }
}
