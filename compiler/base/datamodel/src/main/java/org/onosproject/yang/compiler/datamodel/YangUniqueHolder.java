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

import java.util.List;

/**
 * Abstraction of unique entity. It is used to abstract the data holders of
 * unique.
 */
public interface YangUniqueHolder {

    /**
     * Adds unique path in data holder like list.
     *
     * @param path unique path
     */
    void addUnique(List<YangAtomicPath> path);

    /**
     * Sets the list of unique path.
     *
     * @param pathList unique path list
     */
    void setPathList(List<List<YangAtomicPath>> pathList);

    /**
     * Returns the list of unique path from data holder like list.
     *
     * @return unique path list
     */
    List<List<YangAtomicPath>> getPathList();

    /**
     * Returns the list of YANG leaves, unique was referring to.
     *
     * @return YANG leaves list
     */
    List<YangLeaf> getUniqueLeaves();

    /**
     * Adds the YANG leaf, unique is referring to.
     *
     * @param uniqueLeaf YANG leaf
     */
    void addUniqueLeaf(YangLeaf uniqueLeaf);
}
