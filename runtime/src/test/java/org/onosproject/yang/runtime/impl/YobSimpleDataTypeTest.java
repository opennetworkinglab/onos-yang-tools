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
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.DefaultCont;
import org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.gr.Cont3;
import org.onosproject.yang.gen.v1.simpledatatypesll.rev20131112.simpledatatypesll.DefaultCont1;
import org.onosproject.yang.gen.v1.simpledatatypesll.rev20131112.simpledatatypesll.cont1.Lfenum2Enum;
import org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826.ytbdatatypes.Leaf7;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.DataNode.Builder;
import org.onosproject.yang.model.DefaultResourceData;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.ModelObjectData;
import org.onosproject.yang.model.ResourceData;

import java.util.Base64;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.tpdfun0.Tpdfun0Enum.ASTERISK;
import static org.onosproject.yang.gen.v1.simpledatatypes.rev20131112.simpledatatypes.tpdfun0.Tpdfun0Enum.SUCCESSFUL_EXIT;
import static org.onosproject.yang.gen.v1.simpledatatypesll.rev20131112.simpledatatypesll.cont1.Lfenum1Enum.GRACE_PERIOD_EXPIRED;
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
import static org.onosproject.yang.runtime.SerializerHelper.addDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.exitDataNode;
import static org.onosproject.yang.runtime.SerializerHelper.initializeDataNode;
import static org.onosproject.yang.runtime.impl.TestUtils.validateDataNode;

/**
 * Tests the YANG object building for different data types.
 */
public class YobSimpleDataTypeTest {

    public static final String DT_NS = "simple:data:types";
    private static final String DT_LL =
            "simple:data:types:ll";
    private static final String VALUE_NAMESPACE = "yms:test:ytb:data:types";
    TestYangSerializerContext context = new TestYangSerializerContext();
    private Builder dBlr;
    private String value;

    private DataNode buildDataNodeForSimpleDataTypes() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "cont", DT_NS, value, null);

        value = "-128";
        dBlr = addDataNode(dBlr, "lfnint8Min", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "127";
        dBlr = addDataNode(dBlr, "lfnint8Max", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-32768";
        dBlr = addDataNode(dBlr, "lfnint16Min", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "32767";
        dBlr = addDataNode(dBlr, "lfnint16Max", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-2147483648";
        dBlr = addDataNode(dBlr, "lfnint32Min", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "2147483647";
        dBlr = addDataNode(dBlr, "lfnint32Max", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "10090";
        dBlr = addDataNode(dBlr, "lfnint64Min", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "100700";
        dBlr = addDataNode(dBlr, "lfnint64Max", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "255";
        dBlr = addDataNode(dBlr, "lfnuint8Max", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "65535";
        dBlr = addDataNode(dBlr, "lfnuint16Max", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "10000";
        dBlr = addDataNode(dBlr, "lfnuint32Max", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "32656256558";
        dBlr = addDataNode(dBlr, "lfuint64Max", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "string1";
        dBlr = addDataNode(dBlr, "lfstr", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "string2";
        dBlr = addDataNode(dBlr, "lfstr1", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "true";
        dBlr = addDataNode(dBlr, "lfbool1", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "false";
        dBlr = addDataNode(dBlr, "lfbool2", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "false";
        dBlr = addDataNode(dBlr, "lfbool3", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-922337203685477580.8";
        dBlr = addDataNode(dBlr, "lfdecimal1", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-92233720368547758.08";
        dBlr = addDataNode(dBlr, "lfdecimal2", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-9223372036854775.808";
        dBlr = addDataNode(dBlr, "lfdecimal3", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-922337203685477.5808";
        dBlr = addDataNode(dBlr, "lfdecimal4", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-9223372036854.775808";
        dBlr = addDataNode(dBlr, "lfdecimal6", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "3.3ms";
        dBlr = addDataNode(dBlr, "lfenum", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "bit1";
        dBlr = addDataNode(dBlr, "lfbits", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "aGV5";
        dBlr = addDataNode(dBlr, "lfbinary", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "path";
        dBlr = addDataNode(dBlr, "lfref1", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "100";
        dBlr = addDataNode(dBlr, "lfref2", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "";
        dBlr = addDataNode(dBlr, "lfempty", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "cost";
        dBlr = addDataNode(dBlr, "lfunion1", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-92233720368547758.08";
        dBlr = addDataNode(dBlr, "lfunion2", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "true";
        dBlr = addDataNode(dBlr, "lfunion4", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "color";
        dBlr = addDataNode(dBlr, "lfunion5", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "b1";
        dBlr = addDataNode(dBlr, "lfunion7", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "bandwidth";
        dBlr = addDataNode(dBlr, "lfunion8", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "200";
        dBlr = addDataNode(dBlr, "lfunion9", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "true";
        dBlr = addDataNode(dBlr, "lfunion10", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "b1";
        dBlr = addDataNode(dBlr, "lfunion11", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "true";
        dBlr = addDataNode(dBlr, "lfunion12", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "b2";
        dBlr = addDataNode(dBlr, "lfunion13", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "one";
        dBlr = addDataNode(dBlr, "lfunion14", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "iden";
        dBlr = addDataNode(dBlr, "identityref1", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "pro";
        dBlr = addDataNode(dBlr, "identityref2", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "successful exit";
        dBlr = addDataNode(dBlr, "lfenum1", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "*";
        dBlr = addDataNode(dBlr, "lfenum2", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        value = "/cont";
        dBlr = addDataNode(dBlr, "inst-iden", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);

        dBlr = getLeafRefData(dBlr);
        validateDataNodeForSimpleDataTypes(dBlr.build());
        dBlr = exitDataNode(dBlr);
        return dBlr.build();
    }

    public static void validateDataNodeForSimpleDataTypes(DataNode node) {
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
    }

    static void validateLeafRef(Iterator<DataNode> it3) {

        DataNode n = it3.next();
        validateDataNode(n, "lref1", DT_NS, INT8, "8", true);
        n = it3.next();
        validateDataNode(n, "lref2", DT_NS, IDENTITYREF, "val", true);
        n = it3.next();
        validateDataNode(n, "lref3", DT_NS, ENUMERATION, "zero", true);
        n = it3.next();
        validateDataNode(n, "lref4", DT_NS, BITS, "b2 b3", true);
        n = it3.next();
        validateDataNode(n, "lref5", DT_NS, DECIMAL64, "-92233720368547758.08",
                         true);
        n = it3.next();
        validateDataNode(n, "lref6", DT_NS, INT8, "10", true);
        n = it3.next();
        validateDataNode(n, "lref7", DT_NS, ENUMERATION, "enum4", true);
        n = it3.next();
        validateDataNode(n, "lref8", DT_NS, DECIMAL64, "-9223372036854.775808",
                         true);
        n = it3.next();
        validateDataNode(n, "lref9", DT_NS, BINARY, "MTAxMDEwMTAx", true);
        n = it3.next();
        validateDataNode(n, "lref10", DT_NS, EMPTY, null, true);
        n = it3.next();
        validateDataNode(n, "lref11", DT_NS, EMPTY, null, true);
        n = it3.next();
        validateDataNode(n, "lref12", DT_NS, BITS, "b3", true);
        n = it3.next();
        validateDataNode(n, "lref13", DT_NS, EMPTY, null, true);
        n = it3.next();
        validateDataNode(n, "lref14", DT_NS, BITS, "b1 b2 b3", true);
        n = it3.next();
        validateDataNode(n, "lref15", DT_NS, INSTANCE_IDENTIFIER, "/cont", true);
        n = it3.next();
        validateDataNode(n, "lref16", DT_NS, IDENTITYREF, "physical", true);
        n = it3.next();
        validateDataNode(n, "lref17", DT_NS, UINT64, "784985", true);
        n = it3.next();
        validateDataNode(n, "lref18", DT_NS, BINARY, "MTExMTExMTE=", true);
        n = it3.next();
        validateDataNode(n, "lref19", DT_NS, ENUMERATION, "hundred", true);
        n = it3.next();
        validateDataNode(n, "lref20", DT_NS, STRING, "leafref", true);
        n = it3.next();
        validateDataNode(n, "iref1", DT_NS, IDENTITYREF, "val", true);
        n = it3.next();
        validateDataNode(n, "iref3", DT_NS, IDENTITYREF, "physical", true);
        n = it3.next();
        validateDataNode(n, "iref2", DT_NS, IDENTITYREF, "pro", false);
        n = it3.next();
        validateDataNode(n, "iref4", DT_NS, IDENTITYREF, "virtual", false);
    }

    private Builder getLeafRefData(Builder dBlr) {
        value = "8";
        dBlr = addDataNode(dBlr, "lref1", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "val";
        dBlr = addDataNode(dBlr, "lref2", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "zero";
        dBlr = addDataNode(dBlr, "lref3", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "b2 b3";
        dBlr = addDataNode(dBlr, "lref4", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "-92233720368547758.08";
        dBlr = addDataNode(dBlr, "lref5", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "10";
        dBlr = addDataNode(dBlr, "lref6", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "enum4";
        dBlr = addDataNode(dBlr, "lref7", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "-9223372036854.775808";
        dBlr = addDataNode(dBlr, "lref8", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "MTAxMDEwMTAx";
        dBlr = addDataNode(dBlr, "lref9", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "";
        dBlr = addDataNode(dBlr, "lref10", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "";
        dBlr = addDataNode(dBlr, "lref11", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "b3";
        dBlr = addDataNode(dBlr, "lref12", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "";
        dBlr = addDataNode(dBlr, "lref13", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "b1 b2 b3";
        dBlr = addDataNode(dBlr, "lref14", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "/cont";
        dBlr = addDataNode(dBlr, "lref15", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "physical";
        dBlr = addDataNode(dBlr, "lref16", DT_NS, value,
                           VALUE_NAMESPACE, null);
        dBlr = exitDataNode(dBlr);
        value = "784985";
        dBlr = addDataNode(dBlr, "lref17", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "MTExMTExMTE=";
        dBlr = addDataNode(dBlr, "lref18", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "hundred";
        dBlr = addDataNode(dBlr, "lref19", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "leafref";
        dBlr = addDataNode(dBlr, "lref20", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "val";
        dBlr = addDataNode(dBlr, "iref1", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "physical";
        dBlr = addDataNode(dBlr, "iref3", DT_NS, value,
                           VALUE_NAMESPACE, null);
        dBlr = exitDataNode(dBlr);
        value = "pro";
        dBlr = addDataNode(dBlr, "iref2", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "virtual";
        dBlr = addDataNode(dBlr, "iref4", DT_NS, value,
                           VALUE_NAMESPACE, null);
        dBlr = exitDataNode(dBlr);
        dBlr = addDataNode(dBlr, "cont3", DT_NS, null, null);
        dBlr = getLeafListBuilder(dBlr);
        dBlr = exitDataNode(dBlr);
        return dBlr;
    }

    private Builder getLeafListBuilder(Builder dBlr) {
        value = "108";
        dBlr = addDataNode(dBlr, "llref1", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "val";
        dBlr = addDataNode(dBlr, "llref2", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "one";
        dBlr = addDataNode(dBlr, "llref3", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "b1 b3";
        dBlr = addDataNode(dBlr, "llref4", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "-922337203685470058.08";
        dBlr = addDataNode(dBlr, "llref5", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "121";
        dBlr = addDataNode(dBlr, "llref6", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "3.3ms";
        dBlr = addDataNode(dBlr, "llref7", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "-9223372036000.775808";
        dBlr = addDataNode(dBlr, "llref8", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "MTExMTExMTE=";
        dBlr = addDataNode(dBlr, "llref9", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "";
        dBlr = addDataNode(dBlr, "llref11", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "b2";
        dBlr = addDataNode(dBlr, "llref12", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "";
        dBlr = addDataNode(dBlr, "llref13", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "b1";
        dBlr = addDataNode(dBlr, "llref14", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "/cont/con2";
        dBlr = addDataNode(dBlr, "llref15", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "virtual";
        dBlr = addDataNode(dBlr, "llref16", DT_NS, value,
                           "yms:test:ytb:data:types", null);
        dBlr = exitDataNode(dBlr);
        value = "78498522";
        dBlr = addDataNode(dBlr, "llref17", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "MDEwMTAxMDEw";
        dBlr = addDataNode(dBlr, "llref18", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "ten";
        dBlr = addDataNode(dBlr, "llref19", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "leaflistref";
        dBlr = addDataNode(dBlr, "llref20", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "check";
        dBlr = addDataNode(dBlr, "lref21", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "val";
        dBlr = addDataNode(dBlr, "iref1", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "pro";
        dBlr = addDataNode(dBlr, "iref2", DT_NS, value, null);
        dBlr = exitDataNode(dBlr);
        value = "physical";
        dBlr = addDataNode(dBlr, "iref3", DT_NS, value,
                           VALUE_NAMESPACE, null);
        dBlr = exitDataNode(dBlr);
        value = "virtual";
        dBlr = addDataNode(dBlr, "iref4", DT_NS, value,
                           VALUE_NAMESPACE, null);
        dBlr = exitDataNode(dBlr);
        return dBlr;
    }


    private DataNode buildDnForLeafListSimpleDataTypes() {
        dBlr = initializeDataNode(context);
        value = null;
        dBlr = addDataNode(dBlr, "cont1", DT_LL, value, null);

        value = "-128";
        dBlr = addDataNode(dBlr, "lfnint8Min", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "127";
        dBlr = addDataNode(dBlr, "lfnint8Max", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-32768";
        dBlr = addDataNode(dBlr, "lfnint16Min", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "32767";
        dBlr = addDataNode(dBlr, "lfnint16Max", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-2147483648";
        dBlr = addDataNode(dBlr, "lfnint32Min", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "2147483647";
        dBlr = addDataNode(dBlr, "lfnint32Max", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "10090";
        dBlr = addDataNode(dBlr, "lfnint64Min", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "100700";
        dBlr = addDataNode(dBlr, "lfnint64Max", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "255";
        dBlr = addDataNode(dBlr, "lfnuint8Max", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "65535";
        dBlr = addDataNode(dBlr, "lfnuint16Max", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "10000";
        dBlr = addDataNode(dBlr, "lfnuint32Max", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "32656256558";
        dBlr = addDataNode(dBlr, "lfuint64Max", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "string1";
        dBlr = addDataNode(dBlr, "lfstr", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "string2";
        dBlr = addDataNode(dBlr, "lfstr1", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "true";
        dBlr = addDataNode(dBlr, "lfbool1", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "false";
        dBlr = addDataNode(dBlr, "lfbool2", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "false";
        dBlr = addDataNode(dBlr, "lfbool3", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-922337203685477580.8";
        dBlr = addDataNode(dBlr, "lfdecimal1", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-92233720368547758.08";
        dBlr = addDataNode(dBlr, "lfdecimal2", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-9223372036854775.808";
        dBlr = addDataNode(dBlr, "lfdecimal3", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-922337203685477.5808";
        dBlr = addDataNode(dBlr, "lfdecimal4", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-9223372036854.775808";
        dBlr = addDataNode(dBlr, "lfdecimal6", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "enum1";
        dBlr = addDataNode(dBlr, "lfenum", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "bit1";
        dBlr = addDataNode(dBlr, "lfbits", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "aGVsbG8=";
        dBlr = addDataNode(dBlr, "lfbinary", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "path";
        dBlr = addDataNode(dBlr, "lfref1", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "100";
        dBlr = addDataNode(dBlr, "lfref2", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "";
        dBlr = addDataNode(dBlr, "lfempty", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "cost";
        dBlr = addDataNode(dBlr, "lfunion1", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "-92233720368547758.08";
        dBlr = addDataNode(dBlr, "lfunion2", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "true";
        dBlr = addDataNode(dBlr, "lfunion4", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "color";
        dBlr = addDataNode(dBlr, "lfunion5", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "b1";
        dBlr = addDataNode(dBlr, "lfunion7", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "bandwidth";
        dBlr = addDataNode(dBlr, "lfunion8", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "200";
        dBlr = addDataNode(dBlr, "lfunion9", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "true";
        dBlr = addDataNode(dBlr, "lfunion10", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "b1";
        dBlr = addDataNode(dBlr, "lfunion11", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "true";
        dBlr = addDataNode(dBlr, "lfunion12", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "b2";
        dBlr = addDataNode(dBlr, "lfunion13", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "one";
        dBlr = addDataNode(dBlr, "lfunion14", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "iden";
        dBlr = addDataNode(dBlr, "identityref1", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "grace period expired";
        dBlr = addDataNode(dBlr, "lfenum1", null, value, null);
        dBlr = exitDataNode(dBlr);

        value = "*";
        dBlr = addDataNode(dBlr, "lfenum2", null, value, null);
        dBlr = exitDataNode(dBlr);
        validateLeafListDataTypes(dBlr.build());
        dBlr = exitDataNode(dBlr);

        return dBlr.build();
    }

    private void validateLeafListDataTypes(DataNode node) {
        DataNode n = node;
        validateDataNode(n, "cont1", DT_LL, SINGLE_INSTANCE_NODE,
                         true, null);
        Iterator<DataNode> it3 = ((InnerNode) n).childNodes().values()
                .iterator();
        n = it3.next();
        validateDataNode(n, "lfnint8Min", DT_LL, INT8, "-128", false);
        n = it3.next();
        validateDataNode(n, "lfnint8Max", DT_LL, INT8, "127", false);
        n = it3.next();
        validateDataNode(n, "lfnint16Min", DT_LL, INT16, "-32768", false);
        n = it3.next();
        validateDataNode(n, "lfnint16Max", DT_LL, INT16, "32767", false);
        n = it3.next();
        validateDataNode(n, "lfnint32Min", DT_LL, INT32, "-2147483648", false);
        n = it3.next();
        validateDataNode(n, "lfnint32Max", DT_LL, INT32, "2147483647", false);
        n = it3.next();
        validateDataNode(n, "lfnint64Min", DT_LL, INT64, "10090", false);
        n = it3.next();
        validateDataNode(n, "lfnint64Max", DT_LL, INT64, "100700", false);
        n = it3.next();
        validateDataNode(n, "lfnuint8Max", DT_LL, UINT8, "255", false);
        n = it3.next();
        validateDataNode(n, "lfnuint16Max", DT_LL, UINT16, "65535", false);
        n = it3.next();
        validateDataNode(n, "lfnuint32Max", DT_LL, UINT32, "10000", false);
        n = it3.next();
        validateDataNode(n, "lfuint64Max", DT_LL, UINT64, "32656256558", false);
        n = it3.next();
        validateDataNode(n, "lfstr", DT_LL, STRING, "string1", false);
        n = it3.next();
        validateDataNode(n, "lfstr1", DT_LL, STRING, "string2", false);
        n = it3.next();
        validateDataNode(n, "lfbool1", DT_LL, BOOLEAN, "true", false);
        n = it3.next();
        validateDataNode(n, "lfbool2", DT_LL, BOOLEAN, "false", false);
        n = it3.next();
        validateDataNode(n, "lfbool3", DT_LL, BOOLEAN, "false", false);
        n = it3.next();
        validateDataNode(n, "lfdecimal1", DT_LL, DECIMAL64,
                         "-922337203685477580.8", false);
        n = it3.next();
        validateDataNode(n, "lfdecimal2", DT_LL, DECIMAL64,
                         "-92233720368547758.08", false);
        n = it3.next();
        validateDataNode(n, "lfdecimal3", DT_LL, DECIMAL64,
                         "-9223372036854775.808", false);
        n = it3.next();
        validateDataNode(n, "lfdecimal4", DT_LL, DECIMAL64,
                         "-922337203685477.5808", false);
        n = it3.next();
        validateDataNode(n, "lfdecimal6", DT_LL, DECIMAL64,
                         "-9223372036854.775808", false);
        n = it3.next();
        validateDataNode(n, "lfenum", DT_LL, ENUMERATION, "enum1", false);
        n = it3.next();
        validateDataNode(n, "lfbits", DT_LL, BITS, "bit1", false);
        n = it3.next();
        validateDataNode(n, "lfbinary", DT_LL, BINARY, "aGVsbG8=", false);
        n = it3.next();
        validateDataNode(n, "lfref1", DT_LL, STRING, "path", false);
        n = it3.next();
        validateDataNode(n, "lfref2", DT_LL, INT8, "100", false);
        n = it3.next();
        validateDataNode(n, "lfempty", DT_LL, EMPTY, null, false);
        n = it3.next();
        validateDataNode(n, "lfunion1", DT_LL, STRING, "cost", false);
        n = it3.next();
        validateDataNode(n, "lfunion2", DT_LL, DECIMAL64,
                         "-92233720368547758.08", false);
        n = it3.next();
        validateDataNode(n, "lfunion4", DT_LL, BOOLEAN, "true", false);
        n = it3.next();
        validateDataNode(n, "lfunion5", DT_LL, STRING, "color", false);
        n = it3.next();
        validateDataNode(n, "lfunion7", DT_LL, BITS, "b1", false);
        n = it3.next();
        validateDataNode(n, "lfunion8", DT_LL, STRING, "bandwidth", false);
        n = it3.next();
        validateDataNode(n, "lfunion9", DT_LL, UINT16, "200", false);
        n = it3.next();
        validateDataNode(n, "lfunion10", DT_LL, BOOLEAN, "true", false);
        n = it3.next();
        validateDataNode(n, "lfunion11", DT_LL, BITS, "b1", false);
        n = it3.next();
        // need to be handled later
        validateDataNode(n, "lfunion12", DT_LL, EMPTY, "true", false);
        n = it3.next();
        validateDataNode(n, "lfunion13", DT_LL, BITS, "b2", false);
        n = it3.next();
        validateDataNode(n, "lfunion14", DT_LL, ENUMERATION, "one", false);
        n = it3.next();
        validateDataNode(n, "identityref1", DT_LL, IDENTITYREF, "iden", false);
        n = it3.next();
        validateDataNode(n, "lfenum1", DT_LL, ENUMERATION,
                         "grace period expired", false);
        n = it3.next();
        validateDataNode(n, "lfenum2", DT_LL, ENUMERATION, "*", false);
    }


    @Test
    public void allDataTypesTest() {
        DataNode dataNode = buildDataNodeForSimpleDataTypes();
        ResourceData data = DefaultResourceData.builder().addDataNode(dataNode).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultCont cont = ((DefaultCont) modelObject);
        byte value = -128;
        assertThat(cont.lfnint8Min(), is(value));

        value = 127;
        assertThat(cont.lfnint8Max(), is(value));

        short val = -32768;
        assertThat(cont.lfnint16Min(), is(val));

        val = 32767;
        assertThat(cont.lfnint16Max(), is(val));

        assertThat(cont.lfnint32Min(), is(-2147483648));
        assertThat(cont.lfnint32Max(), is(2147483647));
        assertThat(cont.lfnint64Min(), is(10090L));
        assertThat(cont.lfnint64Max(), is(100700L));

        val = 255;
        assertThat(cont.lfnuint8Max(), is(val));
        assertThat(cont.lfnuint16Max(), is(65535));
        assertThat(cont.lfnuint32Max(), is(10000L));
        assertThat(cont.lfuint64Max().toString(), is("32656256558"));
        assertThat(cont.lfstr(), is("string1"));
        assertThat(cont.lfstr1(), is("string2"));
        assertThat(cont.lfbool1(), is(true));
        assertThat(cont.lfbool2(), is(false));
        assertThat(cont.lfbool3(), is(false));
        assertThat(cont.lfdecimal1().toString(),
                   is("-922337203685477580.8"));
        assertThat(cont.lfdecimal2().toString(),
                   is("-92233720368547758.08"));
        assertThat(cont.lfdecimal3().toString(),
                   is("-9223372036854775.808"));
        assertThat(cont.lfdecimal4().toString(),
                   is("-922337203685477.5808"));
        assertThat(cont.lfdecimal6().toString(),
                   is("-9223372036854.775808"));
        assertThat(cont.lfenum().toString(), is("3.3ms"));
        assertThat(cont.lfbits().toString(), is("{0}"));
        String str = new String(cont.lfbinary());
        assertThat(str, is("hey"));
        assertThat(cont.lfref1(), is("path"));
        value = 100;
        assertThat(cont.lfref2(), is(value));
        assertThat(cont.lfempty(), is(true));
        assertThat(cont.lfunion1().toString(), is("cost"));
        assertThat(cont.lfunion2().toString(), is("-92233720368547758.08"));
        assertThat(cont.lfunion4().toString(), is("true"));
        assertThat(cont.lfunion5().toString(), is("color"));
        assertThat(cont.lfunion7().toString(), is("b1 "));
        assertThat(cont.lfunion8().toString(), is("bandwidth"));
        assertThat(cont.lfunion9().toString(), is("200"));
        assertThat(cont.lfunion10().toString(), is("true"));
        assertThat(cont.lfunion11().toString(), is("b1 "));
        assertThat(cont.lfunion12().toString(), is("true"));
        assertThat(cont.lfunion13().toString(), is("b2 "));
        assertThat(cont.lfunion14().toString(), is("one"));
        assertThat(cont.identityref1().getSimpleName(), is("Iden"));
        assertThat(cont.identityref2().getSimpleName(), is("Pro"));
        assertThat(cont.lfenum1().enumeration(), is(SUCCESSFUL_EXIT));
        assertThat(cont.lfenum2().enumeration(), is(ASTERISK));
        assertThat(cont.instIden(), is("/cont"));
        value = 8;
        assertThat(cont.lref1(), is(value));
        assertThat(cont.lref2().toString(), is(
                "class org.onosproject.yang.gen.v1.simpledatatypes" +
                        ".rev20131112.simpledatatypes.Val"));
        assertThat(cont.lref3().toString(), is("zero"));
        assertThat(cont.lref4().toString(), is("b2 b3 "));
        assertThat(cont.lref5().toString(), is("-92233720368547758.08"));
        value = 10;
        assertThat(cont.lref6(), is(value));
        assertThat(cont.lref7().toString(), is("enum4"));
        assertThat(cont.lref8().toString(), is("-9223372036854.775808"));
        byte[] arr = Base64.getDecoder().decode("MTAxMDEwMTAx");
        assertThat(cont.lref9(), is(arr));
        assertThat(cont.lref10(), is(true));
        assertThat(cont.lref11().toString(), is("true"));
        assertThat(cont.lref12().toString(), is("b3 "));
        assertThat(cont.lref13(), is(true));
        String bit = Leaf7.toString(cont.lref14());
        assertThat(bit, is("b1 b2 b3 "));
        assertThat(cont.lref15(), is("/cont"));
        assertThat(cont.lref16().toString(), is(
                "class org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826." +
                        "ytbdatatypes.Physical"));
        assertThat(cont.lref17().toString(), is("784985"));
        arr = Base64.getDecoder().decode("MTExMTExMTE=");
        assertThat(cont.lref18(), is(arr));
        assertThat(cont.lref19().toString(), is("hundred"));
        assertThat(cont.lref20(), is("leafref"));
        assertThat(cont.iref1().toString(), is(
                "class org.onosproject.yang.gen.v1.simpledatatypes.rev201311" +
                        "12.simpledatatypes.Val"));
        assertThat(cont.iref2().get(0).toString(), is(
                "class org.onosproject.yang.gen.v1.simpledatatypes.rev201311" +
                        "12.simpledatatypes.Pro"));
        assertThat(cont.iref3().toString(), is(
                "class org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826." +
                        "ytbdatatypes.Physical"));
        assertThat(cont.iref4().get(0).toString(), is(
                "class org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826." +
                        "ytbdatatypes.Virtual"));
        Cont3 cont3 = cont.cont3();
        value = 108;
        assertThat(cont3.llref1().get(0), is(value));
        assertThat(cont3.llref2().get(0).toString(), is(
                "class org.onosproject.yang.gen.v1.simpledatatypes" +
                        ".rev20131112.simpledatatypes.Val"));
        assertThat(cont3.llref3().get(0).toString(), is("one"));
        assertThat(cont3.llref4().get(0).toString(), is("b1 b3 "));
        assertThat(cont3.llref5().get(0).toString(), is("-922337203685470058.08"));
        value = 121;
        assertThat(cont3.llref6().get(0), is(value));
        assertThat(cont3.llref7().get(0).toString(), is("3.3ms"));
        assertThat(cont3.llref8().get(0).toString(), is("-9223372036000.775808"));
        arr = Base64.getDecoder().decode("MTExMTExMTE=");
        assertThat(cont3.llref9().get(0), is(arr));
        assertThat(cont3.llref11().get(0).toString(), is("true"));
        assertThat(cont3.llref12().get(0).toString(), is("b2 "));
        assertThat(cont3.llref13().get(0), is(true));
        bit = Leaf7.toString((BitSet) cont3.llref14().get(0));
        assertThat(bit, is("b1 "));
        assertThat(cont3.llref15().get(0), is("/cont/con2"));
        assertThat(cont3.llref16().get(0).toString(), is(
                "class org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826." +
                        "ytbdatatypes.Virtual"));
        assertThat(cont3.llref17().get(0).toString(), is("78498522"));
        arr = Base64.getDecoder().decode("MDEwMTAxMDEw");
        assertThat(cont3.llref18().get(0), is(arr));
        assertThat(cont3.llref19().get(0).toString(), is("ten"));
        assertThat(cont3.llref20().get(0), is("leaflistref"));
        assertThat(cont3.lref21(), is("check"));
        assertThat(cont3.iref1().toString(), is(
                "class org.onosproject.yang.gen.v1.simpledatatypes.rev201311" +
                        "12.simpledatatypes.Val"));
        assertThat(cont3.iref2().get(0).toString(), is(
                "class org.onosproject.yang.gen.v1.simpledatatypes.rev201311" +
                        "12.simpledatatypes.Pro"));
        assertThat(cont3.iref3().toString(), is(
                "class org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826." +
                        "ytbdatatypes.Physical"));
        assertThat(cont3.iref4().get(0).toString(), is(
                "class org.onosproject.yang.gen.v1.ytbdatatypes.rev20160826." +
                        "ytbdatatypes.Virtual"));
    }


    @Test
    public void allDataTypesTestForLeafList() {
        DataNode dataNode = buildDnForLeafListSimpleDataTypes();
        ResourceData data = DefaultResourceData.builder().addDataNode(dataNode).build();
        DefaultYobBuilder builder = new DefaultYobBuilder(
                (DefaultYangModelRegistry) context.getContext());
        ModelObjectData modelObjectData = builder.getYangObject(data);
        List<ModelObject> modelObjectList = modelObjectData.modelObjects();
        ModelObject modelObject = modelObjectList.get(0);
        DefaultCont1 cont = ((DefaultCont1) modelObject);
        byte value = -128;
        assertThat(cont.lfnint8Min().get(0), is(value));

        value = 127;
        assertThat(cont.lfnint8Max().get(0), is(value));

        short val = -32768;
        assertThat(cont.lfnint16Min().get(0), is(val));

        val = 32767;
        assertThat(cont.lfnint16Max().get(0), is(val));

        assertThat(cont.lfnint32Min().get(0), is(-2147483648));
        assertThat(cont.lfnint32Max().get(0), is(2147483647));
        assertThat(cont.lfnint64Min().get(0), is(10090L));
        assertThat(cont.lfnint64Max().get(0), is(100700L));

        val = 255;
        assertThat(cont.lfnuint8Max().get(0), is(val));
        assertThat(cont.lfnuint16Max().get(0), is(65535));
        assertThat(cont.lfnuint32Max().get(0), is(10000L));
        assertThat(cont.lfuint64Max().get(0).toString(), is("32656256558"));
        assertThat(cont.lfstr().get(0), is("string1"));
        assertThat(cont.lfstr1().get(0), is("string2"));
        assertThat(cont.lfbool1().get(0), is(true));
        assertThat(cont.lfbool2().get(0), is(false));
        assertThat(cont.lfbool3().get(0), is(false));
        assertThat(cont.lfdecimal1().get(0).toString(),
                   is("-922337203685477580.8"));
        assertThat(cont.lfdecimal2().get(0).toString(),
                   is("-92233720368547758.08"));
        assertThat(cont.lfdecimal3().get(0).toString(),
                   is("-9223372036854775.808"));
        assertThat(cont.lfdecimal4().get(0).toString(),
                   is("-922337203685477.5808"));
        assertThat(cont.lfdecimal6().get(0).toString(),
                   is("-9223372036854.775808"));
        assertThat(cont.lfenum().get(0).toString(), is("enum1"));
        assertThat(cont.lfbits().get(0).toString(), is("{0}"));
        String str = new String(cont.lfbinary().get(0));
        assertThat(str, is("hello"));
        assertThat(cont.lfref1().get(0), is("path"));
        value = 100;
        assertThat(cont.lfref2().get(0), is(value));
        assertThat(cont.lfempty().get(0), is(true));
        assertThat(cont.lfunion1().get(0).toString(), is("cost"));
        assertThat(cont.lfunion2().get(0).toString(), is("-92233720368547758.08"));
        assertThat(cont.lfunion4().get(0).toString(), is("true"));
        assertThat(cont.lfunion5().get(0).toString(), is("color"));
        assertThat(cont.lfunion7().get(0).toString(), is("b1 "));
        assertThat(cont.lfunion8().get(0).toString(), is("bandwidth"));
        assertThat(cont.lfunion9().get(0).toString(), is("200"));
        assertThat(cont.lfunion10().get(0).toString(), is("true"));
        assertThat(cont.lfunion11().get(0).toString(), is("b1 "));
        assertThat(cont.lfunion12().get(0).toString(), is("true"));
        assertThat(cont.lfunion13().get(0).toString(), is("b2 "));
        assertThat(cont.lfunion14().get(0).toString(), is("one"));
        assertThat(cont.identityref1().get(0).getSimpleName().toString(),
                   is("Iden"));
        assertThat(cont.lfenum1().get(0), is(GRACE_PERIOD_EXPIRED));
        assertThat(cont.lfenum2().get(0), is(Lfenum2Enum.ASTERISK));
    }
}
