/*
 * Copyright 2016-present Open Networking Laboratory
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

package org.onosproject.yangutils.datamodel;

import java.util.List;

/**
 * Represents class having rpc and notification.
 */
public interface RpcNotificationContainer {

    /**
     * Sets notification presence flag.
     *
     * @param notificationPresent notification presence flag
     */
    void setNotificationPresenceFlag(boolean notificationPresent);

    /**
     * Adds to notification enumeration map.
     *
     * @param enumName   name of notification in enum
     * @param schemaNode schema node of notification
     */
    void addToNotificationEnumMap(String enumName, YangSchemaNode schemaNode);

    /**
     * Adds augment which is augmenting input node to augment list.
     *
     * @param augment augment which is augmenting input
     */
    void addToAugmentList(YangAugment augment);

    /**
     * Returns augment list.
     *
     * @return augment list
     */
    List<YangAugment> getAugmentList();

    /**
     * Returns prefix.
     *
     * @return prefix
     */
    String getPrefix();

    /**
     * Returns list of notification nodes.
     *
     * @return list of notification nodes
     */
    List<YangNode> getNotificationNodes();
}
