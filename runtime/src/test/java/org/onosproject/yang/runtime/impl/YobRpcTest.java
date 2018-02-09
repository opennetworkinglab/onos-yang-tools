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
import org.onosproject.yang.gen.v1.exampleops.rev20160707.exampleops.reboot.DefaultRebootInput;
import org.onosproject.yang.gen.v1.exampleops.rev20160707.exampleops.reboot.DefaultRebootOutput;
import org.onosproject.yang.gen.v1.ymsiptopology.rev20140101.ymsiptopology.reboot.input.DefaultAugmentedInput;
import org.onosproject.yang.gen.v1.ymsiptopology.rev20140101.ymsiptopology.reboot.output.DefaultAugmentedOutput;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.addToResourceId;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeResourceId;

/**
 * Tests the YANG object building for the YANG RPC input and output.
 */
public class YobRpcTest {
    private static final String EXAMPLE_NS = "https://example.com/ns/example-ops";
    private static final String IPTOPO_NS = "urn:ip:topo";
    private TestYangSerializerContext context = new TestYangSerializerContext();
    private DataNode.Builder dBlr;
    private String value;
    private ResourceId.Builder rIdBlr;

    private DataNode buildDataNodeForRpcInputNode() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "reboot", EXAMPLE_NS, value, null);
        dBlr = addDataNode(dBlr, "input", EXAMPLE_NS, value, null);

        value = "str1";
        dBlr = addDataNode(dBlr, "delay", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str2";
        dBlr = addDataNode(dBlr, "message", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str3";
        dBlr = addDataNode(dBlr, "language", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

        dBlr = exitDataNode(dBlr);
        return dBlr.build();
    }

    private ResourceData buildDataNodeForInputNode() {
        value = null;
        rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "reboot",
                                 EXAMPLE_NS, value);
        dBlr = initializeDataNode(rIdBlr);
        dBlr = addDataNode(dBlr, "input", EXAMPLE_NS, value, null);

        value = "str1";
        dBlr = addDataNode(dBlr, "delay", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str2";
        dBlr = addDataNode(dBlr, "message", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str3";
        dBlr = addDataNode(dBlr, "language", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);
        return DefaultResourceData.builder()
                .resourceId(rIdBlr.build()).addDataNode(dBlr.build()).build();
    }

    private ResourceData buildDataNodeForOutputNode() {
        value = null;
        rIdBlr = initializeResourceId(context);
        rIdBlr = addToResourceId(rIdBlr, "reboot",
                                 EXAMPLE_NS, value);
        dBlr = initializeDataNode(rIdBlr);
        dBlr = addDataNode(dBlr, "output", EXAMPLE_NS, value, null);

        value = "str1";
        dBlr = addDataNode(dBlr, "reboot-time", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str2";
        dBlr = addDataNode(dBlr, "message", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str3";
        dBlr = addDataNode(dBlr, "language", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);
        return DefaultResourceData.builder()
                .resourceId(rIdBlr.build()).addDataNode(dBlr.build()).build();
    }

    private DataNode buildDnForRpcInputWithAugment() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "reboot", EXAMPLE_NS, value, null);
        dBlr = addDataNode(dBlr, "input", EXAMPLE_NS, value, null);

        value = "str1";
        dBlr = addDataNode(dBlr, "delay", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str2";
        dBlr = addDataNode(dBlr, "message", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str3";
        dBlr = addDataNode(dBlr, "language", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);
        // adding augment nodes
        value = null;
        dBlr = addDataNode(dBlr, "status", IPTOPO_NS, value, null);
        value = "str4";
        dBlr = addDataNode(dBlr, "success", IPTOPO_NS, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

        dBlr = exitDataNode(dBlr);
        return dBlr.build();
    }

    private DataNode buildDnForRpcOutputWithAugment() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "reboot", EXAMPLE_NS, value, null);
        dBlr = addDataNode(dBlr, "output", EXAMPLE_NS, value, null);

        value = "str1";
        dBlr = addDataNode(dBlr, "reboot-time", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str2";
        dBlr = addDataNode(dBlr, "message", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str3";
        dBlr = addDataNode(dBlr, "language", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);
        // adding augment nodes
        value = null;
        dBlr = addDataNode(dBlr, "bw", IPTOPO_NS, value, null);
        value = "str4";
        dBlr = addDataNode(dBlr, "usage", IPTOPO_NS, value, null);
        dBlr = exitDataNode(dBlr);

        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);
        return dBlr.build();
    }

    private DataNode buildDataNodeForRpcOutputNode() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "reboot", EXAMPLE_NS, value, null);
        dBlr = addDataNode(dBlr, "output", EXAMPLE_NS, value, null);

        value = "str1";
        dBlr = addDataNode(dBlr, "reboot-time", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str2";
        dBlr = addDataNode(dBlr, "message", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "str3";
        dBlr = addDataNode(dBlr, "language", EXAMPLE_NS, value, null);
        dBlr = exitDataNode(dBlr);
        dBlr = exitDataNode(dBlr);

        dBlr = exitDataNode(dBlr);
        return dBlr.build();
    }

    /**
     * Processes rpc with input node.
     */
    @Test
    public void processRpcInputNode() {
        DataNode data = buildDataNodeForRpcInputNode();
        ResourceData rData = DefaultResourceData.builder().addDataNode(data)
                .build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(rData);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultRebootInput input = (DefaultRebootInput) modelObject;
        assertThat(input.delay(), is("str1"));
        assertThat(input.message(), is("str2"));
        assertThat(input.language(), is("str3"));
    }

    /**
     * Processes rpc with output node.
     */
    @Test
    public void processRpcOutputNode() {
        DataNode data = buildDataNodeForRpcOutputNode();
        ResourceData rData = DefaultResourceData.builder().addDataNode(data)
                .build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(rData);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultRebootOutput output = (DefaultRebootOutput) modelObject;
        assertThat(output.rebootTime(), is("str1"));
        assertThat(output.message(), is("str2"));
        assertThat(output.language(), is("str3"));
    }

    /**
     * Processes rpc with input and augment node.
     */
    @Test
    public void processRpcInputNodeWithAugment() {
        DataNode data = buildDnForRpcInputWithAugment();
        ResourceData rData = DefaultResourceData.builder().addDataNode(data)
                .build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(rData);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultRebootInput input = (DefaultRebootInput) modelObject;
        assertThat(input.delay(), is("str1"));
        assertThat(input.message(), is("str2"));
        assertThat(input.language(), is("str3"));
        DefaultAugmentedInput aug = input.augmentation(DefaultAugmentedInput.class);
        assertThat(aug.status().success(), is("str4"));
    }

    /**
     * Processes rpc with output and augment node.
     */
    @Test
    public void processRpcOutputNodeWithAugment() {
        DataNode data = buildDnForRpcOutputWithAugment();
        ResourceData rData = DefaultResourceData.builder().addDataNode(data)
                .build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(rData);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultRebootOutput output = (DefaultRebootOutput) modelObject;
        assertThat(output.rebootTime(), is("str1"));
        assertThat(output.message(), is("str2"));
        assertThat(output.language(), is("str3"));
        DefaultAugmentedOutput aug = output.augmentation(DefaultAugmentedOutput.class);
        assertThat(aug.bw().usage(), is("str4"));
    }

    /**
     * Processes input node with rpc as resource id.
     */
    @Test
    public void processInputNode() {
        ResourceData rData  = buildDataNodeForInputNode();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(rData);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultRebootInput input = (DefaultRebootInput) modelObject;
        assertThat(input.delay(), is("str1"));
        assertThat(input.message(), is("str2"));
        assertThat(input.language(), is("str3"));
    }

    /**
     * Processes output node with rpc as resource id.
     */
    @Test
    public void processOutputNode() {
        ResourceData rData  = buildDataNodeForOutputNode();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(rData);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultRebootOutput output = (DefaultRebootOutput) modelObject;
        assertThat(output.rebootTime(), is("str1"));
        assertThat(output.message(), is("str2"));
        assertThat(output.language(), is("str3"));
    }
}