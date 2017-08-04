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

import org.onosproject.yang.compiler.datamodel.YangIdentityRef;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.linker.impl.YangResolutionInfoImpl;
import org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.IdentityrefSpecificationContext;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.addResolutionInfo;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.UNRESOLVED;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.BASE_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.IDENTITYREF_DATA;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.EXIT;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructExtendedListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_CURRENT_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.UNHANDLED_PARSED_DATA;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.getValidNodeIdentifier;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 * <p/>
 * ABNF grammar as per RFC6020
 * identityref-specification =
 * base-stmt stmtsep
 * base-stmt           = base-keyword sep identifier-ref-arg-str
 * optsep stmtend*
 * identifier-ref-arg  = [prefix ":"] identifier
 */

/**
 * Represents listener based call back function corresponding to the
 * "identityref" rule defined in ANTLR grammar file for corresponding ABNF
 * rule in RFC 6020.
 */
public final class IdentityRefListener {

    // No instantiation.
    private IdentityRefListener() {
    }

    /**
     * Performs validation and updates the data model tree when parser receives an input
     * matching the grammar rule (identity-ref).
     *
     * @param listener listener object
     * @param ctx      context object
     */
    public static void processIdentityRefEntry(TreeWalkListener listener,
                                               IdentityrefSpecificationContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, IDENTITYREF_DATA,
                             EMPTY_STRING, ENTRY);

        if (!(listener.getParsedDataStack().peek() instanceof YangType)) {
            throw new ParserException(constructListenerErrorMessage(
                    INVALID_HOLDER, IDENTITYREF_DATA, EMPTY_STRING, ENTRY));
        }

        YangIdentityRef idRef = new YangIdentityRef();
        Parsable typeData = listener.getParsedDataStack().pop();
        YangResolutionInfoImpl<YangIdentityRef> resolutionInfo;

        // Validate node identifier.
        YangNodeIdentifier nodeId = getValidNodeIdentifier(
                ctx.baseStatement().string().getText(), BASE_DATA, ctx);
        idRef.setBaseIdentity(nodeId);
        ((YangType) typeData).setDataTypeExtendedInfo(idRef);

        int errLine = ctx.getStart().getLine();
        int errPos = ctx.getStart().getCharPositionInLine();

        idRef.setLineNumber(errLine);
        idRef.setCharPosition(errPos);
        idRef.setFileName(listener.getFileName());

        Parsable tmpData = listener.getParsedDataStack().peek();
        Parsable parentNode;
        switch (tmpData.getYangConstructType()) {

            case LEAF_DATA:
                Parsable leaf = listener.getParsedDataStack().pop();
                parentNode = listener.getParsedDataStack().peek();
                listener.getParsedDataStack().push(leaf);
                break;

            case LEAF_LIST_DATA:
                Parsable leafList = listener.getParsedDataStack().pop();
                parentNode = listener.getParsedDataStack().peek();
                listener.getParsedDataStack().push(leafList);
                break;

            case UNION_DATA:
                parentNode = listener.getParsedDataStack().peek();
                break;

            case TYPEDEF_DATA:
                parentNode = listener.getParsedDataStack().peek();
                break;

            default:
                throw new ParserException(
                        constructListenerErrorMessage(INVALID_HOLDER,
                                                      IDENTITYREF_DATA,
                                                      ctx.getText(), EXIT));
        }

        if (!(parentNode instanceof YangNode)) {
            throw new ParserException(
                    constructListenerErrorMessage(INVALID_HOLDER,
                                                  IDENTITYREF_DATA,
                                                  ctx.getText(), EXIT));
        }
        idRef.setResolvableStatus(UNRESOLVED);
        // Adds resolution information to the list
        resolutionInfo = new YangResolutionInfoImpl<>(
                idRef, (YangNode) parentNode, errLine, errPos);
        addToResolution(resolutionInfo, ctx);

        listener.getParsedDataStack().push(typeData);
        listener.getParsedDataStack().push(idRef);
    }

    /**
     * Performs validations and update the data model tree when parser exits
     * from grammar rule (identity-ref).
     *
     * @param listener Listener's object
     * @param ctx      context object
     */
    public static void processIdentityRefExit(TreeWalkListener listener,
                                              IdentityrefSpecificationContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_CURRENT_HOLDER, IDENTITYREF_DATA,
                             ctx.getText(), EXIT);

        Parsable parsableType = listener.getParsedDataStack().pop();
        if (!(parsableType instanceof YangIdentityRef)) {
            throw new ParserException(constructListenerErrorMessage(
                    INVALID_HOLDER, IDENTITYREF_DATA, ctx.getText(), EXIT));
        }
    }

    /**
     * Adds to resolution list.
     *
     * @param info resolution info
     * @param ctx  context object
     */
    private static void addToResolution(YangResolutionInfoImpl<YangIdentityRef> info,
                                        IdentityrefSpecificationContext ctx) {
        try {
            addResolutionInfo(info);
        } catch (DataModelException e) {
            throw new ParserException(constructExtendedListenerErrorMessage(
                    UNHANDLED_PARSED_DATA, IDENTITYREF_DATA, ctx.getText(),
                    ENTRY, e.getMessage()));
        }
    }
}
