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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Represents default YANG model implementation.
 */
public class DefaultYangModel implements YangModel, Serializable {

    private final Map<YangModuleId, YangModule> moduleMap;
    private String modelId;

    /**
     * Creates an instance of YANG model.
     *
     * @param b YANG model builder
     */
    private DefaultYangModel(Builder b) {
        modelId = b.modelId;
        moduleMap = b.moduleMap;
    }

    /**
     * Creates an instance of YANG model.
     */
    @Deprecated
    public DefaultYangModel() {
        moduleMap = new LinkedHashMap<>();
    }

    @Override
    public Set<YangModule> getYangModules() {
        Set<YangModule> modules = new LinkedHashSet<>();
        for (Map.Entry<YangModuleId, YangModule> entry : moduleMap.entrySet()) {
            modules.add(entry.getValue());
        }
        return modules;
    }

    @Override
    public Set<YangModuleId> getYangModulesId() {
        Set<YangModuleId> ids = new LinkedHashSet<>();
        for (Map.Entry<YangModuleId, YangModule> entry : moduleMap.entrySet()) {
            if (!entry.getValue().isInterJar()) {
                ids.add(entry.getKey());
            }
        }
        return ids;
    }

    @Override
    public String getYangModelId() {
        return modelId;
    }

    @Override
    public YangModule getYangModule(YangModuleId id) {
        return moduleMap.get(id);
    }

    @Override
    @Deprecated
    public void addModule(YangModuleId id, YangModule module) {
        moduleMap.put(id, module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleMap, modelId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        DefaultYangModel that = (DefaultYangModel) obj;

        if (moduleMap.size() == that.moduleMap.size()) {
            for (Map.Entry<YangModuleId, YangModule> entry : moduleMap.entrySet()) {
                if (!that.moduleMap.containsKey(entry.getKey()) ||
                        !that.moduleMap.containsValue(entry.getValue())) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return Objects.equals(modelId, that.modelId);
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("model", moduleMap)
                .add("modelId", modelId)
                .toString();
    }

    /**
     * Retrieves a new YANG model builder.
     *
     * @return YANG model builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Represents implementation of YANG model builder.
     */
    public static class Builder implements YangModel.Builder {
        private final Map<YangModuleId, YangModule> moduleMap;
        private String modelId;

        /**
         * Creates an instance of YANG model builder.
         */
        public Builder() {
            moduleMap = new LinkedHashMap<>();
        }

        @Override
        public Builder addModule(YangModuleId id, YangModule module) {
            moduleMap.put(id, module);
            return this;
        }

        @Override
        public Builder addModelId(String id) {
            modelId = id;
            return this;
        }

        @Override
        public YangModel build() {
            return new DefaultYangModel(this);
        }
    }
}
