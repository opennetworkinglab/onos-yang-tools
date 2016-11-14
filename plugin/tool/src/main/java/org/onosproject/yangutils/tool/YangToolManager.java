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

package org.onosproject.yangutils.tool;

import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangReferenceResolver;
import org.onosproject.yangutils.datamodel.exceptions.DataModelException;
import org.onosproject.yangutils.linker.YangLinker;
import org.onosproject.yangutils.linker.exceptions.LinkerException;
import org.onosproject.yangutils.linker.impl.YangLinkerManager;
import org.onosproject.yangutils.parser.YangUtilsParser;
import org.onosproject.yangutils.parser.exceptions.ParserException;
import org.onosproject.yangutils.parser.impl.YangUtilsParserManager;
import org.onosproject.yangutils.tool.exception.YangToolException;
import org.onosproject.yangutils.utils.io.YangPluginConfig;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.sort;
import static org.onosproject.yangutils.datamodel.ResolvableType.YANG_DERIVED_DATA_TYPE;
import static org.onosproject.yangutils.datamodel.ResolvableType.YANG_IDENTITYREF;
import static org.onosproject.yangutils.linker.impl.YangLinkerUtils.resolveGroupingInDefinationScope;
import static org.onosproject.yangutils.tool.ToolConstants.E_CODE_GEN_PATH;
import static org.onosproject.yangutils.tool.ToolConstants.E_MISSING_INPUT;
import static org.onosproject.yangutils.translator.tojava.JavaCodeGeneratorUtil.generateJavaCode;
import static org.onosproject.yangutils.translator.tojava.JavaCodeGeneratorUtil.translatorErrorHandler;
import static org.onosproject.yangutils.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yangutils.utils.UtilConstants.SLASH;
import static org.onosproject.yangutils.utils.UtilConstants.TEMP;
import static org.onosproject.yangutils.utils.UtilConstants.YANG_RESOURCES;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.createDirectories;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents ONOS YANG tool manager.
 */
public class YangToolManager {

    private static final Logger log = getLogger(YangToolManager.class);
    public static final String DEFAULT_JAR_RES_PATH = SLASH + TEMP + SLASH +
            YANG_RESOURCES + SLASH;
    public static final String YANG_META_DATA = "YangMetaData";
    public static final String SERIALIZED_FILE_EXTENSION = ".ser";

    // YANG file information set.
    private Set<YangFileInfo> yangFileInfoSet; //initialize in tool invocation;
    private final YangUtilsParser yangUtilsParser = new YangUtilsParserManager();
    private final YangLinker yangLinker = new YangLinkerManager();
    private YangFileInfo curYangFileInfo = new YangFileInfo();
    private final Set<YangNode> yangNodeSet = new HashSet<>();

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
     * Compile te YANG files and generate the corresponding Java files.
     * Update the generated bundle with the schema metadata.
     *
     * @param yangFiles Application YANG files
     * @param config    tool configuration
     * @param plugin    invoking plugin
     */
    public void compileYangFiles(Set<YangFileInfo> yangFiles,
                                 List<YangNode> dependentSchema,
                                 YangPluginConfig config,
                                 CallablePlugin plugin) throws IOException {

        try {

            if (config == null || yangFiles == null) {
                throw new YangToolException(E_MISSING_INPUT);
            }
            yangFileInfoSet = yangFiles;

            if (config.getCodeGenDir() == null) {
                throw new YangToolException(E_CODE_GEN_PATH);
            }

            // Check if there are any file to translate, if not return.
            if (yangFileInfoSet == null || yangFileInfoSet.isEmpty()) {
                // No files to translate
                return;
            }

            createDirectories(config.resourceGenDir());

            // Resolve inter jar dependency.
            addSchemaToFileSet(dependentSchema);


            // Carry out the parsing for all the YANG files.
            parseYangFileInfoSet();

            // Resolve dependencies using linker.
            resolveDependenciesUsingLinker();

            // Perform translation to JAVA.
            translateToJava(config);

            // Serialize data model.
            Set<YangNode> compiledSchemas = new HashSet<>();
            for (YangFileInfo fileInfo : yangFileInfoSet) {
                compiledSchemas.add(fileInfo.getRootNode());
            }

            String serFileName = config.resourceGenDir() + YANG_META_DATA + SERIALIZED_FILE_EXTENSION;
            FileOutputStream fileOutputStream = new FileOutputStream(serFileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(compiledSchemas);
            objectOutputStream.close();
            fileOutputStream.close();


            //add YANG files to JAR
            List<File> files = getListOfFile(yangFileInfoSet);
            String path = config.resourceGenDir();
            File targetDir = new File(path);
            targetDir.mkdirs();

            for (File file : files) {
                Files.copy(file.toPath(),
                           new File(path + file.getName()).toPath(),
                           StandardCopyOption.REPLACE_EXISTING);
            }

            if (plugin != null) {
                plugin.addCompiledSchemaToBundle();
                plugin.addGeneratedCodeToBundle();
                plugin.addYangFilesToBundle();
            }

        } catch (IOException | ParserException e) {
            YangToolException exception =
                    new YangToolException(e.getMessage(), e);
            exception.setCurYangFile(curYangFileInfo);

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

    /**
     * Resolved inter-jar dependencies.
     *
     * @param dependentSchema dependent schema list
     * @throws IOException when fails to do IO operations
     */
    private void addSchemaToFileSet(List<YangNode> dependentSchema)
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
     * @throws YangToolException failed to link schema
     */
    public void resolveDependenciesUsingLinker() {
        createYangNodeSet();
        try {
            yangLinker.resolveDependencies(yangNodeSet);
        } catch (LinkerException e) {
            printLog(e.getFileName(), e.getLineNumber(), e.getCharPositionInLine(),
                     e.getMessage(), e.getLocalizedMessage());
            throw new YangToolException(e.getMessage());
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
            if (node.isToTranslate()) {
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

}
