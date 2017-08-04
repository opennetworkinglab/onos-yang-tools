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

import org.antlr.v4.runtime.tree.TerminalNode;
import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangPatternRestriction;
import org.onosproject.yang.compiler.datamodel.YangStringRestriction;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.PATTERN_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.TYPE_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.getYangConstructType;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DERIVED;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.STRING;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.PatternStatementContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.EXIT;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_CURRENT_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 *  pattern-stmt        = pattern-keyword sep string optsep
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
 *  patternStatement : PATTERN_KEYWORD string (STMTEND |
 *  LEFT_CURLY_BRACE commonStatements RIGHT_CURLY_BRACE);
 */

/**
 * Represents listener based call back function corresponding to the "pattern"
 * rule defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class PatternRestrictionListener {

    private static final String E_INVALID_TYPE = "YANG file error : Pattern " +
            "can only be used to restrict the built-in type string or types" +
            " derived from string.";

    /**
     * Creates a new pattern restriction listener.
     */
    private PatternRestrictionListener() {
    }

    /**
     * Processes pattern restriction, when parser receives an input matching
     * the grammar rule (pattern), performs validation and updates the data
     * model tree.
     *
     * @param lis listener object
     * @param ctx context object
     */
    public static void processPatternRestrictionEntry(TreeWalkListener lis,
                                                      PatternStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(lis, MISSING_HOLDER, PATTERN_DATA,
                             ctx.string().getText(), ENTRY);

        Parsable tmpData = lis.getParsedDataStack().peek();
        if (tmpData.getYangConstructType() == TYPE_DATA) {
            YangType type = (YangType) tmpData;
            setPatternRestriction(lis, type, ctx);
        } else {
            throw new ParserException(constructListenerErrorMessage(
                    INVALID_HOLDER, PATTERN_DATA,
                    ctx.string().getText(), ENTRY));
        }
    }

    /**
     * Sets the pattern restriction to type.
     *
     * @param lis  listener object
     * @param type YANG type
     * @param ctx  context object
     */
    private static void setPatternRestriction(TreeWalkListener lis,
                                              YangType type,
                                              PatternStatementContext ctx) {

        if (type.getDataType() != STRING && type.getDataType() != DERIVED) {
            ParserException exc = new ParserException(E_INVALID_TYPE);
            exc.setLine(ctx.getStart().getLine());
            exc.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw exc;
        }

        // Validate and get valid pattern restriction string.
        String pattern = getValidPattern(ctx);

        YangPatternRestriction patRes = new YangPatternRestriction(pattern);
        patRes.setFileName(lis.getFileName());
        patRes.setCharPosition(ctx.getStart().getCharPositionInLine());
        patRes.setLineNumber(ctx.getStart().getLine());

        if (type.getDataType() == STRING) {
            YangStringRestriction strRes = (YangStringRestriction) type
                    .getDataTypeExtendedInfo();
            if (strRes == null) {
                strRes = new YangStringRestriction();
                strRes.setFileName(lis.getFileName());
                strRes.setCharPosition(ctx.getStart().getCharPositionInLine());
                strRes.setLineNumber(ctx.getStart().getLine());
                type.setDataTypeExtendedInfo(strRes);
            }
            strRes.addPaternRes(patRes);
        } else {
            YangDerivedInfo<?> info = (YangDerivedInfo<?>) type
                    .getDataTypeExtendedInfo();
            info.addPatternRes(patRes);
        }
        lis.getParsedDataStack().push(patRes);
    }

    /**
     * Performs validation and updates the data model tree.
     * It is called when parser exits from grammar rule (pattern).
     *
     * @param listener listener object
     * @param ctx      context object
     */
    public static void processPatternRestrictionExit(TreeWalkListener listener,
                                                     PatternStatementContext ctx) {

        String txt = ctx.string().getText();

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, PATTERN_DATA, txt, EXIT);

        Parsable tmpData = listener.getParsedDataStack().peek();
        if (!(tmpData instanceof YangPatternRestriction)) {
            throw new ParserException(constructListenerErrorMessage(
                    MISSING_CURRENT_HOLDER, PATTERN_DATA, txt, EXIT));
        }
        listener.getParsedDataStack().pop();
    }

    /**
     * Validates and return the valid pattern.
     *
     * @param ctx context object
     * @return validated string
     */
    private static String getValidPattern(PatternStatementContext ctx) {
        List<TerminalNode> patternList = ctx.string().STRING();
        StringBuilder inputPat = new StringBuilder();
        String compile;
        for (TerminalNode pattern : patternList) {
            inputPat.append(pattern.getText());
        }
        compile = inputPat.toString().replaceAll("[\'\"]", EMPTY_STRING);
        try {
            Pattern.compile(compile);
        } catch (PatternSyntaxException e) {
            ParserException exc = new ParserException(
                    "YANG file error : " + getYangConstructType(PATTERN_DATA)
                            + " name " + ctx.string().getText()
                            + " is not a valid regular expression");
            exc.setLine(ctx.getStart().getLine());
            exc.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw exc;
        }
        return compile;
    }
}
