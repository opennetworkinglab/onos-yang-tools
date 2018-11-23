/*
 * Copyright 2017-present Open Networking Foundation
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

package org.onosproject.yang.compiler.tool;

import org.apache.commons.io.IOUtils;
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
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.onosproject.yang.model.DefaultYangModel;
import org.onosproject.yang.model.DefaultYangModuleId;
import org.onosproject.yang.model.YangModel;
import org.onosproject.yang.model.YangModule;
import org.onosproject.yang.model.YangModuleId;
import org.slf4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Collections.sort;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_DERIVED_DATA_TYPE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_IDENTITYREF;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.getDateInStringFormat;
import static org.onosproject.yang.compiler.linker.impl.YangLinkerUtils.resolveGroupingInDefinationScope;
import static org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorUtil.generateJavaCode;
import static org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorUtil.translatorErrorHandler;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_META_DATA;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_RESOURCES;
import static org.onosproject.yang.compiler.utils.io.impl.YangFileScanner.getJavaFiles;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.createDirectories;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents implementation of YANG compiler manager.
 */
public class YangCompilerManager implements YangCompilerService {

    private static final Logger log = getLogger(YangCompilerManager.class);
    private static final String SLASH = File.separator;
    private final YangUtilsParser yangUtilsParser = new YangUtilsParserManager();
    private final YangLinker yangLinker = new YangLinkerManager();
    private final Set<YangNode> yangNodeSet = new HashSet<>();
    // YANG file information set.
    private Set<YangFileInfo> yangFileInfoSet; //initialize in tool invocation;
    private YangFileInfo curYangFileInfo = new YangFileInfo();
    private Set<Path> genJavaPath = new LinkedHashSet<>();
    private YangModel model;

    @Override
    public YangCompiledOutput compileYangFiles(YangCompilationParam param)
            throws IOException, YangCompilerException {
        synchronized (YangCompilerManager.class) {
            processYangFiles(param);
            return new DefaultYangCompiledOutput(model, genJavaPath);
        }
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
     * @param param YANG compilation parameters
     * @throws IOException when fails to do IO operations
     */
    private void processYangFiles(YangCompilationParam param) throws IOException {
        YangPluginConfig config = new YangPluginConfig();
        synchronized (YangCompilerManager.class) {
            try {
                String codeGenDir = param.getCodeGenDir() + SLASH;
                String resourceGenDir = param.getMetadataGenDir() + SLASH;
                config.setCodeGenDir(codeGenDir);
                config.resourceGenDir(resourceGenDir);
                yangFileInfoSet = createYangFileInfoSet(param.getYangFiles());

                // Check if there are any file to translate, if not return.
                if (yangFileInfoSet.isEmpty()) {
                    // No files to translate
                    return;
                }

                //Create resource directory.
                createDirectories(resourceGenDir);

                // Resolve inter jar dependency.
                addSchemaToFileSet(dependentSchema(param.getDependentSchemas()));

                // Carry out the parsing for all the YANG files.
                parseYangFileInfoSet();

                createYangNodeSet();

                // Serialize data model.
                processSerialization(resourceGenDir, param.getModelId());

                // Resolve dependencies using linker.
                try {
                    resolveDependenciesUsingLinker();
                } catch (Exception e) {
                    log.error("DependentSchemas: {}",
                              dependentSchema(param.getDependentSchemas())
                                  .stream()
                                  .map(YangNode::getName)
                                  .collect(Collectors.toList()), e);
                    throw e;
                }

                // Perform translation to JAVA.
                translateToJava(config);

                //add to generated java code map
                processGeneratedCode(codeGenDir);

                //add YANG files to JAR
                processCopyYangFile(resourceGenDir);
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
                        log.error("Error in processing the files", e);
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
            log.trace("from file:{}", path.getParent());
            try {
                Set<YangNode> yangNodes = getYangNodes(deSerializeDataModel(path.toString()));
                if (log.isTraceEnabled()) {
                    log.trace(" got: {}", yangNodes.stream()
                                              .map(YangNode::getName)
                                              .collect(Collectors.toList()));
                }
                depNodes.addAll(yangNodes);
            } catch (IOException e) {
                throw new YangCompilerException(
                        "Failed to fetch dependent schema from given " +
                                "path :" + path.toString(), e);
            }
        }
        return depNodes;
    }

    /**
     * Resolved inter-jar dependencies.
     *
     * @param dependentSchema dependent schema list
     */
    private void addSchemaToFileSet(Set<YangNode> dependentSchema) {
        if (dependentSchema == null || dependentSchema.isEmpty()) {
            return;
        }

        for (YangNode node : dependentSchema) {
            YangFileInfo dependentFileInfo = new YangFileInfo();
            node.setToTranslate(false);
            dependentFileInfo.setRootNode(node);
            dependentFileInfo.setForTranslator(false);
            dependentFileInfo.setYangFileName(node.getName());
            dependentFileInfo.setInterJar(true);
            yangFileInfoSet.add(dependentFileInfo);
        }
    }

    /**
     * Links all the provided schema in the YANG file info set.
     *
     * @throws YangCompilerException failed to link schema
     */
    public void resolveDependenciesUsingLinker() {
        try {
            yangLinker.resolveDependencies(yangNodeSet);
        } catch (LinkerException e) {
            printLog(e.getFileName(), e.getLineNumber(), e.getCharPositionInLine(),
                     e.getMessage(), e.getLocalizedMessage());
            log.error("Linking failed", e);
            throw new YangCompilerException(e.getMessage(), e);
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
     * @param id   model id
     * @throws IOException when fails to IO operations
     */
    public void processSerialization(String path, String id) throws IOException {
        List<YangNodeInfo> nodeInfo = new ArrayList<>();
        setNodeInfo(yangFileInfoSet, nodeInfo);
        model = processYangModel(path, nodeInfo, id, false);
        String serFileName = path + YANG_META_DATA;
        try (FileOutputStream out = new FileOutputStream(serFileName);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(out))) {
            objectOutputStream.writeObject(model);
        }
    }

    private void setNodeInfo(Set<YangFileInfo> yangFileInfoSet,
                             List<YangNodeInfo> infos) {
        for (YangFileInfo i : yangFileInfoSet) {
            infos.add(new YangNodeInfo(i.getRootNode(), i.isInterJar()));
        }
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
                        "failed to create yang resource directory: " + path);
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

    /**
     * Returns YANG model for application.
     *
     * @param path    path for metadata file
     * @param info    list of YANG node info
     * @param modelId model id
     * @param fromUt  if method is called from unit test
     * @return YANG model
     */
    public static YangModel processYangModel(
            String path, List<YangNodeInfo> info, String modelId, boolean fromUt) {
        YangModel.Builder b = DefaultYangModel.builder();
        YangModuleId id;
        for (YangNodeInfo i : info) {
            id = processModuleId(i.getNode());
            String serFile = path + id.moduleName() + id.revision() + ".ser";
            if (!fromUt) {
                serializeModuleMetaData(serFile, i.getNode());
            }
            //take the absolute jar path and make a new path for our yang files.
            String fileName = getFileName(i.getNode().getFileName());
            YangModuleExtendedInfo module = new YangModuleExtendedInfo(
                    id, new File(path + fileName), new File(serFile), i.isInterJar());
            module.setSchema(i.getNode());
            b.addModule(id, module);
        }
        return b.addModelId(modelId).build();
    }

    /**
     * Returns the file name from provided absolute path.
     *
     * @param absPath absolute path
     * @return file name
     */
    private static String getFileName(String absPath) {
        String[] file = absPath.split(SLASH);
        return file[file.length - 1];
    }

    /**
     * Serializes YANG Node.
     *
     * @param serFileName path of resource directory
     * @param node        YangNode
     */
    private static void serializeModuleMetaData(String serFileName, YangNode node) {
        try (FileOutputStream outStream = new FileOutputStream(serFileName);
             ObjectOutputStream objOutStream = new ObjectOutputStream(new BufferedOutputStream(outStream))) {
            objOutStream.writeObject(node);
        } catch (IOException e) {
            log.info("Error while serializing YANG node", e);
        }
    }

    /**
     * Returns YANG module id for a given YANG module node.
     *
     * @param module YANG module
     * @return YANG module id for a given YANG module node
     */
    public static YangModuleId processModuleId(YangNode module) {
        String rev = getDateInStringFormat(module);
        return new DefaultYangModuleId(module.getName(), rev);
    }

    /**
     * Returns YANG model for serialization.
     *
     * @param path    path for metadata file
     * @param list    set of YANG file info
     * @param modelId model id
     * @param fromUt  if method is called from unit test
     * @return YANG model
     */
    private static YangModel getModelForSerialization(
            String path, Set<YangFileInfo> list, String modelId, boolean fromUt) {
        YangModel.Builder b = DefaultYangModel.builder();
        YangModuleId id;
        boolean interJar;

        for (YangFileInfo info : list) {
            YangNode node = info.getRootNode();
            id = processModuleId(node);
            interJar = info.isInterJar();
            String serFile = path + id.moduleName() + id.revision() + ".ser";
            if (!fromUt) {
                serializeModuleMetaData(serFile, node);
            }
            //take the absolute jar path and make a new path for our yang files.
            String fileName = getFileName(node.getFileName());
            YangModuleExtendedInfo module = new YangModuleExtendedInfo(
                    id, new File(path + fileName), new File(serFile), interJar);
            module.setSchema(node);
            b.addModule(id, module);
        }
        return b.addModelId(modelId).build();
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
     * Returns de-serializes YANG data-model.
     *
     * @param info serialized File path
     * @return de-serializes YANG data-model
     * @throws IOException when fails do IO operations
     */
    public static YangModel deSerializeDataModel(String info)
            throws IOException {
        YangModel model;
        try (FileInputStream fileInputStream = new FileInputStream(info);
             ObjectInputStream objectInputStream =
                     new ObjectInputStream(new BufferedInputStream(fileInputStream))) {
            model = ((YangModel) objectInputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new IOException(info + " failed to fetch nodes due to " + e
                    .getLocalizedMessage(), e);
        }
        return model;
    }

    /**
     * Returns the set of YANG nodes from a given YANG model.
     *
     * @param model YANG model
     * @return set of YANG nodes
     */
    public static Set<YangNode> getYangNodes(YangModel model) {
        Set<YangNode> yangNodes = new HashSet<>();
        if (model != null) {
            Set<YangModule> modules = model.getYangModules();
            for (YangModule info : modules) {
                yangNodes.add(((YangModuleExtendedInfo) info).getSchema());
            }
        }
        return yangNodes;
    }

    /**
     * Sets YANG node info.
     *
     * @param model YANG model
     * @param infos node info to be filled
     */
    public static void setNodeInfo(YangModel model, List<YangNodeInfo> infos) {
        for (YangModule m : model.getYangModules()) {
            YangModuleExtendedInfo i = (YangModuleExtendedInfo) m;
            infos.add(new YangNodeInfo(i.getSchema(), i.isInterJar()));
        }
    }

    /**
     * Extracts .yang files and YangMetaData from jar file to
     * target directory and returns YangModel found.
     *
     * @param jarFile   jar file to be parsed
     * @param directory directory where to extract files to
     * @return YangModel found
     * @throws IOException when fails to do IO operations
     */
    public static YangModel parseJarFile(String jarFile, String directory)
            throws IOException {

        log.trace("Searching YangModel in {}", jarFile);
        YangModel model = null;
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<?> enumEntries = jar.entries();

            File dir = new File(directory + SLASH + YANG_RESOURCES);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            while (enumEntries.hasMoreElements()) {
                JarEntry file = (JarEntry) enumEntries.nextElement();
                if (file.getName().endsWith(YANG_META_DATA) ||
                        file.getName().endsWith(".yang")) {
                    String name = getFileName(file.getName());
                    File serializedFile = new File(directory + SLASH +
                                                           YANG_RESOURCES + SLASH + name);
                    if (file.isDirectory()) {
                        serializedFile.mkdirs();
                        continue;
                    }
                    try (InputStream inputStream = jar.getInputStream(file);
                         FileOutputStream fileOutputStream =
                                 new FileOutputStream(serializedFile)) {

                        IOUtils.copy(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        // FIXME hack to return first model found
                        if (model == null &&
                            serializedFile.getName().endsWith(YANG_META_DATA)) {
                            model = deSerializeDataModel(serializedFile.toString());
                            log.trace(" found {} at {}",
                                      model.getYangModelId(),
                                      serializedFile.getName());
                        }
                    }
                }
            }
        }
        return model;
    }
}
