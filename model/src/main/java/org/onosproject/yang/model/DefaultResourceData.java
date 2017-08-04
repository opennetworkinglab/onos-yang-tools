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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.hash;

/**
 * Representation of default implementation of resource data.
 */
public class DefaultResourceData implements ResourceData {

    private List<DataNode> nodes;
    private ResourceId resourceId;

    /**
     * Creates an instance of resource data.
     *
     * @param b resource data builder
     */
    protected DefaultResourceData(Builder b) {
        resourceId = b.resourceId;
        nodes = b.nodes;
    }

    @Override
    public List<DataNode> dataNodes() {
        return nodes;
    }

    @Override
    public ResourceId resourceId() {
        return resourceId;
    }

    @Override
    public int hashCode() {
        return hash(resourceId, nodes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DefaultResourceData) {
            DefaultResourceData that = (DefaultResourceData) obj;
            return Objects.equals(resourceId, that.resourceId) &&
                    Objects.equals(nodes, that.nodes);
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("resourceId", resourceId)
                .add("nodes", nodes)
                .toString();
    }

    /**
     * Retrieves a new resource data builder.
     *
     * @return resource data builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Represents implementation of resource data builder.
     */
    public static class Builder implements ResourceData.Builder {

        private List<DataNode> nodes;
        private ResourceId resourceId;

        /**
         * Creates an instance of resource data builder.
         */
        protected Builder() {
            nodes = new LinkedList<>();
        }

        @Override
        public ResourceData.Builder addDataNode(DataNode node) {
            nodes.add(node);
            return this;
        }

        @Override
        public ResourceData.Builder resourceId(ResourceId id) {
            resourceId = id;
            return this;
        }

        @Override
        public ResourceData build() {
            return new DefaultResourceData(this);
        }
    }
}
