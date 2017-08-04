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

import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangDeviateAdd;
import org.onosproject.yang.compiler.datamodel.YangDeviateDelete;
import org.onosproject.yang.compiler.datamodel.YangUniqueHolder;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.UniqueStatementContext;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import java.util.List;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.UNIQUE_DATA;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.SPACE;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.removeQuotesAndHandleConcat;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.validateUniqueInList;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 * unique-stmt         = unique-keyword sep unique-arg-str stmtend
 *
 * ANTLR grammar rule
 * uniqueStatement: UNIQUE_KEYWORD unique STMTEND;
 * unique : string;
 */

/**
 * Represents listener based call back function corresponding to the "unique"
 * rule defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class UniqueListener {

    /**
     * Creates unique listener.
     */
    private UniqueListener() {
    }

    /**
     * Processes when parser receives an input matching the grammar rule
     * (unique), perform validations and updates the data model tree.
     *
     * @param listener listener's object
     * @param ctx      context object
     */
    public static void processUniqueEntry(TreeWalkListener listener,
                                          UniqueStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, UNIQUE_DATA,
                             ctx.unique().getText(), ENTRY);

        Parsable tmpData = listener.getParsedDataStack().peek();
        if (!(tmpData instanceof YangUniqueHolder)) {
            throw new ParserException(constructListenerErrorMessage(
                    INVALID_HOLDER, UNIQUE_DATA, ctx.unique().getText(),
                    ENTRY));
        }

        YangUniqueHolder holder = (YangUniqueHolder) tmpData;
        if (holder instanceof YangDeviateAdd ||
                holder instanceof YangDeviateDelete) {
            return;
        }
        String tmpVal = removeQuotesAndHandleConcat(ctx.unique().getText());

        String[] values = tmpVal.split(SPACE);
        for (String val : values) {
            List<YangAtomicPath> atomicPath = validateUniqueInList(
                    holder, val, ctx);
            holder.addUnique(atomicPath);
        }
    }
}