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

// Generated from GeneratedYang.g4 by ANTLR 4.5

package org.onosproject.yang.compiler.parser.antlrgencode;

import org.antlr.v4.runtime.tree.ParseTreeListener;

import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.*;

/**
 * Represents ANTLR interfaces to be implemented by listener to traverse the parse tree.
 */
public interface GeneratedYangListener extends ParseTreeListener {

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule yangfile.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterYangfile(YangfileContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule yangfile.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitYangfile(YangfileContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule moduleStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterModuleStatement(ModuleStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule moduleStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitModuleStatement(ModuleStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule moduleBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterModuleBody(ModuleBodyContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule moduleBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitModuleBody(ModuleBodyContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule moduleHeaderStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterModuleHeaderStatement(ModuleHeaderStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule moduleHeaderStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitModuleHeaderStatement(ModuleHeaderStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule linkageStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterLinkageStatements(LinkageStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule linkageStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitLinkageStatements(LinkageStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule metaStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterMetaStatements(MetaStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule metaStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitMetaStatements(MetaStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule revisionStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRevisionStatements(RevisionStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule revisionStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRevisionStatements(RevisionStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule bodyStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterBodyStatements(BodyStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule bodyStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitBodyStatements(BodyStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule yangVersionStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterYangVersionStatement(YangVersionStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule yangVersionStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitYangVersionStatement(YangVersionStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule namespaceStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterNamespaceStatement(NamespaceStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule namespaceStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitNamespaceStatement(NamespaceStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule prefixStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterPrefixStatement(PrefixStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule prefixStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitPrefixStatement(PrefixStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule importStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterImportStatement(ImportStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule importStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitImportStatement(ImportStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule importStatementBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterImportStatementBody(ImportStatementBodyContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule importStatementBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitImportStatementBody(ImportStatementBodyContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule revisionDateStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRevisionDateStatement(RevisionDateStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule revisionDateStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRevisionDateStatement(RevisionDateStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule includeStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterIncludeStatement(IncludeStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule includeStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitIncludeStatement(IncludeStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule organizationStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterOrganizationStatement(OrganizationStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule organizationStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitOrganizationStatement(OrganizationStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule contactStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterContactStatement(ContactStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule contactStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitContactStatement(ContactStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule descriptionStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterDescriptionStatement(DescriptionStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule descriptionStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitDescriptionStatement(DescriptionStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule referenceStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterReferenceStatement(ReferenceStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule referenceStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitReferenceStatement(ReferenceStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule revisionStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRevisionStatement(RevisionStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule revisionStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRevisionStatement(RevisionStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule revisionStatementBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRevisionStatementBody(RevisionStatementBodyContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule revisionStatementBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRevisionStatementBody(RevisionStatementBodyContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule subModuleStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterSubModuleStatement(SubModuleStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule subModuleStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitSubModuleStatement(SubModuleStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule submoduleBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterSubmoduleBody(SubmoduleBodyContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule submoduleBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitSubmoduleBody(SubmoduleBodyContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule submoduleHeaderStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterSubmoduleHeaderStatement(SubmoduleHeaderStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule submoduleHeaderStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitSubmoduleHeaderStatement(SubmoduleHeaderStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule belongstoStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterBelongstoStatement(BelongstoStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule belongstoStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitBelongstoStatement(BelongstoStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule belongstoStatementBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterBelongstoStatementBody(BelongstoStatementBodyContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule belongstoStatementBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitBelongstoStatementBody(BelongstoStatementBodyContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule extensionStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterExtensionStatement(ExtensionStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule extensionStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitExtensionStatement(ExtensionStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule extensionBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterExtensionBody(ExtensionBodyContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule extensionBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitExtensionBody(ExtensionBodyContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule argumentStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterArgumentStatement(ArgumentStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule argumentStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitArgumentStatement(ArgumentStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule argumentBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterArgumentBody(ArgumentBodyContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule argumentBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitArgumentBody(ArgumentBodyContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule yinElementStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterYinElementStatement(YinElementStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule yinElementStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitYinElementStatement(YinElementStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule identityStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterIdentityStatement(IdentityStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule identityStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitIdentityStatement(IdentityStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule identityBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterIdentityBody(IdentityBodyContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule identityBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitIdentityBody(IdentityBodyContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule baseStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterBaseStatement(BaseStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule baseStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitBaseStatement(BaseStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule featureStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterFeatureStatement(FeatureStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule featureStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitFeatureStatement(FeatureStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule featureBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterFeatureBody(FeatureBodyContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule featureBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitFeatureBody(FeatureBodyContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule dataDefStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterDataDefStatement(DataDefStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule dataDefStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitDataDefStatement(DataDefStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule ifFeatureStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterIfFeatureStatement(IfFeatureStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule ifFeatureStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitIfFeatureStatement(IfFeatureStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule unitsStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterUnitsStatement(UnitsStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule unitsStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitUnitsStatement(UnitsStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule typedefStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterTypedefStatement(TypedefStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule typedefStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitTypedefStatement(TypedefStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule typeStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterTypeStatement(TypeStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule typeStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitTypeStatement(TypeStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule typeBodyStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterTypeBodyStatements(TypeBodyStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule typeBodyStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitTypeBodyStatements(TypeBodyStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule numericalRestrictions.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterDecimal64Specification(Decimal64SpecificationContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule numericalRestrictions.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitDecimal64Specification(Decimal64SpecificationContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule numericalRestrictions.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterFractionDigitStatement(FractionDigitStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule
     * numericalRestrictions.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitFractionDigitStatement(FractionDigitStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule
     * numericalRestrictions.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterNumericalRestrictions(NumericalRestrictionsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule numericalRestrictions.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitNumericalRestrictions(NumericalRestrictionsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule rangeStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRangeStatement(RangeStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule rangeStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRangeStatement(RangeStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule commonStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterCommonStatements(CommonStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule commonStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitCommonStatements(CommonStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule stringRestrictions.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterStringRestrictions(StringRestrictionsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule stringRestrictions.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitStringRestrictions(StringRestrictionsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule lengthStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterLengthStatement(LengthStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule lengthStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitLengthStatement(LengthStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule patternStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterPatternStatement(PatternStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule patternStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitPatternStatement(PatternStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule defaultStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterDefaultStatement(DefaultStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule defaultStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitDefaultStatement(DefaultStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule enumSpecification.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterEnumSpecification(EnumSpecificationContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule enumSpecification.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitEnumSpecification(EnumSpecificationContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule enumStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterEnumStatement(EnumStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule enumStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitEnumStatement(EnumStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule enumStatementBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterEnumStatementBody(EnumStatementBodyContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule enumStatementBody.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitEnumStatementBody(EnumStatementBodyContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule leafrefSpecification.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterLeafrefSpecification(LeafrefSpecificationContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule leafrefSpecification.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitLeafrefSpecification(LeafrefSpecificationContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule pathStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterPathStatement(PathStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule pathStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitPathStatement(PathStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule requireInstanceStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRequireInstanceStatement(RequireInstanceStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule requireInstanceStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRequireInstanceStatement(RequireInstanceStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule instanceIdentifierSpecification.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterInstanceIdentifierSpecification(
            InstanceIdentifierSpecificationContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule instanceIdentifierSpecification.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitInstanceIdentifierSpecification(InstanceIdentifierSpecificationContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule identityrefSpecification.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterIdentityrefSpecification(IdentityrefSpecificationContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule identityrefSpecification.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitIdentityrefSpecification(IdentityrefSpecificationContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule unionSpecification.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterUnionSpecification(UnionSpecificationContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule unionSpecification.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitUnionSpecification(UnionSpecificationContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule bitsSpecification.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterBitsSpecification(BitsSpecificationContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule bitsSpecification.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitBitsSpecification(BitsSpecificationContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule bitStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterBitStatement(BitStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule bitStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitBitStatement(BitStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule bitBodyStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterBitBodyStatement(BitBodyStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule bitBodyStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitBitBodyStatement(BitBodyStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule positionStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterPositionStatement(PositionStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule positionStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitPositionStatement(PositionStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule statusStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterStatusStatement(StatusStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule statusStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitStatusStatement(StatusStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule configStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterConfigStatement(ConfigStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule configStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitConfigStatement(ConfigStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule mandatoryStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterMandatoryStatement(MandatoryStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule mandatoryStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitMandatoryStatement(MandatoryStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule presenceStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterPresenceStatement(PresenceStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule presenceStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitPresenceStatement(PresenceStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule orderedByStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterOrderedByStatement(OrderedByStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule orderedByStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitOrderedByStatement(OrderedByStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule mustStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterMustStatement(MustStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule mustStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitMustStatement(MustStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule errorMessageStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterErrorMessageStatement(ErrorMessageStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule errorMessageStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitErrorMessageStatement(ErrorMessageStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule errorAppTagStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterErrorAppTagStatement(ErrorAppTagStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule errorAppTagStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitErrorAppTagStatement(ErrorAppTagStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule minElementsStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterMinElementsStatement(MinElementsStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule minElementsStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitMinElementsStatement(MinElementsStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule maxElementsStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterMaxElementsStatement(MaxElementsStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule maxElementsStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitMaxElementsStatement(MaxElementsStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule valueStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterValueStatement(ValueStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule valueStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitValueStatement(ValueStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule groupingStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterGroupingStatement(GroupingStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule groupingStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitGroupingStatement(GroupingStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule containerStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterContainerStatement(ContainerStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule containerStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitContainerStatement(ContainerStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule leafStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterLeafStatement(LeafStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule leafStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitLeafStatement(LeafStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule leafListStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterLeafListStatement(LeafListStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule leafListStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitLeafListStatement(LeafListStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule listStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterListStatement(ListStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule listStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitListStatement(ListStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule keyStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterKeyStatement(KeyStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule keyStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitKeyStatement(KeyStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule uniqueStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterUniqueStatement(UniqueStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule uniqueStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitUniqueStatement(UniqueStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule choiceStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterChoiceStatement(ChoiceStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule choiceStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitChoiceStatement(ChoiceStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule shortCaseStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterShortCaseStatement(ShortCaseStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule shortCaseStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitShortCaseStatement(ShortCaseStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule caseStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterCaseStatement(CaseStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule caseStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitCaseStatement(CaseStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule anyxmlStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterAnyxmlStatement(AnyxmlStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule anyxmlStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitAnyxmlStatement(AnyxmlStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule usesStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterUsesStatement(UsesStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule usesStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitUsesStatement(UsesStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule refineStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRefineStatement(RefineStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule refineStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRefineStatement(RefineStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule refineContainerStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRefineContainerStatements(RefineContainerStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule refineContainerStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRefineContainerStatements(RefineContainerStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule refineLeafStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRefineLeafStatements(RefineLeafStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule refineLeafStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRefineLeafStatements(RefineLeafStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule refineLeafListStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRefineLeafListStatements(RefineLeafListStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule refineLeafListStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRefineLeafListStatements(RefineLeafListStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule refineListStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRefineListStatements(RefineListStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule refineListStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRefineListStatements(RefineListStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule refineChoiceStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRefineChoiceStatements(RefineChoiceStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule refineChoiceStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRefineChoiceStatements(RefineChoiceStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule refineCaseStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRefineCaseStatements(RefineCaseStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule refineCaseStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRefineCaseStatements(RefineCaseStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule refineAnyxmlStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRefineAnyxmlStatements(RefineAnyxmlStatementsContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule refineAnyxmlStatements.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRefineAnyxmlStatements(RefineAnyxmlStatementsContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule augmentStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterAugmentStatement(AugmentStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule augmentStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitAugmentStatement(AugmentStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule whenStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterWhenStatement(WhenStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule whenStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitWhenStatement(WhenStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule rpcStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRpcStatement(RpcStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule rpcStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRpcStatement(RpcStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule inputStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterInputStatement(InputStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule inputStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitInputStatement(InputStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule outputStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterOutputStatement(OutputStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule outputStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitOutputStatement(OutputStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule notificationStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterNotificationStatement(NotificationStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule notificationStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitNotificationStatement(NotificationStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule deviationStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterDeviationStatement(DeviationStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule deviationStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitDeviationStatement(DeviationStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule deviateNotSupportedStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterDeviateNotSupportedStatement(DeviateNotSupportedStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule deviateNotSupportedStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitDeviateNotSupportedStatement(DeviateNotSupportedStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule deviateAddStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterDeviateAddStatement(DeviateAddStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule deviateAddStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitDeviateAddStatement(DeviateAddStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule deviateDeleteStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterDeviateDeleteStatement(DeviateDeleteStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule deviateDeleteStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitDeviateDeleteStatement(DeviateDeleteStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule deviateReplaceStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterDeviateReplaceStatement(DeviateReplaceStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule deviateReplaceStatement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitDeviateReplaceStatement(DeviateReplaceStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule string.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterString(StringContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule string.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitString(StringContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule identifier.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterIdentifier(IdentifierContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule identifier.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitIdentifier(IdentifierContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule version.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterVersion(VersionContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule version.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitVersion(VersionContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule range.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRange(RangeContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule range.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRange(RangeContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule dateArgumentString.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterDateArgumentString(DateArgumentStringContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule dateArgumentString.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitDateArgumentString(DateArgumentStringContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule length.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterLength(LengthContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule length.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitLength(LengthContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule path.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterPath(PathContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule path.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitPath(PathContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule position.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterPosition(PositionContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule position.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitPosition(PositionContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule status.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterStatus(StatusContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule status.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitStatus(StatusContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule config.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterConfig(ConfigContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule config.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitConfig(ConfigContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule mandatory.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterMandatory(MandatoryContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule mandatory.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitMandatory(MandatoryContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule ordered-by.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterOrderedBy(OrderedByContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule ordered-by.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitOrderedBy(OrderedByContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule min elements value.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterMinValue(MinValueContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule min elements value.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitMinValue(MinValueContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule  max elements value.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterMaxValue(MaxValueContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule max elements value.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitMaxValue(MaxValueContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule key.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterKey(KeyContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule key.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitKey(KeyContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule unique.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterUnique(UniqueContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule unique.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitUnique(UniqueContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule refine.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRefine(RefineContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule refine.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRefine(RefineContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule augment.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterAugment(AugmentContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule augment.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitAugment(AugmentContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule augment.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterFraction(FractionContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule augment.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitFraction(FractionContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule deviation.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterDeviation(DeviationContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule deviation.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitDeviation(DeviationContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule deviation.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterValue(ValueContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule deviation.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitValue(ValueContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule yang construct.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterYangConstruct(YangConstructContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule yang construct.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitYangConstruct(YangConstructContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule compiler annotation statement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterCompilerAnnotationStatement(CompilerAnnotationStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule compiler annotation statement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitCompilerAnnotationStatement(CompilerAnnotationStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule compiler annotation body statement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterCompilerAnnotationBodyStatement(CompilerAnnotationBodyStatementContext
                                                      currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule compiler annotation body statement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitCompilerAnnotationBodyStatement(CompilerAnnotationBodyStatementContext
                                                     currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule app data structure statement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterAppDataStructureStatement(AppDataStructureStatementContext
                                                currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule app data structure statement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitAppDataStructureStatement(AppDataStructureStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule app data structure.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterAppDataStructure(AppDataStructureContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule app data strcuture.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitAppDataStructure(AppDataStructureContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule app extended statement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterAppExtendedStatement(AppExtendedStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule app extended statement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitAppExtendedStatement(AppExtendedStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule extended name.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterExtendedName(ExtendedNameContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule extended name.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitExtendedName(ExtendedNameContext currentContext);


    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule
     * data structure key statement.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterDataStructureKeyStatement(DataStructureKeyStatementContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar rule
     * data structure key statement.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitDataStructureKeyStatement(DataStructureKeyStatementContext currentContext);

    /**
     * Enters a parse tree produced by GeneratedYangParser for grammar rule require instance.
     *
     * @param currentContext current context in the parsed tree
     */
    void enterRequireInstance(RequireInstanceContext currentContext);

    /**
     * Exits a parse tree produced by GeneratedYangParser for grammar require instance.
     *
     * @param currentContext current context in the parsed tree
     */
    void exitRequireInstance(RequireInstanceContext currentContext);
}
