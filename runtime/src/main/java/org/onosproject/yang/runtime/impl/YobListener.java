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

import org.onosproject.yang.compiler.datamodel.YangRpc;
import org.onosproject.yang.compiler.datamodel.YangSchemaNode;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.InnerNode;
import org.onosproject.yang.model.ModelObject;
import org.onosproject.yang.model.SchemaContext;
import org.onosproject.yang.model.SchemaId;
import org.onosproject.yang.runtime.DataNodeListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static org.onosproject.yang.runtime.SerializerHelper.getChildSchemaContext;
import static org.onosproject.yang.runtime.impl.YobHandlerFactory.instance;
import static org.onosproject.yang.runtime.impl.YobUtils.FORWARD_SLASH;
import static org.onosproject.yang.runtime.impl.YobUtils.buildLeafModelObject;

/**
 * Represents implementation of YANG object builder listener.
 */
class YobListener implements DataNodeListener {

    /**
     * Reference to YOB handler.
     */
    private final YobHandlerFactory handlerFactory;
    /**
     * Reference to YANG model registry.
     */
    private final DefaultYangModelRegistry registry;
    /**
     * Stack of YOB workbench.
     */
    private final Stack<YobWorkBench> wbStack;
    /**
     * Reference to parent schema.
     */
    private YangSchemaNode lastIndexSchema;
    /**
     * List of model objects.
     */
    private List<ModelObject> modelObjectList;


    /**
     * Creates an instance of YANG object builder listener.
     *
     * @param node last index schema node
     * @param reg YANG model registry
     */
    public YobListener(YangSchemaNode node, DefaultYangModelRegistry reg) {
        this.lastIndexSchema = node;
        this.handlerFactory = instance();
        this.registry = reg;
        this.wbStack = new Stack<>();
        this.modelObjectList = new LinkedList<>();
    }

    /**
     * Returns the list of model object.
     *
     * @return list of model object
     */
    public List<ModelObject> modelObjectList() {
        return modelObjectList;
    }

    /**
     * Sets the list of model object.
     *
     * @param moList list of model object
     */
    public void modelObjectList(List<ModelObject> moList) {
        modelObjectList = moList;
    }

    /**
     * Returns the YOB work bench stack.
     *
     * @return YOB work bench stack
     */
    public Stack<YobWorkBench> wbStack() {
        return wbStack;
    }


    /**
     * Returns the parent schema node.
     *
     * @return parent schema node
     */
    public YangSchemaNode lastIndexSchema() {
        return lastIndexSchema;
    }

    /**
     * Sets the parent schema node.
     *
     * @param schemaNode YANG schema node
     */
    public void lastIndexSchema(YangSchemaNode schemaNode) {
        lastIndexSchema = schemaNode;
    }

    @Override
    public void enterDataNode(DataNode node) {
        SchemaId schemaId = node.key().schemaId();
        if (schemaId.name().equals(FORWARD_SLASH)) {
            return;
        }

        YangSchemaNode schemaNode;
        if (wbStack.isEmpty() && lastIndexSchema == null) {
            /*
             * It is first level child and resource id is null. So get the
             * schema information from registry.
             */
            schemaNode = ((YangSchemaNode) registry.getChildContext(schemaId));
        } else if (wbStack.isEmpty() && lastIndexSchema != null) {
            /*
             * Resource id is not null, lastIndexSchema will have schema node
             * of last node key in resource id.
             */
            schemaNode = ((YangSchemaNode)
                    getChildSchemaContext(lastIndexSchema, schemaId.name(),
                                          schemaId.namespace()));
        } else {
            /*
             * get schema context for the node from parent data node's schema
             * context.
             */
            SchemaContext parentContext = wbStack.peek().schemaNode();
            schemaNode = ((YangSchemaNode)
                    getChildSchemaContext(parentContext, schemaId.name(),
                                          schemaId.namespace()));
        }

        // get YOB handler based on node type
        YobHandler nodeHandler = handlerFactory.getYobHandlerForContext(node.type());

        // Create object for the data node
        YobWorkBench workBench = nodeHandler.createObject(schemaNode, registry);
        if (workBench != null) {
            wbStack.push(workBench);
        }
    }

    @Override
    public void exitDataNode(DataNode node) {
        SchemaId schemaId = node.key().schemaId();
        if (schemaId.name().equals(FORWARD_SLASH)) {
            return;
        }

        YobWorkBench curWb;
        YobWorkBench parentWb = null;
        if (node instanceof InnerNode) {
            /*
             * If its top level node, it should not be set to parent or
             * if node is RPC input or output node, it should not be set to
             * parent.
             */
            if (wbStack.size() == 1 || (!wbStack.isEmpty() &&
                wbStack.peek().schemaNode().getParentContext() instanceof YangRpc)) {
                curWb = wbStack.pop();
                YobHandler nodeHandler = handlerFactory.getYobHandlerForContext(node.type());
                nodeHandler.buildObject(curWb, registry);
                modelObjectList.add(((ModelObject) curWb.getBuiltObject()));
                return;
            } else {
                curWb = wbStack.pop();
                parentWb = wbStack.peek();
            }
        } else {
            if (wbStack.isEmpty()) {
                ModelObject obj = buildLeafModelObject(node, lastIndexSchema, registry);
                modelObjectList.add(obj);
                return;
            }
            curWb = wbStack.peek();
        }

        YobHandler nodeHandler = handlerFactory.getYobHandlerForContext(node.type());
        nodeHandler.buildObject(curWb, registry);
        nodeHandler.setInParent(node, curWb, parentWb, registry);
    }
}
