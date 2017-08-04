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

import org.onosproject.yang.gen.v1.check.check.Cont53;
import org.onosproject.yang.gen.v1.check.check.DefaultCont50;
import org.onosproject.yang.gen.v1.check.check.DefaultCont53;
import org.onosproject.yang.gen.v1.check.check.DefaultList52;
import org.onosproject.yang.gen.v1.check.check.DefaultList56;
import org.onosproject.yang.gen.v1.check.check.List52Keys;
import org.onosproject.yang.gen.v1.check.check.List56;
import org.onosproject.yang.gen.v1.check.check.List56Keys;
import org.onosproject.yang.model.ModelObjectId;

import java.io.IOException;

/**
 * Unit test for model object id.
 */
public class ModelObjectIdTest {

    /**
     * Unit test case for creating model object id for container.
     *
     * @throws IOException when fails to do IO operation
     */
    @Test
    public void testCreateContainerModelObjectId()
            throws IOException {
        ModelObjectId id = new ModelObjectId.Builder()
                .addChild(DefaultCont50.class).build();
    }

    /**
     * Unit test case for creating model object id for list.
     *
     * @throws IOException when fails to do IO operation
     */
    @Test
    public void testCreateListModelObjectId()
            throws IOException {
        List52Keys key = new List52Keys();
        key.leaf52(52);
        ModelObjectId id = new ModelObjectId.Builder()
                .addChild(DefaultList52.class, key).build();
    }

    /**
     * Unit test case for creating model object id for leaf.
     *
     * @throws IOException when fails to do IO operation
     */
    @Test
    public void testCreateleafModelObjectId()
            throws IOException {
        ModelObjectId id = new ModelObjectId.Builder()
                .addChild(DefaultCont53.class)
                .addChild(Cont53.LeafIdentifier.LEAF55).build();
    }

    /**
     * Unit test case for creating model object id for leaf-list.
     *
     * @throws IOException when fails to do IO operation
     */
    @Test
    public void testCreateleaflistModelObjectId()
            throws IOException {
        ModelObjectId id = new ModelObjectId.Builder()
                .addChild(DefaultCont53.class)
                .addChild(Cont53.LeafIdentifier.LEAF54, "hello").build();
    }

    /**
     * Unit test case for creating model object id for leaf-list.
     *
     * @throws IOException when fails to do IO operation
     */
    @Test
    public void testCreatelistWithoutKeyModelObjectId()
            throws IOException {
        List56Keys key = new List56Keys();
        ModelObjectId id = new ModelObjectId.Builder()
                .addChild(DefaultList56.class, key)
                .addChild(List56.LeafIdentifier.LEAF57, "hello").build();
    }
}
