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

package org.onosproject.yang.runtime.app;

import org.onosproject.yang.runtime.api.AppModuleInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents default application module information.
 */
public class DefaultAppModuleInfo implements AppModuleInfo {

    private Class<?> module;
    private final List<String> features;

    /**
     * Creates an instance of application module information.
     */
    public DefaultAppModuleInfo() {
        features = new ArrayList<>();
    }

    @Override
    public Class<?> getModuleClass() {
        return module;
    }

    @Override
    public void setModuleClass(Class<?> moduleClass) {
        checkNotNull(moduleClass);
        module = moduleClass;
    }

    @Override
    public List<String> getFeatureList() {
        return features;
    }

    @Override
    public void addFeature(String feature) {
        features.add(feature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, features);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        DefaultAppModuleInfo that = (DefaultAppModuleInfo) obj;
        return Objects.equals(module, that.module);
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("moduleClass", module)
                .add("features", features)
                .toString();
    }
}
