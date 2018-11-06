package com.tms.csp.data;

import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Parser;
import com.tms.csp.ssutil.Path;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;
import java.util.Set;

public enum CspSample {

    Tiny("tiny"),
    TinyNnf("tiny.nnf"),
    Tiny2("tiny-2"),
    TinyNoDc("tiny-no-dc"),
    Trim("trim-prefix"),
    TrimSmall("trim-small"),
    TrimSmallNnf("trim-small.nnf"),
    TrimNnf("trim.nnf"),
    TrimColor("trim-color"),
    TrimColorOptions("trim-color-options"),
    TrimColorOptionsNoDc("trim-color-options-no-dc"),
    CamryDnnf("camry.dnnf.txt"),
    Camry2011("camry-2011"),
    Camry2011NoDc("camry-2011-no-dc"),
    CamryNnf("camry.nnf"),
    Tundra("tundra-2013"),
    TundraNnf("tundra.nnf"),
    EfcPlVm("efc.pl.vm"),
    EfcPlTiny("efc.pl.tiny"),
    EfcNnf("efc.nnf"),
    Efc("efc"),
    EfcOriginal("efcOriginal/factory.txt"),
    EfcOriginalDnnf("efcOriginal/factory.dnnf.txt"),
    EfcDnnf("efc.dnnf.txt"),
    EfcSimple("efc.simple"),
    EfcSimpleAig("efc.simple.sorted.aig"),
    EfcModCore("efc.mod.core"),
    EfcModTrim("efc.mod.trim"),
    EfcModColor("efc.mod.color"),
    EfcModAccessory("efc.mod.accessory"),
    EfcProdFactoryRules("g/ProdFactoryRules.txt"),
    EfcCnf("efc.cnf"),
    EfcCnf5("efc5.cnf"),
    EfcLite("efc-lite"),
    EfcLite2("efc-lite-2"),
    EfcSerial("efc-serial"),
    Complex("complex"),
    ProdFactoryRulesDnnf("g/ProdFactoryRules.dnnf.txt"),
    ProdFactoryRules("g/ProdFactoryRules.txt"),
    ComboFactoryPlusInv("g/combo-factory-plus-inv.dnnf.txt"),

    VarMapTiny("tiny");

    public static final String CSP_FILE_SUFFIX = "csp.txt";

    String fileName;

    CspSample(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }


//    public static String getCspFileName(String name) {
//        return name + CSP_FILE_SUFFIX;
//    }

    public Path getPath() {
        Path prefix = getPrefix();
        if (fileName.endsWith(".txt")) {
            return prefix.append(fileName);
        } else {
            return prefix.append(fileName + "." + getSuffix());
        }
    }

    public String getSuffix() {
        if (isVarMap()) {
            return "vm.txt";
        } else {
            return "csp.txt";
        }
    }

    public Path getCspFilePath(Path dir) {
        return new Path(dir, getPath());
    }


    public File getCspFile(Path dir) {
        return new File(getCspFilePath(dir).toString());
    }

    public static String loadClob(CspSample cspSample) {
        return TestData.loadText(cspSample);
    }

    public String loadText(Class contextClass) {
        return TestData.loadText(contextClass, this);
    }

    @Nonnull
    public Csp csp() {
        String clob = loadText();
        return Csp.parse(clob);
    }

    public Exp compileDnnf() {
        String clob = loadText();
        return Csp.compileDnnf(clob);
    }

    public Exp parseDnnf() {
        String clob = loadText();
        return Exp.parseTinyDnnf(clob);
    }


    public String loadText() {
        return TestData.loadText(this);
    }

    public String loadTextForVarInfo() {
        return TestData.loadTextForVarInfo(this);
    }

    public String loadTextMvn() {
        return TestData.loadTextMvn(this);
    }

    public List<String> loadLines() {
        String s = loadText();
        return TestData.parseClobIntoLines(s);
    }


    public boolean isVarMap() {
        String n1 = name().toLowerCase();
        String n2 = "varSpace".toLowerCase();
        return n1.contains(n2);
    }

    public Path getPrefix() {
        if (isVarMap()) {
            return new Path("varMaps");
        } else {
            return new Path("csp");
        }
    }

}
