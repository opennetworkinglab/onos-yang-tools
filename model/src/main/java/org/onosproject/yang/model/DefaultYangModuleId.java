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

package org.onosproject.yang.model;

import java.io.Serializable;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents default YANG module identifier.
 */
public class DefaultYangModuleId implements YangModuleId, Serializable {

    private String moduleName;
    private String revision;

    /**
     * Creates an instance of default YANG module id.
     *
     * @param name name of module
     * @param rev  revision of module
     */
    public DefaultYangModuleId(String name, String rev) {
        checkNotNull(name);
        moduleName = name;
        revision = rev;
    }

    @Override
    public String moduleName() {
        return moduleName;
    }

    @Override
    public String revision() {
        return revision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleName, revision);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        DefaultYangModuleId that = (DefaultYangModuleId) obj;
        return Objects.equals(moduleName, that.moduleName) &&
                Objects.equals(revision, that.revision);
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("moduleName", moduleName)
                .add("revision", revision)
                .toString();
    }
}
