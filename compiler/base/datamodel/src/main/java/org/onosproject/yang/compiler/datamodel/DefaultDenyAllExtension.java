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
package org.onosproject.yang.compiler.datamodel;

/**
 * Methods related to the extensions defined in ietf-netconf-acm.yang RFC 6536.
 * <p>
 * RFC 6536 defines two extensions default-deny-write and default-deny-all
 * that can be used to mark exceptions for data nodes to the standard access
 * control exceptions.
 * There is no attribute to this extension and so has been represented as a
 * boolean flag
 */
public interface DefaultDenyAllExtension {
    /**
     * Returns the defaultDenyAll.
     *
     * @return the defaultDenyAll
     */
    public boolean getDefaultDenyAll();

    /**
     * Sets the defaultDenyAll.
     *
     * @param defaultDenyAll the defaultDenyAll value
     */
    public void setDefaultDenyAll(boolean defaultDenyAll);
}
