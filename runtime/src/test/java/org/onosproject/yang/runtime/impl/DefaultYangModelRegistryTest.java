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
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangRevision;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.getDateInStringFormat;

/**
 * Unit test for model registry.
 */
public class DefaultYangModelRegistryTest {

    private static final String SCHEMA_NAME_3 = "ietf-network3";
    private static final String INTERFACE_NAME_3 =
            "org.onosproject.yang.gen.v1.ietfnetwork3.rev20151208.IetfNetwork3";

    private static final String SCHEMA_NAME_4_14 = "ietf-network4@2014-12-08";
    private static final String SCHEMA_NAME_4_15 = "ietf-network4@2015-12-08";
    private static final String SCHEMA_NAME_4_16 = "ietf-network4@2016-12-08";
    private static final String SCHEMA_NAME_4_17 = "ietf-network4@2017-12-08";
    private static final String SCHEMA_NAME_4 = "ietf-network4";
    private static final String INTERFACE_NAME_REV_14 =
            "org.onosproject.yang.gen.v1.ietfnetwork4.rev20141208.IetfNetwork4";
    private static final String INTERFACE_NAME_REV_15 =
            "org.onosproject.yang.gen.v1.ietfnetwork4.rev20151208.IetfNetwork4";
    private static final String INTERFACE_NAME_REV_16 =
            "org.onosproject.yang.gen.v1.ietfnetwork4.rev20161208.IetfNetwork4";
    private static final String INTERFACE_NAME_REV_17 =
            "org.onosproject.yang.gen.v1.ietfnetwork4.rev20171208.IetfNetwork4";
    private static final String INTERFACE_NAME_NO_REV =
            "org.onosproject.yang.gen.v1.ietfnetwork4.IetfNetwork4";

    private static final String CHECK = "check";
    private static final String DATE_NAMESPACE = "2015-12-08";
    private static final String NAMESPACE =
            "urn:ietf:params:xml:ns:yang:ietf-network4:check:namespace";

    private final TestYangSchemaNodeProvider provider =
            new TestYangSchemaNodeProvider();

    /**
     * Unit test case in which schema node should be present.
     *
     * @throws IOException when fails to do IO operation
     */
    @Test
    public void testForGetSchemaNode()
            throws IOException {

        provider.processSchemaRegistry();

        List<YangNode> nodes = new ArrayList<>();
        DefaultYangModelRegistry registry = provider.registry();

        YangSchemaNode yangNode = registry.getForSchemaName(SCHEMA_NAME_3);
        assertThat(true, is(SCHEMA_NAME_3.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_3);
        assertThat(true, is(SCHEMA_NAME_3.equals(yangNode.getName())));

        Class<?> cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(INTERFACE_NAME_3.equals(cls.getName())));

        //Unregister service
        nodes.add((YangNode) yangNode);
        provider.unRegister(nodes);

        yangNode = registry.getForSchemaName(SCHEMA_NAME_3);
        assertThat(true, is(yangNode == null));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_3);
        assertThat(true, is(yangNode == null));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(cls == null));
    }

    /**
     * Unit test case in which schema node should be present with multi
     * revisions.
     *
     * @throws IOException when fails to do IO operation
     */
    @Test
    public void testForGetSchemaNodeWhenNoRevision()
            throws IOException {

        provider.processSchemaRegistry();
        DefaultYangModelRegistry registry = provider.registry();

        List<YangNode> nodes = new ArrayList<>();
        //Service with rev.
        YangSchemaNode yangNode = registry.getForSchemaName(SCHEMA_NAME_4_15);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_15);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        Class<?> cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(INTERFACE_NAME_REV_15.equals(cls.getName())));

        //unregister SERVICE_NAME_REV_15.
        nodes.add((YangNode) yangNode);
        provider.unRegister(nodes);

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode != null));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_15);
        assertThat(true, is(yangNode == null));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(cls == null));

        //Here the yangNode should be the node which does not have revision.
        // asset should pass with false.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(((YangNode) yangNode).getRevision() == null));

        //---------------------------------------------------------------//

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_NO_REV);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(INTERFACE_NAME_NO_REV.equals(cls.getName())));

        //unregister SERVICE_NAME_NO_REV.
        nodes.add((YangNode) yangNode);
        provider.unRegister(nodes);

        //Here the yangNode should be the node which have different revision.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode != null));
        assertThat(true, is(((YangNode) yangNode).getRevision() != null));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_NO_REV);
        assertThat(true, is(yangNode == null));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(cls == null));
    }

    /**
     * Unit test case in which schema node should be present with multi
     * revisions.
     *
     * @throws IOException when fails to do IO operation
     */
    @Test
    public void testForGetSchemaNodeWhenMultiRevision()
            throws IOException {

        provider.processSchemaRegistry();
        DefaultYangModelRegistry registry = provider.registry();

        List<YangNode> nodes = new ArrayList<>();
        //Service with rev.
        YangSchemaNode yangNode = registry.getForSchemaName(SCHEMA_NAME_4_15);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_15);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        Class<?> cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(INTERFACE_NAME_REV_15.equals(cls.getName())));

        //Unregister SERVICE_NAME_REV_15.
        nodes.add((YangNode) yangNode);
        provider.unRegister(nodes);

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode != null));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_15);
        assertThat(true, is(yangNode == null));

        //Here the yangNode should be the node which does not have revision.
        // asset should pass with false.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(((YangNode) yangNode).getRevision() == null));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(cls != null));

        //---------------------------------------------------------------//

        //Here the yangNode should be the node which does not have revision.
        // asset should pass with false.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(((YangNode) yangNode).getRevision() == null));

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4_16);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_16);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(INTERFACE_NAME_REV_16.equals(cls.getName())));

        //Unregister SERVICE_NAME_REV_16.
        nodes.add((YangNode) yangNode);
        provider.unRegister(nodes);

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_16);
        assertThat(true, is(yangNode == null));

        //Here the yangNode should be the node which have different revision.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(((YangNode) yangNode).getRevision() == null));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(cls != null));

        //---------------------------------------------------------------//

        //Service with different revision.

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4_17);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_17);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(INTERFACE_NAME_REV_17.equals(cls.getName())));

        //Unregister SERVICE_NAME_REV_17.
        nodes.add((YangNode) yangNode);
        provider.unRegister(nodes);

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_17);
        assertThat(true, is(yangNode == null));

        //Here the yangNode should be the node which have different revision.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(((YangNode) yangNode).getRevision() == null));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(cls != null));

        //---------------------------------------------------------------//

        //Service no revision.

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_NO_REV);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(INTERFACE_NAME_NO_REV.equals(cls.getName())));

        //Unregister SERVICE_NAME_NO_REV.
        nodes.add((YangNode) yangNode);
        provider.unRegister(nodes);

        yangNode = registry.getForRegClassName(INTERFACE_NAME_NO_REV);
        assertThat(true, is(yangNode == null));

        //Here the yangNode should be the node which have different revision.
        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode != null));
        assertThat(true, is(((YangNode) yangNode).getRevision() != null));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(cls != null));

        //---------------------------------------------------------------//

        //Service with different revision.

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4_14);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_14);
        assertThat(true, is(SCHEMA_NAME_4.equals(yangNode.getName())));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(INTERFACE_NAME_REV_14.equals(cls.getName())));

        //Unregister SERVICE_NAME_REV_14.
        nodes.add((YangNode) yangNode);
        provider.unRegister(nodes);

        yangNode = registry.getForSchemaName(SCHEMA_NAME_4);
        assertThat(true, is(yangNode == null));

        yangNode = registry.getForRegClassName(INTERFACE_NAME_REV_14);
        assertThat(true, is(yangNode == null));

        cls = registry.getRegisteredClass(yangNode);
        assertThat(true, is(cls == null));
    }

    /**
     * get schema for namespace in decode test.
     */
    @Test
    public void testGetNodeWrtNamespace() {
        provider.processSchemaRegistry();
        DefaultYangModelRegistry registry = provider.registry();

        YangSchemaNode yangNode = registry.getForNameSpace(NAMESPACE, false);
        assertThat(true, is(CHECK.equals(yangNode.getName())));

        YangRevision rev = ((YangNode) yangNode).getRevision();
        assertThat(true, is(rev != null));

        String date = getDateInStringFormat((YangNode) yangNode);
        assertThat(true, is(DATE_NAMESPACE.equals(date)));
    }
}
