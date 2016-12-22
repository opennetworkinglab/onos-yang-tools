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

package org.onosproject.yang.model;

/**
 * Abstraction of an entity which identifies a resource in the logical tree
 * data store. It is a recursive approach to locate a resource in the
 * instance tree.
 */

 /*-- Assume the below schema
 * container node1{
 *     list node2{
 *         key node3;
 *         leaf node3{
 *             type string;
 *         }
 *         leaf-list node4{
 *             type string
 *         }
 *     }
 * }
 * Assume an instance tree as below
 * node1
 * |----node2
 * |    |----node3
 * |    |    |----"val1"
 * |    |----node4
 * |    |    |----"val2"
 * |    |    |----"val3"
 * |----node2
 * |    |----node3
 * |    |    |----"val4"
 * |    |----node4
 * |    |    |----"val5"
 * |    |    |----"val6"
 * <p>
 *
 * Assume a resource identifier variable a is pointing to node4 with val3,
 * then its value is as follows
 *
 *      //identifies node1
 *      NodeKey containerKey = a.nodeKey();
 *      SchemaIdentifier schemaId = containerKey.identifier();//"node1"
 *
 *      //identifies a specific entry of list node2
 *      MultiInstanceNodeKey listKey;
 *      listKey = (MultiInstanceNodeKey) a.descendentIdentifier().nodeKey();
 *      schemaId = listKey.identifier();//"node2"
 *      List<LeafNode> keyLeaves = listKey.keyLeafs();
 *      LeafNode key = keyLeaves.get(0);
 *      schemaId = key.identifier();//"node3"
 *      String keyVal = key.asString();//"val1"
 *
 *      //identifiers a specific entry of leaf-list node4
 *      MultiInstanceLeafKey leafKey;
 *      leafKey = (MultiInstanceLeafKey) a.descendentIdentifier().
 *              descendentIdentifier().nodeKey();
 *      schemaId = leafKey.identifier();//"node4"
 *      keyVal = leafKey.asString();//val3
 *
 *      ResourceIdentifier termination = a.descendentIdentifier().descendentIdentifier().
 *              descendentIdentifier(); //null
 *
 */
public interface ResourceIdentifier {
    /**
     * Returns the node key used to uniquely identify the branch in the
     * logical tree.
     *
     * @return node key uniquely identifying the branch
     */
    NodeKey nodeKey();

    /**
     * Returns the descendent resource identifier.
     *
     * @return descendent resource identifier
     */
    ResourceIdentifier descendentIdentifier();
}
