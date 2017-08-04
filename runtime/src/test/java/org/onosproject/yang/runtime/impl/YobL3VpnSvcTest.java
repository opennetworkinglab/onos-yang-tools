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
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.DefaultL3VpnSvc;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.SvcId;
import org.onosproject.yang.gen.v1.ietfl3vpnsvc.rev20160730.ietfl3vpnsvc.accessvpnpolicy.vpnattachment.attachmentflavor.VpnId;
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
import org.onosproject.yang.gen.v1.l3vpnsvcext.rev20160730.l3vpnsvcext.l3vpnsvc.sites.site.sitenetworkaccesses.sitenetworkaccess.bearer.DefaultAugmentedL3VpnBearer;
import org.onosproject.yang.gen.v1.l3vpnsvcext.rev20160730.l3vpnsvcext.l3vpnsvc.sites.site.sitenetworkaccesses.sitenetworkaccess.bearer.requestedtype.DefaultAugmentedL3VpnRequestedType;
import org.onosproject.yang.gen.v1.l3vpnsvcext.rev20160730.l3vpnsvcext.requestedtypegrouping.requestedtypeprofile.requestedtypechoice.PhysicalCase;
import org.onosproject.yang.gen.v1.l3vpnsvcext.rev20160730.l3vpnsvcext.requestedtypegrouping.requestedtypeprofile.requestedtypechoice.physicalcase.Physical;
import org.onosproject.yang.model.AtomicPath;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.KeyInfo;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.MultiInstanceNode;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SingleInstanceNode;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.model.DataNode.Type.MULTI_INSTANCE_NODE;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.addToResourceId;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeResourceId;

/**
 * Tests the YANG object building for the L3 VPN YANG data nodes.
 */
public class YobL3VpnSvcTest {

    private static final String L3VPN_SVC_NS =
            "urn:ietf:params:xml:ns:yang:ietf-l3vpn-svc";
    private static final String L3VPN_SVC_EXT_NS =
            "urn:ietf:params:xml:ns:yang:l3vpn:svc:ext";
    TestYangSerializerContext context = new TestYangSerializerContext();
    private DataNode.Builder dBlr;
    private String value = null;
    private ResourceId.Builder rIdBlr;

    private void buildDnForSiteRouting() {
        dBlr = addDataNode(dBlr, "routing-protocols", null, value, null);
        dBlr = addDataNode(dBlr, "routing-protocol", null, value, null);
        dBlr = addDataNode(dBlr, "type", null, "ospf", null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
    }

    private void buildDnForSiteTopLevelCfg() {
        buildDnForSiteRouting();         // uses site-routing
    }

    private void buildDnForSiteNetworkAccessTopLevelCfg() {
        dBlr = addDataNode(dBlr, "site-network-access-type", null, "point-to-point", null);
        dBlr = exitDataNode(dBlr);
        buildDnForSiteAttachmentBearer(); // site-attachment-bearer
        buildDnForSiteAttachmentIpConnection(); // site-attachment-ip-connection
        buildDnForSiteRouting(); // site-routing
        buildDnForAccessVpnPolicy(); // access-vpn-policy
    }

    private void buildDnForSiteAttachmentBearer() {
        dBlr = addDataNode(dBlr, "bearer", null, value, null);
        dBlr = addDataNode(dBlr, "bearer-attachment", L3VPN_SVC_EXT_NS, value, null);
        dBlr = addDataNode(dBlr, "pe-name", null, "pe-name", null);
        dBlr = exitDataNode(dBlr);
        dBlr = addDataNode(dBlr, "pe-mgmt-ip", null, "192.1.1.1", null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // bearer-attachment
        dBlr = addDataNode(dBlr, "requested-type", L3VPN_SVC_NS, value, null);
        dBlr = addDataNode(dBlr, "requested-type-profile", L3VPN_SVC_EXT_NS, value, null);
        dBlr = addDataNode(dBlr, "physical", null, value, null);
        dBlr = addDataNode(dBlr, "physical-if", null, "eth0/0/0", null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // physical
        dBlr = addDataNode(dBlr, "circuit-id", null, "circuit-id", null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // requested-type-profile
        dBlr = exitDataNode(dBlr); // requested-type
        dBlr = exitDataNode(dBlr); // bearer
    }

    private void buildDnForSiteAttachmentIpConnection() {
        dBlr = addDataNode(dBlr, "ip-connection", L3VPN_SVC_NS, value, null);
        dBlr = addDataNode(dBlr, "ipv4", null, value, null);
        dBlr = addDataNode(dBlr, "addresses", null, value, null);
        dBlr = addDataNode(dBlr, "provider-address", null, "192.12.1.1", null);
        dBlr = exitDataNode(dBlr);
        dBlr = addDataNode(dBlr, "mask", null, "24", null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // addresses
        dBlr = exitDataNode(dBlr); // ipv4
        dBlr = addDataNode(dBlr, "ipv6", null, value, null);
        dBlr = addDataNode(dBlr, "addresses", null, value, null);
        dBlr = addDataNode(dBlr, "provider-address", null, "0:0:0:0:0:0:0:0", null);
        dBlr = exitDataNode(dBlr);
        dBlr = addDataNode(dBlr, "mask", null, "32", null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr); // addresses
        dBlr = exitDataNode(dBlr); // ipv6
        dBlr = exitDataNode(dBlr); // ip-connection
    }

    private void buildDnForAccessVpnPolicy() {
        dBlr = addDataNode(dBlr, "vpn-attachment", null, value, null);
        dBlr = addDataNode(dBlr, "vpn-id", null, "10", null);
        dBlr = exitDataNode(dBlr);
        dBlr = addDataNode(dBlr, "site-role", null, "hub-role", null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
    }

    private ResourceId.Builder buildResourceIdForL3VpnSvc() {
        rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "l3vpn-svc", L3VPN_SVC_NS, value);
        return rIdBlr;
    }

    private ResourceId.Builder buildResourceIdForSites() {
        rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "l3vpn-svc", L3VPN_SVC_NS, value);
        rIdBlr = addToResourceId(rIdBlr, "sites", L3VPN_SVC_NS, value);
        return rIdBlr;
    }

    private ResourceId.Builder buildResourceIdForSnas() {
        rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "l3vpn-svc", L3VPN_SVC_NS, value);
        rIdBlr = addToResourceId(rIdBlr, "sites", L3VPN_SVC_NS, value);
        rIdBlr = addToResourceId(rIdBlr, "site", L3VPN_SVC_NS, value);
        rIdBlr = addToResourceId(rIdBlr, "site-id", L3VPN_SVC_NS, "10");
        rIdBlr = addToResourceId(rIdBlr, "site-network-accesses",
                                 L3VPN_SVC_NS, value);
        return rIdBlr;
    }

    private ResourceId.Builder buildResourceIdForSite() {
        rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "l3vpn-svc", L3VPN_SVC_NS, value);
        rIdBlr = addToResourceId(rIdBlr, "sites", L3VPN_SVC_NS, value);
        rIdBlr = addToResourceId(rIdBlr, "site", L3VPN_SVC_NS, value);
        rIdBlr = addToResourceId(rIdBlr, "site-id", L3VPN_SVC_NS, "10");
        return rIdBlr;
    }

    private DataNode buildVpnServicesDataNode() {
        ResourceId.Builder builder = buildResourceIdForL3VpnSvc();
        dBlr = initializeDataNode(builder);
        dBlr = addDataNode(dBlr, "vpn-services", L3VPN_SVC_NS, value, null);
        dBlr = addDataNode(dBlr, "vpn-svc", L3VPN_SVC_NS, value, null);
        dBlr = addDataNode(dBlr, "vpn-id", L3VPN_SVC_NS, "10", null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        return dBlr.build();
    }

    private DataNode buildSitesDataNode() {
        ResourceId.Builder builder = buildResourceIdForL3VpnSvc();
        dBlr = initializeDataNode(builder);
        dBlr = addDataNode(dBlr, "sites", L3VPN_SVC_NS, value, null);
        dBlr = addDataNode(dBlr, "site", null, value, null);
        dBlr = addDataNode(dBlr, "site-id", null, "100", null);
        dBlr = exitDataNode(dBlr);
        buildDnForSiteTopLevelCfg(); //site-top-level-cfg

        dBlr = addDataNode(dBlr, "site-network-accesses", null, value, null);
        dBlr = addDataNode(dBlr, "site-network-access", null, value, null);
        dBlr = addDataNode(dBlr, "site-network-access-id", null, "1000", null);
        dBlr = exitDataNode(dBlr);

        buildDnForSiteNetworkAccessTopLevelCfg(); // site-network-access-top-level-cfg

        dBlr = exitDataNode(dBlr); // end site-network-access
        dBlr = exitDataNode(dBlr); // end site-network-accesses
        dBlr = exitDataNode(dBlr); // end site
        return dBlr.build();
    }

    private DataNode buildSiteDataNode(String siteId, String sncId) {
        ResourceId.Builder builder = buildResourceIdForSites();
        dBlr = initializeDataNode(builder);
        dBlr = addDataNode(dBlr, "site", null, value, null);
        dBlr = addDataNode(dBlr, "site-id", null, siteId, null);
        dBlr = exitDataNode(dBlr);

        buildDnForSiteTopLevelCfg(); // site-top-level-cfg

        value = null;
        dBlr = addDataNode(dBlr, "site-network-accesses", null, value, null);
        dBlr = addDataNode(dBlr, "site-network-access", null, value, null);
        dBlr = addDataNode(dBlr, "site-network-access-id", null, sncId, null);
        dBlr = exitDataNode(dBlr);

        buildDnForSiteNetworkAccessTopLevelCfg();  // site-network-access-top-level-cfg

        dBlr = exitDataNode(dBlr); // end site-network-access
        dBlr = exitDataNode(dBlr); // end site-network-accesses
        return dBlr.build();
    }

    private DataNode buildDnForSiteNetworkAccesses() {
        ResourceId.Builder builder = buildResourceIdForSite();
        dBlr = initializeDataNode(builder);
        dBlr = addDataNode(dBlr, "site-network-accesses", null, value, null);
        dBlr = addDataNode(dBlr, "site-network-access", null, value, null);
        dBlr = addDataNode(dBlr, "site-network-access-id", null, "10", null);
        dBlr = exitDataNode(dBlr);

        buildDnForSiteNetworkAccessTopLevelCfg(); //site-network-access-top-level-cfg

        dBlr = exitDataNode(dBlr); // end site-network-access
        return dBlr.build();
    }

    /**
     * Tests creation of vpn-services and sites with resource id l3vpn-svc.
     */
    @Test
    public void createVpnServices() {
        DataNode dataNode = buildVpnServicesDataNode();
        DataNode sitesDn = buildSitesDataNode();
        ResourceData data = DefaultResourceData.builder()
                .addDataNode(dataNode).addDataNode(sitesDn)
                .resourceId(buildResourceIdForL3VpnSvc().build()).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        ModelObjectId id = modelObjectData.identifier();
        assertThat(id.atomicPaths().size(), is(1));
        AtomicPath path = id.atomicPaths().get(0);
        assertThat(path.type(), is(SINGLE_INSTANCE_NODE));
        assertThat(((SingleInstanceNode) path).container().getSimpleName(),
                   is("DefaultL3VpnSvc"));
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        DefaultVpnServices vpnServices = ((DefaultVpnServices) modelObjectList
                .get(0));
        assertThat(vpnServices.vpnSvc().get(0).vpnId().string(), is("10"));
        DefaultSites sites = ((DefaultSites) modelObjectList.get(1));
        DefaultSite site = ((DefaultSite) sites.site().get(0));
        assertThat(site.siteId().string(), is("100"));
        assertThat(site.routingProtocols().routingProtocol().get(0).type()
                           .getSimpleName(), is("Ospf"));

        DefaultSiteNetworkAccess sna = ((DefaultSiteNetworkAccess) site
                .siteNetworkAccesses().siteNetworkAccess().get(0));
        assertThat(sna.siteNetworkAccessId().string(), is("1000"));
        DefaultBearer bearer = ((DefaultBearer) sna.bearer());
        DefaultAugmentedL3VpnBearer bearerAttach = bearer
                .augmentation(DefaultAugmentedL3VpnBearer.class);
        assertThat(bearerAttach.bearerAttachment().peMgmtIp().string(),
                   is("192.1.1.1"));
        assertThat(bearerAttach.bearerAttachment().peName(), is("pe-name"));
        DefaultAugmentedL3VpnRequestedType reqType =
                ((DefaultRequestedType) bearer.requestedType())
                        .augmentation(DefaultAugmentedL3VpnRequestedType.class);
        Physical py = ((PhysicalCase) reqType.requestedTypeProfile()
                .requestedTypeChoice()).physical();
        assertThat(py.physicalIf(), is("eth0/0/0"));
        assertThat(reqType.requestedTypeProfile().circuitId(), is("circuit-id"));

        DefaultIpConnection ipConnection = ((DefaultIpConnection) sna.ipConnection());
        assertThat(ipConnection.ipv4().addresses().providerAddress().string(),
                   is("192.12.1.1"));
        Short mask = 24;
        assertThat(ipConnection.ipv4().addresses().mask(), is(mask));
        assertThat(ipConnection.ipv6().addresses().providerAddress()
                           .string(), is("0:0:0:0:0:0:0:0"));
        mask = 32;
        assertThat(ipConnection.ipv6().addresses().mask(), is(mask));
        assertThat(sna.routingProtocols().routingProtocol().get(0).type()
                           .getSimpleName(), is("Ospf"));
        SvcId sId = (SvcId) ((VpnId) sna.vpnAttachment().attachmentFlavor())
                .vpnId();
        assertThat(sId.string(), is("10"));
        assertThat(((VpnId) sna.vpnAttachment().attachmentFlavor()).siteRole()
                           .getSimpleName(), is("HubRole"));
    }

    /**
     * Tests creation of multiple instance of vpn-services with resource id
     * vpn-services.
     */
    @Test
    public void createListVpnService() {
        rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "l3vpn-svc", L3VPN_SVC_NS, value);
        rIdBlr = addToResourceId(rIdBlr, "vpn-services", L3VPN_SVC_NS, value);
        dBlr = initializeDataNode(rIdBlr);
        dBlr = addDataNode(dBlr, "vpn-svc", L3VPN_SVC_NS, value, null);
        dBlr = addDataNode(dBlr, "vpn-id", L3VPN_SVC_NS, "10", null);
        dBlr = exitDataNode(dBlr);
        ResourceData.Builder dataBuilder = DefaultResourceData.builder()
                .addDataNode(dBlr.build()).resourceId(rIdBlr.build());
        dBlr = initializeDataNode(rIdBlr);
        dBlr = addDataNode(dBlr, "vpn-svc", L3VPN_SVC_NS, value, null);
        dBlr = addDataNode(dBlr, "vpn-id", L3VPN_SVC_NS, "20", null);
        dBlr = exitDataNode(dBlr);
        dataBuilder.addDataNode(dBlr.build());

        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(dataBuilder.build());
        ModelObjectId id = modelObjectData.identifier();
        assertThat(id.atomicPaths().size(), is(2));
        AtomicPath path = id.atomicPaths().get(0);
        assertThat(path.type(), is(SINGLE_INSTANCE_NODE));
        assertThat(((SingleInstanceNode) path).container().getSimpleName(),
                   is("DefaultL3VpnSvc"));
        path = id.atomicPaths().get(1);
        assertThat(path.type(), is(SINGLE_INSTANCE_NODE));
        assertThat(((SingleInstanceNode) path).container().getSimpleName(),
                   is("DefaultVpnServices"));
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        DefaultVpnSvc vpnSvc = ((DefaultVpnSvc) modelObjectList.get(0));
        assertThat(vpnSvc.vpnId().string(), is("10"));
        DefaultVpnSvc vpnSvc1 = ((DefaultVpnSvc) modelObjectList.get(1));
        assertThat(vpnSvc1.vpnId().string(), is("20"));
    }

    /**
     * Tests creation of multiple instance of site with resource id
     * vpn-services.
     */
    @Test
    public void createListSite() {
        DataNode siteDn1 = buildSiteDataNode("10", "100");
        DataNode siteDn2 = buildSiteDataNode("20", "200");
        ResourceData data = DefaultResourceData.builder()
                .addDataNode(siteDn1).addDataNode(siteDn2)
                .resourceId(buildResourceIdForSites().build()).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        ModelObjectId id = modelObjectData.identifier();
        assertThat(id.atomicPaths().size(), is(2));
        AtomicPath path = id.atomicPaths().get(0);
        assertThat(path.type(), is(SINGLE_INSTANCE_NODE));
        assertThat(((SingleInstanceNode) path).container().getSimpleName(),
                   is("DefaultL3VpnSvc"));
        path = id.atomicPaths().get(1);
        assertThat(path.type(), is(SINGLE_INSTANCE_NODE));
        assertThat(((SingleInstanceNode) path).container().getSimpleName(),
                   is("DefaultSites"));
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        DefaultSite site1 = ((DefaultSite) modelObjectList.get(0));
        assertThat(site1.siteId().string(), is("10"));
        assertThat(site1.routingProtocols().routingProtocol().get(0).type()
                           .getSimpleName(), is("Ospf"));

        DefaultSiteNetworkAccess sna = ((DefaultSiteNetworkAccess) site1
                .siteNetworkAccesses().siteNetworkAccess().get(0));
        assertThat(sna.siteNetworkAccessId().string(), is("100"));
        DefaultBearer bearer = ((DefaultBearer) sna.bearer());
        DefaultAugmentedL3VpnBearer bearerAttach = bearer
                .augmentation(DefaultAugmentedL3VpnBearer.class);
        assertThat(bearerAttach.bearerAttachment().peMgmtIp().string(),
                   is("192.1.1.1"));
        assertThat(bearerAttach.bearerAttachment().peName(), is("pe-name"));
        DefaultAugmentedL3VpnRequestedType reqType =
                ((DefaultRequestedType) bearer.requestedType())
                        .augmentation(DefaultAugmentedL3VpnRequestedType.class);
        Physical py = ((PhysicalCase) reqType.requestedTypeProfile()
                .requestedTypeChoice()).physical();
        assertThat(py.physicalIf(), is("eth0/0/0"));
        assertThat(reqType.requestedTypeProfile().circuitId(), is("circuit-id"));

        DefaultIpConnection ipConnection = ((DefaultIpConnection) sna
                .ipConnection());
        assertThat(ipConnection.ipv4().addresses().providerAddress().string(),
                   is("192.12.1.1"));
        Short mask = 24;
        assertThat(ipConnection.ipv4().addresses().mask(), is(mask));
        assertThat(ipConnection.ipv6().addresses().providerAddress()
                           .string(), is("0:0:0:0:0:0:0:0"));
        mask = 32;
        assertThat(ipConnection.ipv6().addresses().mask(), is(mask));
        assertThat(sna.routingProtocols().routingProtocol().get(0).type()
                           .getSimpleName(), is("Ospf"));
        SvcId sId = (SvcId) ((VpnId) sna.vpnAttachment().attachmentFlavor())
                .vpnId();
        assertThat(sId.string(), is("10"));
        assertThat(((VpnId) sna.vpnAttachment().attachmentFlavor()).siteRole()
                           .getSimpleName(), is("HubRole"));
        DefaultSite site2 = ((DefaultSite) modelObjectList.get(1));
        assertThat(site2.siteId().string(), is("20"));
        assertThat(site2.routingProtocols().routingProtocol().get(0).type()
                           .getSimpleName(), is("Ospf"));
        sna = ((DefaultSiteNetworkAccess) site2.siteNetworkAccesses()
                .siteNetworkAccess().get(0));
        assertThat(sna.siteNetworkAccessId().string(), is("200"));
        ipConnection = ((DefaultIpConnection) sna.ipConnection());
        assertThat(ipConnection.ipv4().addresses().providerAddress().string(),
                   is("192.12.1.1"));
        mask = 24;
        assertThat(ipConnection.ipv4().addresses().mask(), is(mask));
        assertThat(ipConnection.ipv6().addresses().providerAddress()
                           .string(), is("0:0:0:0:0:0:0:0"));
        mask = 32;
        assertThat(ipConnection.ipv6().addresses().mask(), is(mask));
        assertThat(sna.routingProtocols().routingProtocol().get(0).type()
                           .getSimpleName(), is("Ospf"));
        sId = (SvcId) ((VpnId) sna.vpnAttachment().attachmentFlavor())
                .vpnId();
        assertThat(sId.string(), is("10"));
        assertThat(((VpnId) sna.vpnAttachment().attachmentFlavor()).siteRole()
                           .getSimpleName(), is("HubRole"));
    }

    /**
     * Tests creation of site-network-accesses with resource id
     * site.
     */
    @Test
    public void createSiteNetworkAccesses() {
        ResourceId.Builder rIdBuilder = buildResourceIdForSnas(); // site-network-accesses
        dBlr = initializeDataNode(rIdBuilder);
        dBlr = addDataNode(dBlr, "site-network-access", null, value, null);
        dBlr = addDataNode(dBlr, "site-network-access-id", null, "1000", null);
        dBlr = exitDataNode(dBlr);

        buildDnForSiteNetworkAccessTopLevelCfg(); // site-network-access-top-level-cfg
        ResourceData.Builder dataBdlr = DefaultResourceData.builder()
                .addDataNode(dBlr.build())
                .resourceId(buildResourceIdForSnas().build());
        dBlr = initializeDataNode(rIdBuilder);
        value = null;
        dBlr = addDataNode(dBlr, "site-network-access", null, value, null);
        dBlr = addDataNode(dBlr, "site-network-access-id", null, "2000", null);
        dBlr = exitDataNode(dBlr);
        buildDnForSiteNetworkAccessTopLevelCfg();
        dataBdlr.addDataNode(dBlr.build());
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(dataBdlr.build());
        ModelObjectId id = modelObjectData.identifier();
        assertThat(id.atomicPaths().size(), is(4));
        AtomicPath path = id.atomicPaths().get(0);
        assertThat(path.type(), is(SINGLE_INSTANCE_NODE));
        assertThat(((SingleInstanceNode) path).container().getSimpleName(),
                   is("DefaultL3VpnSvc"));
        path = id.atomicPaths().get(1);
        assertThat(path.type(), is(SINGLE_INSTANCE_NODE));
        assertThat(((SingleInstanceNode) path).container().getSimpleName(),
                   is("DefaultSites"));
        path = id.atomicPaths().get(2);
        assertThat(path.type(), is(MULTI_INSTANCE_NODE));
        assertThat(((MultiInstanceNode) path).listClass().getSimpleName(),
                   is("DefaultSite"));
        KeyInfo obj = ((MultiInstanceNode) path).key();
        assertThat(((SiteKeys) obj).siteId().string(), is("10"));
        path = id.atomicPaths().get(3);
        assertThat(path.type(), is(SINGLE_INSTANCE_NODE));
        assertThat(((SingleInstanceNode) path).container().getSimpleName(),
                   is("DefaultSiteNetworkAccesses"));

        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        DefaultSiteNetworkAccess sna1 = ((DefaultSiteNetworkAccess)
                modelObjectList.get(0));
        assertThat(sna1.siteNetworkAccessId().string(), is("1000"));
        DefaultBearer bearer = ((DefaultBearer) sna1.bearer());
        DefaultAugmentedL3VpnBearer bearerAttach = bearer
                .augmentation(DefaultAugmentedL3VpnBearer.class);
        assertThat(bearerAttach.bearerAttachment().peMgmtIp().string(),
                   is("192.1.1.1"));
        assertThat(bearerAttach.bearerAttachment().peName(), is("pe-name"));
        DefaultAugmentedL3VpnRequestedType reqType =
                ((DefaultRequestedType) bearer.requestedType())
                        .augmentation(DefaultAugmentedL3VpnRequestedType.class);
        Physical py = ((PhysicalCase) reqType.requestedTypeProfile()
                .requestedTypeChoice()).physical();
        assertThat(py.physicalIf(), is("eth0/0/0"));
        assertThat(reqType.requestedTypeProfile().circuitId(), is("circuit-id"));
        DefaultIpConnection ipConnection = ((DefaultIpConnection) sna1.ipConnection());
        assertThat(ipConnection.ipv4().addresses().providerAddress().string(),
                   is("192.12.1.1"));
        Short mask = 24;
        assertThat(ipConnection.ipv4().addresses().mask(), is(mask));
        assertThat(ipConnection.ipv6().addresses().providerAddress()
                           .string(), is("0:0:0:0:0:0:0:0"));
        mask = 32;
        assertThat(ipConnection.ipv6().addresses().mask(), is(mask));
        assertThat(sna1.routingProtocols().routingProtocol().get(0).type()
                           .getSimpleName(), is("Ospf"));
        SvcId sId = (SvcId) ((VpnId) sna1.vpnAttachment().attachmentFlavor())
                .vpnId();
        assertThat(sId.string(), is("10"));
        assertThat(((VpnId) sna1.vpnAttachment().attachmentFlavor()).siteRole()
                           .getSimpleName(), is("HubRole"));

        DefaultSiteNetworkAccess sna2 = ((DefaultSiteNetworkAccess)
                modelObjectList.get(1));
        assertThat(sna2.siteNetworkAccessId().string(), is("2000"));
        bearer = ((DefaultBearer) sna2.bearer());
        bearerAttach = bearer.augmentation(DefaultAugmentedL3VpnBearer.class);
        assertThat(bearerAttach.bearerAttachment().peMgmtIp().string(), is("192.1.1.1"));
        assertThat(bearerAttach.bearerAttachment().peName(), is("pe-name"));
        reqType = bearer.requestedType().augmentation(
                DefaultAugmentedL3VpnRequestedType.class);
        py = ((PhysicalCase) reqType.requestedTypeProfile()
                .requestedTypeChoice()).physical();
        assertThat(py.physicalIf(), is("eth0/0/0"));
        assertThat(reqType.requestedTypeProfile().circuitId(), is("circuit-id"));
        ipConnection = ((DefaultIpConnection) sna2.ipConnection());
        assertThat(ipConnection.ipv4().addresses().providerAddress().string(),
                   is("192.12.1.1"));
        mask = 24;
        assertThat(ipConnection.ipv4().addresses().mask(), is(mask));
        assertThat(ipConnection.ipv6().addresses().providerAddress()
                           .string(), is("0:0:0:0:0:0:0:0"));
        mask = 32;
        assertThat(ipConnection.ipv6().addresses().mask(), is(mask));
        assertThat(sna2.routingProtocols().routingProtocol().get(0).type()
                           .getSimpleName(), is("Ospf"));
        sId = (SvcId) ((VpnId) sna2.vpnAttachment().attachmentFlavor())
                .vpnId();
        assertThat(sId.string(), is("10"));
        assertThat(((VpnId) sna2.vpnAttachment().attachmentFlavor()).siteRole()
                           .getSimpleName(), is("HubRole"));
    }

    /**
     * Tests creation of site-network-access with resource id
     * site-network-accesses.
     */
    @Test
    public void createSiteNetworkAccess() {
        ResourceData data = DefaultResourceData.builder()
                .addDataNode(buildDnForSiteNetworkAccesses())
                .resourceId(buildResourceIdForSite().build()).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        ModelObjectId id = modelObjectData.identifier();
        assertThat(id.atomicPaths().size(), is(3));
        AtomicPath path = id.atomicPaths().get(0);
        assertThat(path.type(), is(SINGLE_INSTANCE_NODE));
        assertThat(((SingleInstanceNode) path).container().getSimpleName(),
                   is("DefaultL3VpnSvc"));
        path = id.atomicPaths().get(1);
        assertThat(path.type(), is(SINGLE_INSTANCE_NODE));
        assertThat(((SingleInstanceNode) path).container().getSimpleName(),
                   is("DefaultSites"));
        path = id.atomicPaths().get(2);
        assertThat(path.type(), is(MULTI_INSTANCE_NODE));
        assertThat(((MultiInstanceNode) path).listClass().getSimpleName(),
                   is("DefaultSite"));
        KeyInfo obj = ((MultiInstanceNode) path).key();
        assertThat(((SiteKeys) obj).siteId().string(), is("10"));
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        DefaultSiteNetworkAccesses snas = ((DefaultSiteNetworkAccesses)
                modelObjectList.get(0));
        DefaultSiteNetworkAccess sna = ((DefaultSiteNetworkAccess) snas
                .siteNetworkAccess().get(0));
        assertThat(sna.siteNetworkAccessId().string(), is("10"));
        DefaultBearer bearer = ((DefaultBearer) sna.bearer());
        DefaultAugmentedL3VpnBearer bearerAttach = bearer
                .augmentation(DefaultAugmentedL3VpnBearer.class);
        assertThat(bearerAttach.bearerAttachment().peMgmtIp().string(),
                   is("192.1.1.1"));
        assertThat(bearerAttach.bearerAttachment().peName(), is("pe-name"));
        DefaultAugmentedL3VpnRequestedType reqType =
                ((DefaultRequestedType) bearer.requestedType())
                        .augmentation(DefaultAugmentedL3VpnRequestedType.class);
        Physical py = ((PhysicalCase) reqType.requestedTypeProfile()
                .requestedTypeChoice()).physical();
        assertThat(py.physicalIf(), is("eth0/0/0"));
        assertThat(reqType.requestedTypeProfile().circuitId(), is("circuit-id"));

        DefaultIpConnection ipConnection = ((DefaultIpConnection) sna
                .ipConnection());
        assertThat(ipConnection.ipv4().addresses().providerAddress().string(),
                   is("192.12.1.1"));
        Short mask = 24;
        assertThat(ipConnection.ipv4().addresses().mask(), is(mask));
        assertThat(ipConnection.ipv6().addresses().providerAddress()
                           .string(), is("0:0:0:0:0:0:0:0"));
        mask = 32;
        assertThat(ipConnection.ipv6().addresses().mask(), is(mask));
        assertThat(sna.routingProtocols().routingProtocol().get(0).type()
                           .getSimpleName(), is("Ospf"));
        SvcId sId = (SvcId) ((VpnId) sna.vpnAttachment().attachmentFlavor())
                .vpnId();
        assertThat(sId.string(), is("10"));
        assertThat(((VpnId) sna.vpnAttachment().attachmentFlavor()).siteRole()
                           .getSimpleName(), is("HubRole"));
    }

    /**
     * Tests creation of l3vpn-svc with resource id as "/".
     */
    @Test
    public void deleteL3VpnService() {
        dBlr = initializeDataNode(context);
        dBlr = addDataNode(dBlr, "l3vpn-svc", L3VPN_SVC_NS, value, null);
        ResourceData data = DefaultResourceData.builder()
                .addDataNode(dBlr.build()).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        DefaultL3VpnSvc l3VpnSvc = ((DefaultL3VpnSvc) modelObjectList.get(0));
    }
}