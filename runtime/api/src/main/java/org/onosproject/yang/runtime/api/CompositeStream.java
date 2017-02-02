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

import java.io.InputStream;

/**
 * Representation of composite stream comprising of resource identifier and
 * its data.
 */
public interface CompositeStream {

    /**
     * Retrieves the resource identifier stream on which the operation is being
     * performed.
     *
     * @return string representation of the resource being identified
     */
    String resourceId();

    /**
     * Sets resource identifier on which operation is to be performed.
     *
     * @param uri resource identifier string as per RFC 3986
     */
    void resourceId(String uri);

    /**
     * Retrieves the resource data stream in the protocol encoding format.
     *
     * @return resource data
     */
    InputStream resourceData();

    /**
     * Sets resource data stream in the protocol encoding format.
     *
     * @param stream resource data
     */
    void resourceData(InputStream stream);
}
