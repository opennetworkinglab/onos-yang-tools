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

package org.onosproject.yang.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents default YANG module.
 */
public class DefaultYangModule implements YangModule, Serializable {

    private YangModuleId id;
    private File yangSrc;
    private File metadata;
    private boolean interJar;

    /**
     * Creates an instance of default YANG module.
     *
     * @param id       YANG module id
     * @param yangSrc  YANG source file path
     * @param metadata YANG metadata source file path
     */
    public DefaultYangModule(YangModuleId id, File yangSrc, File metadata) {
        checkNotNull(yangSrc);
        checkNotNull(metadata);
        checkNotNull(id);
        this.id = id;
        this.yangSrc = yangSrc;
        this.metadata = metadata;
    }

    /**
     * Creates an instance of default YANG module.
     *
     * @param id       YANG module id
     * @param yangSrc  YANG source file path
     * @param metadata YANG metadata source file path
     * @param interJar is this module is in dependent jar
     */
    public DefaultYangModule(YangModuleId id, File yangSrc, File metadata,
                             boolean interJar) {
        checkNotNull(yangSrc);
        checkNotNull(metadata);
        checkNotNull(id);
        this.id = id;
        this.yangSrc = yangSrc;
        this.metadata = metadata;
        this.interJar = interJar;
    }

    @Override
    public YangModuleId getYangModuleId() {
        return id;
    }

    @Override
    public InputStream getYangSource() {
        try {
            return new FileInputStream(yangSrc);
        } catch (FileNotFoundException e) {
            throw new ModelException("Yang source file not found." + yangSrc);
        }
    }

    @Override
    public InputStream getMetadata() {
        try {
            return new FileInputStream(metadata);
        } catch (FileNotFoundException e) {
            throw new ModelException("metadata source file not found." +
                                             metadata);
        }
    }

    /**
     * Returns true if it's inter-jar node.
     *
     * @return true if inter-jar node, false otherwise
     */
    public boolean isInterJar() {
        return interJar;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, yangSrc, metadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        DefaultYangModule that = (DefaultYangModule) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(yangSrc, that.yangSrc) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("moduleId", id)
                .add("yangSource", yangSrc)
                .add("yangMetadata", metadata)
                .toString();
    }
}
