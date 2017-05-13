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

import org.onosproject.yang.compiler.datamodel.SchemaDataNode;
import org.onosproject.yang.compiler.datamodel.YangChoice;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.model.SingleInstanceNodeContext;
import org.onosproject.yang.model.YangModel;
import org.onosproject.yang.model.YangModuleId;
import org.onosproject.yang.runtime.AppModuleInfo;
import org.onosproject.yang.runtime.ModelRegistrationParam;
import org.onosproject.yang.runtime.YangModelRegistry;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableSet;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.getDateInStringFormat;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.getNodeIdFromSchemaId;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.RuntimeHelper.getInterfaceClassName;
import static org.onosproject.yang.runtime.RuntimeHelper.getNodes;
import static org.onosproject.yang.runtime.RuntimeHelper.getServiceName;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents YANG model registry implementation.
 */
public class DefaultYangModelRegistry implements YangModelRegistry,
        SingleInstanceNodeContext {

    private static final String AT = "@";
    private final Logger log = getLogger(getClass());
    /*
     * Map for storing YANG schema nodes. Key will be the schema name of
     * module node defined in YANG file.
     */
    private final ConcurrentMap<String, ConcurrentMap<String, YangSchemaNode>>
            yangSchemaStore;

    /*
     * Map for storing YANG schema nodes with respect to root's generated
     * file name by which registration is being done.
     */
    private final ConcurrentMap<String, YangSchemaNode> regClassNameKeyStore;

    /*
     * Map for storing YANG schema nodes with respect to root's generated
     * file's qualified name.
     */
    private final ConcurrentMap<String, YangSchemaNode> qNameKeyStore;

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
        regClassNameKeyStore = new ConcurrentHashMap<>();
        registerClassStore = new ConcurrentHashMap<>();
        nameSpaceSchemaStore = new ConcurrentHashMap<>();
        qNameKeyStore = new ConcurrentHashMap<>();
    }

    @Override
    public void registerModel(ModelRegistrationParam param) {
        YangModel model = checkNotNull(param.getYangModel(), "Model must not be null");
        Set<YangNode> curNodes = getNodes(model);
        models.add(model);
        String name;
        Class<?> service;
        AppModuleInfo info;
        //adding class info if added by application.
        for (YangModuleId id : model.getYangModulesId()) {
            info = param.getAppModuleInfo(id);
            if (info != null) {
                service = info.getModuleClass();
                name = service.getName();
                if (!registerClassStore.containsKey(name)) {
                    registerClassStore.put(name, service);
                }
            }
        }

        //Register all the YANG nodes.
        registerModule(curNodes);

        //update child context
        updateChildContext(curNodes);
    }

    /**
     * Register specific model.
     *
     * @param curNodes current nodes
     */
    private void registerModule(Set<YangNode> curNodes) {
        String name;
        //register all the nodes present in YANG model.
        if (curNodes != null && !curNodes.isEmpty()) {
            for (YangNode node : curNodes) {
                name = getInterfaceClassName(node);
                if (!regClassNameKeyStore.containsKey(name)) {
                    processApplicationContext(node, name);
                } else {
                    log.info("class already registered with model registry " +
                                     "{}", name);
                }
            }
        }
    }

    @Override
    public void unregisterModel(ModelRegistrationParam param) {
        synchronized (DefaultYangModelRegistry.class) {
            YangModel model = checkNotNull(param.getYangModel(),
                                           "Model must not be null");
            models.remove(model);
            //Unregister all yang files
            Set<YangNode> curNodes = getNodes(model);
            for (YangNode node : curNodes) {
                processUnReg(getInterfaceClassName(node));
            }
        }
    }

    private void processUnReg(String serviceName) {
        YangSchemaNode curNode = regClassNameKeyStore.get(serviceName);

        if (curNode != null) {
            removeSchemaNode(curNode);
            regClassNameKeyStore.remove(serviceName);
            qNameKeyStore.remove(serviceName.toLowerCase());
            nameSpaceSchemaStore.remove(
                    curNode.getNameSpace().getModuleNamespace());
            registerClassStore.remove(serviceName);
            log.info(" service class {} of model is " +
                             "unregistered.", serviceName);
        } else {
            log.error("Either {} service was not registered or " +
                              "already unregistered from model " +
                              "registry.", serviceName);
        }
    }

    @Override
    public Set<YangModel> getModels() {
        return unmodifiableSet(models);
    }

    /**
     * Returns schema node for the given name. Name should be schema defined
     * name.
     *
     * @param schemaName schema name
     * @return YANG schema node
     */
    YangSchemaNode getForSchemaName(String schemaName) {
        return getForNameWithRev(schemaName);
    }

    /**
     * Returns schema node for the given name. Name should be generated class
     * name. the name provided here should be for registered class.
     *
     * @param name interface class name
     * @return YANG schema node
     */
    YangSchemaNode getForRegClassName(String name) {
        YangSchemaNode node = regClassNameKeyStore.get(name);
        if (node == null) {
            log.error("{} not found.", name);
        }
        return node;
    }

    /**
     * Returns schema node for the given package. pkg should be generated class
     * pkg. the pkg provided here should be for registered interface class.
     * pkg string contains the java package and java name of the module
     * generated class in lower case.
     *
     * @param pkg       interface class pkg
     * @param isFromDnb true when request has come from data tree builder
     * @return YANG schema node
     */
    YangSchemaNode getForRegClassQualifiedName(String pkg, boolean isFromDnb) {
        YangSchemaNode node = qNameKeyStore.get(pkg);
        if (node == null && !isFromDnb) {
            log.error("{} not found.", pkg);
        }
        return node;
    }

    /**
     * Returns schema node for the given name. Name should be nodes namespace
     * defined in YANG file
     *
     * @param nameSpace         name space of YANG file
     * @param isForChildContext if the method call has arrived for child context
     * @return YANG schema node
     */
    public YangSchemaNode getForNameSpace(String nameSpace,
                                          boolean isForChildContext) {

        YangSchemaNode node = nameSpaceSchemaStore.get(nameSpace);
        if (node == null && !isForChildContext) {
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
    Class<?> getRegisteredClass(YangSchemaNode schemaNode) {
        Class<?> regClass = null;
        if (schemaNode != null) {
            String interfaceName = getInterfaceClassName(schemaNode);
            String serviceName = getServiceName(schemaNode);
            regClass = registerClassStore.get(serviceName);
            if (regClass == null) {
                regClass = registerClassStore.get(interfaceName);
            }
        }
        if (regClass == null) {
            log.error("{} node should not be null.");
        }
        return regClass;
    }

    /**
     * Process an application an updates the maps for YANG model registry.
     *
     * @param appNode application YANG schema nodes
     * @param name    class name
     */
    void processApplicationContext(YangSchemaNode appNode, String name) {

        // Updates schema store.
        addToSchemaStore(appNode);

        // update interface store.
        regClassNameKeyStore.put(name, appNode);

        qNameKeyStore.put(getInterfaceClassName(appNode).toLowerCase(), appNode);

        //update namespaceSchema store.
        nameSpaceSchemaStore.put(appNode.getNameSpace().getModuleNamespace(),
                                 appNode);

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

    private String getLatestVersion(ConcurrentMap<String,
            YangSchemaNode> revMap) {
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
        ConcurrentMap<String, YangSchemaNode> revMap =
                yangSchemaStore.get(name);
        if (revMap != null && !revMap.isEmpty() && revMap.size() != 1) {
            revMap.remove(revName);
        } else {
            yangSchemaStore.remove(removableNode.getName());
        }
    }

    @Override
    public SchemaContext getParentContext() {
        return null;
    }

    @Override
    public DataNode.Type getType() {
        return SINGLE_INSTANCE_NODE;
    }

    @Override
    public SchemaId getSchemaId() {
        return new SchemaId("/", null);
    }

    /**
     * Updates child's context. It sets itself as a parent context for first
     * level child's in module/sub-module.
     *
     * @param nodes set of module/submodule nodes
     */
    private void updateChildContext(Set<YangNode> nodes) {
        // Preparing schema id for logical node with name "/"
        for (YangNode node : nodes) {
            node.setLeafRootContext(this);
            YangNode child = node.getChild();
            while (child != null) {
                if (child instanceof YangChoice) {
                    updateSchemaContextForChoiceChild(child);
                } else if (child instanceof SchemaDataNode) {
                    child.setRootContext(this);
                }
                child = child.getNextSibling();
            }
        }
    }

    /**
     * Updates the parent context for given choice-case node child's.
     *
     * @param child yang node
     */
    private void updateContextForChoiceCase(YangNode child) {
        while (child != null) {
            if (child instanceof YangChoice) {
                updateSchemaContextForChoiceChild(child);
            } else if (child instanceof SchemaDataNode) {
                child.setRootContext(this);
            }
            child = child.getNextSibling();
        }
    }

    /**
     * Updates the parent context for given choice node child's.
     *
     * @param curNode choice node
     */
    private void updateSchemaContextForChoiceChild(YangNode curNode) {
        YangNode child = curNode.getChild();
        // Setting the parent context for case
        while (child != null) {
            updateSchemaContextForCaseChild(child);
            child = child.getNextSibling();
        }
    }

    /**
     * Updates the parent context for given case node child's.
     *
     * @param curNode case node
     */
    private void updateSchemaContextForCaseChild(YangNode curNode) {
        curNode.setLeafRootContext(this);
        YangNode child = curNode.getChild();
        updateContextForChoiceCase(child);
    }

    @Override
    public SchemaContext getChildContext(SchemaId schemaId) {

        checkNotNull(schemaId);
        if (schemaId.namespace() == null) {
            log.error("node with {} namespace not found.", schemaId.namespace());
        }

        String namespace = schemaId.namespace();
        YangSchemaNode node = getForNameSpace(namespace, true);
        if (node == null) {
            //If namespace if module name.
            node = getForSchemaName(schemaId.namespace());
        }
        YangSchemaNodeIdentifier id = getNodeIdFromSchemaId(schemaId, namespace);

        try {
            if (node != null) {
                return node.getChildSchema(id).getSchemaNode();
            } else {
                log.error("node with {} namespace not found.", schemaId
                        .namespace());
            }
        } catch (DataModelException e) {
            log.error("failed to get child schema", e);
        }
        return null;
    }
}
