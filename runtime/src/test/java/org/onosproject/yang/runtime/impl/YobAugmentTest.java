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
import org.onosproject.yang.gen.v1.ymsiptopology.rev20140101.ymsiptopology.node.DefaultAugmentedTopoNode;
import org.onosproject.yang.gen.v1.ymstopology.rev20140101.ymstopology.DefaultNode;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.DefaultNetworks;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.DefaultNetwork;
import org.onosproject.yang.gen.v1.yrtietfnetwork.rev20151208.yrtietfnetwork.networks.Network;
import org.onosproject.yang.gen.v1.yrtietftetopology.rev20160317.yrtietftetopology.TeTemplateName;
import org.onosproject.yang.gen.v1.yrtietftetopology.rev20160317.yrtietftetopology.networks.network.link.DefaultAugmentedNtLink;
import org.onosproject.yang.gen.v1.yrtietftetopology.rev20160317.yrtietftetopology.telinkaugment.te.Config;
import org.onosproject.yang.gen.v1.yrtietftetopology.rev20160317.yrtietftetopology.telinkconfig.bundlestacklevel.Bundle;
import org.onosproject.yang.gen.v1.yrtietftetopology.rev20160317.yrtietftetopology.telinkconfig.bundlestacklevel.bundle.bundledlinks.BundledLink;
import org.onosproject.yang.gen.v1.yrtietfyangtypes.rev20130715.yrtietfyangtypes.DateAndTime;
import org.onosproject.yang.gen.v1.yrtnetworktopology.rev20151208.yrtnetworktopology.TpId;
import org.onosproject.yang.gen.v1.yrtnetworktopology.rev20151208.yrtnetworktopology.networks.network.DefaultAugmentedNdNetwork;
import org.onosproject.yang.gen.v1.yrtnetworktopology.rev20151208.yrtnetworktopology.networks.network.augmentedndnetwork.Link;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.DefaultSubscriptions;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.DefaultTest;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.StreamType;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.check.Value;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.establishsubscription.DefaultEstablishSubscriptionInput;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.establishsubscription.DefaultEstablishSubscriptionOutput;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.establishsubscription.establishsubscriptionoutput.result.augmentedresult.Success;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.receiverinfo.receivers.Receiver;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.subscriptionpolicy.target.eventstream.AugmentedEventStream;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.subscriptionpolicynonmodifiable.target.EventStream;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.subscriptions.Subscription;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.subscriptions.subscription.receivers.receiver.AugmentedReceiver;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.test.value.AugmentedValue;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.InnerModelObject;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.addToResourceId;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeResourceId;

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
    private static final String SUB_NAME_SPACE = "http://org/ns/yrt/subscription";
    TestYangSerializerContext context = new TestYangSerializerContext();
    DataNode.Builder dBlr;
    String value;
    private ResourceId.Builder rIdBlr;

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

        value = "r1tor2";
        dBlr = addDataNode(dBlr, "router-path", IP_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);

        value = null;
        dBlr = addDataNode(dBlr, "aug-route", IP_TOPO_NAME_SPACE, value, null);
        value = "r2tor1";
        dBlr = addDataNode(dBlr, "router-path", IP_TOPO_NAME_SPACE, value, null);

        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

        value = "17";
        dBlr = addDataNode(dBlr, "value", IP_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);

        value = "18";
        dBlr = addDataNode(dBlr, "value", IP_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);

        value = null;
        dBlr = addDataNode(dBlr, "aug-route-c", IP_TOPO_NAME_SPACE, value, null);

        value = "valid";
        dBlr = addDataNode(dBlr, "val", IP_TOPO_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);

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
        assertThat(obj.routerPath(), is("r1tor2"));
        assertThat(obj.augRoute().routerPath(), is("r2tor1"));
        assertThat(obj.value().get(0).toString(), is("17"));
        assertThat(obj.value().get(1).toString(), is("18"));
        assertThat(obj.augRoutec().get(0).val(), is("valid"));
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
        DefaultAugmentedNdNetwork augNw = network.augmentation(
                DefaultAugmentedNdNetwork.class);
        Link link = augNw.link().get(0);
        assertThat(link.linkId().toString(), is("link-id"));
        DefaultAugmentedNtLink augLink = link.augmentation(
                DefaultAugmentedNtLink.class);
        Config config = augLink.te().config();
        TeTemplateName tName = (TeTemplateName) config.teLinkTemplate().get(0);
        assertThat(tName.string(), is("abc"));
        BundledLink bundledLink = ((Bundle) config.bundleStackLevel())
                .bundledLinks().bundledLink().get(0);
        TpId id = (TpId) bundledLink.srcTpRef();
        assertThat(id.uri().string(), is("101"));
        assertThat(bundledLink.sequence(), is(100L));
    }

    /**
     * Processes rpc input with uses augment.
     */
    @Test
    public void processRpcInputForUsesAug() {
        ResourceData data = buildDnForInputUsesAug();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultEstablishSubscriptionInput input =
                ((DefaultEstablishSubscriptionInput) modelObject);
        EventStream tgt = (EventStream) input.target();
        StreamType stream = tgt.stream();
        assertThat(stream.toString(), is("stream"));
        Map<Class<? extends InnerModelObject>, InnerModelObject> augments =
                tgt.augmentations();
        Iterator<Map.Entry<Class<? extends InnerModelObject>,
                InnerModelObject>> it = augments.entrySet().iterator();
        InnerModelObject obj = it.next().getValue();
        AugmentedEventStream es = (AugmentedEventStream) obj;
        DateAndTime replay = es.replayStartTime();
        assertThat(replay.toString(), is("2000-06-12T06:23:21.52Z"));
    }

    private ResourceData buildDnForInputUsesAug() {
        value = null;
        rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "establish-subscription",
                                 SUB_NAME_SPACE, value);
        dBlr = initializeDataNode(rIdBlr);
        dBlr = addDataNode(dBlr, "input", SUB_NAME_SPACE, value, null);
        value = "stream";
        dBlr = addDataNode(dBlr, "stream", SUB_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = "2000-06-12T06:23:21.52Z";
        dBlr = addDataNode(dBlr, "replay-start-time", SUB_NAME_SPACE,
                           value, null);
        dBlr = exitDataNode(dBlr);
        return DefaultResourceData.builder()
                .addDataNode(dBlr.build()).resourceId(rIdBlr.build()).build();
    }

    /**
     * Processes rpc output with uses augment.
     */
    @Test
    public void processRpcOutputForUsesAug() {
        ResourceData data = buildDnForOutputUsesAug();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultEstablishSubscriptionOutput output =
                ((DefaultEstablishSubscriptionOutput) modelObject);
        Success success = (Success) output.result();
        assertThat(success.identifier().toString(), is("1123"));
    }

    private ResourceData buildDnForOutputUsesAug() {
        value = null;
        rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "establish-subscription",
                                 SUB_NAME_SPACE, value);
        dBlr = initializeDataNode(rIdBlr);
        dBlr = addDataNode(dBlr, "output", SUB_NAME_SPACE, value, null);
        value = "1123";
        dBlr = addDataNode(dBlr, "identifier", SUB_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        return DefaultResourceData.builder()
                .addDataNode(dBlr.build()).resourceId(rIdBlr.build()).build();
    }

    /**
     * Processes container with uses augment with leaf node.
     */
    @Test
    public void processContainerForUsesAug() {
        ResourceData data = buildDnForContainerUsesAug();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultSubscriptions deSub = (DefaultSubscriptions) modelObject;
        List<Subscription> subList = deSub.subscription();
        Subscription sub = subList.get(0);
        assertThat(sub.identifier().toString(), is("1123"));
        Receiver rec = sub.receivers().receiver().get(0);
        assertThat(rec.address().toString(), is("dom1"));
        assertThat(rec.port().toString(), is("111"));
        Map<Class<? extends InnerModelObject>, InnerModelObject> augments =
                rec.augmentations();
        Iterator<Map.Entry<Class<? extends InnerModelObject>,
                InnerModelObject>> it = augments.entrySet().iterator();
        InnerModelObject obj = it.next().getValue();
        AugmentedReceiver aug = (AugmentedReceiver) obj;
        assertThat(aug.pushedNotifications().toString(), is("1113"));
        assertThat(aug.excludedNotifications().toString(), is("456"));
        assertThat(aug.status().toString(), is("subscription-status"));
    }

    private ResourceData buildDnForContainerUsesAug() {
        value = null;
        dBlr = initializeDataNode(context);
        dBlr = addDataNode(dBlr, "subscriptions", SUB_NAME_SPACE, value, null);
        dBlr = addDataNode(dBlr, "subscription", SUB_NAME_SPACE, value, null);
        value = "1123";
        dBlr = addDataNode(dBlr, "identifier", SUB_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "receivers", SUB_NAME_SPACE, value, null);
        dBlr = addDataNode(dBlr, "receiver", SUB_NAME_SPACE, value, null);
        value = "dom1";
        dBlr = addDataNode(dBlr, "address", SUB_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = "111";
        dBlr = addDataNode(dBlr, "port", SUB_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        value = "1113";
        dBlr = addDataNode(dBlr, "pushed-notifications", SUB_NAME_SPACE, value,
                           null);
        dBlr = exitDataNode(dBlr);
        value = "456";
        dBlr = addDataNode(dBlr, "excluded-notifications", SUB_NAME_SPACE,
                           value, null);
        dBlr = exitDataNode(dBlr);
        value = "subscription-status";
        dBlr = addDataNode(dBlr, "status", SUB_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        return DefaultResourceData.builder().addDataNode(dBlr.build()).build();
    }

    /**
     * Processes container with uses augment with node.
     */
    @Test
    public void processContainerTestForUsesAug() {
        ResourceData data = buildDnForContainerTestUsesAug();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultTest test = (DefaultTest) modelObject;
        Value val = test.value();
        assertThat(val.stop().isPresent(), is(true));
        Map<Class<? extends InnerModelObject>, InnerModelObject> augments =
                val.augmentations();
        Iterator<Map.Entry<Class<? extends InnerModelObject>,
                InnerModelObject>> it = augments.entrySet().iterator();
        InnerModelObject obj = it.next().getValue();
        AugmentedValue pro = (AugmentedValue) obj;
        assertThat(pro.proceed().isPresent(), is(true));
    }

    private ResourceData buildDnForContainerTestUsesAug() {
        value = null;
        dBlr = initializeDataNode(context);
        dBlr = addDataNode(dBlr, "test", SUB_NAME_SPACE, value, null);
        dBlr = addDataNode(dBlr, "value", SUB_NAME_SPACE, value, null);
        dBlr = addDataNode(dBlr, "proceed", SUB_NAME_SPACE, value, null);
        value = "true";
        dBlr = addDataNode(dBlr, "isPresent", SUB_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        value = null;
        dBlr = addDataNode(dBlr, "stop", SUB_NAME_SPACE, value, null);
        value = "true";
        dBlr = addDataNode(dBlr, "isPresent", SUB_NAME_SPACE, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        return DefaultResourceData.builder().addDataNode(dBlr.build()).build();
    }
}
