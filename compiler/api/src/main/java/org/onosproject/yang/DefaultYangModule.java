/*
 * Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang;

import org.onosproject.yang.compiler.api.YangCompilerException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents default YANG module.
 */
public class DefaultYangModule implements YangModule {

    private YangModuleId id;
    private Path yangSrc;
    private Path metadata;

    /**
     * Creates an instance of default YANG module.
     *
     * @param id       YANG module id
     * @param yangSrc  YANG source file path
     * @param metadata YANG metadata source file path
     */
    public DefaultYangModule(YangModuleId id, Path yangSrc, Path metadata) {
        checkNotNull(yangSrc);
        checkNotNull(metadata);
        checkNotNull(id);
        this.id = id;
        this.yangSrc = yangSrc;
        this.metadata = metadata;
    }

    @Override
    public YangModuleId getYangModuleId() {
        return id;
    }

    @Override
    public InputStream getYangSource() {
        try {
            return new FileInputStream(yangSrc.toString());
        } catch (FileNotFoundException e) {
            throw new YangCompilerException("Yang source file not found." +
                                                    yangSrc);
        }
    }

    @Override
    public InputStream getMetadata() {
        try {
            return new FileInputStream(metadata.toString());
        } catch (FileNotFoundException e) {
            throw new YangCompilerException("metadata source file not found." +
                                                    metadata);
        }
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
