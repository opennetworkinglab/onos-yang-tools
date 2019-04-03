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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.yang.model.LeafType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.Stack;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Represents implementation of interfaces to build and obtain JSON data tree.
 */
public class DefaultJsonBuilder implements JsonBuilder {
    private static final String LEFT_BRACE = "{";
    private static final String RIGHT_BRACE = "}";
    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String QUOTE = "\"";
    private static final String ROOT_MODULE_NAME = "ROOT";
    private Logger log = LoggerFactory.getLogger(getClass());
    private StringBuilder treeString;
    private Stack<String> moduleNameStack;

    public DefaultJsonBuilder(String rootName) {
        checkNotNull(rootName);
        treeString = new StringBuilder(rootName);
        moduleNameStack = new Stack<>();
    }

    public DefaultJsonBuilder() {
        treeString = new StringBuilder();
        moduleNameStack = new Stack<>();
    }

    @Override
    public void addNodeTopHalf(String nodeName, JsonNodeType nodeType) {
        appendField(nodeName);

        switch (nodeType) {
            case OBJECT:
                treeString.append(LEFT_BRACE);
                break;
            case ARRAY:
                treeString.append(LEFT_BRACKET);
                break;
            default:
                log.error("Unknown support type {} for this method.", nodeType);
        }
    }

    @Override
    public void addNodeWithValueTopHalf(String nodeName, String value,
                                        String valNamespace, LeafType type) {
        if (isNullOrEmpty(nodeName)) {
            return;
        }
        appendField(nodeName);
        if (valNamespace != null) {
            treeString.append(QUOTE);
            treeString.append(valNamespace);
            treeString.append(COLON);
        }
        appendLeafValue(type, value, valNamespace);
        treeString.append(COMMA);
    }

    /**
     * Appends the value with quotes orr without quotes based on given
     * leaf-type.
     *
     * @param type current leaf type
     * @param v    value
     * @param ns   namespace for value in case of identity ref
     */
    private void appendLeafValue(LeafType type, String v, String ns) {
        switch (type) {
            case INT8:
            case INT16:
            case UINT8:
            case INT32:
            case UINT16:
            case UINT32:
            case BOOLEAN:
                treeString.append(v);
                break;
            default:
                if (ns == null) {
                    treeString.append(QUOTE);
                }
                treeString.append(v);
                treeString.append(QUOTE);
        }
    }

    @Override
    public void addNodeWithSetTopHalf(String nodeName, Set<String> sets) {
        if (isNullOrEmpty(nodeName)) {
            return;
        }
        appendField(nodeName);
        treeString.append(LEFT_BRACKET);
        for (String el : sets) {
            treeString.append(QUOTE);
            treeString.append(el);
            treeString.append(QUOTE);
            treeString.append(COMMA);
        }
    }

    @Override
    public void addValueToLeafListNode(String v, String ns, LeafType t) {
        if (isNullOrEmpty(v)) {
            return;
        }
        if (ns != null) {
            treeString.append(QUOTE);
            treeString.append(ns);
            treeString.append(COLON);
        }
        appendLeafValue(t, v, ns);
        treeString.append(COMMA);
    }

    @Override
    public void addNodeBottomHalf(JsonNodeType nodeType) {

        switch (nodeType) {
            case OBJECT:
                removeCommaIfExist();
                treeString.append(RIGHT_BRACE);
                treeString.append(COMMA);
                break;
            case ARRAY:
                removeCommaIfExist();
                treeString.append(RIGHT_BRACKET);
                treeString.append(COMMA);
                break;
            case BINARY:
            case BOOLEAN:
            case MISSING:
            case NULL:
            case NUMBER:
            case POJO:
            case STRING:
                break;
            default:
                log.info("Unknown json node type {}", nodeType);
        }
    }

    @Override
    public String getTreeString() {
        return treeString.toString();
    }

    private void removeFirstFieldNameIfExist() {
        int index1 = treeString.indexOf(LEFT_BRACE);
        int index2 = treeString.indexOf(LEFT_BRACKET);
        if (index1 < 0 && index2 < 0) {
            return;
        }
        int index;

        if (index1 < 0) {
            index = index2;
        } else if (index2 < 0) {
            index = index1;
        } else {
            index = (index1 < index2) ? index1 : index2;
        }
        treeString.delete(0, index);
    }

    @Override
    public ObjectNode getTreeNode() {
        ObjectNode node = null;
        try {
            ObjectMapper m = new ObjectMapper();
            m.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            node = (ObjectNode) m.readTree(getTreeString());
        } catch (IOException e) {
            log.error("Error in processing the tree", e);
            log.debug("tree {}", getTreeString());
        }
        return node;
    }

    @Override
    public String subTreeModuleName() {
        return moduleNameStack.peek();
    }

    @Override
    public void pushModuleName(String moduleName) {
        moduleNameStack.push(moduleName);
    }

    @Override
    public void popModuleName() {
        moduleNameStack.pop();
    }

    @Override
    public void initializeJson() {
        if (!moduleNameStack.empty()) {
            moduleNameStack.removeAllElements();
        }
        moduleNameStack.push(ROOT_MODULE_NAME);
        treeString.setLength(0);
        treeString.append(LEFT_BRACE);
    }

    @Override
    public void finalizeJson(boolean isRootTypeMultiInstance) {
        removeCommaIfExist();

        if (isRootTypeMultiInstance) {
            /*
             * If the root node of the JSON tree is an array
             * type, we need to close the array with the right
             * bracket.
             */
            treeString.append(RIGHT_BRACKET);
        }

        treeString.append(RIGHT_BRACE);
    }

    private void appendField(String fieldName) {
        if (fieldName != null && !fieldName.isEmpty()) {
            treeString.append(QUOTE);
            treeString.append(fieldName);
            treeString.append(QUOTE);
            treeString.append(COLON);
        }
    }

    private void removeCommaIfExist() {
        if (treeString.charAt(treeString.length() - 1) == COMMA.charAt(0)) {
            treeString.deleteCharAt(treeString.length() - 1);
        }
    }
}
