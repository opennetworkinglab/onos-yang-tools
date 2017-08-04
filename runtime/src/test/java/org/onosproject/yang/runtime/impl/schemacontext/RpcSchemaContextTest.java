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

package org.onosproject.yang.runtime.impl.schemacontext;

/**
 * Tests the default schema context methods.
 */
public class RpcSchemaContextTest {

    //TODO : need to be updated afetr RPC implementation

//    private static TestYangSchemaNodeProvider schemaProvider =
//            new TestYangSchemaNodeProvider();
//
//    public static final String HELLONS = "urn:yrt-hello_onos";
//    public static final String FOODNS = "yrt:food";
//    public static final String CASENS = "yrt:choice-case";
//
//    /**
//     * Checks rpc, input, output data node parent context.
//     */
//    @Test
//    public void rpcDataNodeSchContTest() {
//
//        schemaProvider.processSchemaRegistry();
//        DefaultYangModelRegistry registry = schemaProvider.registry();
//        SchemaId id = new SchemaId("hello-world", HELLONS);
//        YangNode child = (YangNode) registry.getChildContext(id);
//        checkSchemaContext("hello-world", HELLONS, "/", null,
//                           DataNode.Type.SINGLE_INSTANCE_NODE, child);
//
//        // Validating input node parent context.
//        child = child.getChild();
//        checkSchemaContext("input", HELLONS, "hello-world", HELLONS,
//                           DataNode.Type.SINGLE_INSTANCE_NODE, child);
//
//        List<YangLeafList> leafList = ((YangLeavesHolder) child)
//                .getListOfLeafList();
//        checkLeafListSchemaContext("stringList", HELLONS, "input",
//                                   HELLONS,
//                                   leafList.get(0));
//
//        // Validating output node parent context.
//        child = child.getNextSibling();
//        checkSchemaContext("output", HELLONS, "hello-world", HELLONS,
//                           DataNode.Type.SINGLE_INSTANCE_NODE, child);
//
//        List<YangLeaf> leafs = ((YangLeavesHolder) child).getListOfLeaf();
//        checkLeafSchemaContext("greetingOut", HELLONS, "output", HELLONS,
//                               leafs.get(0));
//    }
}
