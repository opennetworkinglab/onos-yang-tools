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

package org.onosproject.yang.runtime.impl;

import org.onosproject.yang.YangModel;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.runtime.AppModuleInfo;
import org.onosproject.yang.runtime.DefaultAppModuleInfo;
import org.onosproject.yang.runtime.DefaultModelRegistrationParam;
import org.onosproject.yang.runtime.ModelRegistrationParam;
import org.onosproject.yang.runtime.YangModelRegistry;
import org.onosproject.yang.runtime.ymrimpl.DefaultYangModelRegistry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.deSerializeDataModel;
import static org.onosproject.yang.compiler.plugin.utils.YangApacheUtils.processModuleId;
import static org.onosproject.yang.compiler.plugin.utils.YangApacheUtils.processYangModel;
import static org.onosproject.yang.compiler.utils.UtilConstants.TEMP;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.deleteDirectory;
import static org.onosproject.yang.runtime.helperutils.RuntimeHelper.getInterfaceClassName;

/**
 * Represents mock bundle context. provides bundle context for YSR to do unit
 * testing.
 */
public class TestYangSchemaNodeProvider {

    private static final String FS = File.separator;
    private static final String PATH = System.getProperty("user.dir") +
            FS + "target" + FS + "classes" + FS;
    private static final String SER_FILE_PATH = "yang" + FS + "resources" +
            FS + "YangMetaData.ser";
    private static final String META_PATH = PATH + SER_FILE_PATH;
    private static final String TEMP_FOLDER_PATH = PATH + TEMP;
    private YangModelRegistry reg = new DefaultYangModelRegistry();
    private List<YangNode> nodes = new ArrayList<>();

    /**
     * Creates an instance of mock bundle context.
     */
    public TestYangSchemaNodeProvider() {
    }

    /**
     * Process YANG schema node for a application.
     */
    public void processSchemaRegistry() {
        try {
            //Need to deserialize generated meta data file for unit tests.
            Set<YangNode> appNode = deSerializeDataModel(META_PATH);
            nodes.addAll(appNode);
            reg.registerModel(prepareParam(nodes));
            deleteDirectory(TEMP_FOLDER_PATH);
        } catch (IOException e) {
        }
    }

    /**
     * Unregister given nodes from runtime service.
     *
     * @param nodes list of nodes
     */
    public void unRegister(List<YangNode> nodes) {
        reg.unregisterModel(prepareParam(nodes));
    }

    /**
     * Prepares model registration parameter.
     *
     * @param nodes list of nodes
     * @return model registration parameter
     */
    private ModelRegistrationParam prepareParam(List<YangNode> nodes) {
        //Process loading class file.
        String appName;
        ClassLoader classLoader = getClass().getClassLoader();

        //Create model registration param.
        ModelRegistrationParam.Builder b =
                DefaultModelRegistrationParam.builder();

        //create a new YANG model
        YangModel model = processYangModel(META_PATH, nodes);
        //set YANG model
        b.setYangModel(model);

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

            //generate app info.
            AppModuleInfo info = new DefaultAppModuleInfo(cls, null);
            b.addAppModuleInfo(processModuleId((YangNode) node), info);
        }
        return b.build();
    }

    /**
     * Returns schema registry.
     *
     * @return schema registry
     */
    public DefaultYangModelRegistry registry() {
        return (DefaultYangModelRegistry) reg;
    }
}
