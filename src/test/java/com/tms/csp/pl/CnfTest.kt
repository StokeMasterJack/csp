package com.tms.csp.pl

import com.tms.csp.ast.Csp
import com.tms.csp.data.CspSample
import com.tms.csp.util.CspBaseTest2
import org.junit.Test

class CnfTest : CspBaseTest2() {

    /*
    name1[csp/camry-2011-no-dc.csp.txt]
    name2[csp/efcOriginal/factory.txt]
     */

    @Test
    @Throws(Exception::class)
    fun test1() {
        //        String clob1 = loadText(CspSample.Camry2011NoDc);
        //        String clob1 = "ppop";


        val clob1 = CspSample.EfcOriginal.loadText()
        System.err.println("clob1 loaded")

        val clob2 = CspSample.Camry2011NoDc.loadText()
        System.err.println("clob2 loaded")


        //        String clob2 = loadResource(path2);
        //        String clob = SpaceJvm.loadResource(CspSample.Camry2011NoDc.getPath());

        if (true) return


        //
        //        URL url1 = Resources.getResource(localResourceName);
        //
        //        Space space = new Space(clob1);
        //        space.toCnf();
        //        String dimacs = space.serializeDimacs();
        //
        //        System.err.println(dimacs);
        //
        //        long satCount = space.toDnnf().getSmooth().getSatCount();
        //
        //        System.err.println("satCount[" + satCount + "]");  //satCount[520128]
    }

    @Test
    @Throws(Exception::class)
    fun test2() {

        val csp = CspSample.Tundra.csp()
        csp.toCnf()
        val dimacs = csp.serializeDimacs()

        System.err.println(dimacs)


        val satCount = csp.toDnnf().smooth.satCount

        System.err.println("satCount[$satCount]")  //satCount[520128]
    }


    /**
     * This is to make something small enough would actually complete when converting to CNF
     */
    private fun buildEfc2013MinusTundraPL(): Csp {
        val clob = CspSample.EfcOriginal.loadText()
        var csp1 = Csp.parse(clob)
        csp1 = csp1.refine("YR_2013", "!YR_2014")
        csp1.simplifySeriesModelAnds()
        csp1 = csp1.refine("!SER_tundra")
        csp1.simple!!.clear()
        return csp1
    }


    @Test
    @Throws(Exception::class)
    fun testEfc2013MinusTundraToCnf() {
        val csp1 = buildEfc2013MinusTundraPL()
        csp1.toCnf()
        val dimacs = csp1.serializeDimacs()
        System.err.println(dimacs)
    }

    @Test
    @Throws(Exception::class)
    fun testEfc2013MinusTundraToDnnfPlusSatCount() {
        val csp1 = buildEfc2013MinusTundraPL()

        val exp = csp1.toDnnf().smooth
        val satCount = exp.satCount

        System.err.println("satCount[$satCount]")
        System.err.println()
        System.err.println()
        System.err.println(exp.serializeTinyDnnfSpace())


    }


}
