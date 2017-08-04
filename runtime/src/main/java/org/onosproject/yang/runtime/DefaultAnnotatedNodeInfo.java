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

import org.onosproject.yang.model.ResourceId;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.hash;

/**
 * Representation of default implementation of annotated node information.
 */
public class DefaultAnnotatedNodeInfo implements AnnotatedNodeInfo {

    private ResourceId resourceId;
    private List<Annotation> annotations;

    /**
     * Creates an instance of data node.
     *
     * @param b data node builder
     */
    protected DefaultAnnotatedNodeInfo(Builder b) {
        resourceId = b.resourceId;
        annotations = b.annotations;
    }

    @Override
    public ResourceId resourceId() {
        return resourceId;
    }

    @Override
    public List<Annotation> annotations() {
        return annotations;
    }

    @Override
    public int hashCode() {
        return hash(resourceId, annotations);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DefaultAnnotatedNodeInfo) {
            DefaultAnnotatedNodeInfo that = (DefaultAnnotatedNodeInfo) obj;
            return Objects.equals(resourceId, that.resourceId) &&
                    Objects.equals(annotations, that.annotations);
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("resourceId", resourceId)
                .add("annotations", annotations)
                .toString();
    }

    /**
     * Retrieves a new annotated node info builder.
     *
     * @return annotated node info builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Represents implementation of annotated node information builder.
     */
    public static class Builder implements AnnotatedNodeInfo.Builder {

        private ResourceId resourceId;
        private List<Annotation> annotations;

        /**
         * Creates an instance of annotated node info builder.
         */
        public Builder() {
            annotations = new LinkedList<>();
        }

        @Override
        public Builder resourceId(ResourceId id) {
            resourceId = id;
            return this;
        }

        @Override
        public Builder addAnnotation(Annotation a) {
            annotations.add(a);
            return this;
        }

        @Override
        public AnnotatedNodeInfo build() {
            return new DefaultAnnotatedNodeInfo(this);
        }
    }
}
