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

package org.onosproject.yang.compiler.linker.impl;

import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangReferenceResolver;
import org.onosproject.yang.compiler.datamodel.YangSubModule;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.linker.YangLinker;
import org.onosproject.yang.compiler.linker.exceptions.LinkerException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.sort;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_AUGMENT;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_BASE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_COMPILER_ANNOTATION;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_DERIVED_DATA_TYPE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_DEVIATION;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_IDENTITYREF;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_IF_FEATURE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_LEAFREF;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_USES;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_USES_AUGMENT;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.updateFilePriority;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;

/**
 * Representation of entity which provides linking service of YANG files.
 */
public class YangLinkerManager
        implements YangLinker {

    /*
     * Set of all the YANG nodes, corresponding to the YANG files parsed by
     * parser.
     */
    private Set<YangNode> yangNodeSet = new HashSet<>();

    /**
     * Returns set of YANG node.
     *
     * @return set of YANG node
     */
    public Set<YangNode> getYangNodeSet() {
        return yangNodeSet;
    }

    /**
     * Creates YANG nodes set.
     *
     * @param yangNodeSet YANG node information set
     */
    public void createYangNodeSet(Set<YangNode> yangNodeSet) {
        getYangNodeSet().addAll(yangNodeSet);
    }

    @Override
    public void resolveDependencies(Set<YangNode> yangNodeSet) {

        // Create YANG node set.
        createYangNodeSet(yangNodeSet);

        // Carry out linking of sub module with module.
        linkSubModulesToParentModule(yangNodeSet);

        // Add references to import list.
        addRefToYangFilesImportList(yangNodeSet);

        // Add reference to include list.
        addRefToYangFilesIncludeList(yangNodeSet);

        // Update the priority for all the files.
        updateFilePriority(yangNodeSet);

        // TODO check for circular import/include.

        // Carry out inter-file linking.
        processInterFileLinking(yangNodeSet);

        processUniqueLinking(yangNodeSet);

        processIdentityExtendList(yangNodeSet);
    }

    /**
     * Processes the identities and add it to all the parents respectively.
     *
     * @param nodeSet set of YANG files info
     */
    public void processIdentityExtendList(Set<YangNode> nodeSet) {
        List<YangNode> list = new LinkedList<>();
        list.addAll(nodeSet);
        sort(list);
        for (YangNode yangNode : list) {
            try {
                YangReferenceResolver resolver =
                        ((YangReferenceResolver) yangNode);
                resolver.resolveIdentityExtendList();
            } catch (DataModelException e) {
                String errorInfo = "Error in file: " + yangNode.getName() +
                        " in " + yangNode.getFileName() + " at " +
                        "line: " + e.getLineNumber() + " at position: " +
                        e.getCharPositionInLine() + NEW_LINE +
                        e.getLocalizedMessage();
                throw new LinkerException(errorInfo, e);
                // TODO add file path in exception message in util manager.
            } catch (LinkerException e) {
                String errorInfo = "Error in file: " + yangNode.getName() +
                        " in " + yangNode.getFileName() + " at " +
                        "line: " + e.getLineNumber() + " at position: " +
                        e.getCharPositionInLine() + NEW_LINE +
                        e.getLocalizedMessage();
                throw new LinkerException(errorInfo, e);
                // TODO add file path in exception message in util manager.
            }
        }
    }

    /**
     * Resolves sub-module linking by linking sub module with parent module.
     *
     * @param yangNodeSet set of YANG files info
     * @throws LinkerException fails to link sub-module to parent module
     */
    public void linkSubModulesToParentModule(Set<YangNode> yangNodeSet)
            throws LinkerException {
        for (YangNode yangNode : yangNodeSet) {
            if (yangNode instanceof YangSubModule) {
                try {
                    ((YangSubModule) yangNode).linkWithModule(getYangNodeSet());
                } catch (DataModelException e) {
                    String errorInfo = "Error in file: " + yangNode.getName() + " in " +
                            yangNode.getFileName() + " at " +
                            "line: " + e.getLineNumber() + " at position: " + e.getCharPositionInLine() + NEW_LINE
                            + e.getLocalizedMessage();
                    throw new LinkerException(errorInfo, e);
                    // TODO add file path in exception message in util manager.
                }
            }
        }
    }

    /**
     * Adds imported node information to the import list.
     *
     * @param yangNodeSet set of YANG files info
     * @throws LinkerException fails to find imported module
     */
    public void addRefToYangFilesImportList(Set<YangNode> yangNodeSet)
            throws LinkerException {
        for (YangNode yangNode : yangNodeSet) {
            if (yangNode instanceof YangReferenceResolver) {
                try {
                    ((YangReferenceResolver) yangNode).addReferencesToImportList(getYangNodeSet());
                } catch (DataModelException e) {
                    String errorInfo = "Error in file: " + yangNode.getName() + " in " +
                            yangNode.getFileName() + " at " +
                            "line: " + e.getLineNumber() + " at position: " + e.getCharPositionInLine() + NEW_LINE
                            + e.getLocalizedMessage();
                    throw new LinkerException(errorInfo, e);
                    // TODO add file path in exception message in util manager.
                }
            }
        }
    }

    /**
     * Adds included node information to the include list.
     *
     * @param yangNodeSet set of YANG files info
     * @throws LinkerException fails to find included sub-module
     */
    public void addRefToYangFilesIncludeList(Set<YangNode> yangNodeSet)
            throws LinkerException {
        for (YangNode yangNode : yangNodeSet) {
            if (yangNode instanceof YangReferenceResolver) {
                try {
                    ((YangReferenceResolver) yangNode).addReferencesToIncludeList(getYangNodeSet());
                } catch (DataModelException e) {
                    String errorInfo = "Error in file: " + yangNode.getName() + " in " +
                            yangNode.getFileName() + " at " +
                            "line: " + e.getLineNumber() + " at position: " + e.getCharPositionInLine() + NEW_LINE
                            + e.getLocalizedMessage();
                    throw new LinkerException(errorInfo, e);
                    // TODO add file path in exception message in util manager.
                }
            }
        }
    }

    /**
     * Processes inter file linking for type and uses.
     *
     * @param yangNodeSet set of YANG files info
     * @throws LinkerException a violation in linker execution
     */
    public void processInterFileLinking(Set<YangNode> yangNodeSet)
            throws LinkerException {
        List<YangNode> yangNodeSortedList = new LinkedList<>();
        yangNodeSortedList.addAll(yangNodeSet);
        sort(yangNodeSortedList);
        for (YangNode yangNode : yangNodeSortedList) {
            try {
                YangReferenceResolver resolver = ((YangReferenceResolver)
                        yangNode);
                resolver.resolveInterFileLinking(YANG_IF_FEATURE);
                resolver.resolveInterFileLinking(YANG_USES);
                resolver.resolveInterFileLinking(YANG_USES_AUGMENT);
                resolver.resolveInterFileLinking(YANG_AUGMENT);
                resolver.resolveInterFileLinking(YANG_DERIVED_DATA_TYPE);
                resolver.resolveInterFileLinking(YANG_BASE);
                resolver.resolveInterFileLinking(YANG_IDENTITYREF);
                resolver.resolveInterFileLinking(YANG_LEAFREF);
                resolver.resolveInterFileLinking(YANG_COMPILER_ANNOTATION);
                resolver.resolveInterFileLinking(YANG_DEVIATION);
            } catch (DataModelException e) {
                String errorInfo = "Error in file: " + yangNode.getName() + " in " +
                        yangNode.getFileName() + " at " +
                        "line: " + e.getLineNumber() + " at position: " + e.getCharPositionInLine() + NEW_LINE
                        + e.getLocalizedMessage();
                throw new LinkerException(errorInfo, e);
                // TODO add file path in exception message in util manager.
            } catch (LinkerException e) {
                String errorInfo = "Error in file: " + yangNode.getName() + " in " +
                        yangNode.getFileName() + " at " +
                        "line: " + e.getLineNumber() + " at position: " + e.getCharPositionInLine() + NEW_LINE
                        + e.getLocalizedMessage();
                throw new LinkerException(errorInfo, e);
                // TODO add file path in exception message in util manager.
            }
        }
    }

    /**
     * Processes unique linking, which takes place after all linking.
     *
     * @param nodeSet set of YANG files info
     * @throws LinkerException a violation in linker execution
     */
    public void processUniqueLinking(Set<YangNode> nodeSet)
            throws LinkerException {
        List<YangNode> list = new LinkedList<>();
        list.addAll(nodeSet);
        sort(list);
        for (YangNode yangNode : list) {
            try {
                YangReferenceResolver resolver =
                        ((YangReferenceResolver) yangNode);
                resolver.resolveUniqueLinking();
            } catch (DataModelException e) {
                String errorInfo = "Error in file: " + yangNode.getName() +
                        " in " + yangNode.getFileName() + " at " +
                        "line: " + e.getLineNumber() + " at position: " +
                        e.getCharPositionInLine() + NEW_LINE +
                        e.getLocalizedMessage();
                throw new LinkerException(errorInfo, e);
                // TODO add file path in exception message in util manager.
            } catch (LinkerException e) {
                String errorInfo = "Error in file: " + yangNode.getName() +
                        " in " + yangNode.getFileName() + " at " +
                        "line: " + e.getLineNumber() + " at position: " +
                        e.getCharPositionInLine() + NEW_LINE +
                        e.getLocalizedMessage();
                throw new LinkerException(errorInfo, e);
                // TODO add file path in exception message in util manager.
            }
        }
    }
}
