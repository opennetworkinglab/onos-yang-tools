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

package org.onosproject.yang.compiler.translator.tojava.utils;

import org.junit.Test;
import org.onosproject.yang.compiler.translator.tojava.JavaQualifiedTypeInfoTranslator;
import org.onosproject.yang.compiler.utils.UtilConstants;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.onosproject.yang.compiler.translator.tojava.utils.JavaCodeSnippetGen.getJavaAttributeDefinition;

/**
 * Unit test cases for java code snippet generator.
 */
public class JavaCodeSnippetGenTest {

    private static final String PKG_INFO = "org.onosproject.unittest";
    private static final String CLASS_INFO = "JavaCodeSnippetGenTest";
    private static final String YANG_NAME = "Test";

    /**
     * Unit test for private constructor.
     *
     * @throws SecurityException         if any security violation is observed
     * @throws NoSuchMethodException     if when the method is not found
     * @throws IllegalArgumentException  if there is illegal argument found
     * @throws InstantiationException    if instantiation is provoked for the private constructor
     * @throws IllegalAccessException    if instance is provoked or a method is provoked
     * @throws InvocationTargetException when an exception occurs by the method or constructor
     */
    @Test
    public void callPrivateConstructors()
            throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {

        Class<?>[] classesToConstruct = {JavaCodeSnippetGen.class};
        for (Class<?> clazz : classesToConstruct) {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            assertThat(null, not(constructor.newInstance()));
        }
    }

    /**
     * Unit test case for import text.
     */
    @Test
    public void testForImportText() {
        JavaQualifiedTypeInfoTranslator importInfo = new JavaQualifiedTypeInfoTranslator();
        importInfo.setPkgInfo(PKG_INFO);
        importInfo.setClassInfo(CLASS_INFO);

        String imports = JavaCodeSnippetGen.getImportText(importInfo);

        assertThat(true, is(imports.equals(UtilConstants.IMPORT + PKG_INFO +
                                                   UtilConstants.PERIOD + CLASS_INFO +
                                                   UtilConstants.SEMI_COLON + UtilConstants.NEW_LINE)));
    }

    /**
     * Unit test case for java attribute info.
     */
    @Test
    public void testForJavaAttributeInfo() {
        String attributeWithoutTypePkg =
                getJavaAttributeDefinition(null, UtilConstants.STRING_DATA_TYPE, YANG_NAME,
                                           false, UtilConstants.PRIVATE, null);
        assertThat(true, is(attributeWithoutTypePkg.contains(
                UtilConstants.PRIVATE + UtilConstants.SPACE + UtilConstants.STRING_DATA_TYPE +
                        UtilConstants.SPACE + YANG_NAME + UtilConstants.SEMI_COLON + UtilConstants.NEW_LINE)));

        String attributeWithTypePkg =
                getJavaAttributeDefinition(UtilConstants.JAVA_LANG, UtilConstants.STRING_DATA_TYPE, YANG_NAME,
                                           false, UtilConstants.PRIVATE, null);
        assertThat(true, is(attributeWithTypePkg.contains(UtilConstants.PRIVATE + UtilConstants.SPACE +
                                                                  UtilConstants.JAVA_LANG + UtilConstants.PERIOD +
                                                                  UtilConstants.STRING_DATA_TYPE +
                                                                  UtilConstants.SPACE + YANG_NAME +
                                                                  UtilConstants.SEMI_COLON + UtilConstants.NEW_LINE)));

        String attributeWithListPkg =
                getJavaAttributeDefinition(UtilConstants.JAVA_LANG, UtilConstants.STRING_DATA_TYPE, YANG_NAME,
                                           true, UtilConstants.PRIVATE, null);
        assertThat(true, is(attributeWithListPkg.contains(
                UtilConstants.PRIVATE + UtilConstants.SPACE + UtilConstants.LIST +
                        UtilConstants.DIAMOND_OPEN_BRACKET + UtilConstants.JAVA_LANG +
                        UtilConstants.PERIOD + UtilConstants.STRING_DATA_TYPE +
                        UtilConstants.DIAMOND_CLOSE_BRACKET + UtilConstants.SPACE + YANG_NAME)));

        String attributeWithListWithoutPkg =
                getJavaAttributeDefinition(null, UtilConstants.STRING_DATA_TYPE, YANG_NAME,
                                           true, UtilConstants.PRIVATE, null);
        assertThat(true, is(attributeWithListWithoutPkg.contains(
                UtilConstants.PRIVATE + UtilConstants.SPACE + UtilConstants.LIST +
                        UtilConstants.DIAMOND_OPEN_BRACKET + UtilConstants.STRING_DATA_TYPE +
                        UtilConstants.DIAMOND_CLOSE_BRACKET + UtilConstants.SPACE + YANG_NAME)));
    }
}
