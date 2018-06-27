/*
 * Copyright 2018-present Open Networking Laboratory
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

package org.onosproject.yang.compiler.main;

import com.google.common.collect.ImmutableList;
import org.onosproject.yang.compiler.tool.DefaultYangCompilationParam;
import org.onosproject.yang.compiler.tool.YangCompilerManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.parseDepSchemaPath;

/**
 * Provides Java binary to invoke the YANG compiler as an executable.
 */
public final class YangCompilerMain {

    private static final String USAGE =
            "usage: main modelId outputDir [-d dependencyJar1 -d dependencyJar2...] " +
                    "inputYangFile1 inputYangFile2...";

    private static final String DEPENDENCY_FLAG = "-d";
    private static final String STDIN_FLAG = "-";

    // No use creating an instance
    private YangCompilerMain() {
    }

    private static void usage() {
        System.err.println(USAGE);
        System.exit(1);
    }

    /**
     * Main executable entry point.
     * <p>
     * usage: main modelId outputDir [-d dependencyJar1 -d dependencyJar2...] \
     *                      inputYangFile1 inputYangFile2...
     *
     * @param args see usage above
     * @throws IOException if issues arise when writing out generated classes
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            usage();
        }

        ImmutableList.Builder<Path> yangFiles = ImmutableList.builder();
        ImmutableList.Builder<Path> depJars = ImmutableList.builder();

        // Scan tail arguments and separate dependencies from input sources
        for (int i = 2; i < args.length; i++) {
            if (DEPENDENCY_FLAG.equals(args[i])) {
                i++;
                if (i >= args.length) {
                    usage();
                }
                depJars.add(new File(args[i]).toPath());
            } else if (STDIN_FLAG.equals(args[i])) {
                if (i != args.length - 1) {
                    usage();
                }
                try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
                    String line;
                    while ((line = input.readLine()) != null) {
                        yangFiles.add(new File(line).toPath());
                    }
                }

            } else {
                yangFiles.add(new File(args[i]).toPath());
            }
        }

        runYangCompiler(args[0], args[1], depJars.build(), yangFiles.build());
    }

    // Runs the YANG compiler on the YANG sources in the specified directory.
    private static void runYangCompiler(String modelId, String outputDir,
                                        List<Path> depJars, List<Path> yangFiles)
            throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Unable to create output directory");
        }

        // Prepare the compilation parameter
        DefaultYangCompilationParam.Builder param = DefaultYangCompilationParam.builder()
                .setCodeGenDir(new File(outputDir, "src").toPath())
                .setMetadataGenDir(new File(outputDir, "schema").toPath())
                .setModelId(modelId);

        for (Path d : depJars) {
            File jar = parseDepSchemaPath(d.toString(), outputDir);
            if (jar != null) {
                param.addDependentSchema(jar.toPath());
            }
        }

        // Enumerate all input YANG source files.
        yangFiles.forEach(param::addYangFile);

        // Run the YANG compiler and collect the results
        new YangCompilerManager().compileYangFiles(param.build());
    }
}
