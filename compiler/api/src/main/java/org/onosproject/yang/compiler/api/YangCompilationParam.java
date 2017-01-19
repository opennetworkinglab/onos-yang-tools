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
     * Adds YANG file path. This could be a directory in which YANG files are
     * available.
     *
     * @param path YANG file path
     */
    void addYangFile(Path path);

    /**
     * Returns set of dependent metadata paths.
     *
     * @return set of dependent metadata path
     */
    Set<Path> getDependentSchemas();

    /**
     * Adds dependent metadata path.
     *
     * @param path metadata path
     */
    void addDependentSchema(Path path);

    /**
     * Returns the desired path of generated code. If its not specified default
     * path will be taken.
     *
     * @return path to generated code destination directory
     */
    Path getCodeGenDir();

    /**
     * Sets code generation directory.
     *
     * @param path expected code generation directory
     */
    void setCodeGenDir(Path path);

    /**
     * Returns the desired path of metadata. If its not specified default
     * path will be taken.
     *
     * @return path to generated metadata destination directory.
     */
    Path getMetadataGenDir();

    /**
     * Sets metadata generation directory.
     *
     * @param path expected metadata generation directory
     */
    void setMetadataGenDir(Path path);
}
