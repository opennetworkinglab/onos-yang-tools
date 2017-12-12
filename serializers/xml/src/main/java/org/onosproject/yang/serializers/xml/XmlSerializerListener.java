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
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.AnnotatedNodeInfo;
import org.onosproject.yang.runtime.CompositeData;
import org.onosproject.yang.runtime.HelperContext;

import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.getResourceId;
import static org.onosproject.yang.serializers.utils.SerializersUtil.convertXmlAttributesToAnnotations;
import static org.onosproject.yang.serializers.utils.SerializersUtil.getLatterSegment;
import static org.onosproject.yang.serializers.utils.SerializersUtil.getPreSegment;
import static org.onosproject.yang.serializers.xml.XmlNodeType.OBJECT_NODE;
import static org.onosproject.yang.serializers.xml.XmlNodeType.TEXT_NODE;

/**
 * Default implementation of XML listener.
 */
class XmlSerializerListener implements XmlListener {

    private static final String COLON = ":";

    /**
     * Data node builder.
     */
    private DataNode.Builder dnBuilder;

    /**
     * Composite data builder.
     */
    private CompositeData.Builder cBuilder;

    /**
     * Sets the data node builder.
     *
     * @param builder data node builder
     */
    void dnBuilder(DataNode.Builder builder) {
        dnBuilder = builder;
    }

    /**
     * Returns data node builder.
     *
     * @return data node builder
     */
    DataNode.Builder dnBuilder() {
        return dnBuilder;
    }

    /**
     * Sets the composite data node builder.
     *
     * @return composite data builder
     */
    public CompositeData.Builder cBuilder() {
        return cBuilder;
    }

    /**
     * Returns composite data builder.
     *
     * @return composite data builder
     */
    public void cBuilder(CompositeData.Builder cBuilder) {
        this.cBuilder = cBuilder;
    }


    @Override
    public void enterXmlElement(Element element, XmlNodeType nodeType,
                                Element rootElement) {

        // root element should not be added to data node
        if (element.equals(rootElement)) {
            return;
        }

        if (nodeType == OBJECT_NODE) {
            if (dnBuilder != null) {
                dnBuilder = addDataNode(dnBuilder, element.getName(),
                                        element.getNamespace().getURI(),
                                        null, null);
            }
        } else if (nodeType == TEXT_NODE) {
            if (dnBuilder != null) {
                String valNamespace = null;
                String actVal;
                String valPrefix;
                String value = element.getText();
                if (value != null) {
                    actVal = getLatterSegment(value, COLON);
                    valPrefix = getPreSegment(value, COLON);
                    if (valPrefix != null) {
                        valNamespace = element.getNamespaceForPrefix(valPrefix).getURI();
                    }
                } else {
                    actVal = value;
                }
                dnBuilder = addDataNode(dnBuilder, element.getName(),
                                        element.getNamespace().getURI(),
                                        actVal, valNamespace, null);
            }
        }
    }

    @Override
    public void exitXmlElement(Element element, XmlNodeType nodeType,
                               Element rootElement) {
        // Ignore the root element as it is not added to data node
        if (element.equals(rootElement)) {
            return;
        }

        // Build resource Id for annotations
        ResourceId id = getResourceId(dnBuilder);
        AnnotatedNodeInfo annotatedNodeInfo =
                convertXmlAttributesToAnnotations(element, id);
        if (annotatedNodeInfo != null) {
            cBuilder.addAnnotatedNodeInfo(annotatedNodeInfo);
        }

        /*
         * Since we need to build data node from top node, we should not
         * traverse back to parent for top node.
         */
        HelperContext info = (HelperContext) dnBuilder.appInfo();
        if (info.getParentResourceIdBldr() == null) {
            dnBuilder = exitDataNode(dnBuilder);
        }
    }
}
