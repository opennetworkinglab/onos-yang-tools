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
package org.onosproject.yang.compiler.parser.impl.listeners;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DEFAULT_DENY_WRITE_DATA;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.EXIT;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;

import org.onosproject.yang.compiler.datamodel.YangContainer;
import org.onosproject.yang.compiler.datamodel.YangLeaf;
import org.onosproject.yang.compiler.datamodel.YangLeafList;
import org.onosproject.yang.compiler.datamodel.YangList;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

public final class DefaultDenyWriteExtRefListener {

    private DefaultDenyWriteExtRefListener() {

    }


    public static void processDefaultDenyWriteStructureEntry(TreeWalkListener listener,
            GeneratedYangParser.DefaultDenyWriteStatementContext ctx) {
        checkStackIsNotEmpty(listener, MISSING_HOLDER, DEFAULT_DENY_WRITE_DATA, "", ENTRY);

        Parsable tmpData = listener.getParsedDataStack().peek();
        if (tmpData instanceof YangContainer) {
            YangContainer holder = (YangContainer) tmpData;
            holder.setDefaultDenyWrite(true);
        } else if (tmpData instanceof YangLeaf) {
            YangLeaf holder = (YangLeaf) tmpData;
            holder.setDefaultDenyWrite(true);
        } else if (tmpData instanceof YangLeafList) {
            YangLeafList holder = (YangLeafList) tmpData;
            holder.setDefaultDenyWrite(true);
        } else if (tmpData instanceof YangList) {
            YangList holder = (YangList) tmpData;
            holder.setDefaultDenyWrite(true);
        } else {
            throw new ParserException(constructListenerErrorMessage(
                    INVALID_HOLDER, DEFAULT_DENY_WRITE_DATA, "", ENTRY));
        }
    }

    public static void processDefaultDenyWriteStructureExit(TreeWalkListener listener,
            GeneratedYangParser.DefaultDenyWriteStatementContext ctx) {
        checkStackIsNotEmpty(listener, MISSING_HOLDER, DEFAULT_DENY_WRITE_DATA, "", EXIT);
        // Nothing.to do
    }
}
