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

package org.onosproject.yang.compiler.datamodel;

import org.onosproject.yang.model.YangNamespace;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents YANG data node identifier which is a combination of name and namespace.
 * DefaultYangNamespace will be present only if node is module/sub-module or augmented node.
 */
public class YangSchemaNodeIdentifier extends DefaultLocationInfo
        implements Serializable, Cloneable {

    private static final long serialVersionUID = 806201648L;

    // Name of YANG data node.
    private String name;

    // DefaultYangNamespace of YANG data node.
    private YangNamespace namespace;

    /**
     * Creates an instance of YANG data node identifier.
     */
    public YangSchemaNodeIdentifier() {
    }

    /**
     * Returns the name of the node.
     *
     * @return name of the node
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the node.
     *
     * @param name name of the node
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns namespace of the node.
     *
     * @return namespace of the node
     */
    public YangNamespace getNameSpace() {
        return namespace;
    }

    /**
     * Sets namespace of the node.
     *
     * @param namespace namespace of the node
     */
    public void setNameSpace(YangNamespace namespace) {
        this.namespace = namespace;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj instanceof YangSchemaNodeIdentifier) {
            YangSchemaNodeIdentifier other = (YangSchemaNodeIdentifier) obj;

            if (!Objects.equals(name, other.name)) {
                return false;
            }
            String name = namespace.getModuleName();
            String otherName = other.getNameSpace().getModuleName();
            if (name != null && otherName != null) {
                if (namespace.getModuleName()
                        .equals(other.getNameSpace().getModuleName())) {
                    return true;
                }

            }
            String nSpace = namespace.getModuleNamespace();
            String otherNspace = other.getNameSpace().getModuleNamespace();
            if (nSpace != null && otherNspace != null) {
                if (nSpace.equals(otherNspace)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public YangSchemaNodeIdentifier clone() throws CloneNotSupportedException {
        return (YangSchemaNodeIdentifier) super.clone();
    }

    @Override
    public int hashCode() {
        // if one of moduleName or moduleNamespace is null,
        // it is not used for comparison, even if the other one is non-null
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("name", name)
                .add("moduleName",
                     Optional.ofNullable(namespace).map(YangNamespace::getModuleName).orElse(null))
                .add("moduleNamespace",
                     Optional.ofNullable(namespace).map(YangNamespace::getModuleNamespace).orElse(null))
                .toString();
    }
}
