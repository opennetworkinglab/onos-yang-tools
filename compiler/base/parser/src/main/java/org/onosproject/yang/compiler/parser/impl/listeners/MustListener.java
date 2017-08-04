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

import org.onosproject.yang.compiler.datamodel.YangMust;
import org.onosproject.yang.compiler.datamodel.YangMustHolder;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.MUST_DATA;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.MustStatementContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.EXIT;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_CURRENT_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.removeQuotesAndHandleConcat;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 *
 *  must-stmt           = must-keyword sep string optsep
 *                        (";" /
 *                         "{" stmtsep
 *                             ;; these stmts can appear in any order
 *                             [error-message-stmt stmtsep]
 *                             [error-app-tag-stmt stmtsep]
 *                             [description-stmt stmtsep]
 *                             [reference-stmt stmtsep]
 *                          "}")
 *
 * ANTLR grammar rule
 * mustStatement : MUST_KEYWORD string (STMTEND | LEFT_CURLY_BRACE commonStatements RIGHT_CURLY_BRACE);
 */

/**
 * Represents listener based call back function corresponding to the
 * "must" rule defined in ANTLR grammar file for corresponding ABNF rule
 * in RFC 6020.
 */
public final class MustListener {

    /**
     * Creates a new must listener.
     */
    private MustListener() {
    }

    /**
     * Perform validations and updates the data model tree.It is called when parser
     * receives an input matching the grammar rule (must).
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processMustEntry(TreeWalkListener listener,
                                        MustStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, MUST_DATA, ctx.string().getText(), ENTRY);
        String constraint = removeQuotesAndHandleConcat(ctx.string().getText());

        // Obtain the node of the stack.
        Parsable tmpNode = listener.getParsedDataStack().peek();
        if (tmpNode instanceof YangMustHolder) {

            YangMust must = new YangMust();
            must.setConstraint(constraint);

            must.setLineNumber(ctx.getStart().getLine());
            must.setCharPosition(ctx.getStart().getCharPositionInLine());
            must.setFileName(listener.getFileName());
            YangMustHolder mustHolder = (YangMustHolder) tmpNode;
            mustHolder.addMust(must);

            listener.getParsedDataStack().push(must);
        } else {
            throw new ParserException(constructListenerErrorMessage(INVALID_HOLDER, MUST_DATA,
                                                                    ctx.string().getText(), ENTRY));
        }
    }

    /**
     * Performs validation and updates the data model tree.It is called when parser
     * exits from grammar rule (must).
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processMustExit(TreeWalkListener listener,
                                       MustStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, MUST_DATA, ctx.string().getText(), EXIT);

        if (listener.getParsedDataStack().peek() instanceof YangMust) {
            listener.getParsedDataStack().pop();
        } else {
            throw new ParserException(constructListenerErrorMessage(MISSING_CURRENT_HOLDER, MUST_DATA,
                                                                    ctx.string().getText(), EXIT));
        }
    }
}
