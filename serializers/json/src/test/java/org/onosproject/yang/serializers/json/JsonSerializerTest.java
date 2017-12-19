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

package org.onosproject.yang.serializers.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.runtime.CompositeData;
import org.onosproject.yang.runtime.CompositeStream;
import org.onosproject.yang.runtime.DefaultCompositeData;
import org.onosproject.yang.runtime.DefaultCompositeStream;
import org.onosproject.yang.runtime.DefaultRuntimeContext;
import org.onosproject.yang.runtime.RuntimeContext;
import org.onosproject.yang.runtime.YangSerializer;
import org.onosproject.yang.runtime.YangSerializerContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.onosproject.yang.serializers.utils.SerializersUtil.convertRidToUri;

/**
 * Unit Test for Json Serializer.
 */
public class JsonSerializerTest {

    private static YangSerializerContext context;
    private static YangSerializer jsonSerializer;

    private static String outputIdTestJson = "{\"identity-test:con\":{\"inte" +
            "rface\":\"identity-types:physical\",\"interfaces\":{\"int-list" +
            "\":[{\"iden\":\"identity-types-second:virtual\",\"available\":" +
            "{\"ll\":[\"Loopback:identity-types\",\"Giga:identity-test\",\"" +
            "Ethernet:identity-types-second\"]}},{\"iden\":\"optical\",\"av" +
            "ailable\":{\"ll\":[\"Giga\"]}}]}}}";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void demo1Test() throws IOException {
        String path = "src/test/resources/test.json";
        // decode
        DefaultCompositeStream external =
                new DefaultCompositeStream("demo1:device", parseInput(path));
        CompositeData compositeData = jsonSerializer.decode(external, context);
        ResourceData resourceData = compositeData.resourceData();
        ResourceId rid = resourceData.resourceId();
        DataNode rootNode = resourceData.dataNodes().get(0);

        // encode
        RuntimeContext.Builder runtimeContextBuilder = DefaultRuntimeContext.builder();
        runtimeContextBuilder.setDataFormat("JSON");
        DefaultResourceData.Builder resourceDataBuilder = DefaultResourceData.builder();
        resourceDataBuilder.addDataNode(rootNode);
        resourceDataBuilder.resourceId(rid);

        ResourceData resourceDataOutput = resourceDataBuilder.build();
        DefaultCompositeData.Builder compositeDataBuilder = DefaultCompositeData.builder();
        compositeDataBuilder.resourceData(resourceDataOutput);
        CompositeData compositeData1 = compositeDataBuilder.build();
        // CompositeData --- YangRuntimeService ---> CompositeStream.
        CompositeStream compositeStreamOutPut = jsonSerializer.encode(compositeData1,
                                                                      context);
        InputStream inputStreamOutput = compositeStreamOutPut.resourceData();
        ObjectNode rootNodeOutput;
        ObjectMapper mapper = new ObjectMapper();
        try {
            rootNodeOutput = (ObjectNode) mapper.readTree(inputStreamOutput);
            assertEquals(true, rootNodeOutput != null);
        } catch (IOException e) {
            throw e;
        }
    }

    @Test
    public void identityValueNsTest() throws IOException {
        String path = "src/test/resources/id-test1.json";
        // decode
        DefaultCompositeStream external =
                new DefaultCompositeStream("identity-test:test", parseInput(path));
        CompositeData compositeData = jsonSerializer.decode(external, context);
        ResourceData resourceData = compositeData.resourceData();
        ResourceId rid = resourceData.resourceId();
        DataNode rootNode = resourceData.dataNodes().get(0);

        // encode
        RuntimeContext.Builder runtimeContextBuilder = DefaultRuntimeContext.builder();
        runtimeContextBuilder.setDataFormat("JSON");
        DefaultResourceData.Builder resourceDataBuilder = DefaultResourceData.builder();
        resourceDataBuilder.addDataNode(rootNode);
        resourceDataBuilder.resourceId(rid);

        ResourceData resourceDataOutput = resourceDataBuilder.build();
        DefaultCompositeData.Builder compositeDataBuilder = DefaultCompositeData.builder();
        compositeDataBuilder.resourceData(resourceDataOutput);
        CompositeData compositeData1 = compositeDataBuilder.build();
        // CompositeData --- YangRuntimeService ---> CompositeStream.
        CompositeStream compositeStreamOutPut = jsonSerializer.encode(compositeData1,
                                                                      context);
        InputStream inputStreamOutput = compositeStreamOutPut.resourceData();
        ObjectNode rootNodeOutput;
        ObjectMapper mapper = new ObjectMapper();
        try {
            rootNodeOutput = (ObjectNode) mapper.readTree(inputStreamOutput);
            assertEquals(rootNodeOutput.toString(), outputIdTestJson);
        } catch (IOException e) {
            throw e;
        }
    }

    @Test
    public void identityValueNsErrorTest() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid input for value namespace");
        String path = "src/test/resources/id-test2.json";
        DefaultCompositeStream external =
                new DefaultCompositeStream("identity-test:test", parseInput(path));
        jsonSerializer.decode(external, context);
    }

    @Test
    public void testContainerInResourceIdToUri() {
        ResourceId rid = ResourceId.builder().addBranchPointSchema("/", null)
                .addBranchPointSchema("device", "namespace1")
                .addBranchPointSchema("device", "namespace1")
                .addKeyLeaf("deviceid", "namespace1", "val").build();
        String uriString = convertRidToUri(rid, context);
        assertThat(uriString, is("demo1:device/device=val"));
        rid = ResourceId.builder().addBranchPointSchema("/", null)
                .addBranchPointSchema("device", "namespace1")
                .addBranchPointSchema("Purchasing-supervisor", "namespace1")
                .addBranchPointSchema("cont7", "namespace1").build();
        uriString = convertRidToUri(rid, context);
        assertThat(uriString, is("demo1:device/Purchasing-supervisor/cont7"));
    }

    @Test
    public void testListInResourceIdToUri() {
        ResourceId rid = ResourceId.builder().addBranchPointSchema("/", null)
                .addBranchPointSchema("list1", "namespace1")
                .addKeyLeaf("leaf1", "namespace1", "val")
                .build();
        String uriString = convertRidToUri(rid, context);
        assertThat(uriString, is("demo1:list1=val"));
    }

    /**
     * Converts name and name space to NodeKey.
     *
     * @param name      name
     * @param nameSpace name space
     * @return NodeKey
     */
    private NodeKey convertNameStringToNodeKey(String name, String nameSpace) {
        SchemaId schemaId = new SchemaId("top1", "jsonlist");
        NodeKey.NodeKeyBuilder nodeKeyBuilder =
                new NodeKey.NodeKeyBuilder<>().schemaId(schemaId);
        NodeKey nodeKey = nodeKeyBuilder.build();
        return nodeKey;
    }

    /**
     * Obtain a list of NodeKey from Map container.
     *
     * @param nodeChils Map container of InnerNode
     * @return List<NodeKey> list of nodeChils' key
     */
    private List<NodeKey> listNodeChildsKey(Map<NodeKey, DataNode> nodeChils) {
        Set<NodeKey> set = nodeChils.keySet();
        List<NodeKey> list = new ArrayList<>();
        list.addAll(set);
        return list;
    }

    /**
     * Obtain a list of DataNode from Map container.
     *
     * @param nodeChils Map container of InnerNode
     * @return List<NodeKey> list of nodeChils' value
     */
    private List<DataNode> listNodeChildsValue(Map<NodeKey, DataNode> nodeChils) {
        Collection<DataNode> coll = nodeChils.values();
        List<DataNode> list = new ArrayList(coll);
        return list;
    }

    /**
     * Obtain the DataNode for Map with specific name.
     *
     * @param nodeChils Map container of InnerNode
     * @param keyName   name of the DataNode which also shown in Json File.
     * @return DataNode
     */
    private DataNode getDataNode(Map<NodeKey, DataNode> nodeChils, String keyName) {
        List<DataNode> top1DataNodeList = listNodeChildsValue(nodeChils);
        DataNode l1DataNode = null;
        for (DataNode node : top1DataNodeList) {
            String actualName = String.valueOf(node.key().schemaId().name());
            String expectName = String.valueOf(keyName);
            if (actualName.equals(expectName)) {
                l1DataNode = node;
                break;
            }
        }
        return l1DataNode;
    }

    @BeforeClass
    public static void prepare() {
        context = new MockYangSerializerContext();
        jsonSerializer = new JsonSerializer();
    }

    /**
     * Reads JSON contents from file path and returns input stream.
     *
     * @param path path of JSON file
     * @return input stream
     */
    private static InputStream parseInput(String path) {
        String temp;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((temp = br.readLine()) != null) {
                temp = removeSpace(temp);
                sb.append(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return IOUtils.toInputStream(sb);
    }

    /**
     * Reads JSON contents from file path and returns input stream.
     *
     * @param path path of JSON file
     * @return json string
     */
    private static String parseJsonToString(String path) {
        String temp;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((temp = br.readLine()) != null) {
                temp = removeSpaceChangeName(temp);
                sb.append(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * While traversing a String line first remove all the white space.
     *
     * @param line a line of Json file.
     * @return String modified line
     */
    private static String removeSpace(String line) {
        if (line.length() == 0) {
            return line;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != ' ') {
                sb.append(line.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * While traversing a String line
     * first remove all the white space.
     * second if the json attribute's name has no namespace, add it on.
     *
     * @param line a line of Json file.
     * @return String modified line
     */
    private static String removeSpaceChangeName(String line) {
        if (line.length() == 0) {
            return line;
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                count++;
                continue;
            }
            if (line.charAt(i) == '"') {
                sb.append(line.charAt(i));
                if (count > 4) {
                    sb.append("jsonlist:");
                    count = 0;
                }
                i++;
                int start = i;
                while (line.charAt(i) != '"') {
                    i++;
                }
                int end = i;
                for (int j = start; j < end; j++) {
                    sb.append(line.charAt(j));
                }
            }
            sb.append(line.charAt(i));
        }
        return sb.toString();
    }

    /**
     * Converts input stream to string format.
     *
     * @param inputStream input stream of xml
     * @return JSON string
     */
    private static String convertInputStreamToString(InputStream inputStream) {
        BufferedReader br;
        StringBuilder sb = new StringBuilder();
        String xmlData;
        br = new BufferedReader(new InputStreamReader(inputStream));
        try {
            while ((xmlData = br.readLine()) != null) {
                sb.append(xmlData);
            }
        } catch (IOException e) {
            throw new SerializerException(e.getMessage());
        }
        return sb.toString();
    }
}
