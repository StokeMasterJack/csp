package com.tms.csp.dnnf;

import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.data.CspSample;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.ssutil.Path;
import com.tms.csp.util.CspBaseTest2;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

//efc dnnf-compile Delta: 64715
public class PL2DnnfDirect1 extends CspBaseTest2 {

    String factory2 = "csp/g/ProdFactoryRules.txt";

    public Path tempXmlFile2 = new Path("/Users/dford/Downloads/tmp.xml");

    @Test
    public void tiny() throws Exception {
        int expectedSatCount = 3;
        CspSample sample = CspSample.TinyNoDc;


        String clob = sample.loadText();
        Csp csp = Csp.parse(clob);

        long plSatCount = csp.satCountPL();
        assertEquals(expectedSatCount, plSatCount);


        Exp rough = csp.toDnnf();
        Exp smooth = rough.getSmooth();

        assert smooth.checkDnnf();


        long satCount = smooth.getSatCount();

//        writeTempXml(tempXmlFile2, smooth);


        assertEquals(expectedSatCount, satCount);

    }

    @Test
    public void trim() throws Exception {
        int expectedSatCount = 11;
        CspSample sample = CspSample.Trim;

        String clob = sample.loadText();
        Csp csp = Csp.parse(clob);

        long plSatCount = csp.satCountPL();
        assertEquals(expectedSatCount, plSatCount);

        Exp rough = csp.toDnnf();
        Exp smooth = rough.getSmooth();

        assert smooth.checkDnnf();


        long satCount = smooth.getSatCount();

//        writeTempXml(tempXmlFile2, smooth);


        assertEquals(expectedSatCount, satCount);
    }


    @Test
    public void trimColor() throws Exception {

        int expectedSatCount = 227;
        CspSample sample = CspSample.TrimColor;

        String clob = sample.loadText();
        Csp csp = Csp.parse(clob);

        long plSatCount = csp.satCountPL();
        System.err.println("plSatCount[" + plSatCount + "]");
        assertEquals(expectedSatCount, plSatCount);

//        csp.print();

        Exp rough = csp.toDnnf();

        Exp smooth = rough.getSmooth();

        assert smooth.checkDnnf();

        long satCount = smooth.getSatCount();
        Set<Cube> cubesSmooth = smooth.getCubesSmooth();

//        writeTempXml(tempXmlFile2, smooth);


        assertEquals(expectedSatCount, satCount);
    }

    @Test
    public void trimColorOptions() throws Exception {

        int expectedSatCount = 22472;
        CspSample sample = CspSample.TrimColorOptions;

        String clob = sample.loadText();
        Csp csp = Csp.parse(clob);


        long plSatCount = csp.satCountPL();
        System.err.println("plSatCount[" + plSatCount + "]");
//        assertEquals(expectedSatCount, plSatCount);

//        csp.print();

        Exp rough = csp.toDnnf();


        Exp smooth = rough.getSmooth();
        writeTempXml(tempXmlFile2, smooth);

        assert smooth.checkDnnf();

        long satCount = smooth.getSatCount();
        Set<Cube> cubesSmooth = smooth.getCubesSmooth();
        System.err.println("cubesSmooth[" + cubesSmooth.size() + "]");

//        writeTempXml(tempXmlFile2, smooth);


        assertEquals(expectedSatCount, satCount);
    }

    @Test
    public void camry() throws Exception {

        int expectedSatCount = 520128;
        CspSample sample = CspSample.Camry2011NoDc;

        String clob = sample.loadText();
        Csp csp = Csp.parse(clob);

        Exp rough = csp.toDnnf();
        Exp smooth = rough.getSmooth();

        assert smooth.checkDnnf();

        long satCount = smooth.getSatCount();

//        writeTempXml(tempXmlFile2, smooth);


        assertEquals(expectedSatCount, satCount);
    }


    @Test
    public void efc() throws Exception {
        long expectedSatCount = 1947899197814472704L;

        long t1 = System.currentTimeMillis();


        CspSample sample = CspSample.ProdFactoryRules;
        long t2 = System.currentTimeMillis();

        Csp csp1 = sample.csp();
        long t3 = System.currentTimeMillis();

        csp1.maybeAddAlwaysTrueVars();
        csp1.addConstraint("YR_2014");
        long t4 = System.currentTimeMillis();

        csp1.propagate();
        long t5 = System.currentTimeMillis();

        Csp csp2 = csp1.reduce();
        long t6 = System.currentTimeMillis();

        Exp rough = csp2.toDnnf();
        long t7 = System.currentTimeMillis();

        Exp smooth = rough.getSmooth();
        long t8 = System.currentTimeMillis();

        long satCount = smooth.getSatCount();

        assertEquals(expectedSatCount, satCount);

        System.err.println("load: " + (t2 - t1));
        System.err.println("parseCsp: " + (t3 - t2));
        System.err.println("csp.assign: " + (t4 - t3));
        System.err.println("csp.propagate: " + (t5 - t4));
        System.err.println("csp.reduce: " + (t6 - t5));
        System.err.println("dnnf compile rough: " + (t7 - t6));
        System.err.println("dnnf compile smooth: " + (t8 - t7));


    }

    private void both(CspSample sample, int expectedSatCount) {

        String clob = sample.loadText();
        Csp csp = Csp.parse(clob);

        Exp rough = csp.toDnnf();
        Exp smooth = rough.getSmooth();

        assert smooth.checkDnnf();

        writeTempXml(tempXmlFile2, smooth);

        long satCount = smooth.getSatCount();

        if (expectedSatCount != -1) {
            assertEquals(expectedSatCount, satCount);
        } else {
            //ok
        }
    }


}