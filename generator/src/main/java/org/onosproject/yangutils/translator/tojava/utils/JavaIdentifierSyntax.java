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

package org.onosproject.yangutils.translator.tojava.utils;

import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangRevision;
import org.onosproject.yangutils.translator.exception.TranslatorException;
import org.onosproject.yangutils.translator.tojava.JavaFileInfoContainer;
import org.onosproject.yangutils.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yangutils.utils.io.YangToJavaNamingConflictUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.getParentNodeInGenCode;
import static org.onosproject.yangutils.utils.UtilConstants.COLON;
import static org.onosproject.yangutils.utils.UtilConstants.DEFAULT_BASE_PKG;
import static org.onosproject.yangutils.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.HYPHEN;
import static org.onosproject.yangutils.utils.UtilConstants.JAVA_KEY_WORDS;
import static org.onosproject.yangutils.utils.UtilConstants.PERIOD;
import static org.onosproject.yangutils.utils.UtilConstants.QUOTES;
import static org.onosproject.yangutils.utils.UtilConstants.REGEX_FOR_FIRST_DIGIT;
import static org.onosproject.yangutils.utils.UtilConstants.REGEX_WITH_ALL_SPECIAL_CHAR;
import static org.onosproject.yangutils.utils.UtilConstants.REVISION_PREFIX;
import static org.onosproject.yangutils.utils.UtilConstants.SLASH;
import static org.onosproject.yangutils.utils.UtilConstants.UNDER_SCORE;
import static org.onosproject.yangutils.utils.UtilConstants.VERSION_PREFIX;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.addPackageInfo;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.createDirectories;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getAbsolutePackagePath;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getJavaPackageFromPackagePath;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getPackageDirPathFromJavaJPackage;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getPrefixForIdentifier;

/**
 * Represents an utility Class for translating the name from YANG to java convention.
 */
public final class JavaIdentifierSyntax {

    private static final int INDEX_ZERO = 0;
    private static final int INDEX_ONE = 1;
    private static final int VALUE_CHECK = 10;
    private static final String ZERO = "0";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Create instance of java identifier syntax.
     */
    private JavaIdentifierSyntax() {
    }

    /**
     * Returns the root package string.
     *
     * @param version   YANG version
     * @param nameSpace name space of the module
     * @param revision  revision of the module defined
     * @param resolver  object of YANG to java naming conflict util
     * @return the root package string
     */
    public static String getRootPackage(byte version, String nameSpace,
                                        YangRevision revision,
                                        YangToJavaNamingConflictUtil resolver) {

        StringBuilder pkg = new StringBuilder(DEFAULT_BASE_PKG)
                .append(PERIOD)
                .append(getYangVersion(version))
                .append(PERIOD)
                .append(getPkgFromNameSpace(nameSpace, resolver));
        if (revision != null) {
            pkg.append(PERIOD)
                    .append(getYangRevisionStr(revision.getRevDate()));
        }
        return pkg.toString().toLowerCase();
    }


    /**
     * Returns version.
     *
     * @param ver YANG version
     * @return version
     */
    private static String getYangVersion(byte ver) {
        return VERSION_PREFIX + ver;
    }

    /**
     * Returns package name from name space.
     *
     * @param nameSpace name space of YANG module
     * @param resolver  object of YANG to java naming conflict util
     * @return java package name as per java rules
     */
    private static String getPkgFromNameSpace(String nameSpace,
                                              YangToJavaNamingConflictUtil resolver) {

        ArrayList<String> pkgArr = new ArrayList<>();
        nameSpace = nameSpace.replace(QUOTES, EMPTY_STRING);
        String properNameSpace = nameSpace.replaceAll
                (REGEX_WITH_ALL_SPECIAL_CHAR, COLON);
        String[] nameSpaceArr = properNameSpace.split(COLON);

        Collections.addAll(pkgArr, nameSpaceArr);
        return getPkgFrmArr(pkgArr, resolver);
    }

    /**
     * Returns revision string array.
     *
     * @param date YANG module revision
     * @return revision string
     */
    private static String getYangRevisionStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String dateInString = sdf.format(date);
        String[] revisionArr = dateInString.split(HYPHEN);

        StringBuilder rev = new StringBuilder(REVISION_PREFIX)
                .append(revisionArr[INDEX_ZERO]);

        for (int i = INDEX_ONE; i < revisionArr.length; i++) {
            Integer val = Integer.parseInt(revisionArr[i]);
            if (val < VALUE_CHECK) {
                rev.append(ZERO);
            }
            rev.append(val);
        }
        return rev.toString();
    }

    /**
     * Returns the package string.
     *
     * @param pkgArr           package array
     * @param conflictResolver object of YANG to java naming conflict util
     * @return package string
     */
    private static String getPkgFrmArr(ArrayList<String> pkgArr, YangToJavaNamingConflictUtil conflictResolver) {

        StringBuilder pkg = new StringBuilder();
        int size = pkgArr.size();
        int i = 0;
        for (String member : pkgArr) {
            boolean presenceOfKeyword = JAVA_KEY_WORDS.contains(member.toLowerCase());
            if (presenceOfKeyword || member.matches(REGEX_FOR_FIRST_DIGIT)) {
                String prefix = getPrefixForIdentifier(conflictResolver);
                member = prefix + member;
            }
            pkg.append(member);
            if (i != size - 1) {
                pkg.append(PERIOD);
            }
            i++;
        }
        return pkg.toString();
    }

    /**
     * Returns enum's java name.
     *
     * @param name enum's name
     * @return enum's java name
     */
    public static String getEnumJavaAttribute(String name) {

        name = name.replaceAll(REGEX_WITH_ALL_SPECIAL_CHAR, COLON);
        String[] strArray = name.split(COLON);
        StringBuilder output = new StringBuilder();
        if (strArray[0].isEmpty()) {
            List<String> stringArrangement = new ArrayList<>();
            stringArrangement.addAll(Arrays.asList(strArray).subList(1, strArray.length));
            strArray = stringArrangement.toArray(new String[stringArrangement.size()]);
        }
        for (int i = 0; i < strArray.length; i++) {
            if (i > 0 && i < strArray.length) {
                output.append(UNDER_SCORE);
            }
            output.append(strArray[i]);
        }
        return output.toString();
    }

    /**
     * Creates a package structure with package info java file if not present.
     *
     * @param yangNode YANG node for which code is being generated
     * @throws IOException any IO exception
     */
    public static void createPackage(YangNode yangNode)
            throws IOException {
        if (!(yangNode instanceof JavaFileInfoContainer)) {
            throw new TranslatorException("current node must have java file info " +
                                                  yangNode.getName() + " in " +
                                                  yangNode.getLineNumber() + " at " +
                                                  yangNode.getCharPosition() +
                                                  " in " + yangNode.getFileName());
        }
        String pkgInfo;
        JavaFileInfoTranslator javaFileInfo = ((JavaFileInfoContainer) yangNode)
                .getJavaFileInfo();
        String pkg = getAbsolutePackagePath(javaFileInfo.getBaseCodeGenPath(),
                                            javaFileInfo.getPackageFilePath());
        JavaFileInfoTranslator parentInfo;
        if (!doesPackageExist(pkg)) {
            try {
                File pack = createDirectories(pkg);
                YangNode parent = getParentNodeInGenCode(yangNode);
                if (parent != null) {
                    parentInfo = ((JavaFileInfoContainer) parent).getJavaFileInfo();
                    pkgInfo = parentInfo.getJavaName();
                    addPackageInfo(pack, pkgInfo, getJavaPackageFromPackagePath(pkg),
                                   true);
                } else {
                    pkgInfo = javaFileInfo.getJavaName();
                    addPackageInfo(pack, pkgInfo, getJavaPackageFromPackagePath(pkg),
                                   false);
                }
            } catch (IOException e) {
                throw new IOException("failed to create package-info file");
            }
        }
    }

    /**
     * Checks if the package directory structure created.
     *
     * @param pkg Package to check if it is created
     * @return existence status of package
     */
    static boolean doesPackageExist(String pkg) {
        File pkgDir = new File(getPackageDirPathFromJavaJPackage(pkg));
        File pkgWithFile = new File(pkgDir + SLASH + "package-info.java");
        return pkgDir.exists() && pkgWithFile.isFile();
    }
}
