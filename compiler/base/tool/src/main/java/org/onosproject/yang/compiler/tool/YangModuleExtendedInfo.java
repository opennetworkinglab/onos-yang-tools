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

package org.onosproject.yang.compiler.tool;

import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.model.DefaultYangModule;
import org.onosproject.yang.model.YangModuleId;

import java.io.File;
import java.io.Serializable;

/**
 * Represents extended info for YANG module.
 */
public class YangModuleExtendedInfo extends DefaultYangModule implements Serializable {

    private YangNode schema;

    /**
     * Creates an instance of YANG module extended information.
     *
     * @param id       module id
     * @param yangSrc  YANG resource path
     * @param metadata meta data resource path
     * @param interJar flag indicating if module is from inter-jar
     */
    public YangModuleExtendedInfo(YangModuleId id, File yangSrc, File metadata,
                                  boolean interJar) {
        super(id, yangSrc, metadata, interJar);
    }

    /**
     * Creates an instance of YANG module extended information.
     *
     * @param id       module id
     * @param yangSrc  YANG resource path
     * @param metadata meta data resource path
     */
    public YangModuleExtendedInfo(YangModuleId id, File yangSrc, File metadata) {
        super(id, yangSrc, metadata);
    }

    /**
     * Returns schema info for module.
     *
     * @return schema info for module
     */
    public YangNode getSchema() {
        return schema;
    }

    /**
     * Sets  schema info for module.
     *
     * @param schema schema info for module
     */
    public void setSchema(YangNode schema) {
        this.schema = schema;
    }
}
