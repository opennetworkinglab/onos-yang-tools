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

import org.onosproject.yang.model.YangModel;
import org.onosproject.yang.model.YangModuleId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Represents default model registration parameter.
 */
public class DefaultModelRegistrationParam implements ModelRegistrationParam {

    private final Map<YangModuleId, AppModuleInfo> appInfoMap;
    private YangModel model;

    /**
     * Creates an instance of model registration param.
     *
     * @param b model registration param builder
     */
    protected DefaultModelRegistrationParam(Builder b) {
        appInfoMap = b.appInfoMap;
        model = b.model;
    }

    @Override
    public YangModel getYangModel() {
        return model;
    }

    @Override
    public AppModuleInfo getAppModuleInfo(YangModuleId id) {
        return appInfoMap.get(id);
    }

    @Override
    public boolean ifAppInfoPresent() {
        return !appInfoMap.isEmpty();
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

    /**
     * Retrieves a new model registration parameter builder.
     *
     * @return model registration parameter builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Represents implementation of model registration parameter builder.
     */
    public static class Builder implements ModelRegistrationParam.Builder {

        private final Map<YangModuleId, AppModuleInfo> appInfoMap;
        private YangModel model;

        /**
         * Creates an instance of model registration parameter builder.
         */
        public Builder() {
            appInfoMap = new HashMap<>();
        }

        @Override
        public Builder addAppModuleInfo(
                YangModuleId id, AppModuleInfo info) {
            appInfoMap.put(id, info);
            return this;
        }

        @Override
        public ModelRegistrationParam.Builder setYangModel(YangModel m) {
            model = m;
            return this;
        }

        @Override
        public DefaultModelRegistrationParam build() {
            return new DefaultModelRegistrationParam(this);
        }
    }
}
