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
import org.onosproject.yang.compiler.datamodel.YangNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.YangNotification;
import org.onosproject.yang.compiler.datamodel.YangOutput;
import org.onosproject.yang.compiler.datamodel.YangSubModule;
import org.onosproject.yang.compiler.datamodel.YangUses;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.linker.impl.YangResolutionInfoImpl;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.addResolutionInfo;
import static org.onosproject.yang.compiler.datamodel.utils.GeneratedLanguage.JAVA_GENERATION;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DESCRIPTION_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.REFERENCE_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.STATUS_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.USES_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.WHEN_DATA;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.UsesStatementContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerCollisionDetector.detectCollidingChildUtil;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.EXIT;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructExtendedListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.UNHANDLED_PARSED_DATA;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.getValidNodeIdentifier;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.validateCardinalityMaxOne;
import static org.onosproject.yang.compiler.translator.tojava.YangDataModelFactory.getYangUsesNode;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 * data-def-stmt       = container-stmt /
 *                      leaf-stmt /
 *                      leaf-list-stmt /
 *                      list-stmt /
 *                      choice-stmt /
 *                      anyxml-stmt /
 *                      uses-stmt
 *
 * uses-stmt           = uses-keyword sep identifier-ref-arg-str optsep
 *                       (";" /
 *                        "{" stmtsep
 *                            ;; these stmts can appear in any order
 *                            [when-stmt stmtsep]
 *                            *(if-feature-stmt stmtsep)
 *                            [status-stmt stmtsep]
 *                            [description-stmt stmtsep]
 *                            [reference-stmt stmtsep]
 *                            *(refine-stmt stmtsep)
 *                            *(uses-augment-stmt stmtsep)
 *                        "}")
 *
 * ANTLR grammar rule
 * dataDefStatement : containerStatement
 *                 | leafStatement
 *                 | leafListStatement
 *                 | listStatement
 *                 | choiceStatement
 *                 | usesStatement;
 *
 * usesStatement : USES_KEYWORD string (STMTEND | LEFT_CURLY_BRACE (whenStatement | ifFeatureStatement
 *                 | statusStatement | descriptionStatement | referenceStatement | refineStatement
 *                 | usesAugmentStatement)* RIGHT_CURLY_BRACE);
 */

/**
 * Represents listener based call back function corresponding to the "uses"
 * rule defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class UsesListener {

    /**
     * Creates a new uses listener.
     */
    private UsesListener() {
    }

    /**
     * It is called when parser enters grammar rule (uses), it perform
     * validations and updates the data model tree.
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processUsesEntry(TreeWalkListener listener, UsesStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, USES_DATA, ctx.string().getText(), ENTRY);

        // Validate sub statement cardinality.
        validateSubStatementsCardinality(ctx);

        // Check for identifier collision
        int line = ctx.getStart().getLine();
        int charPositionInLine = ctx.getStart().getCharPositionInLine();

        detectCollidingChildUtil(listener, line, charPositionInLine, ctx.string().getText(), USES_DATA);
        Parsable curData = listener.getParsedDataStack().peek();

        if (curData instanceof YangModule || curData instanceof YangSubModule
                || curData instanceof YangContainer || curData instanceof YangList
                || curData instanceof YangUses || curData instanceof YangAugment
                || curData instanceof YangCase || curData instanceof YangGrouping
                || curData instanceof YangInput || curData instanceof YangOutput
                || curData instanceof YangNotification) {

            YangUses usesNode = getYangUsesNode(JAVA_GENERATION);

            usesNode.setLineNumber(ctx.getStart().getLine());
            usesNode.setCharPosition(ctx.getStart().getCharPositionInLine());
            usesNode.setFileName(listener.getFileName());
            YangNodeIdentifier nodeIdentifier = getValidNodeIdentifier(ctx.string().getText(), USES_DATA, ctx);
            usesNode.setNodeIdentifier(nodeIdentifier);
            usesNode.setCurrentGroupingDepth(listener.getGroupingDepth());
            YangNode curNode = (YangNode) curData;

            try {
                curNode.addChild(usesNode);
            } catch (DataModelException e) {
                throw new ParserException(constructExtendedListenerErrorMessage(UNHANDLED_PARSED_DATA,
                        USES_DATA, ctx.string().getText(), ENTRY, e.getMessage()));
            }
            listener.getParsedDataStack().push(usesNode);
        } else {
            throw new ParserException(constructListenerErrorMessage(INVALID_HOLDER,
                    USES_DATA, ctx.string().getText(), ENTRY));
        }
    }

    /**
     * It is called when parser exits from grammar rule (uses), it perform
     * validations and update the data model tree.
     *
     * @param listener Listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processUsesExit(TreeWalkListener listener,
                                       UsesStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, USES_DATA, ctx.string().getText(), EXIT);

        Parsable parsableUses = listener.getParsedDataStack().pop();
        if (!(parsableUses instanceof YangUses)) {
            throw new ParserException(constructListenerErrorMessage(INVALID_HOLDER, USES_DATA,
                    ctx.string().getText(), EXIT));
        }
        YangUses uses = (YangUses) parsableUses;
        int errorLine = ctx.getStart().getLine();
        int errorPosition = ctx.getStart().getCharPositionInLine();

        // Parent YANG node of uses to be added in resolution information.
        Parsable parentNode = listener.getParsedDataStack().peek();

        // Verify parent node of leaf
        if (!(parentNode instanceof YangNode)) {
            throw new ParserException(constructListenerErrorMessage(INVALID_HOLDER, USES_DATA,
                    ctx.string().getText(), EXIT));
        }

        // Add resolution information to the list
        YangResolutionInfoImpl resolutionInfo = new YangResolutionInfoImpl<YangUses>(uses,
                                                                                     (YangNode) parentNode, errorLine,
                                                                                     errorPosition);
        addToResolutionList(resolutionInfo, ctx);
    }

    // TODO linker to handle collision scenarios like leaf obtained by uses, conflicts with some existing leaf.

    /**
     * Validates the cardinality of case sub-statements as per grammar.
     *
     * @param ctx context object of the grammar rule
     */
    private static void validateSubStatementsCardinality(UsesStatementContext ctx) {
        validateCardinalityMaxOne(ctx.whenStatement(), WHEN_DATA, USES_DATA, ctx.string().getText());
        validateCardinalityMaxOne(ctx.statusStatement(), STATUS_DATA, USES_DATA, ctx.string().getText());
        validateCardinalityMaxOne(ctx.descriptionStatement(), DESCRIPTION_DATA, USES_DATA, ctx.string().getText());
        validateCardinalityMaxOne(ctx.referenceStatement(), REFERENCE_DATA, USES_DATA, ctx.string().getText());
    }

    /**
     * Add to resolution list.
     *
     * @param resolutionInfo resolution information.
     * @param ctx            context object of the grammar rule
     */
    private static void addToResolutionList(YangResolutionInfoImpl<YangUses> resolutionInfo,
                                            UsesStatementContext ctx) {

        try {
            addResolutionInfo(resolutionInfo);
        } catch (DataModelException e) {
            throw new ParserException(constructExtendedListenerErrorMessage(UNHANDLED_PARSED_DATA,
                    USES_DATA, ctx.string().getText(), EXIT, e.getMessage()));
        }
    }
}
