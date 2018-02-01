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

package org.onosproject.yang.compiler.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents utilities constants which are used while generating java files.
 */
public final class UtilConstants {

    /**
     * JavaDocs for impl class.
     */
    public static final String IMPL_CLASS_JAVA_DOC =
            " * Represents the implementation of ";

    /**
     * JavaDocs for interface class.
     */
    public static final String INTERFACE_JAVA_DOC =
            " * Abstraction of an entity which represents the functionality " +
                    "of ";

    /**
     * JavaDocs for event.
     */
    public static final String EVENT_JAVA_DOC =
            " * Represents event implementation of ";

    /**
     * JavaDocs for op param class.
     */
    public static final String OP_PARAM_JAVA_DOC =
            " * Represents operation parameter implementation of ";

    /**
     * JavaDocs for event listener.
     */
    public static final String EVENT_LISTENER_JAVA_DOC =
            " * Abstraction for event listener of ";

    /**
     * JavaDocs for enum class.
     */
    public static final String ENUM_CLASS_JAVADOC =
            " * Represents ENUM data of ";

    /**
     * JavaDocs for enum attribute.
     */
    public static final String ENUM_ATTRIBUTE_JAVADOC = " * Represents ";

    /**
     * JavaDocs for package info class.
     */
    public static final String PACKAGE_INFO_JAVADOC =
            " * Implementation of YANG node ";

    /**
     * JavaDocs for package info class.
     */
    public static final String PACKAGE_INFO_JAVADOC_OF_CHILD =
            "'s children nodes";

    /**
     * JavaDocs's first line.
     */
    public static final String JAVA_DOC_FIRST_LINE = "/**\n";

    /**
     * JavaDocs's last line.
     */
    public static final String JAVA_DOC_END_LINE = " */\n";

    /**
     * JavaDocs's param annotation.
     */
    public static final String JAVA_DOC_PARAM = " * @param ";

    /**
     * JavaDocs's return annotation.
     */
    public static final String JAVA_DOC_RETURN = " * @return ";

    /**
     * JavaDocs's description for setter method.
     */
    public static final String JAVA_DOC_SETTERS =
            " * Sets the attribute ";

    /**
     * JavaDocs's description for add to list method.
     */
    public static final String JAVA_DOC_ADD_TO_LIST = " * Adds to the list of ";

    /**
     * JavaDocs's description for setter method.
     */
    public static final String JAVA_DOC_MANAGER_SETTERS =
            " * Sets the value to attribute ";

    /**
     * JavaDocs's description for OF method.
     */
    public static final String JAVA_DOC_OF = " * Returns the object of ";

    /**
     * JavaDocs's description for typedef' setter method.
     */
    public static final String JAVA_DOC_SETTERS_COMMON =
            " * Sets the value of ";

    /**
     * JavaDocs's description for getter method.
     */
    public static final String JAVA_DOC_GETTERS = " * Returns the attribute ";

    /**
     * JavaDocs's description for getter method.
     */
    public static final String JAVA_DOC_FOR_VALIDATOR =
            " * Validates if value is in given range.";

    /**
     * JavaDocs's description for getter method.
     */
    public static final String JAVA_DOC_FOR_VALIDATOR_RETURN =
            " true if value is in range";

    /**
     * JavaDocs's description for constructor.
     */
    public static final String JAVA_DOC_CONSTRUCTOR =
            " * Creates an instance of ";

    /**
     * JavaDocs's description for build method.
     */
    public static final String JAVA_DOC_BUILD = " * Builds object of ";

    /**
     * JavaDocs's statement for rpc method.
     */
    public static final String JAVA_DOC_RPC = " * Service interface of ";

    /**
     * JavaDocs's statement for rpc's input string.
     */
    public static final String RPC_INPUT_STRING = "input of service interface ";

    /**
     * JavaDocs's statement for rpc's output string.
     */
    public static final String RPC_OUTPUT_STRING =
            "output of service interface ";

    /**
     * Static attribute for new line.
     */
    public static final String NEW_LINE = "\n";

    /**
     * Static attribute for default.
     */
    public static final String DEFAULT = "default";

    /**
     * Static attribute for default.
     */
    public static final String DEFAULT_CAPS = "Default";

    /**
     * Static attribute for java code generation for sbi.
     */
    public static final String SBI = "sbi";

    /**
     * Static attribute for multiple new line.
     */
    public static final String MULTIPLE_NEW_LINE = "\n\n";

    /**
     * Static attribute for empty line.
     */
    public static final String EMPTY_STRING = "";

    /**
     * Static attribute for new line with asterisk.
     */
    public static final String NEW_LINE_ASTERISK = " *\n";

    /**
     * Static attribute for period.
     */
    public static final String PERIOD = ".";

    /**
     * Static attribute for lib.
     */
    public static final String LIB = "lib__";

    /**
     * Static attribute for lib path.
     */
    public static final String LIB_PATH = "//lib:";

    /**
     * Static attribute for output.
     */
    public static final String OUT = "__output";

    /**
     * Static attribute for period.
     */
    public static final String ENTRY = "Entry";

    /**
     * Static attribute for compare to.
     */
    public static final String COMPARE_TO = "compareTo";

    /**
     * Static attribute for parse byte.
     */
    public static final String PARSE_BYTE = "parseByte";

    /**
     * Static attribute for parse boolean.
     */
    public static final String PARSE_BOOLEAN = "parseBoolean";

    /**
     * Static attribute for parse short.
     */
    public static final String PARSE_SHORT = "parseShort";

    /**
     * Static attribute for parse int.
     */
    public static final String PARSE_INT = "parseInt";

    /**
     * Static attribute for parse long.
     */
    public static final String PARSE_LONG = "parseLong";

    /**
     * Static attribute for base64.
     */
    public static final String BASE64 = "Base64";

    /**
     * Static attribute for getEncoder.
     */
    public static final String GET_ENCODER = "getEncoder";

    /**
     * Static attribute for encodeToString.
     */
    public static final String ENCODE_TO_STRING = "encodeToString";

    /**
     * Static attribute for getDecoder.
     */
    public static final String GET_DECODER = "getDecoder";

    /**
     * Static attribute for decode.
     */
    public static final String DECODE = "decode";

    /**
     * Static attribute for omit null value.
     */
    public static final String OMIT_NULL_VALUE_STRING = "omitNullValues()";

    /**
     * Static attribute for underscore.
     */
    public static final String UNDER_SCORE = "_";

    /**
     * Static attribute for semi-colan.
     */
    public static final String SEMI_COLON = ";";

    /**
     * Static attribute for hyphen.
     */
    public static final String HYPHEN = "-";

    /**
     * Static attribute for space.
     */
    public static final String SPACE = " ";

    /**
     * Static attribute for schema name.
     */
    public static final String SCHEMA_NAME = "schemaName";

    /**
     * Static attribute for validateRange.
     */
    public static final String VALIDATE_RANGE = "validateRange";

    /**
     * Static attribute for minRange.
     */
    public static final String MIN_RANGE = "minRange";

    /**
     * Static attribute for maxRange.
     */
    public static final String MAX_RANGE = "maxRange";

    /**
     * Static attribute for minRange.
     */
    public static final String SHORT_MIN_RANGE_ATTR =
            "static final int INT16_MIN_RANGE = -32768;\n";

    /**
     * Static attribute for minRange.
     */
    public static final String SHORT_MIN_RANGE = "INT16_MIN_RANGE";

    /**
     * Static attribute for minRange.
     */
    public static final String SHORT_MAX_RANGE = "INT16_MAX_RANGE";

    /**
     * Static attribute for maxRange.
     */
    public static final String SHORT_MAX_RANGE_ATTR =
            "static final int INT16_MAX_RANGE = 32767;";


    /**
     * Static attribute for minRange.
     */
    public static final String UINT8_MIN_RANGE_ATTR =
            "static final int UINT8_MIN_RANGE = 0;\n";

    /**
     * Static attribute for maxRange.
     */
    public static final String UINT8_MAX_RANGE_ATTR =
            "static final int UINT8_MAX_RANGE = 32767;";


    /**
     * Static attribute for minRange.
     */
    public static final String UINT8_MIN_RANGE = "UINT8_MIN_RANGE";

    /**
     * Static attribute for maxRange.
     */
    public static final String UINT8_MAX_RANGE = "UINT8_MAX_RANGE";

    /**
     * Static attribute for minRange.
     */
    public static final String INT_MIN_RANGE_ATTR =
            "static final int INT32_MIN_RANGE = -2147483648;\n";

    /**
     * Static attribute for minRange.
     */
    public static final String INT_MIN_RANGE = "INT32_MIN_RANGE";

    /**
     * Static attribute for minRange.
     */
    public static final String INT_MAX_RANGE = "INT32_MAX_RANGE";

    /**
     * Static attribute for maxRange.
     */
    public static final String INT_MAX_RANGE_ATTR =
            "static final int INT32_MAX_RANGE = 2147483647;";


    /**
     * Static attribute for minRange.
     */
    public static final String UINT_MIN_RANGE_ATTR =
            "static final int UINT16_MIN_RANGE = 0;\n";

    /**
     * Static attribute for maxRange.
     */
    public static final String UINT_MAX_RANGE_ATTR =
            "static final int UINT16_MAX_RANGE = 2147483647;";


    /**
     * Static attribute for minRange.
     */
    public static final String UINT_MIN_RANGE = "UINT16_MIN_RANGE";

    /**
     * Static attribute for maxRange.
     */
    public static final String UINT_MAX_RANGE = "UINT16_MAX_RANGE";

    /**
     * Static attribute for minRange.
     */
    public static final String LONG_MIN_RANGE_ATTR =
            "static final BigInteger INT64_MIN_RANGE =" +
                    " new BigInteger(\"-9223372036854775808\");\n";

    /**
     * Static attribute for maxRange.
     */
    public static final String LONG_MAX_RANGE_ATTR =
            "static final BigInteger INT64_MAX_RANGE =" +
                    " new BigInteger(\"9223372036854775807\");";

    /**
     * Static attribute for minRange.
     */
    public static final String LONG_MIN_RANGE = "INT64_MIN_RANGE";

    /**
     * Static attribute for maxRange.
     */
    public static final String LONG_MAX_RANGE = "INT64_MAX_RANGE";

    /**
     * Static attribute for minRange.
     */
    public static final String ULONG_MIN_RANGE_ATTR =
            "static final BigInteger UINT32_MIN_RANGE =" +
                    " new BigInteger(\"0\");\n";

    /**
     * Static attribute for maxRange.
     */
    public static final String ULONG_MAX_RANGE_ATTR =
            "static final BigInteger UINT32_MAX_RANGE =" +
                    " new BigInteger(\"9223372036854775807\");";


    /**
     * Static attribute for minRange.
     */
    public static final String ULONG_MIN_RANGE = "UINT32_MIN_RANGE";

    /**
     * Static attribute for maxRange.
     */
    public static final String ULONG_MAX_RANGE = "UINT32_MAX_RANGE";

    /**
     * Static attribute for subject.
     */
    public static final String SUBJECT = "Subject";

    /**
     * Static attribute for ListenerService.
     */
    public static final String LISTENER_SERVICE = "ListenerService";

    /**
     * Static attribute for listener package.
     */
    public static final String ONOS_EVENT_PKG = "org.onosproject.event";

    /**
     * Static attribute for colon.
     */
    public static final String COLON = ":";

    /**
     * Static attribute for caret.
     */
    public static final String CARET = "^";

    /**
     * Static attribute for input string.
     */
    public static final String INPUT = "input";

    /**
     * Static attribute for output string.
     */
    public static final String OUTPUT = "output";

    /**
     * Static attribute for current string.
     */
    public static final String CURRENT = "current";

    /**
     * Static attribute for leafref string.
     */
    public static final String LEAFREF = "leafref";

    /**
     * Static attribute for output variable of rpc.
     */
    public static final String RPC_INPUT_VAR_NAME = "inputVar";

    /**
     * Static attribute for new line.
     */
    public static final String EQUAL = "=";

    /**
     * Static attribute for slash syntax.
     */
    public static final String SLASH = File.separator;

    /**
     * Static attribute for add syntax.
     */
    public static final String ADD = "+";

    /**
     * Static attribute for quotes.
     */
    public static final String QUOTES = "\"";

    /**
     * Static attribute for zero.
     */
    public static final String ZERO = "0";

    /**
     * Static attribute for ampersand.
     */
    public static final String AND = "&";

    /**
     * Static attribute for comma.
     */
    public static final String COMMA = ",";

    /**
     * Static attribute for class.
     */
    public static final String CLASS_STRING = "Class";

    /**
     * Static attribute for put.
     */
    public static final String PUT = "put";

    /**
     * Static attribute for get.
     */
    public static final String GET = "get";

    /**
     * Static attribute for slash character.
     */
    public static final char CHAR_OF_SLASH = '/';

    /**
     * Static attribute for open square bracket character.
     */
    public static final char CHAR_OF_OPEN_SQUARE_BRACKET = '[';

    /**
     * Static attribute for slash string.
     */
    public static final String SLASH_FOR_STRING = "/";

    /**
     * Static attribute for open square bracket.
     */
    public static final String OPEN_SQUARE_BRACKET = "[";

    /**
     * Static attribute for ancestor accessor.
     */
    public static final String ANCESTOR = "..";

    /**
     * Static attribute for ancestor accessor along with path.
     */
    public static final String SLASH_ANCESTOR = "../";

    /**
     * Static attribute for add syntax.
     */
    public static final String ADD_STRING = "add";

    /**
     * Static attribute for key syntax.
     */
    public static final String KEYS = "Keys";

    /**
     * Static attribute for -1 to syntax.
     */
    public static final String NEG_ONE = "-1";

    /**
     * Static attribute for Comparable to syntax.
     */
    public static final String COMPARABLE = "Comparable";

    /**
     * Static attribute for string trim syntax.
     */
    public static final String TRIM_STRING = "trim";

    /**
     * Static attribute for string split syntax.
     */
    public static final String SPLIT_STRING = "split";

    /**
     * Static attribute for Pattern.
     */
    public static final String PATTERN = "Pattern";

    /**
     * Static attribute for Quote.
     */
    public static final String QUOTE_STRING = "quote";

    /**
     * Static attribute for from syntax.
     */
    public static final String FROM_STRING_METHOD_NAME = "fromString";

    /**
     * Static attribute for check not null syntax.
     */
    public static final String CHECK_NOT_NULL_STRING = "checkNotNull";

    /**
     * Static attribute for hash code syntax.
     */
    public static final String HASH_CODE_STRING = "hashCode";

    /**
     * Static attribute for equals syntax.
     */
    public static final String EQUALS_STRING = "equals";

    /**
     * Static attribute for object.
     */
    public static final String OBJECT_STRING = "Object";

    /**
     * Static attribute for instance of syntax.
     */
    public static final String INSTANCE_OF = " instanceof ";

    /**
     * Static attribute for value syntax.
     */
    public static final String VALUE = "value";

    /**
     * Static attribute for value syntax.
     */
    public static final String VALUE_CAPS = "Value";

    /**
     * Static attribute for suffix s.
     */
    public static final String SUFFIX_S = "s";

    /**
     * Static attribute for string builder var.
     */
    public static final String STRING_BUILDER_VAR = "sBuild";
    /**
     * Static attribute for string builder var.
     */
    public static final String APPEND = "append";

    /**
     * Static attribute for if.
     */
    public static final String IF = "if";

    /**
     * Static attribute for of.
     */
    public static final String OF = "of";
    /**
     * Static attribute for of.
     */
    public static final String OF_CAPS = "Of";

    /**
     * Static attribute for other.
     */
    public static final String OTHER = "other";

    /**
     * Static attribute for obj syntax.
     */
    public static final String OBJ = "obj";

    /**
     * Static attribute for hash syntax.
     */
    public static final String HASH = "hash";

    /**
     * Static attribute for to syntax.
     */
    public static final String TO = "to";

    /**
     * Static attribute for to syntax.
     */
    public static final String TO_CAPS = "To";

    /**
     * Static attribute for true syntax.
     */
    public static final String TRUE = "true";

    /**
     * Static attribute for false syntax.
     */
    public static final String FALSE = "false";

    /**
     * Static attribute for org.
     */
    public static final String ORG = "org";

    /**
     * Static attribute for temp.
     */
    public static final String TEMP = "Temp";

    /**
     * Static attribute for YANG file directory.
     */
    public static final String YANG_RESOURCES = "yang/resources";

    /**
     * Static attribute for diamond close bracket syntax.
     */
    public static final String DIAMOND_OPEN_BRACKET = "<";

    /**
     * Static attribute for diamond close bracket syntax.
     */
    public static final String DIAMOND_CLOSE_BRACKET = ">";

    /**
     * Static attribute for event type.
     */
    public static final String EVENT_TYPE = ".Type";

    /**
     * Static attribute for exception syntax.
     */
    public static final String EXCEPTION = "Exception";

    /**
     * Static attribute for exception variable syntax.
     */
    public static final String EXCEPTION_VAR = "e";

    /**
     * Static attribute for open parenthesis syntax.
     */
    public static final String OPEN_PARENTHESIS = "(";

    /**
     * Static attribute for switch syntax.
     */
    public static final String SWITCH = "switch";

    /**
     * Static attribute for case syntax.
     */
    public static final String CASE = "case";

    /**
     * Static attribute for temp val syntax.
     */
    public static final String TMP_VAL = "tmpVal";

    /**
     * Static attribute for close curly bracket syntax.
     */
    public static final String ELSE = " else ";

    /**
     * From string parameter name.
     */
    public static final String FROM_STRING_PARAM_NAME = "valInString";

    /**
     * Static attribute for close parenthesis syntax.
     */
    public static final String CLOSE_PARENTHESIS = ")";

    /**
     * Static attribute for empty parameter function call.
     */
    public static final String OPEN_CLOSE_BRACKET_STRING = "()";

    /**
     * Static attribute for empty parameter function call.
     */
    public static final String OPEN_CLOSE_DIAMOND_STRING = "<>";

    /**
     * Static attribute for open curly bracket syntax.
     */
    public static final String OPEN_CURLY_BRACKET = "{";

    /**
     * Static attribute for close curly bracket syntax.
     */
    public static final String CLOSE_CURLY_BRACKET = "}";

    /**
     * Static attribute for square brackets syntax.
     */
    public static final String SQUARE_BRACKETS = "[]";

    /**
     * Static attribute for getter method prefix.
     */
    public static final String GET_METHOD_PREFIX = "get";

    /**
     * Static attribute for setter method prefix.
     */
    public static final String SET_METHOD_PREFIX = "set";

    /**
     * Static attribute for op param.
     */
    public static final String OP_PARAM = "OpParam";

    /**
     * Static attribute for isEmpty.
     */
    public static final String IS_EMPTY = "isEmpty()";
    /**
     * Static attribute for exception string in bits enum class.
     */
    public static final String EXCEPTION_STRING = "IllegalArgumentException" +
            "(\"not a valid input element\");\n";

    /**
     * Static attribute for priority queue.
     */
    public static final String PRIORITY_QUEUE = "java.util.PriorityQueue<>()";

    /**
     * Static attribute for linked hash set.
     */
    public static final String LINKED_HASH_SET = "java.util.LinkedHashSet<>()";

    /**
     * Static attribute for priority queue.
     */
    public static final String LINKED_HASH_MAP = "java.util.LinkedHashMap<>()";

    /**
     * Static attribute for "throw new ".
     */
    public static final String THROW_NEW = "throw new ";

    /**
     * Static attribute for is isLeafValueSet method prefix.
     */
    public static final String VALUE_LEAF_SET = "isLeafValueSet";

    /**
     * Static attribute for is valueLeafFlags method prefix.
     */
    public static final String VALUE_LEAF = "valueLeafFlags";

    /**
     * Static attribute for is LeafIdentifier enum prefix.
     */
    public static final String LEAF_IDENTIFIER = "LeafIdentifier";

    public static final String MODEL_LEAF_IDENTIFIER = "org.onosproject.yang" +
            ".model.LeafIdentifier";

    /**
     * Static attribute for is leaf.
     */
    public static final String LEAF = "leaf";

    /**
     * Static attribute for four space indentation.
     */
    public static final String FOUR_SPACE_INDENTATION = "    ";

    /**
     * Static attribute for not syntax.
     */
    public static final String NOT = "!";

    /**
     * Static attribute for try syntax.
     */
    public static final String TRY = "try";

    /**
     * Static attribute for catch syntax.
     */
    public static final String CATCH = "catch";

    /**
     * Static attribute for eight space indentation.
     */
    public static final String EIGHT_SPACE_INDENTATION =
            FOUR_SPACE_INDENTATION + FOUR_SPACE_INDENTATION;

    /**
     * Static attribute for twelve space indentation.
     */
    public static final String TWELVE_SPACE_INDENTATION =
            EIGHT_SPACE_INDENTATION + FOUR_SPACE_INDENTATION;

    /**
     * Static attribute for sixteen space indentation.
     */
    public static final String SIXTEEN_SPACE_INDENTATION =
            TWELVE_SPACE_INDENTATION + FOUR_SPACE_INDENTATION;

    /**
     * Static attribute for twenty space indentation.
     */
    public static final String TWENTY_SPACE_INDENTATION =
            SIXTEEN_SPACE_INDENTATION + FOUR_SPACE_INDENTATION;

    /**
     * Static attribute for twenty four space indentation.
     */
    public static final String TWENTY_FOUR_SPACE_INDENTATION =
            TWENTY_SPACE_INDENTATION + FOUR_SPACE_INDENTATION;

    /**
     * Static attribute for twenty eight space indentation.
     */
    public static final String TWENTY_EIGHT_SPACE_INDENTATION =
            TWENTY_FOUR_SPACE_INDENTATION + FOUR_SPACE_INDENTATION;

    /**
     * Static attribute for thirty two space indentation.
     */
    public static final String THIRTY_TWO_SPACE_INDENTATION =
            TWENTY_EIGHT_SPACE_INDENTATION + FOUR_SPACE_INDENTATION;

    /**
     * Static attribute for generated code path.
     */
    public static final String YANG_GEN_DIR = "src/main/java/";

    /**
     * Static attribute for base package.
     */
    public static final String DEFAULT_BASE_PKG = "org.onosproject.yang.gen";

    /**
     * Static attribute for YANG date prefix.
     */
    public static final String REVISION_PREFIX = "rev";

    /**
     * Static attribute for YANG automatic prefix for identifiers with keywords
     * and beginning with digits.
     */
    public static final String YANG_AUTO_PREFIX = "yangAutoPrefix";

    /**
     * Static attribute for YANG version prefix.
     */
    public static final String VERSION_PREFIX = "v";

    /**
     * Static attribute for private modifier.
     */
    public static final String PRIVATE = "private";

    /**
     * Static attribute for public modifier.
     */
    public static final String PUBLIC = "public";

    /**
     * Static attribute for abstract modifier.
     */
    public static final String ABSTRACT = "abstract";

    /**
     * Static attribute for protected modifier.
     */
    public static final String PROTECTED = "protected";

    /**
     * Void java type.
     */
    public static final String VOID = "void";

    /**
     * String built in java type.
     */
    public static final String STRING_DATA_TYPE = "String";

    /**
     * String built in java type.
     */
    public static final String STRING_BUILDER = "StringBuilder";
    /**
     * Java.lang.* packages.
     */
    public static final String JAVA_LANG = "java.lang";

    /**
     * Java.math.* packages.
     */
    public static final String JAVA_MATH = "java.math";

    /**
     * Boolean built in java type.
     */
    public static final String BOOLEAN_DATA_TYPE = "boolean";

    /**
     * BigInteger built in java type.
     */
    public static final String BIG_INTEGER = "BigInteger";

    /**
     * BigDecimal built in java type.
     */
    public static final String BIG_DECIMAL = "BigDecimal";

    /**
     * BitSet built in java type.
     */
    public static final String BIT_SET = "BitSet";

    /**
     * Byte java built in type.
     */
    public static final String BYTE = "byte";

    /**
     * Short java built in type.
     */
    public static final String SHORT = "short";

    /**
     * Int java built in type.
     */
    public static final String INT = "int";

    /**
     * Long java built in type.
     */
    public static final String LONG = "long";

    /**
     * Double java built in type.
     */
    public static final String DOUBLE = "double";

    /**
     * Boolean built in java wrapper type.
     */
    public static final String BOOLEAN_WRAPPER = "Boolean";

    /**
     * Byte java built in wrapper type.
     */
    public static final String BYTE_WRAPPER = "Byte";

    /**
     * Short java built in wrapper type.
     */
    public static final String SHORT_WRAPPER = "Short";

    /**
     * Integer java built in wrapper type.
     */
    public static final String INTEGER_WRAPPER = "Integer";

    /**
     * Long java built in wrapper type.
     */
    public static final String LONG_WRAPPER = "Long";

    /**
     * Static variable for question mark.
     */
    public static final String QUESTION_MARK = "?";

    /**
     * Static variable for forType string.
     */
    public static final String FOR_TYPE_STRING = " for type ";

    /**
     * List of keywords in java, this is used for checking if the input does not
     * contain these keywords.
     */
    public static final Set<String> JAVA_KEY_WORDS =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                    "abstract", "assert", "boolean", "break", "byte",
                    "case", "catch", "char", "class", "const", "continue",
                    "default", "do", "double", "else", "extends", "false",
                    "final", "finally", "float", "for", "goto", "if",
                    "implements", "import", "instanceof", "enum", "int",
                    "interface", "long", "native", "new", "null",
                    "package", "private", "protected", "public", "return",
                    "short", "static", "strictfp", "super", "switch",
                    "synchronized", "this", "throw", "throws", "transient",
                    "true", "try", "void", "volatile", "while", "override",
                    "list", // Not a Java keyword
                    "map", // Not a Java keyword
                    "arrayList", // Not a Java keyword
                    "hashMap", // Not a Java keyword
                    "linkedList", // Not a Java keyword
                    "notify", // method on Object
                    "notifyAll", // method on Object
                    "wait", // method on Object
                    "getClass", // method on Object
                    "hashCode", // method on Object
                    "equals", // method on Object
                    "toString", // method on Object
                    "clone", // method on Object
                    "finalize", // method on Object
                    "Method", // Not a Java keyword
                    "collections") // Not a Java keyword
            ));

    /**
     * Static attribute for regex for all the special characters.
     */
    public static final String REGEX_WITH_ALL_SPECIAL_CHAR = "[\\p{Punct}\\s]+";

    /**
     * Static attribute for regex for three special characters used in
     * identifier.
     */
    public static final String REGEX_FOR_IDENTIFIER_SPECIAL_CHAR = "[. _ -]+";

    /**
     * Static attribute for regex for period.
     */
    public static final String REGEX_FOR_PERIOD = "[.]";

    /**
     * Static attribute for regex for underscore.
     */
    public static final String REGEX_FOR_UNDERSCORE = "[_]";

    /**
     * Static attribute for regex for hyphen.
     */
    public static final String REGEX_FOR_HYPHEN = "[-]";

    /**
     * Static attribute for regex for digits.
     */
    public static final String REGEX_FOR_FIRST_DIGIT = "\\d.*";

    /**
     * Static attribute for regex with digits.
     */
    public static final String REGEX_WITH_DIGITS = "(?=\\d+)";

    /**
     * Static attribute for regex for single letter.
     */
    public static final String REGEX_FOR_SINGLE_LETTER = "[a-zA-Z]";

    /**
     * Static attribute for regex for digits with single letter.
     */
    public static final String REGEX_FOR_DIGITS_WITH_SINGLE_LETTER =
            "[0-9]+[a-zA-Z]";

    /**
     * Static attribute for regex with uppercase.
     */
    public static final String REGEX_WITH_UPPERCASE = "(?=\\p{Upper})";

    /**
     * Static attribute for regex for single capital case letter.
     */
    public static final String REGEX_WITH_SINGLE_CAPITAL_CASE = "[A-Z]";

    /**
     * Static attribute for regex for capital case letter with any number of
     * digits and small case letters.
     */
    public static final String
            REGEX_WITH_SINGLE_CAPITAL_CASE_AND_DIGITS_SMALL_CASES =
            "[A-Z][0-9a-z]+";

    /**
     * Static attribute for regex for any string ending with service.
     */
    public static final String REGEX_FOR_ANY_STRING_ENDING_WITH_SERVICE =
            ".+Service";

    /**
     * Static attribute for class syntax.
     */
    public static final String CLASS = "class";

    /**
     * Static attribute for service syntax.
     */
    public static final String SERVICE = "Service";

    /**
     * Static attribute for interface syntax.
     */
    public static final String INTERFACE = "interface";

    /**
     * Static attribute for enum syntax.
     */
    public static final String ENUM = "enum";

    /**
     * Static attribute for type syntax.
     */
    public static final String TYPE = "Type";

    /**
     * Static attribute for static syntax.
     */
    public static final String STATIC = "static";

    /**
     * Static attribute for final syntax.
     */
    public static final String FINAL = "final";

    /**
     * Static attribute for package syntax.
     */
    public static final String PACKAGE = "package";

    /**
     * Static attribute for import syntax.
     */
    public static final String IMPORT = "import ";

    /**
     * Static attribute for null syntax.
     */
    public static final String NULL = "null";

    /**
     * Static attribute for return syntax.
     */
    public static final String RETURN = "return";

    /**
     * Static attribute for java new syntax.
     */
    public static final String NEW = "new";

    /**
     * Static attribute for java new syntax.
     */
    public static final String TO_STRING_METHOD = "toString";

    /**
     * Static attribute for this syntax.
     */
    public static final String THIS = "this";

    /**
     * Static attribute for implements syntax.
     */
    public static final String IMPLEMENTS = "implements";

    /**
     * Static attribute for extends syntax.
     */
    public static final String EXTEND = "extends";

    /**
     * Static attribute for service interface suffix syntax.
     */
    public static final String SERVICE_METHOD_STRING = "Service";

    /**
     * For event file generation.
     */
    public static final String EVENT_STRING = "Event";

    /**
     * For event listener file generation.
     */
    public static final String EVENT_LISTENER_STRING = "EventListener";

    /**
     * For event subject file generation.
     */
    public static final String EVENT_SUBJECT_NAME_SUFFIX = "EventSubject";

    /**
     * Static attribute for object.
     */
    public static final String OBJECT = "Object";

    /**
     * Static attribute for app instance.
     */
    public static final String APP_INSTANCE = "appInstance";

    /**
     * Static attribute for instance.
     */
    public static final String INSTANCE = "instance";

    /**
     * Static attribute for override annotation.
     */
    public static final String OVERRIDE = "@Override";

    /**
     * Static attribute for collections.
     */
    public static final String COLLECTION_IMPORTS = "java.util";

    /**
     * Static attribute for reflect.
     */
    public static final String REFLECT_IMPORTS = "java.lang.reflect";

    /**
     * Static attribute for map.
     */
    public static final String MAP = "Map";

    /**
     * Static attribute for hash map.
     */
    public static final String HASH_MAP = "HashMap";

    /**
     * Static attribute for new string joiner object.
     */
    public static final String NEW_STRING_JOINER_OBJECT =
            "new StringJoiner(\", \", getClass().getSimpleName() +\"{\", \"}\")";

    /**
     * Static attribute for java utilities import package.
     */
    public static final String JAVA_UTIL_PKG = "java.util";

    /**
     * Static attribute for model object import package.
     */
    public static final String MODEL_OBJECT_PKG = "org.onosproject.yang.model";

    /**
     * Static attribute for java utilities import package.
     */
    public static final String JAVA_UTIL_REGEX_PKG = "java.util.regex";

    /**
     * Static attribute for java utilities import package.
     */
    public static final String SET_VALUE_PARA = "setValue";

    /**
     * Static attribute for Method.
     */
    public static final String METHOD = "Method";

    /**
     * Static attribute for bitset.
     */
    public static final String BITSET = "BitSet";

    /**
     * Static attribute for java utilities objects import class.
     */
    public static final String JAVA_UTIL_OBJECTS_IMPORT_CLASS = "Objects";

    /**
     * Static attribute for augmented.
     */
    public static final String AUGMENTED = "Augmented";

    /**
     * Static attribute for list.
     */
    public static final String LIST = "List";

    /**
     * Static attribute for queue.
     */
    public static final String QUEUE = "Queue";

    /**
     * Static attribute for set.
     */
    public static final String SET = "Set";

    /**
     * Comment to be added for auto generated impl methods.
     */
    public static final String YANG_UTILS_TODO =
            "//TODO: YANG utils generated code";

    /**
     * Static attribute for AbstractEvent.
     */
    public static final String ABSTRACT_EVENT = "AbstractEvent";

    /**
     * Static attribute for EventListener.
     */
    public static final String EVENT_LISTENER = "EventListener";

    /**
     * Static attribute for or operator.
     */
    public static final String OR_OPERATION = "||";

    /**
     * Static attribute for or operator.
     */
    public static final String AND_OPERATION = "&&";

    /**
     * Static attribute for YANG file error.
     */
    public static final String YANG_FILE_ERROR = "YANG file error : ";

    /**
     * Static attribute for unsupported error information.
     */
    public static final String UNSUPPORTED_YANG_CONSTRUCT =
            " is not supported.";

    /**
     * Static attribute for "is invalid" information.
     */
    public static final String IS_INVALID = " is invalid.";

    /**
     * Static attribute for data model tree error information.
     */
    public static final String INVALID_TREE = "Internal datamodel error: " +
            "Datamodel tree is not correct";

    /**
     * Static attribute for currently unsupported error information.
     */
    public static final String CURRENTLY_UNSUPPORTED =
            " is not supported in current version, please check wiki" +
                    " for YANG utils road map.";

    /**
     * Static attribute for leaf ref target node error information.
     */
    public static final String LEAFREF_ERROR = "YANG file error: The target" +
            " node, in the leafref path ";

    /**
     * Static attribute for leaf holder error information.
     */
    public static final String LEAF_HOLDER_ERROR = "Referred node should be of" +
            " type leaves holder in ";

    /**
     * Static attribute for invalid resolve entity error information.
     */
    public static final String INVALID_RESOLVED_ENTITY = "Data Model " +
            "Exception: Entity to resolved is other than type/uses";

    /**
     * Static attribute for invalid resolve entity error information.
     */
    public static final String INVALID_ENTITY = "Data Model Exception: Entity " +
            "to resolved is other than identityref";

    /**
     * Static attribute for invalid state error information.
     */
    public static final String INVALID_LINKER_STATE = "Data Model Exception: " +
            "Unsupported, linker state";

    /**
     * Static attribute for leaf ref resolve entity error information.
     */
    public static final String FAILED_TO_FIND_LEAD_INFO_HOLDER = "YANG file " +
            "error: Unable to find base leaf/leaf-list for given leafref path ";

    /**
     * Static attribute for compiler annotation resolve entity error
     * information.
     */
    public static final String FAILED_TO_FIND_ANNOTATION = "Failed to link " +
            "compiler annotation ";

    /**
     * Static attribute for compiler annotation resolve entity error
     * information.
     */
    public static final String FAILED_TO_FIND_DEVIATION = "Failed to link " +
            "deviation ";

    /**
     * Static attribute for failed to link entity error information.
     */
    public static final String FAILED_TO_LINK = "Failed to link ";

    /**
     * Static attribute for un-resolve entity error information.
     */
    public static final String UNRESOLVABLE = "Data Model Exception: Entity " +
            "to resolved is not Resolvable";

    /**
     * Static attribute for invalid resolve entity error information.
     */
    public static final String LINKER_ERROR = "Data Model Exception: Entity" +
            " to resolved is other than type/uses/if-feature/leafref/base/identityref";

    /**
     * Static attribute for invalid resolve entity error information.
     */
    public static final String INVALID_TARGET = "Invalid target node type ";
    /**
     * Static attribute for typedef linker error information.
     */
    public static final String TYPEDEF_LINKER_ERROR =
            "YANG file error: Unable to find base typedef for given type";

    /**
     * Static attribute for grouping linker error information.
     */
    public static final String GROUPING_LINKER_ERROR =
            "YANG file error: Unable to find base grouping for given uses";

    /**
     * Static attribute for if-feature linker error information.
     */
    public static final String FEATURE_LINKER_ERROR =
            "YANG file error: Unable to find feature for given if-feature";

    /**
     * Static attribute for leafref linker error information.
     */
    public static final String LEAFREF_LINKER_ERROR =
            "YANG file error: Unable to find base leaf/leaf-list for given " +
                    "leafref";

    /**
     * Static attribute for base linker error information.
     */
    public static final String BASE_LINKER_ERROR =
            "YANG file error: Unable to find base identity for given base";

    /**
     * Static attribute for identityref linker error information.
     */
    public static final String IDENTITYREF_LINKER_ERROR =
            "YANG file error: Unable to find base identity for given base";

    /**
     * Static attribute for jar.
     */
    public static final String JAR = "jar";

    /**
     * Static attribute for for.
     */
    public static final String FOR = "for";

    /**
     * Static attribute for InvocationTargetException.
     */
    public static final String INVOCATION_TARGET_EXCEPTION =
            "InvocationTargetException";
    /**
     * Static attribute for arrayList.
     */
    public static final String ARRAY_LIST_INIT = "ArrayList<>()";

    /**
     * Static attribute for arrayList import.
     */
    public static final String ARRAY_LIST_IMPORT =
            IMPORT + COLLECTION_IMPORTS + ".ArrayList;\n";

    /**
     * Static attribute for unused keyword.
     */
    public static final String UNUSED = "UNUSED";

    /**
     * Static attribute for 1 keyword.
     */
    public static final String ONE = "1";

    /**
     * Static attribute for 1.1 keyword.
     */
    public static final String ONE_DOT_ONE = "1.1";

    /**
     * Static attribute for YANG node operation type attribute.
     */
    public static final String OPERATION_TYPE_ATTRIBUTE =
            "OpType";

    /**
     * Static attribute for input keyword to be suffixed with rpc name.
     */
    public static final String INPUT_KEYWORD = "_input";

    /**
     * Static attribute for output keyword to be suffixed with rpc name.
     */
    public static final String OUTPUT_KEYWORD = "_output";

    /**
     * Static attribute for event class.
     */
    public static final String EVENT_CLASS = "event class";

    /**
     * Static attribute for typedef class.
     */
    public static final String TYPEDEF_CLASS = "typedef class";

    /**
     * Static attribute for impl class.
     */
    public static final String IMPL_CLASS = "impl class";

    /**
     * Static attribute for union class.
     */
    public static final String UNION_CLASS = "union class";

    /**
     * Static attribute for enum class.
     */
    public static final String ENUM_CLASS = "enum class";

    /**
     * Static attribute for rpc class.
     */
    public static final String RPC_CLASS = "rpc class";

    /**
     * Static attribute for bits.
     */
    public static final String BITS = "bits";

    /**
     * Static attribute for YANG.
     */
    public static final String YANG = "yang";

    /**
     * Static attribute for error msg.
     */
    public static final String ERROR_MSG_FOR_GEN_CODE = "please check whether " +
            "multiple yang files has same module/submodule" +
            " \"name\" and \"namespace\" or You may have generated code of" +
            " previous build present in your directory.";

    /**
     * Static attribute for error msg.
     */
    public static final String ERROR_MSG_JAVA_IDENTITY = "Expected java " +
            "identity instance node ";

    /**
     * Static attribute for error msg.
     */
    public static final String ERROR_MSG_FOR_AUGMENT_LINKING = "Augment " +
            "linking does not support linking when path contains " +
            "notification/grouping for path: ";

    /**
     * Static attribute for error msg.
     */
    public static final String VERSION_ERROR = "Onos-yang-tools " +
            "does not support maven version below \"3.3.9\" , your current " +
            "version is ";

    /**
     * Static attribute for in.
     */
    public static final String IN = " in ";

    /**
     * Static attribute for at.
     */
    public static final String AT = " at ";

    //File type extension for java classes.
    public static final String JAVA_FILE_EXTENSION = ".java";

    /**
     * Static param for typedef.
     */
    public static final String HOLDER_TYPE_DEF = "typedef";

    /**
     * Static param for last.
     */
    public static final String LAST = "last";

    /**
     * Static param for model object.
     */
    public static final String MODEL_OBJECT = "InnerModelObject";

    /**
     * Static param for add augmentation.
     */
    public static final String ADD_AUGMENTATION = "addAugmentation";

    /**
     * Static param for remove augmentation.
     */
    public static final String REMOVE_AUGMENTATION = "removeAugmentation";

    /**
     * Static param for augmentations.
     */
    public static final String AUGMENTATIONS = "augmentations";

    /**
     * Static param for augmentation.
     */
    public static final String AUGMENTATION = "augmentation";

    /**
     * Static param for variable c.
     */
    public static final String VARIABLE_C = "c";

    /**
     * Static param for left angular bracket.
     */
    public static final String LEFT_ANGULAR_BRACKET = "<";

    /**
     * Static param for right angular brace.
     */
    public static final String RIGHT_ANGULAR_BRACKET = ">";

    /**
     * Static param for template t.
     */
    public static final String TEMPLATE_T = "T";

    /**
     * Static param for class.
     */
    public static final String CAMEL_CLASS = "Class";

    /**
     * Default meta data path.
     */
    public static final String DEFAULT_JAR_RES_PATH = SLASH + TEMP + SLASH +
            YANG_RESOURCES + SLASH;

    /**
     * Meta data file name.
     */
    public static final String YANG_META_DATA = "YangMetaData.ser";

    /**
     * Static attribute for concurrent import package.
     */
    public static final String JAVA_UTIL_CONCURRENT_PKG = "java.util" +
            ".concurrent";

    /**
     * Static attribute for model import package.
     */
    public static final String MODEL_PKG = "org.onosproject.yang.model";

    /**
     * Static attribute for RPC execution status import package.
     */
    public static final String RPC_OUTPUT_STATUS_PKG = "org.onosproject.yang" +
            ".model.RpcOutput.Status";

    /**
     * Static param for model converter.
     */
    public static final String MODEL_CONVERTER = "ModelConverter";

    /**
     * Static param for resource id.
     */
    public static final String RESOURCE_ID = "ResourceId";

    /**
     * Static param for YANG RPC service.
     */
    public static final String YANG_RPC_SERVICE = "YangRpcService";

    /**
     * Static param for RPC handler.
     */
    public static final String RPC_HANDLER = "RpcHandler";

    /**
     * Static param for RPC command.
     */
    public static final String RPC_COMMAND = "RpcCommand";

    /**
     * Static param for RPC input.
     */
    public static final String RPC_INPUT = "RpcInput";

    /**
     * Static param for RPC output.
     */
    public static final String RPC_OUTPUT = "RpcOutput";

    /**
     * Static param for RPC success.
     */
    public static final String RPC_SUCCESS = "RPC_SUCCESS";

    /**
     * Static param for RPC executer.
     */
    public static final String RPC_EXECUTER = "RpcExecuter";

    /**
     * Static attribute for extends.
     */
    public static final String EXTENDS = "extends";

    /**
     * Static attribute for super.
     */
    public static final String SUPER = "super";

    /**
     * Static attribute for linked list.
     */
    public static final String LINKED_LIST = "LinkedList";

    /**
     * Static attribute for data node.
     */
    public static final String DATA_NODE = "DataNode";

    /**
     * Static attribute for executor service.
     */
    public static final String EXECUTOR_SERVICE = "ExecutorService";

    /**
     * Static attribute for executors.
     */
    public static final String EXECUTORS = "Executors";

    /**
     * Static attribute for default RPC handler.
     */
    public static final String DEFAULT_RPC_HANDLER = "DefaultRpcHandler";

    /**
     * Static attribute for RPC extended command.
     */
    public static final String RPC_EXTENDED_COMMAND = "RpcExtendedCommand";

    /**
     * Static attribute for register RPC.
     */
    public static final String REGISTER_RPC = "RegisterRpc";

    /**
     * JavaDocs's description for RPC handler.
     */
    public static final String JAVA_DOC_RPC_HANDLER = "RPC handler";

    /**
     * JavaDocs's description for register RPC.
     */
    public static final String JAVA_DOC_REGISTER_RPC = "register RPC";

    /**
     * JavaDocs's description for RPC executer.
     */
    public static final String JAVA_DOC_RPC_EXECUTER = "Runnable capable of " +
            "invoking the appropriate RPC command's execute method";

    public static final String JAVA_DOC_RPC_EXTENDED_CMD = "* Abstract " +
            "implementation of an RPC extended command";

    public static final String COMMAND = "Command";

    public static final String REGISTER_RPC_JAVADOC = "    * Registers RPC " +
            "handler with dynamic config service";

    public static final String CREATE_RPC_CMD_JAVADOC = "    * Creates RPC " +
            "command for all the RPC";

    public static final String RPC_EXECUTER_JAVADOC = "    * Runnable " +
            "capable of invoking the appropriate RPC command's execute method";

    public static final String RUNNABLE = "Runnable";
    public static final String RUN = "run";
    public static final String BUILDER = "Builder";
    public static final String BUILD = "build";
    public static final String ADD_BRANCH_POINT_SCHEMA = "addBranchPointSchema";
    public static final String CREATE_MODEL = "createModel";
    public static final String CREATE_DATA_NODE = "createDataNode";
    public static final String MULTI_INSTANCE_OBJECT = "MultiInstanceObject";
    public static final String KEY_INFO = "KeyInfo";
    public static final String RESOURCE_DATA = "ResourceData";
    public static final String DEFAULT_RESOURCE_DATA = "DefaultResourceData";
    public static final String MODEL_OBJECT_DATA = "ModelObjectData";
    public static final String DEFAULT_MODEL_OBJECT_DATA =
            "DefaultModelObjectData";
    public static final String STRING_JOINER_CLASS = "StringJoiner";
    public static final String AUGMENTABLE = "Augmentable";
    public static final String RPC_SERVICE = "RpcService";
    public static final String ASTERISK = "asterisk";

    public static final String IDENTITY = "_identity";
    public static final String TYPEDEF = "_typedef";

    public static final String ANYDATA = "Anydata";

    // Regex for model id validation
    public static final String REGEX = "[A-Za-z0-9_\\-.@]+";

    // No instantiation.
    private UtilConstants() {
    }

    /**
     * Represents operation type.
     */
    public enum Operation {
        /**
         * Represents add operation.
         */
        ADD,

        /**
         * Represents remove operation.
         */
        REMOVE
    }
}
