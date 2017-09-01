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
package org.onosproject.yang.model;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class ModelObjectIdTest {


    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(ModelObjectId.builder().build())
            .addEqualityGroup(ModelObjectId.builder()
                              .addChild(TestInnerModelObject.class)
                              .build())
            .addEqualityGroup(ModelObjectId.builder()
                              .addChild(TestInnerModelObject.class)
                              .addChild(TestInnerModelObject.class)
                              .build())
            .addEqualityGroup(ModelObjectId.builder()
                              .addChild(TestMultiInstanceObject.class, TestKeyInfo.KEY_A)
                              .build())
            .addEqualityGroup(ModelObjectId.builder()
                              .addChild(TestMultiInstanceObject.class, TestKeyInfo.KEY_B)
                              .build())
            .addEqualityGroup(ModelObjectId.builder()
                              .addChild(TestMultiInstanceObject.class, TestKeyInfo.KEY_A)
                              .addChild(TestMultiInstanceObject.class, TestKeyInfo.KEY_B)
                              .build())
            .addEqualityGroup(ModelObjectId.builder()
                              .addChild(TestLeafIdentifier.LEAF_A)
                              .build())
            .addEqualityGroup(ModelObjectId.builder()
                              .addChild(TestLeafIdentifier.LEAF_B)
                              .build())
            .addEqualityGroup(ModelObjectId.builder()
                              .addChild(TestLeafIdentifier.LEAF_A)
                              .addChild(TestLeafIdentifier.LEAF_B)
                              .build())
            .addEqualityGroup(ModelObjectId.builder()
                              .addChild(TestLeafIdentifier.LEAF_A, "A")
                              .build())
            .addEqualityGroup(ModelObjectId.builder()
                              .addChild(TestLeafIdentifier.LEAF_A, "B")
                              .build())
            .testEquals();
    }

    static class TestInnerModelObject extends InnerModelObject {

    }

    static class TestMultiInstanceObject
        extends InnerModelObject
        implements MultiInstanceObject<TestKeyInfo> {

    }

    static enum TestKeyInfo implements KeyInfo<TestMultiInstanceObject> {
        KEY_A,
        KEY_B
    }

    static enum TestLeafIdentifier implements LeafIdentifier {
        LEAF_A,
        LEAF_B
    }
}
