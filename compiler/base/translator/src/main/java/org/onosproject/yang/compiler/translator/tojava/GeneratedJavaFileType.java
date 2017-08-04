/*
 * Copyright 2016-present Open Networking Foundation
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

package org.onosproject.yang.compiler.translator.tojava;

/**
 * Represents type of java files generated.
 */
public final class GeneratedJavaFileType {

    /**
     * Interface file.
     */
    public static final int INTERFACE_MASK = 1;

    /**
     * Impl class file.
     */
    public static final int DEFAULT_CLASS_MASK = 8;

    /**
     * Interface and class file.
     */
    public static final int GENERATE_INTERFACE_WITH_BUILDER = 8207;

    /**
     * Java interface corresponding to rpc.
     */
    public static final int GENERATE_SERVICE_AND_MANAGER = 16;

    /**
     * Java class corresponding to YANG enumeration.
     */
    public static final int GENERATE_ENUM_CLASS = 32;

    /**
     * Java class corresponding to typedef.
     */
    public static final int GENERATE_TYPEDEF_CLASS = 64;

    /**
     * Java class corresponding to union.
     */
    public static final int GENERATE_UNION_CLASS = 128;

    /**
     * Java class corresponding to typedef.
     */
    static final int GENERATE_TYPE_CLASS = GENERATE_TYPEDEF_CLASS
            | GENERATE_UNION_CLASS;

    /**
     * Event class.
     */
    public static final int GENERATE_EVENT_CLASS = 256;

    /**
     * Event listener class.
     */
    public static final int GENERATE_EVENT_LISTENER_INTERFACE = 512;

    /**
     * Event listener class.
     */
    public static final int GENERATE_EVENT_SUBJECT_CLASS = 1024;

    /**
     * Java classes for events.
     */
    public static final int GENERATE_ALL_EVENT_CLASS_MASK = GENERATE_EVENT_CLASS | GENERATE_EVENT_LISTENER_INTERFACE
            | GENERATE_EVENT_SUBJECT_CLASS;

    /**
     * Identity listener class.
     */
    public static final int GENERATE_IDENTITY_CLASS = 2048;

    /**
     * Identity key class.
     */
    public static final int GENERATE_KEY_CLASS = 4096;

    /**
     * Default RPC handler class.
     */
    public static final int GENERATE_RPC_HANDLER_CLASS = 8192;

    /**
     * Register RPC class.
     */
    public static final int GENERATE_RPC_REGISTER_CLASS = 16384;

    /**
     * RPC command class.
     */
    public static final int GENERATE_RPC_COMMAND_CLASS = 32768;

    /**
     * Extended RPC command class.
     */
    public static final int GENERATE_RPC_EXTENDED_COMMAND_CLASS = 65536;

    /**
     * Java classes for RPC.
     */
    public static final int GENERATE_ALL_RPC_CLASS_MASK =
            GENERATE_RPC_HANDLER_CLASS | GENERATE_RPC_REGISTER_CLASS
                    | GENERATE_RPC_EXTENDED_COMMAND_CLASS;

    /**
     * Creates an instance of generate java file type.
     */
    private GeneratedJavaFileType() {
    }
}
