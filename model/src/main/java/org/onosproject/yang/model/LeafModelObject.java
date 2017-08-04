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

import java.util.LinkedList;
import java.util.List;

/**
 * Representation of an entity that provides common basis to specify atomic
 * model.
 */
public final class LeafModelObject extends ModelObject {

    private LeafIdentifier leafIdentifier;
    private List<Object> values;

    /**
     * Creates an instance of leaf model object.
     */
    public LeafModelObject() {
        super(ModelObjectType.ATOMIC);
        values = new LinkedList<>();
    }

    /**
     * Returns leaf identifier.
     *
     * @return leaf identifier
     */
    public LeafIdentifier leafIdentifier() {
        return leafIdentifier;
    }

    /**
     * Sets leaf identifier.
     *
     * @param id leaf identifier
     */
    public void leafIdentifier(LeafIdentifier id) {
        leafIdentifier = id;
    }

    /**
     * Returns value of leaf, it will be single value for leaf and can be
     * single or list for leaf-list.
     *
     * @return value(s)
     */
    public List<Object> values() {
        return values;
    }

    /**
     * Sets list of values. For leaf it will always be single value.
     *
     * @param v value(s)
     */
    public void values(List<Object> v) {
        values = v;
    }

    /**
     * Adds value to list.
     *
     * @param v value
     */
    public void addValue(Object v) {
        values.add(v);
    }
}
