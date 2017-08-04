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

import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangRangeRestriction;
import org.onosproject.yang.compiler.datamodel.YangStringRestriction;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.LengthStatementContext;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.RestrictionResolver.processLengthRes;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.LENGTH_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.TYPE_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.BINARY;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DERIVED;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.STRING;
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
 *  length-stmt         = length-keyword sep length-arg-str optsep
 *                        (";" /
 *                         "{" stmtsep
 *                             ;; these stmts can appear in any order
 *                             [error-message-stmt stmtsep]
 *                             [error-app-tag-stmt stmtsep]
 *                             [description-stmt stmtsep]
 *                             [reference-stmt stmtsep]
 *                          "}")
 *
 *
 * ANTLR grammar rule
 * lengthStatement : LENGTH_KEYWORD length (STMTEND | LEFT_CURLY_BRACE
 * commonStatements RIGHT_CURLY_BRACE);
 */

/**
 * Represents listener based call back function corresponding to the "length"
 * rule defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class LengthRestrictionListener {

    private static final String E_INVALID_TYPE = "YANG file error : Length can" +
            " only be used to restrict the built-in type string/binary or " +
            "types derived from string/binary.";

    /**
     * Creates a new length restriction listener.
     */
    private LengthRestrictionListener() {
    }

    /**
     * Processes pattern restriction, when parser receives an input matching
     * the grammar rule (length), performs validation and updates the data
     * model tree.
     *
     * @param lis listener object
     * @param ctx context object
     */
    public static void processLengthRestrictionEntry(TreeWalkListener lis,
                                                     LengthStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(lis, MISSING_HOLDER, LENGTH_DATA,
                             ctx.length().getText(), ENTRY);

        Parsable tmpData = lis.getParsedDataStack().peek();
        if (tmpData.getYangConstructType() == TYPE_DATA) {
            YangType type = (YangType) tmpData;
            setLengthRestriction(lis, type, ctx);
        } else {
            throw new ParserException(constructListenerErrorMessage(
                    INVALID_HOLDER, LENGTH_DATA,
                    ctx.length().getText(), ENTRY));
        }
    }

    /**
     * Sets the length restriction to type.
     *
     * @param lis  listener object
     * @param type YANG type
     * @param ctx  context object
     */
    private static void setLengthRestriction(TreeWalkListener lis,
                                             YangType type,
                                             LengthStatementContext ctx) {

        int line = ctx.getStart().getLine();
        int pos = ctx.getStart().getCharPositionInLine();
        String txt = ctx.length().getText();
        YangDataTypes dataType = type.getDataType();

        YangRangeRestriction lenRes = new YangRangeRestriction(txt);
        lenRes.setFileName(lis.getFileName());
        lenRes.setCharPosition(pos);
        lenRes.setLineNumber(line);
        lis.getParsedDataStack().push(lenRes);

        if (dataType == DERIVED) {
            YangDerivedInfo info = (YangDerivedInfo<?>) type
                    .getDataTypeExtendedInfo();
            info.setLengthRes(lenRes);
            info.setFileName(lis.getFileName());
            info.setCharPosition(pos);
            info.setLineNumber(line);
            return;
        }

        if (dataType != STRING && dataType != BINARY) {
            ParserException exc = new ParserException(E_INVALID_TYPE);
            exc.setLine(line);
            exc.setCharPosition(pos);
            throw exc;
        }
        try {
            lenRes = processLengthRes(null, line, pos, false, lenRes,
                                      lis.getFileName());
        } catch (DataModelException e) {
            ParserException exc = new ParserException(e.getMessage());
            exc.setCharPosition(e.getCharPositionInLine());
            exc.setLine(e.getLineNumber());
            throw exc;
        }

        if (dataType == STRING) {
            YangStringRestriction strRes = (YangStringRestriction) type
                    .getDataTypeExtendedInfo();
            if (strRes == null) {
                strRes = new YangStringRestriction();
                strRes.setFileName(lis.getFileName());
                strRes.setCharPosition(ctx.getStart().getCharPositionInLine());
                strRes.setLineNumber(ctx.getStart().getLine());
                type.setDataTypeExtendedInfo(strRes);
            }
            strRes.setLengthRestriction(lenRes);
        } else {
            type.setDataTypeExtendedInfo(lenRes);
        }
    }

    /**
     * Performs validation and updates the data model tree.
     * It is called when parser exits from grammar rule (length).
     *
     * @param lis listener object
     * @param ctx context object
     */
    public static void processLengthRestrictionExit(TreeWalkListener lis,
                                                    LengthStatementContext ctx) {

        String txt = ctx.length().getText();

        // Check for stack to be non empty.
        checkStackIsNotEmpty(lis, MISSING_HOLDER, LENGTH_DATA, txt, EXIT);

        Parsable tmpData = lis.getParsedDataStack().peek();
        if (!(tmpData instanceof YangRangeRestriction)) {
            throw new ParserException(constructListenerErrorMessage(
                    MISSING_CURRENT_HOLDER, LENGTH_DATA, txt, EXIT));
        }
        lis.getParsedDataStack().pop();
    }
}
