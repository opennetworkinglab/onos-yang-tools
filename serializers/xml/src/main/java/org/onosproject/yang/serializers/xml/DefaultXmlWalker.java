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

import org.dom4j.Element;

import java.util.Iterator;

import static org.onosproject.yang.serializers.xml.XmlNodeType.OBJECT_NODE;
import static org.onosproject.yang.serializers.xml.XmlNodeType.TEXT_NODE;

/**
 * Represents implementation of xml walker.
 */
public class DefaultXmlWalker implements XmlWalker {

    @Override
    public void walk(XmlListener listener, Element element,
                     Element rootElement) {
        try {

            listener.enterXmlElement(element, getElementType(element),
                                     rootElement);

            if (element.hasContent() && !element.isTextOnly()) {
                Iterator i = element.elementIterator();
                while (i.hasNext()) {
                    Element childElement = (Element) i.next();
                    walk(listener, childElement, rootElement);
                }
            }

            listener.exitXmlElement(element, getElementType(element),
                                    rootElement);
        } catch (Exception e) {
            throw new XmlSerializerException(e.getMessage());
        }
    }

    /**
     * Determine the type of an element.
     *
     * @param element to be analysed
     * @return type of the element
     */
    private XmlNodeType getElementType(Element element) {
        Element newElement = element.createCopy();
        newElement.remove(element.getNamespace());
        return newElement.hasContent() && newElement.isTextOnly() ?
                TEXT_NODE : OBJECT_NODE;
    }
}
