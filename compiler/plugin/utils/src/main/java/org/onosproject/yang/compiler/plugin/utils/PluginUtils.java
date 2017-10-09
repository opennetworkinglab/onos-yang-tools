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

package org.onosproject.yang.compiler.plugin.utils;

/**
 * Representation of model id validation util.
 */
public final class PluginUtils {

    // Forbid construction.
    private PluginUtils() {
    }

    /**
     * Returns the valid model id by removing the special character with
     * underscore.
     *
     * @param id user given model id
     * @return model id
     * @throws IllegalArgumentException if user defined model id does not
     *                                  contain at least a alphanumeric character
     */
    public static String getValidModelId(String id) throws
            IllegalArgumentException {
        // checking weather modelId contains the alphanumeric character or not.
        if (id.matches(".*[A-Za-z0-9].*")) {
            // replacing special characters with '_'
            id = id.replaceAll("[\\s\\/:*?\"\\[\\]<>|$@!#%&(){}';,]", "_");
            // remove leading and trailing underscore
            id = id.replaceAll("^_+|_+$", "");
            // replacing the consecutive underscores '_' to single _
            id = id.replaceAll("_+", "_");
            return id;
        } else {
            throw new IllegalArgumentException("Invalid model id " + id);
        }
    }
}
