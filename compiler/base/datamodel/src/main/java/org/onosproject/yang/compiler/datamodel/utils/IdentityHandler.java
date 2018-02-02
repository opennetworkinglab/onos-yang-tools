/*
 * Copyright 2018-present Open Networking Foundation
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
package org.onosproject.yang.compiler.datamodel.utils;

import org.onosproject.yang.compiler.datamodel.YangBase;
import org.onosproject.yang.compiler.datamodel.YangIdentity;

import java.util.List;
import java.util.Stack;

/**
 * Represents identity extend-list handling.
 */
public class IdentityHandler {

    /**
     * Stack to store the resolvable identities.
     */
    private Stack<YangIdentity> stack;

    /**
     * Creates a new identity handler.
     *
     * @param base YANG base
     */
    public IdentityHandler(YangBase base) {
        stack = new Stack<>();
        YangIdentity id = base.getParentIdentity();
        if (id.isAddedToAllParent()) {
            return;
        }
        stack.push(id);
        addToExtendList();
    }

    /**
     * Adds to the extend list for all the parent nodes with its complete tree.
     */
    public void addToExtendList() {
        while (!stack.empty()) {
            YangIdentity baseId = stack.peek();
            List<YangIdentity> exList = baseId.getExtendList();
            if (exList != null && !exList.isEmpty()) {
                for (YangIdentity id : exList) {
                    if (!id.isAddedToAllParent()) {
                        stack.push(id);
                    }
                }
            }
            YangIdentity resolvingId = stack.pop();
            addToAllParent(resolvingId, resolvingId.getBaseNode().getReferredIdentity());
        }
    }

    /**
     * Adds an identity to all the parent nodes.
     *
     * @param referredId referred identity
     * @param baseId     base identity
     */
    private void addToAllParent(YangIdentity referredId, YangIdentity baseId) {
        YangIdentity baseIdentity = baseId;
        while (baseIdentity != null) {
            List<YangIdentity> list = baseIdentity.getExtendList();
            if (!list.contains(referredId)) {
                baseIdentity.addToExtendList(referredId);
            }

            YangBase base = baseIdentity.getBaseNode();
            baseIdentity = null;
            if (base != null) {
                baseIdentity = base.getReferredIdentity();
            }
        }
        referredId.setAddedToAllParent(true);
    }
}
