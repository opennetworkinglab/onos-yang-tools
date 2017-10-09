/*
 * Copyright 2017-present Open Networking Foundation
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

package org.onosproject.yang.runtime.impl;

import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.tool.YangNodeInfo;
import org.onosproject.yang.model.YangModel;
import org.onosproject.yang.runtime.ModelRegistrationParam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.onosproject.yang.compiler.tool.YangCompilerManager.deSerializeDataModel;
import static org.onosproject.yang.compiler.tool.YangCompilerManager.getYangNodes;
import static org.onosproject.yang.compiler.tool.YangCompilerManager.processYangModel;
import static org.onosproject.yang.compiler.utils.UtilConstants.TEMP;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.deleteDirectory;
import static org.onosproject.yang.runtime.DefaultModelRegistrationParam.builder;
import static org.onosproject.yang.runtime.RuntimeHelper.getInterfaceClassName;

/**
 * Represents mock YANG schema node for unit test cases.
 */
public final class MockYangSchemaNodeProvider {

    private static final String FS = File.separator;
    private static final String PATH = System.getProperty("user.dir") +
            FS + "target" + FS + "classes" + FS;
    private static final String SER_FILE_PATH = "yang" + FS + "resources" +
            FS + "YangMetaData.ser";
    private static final String META_PATH = PATH + SER_FILE_PATH;
    private static final String TEMP_FOLDER_PATH = PATH + TEMP;
    private static DefaultYangModelRegistry reg;
    private static List<YangNode> nodes = new ArrayList<>();
    private static String id;

    /**
     * Creates an instance of mock YANG schema for unit test cases.
     */
    private MockYangSchemaNodeProvider() {
    }

    /**
     * Process YANG schema node for a application.
     */
    public static void processSchemaRegistry() {
        try {
            reg = new DefaultYangModelRegistry();
            //Need to deserialize generated meta data file for unit tests.
            YangModel model = deSerializeDataModel(META_PATH);
            Set<YangNode> appNode = getYangNodes(model);
            nodes.addAll(appNode);
            id = model.getYangModelId();
            reg.registerModel(param(nodes));

            //now we need to update the registered classes for YOB.
            addClassInfo(nodes);
            deleteDirectory(TEMP_FOLDER_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unregister given nodes from runtime service.
     *
     * @param nodes list of nodes
     */
    public static void unRegister(List<YangNode> nodes) {
        reg.unregisterModel(param(nodes));
    }

    /**
     * Prepares model registration parameter.
     *
     * @param nodes list of nodes
     * @return model registration parameter
     */
    private static ModelRegistrationParam param(List<YangNode> nodes) {
        //Create model registration param.
        ModelRegistrationParam.Builder b = builder();

        List<YangNodeInfo> nodeInfo = new ArrayList<>();
        setNodeInfo(nodes, nodeInfo);
        YangModel model = processYangModel(META_PATH, nodeInfo, id, true);

        //set YANG model
        b.setYangModel(model);
        return b.build();
    }

    private static void setNodeInfo(List<YangNode> nodes, List<YangNodeInfo> nodeInfos) {
        for (YangNode node : nodes) {
            YangNodeInfo nodeInfo = new YangNodeInfo(node, false);
            nodeInfos.add(nodeInfo);
        }
    }

    static YangModel processModelTest() {
        YangModel model = null;
        try {
            model = deSerializeDataModel(META_PATH);
            Set<YangNode> appNode = getYangNodes(model);
            List<YangNode> nodes = new ArrayList<>();
            nodes.addAll(appNode);
            List<YangNodeInfo> nodeInfo = new ArrayList<>();
            setNodeInfo(nodes, nodeInfo);
            return processYangModel(META_PATH, nodeInfo, id, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    /**
     * Prepares model registration parameter.
     *
     * @param nodes list of nodes
     */
    private static void addClassInfo(List<YangNode> nodes) {
        //Process loading class file.
        String appName;
        ClassLoader classLoader = MockYangSchemaNodeProvider.class.getClassLoader();

        Iterator<YangNode> it = nodes.iterator();
        while (it.hasNext()) {
            YangSchemaNode node = it.next();

            //If service class is not generated then use
            // interface file to load this class.
            appName = getInterfaceClassName(node);
            Class<?> cls;
            try {
                cls = classLoader.loadClass(appName);
            } catch (ClassNotFoundException e) {
                continue;
            }

            reg.addRegClass(appName, cls);
        }
    }

    /**
     * Returns schema registry.
     *
     * @return schema registry
     */

    public static DefaultYangModelRegistry registry() {
        return reg;
    }

    /**
     * Adds a mock node in reg.
     *
     * @param node schema node
     * @param name name of node
     */
    public static void addMockNode(YangSchemaNode node, String name) {
        reg.processApplicationContext(node, name);
    }
}
