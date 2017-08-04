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
import org.onosproject.yang.runtime.YangSerializer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Test cases for default YANG serializer registry.
 */
public class SerializerRegistryTest {

    /**
     * String constant for XML.
     */
    public static final String XML = "XML";

    /**
     * String constant for JSON.
     */
    public static final String JSON = "JSON";

    private DefaultYangSerializerRegistry registry =
            new DefaultYangSerializerRegistry();

    private YangSerializer register(String df) {
        YangSerializer s = new TestSerializer(df);
        registry.registerSerializer(s);
        return s;
    }

    /**
     * Checks for runtime serializer registry.
     */
    @Test(expected = IllegalArgumentException.class)
    public void validatesSchemaRegistry() throws IllegalArgumentException {
        YangSerializer xml = register(XML);
        assertThat(true, is(registry.getSerializers().contains(xml)));
        assertThat(xml, is(registry.getSerializer(XML)));
        assertThat(true, is(registry.getSerializers().contains(xml)));

        YangSerializer json = register(JSON);
        assertThat(true, is(registry.getSerializers().contains(json)));
        assertThat(json, is(registry.getSerializer(JSON)));
        assertThat(true, is(registry.getSerializers().contains(json)));
        assertThat(true, is(registry.getSerializers().contains(xml)));
        assertThat(xml, is(registry.getSerializer(XML)));
        assertThat(true, is(registry.getSerializers().contains(xml)));

        registry.unregisterSerializer(xml);
        assertThat(true, is(registry.getSerializers().contains(json)));
        assertThat(json, is(registry.getSerializer(JSON)));
        assertThat(true, is(registry.getSerializers().contains(json)));
        assertThat(false, is(registry.getSerializers().contains(xml)));
        assertThat(false, is(registry.getSerializers().contains(xml)));

        registry.unregisterSerializer(xml);
        assertThat(true, is(registry.getSerializers().contains(json)));
        assertThat(json, is(registry.getSerializer(JSON)));
        assertThat(false, is(registry.getSerializers().contains(xml)));

        YangSerializer xml2 = register(XML);
        assertThat(true, is(registry.getSerializers().contains(xml2)));
        assertThat(xml2, is(registry.getSerializer(XML)));
        assertThat(true, is(registry.getSerializers().contains(json)));
        assertThat(json, is(registry.getSerializer(JSON)));

        registry.unregisterSerializer(xml);
        registry.unregisterSerializer(json);
        assertThat(false, is(registry.getSerializers().contains(xml2)));
        assertThat(false, is(registry.getSerializers().contains(json)));
    }
}
