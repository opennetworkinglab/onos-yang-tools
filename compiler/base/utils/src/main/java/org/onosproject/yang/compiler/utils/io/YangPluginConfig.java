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

package org.onosproject.yang.compiler.utils.io;

import org.onosproject.yang.compiler.utils.io.impl.YangFileScanner;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Representation of plugin configurations required for YANG utils.
 */
public final class YangPluginConfig {

    /**
     * Contains the code generation directory.
     */
    private String codeGenDir;

    /**
     * Contains information of naming conflicts that can be resolved.
     */
    private YangToJavaNamingConflictUtil conflictResolver;

    /**
     * Java code generation is for sbi.
     */
    private String codeGenerateForSbi;

    /**
     * Path to generate the resource files, which will be used by the  plugin
     * to add it as part of the resource, implicitly by the path location, or
     * some explicit way to add to bundle.
     */
    private String resourceGenDir;

    /**
     * Creates an object for YANG plugin config.
     */
    public YangPluginConfig() {
    }

    /**
     * Returns the string for code generation.
     *
     * @return returns the string for code generation.
     */
    public String getCodeGenerateForSbi() {
        return codeGenerateForSbi;
    }

    /**
     * Sets the string sbi or nbi for code generation.
     *
     * @param codeGenerateForSbi generation is for sbi
     */
    public void setCodeGenerateForSbi(String codeGenerateForSbi) {
        this.codeGenerateForSbi = codeGenerateForSbi;
    }

    /**
     * Sets the path of the java code where it has to be generated.
     *
     * @param codeGenDir path of the directory
     */
    public void setCodeGenDir(String codeGenDir) {
        this.codeGenDir = codeGenDir;
    }

    /**
     * Returns the code generation directory path.
     *
     * @return code generation directory
     */
    public String getCodeGenDir() {
        return codeGenDir;
    }

    /**
     * Sets the object.
     *
     * @param conflictResolver object of the class
     */
    public void setConflictResolver(YangToJavaNamingConflictUtil conflictResolver) {
        this.conflictResolver = conflictResolver;
    }

    /**
     * Returns the object.
     *
     * @return object of the class
     */
    public YangToJavaNamingConflictUtil getConflictResolver() {
        return conflictResolver;
    }


    public String resourceGenDir() {
        return resourceGenDir;
    }

    public void resourceGenDir(String resourceGenDir) {
        this.resourceGenDir = resourceGenDir;
    }

    /**
     * TODO: delete me, it is not part of config, it needs to be updated for
     * test scripts
     * Compiles the generated code for unit tests.
     *
     * @param dir1 directory path
     * @throws IOException when generated code has compilation errors.
     */
    @SuppressWarnings("unchecked")
    public static void compileCode(String dir1) throws IOException {
        String classpath = System.getProperty("java.class.path");
        List<String> optionList = new ArrayList<>();
        optionList.addAll(Arrays.asList("-classpath", classpath));

        List<String> files = YangFileScanner.getJavaFiles(dir1);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager manager = compiler.getStandardFileManager(null, null, null);
        Iterable fileObjects = manager.getJavaFileObjectsFromStrings(files);
        JavaCompiler.CompilationTask task = compiler.getTask(null, null,
                                                             null, optionList, null,
                                                             fileObjects);

        boolean failOnError = !task.call();
        manager.close();
        if (failOnError) {
            throw new IOException("Yang Error : compilation errors in " +
                                          "generated code.");
        }
    }
}
