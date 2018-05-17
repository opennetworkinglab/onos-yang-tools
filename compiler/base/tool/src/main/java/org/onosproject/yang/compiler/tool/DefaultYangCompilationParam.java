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

import org.onosproject.yang.compiler.api.YangCompilationParam;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Collections.unmodifiableSet;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents default YANG compilation parameter.
 */
public final class DefaultYangCompilationParam implements YangCompilationParam {

    private static final Logger log = getLogger(DefaultYangCompilationParam.class);

    private final Set<Path> yangFiles;
    private final Set<Path> dependentSchemas;
    private Path codeGenDir;
    private Path metaDataPath;
    private String modelId;

    /**
     * Creates an instance of YANG compilation parameter.
     *
     * @param files    yang files path
     * @param schemas  dependent schema path
     * @param metaPath metadata path
     * @param id       model id
     * @param path     generated code directory path
     */
    private DefaultYangCompilationParam(Set<Path> files, Set<Path>
            schemas, Path metaPath, String id, Path path) {
        yangFiles = unmodifiableSet(files);
        dependentSchemas = unmodifiableSet(schemas);
        modelId = id;
        codeGenDir = path;
        metaDataPath = metaPath;
    }

    @Override
    public Set<Path> getYangFiles() {
        return yangFiles;
    }

    @Override
    public Set<Path> getDependentSchemas() {
        return dependentSchemas;
    }

    @Override
    public Path getCodeGenDir() {
        return codeGenDir;
    }

    @Override
    public Path getMetadataGenDir() {
        return metaDataPath;
    }

    @Override
    public String getModelId() {
        return modelId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(yangFiles, dependentSchemas, codeGenDir,
                            metaDataPath, modelId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        DefaultYangCompilationParam that = (DefaultYangCompilationParam) obj;
        return yangFiles.size() == that.yangFiles.size() &&
                dependentSchemas.size() == that.dependentSchemas.size() &&
                yangFiles.containsAll(that.yangFiles) &&
                dependentSchemas.containsAll(that.dependentSchemas) &&
                Objects.equals(codeGenDir, that.codeGenDir) &&
                Objects.equals(metaDataPath, that.metaDataPath) &&
                Objects.equals(modelId, that.modelId);
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("yangFilePath", yangFiles)
                .add("dependentSchemas", dependentSchemas)
                .add("codeGenDir", codeGenDir)
                .add("metaDataPath", metaDataPath)
                .add("modelId", modelId)
                .toString();
    }

    /**
     * Retrieves a new YANG compilation param builder.
     *
     * @return YANG compilation param builder
     */
    public static DefaultYangCompilationParam.Builder builder() {
        return new DefaultYangCompilationParam.Builder();
    }

    /**
     * Builder to construct compilation param.
     */
    public static class Builder {
        private final Set<Path> yangFiles;
        private final Set<Path> dependentSchemas;
        private Path codeGenDir;
        private Path metaDataPath;
        private String modelId;

        /**
         * Creates an instance of YANG compilation parameter builder.
         */
        public Builder() {
            yangFiles = new LinkedHashSet<>();
            dependentSchemas = new LinkedHashSet<>();
        }

        /**
         * Sets metadata generation directory.
         *
         * @param path expected metadata generation directory
         * @return updated builder with metadata generated schema
         */
        public Builder setMetadataGenDir(Path path) {
            metaDataPath = path;
            return this;
        }

        /**
         * Sets yang model identifier.
         *
         * @param id model identifier
         * @return updated builder with YANG model id
         * instance
         */
        public Builder setModelId(String id) {
            modelId = id;
            return this;
        }

        /**
         * Sets code generation directory.
         *
         * @param path expected code generation directory
         * @return updated builder with generated code path value instance
         */
        public Builder setCodeGenDir(Path path) {
            codeGenDir = path;
            return this;
        }

        /**
         * Adds dependent metadata path.
         *
         * @param path metadata path
         * @return updated builder with dependent schema path value instance
         */
        public Builder addDependentSchema(Path path) {
            dependentSchemas.add(path);
            return this;
        }

        /**
         * Adds YANG file path. This could be a directory in which YANG files are
         * available.
         *
         * @param path YANG file path
         * @return updated builder with YANG files path
         */
        public Builder addYangFile(Path path) {
            yangFiles.add(path);
            return this;
        }

        /**
         * Builds a YangCompilationParam.
         *
         * @return built YangCompilationParam
         */
        public DefaultYangCompilationParam build() {
            return new DefaultYangCompilationParam(
                    yangFiles, dependentSchemas, metaDataPath, modelId,
                    codeGenDir);
        }
    }
}
