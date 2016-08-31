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
     * @param nameOfNotificationInEnum name of notification in enum
     * @param notficationSchemaNode    schema node of notification
     */
    void addToNotificationEnumMap(String nameOfNotificationInEnum,
                                  YangSchemaNode notficationSchemaNode);
}
