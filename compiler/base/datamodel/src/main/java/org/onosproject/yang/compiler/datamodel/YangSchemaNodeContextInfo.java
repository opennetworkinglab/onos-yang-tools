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

import java.io.Serializable;

/**
 * Abstraction of YANG data node context information, used by YMS to abstractly
 * refer the YANG data nodes schema context information.
 */
public class YangSchemaNodeContextInfo extends DefaultLocationInfo
        implements Serializable {

    private static final long serialVersionUID = 806201613L;

    // Current schema node
    private YangSchemaNode schemaNode;

    /*
     * Context switched schema node, it will be non null only for the scenarios
     * in which context switch is there like augment, choice etc, in this case
     * this node will point to context switched node like YangAugmentInfo.
     */
    private YangSchemaNode contextSwitchedNode;

    // Default instance of YangSchemaNodeContextInfo.
    public YangSchemaNodeContextInfo() {
    }

    /**
     * Returns the YANG schema node.
     *
     * @return YANG schema node
     */
    public YangSchemaNode getSchemaNode() {
        return schemaNode;
    }

    /**
     * Sets YANG schema node.
     *
     * @param schemaNode YANG schema node
     */
    void setSchemaNode(YangSchemaNode schemaNode) {
        this.schemaNode = schemaNode;
    }

    /**
     * Returns context switched node.
     *
     * @return context switched node
     */
    public YangSchemaNode getContextSwitchedNode() {
        return contextSwitchedNode;
    }

    /**
     * Set context switched node.
     *
     * @param contextSwitchedNode context switched node
     */
    void setContextSwitchedNode(YangSchemaNode contextSwitchedNode) {
        this.contextSwitchedNode = contextSwitchedNode;
    }
}
