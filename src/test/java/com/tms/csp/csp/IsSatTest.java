package com.tms.csp.csp;

import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.data.CspSample;
import com.tms.csp.util.CspBaseTest2;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class IsSatTest extends CspBaseTest2 {

    @Test
    public void testTiny() throws Exception {
        Csp csp = loadCsp(CspSample.Tiny);
        assertTrue(csp.isSat());
    }

    @Test
    public void testTrim() throws Exception {
        Csp csp = loadCsp(CspSample.Trim);
        assertTrue(csp.isSat());
    }

    @Test
    public void testTrimColor() throws Exception {
        Csp csp = loadCsp(CspSample.TrimColor);
        assertTrue(csp.isSat());
    }

    @Test
    public void testTrimColorOptions() throws Exception {
        Csp csp = loadCsp(CspSample.TrimColorOptions, true);
        List<Exp> xors = csp.getXorConstraints();
        for (Exp xor : xors) {
            System.err.println(xor);
        }
        assertTrue(csp.isSat());
    }

    @Test
    public void testCamry2011() throws Exception {
        Csp csp = loadCsp(CspSample.Camry2011, true);
        assertTrue(csp.isSat());
    }

    //591 millis
    @Test
    public void testEfc1() throws Exception {
        Csp csp = loadCsp(efcOriginal);
        long t1 = System.currentTimeMillis();
        assertTrue(csp.isSat());
        long t2 = System.currentTimeMillis();
        System.err.println("efc1.isSat Delta: " + (t2 - t1));
    }

    //859 millis
    @Test
    public void testEfc2() throws Exception {
        Csp csp = CspSample.EfcProdFactoryRules.csp();
        long t1 = System.currentTimeMillis();
        assertTrue(csp.isSat());
        long t2 = System.currentTimeMillis();
        System.err.println("efc2.isSat Delta: " + (t2 - t1));
    }

}