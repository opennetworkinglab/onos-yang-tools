/*
 * Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang.runtime.api;

import org.onosproject.yang.YangModel;
import org.onosproject.yang.YangModuleId;

/**
 * Representation of model registration parameters.
 */
public interface ModelRegistrationParam {

    /**
     * Returns YANG model.
     *
     * @return YANG model
     */
    YangModel getYangModel();

    /**
     * Sets YANG model.
     *
     * @param model YANG model
     */
    void setYangModel(YangModel model);

    /**
     * Returns extended app related information of a module/sub-module.
     *
     * @param id YANG module identifier
     * @return application module information
     */
    AppModuleInfo getAppModuleInfo(YangModuleId id);

    /**
     * Adds application module information associated with module identifier.
     * It's expected that application should provide information for all the
     * YANG modules.
     *
     * @param id   YANG module identifier
     * @param info application information associated with module identifier
     */
    void addAppModuleInfo(YangModuleId id, AppModuleInfo info);
}
