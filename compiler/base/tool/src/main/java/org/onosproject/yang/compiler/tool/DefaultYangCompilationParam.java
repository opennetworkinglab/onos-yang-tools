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

package org.onosproject.yang.compiler.tool;

import org.onosproject.yang.compiler.api.YangCompilationParam;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Represents default YANG compilation parameter.
 */
public class DefaultYangCompilationParam implements YangCompilationParam {

    private final Set<Path> yangFiles;
    private final Set<Path> dependentSchemas;
    private Path codeGenDir;
    private Path metaDataPath;

    /**
     * Creates an instance of YANG compilation parameter.
     */
    public DefaultYangCompilationParam() {
        yangFiles = new LinkedHashSet<>();
        dependentSchemas = new LinkedHashSet<>();
    }

    @Override
    public Set<Path> getYangFiles() {
        return yangFiles;
    }

    @Override
    public void addYangFile(Path path) {
        yangFiles.add(path);
    }

    @Override
    public Set<Path> getDependentSchemas() {
        return dependentSchemas;
    }

    @Override
    public void addDependentSchema(Path path) {
        dependentSchemas.add(path);
    }

    @Override
    public Path getCodeGenDir() {
        return codeGenDir;
    }

    @Override
    public void setCodeGenDir(Path path) {
        codeGenDir = path;
    }

    @Override
    public Path getMetadataGenDir() {
        return metaDataPath;
    }

    @Override
    public void setMetadataGenDir(Path path) {
        metaDataPath = path;
    }

    @Override
    public int hashCode() {
        return Objects.hash(yangFiles, dependentSchemas, codeGenDir,
                            metaDataPath);
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
                Objects.equals(metaDataPath, that.metaDataPath);
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("yangFilePath", yangFiles)
                .add("dependentSchemas", dependentSchemas)
                .add("codeGenDir", codeGenDir)
                .add("metaDataPath", metaDataPath)
                .toString();
    }
}
