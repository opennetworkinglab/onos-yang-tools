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
 * Representation of default implementation of composite stream.
 */
public class DefaultCompositeStream implements CompositeStream {

    private String resourceId;
    private InputStream resourceData;

    /**
     * Creates an instance of composite stream.
     *
     * @param id   uri as per RFC 3986
     * @param data input data stream
     */
    public DefaultCompositeStream(String id, InputStream data) {
        resourceId = id;
        resourceData = data;
    }

    @Override
    public String resourceId() {
        return resourceId;
    }

    @Override
    public InputStream resourceData() {
        return resourceData;
    }
}

