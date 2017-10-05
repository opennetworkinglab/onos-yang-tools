/*
 *  Copyright 2017-present Open Networking Foundation
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

package org.onosproject.yang.serializers.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.onosproject.yang.model.KeyLeaf;
import org.onosproject.yang.model.LeafListKey;
import org.onosproject.yang.model.ListKey;
import org.onosproject.yang.model.NodeKey;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.AnnotatedNodeInfo;
import org.onosproject.yang.runtime.Annotation;
import org.onosproject.yang.runtime.DefaultAnnotatedNodeInfo;
import org.onosproject.yang.runtime.DefaultAnnotation;
import org.onosproject.yang.runtime.SerializerHelper;
import org.onosproject.yang.runtime.YangSerializerContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.onosproject.yang.compiler.utils.io.impl.YangIoUtils.trimAtLast;
import static org.onosproject.yang.runtime.SerializerHelper.getModuleNameFromNameSpace;

/**
 * Utilities for serializers.
 */
public final class SerializersUtil {
    private static final Splitter SLASH_SPLITTER = Splitter.on('/');
    private static final Splitter COMMA_SPLITTER = Splitter.on(',');
    private static final String QUOTES = "\"";
    private static final String ROOT_ELEMENT_START = "<root ";
    private static final String ROOT_ELEMENT_END = "</root>";
    private static final String URI_ENCODING_CHAR_SET = "ISO-8859-1";
    private static final String UTF8_ENCODING = "utf-8";
    private static final String ERROR_LIST_MSG = "List/Leaf-list node should be " +
            "in format \"nodeName=key\"or \"nodeName=instance-value\"";
    private static final String EQUAL = "=";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String URI_ENCODED_SLASH = "%2F";
    private static final String URI_ENCODED_COLON = "%3A";

    // no instantiation
    private SerializersUtil() {
    }

    /**
     * Converts XML atrtibutes into annotated node info.
     *
     * @param element XML element
     * @param id      resource id of an element
     * @return annotated node info
     */
    public static AnnotatedNodeInfo convertXmlAttributesToAnnotations(Element element,
                                                                      ResourceId id) {
        Iterator iter = element.attributeIterator();
        if (!iter.hasNext()) {
            // element does not have any attributes
            return null;
        }
        AnnotatedNodeInfo.Builder builder = DefaultAnnotatedNodeInfo.builder();
        builder = builder.resourceId(id);
        while (iter.hasNext()) {
            Attribute attr = (Attribute) iter.next();
            DefaultAnnotation annotation = new DefaultAnnotation(
                    attr.getQualifiedName(), attr.getValue());
            builder = builder.addAnnotation(annotation);
        }
        return builder.build();
    }


    /**
     * Appends the XML data with root element.
     *
     * @param inputStream        XML data
     * @param protocolAnnotation list of annoations for root element
     * @return XML with root element
     * @throws DocumentException if root element cannot be created
     * @throws IOException       if input data cannot be read
     */
    public static String addRootElementWithAnnotation(InputStream inputStream,
                                                      List<Annotation>
                                                              protocolAnnotation)
            throws DocumentException, IOException {
        BufferedReader br;
        StringBuilder sb = new StringBuilder();
        String xmlData;
        // Parse composite stream resourceData
        br = new BufferedReader(new InputStreamReader(inputStream));
        while ((xmlData = br.readLine()) != null) {
            sb.append(xmlData);
        }

        StringBuilder rootElement = new StringBuilder(ROOT_ELEMENT_START);
        if (protocolAnnotation != null) {
            for (Annotation annotation : protocolAnnotation) {
                rootElement.append(annotation.name()).append(EQUAL)
                        .append(QUOTES).append(annotation.value()).append(QUOTES);
            }
        }
        rootElement.append(">").append(sb.toString()).append(ROOT_ELEMENT_END);
        return rootElement.toString();
    }

    /**
     * Converts a URI string to resource identifier.
     *
     * @param uriString given URI
     * @param context   YANG schema context information
     * @return resource ID
     */
    public static ResourceId.Builder convertUriToRid(String uriString,
                                                     YangSerializerContext context) {
        if (uriString == null || uriString.isEmpty()) {
            return null;
        }

        //List<String> paths = urlPathArgsDecode(SLASH_SPLITTER.split(uriString));
        List<String> paths = Arrays.asList(uriString.split(SLASH));

        if (!paths.isEmpty()) {
            ResourceId.Builder ridBuilder =
                    SerializerHelper.initializeResourceId(context);
            processPathSegments(paths, ridBuilder);
            return ridBuilder;
        }

        return null;
    }

    /**
     * Converts a list of path from the original format to ISO-8859-1 code.
     *
     * @param paths the original paths
     * @return list of decoded paths
     */
    @Deprecated
    public static List<String> urlPathArgsDecode(Iterable<String> paths) {
        try {
            List<String> decodedPathArgs = new ArrayList<>();
            for (String pathArg : paths) {
                String decode = URLDecoder.decode(pathArg,
                                                  URI_ENCODING_CHAR_SET);
                decodedPathArgs.add(decode);
            }
            return decodedPathArgs;
        } catch (UnsupportedEncodingException e) {
            throw new SerializerUtilException("Invalid URL path arg '" +
                                                      paths + "': ", e);
        }
    }

    private static ResourceId.Builder processPathSegments(List<String> paths,
                                                          ResourceId.Builder builder) {
        if (paths.isEmpty()) {
            return builder;
        }

        boolean isLastSegment = paths.size() == 1;

        String segment = paths.iterator().next();
        processSinglePathSegment(segment, builder);

        if (isLastSegment) {
            // We have hit the base case of recursion.
            return builder;
        }

        /*
         * Chop off the first segment, and recursively process the rest
         * of the path segments.
         */
        List<String> remainPaths = paths.subList(1, paths.size());
        processPathSegments(remainPaths, builder);

        return builder;
    }

    private static void processSinglePathSegment(String pathSegment,
                                                 ResourceId.Builder builder) {
        if (pathSegment.contains(COLON)) {
            processPathSegmentWithNamespace(pathSegment, builder);
        } else {
            processPathSegmentWithoutNamespace(pathSegment, builder);
        }
    }

    private static void processPathSegmentWithNamespace(String pathSegment,
                                                        ResourceId.Builder builder) {

        String nodeName = getLatterSegment(pathSegment, COLON);
        String namespace = getPreSegment(pathSegment, COLON);
        addNodeNameToRid(nodeName, namespace, builder);
    }

    private static void processPathSegmentWithoutNamespace(String nodeName,
                                                           ResourceId.Builder builder) {
        addNodeNameToRid(nodeName, null, builder);
    }

    private static void addNodeNameToRid(String nodeName,
                                         String namespace,
                                         ResourceId.Builder builder) {
        if (nodeName.contains(EQUAL)) {
            addListOrLeafList(nodeName, namespace, builder);
        } else {
            addLeaf(nodeName, namespace, builder);
        }
    }

    private static void addListOrLeafList(String path,
                                          String namespace,
                                          ResourceId.Builder builder) {
        String nodeName = getPreSegment(path, EQUAL);
        String keyStr = getLatterSegment(path, EQUAL);
        if (keyStr == null) {
            throw new SerializerUtilException(ERROR_LIST_MSG);
        }

        List<String> keys = uriDecodedKeys(keyStr);
        SerializerHelper.addToResourceId(builder, nodeName, namespace, keys);
    }

    private static List<String> uriDecodedKeys(String keyStr) {
        List<String> decodedKeys = Lists.newArrayList();

        if (keyStr.contains(COMMA)) {
            List<String> encodedKeys = Lists.newArrayList(COMMA_SPLITTER.split(keyStr));
            for (String encodedKey : encodedKeys) {
                decodedKeys.add(uriDecodedString(encodedKey));
            }
        } else {
            decodedKeys.add(uriDecodedString(keyStr));
        }

        return decodedKeys;
    }


    private static String uriDecodedString(String keyStr) {
        try {
            keyStr = URLDecoder.decode(keyStr, UTF8_ENCODING);
        } catch (UnsupportedEncodingException ex) {
            throw new SerializerUtilException("UnsupportedEncodingException: " + ex.getMessage());
        }

        return keyStr;
    }

    private static void addLeaf(String nodeName,
                                String namespace,
                                ResourceId.Builder builder) {
        checkNotNull(nodeName);
        String value = null;
        SerializerHelper.addToResourceId(builder, nodeName, namespace, value);
    }

    /**
     * Returns the previous segment of a path which is separated by a split char.
     * For example:
     * <pre>
     * "foo:bar", ":" to "foo"
     * </pre>
     *
     * @param path      the original path string
     * @param splitChar char used to split the path
     * @return the previous segment of the path
     */
    public static String getPreSegment(String path, String splitChar) {
        int idx = path.lastIndexOf(splitChar);
        if (idx == -1) {
            return null;
        }
        return path.substring(0, idx);
    }

    /**
     * Returns the latter segment of a path which is separated by a split char.
     * For example:
     * <pre>
     * "foo:bar", ":" to "bar"
     * </pre>
     *
     * @param path      the original path string
     * @param splitChar char used to split the path
     * @return the latter segment of the path
     */
    public static String getLatterSegment(String path, String splitChar) {
        int idx = path.lastIndexOf(splitChar);
        if (idx == -1) {
            return path;
        }

        return path.substring(idx + 1);
    }

    /**
     * Converts a resource identifier to URI string.
     *
     * @param rid     resource identifier
     * @param context YANG serializer context
     * @return URI
     */
    public static String convertRidToUri(ResourceId rid,
                                         YangSerializerContext context) {
        if (rid == null) {
            return null;
        }

        StringBuilder uriBuilder = new StringBuilder();
        List<NodeKey> nodeKeyList = rid.nodeKeys();
        String curNameSpace = null;
        for (NodeKey key : nodeKeyList) {
            curNameSpace = addNodeKeyToUri(key, curNameSpace, uriBuilder, context);
        }
        return trimAtLast(uriBuilder.toString(), SLASH);
    }

    private static String addNodeKeyToUri(NodeKey key,
                                          String curNameSpace,
                                          StringBuilder uriBuilder,
                                          YangSerializerContext context) {
        String newNameSpace = null;
        if (key instanceof LeafListKey) {
            newNameSpace = addLeafListNodeToUri((LeafListKey) key,
                                                curNameSpace, uriBuilder, context);
        } else if (key instanceof ListKey) {
            newNameSpace = addListNodeToUri((ListKey) key, curNameSpace,
                                            uriBuilder, context);
        } else {
            String name = key.schemaId().name();
            if (!name.equals(SLASH)) {
                newNameSpace = addNodeNameToUri(key, curNameSpace,
                                                uriBuilder, context);
            }
        }
        return newNameSpace;
    }

    private static String addLeafListNodeToUri(LeafListKey key,
                                               String curNameSpace,
                                               StringBuilder uriBuilder,
                                               YangSerializerContext context) {

        String newNameSpace = addNodeNameToUri(key, curNameSpace, uriBuilder,
                                               context);
        uriBuilder.append(EQUAL);
        uriBuilder.append(key.asString());
        return newNameSpace;
    }

    private static String addListNodeToUri(ListKey key,
                                           String curNameSpace,
                                           StringBuilder uriBuilder,
                                           YangSerializerContext context) {
        String newNameSpace = addNodeNameToUri(key, curNameSpace, uriBuilder,
                                               context);
        uriBuilder.append(EQUAL);
        String prefix = "";
        for (KeyLeaf keyLeaf : key.keyLeafs()) {
            uriBuilder.append(prefix);
            prefix = COMMA;
            uriBuilder.append(keyLeaf.leafValue().toString());
        }

        return newNameSpace;
    }

    private static String addNodeNameToUri(NodeKey key,
                                           String curNameSpace,
                                           StringBuilder uriBuilder,
                                           YangSerializerContext context) {
        String newNameSpace = key.schemaId().namespace();
        if (newNameSpace == null) {
            return curNameSpace;
        }

        if (!newNameSpace.equals(curNameSpace)) {
            uriBuilder.append(getModuleNameFromNameSpace(context, newNameSpace));
            uriBuilder.append(COLON);
        }
        uriBuilder.append(key.schemaId().name());
        uriBuilder.append(SLASH);

        return newNameSpace;
    }
}
