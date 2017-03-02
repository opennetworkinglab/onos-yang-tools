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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Represents default YANG model implementation.
 */
public class DefaultYangModel implements YangModel {

    private final Map<YangModuleId, YangModule> moduleMap;

    /**
     * Creates an instance of YANG model.
     */
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
            ids.add(entry.getKey());
        }
        return ids;
    }

    @Override
    public YangModule getYangModule(YangModuleId id) {
        return moduleMap.get(id);
    }

    @Override
    public void addModule(YangModuleId id, YangModule module) {
        moduleMap.put(id, module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleMap);
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
        return true;
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("model", moduleMap)
                .toString();
    }
}
