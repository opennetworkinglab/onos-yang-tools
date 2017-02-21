/*
 *  Copyright 2017-present Open Networking Laboratory
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Represents implementation of interfaces to build and obtain JSON data tree.
 */
public class DefaultJsonBuilder implements JsonBuilder {
    private Logger log = LoggerFactory.getLogger(getClass());
    private StringBuilder treeString;
    private static final String LEFT_BRACE = "{";
    private static final String RIGHT_BRACE = "}";
    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String QUOTE = "\"";


    public DefaultJsonBuilder(String rootName) {
        checkNotNull(rootName);
        this.treeString = new StringBuilder(rootName);
    }

    public DefaultJsonBuilder() {
        this.treeString = new StringBuilder();
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
    public void addNodeWithValueTopHalf(String nodeName, String value) {
        if (isNullOrEmpty(nodeName)) {
            return;
        }
        appendField(nodeName);
        if (value.isEmpty()) {
            return;
        }
        treeString.append(QUOTE);
        treeString.append(value);
        treeString.append(QUOTE);
        treeString.append(COMMA);
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
    public void addValueToLeafListNode(String value) {
        if (isNullOrEmpty(value)) {
            return;
        }

        treeString.append(QUOTE);
        treeString.append(value);
        treeString.append(QUOTE);
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
        removeCommaIfExist();
        removeFirstFieldNameIfExist();
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
            node = (ObjectNode) (new ObjectMapper()).readTree(getTreeString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
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
