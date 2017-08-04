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

package org.onosproject.yang.compiler.datamodel;

import java.util.List;

/**
 * Represents YANG constructs which can be augmented.
 */
public interface YangAugmentableNode {

    /**
     * Adds augment info to the augment info list.
     *
     * @param augmentInfo augment info of node
     */
    void addAugmentation(YangAugment augmentInfo);

    /**
     * Removes augment info from the node.
     *
     * @param augmentInfo augment info of node
     */
    void removeAugmentation(YangAugment augmentInfo);

    /**
     * Returns list of augment info.
     *
     * @return list of augment info
     */
    List<YangAugment> getAugmentedInfoList();

    /**
     * Clones augment info.
     */
    void cloneAugmentInfo();
}
