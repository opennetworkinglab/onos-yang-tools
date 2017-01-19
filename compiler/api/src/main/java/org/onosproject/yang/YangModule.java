/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.onosproject.yang;

import java.io.InputStream;

/**
 * Representation of YANG module information.
 */
public interface YangModule {

    /**
     * Returns YANG module identifier.
     *
     * @return module identifier
     */
    YangModuleId getYangModuleId();

    /**
     * Returns input stream corresponding to a given YANG file path.
     *
     * @return stream
     */
    InputStream getYangSource();

    /**
     * Returns metadata stream.
     *
     * @return stream
     */
    InputStream getMetadata();
}
