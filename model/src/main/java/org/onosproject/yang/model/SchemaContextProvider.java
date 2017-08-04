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

/**
 * Representation of an entity that provides schema context.
 */
public interface SchemaContextProvider {

    /**
     * Returns schema context corresponding to a given resource identifier.
     * It returns null if module is not registered. Resource identifier with
     * "/" is to be provided to obtain root level resource context.
     *
     * @param id absolute resource identifier
     * @return schema context
     * @throws IllegalArgumentException when module as per resource
     *                                  identifier is registered, but given
     *                                  resource identifier is invalid
     */
    SchemaContext getSchemaContext(ResourceId id);

    /**
     * Returns rpc context corresponding to a given resource identifier.
     *
     * @param id absolute resource identifier
     * @return rpc context
     * @throws IllegalArgumentException when module is not reqistered or rpc
     *                                  is invalid
     */
    RpcContext getRpcContext(ResourceId id);
}
