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
 * typedef-stmt        = typedef-keyword sep identifier-arg-str optsep
 *                       "{" stmtsep
 *                           ;; these stmts can appear in any order
 *                           type-stmt stmtsep
 *                          [units-stmt stmtsep]
 *                           [default-stmt stmtsep]
 *                           [status-stmt stmtsep]
 *                           [description-stmt stmtsep]
 *                           [reference-stmt stmtsep]
 *                         "}"
 * default-stmt        = default-keyword sep string stmtend

 *
 * ANTLR grammar rule
 * typedefStatement : TYPEDEF_KEYWORD IDENTIFIER LEFT_CURLY_BRACE
 *                (typeStatement | unitsStatement | defaultStatement | statusStatement
 *                | descriptionStatement | referenceStatement)* RIGHT_CURLY_BRACE;
 * defaultStatement : DEFAULT_KEYWORD string STMTEND;
 */

import org.onosproject.yang.compiler.datamodel.YangDefault;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DEFAULT_DATA;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DefaultStatementContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.removeQuotesAndHandleConcat;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;

/**
 * Represents listener for default YANG statement.
 */
public final class DefaultListener {

    /**
     * Creates a new default listener.
     */
    private DefaultListener() {
    }

    /**
     * It is called when parser enters grammar rule (default), it perform
     * validations and updates the data model tree.
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processDefaultEntry(TreeWalkListener listener,
                                           DefaultStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, DEFAULT_DATA,
                             ctx.string().getText(), ENTRY);
        String value = removeQuotesAndHandleConcat(ctx.string().getText());

        Parsable tmpNode = listener.getParsedDataStack().peek();
        if (tmpNode instanceof YangDefault) {
            YangDefault defaultHolder = ((YangDefault) tmpNode);
            defaultHolder.setDefaultValueInString(value);
        } else {
            throw new ParserException(
                    constructListenerErrorMessage(INVALID_HOLDER,
                                                  DEFAULT_DATA,
                                                  ctx.string().getText(),
                                                  ENTRY));
        }
    }
}
