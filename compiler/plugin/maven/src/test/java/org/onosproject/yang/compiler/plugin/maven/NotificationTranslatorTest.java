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

package org.onosproject.yang.compiler.plugin.maven;

import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.YangUtilsParserManager;
import org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorUtil;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.onosproject.yang.compiler.utils.io.impl.YangIoUtils;

import java.io.IOException;

/**
 * Unit tests for union translator.
 */
public final class NotificationTranslatorTest {

    private final YangUtilsParserManager manager = new YangUtilsParserManager();
    private static final String YANG = "src/test/resources/NotificationTest.yang";
    private static final String DIR = "target/notificationTranslator/";

    /**
     * Checks union translation should not result in any exception.
     */
    @Test
    public void processNotificationTranslator()
            throws IOException, ParserException {
        YangIoUtils.deleteDirectory(DIR);
        YangNode node = manager.getDataModel(YANG);

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);

        JavaCodeGeneratorUtil.generateJavaCode(node, yangPluginConfig);
        YangIoUtils.deleteDirectory(DIR);
    }

    // TODO enhance the test cases, after having a framework of translator test.
}
