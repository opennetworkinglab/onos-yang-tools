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

/**
 * Representation of an entity which maintains additional information
 * required to create the data node.
 */
public class HelperContext {

    /**
     * Reference for resource identifier builder.
     */
    private ExtResourceIdBldr resourceIdBldr;

    /**
     * Reference for parent resource id builder.
     */
    private ExtResourceIdBldr parentResourceIdBldr;

    // Forbid construction.
    public HelperContext() {
        resourceIdBldr = new ExtResourceIdBldr();
    }

    /**
     * Returns resource identifier builder of the node.
     *
     * @return resource identifier builder
     */
    public ExtResourceIdBldr getResourceIdBuilder() {
        return resourceIdBldr;
    }

    /**
     * Adds resource identifier of the current node.
     *
     * @param builder resource identifier builder
     */
    public void setResourceIdBuilder(ExtResourceIdBldr builder) {
        resourceIdBldr = builder;
    }

    /**
     * Returns parent resource id builder.
     * This will be used in case of data node initialization with resource id.
     *
     * @return parent resource id builder
     */
    public ExtResourceIdBldr getParentResourceIdBldr() {
        return parentResourceIdBldr;
    }

    /**
     * Sets parent resource id builder for current node.
     *
     * @param prid parent resource id builder
     */
    public void setParentResourceIdBldr(ExtResourceIdBldr prid) {
        parentResourceIdBldr = prid;
    }
}
