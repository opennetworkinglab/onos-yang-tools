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

import java.io.InputStream;

/**
 * Representation of YANG module information.
 */
public interface YangModule {

    /**
     * Returns YANG module identifier.
     *
     * @return module identifier
     */
    YangModuleId getYangModuleId();

    /**
     * Returns input stream corresponding to a given YANG file path.
     *
     * @return stream
     */
    InputStream getYangSource();

    /**
     * Returns metadata stream.
     *
     * @return stream
     */
    InputStream getMetadata();

    /**
     * Returns true if module is used as inter jar, false otherwise.
     *
     * @return true if module is used as inter jar, false otherwise
     */
    boolean isInterJar();
}
