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
import org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus;
import org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import static org.onosproject.yang.compiler.datamodel.exceptions.ErrorMessages.getErrorMsg;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.INTRA_FILE_RESOLVED;
import static org.onosproject.yang.compiler.datamodel.utils.ResolvableStatus.RESOLVED;
import static org.onosproject.yang.compiler.datamodel.utils.RestrictionResolver.processLengthRes;
import static org.onosproject.yang.compiler.datamodel.utils.RestrictionResolver.processRangeRestriction;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypeUtils.isOfRangeRestrictedType;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.BINARY;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DECIMAL64;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.DERIVED;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.IDENTITYREF;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.LEAFREF;
import static org.onosproject.yang.compiler.datamodel.utils.builtindatatype.YangDataTypes.STRING;

/**
 * Represents the derived information.
 *
 * @param <T> extended information.
 */
public class YangDerivedInfo<T> extends DefaultLocationInfo
        implements Cloneable, Serializable {

    private static final long serialVersionUID = 806201641L;

    /**
     * YANG typedef reference.
     */
    private YangTypeDef referredTypeDef;

    /**
     * Resolved additional information about data type after linking, example
     * restriction info, named values, etc. The extra information is based
     * on the data type. Based on the data type, the extended info can vary.
     */
    private T resolvedExtendedInfo;

    /**
     * Effective built-in type, required in case type of typedef is again a
     * derived type. This information is to be added during linking.
     */
    private YangDataTypes effectiveBuiltInType;

    /**
     * Length restriction string to store the length restriction when the type
     * is derived.
     */
    private YangRangeRestriction lengthRes;

    /**
     * Range restriction string to store the range restriction when the type
     * is derived.
     */
    private YangRangeRestriction rangeRes;

    /**
     * Pattern restriction list to store the pattern restriction when the
     * type is derived.
     */
    private List<YangPatternRestriction> patternResList;

    /**
     * Returns the referred typedef reference.
     *
     * @return referred typedef reference
     */
    public YangTypeDef getReferredTypeDef() {
        return referredTypeDef;
    }

    /**
     * Sets the referred typedef reference.
     *
     * @param referredTypeDef referred typedef reference
     */
    public void setReferredTypeDef(YangTypeDef referredTypeDef) {
        this.referredTypeDef = referredTypeDef;
    }

    /**
     * Returns resolved extended information after successful linking.
     *
     * @return resolved extended information
     */
    public T getResolvedExtendedInfo() {
        return resolvedExtendedInfo;
    }

    /**
     * Returns the length restriction.
     *
     * @return length restriction
     */
    public YangRangeRestriction getLengthRes() {
        return lengthRes;
    }

    /**
     * Sets the length restriction.
     *
     * @param lr length restriction
     */
    public void setLengthRes(YangRangeRestriction lr) {
        lengthRes = lr;
    }

    /**
     * Returns the range restriction.
     *
     * @return range restriction
     */
    public YangRangeRestriction getRangeRes() {
        return rangeRes;
    }

    /**
     * Sets the range restriction.
     *
     * @param rr range restriction
     */
    public void setRangeRes(YangRangeRestriction rr) {
        rangeRes = rr;
    }

    /**
     * Returns the pattern restriction list.
     *
     * @return pattern restriction list
     */
    public List<YangPatternRestriction> getPatternResList() {
        return patternResList;
    }

    /**
     * Adds the pattern to the pattern restriction list.
     *
     * @param pr pattern restriction
     */
    public void addPatternRes(YangPatternRestriction pr) {
        if (patternResList == null) {
            patternResList = new LinkedList<>();
        }
        patternResList.add(pr);
    }

    /**
     * Returns effective built-in type.
     *
     * @return effective built-in type
     */
    public YangDataTypes getEffectiveBuiltInType() {
        return effectiveBuiltInType;
    }

    /**
     * Resolves the type derived info, by obtaining the effective built-in type
     * and resolving the restrictions.
     *
     * @return resolution status
     * @throws DataModelException a violation in data mode rule
     */
    public ResolvableStatus resolve() throws DataModelException {
        YangType<?> baseType = getReferredTypeDef().getTypeDefBaseType();
        YangDataTypes type = baseType.getDataType();
        T extended = (T) baseType.getDataTypeExtendedInfo();

        /*
         * Checks the data type of the referred typedef, if it's derived, obtain
         * effective built-in type and restrictions from it's derived info,
         * otherwise take from the base type of type itself.
         */
        if (type == DERIVED) {
            ResolvableStatus resolvableStatus = resolveTypeDerivedInfo(baseType);
            if (resolvableStatus != null) {
                return resolvableStatus;
            }
        } else if (type == LEAFREF || type == IDENTITYREF) {
            effectiveBuiltInType = type;
            return RESOLVED;
        } else {
            effectiveBuiltInType = type;
            /*
             * Check whether the effective built-in type can have range
             * restrictions, if yes call resolution of range.
             */
            if (isOfRangeRestrictedType(effectiveBuiltInType)) {
                return getResolveStatusForRangeRestrictionType(extended);
            } else if (effectiveBuiltInType == STRING) {
                return getResolveStatusForString(extended);
            } else if (effectiveBuiltInType == BINARY) {
                return getResolveStatusForBinary(extended);
            } else if (effectiveBuiltInType == DECIMAL64) {
                return getResolveStatusForDecimal64(extended);
            }
        }

        /*
         * Check if the data type is the one which can't be restricted, in this
         * case check whether no self restrictions should be present.
         */
        if (effectiveBuiltInType.isNonRestrictedType()) {
            if (lengthRes == null && rangeRes == null && (
                    patternResList == null || patternResList.isEmpty())) {
                return RESOLVED;
            } else {
                throw new DataModelException(getErrorMsg(
                        "YANG file error: Restrictions can't be applied to a " +
                                "given type ", "type.", getLineNumber(),
                        getCharPosition(), getFileName() + "\""));
            }
        }
        // Throw exception for unsupported types
        throw new DataModelException(getErrorMsg(
                "Linker error: Unable to process the derived type. ", "type.",
                getLineNumber(), getCharPosition(), getFileName() + "\""));
    }

    //Returns resolve status for range restrictions.
    private ResolvableStatus getResolveStatusForRangeRestrictionType(T extended)
            throws DataModelException {
        if (extended == null) {
            resolveRangeRestriction(null);
                    /*
                     * Return the resolution status as resolved, if it's not
                     * resolve range/string restriction will throw exception in
                     * previous function.
                     */
            return RESOLVED;
        } else {
            if (!(extended instanceof YangRangeRestriction)) {
                throwError();
            }
            resolveRangeRestriction((YangRangeRestriction) extended);
                    /*
                     * Return the resolution status as resolved, if it's not
                     * resolve range/string restriction will throw exception in
                     * previous function.
                     */
            return RESOLVED;
        }
    }

    //Returns resolve status for string.
    private ResolvableStatus getResolveStatusForString(T extended)
            throws DataModelException {
        if (extended == null) {
            resolveStringRestriction(null);
                    /*
                     * Return the resolution status as resolved, if it's not
                     * resolve range/string restriction will throw exception in
                     * previous function.
                     */
            return RESOLVED;
        } else {
            if (!(extended instanceof YangStringRestriction)) {
                throwError();
            }
            resolveStringRestriction((YangStringRestriction) extended);
                    /*
                     * Return the resolution status as resolved, if it's not
                     * resolve range/string restriction will throw exception in
                     * previous function.
                     */
            return RESOLVED;
        }
    }

    //Returns resolve status for binary type.
    private ResolvableStatus getResolveStatusForBinary(T extended)
            throws DataModelException {
        if (extended == null) {
            resolveBinaryRestriction(null);
                    /*
                     * Return the resolution status as resolved, if it's not
                     * resolve length restriction will throw exception in
                     * previous function.
                     */
            return RESOLVED;
        } else {
            if (!(extended instanceof YangRangeRestriction)) {
                throwError();
            }
            resolveBinaryRestriction((YangRangeRestriction) extended);
                    /*
                     * Return the resolution status as resolved, if it's not
                     * resolve length restriction will throw exception in
                     * previous function.
                     */
            return RESOLVED;
        }
    }

    //Returns resolve status for decimal64 type.
    private ResolvableStatus getResolveStatusForDecimal64(T extended)
            throws DataModelException {
        if (extended != null) {
            if (((YangDecimal64) extended).getRangeRestrictedExtendedInfo() == null) {
                resolveRangeRestriction(null);
                        /*
                         * Return the resolution status as resolved, if it's not;
                         * resolve range restriction will throw exception in
                         * previous function.
                         */
                return RESOLVED;
            } else {
                if (!(((YangDecimal64) extended)
                        .getRangeRestrictedExtendedInfo() instanceof YangRangeRestriction)) {
                    throwError();
                }
                resolveRangeRestriction((YangRangeRestriction) (
                        (YangDecimal64) extended).getRangeRestrictedExtendedInfo());
                        /*
                         * Return the resolution status as resolved, if it's not
                         * resolve range/string restriction will throw exception in
                         * previous function.
                         */
                return RESOLVED;
            }

        } else {
            throw new DataModelException(getErrorMsg(
                    "Linker error: Unable to find type extended info " +
                            "for decimal64.", "type.", getLineNumber(),
                    getCharPosition(), getFileName() + "\""));
        }
    }

    private void throwError() throws DataModelException {
        throw new DataModelException(getErrorMsg(
                "Linker error: Referred typedef restriction info is of invalid ",
                "type.", getLineNumber(), getCharPosition(), getFileName() + "\""));
    }

    /**
     * Resolves the type derived info, by obtaining the effective built-in type
     * and resolving the restrictions.
     *
     * @param baseType base type of typedef
     * @return resolution status
     * @throws DataModelException a violation in data mode rule
     */
    private ResolvableStatus resolveTypeDerivedInfo(YangType<?> baseType)
            throws DataModelException {

        //Check whether the referred typedef is resolved.
        if (baseType.getResolvableStatus() != INTRA_FILE_RESOLVED &&
                baseType.getResolvableStatus() != RESOLVED) {
            throwError();
        }

        /*
         * Check if the referred typedef is intra file resolved, if yes sets
         * current status also to intra file resolved .
         */
        if (getReferredTypeDef().getTypeDefBaseType()
                .getResolvableStatus() == INTRA_FILE_RESOLVED) {
            return INTRA_FILE_RESOLVED;
        }
        effectiveBuiltInType = ((YangDerivedInfo<?>) baseType
                .getDataTypeExtendedInfo()).getEffectiveBuiltInType();
        YangDerivedInfo refDerivedInfo = (YangDerivedInfo<?>) baseType.getDataTypeExtendedInfo();
        T extendedInfo = (T) refDerivedInfo.getResolvedExtendedInfo();
        /*
         * Check whether the effective built-in type can have range
         * restrictions, if yes call resolution of range.
         */
        if (isOfRangeRestrictedType(effectiveBuiltInType)) {
            return getResolveStatusForRangeRestrictionType(extendedInfo);
        } else if (effectiveBuiltInType == STRING) {
            return getResolveStatusForString(extendedInfo);
        } else if (effectiveBuiltInType == BINARY) {
            return getResolveStatusForBinary(extendedInfo);
        } else if (effectiveBuiltInType == DECIMAL64) {
            if (extendedInfo == null) {
                resolveRangeRestriction(null);
                 /*
                  * Return the resolution status as resolved, if it's not;
                  * resolve range restriction will throw exception in
                  * previous function.
                  */
                return RESOLVED;
            } else {
                if (!(extendedInfo instanceof YangRangeRestriction)) {
                    throwError();
                }
                resolveRangeRestriction((YangRangeRestriction) extendedInfo);
                /*
                 * Return the resolution status as resolved, if it's not
                 * resolve range/string restriction will throw exception in
                 * previous function.
                 */
                return RESOLVED;
            }
        }
        return null;
    }

    /**
     * Resolves the string restrictions.
     *
     * @param refSr referred string restriction of typedef
     * @throws DataModelException a violation in data model rule
     */
    private void resolveStringRestriction(YangStringRestriction refSr)
            throws DataModelException {
        YangStringRestriction curSr = null;
        YangRangeRestriction refRr = null;
        List<YangPatternRestriction> refPr = null;

        /*
         * Check that range restriction should be null when built-in type is
         * string.
         */
        if (rangeRes != null) {
            throw new DataModelException(getErrorMsg(
                    "YANG file error: Range restriction should't be present for" +
                            " string data type.", ".", getLineNumber(),
                    getCharPosition(), getFileName()));
        }

        /*
         * If referred restriction and self restriction both are null, no
         * resolution is required.
         */
        if (refSr == null && lengthRes == null && (patternResList == null ||
                patternResList.isEmpty())) {
            return;
        }

        /*
         * If referred string restriction is not null, take value of length and
         * pattern restriction and assign.
         */
        if (refSr != null) {
            refRr = refSr.getLengthRestriction();
            refPr = refSr.getPatternResList();
        }

        YangRangeRestriction lr = resolveLengthRestriction(refRr);
        List<YangPatternRestriction> pr = resolvePatternRestriction(refPr);

        /*
         * Check if either of length or pattern restriction is present, if yes
         * create string restriction and assign value.
         */
        if (lr != null || pr != null) {
            curSr = new YangStringRestriction();
            curSr.setCharPosition(getCharPosition());
            curSr.setFileName(getFileName());
            curSr.setLineNumber(getLineNumber());
            curSr.setLengthRestriction(lr);
            curSr.setPatternResList(pr);
        }
        resolvedExtendedInfo = (T) curSr;
    }

    /**
     * Resolves the binary restrictions.
     *
     * @param refLr referred length restriction of typedef
     * @throws DataModelException a violation in data model rule
     */
    private void resolveBinaryRestriction(YangRangeRestriction refLr)
            throws DataModelException {

        if (rangeRes != null || !(patternResList == null ||
                patternResList.isEmpty())) {
            throw new DataModelException(getErrorMsg(
                    "YANG file error: for binary range restriction or " +
                            "pattern restriction is not allowed.", "type.",
                    getLineNumber(), getCharPosition(), getFileName()));
        }

        // Get the final resolved length restriction
        YangRangeRestriction lr = resolveLengthRestriction(refLr);
        // Set the length restriction.
        resolvedExtendedInfo = (T) lr;
    }

    /**
     * Resolves pattern restriction.
     *
     * @param refPr referred pattern restriction of typedef
     * @return resolved pattern restriction
     */
    private List<YangPatternRestriction> resolvePatternRestriction(List<YangPatternRestriction> refPr) {
        /*
         * If referred restriction and self restriction both are null, no
         * resolution is required.
         */
        if ((refPr == null || refPr.isEmpty()) && (patternResList == null ||
                patternResList.isEmpty())) {
            return null;
        }

        /*
         * If self restriction is null, and referred restriction is present
         * shallow copy the referred to self.
         */
        if (patternResList == null || patternResList.isEmpty()) {
            return refPr;
        }

        /*
         * If referred restriction is null, and self restriction is present
         * carry out self resolution.
         */
        if (refPr == null || refPr.isEmpty()) {
            return patternResList;
        }

        /*
         * Get patterns of referred type and add it to current pattern
         * restrictions.
         */
        for (YangPatternRestriction pattern : refPr) {
            addPatternRes(pattern);
        }
        return patternResList;
    }

    /**
     * Resolves the length restrictions.
     *
     * @param refLr referred length restriction of typedef
     * @return resolved length restriction
     * @throws DataModelException a violation in data model rule
     */
    private YangRangeRestriction resolveLengthRestriction(YangRangeRestriction refLr)
            throws DataModelException {

        /*
         * If referred restriction and self restriction both are null, no
         * resolution is required.
         */
        if (refLr == null && lengthRes == null) {
            return null;
        }

        /*
         * If self restriction is null, and referred restriction is present
         * shallow copy the referred to self.
         */
        if (lengthRes == null) {
            return refLr;
        }

        /*
         * If referred restriction is null, and self restriction is present
         * carry out self resolution.
         */
        if (refLr == null) {
            return processLengthRes(null, getLineNumber(), getCharPosition(),
                                    false, lengthRes, getFileName());
        }

        /*
         * Carry out self resolution based with obtained effective built-in type
         * and MIN/MAX values as per the referred typedef's values.
         */
        YangRangeRestriction curLr = processLengthRes(
                refLr, getLineNumber(), getCharPosition(), true,
                lengthRes, getFileName());

        // Resolve the range with referred typedef's restriction.
        resolveLengthAndRangeRestriction(refLr, curLr);
        return curLr;
    }

    /**
     * Resolves the length/range self and referred restriction, to check whether
     * the all the range interval in self restriction is stricter than the
     * referred typedef's restriction.
     *
     * @param refRestriction referred restriction
     * @param curRestriction self restriction
     */
    private void resolveLengthAndRangeRestriction(YangRangeRestriction refRestriction,
                                                  YangRangeRestriction curRestriction)
            throws DataModelException {
        for (Object curInterval : curRestriction.getAscendingRangeIntervals()) {
            if (!(curInterval instanceof YangRangeInterval)) {
                throw new DataModelException(getErrorMsg(
                        "Linker error: Current range intervals not processed correctly.",
                        "type.", getLineNumber(), getCharPosition(), getFileName()));
            }
            try {
                refRestriction.isValidInterval((YangRangeInterval)
                                                       curInterval, getFileName());
            } catch (DataModelException e) {
                throw new DataModelException(getErrorMsg(
                        e.getMessage(), "type.", getLineNumber(), getCharPosition(),
                        getFileName()));
            }
        }
    }

    /**
     * Resolves the range restrictions.
     *
     * @param refRangeRestriction referred range restriction of typedef
     * @throws DataModelException a violation in data model rule
     */
    private void resolveRangeRestriction(YangRangeRestriction refRangeRestriction)
            throws DataModelException {

        /*
         * Check that string restriction should be null when built-in type is of
         * range type.
         */
        if (lengthRes != null || !(patternResList == null ||
                patternResList.isEmpty())) {
            throw new DataModelException(getErrorMsg(
                    "YANG file error: Length/Pattern restriction should't be " +
                            "present for int/uint/decimal data type.", "type.",
                    getLineNumber(), getCharPosition(), getFileName()));
        }

        /*
         * If referred restriction and self restriction both are null, no
         * resolution is required.
         */
        if (refRangeRestriction == null && rangeRes == null) {
            return;
        }

        /*
         * If self restriction is null, and referred restriction is present
         * shallow copy the referred to self.
         */
        if (rangeRes == null) {
            resolvedExtendedInfo = (T) refRangeRestriction;
            return;
        }

        /*
         * If referred restriction is null, and self restriction is present
         * carry out self resolution.
         */
        if (refRangeRestriction == null) {
            YangRangeRestriction curRangeRestriction =
                    processRangeRestriction(null, getLineNumber(),
                                            getCharPosition(), false, rangeRes,
                                            effectiveBuiltInType, getFileName());
            resolvedExtendedInfo = (T) curRangeRestriction;
            return;
        }

        /*
         * Carry out self resolution based with obtained effective built-in type
         * and MIN/MAX values as per the referred typedef's values.
         */
        YangRangeRestriction curRangeRestriction =
                processRangeRestriction(refRangeRestriction, getLineNumber(),
                                        getCharPosition(), true, rangeRes,
                                        effectiveBuiltInType, getFileName());

        // Resolve the range with referred typedef's restriction.
        resolveLengthAndRangeRestriction(refRangeRestriction, curRangeRestriction);
        resolvedExtendedInfo = (T) curRangeRestriction;
    }
}
