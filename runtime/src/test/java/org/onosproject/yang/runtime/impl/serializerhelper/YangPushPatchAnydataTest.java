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

package org.onosproject.yang.runtime.impl.serializerhelper;

import org.junit.Test;
import org.onosproject.yang.gen.v1.yrtietfinterfaces.rev20140508.yrtietfinterfaces.DefaultInterfacesState;
import org.onosproject.yang.gen.v11.yrtietfyangpatch.rev20170222.yrtietfyangpatch.yangpatch.DefaultYangPatch;
import org.onosproject.yang.gen.v11.yrtietfyangpatch.rev20170222.yrtietfyangpatch.yangpatch.yangpatch.DefaultEdit;
import org.onosproject.yang.gen.v11.yrtietfyangpatch.rev20170222.yrtietfyangpatch.yangpatch.yangpatch.edit.DefaultValue;
import org.onosproject.yang.gen.v11.yrtietfyangpush.rev20161028.yrtietfyangpush.DefaultPushChangeUpdate;
import org.onosproject.yang.gen.v11.yrtietfyangpush.rev20161028.yrtietfyangpush.pushchangeupdate.DefaultDatastoreChanges;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import java.util.Iterator;

import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.INTERF_NS;
import static org.onosproject.yang.runtime.impl.TestUtils.PATCH_NS;
import static org.onosproject.yang.runtime.impl.TestUtils.PUSH_NS;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.walkINTree;

/**
 * Tests the serializer helper methods.
 */
public class YangPushPatchAnydataTest {


    private static final String[] EXPECTED = {
            "Entry Node is /.",
            "Entry Node is push-change-update.",
            "Entry Node is subscription-id.",
            "Exit Node is subscription-id.",
            "Entry Node is datastore-changes.",
            "Entry Node is yang-patch.",
            "Entry Node is patch-id.",
            "Exit Node is patch-id.",
            "Entry Node is edit.",
            "Entry Node is edit-id.",
            "Exit Node is edit-id.",
            "Entry Node is operation.",
            "Exit Node is operation.",
            "Entry Node is target.",
            "Exit Node is target.",
            "Entry Node is value.",
            "Entry Node is interfaces-state.",
            "Entry Node is interface.",
            "Entry Node is name.",
            "Exit Node is name.",
            "Entry Node is oper-status.",
            "Exit Node is oper-status.",
            "Exit Node is interface.",
            "Exit Node is interfaces-state.",
            "Exit Node is value.",
            "Exit Node is edit.",
            "Exit Node is yang-patch.",
            "Exit Node is datastore-changes.",
            "Exit Node is push-change-update.",
            "Exit Node is /."
    };

    /**
     * Test anydata add to data node builder.
     */
    @Test
    public void yangPushDataNodeTest() {

        TestYangSerializerContext context = new TestYangSerializerContext();
        DataNode.Builder dBlr = initializeDataNode(context);
        ModelObjectId id = new ModelObjectId.Builder()
                .addChild(DefaultPushChangeUpdate.class)
                .addChild(DefaultDatastoreChanges.class)
                .build();
        ModelObjectId id2 = new ModelObjectId.Builder()
                .addChild(DefaultYangPatch.class).build();

        ModelObjectId id1 = new ModelObjectId.Builder()
                .addChild(DefaultPushChangeUpdate.class)
                .addChild(DefaultDatastoreChanges.class)
                .addChild(DefaultYangPatch.class)
                .addChild(DefaultEdit.class, null)
                .addChild(DefaultValue.class)
                .build();
        ModelObjectId id3 = new ModelObjectId.Builder()
                .addChild(DefaultInterfacesState.class)
                .build();
        context.getRegistry().registerAnydataSchema(id, id2);
        context.getRegistry().registerAnydataSchema(id1, id3);
        DataNode n = yangPushDataTree(dBlr);
        validatePushDataNodeTree(n, false);
        walkINTree(n, EXPECTED);
    }

    /**
     * Creates the data node tree for actn anydata test case.
     *
     * @param dBlr data node builder
     */
    public static DataNode yangPushDataTree(DataNode.Builder dBlr) {

        String value = null;
        // Adding container configuration-schedules
        dBlr = addDataNode(dBlr, "push-change-update", PUSH_NS, value, null);
        value = "89";
        dBlr = addDataNode(dBlr, "subscription-id", null, value, null);
        dBlr = exitDataNode(dBlr);
        // Adding anydata container
        value = null;
        dBlr = addDataNode(dBlr, "datastore-changes", null, value, null);

        dBlr = addDataNode(dBlr, "yang-patch", PATCH_NS, value, null);
        value = "1";
        dBlr = addDataNode(dBlr, "patch-id", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = null;
        dBlr = addDataNode(dBlr, "edit", null, value, null);
        value = "edit1";
        dBlr = addDataNode(dBlr, "edit-id", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "merge";
        dBlr = addDataNode(dBlr, "operation", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "/ietf-interfaces/interfaces-state";
        dBlr = addDataNode(dBlr, "target", null, value, null);
        dBlr = exitDataNode(dBlr);

        // Adding anydata container
        value = null;
        dBlr = addDataNode(dBlr, "value", null, value, null);
        dBlr = addDataNode(dBlr, "interfaces-state", INTERF_NS, value, null);
        dBlr = addDataNode(dBlr, "interface", null, value, null);
        value = "eth0";
        dBlr = addDataNode(dBlr, "name", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "down";
        dBlr = addDataNode(dBlr, "oper-status", null, value, null);
        dBlr = exitDataNode(dBlr);

        dBlr = exitDataNode(dBlr); // interface
        dBlr = exitDataNode(dBlr); // interfaces-state
        dBlr = exitDataNode(dBlr); // value
        dBlr = exitDataNode(dBlr); // edit
        dBlr = exitDataNode(dBlr); // yang-patch
        dBlr = exitDataNode(dBlr); // datastore-changes
        dBlr = exitDataNode(dBlr); // push-change-update

        return dBlr.build();
    }

    /**
     * Validates the given data node sub-tree.
     *
     * @param node  data node which needs to be validated
     * @param isYtb is it YTB call as tree comes from YTB will not be having
     *              "/" as parent node
     */
    public static void validatePushDataNodeTree(DataNode node, boolean isYtb) {
        // Validating the data node.
        DataNode n = node;
        Iterator<DataNode> it;
        if (!isYtb) {
            validateDataNode(n, "/", null,
                             SINGLE_INSTANCE_NODE, true, null);
            it = ((InnerNode) n).childNodes().values().iterator();
            n = it.next();
        }
        validateDataNode(n, "push-change-update", PUSH_NS,
                         SINGLE_INSTANCE_NODE, true, null);
        it = ((InnerNode) n).childNodes().values().iterator();
        n = it.next();
        validateDataNode(n, "subscription-id", PUSH_NS,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "89");
        n = it.next();
        validateDataNode(n, "datastore-changes", PUSH_NS,
                         SINGLE_INSTANCE_NODE, true, null);

        Iterator<DataNode> it1 = ((InnerNode) n).childNodes().values()
                .iterator();
        n = it1.next();
        validateDataNode(n, "yang-patch", PATCH_NS, SINGLE_INSTANCE_NODE,
                         true, null);
        it1 = ((InnerNode) n).childNodes().values().iterator();
        n = it1.next();
        validateDataNode(n, "patch-id", PATCH_NS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "1");

        n = it1.next();
        validateDataNode(n, "edit", PATCH_NS, MULTI_INSTANCE_NODE,
                         true, null);

        Iterator<DataNode> it3 = ((InnerNode) n).childNodes().values()
                .iterator();
        n = it3.next();
        validateDataNode(n, "edit-id", PATCH_NS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "edit1");
        n = it3.next();
        validateDataNode(n, "operation", PATCH_NS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "merge");
        n = it3.next();
        validateDataNode(n, "target", PATCH_NS,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "/ietf-interfaces/interfaces-state");

        n = it3.next();
        validateDataNode(n, "value", PATCH_NS, SINGLE_INSTANCE_NODE,
                         true, null);

        it3 = ((InnerNode) n).childNodes().values().iterator();
        n = it3.next();
        validateDataNode(n, "interfaces-state", INTERF_NS, SINGLE_INSTANCE_NODE,
                         true, null);
        it3 = ((InnerNode) n).childNodes().values().iterator();
        n = it3.next();
        validateDataNode(n, "interface", INTERF_NS, MULTI_INSTANCE_NODE,
                         true, null);
        it3 = ((InnerNode) n).childNodes().values().iterator();
        n = it3.next();
        validateDataNode(n, "name", INTERF_NS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "eth0");
        n = it3.next();
        validateDataNode(n, "oper-status", INTERF_NS, SINGLE_INSTANCE_LEAF_VALUE_NODE,
                         false, "down");
    }
}
