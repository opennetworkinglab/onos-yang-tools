/*
 * Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang.compiler.tool.impl;

import org.onosproject.yang.DefaultYangModel;
import org.onosproject.yang.DefaultYangModule;
import org.onosproject.yang.DefaultYangModuleId;
import org.onosproject.yang.YangModel;
import org.onosproject.yang.YangModuleId;
import org.onosproject.yang.compiler.api.YangCompilationParam;
import org.onosproject.yang.compiler.api.YangCompiledOutput;
import org.onosproject.yang.compiler.api.YangCompilerException;
import org.onosproject.yang.compiler.api.YangCompilerService;
import org.onosproject.yang.compiler.datamodel.YangDeviationHolder;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangReferenceResolver;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.linker.YangLinker;
import org.onosproject.yang.compiler.linker.exceptions.LinkerException;
import org.onosproject.yang.compiler.linker.impl.YangLinkerManager;
import org.onosproject.yang.compiler.parser.YangUtilsParser;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;
import org.onosproject.yang.compiler.tool.YangFileInfo;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Collections.sort;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_DERIVED_DATA_TYPE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_IDENTITYREF;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.deSerializeDataModel;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.resolveGroupingInDefinationScope;
import static org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorUtil.generateJavaCode;
import static org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorUtil.translatorErrorHandler;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_META_DATA;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getJavaFiles;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.createDirectories;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents implementation of YANG compiler manager.
 */
public class YangCompilerManager implements YangCompilerService {

    private static final Logger log = getLogger(YangCompilerManager.class);
    private final YangUtilsParser yangUtilsParser = new YangUtilsParserManager();
    private final YangLinker yangLinker = new YangLinkerManager();
    private final Set<YangNode> yangNodeSet = new HashSet<>();

    // YANG file information set.
    private Set<YangFileInfo> yangFileInfoSet; //initialize in tool invocation;
    private YangFileInfo curYangFileInfo = new YangFileInfo();
    private Set<Path> genJavaPath = new LinkedHashSet<>();

    @Override
    public YangCompiledOutput compileYangFiles(YangCompilationParam param)
            throws IOException, YangCompilerException {

        YangPluginConfig config = new YangPluginConfig();
        config.setCodeGenDir(param.getCodeGenDir().toString() + SLASH);
        config.resourceGenDir(param.getMetadataGenDir().toString() +
                                      SLASH);

        processYangFiles(createYangFileInfoSet(param.getYangFiles()),
                         dependentSchema(param.getDependentSchemas()), config);

        return new DefaultYangCompiledOutput(
                processYangModel(config.resourceGenDir()), genJavaPath);
    }

    /**
     * Returns YANG model for application.
     *
     * @param path path for metadata file
     * @return YANG model
     */
    private YangModel processYangModel(String path) {
        DefaultYangModel model = new DefaultYangModel();
        YangModuleId id;
        for (YangNode node : yangNodeSet) {
            id = processModuleId(node);
            org.onosproject.yang.YangModule module =
                    new DefaultYangModule(id, get(node.getFileName()), get(path));
            model.addModule(id, module);
        }
        return model;
    }

    /**
     * Returns YANG module id for a given YANG module node.
     *
     * @param module YANG module
     * @return YANG module id for a given YANG module node
     */
    private YangModuleId processModuleId(YangNode module) {
        return new DefaultYangModuleId(module.getName(), module.getRevision()
                .getRevDate().toString());
    }

    /**
     * Returns YANG node set.
     *
     * @return YANG node set
     */
    public Set<YangNode> getYangNodeSet() {
        return yangNodeSet;
    }

    /**
     * Provides a list of files from list of strings.
     *
     * @param yangFileInfo set of yang file information
     * @return list of files
     */
    private static List<File> getListOfFile(Set<YangFileInfo> yangFileInfo) {
        List<File> files = new ArrayList<>();
        for (YangFileInfo yangFile : yangFileInfo) {
            if (yangFile.isForTranslator()) {
                files.add(new File(yangFile.getYangFileName()));
            }
        }
        return files;
    }

    /**
     * Creates a YANG file info set.
     *
     * @param yangFileList YANG files list
     * @return yang file info set
     */
    public Set<YangFileInfo> createYangFileInfoSet(Set<Path> yangFileList) {
        if (yangFileInfoSet == null) {
            yangFileInfoSet = new HashSet<>();
        }
        for (Path yangFile : yangFileList) {
            YangFileInfo yangFileInfo = new YangFileInfo();
            yangFileInfo.setYangFileName(yangFile.toString());
            yangFileInfoSet.add(yangFileInfo);
        }
        return yangFileInfoSet;
    }

    /**
     * Compile te YANG files and generate the corresponding Java files.
     * Update the generated bundle with the schema metadata.
     *
     * @param yangFiles       Application YANG files
     * @param dependentSchema inter jar linked schema nodes
     * @param config          tool configurations
     * @throws IOException when fails to do IO operations
     */
    private void processYangFiles(Set<YangFileInfo> yangFiles,
                                  Set<YangNode> dependentSchema,
                                  YangPluginConfig config) throws IOException {

        synchronized (YangCompilerManager.class) {
            try {

                yangFileInfoSet = yangFiles;

                // Check if there are any file to translate, if not return.
                if (yangFileInfoSet.isEmpty()) {
                    // No files to translate
                    return;
                }

                //Create resource directory.
                createDirectories(config.resourceGenDir());

                // Resolve inter jar dependency.
                addSchemaToFileSet(dependentSchema);

                // Carry out the parsing for all the YANG files.
                parseYangFileInfoSet();

                // Resolve dependencies using linker.
                resolveDependenciesUsingLinker();

                // Perform translation to JAVA.
                translateToJava(config);

                //add to generated java code map
                processGeneratedCode(config.getCodeGenDir());

                // Serialize data model.
                processSerialization(config.resourceGenDir());

                //add YANG files to JAR
                processCopyYangFile(config.resourceGenDir());
            } catch (IOException | ParserException e) {
                //TODO: provide unified framework for exceptions
                YangCompilerException exception =
                        new YangCompilerException(e.getMessage(), e);
                exception.setYangFile(get(
                        curYangFileInfo.getYangFileName()));

                if (curYangFileInfo != null &&
                        curYangFileInfo.getRootNode() != null) {
                    try {
                        translatorErrorHandler(curYangFileInfo.getRootNode(),
                                               config);
                    } catch (IOException ex) {
                        e.printStackTrace();
                        throw ex;
                    }
                }
                throw exception;
            }
        }
    }

    /**
     * Adds all generated java class paths to YANG model.
     *
     * @param codeGenDir code gen directory.
     * @throws IOException when fails to do IO operations
     */
    private void processGeneratedCode(String codeGenDir) throws IOException {
        List<String> files = getJavaFiles(codeGenDir);
        for (String file : files) {
            genJavaPath.add(Paths.get(file));
        }
    }

    /**
     * Returns dependent schema nodes.
     *
     * @param dependentSchemaPath dependent schema paths
     * @return dependent schema nodes
     */
    private Set<YangNode> dependentSchema(Set<Path> dependentSchemaPath) {
        Set<YangNode> depNodes = new LinkedHashSet<>();
        for (Path path : dependentSchemaPath) {
            try {
                depNodes.addAll(deSerializeDataModel(path.toString()));
            } catch (IOException e) {
                throw new YangCompilerException(
                        "Failed to fetch dependent schema from given " +
                                "path :" + path.toString());
            }
        }
        return depNodes;
    }

    /**
     * Resolved inter-jar dependencies.
     *
     * @param dependentSchema dependent schema list
     * @throws IOException when fails to do IO operations
     */
    private void addSchemaToFileSet(Set<YangNode> dependentSchema)
            throws IOException {
        if (dependentSchema == null || dependentSchema.isEmpty()) {
            return;
        }

        for (YangNode node : dependentSchema) {
            YangFileInfo dependentFileInfo = new YangFileInfo();
            node.setToTranslate(false);
            dependentFileInfo.setRootNode(node);
            dependentFileInfo.setForTranslator(false);
            dependentFileInfo.setYangFileName(node.getName());
            yangFileInfoSet.add(dependentFileInfo);
        }
    }

    /**
     * Links all the provided schema in the YANG file info set.
     *
     * @throws YangCompilerException failed to link schema
     */
    public void resolveDependenciesUsingLinker() {
        createYangNodeSet();
        try {
            yangLinker.resolveDependencies(yangNodeSet);
        } catch (LinkerException e) {
            printLog(e.getFileName(), e.getLineNumber(), e.getCharPositionInLine(),
                     e.getMessage(), e.getLocalizedMessage());
            throw new YangCompilerException(e.getMessage());
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
     * @param pluginConfig YANG plugin config
     * @throws IOException when fails to generate java code file the current node
     */
    public void translateToJava(YangPluginConfig pluginConfig)
            throws IOException {
        List<YangNode> yangNodeSortedList = new LinkedList<>();
        yangNodeSortedList.addAll(yangNodeSet);
        sort(yangNodeSortedList);
        for (YangNode node : yangNodeSortedList) {
            if (node.isToTranslate() && !((YangDeviationHolder) node)
                    .isModuleForDeviation()) {
                generateJavaCode(node, pluginConfig);
            }
        }
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
        log.info(logInfo);
    }

    /**
     * Process serialization of datamodel.
     *
     * @param path path of resource directory
     * @throws IOException when fails to IO operations
     */
    private void processSerialization(String path) throws IOException {
        Set<YangNode> compiledSchemas = new HashSet<>();
        for (YangFileInfo fileInfo : yangFileInfoSet) {
            compiledSchemas.add(fileInfo.getRootNode());
        }

        String serFileName = path + YANG_META_DATA;
        FileOutputStream fileOutputStream = new FileOutputStream(serFileName);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(compiledSchemas);
        objectOutputStream.close();
        fileOutputStream.close();
    }

    /**
     * Copies yang files to resource directory.
     *
     * @param path yang file paths
     * @throws IOException when fails to do IO operations
     */
    private void processCopyYangFile(String path) throws IOException {

        //add YANG files to JAR
        List<File> files = getListOfFile(yangFileInfoSet);
        File targetDir = new File(path);
        if (!targetDir.exists()) {
            boolean isCreated = targetDir.mkdirs();
            if (!isCreated) {
                throw new YangCompilerException(
                        "failed to create yang resource directory.");
            }
        }

        for (File file : files) {
            copy(file.toPath(),
                 new File(path + file.getName()).toPath(),
                 REPLACE_EXISTING);
        }
    }

    /**
     * Returns yang file info set.
     *
     * @return yang file info set
     */
    public Set<YangFileInfo> getYangFileInfoSet() {
        return yangFileInfoSet;
    }

    /**
     * Sets yang file info set.
     *
     * @param yangFileInfoSet yang file info set
     */
    public void setYangFileInfoSet(Set<YangFileInfo> yangFileInfoSet) {
        this.yangFileInfoSet = yangFileInfoSet;
    }
}
