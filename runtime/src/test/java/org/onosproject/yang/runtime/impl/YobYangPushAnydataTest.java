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

import org.junit.Test;
import org.onosproject.yang.gen.v1.yrtietfinterfaces.rev20140508.yrtietfinterfaces.DefaultInterfacesState;
import org.onosproject.yang.gen.v1.yrtietfinterfaces.rev20140508.yrtietfinterfaces.interfacesstate.YangAutoPrefixInterface;
import org.onosproject.yang.gen.v11.yrtietfyangpatch.rev20170222.yrtietfyangpatch.yangpatch.DefaultYangPatch;
import org.onosproject.yang.gen.v11.yrtietfyangpatch.rev20170222.yrtietfyangpatch.yangpatch.yangpatch.DefaultEdit;
import org.onosproject.yang.gen.v11.yrtietfyangpatch.rev20170222.yrtietfyangpatch.yangpatch.yangpatch.Edit;
import org.onosproject.yang.gen.v11.yrtietfyangpatch.rev20170222.yrtietfyangpatch.yangpatch.yangpatch.edit.DefaultValue;
import org.onosproject.yang.gen.v11.yrtietfyangpush.rev20161028.yrtietfyangpush.DefaultPushChangeUpdate;
import org.onosproject.yang.gen.v11.yrtietfyangpush.rev20161028.yrtietfyangpush.pushchangeupdate.DefaultDatastoreChanges;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.InnerModelObject;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.ResourceData;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.serializerhelper.YangPushPatchAnydataTest.yangPushDataTree;

/**
 * Tests the YANG object building for the YANG data nodes based on the non
 * schema augmented nodes.
 */
public class YobYangPushAnydataTest {
    TestYangSerializerContext context = new TestYangSerializerContext();

    @Test
    public void anydataTest() {
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
        DataNode dataNode = yangPushDataTree(dBlr);

        ResourceData data = DefaultResourceData.builder()
                .addDataNode(dataNode).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(context.getRegistry());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultPushChangeUpdate update = ((DefaultPushChangeUpdate) modelObject);
        assertThat(update.subscriptionId().toString(), is("89"));
        DefaultDatastoreChanges changes = ((DefaultDatastoreChanges) update
                .datastoreChanges());

        List<InnerModelObject> patch = changes.anydata(DefaultYangPatch.class);
        assertThat(((DefaultYangPatch) patch.get(0)).patchId().toString(), is("1"));

        Edit edit = ((DefaultYangPatch) patch.get(0)).edit().get(0);
        assertThat(edit.editId().toString(), is("edit1"));
        assertThat(edit.operation().toString(), is("merge"));
        assertThat(edit.target().toString(), is("/ietf-interfaces/interfaces-state"));

        DefaultValue val = (DefaultValue) edit.value();
        List<InnerModelObject> interState = val.anydata(DefaultInterfacesState.class);
        YangAutoPrefixInterface interfaces =
                ((DefaultInterfacesState) interState.get(0)).yangAutoPrefixInterface().get(0);
        assertThat(interfaces.name(), is("eth0"));
        assertThat(interfaces.operStatus().toString(), is("down"));
    }
}
