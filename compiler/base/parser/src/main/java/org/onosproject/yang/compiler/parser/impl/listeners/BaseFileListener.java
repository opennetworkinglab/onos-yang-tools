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
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSubModule;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.YANGBASE_DATA;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.YangfileContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.EXIT;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_CHILD;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsEmpty;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ANTLR grammar rule
 * yangfile : module_stmt
 *          | submodule_stmt;
 */

/**
 * Representation of call back function corresponding to the "base rule" defined in
 * ANTLR grammar file.
 */
public final class BaseFileListener {

    /**
     * Creates a new base listener.
     */
    private BaseFileListener() {
    }

    /**
     * It is called when parser receives an input matching the grammar rule
     * (yangfile), perform validations and update the data model tree.
     *
     * @param listener Listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processYangFileEntry(TreeWalkListener listener, YangfileContext ctx) {

        // Check if stack is empty.
        checkStackIsEmpty(listener, INVALID_HOLDER, YANGBASE_DATA, "", ENTRY);
    }

    /**
     * It is called when parser exits from grammar rule (yangfile), it perform
     * validations and update the data model tree.
     *
     * @param listener Listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processYangFileExit(TreeWalkListener listener, YangfileContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, YANGBASE_DATA, "", EXIT);

        // Data Model tree root node is set.
        if (listener.getParsedDataStack().peek() instanceof YangModule
                || listener.getParsedDataStack().peek() instanceof YangSubModule) {
            listener.setRootNode((YangNode) listener.getParsedDataStack().pop());
        } else {
            throw new ParserException(constructListenerErrorMessage(INVALID_CHILD, YANGBASE_DATA, "", EXIT));
        }

        // Check if stack is empty.
        checkStackIsEmpty(listener, INVALID_HOLDER, YANGBASE_DATA, "", EXIT);
    }
}
