/*
 *  Copyright 2017-present Open Networking Foundation
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

package org.onosproject.yang.serializers.json;

import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.yang.model.LeafType;

import java.util.Set;

/**
 * Abstraction of an entity which provides interfaces to build and obtain JSON
 * data tree.
 */
public interface JsonBuilder {

    /**
     * Adds a to half(a left brace/bracket and the field name) of a JSON
     * object/array to the JSON tree. This method is used by protocols which
     * knows the nature (object/array) of node.
     *
     * @param nodeName name of child to be added
     * @param nodeType the type of the child
     */
    void addNodeTopHalf(String nodeName, JsonNodeType nodeType);

    /**
     * Adds a child with value and a comma to the JSON tree.
     * Protocols unaware of nature of node (single/multiple) will use it to add
     * both single instance and multi instance node. Protocols aware of nature
     * of node will use it for single instance value node addition.
     *
     * @param nodeName     name of child to be added
     * @param value        value of the child node
     * @param valNamespace value namespace
     * @param type         leaf value type
     */
    void addNodeWithValueTopHalf(String nodeName, String value,
                                 String valNamespace, LeafType type);

    /**
     * Adds a child with list of values to JSON data tree. This method is
     * used by protocols which knows the nature (object/array) of node for
     * ArrayNode addition.
     *
     * @param nodeName name of child to be added
     * @param sets     the value list of the child
     */
    void addNodeWithSetTopHalf(String nodeName, Set<String> sets);

    /**
     * Adds value to a leaf list node.
     *
     * @param value        value to be added
     * @param valNamespace value namespace
     * @param type         leaf-list value type
     */
    void addValueToLeafListNode(String value, String valNamespace, LeafType type);

    /**
     * Adds the bottom half(a right brace/bracket) of a JSON
     * object/array to the JSON tree. for the text, a comma should be
     * taken out.
     *
     * @param nodeType the type of the child
     */
    void addNodeBottomHalf(JsonNodeType nodeType);

    /**
     * Returns the JSON tree after build operations in the format of string.
     *
     * @return the final string JSON tree after build operations
     */
    String getTreeString();

    /**
     * Returns the JSON tree after build operations in the format of string.
     *
     * @return the final ObjectNode JSON tree after build operations
     */
    ObjectNode getTreeNode();

    /**
     * Returns the YANG module name of the JSON subtree that the builder
     * is currently building. The YANG module name represents the name
     * space of the subtree.
     *
     * @return YANG module name
     */
    String subTreeModuleName();

    /**
     * Updates the YANG module name of the JSON subtree that the builder
     * is currently building. The YANG module name represents the name
     * space of the subtree. This function may be called when the builder
     * starts to build a data node.
     *
     * @param moduleName YANG module name of the current subtree
     */
    void pushModuleName(String moduleName);

    /**
     * Removes the YANG module name of the JSON subtree that the builder
     * is currently building. This function may be called when the builder
     * finishes building a data node.
     */
    void popModuleName();

    /**
     * Initializes the output JSON and emits the JSON starting symbol
     * (e.g., the left curly bracket). This method should be the first method
     * to be called when a JSON building process starts.
     */
    void initializeJson();

    /**
     * Finalizes the output JSON and emits the JSON terminating symbol
     * (e.g., the right curly bracket). This method should be the last method
     * to be called when a JSON building process finishes.
     *
     * @param isRootTypeMultiInstance true if the root node of the JSON
     *                                tree has the multi-instance node type
     */
    void finalizeJson(boolean isRootTypeMultiInstance);
}
