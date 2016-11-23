/*
 * Copyright 2016-present Open Networking Laboratory
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

import java.util.List;

/**
 * Abstraction of an entity which identifies a unique branching node
 * corresponding to a multi instance schema definition.
 */
public interface MultiInstanceNodeKey extends NodeKey {

    /**
     * Returns the list of key leaf nodes of a multi instance node, which
     * uniquely identifies the branching node entry corresponding to a multi
     * instance schema definition.
     *
     * @return List of key leaf nodes
     */
    List<LeafNode> keyLeafs();
}
