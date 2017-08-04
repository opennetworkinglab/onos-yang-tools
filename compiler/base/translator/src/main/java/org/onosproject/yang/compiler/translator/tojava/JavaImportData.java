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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableSortedSet;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getImportString;
import static org.onosproject.yang.compiler.translator.tojava.utils.StringGenerator.getStaticImportString;
import static org.onosproject.yang.compiler.utils.UtilConstants.ABSTRACT_EVENT;
import static org.onosproject.yang.compiler.utils.UtilConstants.ARRAY_LIST_IMPORT;
import static org.onosproject.yang.compiler.utils.UtilConstants.BIG_INTEGER;
import static org.onosproject.yang.compiler.utils.UtilConstants.BITSET;
import static org.onosproject.yang.compiler.utils.UtilConstants.COLLECTION_IMPORTS;
import static org.onosproject.yang.compiler.utils.UtilConstants.DATA_NODE;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.EVENT_LISTENER;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXECUTORS;
import static org.onosproject.yang.compiler.utils.UtilConstants.EXECUTOR_SERVICE;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_LANG;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_MATH;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_UTIL_CONCURRENT_PKG;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_UTIL_OBJECTS_IMPORT_CLASS;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_UTIL_PKG;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_UTIL_REGEX_PKG;
import static org.onosproject.yang.compiler.utils.UtilConstants.LINKED_LIST;
import static org.onosproject.yang.compiler.utils.UtilConstants.LIST;
import static org.onosproject.yang.compiler.utils.UtilConstants.LISTENER_SERVICE;
import static org.onosproject.yang.compiler.utils.UtilConstants.MAP;
import static org.onosproject.yang.compiler.utils.UtilConstants.MODEL_CONVERTER;
import static org.onosproject.yang.compiler.utils.UtilConstants.MODEL_PKG;
import static org.onosproject.yang.compiler.utils.UtilConstants.ONOS_EVENT_PKG;
import static org.onosproject.yang.compiler.utils.UtilConstants.PATTERN;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUEUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.RESOURCE_ID;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_COMMAND;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_HANDLER;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_INPUT;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_OUTPUT;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_OUTPUT_STATUS_PKG;
import static org.onosproject.yang.compiler.utils.UtilConstants.RPC_SUCCESS;
import static org.onosproject.yang.compiler.utils.UtilConstants.SET;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_RPC_SERVICE;

/**
 * Represents that generated Java file can contain imports.
 */
public class JavaImportData {

    /**
     * Flag to denote if any list in imported.
     */
    private boolean isListToImport;
    /**
     * Flag to denote if any queue is imported due to compiler annotation.
     */
    private boolean isQueueToImport;
    /**
     * Flag to denote if any set is imported due to compiler annotation.
     */
    private boolean isSetToImport;
    /**
     * Flag to denote if any map is imported due to compiler annotation.
     */
    private boolean isMapToImport;

    /**
     * Sorted set of import info, to be used to maintain the set of classes to
     * be imported in the generated class.
     */
    private final SortedSet<JavaQualifiedTypeInfoTranslator> importSet;

    /**
     * Creates java import data object.
     */
    public JavaImportData() {
        importSet = new TreeSet<>();
    }


    /**
     * Sets the status of importing list.
     *
     * @param isList status to mention list is bing imported
     */
    void setIfListImported(boolean isList) {
        isListToImport = isList;
    }


    /**
     * Sets the status of the queue to be imported due to compiler annotations.
     *
     * @param queueToImport status of queue to import
     */
    void setQueueToImport(boolean queueToImport) {
        isQueueToImport = queueToImport;
    }

    /**
     * Sets the status of the set to be imported due to compiler annotations.
     *
     * @param setToImport status of set to import
     */
    void setSetToImport(boolean setToImport) {
        isSetToImport = setToImport;
    }

    /**
     * Sets true if map is imported due to compiler annotations.
     *
     * @param mapToImport true if map is imported due to compiler annotations
     */
    void setMapToImport(boolean mapToImport) {
        isMapToImport = mapToImport;
    }

    /**
     * Returns the set containing the imported class/interface info.
     *
     * @return the set containing the imported class/interface info
     */
    public SortedSet<JavaQualifiedTypeInfoTranslator> getImportSet() {
        return unmodifiableSortedSet(importSet);
    }

    /**
     * Adds an imported class/interface info if it is not already part of the
     * collection.
     * <p>
     * If already part of the collection, check if the packages are same, if so
     * then return true, to denote it is already in the import collection, and
     * it can be accessed without qualified access. If the packages do not
     * match, then do not add to the import collection, and return false to
     * denote, it is not added to import collection and needs to be accessed in
     * a qualified manner.
     *
     * @param newImportInfo class/interface info being imported
     * @param className     name of the call being generated
     * @param classPkg      generated class package
     * @return qualified access status of the import node being added
     */
    public boolean addImportInfo(JavaQualifiedTypeInfoTranslator newImportInfo,
                                 String className, String classPkg) {

        if (newImportInfo.getClassInfo().contentEquals(className)) {
            /*
             * If the current class name is same as the attribute class name,
             * then the attribute must be accessed in a qualified manner.
             */
            return true;
        } else if (newImportInfo.getPkgInfo() == null) {
            /*
             * If the package info is null, then it is not a candidate for import
              * / qualified access
             */
            return false;
        }

        /*
         * If the attribute type is having the package info, it is contender
         * for import list and also need to check if it needs to be a
         * qualified access.
         */
        if (newImportInfo.getPkgInfo().contentEquals(classPkg)) {
            /*
             * Package of the referred attribute and the generated class is same,
              * so no need import
             * or qualified access.
             */
            return false;
        }

        for (JavaQualifiedTypeInfoTranslator curImportInfo : getImportSet()) {
            if (curImportInfo.getClassInfo()
                    .contentEquals(newImportInfo.getClassInfo())) {
                return !curImportInfo.getPkgInfo()
                        .contentEquals(newImportInfo.getPkgInfo());
            }
        }

        /*
         * Import is added, so it is a member for non qualified access
         */
        importSet.add(newImportInfo);
        return false;
    }

    /**
     * Removes the import info from importSet.
     *
     * @param importInfo import info to be removed
     */
    public void removeFromImportData(JavaQualifiedTypeInfoTranslator
                                             importInfo) {
        Iterator<JavaQualifiedTypeInfoTranslator> it = getImportSet().iterator();
        while (it.hasNext()) {
            JavaQualifiedTypeInfoTranslator info = it.next();
            if (info.getClassInfo().contentEquals(importInfo.getClassInfo())
                    && info.getPkgInfo()
                    .contentEquals(importInfo.getPkgInfo())) {
                importSet.remove(importInfo);
                return;
            }
        }
    }

    /**
     * Returns import for class.
     *
     * @param isForInterface if needs to check for interface
     * @return imports for class
     */
    public List<String> getImports(boolean isForInterface) {

        String importString;
        List<String> imports = new ArrayList<>();
        boolean check;
        for (JavaQualifiedTypeInfoTranslator importInfo : getImportSet()) {
            check = importInfo.isForInterface();
            if (!isForInterface) {
                check = true;
            }
            if (!importInfo.getPkgInfo().equals(EMPTY_STRING) &&
                    importInfo.getClassInfo() != null &&
                    !importInfo.getPkgInfo().equals(JAVA_LANG) &&
                    check) {
                importString = getImportString(importInfo.getPkgInfo(), importInfo
                        .getClassInfo());
                imports.add(importString);
            }
        }
        if (isListToImport) {
            imports.add(getImportForList());
            if (!isForInterface) {
                imports.add(ARRAY_LIST_IMPORT);
            }
        }
        if (isQueueToImport) {
            imports.add(getImportForQueue());
        }
        if (isSetToImport) {
            imports.add(getImportForSet());
        }
        if (isMapToImport) {
            imports.add(getImportForMap());
        }

        sort(imports);
        return imports;
    }

    /**
     * Returns import for hash and equals method.
     *
     * @return import for hash and equals method
     */
    String getImportForHashAndEquals() {
        return getImportString(JAVA_UTIL_PKG,
                               JAVA_UTIL_OBJECTS_IMPORT_CLASS);
    }

    /**
     * Returns import for to bitset method.
     *
     * @return import for to bitset method
     */
    public String getImportForToBitSet() {
        return getImportString(JAVA_UTIL_PKG, BITSET);
    }

    /**
     * Returns import for to bitset method.
     *
     * @return import for to bitset method
     */
    public String getImportForPattern() {
        return getImportString(JAVA_UTIL_REGEX_PKG, PATTERN);
    }

    /**
     * Returns import for list attribute.
     *
     * @return import for list attribute
     */
    public String getImportForList() {
        return getImportString(COLLECTION_IMPORTS, LIST);
    }

    /**
     * Returns import for map attribute.
     *
     * @return import for map attribute
     */
    private String getImportForMap() {
        return getImportString(COLLECTION_IMPORTS, MAP);
    }

    /**
     * Returns import for queue attribute.
     *
     * @return import for queue attribute
     */
    private String getImportForQueue() {
        return getImportString(COLLECTION_IMPORTS, QUEUE);
    }

    /**
     * Returns import for set attribute.
     *
     * @return import for set attribute
     */
    private String getImportForSet() {
        return getImportString(COLLECTION_IMPORTS, SET);
    }

    /**
     * Returns import string for ListenerService class.
     *
     * @return import string for ListenerService class
     */
    public String getListenerServiceImport() {
        return getImportString(ONOS_EVENT_PKG, LISTENER_SERVICE);
    }

    /**
     * Returns import string for AbstractEvent class.
     *
     * @return import string for AbstractEvent class
     */
    String getAbstractEventsImport() {
        return getImportString(ONOS_EVENT_PKG, ABSTRACT_EVENT);
    }

    /**
     * Returns import string for EventListener class.
     *
     * @return import string for EventListener class
     */
    String getEventListenerImport() {
        return getImportString(ONOS_EVENT_PKG, EVENT_LISTENER);
    }

    /**
     * Returns import for big integer.
     *
     * @return import for big integer
     */
    public String getBigIntegerImport() {
        return getImportString(JAVA_MATH, BIG_INTEGER);
    }

    /**
     * Returns import for executor service.
     *
     * @return import for executor service
     */
    public String getExecutorServiceImport() {
        return getImportString(JAVA_UTIL_CONCURRENT_PKG, EXECUTOR_SERVICE);
    }

    /**
     * Returns import for executor.
     *
     * @return import for executor
     */
    public String getExecutorsImport() {
        return getImportString(JAVA_UTIL_CONCURRENT_PKG, EXECUTORS);
    }

    /**
     * Returns import for RPC command.
     *
     * @return import for RPC command
     */
    public String getImportForRpcCommand() {
        return getImportString(MODEL_PKG, RPC_COMMAND);
    }

    /**
     * Returns import for RPC handler.
     *
     * @return import for RPC handler
     */
    public String getImportForRpcHandler() {
        return getImportString(MODEL_PKG, RPC_HANDLER);
    }

    /**
     * Returns import for dynamic store service.
     *
     * @return import for dynamic store service
     */
    public String getImportForDynamicStoreService() {
        return getImportString(MODEL_PKG, YANG_RPC_SERVICE);
    }

    /**
     * Returns import for data node.
     *
     * @return import for data node
     */
    public String getImportForDataNode() {
        return getImportString(MODEL_PKG, DATA_NODE);
    }

    /**
     * Returns import for RPC success.
     *
     * @return import for RPC success
     */
    public String getImportForRpcSuccess() {
        return getStaticImportString(RPC_OUTPUT_STATUS_PKG, RPC_SUCCESS);
    }

    /**
     * Returns import for RPC input.
     *
     * @return import for RPC input
     */
    public String getImportForRpcInput() {
        return getImportString(MODEL_PKG, RPC_INPUT);
    }

    /**
     * Returns import for RPC output.
     *
     * @return import for RPC output
     */
    public String getImportForRpcOutput() {
        return getImportString(MODEL_PKG, RPC_OUTPUT);
    }

    /**
     * Returns import for model converter.
     *
     * @return import for model converter
     */
    public String getImportForModelConverter() {
        return getImportString(MODEL_PKG, MODEL_CONVERTER);
    }

    /**
     * Returns import for resource id.
     *
     * @return import for resource id
     */
    public String getImportForResourceId() {
        return getImportString(MODEL_PKG, RESOURCE_ID);
    }

    /**
     * Returns import for linked list attribute.
     *
     * @return import for linked list attribute
     */
    public String getImportForLinkedList() {
        return getImportString(COLLECTION_IMPORTS, LINKED_LIST);
    }

}
