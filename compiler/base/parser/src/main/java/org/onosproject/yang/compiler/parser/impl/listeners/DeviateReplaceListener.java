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

import org.onosproject.yang.compiler.datamodel.YangDeviateReplace;
import org.onosproject.yang.compiler.datamodel.YangDeviation;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DEVIATE_REPLACE;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.EXIT;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_CURRENT_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 *
 *   deviate-replace-stmt = deviate-keyword sep replace-keyword optsep
 *                         (";" /
 *                          "{" stmtsep
 *                              [type-stmt stmtsep]
 *                              [units-stmt stmtsep]
 *                              [default-stmt stmtsep]
 *                              [config-stmt stmtsep]
 *                              [mandatory-stmt stmtsep]
 *                              [min-elements-stmt stmtsep]
 *                              [max-elements-stmt stmtsep]
 *                          "}")
 *
 * ANTLR grammar rule
 *   deviateReplaceStatement: DEVIATE_KEYWORD REPLACE_KEYWORD (STMTEND
 *           | LEFT_CURLY_BRACE (typeStatement | unitsStatement
 *           | defaultStatement | configStatement
 *           | mandatoryStatement | minElementsStatement
 *           | maxElementsStatement)* RIGHT_CURLY_BRACE);
 */

/**
 * Represents listener based call back function corresponding to the "deviate
 * replace" rule defined in ANTLR grammar file for corresponding ABNF rule in
 * RFC 6020.
 */
public final class DeviateReplaceListener {

    //No instantiation
    private DeviateReplaceListener() {
    }

    /**
     * Performs validation and updates the data model tree. It is called when
     * parser receives an input matching the grammar rule(deviate replace).
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processDeviateReplaceEntry(TreeWalkListener listener,
                                                  GeneratedYangParser
                                                          .DeviateReplaceStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, DEVIATE_REPLACE,
                             null, ENTRY);

        // TODO : Validate sub-statements cardinality

        Parsable curData = listener.getParsedDataStack().peek();
        if (curData instanceof YangDeviation) {
            YangDeviation curNode = (YangDeviation) curData;
            YangDeviateReplace deviationReplace = new YangDeviateReplace();
            curNode.addDeviateReplace(deviationReplace);
            listener.getParsedDataStack().push(deviationReplace);
        } else {
            throw new ParserException(constructListenerErrorMessage(INVALID_HOLDER, DEVIATE_REPLACE,
                                                                    null, ENTRY));
        }
    }

    /**
     * Performs validation and updates the data model tree. It is called when
     * parser exits from grammar rule (deviate replace).
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processDeviateReplaceExit(TreeWalkListener listener,
                                                 GeneratedYangParser.DeviateReplaceStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, DEVIATE_REPLACE, null,
                             EXIT);

        if (listener.getParsedDataStack().peek() instanceof YangDeviateReplace) {
            listener.getParsedDataStack().pop();
        } else {
            throw new ParserException(constructListenerErrorMessage(MISSING_CURRENT_HOLDER, DEVIATE_REPLACE,
                                                                    null, EXIT));
        }
    }
}
