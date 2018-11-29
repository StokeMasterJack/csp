package com.smartsoft.csp.data

import com.smartsoft.csp.ast.Csp
import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ssutil.Path
import com.smartsoft.csp.util.SerFormat
import java.io.File
import java.math.BigInteger

enum class CspSample(
        val fileName: String,
        val expectedSatCount: BigInteger = BigInteger.ZERO,
        val format: SerFormat = SerFormat.PL,
        val vars: String? = null
) {

    //simple PL
    TinyNoDc("tiny-no-dc", expectedSatCount = 3),
    TinyDc("tiny", expectedSatCount = 6),

    TrimNoDc("trim", expectedSatCount = 11),

    TrimColorNoDc("trim-color", expectedSatCount = 227),

    //NodeCounts: CubeExp: 66   DAnd: 39   DOr: 31   DcOr: 6   Lit: 160
    TrimColorOptionsNoDc("trim-color-options-no-dc", expectedSatCount = 22472),
    TrimColorOptionsDc("trim-color-options", expectedSatCount = 44944),


    //NodeCounts: CubeExp: 68   DAnd: 44   DOr: 34   DcOr: 10   Lit: 174
    Camry2011NoDc("camry-2011-no-dc", expectedSatCount = 520128),
    Camry2011Dc("camry-2011", expectedSatCount = 2080512),


    //complex PL
    Tundra("tundra-2013", expectedSatCount = "1545337914624"),

    //NodeCounts: DAnd:4891  DcOr:236  CubeExp:691  DOr:1905  Lit:1564
    //NodeCounts: DAnd:4891  DcOr:236  CubeExp:691  DOr:1905  Lit:1564
    //NodeInfo:   DAnd:4891  DcOr:236  CubeExp:691  DOr:1905  Lit:1564

    //NodeCounts: CubeExp: 691   DAnd: 4891   DOr: 1905   DcOr: 236   Lit: 1564
    //NodeCounts: CubeExp: 691   DAnd: 4891   DOr: 1905   DcOr: 236   Lit: 1564
    //NodeCounts: CubeExp: 766   DAnd: 4039   DOr: 1532   DcOr: 237   Lit: 1564
    //NodeCounts: CubeExp: 756   DAnd: 4702   DOr: 1858   DcOr: 235   Lit: 1564
    //NodeCounts: CubeExp: 756   DAnd: 4702   DOr: 1858   DcOr: 235   Lit: 1564
    //NodeCounts: CubeExp: 756   DAnd: 4702   DOr: 1858   DcOr: 235   Lit: 1564 Node count: 143206
    //NodeCounts: CubeExp: 756   DAnd: 4702   DOr: 1858   DcOr: 235   Lit: 1564 Node count: 143145
    EfcOriginal("efcOriginal/factory.txt", expectedSatCount = "262420340940321044675939268"),

    EfcProdFactoryRules("g/ProdFactoryRules.txt", expectedSatCount = "529696075773177406915015081985"),


    TinyNnf("tiny.nnf"),
    Tiny2("tiny-2"),


    TrimSmall("trim-small"),
    TrimSmallNnf("trim-small.nnf"),
    TrimNnf("trim.nnf"),


    CamryDnnf("camry.dnnf.txt"),

    CamryNnf("camry.nnf"),

    TundraNnf("tundra.nnf"),
    EfcPlVm("efc.pl.vm"),
    EfcPlTiny("efc.pl.tiny"),
    EfcNnf("efc.nnf"),

    EfcOriginalDnnf("efcOriginal/factory.dnnf.txt"),
    EfcDnnf("efc.dnnf.txt"),
    EfcSimple("efc.simple"),
    EfcSimpleAig("efc.simple.sorted.aig"),
    EfcModCore("efc.mod.core"),
    EfcModTrim("efc.mod.trim"),
    EfcModColor("efc.mod.color"),
    EfcModAccessory("efc.mod.accessory"),

    EfcCnf("efc.cnf"),
    EfcCnf5("efc5.cnf"),
    EfcLite("efc-lite"),
    EfcLite2("efc-lite-2"),
    EfcSerial("efc-serial"),
    Complex("complex"),
    ProdFactoryRulesDnnf("g/ProdFactoryRules.dnnf.txt"),

    ComboFactoryPlusInvDnnf("g/combo-factory-plus-inv.dnnf.txt"),

    VarMapTiny("tiny");

    constructor(fileName: String, expectedSatCount: String) : this(fileName, BigInteger(expectedSatCount))
    constructor(fileName: String, expectedSatCount: Long) : this(fileName, BigInteger.valueOf(expectedSatCount))
    constructor(fileName: String, expectedSatCount: Int) : this(fileName, expectedSatCount.toLong())


    //    public static String getCspFileName(String name) {
    //        return name + CSP_FILE_SUFFIX;
    //    }

    val path: Path
        get() {
            val prefix = prefix
            return if (fileName.endsWith(".txt")) {
                prefix.append(fileName)
            } else {
                prefix.append("$fileName.$suffix")
            }
        }

    val suffix: String
        get() = if (isVarMap) {
            "vm.txt"
        } else {
            "csp.txt"
        }


    val isVarMap: Boolean
        get() {
            val n1 = name.toLowerCase()
            val n2 = "varSpace".toLowerCase()
            return n1.contains(n2)
        }

    val prefix: Path
        get() = if (isVarMap) {
            Path("varMaps")
        } else {
            Path("csp")
        }


    fun getCspFilePath(dir: Path): Path {
        return Path(dir, path)
    }


    fun getCspFile(dir: Path): File {
        return File(getCspFilePath(dir).toString())
    }

    fun loadText(contextClass: Class<*>): String {
        return TestData.loadText(contextClass, this)
    }

    fun parseCsp(): Csp {
        val clob = loadText()
        return Csp.parse(clob)
    }

    fun compileDnnf(): Exp {
        val clob = loadText()
        return Csp.compileDnnf(clob)
    }

    fun parseDnnf(): Exp {
        val clob = loadText()
        return Exp.parseTinyDnnf(clob)
    }


    fun loadText(): String {
        return TestData.loadText(this)
    }

    fun loadTextForVarInfo(): String? {
        return TestData.loadTextForVarInfo(this)
    }

    fun loadTextMvn(): String {
        return TestData.loadTextMvn(this)
    }

    fun loadLines(): List<String> {
        val s = loadText()
        return TestData.parseClobIntoLines(s)
    }

    companion object {

        val CSP_FILE_SUFFIX = "csp.txt"

        fun loadClob(cspSample: CspSample): String {
            return TestData.loadText(cspSample)
        }

        val allSimplePL
            get() = listOf(
                    TinyNoDc,
                    TinyDc,
                    TrimNoDc,
                    TrimColorNoDc,
                    TrimColorOptionsNoDc,
                    TrimColorOptionsDc,
                    Camry2011NoDc,
                    Camry2011Dc
            )

        val allComplexPL
            get() = listOf(
                    EfcOriginal,
                    EfcProdFactoryRules,
                    Tundra
            )

        val allPL
            get() = allSimplePL.union(allComplexPL)
    }


}
