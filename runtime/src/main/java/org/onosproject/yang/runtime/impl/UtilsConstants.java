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

public final class UtilsConstants {

    static final String AT = "@";
    static final String E_NEXIST = "node with {} namespace not found.";
    static final String E_NULL = "Model must not be null";
    static final String E_NOT_VAL = "Model id is invalid";
    static final String REV_REGEX =
            "rev([12]\\d{3}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01]))";
    private static final String FMT_NOT_EXIST =
            "Schema node with name %s doesn't exist.";
    public static final String FMT_NREG =
            "Node with requested %s identifier is not registered.";
    public static final String FMT_INV =
            "Requested %s identifier is invalid.";
    public static final String E_NCLONE =
            "Unenable to clone node with given identifer %s .";

    // No instantiation.
    private UtilsConstants() {
    }

    /**
     * Returns the error string by filling the parameters in the given
     * formatted error string.
     *
     * @param fmt    error format string
     * @param params parameters to be filled in formatted string
     * @return error string
     */
    public static String errorMsg(String fmt, Object... params) {
        return String.format(fmt, params);
    }
}
