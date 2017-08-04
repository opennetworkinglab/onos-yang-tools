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

package org.onosproject.yang.runtime.mockclass.testmodule.testrpc;

/**
 * Mock output class.
 */
public interface TestOutput {
    /**
     * Identify the leaf of TestModule.
     */
    public enum LeafIdentifier implements org.onosproject.yang.model.LeafIdentifier {
        /**
         * Represents test leaf.
         */
        TEST_LEAF(1);

        private int leafIndex;

        public int getLeafIndex() {
            return leafIndex;
        }

        LeafIdentifier(int value) {
            this.leafIndex = value;
        }
    }

    /**
     * Sets test leaf value.
     *
     * @param val value
     */
    void testLeaf(int val);

    /**
     * Returns test leaf value.
     *
     * @return test leaf value
     */
    int testLeaf();
}
