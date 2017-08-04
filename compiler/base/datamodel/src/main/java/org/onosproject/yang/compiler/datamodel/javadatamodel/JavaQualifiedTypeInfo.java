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

import java.io.Serializable;

/**
 * Represents the information about individual imports in the generated file.
 */
public class JavaQualifiedTypeInfo
        implements Serializable {

    private static final long serialVersionUID = 806201634L;

    /**
     * Package location where the imported class/interface is defined.
     */
    protected String pkgInfo;

    /**
     * Class/interface being referenced.
     */
    protected String classInfo;

    /**
     * attribute name being used.
     */
    protected String javaAttributeName;
    /**
     * Returns the imported package info.
     *
     * @return the imported package info
     */
    public String getPkgInfo() {
        return pkgInfo;
    }

    /**
     * Sets the imported package info.
     *
     * @param pkgInfo the imported package info
     */
    public void setPkgInfo(String pkgInfo) {
        this.pkgInfo = pkgInfo;
    }

    /**
     * Returns the imported class/interface info.
     *
     * @return the imported class/interface info
     */
    public String getClassInfo() {
        return classInfo;
    }

    /**
     * Sets the imported class/interface info.
     *
     * @param classInfo the imported class/interface info
     */
    public void setClassInfo(String classInfo) {
        this.classInfo = classInfo;
    }

    /**
     * Retrieve Java attribute name.
     *
     * @return Java attribute name
     */
    public String getJavaAttributeName() {
        return javaAttributeName;
    }

    /**
     * Assign Java attribute name.
     *
     * @param javaAttributeName Java attribute name
     */
    public void setJavaAttributeName(String javaAttributeName) {
        this.javaAttributeName = javaAttributeName;
    }
}
