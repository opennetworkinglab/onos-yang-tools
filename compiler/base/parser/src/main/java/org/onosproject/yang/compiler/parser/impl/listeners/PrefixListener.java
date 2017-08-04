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

import org.onosproject.yang.compiler.datamodel.YangBelongsTo;
import org.onosproject.yang.compiler.datamodel.YangImport;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.PREFIX_DATA;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.PrefixStatementContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.getValidIdentifier;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 * module-header-stmts = ;; these stmts can appear in any order
 *                       [yang-version-stmt stmtsep]
 *                        namespace-stmt stmtsep
 *                        prefix-stmt stmtsep
 *
 * prefix-stmt         = prefix-keyword sep prefix-arg-str
 *                       optsep stmtend
 *
 * ANTLR grammar rule
 * module_header_statement : yang_version_stmt? namespace_stmt prefix_stmt
 *                         | yang_version_stmt? prefix_stmt namespace_stmt
 *                         | namespace_stmt yang_version_stmt? prefix_stmt
 *                         | namespace_stmt prefix_stmt yang_version_stmt?
 *                         | prefix_stmt namespace_stmt yang_version_stmt?
 *                         | prefix_stmt yang_version_stmt? namespace_stmt
 *                         ;
 * prefix_stmt : PREFIX_KEYWORD identifier STMTEND;
 */

/**
 * Represents listener based call back function corresponding to the "prefix"
 * rule defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class PrefixListener {

    /**
     * Creates a new prefix listener.
     */
    private PrefixListener() {
    }

    /**
     * It is called when parser receives an input matching the grammar rule
     * (prefix),perform validations and update the data model tree.
     *
     * @param listener Listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processPrefixEntry(TreeWalkListener listener, PrefixStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, PREFIX_DATA, ctx.identifier().getText(), ENTRY);

        String identifier = getValidIdentifier(ctx.identifier().getText(), PREFIX_DATA, ctx);

        // Obtain the node of the stack.
        Parsable tmpNode = listener.getParsedDataStack().peek();
        switch (tmpNode.getYangConstructType()) {
            case MODULE_DATA: {
                YangModule module = (YangModule) tmpNode;
                module.setPrefix(identifier);
                break;
            }
            case IMPORT_DATA: {
                YangImport importNode = (YangImport) tmpNode;
                importNode.setPrefixId(identifier);
                break;
            }
            case BELONGS_TO_DATA: {
                YangBelongsTo belongstoNode = (YangBelongsTo) tmpNode;
                belongstoNode.setPrefix(identifier);
                break;
            }
            default:
                throw new ParserException(constructListenerErrorMessage(INVALID_HOLDER, PREFIX_DATA,
                        ctx.identifier().getText(), ENTRY));
        }
    }
}
