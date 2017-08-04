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

import java.io.Serializable;

/**
 * Representation of data model node to maintain path predicate in YANG
 * absolute-path or relative-path.
 */
public class YangPathPredicate extends DefaultLocationInfo
        implements Serializable {

    private static final long serialVersionUID = 806201689L;

    /**
     * YANG node id.
     */
    private YangNodeIdentifier nodeId;

    /**
     * Left axis represents node-id before equality sign.
     */
    private Object leftAxisNode;

    /**
     * YANG path operator.
     */
    private YangPathOperator pathOp;

    /**
     * YANG right relative path in path-predicate.
     */
    private YangRelativePath relPath;

    /**
     * Right axis node represents the node-id after the equality sign.
     */
    private Object rightAxisNode;

    /**
     * Returns the path expression operator.
     *
     * @return path operator
     */
    public YangPathOperator getPathOp() {
        return pathOp;
    }

    /**
     * Sets the path expression operator.
     *
     * @param pathOp path operator
     */
    public void setPathOp(YangPathOperator pathOp) {
        this.pathOp = pathOp;
    }

    /**
     * Returns the right relative path expression.
     *
     * @return relative path
     */
    public YangRelativePath getRelPath() {
        return relPath;
    }

    /**
     * Sets the right relative path expression.
     *
     * @param relPath relative path
     */
    public void setRelPath(YangRelativePath relPath) {
        this.relPath = relPath;
    }

    /**
     * Returns the node identifier.
     *
     * @return node id
     */
    public YangNodeIdentifier getNodeId() {
        return nodeId;
    }

    /**
     * Sets the YANG node identifier.
     *
     * @param nodeId node id
     */
    public void setNodeId(YangNodeIdentifier nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * Returns the left axis node.
     *
     * @return left axis node
     */
    public Object getLeftAxisNode() {
        return leftAxisNode;
    }

    /**
     * Sets the left axis node.
     *
     * @param leftAxisNode left axis node
     */
    public void setLeftAxisNode(Object leftAxisNode) {
        this.leftAxisNode = leftAxisNode;
    }

    /**
     * Returns the right axis node.
     *
     * @return right axis node
     */
    public Object getRightAxisNode() {
        return rightAxisNode;
    }

    /**
     * Sets the right axis node.
     *
     * @param rightAxisNode right axis node
     */
    public void setRightAxisNode(Object rightAxisNode) {
        this.rightAxisNode = rightAxisNode;
    }
}
