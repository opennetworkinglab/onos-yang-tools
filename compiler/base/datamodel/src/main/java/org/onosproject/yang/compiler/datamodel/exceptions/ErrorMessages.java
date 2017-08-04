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
package org.onosproject.yang.compiler.datamodel.exceptions;

/**
 * Represents error messages thrown by data model exception.
 */
public final class ErrorMessages {

    private ErrorMessages() {
    }

    private static final String IN = " in ";
    private static final String AT = " at ";
    private static final String AS = "as ";
    public static final String QUOTES = "\"";
    public static final String CASE = " case ";
    public static final String NOTIFICATION = " notificaiton ";
    public static final String RPC = " rpc ";
    public static final String INPUT = " input ";
    public static final String OUTPUT = " output ";
    public static final String CHOICE = " choice ";
    public static final String GROUPING = " grouping ";
    public static final String TYPEDEF = " typedef ";
    public static final String USES = " uses ";
    public static final String INVALID_CASE_HOLDER
            = "\"Internal Data Model Tree Error: Invalid/Missing \"" +
            "                                   \"holder in case \"";
    public static final String TGT_LEAF = " target node leaf/leaf-list";
    public static final String TARGET_NODE = " target node ";
    public static final String COLLISION_DETECTION = "YANG File Error: " +
            "Identifier collision detected in";
    public static final String FAILED_TO_ADD_CASE = "Failed to add child " +
            "nodes to case node of augment ";

    /**
     * Returns error message for datamodel exception for collision detection.
     *
     * @param msg      message
     * @param name     name of construct
     * @param line     line number
     * @param position character position
     * @param fileName file name
     * @return error message for datamodel exception for collision detection
     */
    public static String getErrorMsg(String msg, String name, int line,
                                     int position, String fileName) {
        StringBuilder builder = new StringBuilder();
        builder.append(msg)
                .append(name)
                .append(IN)
                .append(line)
                .append(AT)
                .append(position)
                .append(IN)
                .append(fileName);
        return builder.toString();
    }

    /**
     * Returns error message for datamodel exception for collision detection.
     *
     * @param msg       message
     * @param name      name of construct
     * @param line      line number
     * @param position  character position
     * @param construct construct name
     * @param fileName  file name
     * @return error message for datamodel exception for collision detection
     */
    public static String getErrorMsgCollision(String msg, String name, int line,
                                              int position, String construct,
                                              String fileName) {
        StringBuilder builder = new StringBuilder();
        builder.append(msg)
                .append(construct)
                .append(AS)
                .append(QUOTES)
                .append(name)
                .append(IN)
                .append(line)
                .append(AT)
                .append(position)
                .append(IN)
                .append(fileName)
                .append(QUOTES);
        return builder.toString();
    }
}
