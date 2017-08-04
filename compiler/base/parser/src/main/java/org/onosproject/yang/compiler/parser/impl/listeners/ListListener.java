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

package org.onosproject.yang.compiler.parser.impl.listeners;

import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangCase;
import org.onosproject.yang.compiler.datamodel.YangContainer;
import org.onosproject.yang.compiler.datamodel.YangGrouping;
import org.onosproject.yang.compiler.datamodel.YangInput;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNotification;
import org.onosproject.yang.compiler.datamodel.YangOutput;
import org.onosproject.yang.compiler.datamodel.YangSubModule;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.GeneratedLanguage.JAVA_GENERATION;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.CONFIG_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DATA_DEF_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DESCRIPTION_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.KEY_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.LIST_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.MAX_ELEMENT_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.MIN_ELEMENT_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.REFERENCE_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.STATUS_DATA;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ListStatementContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerCollisionDetector.detectCollidingChildUtil;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.EXIT;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructExtendedListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_CURRENT_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.UNHANDLED_PARSED_DATA;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.addUniqueHolderToRoot;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.getValidIdentifier;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.getParentNodeConfig;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.validateCardinalityMaxOne;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.validateCardinalityNonZero;
import static org.onosproject.yang.compiler.translator.tojava.YangDataModelFactory.getYangListNode;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 *  list-stmt           = list-keyword sep identifier-arg-str optsep
 *                        "{" stmtsep
 *                            ;; these stmts can appear in any order
 *                            [when-stmt stmtsep]
 *                            *(if-feature-stmt stmtsep)
 *                            *(must-stmt stmtsep)
 *                            [key-stmt stmtsep]
 *                            *(unique-stmt stmtsep)
 *                            [config-stmt stmtsep]
 *                            [min-elements-stmt stmtsep]
 *                            [max-elements-stmt stmtsep]
 *                            [ordered-by-stmt stmtsep]
 *                            [status-stmt stmtsep]
 *                            [description-stmt stmtsep]
 *                            [reference-stmt stmtsep]
 *                            *((typedef-stmt /
 *                               grouping-stmt) stmtsep)
 *                            1*(data-def-stmt stmtsep)
 *                         "}"
 *
 * ANTLR grammar rule
 *  listStatement : LIST_KEYWORD identifier LEFT_CURLY_BRACE (whenStatement | ifFeatureStatement | mustStatement |
 *  keyStatement | uniqueStatement | configStatement | minElementsStatement | maxElementsStatement |
 *  orderedByStatement | statusStatement | descriptionStatement | referenceStatement | typedefStatement |
 *  groupingStatement| dataDefStatement)* RIGHT_CURLY_BRACE;
 */

/**
 * Represents listener based call back function corresponding to the "list" rule
 * defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class ListListener {

    /**
     * Creates a new list listener.
     */
    private ListListener() {
    }

    /**
     * It is called when parser receives an input matching the grammar rule
     * (list), performs validation and updates the data model tree.
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processListEntry(TreeWalkListener listener,
                                        ListStatementContext ctx) {

        YangNode curNode;

        checkStackIsNotEmpty(listener, MISSING_HOLDER, LIST_DATA, ctx.identifier().getText(), ENTRY);

        String identifier = getValidIdentifier(ctx.identifier().getText(), LIST_DATA, ctx);

        // Validate sub statement cardinality.
        validateSubStatementsCardinality(ctx);

        // Check for identifier collision
        int line = ctx.getStart().getLine();
        int charPositionInLine = ctx.getStart().getCharPositionInLine();
        detectCollidingChildUtil(listener, line, charPositionInLine, identifier, LIST_DATA);

        YangList yangList = getYangListNode(JAVA_GENERATION);
        yangList.setName(identifier);
        yangList.setLineNumber(line);
        yangList.setCharPosition(charPositionInLine);
        yangList.setFileName(listener.getFileName());
        /*
         * If "config" is not specified, the default is the same as the parent
         * schema node's "config" value.
         */
        if (ctx.configStatement().isEmpty()) {
            boolean parentConfig = getParentNodeConfig(listener);
            yangList.setConfig(parentConfig);
        }

        Parsable curData = listener.getParsedDataStack().peek();
        if (curData instanceof YangModule || curData instanceof YangContainer
                || curData instanceof YangList || curData instanceof YangCase
                || curData instanceof YangNotification || curData instanceof YangInput
                || curData instanceof YangOutput || curData instanceof YangAugment
                || curData instanceof YangGrouping || curData instanceof YangSubModule) {
            curNode = (YangNode) curData;
            try {
                curNode.addChild(yangList);
            } catch (DataModelException e) {
                throw new ParserException(constructExtendedListenerErrorMessage(UNHANDLED_PARSED_DATA,
                                                                                LIST_DATA, ctx.identifier().getText(),
                                                                                ENTRY, e.getMessage()));
            }
            listener.getParsedDataStack().push(yangList);
        } else {
            throw new ParserException(constructListenerErrorMessage(INVALID_HOLDER, LIST_DATA,
                                                                    ctx.identifier().getText(), ENTRY));
        }
    }

    /**
     * Processes parser exits from grammar rule (list), performing validation
     * and update on the date model tree.
     *
     * @param listener listener's object
     * @param ctx      context object
     */
    public static void processListExit(TreeWalkListener listener,
                                       ListStatementContext ctx) {

        checkStackIsNotEmpty(listener, MISSING_HOLDER, LIST_DATA,
                             ctx.identifier().getText(), EXIT);

        if (listener.getParsedDataStack().peek() instanceof YangList) {
            YangList yangList = (YangList) listener.getParsedDataStack().peek();
            try {
                yangList.validateDataOnExit();
                addUniqueHolderToRoot(yangList);
            } catch (DataModelException e) {
                ParserException exc = new ParserException(
                        constructExtendedListenerErrorMessage(
                                UNHANDLED_PARSED_DATA, LIST_DATA,
                                ctx.identifier().getText(), EXIT,
                                e.getMessage()));
                exc.setLine(ctx.getStart().getLine());
                exc.setCharPosition(ctx.getStart().getCharPositionInLine());
                throw exc;
            }
            listener.getParsedDataStack().pop();
        } else {
            throw new ParserException(constructListenerErrorMessage(
                    MISSING_CURRENT_HOLDER, LIST_DATA,
                    ctx.identifier().getText(), EXIT));
        }
    }

    /**
     * Validates the cardinality of list sub-statements as per grammar.
     *
     * @param ctx context object of the grammar rule
     */
    private static void validateSubStatementsCardinality(ListStatementContext ctx) {

        validateCardinalityMaxOne(ctx.keyStatement(), KEY_DATA, LIST_DATA, ctx.identifier().getText());
        validateCardinalityMaxOne(ctx.configStatement(), CONFIG_DATA, LIST_DATA, ctx.identifier().getText());
        validateCardinalityMaxOne(ctx.maxElementsStatement(), MAX_ELEMENT_DATA, LIST_DATA, ctx.identifier().getText());
        validateCardinalityMaxOne(ctx.minElementsStatement(), MIN_ELEMENT_DATA, LIST_DATA, ctx.identifier().getText());
        validateCardinalityMaxOne(ctx.descriptionStatement(), DESCRIPTION_DATA, LIST_DATA, ctx.identifier().getText());
        validateCardinalityMaxOne(ctx.referenceStatement(), REFERENCE_DATA, LIST_DATA, ctx.identifier().getText());
        validateCardinalityMaxOne(ctx.statusStatement(), STATUS_DATA, LIST_DATA, ctx.identifier().getText());
        validateCardinalityNonZero(ctx.dataDefStatement(), DATA_DEF_DATA, LIST_DATA, ctx.identifier().getText(), ctx);
        //TODO when, typedef, grouping, unique
    }
}
