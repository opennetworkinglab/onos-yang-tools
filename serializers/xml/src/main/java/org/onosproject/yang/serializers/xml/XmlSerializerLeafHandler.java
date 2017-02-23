/*
 * Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang.serializers.xml;

import org.dom4j.Element;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.LeafNode;

import java.util.Stack;

/**
 * Represents a leaf node handler in XML serializer.
 */
public class XmlSerializerLeafHandler extends XmlSerializerHandler {

    @Override
    public void setXmlValue(DataNode node, Stack<Element> elementStack) {
        Object value = ((LeafNode) node).value();
        if (value != null) {
            elementStack.peek().setText(value.toString());
        }
    }
}
