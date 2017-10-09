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

package org.onosproject.yang.compiler.tool;

import org.onosproject.yang.compiler.datamodel.YangNode;

/**
 * Represents YANG node information.
 */
public class YangNodeInfo {

    private YangNode node;
    private boolean interJar;

    /**
     * Creates an instance of YANG node info.
     *
     * @param node YANG node
     * @param interJar flag indicating if it's inter-jar node
     */
    public YangNodeInfo(YangNode node, boolean interJar) {
        this.node = node;
        this.interJar = interJar;
    }

    /**
     * Returns YANG node.
     *
     * @return YANG node
     */
    public YangNode getNode() {
        return node;
    }

    /**
     * Returns true if inter jar.
     *
     * @return true if inter-jar, false otherwise
     */
    public boolean isInterJar() {
        return interJar;
    }
}
