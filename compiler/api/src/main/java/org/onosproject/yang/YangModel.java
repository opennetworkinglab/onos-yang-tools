/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.onosproject.yang;

import java.util.Set;

/**
 * Representation of a compiled YANG model.
 */
public interface YangModel {

    /**
     * Returns set of YANG module with information.
     *
     * @return YANG module info
     */
    Set<YangModule> getYangModules();

    /**
     * Returns set of YANG modules identifier.
     *
     * @return YANG module identifier
     */
    Set<YangModuleId> getYangModulesId();

    /**
     * Returns YANG module information corresponding to a given module
     * identifier.
     *
     * @param id module identifier
     * @return YANG module information
     */
    YangModule getYangModule(YangModuleId id);
}
