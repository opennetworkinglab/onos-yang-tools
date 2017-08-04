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
 * Representation of rpc context which is JAVA coordinates to the RPC.
 */
public class RpcContext {

    private final String rpcName;

    private final Class<? extends RpcService> serviceIntf;

    /**
     * Creates an instance of rpc context.
     *
     * @param name name of rpc
     * @param intf service interface class
     */
    public RpcContext(String name, Class<? extends RpcService> intf) {
        rpcName = name;
        serviceIntf = intf;
    }

    /**
     * Returns JAVA name of the RPC.
     *
     * @return rpc JAVA name
     */
    public String rpcName() {
        return rpcName;
    }

    /**
     * Returns class corresponding to service interface.
     *
     * @return service interface class
     */
    public Class<? extends RpcService> serviceIntf() {
        return serviceIntf;
    }
}
