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
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.rtinfo.RuntimeInformation;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangReferenceResolver;
import org.onosproject.yangutils.datamodel.exceptions.DataModelException;
import org.onosproject.yangutils.linker.YangLinker;
import org.onosproject.yangutils.linker.exceptions.LinkerException;
import org.onosproject.yangutils.linker.impl.YangLinkerManager;
import org.onosproject.yangutils.parser.YangUtilsParser;
import org.onosproject.yangutils.parser.exceptions.ParserException;
import org.onosproject.yangutils.parser.impl.YangUtilsParserManager;
import org.onosproject.yangutils.utils.io.YangPluginConfig;
import org.onosproject.yangutils.utils.io.YangToJavaNamingConflictUtil;
import org.onosproject.yangutils.utils.io.impl.YangFileScanner;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.sort;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE;
import static org.onosproject.yangutils.datamodel.ResolvableType.YANG_DERIVED_DATA_TYPE;
import static org.onosproject.yangutils.datamodel.ResolvableType.YANG_IDENTITYREF;
import static org.onosproject.yangutils.linker.impl.YangLinkerUtils.resolveGroupingInDefinationScope;
import static org.onosproject.yangutils.plugin.manager.YangPluginUtils.addToCompilationRoot;
import static org.onosproject.yangutils.plugin.manager.YangPluginUtils.copyYangFilesToTarget;
import static org.onosproject.yangutils.plugin.manager.YangPluginUtils.resolveInterJarDependencies;
import static org.onosproject.yangutils.plugin.manager.YangPluginUtils.serializeDataModel;
import static org.onosproject.yangutils.translator.tojava.JavaCodeGeneratorUtil.generateJavaCode;
import static org.onosproject.yangutils.translator.tojava.JavaCodeGeneratorUtil.translatorErrorHandler;
import static org.onosproject.yangutils.utils.UtilConstants.DEFAULT_BASE_PKG;
import static org.onosproject.yangutils.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.IN;
import static org.onosproject.yangutils.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yangutils.utils.UtilConstants.SLASH;
import static org.onosproject.yangutils.utils.UtilConstants.TEMP;
import static org.onosproject.yangutils.utils.UtilConstants.VERSION_ERROR;
import static org.onosproject.yangutils.utils.UtilConstants.YANG_RESOURCES;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.deleteDirectory;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getDirectory;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getPackageDirPathFromJavaJPackage;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getVersionValue;

/**
 * Represents ONOS YANG utility maven plugin.
 * Goal of plugin is yang2java.
 * Execution phase is generate-sources.
 * requiresDependencyResolution at compile time.
 */
@Mojo(name = "yang2java", defaultPhase = PROCESS_SOURCES,
        requiresDependencyResolution = COMPILE)
public class YangUtilManager extends AbstractMojo {

    private static final String DEFAULT_PKG =
            getPackageDirPathFromJavaJPackage(DEFAULT_BASE_PKG);
    private static final int SUPPORTED_VERSION = 339;
    private final YangPluginConfig yangPlugin = new YangPluginConfig();
    private YangNode rootNode;
    // YANG file information set.
    private Set<YangFileInfo> yangFileInfoSet = new HashSet<>();
    private final YangUtilsParser yangUtilsParser = new YangUtilsParserManager();
    private final YangLinker yangLinker = new YangLinkerManager();
    private YangFileInfo curYangFileInfo = new YangFileInfo();
    private final Set<YangNode> yangNodeSet = new HashSet<>();

    /**
     * Source directory for YANG files.
     */
    @Parameter(property = "yangFilesDir", defaultValue = "src/main/yang")
    private String yangFilesDir;

    /**
     * Source directory for generated files.
     */
    @Parameter(property = "classFileDir", defaultValue = "target/generated-sources")
    private String classFileDir;

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
     * Replacement required for period special character in the identifier.
     */
    @Parameter(property = "replacementForPeriod")
    private String replacementForPeriod;

    /**
     * Replacement required for underscore special character in the identifier.
     */
    @Parameter(property = "replacementForUnderscore")
    private String replacementForUnderscore;

    /**
     * Replacement required for hyphen special character in the identifier.
     */
    @Parameter(property = "replacementForHyphen")
    private String replacementForHyphen;

    /**
     * Prefix which is required for adding with the identifier.
     */
    @Parameter(property = "prefixForIdentifier")
    private String prefixForIdentifier;

    /**
     * Build context.
     */
    @Component
    private BuildContext context;

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
     * Code generation is for nbi or sbi.
     */
    @Parameter(property = "generateJavaFileForSbi", defaultValue = "nbi")
    private String generateJavaFileForSbi;

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

    private String outputDir;
    private String codeGenDir;

    @Override
    public void execute()
            throws MojoExecutionException, MojoFailureException {

        try {
            validateMavenVersion();
            /*
             * For deleting the generated code in previous build.
             */
            outputDir = getDirectory(baseDir, outputDirectory);
            deleteDirectory(outputDir + SLASH + TEMP);
            deleteDirectory(outputDir + SLASH + YANG_RESOURCES);
            String searchDir = getDirectory(baseDir, yangFilesDir);
            codeGenDir = getDirectory(baseDir, classFileDir) + SLASH;

            // Creates conflict resolver and set values to it.
            YangToJavaNamingConflictUtil conflictResolver = new YangToJavaNamingConflictUtil();
            conflictResolver.setReplacementForPeriod(replacementForPeriod);
            conflictResolver.setReplacementForHyphen(replacementForHyphen);
            conflictResolver.setReplacementForUnderscore(replacementForUnderscore);
            conflictResolver.setPrefixForIdentifier(prefixForIdentifier);
            yangPlugin.setCodeGenDir(codeGenDir);
            yangPlugin.setConflictResolver(conflictResolver);

            yangPlugin.setCodeGenerateForSbi(generateJavaFileForSbi.toLowerCase());
            /*
             * Obtain the YANG files at a path mentioned in plugin and creates
             * YANG file information set.
             */
            createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));

            // Check if there are any file to translate, if not return.
            if (yangFileInfoSet == null || yangFileInfoSet.isEmpty()) {
                // No files to translate
                return;
            }
            // Resolve inter jar dependency.
            resolveInterJarDependency();

            // Carry out the parsing for all the YANG files.
            parseYangFileInfoSet();

            // Resolve dependencies using linker.
            resolveDependenciesUsingLinker();

            // Perform translation to JAVA.
            translateToJava(yangPlugin);

            // Serialize data model.
            serializeDataModel(outputDir, yangFileInfoSet, project, true);
            addToCompilationRoot(codeGenDir, project, context);

            copyYangFilesToTarget(yangFileInfoSet, outputDir, project);
        } catch (IOException | ParserException e) {
            String fileName = EMPTY_STRING;
            if (curYangFileInfo != null) {
                fileName = curYangFileInfo.getYangFileName();
            }
            try {
                translatorErrorHandler(rootNode, yangPlugin);
                deleteDirectory(codeGenDir + DEFAULT_PKG);
            } catch (IOException ex) {
                e.printStackTrace();
                throw new MojoExecutionException(
                        "Error handler failed to delete files for data model node.");
            }
            getLog().info(e);
            throw new MojoExecutionException(
                    "Exception occurred due to " + e.getLocalizedMessage() +
                            IN + fileName + " YANG file.");
        }
    }

    /**
     * Validates current maven version of system.
     *
     * @throws MojoExecutionException when maven version is below 3.3.9
     */
    private void validateMavenVersion() throws MojoExecutionException {
        String version = runtime.getMavenVersion();
        if (getVersionValue(version) < SUPPORTED_VERSION) {
            throw new MojoExecutionException(VERSION_ERROR + version);
        }
    }

    /**
     * Returns the YANG node set.
     *
     * @return YANG node set
     */
    public Set<YangNode> getYangNodeSet() {
        return yangNodeSet;
    }

    /**
     * Resolved inter-jar dependencies.
     *
     * @throws IOException when fails to do IO operations
     */
    private void resolveInterJarDependency()
            throws IOException {
        try {
            List<YangNode> interJarResolvedNodes =
                    resolveInterJarDependencies(project, localRepository,
                                                remoteRepository, outputDir);
            for (YangNode node : interJarResolvedNodes) {
                YangFileInfo dependentFileInfo = new YangFileInfo();
                node.setToTranslate(false);
                dependentFileInfo.setRootNode(node);
                dependentFileInfo.setForTranslator(false);
                dependentFileInfo.setYangFileName(node.getName());
                yangFileInfoSet.add(dependentFileInfo);
            }
        } catch (IOException e) {
            throw new IOException("failed to resolve in inter-jar scenario.");
        }
    }

    /**
     * Links all the provided with the YANG file info set.
     *
     * @throws MojoExecutionException a violation in mojo execution
     */
    public void resolveDependenciesUsingLinker()
            throws MojoExecutionException {
        createYangNodeSet();
        try {
            yangLinker.resolveDependencies(yangNodeSet);
        } catch (LinkerException e) {
            printLog(e.getFileName(), e.getLineNumber(), e.getCharPositionInLine(),
                     e.getMessage(), e.getLocalizedMessage());
            throw new MojoExecutionException(e.getMessage());
        }
    }

    /**
     * Creates YANG nodes set.
     */
    public void createYangNodeSet() {
        for (YangFileInfo yangFileInfo : yangFileInfoSet) {
            yangNodeSet.add(yangFileInfo.getRootNode());
        }
    }

    /**
     * Parses all the provided YANG files and generates YANG data model tree.
     *
     * @throws IOException a violation in IO
     */
    public void parseYangFileInfoSet()
            throws IOException {
        for (YangFileInfo yangFileInfo : yangFileInfoSet) {
            curYangFileInfo = yangFileInfo;
            if (yangFileInfo.isForTranslator()) {
                try {
                    YangNode yangNode = yangUtilsParser.getDataModel(
                            yangFileInfo.getYangFileName());
                    yangFileInfo.setRootNode(yangNode);
                    rootNode = yangNode;
                    resolveGroupingInDefinationScope((YangReferenceResolver) yangNode);
                    try {
                        ((YangReferenceResolver) yangNode)
                                .resolveSelfFileLinking(YANG_DERIVED_DATA_TYPE);
                        ((YangReferenceResolver) yangNode)
                                .resolveSelfFileLinking(YANG_IDENTITYREF);
                    } catch (DataModelException e) {
                        printLog(e.getFileName(), e.getLineNumber(), e
                                .getCharPositionInLine(), e.getMessage(), e
                                         .getLocalizedMessage());
                    }
                } catch (ParserException e) {
                    printLog(e.getFileName(), e.getLineNumber(), e
                            .getCharPositionInLine(), e.getMessage(), e
                                     .getLocalizedMessage());
                    throw e;
                }
            }
        }
    }

    /**
     * Translates to java code corresponding to the YANG schema.
     *
     * @param yangPlugin YANG plugin config
     * @throws IOException when fails to generate java code file the current node
     */
    public void translateToJava(YangPluginConfig yangPlugin)
            throws IOException {
        List<YangNode> yangNodeSortedList = new LinkedList<>();
        yangNodeSortedList.addAll(yangNodeSet);
        sort(yangNodeSortedList);
        for (YangNode node : yangNodeSortedList) {
            if (node.isToTranslate()) {
                generateJavaCode(node, yangPlugin);
            }
        }
    }

    /**
     * Creates a YANG file info set.
     *
     * @param yangFileList YANG files list
     */
    public void createYangFileInfoSet(List<String> yangFileList) {
        for (String yangFile : yangFileList) {
            YangFileInfo yangFileInfo = new YangFileInfo();
            yangFileInfo.setYangFileName(yangFile);
            yangFileInfoSet.add(yangFileInfo);
        }
    }

    /**
     * Returns the YANG file info set.
     *
     * @return the YANG file info set
     */
    public Set<YangFileInfo> getYangFileInfoSet() {
        return yangFileInfoSet;
    }

    /**
     * Sets the YANG file info set.
     *
     * @param yangFileInfoSet the YANG file info set
     */
    void setYangFileInfoSet(Set<YangFileInfo> yangFileInfoSet) {
        this.yangFileInfoSet = yangFileInfoSet;
    }

    /**
     * Adds log info for exception.
     *
     * @param fileName file name
     * @param line     line number
     * @param position character position
     * @param msg      error message
     * @param localMsg local message
     */
    private void printLog(String fileName, int line, int position, String
            msg, String localMsg) {
        String logInfo = "Error in file: " + fileName;
        if (line != 0) {
            logInfo = logInfo + " at line: " + line + " at position: "
                    + position;

        }
        if (msg != null) {
            logInfo = logInfo + NEW_LINE + localMsg;
        }
        getLog().info(logInfo);
    }

}
