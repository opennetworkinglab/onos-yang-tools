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

import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangReferenceResolver;
import org.onosproject.yang.compiler.datamodel.YangResolutionInfo;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.linker.exceptions.LinkerException;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;
import org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType;

import java.util.List;

import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_BASE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_COMPILER_ANNOTATION;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_DERIVED_DATA_TYPE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_IDENTITYREF;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_IF_FEATURE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_LEAFREF;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_USES;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.validateMultipleDeviationStatement;
import static org.onosproject.yang.compiler.datamodel.utils.GeneratedLanguage.JAVA_GENERATION;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.MODULE_DATA;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ModuleStatementContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.EXIT;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_CHILD;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_CURRENT_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.getValidIdentifier;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsEmpty;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;
import static org.onosproject.yang.compiler.translator.tojava.YangDataModelFactory.getYangModuleNode;
import static org.onosproject.yang.compiler.utils.UtilConstants.ONE;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 * module-stmt         = optsep module-keyword sep identifier-arg-str
 *                       optsep
 *                       "{" stmtsep
 *                           module-header-stmts
 *                           linkage-stmts
 *                           meta-stmts
 *                           revision-stmts
 *                           body-stmts
 *                       "}" optsep
 *
 * ANTLR grammar rule
 * module_stmt : MODULE_KEYWORD identifier LEFT_CURLY_BRACE module_body* RIGHT_CURLY_BRACE;
 */

/**
 * Represents listener based call back function corresponding to the "module"
 * rule defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class ModuleListener {

    /**
     * Creates a new module listener.
     */
    private ModuleListener() {
    }

    /**
     * It is called when parser receives an input matching the grammar rule
     * (module), perform validations and update the data model tree.
     *
     * @param listener Listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processModuleEntry(TreeWalkListener listener,
                                          ModuleStatementContext ctx) {

        // Check if stack is empty.
        checkStackIsEmpty(listener, INVALID_HOLDER, MODULE_DATA,
                          ctx.identifier().getText(), ENTRY);
        String identifier = getValidIdentifier(ctx.identifier().getText(),
                                               MODULE_DATA, ctx);
        YangModule yangModule = getYangModuleNode(JAVA_GENERATION);
        yangModule.setName(identifier);
        yangModule.setLineNumber(ctx.getStart().getLine());
        yangModule.setCharPosition(ctx.getStart().getCharPositionInLine());
        yangModule.setFileName(listener.getFileName());

        if (ctx.moduleBody().moduleHeaderStatement().yangVersionStatement() == null) {
            yangModule.setVersion(ONE);
        }

        listener.getParsedDataStack().push(yangModule);
    }

    /**
     * It is called when parser exits from grammar rule (module), it perform
     * validations and update the data model tree.
     *
     * @param listener Listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processModuleExit(TreeWalkListener listener,
                                         ModuleStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, MODULE_DATA,
                             ctx.identifier().getText(), EXIT);
        Parsable tmpNode = listener.getParsedDataStack().peek();
        if (!(tmpNode instanceof YangModule)) {
            throwError(MISSING_CURRENT_HOLDER, ctx);
        }

        YangModule module = (YangModule) tmpNode;
        List<YangResolutionInfo> info = module.getUnresolvedResolutionList(
                YANG_COMPILER_ANNOTATION);
        if (info != null && !info.isEmpty() && module.getChild() != null) {
            throwError(INVALID_CHILD, ctx);
        }
        YangReferenceResolver resolver = (YangReferenceResolver) listener
                .getParsedDataStack().peek();
        try {
            resolver.resolveSelfFileLinking(YANG_IF_FEATURE);
            resolver.resolveSelfFileLinking(YANG_USES);
            resolver.resolveSelfFileLinking(YANG_DERIVED_DATA_TYPE);
            resolver.resolveSelfFileLinking(YANG_LEAFREF);
            resolver.resolveSelfFileLinking(YANG_BASE);
            resolver.resolveSelfFileLinking(YANG_IDENTITYREF);
        } catch (DataModelException e) {
            LinkerException linkerException = new LinkerException(e.getMessage(), e);
            linkerException.setLine(e.getLineNumber());
            linkerException.setCharPosition(e.getCharPositionInLine());
            linkerException.setFileName(listener.getFileName());
            throw linkerException;
        }

        /*
         * Validate whether all deviation statement xpath is referring to same
         * module
         */
        try {
            validateMultipleDeviationStatement(module);
        } catch (DataModelException e) {
            throw new ParserException(e.getMessage());
        }
    }

    private static void throwError(ListenerErrorType type,
                                   ModuleStatementContext ctx) {
        throw new ParserException(constructListenerErrorMessage(
                type, MODULE_DATA, ctx.identifier().getText(), EXIT));
    }
}
