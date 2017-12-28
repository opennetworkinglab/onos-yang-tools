/*
 * Copyright 2016-present Open Networking Foundation
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
package org.onosproject.yang.model;

import org.junit.Before;
import org.junit.Test;
import com.google.common.testing.EqualsTester;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test resource ID.
 */

public class ResourceIdTest {

    private static final ResourceId EMPTY =
            ResourceId.builder().build();
    private static final ResourceId ROOT =
            ResourceId.builder().addBranchPointSchema("/", "").build();
    ResourceId ridA;
    ResourceId ridAcopy;

    @Before
    public void setUp() throws Exception {
        ridA = ResourceId.builder()
                .addBranchPointSchema("/", "")
                .addBranchPointSchema("devices", "onos")
                .addBranchPointSchema("device", "onos")
                .addKeyLeaf("device-id", "onos", "test:A")
                .build();
        ridAcopy = ridA.copyBuilder().build();
    }

    @Test
    public void equality() {
        new EqualsTester()
        .addEqualityGroup(ridA, ridAcopy)
        .addEqualityGroup(ROOT)
        .addEqualityGroup(EMPTY)
        .testEquals();
    }

    @Test
    public void appendNodeKeys() throws CloneNotSupportedException {
        ResourceId devices = ResourceId.builder()
            .append(ridA.nodeKeys().subList(1, 2))
            .build();

        assertEquals(1, devices.nodeKeys().size());
        assertEquals("devices", devices.nodeKeys().get(0).schemaId().name());
        assertEquals("onos", devices.nodeKeys().get(0).schemaId().namespace());
    }

    @Test
    public void resourceIdConstruction() {
        String sampleResId = "/card=8/port=5,eth/stats";
        String[] resourcePath = sampleResId.split("/");

        ResourceId.Builder resBldr = new ResourceId.Builder();

        int i = 0;
        int j;
        while (i < resourcePath.length) {
            if (resourcePath[i].equals("")) {
                i++;
                continue;
            }

            String[] nameValue = resourcePath[i].split("=");
            resBldr.addBranchPointSchema(nameValue[0], "testNameSpace");
            if (nameValue.length == 1) {
                i++;
                continue;
            }

            String[] keys = nameValue[1].split(",");

            j = 0;
            while (j < keys.length) {
                //TODO: get schema name of key using YANG runtime
                String keyName = getKeyName(nameValue[0], j);
                resBldr.addKeyLeaf(keyName, "testNameSpace", keys[j]);
                j++;
            }
            i++;
        }

        ResourceId res = resBldr.build();
        List<NodeKey> keys = res.nodeKeys();
        assertEquals("invalid augmented node created", "card",
                     keys.get(0).schemaId().name());
        assertEquals("invalid augmented node created", ListKey.class,
                     keys.get(0).getClass());
        ListKey listKey = (ListKey) keys.get(0);
        assertEquals("invalid augmented node created", "slot",
                     listKey.keyLeafs().get(0).leafSchema().name());
    }

    private String getKeyName(String s, int j) {
        if (s.equals("card")) {
            return "slot";
        }
        if (s.equals("port")) {
            if (j == 0) {
                return "portno";
            }
        }
        return "type";
    }

}
