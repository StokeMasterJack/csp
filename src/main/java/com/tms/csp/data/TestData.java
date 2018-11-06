package com.tms.csp.data;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Space;
import com.tms.csp.ssutil.Path;
import com.tms.csp.transforms.Transformer;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class TestData {

    public static String cspLocalResourceName(CspSample cspSample) {
        return cspSample.getPath().toStringNoLeadingSlash();
    }


    public static Path cspLocalResourcePath(CspSample cspSample) {
        return cspSample.getPath();
    }


    public static String loadText(Class contextClass, CspSample cspSample) {
        String localResourceName = cspLocalResourceName(cspSample);
        URL url = Resources.getResource(contextClass, localResourceName);

        String retVal;
        try {
            retVal = Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return retVal;
    }

    public static String loadTextMvn(CspSample cspSample) {
        String localResourceName = cspLocalResourceName(cspSample);
        URL url = Resources.getResource(localResourceName);

        String retVal;
        try {
            retVal = Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return retVal;
    }

    public static String loadText(CspSample cspSample) {
        String localResourceName = cspLocalResourceName(cspSample);
        URL url = Resources.getResource(localResourceName);
        String retVal;
        try {
            retVal = Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return retVal;
    }

    public static String loadTextForVarInfo(CspSample cspSample) {
        String localResourceName = cspLocalResourceName(cspSample);
        localResourceName = localResourceName.replace(".txt", ".vr-info.txt");
        URL url = Resources.getResource(localResourceName);

        try {
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            return null;
        }

    }


    public static String loadText(String localName) {
        return loadText(null, localName);
    }

    public static String loadResource(Class context, String localResourceName) {
        if (context == null) {
            context = TestData.class;
        }
        URL url = Resources.getResource(context, localResourceName);
        try {
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String loadText(Class context, String localResourceName) {
        if (context == null) {
            context = TestData.class;
        }
        URL url = Resources.getResource(context, localResourceName);
        try {
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static String loadText(Class ctx, String localResourceName) {
//            URL url = Resources.getResource(TestData.class, localResourceName);
//
//            String retVal;
//            try {
//                retVal = Resources.toString(url, Charsets.UTF_8);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            return retVal;
//        }

    public static List<String> loadCspAsTextLines(CspSample cspSample) {
        String linesOneBigString = loadText(cspSample);
        return parseClobIntoLines(linesOneBigString);
    }

    public static Space loadSpace(CspSample cspSample, Transformer transformer) {
        String clob = loadText(cspSample);
        return Csp.parse(clob).getSpace();
    }

    public static Space loadSpace(CspSample cspSample) {
        return loadSpace(cspSample, null);
    }

    public static Space loadSpaceNnf(CspSample cspSample) {
        return loadSpace(cspSample, Transformer.NNF);
    }

    public static List<String> parseClobIntoLines(String clob) {
        String[] lineArray = clob.split("\n");
        return ImmutableList.copyOf(lineArray);
    }


}
