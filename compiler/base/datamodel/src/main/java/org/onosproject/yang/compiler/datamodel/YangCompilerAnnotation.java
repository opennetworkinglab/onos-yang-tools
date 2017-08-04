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

package org.onosproject.yang.compiler.datamodel;

import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus;
import org.onosproject.yang.compiler.datamodel.utils.YangConstructType;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.COMPILER_ANNOTATION_DATA;

/**
 * Represents data model node to maintain information defined in YANG compiler-annotation.
 */
public class YangCompilerAnnotation extends DefaultLocationInfo
        implements Parsable, YangXPathResolver, Resolvable, Serializable {

    private static final long serialVersionUID = 806201602L;

    /**
     * App data structure information.
     */
    private YangAppDataStructure yangAppDataStructure;

    /**
     * App extended name information.
     */
    private YangAppExtended yangAppExtended;

    /**
     * Prefix of compiler-annotation.
     */
    private String prefix;

    /**
     * Path of compiler-annotation.
     */
    private String path;

    /**
     * Path of compiler-annotation.
     */
    private List<YangAtomicPath> atomicPathList = new LinkedList<>();

    /**
     * Resolution status.
     */
    private ResolvableStatus resolvableStatus;

    /**
     * Returns the YANG app data structure information.
     *
     * @return the YANG app data structure information
     */
    public YangAppDataStructure getYangAppDataStructure() {
        return yangAppDataStructure;
    }

    /**
     * Sets the YANG app data structure information.
     *
     * @param yangAppDataStructure the YANG app data structure to set
     */
    public void setYangAppDataStructure(YangAppDataStructure yangAppDataStructure) {
        this.yangAppDataStructure = yangAppDataStructure;
    }

    /**
     * Returns the prefix.
     *
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix information.
     *
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Returns the path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path.
     *
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns the YANG app extended name information.
     *
     * @return the YANG app extended name information
     */
    public YangAppExtended getYangAppExtendedName() {
        return yangAppExtended;
    }

    /**
     * Sets the YANG app extended name information.
     *
     * @param yangAppExtendedName the YANG app extended name to set
     */
    public void setYangAppExtendedName(YangAppExtended yangAppExtendedName) {
        yangAppExtended = yangAppExtendedName;
    }

    /**
     * Returns the list of atomic path.
     *
     * @return the list of atomic path
     */
    public List<YangAtomicPath> getAtomicPathList() {
        return unmodifiableList(atomicPathList);
    }

    /**
     * Sets the atomic path.
     *
     * @param atomicPathList the atomic path list to set
     */
    public void setAtomicPathList(List<YangAtomicPath> atomicPathList) {
        this.atomicPathList = atomicPathList;
    }

    @Override
    public YangConstructType getYangConstructType() {
        return COMPILER_ANNOTATION_DATA;
    }

    @Override
    public void validateDataOnEntry() throws DataModelException {
        // TODO : to be implemented
    }

    @Override
    public void validateDataOnExit() throws DataModelException {
        // TODO : to be implemented
    }

    @Override
    public ResolvableStatus getResolvableStatus() {
        return resolvableStatus;
    }

    @Override
    public void setResolvableStatus(ResolvableStatus resolvableStatus) {
        this.resolvableStatus = resolvableStatus;
    }

    @Override
    public Object resolve()
            throws DataModelException {
        return null;
    }
}
