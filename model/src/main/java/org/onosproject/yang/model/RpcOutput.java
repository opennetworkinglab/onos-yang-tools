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
 * Abstraction for RPC output.
 */
public class RpcOutput {
    public enum Status {
        /**
         * RPC execution was successful.
         */
        RPC_SUCCESS,
        /**
         * RPC execution failed.
         */
        RPC_FAILURE,
        /**
         * RPC execution don't have any output data.
         */
        RPC_NODATA,
        /**
         * Failed to receive a response from the receiver, within the broker specified timeout.
         */
        RPC_TIMEOUT,
    }
    /**
     * Message id of the Rpc request.
     */
    String messageId;
    /**
     * Status of RPC execution.
     */
    Status status;
    /**
     * Output data from the RPC execution.
     */
    DataNode data;

    /**
     * Creates an instance of RpcOutput.
     *
     * @param status of RPC execution
     * @param data of RPC execution
     */
    public RpcOutput(Status status, DataNode data) {
        this.status = status;
        this.data = data;
    }

    /**
     * Creates an instance of RpcOutput.
     *
     * @param messageId of the Rpc request
     * @param status of RPC execution
     * @param data of RPC execution
     */
    public RpcOutput(String messageId, Status status, DataNode data) {
        this.messageId = messageId;
        this.status = status;
        this.data = data;
    }

    /**
     * Returns messageId of the Rpc request.
     *
     * @return messageId
     */
    public String messageId() {
        return this.messageId;
    }

    /**
     * Returns RPC status.
     *
     * @return status
     */
    public RpcOutput.Status status() {
        return this.status;
    }

    /**
     * Returns RPC output.
     *
     * @return output data
     */
    public DataNode data() {
        return this.data;
    }

    /**
     * Sets the messageId in the Rpc output.
     * @param msgId the msgId to be set in the Rpc output
     */
    public void messageId(String msgId) {
        this.messageId = msgId;
    }
}