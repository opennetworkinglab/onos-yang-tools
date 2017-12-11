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

/**
 * This is a YANG grammar for parser based on which ANTLR will generate YANG parser.
 */

grammar GeneratedYang;
import YangLexer;

    yangfile : moduleStatement EOF
             | subModuleStatement EOF;

    /**
     * module-stmt         = optsep module-keyword sep identifier-arg-str
     *                       optsep
     *                       "{" stmtsep
     *                           module-header-stmts
     *                           linkage-stmts
     *                           meta-stmts
     *                           revision-stmts
     *                           body-stmts
     *                       "}" optsep
     */

    moduleStatement : MODULE_KEYWORD identifier LEFT_CURLY_BRACE stmtSep
            moduleBody RIGHT_CURLY_BRACE;

    moduleBody : moduleHeaderStatement linkageStatements metaStatements revisionStatements bodyStatements;

    /**
     * module-header-stmts = ;; these stmts can appear in any order
     *                       [yang-version-stmt stmtsep]
     *                        namespace-stmt stmtsep
     *                        prefix-stmt stmtsep
     */

    moduleHeaderStatement : (yangVersionStatement stmtSep)? namespaceStatement stmtSep prefixStatement stmtSep
                            | (yangVersionStatement stmtSep)? prefixStatement stmtSep namespaceStatement stmtSep
                            | namespaceStatement stmtSep (yangVersionStatement stmtSep)? prefixStatement stmtSep
                            | namespaceStatement stmtSep prefixStatement stmtSep (yangVersionStatement stmtSep)?
                            | prefixStatement stmtSep namespaceStatement stmtSep (yangVersionStatement stmtSep)?
                            | prefixStatement stmtSep (yangVersionStatement stmtSep)? namespaceStatement stmtSep
                            ;

    /**
     * linkage-stmts       = ;; these stmts can appear in any order
     *                       *(import-stmt stmtsep)
     *                       *(include-stmt stmtsep)
     */
    linkageStatements : (importStatement stmtSep | includeStatement stmtSep)*;

    /**
     * meta-stmts          = ;; these stmts can appear in any order
     *                       [organization-stmt stmtsep]
     *                       [contact-stmt stmtsep]
     *                       [description-stmt stmtsep]
     *                       [reference-stmt stmtsep]
     */
    metaStatements : (organizationStatement stmtSep)? (contactStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
               | (organizationStatement stmtSep)? (contactStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
               | (organizationStatement stmtSep)? (descriptionStatement stmtSep)? (contactStatement stmtSep)? (referenceStatement stmtSep)?
               | (organizationStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (contactStatement stmtSep)?
               | (organizationStatement stmtSep)? (referenceStatement stmtSep)? (contactStatement stmtSep)? (descriptionStatement stmtSep)?
               | (organizationStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (contactStatement stmtSep)?
               | (contactStatement stmtSep)? (organizationStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
               | (contactStatement stmtSep)? (organizationStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
               | (contactStatement stmtSep)? (referenceStatement stmtSep)? (organizationStatement stmtSep)? (descriptionStatement stmtSep)?
               | (contactStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (organizationStatement stmtSep)?
               | (contactStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (organizationStatement stmtSep)?
               | (contactStatement stmtSep)? (descriptionStatement stmtSep)? (organizationStatement stmtSep)? (referenceStatement stmtSep)?
               | (referenceStatement stmtSep)? (contactStatement stmtSep)? (organizationStatement stmtSep)? (descriptionStatement stmtSep)?
               | (referenceStatement stmtSep)? (contactStatement stmtSep)? (descriptionStatement stmtSep)? (organizationStatement stmtSep)?
               | (referenceStatement stmtSep)? (organizationStatement stmtSep)? (contactStatement stmtSep)? (descriptionStatement stmtSep)?
               | (referenceStatement stmtSep)? (organizationStatement stmtSep)? (descriptionStatement stmtSep)? (contactStatement stmtSep)?
               | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (organizationStatement stmtSep)? (contactStatement stmtSep)?
               | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (contactStatement stmtSep)? (organizationStatement stmtSep)?
               | (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (contactStatement stmtSep)? (organizationStatement stmtSep)?
               | (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (organizationStatement stmtSep)? (contactStatement stmtSep)?
               | (descriptionStatement stmtSep)? (contactStatement stmtSep)? (referenceStatement stmtSep)? (organizationStatement stmtSep)?
               | (descriptionStatement stmtSep)? (contactStatement stmtSep)? (organizationStatement stmtSep)? (referenceStatement stmtSep)?
               | (descriptionStatement stmtSep)? (organizationStatement stmtSep)? (contactStatement stmtSep)? (referenceStatement stmtSep)?
               | (descriptionStatement stmtSep)? (organizationStatement stmtSep)? (referenceStatement stmtSep)? (contactStatement stmtSep)?
               ;

    // revision-stmts      = *(revision-stmt stmtsep)
    revisionStatements : (revisionStatement stmtSep)*;

    /**
     * body-stmts          = *((extension-stmt /
     *                          feature-stmt /
     *                          identity-stmt /
     *                          typedef-stmt /
     *                          grouping-stmt /
     *                          data-def-stmt /
     *                          augment-stmt /
     *                          rpc-stmt /
     *                          notification-stmt /
     *                          deviation-stmt) stmtsep)
     */
    bodyStatements : ((extensionStatement | featureStatement | identityStatement
               | typedefStatement | groupingStatement | dataDefStatement
               | augmentStatement | rpcStatement | notificationStatement
               | deviationStatement | compilerAnnotationStatement) stmtSep)*;

    /**
     * yang-version-stmt   = yang-version-keyword sep yang-version-arg-str
     *                       optsep stmtend
     */
    yangVersionStatement :   YANG_VERSION_KEYWORD version stmtEnd;

    /**
     * namespace-stmt      = namespace-keyword sep uri-str optsep stmtend
     * For namespace validation TODO in Listener
     */
    namespaceStatement : NAMESPACE_KEYWORD string stmtEnd;

    /**
     * prefix-stmt         = prefix-keyword sep prefix-arg-str
     *                       optsep stmtend
     */
    prefixStatement : PREFIX_KEYWORD identifier stmtEnd;

    /**
     * import-stmt         = import-keyword sep identifier-arg-str optsep
     *                       "{" stmtsep
     *                           prefix-stmt stmtsep
     *                           [revision-date-stmt stmtsep]
     *                        "}"
     */
    importStatement : IMPORT_KEYWORD identifier LEFT_CURLY_BRACE
            stmtSep importStatementBody RIGHT_CURLY_BRACE;

    importStatementBody : prefixStatement stmtSep (revisionDateStatement stmtSep)?;

    // revision-date-stmt = revision-date-keyword sep revision-date stmtend
    revisionDateStatement : REVISION_DATE_KEYWORD dateArgumentString stmtEnd;

    /**
     * include-stmt        = include-keyword sep identifier-arg-str optsep
     *                             (";" /
     *                              "{" stmtsep
     *                                  [revision-date-stmt stmtsep]
     *                            "}")
     */
    includeStatement : INCLUDE_KEYWORD identifier (STMTEND | LEFT_CURLY_BRACE
            stmtSep (revisionDateStatement stmtSep)? RIGHT_CURLY_BRACE);

    /**
     * organization-stmt   = organization-keyword sep string
     *                            optsep stmtend
     */
    organizationStatement : ORGANIZATION_KEYWORD string stmtEnd;

    // contact-stmt        = contact-keyword sep string optsep stmtend
    contactStatement : CONTACT_KEYWORD string stmtEnd;

    // description-stmt    = description-keyword sep string optsep stmtend
    descriptionStatement : DESCRIPTION_KEYWORD string stmtEnd;

    // reference-stmt      = reference-keyword sep string optsep stmtend
    referenceStatement : REFERENCE_KEYWORD string stmtEnd;

    /**
     * revision-stmt       = revision-keyword sep revision-date optsep
     *                             (";" /
     *                              "{" stmtsep
     *                                  [description-stmt stmtsep]
     *                                  [reference-stmt stmtsep]
     *                              "}")
     */
    revisionStatement : REVISION_KEYWORD dateArgumentString (STMTEND |
             LEFT_CURLY_BRACE stmtSep revisionStatementBody RIGHT_CURLY_BRACE);
    revisionStatementBody : (descriptionStatement stmtSep)?
             (referenceStatement stmtSep)?;

    /**
     * submodule-stmt      = optsep submodule-keyword sep identifier-arg-str
     *                             optsep
     *                             "{" stmtsep
     *                                 submodule-header-stmts
     *                                 linkage-stmts
     *                                 meta-stmts
     *                                 revision-stmts
     *                                 body-stmts
     *                             "}" optsep
     */
    subModuleStatement : SUBMODULE_KEYWORD identifier LEFT_CURLY_BRACE
             stmtSep submoduleBody RIGHT_CURLY_BRACE;
    submoduleBody : submoduleHeaderStatement linkageStatements metaStatements revisionStatements bodyStatements;

    /** submodule-header-stmts =
     *                            ;; these stmts can appear in any order
     *                            [yang-version-stmt stmtsep]
     *                             belongs-to-stmt stmtsep
     */
    submoduleHeaderStatement : (yangVersionStatement stmtSep)? belongstoStatement stmtSep
            | belongstoStatement stmtSep (yangVersionStatement stmtSep)? ;

    /**
     * belongs-to-stmt     = belongs-to-keyword sep identifier-arg-str
     *                       optsep
     *                       "{" stmtsep
     *                           prefix-stmt stmtsep
     *                       "}"
     */
    belongstoStatement : BELONGS_TO_KEYWORD identifier LEFT_CURLY_BRACE
            stmtSep belongstoStatementBody RIGHT_CURLY_BRACE;
    belongstoStatementBody : prefixStatement stmtSep;

    /**
     * extension-stmt      = extension-keyword sep identifier-arg-str optsep
     *                       (";" /
     *                        "{" stmtsep
     *                            ;; these stmts can appear in any order
     *                            [argument-stmt stmtsep]
     *                            [status-stmt stmtsep]
     *                            [description-stmt stmtsep]
     *                            [reference-stmt stmtsep]
     *                        "}")
     */
    extensionStatement : EXTENSION_KEYWORD identifier (STMTEND | LEFT_CURLY_BRACE
             stmtSep extensionBody RIGHT_CURLY_BRACE);
    extensionBody : (argumentStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
                   | (argumentStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
                   | (argumentStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)?
                   | (argumentStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)?
                   | (argumentStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)?
                   | (argumentStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)?
                   | (statusStatement stmtSep)? (referenceStatement stmtSep)? (argumentStatement stmtSep)? (descriptionStatement stmtSep)?
                   | (statusStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (argumentStatement stmtSep)?
                   | (statusStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (argumentStatement stmtSep)?
                   | (statusStatement stmtSep)? (descriptionStatement stmtSep)? (argumentStatement stmtSep)? (referenceStatement stmtSep)?
                   | (statusStatement stmtSep)? (argumentStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
                   | (statusStatement stmtSep)? (argumentStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
                   | (descriptionStatement stmtSep)? (argumentStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)?
                   | (descriptionStatement stmtSep)? (argumentStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)?
                   | (descriptionStatement stmtSep)? (statusStatement stmtSep)? (argumentStatement stmtSep)? (referenceStatement stmtSep)?
                   | (descriptionStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)? (argumentStatement stmtSep)?
                   | (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)? (argumentStatement stmtSep)?
                   | (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (argumentStatement stmtSep)? (statusStatement stmtSep)?
                   | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (argumentStatement stmtSep)? (statusStatement stmtSep)?
                   | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)? (argumentStatement stmtSep)?
                   | (referenceStatement stmtSep)? (statusStatement stmtSep)? (argumentStatement stmtSep)? (descriptionStatement stmtSep)?
                   | (referenceStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)? (argumentStatement stmtSep)?
                   | (referenceStatement stmtSep)? (argumentStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)?
                   | (referenceStatement stmtSep)? (argumentStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)?
                   ;

    /**
     * argument-stmt       = argument-keyword sep identifier-arg-str optsep
     *                       (";" /
     *                        "{" stmtsep
     *                            [yin-element-stmt stmtsep]
     *                        "}")
     */
    argumentStatement : ARGUMENT_KEYWORD identifier (STMTEND | LEFT_CURLY_BRACE
              stmtSep argumentBody RIGHT_CURLY_BRACE);
    argumentBody : (yinElementStatement stmtSep)?;

    /**
     * yin-element-stmt    = yin-element-keyword sep yin-element-arg-str
     *                       stmtend
     */
    yinElementStatement : YIN_ELEMENT_KEYWORD (TRUE_KEYWORD | FALSE_KEYWORD)
            stmtEnd;

    /**
     * identity-stmt       = identity-keyword sep identifier-arg-str optsep
     *                       (";" /
     *                        "{" stmtsep
     *                            ;; these stmts can appear in any order
     *                            [base-stmt stmtsep]
     *                            [status-stmt stmtsep]
     *                            [description-stmt stmtsep]
     *                            [reference-stmt stmtsep]
     *                        "}")
     */
    identityStatement : IDENTITY_KEYWORD identifier (STMTEND | LEFT_CURLY_BRACE
             stmtSep identityBody RIGHT_CURLY_BRACE);
    identityBody : (baseStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
                  | (baseStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
                  | (baseStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)?
                  | (baseStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)?
                  | (baseStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)?
                  | (baseStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)?
                  | (referenceStatement stmtSep)? (baseStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)?
                  | (referenceStatement stmtSep)? (baseStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)?
                  | (referenceStatement stmtSep)? (statusStatement stmtSep)? (baseStatement stmtSep)? (descriptionStatement stmtSep)?
                  | (referenceStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)? (baseStatement stmtSep)?
                  | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)? (baseStatement stmtSep)?
                  | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (baseStatement stmtSep)? (statusStatement stmtSep)?
                  | (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)? (baseStatement stmtSep)?
                  | (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (baseStatement stmtSep)? (statusStatement stmtSep)?
                  | (descriptionStatement stmtSep)? (statusStatement stmtSep)? (baseStatement stmtSep)? (referenceStatement stmtSep)?
                  | (descriptionStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)? (baseStatement stmtSep)?
                  | (descriptionStatement stmtSep)? (baseStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)?
                  | (descriptionStatement stmtSep)? (baseStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)?
                  | (statusStatement stmtSep)? (baseStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
                  | (statusStatement stmtSep)? (baseStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
                  | (statusStatement stmtSep)? (descriptionStatement stmtSep)? (baseStatement stmtSep)? (referenceStatement stmtSep)?
                  | (statusStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (baseStatement stmtSep)?
                  | (statusStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (baseStatement stmtSep)?
                  | (statusStatement stmtSep)? (referenceStatement stmtSep)? (baseStatement stmtSep)? (descriptionStatement stmtSep)?
                  ;

    /**
     * base-stmt           = base-keyword sep identifier-ref-arg-str
     *                          optsep stmtend*
     * identifier-ref-arg  = [prefix ":"] identifier
     */
    baseStatement : BASE_KEYWORD string stmtEnd;

    /**
     *  feature-stmt        = feature-keyword sep identifier-arg-str optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             *(if-feature-stmt stmtsep)
     *                             [status-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                         "}")
     */
    featureStatement : FEATURE_KEYWORD string (STMTEND | LEFT_CURLY_BRACE
            stmtSep featureBody RIGHT_CURLY_BRACE);
    featureBody : (ifFeatureStatement stmtSep)* (statusStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
                 | (ifFeatureStatement stmtSep)* (statusStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
                 | (ifFeatureStatement stmtSep)* (descriptionStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)?
                 | (ifFeatureStatement stmtSep)* (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)?
                 | (ifFeatureStatement stmtSep)* (referenceStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)?
                 | (ifFeatureStatement stmtSep)* (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)?
                 | (statusStatement stmtSep)? (ifFeatureStatement stmtSep)* (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
                 | (statusStatement stmtSep)? (ifFeatureStatement stmtSep)* (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
                 | (statusStatement stmtSep)? (descriptionStatement stmtSep)? (ifFeatureStatement stmtSep)* (referenceStatement stmtSep)?
                 | (statusStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (ifFeatureStatement stmtSep)*
                 | (statusStatement stmtSep)? (referenceStatement stmtSep)? (ifFeatureStatement stmtSep)* (descriptionStatement stmtSep)?
                 | (statusStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (ifFeatureStatement stmtSep)*
                 | (descriptionStatement stmtSep)? (ifFeatureStatement stmtSep)* (statusStatement stmtSep)? (referenceStatement stmtSep)?
                 | (descriptionStatement stmtSep)? (ifFeatureStatement stmtSep)* (referenceStatement stmtSep)? (statusStatement stmtSep)?
                 | (descriptionStatement stmtSep)? (statusStatement stmtSep)? (ifFeatureStatement stmtSep)* (referenceStatement stmtSep)?
                 | (descriptionStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)? (ifFeatureStatement stmtSep)*
                 | (descriptionStatement stmtSep)? (referenceStatement stmtSep)* (statusStatement stmtSep)? (ifFeatureStatement stmtSep)*
                 | (descriptionStatement stmtSep)? (referenceStatement stmtSep)* (ifFeatureStatement stmtSep)? (statusStatement stmtSep)?
                 | (referenceStatement stmtSep)? (ifFeatureStatement stmtSep)* (statusStatement stmtSep)? (descriptionStatement stmtSep)?
                 | (referenceStatement stmtSep)? (ifFeatureStatement stmtSep)* (descriptionStatement stmtSep)? (statusStatement stmtSep)?
                 | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)? (ifFeatureStatement stmtSep)*
                 | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (ifFeatureStatement stmtSep)* (statusStatement stmtSep)?
                 | (referenceStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)? (ifFeatureStatement stmtSep)*
                 | (referenceStatement stmtSep)? (statusStatement stmtSep)? (ifFeatureStatement stmtSep)* (descriptionStatement stmtSep)?
                 ;

    /**
     *  data-def-stmt       = container-stmt /
     *                       leaf-stmt /
     *                       leaf-list-stmt /
     *                       list-stmt /
     *                       choice-stmt /
     *                       anyxml-stmt /
     *                       uses-stmt /
     *                       anydata-stmt
     */
    dataDefStatement : containerStatement
                    | leafStatement
                    | leafListStatement
                    | listStatement
                    | choiceStatement
                    | anyxmlStatement
                    | usesStatement
                    | anydataStatement;

    /**
     *  if-feature-stmt     = if-feature-keyword sep identifier-ref-arg-str
     *                        optsep stmtend
     */
    ifFeatureStatement : IF_FEATURE_KEYWORD string stmtEnd;

    /**
    *    units-stmt          = units-keyword sep string optsep stmtend
    */
    unitsStatement : UNITS_KEYWORD string stmtEnd;

    /**
     *   typedef-stmt        = typedef-keyword sep identifier-arg-str optsep
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             type-stmt stmtsep
     *                            [units-stmt stmtsep]
     *                             [default-stmt stmtsep]
     *                             [status-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                           "}"
     * TODO : 0..1 occurance to be validated in listener
     */
    typedefStatement : TYPEDEF_KEYWORD identifier LEFT_CURLY_BRACE
             stmtSep ((typeStatement stmtSep) | (unitsStatement stmtSep)
             | (defaultStatement stmtSep) | (statusStatement stmtSep)
             | (descriptionStatement stmtSep) | (referenceStatement stmtSep))*
             RIGHT_CURLY_BRACE;

    /**
     *  type-stmt           = type-keyword sep identifier-ref-arg-str optsep
     *                        (";" /
     *                         "{" stmtsep
     *                            type-body-stmts
     *                         "}")
     */
    typeStatement : TYPE_KEYWORD string (STMTEND | LEFT_CURLY_BRACE
            stmtSep typeBodyStatements RIGHT_CURLY_BRACE);

    /**
     *  type-body-stmts     = numerical-restrictions /
     *                        decimal64-specification /
     *                       string-restrictions /
     *                        enum-specification /
     *                        leafref-specification /
     *                        identityref-specification /
     *                        instance-identifier-specification /
     *                        bits-specification /
     *                        union-specification
     *
     */
    typeBodyStatements : numericalRestrictions | decimal64Specification | stringRestrictions | enumSpecification
                    | leafrefSpecification | identityrefSpecification | instanceIdentifierSpecification
                    | bitsSpecification | unionSpecification;

    /**
     *    decimal64-specification = ;; these stmts can appear in any order
     *                               fraction-digits-stmt
     *                               [range-stmt]
     */
     decimal64Specification : fractionDigitStatement rangeStatement?;

    /**
     *  fraction-digits-stmt = fraction-digits-keyword sep
     *                         fraction-digits-arg-str stmtend
     *
     *  fraction-digits-arg-str = < a string that matches the rule
     *                             fraction-digits-arg >
     *
     *  fraction-digits-arg = ("1" ["0" / "1" / "2" / "3" / "4" /
     *                              "5" / "6" / "7" / "8"])
     *                        / "2" / "3" / "4" / "5" / "6" / "7" / "8" / "9"
     */
    fractionDigitStatement : FRACTION_DIGITS_KEYWORD fraction stmtEnd;

    /**
     *  numerical-restrictions = range-stmt stmtsep
     */
    numericalRestrictions : rangeStatement stmtSep;

    /**
     *  range-stmt          = range-keyword sep range-arg-str optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             [error-message-stmt stmtsep]
     *                             [error-app-tag-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                          "}")
     */
    rangeStatement : RANGE_KEYWORD range (STMTEND | LEFT_CURLY_BRACE
             stmtSep commonStatements RIGHT_CURLY_BRACE);

    commonStatements : (errorMessageStatement stmtSep)? (errorAppTagStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
                 | (errorMessageStatement stmtSep)? (errorAppTagStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
                 | (errorMessageStatement stmtSep)? (descriptionStatement stmtSep)? (errorAppTagStatement stmtSep)? (referenceStatement stmtSep)?
                 | (errorMessageStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (errorAppTagStatement stmtSep)?
                 | (errorMessageStatement stmtSep)? (referenceStatement stmtSep)? (errorAppTagStatement stmtSep)? (descriptionStatement stmtSep)?
                 | (errorMessageStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (errorAppTagStatement stmtSep)?
                 | (errorAppTagStatement stmtSep)? (errorMessageStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
                 | (errorAppTagStatement stmtSep)? (errorMessageStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
                 | (errorAppTagStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (errorMessageStatement stmtSep)?
                 | (errorAppTagStatement stmtSep)? (descriptionStatement stmtSep)? (errorMessageStatement stmtSep)? (referenceStatement stmtSep)?
                 | (errorAppTagStatement stmtSep)? (referenceStatement stmtSep)? (errorMessageStatement stmtSep)? (descriptionStatement stmtSep)?
                 | (errorAppTagStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (errorMessageStatement stmtSep)?
                 | (descriptionStatement stmtSep)? (errorMessageStatement stmtSep)? (errorAppTagStatement stmtSep)? (referenceStatement stmtSep)?
                 | (descriptionStatement stmtSep)? (errorMessageStatement stmtSep)? (referenceStatement stmtSep)? (errorAppTagStatement stmtSep)?
                 | (descriptionStatement stmtSep)? (errorAppTagStatement stmtSep)? (errorMessageStatement stmtSep)? (referenceStatement stmtSep)?
                 | (descriptionStatement stmtSep)? (errorAppTagStatement stmtSep)? (referenceStatement stmtSep)? (errorMessageStatement stmtSep)?
                 | (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (errorMessageStatement stmtSep)? (errorAppTagStatement stmtSep)?
                 | (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (errorAppTagStatement stmtSep)? (errorMessageStatement stmtSep)?
                 | (referenceStatement stmtSep)? (errorMessageStatement stmtSep)? (descriptionStatement stmtSep)? (errorAppTagStatement stmtSep)?
                 | (referenceStatement stmtSep)? (errorMessageStatement stmtSep)? (errorAppTagStatement stmtSep)? (descriptionStatement stmtSep)?
                 | (referenceStatement stmtSep)? (errorAppTagStatement stmtSep)? (descriptionStatement stmtSep)? (errorMessageStatement stmtSep)?
                 | (referenceStatement stmtSep)? (errorAppTagStatement stmtSep)? (errorMessageStatement stmtSep)? (descriptionStatement stmtSep)?
                 | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (errorMessageStatement stmtSep)? (errorAppTagStatement stmtSep)?
                 | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (errorAppTagStatement stmtSep)? (errorMessageStatement stmtSep)?
                 ;

    /**
     *  string-restrictions = ;; these stmts can appear in any order
     *                        [length-stmt stmtsep]
     *                        *(pattern-stmt stmtsep)
     */
    stringRestrictions : ((lengthStatement stmtSep)? (patternStatement stmtSep)*)
             | ((patternStatement stmtSep)* (lengthStatement stmtSep)?);

    /**
     *  length-stmt         = length-keyword sep length-arg-str optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             [error-message-stmt stmtsep]
     *                             [error-app-tag-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                          "}")
     */
    lengthStatement : LENGTH_KEYWORD length (STMTEND | LEFT_CURLY_BRACE
              stmtSep commonStatements RIGHT_CURLY_BRACE);

    /**
     *  pattern-stmt        = pattern-keyword sep string optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             [error-message-stmt stmtsep]
     *                             [error-app-tag-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                          "}")
     */
    patternStatement : PATTERN_KEYWORD string (STMTEND | LEFT_CURLY_BRACE
              stmtSep commonStatements RIGHT_CURLY_BRACE);

    /**
     *  default-stmt        = default-keyword sep string stmtend
     */
    defaultStatement : DEFAULT_KEYWORD string stmtEnd;

    /**
     *  enum-specification  = 1*(enum-stmt stmtsep)
     */
    enumSpecification : (enumStatement stmtSep)+;

    /**
     *  enum-stmt           = enum-keyword sep string optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             [value-stmt stmtsep]
     *                             [status-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                          "}")
     */
    enumStatement : ENUM_KEYWORD string (STMTEND | LEFT_CURLY_BRACE
             stmtSep enumStatementBody RIGHT_CURLY_BRACE);

    enumStatementBody : (valueStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
                   | (valueStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
                   | (valueStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)?
                   | (valueStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)?
                   | (valueStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)?
                   | (valueStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)?
                   | (statusStatement stmtSep)? (valueStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
                   | (statusStatement stmtSep)? (valueStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
                   | (statusStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (valueStatement stmtSep)?
                   | (statusStatement stmtSep)? (descriptionStatement stmtSep)? (valueStatement stmtSep)? (referenceStatement stmtSep)?
                   | (statusStatement stmtSep)? (referenceStatement stmtSep)? (valueStatement stmtSep)? (descriptionStatement stmtSep)?
                   | (statusStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (valueStatement stmtSep)?
                   | (descriptionStatement stmtSep)? (valueStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)?
                   | (descriptionStatement stmtSep)? (valueStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)?
                   | (descriptionStatement stmtSep)? (statusStatement stmtSep)? (valueStatement stmtSep)? (referenceStatement stmtSep)?
                   | (descriptionStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)? (valueStatement stmtSep)?
                   | (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (valueStatement stmtSep)? (statusStatement stmtSep)?
                   | (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)? (valueStatement stmtSep)?
                   | (referenceStatement stmtSep)? (valueStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)?
                   | (referenceStatement stmtSep)? (valueStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)?
                   | (referenceStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)? (valueStatement stmtSep)?
                   | (referenceStatement stmtSep)? (statusStatement stmtSep)? (valueStatement stmtSep)? (descriptionStatement stmtSep)?
                   | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (valueStatement stmtSep)? (statusStatement stmtSep)?
                   | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)? (valueStatement stmtSep)?
                   ;

    /**
     *  leafref-specification =
     *                        ;; these stmts can appear in any order
     *                        path-stmt stmtsep
     *                        [require-instance-stmt stmtsep]
     */
    leafrefSpecification : (pathStatement stmtSep (requireInstanceStatement stmtSep)?)
             | ((requireInstanceStatement stmtSep)? pathStatement stmtSep);

    /**
     *  path-stmt           = path-keyword sep path-arg-str stmtend
     */
    pathStatement : PATH_KEYWORD path stmtEnd;

    /**
     *  require-instance-stmt = require-instance-keyword sep
     *                           require-instance-arg-str stmtend
     *  require-instance-arg-str = < a string that matches the rule
     *                             require-instance-arg >
     *  require-instance-arg = true-keyword / false-keyword
     */
    requireInstanceStatement : REQUIRE_INSTANCE_KEYWORD requireInstance stmtEnd;

    /**
     *  instance-identifier-specification =
     *                        [require-instance-stmt stmtsep]
     */
    instanceIdentifierSpecification : (requireInstanceStatement stmtSep)?;

    /**
     * identityref-specification =
     *                        base-stmt stmtsep
     */
    identityrefSpecification : baseStatement stmtSep;

    /**
     *  union-specification = 1*(type-stmt stmtsep)
     */
    unionSpecification : (typeStatement stmtSep)+;

    /**
     *  bits-specification  = 1*(bit-stmt stmtsep)
     */
    bitsSpecification : (bitStatement stmtSep)+;

    /**
     * bit-stmt            = bit-keyword sep identifier-arg-str optsep
     *                       (";" /
     *                        "{" stmtsep
     *                            ;; these stmts can appear in any order
     *                            [position-stmt stmtsep]
     *                            [status-stmt stmtsep]
     *                            [description-stmt stmtsep]
     *                            [reference-stmt stmtsep]
     *                          "}"
     *                        "}")
     */
    bitStatement : BIT_KEYWORD identifier (STMTEND | LEFT_CURLY_BRACE
             stmtSep bitBodyStatement RIGHT_CURLY_BRACE);

    bitBodyStatement : (positionStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
                  | (positionStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
                  | (positionStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)?
                  | (positionStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)?
                  | (positionStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)?
                  | (positionStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)?
                  | (statusStatement stmtSep)? (positionStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)?
                  | (statusStatement stmtSep)? (positionStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)?
                  | (statusStatement stmtSep)? (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (positionStatement stmtSep)?
                  | (statusStatement stmtSep)? (descriptionStatement stmtSep)? (positionStatement stmtSep)? (referenceStatement stmtSep)?
                  | (statusStatement stmtSep)? (referenceStatement stmtSep)? (positionStatement stmtSep)? (descriptionStatement stmtSep)?
                  | (statusStatement stmtSep)? (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (positionStatement stmtSep)?
                  | (descriptionStatement stmtSep)? (positionStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)?
                  | (descriptionStatement stmtSep)? (positionStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)?
                  | (descriptionStatement stmtSep)? (statusStatement stmtSep)? (positionStatement stmtSep)? (referenceStatement stmtSep)?
                  | (descriptionStatement stmtSep)? (statusStatement stmtSep)? (referenceStatement stmtSep)? (positionStatement stmtSep)?
                  | (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (positionStatement stmtSep)? (statusStatement stmtSep)?
                  | (descriptionStatement stmtSep)? (referenceStatement stmtSep)? (statusStatement stmtSep)? (positionStatement stmtSep)?
                  | (referenceStatement stmtSep)? (positionStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)?
                  | (referenceStatement stmtSep)? (positionStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)?
                  | (referenceStatement stmtSep)? (statusStatement stmtSep)? (descriptionStatement stmtSep)? (positionStatement stmtSep)?
                  | (referenceStatement stmtSep)? (statusStatement stmtSep)? (positionStatement stmtSep)? (descriptionStatement stmtSep)?
                  | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (positionStatement stmtSep)? (statusStatement stmtSep)?
                  | (referenceStatement stmtSep)? (descriptionStatement stmtSep)? (statusStatement stmtSep)? (positionStatement stmtSep)?
                  ;

    /**
     *  position-stmt       = position-keyword sep
     *                        position-value-arg-str stmtend
     *  position-value-arg-str = < a string that matches the rule
     *                              position-value-arg >
     *  position-value-arg  = non-negative-integer-value
     */
    positionStatement : POSITION_KEYWORD position stmtEnd;

    /**
     *  status-stmt         = status-keyword sep status-arg-str stmtend
     *  status-arg-str      = < a string that matches the rule
     *                         status-arg >
     *  status-arg          = current-keyword /
     *                        obsolete-keyword /
     *                        deprecated-keyword
     */
    statusStatement : STATUS_KEYWORD status stmtEnd;

    /**
     *  config-stmt         = config-keyword sep
     *                        config-arg-str stmtend
     *  config-arg-str      = < a string that matches the rule
     *                          config-arg >
     *  config-arg          = true-keyword / false-keyword
     */
    configStatement : CONFIG_KEYWORD config STMTEND;

    /**
     *  mandatory-stmt      = mandatory-keyword sep
     *                        mandatory-arg-str stmtend
     *
     *  mandatory-arg-str   = < a string that matches the rule
     *                          mandatory-arg >
     *
     *  mandatory-arg       = true-keyword / false-keyword
     */
    mandatoryStatement : MANDATORY_KEYWORD mandatory stmtEnd;

    /**
     *  presence-stmt       = presence-keyword sep string stmtend
     */
    presenceStatement : PRESENCE_KEYWORD string stmtEnd;

    /**
     *  ordered-by-stmt     = ordered-by-keyword sep
     *                        ordered-by-arg-str stmtend
     *
     *  ordered-by-arg-str  = < a string that matches the rule
     *                          ordered-by-arg >
     *
     *  ordered-by-arg      = user-keyword / system-keyword
     */
    orderedByStatement : ORDERED_BY_KEYWORD orderedBy stmtEnd;

    /**
     *  must-stmt           = must-keyword sep string optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             [error-message-stmt stmtsep]
     *                             [error-app-tag-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                          "}")
     */
    mustStatement : MUST_KEYWORD string (STMTEND | LEFT_CURLY_BRACE
             stmtSep commonStatements RIGHT_CURLY_BRACE);

    /**
     *   error-message-stmt  = error-message-keyword sep string stmtend
     */
    errorMessageStatement : ERROR_MESSAGE_KEYWORD string stmtEnd;

    /**
     *  error-app-tag-stmt  = error-app-tag-keyword sep string stmtend
     */
    errorAppTagStatement : ERROR_APP_TAG_KEYWORD string stmtEnd;

    /**
     *  min-elements-stmt   = min-elements-keyword sep
     *                        min-value-arg-str stmtend
     *  min-value-arg-str   = < a string that matches the rule
     *                          min-value-arg >
     *  min-value-arg       = non-negative-integer-value
     */
    minElementsStatement : MIN_ELEMENTS_KEYWORD minValue stmtEnd;

    /**
     *  max-elements-stmt   = max-elements-keyword sep
     *                        max-value-arg-str stmtend
     *  max-value-arg-str   = < a string that matches the rule
     *                          max-value-arg >
     *  max-value-arg       = unbounded-keyword /
     *                        positive-integer-value
     */
    maxElementsStatement :  MAX_ELEMENTS_KEYWORD maxValue stmtEnd;

    /**
     *  value-stmt          = value-keyword sep integer-value stmtend
     */
    valueStatement : VALUE_KEYWORD value stmtEnd;

    /**
     *   grouping-stmt       = grouping-keyword sep identifier-arg-str optsep
     *                        (";" /
     *                         "{" stmtsep
     *                            ;; these stmts can appear in any order
     *                            [status-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                             *((typedef-stmt /
     *                                grouping-stmt) stmtsep)
     *                             *(data-def-stmt stmtsep)
     *                         "}")
     */
    groupingStatement : GROUPING_KEYWORD identifier (STMTEND | LEFT_CURLY_BRACE
            stmtSep ((statusStatement | descriptionStatement
            | referenceStatement | typedefStatement | groupingStatement
            | dataDefStatement) stmtSep)* RIGHT_CURLY_BRACE);

    /**
     *  container-stmt      = container-keyword sep identifier-arg-str optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             [when-stmt stmtsep]
     *                             *(if-feature-stmt stmtsep)
     *                             *(must-stmt stmtsep)
     *                             [presence-stmt stmtsep]
     *                             [config-stmt stmtsep]
     *                             [status-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                             *((typedef-stmt /
     *                                grouping-stmt) stmtsep)
     *                             *(data-def-stmt stmtsep)
     *                             [default-deny-write-stmt stmtsep]
     *                             [default-deny-all-stmt stmtsep]
     *                         "}")
     */
    containerStatement : CONTAINER_KEYWORD identifier (STMTEND |
            LEFT_CURLY_BRACE stmtSep ((whenStatement | ifFeatureStatement
            | mustStatement | presenceStatement | configStatement
            | statusStatement | descriptionStatement | referenceStatement
            | typedefStatement | groupingStatement | dataDefStatement
            | defaultDenyWriteStatement | defaultDenyAllStatement) stmtSep)*
            RIGHT_CURLY_BRACE);

    /**
     *  leaf-stmt           = leaf-keyword sep identifier-arg-str optsep
     *                        "{" stmtsep
     *                            ;; these stmts can appear in any order
     *                            [when-stmt stmtsep]
     *                            *(if-feature-stmt stmtsep)
     *                            type-stmt stmtsep
     *                            [units-stmt stmtsep]
     *                            *(must-stmt stmtsep)
     *                            [default-stmt stmtsep]
     *                            [config-stmt stmtsep]
     *                            [mandatory-stmt stmtsep]
     *                            [status-stmt stmtsep]
     *                            [description-stmt stmtsep]
     *                            [reference-stmt stmtsep]
     *                            [default-deny-write-stmt stmtsep]
     *                            [default-deny-all-stmt stmtsep]
     *                         "}"
     */
    leafStatement : LEAF_KEYWORD identifier LEFT_CURLY_BRACE stmtSep
            ((whenStatement | ifFeatureStatement | typeStatement
            | unitsStatement | mustStatement | defaultStatement
            | configStatement | mandatoryStatement | statusStatement
            | descriptionStatement | referenceStatement
            | defaultDenyWriteStatement | defaultDenyAllStatement) stmtSep)*
            RIGHT_CURLY_BRACE;

    /**
     *  leaf-list-stmt      = leaf-list-keyword sep identifier-arg-str optsep
     *                        "{" stmtsep
     *                            ;; these stmts can appear in any order
     *                            [when-stmt stmtsep]
     *                            *(if-feature-stmt stmtsep)
     *                            type-stmt stmtsep
     *                            [units-stmt stmtsep]
     *                            *(must-stmt stmtsep)
     *                            [config-stmt stmtsep]
     *                            [min-elements-stmt stmtsep]
     *                            [max-elements-stmt stmtsep]
     *                            [ordered-by-stmt stmtsep]
     *                            [status-stmt stmtsep]
     *                            [description-stmt stmtsep]
     *                            [reference-stmt stmtsep]
     *                            [default-deny-write-stmt stmtsep]
     *                            [default-deny-all-stmt stmtsep]
     *                         "}"
     */
    leafListStatement : LEAF_LIST_KEYWORD identifier LEFT_CURLY_BRACE stmtSep
            ((whenStatement | ifFeatureStatement | typeStatement
            | unitsStatement | mustStatement | configStatement
            | minElementsStatement | maxElementsStatement | orderedByStatement
            | statusStatement | descriptionStatement | referenceStatement
            | defaultDenyWriteStatement | defaultDenyAllStatement) stmtSep)*
            RIGHT_CURLY_BRACE;

    /**
     *  list-stmt           = list-keyword sep identifier-arg-str optsep
     *                        "{" stmtsep
     *                            ;; these stmts can appear in any order
     *                            [when-stmt stmtsep]
     *                            *(if-feature-stmt stmtsep)
     *                            *(must-stmt stmtsep)
     *                            [key-stmt stmtsep]
     *                            *(unique-stmt stmtsep)
     *                            [config-stmt stmtsep]
     *                            [min-elements-stmt stmtsep]
     *                            [max-elements-stmt stmtsep]
     *                            [ordered-by-stmt stmtsep]
     *                            [status-stmt stmtsep]
     *                            [description-stmt stmtsep]
     *                            [reference-stmt stmtsep]
     *                            [default-deny-write-stmt stmtsep]
     *                            [default-deny-all-stmt stmtsep]
     *                            *((typedef-stmt /
     *                               grouping-stmt) stmtsep)
     *                            1*(data-def-stmt stmtsep)
     *                         "}"
     */
    listStatement : LIST_KEYWORD identifier LEFT_CURLY_BRACE stmtSep
            ((whenStatement | ifFeatureStatement | mustStatement | keyStatement
            | uniqueStatement | configStatement | minElementsStatement
            | maxElementsStatement | orderedByStatement | statusStatement
            | descriptionStatement | referenceStatement | defaultDenyWriteStatement
            | defaultDenyAllStatement | typedefStatement | groupingStatement|
             dataDefStatement) stmtSep)* RIGHT_CURLY_BRACE;

    /**
     *  key-stmt            = key-keyword sep key-arg-str stmtend
     */
    keyStatement : KEY_KEYWORD key stmtEnd;

    /**
     *  unique-stmt         = unique-keyword sep unique-arg-str stmtend
     */
    uniqueStatement: UNIQUE_KEYWORD unique stmtEnd;

    /**
     *  choice-stmt         = choice-keyword sep identifier-arg-str optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             [when-stmt stmtsep]
     *                             *(if-feature-stmt stmtsep)
     *                             [default-stmt stmtsep]
     *                             [config-stmt stmtsep]
     *                             [mandatory-stmt stmtsep]
     *                             [status-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                             *((short-case-stmt / case-stmt) stmtsep)
     *                             [default-deny-write-stmt stmtsep]
     *                             [default-deny-all-stmt stmtsep]
     *                         "}")
     */
    choiceStatement : CHOICE_KEYWORD identifier (STMTEND | LEFT_CURLY_BRACE
            stmtSep ((whenStatement | ifFeatureStatement | defaultStatement
            | configStatement | mandatoryStatement | statusStatement
            | descriptionStatement | referenceStatement | shortCaseStatement
            | caseStatement | defaultDenyAllStatement
            | defaultDenyWriteStatement) stmtSep)* RIGHT_CURLY_BRACE);

    /**
     *  short-case-stmt     = container-stmt /
     *                        leaf-stmt /
     *                        leaf-list-stmt /
     *                        list-stmt /
     *                        anyxml-stmt
     */
    shortCaseStatement : containerStatement | leafStatement | leafListStatement
            | listStatement | anyxmlStatement | anydataStatement;

    /**
     *  case-stmt           = case-keyword sep identifier-arg-str optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             [when-stmt stmtsep]
     *                             *(if-feature-stmt stmtsep)
     *                             [status-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                             *(data-def-stmt stmtsep)
     *                         "}")
     */
    caseStatement : CASE_KEYWORD identifier (STMTEND | LEFT_CURLY_BRACE stmtSep
            ((whenStatement | ifFeatureStatement | statusStatement
            | descriptionStatement | referenceStatement | dataDefStatement)
            stmtSep)* RIGHT_CURLY_BRACE);

    /**
     *    anyxml-stmt         = anyxml-keyword sep identifier-arg-str optsep
     *                         (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             [when-stmt stmtsep]
     *                             *(if-feature-stmt stmtsep)
     *                             *(must-stmt stmtsep)
     *                             [config-stmt stmtsep]
     *                             [mandatory-stmt stmtsep]
     *                             [status-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                             [default-deny-write-stmt stmtsep]
     *                             [default-deny-all-stmt stmtsep]
     *                          "}")
     */
     anyxmlStatement : ANYXML_KEYWORD identifier (STMTEND | LEFT_CURLY_BRACE
             stmtSep ((whenStatement | ifFeatureStatement | mustStatement
             | configStatement | mandatoryStatement | statusStatement
             | descriptionStatement | referenceStatement
             | defaultDenyWriteStatement | defaultDenyAllStatement) stmtSep)*
             RIGHT_CURLY_BRACE);

    /**
     *  uses-stmt           = uses-keyword sep identifier-ref-arg-str optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             [when-stmt stmtsep]
     *                             *(if-feature-stmt stmtsep)
     *                             [status-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                             *(refine-stmt stmtsep)
     *                             *(uses-augment-stmt stmtsep)
     *                             [default-deny-write-stmt stmtsep]
     *                             [default-deny-all-stmt stmtsep]
     *                         "}")
     */
    usesStatement : USES_KEYWORD string (STMTEND | LEFT_CURLY_BRACE
            stmtSep ((whenStatement | ifFeatureStatement | statusStatement
            | descriptionStatement | referenceStatement | refineStatement
            | augmentStatement | defaultDenyAllStatement
            | defaultDenyWriteStatement) stmtSep)* RIGHT_CURLY_BRACE);

    /**
     *  refine-stmt         = refine-keyword sep refine-arg-str optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             (refine-container-stmts /
     *                              refine-leaf-stmts /
     *                              refine-leaf-list-stmts /
     *                              refine-list-stmts /
     *                              refine-choice-stmts /
     *                              refine-case-stmts /
     *                              refine-anyxml-stmts)
     *                         "}")
     */
    refineStatement : REFINE_KEYWORD refine (STMTEND  | LEFT_CURLY_BRACE
            stmtSep (refineContainerStatements | refineLeafStatements
            | refineLeafListStatements | refineListStatements
            | refineChoiceStatements | refineCaseStatements
            | refineAnyxmlStatements) RIGHT_CURLY_BRACE);

    /**
     *  refine-container-stmts =
     *                        ;; these stmts can appear in any order
     *                        *(must-stmt stmtsep)
     *                        [presence-stmt stmtsep]
     *                        [config-stmt stmtsep]
     *                        [description-stmt stmtsep]
     *                         [reference-stmt stmtsep]
     * TODO : 0..1 occurance to be checked in listener
     */
    refineContainerStatements : ((mustStatement | presenceStatement
            | configStatement | descriptionStatement stmtSep
            | referenceStatement) stmtSep)* ;

    /**
     *   refine-leaf-stmts   = ;; these stmts can appear in any order
     *                         *(must-stmt stmtsep)
     *                         [default-stmt stmtsep]
     *                         [config-stmt stmtsep]
     *                        [mandatory-stmt stmtsep]
     *                        [description-stmt stmtsep]
     *                        [reference-stmt stmtsep]
     * TODO : 0..1 occurance to be checked in listener
     */
    refineLeafStatements : ((mustStatement | defaultStatement
            | configStatement | mandatoryStatement | descriptionStatement
            | referenceStatement) stmtSep)*;

    /**
     *  refine-leaf-list-stmts =
     *                        ;; these stmts can appear in any order
     *                        *(must-stmt stmtsep)
     *                        [config-stmt stmtsep]
     *                        [min-elements-stmt stmtsep]
     *                        [max-elements-stmt stmtsep]
     *                        [description-stmt stmtsep]
     *                        [reference-stmt stmtsep]
     * TODO : 0..1 occurance to be checked in listener
     */
    refineLeafListStatements : ((mustStatement | configStatement
            | minElementsStatement | maxElementsStatement
            | descriptionStatement | referenceStatement) stmtSep)*;

    /**
     *  refine-list-stmts   = ;; these stmts can appear in any order
     *                        *(must-stmt stmtsep)
     *                        [config-stmt stmtsep]
     *                        [min-elements-stmt stmtsep]
     *                        [max-elements-stmt stmtsep]
     *                        [description-stmt stmtsep]
     *                        [reference-stmt stmtsep]
     * TODO : 0..1 occurance to be checked in listener
     */
    refineListStatements : ((mustStatement | configStatement
            | minElementsStatement | maxElementsStatement
            | descriptionStatement | referenceStatement) stmtSep)*;

    /**
     *  refine-choice-stmts = ;; these stmts can appear in any order
     *                        [default-stmt stmtsep]
     *                        [config-stmt stmtsep]
     *                        [mandatory-stmt stmtsep]
     *                        [description-stmt stmtsep]
     *                        [reference-stmt stmtsep]
     * TODO : 0..1 occurance to be checked in listener
     */
    refineChoiceStatements : ((defaultStatement | configStatement
            | mandatoryStatement | descriptionStatement
            | referenceStatement) stmtSep)*;

    /**
     *  refine-case-stmts   = ;; these stmts can appear in any order
     *                        [description-stmt stmtsep]
     *                        [reference-stmt stmtsep]
     *
     */
    refineCaseStatements : ((descriptionStatement stmtSep)
            | (referenceStatement stmtSep))? | ((referenceStatement stmtSep)
            | (descriptionStatement stmtSep))?;

    /**
     *  refine-anyxml-stmts = ;; these stmts can appear in any order
     *                        *(must-stmt stmtsep)
     *                        [config-stmt stmtsep]
     *                        [mandatory-stmt stmtsep]
     *                        [description-stmt stmtsep]
     *                        [reference-stmt stmtsep]
     */
    refineAnyxmlStatements : ((mustStatement | configStatement
            | mandatoryStatement | descriptionStatement
            | referenceStatement) stmtSep)*;

    /**
     *  augment-stmt        = augment-keyword sep augment-arg-str optsep
     *                        "{" stmtsep
     *                            ;; these stmts can appear in any order
     *                            [when-stmt stmtsep]
     *                            *(if-feature-stmt stmtsep)
     *                            [status-stmt stmtsep]
     *                            [description-stmt stmtsep]
     *                            [reference-stmt stmtsep]
     *                            1*((data-def-stmt stmtsep) /
     *                               (case-stmt stmtsep))
     *                         "}"
     */
    augmentStatement : AUGMENT_KEYWORD augment LEFT_CURLY_BRACE stmtSep
            ((whenStatement | ifFeatureStatement | statusStatement
            | descriptionStatement | referenceStatement
            | dataDefStatement  | caseStatement) stmtSep)* RIGHT_CURLY_BRACE;

    /**
     *  when-stmt           = when-keyword sep string optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                          "}")
     *
     */
    whenStatement : WHEN_KEYWORD string (STMTEND | LEFT_CURLY_BRACE stmtSep
            ((descriptionStatement stmtSep)? (referenceStatement stmtSep)?
            | (referenceStatement stmtSep)? (descriptionStatement stmtSep)?)
            RIGHT_CURLY_BRACE);

    /**
     *  rpc-stmt            = rpc-keyword sep identifier-arg-str optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             *(if-feature-stmt stmtsep)
     *                             [status-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                             *((typedef-stmt /
     *                                grouping-stmt) stmtsep)
     *                             [input-stmt stmtsep]
     *                             [output-stmt stmtsep]
     *                             [default-deny-all-stmt stmtsep]
     *                         "}")
     */
    rpcStatement : RPC_KEYWORD identifier (STMTEND | LEFT_CURLY_BRACE stmtSep
            ((ifFeatureStatement | statusStatement | descriptionStatement
            | referenceStatement | typedefStatement | groupingStatement
            | inputStatement | outputStatement | defaultDenyAllStatement)
            stmtSep)* RIGHT_CURLY_BRACE);

    /**
     * input-stmt          = input-keyword optsep
     *                       "{" stmtsep
     *                           ;; these stmts can appear in any order
     *                           *((typedef-stmt /
     *                              grouping-stmt) stmtsep)
     *                           1*(data-def-stmt stmtsep)
     *                         "}"
     */
    inputStatement : INPUT_KEYWORD LEFT_CURLY_BRACE stmtSep ((typedefStatement
            | groupingStatement | dataDefStatement) stmtSep)* RIGHT_CURLY_BRACE;

    /**
     *  output-stmt         = output-keyword optsep
     *                        "{" stmtsep
     *                            ;; these stmts can appear in any order
     *                            *((typedef-stmt /
     *                               grouping-stmt) stmtsep)
     *                            1*(data-def-stmt stmtsep)
     *                        "}"
     */
    outputStatement : OUTPUT_KEYWORD LEFT_CURLY_BRACE stmtSep ((typedefStatement
            | groupingStatement | dataDefStatement) stmtSep)* RIGHT_CURLY_BRACE;

    /**
     *  notification-stmt   = notification-keyword sep
     *                        identifier-arg-str optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             ;; these stmts can appear in any order
     *                             *(if-feature-stmt stmtsep)
     *                             [status-stmt stmtsep]
     *                             [description-stmt stmtsep]
     *                             [reference-stmt stmtsep]
     *                             [default-deny-all-stmt stmtsep]
     *                             *((typedef-stmt /
     *                                grouping-stmt) stmtsep)
     *                             *(data-def-stmt stmtsep)
     *                         "}")
     */
    notificationStatement : NOTIFICATION_KEYWORD identifier (STMTEND |
            LEFT_CURLY_BRACE stmtSep ((ifFeatureStatement | statusStatement
            | descriptionStatement | referenceStatement | typedefStatement
            | defaultDenyAllStatement | groupingStatement | dataDefStatement)
            stmtSep)* RIGHT_CURLY_BRACE);

    /**
     *  deviation-stmt      = deviation-keyword sep
     *                        deviation-arg-str optsep
     *                        "{" stmtsep
     *                            ;; these stmts can appear in any order
     *                            [description-stmt stmtsep]
     *                            [reference-stmt stmtsep]
     *                            (deviate-not-supported-stmt /
     *                              1*(deviate-add-stmt /
     *                                 deviate-replace-stmt /
     *                                 deviate-delete-stmt))
     *                        "}"
     */
    deviationStatement: DEVIATION_KEYWORD deviation LEFT_CURLY_BRACE stmtSep
            ((descriptionStatement stmtSep) | (referenceStatement stmtSep)
            | deviateNotSupportedStatement | deviateAddStatement | deviateReplaceStatement
            | deviateDeleteStatement)* RIGHT_CURLY_BRACE;

    /**
     * deviate-not-supported-stmt =
     *                       deviate-keyword sep
     *                       not-supported-keyword optsep
     *                       (";" /
     *                        "{" stmtsep
     *                        "}")
     */
    deviateNotSupportedStatement: DEVIATE_KEYWORD NOT_SUPPORTED_KEYWORD (STMTEND
            | LEFT_CURLY_BRACE stmtSep RIGHT_CURLY_BRACE);

    /**
     *  deviate-add-stmt    = deviate-keyword sep add-keyword optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             [units-stmt stmtsep]
     *                             *(must-stmt stmtsep)
     *                             *(unique-stmt stmtsep)
     *                             [default-stmt stmtsep]
     *                             [config-stmt stmtsep]
     *                             [mandatory-stmt stmtsep]
     *                             [min-elements-stmt stmtsep]
     *                             [max-elements-stmt stmtsep]
     *                         "}")
     */
    deviateAddStatement: DEVIATE_KEYWORD ADD_KEYWORD (STMTEND
            | LEFT_CURLY_BRACE stmtSep ((unitsStatement | mustStatement
            | uniqueStatement | defaultStatement | configStatement
            | mandatoryStatement | minElementsStatement | maxElementsStatement)
            stmtSep)* RIGHT_CURLY_BRACE);

    /**
     *  deviate-delete-stmt = deviate-keyword sep delete-keyword optsep
     *                        (";" /
     *                         "{" stmtsep
     *                             [units-stmt stmtsep]
     *                             *(must-stmt stmtsep)
     *                             *(unique-stmt stmtsep)
     *                               [default-stmt stmtsep]
     *                           "}")
     */
    deviateDeleteStatement: DEVIATE_KEYWORD DELETE_KEYWORD (STMTEND
            | LEFT_CURLY_BRACE stmtSep ((unitsStatement | mustStatement
            | uniqueStatement | defaultStatement) stmtSep)* RIGHT_CURLY_BRACE);

    /**
     *   deviate-replace-stmt = deviate-keyword sep replace-keyword optsep
     *                         (";" /
     *                          "{" stmtsep
     *                              [type-stmt stmtsep]
     *                              [units-stmt stmtsep]
     *                              [default-stmt stmtsep]
     *                              [config-stmt stmtsep]
     *                              [mandatory-stmt stmtsep]
     *                              [min-elements-stmt stmtsep]
     *                              [max-elements-stmt stmtsep]
     *                          "}")
     */
    deviateReplaceStatement: DEVIATE_KEYWORD REPLACE_KEYWORD (STMTEND
            | LEFT_CURLY_BRACE stmtSep ((typeStatement | unitsStatement
            | defaultStatement | configStatement | mandatoryStatement
            | minElementsStatement | maxElementsStatement) stmtSep)*
            RIGHT_CURLY_BRACE);

    /**
     *   compiler-annotation-stmt = prefix:compiler-annotation-keyword string
     *                          "{"
     *                              [app-data-structure-stmt stmtsep]
     *                              [app-extended-stmt stmtsep]
     *                          "}"
     */
    compilerAnnotationStatement : COMPILER_ANNOTATION string LEFT_CURLY_BRACE
            compilerAnnotationBodyStatement RIGHT_CURLY_BRACE;

    compilerAnnotationBodyStatement : (appDataStructureStatement stmtSep)?
            (appExtendedStatement stmtSep)?;

    /**
     *   app-data-structure-stmt = prefix:app-data-structure-keyword string
     *                         (";" /
     *                          "{"
     *                              [data-structure-key-stmt stmtsep]
     *                          "}")
     */
    appDataStructureStatement : APP_DATA_STRUCTURE appDataStructure (STMTEND
            | (LEFT_CURLY_BRACE (dataStructureKeyStatement stmtSep)?
            RIGHT_CURLY_BRACE));

    /**
     *   data-structure-key-stmt = prefix:key-keyword string ";"
     */
    dataStructureKeyStatement : DATA_STRUCTURE_KEY string STMTEND;

    /**
     *   app-extended-stmt = prefix:app-extended-name-keyword string ";"
     */
    appExtendedStatement : APP_EXTENDED extendedName STMTEND;

    /**
     *   default-deny-write-stmt = prefix:default-deny-write ";"
     *   From ietf-netconf-acm.yang RFC 6536
     */
    defaultDenyWriteStatement : DEFAULT_DENY_WRITE STMTEND;

    /**
     *   default-deny-all-stmt = prefix:default-deny-all ";"
     *   From ietf-netconf-acm.yang RFC 6536
     */
    defaultDenyAllStatement : DEFAULT_DENY_ALL STMTEND;


   /**
    * anydata-stmt        = anydata-keyword sep identifier-arg-str optsep
    *                       (";" /
    *                        "{" stmtsep
    *                            ;; these stmts can appear in any order
    *                            [when-stmt]
    *                            *if-feature-stmt
    *                            *must-stmt
    *                            [config-stmt]
    *                            [mandatory-stmt]
    *                            [status-stmt]
    *                            [description-stmt]
    *                            [reference-stmt]
    *                         "}") stmtsep
    */
    anydataStatement : ANYDATA_KEYWORD identifier (STMTEND |
               LEFT_CURLY_BRACE stmtSep (whenStatement | ifFeatureStatement
               | mustStatement | configStatement | mandatoryStatement
               | statusStatement | descriptionStatement | referenceStatement)*
               RIGHT_CURLY_BRACE) stmtSep;

    /**
     * unknown-statement   = prefix ":" identifier [sep string] optsep
     *                        (";" / "{" *unknown-statement2 "}")
     */
    unknownStatement : unknown string? (STMTEND
                     | LEFT_CURLY_BRACE (yangStatement |unknownStatement2)*
                     RIGHT_CURLY_BRACE) stmtSep;

    /**
     *   unknown-statement2   = [prefix ":"] identifier [sep string] optsep
     *                          (";" / "{" *unknown-statement2 "}")
     */
    unknownStatement2 : unknown2 string? (STMTEND
                      | LEFT_CURLY_BRACE unknownStatement2* RIGHT_CURLY_BRACE);

    /**
     * yang-stmt as per RFC 7950
     * yang-stmt           = action-stmt / -- not handled
     *                      anydata-stmt /
     *                      anyxml-stmt /
     *                      argument-stmt /
     *                      augment-stmt /
     *                      base-stmt /
     *                      belongs-to-stmt /
     *                      bit-stmt /
     *                      case-stmt /
     *                      choice-stmt /
     *                      config-stmt /
     *                      contact-stmt /
     *                      container-stmt /
     *                      default-stmt /
     *                      description-stmt /
     *                      deviate-add-stmt /
     *                      deviate-delete-stmt /
     *                      deviate-not-supported-stmt /
     *                      deviate-replace-stmt /
     *                      deviation-stmt /
     *                      enum-stmt /
     *                      error-app-tag-stmt /
     *                      error-message-stmt /
     *                      extension-stmt /
     *                      feature-stmt /
     *                      fraction-digits-stmt /
     *                      grouping-stmt /
     *                      identity-stmt /
     *                      if-feature-stmt /
     *                      import-stmt /
     *                      include-stmt /
     *                      input-stmt /
     *                      key-stmt /
     *                      leaf-list-stmt /
     *                      leaf-stmt /
     *                      length-stmt /
     *                      list-stmt /
     *                      mandatory-stmt /
     *                      max-elements-stmt /
     *                      min-elements-stmt /
     *                      modifier-stmt / -- not handled
     *                      module-stmt /
     *                      must-stmt /
     *                      namespace-stmt /
     *                      notification-stmt /
     *                      ordered-by-stmt /
     *                      organization-stmt /
     *                      output-stmt /
     *                      path-stmt /
     *                      pattern-stmt /
     *                      position-stmt /
     *                      prefix-stmt /
     *                      presence-stmt /
     *                      range-stmt /
     *                      reference-stmt /
     *                      refine-stmt /
     *                      require-instance-stmt /
     *                      revision-date-stmt /
     *                      revision-stmt /
     *                      rpc-stmt /
     *                      status-stmt /
     *                      submodule-stmt /
     *                      typedef-stmt /
     *                      type-stmt /
     *                      unique-stmt /
     *                      units-stmt /
     *                      uses-augment-stmt / -- not handled
     *                      uses-stmt /
     *                      value-stmt /
     *                      when-stmt /
     *                      yang-version-stmt /
     *                      yin-element-stmt
     *    ;; Ranges
     */
     yangStatement :  anydataStatement
                | anyxmlStatement | argumentStatement | augmentStatement
                | baseStatement | belongstoStatement | bitStatement
                | caseStatement | choiceStatement    | configStatement
                | contactStatement | containerStatement | defaultStatement
                | descriptionStatement | deviationStatement | enumStatement
                | errorAppTagStatement | errorMessageStatement | extensionStatement
                | featureStatement | fractionDigitStatement | groupingStatement
                | identityStatement | ifFeatureStatement | importStatement
                | includeStatement | inputStatement | keyStatement
                | leafListStatement | leafStatement | lengthStatement
                | listStatement | mandatoryStatement | maxElementsStatement
                | minElementsStatement | moduleStatement | mustStatement
                | namespaceStatement | notificationStatement | orderedByStatement
                | organizationStatement | outputStatement | pathStatement
                | patternStatement | positionStatement | prefixStatement
                | presenceStatement | rangeStatement | referenceStatement
                | refineStatement | requireInstanceStatement | revisionDateStatement
                | revisionStatement | rpcStatement | statusStatement
                | subModuleStatement | typedefStatement | typeStatement
                | uniqueStatement | unitsStatement | usesStatement
                | valueStatement | whenStatement | yangVersionStatement
                | yinElementStatement ;

    /**
     *  stmtend             = ";" / "{" *unknown-statement "}"
     */
    stmtEnd : STMTEND | LEFT_CURLY_BRACE unknownStatement* RIGHT_CURLY_BRACE;

    stmtSep : unknownStatement*;

    string : STRING (PLUS STRING)*
           | IDENTIFIER
           | INTEGER
           | UNKNOWN_STATEMENT
           | UNKNOWN_STATEMENT2
           | yangConstruct;

    unknown : UNKNOWN_STATEMENT;

    unknown2 : UNKNOWN_STATEMENT
             | IDENTIFIER;

    identifier : STRING (PLUS STRING)*
               | IDENTIFIER
               | yangConstruct;

    dateArgumentString : DATE_ARG
                       | STRING (PLUS STRING)*;

    version : string;

    range : string;

    length : string;

    path : string;

    position : string;

    status : string;

    config : string;

    mandatory : string;

    orderedBy : string;

    minValue : string;

    maxValue : string;

    key : string;

    unique : string;

    refine : string;

    requireInstance : string;

    augment : string;

    deviation : string;

    value : string;

    fraction : string;

    appDataStructure : string;

    extendedName : string;

    yangConstruct : ANYXML_KEYWORD | ARGUMENT_KEYWORD | AUGMENT_KEYWORD | BASE_KEYWORD | BELONGS_TO_KEYWORD
                  | BIT_KEYWORD | CASE_KEYWORD | CHOICE_KEYWORD | CONFIG_KEYWORD | CONTACT_KEYWORD | CONTAINER_KEYWORD
                  | DEFAULT_KEYWORD | DESCRIPTION_KEYWORD | ENUM_KEYWORD | ERROR_APP_TAG_KEYWORD | ERROR_MESSAGE_KEYWORD
                  | EXTENSION_KEYWORD | DEVIATION_KEYWORD | DEVIATE_KEYWORD | FEATURE_KEYWORD
                  | FRACTION_DIGITS_KEYWORD | GROUPING_KEYWORD | IDENTITY_KEYWORD | IF_FEATURE_KEYWORD
                  | IMPORT_KEYWORD | INCLUDE_KEYWORD | INPUT_KEYWORD | KEY_KEYWORD | LEAF_KEYWORD | LEAF_LIST_KEYWORD
                  | LENGTH_KEYWORD | LIST_KEYWORD | MANDATORY_KEYWORD | MAX_ELEMENTS_KEYWORD | MIN_ELEMENTS_KEYWORD
                  | MODULE_KEYWORD | MUST_KEYWORD | NAMESPACE_KEYWORD | NOTIFICATION_KEYWORD | ORDERED_BY_KEYWORD
                  | ORGANIZATION_KEYWORD | OUTPUT_KEYWORD | PATH_KEYWORD | PATTERN_KEYWORD | POSITION_KEYWORD
                  | PREFIX_KEYWORD | PRESENCE_KEYWORD | RANGE_KEYWORD | REFERENCE_KEYWORD | REFINE_KEYWORD
                  | REQUIRE_INSTANCE_KEYWORD | REVISION_KEYWORD | REVISION_DATE_KEYWORD | RPC_KEYWORD
                  | STATUS_KEYWORD | SUBMODULE_KEYWORD | TYPE_KEYWORD | TYPEDEF_KEYWORD | UNIQUE_KEYWORD
                  | UNITS_KEYWORD | USES_KEYWORD | VALUE_KEYWORD | WHEN_KEYWORD | YANG_VERSION_KEYWORD
                  | YIN_ELEMENT_KEYWORD | ADD_KEYWORD | CURRENT_KEYWORD | DELETE_KEYWORD | DEPRECATED_KEYWORD
                  | FALSE_KEYWORD | MAX_KEYWORD | MIN_KEYWORD | NOT_SUPPORTED_KEYWORD | OBSOLETE_KEYWORD
                  | REPLACE_KEYWORD | SYSTEM_KEYWORD | TRUE_KEYWORD | UNBOUNDED_KEYWORD | USER_KEYWORD
                  | COMPILER_ANNOTATION_KEYWORD | APP_DATA_STRUCTURE_KEYWORD | DATA_STRUCTURE_KEYWORD
                  | APP_EXTENDED_KEYWORD | DEFAULT_DENY_WRITE_KEYWORD | DEFAULT_DENY_ALL_KEYWORD
                  | ANYDATA_KEYWORD;
