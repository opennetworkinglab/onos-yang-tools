/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.onosproject.yang.model;

/**
 * Abstraction of an entity which identifies a generated class uniquely among
 * its siblings.
 */
public class AtomicPath {

    private DataNode.Type type;

    /**
     * Creates a atomic path object.
     *
     * @param type atomic path type
     */
    protected AtomicPath(DataNode.Type type) {
        this.type = type;
    }

    /**
     * Returns the atomic path type.
     *
     * @return the atomic path type
     */
    public DataNode.Type type() {
        return type;
    }

    /**
     * Sets the atomic path type identifier of leaf-list.
     *
     * @param type atomic path type
     */
    public void type(DataNode.Type type) {
        this.type = type;
    }
}
