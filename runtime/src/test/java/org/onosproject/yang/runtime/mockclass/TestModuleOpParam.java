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

package org.onosproject.yang.runtime.mockclass;

import org.onosproject.yang.model.InnerModelObject;
import org.onosproject.yang.runtime.mockclass.testmodule.testrpc.DefaultTestOutput;
import org.onosproject.yang.runtime.mockclass.testmodule.testrpc.TestInput;
import org.onosproject.yang.runtime.mockclass.testmodule.testrpc.TestOutput;

/**
 * Mock module class.
 */
public class TestModuleOpParam extends InnerModelObject implements TestModule {

    private int testLeaf;

    /**
     * Sets test leaf value.
     *
     * @param val value
     */
    public void testLeaf(int val) {
        testLeaf = val;
    }

    /**
     * Returns test leaf value.
     *
     * @return test leaf value
     */
    public int testLeaf() {
        return testLeaf;
    }

    /**
     * Rpc for test module.
     *
     * @param in input
     * @return output
     */
    public TestOutput rpc(TestInput in) {
        DefaultTestOutput out = new DefaultTestOutput();
        out.testLeaf(in.testLeaf());
        return out;
    }
}
