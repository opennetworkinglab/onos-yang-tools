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

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Abstraction of an entity that provides common basis for all POJOs which are
 * generated from a YANG model.
 */
public abstract class InnerModelObject extends ModelObject {

    private ConcurrentMap<Class<? extends InnerModelObject>, InnerModelObject> augments =
            new ConcurrentHashMap<>();

    /**
     * Creates an instance of Inner model object.
     */
    protected InnerModelObject() {
        super(ModelObjectType.NON_ATOMIC);
    }

    /**
     * Adds the specified augmentation to this model object.
     *
     * @param obj model object of augmentation
     */
    public void addAugmentation(InnerModelObject obj) {
        augments.put(obj.getClass(), obj);
    }

    /**
     * Removes the specified augmentation to this model object.
     *
     * @param obj model object of augmentation
     */
    public void removeAugmentation(InnerModelObject obj) {
        augments.remove(obj.getClass());
    }

    /**
     * Returns the map of augmentations available to this model object.
     *
     * @return map of augmentations
     */
    public Map<Class<? extends InnerModelObject>, InnerModelObject> augmentations() {
        return ImmutableMap.copyOf(augments);
    }

    /**
     * Returns the augmentation for to a given augmentation class.
     *
     * @param c   augmentation class
     * @param <T> augmentation class type
     * @return augmentation object if available, null otherwise
     */
    public <T extends InnerModelObject> T augmentation(Class<T> c) {
        return (T) augments.get(c);
    }
}
