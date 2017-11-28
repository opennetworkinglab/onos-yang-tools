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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Abstraction of anydata behavior generated anydata nodes interfaces
 * will inherit it.
 */
public abstract class Anydata extends InnerModelObject {

    private final ConcurrentMap<Class<? extends InnerModelObject>, InnerModelObject> anydatas =
            new ConcurrentHashMap<>();

    /**
     * Adds the specified anydata to this model object.
     *
     * @param obj model object of anydata
     */
    public void addAnydata(InnerModelObject obj) {
        anydatas.put(obj.getClass(), obj);
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
    public Map<Class<? extends InnerModelObject>, InnerModelObject> anydatas() {
        return ImmutableMap.copyOf(anydatas);
    }

    /**
     * Returns the anydata for to a given anydata class.
     *
     * @param c   anydata class
     * @param <T> anydata class type
     * @return anydata object if available, null otherwise
     */
    public <T extends InnerModelObject> T anydata(Class<T> c) {
        return (T) anydatas.get(c);
    }
}
