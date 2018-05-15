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

import com.google.common.base.Throwables;

import org.onosproject.yang.compiler.datamodel.RpcNotificationContainer;
import org.onosproject.yang.compiler.datamodel.SchemaDataNode;
import org.onosproject.yang.compiler.datamodel.TraversalType;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangAugmentableNode;
import org.onosproject.yang.compiler.datamodel.YangInput;
import org.onosproject.yang.compiler.datamodel.YangLeavesHolder;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeType;
import org.onosproject.yang.compiler.datamodel.YangNotification;
import org.onosproject.yang.compiler.datamodel.YangOutput;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangUses;
import org.onosproject.yang.compiler.translator.exception.InvalidNodeForTranslatorException;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static org.onosproject.yang.compiler.datamodel.TraversalType.CHILD;
import static org.onosproject.yang.compiler.datamodel.TraversalType.PARENT;
import static org.onosproject.yang.compiler.datamodel.TraversalType.ROOT;
import static org.onosproject.yang.compiler.datamodel.TraversalType.SIBLING;
import static org.onosproject.yang.compiler.translator.tojava.YangJavaModelUtils.updateJavaInfo;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaIdentifierSyntax.getEnumJavaAttribute;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.searchAndDeleteTempDir;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Representation of java code generator based on application schema.
 */
public final class JavaCodeGeneratorUtil {

    private static final Logger log = getLogger(JavaCodeGeneratorUtil.class);
    /**
     * Creates a java code generator utility object.
     */
    private JavaCodeGeneratorUtil() {
    }

    /**
     * Translated YANG info to java info.
     *
     * @param rootNode   root node
     * @param yangPlugin YANG plugin configurations
     * @param codeGen    true if code generation is required
     * @throws TranslatorException when fails to generate java code file the current node
     * @throws IOException         when fails to do IO operations
     */
    public static void translate(YangNode rootNode, YangPluginConfig yangPlugin,
                                 boolean codeGen)
            throws TranslatorException, IOException {
        translateToJava(rootNode, yangPlugin, codeGen, true);
    }

    /**
     * Updates tree context including parent context and YsnContextInfoMap.
     *
     * @param rootNode          root node
     * @param yangPlugin        YANG plugin configurations
     * @param codeGen           true if code generation is required
     * @param translateComplete true if translation is for complete tree
     * @throws IllegalArgumentException when fails to set the parent context
     *                                  for provided anydata contained node
     */
    public static void updateTreeContext(YangSchemaNode rootNode, YangPluginConfig
            yangPlugin, boolean codeGen, boolean translateComplete)
            throws IllegalArgumentException {
        try {
            translateToJava((YangNode) rootNode, yangPlugin, codeGen,
                            translateComplete);
        } catch (IOException e) {
            throw new IllegalArgumentException("Updating parent context for "
                                                       + rootNode.getName() +
                                                       " failed.");
        }
    }

    /**
     * Translated YANG info to java info.
     *
     * @param rootNode          root node
     * @param yangPlugin        YANG plugin configurations
     * @param codeGen           true if code generation is required
     * @param translateComplete true if translation is for complete tree
     * @throws TranslatorException when fails to generate java code file the current node
     * @throws IOException         when fails to do IO operations
     */
    private static void translateToJava(YangNode rootNode, YangPluginConfig
            yangPlugin, boolean codeGen, boolean translateComplete)
            throws TranslatorException, IOException {
        YangNode codeGenNode = rootNode;
        YangNode parentNode = null;
        TraversalType curTraversal = ROOT;
        if (!translateComplete) {
            parentNode = rootNode.getParent();
        }

        while (codeGenNode != parentNode) {
            if (curTraversal != PARENT) {
                if (!(codeGenNode instanceof JavaCodeGenerator)) {
                    throw new TranslatorException("Unsupported node to generate code " +
                                                          codeGenNode.getName() + " in " +
                                                          codeGenNode.getLineNumber() + " at " +
                                                          codeGenNode.getCharPosition() + " in " +
                                                          codeGenNode.getFileName());
                }
                try {
                    if (!(codeGenNode instanceof YangUses) ||
                            !((YangUses) codeGenNode).isCloned()) {
                        if (codeGen) {
                            generateCodeEntry(codeGenNode, yangPlugin, rootNode);
                        } else {
                            //this will update java file info for the target
                            // node.
                            if (translateComplete) {
                                updateJavaInfo(codeGenNode, yangPlugin);
                            }
                            if (codeGenNode instanceof YangNotification) {
                                //to know in generated code what was the enum
                                // name generated for current notification
                                String enumName = getEnumJavaAttribute(codeGenNode.getName()
                                                                               .toUpperCase());
                                ((RpcNotificationContainer) codeGenNode.getParent())
                                        .addToNotificationEnumMap(enumName, codeGenNode);
                            }
                        }
                        if (!(codeGenNode instanceof YangUses)) {
                            codeGenNode.setNameSpaceAndAddToParentSchemaMap();
                            if (codeGenNode instanceof YangLeavesHolder ||
                                    codeGenNode instanceof SchemaDataNode) {
                                codeGenNode.setParentContext();
                                // updating the parent context and Ysn
                                // context info map for augmented node
                                if (!translateComplete && codeGenNode
                                        instanceof YangAugmentableNode) {
                                    List<YangAugment> augList =
                                            ((YangAugmentableNode) codeGenNode)
                                                    .getAugmentedInfoList();
                                    if (augList != null && !augList.isEmpty()) {
                                        for (YangAugment a : augList) {
                                            a.setLeafNameSpaceAndAddToParentSchemaMap();
                                            a.setLeafParentContext();
                                            processAugNode(a);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (((YangUses) codeGenNode).isCloned()) {
                            setUsesAugNsAddToParent((YangUses) codeGenNode);
                        }
                        //handle uses ,its java info is not required.
                        if (codeGenNode.getNextSibling() != null) {
                            curTraversal = SIBLING;
                            codeGenNode = codeGenNode.getNextSibling();
                        } else {
                            curTraversal = PARENT;
                            codeGenNode = codeGenNode.getParent();
                        }
                        continue;
                    }
                } catch (InvalidNodeForTranslatorException e) {
                    if (codeGenNode.getNextSibling() != null) {
                        curTraversal = SIBLING;
                        codeGenNode = codeGenNode.getNextSibling();
                    } else {
                        curTraversal = PARENT;
                        codeGenNode = codeGenNode.getParent();
                    }
                    continue;
                } catch (Exception e) {
                    log.error("Encountered exception", e);
                    close(codeGenNode, yangPlugin, rootNode);
                    throw new TranslatorException(e.getMessage());
                }
            }
            if (curTraversal != PARENT && codeGenNode.getChild() != null) {
                curTraversal = CHILD;
                codeGenNode = codeGenNode.getChild();
            } else if (codeGenNode.getNextSibling() != null) {
                try {
                    if (codeGen) {
                        generateCodeExit(codeGenNode, yangPlugin, rootNode);
                    }
                } catch (Exception e) {
                    log.error("Encountered exception", e);
                    close(codeGenNode, yangPlugin, rootNode);
                    Throwables.throwIfInstanceOf(e, TranslatorException.class);
                    throw new TranslatorException(e);
                }
                curTraversal = SIBLING;
                codeGenNode = codeGenNode.getNextSibling();
            } else {
                try {
                    if (codeGen) {
                        generateCodeExit(codeGenNode, yangPlugin, rootNode);
                    }
                } catch (Exception e) {
                    log.error("Encountered exception", e);
                    close(codeGenNode, yangPlugin, rootNode);
                    Throwables.throwIfInstanceOf(e, TranslatorException.class);
                    throw new TranslatorException(e);
                }
                curTraversal = PARENT;
                codeGenNode = codeGenNode.getParent();
            }
        }
    }

    /**
     * Generates Java code files corresponding to the YANG schema.
     *
     * @param rootNode   root node of the data model tree
     * @param yangPlugin YANG plugin config
     * @throws TranslatorException when fails to generate java code file the current node
     * @throws IOException         when fails to do IO operations
     */
    public static void generateJavaCode(YangNode rootNode, YangPluginConfig yangPlugin)
            throws TranslatorException, IOException {
        translate(rootNode, yangPlugin, true);
    }

    /**
     * Generates the current nodes code snippet.
     *
     * @param codeGenNode current data model node for which the code needs to be generated
     * @param yangPlugin  YANG plugin config
     * @param rootNode    YANG root node
     * @throws TranslatorException when fails to generate java code file the current node
     * @throws IOException         when fails to do IO operations
     */
    private static void generateCodeEntry(YangNode codeGenNode,
                                          YangPluginConfig yangPlugin,
                                          YangNode rootNode)
            throws TranslatorException, IOException {

        if (codeGenNode instanceof JavaCodeGenerator) {
            ((JavaCodeGenerator) codeGenNode).generateCodeEntry(yangPlugin);
        } else {
            close(codeGenNode, yangPlugin, rootNode);
            throw new TranslatorException(
                    "Generated data model node cannot be translated to target language code for " +
                            codeGenNode.getName() + " in " + codeGenNode.getLineNumber()
                            + " at " + codeGenNode.getCharPosition() + " in " + codeGenNode.getFileName());
        }
    }

    /**
     * Generates the current nodes code target code from the snippet.
     *
     * @param codeGenNode  current data model node for which the code needs to be generated
     * @param pluginConfig plugin configurations
     * @param rootNode     YANG root node
     * @throws TranslatorException when fails to generate java code file the current node
     * @throws IOException         when fails to do IO operations
     */
    private static void generateCodeExit(YangNode codeGenNode,
                                         YangPluginConfig pluginConfig,
                                         YangNode rootNode)
            throws TranslatorException, IOException {

        if (codeGenNode instanceof JavaCodeGenerator) {
            ((JavaCodeGenerator) codeGenNode).generateCodeExit();
        } else {
            close(codeGenNode, pluginConfig, rootNode);
            throw new TranslatorException(
                    "Generated data model node cannot be translated to target language code for " +
                            codeGenNode.getName() + " in " + codeGenNode.getLineNumber()
                            + " at " + codeGenNode.getCharPosition() + " in " + codeGenNode.getFileName());
        }
    }

    /**
     * Free other YANG nodes of data-model tree when error occurs while file generation of current node.
     *
     * @param freedNode current data model node
     */
    private static void freeRestResources(YangNode freedNode) {

        if (freedNode != null) {
            YangNode tempNode = freedNode;
            TraversalType curTraversal = ROOT;

            while (freedNode != tempNode.getParent()) {

                if (curTraversal != PARENT && freedNode.getChild() != null) {
                    curTraversal = CHILD;
                    freedNode = freedNode.getChild();
                } else if (freedNode.getNextSibling() != null) {
                    curTraversal = SIBLING;
                    if (freedNode != tempNode) {
                        free(freedNode);
                    }
                    freedNode = freedNode.getNextSibling();
                } else {
                    curTraversal = PARENT;
                    if (freedNode != tempNode) {
                        free(freedNode);
                    }
                    freedNode = freedNode.getParent();
                }
            }
        }
    }

    /**
     * Free the current node.
     *
     * @param node YANG node
     */
    private static void free(YangNode node) {

        YangNode parent = node.getParent();
        parent.setChild(null);

        if (node.getNextSibling() != null) {
            parent.setChild(node.getNextSibling());
        } else if (node.getPreviousSibling() != null) {
            parent.setChild(node.getPreviousSibling());
        }
        node = null;
    }

    /**
     * Delete Java code files corresponding to the YANG schema.
     *
     * @param rootNode         root node of data-model tree
     * @param yangPluginConfig plugin configurations
     * @throws IOException when fails to delete java code file the current node
     */
    public static void translatorErrorHandler(YangNode rootNode, YangPluginConfig yangPluginConfig)
            throws IOException {

        if (rootNode != null) {

            // Start removing all open files.
            YangNode tempNode = rootNode;
            YangNode curNode = tempNode.getChild();
            TraversalType curTraversal = ROOT;

            while (tempNode != null) {

                if (curTraversal != PARENT) {
                    close(tempNode, yangPluginConfig, rootNode);
                }
                if (curTraversal != PARENT && tempNode.getChild() != null) {
                    curTraversal = CHILD;
                    tempNode = tempNode.getChild();
                } else if (tempNode.getNextSibling() != null) {
                    curTraversal = SIBLING;
                    tempNode = tempNode.getNextSibling();
                } else {
                    curTraversal = PARENT;
                    tempNode = tempNode.getParent();
                }
            }

            freeRestResources(curNode);
        }
    }

    /**
     * Closes all the current open file handles of node and delete all generated files.
     *
     * @param node       current YANG node
     * @param yangPlugin plugin configurations
     * @param rootNode   YANG root node
     * @throws IOException when fails to do IO operations
     */
    private static void close(YangNode node, YangPluginConfig yangPlugin,
                              YangNode rootNode)
            throws IOException {
        if (node instanceof JavaCodeGenerator && ((TempJavaCodeFragmentFilesContainer) node)
                .getTempJavaCodeFragmentFiles() != null) {
            ((TempJavaCodeFragmentFilesContainer) node).getTempJavaCodeFragmentFiles().freeTemporaryResources(true);
        }
        if (rootNode != null) {
            JavaFileInfoTranslator javaFileInfo = ((JavaFileInfoContainer) rootNode).getJavaFileInfo();
            if (javaFileInfo.getPackage() != null) {
                searchAndDeleteTempDir(javaFileInfo.getBaseCodeGenPath() +
                                               javaFileInfo.getPackageFilePath());
            } else {
                searchAndDeleteTempDir(yangPlugin.getCodeGenDir());
            }
        }
    }

    /**
     * Searches child node in data model tree.
     *
     * @param parentNode parent node
     * @param nodeType   node type
     * @param nodeName   node name
     * @return child node
     */
    public static YangNode searchYangNode(YangNode parentNode, YangNodeType nodeType, String nodeName) {
        YangNode child = parentNode.getChild();
        TraversalType curTraversal = ROOT;
        if (child == null) {
            throw new IllegalArgumentException("Given parent node does not contain any child nodes");
        }

        while (child != null) {
            if (curTraversal != PARENT) {
                if (child instanceof YangInput || child instanceof YangOutput) {
                    if (child.getNodeType().equals(nodeType)) {
                        return child;
                    }
                } else if (child.getName().equals(nodeName) && child.getNodeType().equals(nodeType)) {
                    return child;
                }
            }
            if (curTraversal != PARENT && child.getChild() != null) {
                curTraversal = CHILD;
                child = child.getChild();
            } else if (child.getNextSibling() != null) {
                curTraversal = SIBLING;
                child = child.getNextSibling();
            } else {
                curTraversal = PARENT;
                child = child.getParent();
            }
        }
        return null;
    }

    /**
     * Sets the uses augment and its child's namespace and adds it to the
     * parent.
     *
     * @param uses YANG uses
     */
    public static void setUsesAugNsAddToParent(YangUses uses) {
        YangNode node = uses.getChild();
        while (node != null) {
            if (node instanceof YangAugment) {
                node.setNameSpaceAndAddToParentSchemaMap();
                node.setParentContext();
                processAugNode((YangAugment) node);
            }
            node = node.getNextSibling();
        }
    }

    /**
     * Processes the YANG augment node and its complete tree.
     *
     * @param aug YANG augment
     */
    private static void processAugNode(YangAugment aug) {
        YangNode child = aug.getChild();
        TraversalType curTraversal = ROOT;

        while (child != aug && child != null) {
            if (curTraversal != PARENT) {
                child.setNameSpaceAndAddToParentSchemaMap();
                if (child instanceof YangLeavesHolder ||
                        child instanceof SchemaDataNode) {
                    child.setParentContext();
                }
            }
            if (curTraversal != PARENT && child.getChild() != null) {
                curTraversal = CHILD;
                child = child.getChild();
            } else if (child.getNextSibling() != null) {
                curTraversal = SIBLING;
                child = child.getNextSibling();
            } else {
                curTraversal = PARENT;
                child = child.getParent();
            }
        }
    }
}
