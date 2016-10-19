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

package org.onosproject.yangutils.parser.impl.listeners;

import org.onosproject.yangutils.datamodel.YangAtomicPath;
import org.onosproject.yangutils.datamodel.YangAugment;
import org.onosproject.yangutils.datamodel.YangModule;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangSubModule;
import org.onosproject.yangutils.datamodel.YangUses;
import org.onosproject.yangutils.datamodel.exceptions.DataModelException;
import org.onosproject.yangutils.datamodel.utils.Parsable;
import org.onosproject.yangutils.linker.impl.YangResolutionInfoImpl;
import org.onosproject.yangutils.parser.antlrgencode.GeneratedYangParser.AugmentStatementContext;
import org.onosproject.yangutils.parser.exceptions.ParserException;
import org.onosproject.yangutils.parser.impl.TreeWalkListener;

import java.util.List;

import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.addResolutionInfo;
import static org.onosproject.yangutils.datamodel.utils.GeneratedLanguage.JAVA_GENERATION;
import static org.onosproject.yangutils.datamodel.utils.YangConstructType.AUGMENT_DATA;
import static org.onosproject.yangutils.datamodel.utils.YangConstructType.CASE_DATA;
import static org.onosproject.yangutils.datamodel.utils.YangConstructType.DATA_DEF_DATA;
import static org.onosproject.yangutils.datamodel.utils.YangConstructType.DESCRIPTION_DATA;
import static org.onosproject.yangutils.datamodel.utils.YangConstructType.REFERENCE_DATA;
import static org.onosproject.yangutils.datamodel.utils.YangConstructType.STATUS_DATA;
import static org.onosproject.yangutils.datamodel.utils.YangConstructType.WHEN_DATA;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerCollisionDetector.detectCollidingChildUtil;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorLocation.EXIT;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorMessageConstruction.constructExtendedListenerErrorMessage;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorType.MISSING_CURRENT_HOLDER;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerErrorType.UNHANDLED_PARSED_DATA;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerUtil.getPrefixRemovedName;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerUtil.getValidAbsoluteSchemaNodeId;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerUtil.removeQuotesAndHandleConcat;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerValidation.validateCardinalityEitherOne;
import static org.onosproject.yangutils.parser.impl.parserutils.ListenerValidation.validateCardinalityMaxOne;
import static org.onosproject.yangutils.translator.tojava.YangDataModelFactory.getYangAugmentNode;
import static org.onosproject.yangutils.utils.UtilConstants.EMPTY_STRING;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 *  augment-stmt        = augment-keyword sep augment-arg-str optsep
 *                        "{" stmtsep
 *                            ;; these stmts can appear in any order
 *                            [when-stmt stmtsep]
 *                            *(if-feature-stmt stmtsep)
 *                            [status-stmt stmtsep]
 *                            [description-stmt stmtsep]
 *                            [reference-stmt stmtsep]
 *                            1*((data-def-stmt stmtsep) /
 *                               (case-stmt stmtsep))
 *                         "}"
 *
 * ANTLR grammar rule
 * augmentStatement : AUGMENT_KEYWORD augment LEFT_CURLY_BRACE (whenStatement |
 * ifFeatureStatement | statusStatement | descriptionStatement |
 * referenceStatement | dataDefStatement  | caseStatement)* RIGHT_CURLY_BRACE;
 */

/**
 * Represents listener based call back function corresponding to the "augment"
 * rule defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class AugmentListener {

    // No instantiation.
    private AugmentListener() {
    }

    /**
     * Performs validation and updates the data model tree when parser
     * receives an input matching the grammar rule (augment), performs
     * validation and updates the data model tree.
     *
     * @param listener listener's object
     * @param ctx      context object
     */
    public static void processAugmentEntry(TreeWalkListener listener,
                                           AugmentStatementContext ctx) {

        checkStackIsNotEmpty(listener, MISSING_HOLDER, AUGMENT_DATA,
                             ctx.augment().getText(), ENTRY);

        Parsable curData = listener.getParsedDataStack().peek();

        if (curData instanceof YangUses) {
            throw new ParserException(constructListenerErrorMessage(
                    UNHANDLED_PARSED_DATA, AUGMENT_DATA,
                    ctx.augment().getText(), ENTRY));
        }

        if (!(curData instanceof YangModule) &&
                !(curData instanceof YangSubModule)) {
            throw new ParserException(constructListenerErrorMessage(
                    INVALID_HOLDER, AUGMENT_DATA,
                    ctx.augment().getText(), ENTRY));
        }

        // Validates augment argument string
        List<YangAtomicPath> atomics =
                getValidAbsoluteSchemaNodeId(ctx.augment().getText(),
                                             AUGMENT_DATA, ctx);
        valSubStatCardinality(ctx);

        int line = ctx.getStart().getLine();
        int pos = ctx.getStart().getCharPositionInLine();

        detectCollidingChildUtil(listener, line, pos, EMPTY_STRING,
                                 AUGMENT_DATA);

        YangNode root = (YangNode) curData;
        String name = getPrefixRemovedName(atomics, root);
        YangAugment augment = getYangAugmentNode(JAVA_GENERATION);
        augment.setLineNumber(line);
        augment.setCharPosition(pos);
        augment.setFileName(listener.getFileName());
        augment.setTargetNode(atomics);
        augment.setName(removeQuotesAndHandleConcat(ctx.augment().getText()));
        augment.setPrefixRemovedName(name);

        try {
            root.addChild(augment);
        } catch (DataModelException e) {
            throw new ParserException(constructExtendedListenerErrorMessage(
                    UNHANDLED_PARSED_DATA, AUGMENT_DATA,
                    ctx.augment().getText(), ENTRY, e.getMessage()));
        }
        listener.getParsedDataStack().push(augment);

        // Adds resolution info to the list
        YangResolutionInfoImpl<YangAugment> info =
                new YangResolutionInfoImpl<>(augment, root, line, pos);
        addToResolution(info, ctx);
    }

    /**
     * Performs validations and update the data model tree when parser exits
     * from grammar rule (augment).
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processAugmentExit(TreeWalkListener listener,
                                          AugmentStatementContext ctx) {

        //Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, AUGMENT_DATA,
                             ctx.augment().getText(), EXIT);

        if (!(listener.getParsedDataStack().peek() instanceof YangAugment)) {
            throw new ParserException(constructListenerErrorMessage(
                    MISSING_CURRENT_HOLDER, AUGMENT_DATA,
                    ctx.augment().getText(), EXIT));
        }
        listener.getParsedDataStack().pop();
    }

    /**
     * Validates the cardinality of augment sub-statements as per grammar.
     *
     * @param ctx context object
     */
    private static void valSubStatCardinality(AugmentStatementContext ctx) {

        validateCardinalityMaxOne(ctx.statusStatement(), STATUS_DATA,
                                  AUGMENT_DATA, ctx.augment().getText());

        validateCardinalityMaxOne(ctx.descriptionStatement(), DESCRIPTION_DATA,
                                  AUGMENT_DATA, ctx.augment().getText());

        validateCardinalityMaxOne(ctx.referenceStatement(), REFERENCE_DATA,
                                  AUGMENT_DATA, ctx.augment().getText());

        validateCardinalityMaxOne(ctx.whenStatement(), WHEN_DATA, AUGMENT_DATA,
                                  ctx.augment().getText());

        validateCardinalityEitherOne(ctx.dataDefStatement(), DATA_DEF_DATA,
                                     ctx.caseStatement(), CASE_DATA,
                                     AUGMENT_DATA, ctx.augment().getText(),
                                     ctx);
    }

    /**
     * Add to resolution list.
     *
     * @param info resolution info
     * @param ctx  context object
     */
    private static void addToResolution(YangResolutionInfoImpl<YangAugment> info,
                                        AugmentStatementContext ctx) {
        try {
            addResolutionInfo(info);
        } catch (DataModelException e) {
            throw new ParserException(constructExtendedListenerErrorMessage(
                    UNHANDLED_PARSED_DATA, AUGMENT_DATA,
                    ctx.augment().getText(), EXIT, e.getMessage()));
        }
    }
}
