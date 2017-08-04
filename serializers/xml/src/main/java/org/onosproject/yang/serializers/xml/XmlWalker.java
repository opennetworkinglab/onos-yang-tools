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

/**
 * Abstraction of an entity which provides interfaces for XML walk.
 * This interface serve as common tools for anyone who needs to parse the XML
 * node with depth-first algorithm.
 */
interface XmlWalker {

    /**
     * Walks the XML data tree. Protocols implements XML listener service
     * and walks XML tree with input as implemented object. XML walker provides
     * call backs to implemented methods.
     *
     * @param listener    XML listener implemented by the protocol
     * @param walkElement node(element) of the XML data tree
     * @param rootElement root node(element) of the XML data tree
     */
    void walk(XmlListener listener, Element walkElement,
              Element rootElement);
}
