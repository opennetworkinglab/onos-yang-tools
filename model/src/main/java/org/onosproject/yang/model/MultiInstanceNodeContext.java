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

package org.onosproject.yang.model;

import java.util.Set;

/**
 * Representation of multi instance node schema context.
 */
public interface MultiInstanceNodeContext extends SchemaContext {

    /**
     * Returns child context.
     *
     * @param id schema identifier
     * @return child schema context
     * @throws IllegalArgumentException when schema identifier is invalid
     */
    SchemaContext getChildContext(SchemaId id);

    /**
     * Returns ordered set of key leaf name as per the YANG schema.
     *
     * @return set of key leaf
     */
    Set<String> getKeyLeaf();
}
