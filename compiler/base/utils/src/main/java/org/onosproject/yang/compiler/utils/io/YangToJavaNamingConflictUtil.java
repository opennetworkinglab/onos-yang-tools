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

package org.onosproject.yang.compiler.utils.io;

/**
 * Representation of YANG to java naming conflict resolver util.
 */
public final class YangToJavaNamingConflictUtil {

    /**
     * Contains the replacement value for a period.
     */
    private String replacementForPeriodInIdentifier;

    /**
     * Contains the replacement value for an underscore.
     */
    private String replacementForUnderscoreInIdentifier;

    /**
     * Contains the replacement value for a hyphen.
     */
    private String replacementForHyphenInIdentifier;

    /**
     * Contains the prefix value for adding with the identifier.
     */
    private String prefixForIdentifier;

    /**
     * Creates an object for YANG to java naming conflict util.
     */
    public YangToJavaNamingConflictUtil() {
    }

    /**
     * Sets the replacement value for a period.
     *
     * @param periodReplacement replacement value for period
     */
    public void setReplacementForPeriod(String periodReplacement) {
        replacementForPeriodInIdentifier = periodReplacement;
    }

    /**
     * Returns the replaced period value.
     *
     * @return replaced period
     */
    public String getReplacementForPeriod() {
        return replacementForPeriodInIdentifier;
    }

    /**
     * Sets the replacement value for a hyphen.
     *
     * @param hyphenReplacement replacement value for hyphen
     */
    public void setReplacementForHyphen(String hyphenReplacement) {
        replacementForHyphenInIdentifier = hyphenReplacement;
    }

    /**
     * Returns the replaced hyphen value.
     *
     * @return replaced hyphen
     */
    public String getReplacementForHyphen() {
        return replacementForHyphenInIdentifier;
    }

    /**
     * Sets the replacement value for an underscore.
     *
     * @param underscoreReplacement replacement value for underscore
     */
    public void setReplacementForUnderscore(String underscoreReplacement) {
        replacementForUnderscoreInIdentifier = underscoreReplacement;
    }

    /**
     * Returns the replaced underscore value.
     *
     * @return replaced underscore
     */
    public String getReplacementForUnderscore() {
        return replacementForUnderscoreInIdentifier;
    }

    /**
     * Sets the prefix value for adding with the identifier.
     *
     * @param prefix prefix for identifier
     */
    public void setPrefixForIdentifier(String prefix) {
        prefixForIdentifier = prefix;
    }

    /**
     * Returns the prefix for identifier.
     *
     * @return prefix for identifier
     */
    public String getPrefixForIdentifier() {
        return prefixForIdentifier;
    }
}
