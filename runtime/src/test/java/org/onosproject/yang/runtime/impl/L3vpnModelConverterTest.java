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
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.AddressAllocationType;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.DefaultL3VpnSvc;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.RoutingProtocolType;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.SiteNetworkAccessType;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.SiteRole;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.SvcId;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.accessvpnpolicy.DefaultVpnAttachment;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.accessvpnpolicy.vpnattachment.attachmentflavor.DefaultVpnId;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.l3vpnsvc.DefaultSites;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.l3vpnsvc.DefaultVpnServices;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.l3vpnsvc.sites.DefaultSite;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.l3vpnsvc.sites.SiteKeys;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.l3vpnsvc.sites.site.DefaultSiteNetworkAccesses;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.l3vpnsvc.sites.site.sitenetworkaccesses.DefaultSiteNetworkAccess;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.l3vpnsvc.vpnservices.DefaultVpnSvc;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.siteattachmentbearer.DefaultBearer;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.siteattachmentbearer.bearer.DefaultRequestedType;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.siteattachmentipconnection.DefaultIpConnection;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.siteattachmentipconnection.ipconnection.DefaultIpv4;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.siteattachmentipconnection.ipconnection.ipv4.DefaultAddresses;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.siterouting.DefaultRoutingProtocols;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.siterouting.routingprotocols.DefaultRoutingProtocol;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.siterouting.routingprotocols.routingprotocol.DefaultBgp;
import org.onosproject.yang.gen.v1.l3vpnsvcext.rev20160730.l3vpnsvcext.bearerattachmentgrouping.DefaultBearerAttachment;
import org.onosproject.yang.gen.v1.l3vpnsvcext.rev20160730.l3vpnsvcext.l3vpnsvc.sites.site.sitenetworkaccesses.sitenetworkaccess.bearer.DefaultAugmentedL3VpnBearer;
import org.onosproject.yang.gen.v1.l3vpnsvcext.rev20160730.l3vpnsvcext.l3vpnsvc.sites.site.sitenetworkaccesses.sitenetworkaccess.bearer.requestedtype.DefaultAugmentedL3VpnRequestedType;
import org.onosproject.yang.gen.v1.l3vpnsvcext.rev20160730.l3vpnsvcext.requestedtypegrouping.DefaultRequestedTypeProfile;
import org.onosproject.yang.gen.v1.l3vpnsvcext.rev20160730.l3vpnsvcext.requestedtypegrouping.requestedtypeprofile.requestedtypechoice.DefaultPhysicalCase;
import org.onosproject.yang.gen.v1.l3vpnsvcext.rev20160730.l3vpnsvcext.requestedtypegrouping.requestedtypeprofile.requestedtypechoice.physicalcase.DefaultPhysical;
import org.onosproject.yang.gen.v1.yrtietfinettypes.rev20130715.yrtietfinettypes.Ipv4Address;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultModelObjectData;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_LEAF_VALUE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;

/**
 * Unit test for l3vpn app. test for conversion of model data to resource data.
 */
public class L3vpnModelConverterTest {

    private ResourceData rscData;
    private DefaultDataTreeBuilder treeBuilder;
    private ResourceId id;
    private List<NodeKey> keys;
    private SchemaId sid;
    private List<DataNode> dataNodes;
    private DataNode node;
    private DefaultModelObjectData.Builder data;
    private static final String NAME_SPACE_SVC =
            "urn:ietf:params:xml:ns:yang:ietf-l3vpn-svc";
    private static final String NAME_SPACE_EXT =
            "urn:ietf:params:xml:ns:yang:l3vpn:svc:ext";

    /**
     * Do the prior setup for each UT.
     */
    private void setUp() {
        processSchemaRegistry();
        DefaultYangModelRegistry registry = registry();
        treeBuilder = new DefaultDataTreeBuilder(registry);
    }

    /**
     * Unit test for empty model id and container object of l3vpn-svc.
     */
    @Test
    public void emptyModelId() {
        setUp();
        data = new DefaultModelObjectData.Builder();
        data.addModelObject(l3VpnObject());
        rscData = treeBuilder.getResourceData(data.build());

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(1, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);

        validateDataNode(node, "l3vpn-svc", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        //validate l3vpn-svc 's child nodes.
        //first validate vpn-services.
        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        assertThat(2, is(childMap.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();

        validateDataNode(n.getValue(), "vpn-services", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(1, is(childMap.size()));
        it = childMap.entrySet().iterator();
        n = it.next();

        validateDataNode(n.getValue(), "vpn-svc", NAME_SPACE_SVC,
                         MULTI_INSTANCE_NODE, true, null);

        node = dataNodes.get(0);

        //validate l3vpn-svc 's child nodes.
        //now validate site network access.
        childMap = ((InnerNode) node).childNodes();

        it = childMap.entrySet().iterator();
        n = it.next();
        n = it.next();

        //container sites.
        validateDataNode(n.getValue(), "sites", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE,
                         true, null);

        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(1, is(childMap.size()));
        it = childMap.entrySet().iterator();
        n = it.next();

        //list site
        validateDataNode(n.getValue(), "site", NAME_SPACE_SVC,
                         MULTI_INSTANCE_NODE, true, null);

        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(2, is(childMap.size()));
        it = childMap.entrySet().iterator();
        n = it.next();

        //leaf site-id
        validateDataNode(n.getValue(), "site-id", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "first-site");

        //container site network access
        n = it.next();
        validateDataNode(n.getValue(), "site-network-accesses", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true, null);

        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(1, is(childMap.size()));
        it = childMap.entrySet().iterator();
        n = it.next();

        //list site network access.
        validateDataNode(n.getValue(), "site-network-access", NAME_SPACE_SVC,
                         MULTI_INSTANCE_NODE, true, null);

        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(6, is(childMap.size()));
        it = childMap.entrySet().iterator();
        n = it.next();

        //leaf site network access id.
        validateDataNode(n.getValue(), "site-network-access-id", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "first-site-network-access-id");

        n = it.next();
        //leaf site network access type.
        validateDataNode(n.getValue(), "site-network-access-type", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "site-network-access-type");

        n = it.next();
        //grouping container bearer with augments.
        validateDataNode(n.getValue(), "bearer", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child nodes of bearer.
        validateBearer(n.getValue());

        n = it.next();
        //container ip connection form grouping.
        validateDataNode(n.getValue(), "ip-connection", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate ip connections's child node.
        validateIpConnections(n.getValue());

        n = it.next();
        //container routing protocols form grouping.
        validateDataNode(n.getValue(), "routing-protocols", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child nodes of routing protocols
        validateRoutingProtocols(n.getValue());

        n = it.next();
        //container vpn attachment form grouping.
        validateDataNode(n.getValue(), "vpn-attachment", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child node of vpn attachments.
        validateVpnAttachments(n.getValue());
    }

    /**
     * Unit test for model id with l3vpn-svc and container object of
     * vpn services.
     */
    @Test
    public void modelIdForVpnServices() {
        setUp();
        data = new DefaultModelObjectData.Builder();
        data.addModelObject((ModelObject) l3VpnObject().vpnServices());
        data.identifier(ModelObjectId.builder().addChild(DefaultL3VpnSvc.class)
                                .build());
        rscData = treeBuilder.getResourceData(data.build());

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("l3vpn-svc", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);

        validateDataNode(node, "vpn-services", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE,
                         true, null);
        //validate vpn-services 's child nodes.
        //first validate vpn-services.
        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        assertThat(1, is(childMap.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();
        validateDataNode(n.getValue(), "vpn-svc", NAME_SPACE_SVC,
                         MULTI_INSTANCE_NODE, true, null);
    }

    /**
     * Unit test for model id with l3vpn-svc and vpn-services and container
     * object of list.
     */
    @Test
    public void modelIdForVpnServicesAndChild() {
        setUp();
        data = new DefaultModelObjectData.Builder();
        data.addModelObject((ModelObject) l3VpnObject().vpnServices()
                .vpnSvc().get(0));
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultL3VpnSvc.class)
                                .addChild(DefaultVpnServices.class)
                                .build());
        rscData = treeBuilder.getResourceData(data.build());

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(3, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("l3vpn-svc", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        sid = keys.get(2).schemaId();
        assertThat("vpn-services", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        node = dataNodes.get(0);
        validateDataNode(node, "vpn-svc", NAME_SPACE_SVC,
                         MULTI_INSTANCE_NODE, true, null);
    }

    /**
     * Unit test for model id for l3vpn svc and container object of sites.
     */
    @Test
    public void modelObjSites() {
        setUp();
        data = new DefaultModelObjectData.Builder();
        data.addModelObject((ModelObject) l3VpnObject().sites());
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultL3VpnSvc.class)
                                .build());
        rscData = treeBuilder.getResourceData(data.build());

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(2, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("l3vpn-svc", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        //validate l3vpn-svc 's child nodes.
        //first validate vpn-services.

        node = dataNodes.get(0);

        validateDataNode(node, "sites", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE,
                         true, null);
        //validate l3vpn-svc 's child nodes.
        //now validate site network access.
        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        assertThat(1, is(childMap.size()));
        childMap = ((InnerNode) node).childNodes();

        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();

        //list site
        validateDataNode(n.getValue(), "site", NAME_SPACE_SVC,
                         MULTI_INSTANCE_NODE, true, null);

        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(2, is(childMap.size()));
        it = childMap.entrySet().iterator();
        n = it.next();

        //leaf site-id
        validateDataNode(n.getValue(), "site-id", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "first-site");

        //container site network access
        n = it.next();
        validateDataNode(n.getValue(), "site-network-accesses", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true, null);

        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(1, is(childMap.size()));
        it = childMap.entrySet().iterator();
        n = it.next();

        //list site network access.
        validateDataNode(n.getValue(), "site-network-access", NAME_SPACE_SVC,
                         MULTI_INSTANCE_NODE, true, null);

        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(6, is(childMap.size()));
        it = childMap.entrySet().iterator();
        n = it.next();

        //leaf site network access id.
        validateDataNode(n.getValue(), "site-network-access-id", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "first-site-network-access-id");

        n = it.next();
        //leaf site network access type.
        validateDataNode(n.getValue(), "site-network-access-type", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "site-network-access-type");

        n = it.next();
        //grouping container bearer with augments.
        validateDataNode(n.getValue(), "bearer", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child nodes of bearer.
        validateBearer(n.getValue());

        n = it.next();
        //container ip connection form grouping.
        validateDataNode(n.getValue(), "ip-connection", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate ip connections's child node.
        validateIpConnections(n.getValue());

        n = it.next();
        //container routing protocols form grouping.
        validateDataNode(n.getValue(), "routing-protocols", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child nodes of routing protocols
        validateRoutingProtocols(n.getValue());

        n = it.next();
        //container vpn attachment form grouping.
        validateDataNode(n.getValue(), "vpn-attachment", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child node of vpn attachments.
        validateVpnAttachments(n.getValue());
    }

    /**
     * Unit test for model id sites container and object of list site.
     */
    @Test
    public void modelObjSite() {
        setUp();
        data = new DefaultModelObjectData.Builder();
        data.addModelObject((ModelObject) l3VpnObject().sites().site().get(0));
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultL3VpnSvc.class)
                                .addChild(DefaultSites.class)
                                .build());
        rscData = treeBuilder.getResourceData(data.build());

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(3, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("l3vpn-svc", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        sid = keys.get(2).schemaId();
        assertThat("sites", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        //validate l3vpn-svc 's child nodes.
        //first validate vpn-services.

        node = dataNodes.get(0);
        //list site
        validateDataNode(node, "site", NAME_SPACE_SVC,
                         MULTI_INSTANCE_NODE, true, null);

        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        assertThat(2, is(childMap.size()));
        childMap = ((InnerNode) node).childNodes();

        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();

        //leaf site-id
        validateDataNode(n.getValue(), "site-id", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true, "first-site");

        //container site network access
        n = it.next();
        validateDataNode(n.getValue(), "site-network-accesses", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true, null);

        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(1, is(childMap.size()));
        it = childMap.entrySet().iterator();
        n = it.next();

        //list site network access.
        validateDataNode(n.getValue(), "site-network-access", NAME_SPACE_SVC,
                         MULTI_INSTANCE_NODE, true, null);

        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(6, is(childMap.size()));
        it = childMap.entrySet().iterator();
        n = it.next();

        //leaf site network access id.
        validateDataNode(n.getValue(), "site-network-access-id", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "first-site-network-access-id");

        n = it.next();
        //leaf site network access type.
        validateDataNode(n.getValue(), "site-network-access-type", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "site-network-access-type");

        n = it.next();
        //grouping container bearer with augments.
        validateDataNode(n.getValue(), "bearer", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child nodes of bearer.
        validateBearer(n.getValue());

        n = it.next();
        //container ip connection form grouping.
        validateDataNode(n.getValue(), "ip-connection", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate ip connections's child node.
        validateIpConnections(n.getValue());

        n = it.next();
        //container routing protocols form grouping.
        validateDataNode(n.getValue(), "routing-protocols", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child nodes of routing protocols
        validateRoutingProtocols(n.getValue());

        n = it.next();
        //container vpn attachment form grouping.
        validateDataNode(n.getValue(), "vpn-attachment", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child node of vpn attachments.
        validateVpnAttachments(n.getValue());
    }

    /**
     * Unit test for model id site and container object of list site network
     * access container.
     */
    @Test
    public void modelIdSite() {
        setUp();
        data = new DefaultModelObjectData.Builder();
        data.addModelObject((ModelObject) l3VpnObject().sites().site().get(0)
                .siteNetworkAccesses());
        SiteKeys siteKeys = new SiteKeys();
        siteKeys.siteId(SvcId.fromString("site-keys"));
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultL3VpnSvc.class)
                                .addChild(DefaultSites.class)
                                .addChild(DefaultSite.class, siteKeys)
                                .build());
        rscData = treeBuilder.getResourceData(data.build());

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(4, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("l3vpn-svc", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        sid = keys.get(2).schemaId();
        assertThat("sites", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        sid = keys.get(3).schemaId();
        assertThat("site", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        NodeKey key = keys.get(3);
        assertThat(true, is(key instanceof ListKey));

        ListKey listKey = (ListKey) key;
        List<KeyLeaf> keyLeaves = listKey.keyLeafs();
        assertThat(1, is(keyLeaves.size()));

        KeyLeaf keyLeaf = keyLeaves.get(0);
        sid = keyLeaf.leafSchema();
        assertThat("site-id", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        assertThat(false, is(keyLeaf.leafValue() instanceof SvcId));
        assertThat("site-keys", is(keyLeaf.leafValue()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        //validate l3vpn-svc 's child nodes.
        //first validate vpn-services.

        node = dataNodes.get(0);

        //container site network access
        validateDataNode(node, "site-network-accesses", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true, null);

        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        assertThat(1, is(childMap.size()));
        childMap = ((InnerNode) node).childNodes();

        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();

        //list site network access.
        validateDataNode(n.getValue(), "site-network-access", NAME_SPACE_SVC,
                         MULTI_INSTANCE_NODE, true, null);

        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(6, is(childMap.size()));
        it = childMap.entrySet().iterator();
        n = it.next();

        //leaf site network access id.
        validateDataNode(n.getValue(), "site-network-access-id", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "first-site-network-access-id");

        n = it.next();
        //leaf site network access type.
        validateDataNode(n.getValue(), "site-network-access-type", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "site-network-access-type");

        n = it.next();
        //grouping container bearer with augments.
        validateDataNode(n.getValue(), "bearer", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child nodes of bearer.
        validateBearer(n.getValue());

        n = it.next();
        //container ip connection form grouping.
        validateDataNode(n.getValue(), "ip-connection", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate ip connections's child node.
        validateIpConnections(n.getValue());

        n = it.next();
        //container routing protocols form grouping.
        validateDataNode(n.getValue(), "routing-protocols", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child nodes of routing protocols
        validateRoutingProtocols(n.getValue());

        n = it.next();
        //container vpn attachment form grouping.
        validateDataNode(n.getValue(), "vpn-attachment", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child node of vpn attachments.
        validateVpnAttachments(n.getValue());
    }

    /**
     * Unit test for model id site network accesses and object of site
     * network access list.
     */
    @Test
    public void modelIdSiteNetworkAccess() {
        setUp();
        data = new DefaultModelObjectData.Builder();
        data.addModelObject((ModelObject) l3VpnObject().sites().site().get(0)
                .siteNetworkAccesses().siteNetworkAccess().get(0));
        SiteKeys siteKeys = new SiteKeys();
        siteKeys.siteId(SvcId.fromString("site-keys"));
        data.identifier(ModelObjectId.builder()
                                .addChild(DefaultL3VpnSvc.class)
                                .addChild(DefaultSites.class)
                                .addChild(DefaultSite.class, siteKeys)
                                .addChild(DefaultSiteNetworkAccesses.class)
                                .build());
        rscData = treeBuilder.getResourceData(data.build());

        id = rscData.resourceId();
        keys = id.nodeKeys();
        assertThat(5, is(keys.size()));

        sid = keys.get(0).schemaId();
        assertThat("/", is(sid.name()));
        assertThat(null, is(sid.namespace()));

        sid = keys.get(1).schemaId();
        assertThat("l3vpn-svc", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        sid = keys.get(2).schemaId();
        assertThat("sites", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        sid = keys.get(3).schemaId();
        assertThat("site", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        NodeKey key = keys.get(3);
        assertThat(true, is(key instanceof ListKey));

        ListKey listKey = (ListKey) key;
        List<KeyLeaf> keyLeaves = listKey.keyLeafs();
        assertThat(1, is(keyLeaves.size()));

        KeyLeaf keyLeaf = keyLeaves.get(0);
        sid = keyLeaf.leafSchema();
        assertThat("site-id", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        assertThat(false, is(keyLeaf.leafValue() instanceof SvcId));
        assertThat("site-keys", is(keyLeaf.leafValue()));

        sid = keys.get(4).schemaId();
        assertThat("site-network-accesses", is(sid.name()));
        assertThat(NAME_SPACE_SVC, is(sid.namespace()));

        dataNodes = rscData.dataNodes();
        assertThat(1, is(dataNodes.size()));

        //validate l3vpn-svc 's child nodes.
        //first validate vpn-services.

        node = dataNodes.get(0);
        //list site network access.
        validateDataNode(node, "site-network-access", NAME_SPACE_SVC,
                         MULTI_INSTANCE_NODE, true, null);

        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        assertThat(6, is(childMap.size()));
        childMap = ((InnerNode) node).childNodes();

        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();

        //leaf site network access id.
        validateDataNode(n.getValue(), "site-network-access-id", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "first-site-network-access-id");

        n = it.next();
        //leaf site network access type.
        validateDataNode(n.getValue(), "site-network-access-type", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "site-network-access-type");

        n = it.next();
        //grouping container bearer with augments.
        validateDataNode(n.getValue(), "bearer", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child nodes of bearer.
        validateBearer(n.getValue());

        n = it.next();
        //container ip connection form grouping.
        validateDataNode(n.getValue(), "ip-connection", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate ip connections's child node.
        validateIpConnections(n.getValue());

        n = it.next();
        //container routing protocols form grouping.
        validateDataNode(n.getValue(), "routing-protocols", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child nodes of routing protocols
        validateRoutingProtocols(n.getValue());

        n = it.next();
        //container vpn attachment form grouping.
        validateDataNode(n.getValue(), "vpn-attachment", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //validate child node of vpn attachments.
        validateVpnAttachments(n.getValue());
    }


    //Validates bearer containers child nodes.
    private void validateBearer(DataNode node) {
        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        assertThat(3, is(childMap.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();

        //container attachment added from augment.
        validateDataNode(n.getValue(), "bearer-reference", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "first-ref");

        n = it.next();
        validateDataNode(n.getValue(), "bearer-attachment", NAME_SPACE_EXT,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //handle child nodes of bearer attachments.
        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(2, is(childMap.size()));
        Iterator<Map.Entry<NodeKey, DataNode>> it2 = childMap.entrySet()
                .iterator();
        Map.Entry<NodeKey, DataNode> n2 = it2.next();

        validateDataNode(n2.getValue(), "pe-name", NAME_SPACE_EXT,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "first-pe");

        n2 = it2.next();
        validateDataNode(n2.getValue(), "pe-mgmt-ip", NAME_SPACE_EXT,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "1.1.1.1");

        n = it.next();
        validateDataNode(n.getValue(), "requested-type", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(1, is(childMap.size()));
        it2 = childMap.entrySet().iterator();
        n2 = it2.next();
        validateDataNode(n2.getValue(), "requested-type-profile",
                         NAME_SPACE_EXT, SINGLE_INSTANCE_NODE, true,
                         null);

        childMap = ((InnerNode) n2.getValue()).childNodes();
        assertThat(2, is(childMap.size()));
        it2 = childMap.entrySet().iterator();
        n2 = it2.next();
        validateDataNode(n2.getValue(), "circuit-id",
                         NAME_SPACE_EXT, SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "first-circuit-id");

        n2 = it2.next();
        validateDataNode(n2.getValue(), "physical",
                         NAME_SPACE_EXT, SINGLE_INSTANCE_NODE, true,
                         null);

        childMap = ((InnerNode) n2.getValue()).childNodes();
        assertThat(1, is(childMap.size()));
        it2 = childMap.entrySet().iterator();
        n2 = it2.next();
        validateDataNode(n2.getValue(), "physical-if",
                         NAME_SPACE_EXT, SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "first-id");
    }

    //validates ip connections
    private void validateIpConnections(DataNode node) {
        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        assertThat(1, is(childMap.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();

        //container ipv4.
        validateDataNode(n.getValue(), "ipv4", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        //handle child nodes of bearer attachments.
        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(3, is(childMap.size()));
        Iterator<Map.Entry<NodeKey, DataNode>> it2 = childMap.entrySet()
                .iterator();
        Map.Entry<NodeKey, DataNode> n2 = it2.next();

        validateDataNode(n2.getValue(), "address-allocation-type", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "address-allocation-type");

        n2 = it2.next();
        validateDataNode(n2.getValue(), "number-of-dynamic-address", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "2");

        n2 = it2.next();
        validateDataNode(n2.getValue(), "addresses", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        childMap = ((InnerNode) n2.getValue()).childNodes();
        assertThat(1, is(childMap.size()));
        it2 = childMap.entrySet().iterator();
        n2 = it2.next();
        //child node of addresses
        validateDataNode(n2.getValue(), "customer-address", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "2.2.2.2");
    }

    //validate routing protocols
    private void validateRoutingProtocols(DataNode node) {
        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        assertThat(1, is(childMap.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();

        validateDataNode(n.getValue(), "routing-protocol", NAME_SPACE_SVC,
                         MULTI_INSTANCE_NODE, true,
                         null);

        //handle child nodes of bearer attachments.
        childMap = ((InnerNode) n.getValue()).childNodes();
        assertThat(2, is(childMap.size()));
        Iterator<Map.Entry<NodeKey, DataNode>> it2 = childMap.entrySet()
                .iterator();
        Map.Entry<NodeKey, DataNode> n2 = it2.next();

        validateDataNode(n2.getValue(), "type", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "routing-protocol-type");

        n2 = it2.next();
        validateDataNode(n2.getValue(), "bgp", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_NODE, true,
                         null);

        childMap = ((InnerNode) n2.getValue()).childNodes();
        assertThat(1, is(childMap.size()));
        it2 = childMap.entrySet().iterator();
        n2 = it2.next();
        //child node of bgp
        validateDataNode(n2.getValue(), "autonomous-system", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "120");
    }

    //validates vpn attachments.
    private void validateVpnAttachments(DataNode node) {
        Map<NodeKey, DataNode> childMap = ((InnerNode) node).childNodes();
        assertThat(1, is(childMap.size()));

        Iterator<Map.Entry<NodeKey, DataNode>> it = childMap.entrySet().iterator();
        Map.Entry<NodeKey, DataNode> n = it.next();

        validateDataNode(n.getValue(), "site-role", NAME_SPACE_SVC,
                         SINGLE_INSTANCE_LEAF_VALUE_NODE, true,
                         "site-role");
    }


    /**
     * Returns l3vpn object.
     *
     * @return l3vpn object
     */
    private DefaultL3VpnSvc l3VpnObject() {
        DefaultL3VpnSvc main = new DefaultL3VpnSvc();

        //Default l3vpn service
        DefaultVpnServices services = new DefaultVpnServices();

        //list vpnSvc

        DefaultVpnSvc svc = new DefaultVpnSvc();
        svc.vpnId(SvcId.fromString("first-id"));

        //svc added.
        services.addToVpnSvc(svc);

        //add vpn services to l3vpn svc.
        main.vpnServices(services);

        //Sites container
        DefaultSites sites = new DefaultSites();

        //site list
        DefaultSite site = new DefaultSite();
        site.siteId(SvcId.fromString("first-site"));

        //default network access container.
        DefaultSiteNetworkAccesses access = new
                DefaultSiteNetworkAccesses();

        DefaultSiteNetworkAccess listAccess = new DefaultSiteNetworkAccess();
        listAccess.siteNetworkAccessId(SvcId.fromString(
                "first-site-network-access-id"));

        //handle augments and uses

        DefaultBearer bearer = new DefaultBearer();

        //augment augmenting bearer.
        DefaultAugmentedL3VpnBearer augmentedL3VpnBearer = new
                DefaultAugmentedL3VpnBearer();

        //augments child bearer attachment
        DefaultBearerAttachment attachment = new DefaultBearerAttachment();
        attachment.peMgmtIp(Ipv4Address.fromString("1.1.1.1"));
        attachment.peName("first-pe");
        augmentedL3VpnBearer.bearerAttachment(attachment);

        //adding bearer augment
        bearer.addAugmentation(augmentedL3VpnBearer);
        bearer.bearerReference("first-ref");

        DefaultRequestedType requestedType = new DefaultRequestedType();
        //add augment for request type.

        DefaultAugmentedL3VpnRequestedType augmentedL3VpnRequestedType =
                new DefaultAugmentedL3VpnRequestedType();
        DefaultRequestedTypeProfile profile = new DefaultRequestedTypeProfile();
        profile.circuitId("first-circuit-id");

        //Added default case in augments child container.
        DefaultPhysicalCase physicalCase = new DefaultPhysicalCase();
        DefaultPhysical physical = new DefaultPhysical();
        physical.physicalIf("first-id");

        //add container in case.
        physicalCase.physical(physical);

        //added choice in container profile
        profile.requestedTypeChoice(physicalCase);
        //added in augment.
        augmentedL3VpnRequestedType.requestedTypeProfile(profile);

        requestedType.addAugmentation(augmentedL3VpnRequestedType);

        //added in bearer with augmented request type
        bearer.requestedType(requestedType);

        //added bearer in access list.
        listAccess.bearer(bearer);

        //set site network access type.
        listAccess.siteNetworkAccessType(SiteNetworkAccessType.fromString(
                "site-network-access-type"));

        DefaultIpConnection ipConnection = new DefaultIpConnection();

        //added ipv4 in ip connection
        DefaultIpv4 ipv4 = new DefaultIpv4();
        ipv4.numberOfDynamicAddress((short) 2);
        ipv4.addressAllocationType(AddressAllocationType.fromString(
                "address-allocation-type"));
        DefaultAddresses address = new DefaultAddresses();
        address.customerAddress(Ipv4Address.fromString("2.2.2.2"));
        ipv4.addresses(address);
        ipConnection.ipv4(ipv4);

        //default routing protocols
        DefaultRoutingProtocols protocols = new DefaultRoutingProtocols();
        DefaultRoutingProtocol protocol = new DefaultRoutingProtocol();
        protocol.type(RoutingProtocolType.fromString(
                "routing-protocol-type"));
        DefaultBgp bgp = new DefaultBgp();
        bgp.autonomousSystem(120);
        protocol.bgp(bgp);
        protocols.addToRoutingProtocol(protocol);

        //add uses in access list.
        listAccess.ipConnection(ipConnection);
        listAccess.routingProtocols(protocols);

        DefaultVpnAttachment defaultVpnAttachment = new DefaultVpnAttachment();

        DefaultVpnId vpnId = new DefaultVpnId();
        vpnId.siteRole(SiteRole.fromString("site-role"));
        defaultVpnAttachment.attachmentFlavor(vpnId);

        listAccess.vpnAttachment(defaultVpnAttachment);

        //add list to container.
        access.addToSiteNetworkAccess(listAccess);

        //add access to site.
        site.siteNetworkAccesses(access);

        //add site to sites container.
        sites.addToSite(site);

        //add sites to l3vpnSvc
        main.sites(sites);
        return main;
    }
}
