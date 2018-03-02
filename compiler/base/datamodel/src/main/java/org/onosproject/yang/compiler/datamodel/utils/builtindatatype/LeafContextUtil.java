/*
 * Copyright 2016-present Open Networking Foundation
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

package org.onosproject.yang.compiler.datamodel.utils.builtindatatype;

import org.onosproject.yang.compiler.datamodel.YangBits;
import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangEnum;
import org.onosproject.yang.compiler.datamodel.YangEnumeration;
import org.onosproject.yang.compiler.datamodel.YangIdentity;
import org.onosproject.yang.compiler.datamodel.YangIdentityRef;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangUnion;
import org.onosproject.yang.model.LeafType;
import org.onosproject.yang.model.YangNamespace;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;

public final class LeafContextUtil {

    // Object provider constant error string.
    private static final String E_DATATYPE = "Data type not supported.";
    private static final String T = "true";
    private static final String F = "false";
    // RFC 4648
    private static final String BIN_REGEX = "^(?:[A-Za-z0-9+/]{4})*" +
            "(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0" +
            "-9+/]{4})$";
    // allows the null/empty value
    private static final String BREGEX = "^(?:[A-Za-z0-9+/]{4})*" +
            "(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$";

    /**
     * Restricts creation of object providers instance.
     */
    private LeafContextUtil() {
    }

    /**
     * Returns object from string value.
     *
     * @param typeInfo refers to YANG type information
     * @param v        v argument is leaf value used to set the value in method
     * @param dataType yang data type
     * @return values object of corresponding given data type
     * @throws IllegalArgumentException if input is not valid
     */
    public static Object getObject(YangType typeInfo, String v,
                                   YangDataTypes dataType)
            throws IllegalArgumentException {
        YangDataTypes type;
        if (dataType != null) {
            type = dataType;
        } else {
            type = typeInfo.getDataType();
        }
        switch (type) {
            case INT8:
                return Byte.parseByte(v);
            case UINT8:
            case INT16:
                return Short.parseShort(v);
            case UINT16:
            case INT32:
                return Integer.parseInt(v);
            case UINT32:
            case INT64:
                return Long.parseLong(v);
            case UINT64:
                return new BigInteger(v);
            case EMPTY:
                if (v == null || v.equals("")) {
                    return null;
                } else if (v.equals(T) || v.equals(F)) {
                    return Boolean.parseBoolean(v);
                }
            case BOOLEAN:
                if (v.equals(T) || v.equals(F)) {
                    return Boolean.parseBoolean(v);
                }
                throw new IllegalArgumentException("Invalid boolean value: " + v);
            case ENUMERATION:
                try {
                    SortedSet<YangEnum> set = ((YangEnumeration) typeInfo
                            .getDataTypeExtendedInfo()).getEnumSet();
                    for (YangEnum en : set) {
                        if (en.getNamedValue().equals(v)) {
                            return v;
                        }
                    }
                } catch (Exception e) {
                    // do nothing
                }
                throw new IllegalArgumentException("Invalid " + typeInfo + " value: " + v);
            case BITS:
                try {
                    YangBits e = ((YangBits) typeInfo
                            .getDataTypeExtendedInfo());
                    String[] bitSet = v.trim().split(SPACE);
                    Set set = new HashSet(Arrays.asList(bitSet));
                    if (e.getBitNameMap().keySet().containsAll(set)) {
                        return v;
                    }
                } catch (Exception e) {
                    // do nothing
                }
                throw new IllegalArgumentException("Invalid " + typeInfo + " value: " + v);
            case BINARY:
                if (v.matches(BREGEX)) {
                    return v;
                }
                throw new IllegalArgumentException("Invalid " + typeInfo + " value: " + v);
            case IDENTITYREF:
            case STRING:
            case INSTANCE_IDENTIFIER:
                return v;
            case DECIMAL64:
                return new BigDecimal(v);
            case LEAFREF:
                YangType refType = ((YangLeafRef) typeInfo
                        .getDataTypeExtendedInfo()).getEffectiveDataType();
                return getObject(refType, v, refType.getDataType());
            case DERIVED:
                // referred typedef's list of type will always has only one type
                YangType rt = ((YangDerivedInfo) typeInfo
                        .getDataTypeExtendedInfo()).getReferredTypeDef()
                        .getTypeList().get(0);
                return getObject(rt, v, rt.getDataType());
            case UNION:
                return parseUnionTypeInfo(typeInfo, v);
            default:
                throw new IllegalArgumentException("Unexpected data type " + type);
        }
    }

    /**
     * Returns value namespace from string value.
     *
     * @param typeInfo refers to YANG type information
     * @param v        v argument is leaf value used to set the value in method
     * @param dataType yang data type
     * @return YANG namespace of corresponding given data type and value
     * @throws IllegalArgumentException if input is not valid
     */
    public static YangNamespace getValueNamespace(YangType typeInfo, String v,
                                                  YangDataTypes dataType)
            throws IllegalArgumentException {
        YangDataTypes type;
        if (dataType != null) {
            type = dataType;
        } else {
            type = typeInfo.getDataType();
        }
        switch (type) {
            case INT8:
            case UINT8:
            case INT16:
            case UINT16:
            case INT32:
            case UINT32:
            case INT64:
            case UINT64:
            case EMPTY:
            case BOOLEAN:
            case BINARY:
            case BITS:
            case ENUMERATION:
            case STRING:
            case DECIMAL64:
            case INSTANCE_IDENTIFIER:
                return null;
            case IDENTITYREF:
                YangIdentity refId = ((YangIdentityRef) typeInfo
                        .getDataTypeExtendedInfo()).getReferredIdentity();
                return getReferIdNamespace(refId, v);
            case LEAFREF:
                YangType refType = ((YangLeafRef) typeInfo
                        .getDataTypeExtendedInfo()).getEffectiveDataType();
                return getValueNamespace(refType, v, refType.getDataType());
            case DERIVED:
                // referred typedef's list of type will always has only one type
                YangType rt = ((YangDerivedInfo) typeInfo
                        .getDataTypeExtendedInfo()).getReferredTypeDef()
                        .getTypeList().get(0);
                return getValueNamespace(rt, v, rt.getDataType());
            case UNION:
                return getUnionValNamespace(typeInfo, v);
            default:
                throw new IllegalArgumentException("Unexpected data type " + type);
        }
    }

    private static YangNamespace getReferIdNamespace(YangIdentity refId, String v) {
        String baseIdentity = refId.getYangSchemaNodeIdentifier().getName();
        if (v.equals(baseIdentity)) {
            return refId.getYangSchemaNodeIdentifier().getNameSpace();
        }
        for (YangIdentity i : refId.getExtendList()) {
            String refIdentity = i.getYangSchemaNodeIdentifier().getName();
            if (v.equals(refIdentity)) {
                return i.getYangSchemaNodeIdentifier().getNameSpace();
            }
        }
        throw new IllegalArgumentException("Invalid value of data: " + v);
    }

    private static YangNamespace getUnionValNamespace(YangType type,
                                                      String leafValue) {
        Iterator<YangType<?>> it = ((YangUnion) type.getDataTypeExtendedInfo())
                .getTypeList().listIterator();
        while (it.hasNext()) {
            YangType t = it.next();
            try {
                getObject(t, leafValue, t.getDataType());
                return getValueNamespace(t, leafValue, t.getDataType());
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        throw new IllegalArgumentException("Invalid value of data: " + leafValue);
    }

    /**
     * Returns the object for given data type with respective value.
     *
     * @param type      data type of value
     * @param leafValue value
     * @return object of data type containing the value
     */
    private static Object parseUnionTypeInfo(YangType type, String leafValue) {
        Iterator<YangType<?>> it = ((YangUnion) type.getDataTypeExtendedInfo())
                .getTypeList().listIterator();
        while (it.hasNext()) {
            YangType t = it.next();
            try {
                return getObject(t, leafValue, t.getDataType());
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        throw new IllegalArgumentException("Invalid value of data: " + leafValue);
    }

    /**
     * Returns leaf type from string value.
     *
     * @param typeInfo refers to YANG type information
     * @param v        v argument is leaf value
     * @param dataType yang data type
     * @return values leaf type of corresponding given data type
     * @throws IllegalArgumentException if input is not valid
     */
    public static LeafType getLeafType(YangType typeInfo, String v,
                                       YangDataTypes dataType)
            throws IllegalArgumentException {
        YangDataTypes type;
        if (dataType != null) {
            type = dataType;
        } else {
            type = typeInfo.getDataType();
        }
        switch (type) {
            case INT8:
                return LeafType.INT8;
            case UINT8:
                return LeafType.UINT8;
            case INT16:
                return LeafType.INT16;
            case UINT16:
                return LeafType.UINT16;
            case INT32:
                return LeafType.INT32;
            case UINT32:
                return LeafType.UINT32;
            case INT64:
                return LeafType.INT64;
            case UINT64:
                return LeafType.UINT64;
            case EMPTY:
                if (v == null || v.equals("") || (v.equals(T) || v.equals(F))) {
                    return LeafType.EMPTY;
                }
                throw new IllegalArgumentException("Invalid empty value: " + v);
            case BOOLEAN:
                if (v.equals(T) || v.equals(F)) {
                    return LeafType.BOOLEAN;
                }
                throw new IllegalArgumentException("Invalid boolean value: " + v);
            case BINARY:
                return LeafType.BINARY;
            case BITS:
                return LeafType.BITS;
            case IDENTITYREF:
                return LeafType.IDENTITYREF;
            case ENUMERATION:
                return LeafType.ENUMERATION;
            case STRING:
                return LeafType.STRING;
            case INSTANCE_IDENTIFIER:
                return LeafType.INSTANCE_IDENTIFIER;
            case DECIMAL64:
                return LeafType.DECIMAL64;
            case LEAFREF:
                YangType refType = ((YangLeafRef) typeInfo
                        .getDataTypeExtendedInfo()).getEffectiveDataType();
                return getLeafType(refType, v, refType.getDataType());
            case DERIVED:
                // referred typedef's list of type will always has only one type
                YangType rt = ((YangDerivedInfo) typeInfo
                        .getDataTypeExtendedInfo()).getReferredTypeDef()
                        .getTypeList().get(0);
                return getLeafType(rt, v, rt.getDataType());
            case UNION:
                return parseUnionLeafType(typeInfo, v);
            default:
                throw new IllegalArgumentException("Unexpected data type " + type);
        }
    }

    /**
     * Returns the leaf type for given data type with respective value.
     *
     * @param type      data type of value
     * @param leafValue value
     * @return leaf type of data type containing the value
     */
    private static LeafType parseUnionLeafType(YangType type, String
            leafValue) {
        Iterator<YangType<?>> it = ((YangUnion) type.getDataTypeExtendedInfo())
                .getTypeList().listIterator();
        while (it.hasNext()) {
            YangType t = it.next();
            try {
                getObject(t, leafValue, t.getDataType());
                return getLeafType(t, leafValue, t.getDataType());
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        throw new IllegalArgumentException("Invalid value of data");
    }
}
