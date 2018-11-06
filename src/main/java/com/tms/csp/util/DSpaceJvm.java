package com.tms.csp.util;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.tms.csp.ssutil.Path;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class DSpaceJvm {

    public static final Charset UTF8 = Charsets.UTF_8;

    public static String loadText(Path dir, String name) {
        return loadText(dir.append(name));
    }

    public static void writeText(Path dir, String name, Object clob) {
        writeText(dir, name, clob.toString());
    }

    public static void writeText(Path dir, String name, String clob) {
        Path f = dir.append(name);
        writeText(f, clob);
    }

    public static void writeText(Path p, String clob) {
        File f = new File(p.toStringWithLeadingSlash());
        createParentDirs(f);
        try {
            Files.write(clob, f, UTF8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createParentDirs(File f) {
        try {
            Files.createParentDirs(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadText(Path p) {
        File f = new File(p.toString());
        try {
            return Files.toString(f, UTF8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
