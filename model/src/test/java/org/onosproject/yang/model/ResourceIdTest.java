/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.onosproject.yang.model;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test resource ID.
 */

public class ResourceIdTest {

    @Test
    public void resourceIdConstruction() {
        String sampleResId = "/card=8/port=5,eth/stats";
        String[] resourcePath = sampleResId.split("/");

        ResourceId.Builder resBldr = new ResourceId.Builder();

        int i = 0;
        int j;
        while (i < resourcePath.length) {
            if (resourcePath[i].equals("")) {
                i++;
                continue;
            }

            String[] nameValue = resourcePath[i].split("=");
            resBldr.addBranchPointSchema(nameValue[0], "testNameSpace");
            if (nameValue.length == 1) {
                i++;
                continue;
            }

            String[] keys = nameValue[1].split(",");

            j = 0;
            while (j < keys.length) {
                //TODO: get schema name of key using YANG runtime
                String keyName = getKeyName(nameValue[0], j);
                resBldr.addKeyLeaf(keyName, "testNameSpace", keys[j]);
                j++;
            }
            i++;
        }

        ResourceId res = resBldr.build();
        List<NodeKey> keys = res.nodeKeys();
        assertEquals("invalid augmented node created", "card",
                     keys.get(0).schemaId().name());
        assertEquals("invalid augmented node created", ListKey.class,
                     keys.get(0).getClass());
        ListKey listKey = (ListKey) keys.get(0);
        assertEquals("invalid augmented node created", "slot",
                     listKey.keyLeafs().get(0).leafSchema().name());
    }

    private String getKeyName(String s, int j) {
        if (s.equals("card")) {
            return "slot";
        }
        if (s.equals("port")) {
            if (j == 0) {
                return "portno";
            }
        }
        return "type";
    }

}
