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

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.AnnotatedNodeInfo;
import org.onosproject.yang.runtime.Annotation;
import org.onosproject.yang.runtime.CompositeData;
import org.onosproject.yang.runtime.CompositeStream;
import org.onosproject.yang.runtime.DefaultCompositeData;
import org.onosproject.yang.runtime.DefaultCompositeStream;
import org.onosproject.yang.runtime.YangSerializer;
import org.onosproject.yang.runtime.YangSerializerContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.onosproject.yang.runtime.DefaultDataNodeWalker.walk;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.serializers.utils.SerializersUtil.addRootElementWithAnnotation;
import static org.onosproject.yang.serializers.utils.SerializersUtil.convertRidToUri;
import static org.onosproject.yang.serializers.utils.SerializersUtil.convertUriToRid;

/**
 * Represents an implementation of XML serializer.
 */
public class XmlSerializer implements YangSerializer {
    private static final String XML = "xml";

    @Override
    public String supportsFormat() {
        return XML;
    }

    @Override
    public CompositeData decode(CompositeStream external,
                                YangSerializerContext context) {

        try {
            //parse XML input
            String xmlInput = addRootElementWithAnnotation(
                    external.resourceData(), context.getProtocolAnnotations());
            Document document = DocumentHelper.parseText(xmlInput);

            // initialize all the required builders
            CompositeData.Builder cBuilder = DefaultCompositeData.builder();
            ResourceData.Builder rdBuilder = DefaultResourceData.builder();
            XmlWalker walker = new DefaultXmlWalker();
            XmlSerializerListener listener = new XmlSerializerListener();
            listener.cBuilder(cBuilder);

            String uri = external.resourceId();
            if (uri == null) {
                listener.dnBuilder(initializeDataNode(context));
                walker.walk(listener, document.getRootElement(),
                            document.getRootElement());
                rdBuilder = rdBuilder.addDataNode(listener.dnBuilder().build());
            } else {
                /*
                 * If URI is not null, then each first level elements is
                 * converted to data node and added to list of data nodes in
                 * resource data
                 */
                ResourceId.Builder rIdBuilder = convertUriToRid(uri, context);
                Element rootElement = document.getRootElement();
                if (rootElement.hasContent() && !rootElement.isTextOnly()) {
                    Iterator i = rootElement.elementIterator();
                    while (i.hasNext()) {
                        Element childElement = (Element) i.next();
                        listener.dnBuilder(initializeDataNode(rIdBuilder));
                        walker.walk(listener, childElement, rootElement);
                        rdBuilder = rdBuilder.addDataNode(listener.dnBuilder()
                                                                  .build());
                    }
                }
                rdBuilder.resourceId(rIdBuilder.build());
            }
            return cBuilder.resourceData(rdBuilder.build()).build();
        } catch (DocumentException e) {
            throw new XmlSerializerException(e.getMessage());
        } catch (IOException e) {
            throw new XmlSerializerException(e.getMessage());
        }
    }

    @Override
    public CompositeStream encode(CompositeData internal,
                                  YangSerializerContext context) {
        String uriString = null;
        ResourceId.Builder builder;

        ResourceId resourceId = internal.resourceData().resourceId();
        if (resourceId != null && resourceId.nodeKeys() != null &&
                !resourceId.nodeKeys().isEmpty()) {
            uriString = convertRidToUri(resourceId, context);
            try {
                builder = resourceId.copyBuilder();
            } catch (CloneNotSupportedException e) {
                throw new XmlSerializerException(e.getMessage());
            }
        } else {
            /*
             * If resource id is null, initialise resource id with context
             * and get the resource id builder. Resource id is built for each
             * data node and checked in annotation map for annotations
             * associated with resource id.
             */
            builder = ResourceId.builder();
        }

        // Store annotations in map with resource id as key
        Map<ResourceId, List<Annotation>> annotations = new HashMap<>();
        List<AnnotatedNodeInfo> annotationList = internal.annotatedNodesInfo();
        if (annotationList != null) {
            for (AnnotatedNodeInfo annotationInfo : annotationList) {
                annotations.put(annotationInfo.resourceId(),
                                annotationInfo.annotations());
            }
        }

        // Walk through data node and build the XML
        List<DataNode> dataNodes = internal.resourceData().dataNodes();
        StringBuilder sb = new StringBuilder();
        for (DataNode dataNode : dataNodes) {
            DataNodeXmlListener listener = new DataNodeXmlListener(annotations,
                                                                   builder);
            walk(listener, dataNode);
            sb.append(listener.xmlData());
        }

        // convert XML to input stream and build composite stream
        InputStream inputStream = IOUtils.toInputStream(sb.toString());
        return new DefaultCompositeStream(uriString, inputStream);
    }
}
