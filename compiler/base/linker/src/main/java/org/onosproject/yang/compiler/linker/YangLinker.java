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

package org.onosproject.yang.compiler.linker;

import org.onosproject.yang.compiler.datamodel.YangNode;

import java.util.Set;

/**
 * Abstraction of entity which provides linking service of YANG files.
 */
public interface YangLinker {

    /**
     * Resolve the import and include dependencies for a given resolution
     * information.
     *
     * @param yangNodeSet set of all dependent YANG nodes
     */
    void resolveDependencies(Set<YangNode> yangNodeSet);
}
