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

import org.onosproject.yang.compiler.datamodel.DefaultDenyWriteExtension;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.DEFAULT_DENY_WRITE_DATA;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DefaultDenyWriteStatementContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;

public final class DefaultDenyWriteExtRefListener {

    private DefaultDenyWriteExtRefListener() {
    }

    public static void processDefaultDenyWriteStructureEntry(
            TreeWalkListener listener, DefaultDenyWriteStatementContext ctx) {
        checkStackIsNotEmpty(listener, MISSING_HOLDER, DEFAULT_DENY_WRITE_DATA,
                             "", ENTRY);

        Parsable tmpData = listener.getParsedDataStack().peek();
        if (tmpData instanceof DefaultDenyWriteExtension) {
            DefaultDenyWriteExtension holder = (DefaultDenyWriteExtension) tmpData;
            holder.setDefaultDenyWrite(true);
        } else {
            throw new ParserException(constructListenerErrorMessage(
                    INVALID_HOLDER, DEFAULT_DENY_WRITE_DATA, "", ENTRY));
        }
    }
}
