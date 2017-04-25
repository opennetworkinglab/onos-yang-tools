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

/**
 * Representation of an entity that provides YANG RPC service.
 */
public interface YangRpcService {

    /**
     * Registers an RPC handler.
     *
     * @param handler RPC handler
     * @param command RPC command
     */
    void registerHandler(RpcHandler handler, RpcCommand command);

    /**
     * Unregisters an RPC receiver.
     *
     * @param handler RPC handler
     * @param command RPC command
     */
    void unRegisterHandler(RpcHandler handler, RpcCommand command);

    /**
     * Invokes an RPC.
     *
     * @param caller  of the of the RPC
     * @param msgId   RPC message id
     * @param command RPC command
     * @param input   RPC input
     */
    void invokeRpc(RpcCaller caller, Integer msgId, RpcCommand command,
                   RpcInput input);

    /**
     * Provides response to a a previously invoked RPC.
     *
     * @param msgId  of a previously invoked RPC
     * @param output data from the RPC execution
     */
    void rpcResponse(Integer msgId, RpcOutput output);
}
