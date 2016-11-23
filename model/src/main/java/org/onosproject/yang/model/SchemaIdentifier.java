/*
 * Copyright 2016-present Open Networking Laboratory
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

/**
 * Abstraction of an entity which identifies a schema node in the schema / data
 * tree.
 */
public interface SchemaIdentifier {
    /**
     * Returns node schema name. This is mandatory to identify node according
     * to schema.
     *
     * @return node name
     */
    String name();

    /**
     * Returns node's namespace. This is mandatory serializers must translate
     * any implicit namespace to explicit namespace.
     *
     * @return node's namespace
     */
    String namespace();
}
