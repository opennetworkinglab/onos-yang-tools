/*
 * Copyright 2016-present Open Networking Foundation
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

import static org.onosproject.yang.model.ModelConstants.NON_KEY_LEAF;

/**
 * Abstraction of an entity which represents leaf data tree node.
 */
public final class LeafNode extends DataNode {

    /**
     * Leaf node value.
     */
    private Object value;

    /**
     * Leaf value namespace, it will be non null only if leaf type is
     * identityref.
     */
    private String valueNamespace;

    /**
     * Type of leaf.
     */
    private LeafType leafType;

    /**
     * Returns value contained in leaf node.
     *
     * @return value contained in leaf node
     */
    public Object value() {
        return value;
    }

    /**
     * Returns value namespace contained in leaf node.
     *
     * @return value namespace contained in leaf node
     */
    public String valueNamespace() {
        return valueNamespace;
    }

    /**
     * Returns type of leaf.
     *
     * @return type of leaf
     */
    public LeafType leafType() {
        return leafType;
    }

    /**
     * Returns value as string, for usage in serializers.
     *
     * @return string representation of value
     */
    public String asString() {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return "{" +
                "key=" + key() + ", " +
                "value=" + asString() +
                "valueNamespace=" + valueNamespace() +
                "leafType=" + leafType +
                "}";
    }

    /**
     * Creates an instance of leaf node.
     *
     * @param builder leaf node builder
     */
    public LeafNode(Builder builder) {
        super(builder);
        value = builder.value;
        valueNamespace = builder.valueNamespace;
        leafType = builder.leafType;
    }

    /**
     * Returns data node builder instance.
     *
     * @param name      name of node
     * @param nameSpace namespace of node
     * @return data node builder instance
     */
    public static Builder builder(String name, String nameSpace) {
        return new Builder(name, nameSpace);
    }

    /**
     * Returns data node copy builder.
     *
     * @return data node copy builder
     */
    @Override
    public Builder copyBuilder() {
        return new Builder(this);
    }

    /**
     * Builder with get and set functions to build leaf node,
     * builder will be used both to create leaf node from scratch or from a
     * given leaf node.
     */
    public static final class Builder extends DataNode.Builder<Builder> {

        /**
         * Leaf node value.
         */
        private Object value;

        /**
         * Leaf value namespace, it will be non null only if leaf type is
         * identityref.
         */
        private String valueNamespace;

        /**
         * Type of leaf.
         */
        private LeafType leafType;

        /**
         * Creates an instance of data node builder.
         *
         * @param name      name of node
         * @param namespace namespace of node
         */
        protected Builder(String name, String namespace) {
            keyBuilder = NodeKey.builder().schemaId(name, namespace);
        }

        /**
         * Creates an instance of leaf node copy builder.
         *
         * @param node old leaf node
         */
        public Builder(LeafNode node) {
            super(node);
            value = node.value;
            valueNamespace = node.valueNamespace;
            leafType = node.leafType;
        }

        /**
         * Sets value of leaf node builder.
         *
         * @param value value
         * @return leaf node builder
         */
        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        /**
         * Sets valueNamepsace of leaf node builder.
         *
         * @param valueNamespace value namespace
         * @return leaf node builder
         */
        public Builder valueNamespace(String valueNamespace) {
            this.valueNamespace = valueNamespace;
            return this;
        }

        /**
         * Sets leaf type of leaf node builder.
         *
         * @param leafType type of leaf
         * @return leaf node builder
         */
        public Builder leafType(LeafType leafType) {
            this.leafType = leafType;
            return this;
        }

        @Override
        public InnerNode.Builder createChildBuilder(String name, String nameSpace) {
            throw new IllegalStateException("leaf node can't have a child " +
                                                    "node");
        }

        @Override
        public LeafNode.Builder createChildBuilder(String name, String nameSpace,
                                                   Object value) {
            throw new IllegalStateException("leaf node can't have a child " +
                                                    "node");
        }

        @Override
        public LeafNode.Builder createChildBuilder(String name, String nameSpace,
                                                   Object value, String valNamespace) {
            throw new IllegalStateException("leaf node can't have a child " +
                                                    "node");
        }

        @Override
        public InnerNode.Builder deleteChild(NodeKey key) {
            throw new IllegalStateException("leaf node can't have a child " +
                                                    "node");
        }

        @Override
        public InnerNode.Builder getChildBuilder(NodeKey key) {
            throw new IllegalStateException("leaf node can't have a child " +
                                                    "node");
        }

        @Override
        public InnerNode.Builder addKeyLeaf(String name, String nameSpace, Object val) {
            throw new IllegalStateException("leaf node can't have a key " +
                                                    "leaves node");
        }

        @Override
        public Builder addLeafListValue(Object val) {
            LeafListKey.LeafListKeyBuilder leafListKeyBuilder;
            if (!(keyBuilder instanceof LeafListKey.LeafListKeyBuilder)) {
                if (keyBuilder instanceof ListKey.ListKeyBuilder) {
                    throw new ModelException(NON_KEY_LEAF);
                }

                leafListKeyBuilder = new LeafListKey.LeafListKeyBuilder();
                NodeKey key = keyBuilder.build();
                leafListKeyBuilder.schemaId(key.schemaId());
            } else {
                leafListKeyBuilder = (LeafListKey.LeafListKeyBuilder) keyBuilder;
            }

            leafListKeyBuilder.value(val);
            keyBuilder = leafListKeyBuilder;
            return this;
        }

        /**
         * Builds a leaf node object.
         *
         * @return leaf node
         */
        @Override
        public LeafNode build() {
            if (type == null) {
                throw new IllegalStateException("node should have a type.");
            }
            if (key == null) {
                key = keyBuilder.build();
            }
            return new LeafNode(this);
        }
    }
}