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
 * Represents cached java file handle, which supports the addition of member attributes and
 * methods.
 */
public class JavaFileInfo
        implements Serializable {

    private static final long serialVersionUID = 806102633L;

    /**
     * Name of the module.
     */
    protected String javaName;

    /**
     * Java package of the mapped java class.
     */
    protected String pkg;

    /**
     * Java attribute name.
     */
    protected String javaAttributeName;

    /**
     * Returns the java name of the node.
     *
     * @return the java name of node
     */
    public String getJavaName() {
        return javaName;
    }

    /**
     * Sets the java name of the node.
     *
     * @param name the java name of node
     */
    public void setJavaName(String name) {
        javaName = name;
    }

    /**
     * Returns the mapped java package.
     *
     * @return the java package
     */
    public String getPackage() {
        return pkg;
    }

    /**
     * Sets the node's package.
     *
     * @param nodePackage node's package
     */
    public void setPackage(String nodePackage) {
        pkg = nodePackage;
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
     * Assign the Java attribute Name.
     *
     * @param javaAttributeName Java attribute name
     */
    public void setJavaAttributeName(String javaAttributeName) {
        this.javaAttributeName = javaAttributeName;
    }

}
