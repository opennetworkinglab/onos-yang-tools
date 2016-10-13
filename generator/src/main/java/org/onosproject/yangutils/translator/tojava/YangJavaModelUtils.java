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

package org.onosproject.yangutils.translator.tojava;

import org.onosproject.yangutils.datamodel.RpcNotificationContainer;
import org.onosproject.yangutils.datamodel.YangAtomicPath;
import org.onosproject.yangutils.datamodel.YangAugment;
import org.onosproject.yangutils.datamodel.YangCase;
import org.onosproject.yangutils.datamodel.YangChoice;
import org.onosproject.yangutils.datamodel.YangGrouping;
import org.onosproject.yangutils.datamodel.YangLeavesHolder;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangNodeIdentifier;
import org.onosproject.yangutils.datamodel.YangSchemaNode;
import org.onosproject.yangutils.datamodel.YangTranslatorOperatorNode;
import org.onosproject.yangutils.datamodel.YangTypeHolder;
import org.onosproject.yangutils.datamodel.utils.DataModelUtils;
import org.onosproject.yangutils.translator.exception.TranslatorException;
import org.onosproject.yangutils.translator.tojava.javamodel.YangJavaAugmentTranslator;
import org.onosproject.yangutils.translator.tojava.javamodel.YangJavaEnumerationTranslator;
import org.onosproject.yangutils.translator.tojava.javamodel.YangJavaInputTranslator;
import org.onosproject.yangutils.translator.tojava.javamodel.YangJavaModuleTranslator;
import org.onosproject.yangutils.translator.tojava.javamodel.YangJavaOutputTranslator;
import org.onosproject.yangutils.translator.tojava.javamodel.YangJavaSubModuleTranslator;
import org.onosproject.yangutils.utils.io.YangPluginConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.isRpcChildNodePresent;
import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER;
import static org.onosproject.yangutils.translator.tojava.TempJavaFragmentFiles.addCurNodeInfoInParentTempFile;
import static org.onosproject.yangutils.translator.tojava.utils.IndentationType.FOUR_SPACE;
import static org.onosproject.yangutils.translator.tojava.utils.JavaFileGenerator.generateInterfaceFile;
import static org.onosproject.yangutils.translator.tojava.utils.JavaIdentifierSyntax.getRootPackage;
import static org.onosproject.yangutils.translator.tojava.utils.StringGenerator.methodClose;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorErrorType.INVALID_NODE;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorErrorType.INVALID_PARENT_NODE;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorErrorType.INVALID_TRANSLATION_NODE;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorUtils.getBeanFiles;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorUtils.getErrorMsg;
import static org.onosproject.yangutils.translator.tojava.utils.TranslatorUtils.getErrorMsgForCodeGenerator;
import static org.onosproject.yangutils.utils.UtilConstants.AUGMENTED;
import static org.onosproject.yangutils.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.HYPHEN;
import static org.onosproject.yangutils.utils.UtilConstants.INPUT_KEYWORD;
import static org.onosproject.yangutils.utils.UtilConstants.OUTPUT_KEYWORD;
import static org.onosproject.yangutils.utils.UtilConstants.PERIOD;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.addPackageInfo;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getCapitalCase;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getPackageDirPathFromJavaJPackage;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.insertDataIntoJavaFile;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.trimAtLast;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.validateLineLength;

/**
 * Represents utility class for YANG java model.
 */
public final class YangJavaModelUtils {

    // No instantiation.
    private YangJavaModelUtils() {
    }

    /**
     * Updates YANG java file package information.
     *
     * @param info   YANG java file info node
     * @param config YANG plugin config
     */
    public static void updatePackageInfo(JavaCodeGeneratorInfo info,
                                         YangPluginConfig config) {

        JavaFileInfoTranslator translator = info.getJavaFileInfo();

        if (info instanceof YangJavaAugmentTranslator) {
            updatePackageForAugmentInfo(info, config);
        } else {
            setNodeJavaName(info, config);
            translator.setJavaAttributeName(info.getJavaFileInfo()
                                                    .getJavaName());
            translator.setPackage(getCurNodePackage((YangNode) info));
        }
        updateCommonPackageInfo(translator, info, config);
    }

    /**
     * The java name for input, output is prefixed with rpc name and other
     * nodes are set by taking its own name from YANG.
     *
     * @param info   YANG java file info node
     * @param config YANG plugin config
     */
    private static void setNodeJavaName(JavaCodeGeneratorInfo info,
                                        YangPluginConfig config) {
        String javaGenName;
        if (info instanceof YangJavaInputTranslator) {
            javaGenName = ((YangJavaInputTranslator) info).getParent().getName() +
                    INPUT_KEYWORD;
        } else if (info instanceof YangJavaOutputTranslator) {
            javaGenName = ((YangJavaOutputTranslator) info).getParent().getName() +
                    OUTPUT_KEYWORD;
        } else {
            javaGenName = ((YangNode) info).getName();
        }
        info.getJavaFileInfo().setJavaName(getCamelCase(
                javaGenName, config.getConflictResolver()));
    }

    /**
     * Updates YANG java file package information.
     *
     * @param info   YANG java file info node
     * @param config YANG plugin config
     */
    private static void updatePackageForAugmentInfo(JavaCodeGeneratorInfo info,
                                                    YangPluginConfig config) {

        JavaFileInfoTranslator translator = info.getJavaFileInfo();

        translator.setJavaName(getAugmentClassName(
                (YangJavaAugmentTranslator) info, config));
        translator.setPackage(getAugmentsNodePackage((YangNode) info, config));
        updateCommonPackageInfo(translator, info, config);
    }

    /**
     * Returns package for augment node.
     *
     * @param yangNode augment node
     * @param config   plugin configurations
     * @return package for augment node
     */
    private static String getAugmentsNodePackage(YangNode yangNode,
                                                 YangPluginConfig config) {
        YangAugment augment = (YangAugment) yangNode;
        StringBuilder augmentPkg = new StringBuilder();
        augmentPkg.append(getCurNodePackage(augment));

        StringBuilder pkg = new StringBuilder();
        pkg.append(PERIOD);
        for (YangAtomicPath atomicPath : augment.getTargetNode()) {
            pkg.append(getCamelCase(atomicPath.getNodeIdentifier().getName(),
                                    config.getConflictResolver()))
                    .append(PERIOD);
        }
        augmentPkg.append(trimAtLast(pkg.toString(), PERIOD).toLowerCase());
        return augmentPkg.toString();
    }

    /**
     * Updates YANG java file package information for specified package.
     *
     * @param info   YANG java file info node
     * @param config YANG plugin config
     */
    private static void updatePackageInfo(JavaCodeGeneratorInfo info,
                                          YangPluginConfig config,
                                          String pkg) {

        JavaFileInfoTranslator translator = info.getJavaFileInfo();
        translator.setJavaName(getCamelCase(((YangNode) info).getName(),
                                            config.getConflictResolver()));
        translator.setPackage(pkg);
        updateCommonPackageInfo(translator, info, config);
    }

    /**
     * Updates common package information.
     *
     * @param translator JAVA file info translator
     * @param info       YANG java file info node
     * @param config     YANG plugin config
     */
    private static void updateCommonPackageInfo(JavaFileInfoTranslator translator,
                                                JavaCodeGeneratorInfo info,
                                                YangPluginConfig config) {
        translator.setPackageFilePath(getPackageDirPathFromJavaJPackage(
                info.getJavaFileInfo().getPackage()));
        translator.setBaseCodeGenPath(config.getCodeGenDir());
        translator.setPluginConfig(config);
    }

    /**
     * Updates temporary java code fragment files.
     *
     * @param info YANG java file info node
     * @throws IOException IO operations fails
     */
    private static void createTempFragmentFile(JavaCodeGeneratorInfo info)
            throws IOException {
        info.setTempJavaCodeFragmentFiles(
                new TempJavaCodeFragmentFiles(info.getJavaFileInfo()));
    }

    /**
     * Updates leaf information in temporary java code fragment files.
     *
     * @param info YANG java file info node
     * @throws IOException IO operations fails
     */
    private static void updateTempFragmentFiles(JavaCodeGeneratorInfo info,
                                                YangPluginConfig config)
            throws IOException {

        TempJavaCodeFragmentFiles translator =
                info.getTempJavaCodeFragmentFiles();

        if (info instanceof RpcNotificationContainer) {
            getBeanFiles(info).setRootNode(true);
            /*
             * Module / sub module node code generation.
             */
            if (info instanceof YangJavaModuleTranslator) {
                if (!((YangJavaModuleTranslator) info).getNotificationNodes()
                        .isEmpty()) {
                    updateNotificationNodeInfo(info, config);
                }
            } else if (info instanceof YangJavaSubModuleTranslator) {
                if (!((YangJavaSubModuleTranslator) info).getNotificationNodes()
                        .isEmpty()) {
                    updateNotificationNodeInfo(info, config);
                }
            }
        }
        if (info instanceof YangLeavesHolder) {
            YangLeavesHolder holder = (YangLeavesHolder) info;
            boolean isLeafPresent = holder.getListOfLeaf() != null && !holder
                    .getListOfLeaf().isEmpty();
            boolean isLeafListPresent = holder.getListOfLeafList() != null &&
                    !holder.getListOfLeafList().isEmpty();
            /*
             * Container
             * Case
             * Grouping
             * Input
             * List
             * Notification
             * Output
             */
            if (isLeafPresent || isLeafListPresent) {
                getBeanFiles(info).addCurNodeLeavesInfoToTempFiles((YangNode) info,
                                                                   config);
            }
            //Add value leaf flag attribute to temp file.
            if (isLeafPresent) {
                getBeanFiles(info).addValueLeafFlag(config, (YangNode) info);
            }
            if (((YangNode) info).isOpTypeReq()) {
                // Add operation type as an attribute.
                getBeanFiles(info).addOperationTypeToTempFiles((YangNode) info,
                                                               config);
                if (isLeafPresent) {
                    //Add select leaf flag attribute to temp file.
                    getBeanFiles(info).addSelectLeafFlag(config);
                }
            }
        } else if (info instanceof YangTypeHolder) {
            /*
             * Typedef
             * Union
             */
            translator.addTypeInfoToTempFiles((YangTypeHolder) info, config);
        } else if (info instanceof YangJavaEnumerationTranslator) {
            /*
             * Enumeration
             */
            translator.getEnumTempFiles()
                    .addEnumAttributeToTempFiles((YangNode) info, config);
        } else if (!(info instanceof YangChoice)) {
            /*Do nothing, only the interface needs to be generated for choice*/
            throw new TranslatorException(
                    getErrorMsgForCodeGenerator(INVALID_TRANSLATION_NODE,
                                                info));
        }
    }

    /**
     * Process generate code entry of YANG node.
     *
     * @param info   YANG java file info node
     * @param config plugin configurations
     * @throws IOException IO operations fails
     */
    private static void generateTempFiles(JavaCodeGeneratorInfo info,
                                          YangPluginConfig config)
            throws IOException {
        if (!(info instanceof YangNode)) {
            throw new TranslatorException(
                    getErrorMsgForCodeGenerator(INVALID_TRANSLATION_NODE,
                                                info));
        }
        createTempFragmentFile(info);
        updateTempFragmentFiles(info, config);
    }

    /**
     * Updates notification node info in service temporary file.
     *
     * @param info   java code generator info
     * @param config plugin configurations
     * @throws IOException when fails to do IO operations
     */
    private static void updateNotificationNodeInfo(JavaCodeGeneratorInfo info,
                                                   YangPluginConfig config)
            throws IOException {
        TempJavaCodeFragmentFiles translator =
                info.getTempJavaCodeFragmentFiles();
        if (info instanceof YangJavaModuleTranslator) {
            for (YangNode notification : ((YangJavaModuleTranslator) info)
                    .getNotificationNodes()) {
                translator.getEventFragmentFiles()
                        .addJavaSnippetOfEvent(notification, config);
            }
        }
        if (info instanceof YangJavaSubModuleTranslator) {
            for (YangNode notification : ((YangJavaSubModuleTranslator) info)
                    .getNotificationNodes()) {
                translator.getEventFragmentFiles()
                        .addJavaSnippetOfEvent(notification, config);
            }
        }
    }

    /**
     * Generates code for the current ata model node and adds itself as an
     * attribute in the parent.
     *
     * @param info            YANG java file info node
     * @param config          YANG plugin config
     * @param isMultiInstance flag to indicate whether it's a list
     * @throws IOException IO operations fails
     */
    public static void generateCodeAndUpdateInParent(JavaCodeGeneratorInfo info,
                                                     YangPluginConfig config,
                                                     boolean isMultiInstance)
            throws IOException {
        if (!(info instanceof YangNode)) {
            throw new TranslatorException(
                    getErrorMsgForCodeGenerator(INVALID_TRANSLATION_NODE,
                                                info));
        }

        /*
         * For second level and below cloned nodes code shouldn't be
         * generated also they needn't be added in parent, since
         * generated code will be under grouping, cloned node is only
         * used for YANG namespace.
         */
        YangNode n = (YangNode) info;
        if (n.getReferredSchema() != null &&
                !(((YangNode) n.getReferredSchema()).getParent() instanceof
                        YangGrouping)) {
            return;
        }
        /*
         * If first level cloned node, then it needs to be imported in the
         * generated code. In case uses under grouping, it would further have
         * second level uses, hence needn't add in parent grouping.
         */
        YangSchemaNode rn = getRefSchema(info);
        if (rn != null) {
            YangNode parent = ((YangNode) info).getParent();
            if (!(parent instanceof YangGrouping)) {
                addCurNodeInfoInParentTempFile((YangNode) rn, isMultiInstance,
                                               config, parent);
            }
            return;
        }

        /*
         * Generate the Java files corresponding to the current node.
         */
        generateCodeOfAugmentableNode(info, config);

        /*
         * Update the current nodes info in its parent nodes generated files.
         */
        addCurNodeInfoInParentTempFile((YangNode) info, isMultiInstance,
                                       config);
    }

    /**
     * Returns referred schema node in case of grouping uses.
     *
     * @param info YANG java file info node
     * @return referred schema node
     */
    private static YangSchemaNode getRefSchema(JavaCodeGeneratorInfo info) {

        YangSchemaNode node = (YangSchemaNode) info;
        if (node.getReferredSchema() == null) {
            return null;
        }

        /*
         * Obtain last referred node in case grouping is embedded inside
         * another grouping.
         */
        while (node.getReferredSchema() != null) {
            node = node.getReferredSchema();
        }
        return node;
    }

    /**
     * Generates code for the current data model node and adds support for it to
     * be augmented.
     *
     * @param info   YANG java file info node
     * @param config YANG plugin config
     * @throws IOException IO operations fails
     */
    public static void generateCodeOfAugmentableNode(JavaCodeGeneratorInfo info,
                                                     YangPluginConfig config)
            throws IOException {
        if (!(info instanceof YangNode)) {
            throw new TranslatorException(
                    getErrorMsgForCodeGenerator(INVALID_TRANSLATION_NODE,
                                                info));
        }

        generateCodeOfNode(info, config);
        TempJavaCodeFragmentFiles tempFiles =
                info.getTempJavaCodeFragmentFiles();
        if (!(info instanceof YangChoice)) {
            getBeanFiles(info).addYangAugmentedMap(config);
        }
        if (info instanceof YangCase) {
            YangNode parent = ((YangCase) info).getParent();
            JavaQualifiedTypeInfoTranslator typeInfo =
                    getQualifierInfoForCasesParent(parent, config);
            getBeanFiles(info).getJavaExtendsListHolder()
                    .addToExtendsList(typeInfo, (YangNode) info,
                                      tempFiles.getBeanTempFiles());

            getBeanFiles(info).addParentInfoInCurNodeTempFile((YangNode) info,
                                                              config);
        }
    }

    /**
     * Returns cases parent's qualified info.
     *
     * @param parent parent node
     * @param config plugin configuration
     * @return cases parent's qualified info
     */
    public static JavaQualifiedTypeInfoTranslator
    getQualifierInfoForCasesParent(YangNode parent,
                                   YangPluginConfig config) {
        String parentName;
        String parentPkg;
        JavaFileInfoTranslator parentInfo;
        if (parent instanceof YangChoice) {
            parentInfo = ((JavaFileInfoContainer) parent).getJavaFileInfo();
        } else {
            parent = ((YangAugment) parent).getAugmentedNode();
            parentInfo = ((JavaFileInfoContainer) parent).getJavaFileInfo();
        }
        if (parentInfo.getPackage() != null) {
            parentName = getCapitalCase(parentInfo.getJavaName());
            parentPkg = parentInfo.getPackage();
        } else {
            parentName = getCapitalCase(getCamelCase(parent.getName(),
                                                     config.getConflictResolver()));
            parentPkg = getNodesPackage(parent, config);
        }
        JavaQualifiedTypeInfoTranslator qualifiedTypeInfo =
                new JavaQualifiedTypeInfoTranslator();
        qualifiedTypeInfo.setClassInfo(parentName);
        qualifiedTypeInfo.setPkgInfo(parentPkg);
        return qualifiedTypeInfo;
    }

    /**
     * Generates code for the current data model node.
     *
     * @param info   YANG java file info node
     * @param config YANG plugin config
     * @throws IOException IO operations fails
     */
    public static void generateCodeOfNode(JavaCodeGeneratorInfo info,
                                          YangPluginConfig config)
            throws IOException {
        if (!(info instanceof YangNode)) {
            throw new TranslatorException(
                    getErrorMsgForCodeGenerator(INVALID_TRANSLATION_NODE,
                                                info));
        }
        updatePackageInfo(info, config);
        generateTempFiles(info, config);
    }

    /**
     * Generates code for the root module/sub-module node.
     *
     * @param info    YANG java file info node
     * @param config  YANG plugin config
     * @param rootPkg package of the root node
     * @throws IOException IO operations fails
     */
    public static void generateCodeOfRootNode(JavaCodeGeneratorInfo info,
                                              YangPluginConfig config,
                                              String rootPkg)
            throws IOException {
        if (!(info instanceof YangNode)) {
            throw new TranslatorException(
                    getErrorMsgForCodeGenerator(INVALID_TRANSLATION_NODE,
                                                info));
        }
        updatePackageInfo(info, config, rootPkg);

        if (isRpcChildNodePresent((YangNode) info)) {
            info.getJavaFileInfo()
                    .addGeneratedFileTypes(GENERATE_SERVICE_AND_MANAGER);
        }
        generateTempFiles(info, config);
    }

    /**
     * Returns the node package string.
     *
     * @param curNode current java node whose package string needs to be set
     * @return returns the root package string
     */
    public static String getCurNodePackage(YangNode curNode)
            throws TranslatorException {

        String pkg;
        if (!(curNode instanceof JavaFileInfoContainer) ||
                curNode.getParent() == null) {
            throw new TranslatorException(getErrorMsg(INVALID_NODE, curNode));
        }

        YangNode parentNode = DataModelUtils.getParentNodeInGenCode(curNode);
        if (!(parentNode instanceof JavaFileInfoContainer)) {
            throw new TranslatorException(getErrorMsg(INVALID_PARENT_NODE,
                                                      curNode));
        }
        JavaFileInfoTranslator handle =
                ((JavaFileInfoContainer) parentNode).getJavaFileInfo();
        pkg = handle.getPackage() + PERIOD + handle.getJavaName();
        return pkg.toLowerCase();
    }

    /**
     * Returns true if root node contains any data node.
     *
     * @param node root YANG node
     * @return true if root node contains any data node
     */
    public static boolean isRootNodesCodeGenRequired(YangNode node) {
        return isNodeCodeGenRequired(node, true);
    }

    /**
     * Returns true if get/set method of root node are required.
     *
     * @param node root node
     * @return true if get/set method of root node are required
     */
    public static boolean isGetSetOfRootNodeRequired(YangNode node) {
        return isNodeCodeGenRequired(node, false);
    }

    /**
     * Returns true if either get/set method of root node are required or root
     * node contains any data node. This check is done depending on the
     * boolean parameter indicating whether check to be performed for root
     * node code generation or get/set method generation.
     *
     * @param node        root node
     * @param rootNodeGen flag indicating check type
     * @return true if check pass, false otherwise
     */
    private static boolean isNodeCodeGenRequired(YangNode node,
                                                 boolean rootNodeGen) {
        YangLeavesHolder holder = (YangLeavesHolder) node;

        if (!holder.getListOfLeaf().isEmpty()) {
            return true;
        }
        if (!holder.getListOfLeafList().isEmpty()) {
            return true;
        }
        node = node.getChild();
        if (node == null) {
            return false;
        }

        if (rootNodeGen) {
            while (node != null) {
                if (!(node instanceof YangTranslatorOperatorNode)) {
                    return true;
                }
                node = node.getNextSibling();
            }
            return false;
        }
        while (node != null) {
            if (!(node instanceof YangAugment)) {
                return true;
            }
            node = node.getNextSibling();
        }
        return false;
    }

    /**
     * Returns nodes package.
     *
     * @param node   YANG node
     * @param config plugin config
     * @return java package
     */
    public static String getNodesPackage(YangNode node,
                                         YangPluginConfig config) {

        List<String> clsInfo = new ArrayList<>();
        while (node.getParent() != null) {
            if (node instanceof YangJavaAugmentTranslator) {
                clsInfo.add(getAugmentClassName((YangAugment) node,
                                                config));
            } else {
                clsInfo.add(getCamelCase(node.getName(), config
                        .getConflictResolver()));
            }
            node = node.getParent();
        }

        StringBuilder pkg = new StringBuilder();
        if (node instanceof YangJavaModuleTranslator) {
            YangJavaModuleTranslator module = (YangJavaModuleTranslator) node;
            pkg.append(getRootPackage(module.getVersion(),
                                      module.getModuleNamespace(),
                                      module.getRevision(),
                                      config.getConflictResolver()));
        } else if (node instanceof YangJavaSubModuleTranslator) {
            YangJavaSubModuleTranslator subModule =
                    (YangJavaSubModuleTranslator) node;
            pkg.append(getRootPackage(subModule.getVersion(),
                                      subModule.getNameSpaceFromModule(),
                                      subModule.getRevision(),
                                      config.getConflictResolver()));
        }
        pkg.append(EMPTY_STRING);
        int size = clsInfo.size();
        for (int i = size - 1; i >= 0; i--) {
            pkg.append(PERIOD).append(clsInfo.get(i));
        }
        return pkg.toString().toLowerCase();
    }

    /**
     * Returns augment class name.
     *
     * @param augment YANG augment
     * @param config  plugin configurations
     * @return augment class name
     */
    private static String getAugmentClassName(YangAugment augment,
                                              YangPluginConfig config) {
        YangNodeIdentifier identifier =
                augment.getTargetNode().get(augment.getTargetNode().size() - 1)
                        .getNodeIdentifier();
        String name = getCapitalCase(getCamelCase(identifier.getName(),
                                                  config.getConflictResolver()));
        if (identifier.getPrefix() != null) {
            return getCapitalCase(getCamelCase(AUGMENTED + HYPHEN + identifier
                                                       .getPrefix(),
                                               config.getConflictResolver())) + name;
        }
        return AUGMENTED + name;
    }

    /**
     * Generated java code during exit.
     *
     * @param type generated file type
     * @param node current YANG node
     * @throws IOException when fails to generate java files
     */
    public static void generateJava(int type, YangNode node)
            throws IOException {
        /*
         * Call for file generation if node is not under uses.
         */
        if (node.getReferredSchema() == null) {
            ((TempJavaCodeFragmentFilesContainer) node)
                    .getTempJavaCodeFragmentFiles().generateJavaFile(type, node);
        }
    }

    /**
     * Generates interface file for those yang file which contains only any
     * of these grouping, typedef and identity.
     *
     * @param rootNode root node
     * @throws IOException when fails to do IO operations
     */
    public static void generateInterfaceFileForNonDataNodes(YangNode rootNode) throws
            IOException {
        JavaCodeGeneratorInfo info = (JavaCodeGeneratorInfo) rootNode;
        TempJavaCodeFragmentFiles tempFile = info
                .getTempJavaCodeFragmentFiles();
        JavaFileInfoTranslator fileInfo = info.getJavaFileInfo();
        File filePath = new File(fileInfo.getBaseCodeGenPath() + fileInfo
                .getPackageFilePath());
        String name = getCapitalCase(fileInfo.getJavaName());
        //Add package info file for this.
        addPackageInfo(filePath, name, fileInfo.getPackage(), false);
        //Generate file handle for this.
        File interFace = tempFile.getBeanTempFiles().getJavaFileHandle(
                name);
        //generate java code for interface file.
        validateLineLength(generateInterfaceFile(interFace, null, rootNode,
                                                 false));
        insertDataIntoJavaFile(interFace, methodClose(FOUR_SPACE));
    }
}
