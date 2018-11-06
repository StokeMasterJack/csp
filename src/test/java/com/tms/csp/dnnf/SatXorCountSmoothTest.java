package com.tms.csp.dnnf;

import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.data.CspSample;
import com.tms.csp.util.CspBaseTest2;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SatXorCountSmoothTest extends CspBaseTest2 {

    @Test
    public void tiny() throws Exception {
        Csp csp = Csp.parse(CspSample.TinyNoDc);


        Exp dNode = csp.toDnnf();

        dNode = dNode.getSmooth();

        assertEquals(3, dNode.getSatCount());

    }


    @Test
    public void trim() throws Exception {
        Csp csp = Csp.parse(CspSample.Trim);
        Exp dNode = csp.toDnnf();
        System.err.println(dNode);


        System.err.println("isSmooth[" + dNode.isSmooth() + "]");
        dNode = dNode.getSmooth();
        System.err.println("isSmooth[" + dNode.isSmooth() + "]");
        System.err.println();

        assertEquals(11, dNode.getSatCount());
    }


    @Test
    public void trimColor() throws Exception {
        Csp csp = Csp.parse(CspSample.TrimColor);
        Exp dNode = csp.toDnnf();


        System.err.println("isSmooth[" + dNode.isSmooth() + "]");
        dNode = dNode.getSmooth();
        System.err.println("isSmooth[" + dNode.isSmooth() + "]");
        System.err.println();

        assertEquals(227, dNode.getSatCount());
    }


    @Test
    public void trimColorOptions() throws Exception {
        Csp csp = Csp.parse(CspSample.TrimColorOptions);
        Exp dNode = csp.toDnnf();


        System.err.println("isSmooth[" + dNode.isSmooth() + "]");
        dNode = dNode.getSmooth();

        assert dNode.isSmooth();
        System.err.println("isSmooth[" + dNode.isSmooth() + "]");
        System.err.println();

        assert dNode.isSmooth();
        System.err.println("dNode.getOp1()[" + dNode.getOp() + "]");
        assertEquals(22472, dNode.getSatCount()); //22832
    }


    @Test
    public void camry() throws Exception {
        Csp csp = Csp.parse(CspSample.Camry2011NoDc);
        Exp dNode = csp.toDnnf();

        dNode = dNode.getSmooth();
        long satCount = dNode.getSatCount();
        assertEquals(520128, satCount);

    }

    @Test
    public void tundra() throws Exception {

        Csp csp = Csp.parse(CspSample.Tundra);

        Exp dNode = csp.toDnnf();


        dNode = dNode.getSmooth();
        long satCount = dNode.getSatCount();
        assertEquals(1545337914624L, satCount);

        String tinyDnnf = dNode.gc().getSpace().serializeTinyDnnf();


    }


    Exp loadAndCompile(CspSample sample) {
        String clob = loadText(sample);
        Csp csp = Csp.parse(clob);
        return csp.toDnnf();
    }


    public Exp transform(Exp n) {
        n = n.flatten();
        n.flatten();
        return n;
    }


}