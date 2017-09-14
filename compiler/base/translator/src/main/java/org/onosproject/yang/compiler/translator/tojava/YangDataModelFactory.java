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

import org.onosproject.yang.compiler.datamodel.YangAnydata;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangCase;
import org.onosproject.yang.compiler.datamodel.YangChoice;
import org.onosproject.yang.compiler.datamodel.YangContainer;
import org.onosproject.yang.compiler.datamodel.YangGrouping;
import org.onosproject.yang.compiler.datamodel.YangIdentity;
import org.onosproject.yang.compiler.datamodel.YangInput;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNotification;
import org.onosproject.yang.compiler.datamodel.YangOutput;
import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.datamodel.YangSubModule;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangTypeDef;
import org.onosproject.yang.compiler.datamodel.YangUnion;
import org.onosproject.yang.compiler.datamodel.YangUses;
import org.onosproject.yang.compiler.datamodel.utils.GeneratedLanguage;
import org.onosproject.yang.compiler.translator.exception.TranslatorException;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaAnydataTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaAugmentTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaCaseTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaChoiceTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaContainerTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaEnumerationTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaGroupingTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaIdentityTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaInputTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaLeafListTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaLeafTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaListTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaModuleTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaNotificationTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaOutputTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaRpcTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaSubModuleTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaTypeDefTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaTypeTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaUnionTranslator;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaUsesTranslator;

/**
 * Represents factory to create data model objects based on the target file type.
 */
public final class YangDataModelFactory {

    /**
     * Creates a YANG data model factory object.
     */
    private YangDataModelFactory() {
    }

    /**
     * Based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangModule getYangModuleNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaModuleTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangAugment getYangAugmentNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaAugmentTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangCase getYangCaseNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaCaseTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangChoice getYangChoiceNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaChoiceTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangContainer getYangContainerNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaContainerTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangAnydata getYangAnydataNode(GeneratedLanguage
                                                      targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaAnydataTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangIdentity getYangIdentityNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaIdentityTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangGrouping getYangGroupingNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaGroupingTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangList getYangListNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaListTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangSubModule getYangSubModuleNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaSubModuleTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangTypeDef getYangTypeDefNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaTypeDefTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangUnion getYangUnionNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaUnionTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangUses getYangUsesNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaUsesTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangNotification getYangNotificationNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaNotificationTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangLeaf getYangLeaf(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaLeafTranslator();
            }
            default: {
                throw new RuntimeException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangLeafList getYangLeafList(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaLeafListTranslator();
            }
            default: {
                throw new RuntimeException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangRpc getYangRpcNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaRpcTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangInput getYangInputNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaInputTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangOutput getYangOutputNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaOutputTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }

    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangJavaEnumerationTranslator getYangEnumerationNode(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaEnumerationTranslator();
            }
            default: {
                throw new TranslatorException("Only YANG to Java is supported.");
            }
        }
    }
    /**
     * Returns based on the target language generate the inherited data model node.
     *
     * @param targetLanguage target language in which YANG mapping needs to be
     * generated
     * @return the corresponding inherited node based on the target language
     */
    public static YangType getYangType(GeneratedLanguage targetLanguage) {
        switch (targetLanguage) {
            case JAVA_GENERATION: {
                return new YangJavaTypeTranslator();
            }
            default: {
                throw new RuntimeException("Only YANG to Java is supported.");
            }
        }
    }
}
