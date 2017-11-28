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

package org.onosproject.yang.compiler.datamodel;

/**
 * Represents the nature of YANG node, it is used by YMS to abstractly
 * understand the nature of node without knowing details of the YANG
 * construct.
 */
public enum YangSchemaNodeType {

    /**
     * Represents single instance of YANG node like YANG container, YANG module,
     * YANG sub-module. This is used by YMS to abstractly understand the nature
     * of node.
     */
    YANG_SINGLE_INSTANCE_NODE,

    /**
     * Represents multi instance of YANG node i.e. YANG list.
     * This is used by YMS to abstractly understand the nature of node.
     */
    YANG_MULTI_INSTANCE_NODE,

    /**
     * Represents single instance of YANG leaf node i.e. YANG leaf
     * This is used by YMS to abstractly understand the nature of node.
     */
    YANG_SINGLE_INSTANCE_LEAF_NODE,

    /**
     * Represents multi instance of YANG leaf node i.e. YANG leaflist
     * This is used by YMS to abstractly understand the nature of node.
     */
    YANG_MULTI_INSTANCE_LEAF_NODE,

    /**
     * Represents node which is not a data node.
     */
    YANG_NON_DATA_NODE,

    /**
     * Represents node which cannot be instantiated.
     */
    YANG_CHOICE_NODE,

    /**
     * Represents the Augmented Node.
     */
    YANG_AUGMENT_NODE,

    /**
     * Represents the Anydata Node.
     */
    YANG_ANYDATA_NODE

}
