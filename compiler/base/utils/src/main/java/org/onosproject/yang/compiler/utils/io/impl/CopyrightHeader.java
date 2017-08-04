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

import org.onosproject.yang.compiler.utils.UtilConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

/**
 * Represents the license header for the generated files.
 */
public final class CopyrightHeader {

    private static final int EOF = -1;
    private static final String COPYRIGHT_HEADER_FILE = "CopyrightHeader.txt";
    private static final String COPYRIGHTS_FIRST_LINE = "/*\n * Copyright " + getInstance().get(YEAR)
            + "-present Open Networking Foundation\n";
    private static final String TEMP_FILE = "temp.txt";

    /**
     * Creates an instance of copyright header.
     */
    private CopyrightHeader() {
    }

    /**
     * parses Copyright to the temporary file.
     *
     * @return string of file.
     * @throws IOException when fails to get the copyright header
     */
    public static String parseCopyrightHeader() throws IOException {

        File temp = new File(TEMP_FILE);

        try {
            InputStream stream = CopyrightHeader.class.getClassLoader()
                    .getResourceAsStream(COPYRIGHT_HEADER_FILE);
            OutputStream out = new FileOutputStream(temp);

            int index;
            out.write(COPYRIGHTS_FIRST_LINE.getBytes());
            while ((index = stream.read()) != EOF) {
                out.write(index);
            }
            out.close();
            stream.close();
            return getStringFileContent(temp);
        } catch (IOException e) {
            throw new IOException("failed to parse the Copyright header");
        } finally {
            temp.delete();
        }
    }

    /**
     * Converts it to string.
     *
     * @param toAppend file to be converted.
     * @return string of file.
     * @throws IOException when fails to convert to string
     */
    private static String getStringFileContent(File toAppend) throws IOException {

        FileReader fileReader = new FileReader(toAppend);
        BufferedReader bufferReader = new BufferedReader(fileReader);
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferReader.readLine();

            while (line != null) {
                stringBuilder.append(line);
                stringBuilder.append(UtilConstants.NEW_LINE);
                line = bufferReader.readLine();
            }
            return stringBuilder.toString();
        } finally {
            fileReader.close();
            bufferReader.close();
        }
    }
}
