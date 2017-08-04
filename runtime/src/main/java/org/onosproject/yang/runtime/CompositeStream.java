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

import java.io.InputStream;

/**
 * Abstraction of an entity that is composition of resource identifier and
 * data.
 */
public interface CompositeStream {

    /**
     * Retrieves the resource identifier stream on which the operation is being
     * performed.
     *
     * @return uri as per RFC 3986
     */
    String resourceId();

    /**
     * Retrieves the resource data stream in the protocol encoding format.
     *
     * @return resource data
     */
    InputStream resourceData();
}
