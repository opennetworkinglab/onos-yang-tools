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

/**
 * Abstraction of max-element entity. It is used to abstract the data holders
 * of max-element statement.
 */
public interface YangMaxElementHolder {

    /**
     * Returns the max elements.
     *
     * @return the max elements
     */
    YangMaxElement getMaxElements();

    /**
     * Sets the max elements.
     *
     * @param max the max elements
     */
    void setMaxElements(YangMaxElement max);
}
