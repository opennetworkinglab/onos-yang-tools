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

package org.onosproject.yang.compiler.parser.impl;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.parser.YangUtilsParser;
import org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangLexer;
import org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.parserutils.ParseTreeErrorListener;

import java.io.IOException;

/**
 * Represents file parsing, parse tree creation and data model tree creation
 * corresponding to an input YANG file.
 */
public class YangUtilsParserManager implements YangUtilsParser {

    @Override
    public YangNode getDataModel(String yangFile) throws IOException, ParserException {

        /**
         * Create a char stream that reads from YANG file. Throws an exception
         * in case input YANG file is either null or non existent.
         */
        ANTLRInputStream input;
        try {
            input = new ANTLRFileStream(yangFile);
        } catch (IOException e) {
            throw new ParserException("YANG file error : YANG file does not exist. " + yangFile);
        }

        // Create a lexer that feeds off of input char stream.
        GeneratedYangLexer lexer = new GeneratedYangLexer(input);

        // Create a buffer of tokens pulled from the lexer.
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Create a parser that feeds off the tokens buffer.
        GeneratedYangParser parser = new GeneratedYangParser(tokens);

        // Remove console error listener.
        parser.removeErrorListeners();

        // Create instance of customized error listener.
        ParseTreeErrorListener parseTreeErrorListener = new ParseTreeErrorListener();

        // Add customized error listener to catch errors during parsing.
        parser.addErrorListener(parseTreeErrorListener);

        ParseTree tree;

        try {
            // Begin parsing YANG file and generate parse tree.
            tree = parser.yangfile();
        } catch (ParserException parserException) {
            parserException.setFileName(yangFile);
            throw parserException;
        }

        // Create a walker to walk the parse tree.
        ParseTreeWalker walker = new ParseTreeWalker();

        // Create a listener implementation class object.
        TreeWalkListener treeWalker = new TreeWalkListener();
        treeWalker.setFileName(yangFile);
        /**
         * Walk parse tree, provide call backs to methods in listener and build
         * data model tree.
         */
        try {
            walker.walk(treeWalker, tree);
        } catch (ParserException listenerException) {
            // TODO free incomplete data model tree.
            listenerException.setFileName(yangFile);
            throw listenerException;
        } finally {
            // TODO free parsable stack
        }

        // Returns the Root Node of the constructed data model tree.
        return treeWalker.getRootNode();
    }
}
