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

package org.onosproject.yangutils.plugin.manager;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.onosproject.yangutils.datamodel.YangNode;
import org.slf4j.Logger;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.parseJarFile;
import static org.onosproject.yangutils.tool.YangToolManager.DEFAULT_JAR_RES_PATH;
import static org.onosproject.yangutils.utils.UtilConstants.COLON;
import static org.onosproject.yangutils.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.HYPHEN;
import static org.onosproject.yangutils.utils.UtilConstants.JAR;
import static org.onosproject.yangutils.utils.UtilConstants.PERIOD;
import static org.onosproject.yangutils.utils.UtilConstants.SLASH;
import static org.onosproject.yangutils.utils.UtilConstants.TEMP;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getPackageDirPathFromJavaJPackage;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents YANG plugin utilities.
 */
public final class YangPluginUtils {

    private static final Logger log = getLogger(YangPluginUtils.class);

    private static final String TEXT_FILE_EXTENSION = ".txt";
    private static final String VERSION_META_DATA = "VersionMetaData";
    private static final String PLUGIN_ARTIFACT = "onos-yang-maven-plugin";

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
     * Copies YANG files to the current project's output directory.
     *
     * @param outputDir project's output directory
     * @param project   maven project
     * @throws IOException when fails to copy files to destination resource directory
     */
    static void copyYangFilesToTarget(String outputDir, MavenProject project)
            throws IOException {

        addToProjectResource(outputDir + SLASH + TEMP + SLASH, project);
    }

    /**
     * Serializes data-model.
     *
     * @param directory base directory for serialized files
     * @param project   maven project
     * @param operation true if need to add to resource
     * @throws IOException when fails to do IO operations
     */
    public static void serializeDataModel(String directory,
                                          MavenProject project,
                                          boolean operation)
            throws IOException {

        String serFileDirPath = directory + DEFAULT_JAR_RES_PATH;
        File dir = new File(serFileDirPath);
        dir.mkdirs();

        if (operation) {
            addToProjectResource(directory + SLASH + TEMP + SLASH, project);
        }

        if (operation) {
            addVersionMetaDataFile(project, serFileDirPath);
        }
    }

    /**
     * Adds version meta data files for YSR to know version of YANG tools.
     *
     * @param project maven project
     * @param dir     directory
     * @throws IOException when fails to do IO operations
     */
    private static void addVersionMetaDataFile(MavenProject project, String dir)
            throws IOException {
        List<Plugin> plugins = project.getBuildPlugins();
        Iterator<Plugin> it = plugins.iterator();
        Plugin plugin = it.next();
        String data = EMPTY_STRING;
        while (it.hasNext()) {
            if (plugin.getArtifactId().equals(PLUGIN_ARTIFACT)) {
                data = plugin.getGroupId() + COLON + plugin.getArtifactId()
                        + COLON + plugin.getVersion();
            }
            plugin = it.next();
        }
        if (data.equals(EMPTY_STRING)) {
            throw new IOException("Invalid artifact for " + PLUGIN_ARTIFACT);
        }
        String verFileName = dir + VERSION_META_DATA + TEXT_FILE_EXTENSION;
        PrintWriter out = new PrintWriter(verFileName);
        out.print(data);
        out.close();
    }

    /**
     * Returns list of jar path.
     *
     * @param project         maven project
     * @param localRepository local repository
     * @param remoteRepos     remote repository
     * @return list of jar paths
     */
    private static List<String> resolveDependencyJarPath(MavenProject project, ArtifactRepository localRepository,
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
     * @return list of resolved datamodel nodes
     * @throws IOException when fails to do IO operations
     */
    static List<YangNode> resolveInterJarDependencies(MavenProject project, ArtifactRepository localRepository,
                                                      List<ArtifactRepository> remoteRepos, String directory)
            throws IOException {

        List<String> dependenciesJarPaths = resolveDependencyJarPath(project, localRepository, remoteRepos);
        List<YangNode> resolvedDataModelNodes = new ArrayList<>();
        for (String dependency : dependenciesJarPaths) {
            resolvedDataModelNodes.addAll(parseJarFile(dependency, directory));
        }
        return resolvedDataModelNodes;
    }

    /* Adds directory to resources of project */
    private static void addToProjectResource(String dir, MavenProject project) {
        Resource rsc = new Resource();
        rsc.setDirectory(dir);
        project.addResource(rsc);
    }
}
