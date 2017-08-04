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

import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.model.DataNode;

import java.util.Iterator;
import java.util.Map;

/**
 * Represents data tree info for all the nodes that are added to the
 * data tree.Contains the information which can be attached and retrieved
 * back from data tree while walking.
 */
class DataTreeNodeInfo {

    /**
     * Data node type info.
     */
    private DataNode.Type type;

    /**
     * Object of the corresponding YANG construct. This object is bound to
     * each and every data node. So, whenever walk of parent and sibling
     * happens, object can be retrieved from its data node.
     */
    private Object yangObject;

    /**
     * The list iterator since first content of the multi instance node is
     * faced. With this iterator the node can be walked multiple times till
     * it becomes empty.
     */
    private Iterator<Object> listIterator;

    /**
     * The current data node's, list of augments are iterated through this
     * iterator. Every time an augment is built completely, this iterator
     * gives the next augment node until it becomes empty.
     */
    private Iterator<YangAugment> augmentNodeItr;

    /**
     * The map with case object as value and choice node name as key is added
     * for the current data tree builder info. Every time a case schema node
     * comes, it takes this map and checks if it is present.
     */
    private Map<String, Object> choiceCaseMap;

    /**
     * When the case finds its object in map, it assigns it to case object of
     * the data tree builder info, so when its child wants to take the parent
     * object, they can take from the data tree builder info's case object.
     */
    private Object caseObject;

    /**
     * When the augment object is present, it assigns it to augment object of
     * the data tree builder info, so when its child wants to take the parent
     * object, they can take from the data tree info's augment object.
     */
    private Object augmentObject;

    /**
     * Constructs a default data tree node info.
     */
    DataTreeNodeInfo() {
    }

    /**
     * Returns the object of the YANG schema node.
     *
     * @return YANG node object
     */
    Object getYangObject() {
        return yangObject;
    }

    /**
     * Sets the object of the YANG schema node.
     *
     * @param yangObject YANG node object
     */
    void setYangObject(Object yangObject) {
        this.yangObject = yangObject;
    }

    /**
     * Returns the current list iterator of the YANG schema node.
     *
     * @return current list iterator for the schema node
     */
    Iterator<Object> getListIterator() {
        return listIterator;
    }

    /**
     * Sets the current list iterator of the YANG schema node.
     *
     * @param listIterator current list iterator for the schema node
     */
    void setListIterator(Iterator<Object> listIterator) {
        this.listIterator = listIterator;
    }

    /**
     * Returns the map of choice schema name and case object.
     *
     * @return choice name and case object map
     */
    Map<String, Object> getChoiceCaseMap() {
        return choiceCaseMap;
    }

    /**
     * Sets the map of choice schema name and case object.
     *
     * @param choiceCaseMap choice name and case object map
     */
    void setChoiceCaseMap(Map<String, Object> choiceCaseMap) {
        this.choiceCaseMap = choiceCaseMap;
    }

    /**
     * Returns the case object.
     *
     * @return case object
     */
    Object getCaseObject() {
        return caseObject;
    }

    /**
     * Sets the case node object.
     *
     * @param caseObject case node object
     */
    void setCaseObject(Object caseObject) {
        this.caseObject = caseObject;
    }

    /**
     * Returns the augment node object.
     *
     * @return augment node object
     */
    Object getAugmentObject() {
        return augmentObject;
    }

    /**
     * Sets the augment node object.
     *
     * @param augmentObject augment node object
     */
    void setAugmentObject(Object augmentObject) {
        this.augmentObject = augmentObject;
    }

    /**
     * Returns the current list iterator of the YANG augment node.
     *
     * @return augment node iterator
     */
    Iterator<YangAugment> getAugmentIterator() {
        return augmentNodeItr;
    }

    /**
     * Sets the current list iterator of the YANG augment node.
     *
     * @param augmentNodeItr augment node iterator
     */
    void setAugmentIterator(Iterator<YangAugment> augmentNodeItr) {
        this.augmentNodeItr = augmentNodeItr;
    }

    /**
     * Returns data node type info.
     *
     * @return data node type info
     */
    public DataNode.Type type() {
        return type;
    }

    /**
     * Sets data node type info.
     *
     * @param type data node type info
     */
    public void type(DataNode.Type type) {
        this.type = type;
    }
}
