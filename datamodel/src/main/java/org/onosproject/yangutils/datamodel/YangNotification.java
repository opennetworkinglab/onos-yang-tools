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

import org.onosproject.yangutils.datamodel.exceptions.DataModelException;
import org.onosproject.yangutils.datamodel.utils.Parsable;
import org.onosproject.yangutils.datamodel.utils.YangConstructType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static org.onosproject.yangutils.datamodel.YangNodeType.NOTIFICATION_NODE;
import static org.onosproject.yangutils.datamodel.YangSchemaNodeType.YANG_SINGLE_INSTANCE_NODE;
import static org.onosproject.yangutils.datamodel.YangStatusType.CURRENT;
import static org.onosproject.yangutils.datamodel.exceptions.ErrorMessages.COLLISION_DETECTION;
import static org.onosproject.yangutils.datamodel.exceptions.ErrorMessages.NOTIFICATION;
import static org.onosproject.yangutils.datamodel.exceptions.ErrorMessages.getErrorMsgCollision;
import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.detectCollidingChildUtil;
import static org.onosproject.yangutils.datamodel.utils.YangConstructType.NOTIFICATION_DATA;

/*
 * Reference RFC 6020.
 *
 * YANG allows the definition of notifications suitable for NETCONF.
 * YANG data definition statements are used to model the content of the
 * notification.
 *
 * The "notification" statement is used to define a NETCONF
 * notification.  It takes one argument, which is an identifier,
 * followed by a block of substatements that holds detailed notification
 * information.  The "notification" statement defines a notification
 * node in the schema tree.
 *
 * If a leaf in the notification tree has a "mandatory" statement with
 * the value "true", the leaf MUST be present in a NETCONF notification.
 *
 * If a leaf in the notification tree has a default value, the NETCONF
 * client MUST use this value in the same cases as described in
 * Section 7.6.1.  In these cases, the client MUST operationally behave
 * as if the leaf was present in the NETCONF notification with the
 * default value as its value.
 *
 * If a "config" statement is present for any node in the notification
 * tree, the "config" statement is ignored.
 *
 * The notification's substatements
 *
 *      +--------------+---------+-------------+------------------+
 *      | substatement | section | cardinality |data model mapping|
 *      +--------------+---------+-------------+------------------+
 *      | anyxml       | 7.10    | 0..n        | -not supported   |
 *      | choice       | 7.9     | 0..n        | -child nodes     |
 *      | container    | 7.5     | 0..n        | -child nodes     |
 *      | description  | 7.19.3  | 0..1        | -string          |
 *      | grouping     | 7.11    | 0..n        | -child nodes     |
 *      | if-feature   | 7.18.2  | 0..n        | -YangIfFeature   |
 *      | leaf         | 7.6     | 0..n        | -YangLeaf        |
 *      | leaf-list    | 7.7     | 0..n        | -YangLeafList    |
 *      | list         | 7.8     | 0..n        | -child nodes     |
 *      | reference    | 7.19.4  | 0..1        | -string          |
 *      | status       | 7.19.2  | 0..1        | -YangStatus      |
 *      | typedef      | 7.3     | 0..n        | -child nodes     |
 *      | uses         | 7.12    | 0..n        | -child nodes     |
 *      +--------------+---------+-------------+------------------+
 */

/**
 * Represents data model node to maintain information defined in YANG notification.
 */
public abstract class YangNotification
        extends YangNode
        implements YangLeavesHolder, YangCommonInfo, Parsable, CollisionDetector,
        YangAugmentableNode, YangIfFeatureHolder, InvalidOpTypeHolder {

    private static final long serialVersionUID = 806201611L;

    /**
     * Description of notification.
     */
    private String description;

    /**
     * List of leaves contained.
     */
    private List<YangLeaf> listOfLeaf;

    /**
     * List of leaf-lists contained.
     */
    private List<YangLeafList> listOfLeafList;

    /**
     * Reference of the module.
     */
    private String reference;

    /**
     * Status of the node.
     */
    private YangStatusType status = CURRENT;

    /**
     * List of if-feature.
     */
    private List<YangIfFeature> ifFeatureList;

    private final List<YangAugment> yangAugmentedInfo;

    /**
     * Create a notification node.
     */
    public YangNotification() {
        super(NOTIFICATION_NODE, new HashMap<>());
        listOfLeaf = new LinkedList<>();
        listOfLeafList = new LinkedList<>();
        ifFeatureList = new LinkedList<>();
        yangAugmentedInfo = new LinkedList<>();
    }

    @Override
    public void addToChildSchemaMap(YangSchemaNodeIdentifier id,
                                    YangSchemaNodeContextInfo context)
            throws DataModelException {
        getYsnContextInfoMap().put(id, context);
    }

    @Override
    public void incrementMandatoryChildCount() {
        // TODO
    }

    @Override
    public void addToDefaultChildMap(YangSchemaNodeIdentifier id,
                                     YangSchemaNode yangSchemaNode) {
        // TODO
    }

    @Override
    public YangSchemaNodeType getYangSchemaNodeType() {
        return YANG_SINGLE_INSTANCE_NODE;
    }

    @Override
    public void detectCollidingChild(String identifierName, YangConstructType dataType)
            throws DataModelException {
        // Detect colliding child.
        detectCollidingChildUtil(identifierName, dataType, this);
    }

    @Override
    public void detectSelfCollision(String identifierName, YangConstructType dataType)
            throws DataModelException {
        if (getName().equals(identifierName)) {
            throw new DataModelException(getErrorMsgCollision(
                    COLLISION_DETECTION, getName(), getLineNumber(),
                    getCharPosition(), NOTIFICATION, getFileName()));
        }
    }

    @Override
    public YangConstructType getYangConstructType() {
        return NOTIFICATION_DATA;
    }

    @Override
    public void validateDataOnEntry()
            throws DataModelException {
        //TODO: implement the method.
    }

    @Override
    public void validateDataOnExit()
            throws DataModelException {
        //TODO: implement the method.
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<YangLeaf> getListOfLeaf() {
        return unmodifiableList(listOfLeaf);
    }

    @Override
    public void setListOfLeaf(List<YangLeaf> leafsList) {
        listOfLeaf = leafsList;
    }

    @Override
    public void addLeaf(YangLeaf leaf) {
        listOfLeaf.add(leaf);
    }

    @Override
    public List<YangLeafList> getListOfLeafList() {
        return unmodifiableList(listOfLeafList);
    }

    @Override
    public void setListOfLeafList(List<YangLeafList> listOfLeafList) {
        this.listOfLeafList = listOfLeafList;
    }

    @Override
    public void addLeafList(YangLeafList leafList) {
        listOfLeafList.add(leafList);
    }

    @Override
    public String getReference() {
        return reference;
    }

    @Override
    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public YangStatusType getStatus() {
        return status;
    }

    @Override
    public void setStatus(YangStatusType status) {
        this.status = status;
    }

    @Override
    public List<YangIfFeature> getIfFeatureList() {
        return unmodifiableList(ifFeatureList);
    }

    @Override
    public void addIfFeatureList(YangIfFeature ifFeature) {
        ifFeatureList.add(ifFeature);
    }

    @Override
    public void setIfFeatureList(List<YangIfFeature> ifFeatureList) {
        this.ifFeatureList = ifFeatureList;
    }

    @Override
    public void addAugmentation(YangAugment augmentInfo) {
        yangAugmentedInfo.add(augmentInfo);
    }

    @Override
    public void removeAugmentation(YangAugment augmentInfo) {
        yangAugmentedInfo.remove(augmentInfo);
    }

    @Override
    public List<YangAugment> getAugmentedInfoList() {
        return unmodifiableList(yangAugmentedInfo);
    }

    @Override
    public void setLeafNameSpaceAndAddToParentSchemaMap() {
        // Add namespace for all leafs.
        for (YangLeaf yangLeaf : listOfLeaf) {
            yangLeaf.setLeafNameSpaceAndAddToParentSchemaMap(getNameSpace());
        }
        // Add namespace for all leaf list.
        for (YangLeafList yangLeafList : listOfLeafList) {
            yangLeafList.setLeafNameSpaceAndAddToParentSchemaMap(getNameSpace());
        }
    }
}
