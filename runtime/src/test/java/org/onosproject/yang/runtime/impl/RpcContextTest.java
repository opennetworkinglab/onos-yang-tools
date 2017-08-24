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

import org.junit.Before;
import org.junit.Test;
import org.onosproject.yang.gen.v1.hello.rev20150105.hello.hellosecond.DefaultHelloSecondInput;
import org.onosproject.yang.gen.v1.hello.rev20150105.hello.hellosecond.HelloSecondInput;
import org.onosproject.yang.gen.v1.hello.rev20150105.hello.helloworld.DefaultHelloWorldInput;
import org.onosproject.yang.gen.v1.hello.rev20150105.hello.helloworld.HelloWorldInput;
import org.onosproject.yang.model.DefaultModelObjectData;
import org.onosproject.yang.model.ModelConverter;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.RpcContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.onosproject.yang.runtime.SerializerHelper.addToResourceId;
import static org.onosproject.yang.runtime.SerializerHelper.initializeResourceId;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;

/**
 * Test cases for YANG runtime service interface file generation.
 */
public class RpcContextTest {
    ModIdToRscIdConverter builder;
    DefaultYangModelRegistry reg;
    public static final String NS = "urn:params:xml:ns:yang:hello";
    String value = null;
    TestYangSerializerContext context = new TestYangSerializerContext();

    /**
     * Sets up all prerequisite.
     */
    @Before
    public void setUp() {
        processSchemaRegistry();
        reg = registry();
        builder = new ModIdToRscIdConverter(reg);
    }

    /**
     * Check for service interface file.
     */
    @Test
    public void checkRpcContext() {
        ResourceId.Builder rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "hello-world", NS, value);

        DefaultSchemaContextProvider scp = new DefaultSchemaContextProvider(
                reg);

        RpcContext context = scp.getRpcContext(rIdBlr.build());
        assertEquals(context.rpcName(), "helloWorld");
        assertEquals(context.serviceIntf().toString(), "interface org.onosproject" +
                ".yang.gen.v1.hello.rev20150105.HelloService");
    }

    /*
     * Test the file rpc_test.yang.
     * This specifically tests that when 2 RPC's are specified in a single
     * YANG file that both RPC inputs can be handled through the ModelConverter
     */
    @Test
    public void checkRpcConverter() {
        ModelConverter mc = new DefaultModelConverter(reg);
        HelloWorldInput hwInput = new DefaultHelloWorldInput();
        hwInput.x("input-sample");
        ModelObjectData hwInputMod = DefaultModelObjectData.builder().
                            addModelObject((ModelObject) hwInput).build();
        assertNotNull(hwInputMod);
        ResourceData rd = mc.createDataNode(hwInputMod);
        assertNotNull(rd);

        //Now test converting to a second RPC in the same YANG
        HelloSecondInput hsInput = new DefaultHelloSecondInput();
        hsInput.x("second-input");
        ModelObjectData hsInputMod = DefaultModelObjectData.builder().
                addModelObject((ModelObject) hsInput).build();
        assertNotNull(hsInputMod);
        ResourceData rdSecond = mc.createDataNode(hsInputMod);
        assertNotNull(rdSecond);
    }
}
