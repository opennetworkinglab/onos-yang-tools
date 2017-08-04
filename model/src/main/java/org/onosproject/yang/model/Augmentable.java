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

import java.util.Map;

/**
 * Abstraction of augmentation behavior generated augmentable nodes interfaces
 * will inherit it.
 */
public interface Augmentable {

    /**
     * Adds the specified augmentation to this model object.
     *
     * @param obj model object of augmentation
     */
    void addAugmentation(InnerModelObject obj);

    /**
     * Removes the specified augmentation to this model object.
     *
     * @param obj model object of augmentation
     */
    void removeAugmentation(InnerModelObject obj);

    /**
     * Returns the map of augmentations available to this model object.
     *
     * @return map of augmentations
     */
    Map<Class<? extends InnerModelObject>, InnerModelObject> augmentations();

    /**
     * Returns the augmentation for to a given augmentation class.
     *
     * @param c   augmentation class
     * @param <T> augmentation class type
     * @return augmentation object if available, null otherwise
     */
    <T extends InnerModelObject> T augmentation(Class<T> c);
}
