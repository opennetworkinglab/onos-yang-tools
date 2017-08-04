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

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 *
 *  deviation-stmt      = deviation-keyword sep
 *                        deviation-arg-str optsep
 *                        "{" stmtsep
 *                            ;; these stmts can appear in any order
 *                            [description-stmt stmtsep]
 *                            [reference-stmt stmtsep]
 *                            (deviate-not-supported-stmt /
 *                              1*(deviate-add-stmt /
 *                                 deviate-replace-stmt /
 *                                 deviate-delete-stmt))
 *                        "}"
 *
 * ANTLR grammar rule
 *   deviationStatement: DEVIATION_KEYWORD deviation LEFT_CURLY_BRACE (
 *       descriptionStatement | referenceStatement | deviateNotSupportedStatement
 *      | deviateAddStatement | deviateReplaceStatement
 *      | deviateDeleteStatement)* RIGHT_CURLY_BRACE;
 */

import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangDeviation;
import org.onosproject.yang.compiler.datamodel.YangDeviationHolder;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.linker.impl.YangResolutionInfoImpl;
import org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import java.util.List;

import static org.onosproject.yang.compiler.datamodel.YangNodeType.DEVIATION_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.addResolutionInfo;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DESCRIPTION_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DEVIATE_ADD;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DEVIATE_DELETE;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DEVIATE_NOT_SUPPORTED;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DEVIATE_REPLACE;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DEVIATION_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.REFERENCE_DATA;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerCollisionDetector.detectCollidingChildUtil;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.EXIT;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructExtendedListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_CURRENT_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.UNHANDLED_PARSED_DATA;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.getValidAbsoluteSchemaNodeId;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.removeQuotesAndHandleConcat;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.validateCardinalityMaxOne;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.validateCardinalityMutuallyExclusive;

/**
 * Represents listener based call back function corresponding to the "deviation"
 * rule defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class DeviationListener {

    /**
     * Creates a new deviation listener.
     */
    private DeviationListener() {
    }

    /**
     * Performs validation and updates the data model tree. It is called when
     * parser receives an input matching the grammar rule(deviation).
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processDeviationEntry(TreeWalkListener listener,
                                             GeneratedYangParser.DeviationStatementContext ctx) {

        String deviationArg = removeQuotesAndHandleConcat(ctx.deviation().getText());

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, DEVIATION_DATA,
                             deviationArg, ENTRY);

        // Validates deviation argument string
        List<YangAtomicPath> targetNode = getValidAbsoluteSchemaNodeId(deviationArg,
                                                                       DEVIATION_DATA, ctx);

        validateSubStatementsCardinality(ctx);

        // Check for identifier collision
        int line = ctx.getStart().getLine();
        int charPositionInLine = ctx.getStart().getCharPositionInLine();
        detectCollidingChildUtil(listener, line, charPositionInLine,
                                 deviationArg, DEVIATION_DATA);

        Parsable curData = listener.getParsedDataStack().peek();
        if (curData instanceof YangDeviationHolder) {
            YangDeviation deviation = new YangDeviation(DEVIATION_NODE, null);
            deviation.setName(deviationArg);
            deviation.setLineNumber(line);
            deviation.setCharPosition(charPositionInLine);
            deviation.setFileName(listener.getFileName());
            deviation.setTargetNode(targetNode);
            if (!ctx.deviateNotSupportedStatement().isEmpty()) {
                deviation.setDeviateNotSupported(true);
            }
            YangNode curNode = (YangNode) curData;
            try {
                curNode.addChild(deviation);
                ((YangDeviationHolder) curNode).setModuleForDeviation(true);
            } catch (DataModelException e) {
                throw new ParserException(
                        constructExtendedListenerErrorMessage(UNHANDLED_PARSED_DATA,
                                                              DEVIATION_DATA,
                                                              deviationArg,
                                                              ENTRY, e.getMessage()));
            }
            listener.getParsedDataStack().push(deviation);

            // Adds resolution info to the list
            YangResolutionInfoImpl<YangDeviation> info =
                    new YangResolutionInfoImpl<>(deviation, deviation.getParent(),
                                                 line, charPositionInLine);
            addToResolution(info, ctx);
        } else {
            throw new ParserException(constructListenerErrorMessage(INVALID_HOLDER,
                                                                    DEVIATION_DATA,
                                                                    deviationArg,
                                                                    ENTRY));
        }
    }

    /**
     * Performs validation and updates the data model tree. It is called when
     * parser exits from grammar rule (deviation).
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processDeviationExit(TreeWalkListener listener,
                                            GeneratedYangParser.DeviationStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, DEVIATION_DATA, ctx
                .deviation().getText(), EXIT);

        if (listener.getParsedDataStack().peek() instanceof YangDeviation) {
            listener.getParsedDataStack().pop();
        } else {
            throw new ParserException(constructListenerErrorMessage(MISSING_CURRENT_HOLDER, DEVIATION_DATA,
                                                                    ctx.deviation().getText(), EXIT));
        }
    }

    /**
     * Validates the cardinality of deviation sub-statements as per grammar.
     *
     * @param ctx context object of the grammar rule
     */
    private static void validateSubStatementsCardinality(GeneratedYangParser
                                                                 .DeviationStatementContext ctx) {
        validateCardinalityMaxOne(ctx.descriptionStatement(),
                                  DESCRIPTION_DATA, DEVIATION_DATA,
                                  ctx.deviation().getText());
        validateCardinalityMaxOne(ctx.referenceStatement(), REFERENCE_DATA,
                                  DEVIATION_DATA, ctx.deviation().getText());
        validateCardinalityMaxOne(ctx.deviateNotSupportedStatement(),
                                  DEVIATE_NOT_SUPPORTED,
                                  DEVIATION_DATA, ctx.deviation().getText());
        validateCardinalityMutuallyExclusive(ctx.deviateNotSupportedStatement(),
                                             DEVIATE_NOT_SUPPORTED,
                                             ctx.deviateAddStatement(),
                                             DEVIATE_ADD,
                                             DEVIATION_DATA,
                                             ctx.deviation().getText(),
                                             ctx);
        validateCardinalityMutuallyExclusive(ctx.deviateNotSupportedStatement(),
                                             DEVIATE_NOT_SUPPORTED,
                                             ctx.deviateReplaceStatement(),
                                             DEVIATE_REPLACE,
                                             DEVIATION_DATA,
                                             ctx.deviation().getText(),
                                             ctx);
        validateCardinalityMutuallyExclusive(ctx.deviateNotSupportedStatement(),
                                             DEVIATE_NOT_SUPPORTED,
                                             ctx.deviateDeleteStatement(),
                                             DEVIATE_DELETE,
                                             DEVIATION_DATA,
                                             ctx.deviation().getText(),
                                             ctx);
    }

    /**
     * Add to resolution list.
     *
     * @param info resolution info
     * @param ctx  context object
     */
    private static void addToResolution(YangResolutionInfoImpl<YangDeviation> info,
                                        GeneratedYangParser.DeviationStatementContext ctx) {
        try {
            addResolutionInfo(info);
        } catch (DataModelException e) {
            throw new ParserException(constructExtendedListenerErrorMessage(
                    UNHANDLED_PARSED_DATA, DEVIATION_DATA,
                    ctx.deviation().getText(), EXIT, e.getMessage()));
        }
    }
}
