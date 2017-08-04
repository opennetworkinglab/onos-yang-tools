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

package org.onosproject.yang.compiler.api;

import java.nio.file.Path;

/**
 * Represents base class for exceptions in YANG tool operations.
 */
public class YangCompilerException extends RuntimeException {

    private static final long serialVersionUID = 20161028L;

    private Path yangFile;

    /**
     * Creates a new YANG tool exception with given message. It's expected
     * that caller of YANG compiler display's exception message to communicate
     * error information with user.
     *
     * @param message the detail of exception in string
     */
    public YangCompilerException(String message) {
        super(message);
    }

    /**
     * Creates a new tool exception from given message and cause. It's expected
     * that caller of YANG compiler display's exception cause and message to
     * communicate error information with user.
     *
     * @param message the detail of exception in string
     * @param cause   underlying cause of the error
     */
    public YangCompilerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new tool exception from cause. It's expected
     * that caller of YANG compiler display's exception cause to communicate
     * error information with user.
     *
     * @param cause underlying cause of the error
     */
    public YangCompilerException(final Throwable cause) {
        super(cause);
    }

    /**
     * Retrieves the current YANG files for which exception has occured.
     *
     * @return current YANG file
     */
    public Path getYangFile() {
        return yangFile;
    }


    /**
     * Updates the YANG file which caused the exception.
     *
     * @param yangFile YANG files being processed
     */
    public void setYangFile(Path yangFile) {
        this.yangFile = yangFile;
    }
}
