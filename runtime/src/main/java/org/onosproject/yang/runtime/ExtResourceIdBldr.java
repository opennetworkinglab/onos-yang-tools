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

package org.onosproject.yang.runtime;

import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.LeafListKey.LeafListKeyBuilder;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelException;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;

import java.util.LinkedList;
import java.util.List;

import static org.onosproject.yang.model.ModelConstants.LEAF_IS_TERMINAL;

/**
 * Representation of an entity which extends the resource identifier to
 * provide the additional functionality of traversing back in resource id.
 */
public final class ExtResourceIdBldr extends ResourceId.Builder {

    private List<NodeKey.NodeKeyBuilder> builders = new LinkedList<>();

    @Override
    protected void processCurKey() {
        if (curKeyBuilder != null) {
            if (curKeyBuilder instanceof LeafListKeyBuilder) {
                throw new ModelException(LEAF_IS_TERMINAL);
            }
            builders.add(curKeyBuilder);
        }
    }

    /**
     * Traverses up in current resource id by deleting the last key entry.
     * <p>
     * This shouldn't be called for key leaf.
     */
    public void traveseToParent() {
        if (curKeyBuilder != null) {
            curKeyBuilder = builders.get(builders.size() - 1);
            builders.remove(builders.size() - 1);
        }
    }

    @Override
    public ResourceId build() {
        return getResourceId();
    }

    /**
     * Returns the resource id for current node.
     *
     * @return resource Id
     */
    public ResourceId getResourceId() {
        if (curKeyBuilder != null) {
            builders.add(curKeyBuilder);
        }
        List<NodeKey> keys = new LinkedList<>();
        for (NodeKey.NodeKeyBuilder builder : builders) {
            keys.add(builder.build());
        }
        nodeKeyList = keys;
        builders.remove(builders.size() - 1);
        return (new ResourceId(this));
    }

    /**
     * Creates the extended resource id builder from given resource id
     * builder.
     *
     * @param ridBldr extended resource id builder
     * @param id      resource id
     * @return updated extended resource id builder
     */
    public ExtResourceIdBldr copyBuilder(ExtResourceIdBldr ridBldr,
                                         ResourceId id) {
        SchemaId sId;
        // Preparing the extended resource id builder from resourceId.
        List<NodeKey> keys = id.nodeKeys();

        for (NodeKey k : keys) {
            sId = k.schemaId();
            if (k instanceof ListKey) {
                List<KeyLeaf> kLeaf = ((ListKey) k).keyLeafs();
                for (KeyLeaf kl : kLeaf) {
                    sId = kl.leafSchema();
                    ridBldr.addKeyLeaf(sId.name(), sId.namespace(),
                                       kl.leafValue());
                }
            } else if (k instanceof LeafListKey) {
                sId = k.schemaId();
                ridBldr.addLeafListBranchPoint(sId.name(), sId.namespace(),
                                               ((LeafListKey) k).value());
            } else {
                ridBldr.addBranchPointSchema(sId.name(), sId.namespace());
            }
        }
        return ridBldr;
    }
}
