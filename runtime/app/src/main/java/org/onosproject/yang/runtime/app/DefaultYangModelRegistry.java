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

package org.onosproject.yang.runtime.app;

import org.onosproject.yang.YangModel;
import org.onosproject.yang.YangModuleId;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.runtime.api.AppModuleInfo;
import org.onosproject.yang.runtime.api.ModelRegistrationParam;
import org.onosproject.yang.runtime.api.YangModelRegistry;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Collections.sort;
import static org.onosproject.yang.compiler.plugin.utils.YangApacheUtils.getDateInStringFormat;
import static org.onosproject.yang.runtime.utils.RuntimeHelper.getInterfaceClassName;
import static org.onosproject.yang.runtime.utils.RuntimeHelper.getNodes;
import static org.onosproject.yang.runtime.utils.RuntimeHelper.getOpParamClassName;
import static org.onosproject.yang.runtime.utils.RuntimeHelper.getServiceName;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents YANG model registry implementation.
 */
public class DefaultYangModelRegistry implements YangModelRegistry {

    private static final Logger log = getLogger(DefaultYangModelRegistry.class);
    private static final String AT = "@";

    /*
     * Map for storing YANG schema nodes. Key will be the schema name of
     * module node defined in YANG file.
     */
    private final ConcurrentMap<String, ConcurrentMap<String, YangSchemaNode>>
            yangSchemaStore;

    /*
     * Map for storing YANG schema nodes with respect to root's generated
     * interface file name.
     */
    private final ConcurrentMap<String, YangSchemaNode> interfaceNameKeyStore;

    /*
     * Map for storing YANG schema nodes root's generated op param file name.
     */
    private final ConcurrentMap<String, YangSchemaNode> opParamNameKeyStore;

    /*
     * Map for storing YANG schema nodes with respect to app name. Key will
     * be the registered class name.
     */
    private final ConcurrentMap<String, YangSchemaNode> appNameKeyStore;

    /*
     * Map for storing registered classes. Will be used by YOB for class
     * loading operations. key will be qualified name of generated class.
     * It will be maintained for top level class i.e. module.
     */
    private final ConcurrentMap<String, Class<?>> registerClassStore;

    /**
     * Map for storing schema nodes with respect to namespace. Will be used
     * by YCH in scenario where module name is not present in XML.
     */
    private final ConcurrentMap<String, YangSchemaNode> nameSpaceSchemaStore;

    /**
     * Set of YANG models.
     */
    private final Set<YangModel> models;

    /**
     * Creates an instance of default YANG schema registry.
     */
    public DefaultYangModelRegistry() {
        models = new LinkedHashSet<>();
        yangSchemaStore = new ConcurrentHashMap<>();
        interfaceNameKeyStore = new ConcurrentHashMap<>();
        opParamNameKeyStore = new ConcurrentHashMap<>();
        registerClassStore = new ConcurrentHashMap<>();
        appNameKeyStore = new ConcurrentHashMap<>();
        nameSpaceSchemaStore = new ConcurrentHashMap<>();
    }

    @Override
    public void registerModel(ModelRegistrationParam param) {
        YangModel model = param.getYangModel();
        if (model != null) {
            Set<YangNode> curNodes = getNodes(model);
            models.add(model);
            AppModuleInfo info;
            Class<?> service;
            for (YangModuleId id : model.getYangModulesId()) {
                info = param.getAppModuleInfo(id);
                if (info != null) {
                    service = info.getModuleClass();
                    String name = service.getName();
                    if (!verifyIfApplicationAlreadyRegistered(service)) {
                        if (!registerClassStore.containsKey(name)) {
                            registerClassStore.put(name, service);
                        }
                        if (curNodes != null && !curNodes.isEmpty()) {
                            processRegistration(service, curNodes);
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException("model can not be null.");
        }
    }

    @Override
    public void unregisterModel(ModelRegistrationParam param) {
        synchronized (DefaultYangModelRegistry.class) {
            YangModel model = param.getYangModel();
            if (model != null) {
                AppModuleInfo info;
                for (YangModuleId id : model.getYangModulesId()) {
                    info = param.getAppModuleInfo(id);
                    YangSchemaNode curNode;
                    Class<?> sClass = info.getModuleClass();
                    String serviceName = sClass.getName();
                    //Remove registered class from store.
                    registerClassStore.remove(serviceName);
                    //check if service is in app store.
                    curNode = appNameKeyStore.get(serviceName);
                    if (curNode == null) {
                        curNode = interfaceNameKeyStore.get(serviceName);
                    }

                    if (curNode != null) {
                        removeSchemaNode(curNode);
                        interfaceNameKeyStore.remove(getInterfaceClassName(curNode));
                        opParamNameKeyStore.remove(getOpParamClassName(curNode));
                        appNameKeyStore.remove(serviceName);
                        nameSpaceSchemaStore.remove(curNode.getNameSpace()
                                                            .getModuleNamespace());
                        log.info(" service class {} of model {} is " +
                                         "unregistered.", sClass
                                         .getSimpleName(), param);
                    } else {
                        throw new RuntimeException(sClass.getSimpleName() +
                                                           " service was not registered.");
                    }
                }
            } else {
                throw new RuntimeException("model can not be null.");
            }
        }
    }

    @Override
    public Set<YangModel> getModels() {
        return models;
    }

    /**
     * Returns schema node for the given name. Name should be schema defined
     * name.
     *
     * @param schemaName schema name
     * @return YANG schema node
     */
    public YangSchemaNode getForSchemaName(String schemaName) {
        return getForNameWithRev(schemaName);
    }

    /**
     * Returns schema node for the given name. Name should be generated class
     * name. the name provided here should be for registered service class.
     *
     * @param appName application name
     * @return YANG schema node
     */
    public YangSchemaNode getForAppName(String appName) {
        YangSchemaNode node = appNameKeyStore.get(appName);
        if (node == null) {
            log.error("{} not found.", appName);
        }
        return node;
    }

    /**
     * Returns schema node for the given name. Name should be generated class
     * name. the name provided here should be for registered interface class.
     *
     * @param name interface class name
     * @return YANG schema node
     */
    public YangSchemaNode getForInterfaceFileName(String name) {
        YangSchemaNode node = interfaceNameKeyStore.get(name);
        if (node == null) {
            log.error("{} not found.", name);
        }
        return node;
    }

    /**
     * Returns schema node for the given name. Name should be generated class
     * name. the name provided here should be for registered op param class.
     *
     * @param name opparm class name
     * @return YANG schema node
     */
    public YangSchemaNode getForOpPramFileName(
            String name) {
        YangSchemaNode node = opParamNameKeyStore.get(name);
        if (node == null) {
            log.error("{} not found.", name);
        }
        return node;
    }

    /**
     * Returns schema node for the given name. Name should be nodes namespace
     * defined in YANG file
     *
     * @param nameSpace name space of YANG file
     * @return YANG schema node
     */
    public YangSchemaNode getForNameSpace(String nameSpace) {

        YangSchemaNode node = nameSpaceSchemaStore.get(nameSpace);
        if (node == null) {
            log.error("node with {} namespace not found.", nameSpace);
        }
        return node;
    }

    /**
     * Returns registered service for given schema node.
     *
     * @param schemaNode schema node
     * @return registered class
     */
    public Class<?> getRegisteredClass(YangSchemaNode schemaNode) {
        String interfaceName = getInterfaceClassName(schemaNode);
        String serviceName = getServiceName(schemaNode);
        Class<?> regClass = registerClassStore.get(serviceName);
        if (regClass == null) {
            regClass = registerClassStore.get(interfaceName);
        }
        return regClass;
    }

    /**
     * Process application registration.
     *
     * @param service service class
     * @param nodes   YANG nodes
     */
    private void processRegistration(Class<?> service, Set<YangNode> nodes) {

        // process storing operations.
        YangNode schemaNode = findNodeWhichShouldBeReg(service.getName(), nodes);
        if (schemaNode != null) {
            //Process application context for registrations.
            processApplicationContext(schemaNode, service.getName());
        }
    }

    /**
     * Returns the node for which corresponding class is generated.
     *
     * @param name  generated class name
     * @param nodes list of yang nodes
     * @return node for which corresponding class is generated
     */
    private YangNode findNodeWhichShouldBeReg(String name,
                                              Set<YangNode> nodes) {
        for (YangNode node : nodes) {
            if (name.equals(getServiceName(node)) ||
                    name.equals(getInterfaceClassName(node))) {
                return node;
            }
        }
        return null;
    }

    /**
     * Verifies if application is already registered with runtime.
     *
     * @param appClass application class
     * @return true if application already registered
     */
    private boolean verifyIfApplicationAlreadyRegistered(Class<?> appClass) {
        String appName = appClass.getName();
        return registerClassStore.containsKey(appName) ||
                interfaceNameKeyStore.containsKey(appName);
    }

    /**
     * Process an application an updates the maps for YANG model registry.
     *
     * @param appNode application YANG schema nodes
     * @param name    class name
     */
    private void processApplicationContext(YangSchemaNode appNode, String name) {

        //Update map for which registrations is being called.
        try {
            if (appNode.isNotificationPresent()) {
                //TODO: add logic for getting wrt RPC.
                appNameKeyStore.put(name, appNode);
            }
        } catch (DataModelException e) {
            e.printStackTrace();
        }

        // Updates schema store.
        addToSchemaStore(appNode);
        // update interface store.
        interfaceNameKeyStore.put(getInterfaceClassName(appNode), appNode);

        //update op param store.
        opParamNameKeyStore.put(getOpParamClassName(appNode), appNode);

        //update namespaceSchema store.
        nameSpaceSchemaStore.put(appNode.getNameSpace().getModuleNamespace(), appNode);

        log.info("successfully registered this application {}", name);
    }

    /**
     * Returns schema node based on the revision.
     *
     * @param name name of the schema node
     * @return schema node based on the revision
     */
    private YangSchemaNode getForNameWithRev(String name) {
        ConcurrentMap<String, YangSchemaNode> revMap;
        YangSchemaNode schemaNode;
        if (name.contains(AT)) {
            String[] revArray = name.split(AT);
            revMap = yangSchemaStore.get(revArray[0]);
            schemaNode = revMap.get(name);
            if (schemaNode == null) {
                log.error("{} not found.", name);
            }
            return schemaNode;
        }
        if (yangSchemaStore.containsKey(name)) {
            revMap = yangSchemaStore.get(name);
            if (revMap != null && !revMap.isEmpty()) {
                YangSchemaNode node = revMap.get(name);
                if (node != null) {
                    return node;
                }
                String revName = getLatestVersion(revMap);
                return revMap.get(revName);
            }
        }
        log.error("{} not found.", name);
        return null;
    }

    private String getLatestVersion(ConcurrentMap<String, YangSchemaNode> revMap) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, YangSchemaNode> entry : revMap.entrySet()) {
            keys.add(entry.getKey());
        }
        sort(keys);
        return keys.get(keys.size() - 1);
    }

    /**
     * Adds schema node when different revision of node has received.
     *
     * @param schemaNode schema node
     */
    private void addToSchemaStore(YangSchemaNode schemaNode) {

        String date = getDateInStringFormat((YangNode) schemaNode);
        String name = schemaNode.getName();
        String revName = name;
        if (date != null) {
            revName = name + AT + date;
        }
        //check if already present.
        if (!yangSchemaStore.containsKey(name)) {
            ConcurrentMap<String, YangSchemaNode> revStore =
                    new ConcurrentHashMap<>();
            revStore.put(revName, schemaNode);
            yangSchemaStore.put(name, revStore);
        } else {
            yangSchemaStore.get(name).put(revName, schemaNode);
        }
    }

    /**
     * Removes schema node from schema map.
     *
     * @param removableNode schema node which needs to be removed
     */
    private void removeSchemaNode(YangSchemaNode removableNode) {
        String name = removableNode.getName();
        String revName = name;
        String date = getDateInStringFormat((YangNode) removableNode);
        if (date != null) {
            revName = name + AT + date;
        }
        ConcurrentMap<String, YangSchemaNode> revMap = yangSchemaStore.get(name);
        if (revMap != null && !revMap.isEmpty() && revMap.size() != 1) {
            revMap.remove(revName);
        } else {
            yangSchemaStore.remove(removableNode.getName());
        }
    }
}
