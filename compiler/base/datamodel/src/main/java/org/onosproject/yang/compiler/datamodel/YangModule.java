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
package org.onosproject.yang.compiler.datamodel;

import org.onosproject.yang.compiler.datamodel.exceptions.DataModelException;
import org.onosproject.yang.compiler.datamodel.utils.IdentityHandler;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.datamodel.utils.YangConstructType;
import org.onosproject.yang.model.YangNamespace;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.unmodifiableList;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_AUGMENT;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_BASE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_COMPILER_ANNOTATION;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_DERIVED_DATA_TYPE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_DEVIATION;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_IDENTITYREF;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_IF_FEATURE;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_LEAFREF;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_USES;
import static org.onosproject.yang.compiler.datamodel.ResolvableType.YANG_USES_AUGMENT;
import static org.onosproject.yang.compiler.datamodel.YangSchemaNodeType.YANG_SINGLE_INSTANCE_NODE;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.detectCollidingChildUtil;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.linkInterFileReferences;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.resolveLinkingForResolutionList;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.updateMap;
import static org.onosproject.yang.compiler.datamodel.utils.DataModelUtils.validateUniqueInList;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.MODULE_DATA;

/*-
 * Reference:RFC 6020.
 * The "module" statement defines the module's name,
 * and groups all statements that belong to the module together. The "module"
 * statement's argument is the name of the module, followed by a block of
 * sub statements that hold detailed module information.
 * The module's sub statements
 *
 *                +--------------+---------+-------------+-----------------------+
 *                |sub statement | section | cardinality | data model mapping    |
 *                +--------------+---------+-------------+-----------------------+
 *                | anyxml       | 7.10    | 0..n        | not supported         |
 *                | augment      | 7.15    | 0..n        | child nodes           |
 *                | choice       | 7.9     | 0..n        | child nodes           |
 *                | contact      | 7.1.8   | 0..1        | string                |
 *                | container    | 7.5     | 0..n        | child nodes           |
 *                | description  | 7.19.3  | 0..1        | string                |
 *                | deviation    | 7.18.3  | 0..n        | TODO                  |
 *                | extension    | 7.17    | 0..n        | TODO                  |
 *                | feature      | 7.18.1  | 0..n        | TODO                  |
 *                | grouping     | 7.11    | 0..n        | child nodes           |
 *                | identity     | 7.16    | 0..n        | TODO                  |
 *                | import       | 7.1.5   | 0..n        | list of import info   |
 *                | include      | 7.1.6   | 0..n        | list of include info  |
 *                | leaf         | 7.6     | 0..n        | list of leaf info     |
 *                | leaf-list    | 7.7     | 0..n        | list of leaf-list info|
 *                | list         | 7.8     | 0..n        | child nodes           |
 *                | namespace    | 7.1.3   | 1           | string/uri            |
 *                | notification | 7.14    | 0..n        | TODO                  |
 *                | organization | 7.1.7   | 0..1        | string                |
 *                | prefix       | 7.1.4   | 1           | string                |
 *                | reference    | 7.19.4  | 0..1        | string                |
 *                | revision     | 7.1.9   | 0..n        | revision              |
 *                | rpc          | 7.13    | 0..n        | TODO                  |
 *                | typedef      | 7.3     | 0..n        | child nodes           |
 *                | uses         | 7.12    | 0..n        | child nodes           |
 *                | YANG-version | 7.1.2   | 0..1        | int                   |
 *                +--------------+---------+-------------+-----------------------+
 */

/**
 * Represents data model node to maintain information defined in YANG module.
 */
public abstract class YangModule
        extends YangNode
        implements YangLeavesHolder, YangDesc, YangReference, Parsable,
        CollisionDetector, YangReferenceResolver, RpcNotificationContainer,
        YangFeatureHolder, YangIsFilterContentNodes, YangNamespace,
        YangDeviationHolder, YangVersionHolder {

    private static final long serialVersionUID = 806201610L;

    /**
     * Reference:RFC 6020.
     * <p>
     * The "contact" statement provides contact information for the module. The
     * argument is a string that is used to specify contact information for the
     * person or persons to whom technical queries concerning this module should
     * be sent, such as their name, postal address, telephone number, and
     * electronic mail address.
     */
    private String contact;

    /**
     * Reference:RFC 6020.
     * <p>
     * The "description" statement takes as an argument a string that contains a
     * human-readable textual description of this definition. The text is
     * provided in a language (or languages) chosen by the module developer; for
     * the sake of interoperability.
     */
    private String description;

    /**
     * List of YANG modules imported.
     */
    private List<YangImport> importList;

    /**
     * List of YANG sub-modules included.
     */
    private List<YangInclude> includeList;

    /**
     * List of leaves at root level in the module.
     */
    private List<YangLeaf> listOfLeaf;

    /**
     * List of leaf-lists at root level in the module.
     */
    private List<YangLeafList> listOfLeafList;

    /**
     * List of feature at root level in the module.
     */
    private List<YangFeature> listOfFeature;

    /**
     * Reference:RFC 6020.
     * <p>
     * The "organization" statement defines the party responsible for this
     * module. The argument is a string that is used to specify a textual
     * description of the organization(s) under whose auspices this module was
     * developed.
     */
    private String organization;

    /**
     * Prefix to refer to the objects in module.
     */
    private String prefix;

    /**
     * Reference of the module.
     */
    private String reference;

    /**
     * Revision info of the module.
     */
    private YangRevision revision;

    /**
     * YANG version.
     */
    private String version;

    /*-
     * Reference RFC 6020.
     *
     * Nested typedefs and groupings.
     * Typedefs and groupings may appear nested under many YANG statements,
     * allowing these to be lexically scoped by the hierarchy under which
     * they appear.  This allows types and groupings to be defined near
     * where they are used, rather than placing them at the top level of the
     * hierarchy.  The close proximity increases readability.
     *
     * Scoping also allows types to be defined without concern for naming
     * conflicts between types in different submodules.  Type names can be
     * specified without adding leading strings designed to prevent name
     * collisions within large modules.
     *
     * Finally, scoping allows the module author to keep types and groupings
     * private to their module or submodule, preventing their reuse.  Since
     * only top-level types and groupings (i.e., those appearing as
     * sub-statements to a module or submodule statement) can be used outside
     * the module or submodule, the developer has more control over what
     * pieces of their module are presented to the outside world, supporting
     * the need to hide internal information and maintaining a boundary
     * between what is shared with the outside world and what is kept
     * private.
     *
     * Scoped definitions MUST NOT shadow definitions at a higher scope.  A
     * type or grouping cannot be defined if a higher level in the schema
     * hierarchy has a definition with a matching identifier.
     *
     * A reference to an un-prefixed type or grouping, or one which uses the
     * prefix of the current module, is resolved by locating the closest
     * matching "typedef" or "grouping" statement among the immediate
     * sub-statements of each ancestor statement.
     */
    private List<YangResolutionInfo> derivedTypeResolutionList;

    /**
     * Uses resolution list.
     */
    private List<YangResolutionInfo> usesResolutionList;

    /**
     * If-feature resolution list.
     */
    private List<YangResolutionInfo> ifFeatureResolutionList;

    /**
     * LeafRef resolution list.
     */
    private List<YangResolutionInfo> leafRefResolutionList;

    /**
     * Base resolution list.
     */
    private List<YangResolutionInfo> baseResolutionList;

    /**
     * IdentityRef resolution list.
     */
    private List<YangResolutionInfo> identityRefResolutionList;

    /**
     * Augment resolution list.
     */
    private List<YangResolutionInfo> augmentResolutionList;

    /**
     * Compiler annotation list.
     */
    private List<YangResolutionInfo> compilerAnnotationList;

    /**
     * Uses-augment resolution list.
     */
    private List<YangResolutionInfo> usesAugmentList;

    /**
     * Deviation list.
     */
    private List<YangResolutionInfo> deviationList;

    /**
     * Extension list.
     */
    private List<YangExtension> extensionList;

    /**
     * Flag to indicate the presence of notification.
     */
    private boolean isNotificationPresent;

    /**
     * Flag to indicate the presence of notification.
     */
    private boolean isRpcPresent;

    /**
     * Map of notification enum.
     */
    private final Map<String, YangSchemaNode> notificationEnumMap;

    /**
     * List of augments which augmenting to an input in rpc.
     */
    private final List<YangAugment> augments;

    /**
     * Map of typedef or identity with the list of corresponding schema
     * node to find the name conflict.
     */
    private final Map<String, LinkedList<YangNode>> identityTypedefMap;

    /**
     * List of unique identifiers List.
     */
    private List<YangUniqueHolder> uniqueHolderList;

    /**
     * YANG defined namespace.
     */
    private String namespace;

    /**
     * Flag to check whether target node is already cloned.
     */
    private boolean isDeviatedNodeCloned;

    /**
     * Flag to check whether this module is only for deviation.
     */
    private boolean isModuleForDeviation;

    private static final String E_NONDATA =
            "Method called for other then data node";

    /**
     * Creates a YANG node of module type.
     */
    public YangModule() {
        super(YangNodeType.MODULE_NODE, new HashMap<>());
        derivedTypeResolutionList = new LinkedList<>();
        augmentResolutionList = new LinkedList<>();
        usesResolutionList = new LinkedList<>();
        ifFeatureResolutionList = new LinkedList<>();
        leafRefResolutionList = new LinkedList<>();
        baseResolutionList = new LinkedList<>();
        identityRefResolutionList = new LinkedList<>();
        compilerAnnotationList = new LinkedList<>();
        deviationList = new LinkedList<>();
        importList = new LinkedList<>();
        includeList = new LinkedList<>();
        listOfLeaf = new LinkedList<>();
        listOfLeafList = new LinkedList<>();
        extensionList = new LinkedList<>();
        listOfFeature = new LinkedList<>();
        notificationEnumMap = new HashMap<>();
        augments = new LinkedList<>();
        uniqueHolderList = new LinkedList<>();
        usesAugmentList = new LinkedList<>();
        identityTypedefMap = new HashMap<>();
    }

    @Override
    public void addToChildSchemaMap(YangSchemaNodeIdentifier id,
                                    YangSchemaNodeContextInfo context) {
        getYsnContextInfoMap().put(id, context);
    }

    @Override
    public void incrementMandatoryChildCount() {
        // TODO
    }

    @Override
    public void addToDefaultChildMap(YangSchemaNodeIdentifier id,
                                     YangSchemaNode node) {
        // TODO
    }

    @Override
    public YangSchemaNodeType getYangSchemaNodeType() {
        return YANG_SINGLE_INSTANCE_NODE;
    }

    /**
     * Returns the contact details of the module owner.
     *
     * @return the contact details of YANG owner
     */
    public String getContact() {
        return contact;
    }

    /**
     * Sets the contact details of the module owner.
     *
     * @param contact the contact details of YANG owner
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Returns the description of module.
     *
     * @return the description of YANG module
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of module.
     *
     * @param description set the description of YANG module
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the list of imported modules.
     *
     * @return the list of imported modules
     */
    @Override
    public List<YangImport> getImportList() {
        return unmodifiableList(importList);
    }

    /**
     * Adds the imported module information to the import list.
     *
     * @param importedModule module being imported
     */
    @Override
    public void addToImportList(YangImport importedModule) {
        importList.add(importedModule);
    }

    @Override
    public void setImportList(List<YangImport> importList) {
        this.importList = importList;
    }

    /**
     * Returns the list of included sub modules.
     *
     * @return the included list of sub modules
     */
    @Override
    public List<YangInclude> getIncludeList() {
        return unmodifiableList(includeList);
    }

    /**
     * Adds the included sub module information to the include list.
     *
     * @param includeModule submodule being included
     */
    @Override
    public void addToIncludeList(YangInclude includeModule) {
        includeList.add(includeModule);
    }

    @Override
    public void setIncludeList(List<YangInclude> includeList) {
        this.includeList = includeList;
    }

    /**
     * Adds a unique holder to a unique holder list.
     *
     * @param uniqueHolder unique holder
     */
    @Override
    public void addToUniqueHolderList(YangUniqueHolder uniqueHolder) {
        uniqueHolderList.add(uniqueHolder);
    }

    /**
     * Returns the list of leaves in module.
     *
     * @return the list of leaves
     */
    @Override
    public List<YangLeaf> getListOfLeaf() {
        return unmodifiableList(listOfLeaf);
    }

    @Override
    public void setListOfLeaf(List<YangLeaf> leafsList) {
        listOfLeaf = leafsList;
    }

    /**
     * Adds a leaf in module.
     *
     * @param leaf the leaf to be added
     */
    @Override
    public void addLeaf(YangLeaf leaf) {
        listOfLeaf.add(leaf);
    }

    /**
     * Removes a leaf.
     *
     * @param leaf the leaf to be removed
     */
    @Override
    public void removeLeaf(YangLeaf leaf) {
        getListOfLeaf().remove(leaf);
    }

    /**
     * Returns the list of leaf-list from module.
     *
     * @return the list of leaf-list
     */
    @Override
    public List<YangLeafList> getListOfLeafList() {
        return unmodifiableList(listOfLeafList);
    }

    @Override
    public void setListOfLeafList(List<YangLeafList> listOfLeafList) {
        this.listOfLeafList = listOfLeafList;
    }


    /**
     * Adds a leaf-list in module.
     *
     * @param leafList the leaf-list to be added
     */
    @Override
    public void addLeafList(YangLeafList leafList) {
        listOfLeafList.add(leafList);
    }

    /**
     * Removes a leaf-list.
     *
     * @param leafList the leaf-list to be removed
     */
    @Override
    public void removeLeafList(YangLeafList leafList) {
        getListOfLeafList().remove(leafList);
    }

    @Override
    public List<YangFeature> getFeatureList() {
        return unmodifiableList(listOfFeature);
    }

    @Override
    public void addFeatureList(YangFeature feature) {
        listOfFeature.add(feature);
    }

    @Override
    public void setListOfFeature(List<YangFeature> listOfFeature) {
        this.listOfFeature = listOfFeature;
    }

    /**
     * Returns the modules organization.
     *
     * @return the organization
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * Sets the modules organization.
     *
     * @param org the organization to set
     */
    public void setOrganization(String org) {
        organization = org;
    }

    /**
     * Returns the prefix.
     *
     * @return the prefix
     */
    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix.
     *
     * @param prefix the prefix to set
     */
    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void resolveSelfFileLinking(ResolvableType type)
            throws DataModelException {
        // Get the list to be resolved.
        List<YangResolutionInfo> resolutionList = getUnresolvedResolutionList(type);
        // Resolve linking for a resolution list.
        resolveLinkingForResolutionList(resolutionList, this);
    }

    @Override
    public void resolveInterFileLinking(ResolvableType type)
            throws DataModelException {
        // Get the list to be resolved.
        List<YangResolutionInfo> resolutionList = getUnresolvedResolutionList(type);
        // Resolve linking for a resolution list.
        linkInterFileReferences(resolutionList, this);
    }

    /**
     * Returns the textual reference.
     *
     * @return the reference
     */
    @Override
    public String getReference() {
        return reference;
    }

    /**
     * Sets the textual reference.
     *
     * @param reference the reference to set
     */
    @Override
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Returns the revision.
     *
     * @return the revision
     */
    public YangRevision getRevision() {
        return revision;
    }

    /**
     * Sets the revision.
     *
     * @param revision the revision to set
     */
    public void setRevision(YangRevision revision) {
        this.revision = revision;
    }

    /**
     * Returns the version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Adds extension in extension list.
     *
     * @param extension the extension to be added
     */
    public void addExtension(YangExtension extension) {
        extensionList.add(extension);
    }

    /**
     * Returns the extension list.
     *
     * @return the extension list
     */
    public List<YangExtension> getExtensionList() {
        return unmodifiableList(extensionList);
    }

    /**
     * Sets the extension list.
     *
     * @param extensionList the list of extension
     */
    protected void setExtensionList(List<YangExtension> extensionList) {
        this.extensionList = extensionList;
    }

    /**
     * Adds to extension list.
     *
     * @param extension YANG extension
     */
    public void addToExtensionList(YangExtension extension) {
        extensionList.add(extension);
    }

    /**
     * Validates each yang unique holder in the unique holder list.
     *
     * @throws DataModelException a violation of data model rules
     */
    public void resolveUniqueLinking() throws DataModelException {
        for (YangUniqueHolder holder : uniqueHolderList) {
            validateUniqueInList(holder);
        }
    }

    /**
     * Adds all the derived identities to the base identity.
     *
     * @throws DataModelException a violation of data model rules
     */
    public void resolveIdentityExtendList() throws DataModelException {
        for (YangResolutionInfo base : baseResolutionList) {
           new IdentityHandler((YangBase) base.getEntityToResolveInfo()
                   .getEntityToResolve());
        }
    }

    /**
     * Returns the type of the parsed data.
     *
     * @return returns MODULE_DATA
     */
    @Override
    public YangConstructType getYangConstructType() {
        return MODULE_DATA;
    }

    /**
     * Validates the data on entering the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnEntry()
            throws DataModelException {
        /*
         * Module is root in the data model tree, hence there is no entry
         * validation
         */
    }

    /**
     * Validates the data on exiting the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnExit()
            throws DataModelException {
        /*
         * TODO: perform symbol linking for the imported or included YANG info.
         * TODO: perform symbol resolution for referred YANG entities.
         */
    }

    @Override
    public void detectCollidingChild(String identifierName,
                                     YangConstructType dataType)
            throws DataModelException {
        // Asks helper to detect colliding child.
        detectCollidingChildUtil(identifierName, dataType, this);
    }

    @Override
    public void detectSelfCollision(String identifierName,
                                    YangConstructType dataType)
            throws DataModelException {
        // Not required as module doesn't have any parent.
    }

    @Override
    public List<YangResolutionInfo> getUnresolvedResolutionList(ResolvableType type) {
        if (type == YANG_DERIVED_DATA_TYPE) {
            return unmodifiableList(derivedTypeResolutionList);
        } else if (type == YANG_USES) {
            return unmodifiableList(usesResolutionList);
        } else if (type == YANG_AUGMENT) {
            return unmodifiableList(augmentResolutionList);
        } else if (type == YANG_IF_FEATURE) {
            return unmodifiableList(ifFeatureResolutionList);
        } else if (type == YANG_LEAFREF) {
            return unmodifiableList(leafRefResolutionList);
        } else if (type == YANG_BASE) {
            return unmodifiableList(baseResolutionList);
        } else if (type == YANG_IDENTITYREF) {
            return unmodifiableList(identityRefResolutionList);
        } else if (type == YANG_COMPILER_ANNOTATION) {
            return unmodifiableList(compilerAnnotationList);
        } else if (type == YANG_DEVIATION) {
            return unmodifiableList(deviationList);
        } else {
            return unmodifiableList(usesAugmentList);
        }
    }

    @Override
    public void addToResolutionList(YangResolutionInfo info,
                                    ResolvableType type) {
        if (type == YANG_DERIVED_DATA_TYPE) {
            derivedTypeResolutionList.add(info);
        } else if (type == YANG_USES) {
            usesResolutionList.add(info);
        } else if (type == YANG_IF_FEATURE) {
            ifFeatureResolutionList.add(info);
        } else if (type == YANG_LEAFREF) {
            leafRefResolutionList.add(info);
        } else if (type == YANG_BASE) {
            baseResolutionList.add(info);
        } else if (type == YANG_AUGMENT) {
            augmentResolutionList.add(info);
        } else if (type == YANG_IDENTITYREF) {
            identityRefResolutionList.add(info);
        } else if (type == YANG_COMPILER_ANNOTATION) {
            compilerAnnotationList.add(info);
        } else if (type == YANG_DEVIATION) {
            deviationList.add(info);
        } else if (type == YANG_USES_AUGMENT) {
            usesAugmentList.add(info);
        }
    }

    @Override
    public void setResolutionList(List<YangResolutionInfo> resolutionList,
                                  ResolvableType type) {
        if (type == YANG_DERIVED_DATA_TYPE) {
            derivedTypeResolutionList = resolutionList;
        } else if (type == YANG_USES) {
            usesResolutionList = resolutionList;
        } else if (type == YANG_IF_FEATURE) {
            ifFeatureResolutionList.add((YangResolutionInfo) resolutionList);
        } else if (type == YANG_LEAFREF) {
            leafRefResolutionList = resolutionList;
        } else if (type == YANG_BASE) {
            baseResolutionList = resolutionList;
        } else if (type == YANG_AUGMENT) {
            augmentResolutionList = resolutionList;
        } else if (type == YANG_IDENTITYREF) {
            identityRefResolutionList = resolutionList;
        } else if (type == YANG_COMPILER_ANNOTATION) {
            compilerAnnotationList = resolutionList;
        } else if (type == YANG_DEVIATION) {
            deviationList = resolutionList;
        } else if (type == YANG_USES_AUGMENT) {
            usesAugmentList = resolutionList;
        }
    }

    @Override
    public void addReferencesToImportList(Set<YangNode> yangNodeSet)
            throws DataModelException {
        // Run through the imported list to add references.
        for (YangImport yangImport : getImportList()) {
            yangImport.addReferenceToImport(yangNodeSet);
        }
    }

    @Override
    public void addReferencesToIncludeList(Set<YangNode> yangNodeSet)
            throws DataModelException {
        // Run through the included list to add references.
        for (YangInclude yangInclude : getIncludeList()) {
            YangSubModule subModule = yangInclude
                    .addReferenceToInclude(yangNodeSet);

            // Check if the referred sub-modules parent is self
            if (!subModule.getBelongsTo().getModuleNode().equals(this)) {
                yangInclude.reportIncludeError();
            }
        }
    }

    @Override
    public void setLeafNameSpaceAndAddToParentSchemaMap() {
        // Add namespace for all leafs.
        for (YangLeaf yangLeaf : getListOfLeaf()) {
            yangLeaf.setLeafNameSpaceAndAddToParentSchemaMap(getNameSpace());
        }
        // Add namespace for all leaf list.
        for (YangLeafList yangLeafList : getListOfLeafList()) {
            yangLeafList.setLeafNameSpaceAndAddToParentSchemaMap(getNameSpace());
        }
    }

    @Override
    public void setLeafParentContext() {
        throw new IllegalArgumentException(E_NONDATA);
    }

    @Override
    public boolean isNotificationPresent() {
        return isNotificationPresent;
    }

    @Override
    public boolean isRpcPresent() throws DataModelException {
        return isRpcPresent;
    }

    @Override
    public void setNotificationPresenceFlag(boolean notificationPresent) {
        isNotificationPresent = notificationPresent;
    }

    @Override
    public void addToNotificationEnumMap(String enumName,
                                         YangSchemaNode notification) {
        notificationEnumMap.put(enumName, notification);
    }

    @Override
    public YangSchemaNode getNotificationSchemaNode(String enumName) {
        return notificationEnumMap.get(enumName);
    }

    /**
     * Adds to augment list.
     *
     * @param augment augment which is augmenting input
     */
    public void addToAugmentList(YangAugment augment) {
        augments.add(augment);
    }

    /**
     * Returns augmented list.
     *
     * @return augmented list
     */
    public List<YangAugment> getAugmentList() {
        return unmodifiableList(augments);
    }

    /**
     * Returns a list of uniqueholders.
     *
     * @return unique holder list
     */
    public List<YangUniqueHolder> getUniqueHolderList() {
        return uniqueHolderList;
    }

    @Override
    public String getModuleNamespace() {
        return namespace;
    }

    @Override
    public String getModuleName() {
        return getName();
    }

    public void setModuleNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public boolean isDeviatedNodeCloned() {
        return isDeviatedNodeCloned;
    }

    @Override
    public void setDeviatedNodeCloned(boolean deviatedNodeCloned) {
        this.isDeviatedNodeCloned = deviatedNodeCloned;
    }

    @Override
    public boolean isModuleForDeviation() {
        return isModuleForDeviation;
    }

    @Override
    public void setModuleForDeviation(boolean moduleForDeviation) {
        isModuleForDeviation = moduleForDeviation;
    }

    @Override
    public void addToIdentityTypedefMap(String name, YangNode node)
            throws DataModelException {
        if (node instanceof ConflictResolveNode) {
            updateMap(name, node, identityTypedefMap);
        }
    }

    /**
     * Sets true if rpc is present.
     *
     * @param rpcPresent true if rpc is present
     */
    public void setRpcPresent(boolean rpcPresent) {
        isRpcPresent = rpcPresent;
    }
}
