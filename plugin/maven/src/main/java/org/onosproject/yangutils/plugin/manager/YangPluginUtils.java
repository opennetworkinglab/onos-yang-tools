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
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.onosproject.yangutils.datamodel.YangNode;
import org.slf4j.Logger;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.parseJarFile;
import static org.onosproject.yangutils.utils.UtilConstants.HYPHEN;
import static org.onosproject.yangutils.utils.UtilConstants.JAR;
import static org.onosproject.yangutils.utils.UtilConstants.PERIOD;
import static org.onosproject.yangutils.utils.UtilConstants.SLASH;
import static org.onosproject.yangutils.utils.UtilConstants.TEMP;
import static org.onosproject.yangutils.utils.UtilConstants.YANG_RESOURCES;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getPackageDirPathFromJavaJPackage;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents YANG plugin utilities.
 */
public final class YangPluginUtils {

    private static final Logger log = getLogger(YangPluginUtils.class);
    private static final String TARGET_RESOURCE_PATH = SLASH + TEMP + SLASH +
            YANG_RESOURCES + SLASH;
    private static final String SERIALIZED_FILE_EXTENSION = ".ser";
    private static final String YANG_META_DATA = "YangMetaData";

    private YangPluginUtils() {
    }

    /**
     * Adds generated source directory to the compilation root.
     *
     * @param source  directory
     * @param project current maven project
     * @param context current build context
     */
    static void addToCompilationRoot(String source, MavenProject project,
                                     BuildContext context) {
        project.addCompileSourceRoot(source);
        context.refresh(project.getBasedir());
        log.info("Source directory added to compilation root: " + source);
    }

    /**
     * Copies YANG files to the current project's output directory.
     *
     * @param yangFileInfo list of YANG files
     * @param outputDir    project's output directory
     * @param project      maven project
     * @throws IOException when fails to copy files to destination resource directory
     */
    static void copyYangFilesToTarget(Set<YangFileInfo> yangFileInfo,
                                      String outputDir, MavenProject project)
            throws IOException {

        List<File> files = getListOfFile(yangFileInfo);
        String path = outputDir + TARGET_RESOURCE_PATH;
        File targetDir = new File(path);
        targetDir.mkdirs();
        for (File file : files) {
            Files.copy(file.toPath(),
                       new File(path + file.getName()).toPath(),
                       StandardCopyOption.REPLACE_EXISTING);
        }
        addToProjectResource(outputDir + SLASH + TEMP + SLASH, project);
    }

    /**
     * Provides a list of files from list of strings.
     *
     * @param yangFileInfo set of yang file information
     * @return list of files
     */
    private static List<File> getListOfFile(Set<YangFileInfo> yangFileInfo) {
        List<File> files = new ArrayList<>();
        Iterator<YangFileInfo> yangFileIterator = yangFileInfo.iterator();
        while (yangFileIterator.hasNext()) {
            YangFileInfo yangFile = yangFileIterator.next();
            if (yangFile.isForTranslator()) {
                files.add(new File(yangFile.getYangFileName()));
            }
        }
        return files;
    }

    /**
     * Serializes data-model.
     *
     * @param dir       base directory for serialized files
     * @param fileSet   YANG file info set
     * @param project   maven project
     * @param operation true if need to add to resource
     * @throws IOException when fails to do IO operations
     */
    public static void serializeDataModel(String dir, Set<YangFileInfo> fileSet,
                                          MavenProject project, boolean operation)
            throws IOException {
        String serFileDirPath = dir + TARGET_RESOURCE_PATH;
        File dir1 = new File(serFileDirPath);
        dir1.mkdirs();
        if (operation) {
            addToProjectResource(dir + SLASH + TEMP + SLASH, project);
        }
        Set<YangNode> nodes = new HashSet<>();
        for (YangFileInfo fileInfo : fileSet) {
            nodes.add(fileInfo.getRootNode());
        }

        String serFileName = serFileDirPath + YANG_META_DATA +
                SERIALIZED_FILE_EXTENSION;
        FileOutputStream out = new FileOutputStream(serFileName);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        objectOutputStream.writeObject(nodes);
        objectOutputStream.close();
        out.close();
    }

    /**
     * Returns list of jar path.
     *
     * @param project     maven project
     * @param localRepo   local repository
     * @param remoteRepos remote repository
     * @return list of jar paths
     */
    private static List<String> resolveDependencyJarPath(
            MavenProject project, ArtifactRepository localRepo,
            List<ArtifactRepository> remoteRepos) {

        StringBuilder path = new StringBuilder();
        List<String> jarPaths = new ArrayList<>();
        for (Object obj : project.getDependencies()) {

            Dependency dependency = (Dependency) obj;
            path.append(localRepo.getBasedir()).append(SLASH)
                    .append(getPackageDirPathFromJavaJPackage(dependency.getGroupId()))
                    .append(SLASH).append(dependency.getArtifactId())
                    .append(SLASH).append(dependency.getVersion()).append(SLASH)
                    .append(dependency.getArtifactId()).append(HYPHEN)
                    .append(dependency.getVersion()).append(PERIOD).append(JAR);
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
     * @param project     current maven project
     * @param localRepo   local maven repository
     * @param remoteRepos list of remote repository
     * @param dir         directory for serialized files
     * @return list of resolved datamodel nodes
     * @throws IOException when fails to do IO operations
     */
    static List<YangNode> resolveInterJarDependencies(
            MavenProject project, ArtifactRepository localRepo,
            List<ArtifactRepository> remoteRepos, String dir)
            throws IOException {

        List<String> dependenciesJarPaths =
                resolveDependencyJarPath(project, localRepo, remoteRepos);
        List<YangNode> resolvedDataModelNodes = new ArrayList<>();
        for (String dependency : dependenciesJarPaths) {
            resolvedDataModelNodes.addAll(parseJarFile(dependency, dir));
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
