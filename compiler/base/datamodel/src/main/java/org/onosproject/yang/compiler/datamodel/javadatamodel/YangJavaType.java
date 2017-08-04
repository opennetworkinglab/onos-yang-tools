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

package org.onosproject.yang.compiler.datamodel.javadatamodel;

import org.onosproject.yang.compiler.datamodel.YangType;

/**
 * Represents YANG java type.
 */
public class YangJavaType extends YangType implements JavaQualifiedTypeInfoContainer {

    private static final long serialVersionUID = 19082016001L;
    protected JavaQualifiedTypeInfo javaQualifiedTypeInfo;

    /**
     * Returns java qualified type info.
     *
     * @return java qualified type info
     */

    @Override
    public JavaQualifiedTypeInfo getJavaQualifiedInfo() {
        return javaQualifiedTypeInfo;
    }

    @Override
    public void setJavaQualifiedInfo(JavaQualifiedTypeInfo typeInfo) {
        this.javaQualifiedTypeInfo = typeInfo;
    }
}
