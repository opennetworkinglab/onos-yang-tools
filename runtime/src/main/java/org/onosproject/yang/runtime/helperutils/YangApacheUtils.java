/*
 * Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang.runtime.helperutils;

import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.model.DefaultYangModel;
import org.onosproject.yang.model.DefaultYangModuleId;
import org.onosproject.yang.model.YangModel;
import org.onosproject.yang.model.YangModuleId;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

import static java.nio.file.Paths.get;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.parseJarFile;
import static org.osgi.framework.FrameworkUtil.getBundle;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Utilities for runtime to use apache tools.
 */
public final class YangApacheUtils {

    private static final String SLASH = File.separator;
    private static final String HYPHEN = "-";
    private static final String PERIOD = ".";
    private static final String YANG_META_DATA = "YangMetaData.ser";
    private static final String YANG_RESOURCES = "yang/resources";
    private static final String SYSTEM = SLASH + "system" + SLASH;
    private static final String MAVEN = "mvn:";
    private static final String JAR = ".jar";
    private static final String USER_DIRECTORY = "user.dir";
    private static final String DATE_FORMAT = "yyyy-mm-dd";
    private static final Logger log = getLogger(YangApacheUtils.class);

    // Forbid construction.
    private YangApacheUtils() {
    }

    /**
     * Returns YANG model for generated module class.
     *
     * @param modClass generated module class
     * @return YANG model
     */
    public static YangModel getYangModel(Class<?> modClass) {
        BundleContext context = getBundle(modClass).getBundleContext();
        if (context != null) {
            Bundle bundle = context.getBundle();
            List<YangNode> curNodes;
            String jarPath;
            String metaPath;
            jarPath = getJarPathFromBundleLocation(
                    bundle.getLocation(), context.getProperty(USER_DIRECTORY));
            metaPath = jarPath + SLASH + YANG_RESOURCES + SLASH + YANG_META_DATA;
            curNodes = processJarParsingOperations(jarPath);
            // process model creations.
            if (curNodes != null && !curNodes.isEmpty()) {
                return processYangModel(metaPath, curNodes);
            }
        }
        return null;
    }

    /**
     * Returns YANG model for application.
     *
     * @param path     path for metadata file
     * @param curNodes curNodes YANG nodes
     * @return YANG model
     */
    public static YangModel processYangModel(String path,
                                             List<YangNode> curNodes) {
        YangModel.Builder b = DefaultYangModel.builder();
        YangModuleId id;
        for (YangNode node : curNodes) {
            id = processModuleId(node);
            YangModuleExtendedInfo module =
                    new YangModuleExtendedInfo(id, get(node.getFileName()), get(path));
            module.setSchema(node);
            b.addModule(id, module);
        }
        return b.build();
    }

    /**
     * Returns YANG module id for a given YANG module node.
     *
     * @param module YANG module
     * @return YANG module id for a given YANG module node
     */
    public static YangModuleId processModuleId(YangNode module) {
        String rev = getDateInStringFormat(module);
        return new DefaultYangModuleId(module.getName(), rev);
    }

    /**
     * Returns date in string format.
     *
     * @param schemaNode schema node
     * @return date in string format
     */
    public static String getDateInStringFormat(YangNode schemaNode) {
        if (schemaNode != null) {
            if (schemaNode.getRevision() != null) {
                return new SimpleDateFormat(DATE_FORMAT)
                        .format(schemaNode.getRevision().getRevDate());
            }
        }
        return null;
    }

    /**
     * Returns jar path from bundle mvnLocationPath.
     *
     * @param mvnLocationPath mvnLocationPath of bundle
     * @return path of jar
     */
    private static String getJarPathFromBundleLocation(String mvnLocationPath,
                                                       String currentDirectory) {
        String path = currentDirectory + SYSTEM;
        if (mvnLocationPath.contains(MAVEN)) {
            String[] strArray = mvnLocationPath.split(MAVEN);
            if (strArray[1].contains(File.separator)) {
                String[] split = strArray[1].split(File.separator);
                if (split[0].contains(PERIOD)) {
                    String[] groupId = split[0].split(Pattern.quote(PERIOD));
                    return path + groupId[0] + SLASH + groupId[1] + SLASH + split[1] +
                            SLASH + split[2] + SLASH + split[1] + HYPHEN + split[2];
                }
            }
        }
        return null;
    }

    /**
     * Process jar file for fetching YANG nodes.
     *
     * @param path jar file path
     * @return YANG schema nodes
     */
    private static List<YangNode> processJarParsingOperations(String path) {
        //Deserialize data model and get the YANG node set.
        String jar = path + JAR;
        try {
            File file = new File(jar);
            if (file.exists()) {
                return parseJarFile(path + JAR, path);
            }
        } catch (IOException e) {
            log.error(" failed to parse the jar file in path {} : {} ", path,
                      e.getMessage());
        }
        return null;
    }
}
