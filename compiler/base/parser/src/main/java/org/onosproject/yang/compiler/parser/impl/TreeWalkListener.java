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

package org.onosproject.yang.compiler.parser.impl;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.onosproject.yang.compiler.datamodel.YangNode;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.datamodel.utils.YangConstructType;
import org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangListener;
import org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.AnydataStatementContext;
import org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.YangStatementContext;
import org.onosproject.yang.compiler.parser.impl.listeners.AnydataListener;
import org.onosproject.yang.compiler.parser.impl.listeners.AppDataStructureListener;
import org.onosproject.yang.compiler.parser.impl.listeners.AppExtendedNameListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ArgumentListener;
import org.onosproject.yang.compiler.parser.impl.listeners.AugmentListener;
import org.onosproject.yang.compiler.parser.impl.listeners.BaseFileListener;
import org.onosproject.yang.compiler.parser.impl.listeners.BaseListener;
import org.onosproject.yang.compiler.parser.impl.listeners.BelongsToListener;
import org.onosproject.yang.compiler.parser.impl.listeners.BitListener;
import org.onosproject.yang.compiler.parser.impl.listeners.BitsListener;
import org.onosproject.yang.compiler.parser.impl.listeners.CaseListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ChoiceListener;
import org.onosproject.yang.compiler.parser.impl.listeners.CompilerAnnotationListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ConfigListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ContactListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ContainerListener;
import org.onosproject.yang.compiler.parser.impl.listeners.DataStructureKeyListener;
import org.onosproject.yang.compiler.parser.impl.listeners.Decimal64Listener;
import org.onosproject.yang.compiler.parser.impl.listeners.DefaultDenyAllExtRefListener;
import org.onosproject.yang.compiler.parser.impl.listeners.DefaultDenyWriteExtRefListener;
import org.onosproject.yang.compiler.parser.impl.listeners.DefaultListener;
import org.onosproject.yang.compiler.parser.impl.listeners.DescriptionListener;
import org.onosproject.yang.compiler.parser.impl.listeners.DeviateAddListener;
import org.onosproject.yang.compiler.parser.impl.listeners.DeviateDeleteListener;
import org.onosproject.yang.compiler.parser.impl.listeners.DeviateReplaceListener;
import org.onosproject.yang.compiler.parser.impl.listeners.DeviationListener;
import org.onosproject.yang.compiler.parser.impl.listeners.EnumListener;
import org.onosproject.yang.compiler.parser.impl.listeners.EnumerationListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ErrorAppTagListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ErrorMessageListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ExtensionListener;
import org.onosproject.yang.compiler.parser.impl.listeners.FeatureListener;
import org.onosproject.yang.compiler.parser.impl.listeners.FractionDigitsListener;
import org.onosproject.yang.compiler.parser.impl.listeners.GroupingListener;
import org.onosproject.yang.compiler.parser.impl.listeners.IdentityListener;
import org.onosproject.yang.compiler.parser.impl.listeners.IdentityRefListener;
import org.onosproject.yang.compiler.parser.impl.listeners.IfFeatureListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ImportListener;
import org.onosproject.yang.compiler.parser.impl.listeners.IncludeListener;
import org.onosproject.yang.compiler.parser.impl.listeners.InputListener;
import org.onosproject.yang.compiler.parser.impl.listeners.KeyListener;
import org.onosproject.yang.compiler.parser.impl.listeners.LeafListListener;
import org.onosproject.yang.compiler.parser.impl.listeners.LeafListener;
import org.onosproject.yang.compiler.parser.impl.listeners.LeafrefListener;
import org.onosproject.yang.compiler.parser.impl.listeners.LengthRestrictionListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ListListener;
import org.onosproject.yang.compiler.parser.impl.listeners.MandatoryListener;
import org.onosproject.yang.compiler.parser.impl.listeners.MaxElementsListener;
import org.onosproject.yang.compiler.parser.impl.listeners.MinElementsListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ModuleListener;
import org.onosproject.yang.compiler.parser.impl.listeners.MustListener;
import org.onosproject.yang.compiler.parser.impl.listeners.NamespaceListener;
import org.onosproject.yang.compiler.parser.impl.listeners.NotificationListener;
import org.onosproject.yang.compiler.parser.impl.listeners.OrganizationListener;
import org.onosproject.yang.compiler.parser.impl.listeners.OutputListener;
import org.onosproject.yang.compiler.parser.impl.listeners.PathListener;
import org.onosproject.yang.compiler.parser.impl.listeners.PatternRestrictionListener;
import org.onosproject.yang.compiler.parser.impl.listeners.PositionListener;
import org.onosproject.yang.compiler.parser.impl.listeners.PrefixListener;
import org.onosproject.yang.compiler.parser.impl.listeners.PresenceListener;
import org.onosproject.yang.compiler.parser.impl.listeners.RangeRestrictionListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ReferenceListener;
import org.onosproject.yang.compiler.parser.impl.listeners.RequireInstanceListener;
import org.onosproject.yang.compiler.parser.impl.listeners.RevisionDateListener;
import org.onosproject.yang.compiler.parser.impl.listeners.RevisionListener;
import org.onosproject.yang.compiler.parser.impl.listeners.RpcListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ShortCaseListener;
import org.onosproject.yang.compiler.parser.impl.listeners.StatusListener;
import org.onosproject.yang.compiler.parser.impl.listeners.SubModuleListener;
import org.onosproject.yang.compiler.parser.impl.listeners.TypeDefListener;
import org.onosproject.yang.compiler.parser.impl.listeners.TypeListener;
import org.onosproject.yang.compiler.parser.impl.listeners.UnionListener;
import org.onosproject.yang.compiler.parser.impl.listeners.UniqueListener;
import org.onosproject.yang.compiler.parser.impl.listeners.UnitsListener;
import org.onosproject.yang.compiler.parser.impl.listeners.UsesListener;
import org.onosproject.yang.compiler.parser.impl.listeners.ValueListener;
import org.onosproject.yang.compiler.parser.impl.listeners.VersionListener;
import org.onosproject.yang.compiler.parser.impl.listeners.WhenListener;

import java.util.Stack;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.REFINE_DATA;
import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.UNKNOWN_STATEMENT;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.AnyxmlStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.AppDataStructureContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.AppDataStructureStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.AppExtendedStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ArgumentBodyContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ArgumentStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.AugmentContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.AugmentStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.BaseStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.BelongstoStatementBodyContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.BelongstoStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.BitBodyStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.BitStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.BitsSpecificationContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.BodyStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.CaseStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ChoiceStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.CommonStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.CompilerAnnotationBodyStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.CompilerAnnotationStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ConfigContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ConfigStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ContactStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ContainerStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DataDefStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DataStructureKeyStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DateArgumentStringContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.Decimal64SpecificationContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DefaultDenyAllStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DefaultDenyWriteStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DefaultStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DescriptionStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DeviateAddStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DeviateDeleteStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DeviateNotSupportedStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DeviateReplaceStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DeviationContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.DeviationStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.EnumSpecificationContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.EnumStatementBodyContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.EnumStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ErrorAppTagStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ErrorMessageStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ExtendedNameContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ExtensionBodyContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ExtensionStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.FeatureBodyContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.FeatureStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.FractionContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.FractionDigitStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.GroupingStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.IdentifierContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.IdentityBodyContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.IdentityStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.IdentityrefSpecificationContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.IfFeatureStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ImportStatementBodyContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ImportStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.IncludeStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.InputStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.InstanceIdentifierSpecificationContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.KeyContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.KeyStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.LeafListStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.LeafStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.LeafrefSpecificationContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.LengthContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.LengthStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.LinkageStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ListStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.MandatoryContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.MandatoryStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.MaxElementsStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.MaxValueContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.MetaStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.MinElementsStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.MinValueContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ModuleBodyContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ModuleHeaderStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ModuleStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.MustStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.NamespaceStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.NotificationStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.NumericalRestrictionsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.OrderedByContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.OrderedByStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.OrganizationStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.OutputStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.PathContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.PathStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.PatternStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.PositionContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.PositionStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.PrefixStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.PresenceStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RangeContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RangeStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ReferenceStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RefineAnyxmlStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RefineCaseStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RefineChoiceStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RefineContainerStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RefineContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RefineLeafListStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RefineLeafStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RefineListStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RefineStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RequireInstanceContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RequireInstanceStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RevisionDateStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RevisionStatementBodyContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RevisionStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RevisionStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.RpcStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ShortCaseStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.StatusContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.StatusStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.StmtEndContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.StmtSepContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.StringContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.StringRestrictionsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.SubModuleStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.SubmoduleBodyContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.SubmoduleHeaderStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.TypeBodyStatementsContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.TypeStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.TypedefStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.UnionSpecificationContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.UniqueContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.UniqueStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.UnitsStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.Unknown2Context;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.UnknownContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.UnknownStatement2Context;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.UnknownStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.UsesStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ValueContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.ValueStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.VersionContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.WhenStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.YangConstructContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.YangVersionStatementContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.YangfileContext;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.YinElementStatementContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.handleUnsupportedYangConstruct;
import static org.onosproject.yang.compiler.utils.UtilConstants.CURRENTLY_UNSUPPORTED;
import static org.onosproject.yang.compiler.utils.UtilConstants.UNSUPPORTED_YANG_CONSTRUCT;

/**
 * Represents ANTLR generates parse-tree. ANTLR generates a parse-tree listener interface that responds to events
 * triggered by the built-in tree walker. The methods in listener are just
 * callbacks. This class implements listener interface and generates the
 * corresponding data model tree.
 */
public class TreeWalkListener implements GeneratedYangListener {

    // List of parsable node entries maintained in stack
    private Stack<Parsable> parsedDataStack = new Stack<>();

    // Parse tree root node
    private YangNode rootNode;

    // YANG file name.
    private String fileName;

    /**
     * Parent depth of grouping count for any node.
     */
    private int groupingDepth;

    /**
     * Parent depth of unsupported yang construct count for any node.
     */
    private int unsupportedYangConstructDepth;

    /**
     * Returns number of unsupported yang constructs parents, by a node, at any level.
     *
     * @return depth of unsupported yang constructs
     */
    public int getUnsupportedYangConstructDepth() {
        return unsupportedYangConstructDepth;
    }

    /**
     * Sets number of unsupported yang constructs by a node at any level.
     */
    private void increaseUnsupportedYangConstructDepth() {
        unsupportedYangConstructDepth++;
    }

    /**
     * Sets number of unsupported yang constructs by a node at any level.
     */
    private void decreaseUnsupportedYangConstructDepth() {
        unsupportedYangConstructDepth--;
    }

    /**
     * Returns number of grouping parents, by a node, at any level.
     *
     * @return depth of grouping
     */
    public int getGroupingDepth() {
        return groupingDepth;
    }

    /**
     * Sets number of grouping parents by a node at any level.
     */
    public void increaseGroupingDepth() {
        groupingDepth++;
    }

    /**
     * Sets number of grouping parents by a node at any level.
     */
    public void decreaseGroupingDepth() {
        groupingDepth--;
    }

    /**
     * Returns stack of parsable data.
     *
     * @return stack of parsable data
     */
    public Stack<Parsable> getParsedDataStack() {
        return parsedDataStack;
    }

    /**
     * Set parsed data stack.
     *
     * @param parsedDataStack stack of parsable data objects
     */
    public void setParsedDataStack(Stack<Parsable> parsedDataStack) {
        this.parsedDataStack = parsedDataStack;
    }

    /**
     * Returns root node.
     *
     * @return rootNode of data model tree
     */
    public YangNode getRootNode() {
        return rootNode;
    }

    /**
     * Set root node.
     *
     * @param rootNode root node of data model tree
     */
    public void setRootNode(YangNode rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * Returns YANG file name.
     *
     * @return YANG file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets YANG file name.
     *
     * @param fileName YANG file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void enterYangfile(YangfileContext ctx) {
        BaseFileListener.processYangFileEntry(this, ctx);
    }

    @Override
    public void exitYangfile(YangfileContext ctx) {
        BaseFileListener.processYangFileExit(this, ctx);
    }

    @Override
    public void enterModuleStatement(ModuleStatementContext ctx) {
        ModuleListener.processModuleEntry(this, ctx);
    }

    @Override
    public void exitModuleStatement(ModuleStatementContext ctx) {
        ModuleListener.processModuleExit(this, ctx);
    }

    @Override
    public void enterModuleBody(ModuleBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void exitModuleBody(ModuleBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void enterModuleHeaderStatement(ModuleHeaderStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void exitModuleHeaderStatement(ModuleHeaderStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterLinkageStatements(LinkageStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitLinkageStatements(LinkageStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterMetaStatements(MetaStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitMetaStatements(MetaStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRevisionStatements(RevisionStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitRevisionStatements(RevisionStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterBodyStatements(BodyStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitBodyStatements(BodyStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterYangVersionStatement(YangVersionStatementContext ctx) {
        VersionListener.processVersionEntry(this, ctx);
    }

    @Override
    public void exitYangVersionStatement(YangVersionStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterNamespaceStatement(NamespaceStatementContext ctx) {
        NamespaceListener.processNamespaceEntry(this, ctx);
    }

    @Override
    public void exitNamespaceStatement(NamespaceStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterPrefixStatement(PrefixStatementContext ctx) {
        PrefixListener.processPrefixEntry(this, ctx);
    }

    @Override
    public void exitPrefixStatement(PrefixStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterImportStatement(ImportStatementContext ctx) {
        ImportListener.processImportEntry(this, ctx);
    }

    @Override
    public void exitImportStatement(ImportStatementContext ctx) {
        ImportListener.processImportExit(this, ctx);
    }

    @Override
    public void enterImportStatementBody(ImportStatementBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void exitImportStatementBody(ImportStatementBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRevisionDateStatement(RevisionDateStatementContext ctx) {
        RevisionDateListener.processRevisionDateEntry(this, ctx);
    }

    @Override
    public void exitRevisionDateStatement(RevisionDateStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterIncludeStatement(IncludeStatementContext ctx) {
        IncludeListener.processIncludeEntry(this, ctx);
    }

    @Override
    public void exitIncludeStatement(IncludeStatementContext ctx) {
        IncludeListener.processIncludeExit(this, ctx);
    }

    @Override
    public void enterOrganizationStatement(OrganizationStatementContext ctx) {
        OrganizationListener.processOrganizationEntry(this, ctx);
    }

    @Override
    public void exitOrganizationStatement(OrganizationStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterContactStatement(ContactStatementContext ctx) {
        ContactListener.processContactEntry(this, ctx);
    }

    @Override
    public void exitContactStatement(ContactStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterDescriptionStatement(DescriptionStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            DescriptionListener.processDescriptionEntry(this, ctx);
        }
    }

    @Override
    public void exitDescriptionStatement(DescriptionStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterReferenceStatement(ReferenceStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            ReferenceListener.processReferenceEntry(this, ctx);
        }
    }

    @Override
    public void exitReferenceStatement(ReferenceStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRevisionStatement(RevisionStatementContext ctx) {
        RevisionListener.processRevisionEntry(this, ctx);
    }

    @Override
    public void exitRevisionStatement(RevisionStatementContext ctx) {
        RevisionListener.processRevisionExit(this, ctx);
    }

    @Override
    public void enterRevisionStatementBody(RevisionStatementBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void exitRevisionStatementBody(RevisionStatementBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void enterSubModuleStatement(SubModuleStatementContext ctx) {
        SubModuleListener.processSubModuleEntry(this, ctx);
    }

    @Override
    public void exitSubModuleStatement(SubModuleStatementContext ctx) {
        SubModuleListener.processSubModuleExit(this, ctx);
    }

    @Override
    public void enterSubmoduleBody(SubmoduleBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void exitSubmoduleBody(SubmoduleBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void enterSubmoduleHeaderStatement(SubmoduleHeaderStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void exitSubmoduleHeaderStatement(SubmoduleHeaderStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterBelongstoStatement(BelongstoStatementContext ctx) {
        BelongsToListener.processBelongsToEntry(this, ctx);
    }

    @Override
    public void exitBelongstoStatement(BelongstoStatementContext ctx) {
        BelongsToListener.processBelongsToExit(this, ctx);
    }

    @Override
    public void enterBelongstoStatementBody(BelongstoStatementBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void exitBelongstoStatementBody(BelongstoStatementBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void enterExtensionStatement(ExtensionStatementContext ctx) {
        ExtensionListener.processExtensionEntry(this, ctx);
    }

    @Override
    public void exitExtensionStatement(ExtensionStatementContext ctx) {
        ExtensionListener.processExtensionExit(this, ctx);
    }

    @Override
    public void enterExtensionBody(ExtensionBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void exitExtensionBody(ExtensionBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void enterArgumentStatement(ArgumentStatementContext ctx) {
        ArgumentListener.processArgumentEntry(this, ctx);
    }

    @Override
    public void exitArgumentStatement(ArgumentStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterArgumentBody(ArgumentBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void exitArgumentBody(ArgumentBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void enterYinElementStatement(YinElementStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void exitYinElementStatement(YinElementStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterIdentityStatement(IdentityStatementContext ctx) {
        IdentityListener.processIdentityEntry(this, ctx);
    }

    @Override
    public void exitIdentityStatement(IdentityStatementContext ctx) {
        IdentityListener.processIdentityExit(this, ctx);
    }

    @Override
    public void enterIdentityBody(IdentityBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void exitIdentityBody(IdentityBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void enterBaseStatement(BaseStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            BaseListener.processBaseEntry(this, ctx);
        }
    }

    @Override
    public void exitBaseStatement(BaseStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterFeatureStatement(FeatureStatementContext ctx) {
        FeatureListener.processFeatureEntry(this, ctx);
    }

    @Override
    public void exitFeatureStatement(FeatureStatementContext ctx) {
        FeatureListener.processFeatureExit(this, ctx);
    }

    @Override
    public void enterFeatureBody(FeatureBodyContext ctx) {
        // do nothing
    }

    @Override
    public void exitFeatureBody(FeatureBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void enterDataDefStatement(DataDefStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void exitDataDefStatement(DataDefStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterIfFeatureStatement(IfFeatureStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            IfFeatureListener.processIfFeatureEntry(this, ctx);
        }
    }

    @Override
    public void exitIfFeatureStatement(IfFeatureStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterUnitsStatement(UnitsStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            UnitsListener.processUnitsEntry(this, ctx);
        }
    }

    @Override
    public void exitUnitsStatement(UnitsStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterTypedefStatement(TypedefStatementContext ctx) {
        TypeDefListener.processTypeDefEntry(this, ctx);
    }

    @Override
    public void exitTypedefStatement(TypedefStatementContext ctx) {
        TypeDefListener.processTypeDefExit(this, ctx);
    }

    @Override
    public void enterTypeStatement(TypeStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            TypeListener.processTypeEntry(this, ctx);
        }
    }

    @Override
    public void exitTypeStatement(TypeStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            TypeListener.processTypeExit(this, ctx);
        }
    }

    @Override
    public void enterTypeBodyStatements(TypeBodyStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitTypeBodyStatements(TypeBodyStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterDecimal64Specification(Decimal64SpecificationContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            Decimal64Listener.processDecimal64Entry(this, ctx);
        }
    }

    @Override
    public void exitDecimal64Specification(Decimal64SpecificationContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            Decimal64Listener.processDecimal64Exit(this, ctx);
        }
    }

    @Override
    public void enterFractionDigitStatement(FractionDigitStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            FractionDigitsListener.processFractionDigitsEntry(this, ctx);
        }
    }

    @Override
    public void exitFractionDigitStatement(FractionDigitStatementContext currentContext) {
        // do nothing
    }

    @Override
    public void enterNumericalRestrictions(NumericalRestrictionsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitNumericalRestrictions(NumericalRestrictionsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRangeStatement(RangeStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            RangeRestrictionListener.processRangeRestrictionEntry(this, ctx);
        }
    }

    @Override
    public void exitRangeStatement(RangeStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            RangeRestrictionListener.processRangeRestrictionExit(this, ctx);
        }
    }

    @Override
    public void enterCommonStatements(CommonStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitCommonStatements(CommonStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterStringRestrictions(StringRestrictionsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitStringRestrictions(StringRestrictionsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterLengthStatement(LengthStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            LengthRestrictionListener.processLengthRestrictionEntry(this, ctx);
        }
    }

    @Override
    public void exitLengthStatement(LengthStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            LengthRestrictionListener.processLengthRestrictionExit(this, ctx);
        }
    }

    @Override
    public void enterPatternStatement(PatternStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            PatternRestrictionListener.processPatternRestrictionEntry(this, ctx);
        }
    }

    @Override
    public void exitPatternStatement(PatternStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            PatternRestrictionListener.processPatternRestrictionExit(this, ctx);
        }
    }

    @Override
    public void enterDefaultStatement(DefaultStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            DefaultListener.processDefaultEntry(this, ctx);
        }
    }

    @Override
    public void exitDefaultStatement(DefaultStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterEnumSpecification(EnumSpecificationContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            EnumerationListener.processEnumerationEntry(this, ctx);
        }
    }

    @Override
    public void exitEnumSpecification(EnumSpecificationContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            EnumerationListener.processEnumerationExit(this, ctx);
        }
    }

    @Override
    public void enterEnumStatement(EnumStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            EnumListener.processEnumEntry(this, ctx);
        }
    }

    @Override
    public void exitEnumStatement(EnumStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            EnumListener.processEnumExit(this, ctx);
        }
    }

    @Override
    public void enterEnumStatementBody(EnumStatementBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void exitEnumStatementBody(EnumStatementBodyContext ctx) {
        // do nothing.
    }

    @Override
    public void enterLeafrefSpecification(LeafrefSpecificationContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            LeafrefListener.processLeafrefEntry(this, ctx);
        }
    }

    @Override
    public void exitLeafrefSpecification(LeafrefSpecificationContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            LeafrefListener.processLeafrefExit(this, ctx);
        }
    }

    @Override
    public void enterPathStatement(PathStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            PathListener.processPathEntry(this, ctx);
        }
    }

    @Override
    public void exitPathStatement(PathStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRequireInstanceStatement(RequireInstanceStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            RequireInstanceListener.processRequireInstanceEntry(this, ctx);
        }
    }

    @Override
    public void exitRequireInstanceStatement(RequireInstanceStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterInstanceIdentifierSpecification(InstanceIdentifierSpecificationContext ctx) {
        // do nothing.
    }

    @Override
    public void exitInstanceIdentifierSpecification(InstanceIdentifierSpecificationContext ctx) {
        // do nothing.
    }

    @Override
    public void enterIdentityrefSpecification(IdentityrefSpecificationContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            IdentityRefListener.processIdentityRefEntry(this, ctx);
        }
    }

    @Override
    public void exitIdentityrefSpecification(IdentityrefSpecificationContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            IdentityRefListener.processIdentityRefExit(this, ctx);
        }
    }

    @Override
    public void enterUnionSpecification(UnionSpecificationContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            UnionListener.processUnionEntry(this, ctx);
        }
    }

    @Override
    public void exitUnionSpecification(UnionSpecificationContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            UnionListener.processUnionExit(this, ctx);
        }
    }

    @Override
    public void enterBitsSpecification(BitsSpecificationContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            BitsListener.processBitsEntry(this, ctx);
        }
    }

    @Override
    public void exitBitsSpecification(BitsSpecificationContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            BitsListener.processBitsExit(this, ctx);
        }
    }

    @Override
    public void enterBitStatement(BitStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            BitListener.processBitEntry(this, ctx);
        }
    }

    @Override
    public void exitBitStatement(BitStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            BitListener.processBitExit(this, ctx);
        }
    }

    @Override
    public void enterBitBodyStatement(BitBodyStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void exitBitBodyStatement(BitBodyStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterPositionStatement(PositionStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            PositionListener.processPositionEntry(this, ctx);
        }
    }

    @Override
    public void exitPositionStatement(PositionStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterStatusStatement(StatusStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            StatusListener.processStatusEntry(this, ctx);
        }
    }

    @Override
    public void exitStatusStatement(StatusStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterConfigStatement(ConfigStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            ConfigListener.processConfigEntry(this, ctx);
        }
    }

    @Override
    public void exitConfigStatement(ConfigStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterMandatoryStatement(MandatoryStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            MandatoryListener.processMandatoryEntry(this, ctx);
        }
    }

    @Override
    public void exitMandatoryStatement(MandatoryStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterPresenceStatement(PresenceStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            PresenceListener.processPresenceEntry(this, ctx);
        }
    }

    @Override
    public void exitPresenceStatement(PresenceStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterOrderedByStatement(OrderedByStatementContext ctx) {
        handleUnsupportedYangConstruct(YangConstructType.ORDERED_BY_DATA, ctx,
                                       CURRENTLY_UNSUPPORTED, getFileName(),
                                       ctx.orderedBy().getText());
    }

    @Override
    public void exitOrderedByStatement(OrderedByStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterMustStatement(MustStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            MustListener.processMustEntry(this, ctx);
        }
    }

    @Override
    public void exitMustStatement(MustStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            MustListener.processMustExit(this, ctx);
        }
    }

    @Override
    public void enterErrorMessageStatement(ErrorMessageStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            ErrorMessageListener.processErrorMessageEntry(this, ctx);
        }
    }

    @Override
    public void exitErrorMessageStatement(ErrorMessageStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterErrorAppTagStatement(ErrorAppTagStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            ErrorAppTagListener.processErrorAppTagMessageEntry(this, ctx);
        }
    }

    @Override
    public void exitErrorAppTagStatement(ErrorAppTagStatementContext ctx) {
        //do nothing
    }

    @Override
    public void enterMinElementsStatement(MinElementsStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            MinElementsListener.processMinElementsEntry(this, ctx);
        }
    }

    @Override
    public void exitMinElementsStatement(MinElementsStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterMaxElementsStatement(MaxElementsStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            MaxElementsListener.processMaxElementsEntry(this, ctx);
        }
    }

    @Override
    public void exitMaxElementsStatement(MaxElementsStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterValueStatement(ValueStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            ValueListener.processValueEntry(this, ctx);
        }
    }

    @Override
    public void exitValueStatement(ValueStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterGroupingStatement(GroupingStatementContext ctx) {
        GroupingListener.processGroupingEntry(this, ctx);
    }

    @Override
    public void exitGroupingStatement(GroupingStatementContext ctx) {
        GroupingListener.processGroupingExit(this, ctx);
    }

    @Override
    public void enterContainerStatement(ContainerStatementContext ctx) {
        ContainerListener.processContainerEntry(this, ctx);
    }

    @Override
    public void exitContainerStatement(ContainerStatementContext ctx) {
        ContainerListener.processContainerExit(this, ctx);
    }

    @Override
    public void enterLeafStatement(LeafStatementContext ctx) {
        LeafListener.processLeafEntry(this, ctx);
    }

    @Override
    public void exitLeafStatement(LeafStatementContext ctx) {
        LeafListener.processLeafExit(this, ctx);
    }

    @Override
    public void enterLeafListStatement(LeafListStatementContext ctx) {
        LeafListListener.processLeafListEntry(this, ctx);
    }

    @Override
    public void exitLeafListStatement(LeafListStatementContext ctx) {
        LeafListListener.processLeafListExit(this, ctx);
    }

    @Override
    public void enterListStatement(ListStatementContext ctx) {
        ListListener.processListEntry(this, ctx);
    }

    @Override
    public void exitListStatement(ListStatementContext ctx) {
        ListListener.processListExit(this, ctx);
    }

    @Override
    public void enterKeyStatement(KeyStatementContext ctx) {
        KeyListener.processKeyEntry(this, ctx);
    }

    @Override
    public void exitKeyStatement(KeyStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterUniqueStatement(UniqueStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            UniqueListener.processUniqueEntry(this, ctx);
        }
    }

    @Override
    public void exitUniqueStatement(UniqueStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterChoiceStatement(ChoiceStatementContext ctx) {
        ChoiceListener.processChoiceEntry(this, ctx);
    }

    @Override
    public void exitChoiceStatement(ChoiceStatementContext ctx) {
        ChoiceListener.processChoiceExit(this, ctx);
    }

    @Override
    public void enterShortCaseStatement(ShortCaseStatementContext ctx) {
        ShortCaseListener.processShortCaseEntry(this, ctx);
    }

    @Override
    public void exitShortCaseStatement(ShortCaseStatementContext ctx) {
        ShortCaseListener.processShortCaseExit(this, ctx);
    }

    @Override
    public void enterCaseStatement(CaseStatementContext ctx) {
        CaseListener.processCaseEntry(this, ctx);
    }

    @Override
    public void exitCaseStatement(CaseStatementContext ctx) {
        CaseListener.processCaseExit(this, ctx);
    }

    @Override
    public void enterAnyxmlStatement(AnyxmlStatementContext ctx) {
        increaseUnsupportedYangConstructDepth();
        handleUnsupportedYangConstruct(YangConstructType.ANYXML_DATA, ctx,
                                       UNSUPPORTED_YANG_CONSTRUCT,
                                       getFileName(), ctx.identifier().getText());
    }

    @Override
    public void exitAnyxmlStatement(AnyxmlStatementContext ctx) {
        decreaseUnsupportedYangConstructDepth();
    }

    @Override
    public void enterUsesStatement(UsesStatementContext ctx) {
        UsesListener.processUsesEntry(this, ctx);
    }

    @Override
    public void exitUsesStatement(UsesStatementContext ctx) {
        UsesListener.processUsesExit(this, ctx);
    }

    @Override
    public void enterRefineStatement(RefineStatementContext ctx) {
        increaseUnsupportedYangConstructDepth();
        handleUnsupportedYangConstruct(REFINE_DATA, ctx,
                                       UNSUPPORTED_YANG_CONSTRUCT,
                                       getFileName(), ctx.refine().getText());
    }

    @Override
    public void exitRefineStatement(RefineStatementContext ctx) {
        decreaseUnsupportedYangConstructDepth();
    }

    @Override
    public void enterRefineContainerStatements(RefineContainerStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitRefineContainerStatements(RefineContainerStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRefineLeafStatements(RefineLeafStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitRefineLeafStatements(RefineLeafStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRefineLeafListStatements(RefineLeafListStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitRefineLeafListStatements(RefineLeafListStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRefineListStatements(RefineListStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitRefineListStatements(RefineListStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRefineChoiceStatements(RefineChoiceStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitRefineChoiceStatements(RefineChoiceStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRefineCaseStatements(RefineCaseStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitRefineCaseStatements(RefineCaseStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRefineAnyxmlStatements(RefineAnyxmlStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void exitRefineAnyxmlStatements(RefineAnyxmlStatementsContext ctx) {
        // do nothing.
    }

    @Override
    public void enterAugmentStatement(AugmentStatementContext ctx) {
        AugmentListener.processAugmentEntry(this, ctx);
    }

    @Override
    public void exitAugmentStatement(AugmentStatementContext ctx) {
        AugmentListener.processAugmentExit(this, ctx);
    }

    @Override
    public void enterWhenStatement(WhenStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            WhenListener.processWhenEntry(this, ctx);
        }
    }

    @Override
    public void exitWhenStatement(WhenStatementContext ctx) {
        if (getUnsupportedYangConstructDepth() == 0) {
            WhenListener.processWhenExit(this, ctx);
        }
    }

    @Override
    public void enterRpcStatement(RpcStatementContext ctx) {
        RpcListener.processRpcEntry(this, ctx);
    }

    @Override
    public void exitRpcStatement(RpcStatementContext ctx) {
        RpcListener.processRpcExit(this, ctx);
    }

    @Override
    public void enterInputStatement(InputStatementContext ctx) {
        InputListener.processInputEntry(this, ctx);
    }

    @Override
    public void exitInputStatement(InputStatementContext ctx) {
        InputListener.processInputExit(this, ctx);
    }

    @Override
    public void enterOutputStatement(OutputStatementContext ctx) {
        OutputListener.processOutputEntry(this, ctx);
    }

    @Override
    public void exitOutputStatement(OutputStatementContext ctx) {
        OutputListener.processOutputExit(this, ctx);
    }

    @Override
    public void enterNotificationStatement(NotificationStatementContext ctx) {
        NotificationListener.processNotificationEntry(this, ctx);
    }

    @Override
    public void exitNotificationStatement(NotificationStatementContext ctx) {
        NotificationListener.processNotificationExit(this, ctx);
    }

    @Override
    public void enterDeviationStatement(DeviationStatementContext ctx) {
        DeviationListener.processDeviationEntry(this, ctx);
    }

    @Override
    public void exitDeviationStatement(DeviationStatementContext ctx) {
        DeviationListener.processDeviationExit(this, ctx);
    }

    @Override
    public void enterDeviateNotSupportedStatement(DeviateNotSupportedStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void exitDeviateNotSupportedStatement(DeviateNotSupportedStatementContext ctx) {
        // do nothing.
    }

    @Override
    public void enterDeviateAddStatement(DeviateAddStatementContext ctx) {
        DeviateAddListener.processDeviateAddEntry(this, ctx);
    }

    @Override
    public void exitDeviateAddStatement(DeviateAddStatementContext ctx) {
        DeviateAddListener.processDeviateAddExit(this, ctx);
    }

    @Override
    public void enterDeviateDeleteStatement(DeviateDeleteStatementContext ctx) {
        DeviateDeleteListener.processDeviateDeleteEntry(this, ctx);
    }

    @Override
    public void exitDeviateDeleteStatement(DeviateDeleteStatementContext ctx) {
        DeviateDeleteListener.processDeviateDeleteExit(this, ctx);
    }

    @Override
    public void enterDeviateReplaceStatement(DeviateReplaceStatementContext ctx) {
        DeviateReplaceListener.processDeviateReplaceEntry(this, ctx);
    }

    @Override
    public void exitDeviateReplaceStatement(DeviateReplaceStatementContext ctx) {
        DeviateReplaceListener.processDeviateReplaceExit(this, ctx);
    }

    @Override
    public void enterString(StringContext ctx) {
        // do nothing.
    }

    @Override
    public void exitString(StringContext ctx) {
        // do nothing.
    }

    @Override
    public void enterIdentifier(IdentifierContext ctx) {
        // do nothing.
    }

    @Override
    public void exitIdentifier(IdentifierContext ctx) {
        // do nothing.
    }

    @Override
    public void enterDateArgumentString(DateArgumentStringContext ctx) {
        // do nothing.
    }

    @Override
    public void exitDateArgumentString(DateArgumentStringContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRange(RangeContext ctx) {
        // do nothing.
    }

    @Override
    public void exitRange(RangeContext ctx) {
        // do nothing.
    }

    @Override
    public void enterLength(LengthContext ctx) {
        // do nothing.
    }

    @Override
    public void exitLength(LengthContext ctx) {
        // do nothing.
    }

    @Override
    public void enterPath(PathContext ctx) {
        // do nothing.
    }

    @Override
    public void exitPath(PathContext ctx) {
        // do nothing.
    }

    @Override
    public void enterPosition(PositionContext ctx) {
        // do nothing.
    }

    @Override
    public void exitPosition(PositionContext ctx) {
        // do nothing.
    }

    @Override
    public void enterStatus(StatusContext ctx) {
        // do nothing.
    }

    @Override
    public void exitStatus(StatusContext ctx) {
        // do nothing.
    }

    @Override
    public void enterConfig(ConfigContext ctx) {
        // do nothing.
    }

    @Override
    public void exitConfig(ConfigContext ctx) {
        // do nothing.
    }

    @Override
    public void enterMandatory(MandatoryContext ctx) {
        // do nothing.
    }

    @Override
    public void exitMandatory(MandatoryContext ctx) {
        // do nothing.
    }

    @Override
    public void enterOrderedBy(OrderedByContext ctx) {
        // do nothing.
    }

    @Override
    public void exitOrderedBy(OrderedByContext ctx) {
        // do nothing.
    }

    @Override
    public void enterMinValue(MinValueContext ctx) {
        // do nothing.
    }

    @Override
    public void exitMinValue(MinValueContext ctx) {
        // do nothing.
    }

    @Override
    public void enterMaxValue(MaxValueContext ctx) {
        // do nothing.
    }

    @Override
    public void exitMaxValue(MaxValueContext ctx) {
        // do nothing.
    }

    @Override
    public void enterKey(KeyContext ctx) {
        // do nothing.
    }

    @Override
    public void exitKey(KeyContext ctx) {
        // do nothing.
    }

    @Override
    public void enterUnique(UniqueContext ctx) {
        // do nothing.
    }

    @Override
    public void exitUnique(UniqueContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRefine(RefineContext ctx) {
        // do nothing.
    }

    @Override
    public void exitRefine(RefineContext ctx) {
        // do nothing.
    }

    @Override
    public void enterAugment(AugmentContext ctx) {
        // do nothing.
    }

    @Override
    public void exitAugment(AugmentContext ctx) {
        // do nothing.
    }

    @Override
    public void enterDeviation(DeviationContext ctx) {
        // do nothing.
    }

    @Override
    public void exitDeviation(DeviationContext ctx) {
        // do nothing.
    }

    @Override
    public void enterYangConstruct(YangConstructContext ctx) {
        // do nothing.
    }

    @Override
    public void exitYangConstruct(YangConstructContext ctx) {
        // do nothing.
    }

    @Override
    public void enterCompilerAnnotationStatement(CompilerAnnotationStatementContext ctx) {
        CompilerAnnotationListener.processCompilerAnnotationEntry(this, ctx);
    }

    @Override
    public void exitCompilerAnnotationStatement(CompilerAnnotationStatementContext ctx) {
        CompilerAnnotationListener.processCompilerAnnotationExit(this, ctx);
    }

    @Override
    public void enterCompilerAnnotationBodyStatement(CompilerAnnotationBodyStatementContext ctx) {
        // do nothing
    }

    @Override
    public void exitCompilerAnnotationBodyStatement(CompilerAnnotationBodyStatementContext ctx) {
        // do nothing
    }

    @Override
    public void enterAppDataStructureStatement(AppDataStructureStatementContext ctx) {
        AppDataStructureListener.processAppDataStructureEntry(this, ctx);
    }

    @Override
    public void exitAppDataStructureStatement(AppDataStructureStatementContext ctx) {
        AppDataStructureListener.processAppDataStructureExit(this, ctx);
    }

    @Override
    public void enterAppDataStructure(AppDataStructureContext currentContext) {
        // do nothing
    }

    @Override
    public void exitAppDataStructure(AppDataStructureContext currentContext) {
        // do nothing
    }

    @Override
    public void enterAppExtendedStatement(AppExtendedStatementContext currentContext) {
        AppExtendedNameListener.processAppExtendedNameEntry(this, currentContext);
    }

    @Override
    public void exitAppExtendedStatement(AppExtendedStatementContext currentContext) {
        // TODO : to be implemented
    }

    @Override
    public void enterExtendedName(ExtendedNameContext currentContext) {
        // do nothing
    }

    @Override
    public void exitExtendedName(ExtendedNameContext currentContext) {
        // do nothing
    }

    @Override
    public void enterDataStructureKeyStatement(DataStructureKeyStatementContext ctx) {
        DataStructureKeyListener.processDataStructureKeyEntry(this, ctx);
    }

    @Override
    public void exitDataStructureKeyStatement(DataStructureKeyStatementContext ctx) {
        // do nothing
    }

    @Override
    public void enterVersion(VersionContext ctx) {
        // do nothing.
    }

    @Override
    public void exitVersion(VersionContext ctx) {
        // do nothing.
    }

    @Override
    public void enterValue(ValueContext ctx) {
        // do nothing.
    }

    @Override
    public void exitValue(ValueContext ctx) {
        // do nothing.
    }

    @Override
    public void enterRequireInstance(RequireInstanceContext ctx) {
        // do nothing.
    }

    @Override
    public void exitRequireInstance(RequireInstanceContext ctx) {
        // do nothing.
    }

    @Override
    public void enterFraction(FractionContext ctx) {
        // TODO: implement the method.
    }

    @Override
    public void exitFraction(FractionContext ctx) {
        // TODO: implement the method.
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {
        // do nothing.
    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {
        // do nothing.
    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {
        // do nothing.
    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {
        // do nothing.
    }

    @Override
    public void enterDefaultDenyWriteStatement(
            DefaultDenyWriteStatementContext ctx) {
        DefaultDenyWriteExtRefListener
                .processDefaultDenyWriteStructureEntry(this, ctx);
    }

    @Override
    public void exitDefaultDenyWriteStatement(
            DefaultDenyWriteStatementContext ctx) {
        // do nothing
    }

    @Override
    public void enterDefaultDenyAllStatement(
            DefaultDenyAllStatementContext ctx) {
        DefaultDenyAllExtRefListener
                .processDefaultDenyAllStructureEntry(this, ctx);
    }

    @Override
    public void exitDefaultDenyAllStatement(
            DefaultDenyAllStatementContext ctx) {
        // do nothing
    }

    @Override
    public void enterUnknownStatement(UnknownStatementContext ctx) {
        increaseUnsupportedYangConstructDepth();
        handleUnsupportedYangConstruct(UNKNOWN_STATEMENT, ctx,
                                       CURRENTLY_UNSUPPORTED, getFileName(),
                                       ctx.unknown().getText());
    }

    @Override
    public void exitUnknownStatement(UnknownStatementContext ctx) {
        decreaseUnsupportedYangConstructDepth();
    }

    @Override
    public void enterUnknownStatement2(UnknownStatement2Context ctx) {
        // do nothing
    }

    @Override
    public void exitUnknownStatement2(UnknownStatement2Context ctx) {
        // do nothing
    }

    @Override
    public void enterStmtEnd(StmtEndContext ctx) {
        // do nothing
    }

    @Override
    public void exitStmtEnd(StmtEndContext ctx) {
        // do nothing
    }

    @Override
    public void enterStmtSep(StmtSepContext ctx) {
        // do nothing
    }

    @Override
    public void exitStmtSep(StmtSepContext ctx) {
        // do nothing
    }

    @Override
    public void enterUnknown(UnknownContext currentContext) {
        // do nothing
    }

    @Override
    public void exitUnknown(UnknownContext currentContext) {
        // do nothing
    }

    @Override
    public void enterUnknown2(Unknown2Context currentContext) {
        // do nothing
    }

    @Override
    public void exitUnknown2(Unknown2Context currentContext) {
        // do nothing
    }

    @Override
    public void enterYangStatement(YangStatementContext currentContext) {
        // do nothing
    }

    @Override
    public void exitYangStatement(YangStatementContext currentContext) {
        // do nothing
    }

    @Override
    public void enterAnydataStatement(AnydataStatementContext ctx) {
        AnydataListener.processAnydataEntry(this, ctx);
    }

    @Override
    public void exitAnydataStatement(AnydataStatementContext ctx) {
        AnydataListener.processAnydataExit(this, ctx);
    }
}
