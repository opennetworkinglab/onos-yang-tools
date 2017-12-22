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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.DefaultTest;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.Giga;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.Optical;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.Typed;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.test.Con1;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.test.DefaultCon1;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.test.con1.DefaultInterfaces;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.test.con1.Interfaces;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.test.con1.interfaces.DefaultIntList;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.test.con1.interfaces.IntList;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.test.con1.interfaces.intlist.Available;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.test.con1.interfaces.intlist.DefaultAvailable;
import org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.typed.TypedUnion;
import org.onosproject.yang.gen.v1.identitytypes.rev20130715.identitytypes.Loopback;
import org.onosproject.yang.gen.v1.identitytypes.rev20130715.identitytypes.Physical;
import org.onosproject.yang.gen.v1.identitytypessecond.rev20130715.identitytypessecond.Ethernet;
import org.onosproject.yang.gen.v1.identitytypessecond.rev20130715.identitytypessecond.Virtual;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.Bitdef;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.DefaultType;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.DefaultVal;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.Id;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.Phy;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.Tdef1;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.TypeKeys;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.Vir;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.id.IdUnion;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.tdef1.Tdef1Union;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.type.Leaf1Union;
import org.onosproject.yang.gen.v1.modulelistandkeyaugment.rev20160826.modulelistandkeyaugment.val.AugmentedSchVal;
import org.onosproject.yang.gen.v1.yrtietfinettypes.rev20130715.yrtietfinettypes.DomainName;
import org.onosproject.yang.gen.v1.yrtietfinettypes.rev20130715.yrtietfinettypes.Host;
import org.onosproject.yang.gen.v1.yrtietfinettypes.rev20130715.yrtietfinettypes.PortNumber;
import org.onosproject.yang.gen.v1.yrtietfinettypes.rev20130715.yrtietfinettypes.host.HostUnion;
import org.onosproject.yang.gen.v1.yrtietfyangtypes.rev20130715.yrtietfyangtypes.Counter64;
import org.onosproject.yang.gen.v1.yrtietfyangtypes.rev20130715.yrtietfyangtypes.DateAndTime;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.Active;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.DefaultSubscriptions;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.Netconf;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.StreamType;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.SubscriptionId;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.SubscriptionStatusType;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.Subscriptions;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.establishsubscription.DefaultEstablishSubscriptionInput;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.establishsubscription.DefaultEstablishSubscriptionOutput;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.establishsubscription.EstablishSubscriptionInput;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.establishsubscription.EstablishSubscriptionOutput;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.establishsubscription.establishsubscriptionoutput.result.augmentedresult.DefaultSuccess;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.establishsubscription.establishsubscriptionoutput.result.augmentedresult.Success;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.receiverinfo.DefaultReceivers;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.receiverinfo.Receivers;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.receiverinfo.receivers.DefaultReceiver;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.receiverinfo.receivers.Receiver;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.subscriptionpolicy.target.eventstream.AugmentedEventStream;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.subscriptionpolicy.target.eventstream.DefaultAugmentedEventStream;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.subscriptionpolicynonmodifiable.target.DefaultEventStream;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.subscriptionpolicynonmodifiable.target.EventStream;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.subscriptions.DefaultSubscription;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.subscriptions.Subscription;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.subscriptions.subscription.receivers.receiver.AugmentedReceiver;
import org.onosproject.yang.gen.v1.yrtsubscription.yrtsubscription.subscriptions.subscription.receivers.receiver.DefaultAugmentedReceiver;
import org.onosproject.yang.gen.v1.ytbmodulewithcontainer.rev20160826.ytbmodulewithcontainer.gr1.Cont;
import org.onosproject.yang.gen.v1.ytbmodulewithcontainer.rev20160826.ytbmodulewithcontainer.gr1.DefaultCont;
import org.onosproject.yang.gen.v1.ytbmodulewithcontainer.rev20160826.ytbmodulewithcontainer.gr2.DefaultListener;
import org.onosproject.yang.gen.v1.ytbmodulewithcontainer.rev20160826.ytbmodulewithcontainer.gr2.Listener;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultModelObjectData.Builder;
import org.onosproject.yang.model.InnerModelObject;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafIdentifier;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.LeafNode;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;

import java.math.BigInteger;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.ModuleListAndKey.LeafIdentifier.LL1;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.ModuleListAndKey.LeafIdentifier.LL2;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.ModuleListAndKey.LeafIdentifier.LL3;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.ModuleListAndKey.LeafIdentifier.LL4;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.ModuleListAndKey.LeafIdentifier.LL5;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.ModuleListAndKey.LeafIdentifier.LL6;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.ModuleListAndKey.LeafIdentifier.LL7;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.ModuleListAndKey.LeafIdentifier.LL8;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.ModuleListAndKey.LeafIdentifier.LL9;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.Bitdef.fromString;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.Ll6Enum.ENUM1;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.type.Con1.LeafIdentifier.LL;
import static org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.type.Leaf6Enum.ENUM2;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.model.ModelObjectId.builder;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateLeafDataNodeNs;

/**
 * Unit test cases for resource id conversion from model object id.
 */
public class YtbResourceIdTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    DefaultYangModelRegistry reg;
    private ResourceData rscData;
    private DefaultDataTreeBuilder treeBuilder;
    private ResourceId id;
    private List<NodeKey> keys;
    private SchemaId sid;
    private ModelObjectId mid;
    private Builder data;

    /**
     * Prior setup for each UT.
     */
    @Before
    public void setUp() {
        processSchemaRegistry();
        reg = registry();
        treeBuilder = new DefaultDataTreeBuilder(reg);
    }

    /**
     * Processes and checks the conversion of model object id, which contains
     * a list with many keys, to resource id.
     */
    @Test
    public void processKeysInRid() {
        data = new Builder();
        mid = buildMidWithKeys().build();
        data.identifier(mid);
        rscData = treeBuilder.getResourceData(data.build());
        id = rscData.resourceId();
        validateRidWithKeys(id);
    }

    /**
     * Processes and checks the conversion of model object id, which contains
     * a list with many keys, a container and ends with a leaf-list, to
     * resource id.
     */
    @Test
    public void processNodeAndLeafListInRid() {
        data = new Builder();
        ModelObjectId.Builder builder = buildMidWithKeys();
        Tdef1 tdef1 = new Tdef1(Tdef1Union.fromString("thousand"));
        mid = builder.addChild(org.onosproject.yang.gen.v1.modulelistandkey
                                       .rev20160826.modulelistandkey
                                       .type.DefaultCon1.class)
                .addChild(LL, tdef1).build();
        data.identifier(mid);
        rscData = treeBuilder.getResourceData(data.build());
        id = rscData.resourceId();
        LeafListKey key = (LeafListKey) id.nodeKeys().get(3);
        assertThat(key.value(), is("thousand"));
    }

    /**
     * Processes and checks the conversion of model object id, which contains
     * a container and ends with an augmented leaf-list, to resource id.
     */
    @Test
    public void processAugmentedLeafListRid() {
        data = new Builder();
        mid = builder().addChild(DefaultVal.class)
                .addChild(AugmentedSchVal.LeafIdentifier.LL, fromString("num"))
                .build();
        data.identifier(mid);
        rscData = treeBuilder.getResourceData(data.build());
        id = rscData.resourceId();
        LeafListKey key = (LeafListKey) id.nodeKeys().get(2);
        assertThat(key.value(), is("num"));
    }

    /**
     * Processes and checks the conversion of model object id, which ends with
     * a leaf-list, to resource id.
     */
    @Test
    public void processLeafListInRid() {
        LeafListKey key = buildRidForLeafList(LL1, (byte) 18);
        assertThat(key.value(), is((byte) 18));

        key = buildRidForLeafList(LL2, Vir.class);
        assertThat(key.value(), is("vir"));

        Tdef1 def1 = new Tdef1(Tdef1Union.fromString("hundred"));
        key = buildRidForLeafList(LL3, def1);
        assertThat(key.value(), is("hundred"));

        key = buildRidForLeafList(LL4, Phy.class);
        assertThat(key.value(), is("phy"));

        key = buildRidForLeafList(LL5, "/1/2/3");
        assertThat(key.value(), is("/1/2/3"));

        key = buildRidForLeafList(LL6, ENUM1);
        assertThat(key.value(), is("enum1"));

        key = buildRidForLeafList(LL7, fromString("str"));
        assertThat(key.value(), is("str"));

        byte[] arr = Base64.getDecoder().decode("QXdnRQ==");
        key = buildRidForLeafList(LL8, arr);
        assertThat(key.value(), is("QXdnRQ=="));

        Id id = new Id(IdUnion.fromString("true"));
        key = buildRidForLeafList(LL9, id);
        assertThat(key.value(), is("true"));
    }

    /**
     * Builds resource id with the leaf identifier and the value.
     *
     * @param lId leaf identifier
     * @param val value
     * @return leaf list key
     */
    private LeafListKey buildRidForLeafList(LeafIdentifier lId, Object val) {
        data = new Builder();
        mid = ModelObjectId.builder().addChild(lId, val).build();
        data.identifier(mid);
        rscData = treeBuilder.getResourceData(data.build());
        id = rscData.resourceId();
        keys = id.nodeKeys();
        return (LeafListKey) keys.get(1);
    }

    /**
     * Validates values in the list keys of the resource id.
     *
     * @param id resource id
     */
    private void validateRidWithKeys(ResourceId id) {
        String nameSpace = "yms:test:ytb:tree:builder:for:list:having:list";
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("type", is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));

        ListKey listKey = (ListKey) keys.get(1);

        Iterator<KeyLeaf> it = listKey.keyLeafs().iterator();
        assertThat(9, is(listKey.keyLeafs().size()));

        validateKeyLeaf(it.next(), "leaf1", nameSpace, (byte) 8);

        validateKeyLeaf(it.next(), "leaf2", nameSpace, "vir");

        validateKeyLeaf(it.next(), "leaf3", nameSpace,
                        new BigInteger("176889"));

        validateKeyLeaf(it.next(), "leaf4", nameSpace, "phy");

        validateKeyLeaf(it.next(), "leaf5", nameSpace, "/class");

        validateKeyLeaf(it.next(), "leaf6", nameSpace, "enum2");

        validateKeyLeaf(it.next(), "leaf7", nameSpace, "num");

        validateKeyLeaf(it.next(), "leaf8", nameSpace,
                        "MTEwMTE="); //Base 64 encoding of '11011'

        //FIXME: Union under object provider.
        validateKeyLeaf(it.next(), "leaf9", nameSpace, "true");
    }

    /**
     * Builds model object id, containing list keys.
     *
     * @return model object id
     */
    private ModelObjectId.Builder buildMidWithKeys() {
        Leaf1Union l1 = new Leaf1Union((byte) 8);
        Tdef1Union tdef1Uni = new Tdef1Union(new BigInteger("176889"));
        Tdef1 def1 = new Tdef1(tdef1Uni);
        Bitdef def = fromString("num");
        byte[] arr = Base64.getDecoder().decode("MTEwMTE=");
        IdUnion idUni = new IdUnion(true);
        Id id = new Id(idUni);
        TypeKeys typeKeys = new TypeKeys();
        typeKeys.leaf1(l1);
        typeKeys.leaf2(Vir.class);
        typeKeys.leaf3(def1);
        typeKeys.leaf4(Phy.class);
        typeKeys.leaf5("/class");
        typeKeys.leaf6(ENUM2);
        typeKeys.leaf7(def);
        typeKeys.leaf8(arr);
        typeKeys.leaf9(id);
        return builder().addChild(DefaultType.class, typeKeys);
    }

    /**
     * Validates the key leaf with the respective values.
     *
     * @param keyLeaf   key leaf
     * @param lName     leaf name
     * @param nameSpace name space
     * @param value     leaf-list value
     */
    private void validateKeyLeaf(KeyLeaf keyLeaf, String lName,
                                 String nameSpace, Object value) {
        sid = keyLeaf.leafSchema();
        assertThat(lName, is(sid.name()));
        assertThat(nameSpace, is(sid.namespace()));
        assertThat(value, is(keyLeaf.leafValue()));
    }

    /**
     * Processes rpc input with uses augment.
     */
    @Test
    public void processRpcInputForUsesAug() {
        EstablishSubscriptionInput input = new
                DefaultEstablishSubscriptionInput();
        EventStream tgt = new DefaultEventStream();
        tgt.stream(new StreamType(Netconf.class));
        AugmentedEventStream es = new DefaultAugmentedEventStream();
        es.replayStartTime(DateAndTime.fromString("2000-06-12T06:23:21"));
        tgt.addAugmentation((InnerModelObject) es);
        input.target(tgt);
        data = new Builder();
        data.addModelObject((ModelObject) input);
        rscData = treeBuilder.getResourceData(data.build());

        List<DataNode> inDn = rscData.dataNodes();
        String ns = "http://org/ns/yrt/subscription";
        Iterator<DataNode> it = inDn.iterator();

        DataNode in = it.next();
        validateDataNode(in, "input", ns, SINGLE_INSTANCE_NODE, true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) in).childNodes();
        List<DataNode> inputDN = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            inputDN.add(c.getValue());
        }

        Iterator<DataNode> it1 = inputDN.iterator();
        validateDataNode(it1.next(), "stream", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "NETCONF");
        validateDataNode(it1.next(), "replay-start-time", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "2000-06-12T06:23:21");
    }

    /**
     * Processes rpc output with uses augment.
     */
    @Test
    public void processRpcOutputForUsesAug() {
        EstablishSubscriptionOutput output = new
                DefaultEstablishSubscriptionOutput();
        Success result = new DefaultSuccess();
        result.identifier(new SubscriptionId(876L));
        output.result(result);
        data = new Builder();
        data.addModelObject((ModelObject) output);
        rscData = treeBuilder.getResourceData(data.build());

        List<DataNode> inDn = rscData.dataNodes();
        String ns = "http://org/ns/yrt/subscription";
        Iterator<DataNode> it = inDn.iterator();

        DataNode in = it.next();
        validateDataNode(in, "output", ns, SINGLE_INSTANCE_NODE, true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) in).childNodes();
        List<DataNode> inputDN = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            inputDN.add(c.getValue());
        }

        Iterator<DataNode> it1 = inputDN.iterator();
        validateDataNode(it1.next(), "identifier", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "876");
    }

    /**
     * Processes container with uses augment.
     */
    @Test
    public void processContainerForUsesAug() {
        Subscriptions subs = new DefaultSubscriptions();
        Subscription sub = new DefaultSubscription();
        sub.identifier(new SubscriptionId(1112L));
        AugmentedReceiver aug = new DefaultAugmentedReceiver();
        aug.pushedNotifications(new Counter64(new BigInteger("1113")));
        aug.excludedNotifications(new Counter64(new BigInteger("456")));
        aug.status(new SubscriptionStatusType(Active.class));
        Receivers rcs = new DefaultReceivers();
        Receiver rc = new DefaultReceiver();
        rc.address(new Host(new HostUnion(new DomainName("dom1"))));
        rc.port(new PortNumber(111));
        rc.addAugmentation((InnerModelObject) aug);
        rcs.addToReceiver(rc);
        sub.receivers(rcs);
        subs.addToSubscription(sub);
        data = new Builder();
        data.addModelObject((ModelObject) subs);
        rscData = treeBuilder.getResourceData(data.build());

        List<DataNode> subsDn = rscData.dataNodes();
        String ns = "http://org/ns/yrt/subscription";
        Iterator<DataNode> it = subsDn.iterator();

        DataNode subsNode = it.next();
        validateDataNode(subsNode, "subscriptions", ns, SINGLE_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) subsNode).childNodes();
        List<DataNode> list = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            list.add(c.getValue());
        }

        it = list.iterator();
        DataNode subscription = it.next();
        validateDataNode(subscription, "subscription", ns, MULTI_INSTANCE_NODE,
                         true, null);

        child = ((InnerNode) subscription).childNodes();
        list = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            list.add(c.getValue());
        }

        it = list.iterator();
        validateDataNode(it.next(), "identifier", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "1112");
        DataNode receivers = it.next();
        validateDataNode(receivers, "receivers", ns, SINGLE_INSTANCE_NODE,
                         true, null);

        child = ((InnerNode) receivers).childNodes();
        list = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            list.add(c.getValue());
        }
        it = list.iterator();
        DataNode receiver = it.next();
        validateDataNode(receiver, "receiver", ns, MULTI_INSTANCE_NODE, true,
                         null);

        child = ((InnerNode) receiver).childNodes();
        list = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            list.add(c.getValue());
        }
        it = list.iterator();
        validateDataNode(it.next(), "address", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "dom1");
        validateDataNode(it.next(), "port", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "111");
        validateDataNode(it.next(), "pushed-notifications", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "1113");
        validateDataNode(it.next(), "excluded-notifications", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "456");
        validateDataNode(it.next(), "status", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "active");
    }

    /**
     * Processes uses under module, which has model object.
     */
    @Test
    public void processGroupingInModule() {
        Cont cont = new DefaultCont();
        Listener listener = new DefaultListener();
        listener.yangAutoPrefixWait("wait");
        cont.addToListener(listener);
        data = new Builder();
        data.addModelObject((ModelObject) cont);
        rscData = treeBuilder.getResourceData(data.build());
        List<DataNode> contDn = rscData.dataNodes();
        String ns = "yms:test:ytb:module:with:container";
        Iterator<DataNode> it = contDn.iterator();

        DataNode contNode = it.next();
        validateDataNode(contNode, "cont", ns, SINGLE_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) contNode).childNodes();
        List<DataNode> list = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            list.add(c.getValue());
        }

        it = list.iterator();
        DataNode lis = it.next();
        validateDataNode(lis, "listener", ns, MULTI_INSTANCE_NODE,
                         true, null);
        child = ((InnerNode) lis).childNodes();
        list = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            list.add(c.getValue());
        }
        it = list.iterator();
        validateDataNode(it.next(), "wait", ns,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "wait");
    }

    /**
     * Processes identity with value namespace.
     */
    @Test
    public void processIdentity() {
        org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.Available
                avail1 = new org.onosproject.yang.gen.v1.identitytest
                .rev20130715.identitytest.Available(Loopback.class);
        org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.Available
                avail2 = new org.onosproject.yang.gen.v1.identitytest
                .rev20130715.identitytest.Available(Giga.class);
        org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.Available
                avail3 = new org.onosproject.yang.gen.v1.identitytest
                .rev20130715.identitytest.Available(Ethernet.class);

        Available available = new DefaultAvailable();
        available.addToLl(avail1);
        available.addToLl(avail2);
        available.addToLl(avail3);

        TypedUnion typedUnion = new TypedUnion(Virtual.class);
        Typed typed = new Typed(typedUnion);
        IntList list = new DefaultIntList();
        list.iden(typed);
        list.available(available);

        org.onosproject.yang.gen.v1.identitytest.rev20130715.identitytest.Available
                avail4 = new org.onosproject.yang.gen.v1.identitytest
                .rev20130715.identitytest.Available(Giga.class);

        Available available2 = new DefaultAvailable();
        available2.addToLl(avail4);

        TypedUnion typedUnion2 = new TypedUnion(Optical.class);
        Typed typed2 = new Typed(typedUnion2);
        IntList list2 = new DefaultIntList();
        list2.iden(typed2);
        list2.available(available2);

        Interfaces ifs = new DefaultInterfaces();
        ifs.addToIntList(list);
        ifs.addToIntList(list2);

        Con1 con = new DefaultCon1();
        con.yangAutoPrefixInterface(Physical.class);
        con.interfaces(ifs);

        mid = builder().addChild(DefaultTest.class).build();
        data = new Builder();
        data.identifier(mid);
        data.addModelObject((ModelObject) con);
        rscData = treeBuilder.getResourceData(data.build());

        List<DataNode> contDn = rscData.dataNodes();
        String ns = "identity:ns:test:json:ser";
        String ns2 = "identity:list:ns:test:json:ser";
        String ns3 = "identity:list:second:ns:test:json:ser";
        Iterator<DataNode> it = contDn.iterator();

        DataNode contNode = it.next();
        validateDataNode(contNode, "con1", ns, SINGLE_INSTANCE_NODE,
                         true, null);

        Map<NodeKey, DataNode> child = ((InnerNode) contNode).childNodes();
        List<DataNode> ints = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            ints.add(c.getValue());
        }

        it = ints.iterator();
        DataNode ifl = it.next();
        validateLeafDataNodeNs((LeafNode) ifl, "physical", ns2);

        DataNode inters = it.next();
        validateDataNode(inters, "interfaces", ns, SINGLE_INSTANCE_NODE,
                         true, null);

        child = ((InnerNode) inters).childNodes();
        ints = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            ints.add(c.getValue());
        }
        Iterator<DataNode> listIt = ints.iterator();
        DataNode intLi1 = listIt.next();
        validateDataNode(intLi1, "int-list", ns,
                         MULTI_INSTANCE_NODE, true, null);
        child = ((InnerNode) intLi1).childNodes();
        ints = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            ints.add(c.getValue());
        }
        it = ints.iterator();
        DataNode id1 = it.next();
        validateLeafDataNodeNs((LeafNode) id1, "virtual", ns3);
        DataNode avail = it.next();
        validateDataNode(avail, "available", ns,
                         SINGLE_INSTANCE_NODE, true, null);

        child = ((InnerNode) avail).childNodes();
        ints = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            ints.add(c.getValue());
        }
        it = ints.iterator();
        validateLeafDataNodeNs((LeafNode) it.next(), "Loopback", ns2);
        validateLeafDataNodeNs((LeafNode) it.next(), "Giga", ns);
        validateLeafDataNodeNs((LeafNode) it.next(), "Ethernet", ns3);
        DataNode intLi2 = listIt.next();

        validateDataNode(intLi2, "int-list", ns,
                         MULTI_INSTANCE_NODE, true, null);
        child = ((InnerNode) intLi2).childNodes();
        ints = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            ints.add(c.getValue());
        }
        it = ints.iterator();
        DataNode id2 = it.next();
        validateLeafDataNodeNs((LeafNode) id2, "optical", ns);
        DataNode availab2 = it.next();
        validateDataNode(availab2, "available", ns,
                         SINGLE_INSTANCE_NODE, true, null);

        child = ((InnerNode) availab2).childNodes();
        ints = new LinkedList<>();
        for (Map.Entry<NodeKey, DataNode> c : child.entrySet()) {
            ints.add(c.getValue());
        }
        it = ints.iterator();
        validateLeafDataNodeNs((LeafNode) it.next(), "Giga", ns);
    }
}