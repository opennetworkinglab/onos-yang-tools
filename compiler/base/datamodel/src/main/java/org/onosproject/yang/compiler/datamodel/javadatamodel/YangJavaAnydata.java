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

package org.onosproject.yang.compiler.datamodel.javadatamodel;

import org.onosproject.yang.compiler.datamodel.YangAnydata;

/**
 * Represents YANG java anydata.
 */
public class YangJavaAnydata extends YangAnydata {

    private static final long serialVersionUID = 208201434L;

    protected JavaFileInfo javaFileInfo;

    /**
     * Returns java file info.
     *
     * @return java file info
     */
    public JavaFileInfo getJavaFileInfo() {
        return javaFileInfo;
    }

    @Override
    public String getJavaPackage() {
        return getJavaFileInfo().getPackage();
    }

    @Override
    public String getJavaClassNameOrBuiltInType() {
        return getJavaFileInfo().getJavaName();
    }

    @Override
    public String getJavaAttributeName() {
        return getJavaFileInfo().getJavaAttributeName();
    }
}
