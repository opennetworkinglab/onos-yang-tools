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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.facebook.buck.io.ProjectFilesystem;
import com.facebook.buck.step.AbstractExecutionStep;
import com.facebook.buck.step.ExecutionContext;
import com.facebook.buck.step.StepExecutionResult;

/**
 * Buck build step to trigger Yang Java source file generation.
 */
public class YangStep extends AbstractExecutionStep {

    private static final String DESCRIPTION = "yang-compile";

    private final ProjectFilesystem filesystem;
    private final List<Path> srcs;
    private final Path output;

    YangStep(ProjectFilesystem filesystem,
             List<Path> srcs,
             Path genSourcesDirectory) {
        super(DESCRIPTION);
        this.filesystem = filesystem;
        this.srcs = srcs;
        this.output = genSourcesDirectory;
    }

    @Override
    public StepExecutionResult execute(ExecutionContext executionContext)
            throws IOException, InterruptedException {

        List<File> sourceFiles = srcs.stream().map(Path::toFile)
                .collect(Collectors.toList());

        try {
            new YangGenerator(sourceFiles, output.toString()).execute();
            return StepExecutionResult.SUCCESS;
        } catch(YangParsingException e) {
            executionContext.getConsole().printErrorText(e.getMessage());
            return StepExecutionResult.ERROR;
        }
    }
}
