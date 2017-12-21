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

package org.onosproject.yang.compiler.translator.tojava;

import org.onosproject.yang.compiler.datamodel.ConflictResolveNode;
import org.onosproject.yang.compiler.datamodel.RpcNotificationContainer;
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangBit;
import org.onosproject.yang.compiler.datamodel.YangBits;
import org.onosproject.yang.compiler.datamodel.YangCase;
import org.onosproject.yang.compiler.datamodel.YangChoice;
import org.onosproject.yang.compiler.datamodel.YangEnum;
import org.onosproject.yang.compiler.datamodel.YangEnumeration;
import org.onosproject.yang.compiler.datamodel.YangGrouping;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangSubModule;
import org.onosproject.yang.compiler.datamodel.YangTranslatorOperatorNode;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangTypeHolder;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.javamodel.JavaLeafInfoContainer;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaAnydataTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaAugmentTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaEnumerationTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaInputTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaModuleTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaOutputTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaSubModuleTranslator;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.getParentNodeInGenCode;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.isRpcChildNodePresent;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_ENUM_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER;
import static org.onosproject.yang.compiler.translator.tojava.JavaQualifiedTypeInfoTranslator.updateLeavesJavaQualifiedInfo;
import static org.onosproject.yang.compiler.translator.tojava.TempJavaFragmentFiles.addCurNodeInfoInParentTempFile;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaFileGenerator.generateInterfaceFile;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaIdentifierSyntax.getRootPackage;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorErrorType.INVALID_NODE;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorErrorType.INVALID_PARENT_NODE;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorErrorType.INVALID_TRANSLATION_NODE;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorUtils.getBeanFiles;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorUtils.getErrorMsg;
import static org.onosproject.yang.compiler.translator.tojava.utils.TranslatorUtils.getErrorMsgForCodeGenerator;
import static org.onosproject.yang.compiler.utils.UtilConstants.AUGMENTED;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_CURLY_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.HYPHEN;
import static org.onosproject.yang.compiler.utils.UtilConstants.INPUT_KEYWORD;
import static org.onosproject.yang.compiler.utils.UtilConstants.OUTPUT_KEYWORD;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.addPackageInfo;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.formatFile;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCamelCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getCapitalCase;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.getPackageDirPathFromJavaJPackage;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.insertDataIntoJavaFile;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.trimAtLast;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents utility class for YANG java model.
 */
public final class YangJavaModelUtils {

    private static final Logger LOG = getLogger(YangJavaModelUtils.class);

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
        if (info instanceof ConflictResolveNode && ((ConflictResolveNode)
                info).isNameConflict()) {
            javaGenName = javaGenName + ((ConflictResolveNode) info).getSuffix();
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
        translator.setPackage(getAugmentsNodePackage((YangNode) info));
        updateCommonPackageInfo(translator, info, config);
    }

    /**
     * Returns package for augment node.
     *
     * @param yangNode augment node
     * @return package for augment node
     */
    private static String getAugmentsNodePackage(YangNode yangNode) {
        YangAugment augment = (YangAugment) yangNode;
        StringBuilder augmentPkg = new StringBuilder();
        augmentPkg.append(getCurNodePackage(augment)).append(PERIOD)
                .append(getPkgFromAugment(augment));
        return augmentPkg.toString();
    }

    private static String getPkgFromAugment(YangAugment augment) {
        StringBuilder pkg = new StringBuilder();
        for (YangAtomicPath atomicPath : augment.getTargetNode()) {
            pkg.append(getCamelCase(atomicPath.getNodeIdentifier().getName(),
                                    null)).append(PERIOD);
        }
        return trimAtLast(pkg.toString(), PERIOD).toLowerCase();
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
        String name = ((YangNode) info).getName();
        JavaFileInfoTranslator translator = info.getJavaFileInfo();
        if (info instanceof ConflictResolveNode && ((ConflictResolveNode)
                info).isNameConflict()) {
            name = name + ((ConflictResolveNode) info).getSuffix();
        }
        translator.setJavaName(getCamelCase(name,
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
        TempJavaBeanFragmentFiles bean = getBeanFiles(info);
        if (info instanceof RpcNotificationContainer) {
            bean.setRootNode(true);
            /*
             * event classes code generation.
             */
            updateNotificationNodeInfo(info, config);
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
                bean.addCurNodeLeavesInfoToTempFiles((YangNode) info,
                                                     config);
                //Add value leaf flag attribute to temp file.
                bean.addValueLeafFlag(config, (YangNode) info);
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
        } else if (info instanceof YangJavaAnydataTranslator) {
            // TODO remove this check if anydata code generation is not
            // supported.
            // Do nothing
        } else if (!(info instanceof YangChoice) && !(info instanceof
                YangRpc)) {
            /*Do nothing, only the interface needs to be generated for choice*/
            throw new TranslatorException(
                    getErrorMsgForCodeGenerator(INVALID_TRANSLATION_NODE, info));
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
        TempJavaCodeFragmentFiles tempFile = info.getTempJavaCodeFragmentFiles();
        for (YangNode notification :
                ((RpcNotificationContainer) info).getNotificationNodes()) {
            tempFile.getEventFragmentFiles()
                    .addJavaSnippetOfEvent(notification, config);
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
        TempJavaBeanFragmentFiles tempFiles = getBeanFiles(info);
        if (info instanceof YangCase) {
            YangNode parent = ((YangCase) info).getParent();
            JavaQualifiedTypeInfoTranslator typeInfo =
                    getQualifierInfoForCasesParent(parent, config);
            tempFiles.getJavaExtendsListHolder()
                    .addToExtendsList(typeInfo, (YangNode) info, tempFiles);

            tempFiles.addParentInfoInCurNodeTempFile((YangNode) info, config);
        }
    }

    /**
     * Returns cases parent's qualified info.
     *
     * @param parent parent node
     * @param config plugin configuration
     * @return cases parent's qualified info
     */
    private static JavaQualifiedTypeInfoTranslator getQualifierInfoForCasesParent(
            YangNode parent, YangPluginConfig config) {
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
            info.getJavaFileInfo().addGeneratedFileTypes(GENERATE_SERVICE_AND_MANAGER);
        }
        generateTempFiles(info, config);
    }

    /**
     * Returns the node package string.
     *
     * @param curNode current java node whose package string needs to be set
     * @return returns the root package string
     * @throws TranslatorException on error
     */
    public static String getCurNodePackage(YangNode curNode)
            throws TranslatorException {

        String pkg;
        if (!(curNode instanceof JavaFileInfoContainer) ||
                curNode.getParent() == null) {
            throw new TranslatorException(getErrorMsg(INVALID_NODE, curNode));
        }

        YangNode parentNode = getParentNodeInGenCode(curNode);
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
        if (node.getReferredSchema() != null) {
            node = (YangNode) node.getReferredSchema();
        }
        while (node.getParent() != null) {
            if (node instanceof YangJavaAugmentTranslator) {
                YangJavaAugmentTranslator augment =
                        (YangJavaAugmentTranslator) node;
                clsInfo.add(getAugmentClassName(augment, config));
                clsInfo.add(getPkgFromAugment(augment));
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
                                      module.getModuleName(),
                                      module.getRevision(),
                                      config.getConflictResolver()));
        } else if (node instanceof YangJavaSubModuleTranslator) {
            YangJavaSubModuleTranslator subModule =
                    (YangJavaSubModuleTranslator) node;
            pkg.append(getRootPackage(subModule.getVersion(),
                                      subModule.getModuleName(),
                                      subModule.getRevision(),
                                      config.getConflictResolver()));
        }
        clsInfo.add(getCamelCase(node.getName(), config.getConflictResolver()));

        int size = clsInfo.size();
        for (int i = size - 1; i > 0; i--) {
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
    public static String getAugmentClassName(YangAugment augment,
                                             YangPluginConfig config) {
        YangNodeIdentifier identifier =
                augment.getTargetNode().get(augment.getTargetNode().size() - 1)
                        .getNodeIdentifier();
        String prefix = identifier.getPrefix();
        String idName = identifier.getName();
        StringBuilder name = new StringBuilder(AUGMENTED).append(HYPHEN);
        if (identifier.getPrefix() != null) {
            name.append(prefix).append(HYPHEN);
        }
        name.append(idName);
        return getCapitalCase(getCamelCase(name.toString(),
                                           config.getConflictResolver()));
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

    private static void createAndAddEnum(String name, int value,
                                         YangEnumeration enumeration) {
        YangEnum yangEnum = new YangEnum();
        yangEnum.setNamedValue(name);
        yangEnum.setValue(value);
        try {
            enumeration.addEnumInfo(yangEnum);
        } catch (DataModelException e) {
            LOG.error("failed to add enum in bits enum class " + e);
        }
    }

    /**
     * Returns bits type enum file.
     *
     * @param attr     attribute
     * @param type     data type
     * @param fileInfo file info
     * @param tempFile temp java fragment files
     * @throws IOException when fails to do IO operations
     */
    static void generateBitsFile(
            JavaAttributeInfo attr, YangType type,
            JavaFileInfoTranslator fileInfo, TempJavaFragmentFiles tempFile) throws IOException {
        String className = attr.getAttributeName();
        JavaFileInfoTranslator attrInfo = new JavaFileInfoTranslator();
        attrInfo.setJavaName(className);
        attrInfo.setPackage((fileInfo.getPackage() + "." + fileInfo.getJavaName()
                            ).toLowerCase());
        attrInfo.setBaseCodeGenPath(fileInfo.getBaseCodeGenPath());
        attrInfo.setGeneratedFileTypes(GENERATE_ENUM_CLASS);
        attrInfo.setPackageFilePath(fileInfo.getPackageFilePath() + File.separator +
                                            fileInfo.getJavaName().toLowerCase());
        attrInfo.setPluginConfig(fileInfo.getPluginConfig());
        TempJavaCodeFragmentFiles codeFile = new TempJavaCodeFragmentFiles(
                attrInfo);
        YangJavaEnumerationTranslator enumeration = new YangJavaEnumerationTranslator() {
            @Override
            public String getJavaPackage() {
                return attr.getImportInfo().getPkgInfo();
            }

            @Override
            public String getJavaClassNameOrBuiltInType() {
                return className;
            }

            @Override
            public String getJavaAttributeName() {
                return className;
            }
        };

        enumeration.setName(getCapitalCase(className));
        enumeration.setJavaFileInfo(attrInfo);
        enumeration.setTempJavaCodeFragmentFiles(codeFile);
        YangBits yangBits = (YangBits) type.getDataTypeExtendedInfo();
        Integer key;
        YangBit bit;
        String bitName;
        for (Map.Entry<Integer, YangBit> entry : yangBits.getBitPositionMap()
                .entrySet()) {
            key = entry.getKey();
            bit = entry.getValue();
            if (bit != null) {
                bitName = bit.getBitName();
                createAndAddEnum(bitName, key, enumeration);
            }
        }

        codeFile.getEnumTempFiles()
                .addEnumAttributeToTempFiles(enumeration, fileInfo.getPluginConfig());
        codeFile.getEnumTempFiles().setEnumClass(false);
        codeFile.generateJavaFile(GENERATE_ENUM_CLASS, enumeration);

        //Add to import list.
        JavaQualifiedTypeInfoTranslator info = new
                JavaQualifiedTypeInfoTranslator();
        info.setClassInfo(getCapitalCase(attrInfo.getJavaName()));
        info.setPkgInfo(attrInfo.getPackage());
        if (tempFile instanceof TempJavaTypeFragmentFiles) {
            tempFile.getJavaImportData().addImportInfo(info, fileInfo
                    .getJavaName(), fileInfo.getPackage());
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
        insertDataIntoJavaFile((generateInterfaceFile(interFace, null,
                                                      rootNode, false)),
                               CLOSE_CURLY_BRACKET);
        formatFile(interFace);
    }

    /**
     * Updates java file info for nodes in target linking.
     *
     * @param node   node
     * @param config plugin config
     */
    static void updateJavaInfo(YangNode node, YangPluginConfig config) {
        if (node instanceof YangModule) {
            //handle module
            YangModule module = (YangModule) node;
            String modulePkg = getRootPackage(module.getVersion(),
                                              module.getModuleName(),
                                              module.getRevision(),
                                              null);
            updatePackageInfo((JavaCodeGeneratorInfo) node, config, modulePkg);
        } else if (node instanceof YangSubModule) {
            //handle submodule
            YangJavaSubModuleTranslator subModule = (YangJavaSubModuleTranslator) node;
            String subModulePkg = getRootPackage(
                    subModule.getVersion(), subModule.getModuleName(),
                    subModule.getRevision(),
                    null);
            updatePackageInfo((JavaCodeGeneratorInfo) node, config, subModulePkg);
        } else {
            //handle other nodes and also handle grouping
            if (node.getReferredSchema() != null) {
                //in case of grouping in normal case we generate java file
                // info for node even before cloning so when we clone the
                // actual java file info will be cloned but in this case we
                // will clone first so in the cloned node , we have to update
                // the actual java file info for correct operations. because
                // code will be generated only for actual node.
                YangNode n = (YangNode) getRefSchema((JavaCodeGeneratorInfo) node);
                if (n != null) {
                    updatePackageInfo((JavaCodeGeneratorInfo) n, config);
                    ((JavaCodeGeneratorInfo) node).setJavaFileInfo(
                            ((JavaCodeGeneratorInfo) n).getJavaFileInfo());
                }
            } else {
                updatePackageInfo((JavaCodeGeneratorInfo) node, config);
            }
        }
        if (node instanceof YangLeavesHolder) {
            YangLeavesHolder holder = (YangLeavesHolder) node;
            for (YangLeaf leaf : holder.getListOfLeaf()) {
                updateLeavesJavaQualifiedInfo((JavaLeafInfoContainer) leaf);
            }
            for (YangLeafList leaf : holder.getListOfLeafList()) {
                updateLeavesJavaQualifiedInfo((JavaLeafInfoContainer) leaf);
            }
        }
    }
}
