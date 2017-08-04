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

import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Represents default application module information.
 */
public class DefaultAppModuleInfo implements AppModuleInfo {

    private final List<String> features;
    private Class<?> module;

    /**
     * Creates an instance of application module information.
     *
     * @param m module's class
     * @param f supported features
     */
    public DefaultAppModuleInfo(Class<?> m, List<String> f) {
        features = f;
        module = m;
    }

    @Override
    public Class<?> getModuleClass() {
        return module;
    }

    @Override
    public List<String> getFeatureList() {
        return features;
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
