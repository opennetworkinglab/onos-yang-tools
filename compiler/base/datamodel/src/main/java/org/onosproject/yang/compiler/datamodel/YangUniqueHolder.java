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

package org.onosproject.yang.compiler.datamodel;

import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;

import java.util.List;

/**
 * Abstraction of unique entity. It is used to abstract the data holders of
 * unique.
 */
public interface YangUniqueHolder {

    /**
     * Adds unique in data holder like list.
     *
     * @param unique the unique to be added
     * @throws DataModelException if any violation of data model rules
     */
    void addUnique(String unique) throws DataModelException;

    /**
     * Sets the list of unique.
     *
     * @param uniqueList the list of unique to set
     */
    void setUniqueList(List<String> uniqueList);

    /**
     * Returns the list of unique from data holder like list.
     *
     * @return the list of unique
     */
    List<String> getUniqueList();
}
