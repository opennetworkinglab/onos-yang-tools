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

package org.onosproject.yang.compiler.utils.io.impl;

import org.apache.commons.io.FileUtils;
import org.onosproject.yang.compiler.utils.io.YangToJavaNamingConflictUtil;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static org.onosproject.yang.compiler.utils.UtilConstants.CLOSE_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.HYPHEN;
import static org.onosproject.yang.compiler.utils.UtilConstants.JAVA_KEY_WORDS;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.ONE;
import static org.onosproject.yang.compiler.utils.UtilConstants.OPEN_PARENTHESIS;
import static org.onosproject.yang.compiler.utils.UtilConstants.ORG;
import static org.onosproject.yang.compiler.utils.UtilConstants.PACKAGE;
import static org.onosproject.yang.compiler.utils.UtilConstants.PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_FOR_DIGITS_WITH_SINGLE_LETTER;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_FOR_FIRST_DIGIT;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_FOR_HYPHEN;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_FOR_IDENTIFIER_SPECIAL_CHAR;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_FOR_PERIOD;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_FOR_SINGLE_LETTER;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_FOR_UNDERSCORE;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_WITH_ALL_SPECIAL_CHAR;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_WITH_DIGITS;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_WITH_SINGLE_CAPITAL_CASE;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_WITH_SINGLE_CAPITAL_CASE_AND_DIGITS_SMALL_CASES;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX_WITH_UPPERCASE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SEMI_COLON;
import static org.onosproject.yang.compiler.utils.UtilConstants.SLASH;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;
import static org.onosproject.yang.compiler.utils.UtilConstants.TEMP;
import static org.onosproject.yang.compiler.utils.UtilConstants.UNDER_SCORE;
import static org.onosproject.yang.compiler.utils.UtilConstants.UNUSED;
import static org.onosproject.yang.compiler.utils.UtilConstants.YANG_AUTO_PREFIX;
import static org.onosproject.yang.compiler.utils.io.impl.CopyrightHeader.parseCopyrightHeader;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.JavaDocType.PACKAGE_INFO;
import static org.onosproject.yang.compiler.utils.io.impl.JavaDocGen.getJavaDoc;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents common utility functionalities for code generation.
 */
public final class YangIoUtils {

    private static final int LINE_SIZE = 118;
    private static final int SUB_LINE_SIZE = 116;
    private static final int SUB_SIZE = 60;
    private static final Logger log = getLogger(YangIoUtils.class);

    /**
     * Creates an instance of YANG io utils.
     */
    private YangIoUtils() {
    }

    /**
     * Creates the directory structure.
     *
     * @param path directory path
     * @return directory structure
     * @throws IOException when fails to do IO operations
     */
    public static File createDirectories(String path)
            throws IOException {
        File generatedDir = new File(path);
        if (!generatedDir.exists()) {
            boolean isGenerated = generatedDir.mkdirs();
            if (!isGenerated) {
                throw new IOException("failed to generated directory " + path);
            }
        }
        return generatedDir;
    }

    /**
     * Adds package info file for the created directory.
     *
     * @param path        directory path
     * @param classInfo   class info for the package
     * @param pack        package of the directory
     * @param isChildNode is it a child node
     * @throws IOException when fails to create package info file
     */
    public static void addPackageInfo(File path, String classInfo, String pack,
                                      boolean isChildNode) throws IOException {

        pack = parsePkg(pack);
        try {

            File packageInfo = new File(path + SLASH + "package-info.java");
            if (!packageInfo.exists()) {
                boolean isGenerated = packageInfo.createNewFile();
                if (!isGenerated) {
                    throw new IOException("failed to generated package-info " +
                                                  path);
                }
            }
            FileWriter fileWriter = new FileWriter(packageInfo);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(parseCopyrightHeader());
            //TODO: get the compiler annotations and pass the info
            bufferedWriter.write(getJavaDoc(PACKAGE_INFO, classInfo, isChildNode,
                                            null));
            String pkg = PACKAGE + SPACE + pack + SEMI_COLON;
            bufferedWriter.write(pkg);
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            throw new IOException("Exception occurred while creating package info" +
                                          " file.", e);
        }
    }

    /**
     * Parses package and returns updated package.
     *
     * @param pack package needs to be updated
     * @return updated package
     */
    public static String parsePkg(String pack) {

        if (pack.contains(ORG)) {
            String[] strArray = pack.split(ORG);
            if (strArray.length >= 3) {
                for (int i = 1; i < strArray.length; i++) {
                    if (i == 1) {
                        pack = ORG + strArray[1];
                    } else {
                        pack = pack + ORG + strArray[i];
                    }
                }
            } else {
                pack = ORG + strArray[1];
            }
        }

        return pack;
    }

    /**
     * Cleans the generated directory if already exist in source folder.
     *
     * @param dir generated directory in previous build
     * @throws IOException when failed to delete directory
     */
    public static void deleteDirectory(String dir)
            throws IOException {
        File generatedDirectory = new File(dir);
        if (generatedDirectory.exists()) {
            try {
                FileUtils.deleteDirectory(generatedDirectory);
            } catch (IOException e) {
                throw new IOException(
                        "Failed to delete the generated files in " +
                                generatedDirectory + " directory", e);
            }
        }
    }

    /**
     * Searches and deletes generated temporary directories.
     *
     * @param root root directory
     * @throws IOException when fails to do IO operations.
     */
    public static void searchAndDeleteTempDir(String root)
            throws IOException {
        List<File> store = new LinkedList<>();
        Stack<String> stack = new Stack<>();
        stack.push(root);

        while (!stack.empty()) {
            root = stack.pop();
            File file = new File(root);
            File[] fileList = file.listFiles();
            if (fileList == null || fileList.length == 0) {
                continue;
            }
            for (File current : fileList) {
                if (current.isDirectory()) {
                    stack.push(current.toString());
                    if (current.getName().endsWith(HYPHEN + TEMP)) {
                        store.add(current);
                    }
                }
            }
        }

        for (File dir : store) {
            FileUtils.deleteDirectory(dir);
        }
    }

    /**
     * Removes extra char from the string.
     *
     * @param valueString   string to be trimmed
     * @param removalString extra chars
     * @return new string
     */
    public static String trimAtLast(String valueString, String...
            removalString) {
        StringBuilder stringBuilder = new StringBuilder(valueString);
        String midString;
        int index;
        for (String remove : removalString) {
            midString = stringBuilder.toString();
            index = midString.lastIndexOf(remove);
            if (index != -1) {
                stringBuilder.deleteCharAt(index);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Replaces the last occurrence of a string with a given string.
     *
     * @param valueString     string under operation
     * @param removalString   string to be replaced
     * @param replacingString string with which replacement is to be done
     * @return new string
     */
    public static String replaceLast(String valueString, String removalString,
                                     String replacingString) {
        StringBuilder stringBuilder = new StringBuilder(valueString);
        int index = valueString.lastIndexOf(removalString);
        if (index != -1) {
            stringBuilder.replace(index, index + 1, replacingString);
        } else {
            stringBuilder.append(NEW_LINE + EIGHT_SPACE_INDENTATION + UNUSED +
                                         OPEN_PARENTHESIS + ONE +
                                         CLOSE_PARENTHESIS + SEMI_COLON);
        }
        return stringBuilder.toString();

        // TODO remove generation of ENUM if there is no leaf node.
    }


    /**
     * Returns the directory path of the package in canonical form.
     *
     * @param baseCodeGenPath base path where the generated files needs to be
     *                        put
     * @param pathOfJavaPkg   java package of the file being generated
     * @return absolute path of the package in canonical form
     */
    public static String getDirectory(String baseCodeGenPath, String pathOfJavaPkg) {

        if (pathOfJavaPkg.charAt(pathOfJavaPkg.length() - 1) == File.separatorChar) {
            pathOfJavaPkg = trimAtLast(pathOfJavaPkg, SLASH);
        }
        String[] strArray = pathOfJavaPkg.split(SLASH);
        if (strArray[0].equals(EMPTY_STRING)) {
            return pathOfJavaPkg;
        } else {
            return baseCodeGenPath + SLASH + pathOfJavaPkg;
        }
    }

    /**
     * Returns the absolute path of the package in canonical form.
     *
     * @param baseCodeGenPath base path where the generated files needs to be
     *                        put
     * @param pathOfJavaPkg   java package of the file being generated
     * @return absolute path of the package in canonical form
     */
    public static String getAbsolutePackagePath(String baseCodeGenPath,
                                                String pathOfJavaPkg) {
        return baseCodeGenPath + pathOfJavaPkg;
    }

    /**
     * Merges the temp java files to main java files.
     *
     * @param appendFile temp file
     * @param srcFile    main file
     * @throws IOException when fails to append contents
     */
    public static void mergeJavaFiles(File appendFile, File srcFile)
            throws IOException {
        try {
            FileSystemUtil.appendFileContents(appendFile, srcFile);
        } catch (IOException e) {
            throw new IOException("Failed to merge " + appendFile + " in " +
                                          srcFile);
        }
    }

    /**
     * Inserts data in the generated file.
     *
     * @param file file in which need to be inserted
     * @param data data which need to be inserted
     * @throws IOException when fails to insert into file
     */
    public static void insertDataIntoJavaFile(File file, String data)
            throws IOException {
        try {
            FileSystemUtil.updateFileHandle(file, data, false);
        } catch (IOException e) {
            throw new IOException("Failed to insert in " + file + "file", e);
        }
    }

    /**
     * Formats the generated file.
     *
     * @param dataFile file in which need to verify all lines.
     * @return updated file
     * @throws IOException when fails to do IO operations.
     */
    public static File formatFile(File dataFile) throws IOException {

        // take default Eclipse formatting options.
        // FIXME: signature generated by eclipse jdt pkg need to be removed
        /*Map options = getEclipseDefaultSettings();

        // initialize the compiler settings to be able to format 1.8 code.
        options.put(COMPILER_COMPLIANCE, VERSION_1_8);
        options.put(COMPILER_CODEGEN_TARGET_PLATFORM, VERSION_1_8);
        options.put(COMPILER_SOURCE, VERSION_1_8);

        options.put(FORMATTER_TAB_CHAR, SPACE);

        // change the option to wrap each enum constant on a new line.
        options.put(FORMATTER_ALIGNMENT_FOR_ENUM_CONSTANTS,
                    createAlignmentValue(true, WRAP_ONE_PER_LINE,
                                         INDENT_ON_COLUMN));

        // instantiate the default code formatter with the given options.
        final CodeFormatter codeFormatter = createCodeFormatter(options);

        String source = FileUtils.readFileToString(dataFile, UTF_8);

        final TextEdit edit = codeFormatter.format(
                CodeFormatter.K_COMPILATION_UNIT, // format a compilation unit
                source, // source to format
                0, // starting position
                source.length(), // length
                0, // initial indentation
                System.getProperty("line.separator") // line separator
        );

        IDocument document = new Document(source);
        try {
            edit.apply(document);
        } catch (MalformedTreeException e) {
            log.info(" failed to format the file {} due to malformed tree.",
                     dataFile.getName());
        } catch (BadLocationException e) {
            log.info(" failed to format the file {} due to bad location.",
                     dataFile.getName());
        } catch (NullPointerException e) {
            log.info(" failed to format the file {} due to incomplete file ",
                     dataFile.getName());
        }
        FileWriter writer = new FileWriter(dataFile);
        writer.write(document.get());
        writer.close(); */
        return dataFile;
    }

    /**
     * Returns the java Package from package path.
     *
     * @param packagePath package path
     * @return java package
     */
    public static String getJavaPackageFromPackagePath(String packagePath) {
        return packagePath.replace(SLASH, PERIOD);
    }

    /**
     * Returns the directory path corresponding to java package.
     *
     * @param packagePath package path
     * @return java package
     */
    public static String getPackageDirPathFromJavaJPackage(String packagePath) {
        return packagePath.replace(PERIOD, SLASH);
    }

    /**
     * Returns the YANG identifier name as java identifier with first letter
     * in small.
     *
     * @param yangIdentifier identifier in YANG file.
     * @return corresponding java identifier
     */
    public static String getSmallCase(String yangIdentifier) {
        return yangIdentifier.substring(0, 1).toLowerCase() + yangIdentifier.substring(1);
    }

    /**
     * Returns the YANG identifier name as java identifier with first letter
     * in capital.
     *
     * @param yangIdentifier identifier in YANG file
     * @return corresponding java identifier
     */
    public static String getCapitalCase(String yangIdentifier) {
        yangIdentifier = yangIdentifier.substring(0, 1).toUpperCase() + yangIdentifier.substring(1);
        return restrictConsecutiveCapitalCase(yangIdentifier);
    }

    /**
     * Restricts consecutive capital cased string as a rule in camel case.
     *
     * @param consecCapitalCaseRemover which requires the restriction of consecutive capital case
     * @return string without consecutive capital case
     */
    private static String restrictConsecutiveCapitalCase(String consecCapitalCaseRemover) {

        for (int k = 0; k < consecCapitalCaseRemover.length(); k++) {
            if (k + 1 < consecCapitalCaseRemover.length()) {
                if (Character.isUpperCase(consecCapitalCaseRemover.charAt(k))) {
                    if (Character.isUpperCase(consecCapitalCaseRemover.charAt(k + 1))) {
                        consecCapitalCaseRemover = consecCapitalCaseRemover.substring(0, k + 1)
                                + consecCapitalCaseRemover.substring(k + 1, k + 2).toLowerCase()
                                + consecCapitalCaseRemover.substring(k + 2);
                    }
                }
            }
        }
        return consecCapitalCaseRemover;
    }

    /**
     * Adds prefix, if the string begins with digit or is a java key word.
     *
     * @param camelCasePrefix  string for adding prefix
     * @param conflictResolver object of YANG to java naming conflict util
     * @return prefixed camel case string
     */
    private static String addPrefix(String camelCasePrefix, YangToJavaNamingConflictUtil conflictResolver) {

        String prefix = getPrefixForIdentifier(conflictResolver);
        if (camelCasePrefix.matches(REGEX_FOR_FIRST_DIGIT)) {
            camelCasePrefix = prefix + camelCasePrefix;
        }
        if (JAVA_KEY_WORDS.contains(camelCasePrefix)) {
            camelCasePrefix = prefix + camelCasePrefix.substring(0, 1).toUpperCase()
                    + camelCasePrefix.substring(1);
        }
        return camelCasePrefix;
    }

    /**
     * Applies the rule that a string does not end with a capitalized letter and capitalizes
     * the letter next to a number in an array.
     *
     * @param stringArray      containing strings for camel case separation
     * @param conflictResolver object of YANG to java naming conflict util
     * @return camel case rule checked string
     */
    private static String applyCamelCaseRule(String[] stringArray,
                                             YangToJavaNamingConflictUtil conflictResolver) {

        String ruleChecker = stringArray[0].toLowerCase();
        int i;
        if (ruleChecker.matches(REGEX_FOR_FIRST_DIGIT)) {
            i = 0;
            ruleChecker = EMPTY_STRING;
        } else {
            i = 1;
        }
        for (; i < stringArray.length; i++) {
            if (i + 1 == stringArray.length) {
                if (stringArray[i].matches(REGEX_FOR_SINGLE_LETTER)
                        || stringArray[i].matches(REGEX_FOR_DIGITS_WITH_SINGLE_LETTER)) {
                    ruleChecker = ruleChecker + stringArray[i].toLowerCase();
                    break;
                }
            }
            if (stringArray[i].matches(REGEX_FOR_FIRST_DIGIT)) {
                for (int j = 0; j < stringArray[i].length(); j++) {
                    char letterCheck = stringArray[i].charAt(j);
                    if (Character.isLetter(letterCheck)) {
                        stringArray[i] = stringArray[i].substring(0, j)
                                + stringArray[i].substring(j, j + 1).toUpperCase() + stringArray[i].substring(j + 1);
                        break;
                    }
                }
                ruleChecker = ruleChecker + stringArray[i];
            } else {
                ruleChecker = ruleChecker + stringArray[i].substring(0, 1).toUpperCase() + stringArray[i].substring(1);
            }
        }
        String ruleCheckerWithPrefix = addPrefix(ruleChecker, conflictResolver);
        return restrictConsecutiveCapitalCase(ruleCheckerWithPrefix);
    }

    /**
     * Resolves the conflict when input has upper case.
     *
     * @param stringArray      containing strings for upper case conflict resolver
     * @param conflictResolver object of YANG to java naming conflict util
     * @return camel cased string
     */
    private static String upperCaseConflictResolver(String[] stringArray,
                                                    YangToJavaNamingConflictUtil conflictResolver) {

        for (int l = 0; l < stringArray.length; l++) {
            String[] upperCaseSplitArray = stringArray[l].split(REGEX_WITH_UPPERCASE);
            for (int m = 0; m < upperCaseSplitArray.length; m++) {
                if (upperCaseSplitArray[m].matches(REGEX_WITH_SINGLE_CAPITAL_CASE)) {
                    int check = m;
                    while (check + 1 < upperCaseSplitArray.length) {
                        if (upperCaseSplitArray[check + 1].matches(REGEX_WITH_SINGLE_CAPITAL_CASE)) {
                            upperCaseSplitArray[check + 1] = upperCaseSplitArray[check + 1].toLowerCase();
                            check = check + 1;
                        } else if (upperCaseSplitArray[check + 1]
                                .matches(REGEX_WITH_SINGLE_CAPITAL_CASE_AND_DIGITS_SMALL_CASES)) {
                            upperCaseSplitArray[check + 1] = upperCaseSplitArray[check + 1].toLowerCase();
                            break;
                        } else {
                            break;
                        }
                    }
                }
            }
            StringBuilder strBuilder = new StringBuilder();
            for (String element : upperCaseSplitArray) {
                strBuilder.append(element);
            }
            stringArray[l] = strBuilder.toString();
        }
        List<String> result = new ArrayList<>();
        for (String element : stringArray) {
            String[] capitalCaseSplitArray = element.split(REGEX_WITH_UPPERCASE);
            for (String letter : capitalCaseSplitArray) {
                String[] arrayForAddition = letter.split(REGEX_WITH_DIGITS);
                List<String> list = Arrays.asList(arrayForAddition);
                for (String str : list) {
                    if (str != null && !str.isEmpty()) {
                        result.add(str);
                    }
                }
            }
        }
        stringArray = result.toArray(new String[result.size()]);
        return applyCamelCaseRule(stringArray, conflictResolver);
    }

    /**
     * Returns the YANG identifier name as java identifier.
     *
     * @param yangIdentifier   identifier in YANG file
     * @param conflictResolver object of YANG to java naming conflict util
     * @return corresponding java identifier
     */
    public static String getCamelCase(String yangIdentifier, YangToJavaNamingConflictUtil conflictResolver) {

        if (conflictResolver != null) {
            String replacementForHyphen = conflictResolver.getReplacementForHyphen();
            String replacementForPeriod = conflictResolver.getReplacementForPeriod();
            String replacementForUnderscore = conflictResolver.getReplacementForUnderscore();
            if (replacementForPeriod != null) {
                yangIdentifier = yangIdentifier.replaceAll(REGEX_FOR_PERIOD,
                                                           PERIOD + replacementForPeriod.toLowerCase() + PERIOD);
            }
            if (replacementForUnderscore != null) {
                yangIdentifier = yangIdentifier.replaceAll(REGEX_FOR_UNDERSCORE,
                                                           UNDER_SCORE + replacementForUnderscore.toLowerCase() +
                                                                   UNDER_SCORE);
            }
            if (replacementForHyphen != null) {
                yangIdentifier = yangIdentifier.replaceAll(REGEX_FOR_HYPHEN,
                                                           HYPHEN + replacementForHyphen.toLowerCase() + HYPHEN);
            }
        }
        yangIdentifier = yangIdentifier.replaceAll(REGEX_FOR_IDENTIFIER_SPECIAL_CHAR, COLON);
        String[] strArray = yangIdentifier.split(COLON);
        if (strArray[0].isEmpty()) {
            List<String> stringArrangement = new ArrayList<>();
            stringArrangement.addAll(Arrays.asList(strArray).subList(1, strArray.length));
            strArray = stringArrangement.toArray(new String[stringArrangement.size()]);
        }
        return upperCaseConflictResolver(strArray, conflictResolver);
    }

    /**
     * Prefix for adding with identifier and namespace, when it is a java keyword or starting with digits.
     *
     * @param conflictResolver object of YANG to java naming conflict util
     * @return prefix which needs to be added
     */
    public static String getPrefixForIdentifier(YangToJavaNamingConflictUtil conflictResolver) {

        String prefixForIdentifier = null;
        if (conflictResolver != null) {
            prefixForIdentifier = conflictResolver.getPrefixForIdentifier();
        }
        if (prefixForIdentifier != null) {
            prefixForIdentifier = prefixForIdentifier.replaceAll(REGEX_WITH_ALL_SPECIAL_CHAR, COLON);
            String[] strArray = prefixForIdentifier.split(COLON);
            try {
                if (strArray[0].isEmpty()) {
                    List<String> stringArrangement = new ArrayList<>();
                    stringArrangement.addAll(Arrays.asList(strArray).subList(1, strArray.length));
                    strArray = stringArrangement.toArray(new String[stringArrangement.size()]);
                }
                prefixForIdentifier = strArray[0];
                for (int j = 1; j < strArray.length; j++) {
                    prefixForIdentifier = prefixForIdentifier + strArray[j].substring(0, 1).toUpperCase() +
                            strArray[j].substring(1);
                }
            } catch (ArrayIndexOutOfBoundsException outOfBoundsException) {
                throw new ArrayIndexOutOfBoundsException("The given prefix in pom.xml is invalid.");
            }
        } else {
            prefixForIdentifier = YANG_AUTO_PREFIX;
        }
        return prefixForIdentifier;
    }

    /**
     * Removes empty directory.
     *
     * @param path path to be checked
     */
    public static void removeEmptyDirectory(String path) {
        int index;
        while (path != null && !path.isEmpty()) {
            if (!removeDirectory(path)) {
                break;
            } else {
                index = path.lastIndexOf(SLASH);
                path = path.substring(0, index);
            }
        }
    }

    private static boolean removeDirectory(String path) {
        File dir = new File(path);
        boolean isDeleted = false;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length == 0) {
                isDeleted = dir.delete();
            } else if (files != null && files.length == 1) {
                if ("package-info.java".equals(files[0].getName())
                        || files[0].getName().endsWith("-temp")) {
                    isDeleted = dir.delete();
                }
            }
        }
        return isDeleted;
    }

    /**
     * Converts string to integer number for maven version.
     *
     * @param ver version
     * @return int value of version
     */
    public static int getVersionValue(String ver) {
        String[] array = ver.split(Pattern.quote(PERIOD));
        StringBuilder builder = new StringBuilder();
        for (String str : array) {
            builder.append(str);
        }
        return parseInt(builder.toString());
    }
}
