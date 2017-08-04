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

package org.onosproject.yang.runtime;

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.hash;

/**
 * Representation of default implementation of annotation.
 */
public class DefaultAnnotation implements Annotation {

    private String name;
    private String value;

    /**
     * Creates an instance of annotation.
     *
     * @param n annotation name
     * @param v annotation value
     */
    public DefaultAnnotation(String n, String v) {
        name = n;
        value = v;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public int hashCode() {
        return hash(name, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DefaultAnnotation) {
            DefaultAnnotation that = (DefaultAnnotation) obj;
            return Objects.equals(name, that.name) &&
                    Objects.equals(value, that.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("name", name)
                .add("value", value)
                .toString();
    }
}
