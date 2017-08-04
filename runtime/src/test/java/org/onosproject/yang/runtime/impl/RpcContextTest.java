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

import org.junit.Test;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.RpcContext;

import static org.junit.Assert.assertEquals;
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
    DataNode.Builder dBlr;

    /**
     * Sets up all prerequisite.
     */
    private void setUp() {
        processSchemaRegistry();
        reg = registry();
        builder = new ModIdToRscIdConverter(reg);
    }

    /**
     * Check for service interface file.
     */
    @Test
    public void checkRpcContext() {
        setUp();
        ResourceId.Builder rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "hello-world", NS, value);

        DefaultSchemaContextProvider scp = new DefaultSchemaContextProvider(
                reg);

        RpcContext context = scp.getRpcContext(rIdBlr.build());
        assertEquals(context.rpcName(), "helloWorld");
        assertEquals(context.serviceIntf().toString(), "interface org.onosproject" +
                ".yang.gen.v1.hello.rev20150105.HelloService");
    }
}
