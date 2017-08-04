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

import java.util.List;

/**
 * Representation of an entity which defines additional information about the
 * module required for the model registration. App info should only contain
 * interface/service file generated for module node.
 */
public interface AppModuleInfo {

    /**
     * Returns the module class which will be required by YANG runtime to
     * obtain the class loader.
     *
     * @return module JAVA class
     */
    Class<?> getModuleClass();

    /**
     * Reference RFC 7895
     * Retrieves the list of YANG feature names from this module that are
     * supported by the server, regardless of whether they are
     * defined in the module or any included submodule.
     *
     * @return list of YANG features
     */
    List<String> getFeatureList();
}
