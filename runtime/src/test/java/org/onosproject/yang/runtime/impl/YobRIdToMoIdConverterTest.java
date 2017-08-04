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
import org.onosproject.yang.gen.v1.sample.Sample;
import org.onosproject.yang.gen.v1.sample.sample.DefaultL2;
import org.onosproject.yang.gen.v1.sample.sample.DefaultTop;
import org.onosproject.yang.gen.v1.sample.sample.L2Keys;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.DefaultNetworks;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.DefaultNetwork;
import org.onosproject.yang.gen.v1.yrtietftetopology.rev20160317.yrtietftetopology.telinkconfig.bundlestacklevel.bundle.DefaultBundledLinks;
import org.onosproject.yang.gen.v1.yrtietftetopology.rev20160317.yrtietftetopology.tetopologyaugment.DefaultTe;
import org.onosproject.yang.gen.v1.yrtietftetopology.rev20160317.yrtietftetopology.tetopologyaugment.te.DefaultConfig;
import org.onosproject.yang.gen.v1.yrtnetworktopology.rev20151208.yrtnetworktopology.networks.network.augmentedndnetwork.DefaultLink;
import org.onosproject.yang.gen.v1.yrtnetworktopology.rev20151208.yrtnetworktopology.networks.network.augmentedndnetwork.link.DefaultSource;
import org.onosproject.yang.model.AtomicPath;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.MultiInstanceLeaf;
import org.onosproject.yang.model.MultiInstanceNode;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SingleInstanceLeaf;
import org.onosproject.yang.model.SingleInstanceNode;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.onosproject.yang.runtime.SerializerHelper.addToResourceId;
import static org.onosproject.yang.runtime.SerializerHelper.initializeResourceId;

/**
 * Tests resource id to model object id convertion.
 */
public class YobRIdToMoIdConverterTest {
    private static final String NAME_SPACE = "samplenamespace";
    TestYangSerializerContext context = new TestYangSerializerContext();
    private static final String NW_NS = "urn:ietf:params:xml:ns:yang:yrt-ietf-network";
    private static final String TE_TOPO_NS =
            "urn:ietf:params:xml:ns:yang:yrt-ietf-te-topology";
    private static final String NW_TOPO_NS = "urn:ietf:params:xml:ns:yang:yrt-ietf-network-topology";
    private String value;
    private ResourceId.Builder rIdBlr;

    private ResourceId.Builder buildRIdForTopLevelContainer() {
        rIdBlr = initializeResourceId(context);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "top", NAME_SPACE, value);
        return rIdBlr;
    }

    private ResourceId.Builder buildRIdForTopLevelLeaf() {
        rIdBlr = initializeResourceId(context);
        value = "null";
        rIdBlr = addToResourceId(rIdBlr, "leaf5", NAME_SPACE, value);
        return rIdBlr;
    }

    private ResourceId.Builder buildRIdForTopLevelLeafList() {
        rIdBlr = initializeResourceId(context);
        value = "abc";
        rIdBlr = addToResourceId(rIdBlr, "leaf6", NAME_SPACE, value);
        return rIdBlr;
    }

    private ResourceId.Builder buildRIdForTopLevelList() {
        rIdBlr = initializeResourceId(context);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "l2", NAME_SPACE, value);
        value = "abc";
        rIdBlr = addToResourceId(rIdBlr, "k1", NAME_SPACE, value);
        return rIdBlr;
    }

    private ResourceId.Builder buildRIdForGroupingUses() {
        rIdBlr = initializeResourceId(context);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "networks", NW_NS, value);
        rIdBlr = addToResourceId(rIdBlr, "network", NW_NS, value);
        value = "network-id";
        rIdBlr = addToResourceId(rIdBlr, "network-id", NW_NS, value);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "te", TE_TOPO_NS, value);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "config", TE_TOPO_NS, value);
        return rIdBlr;
    }

    private ResourceId.Builder buildRIdForNodeInsideChoiceCase() {
        rIdBlr = initializeResourceId(context);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "networks", NW_NS, value);
        rIdBlr = addToResourceId(rIdBlr, "network", NW_NS, value);
        value = "network-id";
        rIdBlr = addToResourceId(rIdBlr, "network-id", NW_NS, value);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "link", NW_TOPO_NS, value);
        value = "link-id";
        rIdBlr = addToResourceId(rIdBlr, "link-id", NW_TOPO_NS, value);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "te", TE_TOPO_NS, value);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "config", TE_TOPO_NS, value);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "bundled-links", TE_TOPO_NS, value);
        return rIdBlr;
    }

    private ResourceId.Builder buildRIdForTopListInsideList() {
        rIdBlr = initializeResourceId(context);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "networks", NW_NS, value);
        rIdBlr = addToResourceId(rIdBlr, "network", NW_NS, value);
        value = "network-id";
        rIdBlr = addToResourceId(rIdBlr, "network-id", NW_NS, value);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "link", NW_TOPO_NS, value);
        value = "link-id";
        rIdBlr = addToResourceId(rIdBlr, "link-id", NW_TOPO_NS, value);
        value = null;
        rIdBlr = addToResourceId(rIdBlr, "source", NW_TOPO_NS, value);
        return rIdBlr;
    }

    @Test
    public void testTopLevelContainer() {
        ResourceId id = buildRIdForTopLevelContainer().build();
        ResourceData data = DefaultResourceData.builder()
                .resourceId(id).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        ModelObjectId mId = modelObjectData.identifier();
        List<AtomicPath> atomicPaths = mId.atomicPaths();
        SingleInstanceNode l1 = (SingleInstanceNode) atomicPaths.get(0);
        assertThat(l1.type(), is(DataNode.Type.SINGLE_INSTANCE_NODE));
        assertEquals(l1.container(), (DefaultTop.class));
    }

    @Test
    public void testTopLevelLeaf() {
        ResourceId id = buildRIdForTopLevelLeaf().build();
        ResourceData data = DefaultResourceData.builder()
                .resourceId(id).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        ModelObjectId mId = modelObjectData.identifier();
        List<AtomicPath> atomicPaths = mId.atomicPaths();
        SingleInstanceLeaf leaf5 = (SingleInstanceLeaf) atomicPaths.get(0);
        assertThat(leaf5.type(), is(DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE));
        assertEquals(leaf5.leafIdentifier(), Sample.LeafIdentifier.LEAF5);
    }

    @Test
    public void testTopLevelLeafList() {
        ResourceId id = buildRIdForTopLevelLeafList().build();
        ResourceData data = DefaultResourceData.builder()
                .resourceId(id).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        ModelObjectId mId = modelObjectData.identifier();
        List<AtomicPath> atomicPaths = mId.atomicPaths();
        MultiInstanceLeaf leaf6 = (MultiInstanceLeaf) atomicPaths.get(0);
        assertThat(leaf6.type(), is(DataNode.Type.MULTI_INSTANCE_LEAF_VALUE_NODE));
        assertEquals(leaf6.leafIdentifier(), Sample.LeafIdentifier.LEAF6);
    }

    @Test
    public void testTopLevelList() {
        ResourceId id = buildRIdForTopLevelList().build();
        ResourceData data = DefaultResourceData.builder()
                .resourceId(id).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        ModelObjectId mId = modelObjectData.identifier();
        List<AtomicPath> atomicPaths = mId.atomicPaths();
        MultiInstanceNode l2 = (MultiInstanceNode) atomicPaths.get(0);
        assertThat(l2.type(), is(DataNode.Type.MULTI_INSTANCE_NODE));
        assertEquals(l2.listClass(), DefaultL2.class);
        assertThat(((L2Keys) l2.key()).k1(), is("abc"));
    }

    @Test
    public void testMoIdForListInsideList() {
        ResourceId id = buildRIdForTopListInsideList().build();
        ResourceData data = DefaultResourceData.builder()
                .resourceId(id).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        ModelObjectId mId = modelObjectData.identifier();
        List<AtomicPath> atomicPaths = mId.atomicPaths();
        SingleInstanceNode network = (SingleInstanceNode) atomicPaths.get(0);
        assertThat(network.type(), is(DataNode.Type.SINGLE_INSTANCE_NODE));
        assertEquals(network.container(), DefaultNetworks.class);
        MultiInstanceNode networks = (MultiInstanceNode) atomicPaths.get(1);
        assertThat(networks.type(), is(DataNode.Type.MULTI_INSTANCE_NODE));
        assertEquals(networks.listClass(), DefaultNetwork.class);
        MultiInstanceNode lId = (MultiInstanceNode) atomicPaths.get(2);
        assertThat(lId.type(), is(DataNode.Type.MULTI_INSTANCE_NODE));
        assertEquals(lId.listClass(), DefaultLink.class);
        SingleInstanceNode src = (SingleInstanceNode) atomicPaths.get(3);
        assertThat(src.type(), is(DataNode.Type.SINGLE_INSTANCE_NODE));
        assertEquals(src.container(), DefaultSource.class);
    }

    @Test
    public void testMoIdForGroupingUses() {
        ResourceId id = buildRIdForGroupingUses().build();
        ResourceData data = DefaultResourceData.builder()
                .resourceId(id).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        ModelObjectId mId = modelObjectData.identifier();
        List<AtomicPath> atomicPaths = mId.atomicPaths();
        SingleInstanceNode network = (SingleInstanceNode) atomicPaths.get(0);
        assertThat(network.type(), is(DataNode.Type.SINGLE_INSTANCE_NODE));
        assertEquals(network.container(), DefaultNetworks.class);
        MultiInstanceNode networks = (MultiInstanceNode) atomicPaths.get(1);
        assertThat(networks.type(), is(DataNode.Type.MULTI_INSTANCE_NODE));
        assertEquals(networks.listClass(), DefaultNetwork.class);
        SingleInstanceNode te = (SingleInstanceNode) atomicPaths.get(2);
        assertThat(te.type(), is(DataNode.Type.SINGLE_INSTANCE_NODE));
        assertEquals(te.container(), DefaultTe.class);
        SingleInstanceNode config = (SingleInstanceNode) atomicPaths.get(3);
        assertThat(config.type(), is(DataNode.Type.SINGLE_INSTANCE_NODE));
        assertEquals(config.container(), DefaultConfig.class);
    }

    @Test
    public void testMoIdForNodeInsideChoiceCase() {
        ResourceId id = buildRIdForNodeInsideChoiceCase().build();
        ResourceData data = DefaultResourceData.builder()
                .resourceId(id).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        ModelObjectId mId = modelObjectData.identifier();
        List<AtomicPath> atomicPaths = mId.atomicPaths();
        SingleInstanceNode network = (SingleInstanceNode) atomicPaths.get(0);
        assertThat(network.type(), is(DataNode.Type.SINGLE_INSTANCE_NODE));
        assertEquals(network.container(), DefaultNetworks.class);
        MultiInstanceNode networks = (MultiInstanceNode) atomicPaths.get(1);
        assertThat(networks.type(), is(DataNode.Type.MULTI_INSTANCE_NODE));
        assertEquals(networks.listClass(), DefaultNetwork.class);
        MultiInstanceNode link = (MultiInstanceNode) atomicPaths.get(2);
        assertThat(link.type(), is(DataNode.Type.MULTI_INSTANCE_NODE));
        assertEquals(link.listClass(), DefaultLink.class);
        SingleInstanceNode te = (SingleInstanceNode) atomicPaths.get(3);
        assertThat(te.type(), is(DataNode.Type.SINGLE_INSTANCE_NODE));
        assertEquals(te.container(), org.onosproject.yang.gen.v1.yrtietftetopology
                .rev20160317.yrtietftetopology.telinkaugment.DefaultTe.class);
        SingleInstanceNode config = (SingleInstanceNode) atomicPaths.get(4);
        assertThat(config.type(), is(DataNode.Type.SINGLE_INSTANCE_NODE));
        assertEquals(config.container(), org.onosproject.yang.gen.v1.yrtietftetopology
                .rev20160317.yrtietftetopology.telinkaugment.te.DefaultConfig.class);
        SingleInstanceNode bundledlinks =
                (SingleInstanceNode) atomicPaths.get(5);
        assertThat(bundledlinks.type(), is(DataNode.Type.SINGLE_INSTANCE_NODE));
        assertEquals(bundledlinks.container(), DefaultBundledLinks.class);
    }
}
