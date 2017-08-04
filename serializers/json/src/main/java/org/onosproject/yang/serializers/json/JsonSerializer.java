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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.CompositeData;
import org.onosproject.yang.runtime.CompositeStream;
import org.onosproject.yang.runtime.DefaultCompositeData;
import org.onosproject.yang.runtime.DefaultCompositeStream;
import org.onosproject.yang.runtime.YangSerializer;
import org.onosproject.yang.runtime.YangSerializerContext;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.onosproject.yang.serializers.json.DecoderUtils.convertJsonToDataNode;
import static org.onosproject.yang.serializers.json.EncoderUtils.convertDataNodeToJson;
import static org.onosproject.yang.serializers.utils.SerializersUtil.convertRidToUri;
import static org.onosproject.yang.serializers.utils.SerializersUtil.convertUriToRid;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Implementation of the JSON serializer.
 */
public class JsonSerializer implements YangSerializer {
    private static final String JSON_FORMAT = "JSON";
    private static final String ERROR_INFO = "JSON serializer decode failure";

    private final Logger log = getLogger(getClass());
    private final ObjectMapper mapper = new ObjectMapper();

    public ObjectMapper mapper() {
        return mapper;
    }

    @Override
    public String supportsFormat() {
        return JSON_FORMAT;
    }

    @Override
    public CompositeData decode(CompositeStream compositeStream,
                                YangSerializerContext yangSerializerContext) {
        try {
            ResourceId.Builder rIdBuilder = convertUriToRid(
                    compositeStream.resourceId(), yangSerializerContext);

            ObjectNode rootNode = null;

            if (compositeStream.resourceData() != null) {
                rootNode = (ObjectNode) mapper().
                        readTree(compositeStream.resourceData());
            }

            DataNode dataNode;
            /*
             * initializeDataNode by passing yangSerializerContext is
             * intended to be used in a scenario wherein URL is NULL.
             * initializeDataNode by passing resourceIdBuilder is
             * intended to be used in a scenario when URL is not NULL
             * and in this case the resourceId builder which was constructed
             * for a URL, needs to be given as an Input parameter.
             */
            if (rIdBuilder != null) {
                dataNode = convertJsonToDataNode(rootNode,
                                                 rIdBuilder);

            } else {
                dataNode = convertJsonToDataNode(rootNode,
                                                 yangSerializerContext);
            }

            ResourceData resourceData = DefaultResourceData.builder()
                    .addDataNode(dataNode)
                    .resourceId(rIdBuilder == null ? null : rIdBuilder.build())
                    .build();
            return DefaultCompositeData.builder().resourceData(resourceData).build();
        } catch (JsonProcessingException e) {
            log.error("ERROR: JsonProcessingException {}",
                      e.getMessage());
            log.debug("Exception in decode:", e);
            throw new SerializerException(ERROR_INFO);
        } catch (IOException ex) {
            log.error("ERROR: decode ", ex);
            throw new SerializerException(ERROR_INFO);
        }
    }


    @Override
    public CompositeStream encode(CompositeData compositeData,
                                  YangSerializerContext yangSerializerContext) {
        checkNotNull(compositeData, "compositeData cannot be null");

        String uriString = convertRidToUri(compositeData.resourceData().
                resourceId(), yangSerializerContext);
        InputStream inputStream = null;
        ObjectNode rootNode = null;

        if (compositeData.resourceData().dataNodes() != null) {
            rootNode = convertDataNodeToJson(compositeData.
                    resourceData().dataNodes().get(0), yangSerializerContext);
        }

        if (rootNode != null) {
            inputStream = IOUtils.toInputStream(rootNode.toString());
        }
        // return a CompositeStream
        return new DefaultCompositeStream(uriString, inputStream);
    }
}
