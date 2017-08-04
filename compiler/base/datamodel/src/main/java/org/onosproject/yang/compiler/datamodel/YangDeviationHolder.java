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
 * Abstraction of deviation entity. It is used to abstract the data holders of
 * deviation statement.
 */
public interface YangDeviationHolder {

    /**
     * Returns true if deviated node already cloned, false
     * otherwise.
     *
     * @return the isDeviateNodeCloned flag
     */
    boolean isDeviatedNodeCloned();

    /**
     * Sets the deviatedNodeCloned flag.
     *
     * @param deviatedNodeCloned the flag to set
     */
    void setDeviatedNodeCloned(boolean deviatedNodeCloned);

    /**
     * Returns true if module defined only for deviation,
     * false otherwise.
     *
     * @return the isDeviateNodeCloned flag
     */
    boolean isModuleForDeviation();

    /**
     * Sets the moduleForDeviation flag.
     *
     * @param moduleForDeviation the flag to set
     */
    void setModuleForDeviation(boolean moduleForDeviation);
}
