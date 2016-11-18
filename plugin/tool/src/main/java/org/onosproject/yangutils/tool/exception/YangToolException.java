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

package org.onosproject.yangutils.tool.exception;

import org.onosproject.yangutils.tool.YangFileInfo;

/**
 * Represents base class for exceptions in YANG tool operations.
 */
public class YangToolException extends RuntimeException {

    private static final long serialVersionUID = 20161028L;

    private YangFileInfo curYangFile;

    /**
     * Creates a new YANG tool exception with given message.
     *
     * @param message the detail of exception in string
     */
    public YangToolException(String message) {
        super(message);
    }

    /**
     * Creates a new tool exception from given message and cause.
     *
     * @param message the detail of exception in string
     * @param cause   underlying cause of the error
     */
    public YangToolException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new tool exception from cause.
     *
     * @param cause underlying cause of the error
     */
    public YangToolException(final Throwable cause) {
        super(cause);
    }

    /**
     * Retrieves the current YANG files for which exception has occured.
     *
     * @return current YANG file
     */
    public YangFileInfo getCurYangFile() {
        return curYangFile;
    }


    /**
     * Update the YANG file which caused the exception.
     *
     * @param curYangFile YANG files being processed
     */
    public void setCurYangFile(YangFileInfo curYangFile) {
        this.curYangFile = curYangFile;
    }
}
