package com.smartsoft.csp.util;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.smartsoft.csp.ast.Csp;
import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.PLConstants;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.data.CspSample;
import com.smartsoft.csp.ssutil.Path;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public class SpaceJvm implements PLConstants {

    public static final Charset UTF8 = Charsets.UTF_8;

//    public static void writeEfcModLocal(Space space, Mod mod, String year, Path cspDir) {
//        Csp csp = space.getCsp();
//        Csp efcModLocal = csp.createEfcModLocal(mod);
//        String serial = efcModLocal.serialize();
//        String localFileName = "efc.mod." + mod.getName() + "." + year + ".csp.txt";
//        writeText(cspDir, localFileName, serial);
//    }

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
        return loadFile(p);
    }

    public static String loadFile(Path p) {
        File f = new File(p.toString());
        try {
            return Files.toString(f, UTF8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Space load(Path fileName) {
        String clob = loadText(fileName);
        return Csp.parse(clob).getSpace();
    }

    public Space load(CspSample sample) {
        Csp csp = Csp.parse(sample.loadText());
        return csp.getSpace();
    }


    public Exp loadTinyDnnf(Path resource) {
        String clob = loadResource(resource);
        return Exp.Companion.parseTinyDnnf(clob);
    }


    public static String loadResource(Path path) {
        String name = path.toStringNoLeadingSlash();
        return loadResource(name);
    }

    public static String loadResource(Class ctxClass, String path) {
        URL url = Resources.getResource(ctxClass, path);
        try {
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadResource(Object ctx, String path) {
        return loadResource(ctx.getClass(), path);
    }

    public static String loadResource(String path) {
        URL url = Resources.getResource(path);
        try {
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
