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
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.parseDepSchemaPath;
import static org.onosproject.yang.compiler.utils.UtilConstants.HYPHEN;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAR;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getPackageDirPathFromJavaJPackage;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents YANG plugin utilities.
 */
final class YangPluginUtils {

    private static final Logger log = getLogger(YangPluginUtils.class);

    private YangPluginUtils() {
    }

    /**
     * Adds generated source directory to the compilation root.
     *
     * @param source  directory
     * @param project current maven project
     * @param context current build context
     */
    static void addToCompilationRoot(String source, MavenProject project, BuildContext context) {
        project.addCompileSourceRoot(source);
        context.refresh(project.getBasedir());
        log.info("Source directory added to compilation root: " + source);
    }

    /**
     * Returns list of jar path.
     *
     * @param project         maven project
     * @param localRepository local repository
     * @param remoteRepos     remote repository
     * @return list of jar paths
     */
    private static List<String> resolveDependencyJarPath(
            MavenProject project, ArtifactRepository localRepository,
            List<ArtifactRepository> remoteRepos) {

        StringBuilder path = new StringBuilder();
        List<String> jarPaths = new ArrayList<>();
        for (Object obj : project.getDependencies()) {

            Dependency dependency = (Dependency) obj;
            path.append(localRepository.getBasedir());
            path.append(SLASH);
            path.append(getPackageDirPathFromJavaJPackage(dependency.getGroupId()));
            path.append(SLASH);
            path.append(dependency.getArtifactId());
            path.append(SLASH);
            path.append(dependency.getVersion());
            path.append(SLASH);
            path.append(dependency.getArtifactId() + HYPHEN + dependency.getVersion() + PERIOD + JAR);
            File jarFile = new File(path.toString());
            if (jarFile.exists()) {
                jarPaths.add(path.toString());
            }
            path.delete(0, path.length());
        }

        for (ArtifactRepository repo : remoteRepos) {
            // TODO: add resolver for remote repo.
        }
        return jarPaths;
    }

    /**
     * Resolves inter jar dependencies.
     *
     * @param project         current maven project
     * @param localRepository local maven repository
     * @param remoteRepos     list of remote repository
     * @param directory       directory for serialized files
     * @return list of resolved serialized file paths
     * @throws IOException when fails to do IO operations
     */
    static List<Path> resolveInterJarDependencies(MavenProject project,
                                                  ArtifactRepository localRepository,
                                                  List<ArtifactRepository> remoteRepos,
                                                  String directory)
            throws IOException {

        List<String> depJars = resolveDependencyJarPath(
                project, localRepository, remoteRepos);
        List<Path> serFilePaths = new ArrayList<>();
        for (String dependency : depJars) {
            // Note: when there's multiple deps, it all gets copied to
            // same directory.
            File path = parseDepSchemaPath(dependency, directory);
            if (path != null) {
                serFilePaths.add(Paths.get(path.getAbsolutePath()));
            }
        }
        return serFilePaths;
    }

    /* Adds directory to resources of project */
    static void addToProjectResource(String dir, MavenProject project) {
        Resource rsc = new Resource();
        rsc.setDirectory(dir);
        project.addResource(rsc);
    }
}
