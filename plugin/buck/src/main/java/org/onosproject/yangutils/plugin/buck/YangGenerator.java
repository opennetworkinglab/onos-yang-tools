/*
 * Copyright 2016-present Open Networking Laboratory
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

package org.onosproject.yangutils.plugin.buck;

import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.tool.CallablePlugin;
import org.onosproject.yangutils.tool.YangToolManager;
import org.onosproject.yangutils.utils.io.YangPluginConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.parseJarFile;
import static org.onosproject.yangutils.utils.UtilConstants.SLASH;
import static org.onosproject.yangutils.utils.UtilConstants.YANG_RESOURCES;

/**
 * Generates Java sources from a Yang model.
 */
public class YangGenerator implements CallablePlugin {

    private final List<File> models;
    private final List<String> depJar;
    private String outputDirectory;
    private final String DEFAULT_JAR_RES_PATH = SLASH + YANG_RESOURCES + SLASH;

    YangGenerator(List<File> models, String outputDirectory, List<String> depJar) {
        this.models = models;
        this.depJar = depJar;
        this.outputDirectory = outputDirectory + SLASH;
    }

    public void execute() throws YangParsingException {
        List<String> files = getListOfFile();
        synchronized (files) {
            try {
                YangPluginConfig config = new YangPluginConfig();
                config.setCodeGenDir(outputDirectory);
                config.resourceGenDir(outputDirectory + DEFAULT_JAR_RES_PATH);
                //for inter-jar linking.
                List<YangNode> dependentSchema = new ArrayList<>();
                for (String jar : depJar) {
                    dependentSchema.addAll(parseJarFile(jar, outputDirectory));
                }
                //intra jar file linking.
                YangToolManager manager = new YangToolManager();
                manager.compileYangFiles(manager.createYangFileInfoSet(files),
                                         dependentSchema, config, this);
            } catch (Exception e) {
                throw new YangParsingException(e);
            }
        }
    }

    private List<String> getListOfFile() {
        List<String> files = new ArrayList<>();
        if (models != null) {
            synchronized (models) {
                files.addAll(models.stream().map(File::toString)
                                     .collect(toList()));
            }
        }
        return files;
    }

    @Override
    public void addGeneratedCodeToBundle() {
        //TODO: add functionality.
    }

    @Override
    public void addCompiledSchemaToBundle() throws IOException {
        //TODO: add functionality.
    }

    @Override
    public void addYangFilesToBundle() throws IOException {
        //TODO: add functionality.
    }
}
