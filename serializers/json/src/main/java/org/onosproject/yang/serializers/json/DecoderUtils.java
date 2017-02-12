/*
 *  Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang.serializers.json;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.onosproject.yang.model.DataNode;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.YangSerializerContext;
import org.onosproject.yang.runtime.helperutils.SerializerHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Utilities for parsing URI and JSON strings.
 */
public final class DecoderUtils {
    private static final Splitter SLASH_SPLITTER = Splitter.on('/');
    private static final Splitter COMMA_SPLITTER = Splitter.on(',');
    private static final String EQUAL = "=";
    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String URI_ENCODING_CHAR_SET = "ISO-8859-1";
    private static final String ERROR_LIST_MSG = "List/Leaf-list node should be " +
            "in format \"nodeName=key\"or \"nodeName=instance-value\"";


    // no instantiation
    private DecoderUtils() {
    }

    /**
     * Converts a URI string to resource identifier.
     *
     * @param uriString given URI
     * @param context   YANG schema context information
     * @return resource ID
     */
    public static ResourceId convertUriToRid(String uriString,
                                             YangSerializerContext context) {
        if (uriString == null || uriString.isEmpty()) {
            return null;
        }

        List<String> paths = urlPathArgsDecode(SLASH_SPLITTER.split(uriString));

        if (!paths.isEmpty()) {
            ResourceId.Builder ridBuilder =
                    SerializerHelper.initializeResourceId(context);
            processPathSegments(paths, ridBuilder);
            return ridBuilder.build();
        }

        return null;
    }

    /**
     * Converts JSON data to a data node. This method should be used when
     * the URI corresponding to the JSON body is null (Thus the caller can
     * only provide a serializer context rather than a resource ID).
     *
     * @param rootNode given JSON data
     * @param context  YANG serializer context corresponding
     *                 to the target data node
     * @return data node
     */
    public static DataNode convertJsonToDataNode(ObjectNode rootNode,
                                                 YangSerializerContext context) {
        if (rootNode == null || context == null) {
            return null;
        }

        DataNode.Builder dataNodeBuilder = SerializerHelper.
                initializeDataNode(context);

        JsonWalker jsonWalker = new DefaultJsonWalker(dataNodeBuilder);
        jsonWalker.walkJsonNode(null, rootNode);

        return dataNodeBuilder.build();
    }

    /**
     * Converts JSON data to a data node. This method should be used when
     * the JSON body has a valid URI associated with it (so that the caller
     * can convert the URI to a resource ID).
     *
     * @param rootNode   given JSON data
     * @param ridBuilder resource ID builder corresponding
     *                   to the target data node
     * @return data node
     */
    public static DataNode convertJsonToDataNode(ObjectNode rootNode,
                                                 ResourceId.Builder ridBuilder) {
        if (rootNode == null || ridBuilder == null) {
            return null;
        }

        DataNode.Builder dataNodeBuilder = SerializerHelper.
                initializeDataNode(ridBuilder);

        JsonWalker jsonWalker = new DefaultJsonWalker(dataNodeBuilder);
        jsonWalker.walkJsonNode(null, rootNode);

        return dataNodeBuilder.build();
    }

    /**
     * Converts a list of path from the original format to ISO-8859-1 code.
     *
     * @param paths the original paths
     * @return list of decoded paths
     */
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
            throw new SerializerException("Invalid URL path arg '" +
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
            throw new SerializerException(ERROR_LIST_MSG);
        }

        if (keyStr.contains(COMMA)) {
            List<String> keys = Lists.
                    newArrayList(COMMA_SPLITTER.split(keyStr));
            SerializerHelper.addToResourceId(builder, nodeName, namespace, keys);
        } else {
            SerializerHelper.addToResourceId(builder, nodeName, namespace,
                                             Lists.newArrayList(keyStr));
        }
    }

    private static void addLeaf(String nodeName,
                                String namespace,
                                ResourceId.Builder builder) {
        checkNotNull(nodeName);
        SerializerHelper.addToResourceId(builder, nodeName, namespace, "");
    }

    /**
     * Returns the previous segment of a path which is separated by a split char.
     * For example:
     * <pre>
     * "foo:bar", ":"   -->  "foo"
     * </pre>
     *
     * @param path      the original path string
     * @param splitChar char used to split the path
     * @return the previous segment of the path
     */
    private static String getPreSegment(String path, String splitChar) {
        int idx = path.indexOf(splitChar);
        if (idx == -1) {
            return null;
        }

        if (path.indexOf(splitChar, idx + 1) != -1) {
            return null;
        }

        return path.substring(0, idx);
    }

    /**
     * Returns the latter segment of a path which is separated by a split char.
     * For example:
     * <pre>
     * "foo:bar", ":"   -->  "bar"
     * </pre>
     *
     * @param path      the original path string
     * @param splitChar char used to split the path
     * @return the latter segment of the path
     */
    private static String getLatterSegment(String path, String splitChar) {
        int idx = path.indexOf(splitChar);
        if (idx == -1) {
            return path;
        }

        if (path.indexOf(splitChar, idx + 1) != -1) {
            return null;
        }

        return path.substring(idx + 1);
    }
}
