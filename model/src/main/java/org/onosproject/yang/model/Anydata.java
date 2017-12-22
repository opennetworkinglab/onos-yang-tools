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

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Abstraction of anydata behavior generated anydata nodes interfaces
 * will inherit it.
 */
public abstract class Anydata extends InnerModelObject {

    private final ConcurrentMap<Class<? extends InnerModelObject>,
            List<InnerModelObject>> anydatas =
            new ConcurrentHashMap<>();

    /**
     * Adds the specified anydata to this model object.
     *
     * @param obj model object of anydata
     */
    public void addAnydata(InnerModelObject obj) {
        List<InnerModelObject> node = anydatas.get(obj.getClass());
        if (node != null && !node.isEmpty()) {
            if (node.get(0) instanceof MultiInstanceObject && obj instanceof
                    MultiInstanceObject) {
                node.add(obj);
            } else {
                throw new IllegalArgumentException(
                        "ANYDATA error: Object already exist");
            }
        } else {
            node = new ArrayList<>();
            node.add(obj);
        }
        anydatas.put(obj.getClass(), node);
    }

    /**
     * Removes the specified anydata to this model object.
     *
     * @param obj model object of anydata
     */
    public void removeAnydata(InnerModelObject obj) {
        anydatas.remove(obj.getClass());
    }

    /**
     * Returns the map of anydatas available to this model object.
     *
     * @return map of anydatas
     */
    public Map<Class<? extends InnerModelObject>, List<InnerModelObject>> anydatas() {
        return ImmutableMap.copyOf(anydatas);
    }

    /**
     * Returns the anydata for to a given anydata class.
     *
     * @param c any class which extends the InnerModelObject
     * @return anydata object if available, null otherwise
     */
    public List<InnerModelObject> anydata(Class<? extends InnerModelObject> c) {
        return anydatas.get(c);
    }
}
