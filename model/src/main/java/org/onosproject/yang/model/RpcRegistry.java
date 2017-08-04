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

import com.google.common.annotations.Beta;

import java.util.Set;

/**
 * Entity capable of tracking RPC service end-points.
 */
@Beta
public interface RpcRegistry {
    /**
     * Returns the set of all registered service implementations.
     *
     * @return set of service implementations
     */
    Set<RpcService> getRpcServices();

    /**
     * Returns the RPC service implementation registered with the specified
     * RPC service interface.
     *
     * @param serviceInterface RPC service interface
     * @return RPC service implementation
     */
    RpcService getRpcService(Class<? extends RpcService> serviceInterface);

    /**
     * Registers the specified RPC service.
     *
     * @param service service implementation to be registered
     * @throws RegisterException if register failed
     */
    void registerRpcService(RpcService service);

    /**
     * Registers the specified RPC service.
     *
     * @param service service implementation to be registered
     * @throws RegisterException if unregister failed
     */
    void unregisterRpcService(RpcService service);
}