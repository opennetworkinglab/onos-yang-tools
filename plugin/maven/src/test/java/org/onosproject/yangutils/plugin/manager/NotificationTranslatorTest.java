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

package org.onosproject.yangutils.plugin.manager;

import org.junit.Test;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.parser.exceptions.ParserException;
import org.onosproject.yangutils.parser.impl.YangUtilsParserManager;
import org.onosproject.yangutils.utils.io.YangPluginConfig;

import java.io.IOException;

import static org.onosproject.yangutils.translator.tojava.JavaCodeGeneratorUtil.generateJavaCode;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.deleteDirectory;

/**
 * Unit tests for union translator.
 */
public final class NotificationTranslatorTest {

    private final YangUtilsParserManager manager = new YangUtilsParserManager();
    private final static String YANG = "src/test/resources/NotificationTest" +
            ".yang";
    private final static String DIR = "target/notificationTranslator/";

    /**
     * Checks union translation should not result in any exception.
     */
    @Test
    public void processNotificationTranslator()
            throws IOException, ParserException {
        deleteDirectory(DIR);
        YangNode node = manager.getDataModel(YANG);

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);

        generateJavaCode(node, yangPluginConfig);
        deleteDirectory(DIR);
    }

    // TODO enhance the test cases, after having a framework of translator test.
}
