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

import org.onosproject.yang.compiler.datamodel.SchemaDataNode;
import org.onosproject.yang.compiler.datamodel.YangChoice;
import org.onosproject.yang.compiler.datamodel.YangInclude;
import org.onosproject.yang.compiler.datamodel.YangModule;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.compiler.datamodel.YangSchemaNodeIdentifier;
import org.onosproject.yang.compiler.datamodel.YangSubModule;
import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.tool.YangModuleExtendedInfo;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ModelConverterException;
import org.onosproject.yang.model.ModelObjectId;
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
import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_ANYDATA_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.getDateInStringFormat;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.getNodeIdFromSchemaId;
import static org.onosproject.yang.compiler.translator.tojava.JavaCodeGeneratorUtil.updateTreeContext;
import static org.onosproject.yang.compiler.utils.UtilConstants.REGEX;
import static org.onosproject.yang.model.DataNode.Type.SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.runtime.RuntimeHelper.getInterfaceClassName;
import static org.onosproject.yang.runtime.RuntimeHelper.getNodes;
import static org.onosproject.yang.runtime.RuntimeHelper.getSelfNodes;
import static org.onosproject.yang.runtime.RuntimeHelper.getServiceName;
import static org.onosproject.yang.runtime.impl.UtilsConstants.AT;
import static org.onosproject.yang.runtime.impl.UtilsConstants.E_NEXIST;
import static org.onosproject.yang.runtime.impl.UtilsConstants.E_NOT_VAL;
import static org.onosproject.yang.runtime.impl.UtilsConstants.E_NULL;
import static org.onosproject.yang.runtime.impl.UtilsConstants.FMT_INV;
import static org.onosproject.yang.runtime.impl.UtilsConstants.errorMsg;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents YANG model registry implementation.
 */
public class DefaultYangModelRegistry implements YangModelRegistry,
        SingleInstanceNodeContext {
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
     * Map for storing YANG model with respect to model identifier. Will
     * be used by other application to register and unregister the YANG
     * models based on model identifier.
     */
    private final ConcurrentMap<String, YangModel> modelIdStore;

    /**
     * Creates an instance of default YANG schema registry.
     */
    public DefaultYangModelRegistry() {
        yangSchemaStore = new ConcurrentHashMap<>();
        regClassNameKeyStore = new ConcurrentHashMap<>();
        registerClassStore = new ConcurrentHashMap<>();
        nameSpaceSchemaStore = new ConcurrentHashMap<>();
        qNameKeyStore = new ConcurrentHashMap<>();
        modelIdStore = new ConcurrentHashMap<>();
    }

    @Override
    public void registerModel(ModelRegistrationParam param) throws
            IllegalArgumentException {
        YangModel model = checkNotNull(param.getYangModel(), E_NULL);
        Set<YangNode> curNodes = getNodes(model, yangSchemaStore);

        //adding class info if added by application.
        AppModuleInfo info = null;
        for (YangModuleId id : param.getYangModel().getYangModulesId()) {
            info = param.getAppModuleInfo(id);
            if (info != null) {
                break;
            }
        }

        // Validating the model Id
        String id = model.getYangModelId();
        if (!id.matches(REGEX)) {
            throw new IllegalArgumentException(E_NOT_VAL);
        }

        if (!modelIdStore.containsKey(id)) {
            updateRegClassStore(param);
            modelIdStore.put(id, model);
        } else if (info != null) {
            updateRegClassStore(param);
        } else {
            throw new IllegalArgumentException("ModelId " + id + " already exist");
        }

        //Register all the YANG nodes, excluding nodes from dependent jar.
        if (curNodes != null && !curNodes.isEmpty()) {
            for (YangNode node : curNodes) {
                registerModule(node);
            }
        }

        //update child context
        updateChildContext(curNodes);
        log.debug("ModelId: {} registered!", id);
    }

    @Override
    public void registerAnydataSchema(ModelObjectId aid, ModelObjectId cid) throws
            IllegalArgumentException {
        YangSchemaNode anySchema = null;
        try {
            ModIdToRscIdConverter conv = new ModIdToRscIdConverter(this);
            anySchema = ((YangSchemaNode) conv.fetchResourceId(aid).appInfo());
            if (anySchema != null &&
                    anySchema.getYangSchemaNodeType() == YANG_ANYDATA_NODE) {
                YangSchemaNode cSchema = ((YangSchemaNode) conv
                        .fetchResourceId(cid).appInfo());
                if (cSchema != null) {
                    YangSchemaNode clonedNode = anySchema.addSchema(cSchema);
                    updateTreeContext(clonedNode, null, false, false);
                } else {
                    throw new IllegalArgumentException(errorMsg(FMT_INV, cid));
                }
            } else {
                throw new IllegalArgumentException(errorMsg(FMT_INV, aid));
            }
        } catch (ModelConverterException e) {
            ModelObjectId id = cid;
            if (anySchema == null) {
                id = aid;
            }
            throw new IllegalArgumentException(errorMsg(FMT_INV, id));
        }
    }

    @Override
    public void unregisterAnydataSchema(Class id, Class id1) throws
            IllegalArgumentException {
        //TODO implemention
    }

    /**
     * Register specific model.
     *
     * @param node YANG node
     */
    private void registerModule(YangNode node) {
        String name;
        //register all the nodes present in YANG model.
        name = getInterfaceClassName(node);
        processApplicationContext(node, name);
    }

    @Override
    public void unregisterModel(ModelRegistrationParam param) {
        synchronized (DefaultYangModelRegistry.class) {
            YangModel model = checkNotNull(param.getYangModel(), E_NULL);
            modelIdStore.remove(model.getYangModelId());
            //Unregister all yang files, excluding nodes from dependent jar.
            Set<YangNode> curNodes = getSelfNodes(model, yangSchemaStore);
            if (curNodes != null && !curNodes.isEmpty()) {
                for (YangNode node : curNodes) {
                    processUnReg(getInterfaceClassName(node));
                }
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
        Set<YangModel> models = new LinkedHashSet<>();
        for (Map.Entry<String, YangModel> entry : modelIdStore.entrySet()) {
            models.add(entry.getValue());
        }
        return unmodifiableSet(models);
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

    @Override
    public YangModel getModel(String id) {
        return modelIdStore.get(id);
    }

    @Override
    public org.onosproject.yang.model.YangModule getModule(YangModuleId id) {
        for (Map.Entry<String, YangModel> entry : modelIdStore
                .entrySet()) {
            org.onosproject.yang.model.YangModule module = entry.getValue()
                    .getYangModule(id);
            if (module != null) {
                return module;
            }
        }
        return null;
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
            log.error(E_NEXIST, nameSpace);
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
            if (regClass == null) {
                log.error("Nothing registered for {} or {}", serviceName, interfaceName);
            }
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

        /*
         * The name of a module determines the namespace of all data node names
         * defined in that module.  If a data node is defined in a submodule,
         * then the namespace-qualified member name uses the name of the main
         * module to which the submodule belongs.
         * So skipping the submodule entry in namespace map.
         */
        if (!(appNode instanceof YangSubModule)) {
            //update namespaceSchema store.
            nameSpaceSchemaStore.put(appNode.getNameSpace().getModuleNamespace(),
                                     appNode);
        }

        log.debug("successfully registered this application {}", name);
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

    @Override
    public SchemaContext getChildContext(SchemaId schemaId) {

        checkNotNull(schemaId);
        String ns = schemaId.namespace();
        if (ns == null) {
            log.error("namespace should not be null for a node");
        }
        YangSchemaNode schemaNode = null;

        YangSchemaNode node = getForNameSpace(ns, true);
        if (node == null) {
            //If namespace is module name.
            node = getForSchemaName(ns);
        }
        YangSchemaNodeIdentifier id = getNodeIdFromSchemaId(schemaId, ns);

        if (node != null) {
            try {
                schemaNode = node.getChildSchema(id).getSchemaNode();
            } catch (DataModelException e) {
                // if exception occurs check for submodule
            }
            if (schemaNode == null) {
                List<YangInclude> includeList = ((YangModule) node)
                        .getIncludeList();
                // Checking requested node in submodule.
                schemaNode = getSubModlueChildNode(id, includeList);
            }
            return schemaNode;
        } else {
            log.error(E_NEXIST, ns);
        }
        return null;
    }


    /**
     * Updates registered class store.
     *
     * @param param model registrations param
     */
    void updateRegClassStore(ModelRegistrationParam param) {
        Class<?> service;
        AppModuleInfo info;
        for (YangModuleId id : param.getYangModel().getYangModulesId()) {
            YangModuleExtendedInfo i = (YangModuleExtendedInfo) param
                    .getYangModel().getYangModule(id);
            if (!i.isInterJar()) {
                info = param.getAppModuleInfo(id);
                if (info != null) {
                    service = info.getModuleClass();
                    addRegClass(service.getName(), service);
                }
            }
        }
    }

    /**
     * Adds the registered class.
     *
     * @param name    qualified name of the class
     * @param service generated class
     */
    void addRegClass(String name, Class<?> service) {
        if (!registerClassStore.containsKey(name)) {
            registerClassStore.put(name, service);
        }
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

    /**
     * Returns the child schema for given node id from the list of included
     * submodule.
     * <p>
     * To reference an item that is defined in one of its
     * submodules, the module MUST include the submodule and if
     * a submodule that needs to reference an item defined in another
     * submodule of the same module, MUST include this submodule.
     * <p>
     * In other words if submodule is including any submodule which
     * belongs to same module then module should also include that
     * submodule.
     *
     * @param id   child node identifier
     * @param list list of included submodule
     */
    private YangSchemaNode getSubModlueChildNode(YangSchemaNodeIdentifier id,
                                                 List<YangInclude> list) {
        YangSchemaNode schemaNode = null;
        for (YangInclude l : list) {
            try {
                schemaNode = l.getIncludedNode().getChildSchema(id)
                        .getSchemaNode();
            } catch (DataModelException e) {
                log.error("failed to get child schema", e);
            }
        }
        return schemaNode;
    }
}
