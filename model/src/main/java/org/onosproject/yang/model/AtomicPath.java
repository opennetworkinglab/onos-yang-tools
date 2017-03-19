/*
 * Copyright 2017-present Open Networking Laboratory
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

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.hash;

/**
 * Abstraction of an entity which identifies a generated class uniquely among
 * its siblings.
 */
public class AtomicPath {

    private DataNode.Type type;

    /**
     * Creates a atomic path object.
     *
     * @param type atomic path type
     */
    protected AtomicPath(DataNode.Type type) {
        this.type = type;
    }

    /**
     * Returns the atomic path type.
     *
     * @return the atomic path type
     */
    public DataNode.Type type() {
        return type;
    }

    /**
     * Sets the atomic path type identifier of leaf-list.
     *
     * @param type atomic path type
     */
    public void type(DataNode.Type type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return hash(type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        AtomicPath that = (AtomicPath) obj;
        return Objects.equals(type, that.type);
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("type", type)
                .toString();
    }
}
