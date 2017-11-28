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

package org.onosproject.yang.compiler.datamodel;

/**
 * Represents augmented node schema info to maintain the information for augment
 * schema node and its index position in given class canonical name array
 * splitted with '.' to refer which node needs to be linked as anydata
 * contained node.
 */
public class AugmentedSchemaInfo {

    int position;
    YangSchemaNode schemaNode;

    /**
     * Creates a instance of augmented node schema info.
     *
     * @param s YANG schema node of augmented node
     * @param p index position in given class canonical name
     */
    public AugmentedSchemaInfo(YangSchemaNode s, int p) {
        position = p;
        schemaNode = s;
    }

    /**
     * Returns the position of given class canonical name till the current
     * schema.
     *
     * @return position of given class canonical name
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns the YANG schema node of the augmented node.
     *
     * @return YANG schema node
     */
    public YangSchemaNode getSchemaNode() {
        return schemaNode;
    }
}
