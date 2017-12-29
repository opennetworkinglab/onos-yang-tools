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

package org.onosproject.yang.compiler.utils.io.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static org.onosproject.yang.compiler.utils.UtilConstants.EIGHT_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yang.compiler.utils.UtilConstants.FOUR_SPACE_INDENTATION;
import static org.onosproject.yang.compiler.utils.UtilConstants.MULTIPLE_NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.NEW_LINE;
import static org.onosproject.yang.compiler.utils.UtilConstants.SPACE;

/**
 * Represents utility to handle file system operations.
 */
public final class FileSystemUtil {

    /**
     * Creates an instance of file system util.
     */
    private FileSystemUtil() {
    }

    /**
     * Reads the contents from source file and append its contents to append file.
     *
     * @param toAppend destination file in which the contents of source file is appended
     * @param srcFile  source file from which data is read and added to to append file
     * @throws IOException any IO errors
     */
    public static void appendFileContents(File toAppend, File srcFile)
            throws IOException {
        updateFileHandle(srcFile, readAppendFile(toAppend.toString(),
                                                 FOUR_SPACE_INDENTATION), false);
    }

    /**
     * Reads file and convert it to string.
     *
     * @param toAppend file to be converted
     * @param spaces   spaces to be appended
     * @return string of file
     * @throws IOException when fails to convert to string
     */
    public static String readAppendFile(String toAppend, String spaces)
            throws IOException {

        FileReader fileReader = new FileReader(toAppend);
        BufferedReader bufferReader = new BufferedReader(fileReader);
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferReader.readLine();

            while (line != null) {
                switch (line) {
                    case SPACE:
                    case EMPTY_STRING:
                    case EIGHT_SPACE_INDENTATION:
                    case MULTIPLE_NEW_LINE:
                        stringBuilder.append(NEW_LINE);
                        break;
                    case FOUR_SPACE_INDENTATION:
                        stringBuilder.append(EMPTY_STRING);
                        break;
                    default:
                        String append = spaces + line;
                        stringBuilder.append(append);
                        stringBuilder.append(NEW_LINE);
                        break;
                }
                line = bufferReader.readLine();
            }
            return stringBuilder.toString();
        } finally {
            fileReader.close();
            bufferReader.close();
        }
    }

    /**
     * Updates the generated file handle.
     *
     * @param inputFile        input file
     * @param contentTobeAdded content to be appended to the file
     * @param isClose          when close of file is called.
     * @throws IOException if the named file exists but is a directory rather than a regular file, does not exist but
     *                     cannot be created, or cannot be opened for any other reason
     */
    public static void updateFileHandle(File inputFile, String contentTobeAdded,
                                        boolean isClose)
            throws IOException {


        if (!isClose) {
            FileWriter fileWriter = new FileWriter(inputFile, true);
            PrintWriter outputPrintWriter = new PrintWriter(fileWriter, true);
            outputPrintWriter.write(contentTobeAdded);
            outputPrintWriter.flush();
            outputPrintWriter.close();
        } else {
            // nothing to do
        }
    }

    /**
     * Closes the file handle for temporary file.
     *
     * @param file        file to be closed
     * @param toBeDeleted flag to indicate if file needs to be deleted
     * @throws IOException when failed to close the file handle
     */
    public static void closeFile(File file, boolean toBeDeleted)
            throws IOException {

        if (file != null) {
            updateFileHandle(file, null, true);
            if (toBeDeleted && file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    throw new IOException("Failed to delete temporary file " + file.getName());
                }
            }
        }
    }

    /**
     * Closes the file handle for temporary file with file deletion.
     *
     * @param file file to be closed
     * @throws IOException when failed to close the file handle
     */
    public static void closeFile(File file) throws IOException {
        closeFile(file, true);
    }
    // TODO follow coding guidelines in remaining of this file.
}
