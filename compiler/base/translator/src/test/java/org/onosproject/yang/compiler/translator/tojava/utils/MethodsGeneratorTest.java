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
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.translator.tojava.JavaAttributeInfo;
import org.onosproject.yang.compiler.translator.tojava.JavaQualifiedTypeInfoTranslator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.STRING;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_SERVICE_AND_MANAGER;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_TYPEDEF_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.GeneratedJavaFileType.GENERATE_UNION_CLASS;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getGetterForClass;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getGetterForInterface;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getSetterForClass;
import static org.onosproject.yang.compiler.translator.tojava.utils.MethodsGenerator.getSetterForInterface;
import static org.onosproject.yang.compiler.utils.UtilConstants.ADD;
import static org.onosproject.yang.compiler.utils.UtilConstants.ADD_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.CHECK_NOT_NULL_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.COMMA;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.EQUAL;
import static org.onosproject.yang.compiler.utils.UtilConstants.GET_METHOD_PREFIX;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_LANG;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.OF;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.OVERRIDE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.PUBLIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.QUOTES;
import static org.onosproject.yang.compiler.utils.UtilConstants.SEMI_COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.SET_METHOD_PREFIX;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.STATIC;
import static org.onosproject.yang.compiler.utils.UtilConstants.STRING_DATA_TYPE;
import static org.onosproject.yang.compiler.utils.UtilConstants.TWELVE_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.VALUE;
import static org.onosproject.yang.compiler.utils.UtilConstants.VOID;

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
                testAttr, CLASS_NAME, GENERATE_TYPEDEF_CLASS, 0);
        StringBuilder builder = new StringBuilder()
                .append(PUBLIC).append(SPACE).append(CLASS_NAME)
                .append(OPEN_PARENTHESIS);
        assertThat(true, is(test.contains(builder.toString())));
    }

    /**
     * Unit test case for checking the parse builder and type constructor.
     */
    @Test
    public void getTypeConstructorForUnionTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String test = MethodsGenerator.getTypeConstructorStringAndJavaDoc(
                testAttr, CLASS_NAME, GENERATE_UNION_CLASS, 0);
        StringBuilder builder = new StringBuilder()
                .append(PUBLIC).append(SPACE).append(CLASS_NAME)
                .append(OPEN_PARENTHESIS);
        assertThat(true, is(test.contains(builder.toString())));
        assertThat(true, is(test.contains(SET)));
    }

    /**
     * Test for check not null method.
     */
    @Test
    public void getCheckNotNullTest() {
        String method = StringGenerator.getCheckNotNull(CLASS_NAME);
        StringBuilder builder = new StringBuilder()
                .append(EIGHT_SPACE_INDENTATION).append(CHECK_NOT_NULL_STRING)
                .append(OPEN_PARENTHESIS).append(CLASS_NAME).append(COMMA)
                .append(SPACE).append(CLASS_NAME).append(CLOSE_PARENTHESIS)
                .append(SEMI_COLON).append(NEW_LINE);
        assertThat(true, is(method.equals(builder.toString())));
    }

    /**
     * Test case for equals method.
     */
    @Test
    public void getEqualsMethodTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String method = MethodsGenerator.getEqualsMethod(testAttr);
        assertThat(method, containsString(testAttr.getAttributeName()));
    }

    /**
     * Test for to string method.
     */
    @Test
    public void getToStringMethodTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String method = MethodsGenerator.getToStringMethod(testAttr);
        StringBuilder builder = new StringBuilder()
                .append(TWELVE_SPACE_INDENTATION).append(PERIOD)
                .append(ADD_STRING).append(OPEN_PARENTHESIS).append(QUOTES)
                .append(testAttr.getAttributeName()).append(EQUAL)
                .append(QUOTES).append(SPACE).append(ADD).append(SPACE)
                .append(testAttr.getAttributeName()).append(CLOSE_PARENTHESIS);
        assertThat(true, is(method.equals(builder.toString())));
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
        String method = getGetterForClass(testAttr, GENERATE_SERVICE_AND_MANAGER);
        StringBuilder builder = new StringBuilder()
                .append(PUBLIC).append(SPACE).append(STRING_DATA_TYPE)
                .append(SPACE).append(GET_METHOD_PREFIX);
        assertThat(true, is(method.contains(builder.toString())));
    }

    /**
     * Test for getter of interface.
     */
    @Test
    public void getGetterForInterfaceTest() {
        String method = getGetterForInterface(CLASS_NAME, STRING_DATA_TYPE, false,
                                              GENERATE_SERVICE_AND_MANAGER, null);
        StringBuilder builder = new StringBuilder()
                .append(STRING_DATA_TYPE).append(SPACE)
                .append(GET_METHOD_PREFIX);
        assertThat(true, is(method.contains(builder.toString())));
    }

    /**
     * Test case for setter method of class.
     */
    @Test
    public void getSetterForClassTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String method = getSetterForClass(
                testAttr, GENERATE_SERVICE_AND_MANAGER);
        StringBuilder builder = new StringBuilder()
                .append(PUBLIC).append(SPACE).append(VOID).append(SPACE)
                .append(SET_METHOD_PREFIX).append(CLASS_NAME)
                .append(OPEN_PARENTHESIS).append(STRING_DATA_TYPE).append(SPACE)
                .append(ATTRIBUTE_NAME);
        assertThat(true, is(method.contains(builder.toString())));
    }

    /**
     * Test for setter method of interface.
     */
    @Test
    public void getSetterForInterfaceTest() {
        String method = getSetterForInterface(CLASS_NAME, STRING_DATA_TYPE,
                                              CLASS_NAME, false,
                                              GENERATE_SERVICE_AND_MANAGER, null);
        StringBuilder builder = new StringBuilder()
                .append(VOID).append(SPACE).append(SET_METHOD_PREFIX)
                .append(CLASS_NAME);
        assertThat(true, is(method.contains(builder.toString())));
    }

    /**
     * Test case for of method.
     */
    @Test
    public void getOfMethodTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String method = MethodsGenerator.getOfMethodStringAndJavaDoc(testAttr,
                                                                     CLASS_NAME);
        StringBuilder builder = new StringBuilder()
                .append(PUBLIC).append(SPACE).append(STATIC).append(SPACE)
                .append(CLASS_NAME).append(SPACE).append(OF)
                .append(OPEN_PARENTHESIS).append(STRING_DATA_TYPE)
                .append(SPACE).append(VALUE).append(CLOSE_PARENTHESIS);
        assertThat(true, is(method.contains(builder.toString())));
    }

    /**
     * Test case for setter in type def class.
     */
    @Test
    public void getSetterForTypeDefClassTest() {
        JavaAttributeInfo testAttr = getTestAttribute();
        String method = MethodsGenerator.getSetterForTypeDefClass(testAttr);
        StringBuilder builder = new StringBuilder()
                .append(PUBLIC).append(SPACE).append(VOID).append(SPACE)
                .append(SET_METHOD_PREFIX);
        assertThat(true, is(method.contains(builder.toString())));
    }

    /**
     * Test case for over ride string.
     */
    @Test
    public void getOverRideStringTest() {
        String method = StringGenerator.getOverRideString();
        assertThat(true, is(method.contains(OVERRIDE)));
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
        info.setPkgInfo(JAVA_LANG);
        info.setClassInfo(STRING_DATA_TYPE);
        return info;
    }

    /**
     * Returns stub YANG type.
     *
     * @return test YANG type
     */
    private YangType<?> getTestYangType() {
        YangType<?> attrType = new YangType<>();
        attrType.setDataTypeName(STRING_DATA_TYPE);
        attrType.setDataType(STRING);
        return attrType;
    }
}
