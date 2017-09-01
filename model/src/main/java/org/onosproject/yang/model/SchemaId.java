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

package org.onosproject.yang.model;

import java.io.Serializable;
import java.util.Objects;

import com.google.common.collect.ComparisonChain;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.onosproject.yang.model.ModelConstants.INCOMPLETE_SCHEMA_INFO;

/**
 * Representation of an entity which identifies a schema node in the schema /
 * data tree.
 */
public class SchemaId implements Comparable<SchemaId>, Cloneable, Serializable {

    private String name;
    private String nameSpace;

    private SchemaId() {
    }

    public SchemaId(String name, String nameSpace) {
        checkNotNull(name, INCOMPLETE_SCHEMA_INFO);
        this.name = name;
        this.nameSpace = nameSpace;
    }

    /**
     * Returns node schema name. This is mandatory to identify node according
     * to schema.
     *
     * @return node name
     */
    public String name() {
        return name;
    }

    /**
     * Returns node's namespace. This is mandatory serializers must translate
     * any implicit namespace to explicit namespace.
     *
     * @return node's namespace
     */
    public String namespace() {
        return nameSpace;
    }

    /**
     * Creates and returns a deep copy of this object.
     *
     * @return cloned copy
     * @throws CloneNotSupportedException if the object's class does not
     *                                    support the {@code Cloneable} interface
     */
    @Override
    public SchemaId clone() throws CloneNotSupportedException {
        return (SchemaId) super.clone();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nameSpace);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SchemaId) {
            SchemaId that = (SchemaId) obj;
            return Objects.equals(name, that.name) &&
                    Objects.equals(nameSpace, that.nameSpace);
        }
        return false;
    }

    @Override
    public int compareTo(SchemaId o) {
        return ComparisonChain.start()
                .compare(this.name, o.name)
                .compare(this.nameSpace, o.nameSpace)
                .result();
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("name", name)
                .add("nameSpace", nameSpace)
                .toString();
    }
}
