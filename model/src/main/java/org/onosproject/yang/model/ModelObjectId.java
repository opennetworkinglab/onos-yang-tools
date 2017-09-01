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
 * Representation of an entity which identifies a resource in the generated
 * java code. It is a list of atomic path to identify the node
 * hierarchy to reach a resource in the instance tree.
 */
public final class ModelObjectId {

    /**
     * List of atomic paths.
     */
    private final List<AtomicPath> atomicPaths;

    /**
     * Create object from builder.
     *
     * @param builder initialized builder
     */
    private ModelObjectId(Builder builder) {
        atomicPaths = builder.atomicPathList;
    }

    /**
     * Returns the list of atomic used to uniquely identify the node.
     *
     * @return atomic path uniquely identifying the branch
     */
    public List<AtomicPath> atomicPaths() {
        return atomicPaths;
    }

    /**
     * Retrieves a new path identifier builder.
     *
     * @return path identifier builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int hashCode() {
        return hash(atomicPaths);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ModelObjectId) {
            ModelObjectId that = (ModelObjectId) obj;
            return Objects.equals(this.atomicPaths, that.atomicPaths);
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("atomicPaths", atomicPaths)
                .toString();
    }

    /**
     * Builder to construct java path identifier.
     */
    public static class Builder {

        private final List<AtomicPath> atomicPathList = new LinkedList<>();

        /**
         * Adds the node's identity for container.
         *
         * @param container generated default container class
         * @param <T>       generated java class which extends model object
         * @return updated builder pointing to the specified schema location
         */
        public <T extends InnerModelObject> Builder addChild(Class<T> container) {
            atomicPathList.add(new SingleInstanceNode<>(container));
            return this;
        }

        /**
         * Adds the node's identity for list.
         *
         * @param list generated default list class
         * @param key  key object to identify the list instance
         * @param <T>  generated java class which extends model object
         * @param <K>  generated java class for key to uniquely
         *             identify the list
         * @return updated builder pointing to the specified schema location
         */
        public <T extends InnerModelObject & MultiInstanceObject<K>,
                K extends KeyInfo<T>> Builder addChild(Class<T> list, K key) {
            atomicPathList.add(new MultiInstanceNode<>(list, key));
            return this;
        }

        /**
         * Adds the node's identity for leaf.
         *
         * @param leaf leaf attribute in generated java
         * @param <E>  generated leaf identifier for leaf
         * @return updated builder pointing to the specified schema location
         */
        public <E extends LeafIdentifier> Builder addChild(E leaf) {
            atomicPathList.add(new SingleInstanceLeaf<>(leaf));
            return this;
        }

        /**
         * Adds the node's identity for leaf list.
         *
         * @param leafList leaf-list attribute in generated java.
         * @param value    value to identify the leaf-list instance
         * @param <E>      generated leaf identifier for leaf-list
         * @return updated builder pointing to the specified schema location
         */
        public <E extends LeafIdentifier> Builder addChild(E leafList,
                                                           Object value) {
            atomicPathList.add(new MultiInstanceLeaf<>(leafList, value));
            return this;
        }

        /**
         * Builds a path identifier to based on set path information of
         * the generated java class.
         *
         * @return built path identifier
         */
        public ModelObjectId build() {
            return new ModelObjectId(this);
        }
    }
}
