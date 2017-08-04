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

import java.nio.file.Path;
import java.util.Set;

/**
 * Representation of parameters required for YANG compilation.
 */
public interface YangCompilationParam {

    /**
     * Returns set of YANG files path. This could be a directory in which
     * YANG files resides, all files under that directory will be taken.
     *
     * @return set of YANG file path
     */
    Set<Path> getYangFiles();

    /**
     * Returns set of dependent metadata paths.
     *
     * @return set of dependent metadata path
     */
    Set<Path> getDependentSchemas();

    /**
     * Returns the desired path of generated code. If its not specified default
     * path will be taken.
     *
     * @return path to generated code destination directory
     */
    Path getCodeGenDir();

    /**
     * Returns the desired path of metadata. If its not specified default
     * path will be taken.
     *
     * @return path to generated metadata destination directory.
     */
    Path getMetadataGenDir();

    /**
     * Returns the model identifier.
     *
     * @return model identifier
     */
    String getModelId();
}
