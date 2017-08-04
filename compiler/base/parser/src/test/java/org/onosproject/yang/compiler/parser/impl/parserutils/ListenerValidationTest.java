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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onosproject.yang.compiler.datamodel.YangRevision;
import org.onosproject.yang.compiler.datamodel.utils.YangConstructType;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;

/**
 * Test case for testing listener validation util.
 */
public class ListenerValidationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Checks for exception in case parsable stack is empty while validating for
     * not empty scenario.
     */
    @Test
    public void validateStackIsNotEmptyForEmptyStack() {

        String expectedError = constructListenerErrorMessage(ListenerErrorType.MISSING_HOLDER,
                                                             YangConstructType.YANGBASE_DATA,
                                                             "", ListenerErrorLocation.EXIT);

        // Get the exception occurred during parsing.
        thrown.expect(ParserException.class);
        thrown.expectMessage(expectedError);

        // Create test walker and assign test error to it.
        TreeWalkListener testWalker = new TreeWalkListener();

        ListenerValidation.checkStackIsNotEmpty(testWalker, ListenerErrorType.MISSING_HOLDER,
                                                YangConstructType.YANGBASE_DATA, "", ListenerErrorLocation.EXIT);
    }

    /**
     * Checks if there is no exception in case parsable stack is not empty while
     * validating for not empty scenario.
     */
    @Test
    public void validateStackIsNotEmptyForNonEmptyStack() {

        // Create test walker and assign test error to it.
        TreeWalkListener testWalker = new TreeWalkListener();

        // Create a temporary node of parsable.
        YangRevision tmpNode = new YangRevision();
        testWalker.getParsedDataStack().push(tmpNode);

        ListenerValidation.checkStackIsNotEmpty(testWalker, ListenerErrorType.MISSING_HOLDER,
                                                YangConstructType.YANGBASE_DATA, "", ListenerErrorLocation.EXIT);
    }

    /**
     * Checks for exception in case parsable stack is not empty while validating
     * for empty scenario.
     */
    @Test
    public void validateStackIsEmptyForNonEmptyStack() {

        String expectedError = constructListenerErrorMessage(ListenerErrorType.MISSING_HOLDER,
                                                             YangConstructType.YANGBASE_DATA,
                                                             "", ListenerErrorLocation.EXIT);

        // Get the exception occurred during parsing.
        thrown.expect(ParserException.class);
        thrown.expectMessage(expectedError);

        // Create test walker and assign test error to it.
        TreeWalkListener testWalker = new TreeWalkListener();

        // Create a temporary node of parsable.
        YangRevision tmpNode = new YangRevision();
        testWalker.getParsedDataStack().push(tmpNode);

        ListenerValidation.checkStackIsEmpty(testWalker, ListenerErrorType.MISSING_HOLDER,
                                             YangConstructType.YANGBASE_DATA, "", ListenerErrorLocation.EXIT);
    }

    /**
     * Checks if there is no exception in case parsable stack is empty while
     * validating for empty scenario.
     */
    @Test
    public void validateStackIsEmptyForEmptyStack() {

        // Create test walker and assign test error to it.
        TreeWalkListener testWalker = new TreeWalkListener();

        ListenerValidation.checkStackIsEmpty(testWalker, ListenerErrorType.MISSING_HOLDER,
                                             YangConstructType.YANGBASE_DATA, "", ListenerErrorLocation.EXIT);
    }
}
