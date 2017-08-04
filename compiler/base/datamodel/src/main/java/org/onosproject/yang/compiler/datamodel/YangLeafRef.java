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
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus;
import org.onosproject.yang.compiler.datamodel.utils.YangConstructType;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.INTRA_FILE_RESOLVED;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.RESOLVED;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.LEAFREF_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangErrMsgConstants.DATA_MISSING_ERROR_TAG;
import static org.onosproject.yang.compiler.datamodel.utils.YangErrMsgConstants.ERROR_PATH_LEAFREF_LEAF;
import static org.onosproject.yang.compiler.datamodel.utils.YangErrMsgConstants.INSTANCE_REQUIRED_ERROR_APP_TAG;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.LEAFREF;

/*
 * Reference:RFC 6020.
 * The leafref type is used to reference a particular leaf instance in
 * the data tree.  The "path" substatement (Section 9.9.2) selects a set
 * of leaf instances, and the leafref value space is the set of values
 * of these leaf instances.
 */

/**
 * Represents the leafref information.
 *
 * @param <T> YANG leafref info
 */
public class YangLeafRef<T> extends DefaultLocationInfo
        implements Cloneable, Parsable, Resolvable, Serializable, YangIfFeatureHolder,
        YangXPathResolver, YangAppErrorHolder {

    private static final long serialVersionUID = 286201644L;

    /**
     * YANG data type.
     */
    private YangType effectiveDataType;

    /**
     * Referred leaf/leaf-list for the path specified in the leafref type.
     */
    private T referredLeafOrLeafList;

    /**
     * Path of the leafref.
     */
    private String path;

    /**
     * Leafref path type. Either absolute or relative path.
     */
    private YangPathArgType pathType;

    /**
     * List of atomic paths in absolute Path.
     */
    private List<YangAtomicPath> atomicPath;

    /**
     * YANG relative path.
     */
    private YangRelativePath relativePath;

    /**
     * Status of resolution. If completely resolved enum value is "RESOLVED",
     * if not enum value is "UNRESOLVED", in case reference of grouping/typedef/leafref
     * is added to uses/type/leafref but it's not resolved value of enum should be
     * "INTRA_FILE_RESOLVED".
     */
    private ResolvableStatus resolvableStatus;

    /**
     * Require instance status in leafref type.
     */
    private boolean requireInstance;

    /**
     * List of if-feature.
     */
    private List<YangIfFeature> ifFeatureList;

    /**
     * Parent node of the leafref's leaf.
     */
    private YangNode parentNode;

    /**
     * Prefix in the nodes of the leafref path and its imported node name.
     */
    private Map<String, String> prefixAndNode;

    /**
     * Flag to indicate whether the leaf ref is inside grouping.
     */
    private boolean inGrouping;

    /**
     * Returns the prefix in the leafref path and its imported node name.
     *
     * @return map of prefix with node
     */
    public Map<String, String> getPrefixAndNode() {
        return prefixAndNode;
    }

    /**
     * Sets the prefix in the leaf-ref path and its imported node name.
     *
     * @param prefixAndNode prefix with node map
     */
    public void setPrefixAndNode(Map<String, String> prefixAndNode) {
        this.prefixAndNode = prefixAndNode;
    }

    /**
     * Returns the parent node from the leafref's leaf.
     *
     * @return parent node of the leafref
     */
    public YangNode getParentNode() {
        return parentNode;
    }

    /**
     * Sets the parent node from the leafref's leaf.
     *
     * @param parentNode parent node
     */
    public void setParentNode(YangNode parentNode) {
        this.parentNode = parentNode;
    }

    /**
     * YANG application error information.
     */
    private YangAppErrorInfo yangAppErrorInfo;

    /**
     * Creates a YANG leaf ref.
     */
    public YangLeafRef() {
        yangAppErrorInfo = new YangAppErrorInfo();
        prefixAndNode = new HashMap<>();
        atomicPath = new ArrayList<>();
        ifFeatureList = new ArrayList<>();
        yangAppErrorInfo.setErrorTag(DATA_MISSING_ERROR_TAG);
        yangAppErrorInfo.setErrorAppTag(INSTANCE_REQUIRED_ERROR_APP_TAG);
        yangAppErrorInfo.setErrorAppPath(ERROR_PATH_LEAFREF_LEAF);
    }

    /**
     * Returns the status of the require instance in leafref.
     *
     * @return status of the require instance
     */
    public boolean getRequireInstance() {
        return requireInstance;
    }

    /**
     * Sets the status of the require instance in leafref.
     *
     * @param requireInstance status of the require instance
     */
    public void setRequireInstance(boolean requireInstance) {
        this.requireInstance = requireInstance;
    }

    /**
     * Returns the type of data.
     *
     * @return the data type
     */
    public YangType getEffectiveDataType() {
        return effectiveDataType;
    }

    /**
     * Sets the type of data.
     *
     * @param effectiveDataType data type
     */
    public void setEffectiveDataType(YangType effectiveDataType) {
        this.effectiveDataType = effectiveDataType;
    }

    /**
     * Returns the path of the leafref.
     *
     * @return path of the leafref
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path of the leafref.
     *
     * @param path leafref path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns the type of path in the leafref.
     *
     * @return type of path
     */
    public YangPathArgType getPathType() {
        return pathType;
    }

    /**
     * Sets the type of path in the leafref. It can be either absolute or relative type.
     *
     * @param pathType type of path
     */
    public void setPathType(YangPathArgType pathType) {
        this.pathType = pathType;
    }

    /**
     * Returns the list of atomic path.
     *
     * @return list of atomic path
     */
    public List<YangAtomicPath> getAtomicPath() {
        return unmodifiableList(atomicPath);
    }

    /**
     * Sets the list of atomic path.
     *
     * @param atomicPath list of atomic path.
     */
    public void setAtomicPath(List<YangAtomicPath> atomicPath) {
        this.atomicPath = atomicPath;
    }

    /**
     * Returns the object of relative path.
     *
     * @return object of relative path
     */
    public YangRelativePath getRelativePath() {
        return relativePath;
    }

    /**
     * Sets the object of relative path.
     *
     * @param relativePath object of relative path.
     */
    public void setRelativePath(YangRelativePath relativePath) {
        this.relativePath = relativePath;
    }

    /**
     * Returns the object of referred leaf/leaf-list.
     *
     * @return object of referred leaf/leaf-list
     */
    public T getReferredLeafOrLeafList() {
        return referredLeafOrLeafList;
    }

    /**
     * Sets the object of referred leaf/leaf-list.
     *
     * @param targetExtendedInfo object of referred leaf/leaf-list
     */
    public void setReferredLeafOrLeafList(T targetExtendedInfo) {
        this.referredLeafOrLeafList = targetExtendedInfo;
    }

    @Override
    public List<YangIfFeature> getIfFeatureList() {
        return unmodifiableList(ifFeatureList);
    }

    @Override
    public void addIfFeatureList(YangIfFeature ifFeature) {
        ifFeatureList.add(ifFeature);
    }

    @Override
    public void setIfFeatureList(List<YangIfFeature> ifFeatureList) {
        this.ifFeatureList = ifFeatureList;
    }

    @Override
    public YangConstructType getYangConstructType() {
        return LEAFREF_DATA;
    }

    @Override
    public void validateDataOnEntry()
            throws DataModelException {
        // TODO auto-generated method stub, to be implemented by parser
    }

    @Override
    public void validateDataOnExit()
            throws DataModelException {
        // TODO auto-generated method stub, to be implemented by parser
    }

    @Override
    public ResolvableStatus getResolvableStatus() {
        return resolvableStatus;
    }

    @Override
    public void setResolvableStatus(ResolvableStatus resolvableStatus) {
        this.resolvableStatus = resolvableStatus;
    }

    @Override
    public void setAppErrorInfo(YangAppErrorInfo yangAppErrorInfo) {
        this.yangAppErrorInfo = yangAppErrorInfo;
    }

    @Override
    public YangAppErrorInfo getAppErrorInfo() {
        return yangAppErrorInfo;
    }

    @Override
    public Object resolve()
            throws DataModelException {

        if (getReferredLeafOrLeafList() == null) {
            throw new DataModelException("Linker Error: The leafref does not refer to any leaf/leaf-list." +
                    " in " +
                    getLineNumber() + " at " +
                    getCharPosition() +
                    " in " + getFileName() + "\"");
        }

        // Initiate the resolution
        try {
            setResolvableStatus(getResolution());
        } catch (DataModelException e) {
            throw new DataModelException(e.getMessage());
        }
        return null;
    }

    /**
     * Returns the resolution status by getting the effective built-in type.
     *
     * @return status of resolution
     * @throws DataModelException a violation of data model rules
     */
    private ResolvableStatus getResolution()
            throws DataModelException {

        if (getReferredLeafOrLeafList() instanceof YangLeaf) {
            YangLeaf yangLeaf = ((YangLeaf) getReferredLeafOrLeafList());
            YangType baseType = yangLeaf.getDataType();

            if (baseType.getDataType() == LEAFREF) {
                YangLeafRef referredLeafRefInfo = (YangLeafRef) (yangLeaf.getDataType().getDataTypeExtendedInfo());

                //Check whether the referred typedef is resolved.
                if (referredLeafRefInfo.getResolvableStatus() != INTRA_FILE_RESOLVED
                        && referredLeafRefInfo.getResolvableStatus() != RESOLVED) {
                    throw new DataModelException("Linker Error: Referred typedef is not resolved for type." +
                            " in " +
                            getLineNumber() + " at " +
                            getCharPosition() +
                            " in " + getFileName() + "\"");
                }

                /*
                 * Check if the referred typedef is intra file resolved, if yes
                 * sets current status also to intra file resolved .
                 */
                if ((referredLeafRefInfo.getResolvableStatus() == INTRA_FILE_RESOLVED)) {
                    return INTRA_FILE_RESOLVED;
                }

                // Add the if-feature list from referred leafref to current leafref.
                List<YangIfFeature> referredLeafIfFeatureListFromLeafref = referredLeafRefInfo.getIfFeatureList();
                if (referredLeafIfFeatureListFromLeafref != null && !referredLeafIfFeatureListFromLeafref.isEmpty()) {
                    Iterator<YangIfFeature> referredLeafIfFeature = referredLeafIfFeatureListFromLeafref.iterator();
                    while (referredLeafIfFeature.hasNext()) {
                        YangIfFeature ifFeature = referredLeafIfFeature.next();
                        addIfFeatureList(ifFeature);
                    }
                }
                setEffectiveDataType(referredLeafRefInfo.getEffectiveDataType());
            } else if (baseType.getDataType() == YangDataTypes.DERIVED) {

                // Check whether the referred typedef is resolved.
                if (baseType.getResolvableStatus() != INTRA_FILE_RESOLVED
                        && baseType.getResolvableStatus() != RESOLVED) {
                    throw new DataModelException("Linker Error: Referred typedef is not resolved for type." +
                            " in " +
                            getLineNumber() + " at " +
                            getCharPosition() +
                            " in " + getFileName() + "\"");
                }
                /*
                 * Check if the referred typedef is intra file resolved, if yes
                 * sets current status also to intra file resolved .
                 */
                if ((baseType.getResolvableStatus() == INTRA_FILE_RESOLVED)) {
                    return INTRA_FILE_RESOLVED;
                }
                setEffectiveDataType(baseType);
            } else {
                setEffectiveDataType(baseType);
            }

            // Add the if-feature list from referred leaf to current leafref.
            List<YangIfFeature> referredLeafIfFeatureList = yangLeaf.getIfFeatureList();
            if (referredLeafIfFeatureList != null && !referredLeafIfFeatureList.isEmpty()) {
                Iterator<YangIfFeature> referredLeafIfFeature = referredLeafIfFeatureList.iterator();
                while (referredLeafIfFeature.hasNext()) {
                    YangIfFeature ifFeature = referredLeafIfFeature.next();
                    addIfFeatureList(ifFeature);
                }
            }
            return RESOLVED;
        } else if (getReferredLeafOrLeafList() instanceof YangLeafList) {
            YangLeafList yangLeafList = ((YangLeafList) getReferredLeafOrLeafList());
            YangType baseType = yangLeafList.getDataType();

            if (baseType.getDataType() == LEAFREF) {
                YangLeafRef referredLeafRefInfo = (YangLeafRef) yangLeafList.getDataType().getDataTypeExtendedInfo();

                //Check whether the referred typedef is resolved.
                if (referredLeafRefInfo.getResolvableStatus() != INTRA_FILE_RESOLVED
                        && referredLeafRefInfo.getResolvableStatus() != RESOLVED) {
                    throw new DataModelException("Linker Error: Referred typedef is not resolved for type." +
                            " in " +
                            getLineNumber() + " at " +
                            getCharPosition() +
                            " in " + getFileName() + "\"");
                }
                /*
                 * Check if the referred typedef is intra file resolved, if yes
                 * sets current status also to intra file resolved .
                 */
                if ((referredLeafRefInfo.getResolvableStatus() == INTRA_FILE_RESOLVED)) {
                    return INTRA_FILE_RESOLVED;
                }
                // Add the if-feature list from referred leafref to current leafref.
                List<YangIfFeature> referredLeafListIfFeatureListFromLeafref = referredLeafRefInfo.getIfFeatureList();
                if (referredLeafListIfFeatureListFromLeafref != null
                        && !referredLeafListIfFeatureListFromLeafref.isEmpty()) {
                    Iterator<YangIfFeature> referredLeafListIfFeature = referredLeafListIfFeatureListFromLeafref
                            .iterator();
                    while (referredLeafListIfFeature.hasNext()) {
                        YangIfFeature ifFeature = referredLeafListIfFeature.next();
                        addIfFeatureList(ifFeature);
                    }
                }
                setEffectiveDataType(referredLeafRefInfo.getEffectiveDataType());
            } else if (baseType.getDataType() == YangDataTypes.DERIVED) {

                //Check whether the referred typedef is resolved.
                if (baseType.getResolvableStatus() != INTRA_FILE_RESOLVED
                        && baseType.getResolvableStatus() != RESOLVED) {
                    throw new DataModelException("Linker Error: Referred typedef is not resolved for type." +
                            " in " +
                            getLineNumber() + " at " +
                            getCharPosition() +
                            " in " + getFileName() + "\"");
                }
                /*
                 * Check if the referred typedef is intra file resolved, if yes
                 * sets current status also to intra file resolved .
                 */
                if ((baseType.getResolvableStatus() == INTRA_FILE_RESOLVED)) {
                    return INTRA_FILE_RESOLVED;
                }
                setEffectiveDataType(baseType);
            } else {
                setEffectiveDataType(baseType);
            }
            // Add the if-feature list from referred leaf-list to current leafref.
            List<YangIfFeature> referredLeafListIfFeatureList = yangLeafList.getIfFeatureList();
            if (referredLeafListIfFeatureList != null && !referredLeafListIfFeatureList.isEmpty()) {
                Iterator<YangIfFeature> referredLeafListIfFeature = referredLeafListIfFeatureList.iterator();
                while (referredLeafListIfFeature.hasNext()) {
                    YangIfFeature ifFeature = referredLeafListIfFeature.next();
                    addIfFeatureList(ifFeature);
                }
            }
            return RESOLVED;
        } else {
            throw new DataModelException("Linker Error: The leafref must refer only to leaf/leaf-list." +
                    " in " +
                    getLineNumber() + " at " +
                    getCharPosition() +
                    " in " + getFileName() + "\"");
        }
    }

    @Override
    public YangLeafRef<T> clone()
            throws CloneNotSupportedException {
        YangLeafRef<T> clonedLeafRef = (YangLeafRef<T>) super.clone();
        return clonedLeafRef;
    }

    /**
     * Returns flag value indicating whether leafref is inside grouping.
     *
     * @return true if leafref is in grouping, false otherwise
     */
    public boolean isInGrouping() {
        return inGrouping;
    }

    /**
     * Sets in grouping flag.
     *
     * @param inGrouping flag
     */
    public void setInGrouping(boolean inGrouping) {
        this.inGrouping = inGrouping;
    }
}
