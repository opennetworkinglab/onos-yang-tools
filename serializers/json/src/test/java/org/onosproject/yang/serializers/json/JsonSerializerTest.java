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

package org.onosproject.yang.serializers.json;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DataNode.Type;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.LeafNode;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.runtime.CompositeData;
import org.onosproject.yang.runtime.CompositeStream;
import org.onosproject.yang.runtime.DefaultCompositeStream;
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

import static org.junit.Assert.assertEquals;

/**
 * Unit Test for Json Serializer.
 */
public class JsonSerializerTest {
    private static final String WRONG_STRUCTURE = "The Data Node structure is wrong!";
    private static final String WRONG_TYPE = "The Data Node type is wrong!";

    private static YangSerializerContext context;
    private static YangSerializer jsonSerializer;

    @BeforeClass
    public static void prepare() {
        context = new MockYangSerializerContext();
        jsonSerializer = new JsonSerializer();
    }

    @Test
    public void jsonSerializerTest() {
        String path = "src/test/resources/testinput.json";
        DefaultCompositeStream external =
                new DefaultCompositeStream(null, parseInput(path));
        CompositeData compositeData = jsonSerializer.decode(external, context);
        ResourceData resourceData = compositeData.resourceData();
        DataNode rootNode = resourceData.dataNodes().get(0);

        // 1. test if rootNode is SINGLE_INSTANCE_NODE.
        assertEquals(WRONG_TYPE, Type.SINGLE_INSTANCE_NODE, rootNode.type());
        InnerNode innerNode = (InnerNode) rootNode;
        Map<NodeKey, DataNode> nodeChilds = innerNode.childNodes();
        // 2. test if Root Node only have one child.
        assertEquals(WRONG_STRUCTURE, 1, nodeChilds.size());
        NodeKey nodeChildKey = convertNameStringToNodeKey("top1", "jsonlist");
        // 3. test if l1 is the only child of Root Node.
        assertEquals(WRONG_STRUCTURE, true, nodeChilds.containsKey(nodeChildKey));
        InnerNode nodeTop1 = (InnerNode) nodeChilds.get(nodeChildKey);
        Map<NodeKey, DataNode> nodeTop1Childs = nodeTop1.childNodes();
        // 4. test if top1 contains three childs.
        assertEquals(WRONG_STRUCTURE, 3, nodeTop1Childs.size());
        DataNode l1DataNode = getDataNode(nodeTop1Childs, "l1");
        // 5. test if l1 is multi_instance_node
        assertEquals(WRONG_TYPE, Type.MULTI_INSTANCE_NODE, l1DataNode.type());

        InnerNode l1InnerNode = (InnerNode) l1DataNode;
        Map<NodeKey, DataNode> l1ChildNodes = l1InnerNode.childNodes();
        DataNode k1DataNode = getDataNode(l1ChildNodes, "k1");
        LeafNode k1LeafNode = (LeafNode) k1DataNode;
        DataNode k2DataNode = getDataNode(l1ChildNodes, "k2");
        LeafNode k2LeafNode = (LeafNode) k2DataNode;
        DataNode k3DataNode = getDataNode(l1ChildNodes, "k3");
        LeafNode k3LeafNode = (LeafNode) k3DataNode;
        // 6. test if k1, k2, k3 are with the right value.
        assertEquals(WRONG_STRUCTURE, true,
                     k1LeafNode.asString().equals("k1value"));
        assertEquals(WRONG_STRUCTURE, true,
                     k2LeafNode.asString().equals("k2value"));
        assertEquals(WRONG_STRUCTURE, true,
                     k3LeafNode.asString().equals("k3value"));

        // 7. test if c1 is in the right structure.
        DataNode c1DataNode = getDataNode(l1ChildNodes, "c1");
        InnerNode c1InnerNode = (InnerNode) c1DataNode;
        DataNode leafC1DataNode = getDataNode(c1InnerNode.childNodes(), "leaf_c1");
        LeafNode leafC1LeafNode = (LeafNode) leafC1DataNode;
        assertEquals(WRONG_STRUCTURE, 1, c1InnerNode.childNodes().size());
        assertEquals(WRONG_TYPE, Type.SINGLE_INSTANCE_LEAF_VALUE_NODE, leafC1DataNode.type());
        assertEquals(WRONG_STRUCTURE, true, leafC1LeafNode.asString().equals("c1leaf"));

        DataNode c2DataNode = getDataNode(nodeTop1Childs, "c2");
        // 8. test if c2 is single_instance_node.
        assertEquals(WRONG_TYPE, Type.SINGLE_INSTANCE_NODE, c2DataNode.type());

        InnerNode c2InnerNode = (InnerNode) c2DataNode;
        Map<NodeKey, DataNode> c2ChildNodes = c2InnerNode.childNodes();
        DataNode leaflist1DataNode = getDataNode(c2ChildNodes, "leaflist1");
        LeafNode leafList1LeafNode = (LeafNode) leaflist1DataNode;
        // 9. test if leaflist1 is in the right structure.
        assertEquals(WRONG_TYPE, Type.MULTI_INSTANCE_LEAF_VALUE_NODE, leafList1LeafNode.type());

    }

    @Test
    public void encodeTest() {
        String path = "src/test/resources/testinput.json";
        DefaultCompositeStream external =
                new DefaultCompositeStream(null, parseInput(path));
        CompositeData compositeData = jsonSerializer.decode(external, context);
        CompositeStream compositeStream = jsonSerializer.encode(compositeData, context);
        InputStream inputStream = compositeStream.resourceData();
        String expectString = parseJsonToString(path);
        assertEquals(WRONG_STRUCTURE, expectString, convertInputStreamToString(inputStream));
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
     * Converts name and name space to NodeKey.
     *
     * @param name name
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
     * @param keyName name of the DataNode which also shown in Json File.
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
