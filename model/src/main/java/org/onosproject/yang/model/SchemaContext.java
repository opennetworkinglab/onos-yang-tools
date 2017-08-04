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

package org.onosproject.yang.model;

import static org.onosproject.yang.model.DataNode.Type;

/**
 * Representation of a context for obtaining schema related information.
 */
public interface SchemaContext {

    /**
     * Returns parent context. Returns null if current context is of topmost
     * node "/" which doesn't have any parent.
     *
     * @return parent context
     */
    SchemaContext getParentContext();

    /**
     * Returns type of the node.
     *
     * @return node type
     */
    Type getType();

    /**
     * Returns schema identifier of the node.
     *
     * @return schema identifier
     */
    SchemaId getSchemaId();
}
