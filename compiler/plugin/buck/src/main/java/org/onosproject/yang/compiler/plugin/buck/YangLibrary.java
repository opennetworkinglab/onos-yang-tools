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

package org.onosproject.yang.compiler.plugin.buck;

import com.facebook.buck.jvm.java.JarDirectoryStep;
import com.facebook.buck.model.BuildTargets;
import com.facebook.buck.rules.AbstractBuildRule;
import com.facebook.buck.rules.AddToRuleKey;
import com.facebook.buck.rules.BuildContext;
import com.facebook.buck.rules.BuildRuleParams;
import com.facebook.buck.rules.BuildableContext;
import com.facebook.buck.rules.SourcePath;
import com.facebook.buck.rules.SourcePathResolver;
import com.facebook.buck.step.Step;
import com.facebook.buck.step.fs.MakeCleanDirectoryStep;
import com.facebook.buck.step.fs.MkdirStep;
import com.facebook.buck.step.fs.RmStep;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG;

/**
 * Buck rule to define a library built form a Yang model.
 */
public class YangLibrary extends AbstractBuildRule {

    // Inject the SHA of this rule's jar into the rule key
    private static String pluginJarHash;
    static {
        URL pluginJarLocation = YangLibrary.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            DigestInputStream dis = new DigestInputStream(pluginJarLocation.openStream(), md);
            //CHECKSTYLE:OFF Consume the InputStream...
            while (dis.read() != -1);
            //CHECKSTYLE:ON
            pluginJarHash = String.format("%032x", new BigInteger(1, md.digest()));
        } catch (NoSuchAlgorithmException | IOException e) {
            System.err.println("Failed to compute hash for YangLibrary rule");
            pluginJarHash = "nil";
            //TODO consider bailing here instead
        }
    }
    @AddToRuleKey
    private final String ruleVersion = pluginJarHash;

    // src path to yang files
    @AddToRuleKey
    private final ImmutableSortedSet<SourcePath> srcs;

    private final BuildRuleParams params;

    private final Path genSrcsDirectory;
    private final Path outputDirectory;
    private final Path output;

    @AddToRuleKey
    private final String modelId;

    public YangLibrary(
            BuildRuleParams params,
            SourcePathResolver resolver,
            ImmutableSortedSet<SourcePath> srcs,
            String id) {
        super(params, resolver);
        this.srcs = srcs;
        this.params = params;
        modelId = id;
        genSrcsDirectory = BuildTargets.getGenPath(getProjectFilesystem(),
                                                   params.getBuildTarget(),
                                                   "%s__yang-gen");
        outputDirectory = BuildTargets.getGenPath(getProjectFilesystem(),
                                                  params.getBuildTarget(),
                                                  "%s__yang-output");
        output = Paths.get(String.format("%s/%s-sources.jar",
                                         outputDirectory,
                                         params.getBuildTarget().getShortNameAndFlavorPostfix()));
    }

    @Override
    public ImmutableList<Step> getBuildSteps(BuildContext buildContext, BuildableContext buildableContext) {
        ImmutableList.Builder<Step> steps = ImmutableList.builder();

        // Delete the old output for this rule, if it exists.
        steps.add(
                new RmStep(
                        getProjectFilesystem(),
                        getPathToOutput(),
                        /* shouldForceDeletion */ true,
                        /* shouldRecurse */ true));

        // Make sure that the directory to contain the output file exists. Rules get output to a
        // directory named after the base path, so we don't want to nuke the entire directory.
        steps.add(new MkdirStep(getProjectFilesystem(), outputDirectory));

        steps.add(new MakeCleanDirectoryStep(getProjectFilesystem(), genSrcsDirectory));

        List<Path> sourcePaths = srcs.stream()
                .map(s -> getResolver().getRelativePath(s))
                .collect(Collectors.toList());

        steps.add(new YangStep(getProjectFilesystem(), sourcePaths, genSrcsDirectory,
                               params.getDeps(), modelId));

        steps.add(new JarDirectoryStep(
                getProjectFilesystem(),
                output,
                ImmutableSortedSet.of(genSrcsDirectory),
                null,
                null));

        return steps.build();
    }

    @Nullable
    @Override
    public Path getPathToOutput() {
        return output;
    }


    /**
     * Returns generated sources directory.
     *
     * @return generated sources directory
     */
    public Path getGenSrcsDirectory() {
        synchronized (YangLibrary.class) {
            File dir = new File(genSrcsDirectory.toString() + SLASH + YANG);
            dir.mkdirs();
            return genSrcsDirectory;
        }
    }
}
