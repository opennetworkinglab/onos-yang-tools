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

import org.onosproject.yang.compiler.datamodel.YangDecimal64;
import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangRangeRestriction;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.RestrictionResolver.processRangeRestriction;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.RANGE_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.TYPE_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypeUtils.isOfRangeRestrictedType;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DECIMAL64;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DERIVED;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RangeStatementContext;
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
 *  range-stmt          = range-keyword sep range-arg-str optsep
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
 *  rangeStatement : RANGE_KEYWORD range (STMTEND | LEFT_CURLY_BRACE
 *  commonStatements RIGHT_CURLY_BRACE);
 */

/**
 * Represents listener based call back function corresponding to the "range"
 * rule defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class RangeRestrictionListener {

    private static final String E_INVALID_TYPE = "YANG file error: Range " +
            "restriction can't be applied to a given type";

    /**
     * Creates a new range restriction listener.
     */
    private RangeRestrictionListener() {
    }

    /**
     * Processes pattern restriction, when parser receives an input matching
     * the grammar rule (range), performs validation and updates the data model
     * tree.
     *
     * @param lis listener object
     * @param ctx context object
     */
    public static void processRangeRestrictionEntry(TreeWalkListener lis,
                                                    RangeStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(lis, MISSING_HOLDER, RANGE_DATA,
                             ctx.range().getText(), ENTRY);

        Parsable tmpData = lis.getParsedDataStack().peek();
        if (tmpData.getYangConstructType() == TYPE_DATA) {
            YangType type = (YangType) tmpData;
            setRangeRestriction(lis, type, ctx);
        } else {
            throw new ParserException(constructListenerErrorMessage(
                    INVALID_HOLDER, RANGE_DATA, ctx.range().getText(), ENTRY));
        }
    }

    /**
     * Sets the range restriction to type.
     *
     * @param lis  listener's object
     * @param type YANG type
     * @param ctx  context object
     */
    private static void setRangeRestriction(TreeWalkListener lis, YangType type,
                                            RangeStatementContext ctx) {
        String txt = ctx.range().getText();
        int line = ctx.getStart().getLine();
        int pos = ctx.getStart().getCharPositionInLine();
        YangDataTypes dataType = type.getDataType();

        YangRangeRestriction ranRes = new YangRangeRestriction(txt);
        ranRes.setFileName(lis.getFileName());
        ranRes.setCharPosition(pos);
        ranRes.setLineNumber(line);
        lis.getParsedDataStack().push(ranRes);

        if (dataType == DERIVED) {
            YangDerivedInfo info = (YangDerivedInfo<?>) type
                    .getDataTypeExtendedInfo();
            info.setRangeRes(ranRes);
            info.setFileName(lis.getFileName());
            info.setCharPosition(pos);
            info.setLineNumber(line);
            return;
        }

        if (!(isOfRangeRestrictedType(dataType)) && (dataType != DECIMAL64)) {
            ParserException exc = new ParserException(E_INVALID_TYPE);
            exc.setLine(line);
            exc.setCharPosition(pos);
            throw exc;
        }
        try {
            if (dataType == DECIMAL64) {
                YangDecimal64 deci64 = (YangDecimal64) type
                        .getDataTypeExtendedInfo();
                ranRes = processRangeRestriction(
                        deci64.getDefaultRangeRestriction(), line, pos, true,
                        ranRes, dataType, lis.getFileName());
            } else {
                ranRes = processRangeRestriction(null, line, pos, false, ranRes,
                                                 dataType, lis.getFileName());
            }
        } catch (DataModelException e) {
            ParserException exc = new ParserException(e.getMessage());
            exc.setCharPosition(e.getCharPositionInLine());
            exc.setLine(e.getLineNumber());
            throw exc;
        }

        if (ranRes != null) {
            if (dataType == DECIMAL64) {
                ((YangDecimal64<YangRangeRestriction>) type
                        .getDataTypeExtendedInfo())
                        .setRangeRestrictedExtendedInfo(ranRes);
            } else {
                type.setDataTypeExtendedInfo(ranRes);
            }
        }
    }

    /**
     * Performs validation and updates the data model tree.
     * It is called when parser exits from grammar rule (range).
     *
     * @param lis listener object
     * @param ctx context object
     */
    public static void processRangeRestrictionExit(TreeWalkListener lis,
                                                   RangeStatementContext ctx) {

        String txt = ctx.range().getText();

        // Check for stack to be non empty.
        checkStackIsNotEmpty(lis, MISSING_HOLDER, RANGE_DATA, txt, EXIT);

        Parsable tmpData = lis.getParsedDataStack().peek();
        if (!(tmpData instanceof YangRangeRestriction)) {
            throw new ParserException(constructListenerErrorMessage(
                    MISSING_CURRENT_HOLDER, RANGE_DATA, txt, EXIT));
        }
        lis.getParsedDataStack().pop();
    }
}
