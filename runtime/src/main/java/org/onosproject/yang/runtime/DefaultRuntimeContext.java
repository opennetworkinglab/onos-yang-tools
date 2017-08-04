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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.hash;

/**
 * Representation of default implementation of runtime context.
 */
public class DefaultRuntimeContext implements RuntimeContext {

    private String dataFormat;
    private List<Annotation> annotations;

    /**
     * Creates an instance of data node.
     *
     * @param b data node builder
     */
    protected DefaultRuntimeContext(Builder b) {
        dataFormat = b.dataFormat;
        annotations = b.annotations;
    }

    @Override
    public String getDataFormat() {
        return dataFormat;
    }

    @Override
    public List<Annotation> getProtocolAnnotations() {
        return annotations;
    }

    @Override
    public int hashCode() {
        return hash(dataFormat, annotations);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DefaultRuntimeContext) {
            DefaultRuntimeContext that = (DefaultRuntimeContext) obj;
            return Objects.equals(dataFormat, that.dataFormat) &&
                    Objects.equals(annotations, that.annotations);
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("dataFormat", dataFormat)
                .add("annotations", annotations)
                .toString();
    }

    /**
     * Retrieves a new runtime context builder.
     *
     * @return runtime context builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Represents implementation of runtime context builder.
     */
    public static class Builder implements RuntimeContext.Builder {

        private String dataFormat;
        private List<Annotation> annotations;

        /**
         * Creates an instance of runtime context builder.
         */
        public Builder() {
            annotations = new LinkedList<>();
        }

        @Override
        public RuntimeContext.Builder setDataFormat(String df) {
            dataFormat = df;
            return this;
        }

        @Override
        public RuntimeContext.Builder addAnnotation(Annotation a) {
            annotations.add(a);
            return this;
        }

        @Override
        public RuntimeContext build() {
            return new DefaultRuntimeContext(this);
        }
    }
}
