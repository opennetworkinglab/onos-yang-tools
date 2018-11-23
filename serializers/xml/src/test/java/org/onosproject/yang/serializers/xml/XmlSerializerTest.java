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
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.LeafNode;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.runtime.AnnotatedNodeInfo;
import org.onosproject.yang.runtime.Annotation;
import org.onosproject.yang.runtime.CompositeData;
import org.onosproject.yang.runtime.CompositeStream;
import org.onosproject.yang.runtime.DefaultAnnotatedNodeInfo;
import org.onosproject.yang.runtime.DefaultCompositeData;
import org.onosproject.yang.runtime.DefaultCompositeStream;
import org.onosproject.yang.runtime.YangSerializer;
import org.onosproject.yang.runtime.YangSerializerContext;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 */
public class XmlSerializerTest {

    private static final Logger log = getLogger(XmlSerializerTest.class);

    public static final String LNS = "yrt:list.anydata";
    private static final String LIST_NS = "yrt:list";
    private static YangSerializerContext context;
    private static YangSerializer xmlSerializer;
    private static String idXml = "<test xmlns=\"identity:ns:test:json:ser" +
            "\"><con1><interface xmlns:yangid=\"identity:list:ns:test:jso" +
            "n:ser\">yangid:physical</interface><interfaces><int-list><ide" +
            "n xmlns:yangid=\"identity:list:second:ns:test:json:ser\">ya" +
            "ngid:virtual</iden><available><ll xmlns:yangid=\"identity:li" +
            "st:ns:test:json:ser\">yangid:Loopback</ll><ll xmlns:yangid=" +
            "\"identity:ns:test:json:ser\">yangid:Giga</ll><ll xmlns:yan" +
            "gid=\"identity:list:second:ns:test:json:ser\">yangid:Ether" +
            "net</ll></available></int-list><int-list><iden>optical</iden><av" +
            "ailable><ll>Giga</ll></available></int-list></interfaces></con1" +
            "></test>";
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void prepare() {
        context = new MockYangSerializerContext();
        xmlSerializer = new XmlSerializer();
    }

    /**
     * Validates and returns container data node.
     *
     * @param parent    data node holding container
     * @param name      name of the container
     * @param namespace namespace of the container
     * @return container data node
     */
    private static DataNode validateContainerDataNode(DataNode parent,
                                                      String name,
                                                      String namespace) {
        Map<NodeKey, DataNode> childNodes = ((InnerNode) parent).childNodes();
        NodeKey key = NodeKey.builder().schemaId(name, namespace).build();
        DataNode dataNode = childNodes.get(key);
        SchemaId schemaId = dataNode.key().schemaId();
        assertThat(schemaId.name(), is(name));
        assertThat(schemaId.namespace(), is(namespace));
        return dataNode;
    }

    /**
     * Validates and returns list data node.
     *
     * @param parent    data node holding list
     * @param name      name of the list
     * @param namespace namespace of the list
     * @param keynames  list of key leaf names
     * @param keyNs     list of key leaf namespace
     * @param keyVal    list of key leaf values
     * @return list data node
     */
    private static DataNode validateListDataNode(DataNode parent,
                                                 String name, String namespace,
                                                 List<String> keynames,
                                                 List<String> keyNs,
                                                 List<Object> keyVal) {
        Map<NodeKey, DataNode> childNodes = ((InnerNode) parent).childNodes();
        NodeKey key = NodeKey.builder().schemaId(name, namespace).build();
        DataNode dataNode;
        if (keynames != null && !keynames.isEmpty()) {
            ListKey.ListKeyBuilder listKeyBldr = new ListKey.ListKeyBuilder();
            listKeyBldr.schemaId(key.schemaId());
            for (int i = 0; i < keynames.size(); i++) {
                listKeyBldr.addKeyLeaf(keynames.get(i), keyNs.get(i),
                                       keyVal.get(i));
            }
            dataNode = childNodes.get(listKeyBldr.build());
        } else {
            dataNode = childNodes.get(key);
        }
        SchemaId schemaId = dataNode.key().schemaId();
        assertThat(schemaId.name(), is(name));
        assertThat(schemaId.namespace(), is(namespace));
        return dataNode;
    }

    /**
     * Validates leaf data node.
     *
     * @param parent    data node holding leaf
     * @param name      name of the leaf
     * @param namespace namespace of the leaf
     * @param value     leaf value
     */
    private static void validateLeafDataNode(DataNode parent,
                                             String name, String namespace,
                                             Object value) {
        Map<NodeKey, DataNode> childNodes = ((InnerNode) parent).childNodes();
        NodeKey key = NodeKey.builder().schemaId(name, namespace).build();
        LeafNode dataNode = ((LeafNode) childNodes.get(key));
        SchemaId schemaId = dataNode.key().schemaId();
        assertThat(schemaId.name(), is(name));
        assertThat(schemaId.namespace(), is(namespace));
        if (dataNode.value() != null) {
            assertThat(dataNode.value().toString(), is(value));
        }
    }

    /**
     * Validates an empty leaf data node.
     *
     * @param parent    data node holding leaf
     * @param name      name of the leaf
     * @param namespace namespace of the leaf
     * @param value     leaf value
     */
    private static void validateNullLeafDataNode(DataNode parent,
                                                 String name, String namespace,
                                                 Object value) {
        Map<NodeKey, DataNode> childNodes = ((InnerNode) parent).childNodes();
        NodeKey key = NodeKey.builder().schemaId(name, namespace).build();
        LeafNode dataNode = ((LeafNode) childNodes.get(key));
        SchemaId schemaId = dataNode.key().schemaId();
        assertThat(schemaId.name(), is(name));
        assertThat(schemaId.namespace(), is(namespace));
        assertThat(dataNode.value(), is(value));
    }

    /**
     * Validates leaf-list data node.
     *
     * @param parent    data node holding leaf-list
     * @param name      name of the leaf-list
     * @param namespace namespace of the leaf-list
     * @param value     leaf-list value
     */
    private static void validateLeafListDataNode(DataNode parent,
                                                 String name, String namespace,
                                                 Object value) {
        Map<NodeKey, DataNode> childNodes = ((InnerNode) parent).childNodes();
        NodeKey key = NodeKey.builder().schemaId(name, namespace).build();
        LeafListKey.LeafListKeyBuilder leafListBldr =
                new LeafListKey.LeafListKeyBuilder();
        leafListBldr.schemaId(key.schemaId());
        leafListBldr.value(value);
        DataNode leafListNode = childNodes.get(leafListBldr.build());
        SchemaId schemaId = leafListNode.key().schemaId();
        assertThat(schemaId.name(), is(name));
        assertThat(schemaId.namespace(), is(namespace));
        assertThat(((LeafNode) leafListNode).value().toString(), is(value));
    }

    /**
     * Validates root data node.
     *
     * @param resourceData resource data which holds data node
     * @return root data node
     */
    private static DataNode validateRootDataNode(ResourceData resourceData) {
        List<DataNode> dataNodes = resourceData.dataNodes();
        DataNode rootNode = dataNodes.get(0);
        SchemaId rootSchemaId = rootNode.key().schemaId();
        assertThat(rootSchemaId.name(), is("/"));
        return rootNode;
    }

    /**
     * Reads XML contents from file path and returns input stream.
     *
     * @param path path of XML file
     * @return input stream
     */
    private static InputStream parseInput(String path) {
        String temp;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
        } catch (IOException e) {
            log.error("Error in processing the path {}", path, e);
        }
        return IOUtils.toInputStream(sb);
    }

    /**
     * Reads XML contents from file path and returns input stream.
     *
     * @param path path of XML file
     * @return input stream
     */
    private static String parseXml(String path) {
        String temp;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Converts input stream to string format.
     *
     * @param inputStream input stream of xml
     * @return XML string
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
            throw new XmlSerializerException(e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Validates data node in which XML element is of type YANG container.
     */
    @Test
    public void testContainer() {
        String path = "src/test/resources/testContainer.xml";

        DefaultCompositeStream external =
                new DefaultCompositeStream(null, parseInput(path));
        CompositeData compositeData = xmlSerializer.decode(external, context);
        DataNode rootNode = validateRootDataNode(compositeData.resourceData());
        DataNode food = validateContainerDataNode(rootNode, "food", "yrt:food");
        validateLeafDataNode(food, "p1", "yrt:food", "p1_value");
        validateLeafListDataNode(food, "p2", "yrt:food", "p2_value");
        validateLeafListDataNode(food, "p2", "yrt:food", "p2_value1");
        DataNode c2 = validateContainerDataNode(rootNode, "c2", "yrt:food");
        validateLeafDataNode(c2, "p3", "yrt:food", "p3_value");

        // encode test
        CompositeStream compositeStream = xmlSerializer.encode(compositeData,
                                                               context);
        InputStream inputStream = compositeStream.resourceData();
        assertThat(convertInputStreamToString(inputStream), is(parseXml(path)));
    }

    /**
     * Validates data node in which XML element is of type YANG empty
     * container and empty leaf inside a container.
     */
    @Test
    public void testEmptyContainer() {
        String path = "src/test/resources/emptyContainers.xml";

        DefaultCompositeStream external =
                new DefaultCompositeStream(null, parseInput(path));
        CompositeData compositeData = xmlSerializer.decode(external, context);
        DataNode rootNode = validateRootDataNode(compositeData.resourceData());
        DataNode animal = validateContainerDataNode(rootNode, "animal",
                                                    "yrt:animal");
        DataNode c3 = validateContainerDataNode(animal, "c3",
                                                "yrt:animal");
        DataNode c4 = validateContainerDataNode(rootNode, "c4", "yrt:animal");
        DataNode c2 = validateContainerDataNode(rootNode, "c2",
                                                "yrt:animal");
        validateNullLeafDataNode(c2, "p3", "yrt:animal", null);

        // encode test
        CompositeStream compositeStream = xmlSerializer.encode(compositeData,
                                                               context);
        InputStream inputStream = compositeStream.resourceData();
        assertThat(convertInputStreamToString(inputStream), is(parseXml(path)));
    }

    /**
     * Validates data node in which XML element is of type YANG leaf.
     */
    @Test
    public void testModuleLevelLeaf() {
        String path = "src/test/resources/testModuleLevelLeaf.xml";

        DefaultCompositeStream external =
                new DefaultCompositeStream(null, parseInput(path));
        CompositeData compositeData = xmlSerializer.decode(external, context);
        DataNode rootNode = validateRootDataNode(compositeData.resourceData());
        validateLeafDataNode(rootNode, "bool", "yrt:food", "true");

        // encode test
        CompositeStream compositeStream = xmlSerializer.encode(compositeData,
                                                               context);
        InputStream inputStream = compositeStream.resourceData();
        assertThat(convertInputStreamToString(inputStream), is(parseXml(path)));
    }

    /**
     * Validates data node in which XML element is of type YANG list.
     */
    @Test
    public void testListWithKeyleaves() {
        String path = "src/test/resources/testListWithKeyleaves.xml";
        DefaultCompositeStream external =
                new DefaultCompositeStream(null, parseInput(path));
        CompositeData compositeData = xmlSerializer.decode(external, context);
        DataNode rootNode = validateRootDataNode(compositeData.resourceData());
        List<String> keyNames = new LinkedList<>();
        keyNames.add("k1");
        keyNames.add("k2");
        keyNames.add("k3");

        List<String> keyNs = new LinkedList<>();
        keyNs.add("yrt:list");
        keyNs.add("yrt:list");
        keyNs.add("yrt:list");

        List<Object> values = new LinkedList<>();
        values.add("k1_Value");
        values.add("k2_Value");
        values.add("k3_Value");

        DataNode listl1 = validateListDataNode(rootNode, "l1", "yrt:list",
                                               keyNames, keyNs, values);
        validateLeafDataNode(listl1, "k1", "yrt:list", "k1_Value");
        validateLeafDataNode(listl1, "k2", "yrt:list", "k2_Value");
        validateLeafDataNode(listl1, "k3", "yrt:list", "k3_Value");
        DataNode c1 = validateContainerDataNode(listl1, "c1", "yrt:list");
        validateLeafDataNode(c1, "leaf_c1", "yrt:list", "l1_value");

        values = new LinkedList<>();
        values.add("k1_Value1");
        values.add("k2_Value2");
        values.add("k3_Value3");

        DataNode listl2 = validateListDataNode(rootNode, "l1", "yrt:list",
                                               keyNames, keyNs, values);
        validateLeafDataNode(listl2, "k1", "yrt:list", "k1_Value1");
        validateLeafDataNode(listl2, "k2", "yrt:list", "k2_Value2");
        validateLeafDataNode(listl2, "k3", "yrt:list", "k3_Value3");
        c1 = validateContainerDataNode(listl2, "c1", "yrt:list");
        validateLeafDataNode(c1, "leaf_c1", "yrt:list", "l1_value1");

        // encode test
        CompositeStream compositeStream = xmlSerializer.encode(
                getNewCompositeData(compositeData), context);
        InputStream inputStream = compositeStream.resourceData();
        assertThat(convertInputStreamToString(inputStream), is(parseXml(path)));
    }

    /**
     * Validates data node in which XML element is child of YANG
     * choice-case.
     */
    @Test
    public void testChoiceCase() {
        String path = "src/test/resources/testChoiceCase.xml";

        DefaultCompositeStream external =
                new DefaultCompositeStream(null, parseInput(path));
        CompositeData compositeData = xmlSerializer.decode(external, context);
        DataNode rootNode = validateRootDataNode(compositeData.resourceData());
        DataNode list1 = validateListDataNode(rootNode, "banana",
                                              "yrt:choice-case", null, null, null);
        validateLeafDataNode(list1, "l1", "yrt:choice-case", "value2");
        DataNode coldDrink = validateContainerDataNode(rootNode, "cold-drink",
                                                       "yrt:choice-case");
        validateLeafListDataNode(coldDrink, "flavor", "yrt:choice-case",
                                 "value3");

        // encode test
        CompositeStream compositeStream = xmlSerializer.encode(
                getNewCompositeData(compositeData), context);
        InputStream inputStream = compositeStream.resourceData();
        assertThat(convertInputStreamToString(inputStream), is(parseXml(path)));
    }

    /**
     * Validates data node in which XML element is of YANG augmented nodes.
     */
    @Test
    public void testAugment() {
        String path = "src/test/resources/testAugment.xml";

        String uri = "urn:ietf:params:xml:ns:yang:yrt-ietf-network:networks/network/node";
        DefaultCompositeStream external =
                new DefaultCompositeStream(uri, parseInput(path));
        CompositeData compositeData = xmlSerializer.decode(external, context);
        ResourceData resourceData = compositeData.resourceData();
        List<DataNode> dataNodes = resourceData.dataNodes();
        DataNode dataNode = dataNodes.get(0);
        SchemaId schemaId = dataNode.key().schemaId();
        assertThat(schemaId.name(), is("t-point"));
        assertThat(schemaId.namespace(), is("urn:ietf:params:xml:ns:yang:yrt-ietf-network-topology"));
        validateLeafDataNode(dataNode, "tp-id",
                             "urn:ietf:params:xml:ns:yang:yrt-ietf-network-topology", "Stub");

        // encode test
        CompositeStream compositeStream = xmlSerializer.encode(compositeData,
                                                               context);
        InputStream inputStream = compositeStream.resourceData();
        assertThat(convertInputStreamToString(inputStream), is(parseXml(path)));
    }

    /**
     * Validates XML encode and decode with value namespace for leaf with
     * identity-ref types.
     */
    @Test
    public void identityValueNsTest() {
        String path = "src/test/resources/id-test.xml";
        DefaultCompositeStream external =
                new DefaultCompositeStream(null, parseInput(path));
        CompositeData compositeData = xmlSerializer.decode(external, context);
        CompositeStream compositeStream = xmlSerializer.encode(compositeData,
                                                               context);
        InputStream inputStream = compositeStream.resourceData();
        assertThat(convertInputStreamToString(inputStream), is(idXml));
    }

    /**
     * Validates the error message for identity-ref without proper namespace.
     */
    @Test
    public void identityValueNsErrorTest() {
        thrown.expect(XmlSerializerException.class);
        thrown.expectMessage("Invalid input for value namespace");
        String path = "src/test/resources/id-test2.xml";
        DefaultCompositeStream external =
                new DefaultCompositeStream(null, parseInput(path));
        xmlSerializer.decode(external, context);
    }

    /**
     * Validates whether XML attributes is converted to annotations.
     */
    @Test
    public void testXmlAttributes() {
        String path = "src/test/resources/testXmlAttributes.xml";
        String namespace = "http://example.com/schema/1.2/config";

        DefaultCompositeStream external =
                new DefaultCompositeStream(null, parseInput(path));
        CompositeData compositeData = xmlSerializer.decode(external, context);
        DataNode rootNode = validateRootDataNode(compositeData.resourceData());
        DataNode top = validateContainerDataNode(rootNode, "top", namespace);
        DataNode interfaceDn = validateContainerDataNode(top, "interface",
                                                         namespace);
        validateLeafDataNode(interfaceDn, "name", namespace, "Ethernet0/0");
        validateLeafDataNode(interfaceDn, "mtu", namespace, "1500");
        DataNode address = validateContainerDataNode(interfaceDn, "address",
                                                     namespace);
        validateLeafDataNode(address, "name", namespace, "192.0.2.4");
        validateLeafDataNode(address, "prefix-length", namespace, "24");

        List<AnnotatedNodeInfo> annotatedNodeInfos = compositeData
                .annotatedNodesInfo();
        AnnotatedNodeInfo annotatedNodeInfo = annotatedNodeInfos.get(0);
        List<Annotation> annotationList = annotatedNodeInfo.annotations();
        Annotation annotation = annotationList.get(0);
        assertThat(annotation.name(), is("xc:operation"));
        assertThat(annotation.value(), is("replace"));

        // encode test
        CompositeStream compositeStream = xmlSerializer.encode(
                getNewCompositeData(compositeData), context);
        InputStream inputStream = compositeStream.resourceData();
        assertThat(convertInputStreamToString(inputStream), is(parseXml(path)));
    }

    private CompositeData getNewCompositeData(CompositeData data) {
        List<AnnotatedNodeInfo> annotatedNodeInfos = data
                .annotatedNodesInfo();
        CompositeData.Builder newCompBuilder = DefaultCompositeData.builder()
                .resourceData(data.resourceData());

        if (annotatedNodeInfos != null && !annotatedNodeInfos.isEmpty()) {
            for (AnnotatedNodeInfo annotatedNodeInfo : annotatedNodeInfos) {
                AnnotatedNodeInfo.Builder newAnnoBuilder = DefaultAnnotatedNodeInfo
                        .builder();
                ResourceId id = annotatedNodeInfo.resourceId();
                ResourceId.Builder newIdBuilder = ResourceId.builder();
                List<NodeKey> keys = id.nodeKeys();
                if (keys != null && !keys.isEmpty()) {
                    for (NodeKey key : keys) {
                        if (!key.schemaId().name().equals("/")) {
                            SchemaId schemaId = key.schemaId();
                            if (key instanceof LeafListKey) {
                                newIdBuilder.addLeafListBranchPoint(
                                        schemaId.name(), schemaId.namespace(),
                                        ((LeafListKey) key).value());
                            } else if (key instanceof ListKey) {
                                newIdBuilder.addBranchPointSchema(
                                        schemaId.name(), schemaId.namespace());
                                List<KeyLeaf> listKeys = ((ListKey) key)
                                        .keyLeafs();
                                for (KeyLeaf listKey : listKeys) {
                                    SchemaId keySchemaId = listKey.leafSchema();
                                    newIdBuilder = newIdBuilder.addKeyLeaf(
                                            keySchemaId.name(), keySchemaId.namespace(),
                                            listKey.leafValue());
                                }
                            } else {
                                newIdBuilder.addBranchPointSchema(
                                        schemaId.name(), schemaId.namespace());
                            }
                        }
                    }
                }
                newAnnoBuilder.resourceId(newIdBuilder.build());
                List<Annotation> annotations = annotatedNodeInfo.annotations();
                for (Annotation annotation : annotations) {
                    newAnnoBuilder.addAnnotation(annotation);
                }
                newCompBuilder.addAnnotatedNodeInfo(newAnnoBuilder.build());
            }
        }
        return newCompBuilder.build();
    }

    /**
     * Validates data node in which XML element is of type YANG anydata.
     */
    @Test
    public void testListWithAnydata() {
        String path = "src/test/resources/testListAnydata.xml";
        DefaultCompositeStream external =
                new DefaultCompositeStream(null, parseInput(path));
        CompositeData compositeData = xmlSerializer.decode(external, context);
        DataNode rootNode = validateRootDataNode(compositeData.resourceData());
        List<String> keyNames = new LinkedList<>();
        keyNames.add("k1");
        keyNames.add("k2");
        keyNames.add("k3");

        List<String> keyNs = new LinkedList<>();
        keyNs.add(LNS);
        keyNs.add(LNS);
        keyNs.add(LNS);

        List<Object> values = new LinkedList<>();
        values.add("k1_Value");
        values.add("k2_Value");
        values.add("k3_Value");

        DataNode listl1 = validateListDataNode(rootNode, "l1", LNS,
                                               keyNames, keyNs, values);
        validateLeafDataNode(listl1, "k1", LNS, "k1_Value");
        validateLeafDataNode(listl1, "k2", LNS, "k2_Value");
        validateLeafDataNode(listl1, "k3", LNS, "k3_Value");
        DataNode c1 = validateContainerDataNode(listl1, "c1", LNS);
        DataNode mydata = validateContainerDataNode(c1, "mydata", LNS);
        validateLeafDataNode(c1, "leaf_c1", LNS, "l1_value");

        values = new LinkedList<>();
        values.add("k1_Value1");
        values.add("k2_Value2");
        values.add("k3_Value3");

        DataNode listl2 = validateListDataNode(rootNode, "l1", LNS,
                                               keyNames, keyNs, values);
        validateLeafDataNode(listl2, "k1", LNS, "k1_Value1");
        validateLeafDataNode(listl2, "k2", LNS, "k2_Value2");
        validateLeafDataNode(listl2, "k3", LNS, "k3_Value3");
        c1 = validateContainerDataNode(listl2, "c1", LNS);
        mydata = validateContainerDataNode(c1, "mydata", LNS);
        validateLeafDataNode(c1, "leaf_c1", LNS, "l1_value1");

        // validate module level anydata
        validateContainerDataNode(rootNode, "mydata", LNS);

        // encode test
        CompositeStream compositeStream = xmlSerializer.encode(
                getNewCompositeData(compositeData), context);
        InputStream inputStream = compositeStream.resourceData();
        assertThat(convertInputStreamToString(inputStream), is(parseXml(path)));
    }

    /**
     * Validates data node in which XML element is of type YANG List with config false.
     */
    @Test
    public void testListWithConfigFalse() {
        String path = "src/test/resources/testListWithConfigFalse.xml";
        DefaultCompositeStream external =
                new DefaultCompositeStream(null, parseInput(path));
        CompositeData compositeData = xmlSerializer.decode(external, context);
        DataNode rootNode = validateRootDataNode(compositeData.resourceData());
        List<String> keyNames = new LinkedList<>();
        keyNames.add("m1");
        keyNames.add("m2");

        List<String> keyNs = new LinkedList<>();
        keyNs.add(LIST_NS);
        keyNs.add(LIST_NS);

        List<Object> values = new LinkedList<>();
        values.add("m1_Value");
        values.add("m2_Value");

        DataNode c2Node = validateContainerDataNode(rootNode, "c2", LIST_NS);
        DataNode listl1 = validateListDataNode(c2Node, "l2", LIST_NS,
                                               keyNames, keyNs, values);
        validateLeafDataNode(listl1, "m1", LIST_NS, "m1_Value");
        validateLeafDataNode(listl1, "m2", LIST_NS, "m2_Value");

        List<Object> values1 = new LinkedList<>();
        values1.add("m1_Value1");
        values1.add("m2_Value1");

        DataNode listl2 = validateListDataNode(c2Node, "l2", LIST_NS,
                                               keyNames, keyNs, values1);
        validateLeafDataNode(listl2, "m1", LIST_NS, "m1_Value1");
        validateLeafDataNode(listl2, "m2", LIST_NS, "m2_Value1");

        // encode test
        CompositeStream compositeStream = xmlSerializer.encode(
                getNewCompositeData(compositeData), context);
        InputStream inputStream = compositeStream.resourceData();
        assertThat(convertInputStreamToString(inputStream), is(parseXml(path)));
    }
}
