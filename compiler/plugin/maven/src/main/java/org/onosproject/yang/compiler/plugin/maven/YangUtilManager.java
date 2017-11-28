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

package org.onosproject.yang.compiler.plugin.maven;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.rtinfo.RuntimeInformation;
import org.onosproject.yang.compiler.api.YangCompiledOutput;
import org.onosproject.yang.compiler.api.YangCompilerException;
import org.onosproject.yang.compiler.api.YangCompilerService;
import org.onosproject.yang.compiler.tool.DefaultYangCompilationParam;
import org.onosproject.yang.compiler.tool.YangCompilerManager;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE;
import static org.onosproject.yang.compiler.plugin.maven.YangPluginUtils.addToCompilationRoot;
import static org.onosproject.yang.compiler.plugin.maven.YangPluginUtils.addToProjectResource;
import static org.onosproject.yang.compiler.plugin.maven.YangPluginUtils.resolveInterJarDependencies;
import static org.onosproject.yang.compiler.plugin.utils.PluginUtils.getValidModelId;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_BASE_PKG;
import static org.onosproject.yang.compiler.utils.UtilConstants.DEFAULT_JAR_RES_PATH;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.IN;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH;
import static org.onosproject.yang.compiler.utils.UtilConstants.TEMP;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_RESOURCES;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getYangFiles;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.deleteDirectory;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getDirectory;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getPackageDirPathFromJavaJPackage;

/**
 * Represents ONOS YANG utility maven plugin.
 * Goal of plugin is yang2java.
 * Execution phase is generate-sources.
 * requiresDependencyResolution at compile time.
 */
@Mojo(name = "yang2java", defaultPhase = PROCESS_SOURCES,
        requiresDependencyResolution = COMPILE)
public class YangUtilManager extends AbstractMojo {

    private String codeGenDir;
    private YangCompiledOutput output;

    /**
     * Source directory for YANG files.
     */
    @Parameter(property = "yangFilesDir", defaultValue = "src/main/yang/")
    private String yangFilesDir;

    /**
     * Source directory for generated files.
     */
    @Parameter(property = "classFileDir", defaultValue =
            "target/generated-sources/")
    private String classFileDir;

    /**
     * YANG Model id.
     */
    @Parameter(property = "modelId")
    private String modelId;

    /**
     * Base directory for project.
     */
    @Parameter(property = "basedir", defaultValue = "${basedir}")
    private String baseDir;

    /**
     * Output directory.
     */
    @Parameter(property = "project.build.outputDirectory", required = true,
            defaultValue = "target/classes")
    private String outputDirectory;

    /**
     * Current maven project.
     */
    @Parameter(property = "project", required = true, readonly = true,
            defaultValue = "${project}")
    private MavenProject project;

    /**
     * Local maven repository.
     */
    @Parameter(readonly = true, defaultValue = "${localRepository}")
    private ArtifactRepository localRepository;

    /**
     * Remote maven repositories.
     */
    @Parameter(readonly = true, defaultValue = "${project.remoteArtifactRepositories}")
    private List<ArtifactRepository> remoteRepository;

    /**
     * Build context.
     */
    @Component
    private BuildContext context;

    /**
     * The Runtime information for the current instance of Maven.
     */
    @Component
    private RuntimeInformation runtime;

    /**
     * The name of the property in which to store the version of Maven.
     */
    @Parameter(defaultValue = "maven.version")
    private String versionProperty;

    @Override
    public void execute()
            throws MojoExecutionException, MojoFailureException {

        String metaDataGenDir;
        String outputDir;
        try {
            /*
             * For deleting the generated code in previous build.
             */
            outputDir = getDirectory(baseDir, outputDirectory);
            deleteDirectory(outputDir + SLASH + TEMP);
            deleteDirectory(outputDir + SLASH + YANG_RESOURCES);
            String searchDir = getDirectory(baseDir, yangFilesDir);

            //Get the code gen directory.
            codeGenDir = getDirectory(baseDir, classFileDir) + SLASH;
            //Get the meta data gen directory.
            metaDataGenDir = outputDir + SLASH + DEFAULT_JAR_RES_PATH;

            //Yang compiler service.
            YangCompilerService compiler = new YangCompilerManager();

            //Need to get dependent schema paths to give inter jar dependencies.
            List<Path> depSchemas = resolveInterJarDependencies(
                    project, localRepository, remoteRepository, outputDir);

            //Create compiler param.
            DefaultYangCompilationParam.Builder bldr =
                    DefaultYangCompilationParam.builder();

            bldr.setCodeGenDir(Paths.get(codeGenDir));
            bldr.setMetadataGenDir(Paths.get(metaDataGenDir));

            for (Path path : depSchemas) {
                bldr.addDependentSchema(path);
            }

            for (String file : getYangFiles(searchDir)) {
                bldr.addYangFile(Paths.get(file));
            }

            if (modelId != null) {
                bldr.setModelId(getValidModelId(modelId));
            } else {
                bldr.setModelId(project.getArtifactId());
            }

            //Compile yang files and generate java code.
            output = compiler.compileYangFiles(bldr.build());

            addToCompilationRoot(codeGenDir, project, context);
            addToProjectResource(outputDir + SLASH + TEMP + SLASH, project);
        } catch (YangCompilerException e) {
            String fileName = EMPTY_STRING;
            if (e.getYangFile() != null) {
                fileName = e.getYangFile().toString();
            }
            try {
                deleteDirectory(codeGenDir + getPackageDirPathFromJavaJPackage(
                        DEFAULT_BASE_PKG));
            } catch (IOException ex) {
                throw new MojoExecutionException(
                        "Error handler failed to delete files for data model node.");
            }
            getLog().info(e.getCause());
            throw new MojoExecutionException(
                    "Exception occurred due to " + e.getLocalizedMessage() +
                            IN + fileName + " YANG file.");
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Failed to process files");
        }
    }
}
