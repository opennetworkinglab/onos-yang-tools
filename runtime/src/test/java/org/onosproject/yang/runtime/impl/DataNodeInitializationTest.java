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

package org.onosproject.yang.runtime.impl;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.runtime.helperutils.HelperContext;

import static org.onosproject.yang.runtime.helperutils.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.initializeResourceId;
import static org.onosproject.yang.runtime.impl.TestUtils.checkRootLevelContext;

/**
 * Tests the initialize data node methods in serializer helper.
 */

@FixMethodOrder(MethodSorters.DEFAULT)
public class DataNodeInitializationTest {

    TestYangSerializerContext context = new TestYangSerializerContext();

    /*
     * Reference for resource id builder.
     */
    ResourceId.Builder rIdBlr;

    /*
     * Reference for data node info.
     */
    HelperContext info;

    /*
     * Reference for data node builder.
     */
    DataNode.Builder dBlr;

    /**
     * Checks initialize data node using context.
     */
    @Test
    public void initializeDataNodeTest() {

        dBlr = initializeDataNode(context);
        info = (HelperContext) dBlr.appInfo();
        checkRootLevelContext((SchemaContext) info.getResourceIdBuilder()
                .appInfo());
    }

    /**
     * Checks initialize data node using resource id.
     */
    @Test
    public void initializeDataNodeRIdTest() {

        rIdBlr = initializeResourceId(context);
        dBlr = initializeDataNode(rIdBlr);
        info = (HelperContext) dBlr.appInfo();
        checkRootLevelContext((SchemaContext) info.getParentResourceIdBldr()
                .appInfo());
    }
}
