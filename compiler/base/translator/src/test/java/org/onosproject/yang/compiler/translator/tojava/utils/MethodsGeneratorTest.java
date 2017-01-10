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

package org.onosproject.yang.compiler.translator.tojava.utils;

import org.junit.Test;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType;
import org.onosproject.yang.compiler.translator.tojava.JavaAttributeInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaQualifiedTypeInfoTranslator;
import org.onosproject.yang.compiler.utils.UtilConstants;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.STRING;

/**
 * Unit tests for generated methods from the file type.
 */
public final class MethodsGeneratorTest {

    private static final String CLASS_NAME = "Testname";
    private static final String ATTRIBUTE_NAME = "testname";
    private static final String SET = "setValue.set(0);\n";
    private static final String UNION = "    @Override\n" +
            "    public String toString() {\n" +
            "        if (setValue.get(0)) {\n" +
            "            return string;\n" +
            "        }\n" +
            "        return null;\n" +
            "    }";

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

        Class<?>[] classesToConstruct = {MethodsGenerator.class};
        for (Class<?> clazz : classesToConstruct) {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            assertThat(null, not(constructor.newInstance()));
        }
    }

    /**
     * Unit test case for checking the parse builder and type constructor.
     */
    @Test
    public void getTypeConstructorTest() {

        JavaAttributeInfo testAttr = getTestAttribute();
        String test = MethodsGenerator.getTypeConstructorStringAndJavaDoc(
                testAttr, CLASS_NAME, GeneratedJavaFileType.GENERATE_TYPEDEF_CLASS, 0);
        assertThat(true, is(test.contains(UtilConstants.PUBLIC + UtilConstants.SPACE + CLASS_NAME +
                                                  UtilConstants.OPEN_PARENTHESIS)));
    }

    /**
     * Unit test case for checking the parse builder and type constructor.
     */
    @Test
    public void getTypeConstructorForUnionTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String test = MethodsGenerator.getTypeConstructorStringAndJavaDoc(
                testAttr, CLASS_NAME, GeneratedJavaFileType.GENERATE_UNION_CLASS, 0);
        assertThat(true, is(test.contains(UtilConstants.PUBLIC + UtilConstants.SPACE + CLASS_NAME +
                                                  UtilConstants.OPEN_PARENTHESIS)));
        assertThat(true, is(test.contains(SET)));
    }

    /**
     * Test for build method for class.
     */
    @Test
    public void getBuildTest() {
        String method = MethodsGenerator.getBuild(CLASS_NAME, false);
        assertThat(true, is(method.equals(
                UtilConstants.FOUR_SPACE_INDENTATION + UtilConstants.PUBLIC + UtilConstants.SPACE + CLASS_NAME + UtilConstants.SPACE +
                        UtilConstants.BUILD + UtilConstants.OPEN_PARENTHESIS + UtilConstants.CLOSE_PARENTHESIS + UtilConstants.SPACE +
                        UtilConstants.OPEN_CURLY_BRACKET + UtilConstants.NEW_LINE + UtilConstants.EIGHT_SPACE_INDENTATION +
                        UtilConstants.RETURN + UtilConstants.SPACE + UtilConstants.NEW + UtilConstants.SPACE + UtilConstants.DEFAULT_CAPS + CLASS_NAME +
                        UtilConstants.OPEN_PARENTHESIS + UtilConstants.THIS + UtilConstants.CLOSE_PARENTHESIS +
                        UtilConstants.SEMI_COLON + UtilConstants.NEW_LINE + UtilConstants.FOUR_SPACE_INDENTATION +
                        UtilConstants.CLOSE_CURLY_BRACKET + UtilConstants.NEW_LINE)));
    }

    /**
     * Test for build method of interface.
     */
    @Test
    public void getBuildForInterfaceTest() {
        String method = MethodsGenerator.getBuildForInterface(CLASS_NAME);
        assertThat(true, is(method.equals(
                UtilConstants.FOUR_SPACE_INDENTATION + CLASS_NAME + UtilConstants.SPACE + UtilConstants.BUILD +
                        UtilConstants.OPEN_PARENTHESIS + UtilConstants.CLOSE_PARENTHESIS + UtilConstants.SEMI_COLON +
                        UtilConstants.NEW_LINE)));
    }

    /**
     * Test for check not null method.
     */
    @Test
    public void getCheckNotNullTest() {
        String method = StringGenerator.getCheckNotNull(CLASS_NAME);
        assertThat(true, is(method.equals(
                UtilConstants.EIGHT_SPACE_INDENTATION + UtilConstants.CHECK_NOT_NULL_STRING +
                        UtilConstants.OPEN_PARENTHESIS + CLASS_NAME + UtilConstants.COMMA + UtilConstants.SPACE +
                        CLASS_NAME + UtilConstants.CLOSE_PARENTHESIS + UtilConstants.SEMI_COLON + UtilConstants.NEW_LINE)));
    }

    /**
     * Test case for constructor.
     */
    @Test
    public void getConstructorTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String method = MethodsGenerator.getConstructor(testAttr, GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER
        );
        assertThat(true, is(method.contains(
                ATTRIBUTE_NAME + UtilConstants.SPACE + UtilConstants.EQUAL + UtilConstants.SPACE +
                        UtilConstants.BUILDER_LOWER_CASE + UtilConstants.OBJECT + UtilConstants.PERIOD +
                        UtilConstants.GET_METHOD_PREFIX + CLASS_NAME + UtilConstants.OPEN_PARENTHESIS +
                        UtilConstants.CLOSE_PARENTHESIS + UtilConstants.SEMI_COLON)));
    }

    /**
     * Test for constructor start method.
     */
    @Test
    public void getConstructorStartTest() {
        String method = MethodsGenerator.getConstructorStart(CLASS_NAME, false);
        assertThat(true, is(method.contains(
                UtilConstants.PROTECTED + UtilConstants.SPACE + UtilConstants.DEFAULT_CAPS + CLASS_NAME +
                        UtilConstants.OPEN_PARENTHESIS + CLASS_NAME + UtilConstants.BUILDER + UtilConstants.SPACE +
                        UtilConstants.BUILDER_LOWER_CASE + UtilConstants.OBJECT + UtilConstants.CLOSE_PARENTHESIS + UtilConstants.SPACE +
                        UtilConstants.OPEN_CURLY_BRACKET)));
    }

    /**
     * Test case for equals method.
     */
    @Test
    public void getEqualsMethodTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String method = MethodsGenerator.getEqualsMethod(testAttr);
        assertThat(true, is(method.contains(
                UtilConstants.SIXTEEN_SPACE_INDENTATION + UtilConstants.OBJECT_STRING + UtilConstants.SUFFIX_S +
                        UtilConstants.PERIOD + UtilConstants.EQUALS_STRING + UtilConstants.OPEN_PARENTHESIS)));
    }

    /**
     * Test for to string method.
     */
    @Test
    public void getToStringMethodTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String method = MethodsGenerator.getToStringMethod(testAttr);
        assertThat(true, is(method.equals(
                UtilConstants.TWELVE_SPACE_INDENTATION + UtilConstants.PERIOD + UtilConstants.ADD_STRING +
                        UtilConstants.OPEN_PARENTHESIS + UtilConstants.QUOTES + testAttr.getAttributeName() +
                        UtilConstants.QUOTES + UtilConstants.COMMA + UtilConstants.SPACE + testAttr.getAttributeName() +
                        UtilConstants.CLOSE_PARENTHESIS)));
    }

    /**
     * Test for to string method.
     */
    @Test
    public void getToStringMethodForUnionTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        List<YangType<?>> types = new ArrayList<>();
        types.add(testAttr.getAttributeType());
        String method = MethodsGenerator.getUnionToStringMethod(types);
        assertThat(true, is(method.contains(UNION)));
    }

    /**
     * Test for getter method of class.
     */
    @Test
    public void getGetterForClassTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String method = MethodsGenerator.getGetterForClass(testAttr, GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER);
        assertThat(true, is(method.contains(UtilConstants.PUBLIC + UtilConstants.SPACE + UtilConstants.STRING_DATA_TYPE +
                                                    UtilConstants.SPACE + UtilConstants.GET_METHOD_PREFIX)));
    }

    /**
     * Test for getter of interface.
     */
    @Test
    public void getGetterForInterfaceTest() {
        String method = MethodsGenerator.getGetterForInterface(CLASS_NAME, UtilConstants.STRING_DATA_TYPE, false,
                                                               GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER, null);
        assertThat(true, is(method.contains(UtilConstants.STRING_DATA_TYPE + UtilConstants.SPACE +
                                                    UtilConstants.GET_METHOD_PREFIX)));
    }

    /**
     * Test case for setter method of class.
     */
    @Test
    public void getSetterForClassTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String method = MethodsGenerator.getSetterForClass(testAttr, CLASS_NAME,
                                                           GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER);
        assertThat(true, is(
                method.contains(UtilConstants.PUBLIC + UtilConstants.SPACE + UtilConstants.VOID + UtilConstants.SPACE + UtilConstants.SET_METHOD_PREFIX +
                                        CLASS_NAME + UtilConstants.OPEN_PARENTHESIS +
                                        UtilConstants.STRING_DATA_TYPE + UtilConstants.SPACE +
                                        ATTRIBUTE_NAME)));
    }

    /**
     * Test for setter method of interface.
     */
    @Test
    public void getSetterForInterfaceTest() {
        String method = MethodsGenerator.getSetterForInterface(CLASS_NAME, UtilConstants.STRING_DATA_TYPE,
                                                               CLASS_NAME, false,
                                                               GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER, null);
        assertThat(true, is(method.contains(UtilConstants.VOID + UtilConstants.SPACE + UtilConstants.SET_METHOD_PREFIX +
                                                    CLASS_NAME)));
    }

    /**
     * Test case for of method.
     */
    @Test
    public void getOfMethodTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String method = MethodsGenerator.getOfMethodStringAndJavaDoc(testAttr, CLASS_NAME);
        assertThat(true, is(method.contains(
                UtilConstants.PUBLIC + UtilConstants.SPACE + UtilConstants.STATIC + UtilConstants.SPACE + CLASS_NAME + UtilConstants.SPACE + UtilConstants.OF +
                        UtilConstants.OPEN_PARENTHESIS + UtilConstants.STRING_DATA_TYPE + UtilConstants.SPACE + UtilConstants.VALUE +
                        UtilConstants.CLOSE_PARENTHESIS)));
    }

    /**
     * Test case for setter in type def class.
     */
    @Test
    public void getSetterForTypeDefClassTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String method = MethodsGenerator.getSetterForTypeDefClass(testAttr);
        assertThat(true, is(method.contains(UtilConstants.PUBLIC + UtilConstants.SPACE + UtilConstants.VOID + UtilConstants.SPACE +
                                                    UtilConstants.SET_METHOD_PREFIX)));
    }

    /**
     * Test case for over ride string.
     */
    @Test
    public void getOverRideStringTest() {
        String method = StringGenerator.getOverRideString();
        assertThat(true, is(method.contains(UtilConstants.OVERRIDE)));
    }

    /**
     * Returns java attribute.
     *
     * @return java attribute
     */
    private JavaAttributeInfo getTestAttribute() {
        JavaAttributeInfo testAttr = new JavaAttributeInfo(
                getTestYangType(), ATTRIBUTE_NAME, false, false);
        testAttr.setAttributeName(ATTRIBUTE_NAME);
        testAttr.setAttributeType(getTestYangType());
        testAttr.setImportInfo(getTestJavaQualifiedTypeInfo());
        return testAttr;
    }

    /**
     * Returns java qualified info.
     *
     * @return java qualified info
     */
    private JavaQualifiedTypeInfoTranslator getTestJavaQualifiedTypeInfo() {
        JavaQualifiedTypeInfoTranslator info = new JavaQualifiedTypeInfoTranslator();
        info.setPkgInfo(UtilConstants.JAVA_LANG);
        info.setClassInfo(UtilConstants.STRING_DATA_TYPE);
        return info;
    }

    /**
     * Returns stub YANG type.
     *
     * @return test YANG type
     */
    private YangType<?> getTestYangType() {
        YangType<?> attrType = new YangType<>();
        attrType.setDataTypeName(UtilConstants.STRING_DATA_TYPE);
        attrType.setDataType(STRING);
        return attrType;
    }
}
