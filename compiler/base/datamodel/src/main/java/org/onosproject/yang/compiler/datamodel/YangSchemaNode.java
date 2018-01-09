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

import java.util.Map;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.model.YangNamespace;

/**
 * Abstraction of YANG data node, used by YMS to abstractly refer the data
 * nodes in YANG data tree.
 */
public interface YangSchemaNode extends LocationInfo, SchemaContext {

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
     * @throws DataModelException data model exception in searching the child
     */
    YangSchemaNodeContextInfo getChildSchema(YangSchemaNodeIdentifier dataNodeIdentifier)
        throws DataModelException;

    /**
     * Validates whether the leaf/leaf-list value is valid as per YANG. It is
     * used by YMS to validate input value.
     *
     * @param value value of leaf/leaf-list
     * @throws DataModelException a violation in data model rule
     */
    void isValueValid(String value)
        throws DataModelException;

    /**
     * Returns count of mandatory child nodes, this is used by YMS to identify
     * whether in request all mandatory child nodes are available.
     *
     * @return count of YANG schema nodes
     * @throws DataModelException a violation in data model rule
     */
    int getMandatoryChildCount()
        throws DataModelException;

    /**
     * Returns map of default child nodes, this is used by YMS to identify
     * whether
     * in request all default child nodes are available.
     *
     * @param dataNodeIdentifier YANG data node identifier
     * @return map of default child nodes
     */
    Map<YangSchemaNodeIdentifier, YangSchemaNode> getDefaultChild(
        YangSchemaNodeIdentifier dataNodeIdentifier);

    /**
     * Get Java class's package corresponding to the schema node.
     *
     * @return java package, it is null, if the Java type is a built in data
     * type
     */
    String getJavaPackage();

    /**
     * Get Java class or built in data type corresponding to the schema node.
     *
     * @return Java class or built in data type corresponding to the schema node
     */
    String getJavaClassNameOrBuiltInType();

    /**
     * Returns schema node identifier.
     *
     * @return schema node identifier
     */
    YangSchemaNodeIdentifier getYangSchemaNodeIdentifier();

    /**
     * Returns name of the node.
     *
     * @return name of the node
     */
    String getName();

    /**
     * Returns Java attribute name.
     *
     * @return Java attribute name
     */
    String getJavaAttributeName();

    /**
     * Returns namespace of the node.
     *
     * @return namespace of the node
     */
    YangNamespace getNameSpace();

    /**
     * Checks for the presence of notification in module/sub-module. Exception
     * will be thrown if this is called for any other node type.
     *
     * @return true if notification is present, false otherwise
     * @throws DataModelException a violation in data model rule
     */
    boolean isNotificationPresent()
        throws DataModelException;

    /**
     * Checks for the presence of rpc in module/sub-module. Exception
     * will be thrown if this is called for any other node type.
     *
     * @return true if rpc is present, false otherwise
     * @throws DataModelException a violation in data model rule
     */
    boolean isRpcPresent() throws DataModelException;

    /**
     * Returns notification schema node corresponding to the name of
     * notification as per the generated code enumeration. This is to be used
     * for notification processing in YMS.
     *
     * @param notificationNameInEnum notification name in enum
     * @return notification schema node
     * @throws DataModelException a violation in data model rule
     */
    YangSchemaNode getNotificationSchemaNode(String notificationNameInEnum)
        throws DataModelException;

    /**
     * Returns referred schema node in case of grouping.
     *
     * @return referred schema node
     */
    YangSchemaNode getReferredSchema();

    /**
     * Checks for the presence of empty data-type in requested schema node.
     * Exception will be thrown if this is called for other then leaf/leaf-list
     * node type.
     *
     * @return true if empty data-type is present, false otherwise
     * @throws DataModelException when fails to do data model operations
     * @deprecated use LeafSchemaContext getLeafType() instead
     */
    @Deprecated
    boolean isEmptyDataType() throws DataModelException;

    /**
     * Sets the root parent context for the module/submodule child data node.
     *
     * @param context schema context
     */
    void setRootContext(SchemaContext context);

    /**
     * Returns YANG schema node context info map.
     *
     * @return YANG schema node context info map
     */
    Map<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo> getYsnContextInfoMap();

    /**
     * Adds schema to anydata.
     *
     * @param containedSchema schema to be added
     * @return cloned YANG schema node
     * @throws IllegalArgumentException when fails to do data model operations
     */
    YangSchemaNode addSchema(YangSchemaNode containedSchema)
        throws IllegalArgumentException;
}
