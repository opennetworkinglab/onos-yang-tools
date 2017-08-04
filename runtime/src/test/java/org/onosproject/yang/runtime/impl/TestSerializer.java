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

package org.onosproject.yang.runtime.impl;

import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.AnnotatedNodeInfo;
import org.onosproject.yang.runtime.CompositeData;
import org.onosproject.yang.runtime.CompositeStream;
import org.onosproject.yang.runtime.YangSerializer;
import org.onosproject.yang.runtime.YangSerializerContext;

import java.io.InputStream;
import java.util.List;

/**
 * Represents mock YANG serializer to do unit test.
 */
public class TestSerializer implements YangSerializer {

    /**
     * String constant for TestStream.
     */
    public static final String TESTSTREAM = "TestStream";
    private String dataFormat;

    /**
     * Creates an instance of test serializer.
     *
     * @param df data format
     */
    public TestSerializer(String df) {
        dataFormat = df;
    }

    @Override
    public String supportsFormat() {
        return dataFormat;
    }

    @Override
    public CompositeData decode(CompositeStream external,
                                YangSerializerContext context) {
        return new CompositeData() {
            @Override
            public ResourceData resourceData() {
                return new ResourceData() {
                    @Override
                    public List<DataNode> dataNodes() {
                        return null;
                    }

                    @Override
                    public ResourceId resourceId() {
                        ResourceId.Builder builder = ResourceId.builder();
                        return builder.addBranchPointSchema(TESTSTREAM, TESTSTREAM)
                                .build();
                    }
                };
            }

            @Override
            public List<AnnotatedNodeInfo> annotatedNodesInfo() {
                return null;
            }
        };
    }

    @Override
    public CompositeStream encode(CompositeData internal,
                                  YangSerializerContext context) {
        return new CompositeStream() {
            @Override
            public String resourceId() {
                return TESTSTREAM;
            }

            @Override
            public InputStream resourceData() {
                return null;
            }
        };
    }
}
