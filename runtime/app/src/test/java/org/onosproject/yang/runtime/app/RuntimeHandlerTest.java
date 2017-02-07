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

package org.onosproject.yang.runtime.app;

import org.junit.Test;
import org.onosproject.yang.runtime.api.CompositeData;
import org.onosproject.yang.runtime.api.CompositeStream;
import org.onosproject.yang.runtime.api.YangRuntimeService;
import org.onosproject.yang.runtime.api.YangSerializer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.runtime.app.SerializerRegistryTest.JSON;
import static org.onosproject.yang.runtime.app.SerializerRegistryTest.XML;
import static org.onosproject.yang.runtime.app.TestSerializer.TESTSTREAM;

/**
 * Test cases for YANG runtime handler.
 */
public class RuntimeHandlerTest {

    private DefaultYangSerializerRegistry registry =
            new DefaultYangSerializerRegistry();
    private YangRuntimeService s =
            new DefaultYangRuntimeHandler(registry, null);

    private void register(String df) {
        YangSerializer s = new TestSerializer(df);
        registry.registerSerializer(s);
    }

    /**
     * Checks for runtime handler.
     */
    @Test
    public void validateRuntimeHandler() {
        register(XML);
        CompositeData dd = s.decode(null, XML);
        assertThat(TESTSTREAM, is(dd.resourceData().resourceId().nodeKeys()
                                          .get(0).schemaId().name()));

        CompositeStream cs = s.encode(null, XML);
        assertThat(TESTSTREAM, is(cs.resourceId()));
    }

    /**
     * Checks for runtime handler.
     */
    @Test(expected = RuntimeException.class)
    public void validateRuntimeHandlerError() throws RuntimeException {
        register(XML);
        CompositeData dd = s.decode(null, JSON);
        assertThat(TESTSTREAM, is(dd.resourceData().resourceId().nodeKeys()
                                          .get(0).schemaId().name()));
    }
}