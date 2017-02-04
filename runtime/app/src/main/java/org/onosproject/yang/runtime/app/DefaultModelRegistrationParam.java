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

import org.onosproject.yang.YangModel;
import org.onosproject.yang.YangModuleId;
import org.onosproject.yang.runtime.api.AppModuleInfo;
import org.onosproject.yang.runtime.api.ModelRegistrationParam;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents default model registration parameter.
 */
public class DefaultModelRegistrationParam implements ModelRegistrationParam {

    private YangModel model;
    private final Map<YangModuleId, AppModuleInfo> appInfoMap;

    /**
     * Creates an instance of model registration parameter.
     */
    public DefaultModelRegistrationParam() {
        appInfoMap = new LinkedHashMap<>();
    }

    @Override
    public YangModel getYangModel() {
        return model;
    }

    @Override
    public void setYangModel(YangModel model) {
        checkNotNull(model);
        this.model = model;
    }

    @Override
    public AppModuleInfo getAppModuleInfo(YangModuleId id) {
        return appInfoMap.get(id);
    }

    @Override
    public void addAppModuleInfo(YangModuleId id, AppModuleInfo info) {
        appInfoMap.put(id, info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appInfoMap, model);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        DefaultModelRegistrationParam that = (DefaultModelRegistrationParam) obj;
        if (appInfoMap.size() == that.appInfoMap.size()) {
            for (Map.Entry<YangModuleId, AppModuleInfo> entry : appInfoMap.entrySet()) {
                if (!that.appInfoMap.containsKey(entry.getKey()) ||
                        !that.appInfoMap.containsValue(entry.getValue())) {
                    return false;
                }
            }
            return Objects.equals(model, that.model);
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("model", model)
                .add("appInfo", appInfoMap)
                .toString();
    }
}
