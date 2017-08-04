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

import org.onosproject.yang.model.ResourceData;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.hash;

/**
 * Representation of default implementation of composite data.
 */
public class DefaultCompositeData implements CompositeData {

    private ResourceData data;
    private List<AnnotatedNodeInfo> annotationsInfo;

    /**
     * Creates an instance of composite data.
     *
     * @param b composite data builder
     */
    protected DefaultCompositeData(Builder b) {
        data = b.data;
        annotationsInfo = b.annotationsInfo;
    }

    @Override
    public ResourceData resourceData() {
        return data;
    }

    @Override
    public List<AnnotatedNodeInfo> annotatedNodesInfo() {
        return annotationsInfo;
    }

    @Override
    public int hashCode() {
        return hash(data, annotationsInfo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DefaultCompositeData) {
            DefaultCompositeData that = (DefaultCompositeData) obj;
            return Objects.equals(data, that.data) &&
                    Objects.equals(annotationsInfo, that.annotationsInfo);
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("data", data)
                .add("annotationsInfo", annotationsInfo)
                .toString();
    }

    /**
     * Retrieves a new composite data builder.
     *
     * @return composite data builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Represents implementation of composite data builder.
     */
    public static class Builder implements CompositeData.Builder {

        private ResourceData data;
        private List<AnnotatedNodeInfo> annotationsInfo;

        /**
         * Creates an instance of composite data builder.
         */
        protected Builder() {
            annotationsInfo = new LinkedList<>();
        }

        @Override
        public Builder resourceData(ResourceData rd) {
            data = rd;
            return this;
        }

        @Override
        public Builder addAnnotatedNodeInfo(AnnotatedNodeInfo info) {
            annotationsInfo.add(info);
            return this;
        }

        @Override
        public CompositeData build() {
            return new DefaultCompositeData(this);
        }
    }
}
