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

package org.onosproject.yangutils.tool;

import java.io.IOException;

/**
 * Abstractions of the hooks that needs to be supported by plugins, which
 * will be used in by tool to be plugin agnostic.
 */
public interface CallablePlugin {
    /**
     * Adds generated source directory to the compilation root.
     */
    void addGeneratedCodeToBundle();

    /**
     * serialize the compiled schema and place it in the appropriate location
     * so that it will be part of the generated OSGi bundle.
     *
     */
    void addCompiledSchemaToBundle()
            throws IOException;


    /**
     * Add the YANG files in the bundle, to support YANG display in protocols
     * like RESTCONF.
     *
     */
    void addYangFilesToBundle() throws IOException;
}
