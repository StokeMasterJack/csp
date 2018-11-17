package com.tms.csp.util;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.tms.csp.ast.*;
import com.tms.csp.data.CspSample;
import com.tms.csp.data.TestData;
import com.tms.csp.fm.dnnf.Dnnf;
import com.tms.csp.ssutil.Path;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.Charset;

public class CspBaseTest2 extends CspBaseTest {

    static String getTmpDir() {
        return System.getProperty("java.io.tmpdir");
    }

    public Path tempXmlFile = new Path(getTmpDir() + "/tmp.xml");
//    public Path cspDir = new Path("/r/dev/logic-engine/csp-parent/csp/src/test/resources/testData/");

    Charset UTF8 = Charsets.UTF_8;


    public void writeTempXml(Exp n) {
        writeTempXml(null, n.toXml());
    }

    public void writeTempXml(Path tempXmlFile, Exp n) {
        writeTempXml(tempXmlFile, n.toXml());
    }

    public void writeTempXml(Path tempXmlFile, String clob) {
        if (tempXmlFile == null) {
            tempXmlFile = this.tempXmlFile;
        }
        writeText(tempXmlFile, clob);
    }

//    Space loadPLConstraints(CspSample cspSample) {
//        return TestData.loadSpaceNnf(cspSample);
//    }


    protected Exp loadAndParse(CspSample cspSample) throws Exception {
        String clob = TestData.loadText(cspSample);
        Space ss = new Space();
        Parser parser = ss.parser;
        return parser.parseExp(clob);
    }

//    public String loadText(Path dir, String name) {
//        return loadText(dir.append(name));
//    }
//
//    public void writeText(Path dir, String name, String clob) {
//        Path fCon = dir.append(name);
//        writeText(fCon, clob);
//    }
//
//    public void writeText(Path p, String clob) {
//        File fCon = new File(p.toString());
//        try {
//            Files.write(clob, fCon, UTF8);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public String loadText(String file) {
        File f = new File(file);
        try {
            return Files.toString(f, UTF8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public String loadText(Path p) {
//        File fCon = new File(p.toString());
//        try {
//            return Files.toString(fCon, UTF8);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public SerAppender fileAppender(Ser a) throws Exception {
//        Path fCon = a.getFilePath();
//        return new SerAppender(fCon);
//    }

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
//        File fCon = CspSample.getCspFile(cspDir, name);
//        BufferedWriter aa = Files.newWriter(fCon, Charsets.UTF_8);
//        Ser a = new Ser(aa);
//        space.serialize(a);
//        aa.close();
//    }


    public Csp loadCsp(CspSample cspSample, boolean ignore) {
        return loadCsp(cspSample);
    }

    public Csp loadCsp(CspSample cspSample) {
        return cspSample.csp();

    }


    @Nonnull
    public Csp loadCsp(Path resourcePath) {
        String clob = loadResource(resourcePath);
        return Csp.parse(clob);
    }


    @Nonnull
    public Exp loadDnnf(CspSample cspSample) {
        return Csp.compileDnnf(cspSample.loadText());
    }

//    public Exp loadDnnf(String clob) {
//        return Exp.parseTinyDnnf(clob);
//    }

    @Nonnull
    public Exp loadDnnfTinyNoDc() {
        return loadDnnf(CspSample.TinyNoDc);
    }

    @Nonnull
    public Exp loadDnnfTrim() {
        return Csp.compileDnnf(CspSample.TrimNoDc);
    }

    @Nonnull
    public Exp loadDnnfTrimColor() {
        return Csp.compileDnnf(CspSample.TrimColorNoDc);
    }

    @Nonnull
    public Exp loadDnnfTrimColorOptions() {
        return Csp.compileDnnf(CspSample.TrimColorOptionsDc);
    }

    @Nonnull
    public Exp loadDnnfCamry() {
        return Csp.compileDnnf(CspSample.Camry2011NoDc);
    }

    @Nonnull
    public Exp loadDnnfEfc() {
        String tiny = loadResource(efcOriginalDnnf);
        return Exp.Companion.compileDnnf(tiny);
    }

    public Exp loadDnnfTundra() {
        return Csp.compileDnnf(CspSample.Tundra);
    }



    public Dnnf loadCspTiny() {
        Csp csp = Csp.parse(CspSample.TinyDc);
        return csp.toDnnfCsp();
    }

    public Dnnf loadCspTrim() {
        return new Dnnf(loadDnnfTrim());
    }

    public Dnnf loadCspTrimColor() {
        return new Dnnf(loadDnnfTrimColor());
    }

    public Dnnf loadCspTrimColorOptions() {
        return new Dnnf(loadDnnfTrimColorOptions());
    }

    public Dnnf loadCspCamry() {
        return new Dnnf(loadDnnfCamry());
    }

//    public Space loadSpaceEfc() {
//        String clob = loadText(efcOriginalFile);
//        return new Space(clob);
//    }
//
//    public Space loadSpaceTrim() {
//        String clob = CspSample.Trim.loadText();
//        return new Space(clob);
//    }

//    public ComboCsp parseComboEfc() {
//        return parseCombo(efcOriginalFile, efcInvQty);
//    }


    protected Dnnf loadCspEfc() {
        String clob = loadResource(efcOriginalDnnf);
        Exp n = Exp.Companion.parseTinyDnnf(clob);
        return new Dnnf(n);
    }

    protected Dnnf loadCspEfcAT1() {
        return loadCspEfc();
    }


}
