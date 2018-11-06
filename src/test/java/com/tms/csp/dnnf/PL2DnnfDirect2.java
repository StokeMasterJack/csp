package com.tms.csp.dnnf;

import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Space;
import com.tms.csp.data.CspSample;
import com.tms.csp.ssutil.Path;
import com.tms.csp.ssutil.TT;
import com.tms.csp.util.CspBaseTest2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

//efc dnnf-compile Delta: 64715
public class PL2DnnfDirect2 extends CspBaseTest2 {


    //pl2dnnf delta: 39s
    //satCount: 1545337914624
    @Test
    public void tundra() throws Exception {
        CspSample sample = CspSample.Tundra;
        long satCount = both(sample, -1);
        System.err.println("satCount[" + satCount + "]");
    }

    @Test
    public void tundra2() throws Exception {
        TT tt = new TT();
        String clob = CspSample.Tundra.loadText();
        tt.t("load text");

        Csp csp = Csp.parse(clob);
        //csp.at();   //this makes a huge difference formula speed - at slows it way down todo
        tt.t("parse text");

        Exp nRough = csp.toDnnf();
        tt.t("toDnnf");

        Exp nSmooth = nRough.getSmooth();
        tt.t("getSmooth");


        long satCount = nSmooth.getSatCount();
        tt.t("satCountSmooth");

        assertEquals(1545337914624L, satCount);

        Exp nn = nSmooth.copyToOtherSpace();
        String tiny = nn.getSpace().serializeTinyDnnf();

        int dnnfSize = tiny.length();
        System.err.println("dnnfSize[" + dnnfSize + "]");

        Exp nnn = Exp.parseTinyDnnf(tiny);
        assertEquals(1545337914624L, nnn.getSatCount());

    }


    //toDnnf delta:
    //   before formula caching:    142s
    //   after  formula caching:    34s
    //   after xorSplit 1st:        10s     7.5s no -ea
    //   after ExpSet.sameContent:          5.5s no -ea
    //   after PosComplex.containsVarId:    4.5s no -ea
    //   after bucket index                 2.5s no -ea
    @Test
    public void efcOriginal() throws Exception {

        TT tt = new TT();
        String clob = loadResource(efcOriginal);
//        String clob = loadText(efcOriginalFile);
        tt.t("load text");
        Csp csp = Csp.parse(clob);

        Space space = csp.getSpace();

//        csp.at();   //at makes toyota-wide compile slower
        tt.t("parse text");

        Exp nRough = csp.toDnnf();
        tt.t("toDnnf");

        Exp nSmooth = nRough.getSmooth();
        tt.t("getSmooth");

        long satCount = nSmooth.getSatCount();
        tt.t("satCountSmooth");

        assertEquals(3460501125462739908L, satCount);

        Exp nn = nSmooth.copyToOtherSpace();
        tt.t("copyToOtherSpace");

        String tiny = nn.getSpace().serializeTinyDnnf();
        tt.t("serializeTinyDnnf");

//        int dnnfSize = tiny.length();
//        System.err.println("dnnfSize[" + dnnfSize + "]");

        Exp nnn = Exp.parseTinyDnnf(tiny);
        tt.t("parseTinyDnnf");

        satCount = nnn.getSatCount();
        tt.t("satCount");

        assertEquals(3460501125462739908L, satCount);

        space.printPosComplexTableReport();

        System.err.println();


    }


    //pl2dnnf2 delta:    20s    17s
    //toDnnf delta: 55s *
    @Test
    public void efcProdFactoryRules() throws Exception {

        Csp csp = CspSample.EfcProdFactoryRules.csp();
        TT tt = new TT();
        csp.printVarInfo();
        Exp n = csp.toDnnf();

        long satCount = n.getSatCount();

        System.err.println("satCount[" + satCount + "]");


    }


    @Test
    public void serialize11() throws Exception {
        if (skipSlowTests) return;
        Path p = new Path("/Users/dford/temp/csp/testData/2014-02-24/fullfactorystage.txt");
        String clob = loadText(p);
        Csp csp = Csp.parse(clob);
        TT tt = new TT();
        Exp n = csp.toDnnf();
        tt.t("toDnnf");
        long satCount = n.getSatCount();
        tt.t("satCount");
        System.err.println("satCount[" + satCount + "]");
//        assertEquals(8699182891675808002L, satCount);
    }


    private long both(CspSample sample, int expectedSatCount) {

        TT tt = new TT();
        Exp n = pl2dnnf(sample);
        assert n.checkDnnf();
        tt.t("pl2dnnf");

//        writeTempXml(tempXmlFile2, n);

        long satCount = n.getSatCount();


        if (expectedSatCount != -1) {
            assertEquals(expectedSatCount, satCount);
        } else {
            //ok
        }

        return satCount;
    }

    private Exp pl2dnnf(CspSample sample) {
        Csp csp = sample.csp();

//        csp.toNnfKeepXors();
        //        csp.print();

        Exp nn = csp.toDnnf();
        assert nn.checkDnnf();
        return nn;
    }


}