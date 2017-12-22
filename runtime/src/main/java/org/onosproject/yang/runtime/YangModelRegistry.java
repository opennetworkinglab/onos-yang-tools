/*
 * Copyright 2017-present Open Networking Foundation
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

import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.YangModel;
import org.onosproject.yang.model.YangModule;
import org.onosproject.yang.model.YangModuleId;

import java.util.Set;

/**
 * Registry of YANG models known to YANG runtime.
 */
public interface YangModelRegistry {

    /**
     * Registers a new model.
     *
     * @param param parameters having model to be registered with additional
     *              information provided by app
     * @throws IllegalArgumentException when requested model with provided
     *                                  identifier is already registered or
     *                                  not valid
     */
    void registerModel(ModelRegistrationParam param)
            throws IllegalArgumentException;

    /**
     * Registers the given generated node referenced by given model object
     * identifier under provided anydata model object identifier.
     *
     * @param id  identifier to reference anydata container under which
     *            application is expecting the data
     * @param id1 identifier to reference the node defined in YANG file which
     *            application can send as content or child nodes under anydata
     * @throws IllegalArgumentException when provided identifier is not
     *                                  not valid
     */
    void registerAnydataSchema(ModelObjectId id, ModelObjectId id1)
            throws IllegalArgumentException;

    /**
     * Unregisters the given generated node class under provided anydata class.
     *
     * @param id  identifier to reference anydata container under which
     *            application has registered the schema
     * @param id1 identifier to reference the node defined in YANG file which
     *            application can send as content or child nodes under anydata
     * @throws IllegalArgumentException when provided identifier is not
     *                                  not valid
     */
    void unregisterAnydataSchema(Class id, Class id1)
            throws IllegalArgumentException;

    /**
     * Unregisters the specified model.
     *
     * @param param parameters having model to be registered with additional
     *              information provided by app
     */
    void unregisterModel(ModelRegistrationParam param);

    /**
     * Returns collection of all registered models.
     *
     * @return collection of models
     */
    Set<YangModel> getModels();

    /**
     * Returns YANG model for given model id.
     *
     * @param id model identifier
     * @return YANG model
     */
    YangModel getModel(String id);

    /**
     * Returns YANG module for given YANG module id.
     *
     * @param id module identifier
     * @return YANG module
     */
    YangModule getModule(YangModuleId id);
}
