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

package org.onosproject.yang;

import java.util.List;

/**
 * Representation of a container data node capable of containing children nodes.
 */
public interface ContainerDataNode extends DataNode {

    // List of children
    List<DataNode> children();

    // Child with specific identifier
    DataNode child(String identifier); // TODO: Use NodeIdentifier instead of String

}
