/*
 * Copyright 2016-present Open Networking Laboratory
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

package org.onosproject.yangutils.parser.impl.parserutils;

import org.antlr.v4.runtime.ParserRuleContext;
import org.onosproject.yangutils.datamodel.YangAtomicPath;
import org.onosproject.yangutils.datamodel.YangImport;
import org.onosproject.yangutils.datamodel.YangLeaf;
import org.onosproject.yangutils.datamodel.YangLeafRef;
import org.onosproject.yangutils.datamodel.YangLeavesHolder;
import org.onosproject.yangutils.datamodel.YangList;
import org.onosproject.yangutils.datamodel.YangModule;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangNodeIdentifier;
import org.onosproject.yangutils.datamodel.YangPathPredicate;
import org.onosproject.yangutils.datamodel.YangReferenceResolver;
import org.onosproject.yangutils.datamodel.YangRelativePath;
import org.onosproject.yangutils.datamodel.YangSubModule;
import org.onosproject.yangutils.datamodel.utils.YangConstructType;
import org.onosproject.yangutils.parser.antlrgencode.GeneratedYangParser;
import org.onosproject.yangutils.parser.exceptions.ParserException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.onosproject.yangutils.datamodel.YangPathArgType.ABSOLUTE_PATH;
import static org.onosproject.yangutils.datamodel.YangPathArgType.RELATIVE_PATH;
import static org.onosproject.yangutils.datamodel.YangPathOperator.EQUALTO;
import static org.onosproject.yangutils.datamodel.utils.YangConstructType.getYangConstructType;
import static org.onosproject.yangutils.parser.antlrgencode.GeneratedYangParser.PathStatementContext;
import static org.onosproject.yangutils.utils.UtilConstants.ADD;
import static org.onosproject.yangutils.utils.UtilConstants.ANCESTOR;
import static org.onosproject.yangutils.utils.UtilConstants.CARET;
import static org.onosproject.yangutils.utils.UtilConstants.CHAR_OF_OPEN_SQUARE_BRACKET;
import static org.onosproject.yangutils.utils.UtilConstants.CHAR_OF_SLASH;
import static org.onosproject.yangutils.utils.UtilConstants.CLOSE_PARENTHESIS;
import static org.onosproject.yangutils.utils.UtilConstants.COLON;
import static org.onosproject.yangutils.utils.UtilConstants.CURRENT;
import static org.onosproject.yangutils.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.FALSE;
import static org.onosproject.yangutils.utils.UtilConstants.INVALID_TREE;
import static org.onosproject.yangutils.utils.UtilConstants.OPEN_SQUARE_BRACKET;
import static org.onosproject.yangutils.utils.UtilConstants.QUOTES;
import static org.onosproject.yangutils.utils.UtilConstants.SLASH;
import static org.onosproject.yangutils.utils.UtilConstants.SLASH_ANCESTOR;
import static org.onosproject.yangutils.utils.UtilConstants.SLASH_FOR_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.TRUE;
import static org.onosproject.yangutils.utils.UtilConstants.YANG_FILE_ERROR;

/**
 * Represents an utility for listener.
 */
public final class ListenerUtil {

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
    private static final String ONE = "1";
    private static final int IDENTIFIER_LENGTH = 64;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String REGEX_EQUAL = "[=]";
    private static final String REGEX_OPEN_BRACE = "[(]";

    private static YangConstructType pathType;
    private static PathStatementContext pathCtx;
    private static YangLeafRef yangLeafRef;

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

        yangStringData = yangStringData.replace("\"", EMPTY_STRING);
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
    public static String getValidIdentifier(String identifier, YangConstructType yangConstruct, ParserRuleContext ctx) {

        String identifierString = removeQuotesAndHandleConcat(identifier);
        ParserException parserException;

        if (identifierString.length() > IDENTIFIER_LENGTH) {
            parserException = new ParserException("YANG file error : " +
                                                          getYangConstructType(yangConstruct) + " name " + identifierString + " is " +
                                                          "greater than 64 characters.");
        } else if (!IDENTIFIER_PATTERN.matcher(identifierString).matches()) {
            parserException = new ParserException("YANG file error : " +
                                                          getYangConstructType(yangConstruct) + " name " + identifierString + " is not " +
                                                          "valid.");
        } else if (identifierString.toLowerCase().startsWith(XML)) {
            parserException = new ParserException("YANG file error : " +
                                                          getYangConstructType(yangConstruct) + " identifier " + identifierString +
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
    public static byte getValidVersion(GeneratedYangParser.YangVersionStatementContext ctx) {

        String value = removeQuotesAndHandleConcat(ctx.version().getText());
        if (!value.equals(ONE)) {
            ParserException parserException = new ParserException("YANG file error: Input version not supported");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }

        return Byte.valueOf(value);
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
                                                                          getYangConstructType(yangConstruct) + " value " + value + " is not " +
                                                                          "valid.");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }

        int valueInInteger;
        try {
            valueInInteger = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            ParserException parserException = new ParserException("YANG file error : " +
                                                                          getYangConstructType(yangConstruct) + " value " + value + " is not " +
                                                                          "valid.");
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
                                                                          getYangConstructType(yangConstruct) + " value " + value + " is not " +
                                                                          "valid.");
            parserException.setLine(ctx.getStart().getLine());
            parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
            throw parserException;
        }

        int valueInInteger;
        try {
            valueInInteger = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            ParserException parserException = new ParserException("YANG file error : " +
                                                                          getYangConstructType(yangConstruct) + " value " + value + " is not " +
                                                                          "valid.");
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
                                                                          getYangConstructType(yangConstruct) + " value " + value + " is not " +
                                                                          "valid.");
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
                                                                          getYangConstructType(yangConstruct) + " name " + nodeIdentifierString +
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
     * Validates the unique syntax from the reference path.
     *
     * @param uniquePath   path of unique
     * @param prefixOfFile current file's prefix
     * @param ctx          yang construct's context to get the line number and character position
     * @return list of absolute path
     */
    private static List<YangAtomicPath> validateUniqueValues(String uniquePath, String prefixOfFile,
                                                             ParserRuleContext ctx) {
        List<YangAtomicPath> atomicPath = new LinkedList<>();
        String[] pathInUnique = uniquePath.split(SLASH_FOR_STRING);
        for (String uniqueValue : pathInUnique) {
            YangAtomicPath yangAtomicPathPath = new YangAtomicPath();
            YangNodeIdentifier nodeIdentifier = getValidNodeIdentifier(uniqueValue, YangConstructType.UNIQUE_DATA, ctx);
            yangAtomicPathPath.setNodeIdentifier(nodeIdentifier);
            atomicPath.add(yangAtomicPathPath);
            if (nodeIdentifier.getPrefix() != null && nodeIdentifier.getPrefix() != prefixOfFile) {
                ParserException parserException = new ParserException("YANG file error : A leaf reference, in unique," +
                                                                              " must refer to a leaf in the list");
                parserException.setLine(ctx.getStart().getLine());
                parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
                throw parserException;
            }
        }
        return atomicPath;
    }

    /**
     * Validates unique field from the list.
     *
     * @param yangList instance of YANG list
     * @param ctx      yang construct's context to get the line number and character position
     */
    public static void validateUniqueInList(YangList yangList, ParserRuleContext ctx) {
        YangLeaf leaf;
        // Returns the prefix for the file where unique is present.
        String prefixOfTheFile = getRootPrefix(yangList);
        List<String> uniques = yangList.getUniqueList();
        if (uniques != null && !uniques.isEmpty()) {
            Iterator<String> uniqueList = uniques.listIterator();
            while (uniqueList.hasNext()) {
                String pathInUnique = uniqueList.next();
                List<YangAtomicPath> atomicPathInUnique = validateUniqueValues(pathInUnique, prefixOfTheFile, ctx);
                YangAtomicPath leafInPath = atomicPathInUnique.get(atomicPathInUnique.size() - 1);
                if (atomicPathInUnique.size() == 1) {
                    leaf = getReferenceLeafFromUnique(yangList, leafInPath);
                } else {
                    atomicPathInUnique.remove(atomicPathInUnique.size() - 1);
                    YangNode holderOfLeaf = getNodeUnderListFromPath(atomicPathInUnique, yangList, ctx);
                    leaf = getReferenceLeafFromUnique(holderOfLeaf, leafInPath);
                }
                if (leaf == null) {
                    ParserException parserException = new ParserException("YANG file error : A leaf reference, in " +
                                                                                  "unique," +
                                                                                  " must refer to a leaf under the list");
                    parserException.setLine(ctx.getStart().getLine());
                    parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
                    throw parserException;
                }
            }
        }
    }

    /**
     * Returns the last node under the unique path.
     *
     * @param uniquePath atomic path list
     * @param node       root node from where it starts searching
     * @param ctx        yang construct's context to get the line number and character position
     * @return last node in the list
     */
    private static YangNode getNodeUnderListFromPath(List<YangAtomicPath> uniquePath, YangNode node,
                                                     ParserRuleContext ctx) {
        Iterator<YangAtomicPath> nodesInReference = uniquePath.listIterator();
        YangNode potentialReferredNode = node.getChild();
        while (nodesInReference.hasNext()) {
            YangAtomicPath nodeInUnique = nodesInReference.next();
            YangNode referredNode = getReferredNodeFromTheUniqueNodes(nodeInUnique.getNodeIdentifier(),
                                                                      potentialReferredNode);
            if (referredNode == null) {
                ParserException parserException = new ParserException("YANG file error : The target node in unique " +
                                                                              "reference path is invalid");
                parserException.setLine(ctx.getStart().getLine());
                parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
                throw parserException;
            } else {
                potentialReferredNode = referredNode.getChild();
            }
        }
        return potentialReferredNode;
    }

    /**
     * Returns the node that matches with the name of the node in path.
     *
     * @param nodeInUnique          node name in path
     * @param potentialReferredNode node under which it has to match
     * @return referred node
     */
    private static YangNode getReferredNodeFromTheUniqueNodes(YangNodeIdentifier nodeInUnique, YangNode
            potentialReferredNode) {
        while (potentialReferredNode != null) {
            // Check if the potential referred node is the actual referred node
            if (potentialReferredNode.getName().equals(nodeInUnique.getName())) {
                return potentialReferredNode;
            }
            potentialReferredNode = potentialReferredNode.getNextSibling();
        }
        return null;
    }

    /**
     * Returns the leaf which unique refers.
     *
     * @param nodeForLeaf  last node where leaf is referred
     * @param leafInUnique leaf in unique path
     * @return YANG leaf
     */
    private static YangLeaf getReferenceLeafFromUnique(YangNode nodeForLeaf, YangAtomicPath leafInUnique) {
        YangLeavesHolder leavesHolder = (YangLeavesHolder) nodeForLeaf;
        List<YangLeaf> leaves = leavesHolder.getListOfLeaf();
        if (leaves != null && !leaves.isEmpty()) {
            for (YangLeaf leaf : leaves) {
                if (leafInUnique.getNodeIdentifier().getName().equals(leaf.getName())) {
                    return leaf;
                }
            }
        }
        return null;
    }

    /**
     * Returns the prefix of the root node from any node inside it.
     *
     * @param curNode YANG node
     * @return prefix of the root node
     */
    public static String getRootPrefix(YangNode curNode) {

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
            ParserException parserException = new ParserException("YANG file error : " +
                                                                          getYangConstructType(yangConstructType) + " name " + argumentString +
                                                                          "is not valid");
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
     * @param type      construct type
     * @param ctx       construct context
     * @param errorInfo error msg
     * @param fileName  YANG file name
     */
    public static void handleUnsupportedYangConstruct(YangConstructType type,
                                                      ParserRuleContext ctx,
                                                      String errorInfo,
                                                      String fileName) {
        ParserException parserException = new ParserException(
                YANG_FILE_ERROR + QUOTES + getYangConstructType(
                        type) + QUOTES + errorInfo);
        parserException.setLine(ctx.getStart().getLine());
        parserException.setCharPosition(ctx.getStart().getCharPositionInLine());
        //FIXME this exception should probably be thrown rather than just logged
        //throw parserException;
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
            ParserException parserException = new ParserException("YANG file error : " +
                                                                          getYangConstructType(yangConstruct) + " name " + inputString +
                                                                          " is not valid.");
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
        pathType = type;
        pathCtx = ctx;
        yangLeafRef = leafRef;
        if (!concatPath.startsWith(SLASH_FOR_STRING) &&
                !concatPath.startsWith(ANCESTOR)) {
            throw getPathException();
        }
        leafRef.setPath(concatPath);
        if (concatPath.startsWith(SLASH_FOR_STRING)) {
            List<YangAtomicPath> atomicList = new LinkedList<>();
            valAbsPath(concatPath, atomicList);
            leafRef.setPathType(ABSOLUTE_PATH);
            valPrefix(atomicList, leafRef);
            leafRef.setAtomicPath(atomicList);
            return;
        }
        leafRef.setPathType(RELATIVE_PATH);
        valRelPath(concatPath, leafRef);
    }

    /**
     * Validates relative path, parses the string and stores it in the leaf-ref.
     *
     * @param path    leaf-ref path
     * @param leafRef YANG leaf-ref
     */
    private static void valRelPath(String path, YangLeafRef leafRef) {

        YangRelativePath relPath = new YangRelativePath();
        int count = 0;
        while (path.startsWith(SLASH_ANCESTOR)) {
            path = path.replaceFirst(SLASH_ANCESTOR, EMPTY_STRING);
            count = count + 1;
        }
        if (path.isEmpty()) {
            throw getPathException();
        }

        List<YangAtomicPath> atomicList = new ArrayList<>();
        relPath.setAncestorNodeCount(count);
        valAbsPath(SLASH_FOR_STRING + path, atomicList);
        valPrefix(atomicList, leafRef);
        relPath.setAtomicPathList(atomicList);
        leafRef.setRelativePath(relPath);
    }

    /**
     * Validates absolute path, parses the string and stores it in leaf-ref.
     *
     * @param path    leaf-ref path
     * @param atomics atomic content list
     */
    private static void valAbsPath(String path, List<YangAtomicPath> atomics) {

        String comPath = path;
        while (comPath != null) {
            comPath = comPath.substring(1);
            if (comPath.isEmpty()) {
                throw getPathException();
            }
            int nodeId = comPath.indexOf(CHAR_OF_SLASH);
            int predicate = comPath.indexOf(CHAR_OF_OPEN_SQUARE_BRACKET);
            if (predicate < nodeId && predicate != -1) {
                comPath = getPathWithPredicate(comPath, atomics);
            } else {
                comPath = getPath(comPath, atomics);
            }
        }
    }

    /**
     * Returns the remaining path after parsing and the predicates of an atomic
     * content.
     *
     * @param path    leaf-ref path
     * @param atomics atomic content list
     * @return parsed path after removing one atomic content.
     */
    private static String getPathWithPredicate(String path,
                                               List<YangAtomicPath> atomics) {

        String[] node = new String[2];
        int bracket = path.indexOf(CHAR_OF_OPEN_SQUARE_BRACKET);
        node[0] = path.substring(0, bracket);
        node[1] = path.substring(bracket);
        return getParsedPath(node[0], node[1], atomics);
    }

    /**
     * Returns the path after taking all the path predicates of an atomic
     * content.
     *
     * @param nodeId  atomic content nodeId
     * @param path    leaf-ref path
     * @param atomics atomic content list
     * @return parsed path after removing one atomic content.
     */
    public static String getParsedPath(String nodeId, String path,
                                       List<YangAtomicPath> atomics) {

        String comPath = path;
        List<String> predicateList = new ArrayList<>();
        while (comPath.startsWith(OPEN_SQUARE_BRACKET)) {
            String matchedVal = getMatchedPredicate(comPath);
            if (matchedVal == null || matchedVal.isEmpty()) {
                throw getPathException();
            }
            predicateList.add(matchedVal);
            comPath = comPath.substring(matchedVal.length());
        }

        YangAtomicPath atomicPath = new YangAtomicPath();
        YangNodeIdentifier validId =
                getValidNodeIdentifier(nodeId, pathType, pathCtx);

        List<YangPathPredicate> predicates = valPathPredicates(predicateList);
        atomicPath.setNodeIdentifier(validId);
        atomicPath.setPathPredicatesList(predicates);
        atomics.add(atomicPath);
        return comPath;
    }

    /**
     * Validates the path predicates of an atomic content after parsing the
     * predicates and storing it in the leaf-ref.
     *
     * @param predicates list of predicates
     * @return list of path predicates of an atomic content
     */
    private static List<YangPathPredicate> valPathPredicates(List<String> predicates) {

        List<YangPathPredicate> result = new ArrayList<>();
        for (String p : predicates) {
            p = p.substring(1, p.length() - 1);
            result.add(valPathEqualityExp(p.trim()));
        }
        return result;
    }

    /**
     * Validates the path equality expression of a path predicate and after
     * parsing the string assigns it to the YANG path predicate.
     *
     * @param predicate path predicate
     * @return YANG path predicate
     */
    private static YangPathPredicate valPathEqualityExp(String predicate) {

        String[] exp = predicate.split(REGEX_EQUAL);
        YangNodeIdentifier nodeId =
                getValidNodeIdentifier(exp[0].trim(), pathType, pathCtx);
        YangRelativePath relPath = valPathKeyExp(exp[1].trim());

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
     * @param relPath relative path
     * @return YANG relative path
     */
    private static YangRelativePath valPathKeyExp(String relPath) {

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
        relativePath.setAtomicPathList(valRelPathKeyExp(atomicContent));
        return relativePath;
    }

    /**
     * Validates relative path key expression in the right relative path of
     * the path predicate, by taking every atomic content in it.
     *
     * @param content atomic content list
     * @return YANG atomic content list
     */
    private static List<YangAtomicPath> valRelPathKeyExp(List<String> content) {

        String current = content.get(0);
        String[] curStr = (current.trim()).split(REGEX_OPEN_BRACE);
        if (!(curStr[0].trim().equals(CURRENT)) ||
                !(curStr[1].trim().equals(CLOSE_PARENTHESIS))) {
            throw getPathException();
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
     * @return remaining path after parsing one atomic content
     */
    public static String getPath(String path, List<YangAtomicPath> atomicList) {

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
     * @return parser exception
     */
    private static ParserException getPathException() {
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
}
