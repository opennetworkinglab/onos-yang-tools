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

package org.onosproject.yang.serializers.xml;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.LeafNode;

import java.util.Stack;

import static org.onosproject.yang.serializers.xml.XmlSerializerLeafHandler.XML_PREFIX;

/**
 * Represents an serializer handler to process the XML content and add
 * element to the stack.
 */
public abstract class XmlSerializerHandler {

    /**
     * Sets the namespace and tag name in element tree maintained in stack.
     *
     * @param dataNode data node
     * @param elementStack element tree stack
     * @return Element
     */
    Element processXmlContext(DataNode dataNode,
                              Stack<Element> elementStack) {

        Element newElement = updateNameAndNamespace(dataNode,
                                                    elementStack);
        return newElement;
    }

    /**
     * Returns the new element name by updating tag name and namespace.
     *
     * @param node YDT context node
     * @param elementStack dom elements
     * @return new element name by updating tag name and namespace
     */
    Element updateNameAndNamespace(DataNode node,
                                   Stack<Element> elementStack) {
        String nameSpace = null;
        String name = null;
        if (node.key() != null && node.key().schemaId() != null) {
            nameSpace = node.key().schemaId().namespace();
            name = node.key().schemaId().name();
        }
        String valueNs = null;
        if (node instanceof LeafNode) {
            valueNs = ((LeafNode) node).valueNamespace();
        }

        if (elementStack.isEmpty()) {
            Element rootElement = DocumentHelper.createDocument()
                    .addElement(name);
            if (nameSpace != null) {
                rootElement.add(Namespace.get(nameSpace));
            }
            return rootElement;
        } else {
            /*
             * If element stack is not empty then root element is already
             * created.
             */
            Element xmlElement = elementStack.peek();
            Element newElement;
            if (nameSpace != null) {
                newElement = xmlElement.addElement(name,
                                                   nameSpace);
            } else {
                newElement = xmlElement.addElement(name);
            }
            if (valueNs != null) {
                newElement.addNamespace(XML_PREFIX, valueNs);
            }
            return newElement;
        }
    }

    /**
     * Sets the leaf value in the current element maintained in stack.
     * Default behaviour is to do nothing.
     *
     * @param domElementStack current element node in the stack
     * @param dataNode        data node
     */
    public void setXmlValue(DataNode dataNode,
                            Stack<Element> domElementStack) {
    }
}
