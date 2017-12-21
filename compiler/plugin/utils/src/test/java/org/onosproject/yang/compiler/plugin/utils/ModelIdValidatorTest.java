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
package org.onosproject.yang.compiler.plugin.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.onosproject.yang.compiler.plugin.utils.PluginUtils.getValidModelId;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX;

public final class ModelIdValidatorTest {

    private static final String E_INVAL = "ERROR: Model Id validation not " +
            "working";
    /*
     * Reference for string values array to provide modelId value for
     * different-different scenario.
     */
    String[] nA = {
            "list Any data $doller @rate < arrow>end |or (copy)",
            "onos-yang-runtime",
            "Logistic.manager.1.",
            "list Any data []{}",
            "onos-yang     runtime      ",
            "[]   xyz  []",
            "onos_ yang_ runtime",
            "onos_yang_runtime",
            "org.onosproject.runtime"
    };

    private static final String[] EXPECTED = {
            "list_Any_data_doller_rate_arrow_end_or_copy",
            "onos-yang-runtime",
            "Logistic.manager.1.",
            "list_Any_data",
            "onos-yang_runtime",
            "xyz",
            "onos_yang_runtime",
            "onos_yang_runtime",
            "org.onosproject.runtime"
    };

    /*
     * Invalid values for testing the negative scenario for getValidModelId.
     */
    String[] invalidVal = {
            ".",
            "-",
            "_"
    };

    /*
     * Invalid values for testing the negative scenario for user given
     * model id at the time of registration.
     */
    String[] invalidVal1 = {
            "list Any data $doller @rate < arrow>end |or (copy)",
            "list Any data []{}",
            "onos-yang     runtime      ",
            "[]   xyz  []",
            "onos_ yang_ runtime",
    };

    /**
     * Test positive scenario for getValidModelId functionality.
     */
    @Test
    public void validateGetModelIdTest() {
        for (int i = 0; i < EXPECTED.length; i++) {
            assertEquals(EXPECTED[i], getValidModelId(nA[i]));
        }
    }

    /**
     * Test negative scenario for getValidModelId functionality.
     */
    @Test
    public void validateNegativeScenarioGetModelIdTest() {
        int i = 0;
        for (; i < invalidVal.length; i++) {
            try {
                getValidModelId(invalidVal[i]);
            } catch (IllegalArgumentException e) {
                assertEquals(e.getMessage(), "Invalid model id " +
                        invalidVal[i]);
            }
        }

        if (i != invalidVal.length) {
            throw new RuntimeException(E_INVAL);
        }
    }

    /**
     * Test positive scenario for user supplied model id functionality.
     */
    @Test
    public void validateSuppliedModelIdTest() {
        for (int i = 0; i < EXPECTED.length; i++) {
            if (!EXPECTED[i].matches(REGEX)) {
                throw new RuntimeException(E_INVAL);
            }
        }
    }

    /**
     * Test negative scenario for user supplied model id functionality.
     */
    @Test
    public void validateNegativeScenarioSuppliedModelIdTest() {
        for (int i = 0; i < invalidVal1.length; i++) {
            if (invalidVal1[i].matches(REGEX)) {
                throw new RuntimeException(E_INVAL);
            }
        }
    }
}
