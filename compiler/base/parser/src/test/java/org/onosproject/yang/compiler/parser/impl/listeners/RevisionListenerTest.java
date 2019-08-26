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

import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Test cases for testing revision listener functionality.
 */
public class RevisionListenerTest {

    private final YangUtilsParserManager manager = new YangUtilsParserManager();

    /**
     * Checks if revision doesn't have optional parameters "revision and
     * description".
     */
    @Test
    public void processRevisionNoOptionalParameter() throws IOException, ParserException, ParseException {

        YangNode node = manager.getDataModel("src/test/resources/RevisionNoOptionalParameter.yang");
        // Checks for the version value in data model tree.
        assertThat(((YangModule) node).getRevision().getRevDate(), is(LocalDate.parse("2016-02-03")));
    }

    /**
     * Checks if the syntax of revision is correct.
     */
    @Test(expected = ParserException.class)
    public void processRevisionInValidSyntax() throws IOException, ParserException {

        YangNode node = manager.getDataModel("src/test/resources/RevisionInValidSyntax.yang");
    }

    /**
     * Checks if the correct order is followed.
     */
    @Test(expected = ParserException.class)
    public void processRevisionInValidOrder() throws IOException, ParserException {

        YangNode node = manager.getDataModel("src/test/resources/RevisionInValidOrder.yang");
    }

    /**
     * Checks the revision with current date is created for empty revision statement.
     */
    @Test
    public void processWithoutRevision() throws IOException, ParserException {

        YangNode node = manager.getDataModel("src/test/resources/RevisionAbsence.yang");
        assertThat(true, is((node).getRevision() == null));
    }

    /**
     * Checks latest date is stored when there are multiple revisions.
     */
    @Test
    public void processWithMultipleRevision() throws IOException, ParserException, ParseException {

        YangNode node = manager.getDataModel("src/test/resources/MultipleRevision.yang");
        assertThat((node).getRevision().getRevDate(), is(LocalDate.parse("2013-07-15")));
    }

    /**
     * Checks revision month of date in single digit format.
     */
    @Test
    public void processRevisionDateWithSingleDigitMonth() throws IOException, ParserException, ParseException {
        YangNode node = manager.getDataModel("src/test/resources/RevisionMonthSingleDigit.yang");
        assertThat((node).getRevision().getRevDate(), is(LocalDate.parse("2013-07-15")));
    }

    /**
     * Checks revision day in single digit format.
     */
    @Test
    public void processRevisionDateWithSingleDigitDay() throws IOException, ParserException, ParseException {
        YangNode node = manager.getDataModel("src/test/resources/RevisionDaySingleDigit.yang");
        assertThat((node).getRevision().getRevDate(), is(LocalDate.parse("2013-10-07")));
    }

    /**
     * Checks revision day and month in single digit format.
     */
    @Test
    public void processRevisionDateWithSingleDigitDayMonth() throws IOException, ParserException, ParseException {
        YangNode node = manager.getDataModel("src/test/resources/RevisionDayMonthSingleDigit.yang");
        assertThat((node).getRevision().getRevDate(), is(LocalDate.parse("2013-07-07")));
    }
}