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

package org.onosproject.yang.runtime;

import org.onosproject.yang.model.YangModel;

import java.util.Set;

/**
 * Registry of YANG models known to YANG runtime.
 */
public interface YangModelRegistry {

    /**
     * Registers a new model.
     *
     * @param param parameters having model to be registered with additional
     *              informations provided by app
     */
    void registerModel(ModelRegistrationParam param);

    /**
     * Unregisters the specified model.
     *
     * @param param parameters having model to be registered with additional
     *              informations provided by app
     */
    void unregisterModel(ModelRegistrationParam param);

    /**
     * Returns collection of all registered models.
     *
     * @return collection of models
     */
    Set<YangModel> getModels();
}
