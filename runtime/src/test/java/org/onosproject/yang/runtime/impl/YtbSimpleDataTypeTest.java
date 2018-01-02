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
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.DefaultCont;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.Iden;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.Pro;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.Tpdfbit;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.Tpdfun0;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.Tpdfun1;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.Tpdfun2;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.Tpdfun3;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.Tpdfun4;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.Val;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.cont.LfenumEnum;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.cont.Lfunion10Union;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.cont.Lfunion11Union;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.cont.Lfunion14Union;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.cont.Lfunion1Union;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.cont.Lfunion2Union;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.cont.Lfunion4Union;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.cont.Lfunion5Union;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.cont.Lfunion8Union;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.cont.Lfunion9Union;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.gr.Cont3;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.gr.DefaultCont3;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.tpdfun1.Tpdfun1Union;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.tpdfun2.Tpdfun2Union;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.tpdfun3.Tpdfun3Union;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Def1;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Def2;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Def3;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Physical;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Type;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Virtual;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.def1.Def1Union;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DefaultModelObjectData.Builder;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.ResourceData;
import org.onosproject.yang.model.ResourceId;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import static org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.cont.lfunion14union.Lfunion14UnionEnum1.ONE;
import static org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.cont.lfunion14union.Lfunion14UnionEnum1.ZERO;
import static org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.tpdfun0.Tpdfun0Enum.ASTERISK;
import static org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.tpdfun0.Tpdfun0Enum.SUCCESSFUL_EXIT;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.def1.def1union.Def1UnionEnum1.HUNDRED;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.def1.def1union.Def1UnionEnum1.TEN;
import static org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.def1.def1union.Def1UnionEnum1.YANGAUTOPREFIX3_3MS;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.model.LeafType.BINARY;
import static org.onosproject.yang.model.LeafType.BITS;
import static org.onosproject.yang.model.LeafType.BOOLEAN;
import static org.onosproject.yang.model.LeafType.DECIMAL64;
import static org.onosproject.yang.model.LeafType.EMPTY;
import static org.onosproject.yang.model.LeafType.ENUMERATION;
import static org.onosproject.yang.model.LeafType.IDENTITYREF;
import static org.onosproject.yang.model.LeafType.INSTANCE_IDENTIFIER;
import static org.onosproject.yang.model.LeafType.INT16;
import static org.onosproject.yang.model.LeafType.INT32;
import static org.onosproject.yang.model.LeafType.INT64;
import static org.onosproject.yang.model.LeafType.INT8;
import static org.onosproject.yang.model.LeafType.STRING;
import static org.onosproject.yang.model.LeafType.UINT16;
import static org.onosproject.yang.model.LeafType.UINT32;
import static org.onosproject.yang.model.LeafType.UINT64;
import static org.onosproject.yang.model.LeafType.UINT8;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.processSchemaRegistry;
import static org.onosproject.yang.runtime.impl.MockYangSchemaNodeProvider.registry;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;
import static org.onosproject.yang.runtime.impl.YobSimpleDataTypeTest.DT_NS;
import static org.onosproject.yang.runtime.impl.YobSimpleDataTypeTest.validateLeafRef;

/**
 * Unit test cases for resource id conversion from model object id.
 */
public class YtbSimpleDataTypeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ResourceData rscData;
    private DefaultDataTreeBuilder treeBuilder;
    private ResourceId id;
    private Builder data;
    DefaultYangModelRegistry reg;

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
     * Processes simple datatype with different datatype combination.
     */
    @Test
    public void processDataTypeValidationTest() {

        DefaultCont cont = new DefaultCont();

        byte value = -128;
        cont.lfnint8Min(value);
        value = 127;
        cont.lfnint8Max(value);
        short val = -32768;
        cont.lfnint16Min(val);
        val = 32767;
        cont.lfnint16Max(val);
        cont.lfnint32Min(-2147483648);
        cont.lfnint32Max(2147483647);
        cont.lfnint64Min(10090L);
        cont.lfnint64Max(100700L);
        val = 255;
        cont.lfnuint8Max(val);
        cont.lfnuint16Max(65535);
        cont.lfnuint32Max(10000L);
        cont.lfuint64Max((BigInteger.valueOf(32656256558L)));
        cont.lfstr("string1");
        cont.lfstr1("string2");
        cont.lfbool1(true);
        cont.lfbool2(false);
        cont.lfbool3(false);
        cont.lfdecimal1(new BigDecimal("-922337203685477580.8"));
        cont.lfdecimal2(new BigDecimal("-92233720368547758.08"));
        cont.lfdecimal3(new BigDecimal("-9223372036854775.808"));
        cont.lfdecimal4(new BigDecimal("-922337203685477.5808"));
        cont.lfdecimal6(new BigDecimal("-9223372036854.775808"));
        cont.lfenum(LfenumEnum.YANGAUTOPREFIX3_3MS);
        BitSet bits = new BitSet();
        bits.set(0, true);
        cont.lfbits(bits);
        cont.lfbinary("hey".getBytes());
        cont.lfref1("path");
        value = 100;
        cont.lfref2(value);
        cont.lfempty(true);
        cont.lfunion1(new Lfunion1Union("cost"));
        cont.lfunion2(new Lfunion2Union(
                new BigDecimal("-92233720368547758.08")));
        cont.lfunion4(new Lfunion4Union("true"));
        cont.lfunion5(new Lfunion5Union("color"));
        cont.lfunion7(new Tpdfun3(new Tpdfun3Union(new Tpdfbit(bits))));
        cont.lfunion8(new Lfunion8Union("bandwidth"));
        cont.lfunion9(new Lfunion9Union(200));
        cont.lfunion10(new Lfunion10Union(true));
        cont.lfunion11(new Lfunion11Union(new Tpdfun2(new Tpdfun2Union(
                new Tpdfun3(new Tpdfun3Union(new Tpdfbit(bits)))))));
        cont.lfunion12(new Tpdfun2(new Tpdfun2Union(new Tpdfun4(true))));
        BitSet bits1 = new BitSet();
        bits1.set(1, true);
        cont.lfunion13(new Tpdfbit(bits1));
        cont.lfunion14(new Lfunion14Union(ONE));
        cont.identityref1(Iden.class);
        cont.identityref2(Pro.class);
        cont.lfenum1(new Tpdfun0(SUCCESSFUL_EXIT));
        cont.lfenum2(new Tpdfun0(ASTERISK));
        cont.instIden("/cont");
        value = 8;
        cont.lref1(value);
        cont.lref2(Val.class);
        cont.lref3(new Lfunion14Union(ZERO));
        BitSet bits2 = new BitSet();
        bits2.set(1, 3, true);
        cont.lref4(new Tpdfbit(bits2));
        cont.lref5(new Lfunion11Union(new Tpdfun1(new Tpdfun1Union(
                new BigDecimal("-92233720368547758.08")))));
        value = 10;
        cont.lref6(value);
        cont.lref7(LfenumEnum.ENUM4);
        cont.lref8(new BigDecimal("-9223372036854.775808"));
        byte[] arr = Base64.getDecoder().decode("MTAxMDEwMTAx");
        cont.lref9(arr);
        cont.lref10(true);
        cont.lref11(new Def3(true));
        BitSet bits3 = new BitSet();
        bits3.set(2, true);
        cont.lref12(new Def2(bits3));
        cont.lref13(true);
        BitSet bits4 = new BitSet();
        bits4.set(0, 3, true);
        cont.lref14(bits4);
        cont.lref15("/cont");
        cont.lref16(Physical.class);
        cont.lref17(new Def1(new Def1Union(BigInteger.valueOf(784985))));
        arr = Base64.getDecoder().decode("MTExMTExMTE=");
        cont.lref18(arr);
        cont.lref19(new Def1(new Def1Union(HUNDRED)));
        cont.lref20("leafref");
        cont.iref1(Val.class);
        List<Class<? extends Iden>> l = new ArrayList<>();
        l.add(Pro.class);
        cont.iref2(l);
        cont.iref3(Physical.class);
        List<Class<? extends Type>> l1 = new ArrayList<>();
        l1.add(Virtual.class);
        cont.iref4(l1);

        Cont3 cont3 = getCont3Object();
        cont.cont3(cont3);
        data = new Builder();
        data.addModelObject(cont);
        rscData = treeBuilder.getResourceData(data.build());

        List<DataNode> nodes = rscData.dataNodes();
        DataNode n = nodes.get(0);
        validateDataNodeForSimpleDataTypes(n);
        DataNode n1 = nodes.get(0);
    }

    private Cont3 getCont3Object() {
        DefaultCont3 cont3 = new DefaultCont3();
        cont3.addToLlref1(108);
        cont3.addToLlref2("val");
        cont3.addToLlref3(ONE);
        BitSet bits2 = new BitSet();
        bits2.set(0, true);
        bits2.set(2, true);
        cont3.addToLlref4(new Tpdfbit(bits2));
        cont3.addToLlref5(new BigDecimal("-92233720368547758.08"));
        cont3.addToLlref6(121);
        cont3.addToLlref7(YANGAUTOPREFIX3_3MS);
        cont3.addToLlref8(new BigDecimal("-9223372036000.775808"));
        byte[] arr = Base64.getDecoder().decode("MTExMTExMTE=");
        cont3.addToLlref9(arr);
        cont3.addToLlref10(true);
        cont3.addToLlref11(true);
        BitSet bits4 = new BitSet();
        bits4.set(1, true);
        cont3.addToLlref12(new Tpdfbit(bits4));
        cont3.addToLlref13(true);
        BitSet bits3 = new BitSet();
        bits3.set(0, true);
        cont3.addToLlref14(bits3);
        cont3.addToLlref15("/cont/con2");
        cont3.addToLlref16(Virtual.class);
        cont3.addToLlref17(78498522);
        cont3.addToLlref18(Base64.getDecoder().decode("MDEwMTAxMDEw"));
        cont3.addToLlref19(TEN);
        cont3.addToLlref20("leaflistref");
        cont3.iref1(Val.class);
        List<Class<? extends Iden>> l = new ArrayList<>();
        l.add(Pro.class);
        cont3.iref2(l);
        cont3.iref3(Physical.class);
        cont3.addToIref4(Virtual.class);
        return cont3;
    }

    public void validateDataNodeForSimpleDataTypes(DataNode node) {
        DataNode n = node;
        validateDataNode(n, "cont", DT_NS, SINGLE_INSTANCE_NODE,
                         true, null);
        Iterator<DataNode> it3 = ((InnerNode) n).childNodes().values()
                .iterator();
        n = it3.next();
        validateDataNode(n, "lfnint8Min", DT_NS, INT8, "-128", true);
        n = it3.next();
        validateDataNode(n, "lfnint8Max", DT_NS, INT8, "127", true);
        n = it3.next();
        validateDataNode(n, "lfnint16Min", DT_NS, INT16, "-32768", true);
        n = it3.next();
        validateDataNode(n, "lfnint16Max", DT_NS, INT16, "32767", true);
        n = it3.next();
        validateDataNode(n, "lfnint32Min", DT_NS, INT32, "-2147483648", true);
        n = it3.next();
        validateDataNode(n, "lfnint32Max", DT_NS, INT32, "2147483647", true);
        n = it3.next();
        validateDataNode(n, "lfnint64Min", DT_NS, INT64, "10090", true);
        n = it3.next();
        validateDataNode(n, "lfnint64Max", DT_NS, INT64, "100700", true);
        n = it3.next();
        validateDataNode(n, "lfnuint8Max", DT_NS, UINT8, "255", true);
        n = it3.next();
        validateDataNode(n, "lfnuint16Max", DT_NS, UINT16, "65535", true);
        n = it3.next();
        validateDataNode(n, "lfnuint32Max", DT_NS, UINT32, "10000", true);
        n = it3.next();
        validateDataNode(n, "lfuint64Max", DT_NS, UINT64, "32656256558", true);
        n = it3.next();
        validateDataNode(n, "lfstr", DT_NS, STRING, "string1", true);
        n = it3.next();
        validateDataNode(n, "lfstr1", DT_NS, STRING, "string2", true);
        n = it3.next();
        validateDataNode(n, "lfbool1", DT_NS, BOOLEAN, "true", true);
        n = it3.next();
        validateDataNode(n, "lfbool2", DT_NS, BOOLEAN, "false", true);
        n = it3.next();
        validateDataNode(n, "lfbool3", DT_NS, BOOLEAN, "false", true);
        n = it3.next();
        validateDataNode(n, "lfdecimal1", DT_NS, DECIMAL64,
                         "-922337203685477580.8", true);
        n = it3.next();
        validateDataNode(n, "lfdecimal2", DT_NS, DECIMAL64,
                         "-92233720368547758.08", true);
        n = it3.next();
        validateDataNode(n, "lfdecimal3", DT_NS, DECIMAL64,
                         "-9223372036854775.808", true);
        n = it3.next();
        validateDataNode(n, "lfdecimal4", DT_NS, DECIMAL64,
                         "-922337203685477.5808", true);
        n = it3.next();
        validateDataNode(n, "lfdecimal6", DT_NS, DECIMAL64,
                         "-9223372036854.775808", true);
        n = it3.next();
        validateDataNode(n, "lfenum", DT_NS, ENUMERATION, "3.3ms", true);
        n = it3.next();
        validateDataNode(n, "lfbits", DT_NS, BITS, "bit1", true);
        n = it3.next();
        validateDataNode(n, "lfbinary", DT_NS, BINARY, "aGV5", true);
        n = it3.next();
        validateDataNode(n, "lfref1", DT_NS, STRING, "path", true);
        n = it3.next();
        validateDataNode(n, "lfref2", DT_NS, INT8, "100", true);
        n = it3.next();
        validateDataNode(n, "lfempty", DT_NS, EMPTY, null, true);
        n = it3.next();
        validateDataNode(n, "lfunion1", DT_NS, STRING, "cost", true);
        n = it3.next();
        validateDataNode(n, "lfunion2", DT_NS, DECIMAL64,
                         "-92233720368547758.08", true);
        n = it3.next();
        validateDataNode(n, "lfunion4", DT_NS, BOOLEAN, "true", true);
        n = it3.next();
        validateDataNode(n, "lfunion5", DT_NS, STRING, "color", true);
        n = it3.next();
        validateDataNode(n, "lfunion7", DT_NS, BITS, "b1", true);
        n = it3.next();
        validateDataNode(n, "lfunion8", DT_NS, STRING, "bandwidth", true);
        n = it3.next();
        validateDataNode(n, "lfunion9", DT_NS, UINT16, "200", true);
        n = it3.next();
        validateDataNode(n, "lfunion10", DT_NS, BOOLEAN, "true", true);
        n = it3.next();
        validateDataNode(n, "lfunion11", DT_NS, BITS, "b1", true);
        n = it3.next();
        validateDataNode(n, "lfunion12", DT_NS, EMPTY, "true", true);
        n = it3.next();
        validateDataNode(n, "lfunion13", DT_NS, BITS, "b2", true);
        n = it3.next();
        validateDataNode(n, "lfunion14", DT_NS, ENUMERATION, "one", true);
        n = it3.next();
        validateDataNode(n, "identityref1", DT_NS, IDENTITYREF, "iden", true);
        n = it3.next();
        validateDataNode(n, "identityref2", DT_NS, IDENTITYREF, "pro", true);
        n = it3.next();
        validateDataNode(n, "lfenum1", DT_NS, ENUMERATION, "successful exit", true);
        n = it3.next();
        validateDataNode(n, "lfenum2", DT_NS, ENUMERATION, "*", true);
        n = it3.next();
        validateDataNode(n, "inst-iden", DT_NS, INSTANCE_IDENTIFIER, "/cont", true);
        validateLeafRef(it3);
        n = it3.next();
        validateDataNode(n, "cont3", DT_NS, SINGLE_INSTANCE_NODE,
                         true, null);
        Iterator<DataNode> it4 = ((InnerNode) n).childNodes().values()
                .iterator();
        validateLiftListDatatype(it4);
    }

    private void validateLiftListDatatype(Iterator<DataNode> it3) {
        DataNode n = it3.next();
        validateDataNode(n, "iref1", DT_NS, IDENTITYREF, "val", true);
        n = it3.next();
        validateDataNode(n, "iref3", DT_NS, IDENTITYREF, "physical", true);
        n = it3.next();
        validateDataNode(n, "llref1", DT_NS, INT8, "108", false);
        n = it3.next();
        validateDataNode(n, "llref2", DT_NS, IDENTITYREF, "val", false);
        n = it3.next();
        validateDataNode(n, "llref3", DT_NS, ENUMERATION, "one", false);
        n = it3.next();
        validateDataNode(n, "llref4", DT_NS, BITS, "b1 b3", false);
        n = it3.next();
        validateDataNode(n, "llref5", DT_NS, DECIMAL64,
                         "-92233720368547758.08",
                         false);
        n = it3.next();
        validateDataNode(n, "llref6", DT_NS, INT8, "121", false);
        n = it3.next();
        validateDataNode(n, "llref7", DT_NS, ENUMERATION, "3.3ms", false);
        n = it3.next();
        validateDataNode(n, "llref8", DT_NS, DECIMAL64,
                         "-9223372036000.775808",
                         false);
        n = it3.next();
        validateDataNode(n, "llref9", DT_NS, BINARY, "MTExMTExMTE=", false);
        n = it3.next();
        validateDataNode(n, "llref10", DT_NS, EMPTY, null, false);
        n = it3.next();
        validateDataNode(n, "llref11", DT_NS, EMPTY, null, false);
        n = it3.next();
        validateDataNode(n, "llref12", DT_NS, BITS, "b2", false);
        n = it3.next();
        validateDataNode(n, "llref13", DT_NS, EMPTY, null, false);
        n = it3.next();
        validateDataNode(n, "llref14", DT_NS, BITS, "b1", false);
        n = it3.next();
        validateDataNode(n, "llref15", DT_NS, INSTANCE_IDENTIFIER,
                         "/cont/con2", false);
        n = it3.next();
        validateDataNode(n, "llref16", DT_NS, IDENTITYREF, "virtual", false);
        n = it3.next();
        validateDataNode(n, "llref17", DT_NS, UINT64, "78498522", false);
        n = it3.next();
        validateDataNode(n, "llref18", DT_NS, BINARY, "MDEwMTAxMDEw", false);
        n = it3.next();
        validateDataNode(n, "llref19", DT_NS, ENUMERATION, "ten", false);
        n = it3.next();
        validateDataNode(n, "llref20", DT_NS, STRING, "leaflistref", false);
        n = it3.next();

        validateDataNode(n, "iref2", DT_NS, IDENTITYREF, "pro", false);
        n = it3.next();
        validateDataNode(n, "iref4", DT_NS, IDENTITYREF, "virtual", false);
    }
}
