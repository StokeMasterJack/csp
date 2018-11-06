package com.tms.csp.util;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.tms.csp.ast.*;
import com.tms.csp.data.CspSample;
import com.tms.csp.data.TestData;
import com.tms.csp.ssutil.Path;
import org.junit.Before;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.LogManager;

public abstract class CspBaseTest extends SpaceJvm implements PLConstants {

    public static boolean skipSlowTests = true;

    public static final String VAR_INFO_S = "csp/inventory/varInfo.txt";
    public static final Path VAR_INFO = new Path("csp/inventory/varInfo.txt");

    public static final Path LOGGING_CONFIG_RESOURCE_NAME = new Path("csp/logging.properties");

    public static Path cspWriteDir = new Path(System.getProperty("user.home") + "/temp/csp/");
    public static Path cspRDir = new Path("csp/");

//    public static Path dnfDir = cspDir.append("dnnf");

    //    public Path efcInv = new Path(cspDir, "invClob/inv_export_toyota.txt");
//    public Path efcInvQty = new Path(cspDir, "invClob/inventory.txt");
//    public Path efcOriginalFile = new Path(cspDir, "original/EFC_CONSTRAINTS_NEW_FORMAT.txt");
    public Path efcOriginal = new Path("csp/efcOriginal/factory.txt");
    public Path efcOriginalDnnf = new Path("csp/efcOriginal/factory.dnnf.txt");
    public Path efcProdFactoryRules = new Path("csp/g/ProdFactoryRules.txt");

//    public static Path dnnfEfc = new Path(dnfDir, "efc.dnnf.txt");
//    public static Path dnnfEfcFromSpace = new Path(dnfDir, "efc.dnnf-from-space-csp.txt");
//
//    protected Parser createParser(Space space) {
//        return space.getParser();
//    }

    protected Parser createParser(Space space) {
        return space.parser;
    }


//    protected String loadText(String fileName) {
//        return loadText(cspDir, fileName);
//    }


    protected String loadText(CspSample sample) {
        return sample.loadText();
    }

//    protected Space load(String fileName) {
//        String txt = loadText(fileName);
//        return new Space(txt);
//    }

    protected Exp loadAndParse(CspSample cspSample) throws Exception {
        String clob = TestData.loadText(cspSample);
        Space space = new Space();
        return space.parser.parseExp(clob);
    }


    public SerAppender fileAppender(Ser a) throws Exception {
        Path f = a.getFilePath();
        return new SerAppender(f);
    }

    public static class SerAppender implements Appendable, Ser.Closer {

        private final static Charset utf = Charsets.UTF_8;

        private final BufferedWriter delegate;


        public SerAppender(Path path) {
            this.delegate = newWriter(path);
        }

        private static BufferedWriter newWriter(Path p) {
            File f = new File(p.toString());
            try {
                return Files.newWriter(f, utf);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Appendable append(CharSequence csq) throws IOException {
            return delegate.append(csq);
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            return delegate.append(csq, start, end);
        }

        @Override
        public Appendable append(char c) throws IOException {
            return delegate.append(c);
        }

        @Override
        public void closeQuietly() {
            if (delegate != null) {
                try {
                    delegate.flush();
                    delegate.close();
                } catch (IOException e) {
                    //
                }
            }
        }
    }


    public void closeSer(Ser ser) throws Exception {
        Appendable a = ser.appendable;
        if (a != null && a instanceof Closeable) {
            Closeable cc = (Closeable) a;
            cc.close();
        }
    }

//    public void serializeToFile(Space space) throws IOException {
//        String name = space.getName();
//        File f = CspSample.getCspFile(cspDir, name);
//        BufferedWriter aa = Files.newWriter(f, Charsets.UTF_8);
//        Ser a = new Ser(aa);
//        space.serialize(a);
//        aa.close();
//    }


//    Space createPLSpace() {
//        return new Space();
//    }

    public void writeText(Path path, Object clob) {
        writeText(path, clob.toString());
    }

    public void writeText(String fileName, Object clob) {
        writeText(fileName, clob.toString());
    }

    public void writeText(String fileName, String clob) {
        writeText(cspWriteDir, fileName, clob);
    }

    public static String loadResource(String fullResourceName) {
        URL url = Resources.getResource(fullResourceName);
        try {
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadResource(Class contextClass, String localResourceName) {
        System.err.println("contextClass[" + contextClass + "]");
        System.err.println("localResourceName[" + localResourceName + "]");

        URL url = Resources.getResource(contextClass, localResourceName);
        try {
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadResource(Object contextObject, String localResourceName) {
        Class contextClass;
        if (contextObject instanceof Class) {
            contextClass = (Class) contextObject;
        } else {
            contextClass = contextObject.getClass();
        }
        return loadResource(contextClass, localResourceName);
    }

    public static String loadLogConfig() {
        return loadResource(LOGGING_CONFIG_RESOURCE_NAME.toStringNoLeadingSlash());
    }

    @Before
    public void initLogging() throws Exception {
        try {
            String config = loadLogConfig();
            InputStream is = new StringBufferInputStream(config);
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException ex) {
            System.out.println("WARNING: Could not open configuration file");
            System.out.println("WARNING: Logging not configured (console output only)");
        }
    }


}
