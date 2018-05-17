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

import com.facebook.buck.cli.BuckConfig;
import com.facebook.buck.jvm.java.CalculateAbi;
import com.facebook.buck.jvm.java.DefaultJavaLibrary;
import com.facebook.buck.jvm.java.JavaBuckConfig;
import com.facebook.buck.jvm.java.JavaOptions;
import com.facebook.buck.jvm.java.JavacOptions;
import com.facebook.buck.jvm.java.JavacOptionsAmender;
import com.facebook.buck.jvm.java.JavacOptionsFactory;
import com.facebook.buck.jvm.java.JavacToJarStepFactory;
import com.facebook.buck.jvm.java.JvmLibraryArg;
import com.facebook.buck.model.BuildTarget;
import com.facebook.buck.model.BuildTargets;
import com.facebook.buck.model.Flavor;
import com.facebook.buck.model.Flavored;
import com.facebook.buck.model.ImmutableFlavor;
import com.facebook.buck.model.UnflavoredBuildTarget;
import com.facebook.buck.parser.NoSuchBuildTargetException;
import com.facebook.buck.rules.BuildRule;
import com.facebook.buck.rules.BuildRuleParams;
import com.facebook.buck.rules.BuildRuleResolver;
import com.facebook.buck.rules.BuildRuleType;
import com.facebook.buck.rules.BuildTargetSourcePath;
import com.facebook.buck.rules.Description;
import com.facebook.buck.rules.PathSourcePath;
import com.facebook.buck.rules.SourcePath;
import com.facebook.buck.rules.SourcePathResolver;
import com.facebook.buck.rules.SourcePaths;
import com.facebook.buck.rules.TargetGraph;
import com.google.common.base.Optional;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import org.onosproject.yang.compiler.utils.UtilConstants;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.onosproject.yang.compiler.plugin.utils.PluginUtils.getValidModelId;

/**
 * Description of a Buck Yang Library.
 */
public class YangLibraryDescription
        implements Description<YangLibraryDescription.Arg>, Flavored {
    public static final BuildRuleType TYPE = BuildRuleType.of("yang_library");
    public static final Flavor SOURCES = ImmutableFlavor.of("srcs");

    private final JavacOptions defaultJavacOptions;
    private final JavaOptions defaultJavaOptions;

    public YangLibraryDescription(BuckConfig config) {
        JavaBuckConfig javaConfig = new JavaBuckConfig(config);
        defaultJavacOptions = javaConfig.getDefaultJavacOptions();
        defaultJavaOptions = javaConfig.getDefaultJavaOptions();
    }

    @Override
    public BuildRuleType getBuildRuleType() {
        return TYPE;
    }

    @Override
    public Arg createUnpopulatedConstructorArg() {
        return new Arg();
    }

    @Override
    public <A extends Arg> BuildRule createBuildRule(TargetGraph targetGraph,
                                                     BuildRuleParams params,
                                                     BuildRuleResolver resolver,
                                                     A args)
            throws NoSuchBuildTargetException {

        SourcePathResolver pathResolver = new SourcePathResolver(resolver);

        UnflavoredBuildTarget unflavoredBuildTarget =
                params.getBuildTarget().getUnflavoredBuildTarget();
        BuildRuleParams yangParams = params.copyWithBuildTarget(
                BuildTargets.createFlavoredBuildTarget(
                        unflavoredBuildTarget, SOURCES));

        BuildRule yangLib = resolver.getRuleOptional(yangParams.getBuildTarget())
                .or(() -> resolver.addToIndex(new YangLibrary(
                        yangParams, pathResolver, args.srcs,
                        getValidModelId(args.modelId.get()))));

        if (params.getBuildTarget().getFlavors().contains(SOURCES)) {
            return yangLib;
        }

        JavacOptions javacOptions = JavacOptionsFactory.create(
                defaultJavacOptions,
                params,
                resolver,
                pathResolver,
                args
        );

        // Create to main compile rule.
        BuildRuleParams javaParams = params.copyWithChanges(
                params.getBuildTarget(),
                Suppliers.ofInstance(
                        ImmutableSortedSet.<BuildRule>naturalOrder()
                                .add(yangLib)
//                                .addAll(deps)
                                //FIXME remove when we figure out compile time deps
                                .addAll((args.deps.or(ImmutableSortedSet.<BuildTarget>of()))
                                        .stream()
                                        .map(resolver::getRule)
                                        .collect(Collectors.toList()))
//                                .addAll(BuildRules.getExportedRules(deps))
                                .addAll(pathResolver.filterBuildRuleInputs(javacOptions.getInputs(pathResolver)))
                                .build()),
                Suppliers.ofInstance(ImmutableSortedSet.<BuildRule>of()));

        BuildTarget abiJarTarget = params.getBuildTarget().withAppendedFlavors(CalculateAbi.FLAVOR);

        //Add yang meta data resources to generated jar file resources.

        Path rscRoot = ((YangLibrary) yangLib).getGenSrcsDirectory();
        Path resPath = Paths.get(rscRoot + UtilConstants.SLASH + UtilConstants.YANG);

        SourcePath path = new PathSourcePath(params.getProjectFilesystem(),
                                             resPath);

        DefaultJavaLibrary library =
                resolver.addToIndex(
                        new DefaultJavaLibrary(
                                javaParams,
                                pathResolver,
                                ImmutableSet.of(SourcePaths.getToBuildTargetSourcePath().apply(yangLib)),
                                /* resources */ImmutableSet.of(path),
                                javacOptions.getGeneratedSourceFolderName(),
                                /* proguardConfig */ Optional.<SourcePath>absent(),
                                /* postprocessClassesCommands */ ImmutableList.<String>of(),
                                /* exportedDeps */ ImmutableSortedSet.<BuildRule>of(),
                                /* providedDeps */ ImmutableSortedSet.<BuildRule>of(),
                                /* abiJar */ new BuildTargetSourcePath(abiJarTarget),
                                javacOptions.trackClassUsage(),
                                /* additionalClasspathEntries */ ImmutableSet.<Path>of(),
                                new JavacToJarStepFactory(javacOptions, JavacOptionsAmender.IDENTITY),
                                /* resourcesRoot */ Optional.<Path>of(rscRoot),
                                /* manifestFile */ Optional.absent(),
                                /* mavenCoords */ Optional.<String>absent(),
                                /* tests */ ImmutableSortedSet.<BuildTarget>of(),
                                /* classesToRemoveFromJar */ ImmutableSet.<Pattern>of()));

        resolver.addToIndex(
                CalculateAbi.of(
                        abiJarTarget,
                        pathResolver,
                        params,
                        new BuildTargetSourcePath(library.getBuildTarget())));

        return library;
    }

    @Override
    public boolean hasFlavors(ImmutableSet<Flavor> flavors) {
        return flavors.isEmpty() || flavors.contains(SOURCES);
    }

    public static class Arg extends JvmLibraryArg {
        public ImmutableSortedSet<SourcePath> srcs;
        public Optional<ImmutableSortedSet<BuildTarget>> deps;
        public Optional<String> modelId;

        //TODO other params here
    }
}
