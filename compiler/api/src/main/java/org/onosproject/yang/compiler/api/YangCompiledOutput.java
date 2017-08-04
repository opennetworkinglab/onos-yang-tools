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

package org.onosproject.yang.compiler.api;

import org.onosproject.yang.model.YangModel;

import java.nio.file.Path;
import java.util.Set;

/**
 * Representation of an entity that provides YANG compiled output.
 */
public interface YangCompiledOutput {

    /**
     * Returns compiled YANG model.
     *
     * @return YANG model
     */
    YangModel getYangModel();

    /**
     * Returns generated JAVA files.
     *
     * @return generated JAVA files.
     */
    Set<Path> getGeneratedJava();
}