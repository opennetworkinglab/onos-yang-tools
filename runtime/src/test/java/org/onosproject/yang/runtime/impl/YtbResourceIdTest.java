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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.type.DefaultCon1;
import org.onosproject.yang.gen.v1.modulelistandkey.rev20160826.modulelistandkey.type.Leaf1Union;
import org.onosproject.yang.gen.v1.modulelistandkeyaugment.rev20160826.modulelistandkeyaugment.val.AugmentedSchVal;
import org.onosproject.yang.model.DefaultModelObjectData.Builder;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafIdentifier;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.ModelObjectId;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.model.SchemaId;

import java.math.BigInteger;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

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
import static org.onosproject.yang.model.ModelObjectId.builder;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;

/**
 * Unit test cases for resource id conversion from model object id.
 */
public class YtbResourceIdTest {


    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ResourceData rscData;
    private DefaultDataTreeBuilder treeBuilder;
    private ResourceId id;
    private List<NodeKey> keys;
    private SchemaId sid;
    private ModelObjectId mid;
    private Builder data;
    DefaultYangModelRegistry reg;

    /**
     * Prior setup for each UT.
     */
    private void setUp() {
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
        setUp();
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
        setUp();
        data = new Builder();
        ModelObjectId.Builder builder = buildMidWithKeys();
        Tdef1 tdef1 = new Tdef1(Tdef1Union.fromString("thousand"));
        mid = builder.addChild(DefaultCon1.class).addChild(LL, tdef1).build();
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
        setUp();
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
        assertThat(key.value(), is("AwgE"));

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
        setUp();
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

        validateKeyLeaf(it.next(), "leaf8", nameSpace, "11011");

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
}
