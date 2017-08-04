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
import org.onosproject.yang.compiler.datamodel.YangTypeHolder;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;

import java.io.IOException;

import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_ALL_EVENT_CLASS_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_ALL_RPC_CLASS_MASK;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_ENUM_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_INTERFACE_WITH_BUILDER;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_RPC_COMMAND_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_TYPE_CLASS;

/**
 * Represents implementation of java code fragments temporary implementations.
 * Contains fragment file object of different types of java file.
 * Uses required object(s) to generate the target java file(s).
 */
public class TempJavaCodeFragmentFiles {

    /**
     * Has the temporary files required for bean generated classes.
     */
    private TempJavaBeanFragmentFiles beanTempFiles;

    /**
     * Has the temporary files required for bean generated classes.
     */
    private TempJavaTypeFragmentFiles typeTempFiles;

    /**
     * Has the temporary files required for service generated classes.
     */
    private TempJavaServiceFragmentFiles serviceTempFiles;

    /**
     * Has the temporary files required for enumeration generated classes.
     */
    private TempJavaEnumerationFragmentFiles enumTempFiles;

    /**
     * Has the temporary files required for enumeration generated classes.
     */
    private TempJavaEventFragmentFiles eventTempFiles;

    /**
     * Has the temporary files required for RPC generated classes.
     */
    private TempJavaRpcFragmentFiles rpcTempFiles;

    /**
     * Has the temporary files required for RPC command generated classes.
     */
    private TempJavaRpcCommandFragmentFiles rpcCommandTempFiles;

    /**
     * Creates an instance of temporary java code fragment.
     *
     * @param javaFileInfo generated java file info
     * @throws IOException when fails to create new file handle
     */
    public TempJavaCodeFragmentFiles(JavaFileInfoTranslator javaFileInfo)
            throws IOException {

        int genType = javaFileInfo.getGeneratedFileTypes();
        if ((genType & GENERATE_INTERFACE_WITH_BUILDER) != 0) {
            beanTempFiles = new TempJavaBeanFragmentFiles(javaFileInfo);
        }

        if ((genType & GENERATE_TYPE_CLASS) != 0) {
            typeTempFiles = new TempJavaTypeFragmentFiles(javaFileInfo);
        }

        if ((genType & GENERATE_ENUM_CLASS) != 0) {
            enumTempFiles = new TempJavaEnumerationFragmentFiles(javaFileInfo);
        }

        if ((genType & GENERATE_SERVICE_AND_MANAGER) != 0) {
            serviceTempFiles = new TempJavaServiceFragmentFiles(javaFileInfo);
        }

        if ((genType & GENERATE_ALL_EVENT_CLASS_MASK) != 0) {
            eventTempFiles = new TempJavaEventFragmentFiles(javaFileInfo);
        }

        if ((genType & GENERATE_ALL_RPC_CLASS_MASK) != 0) {
            rpcTempFiles = new TempJavaRpcFragmentFiles(javaFileInfo);
        }

        if ((genType & GENERATE_RPC_COMMAND_CLASS) != 0) {
            rpcCommandTempFiles = new TempJavaRpcCommandFragmentFiles(javaFileInfo);
        }
    }

    /**
     * Retrieves the temp file handle for bean file generation.
     *
     * @return temp file handle for bean file generation
     */
    public TempJavaBeanFragmentFiles getBeanTempFiles() {
        return beanTempFiles;
    }

    /**
     * Retrieves the temp file handle for data type file generation.
     *
     * @return temp file handle for data type file generation
     */
    public TempJavaTypeFragmentFiles getTypeTempFiles() {
        return typeTempFiles;
    }


    /**
     * Retrieves the temp file handle for service file generation.
     *
     * @return temp file handle for service file generation
     */
    public TempJavaServiceFragmentFiles getServiceTempFiles() {
        return serviceTempFiles;
    }

    /**
     * Retrieves the temp file handle for enumeration file generation.
     *
     * @return temp file handle for enumeration file generation
     */
    public TempJavaEnumerationFragmentFiles getEnumTempFiles() {
        return enumTempFiles;
    }

    /**
     * Retrieves the temp file handle for event file generation.
     *
     * @return temp file handle for enumeration file generation
     */
    public TempJavaEventFragmentFiles getEventFragmentFiles() {
        return eventTempFiles;
    }

    /**
     * Retrieves the temp file handle for RPC file generation.
     *
     * @return temp file handle for RPC file generation
     */
    public TempJavaRpcFragmentFiles getRpcFragmentFiles() {
        return rpcTempFiles;
    }

    /**
     * Retrieves the temp file handle for RPC command file generation.
     *
     * @return temp file handle for RPC command file generation
     */
    public TempJavaRpcCommandFragmentFiles getRpcCommandTempFiles() {
        return rpcCommandTempFiles;
    }

    /**
     * Constructs java code exit.
     *
     * @param fileType generated file type
     * @param curNode  current YANG node
     * @throws IOException when fails to generate java files
     */
    public void generateJavaFile(int fileType, YangNode curNode)
            throws IOException {

        if ((fileType & GENERATE_INTERFACE_WITH_BUILDER) != 0) {
            beanTempFiles.generateJavaFile(fileType, curNode);
        }

        /*
         * Creates user defined data type class file.
         */
        if ((fileType & GENERATE_TYPE_CLASS) != 0) {
            typeTempFiles.generateJavaFile(fileType, curNode);
        }

        /*
         * Creates service and manager class file.
         */
        if (fileType == GENERATE_SERVICE_AND_MANAGER) {
            serviceTempFiles.generateJavaFile(GENERATE_SERVICE_AND_MANAGER, curNode);
        }

        /*
         * Creates event, event listener and event subject files.
         */
        if (fileType == GENERATE_ALL_EVENT_CLASS_MASK) {
            eventTempFiles.generateJavaFile(GENERATE_ALL_EVENT_CLASS_MASK, curNode);
        }

        /*
         * Creates enumeration class file.
         */
        if (fileType == GENERATE_ENUM_CLASS) {
            enumTempFiles.generateJavaFile(GENERATE_ENUM_CLASS, curNode);
        }

        /*
         * Creates RPC files.
         */
        if (fileType == GENERATE_ALL_RPC_CLASS_MASK) {
            rpcTempFiles.generateJavaFile(GENERATE_ALL_RPC_CLASS_MASK, curNode);
        }

        /*
         * Creates RPC command file.
         */
        if (fileType == GENERATE_RPC_COMMAND_CLASS) {
            rpcCommandTempFiles.generateJavaFile(GENERATE_RPC_COMMAND_CLASS, curNode);
        }
    }

    /**
     * Add all the type in the current data model node as part of the
     * generated temporary file.
     *
     * @param typeHolder YANG java data model node which has type info, eg union / typedef
     * @param config     plugin configurations for naming convention
     * @throws IOException IO operation fail
     */
    void addTypeInfoToTempFiles(YangTypeHolder typeHolder, YangPluginConfig config)
            throws IOException {
        typeTempFiles.addTypeInfoToTempFiles(typeHolder, config);
    }

    /**
     * Adds default constructor for class.
     *
     * @param modifier modifier for constructor.
     * @param toAppend string which need to be appended with the class name
     * @return default constructor for class
     * @throws IOException when fails to append to file
     */
    public String addDefaultConstructor(String modifier, String toAppend)
            throws IOException {
        boolean isSuffix = false;

        if (typeTempFiles != null) {
            return typeTempFiles.addDefaultConstructor(modifier, toAppend,
                                                       isSuffix);
        }
        if (beanTempFiles != null) {
            return beanTempFiles.addDefaultConstructor(modifier, toAppend,
                                                       isSuffix);
        }

        throw new TranslatorException("default constructor should not be added");
    }

    /**
     * Removes all temporary file handles.
     * when translator fails to generate java files we need to close
     * all open file handles include temporary files and java files
     *
     * @param isErrorOccurred if error occurred
     * @throws IOException when failed to delete the temporary files
     */
    public void freeTemporaryResources(boolean isErrorOccurred)
            throws IOException {

        if (beanTempFiles != null) {
            beanTempFiles.freeTemporaryResources(isErrorOccurred);
        }

        if (typeTempFiles != null) {
            typeTempFiles.freeTemporaryResources(isErrorOccurred);
        }

        if (enumTempFiles != null) {
            enumTempFiles.freeTemporaryResources(isErrorOccurred);
        }

        if (serviceTempFiles != null) {
            serviceTempFiles.freeTemporaryResources(isErrorOccurred);
        }

        if (eventTempFiles != null) {
            eventTempFiles.freeTemporaryResources(isErrorOccurred);
        }

        if (rpcTempFiles != null) {
            rpcTempFiles.freeTemporaryResources(isErrorOccurred);
        }

        if (rpcCommandTempFiles != null) {
            rpcCommandTempFiles.freeTemporaryResources(isErrorOccurred);
        }
    }
}
