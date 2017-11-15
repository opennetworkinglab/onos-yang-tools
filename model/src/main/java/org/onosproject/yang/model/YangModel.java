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

package org.onosproject.yang.model;

import java.util.Set;

/**
 * Representation of a compiled YANG model.
 */
public interface YangModel {

    /**
     * Returns set of YANG module with information.
     *
     * @return YANG module info
     */
    Set<YangModule> getYangModules();

    /**
     * Returns set of YANG modules identifier which belongs to this artifact.
     *
     * @return YANG module identifier
     */
    Set<YangModuleId> getYangModulesId();

    /**
     * Returns model id for requested context.
     *
     * @return YANG model identifier
     */
    String getYangModelId();

    /**
     * Returns YANG module information corresponding to a given module
     * identifier.
     *
     * @param id module identifier
     * @return YANG module information
     */
    YangModule getYangModule(YangModuleId id);

    /**
     * Adds YANG module information for a given module identifier.
     *
     * @param id     module identifier
     * @param module YANG module information
     */
    @Deprecated
    void addModule(YangModuleId id, YangModule module);

    /**
     * Abstraction of an entity that represents builder of YANG model.
     */
    interface Builder {

        /**
         * Adds YANG module information for a given module identifier.
         *
         * @param id     module identifier
         * @param module YANG module information
         * @return builder
         */
        Builder addModule(YangModuleId id, YangModule module);

        /**
         * Adds model identifier.
         *
         * @param modelId model identifier
         * @return builder
         */
        Builder addModelId(String modelId);

        /**
         * Builds an instance of YANG model.
         *
         * @return YANG model
         */
        YangModel build();
    }
}
