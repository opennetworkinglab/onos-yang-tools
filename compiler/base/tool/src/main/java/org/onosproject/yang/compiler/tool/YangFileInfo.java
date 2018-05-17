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

package org.onosproject.yang.compiler.tool;


import java.util.Objects;
import org.onosproject.yang.compiler.datamodel.YangNode;

import com.google.common.base.MoreObjects;

/**
 * Represents YANG file information.
 */
public class YangFileInfo {

    /**
     * YANG file name.
     */
    private String yangFileName;

    /**
     * Data model node after parsing YANG file.
     */
    private YangNode rootNode;

    /**
     * Flag to know if the root node require to be translated.
     */
    private boolean isForTranslator = true;

    /**
     * Flag to know if the root node is an inter-jar node.
     */
    private boolean isInterJar;

    /**
     * Returns data model node for YANG file.
     *
     * @return data model node for YANG file
     */
    public YangNode getRootNode() {
        return rootNode;
    }

    /**
     * Sets data model node for YANG file.
     *
     * @param rootNode of the Yang file
     */
    public void setRootNode(YangNode rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * Returns YANG file name.
     *
     * @return yangFileName YANG file name
     */
    public String getYangFileName() {
        return yangFileName;
    }

    /**
     * Sets YANG file name.
     *
     * @param yangFileName YANG file name
     */
    public void setYangFileName(String yangFileName) {
        this.yangFileName = yangFileName;
    }

    /**
     * Returns true if node need to be translated.
     *
     * @return isForTranslator true if node need to be translated
     */
    public boolean isForTranslator() {
        return isForTranslator;
    }

    /**
     * Sets true if node need to be translated.
     *
     * @param isForTranslator true if node need to be translated
     */
    public void setForTranslator(boolean isForTranslator) {
        this.isForTranslator = isForTranslator;
    }

    /**
     * Return true if node is inter-jar node.
     *
     * @return true if inter-jar, false otherwise
     */
    public boolean isInterJar() {
        return isInterJar;
    }

    /**
     * Sets true if it's inter-jar node.
     *
     * @param interJar flag indicating if it's inter-jar node
     */
    public void setInterJar(boolean interJar) {
        isInterJar = interJar;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj instanceof YangFileInfo) {
            final YangFileInfo other = (YangFileInfo) obj;
            return Objects.equals(yangFileName, other.yangFileName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(yangFileName);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("yangFileName", yangFileName)
                .add("isInterJar", isInterJar)
                .toString();
    }
}
