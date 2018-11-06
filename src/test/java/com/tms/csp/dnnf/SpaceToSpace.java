package com.tms.csp.dnnf;

import com.tms.csp.util.CspBaseTest2;
import com.tms.csp.data.CspSample;
import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SpaceToSpace extends CspBaseTest2 {

    @Test
    public void serializeAndParseTiny() throws Exception {
        long expectedSatCount = 3L;
        Csp csp = loadCsp(CspSample.TinyNoDc);
        serializeAndParse(expectedSatCount, csp);
    }

    @Test
    public void serializeAndParseTrim() throws Exception {
        long expectedSatCount = 11;
        Csp csp = loadCsp(CspSample.Trim);
        serializeAndParse(expectedSatCount, csp);
    }

    @Test
    public void serializeAndParseTrimColor() throws Exception {
        long expectedSatCount = 227;
        Csp csp = loadCsp(CspSample.TrimColor);
        serializeAndParse(expectedSatCount, csp);
    }

    @Test
    public void serializeAndParseTrimColorOptions() throws Exception {
        long expectedSatCount = 22472;
        Csp csp = loadCsp(CspSample.TrimColorOptions);
        serializeAndParse(expectedSatCount, csp);
    }

    @Test
    public void serializeAndParseCamry() throws Exception {
        long expectedSatCount = 520128L;
        Csp csp = loadCsp(CspSample.Camry2011NoDc);
        serializeAndParse(expectedSatCount, csp);
    }

    @Test
    public void serializeAndParseEfc() throws Exception {
        long expectedSatCount = 3460501125462739908L;
        Csp csp = loadCsp(efcOriginal);
        serializeAndParse(expectedSatCount, csp);
    }


    public void serializeAndParse(long expectedSatCount, Csp csp) throws Exception {

//        csp.toNnf(); //good
//        csp.toNnf2(); //good
//        csp.toBnf();   //good
//        csp.toNnfKeepXors();//fail
//        csp.toNnfKeepXors2();  //fail
//        csp.toBnfKeepXors();     //fail

        Exp n = csp.toDnnf().getSmooth();
        long satCount1 = n.getSatCount();

        Exp nn = n.copyToOtherSpace();
        assert n.getSpace() != nn.getSpace();
        long satCount2 = nn.getSatCount();

        String tiny1 = nn.getSpace().serializeTinyDnnf();
        Exp nnn = Exp.parseTinyDnnf(tiny1);

        String tiny2 = nnn.getSpace().serializeTinyDnnf();
        assertEquals(tiny1, tiny2);

        long satCount3 = nnn.getSatCount();

        assertEquals(expectedSatCount, satCount1);
        assertEquals(expectedSatCount, satCount2);
        assertEquals(expectedSatCount, satCount3);   //this one fails - comes out double

    }


}