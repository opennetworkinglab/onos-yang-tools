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

package org.onosproject.yang.compiler.parser.impl.parserutils;

import org.antlr.v4.runtime.ParserRuleContext;
import org.onosproject.yang.compiler.datamodel.YangAtomicPath;
import org.onosproject.yang.compiler.datamodel.YangAugment;
import org.onosproject.yang.compiler.datamodel.YangImport;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.YangPathArgType;
import org.onosproject.yang.compiler.datamodel.YangPathPredicate;
import org.onosproject.yang.compiler.datamodel.YangReferenceResolver;
import org.onosproject.yang.compiler.datamodel.YangRelativePath;
import org.onosproject.yang.compiler.datamodel.YangSubModule;
import org.onosproject.yang.compiler.datamodel.YangUniqueHolder;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.YangConstructType;
import org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.AugmentStatementContext;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.translator.tojava.javamodel.YangJavaAugmentTranslator;
import org.slf4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.onosproject.yang.compiler.datamodel.YangPathOperator.EQUALTO;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.AUGMENT_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.UNIQUE_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.getYangConstructType;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.PathStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.YangVersionStatementContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructExtendedListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.UNHANDLED_PARSED_DATA;
import static org.onosproject.yang.compiler.utils.UtilConstants.ADD;
import static org.onosproject.yang.compiler.utils.UtilConstants.ANCESTOR;
import static org.onosproject.yang.compiler.utils.UtilConstants.AT;
import static org.onosproject.yang.compiler.utils.UtilConstants.CARET;
import static org.onosproject.yang.compiler.utils.UtilConstants.CHAR_OF_OPEN_SQUARE_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.CHAR_OF_SLASH;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.CURRENT;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.FALSE;
import static org.onosproject.yang.compiler.utils.UtilConstants.IN;
import static org.onosproject.yang.compiler.utils.UtilConstants.INVALID_TREE;
import static org.onosproject.yang.compiler.utils.UtilConstants.ONE;
import static org.onosproject.yang.compiler.utils.UtilConstants.ONE_DOT_ONE;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_SQUARE_BRACKET;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUOTES;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH_ANCESTOR;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH_FOR_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.TRUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_FILE_ERROR;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents an utility for listener.
 */
public final class ListenerUtil {
    public static final String SPACE = " ";
    public static final String LINE_NUMBER = "line number ";
    public static final String CHARACTER_POSITION = "character position ";
    public static final String FILE = "file ";
    private static final Logger log = getLogger(ListenerUtil.class);
    private static final Pattern IDENTIFIER_PATTERN =
            Pattern.compile("[a-zA-Z_][a-zA-Z0-9_.-]*");
    private static final String DATE_PATTERN =
            "[0-9]{4}-([0-9]{2}|[0-9])-([0-9]{2}|[0-9])";
    private static final String NON_NEGATIVE_INTEGER_PATTERN = "[0-9]+";
    private static final Pattern INTEGER_PATTERN =
            Pattern.compile("[-][0-9]+|[0-9]+");
    private static final Pattern PREDICATE =
            Pattern.compile("\\[(.*?)\\]");
    private static final String XML = "xml";
    private static final int IDENTIFIER_LENGTH = 64;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String REGEX_EQUAL = "[=]";
    private static final String REGEX_OPEN_BRACE = "[(]";
    private static final String E_DES_NODE = "YANG file error : The " +
            "reference path of descendant schema is not pointing to node " +
            "inside itself";
    private static final String E_DES_FORMAT = "YANG file error : The " +
            "descendant path must not start with a slash(/)";

    // No instantiation.
    private ListenerUtil() {
    }

    /**
     * Removes doubles quotes and concatenates if string has plus symbol.
     *
     * @param yangStringData string from yang file
     * @return concatenated string after removing double quotes
     */
    public static String removeQuotesAndHandleConcat(String yangStringData) {

        yangStringData = yangStringData.replaceAll("[\'\"]", EMPTY_STRING);
        String[] tmpData = yangStringData.split(Pattern.quote(ADD));
        StringBuilder builder = new StringBuilder();
        for (String yangString : tmpData) {
            builder.append(yangString);
        }
        return builder.toString();
    }

    /**
     * Validates identifier and returns concatenated string if string contains plus symbol.
     *
     * @param identifier    string from yang file
     * @param yangConstruct yang construct for creating error message
     * @param ctx           yang construct's context to get the line number and character position
     * @return concatenated string after removing double quotes
     */
    public static String getValidIdentifier(String identifier,
                                            YangConstructType yangConstruct,
                                            ParserRuleContext ctx) {

        String identifierString = removeQuotesAndHandleConcat(identifier);
        ParserException parserException;

        if (identifierString.length() > IDENTIFIER_LENGTH) {
            parserException = new ParserException("YANG file error : " +
                                                          getYangConstructType(yangConstruct) + " name " +
                                                          identifierString + " is " +
                                                          "greater than 64 characters.");
        } else if (!IDENTIFIER_PATTERN.matcher(identifierString).matches()) {
            parserException = new ParserException("YANG file error : " +
                                                          getYangConstructType(yangConstruct) + " name " +
                                                          identifierString + " is not " +
                                                          "valid.");
        } else if (identifierString.toLowerCase().startsWith(XML)) {
            parserException = new ParserException("YANG file error : " +
                                                          getYangConstructType(yangConstruct) + " identifier " +
                                                          identifierString +
                                                          " must not start with (('X'|'x') ('M'|'m') ('L'|'l')).");
        } else {
            return identifierString;
        }

        parserException.setLine(ctx.getStart().getLine());
        parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
        throw parserException;
    }

    /**
     * Validates the revision date.
     *
     * @param dateToValidate input revision date
     * @return validation result, true for success, false for failure
     */
    public static boolean isDateValid(String dateToValidate) {
        if (dateToValidate == null || !dateToValidate.matches(DATE_PATTERN)) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false);

        try {
            //if not valid, it will throw ParseException
            sdf.parse(dateToValidate);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    /**
     * Validates YANG version.
     *
     * @param ctx version context object of the grammar rule
     * @return valid version
     */
    public static String getValidVersion(YangVersionStatementContext ctx) {

        String value = removeQuotesAndHandleConcat(ctx.version().getText());
        if (value.equals(ONE) || value.equals(ONE_DOT_ONE)) {
            return value;
        }
        ParserException parserException = new ParserException("YANG file error: Input version not supported");
        parserException.setLine(ctx.getStart().getLine());
        parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
        throw parserException;
    }

    /**
     * Validates non negative integer value.
     *
     * @param integerValue  integer to be validated
     * @param yangConstruct yang construct for creating error message
     * @param ctx           context object of the grammar rule
     * @return valid non negative integer value
     */
    public static int getValidNonNegativeIntegerValue(String integerValue, YangConstructType yangConstruct,
                                                      ParserRuleContext ctx) {

        String value = removeQuotesAndHandleConcat(integerValue);
        if (!value.matches(NON_NEGATIVE_INTEGER_PATTERN)) {
            ParserException parserException = new ParserException("YANG file error : " +
                                                                          getYangConstructType(yangConstruct) +
                                                                          " value " + value + " is not valid.");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }

        int valueInInteger;
        try {
            valueInInteger = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            ParserException parserException = new ParserException("YANG file error : " +
                                                                          getYangConstructType(yangConstruct) +
                                                                          " value " + value + " is not valid.");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }
        return valueInInteger;
    }

    /**
     * Validates integer value.
     *
     * @param integerValue  integer to be validated
     * @param yangConstruct yang construct for creating error message
     * @param ctx           context object of the grammar rule
     * @return valid integer value
     */
    public static int getValidIntegerValue(String integerValue, YangConstructType yangConstruct,
                                           ParserRuleContext ctx) {

        String value = removeQuotesAndHandleConcat(integerValue);
        if (!INTEGER_PATTERN.matcher(value).matches()) {
            ParserException parserException = new ParserException("YANG file error : " +
                                                                          getYangConstructType(yangConstruct) +
                                                                          " value " + value + " is not valid.");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }

        int valueInInteger;
        try {
            valueInInteger = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            ParserException parserException = new ParserException("YANG file error : " +
                                                                          getYangConstructType(yangConstruct) +
                                                                          " value " + value + " is not valid.");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }
        return valueInInteger;
    }

    /**
     * Validates boolean value.
     *
     * @param booleanValue  value to be validated
     * @param yangConstruct yang construct for creating error message
     * @param ctx           context object of the grammar rule
     * @return boolean value either true or false
     */
    public static boolean getValidBooleanValue(String booleanValue, YangConstructType yangConstruct,
                                               ParserRuleContext ctx) {

        String value = removeQuotesAndHandleConcat(booleanValue);
        if (value.equals(TRUE)) {
            return true;
        } else if (value.equals(FALSE)) {
            return false;
        } else {
            ParserException parserException = new ParserException("YANG file error : " +
                                                                          getYangConstructType(yangConstruct) +
                                                                          " value " + value + " is not valid.");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }
    }

    /**
     * Returns current date and makes it in usable format for revision.
     *
     * @return usable current date format for revision
     */
    public static Date getCurrentDateForRevision() {

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        Date date = new Date();
        String dateInString = dateFormat.format(date);
        try {
            //if not valid, it will throw ParseException
            Date now = dateFormat.parse(dateInString);
            return date;
        } catch (ParseException e) {
            ParserException parserException = new ParserException("YANG file error: Input date is not correct");
            throw parserException;
        }
    }

    /**
     * Checks and return valid node identifier.
     *
     * @param nodeIdentifierString string from yang file
     * @param yangConstruct        yang construct for creating error message
     * @param ctx                  yang construct's context to get the line number and character position
     * @return valid node identifier
     */
    public static YangNodeIdentifier getValidNodeIdentifier(String nodeIdentifierString,
                                                            YangConstructType yangConstruct, ParserRuleContext ctx) {
        String tmpIdentifierString = removeQuotesAndHandleConcat(nodeIdentifierString);
        String[] tmpData = tmpIdentifierString.split(Pattern.quote(COLON));
        if (tmpData.length == 1) {
            YangNodeIdentifier nodeIdentifier = new YangNodeIdentifier();
            nodeIdentifier.setName(getValidIdentifier(tmpData[0], yangConstruct, ctx));
            return nodeIdentifier;
        } else if (tmpData.length == 2) {
            YangNodeIdentifier nodeIdentifier = new YangNodeIdentifier();
            nodeIdentifier.setPrefix(getValidIdentifier(tmpData[0], yangConstruct, ctx));
            nodeIdentifier.setName(getValidIdentifier(tmpData[1], yangConstruct, ctx));
            return nodeIdentifier;
        } else {
            ParserException parserException = new ParserException("YANG file error : " +
                                                                          getYangConstructType(yangConstruct) +
                                                                          " name " + nodeIdentifierString +
                                                                          " is not valid.");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }
    }

    /**
     * Validates the prefix of the YANG file where leaf-ref is present and
     * puts it in the map of node and prefix in leaf-ref.
     *
     * @param atomicList atomic content list in leaf-ref
     * @param leafRef    YANG leaf-ref
     */
    private static void valPrefix(List<YangAtomicPath> atomicList,
                                  YangLeafRef leafRef) {

        for (YangAtomicPath atomicPath : atomicList) {
            String prefix = atomicPath.getNodeIdentifier().getPrefix();
            YangNode parent = leafRef.getParentNode();
            YangNode rootNode = getRootNode(parent);

            List<YangImport> imports;
            if (rootNode instanceof YangModule) {
                imports = ((YangModule) rootNode).getImportList();
            } else {
                imports = ((YangSubModule) rootNode).getImportList();
            }
            updatePrefixWithNode(rootNode, imports, prefix, leafRef);
        }
    }

    /**
     * Updates the prefix and its respective node in the leaf-ref by taking
     * the node from the import list of the root node which matches with the
     * prefix.
     *
     * @param root    root node
     * @param imports import list
     * @param prefix  prefix in path
     * @param leafRef YANG leaf-ref
     */
    private static void updatePrefixWithNode(YangNode root,
                                             List<YangImport> imports,
                                             String prefix,
                                             YangLeafRef<?> leafRef) {

        Map<String, String> prefixMap = leafRef.getPrefixAndNode();
        if (prefixMap == null) {
            prefixMap = new HashMap<>();
            leafRef.setPrefixAndNode(prefixMap);
        }

        if (prefix == null ||
                prefix.equals(((YangReferenceResolver) root).getPrefix())) {
            prefixMap.put(prefix, root.getName());
            return;
        }

        if (imports != null) {
            for (YangImport yangImp : imports) {
                if (yangImp.getPrefixId().equals(prefix)) {
                    prefixMap.put(prefix, yangImp.getModuleName());
                }
            }
        }
    }

    /**
     * Returns the root node from the current node.
     *
     * @param node YANG node
     * @return root node
     */
    private static YangNode getRootNode(YangNode node) {

        YangNode curNode = node;
        while (!(curNode instanceof YangModule) &&
                !(curNode instanceof YangSubModule)) {
            if (curNode == null) {
                throw new ParserException(INVALID_TREE);
            }
            curNode = curNode.getParent();
        }
        return curNode;
    }

    /**
     * Parses the uses augment statement, validates the statement and creates
     * a list of YANG atomic path.
     *
     * @param uses YANG uses
     * @param ctx  YANG construct
     * @return YANG atomic path list
     */
    public static List<YangAtomicPath> parseUsesAugment(YangNode uses,
                                                        AugmentStatementContext ctx) {
        String val = removeQuotesAndHandleConcat(ctx.augment().getText());
        String rootPre = getRootPrefix(uses);
        return validateDesSchemaNode(val, rootPre, ctx, AUGMENT_DATA);
    }

    /**
     * Validates the descendant schema node id, after parsing, by converting
     * it to YANG atomic path.
     *
     * @param val    descendant schema
     * @param prefix current file's prefix
     * @param ctx    yang construct
     * @param type   YANG construct type
     * @return list of YANG atomic path
     */
    private static List<YangAtomicPath> validateDesSchemaNode(String val,
                                                              String prefix,
                                                              ParserRuleContext ctx,
                                                              YangConstructType type) {
        if (val.startsWith(SLASH_FOR_STRING)) {
            ParserException exc = new ParserException(E_DES_FORMAT);
            exc.setLine(ctx.getStart().getLine());
            exc.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw exc;
        }
        List<YangAtomicPath> pathList = new LinkedList<>();
        String[] path = val.split(SLASH_FOR_STRING);
        for (String uniVal : path) {
            YangAtomicPath atomicPath = new YangAtomicPath();
            YangNodeIdentifier id = getValidNodeIdentifier(uniVal, type, ctx);
            atomicPath.setNodeIdentifier(id);
            pathList.add(atomicPath);
            if (id.getPrefix() != null &&
                    !id.getPrefix().equals(prefix)) {
                ParserException exc = new ParserException(E_DES_NODE);
                exc.setLine(ctx.getStart().getLine());
                exc.setCharPosition(ctx.getStart().getCharPositionInLine());
                throw exc;
            }
        }
        return pathList;
    }

    /**
     * Adds the unique holder to the module or sub-module node.
     *
     * @param holder unique holder
     */
    public static void addUniqueHolderToRoot(YangUniqueHolder holder) {
        List<List<YangAtomicPath>> uniques = holder.getPathList();
        if (uniques != null && !uniques.isEmpty()) {
            YangReferenceResolver root = (YangReferenceResolver) getRootNode(
                    (YangNode) holder);
            root.addToUniqueHolderList(holder);
        }
    }

    /**
     * Validates unique from the list.
     *
     * @param holder unique holder
     * @param val    path of unique
     * @param ctx    yang construct's context
     * @return list of yang atomic path
     */
    public static List<YangAtomicPath> validateUniqueInList(YangUniqueHolder holder,
                                                            String val,
                                                            ParserRuleContext ctx) {
        YangNode node = (YangNode) holder;
        String rootPre = getRootPrefix(node);
        return validateDesSchemaNode(val, rootPre, ctx, UNIQUE_DATA);
    }

    /**
     * Returns the prefix of the root node from any node inside it.
     *
     * @param curNode YANG node
     * @return prefix of the root node
     */
    private static String getRootPrefix(YangNode curNode) {

        String prefix;
        YangNode node = getRootNode(curNode);
        if (node instanceof YangModule) {
            YangModule yangModule = (YangModule) node;
            prefix = yangModule.getPrefix();
        } else {
            YangSubModule yangSubModule = (YangSubModule) node;
            prefix = yangSubModule.getPrefix();
        }
        return prefix;
    }

    /**
     * Returns the matched first path predicate in a given string. Returns
     * null if match is not found.
     *
     * @param str string to be matched
     * @return the matched string
     */
    private static String getMatchedPredicate(String str) {

        Matcher matcher = PREDICATE.matcher(str);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    /**
     * Checks and return valid absolute schema node id.
     *
     * @param argumentString    string from yang file
     * @param yangConstructType yang construct for creating error message
     * @param ctx               yang construct's context to get the line number and character position
     * @return target nodes list of absolute schema node id
     */
    public static List<YangAtomicPath> getValidAbsoluteSchemaNodeId(String argumentString,
                                                                    YangConstructType yangConstructType,
                                                                    ParserRuleContext ctx) {

        List<YangAtomicPath> targetNodes = new ArrayList<>();
        YangNodeIdentifier yangNodeIdentifier;
        String tmpSchemaNodeId = removeQuotesAndHandleConcat(argumentString);

        // absolute-schema-nodeid = 1*("/" node-identifier)
        if (!tmpSchemaNodeId.startsWith(SLASH)) {
            ParserException parserException =
                    new ParserException("YANG file error : " + getYangConstructType(yangConstructType) +
                                                " name " + argumentString + "is not valid");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }
        String[] tmpData = tmpSchemaNodeId.replaceFirst(CARET + SLASH, EMPTY_STRING).split(SLASH);
        for (String nodeIdentifiers : tmpData) {
            yangNodeIdentifier = getValidNodeIdentifier(nodeIdentifiers, yangConstructType, ctx);
            YangAtomicPath yangAbsPath = new YangAtomicPath();
            yangAbsPath.setNodeIdentifier(yangNodeIdentifier);
            targetNodes.add(yangAbsPath);
        }
        return targetNodes;
    }

    /**
     * Throws parser exception for unsupported YANG constructs.
     *
     * @param type       construct type
     * @param ctx        construct context
     * @param errorInfo  error msg
     * @param fileName   YANG file name
     * @param identifier identifier of the node
     */
    public static void handleUnsupportedYangConstruct(YangConstructType type,
                                                      ParserRuleContext ctx,
                                                      String errorInfo,
                                                      String fileName,
                                                      String identifier) {
        StringBuilder b = new StringBuilder();
        int lineNumber = ctx.getStart().getLine();
        int charPostion = ctx.getStart().getCharPositionInLine();
        b.append(YANG_FILE_ERROR).append(QUOTES).append(
                getYangConstructType(type)).append(SPACE).append(identifier)
                .append(QUOTES).append(AT).append(LINE_NUMBER)
                .append(lineNumber).append(AT).append(CHARACTER_POSITION)
                .append(charPostion).append(IN).append(FILE)
                .append(fileName).append(errorInfo);
        log.info(b.toString());
    }

    /**
     * Returns date and makes it in usable format for revision.
     *
     * @param dateInString date argument string from yang file
     * @param ctx          yang construct's context to get the line number and character position
     * @return date format for revision
     */
    public static Date getValidDateFromString(String dateInString, ParserRuleContext ctx) {
        String dateArgument = removeQuotesAndHandleConcat(dateInString);
        if (!dateArgument.matches(DATE_PATTERN)) {
            ParserException parserException = new ParserException("YANG file error: Input date is not correct");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false);

        try {
            //if not valid, it will throw ParseException
            return sdf.parse(dateArgument);
        } catch (ParseException e) {
            ParserException parserException = new ParserException("YANG file error: Input date is not correct");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }
    }

    /**
     * Checks and return valid prefix.
     *
     * @param inputString   string from yang file
     * @param yangConstruct yang construct for creating error message
     * @param ctx           yang construct's context to get the line number and character position
     * @return valid prefix
     */
    public static String getValidPrefix(String inputString,
                                        YangConstructType yangConstruct, ParserRuleContext ctx) {
        String tmpPrefixString = removeQuotesAndHandleConcat(inputString);
        String[] tmpData = tmpPrefixString.split(Pattern.quote(COLON));
        if (tmpData.length == 2) {
            return tmpData[0];
        } else {
            ParserException parserException =
                    new ParserException("YANG file error : " + getYangConstructType(yangConstruct) +
                                                " name " + inputString + " is not valid.");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }
    }

    /**
     * Validates path under leaf-ref and parses the path and stores in the
     * leaf-ref.
     *
     * @param path    path of leaf-ref
     * @param type    construct type
     * @param ctx     construct details
     * @param leafRef YANG leaf-ref having path
     */
    public static void validatePath(String path, YangConstructType type,
                                    PathStatementContext ctx,
                                    YangLeafRef leafRef) {

        String concatPath = removeQuotesAndHandleConcat(path);
        if (!concatPath.startsWith(SLASH_FOR_STRING) &&
                !concatPath.startsWith(ANCESTOR)) {
            throw getPathException(ctx, leafRef);
        }
        leafRef.setPath(concatPath);
        if (concatPath.startsWith(SLASH_FOR_STRING)) {
            List<YangAtomicPath> atomicList = new LinkedList<>();
            valAbsPath(concatPath, atomicList, ctx, leafRef, type);
            leafRef.setPathType(YangPathArgType.ABSOLUTE_PATH);
            valPrefix(atomicList, leafRef);
            leafRef.setAtomicPath(atomicList);
            return;
        }
        leafRef.setPathType(YangPathArgType.RELATIVE_PATH);
        valRelPath(concatPath, leafRef, ctx, type);
    }

    /**
     * Validates relative path, parses the string and stores it in the leaf-ref.
     *
     * @param path     leaf-ref path
     * @param leafRef  YANG leaf-ref data model information
     * @param pathType yang construct for creating error message
     */
    private static void valRelPath(String path, YangLeafRef leafRef,
                                   PathStatementContext pathCtx,
                                   YangConstructType pathType) {

        YangRelativePath relPath = new YangRelativePath();
        int count = 0;
        while (path.startsWith(SLASH_ANCESTOR)) {
            path = path.replaceFirst(SLASH_ANCESTOR, EMPTY_STRING);
            count = count + 1;
        }
        if (path.isEmpty()) {
            throw getPathException(pathCtx, leafRef);
        }

        List<YangAtomicPath> atomicList = new ArrayList<>();
        relPath.setAncestorNodeCount(count);
        valAbsPath(SLASH_FOR_STRING + path, atomicList, pathCtx, leafRef,
                   pathType);
        valPrefix(atomicList, leafRef);
        relPath.setAtomicPathList(atomicList);
        leafRef.setRelativePath(relPath);
    }

    /**
     * Validates absolute path, parses the string and stores it in leaf-ref.
     *
     * @param path        leaf-ref path
     * @param atomics     atomic content list
     * @param pathCtx     path statement context
     * @param yangLeafRef YANG leaf ref
     * @param pathType    yang construct for creating error message
     */
    private static void valAbsPath(String path, List<YangAtomicPath> atomics,
                                   PathStatementContext pathCtx,
                                   YangLeafRef yangLeafRef,
                                   YangConstructType pathType) {

        String comPath = path;
        while (comPath != null) {
            comPath = comPath.substring(1);
            if (comPath.isEmpty()) {
                throw getPathException(pathCtx, yangLeafRef);
            }
            int nodeId = comPath.indexOf(CHAR_OF_SLASH);
            int predicate = comPath.indexOf(CHAR_OF_OPEN_SQUARE_BRACKET);
            if (predicate < nodeId && predicate != -1) {
                comPath = getPathWithPredicate(comPath, atomics,
                                               pathCtx, yangLeafRef, pathType);
            } else {
                comPath = getPath(comPath, atomics, pathType, pathCtx);
            }
        }
    }

    /**
     * Returns the remaining path after parsing and the predicates of an atomic
     * content.
     *
     * @param path        leaf-ref path
     * @param atomics     atomic content list
     * @param pathCtx     yang construct's context to get the line number and
     *                    character position
     * @param yangLeafRef YANG leaf-ref data model information
     * @param pathType    yang construct for creating error message
     * @return parsed path after removing one atomic content.
     */
    private static String getPathWithPredicate(String path,
                                               List<YangAtomicPath> atomics,
                                               PathStatementContext pathCtx,
                                               YangLeafRef yangLeafRef,
                                               YangConstructType pathType) {

        String[] node = new String[2];
        int bracket = path.indexOf(CHAR_OF_OPEN_SQUARE_BRACKET);
        node[0] = path.substring(0, bracket);
        node[1] = path.substring(bracket);
        return getParsedPath(node[0], node[1], atomics, pathCtx, yangLeafRef,
                             pathType);
    }

    /**
     * Returns the path after taking all the path predicates of an atomic
     * content.
     *
     * @param nodeId      atomic content nodeId
     * @param path        leaf-ref path
     * @param atomics     atomic content list
     * @param pathCtx     yang construct's context to get the line number
     *                    and character position
     * @param yangLeafRef YANG leaf-ref data model information
     * @param pathType    yang construct for creating error message
     * @return parsed path after removing one atomic content.
     */
    public static String getParsedPath(String nodeId, String path,
                                       List<YangAtomicPath> atomics,
                                       PathStatementContext pathCtx,
                                       YangLeafRef yangLeafRef,
                                       YangConstructType pathType) {

        String comPath = path;
        List<String> predicateList = new ArrayList<>();
        while (comPath.startsWith(OPEN_SQUARE_BRACKET)) {
            String matchedVal = getMatchedPredicate(comPath);
            if (matchedVal == null || matchedVal.isEmpty()) {
                throw getPathException(pathCtx, yangLeafRef);
            }
            predicateList.add(matchedVal);
            comPath = comPath.substring(matchedVal.length());
        }

        YangAtomicPath atomicPath = new YangAtomicPath();
        YangNodeIdentifier validId =
                getValidNodeIdentifier(nodeId, pathType, pathCtx);

        List<YangPathPredicate> predicates = valPathPredicates(predicateList,
                                                               pathType,
                                                               pathCtx, yangLeafRef);
        atomicPath.setNodeIdentifier(validId);
        atomicPath.setPathPredicatesList(predicates);
        atomics.add(atomicPath);
        return comPath;
    }

    /**
     * Validates the path predicates of an atomic content after parsing the
     * predicates and storing it in the leaf-ref.
     *
     * @param predicates  list of predicates
     * @param pathType    yang construct for creating error message
     * @param pathCtx     yang construct's context to get the line number
     *                    and character position
     * @param yangLeafRef YANG leaf-ref data model information
     * @return list of path predicates of an atomic content
     */
    private static List<YangPathPredicate> valPathPredicates(List<String> predicates,
                                                             YangConstructType pathType,
                                                             PathStatementContext pathCtx,
                                                             YangLeafRef yangLeafRef) {

        List<YangPathPredicate> result = new ArrayList<>();
        for (String p : predicates) {
            p = p.substring(1, p.length() - 1);
            result.add(valPathEqualityExp(p.trim(), pathType, pathCtx, yangLeafRef));
        }
        return result;
    }

    /**
     * Validates the path equality expression of a path predicate and after
     * parsing the string assigns it to the YANG path predicate.
     *
     * @param predicate   path predicate
     * @param pathType    yang construct for creating error message
     * @param pathCtx     yang construct's context to get the line number
     *                    and character position
     * @param yangLeafRef YANG leaf-ref data model information
     * @return YANG path predicate
     */
    private static YangPathPredicate valPathEqualityExp(String predicate,
                                                        YangConstructType pathType,
                                                        PathStatementContext pathCtx,
                                                        YangLeafRef yangLeafRef) {

        String[] exp = predicate.split(REGEX_EQUAL);
        YangNodeIdentifier nodeId =
                getValidNodeIdentifier(exp[0].trim(), pathType, pathCtx);
        YangRelativePath relPath = valPathKeyExp(exp[1].trim(), pathType,
                                                 pathCtx, yangLeafRef);

        YangPathPredicate pathPredicate = new YangPathPredicate();
        pathPredicate.setNodeId(nodeId);
        pathPredicate.setPathOp(EQUALTO);
        pathPredicate.setRelPath(relPath);
        return pathPredicate;
    }

    /**
     * Validates the path key expression of the path-predicate and stores it
     * in the relative path of the leaf-ref.
     *
     * @param relPath     relative path
     * @param pathType    yang construct for creating error message
     * @param pathCtx     yang construct's context to get the line number
     *                    and character position
     * @param yangLeafRef YANG leaf-ref data model information
     * @return YANG relative path
     */
    private static YangRelativePath valPathKeyExp(String relPath,
                                                  YangConstructType pathType,
                                                  PathStatementContext pathCtx,
                                                  YangLeafRef yangLeafRef) {

        String[] relative = relPath.split(SLASH_FOR_STRING);
        int count = 0;
        List<String> atomicContent = new ArrayList<>();
        for (String val : relative) {
            if (val.trim().equals(ANCESTOR)) {
                count = count + 1;
            } else {
                atomicContent.add(val);
            }
        }

        YangRelativePath relativePath = new YangRelativePath();
        relativePath.setAncestorNodeCount(count);
        relativePath.setAtomicPathList(valRelPathKeyExp(atomicContent,
                                                        pathType,
                                                        pathCtx, yangLeafRef));
        return relativePath;
    }

    /**
     * Validates relative path key expression in the right relative path of
     * the path predicate, by taking every atomic content in it.
     *
     * @param content     atomic content list
     * @param pathType    yang construct for creating error message
     * @param pathCtx     yang construct's context to get the line number
     *                    and character position
     * @param yangLeafRef YANG leaf-ref data model information
     * @return YANG atomic content list
     */
    private static List<YangAtomicPath> valRelPathKeyExp(List<String> content,
                                                         YangConstructType pathType,
                                                         PathStatementContext pathCtx,
                                                         YangLeafRef yangLeafRef) {

        String current = content.get(0);
        String[] curStr = (current.trim()).split(REGEX_OPEN_BRACE);
        if (!(curStr[0].trim().equals(CURRENT)) ||
                !(curStr[1].trim().equals(CLOSE_PARENTHESIS))) {
            throw getPathException(pathCtx, yangLeafRef);
        }

        content.remove(0);
        List<YangAtomicPath> atomicList = new ArrayList<>();
        for (String relPath : content) {
            YangNodeIdentifier nodeId =
                    getValidNodeIdentifier(relPath, pathType, pathCtx);
            YangAtomicPath atomicPath = new YangAtomicPath();
            atomicPath.setNodeIdentifier(nodeId);
            atomicList.add(atomicPath);
        }
        return atomicList;
    }

    /**
     * Returns the remaining path after parsing and processing an atomic
     * content which doesn't have path-predicate.
     *
     * @param path       leaf-ref path
     * @param atomicList atomic content list
     * @param pathType   yang construct for creating error message
     * @param pathCtx    yang construct's context to get the line number
     *                   and character position
     * @return remaining path after parsing one atomic content
     */
    public static String getPath(String path, List<YangAtomicPath> atomicList,
                                 YangConstructType pathType,
                                 PathStatementContext pathCtx) {

        String comPath = path;
        String nodeId;
        if (comPath.contains(SLASH_FOR_STRING)) {
            nodeId = comPath.substring(0, comPath.indexOf(CHAR_OF_SLASH));
            comPath = comPath.substring(comPath.indexOf(CHAR_OF_SLASH));
        } else {
            nodeId = comPath;
            comPath = null;
        }

        YangNodeIdentifier validNodeId =
                getValidNodeIdentifier(nodeId, pathType, pathCtx);
        YangAtomicPath atomicPath = new YangAtomicPath();
        atomicPath.setNodeIdentifier(validNodeId);
        atomicList.add(atomicPath);
        return comPath;
    }

    /**
     * Returns the path syntax parser exception.
     *
     * @param pathCtx     yang construct's context to get the line number
     *                    and character position
     * @param yangLeafRef YANG leaf-ref data model information
     * @return parser exception
     */
    private static ParserException getPathException(PathStatementContext pathCtx,
                                                    YangLeafRef yangLeafRef) {
        ParserException exception = new ParserException(
                "YANG file error : Path " + yangLeafRef.getPath() +
                        " does not follow valid path syntax");
        exception.setLine(pathCtx.getStart().getLine());
        exception.setCharPosition(pathCtx.getStart().getCharPositionInLine());
        return exception;
    }

    /**
     * Returns the augment name, after removing the prefix, in each atomic
     * content, which is equal to the root prefix.
     *
     * @param atomics atomic content list
     * @param root    root node
     * @return prefix removed augment name
     */
    public static String getPrefixRemovedName(List<YangAtomicPath> atomics,
                                              YangNode root) {

        String rootPrefix = getRootPrefix(root);
        StringBuilder builder = new StringBuilder();
        for (YangAtomicPath atomic : atomics) {
            String id;
            String prefix = atomic.getNodeIdentifier().getPrefix();
            String name = atomic.getNodeIdentifier().getName();
            if (rootPrefix.equals(prefix) || prefix == null) {
                id = SLASH_FOR_STRING + name;
            } else {
                id = SLASH_FOR_STRING + prefix + COLON + name;
            }
            builder.append(id);
        }
        return builder.toString();
    }

    /**
     * Checks if the augment node is a duplicate node. If the augment is
     * duplicate, then it adds all its children to the logical node.
     *
     * @param augment YANG augment
     * @return true if it is a duplicate node; false otherwise
     */
    public static boolean isDuplicateNode(YangAugment augment) {
        YangAugment logical = augment.getLogicalNode();
        if (logical == null) {
            return false;
        }
        YangNode lastChild = logical;

        YangNode child = logical.getChild();
        while (child != null) {
            lastChild = child;
            child = child.getNextSibling();
        }
        addChildToLogicalNode(logical, augment, lastChild);
        addLeafAndLeafList(logical, augment);
        return true;
    }

    /**
     * Adds the child and its sibling in duplicate augment to the logical
     * augment node.
     *
     * @param logical   logical augment node
     * @param augment   YANG augment
     * @param lastChild logical node's last node
     */
    private static void addChildToLogicalNode(YangAugment logical,
                                              YangAugment augment,
                                              YangNode lastChild) {
        YangNode augChild = augment.getChild();
        while (augChild != null) {
            if (lastChild == logical) {
                augChild.setParent(lastChild);
                try {
                    lastChild.addChild(augChild);
                } catch (DataModelException e) {
                    throw new ParserException(
                            constructExtendedListenerErrorMessage(
                                    UNHANDLED_PARSED_DATA, AUGMENT_DATA,
                                    augment.getName(), ENTRY, e.getMessage()));
                }
            } else {
                augChild.setParent(lastChild.getParent());
                augChild.setPreviousSibling(lastChild);
                lastChild.setNextSibling(augChild);
            }
            lastChild = augChild;
            augChild = augChild.getNextSibling();
        }
    }

    /**
     * Adds leaf and leaf-list from the YANG augment to the logical augment
     * node.
     *
     * @param logical logical YANG augment
     * @param augment duplicate YANG augment
     */
    private static void addLeafAndLeafList(YangAugment logical,
                                           YangAugment augment) {

        List<YangLeaf> logLeaves = logical.getListOfLeaf();
        List<YangLeafList> logLl = logical.getListOfLeafList();
        List<YangLeaf> augLeaves = augment.getListOfLeaf();
        List<YangLeafList> augLl = augment.getListOfLeafList();

        if (augLeaves != null && !augLeaves.isEmpty()) {
            for (YangLeaf leaf : augLeaves) {
                leaf.setContainedIn(logical);
                if (logLeaves == null) {
                    logLeaves = new LinkedList<>();
                }
                logLeaves.add(leaf);
            }
        }
        if (augLl != null && !augLl.isEmpty()) {
            for (YangLeafList ll : augLl) {
                ll.setContainedIn(logical);
                if (logLl == null) {
                    logLl = new LinkedList<>();
                }
                logLl.add(ll);
            }
        }
    }

    /**
     * Checks for the augment name name collision. If there are augments with
     * the same name present, then the new augment will be set with a logical
     * node.
     *
     * @param root augment parent node
     * @param aug  YANG augment node
     */
    public static void checkAugNameCollision(YangNode root, YangAugment aug) {
        YangNode child = root.getChild();
        while (child != null) {
            if (child instanceof YangAugment) {
                if (child.getName().equals(aug.getName())) {
                    aug.setLogicalNode((YangAugment) child);
                }
            }
            child = child.getNextSibling();
        }
    }

    /**
     * Removes the duplicate YANG augment from the data tree by detaching it
     * from its parent and from its sibling.
     *
     * @param augment duplicate YANG augment
     */
    public static void removeAugment(YangAugment augment) {
        YangNode root = augment.getParent();
        YangNode child = root.getChild();
        YangNode pSib = null;
        if (child == null) {
            throw new ParserException("The root node of augment " + augment
                    .getName() + " must have atleast one child");
        }
        while (child != null) {
            if (child == augment) {
                if (pSib == null) {
                    root.setChild(null);
                } else {
                    pSib.setNextSibling(null);
                }
            }
            pSib = child;
            child = child.getNextSibling();
        }
        augment = new YangJavaAugmentTranslator();
        augment = null;
    }
}
