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

import java.util.Map;
import org.onosproject.yangutils.datamodel.exceptions.DataModelException;

/**
 * Abstraction of YANG data node, used by YMS to abstractly refer the data
 * nodes in YANG data tree.
 */
public interface YangSchemaNode {

    /**
     * Returns type of YANG schema node.
     *
     * @return type of YANG schema node
     */
    YangSchemaNodeType getYangSchemaNodeType();

    /**
     * Returns child schema information. It is used by YMS to obtain the child
     * schema corresponding to data node identifier.
     *
     * @param dataNodeIdentifier YANG data node identifier
     * @return YANG data node context information
     */
    YangSchemaNodeContextInfo getChildSchema(YangSchemaNodeIdentifier dataNodeIdentifier);

    /**
     * Returns count of mandatory child nodes, this is used by YMS to identify whether
     * in request all mandatory child nodes are available.
     *
     * @return count of YANG schema nodes
     * @throws DataModelException a violation in data model rule
     */
    int getMandatoryChildCount() throws DataModelException;

    /**
     * Returns map of default child nodes, this is used by YMS to identify whether
     * in request all default child nodes are available.
     *
     * @param dataNodeIdentifier YANG data node identifier
     * @return map of default child nodes
     */
    Map<YangSchemaNodeIdentifier, YangSchemaNode> getDefaultChild(YangSchemaNodeIdentifier dataNodeIdentifier);
}
