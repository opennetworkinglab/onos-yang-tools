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

import org.onosproject.yang.compiler.datamodel.YangDeviateAdd;
import org.onosproject.yang.compiler.datamodel.YangDeviateDelete;
import org.onosproject.yang.compiler.datamodel.YangDeviateReplace;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangTypeDef;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.UNITS_DATA;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.UnitsStatementContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 * units-stmt          = units-keyword sep string optsep stmtend
 *
 * ANTLR grammar rule
 * unitsStatement : UNITS_KEYWORD string STMTEND;
 */

/**
 * Represents listener based call back function corresponding to the "units"
 * rule defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class UnitsListener {

    /**
     * Creates a new units listener.
     */
    private UnitsListener() {
    }

    /**
     * It is called when parser receives an input matching the grammar
     * rule (units), performs validation and updates the data model
     * tree.
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processUnitsEntry(TreeWalkListener listener,
                                         UnitsStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, UNITS_DATA, ctx.string().getText(), ENTRY);

        Parsable tmpData = listener.getParsedDataStack().peek();
        switch (tmpData.getYangConstructType()) {
            case LEAF_DATA:
                YangLeaf leaf = (YangLeaf) tmpData;
                leaf.setUnits(ctx.string().getText());
                break;
            case LEAF_LIST_DATA:
                YangLeafList leafList = (YangLeafList) tmpData;
                leafList.setUnits(ctx.string().getText());
                break;
            case TYPEDEF_DATA:
                YangTypeDef typeDef = (YangTypeDef) tmpData;
                typeDef.setUnits(ctx.string().getText());
                break;
            case DEVIATE_ADD:
                YangDeviateAdd deviateAdd = (YangDeviateAdd) tmpData;
                deviateAdd.setUnits(ctx.string().getText());
                break;
            case DEVIATE_DELETE:
                YangDeviateDelete deviateDelete = (YangDeviateDelete) tmpData;
                deviateDelete.setUnits(ctx.string().getText());
                break;
            case DEVIATE_REPLACE:
                YangDeviateReplace replace = (YangDeviateReplace) tmpData;
                replace.setUnits(ctx.string().getText());
                break;
            default:
                throw new ParserException(
                        constructListenerErrorMessage(INVALID_HOLDER,
                                                      UNITS_DATA,
                                                      ctx.string().getText(),
                                                      ENTRY));
        }
    }
}