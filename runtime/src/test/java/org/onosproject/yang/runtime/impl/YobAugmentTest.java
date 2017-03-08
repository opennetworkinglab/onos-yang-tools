/*
 * Copyright 2017-present Open Networking Laboratory
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
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.network.rev20151208.yrtietfnetwork.DefaultNetworks;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.network.rev20151208.yrtietfnetwork.networks.DefaultNetwork;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.network.rev20151208.yrtietfnetwork.networks.Network;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.network.topology.rev20151208.yrtnetworktopology.networks.network.DefaultAugmentedNdNetwork;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.network.topology.rev20151208.yrtnetworktopology.networks.network.augmentedndnetwork.DefaultLink;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.network.topology.rev20151208.yrtnetworktopology.networks.network.augmentedndnetwork.Link;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.te.topology.rev20160317.yrtietftetopology.networks.network.link.DefaultAugmentedNtLink;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.te.topology.rev20160317.yrtietftetopology.telinkaugment.te.Config;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.te.topology.rev20160317.yrtietftetopology.telinkconfig.bundlestacklevel.Bundle;
import org.onosproject.yang.gen.v1.urn.ietf.params.xml.ns.yang.yrt.ietf.te.topology.rev20160317.yrtietftetopology.telinkconfig.bundlestacklevel.bundle.bundledlinks.BundledLink;
import org.onosproject.yang.gen.v1.urn.ip.topo.rev20140101.ymsiptopology.node.DefaultAugmentedTopoNode;
import org.onosproject.yang.gen.v1.urn.topo.rev20140101.ymstopology.DefaultNode;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.runtime.DefaultResourceData;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.initializeDataNode;

/**
 * Tests the YANG object building for the YANG data nodes based on the non
 * schema augmented nodes.
 */
public class YobAugmentTest {
    private static final String TOPO_NAME_SPACE = "urn:topo";
    private static final String IP_TOPO_NAME_SPACE = "urn:ip:topo";
    private static final String NW_NAME_SPACE = "urn:ietf:params:xml:ns:yang:yrt-ietf-network";
    private static final String NW_TOPO_NAME_SPACE = "urn:ietf:params:xml:ns:yang:yrt-ietf-network-topology";
    private static final String TE_TOPO_NAME_SPACE =
            "urn:ietf:params:xml:ns:yang:yrt-ietf-te-topology";
    TestYangSerializerContext context = new TestYangSerializerContext();
    DataNode.Builder dBlr;
    String value;

    public DataNode buildDataNodeForAugmentedLeaves() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "node", TOPO_NAME_SPACE, value, null);

        value = "id";
        dBlr = addDataNode(dBlr, "node-id", TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str1";
        dBlr = addDataNode(dBlr, "router-id", IP_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str2";
        dBlr = addDataNode(dBlr, "router-ip", IP_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        return dBlr.build();
    }

    public DataNode buildDnForAugmentedList() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "networks", NW_NAME_SPACE, value, null);
        dBlr = addDataNode(dBlr, "network", NW_NAME_SPACE, value, null);
        value = "network-id";
        dBlr = addDataNode(dBlr, "network-id", NW_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "link", NW_TOPO_NAME_SPACE, value, null);
        value = "link-id";
        dBlr = addDataNode(dBlr, "link-id", NW_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "source", NW_TOPO_NAME_SPACE, value, null);
        value = "source-node";
        dBlr = addDataNode(dBlr, "source-node", NW_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // exit source
        dBlr = exitDataNode(dBlr); // exit link
        dBlr = exitDataNode(dBlr); // exit network
        dBlr = exitDataNode(dBlr); // exit networks
        return dBlr.build();
    }

    public DataNode buildDnForAugmentedListWithResourceId() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "node", TOPO_NAME_SPACE, value, null);

        value = "id";
        dBlr = addDataNode(dBlr, "node-id", TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str1";
        dBlr = addDataNode(dBlr, "router-id", IP_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str2";
        dBlr = addDataNode(dBlr, "router-ip", IP_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        return dBlr.build();
    }

    public DataNode buildDnForChoiceCase() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "networks", NW_NAME_SPACE, value, null);
        dBlr = addDataNode(dBlr, "network", NW_NAME_SPACE, value, null);
        value = "network-id";
        dBlr = addDataNode(dBlr, "network-id", NW_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "link", NW_TOPO_NAME_SPACE, value, null);
        value = "link-id";
        dBlr = addDataNode(dBlr, "link-id", NW_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "te", TE_TOPO_NAME_SPACE, value, null);
        dBlr = addDataNode(dBlr, "config", TE_TOPO_NAME_SPACE, value, null);
        dBlr = addDataNode(dBlr, "bundled-links", TE_TOPO_NAME_SPACE, value, null);
        dBlr = addDataNode(dBlr, "bundled-link", TE_TOPO_NAME_SPACE, value, null);
        value = "100";
        dBlr = addDataNode(dBlr, "sequence", TE_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr); // exit sequence
        value = "101";
        dBlr = addDataNode(dBlr, "src-tp-ref", TE_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr); // exit src-tp-ref
        dBlr = exitDataNode(dBlr); // exit list bundled-link
        dBlr = exitDataNode(dBlr); // exit bundled-links
        value = "abc";
        dBlr = addDataNode(dBlr, "te-link-template", TE_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr); // te-link-template
        dBlr = exitDataNode(dBlr); // config
        dBlr = exitDataNode(dBlr); // te
        dBlr = exitDataNode(dBlr); // exit link
        dBlr = exitDataNode(dBlr); // exit network
        dBlr = exitDataNode(dBlr); // exit networks
        return dBlr.build();
    }

    @Test
    public void augmentedLeaf() {
        DataNode dataNode = buildDataNodeForAugmentedLeaves();
        ResourceData data = DefaultResourceData.builder().addDataNode(dataNode)
                .build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultNode node = ((DefaultNode) modelObject);
        assertThat(node.nodeId(), is("id"));
        DefaultAugmentedTopoNode obj = node
                .augmentation(DefaultAugmentedTopoNode.class);
        assertThat(obj.routerId(), is("str1"));
        assertThat(obj.routerIp(), is("str2"));
    }

    @Test
    public void augmentedList() {
        DataNode dataNode = buildDnForAugmentedList();
        ResourceData data = DefaultResourceData.builder()
                .addDataNode(dataNode).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultNetworks networks = ((DefaultNetworks) modelObject);
        Network network = networks.network().get(0);
        assertThat(network.networkId().toString(), is("network-id"));
        DefaultAugmentedNdNetwork augNw = ((DefaultNetwork) network)
                .augmentation(DefaultAugmentedNdNetwork.class);
        Link link = augNw.link().get(0);
        assertThat(link.linkId().toString(), is("link-id"));
        assertThat(link.source().sourceNode().toString(), is("source-node"));
    }

    @Test
    public void augmentedChoiceCase() {
        DataNode dataNode = buildDnForChoiceCase();
        ResourceData data = DefaultResourceData.builder()
                .addDataNode(dataNode).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultNetworks networks = ((DefaultNetworks) modelObject);
        Network network = networks.network().get(0);
        assertThat(network.networkId().toString(), is("network-id"));
        DefaultAugmentedNdNetwork augNw = ((DefaultNetwork) network)
                .augmentation(DefaultAugmentedNdNetwork.class);
        Link link = augNw.link().get(0);
        assertThat(link.linkId().toString(), is("link-id"));
        DefaultAugmentedNtLink augLink = ((DefaultLink) link)
                .augmentation(DefaultAugmentedNtLink.class);
        Config config = augLink.te().config();
        assertThat(config.teLinkTemplate().get(0), is("abc"));
        BundledLink bundledLink = ((Bundle) config.bundleStackLevel())
                .bundledLinks().bundledLink().get(0);
        assertThat(bundledLink.srcTpRef(), is("101"));
        assertThat(bundledLink.sequence(), is(100L));
    }
}
