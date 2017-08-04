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
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafNode;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.runtime.Annotation;
import org.onosproject.yang.runtime.DataNodeListener;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Represents implementation of data node listener.
 */
class DataNodeXmlListener implements DataNodeListener {

    /**
     * Stack for element is maintained for hierarchical references, this is
     * used during data node walker and preparation of XML.
     */
    private final Stack<Element> elementStack = new Stack<>();

    /**
     * Root element of XML.
     */
    private Element rootElement;

    /**
     * XML string for data node.
     */
    private String xmlData = EMPTY_STRING;

    /**
     * Annotation map used to search list of annotations associated with
     * resource id.
     */
    private Map<ResourceId, List<Annotation>> annotationMap;

    /**
     * Resource id builder.
     */
    private ResourceId.Builder rIdBuilder;

    private static final String FORWARD_SLASH = "/";
    private static final String EMPTY_STRING = "";

    /**
     * Creates a new data node XML serializer listener.
     *
     * @param annotations annotation map with resource id as key
     * @param ridBuilder  resource id builder
     */
    DataNodeXmlListener(Map<ResourceId, List<Annotation>> annotations,
                        ResourceId.Builder ridBuilder) {
        annotationMap = annotations;
        rIdBuilder = ridBuilder;
    }

    /**
     * Sets the root XML element.
     *
     * @param rootElement root element
     */
    public void rootElement(Element rootElement) {
        this.rootElement = rootElement;
    }

    /**
     * Returns XML string.
     *
     * @return XML string
     */
    public String xmlData() {
        return xmlData;
    }

    @Override
    public void enterDataNode(DataNode node) {
        if (!isRootDataNode(node)) {
            SerializerHandlerFactory factory = SerializerHandlerFactory.instance();
            XmlSerializerHandler handler =
                    factory.getSerializerHandlerForContext(node);
            try {
                if (handler != null) {
                    Element element = handler.processXmlContext(node,
                                                                elementStack);
                    if (elementStack.isEmpty()) {
                        rootElement(element);
                    }
                    elementStack.push(element);

                    // search in map whether there is entry for this resource id
                    ResourceId id = getResourceId(node);
                    List<Annotation> annotations = annotationMap.get(id);

                    /**
                     * If there is annotations for given resource id then get
                     * list of annotations and add as attribute
                     */
                    if (annotations != null) {
                        for (Annotation annotation : annotations) {
                            element.addAttribute(annotation.name(),
                                                 annotation.value());
                        }
                    }
                }
            } catch (Exception e) {
                throw new XmlSerializerException(e.getMessage());
            }

            if (handler != null) {
                handler.setXmlValue(node, elementStack);
            }
        }
    }

    @Override
    public void exitDataNode(DataNode dataNode) {
        if (!elementStack.isEmpty() &&
                elementStack.peek().equals(rootElement)) {
            xmlData = xmlData + rootElement.asXML();
        }

        if (!isRootDataNode(dataNode)) {
            elementStack.pop();
            rIdBuilder.removeLastKey();
        }
    }

    /**
     * Returns resource id for the data node.
     *
     * @param dataNode data node
     * @return resource id for the data node
     */
    private ResourceId getResourceId(DataNode dataNode) {
        SchemaId schemaId = dataNode.key().schemaId();
        switch (dataNode.type()) {
            case MULTI_INSTANCE_LEAF_VALUE_NODE:
                Object valObject = ((LeafNode) dataNode).value();
                rIdBuilder = rIdBuilder.addLeafListBranchPoint(schemaId.name(),
                                                               schemaId.namespace(),
                                                               valObject);
                break;
            case MULTI_INSTANCE_NODE:
                rIdBuilder = rIdBuilder.addBranchPointSchema(schemaId.name(),
                                                             schemaId.namespace());
                NodeKey key = dataNode.key();
                if (key instanceof ListKey) {
                    List<KeyLeaf> keyLeaves = ((ListKey) key).keyLeafs();
                    if (keyLeaves != null) {
                        for (KeyLeaf keyLeaf : keyLeaves) {
                            SchemaId leafSchema = keyLeaf.leafSchema();
                            rIdBuilder = rIdBuilder.addKeyLeaf(leafSchema.name(),
                                                               leafSchema.namespace(),
                                                               keyLeaf.leafValue());
                        }
                    }
                }
                break;
            case SINGLE_INSTANCE_LEAF_VALUE_NODE:
            case SINGLE_INSTANCE_NODE:
                rIdBuilder = rIdBuilder.addBranchPointSchema(schemaId.name(),
                                                             schemaId.namespace());
                break;
            default:
                throw new XmlSerializerException("Unsupported type" +
                                                         dataNode.type());
        }
        return rIdBuilder.build();
    }

    /**
     * Returns true if it is root data node.
     *
     * @param node data node
     * @return true if it is root data node, false otherwise
     */
    private static boolean isRootDataNode(DataNode node) {
        return node.key().schemaId().name().equals(FORWARD_SLASH);
    }
}
