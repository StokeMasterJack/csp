package com.smartsoft.csp.pl;

import com.smartsoft.csp.ast.Csp;
import com.smartsoft.csp.util.CspBaseTest2;
import org.junit.Test;

import java.math.BigInteger;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimplifySeriesModelAndsTest extends CspBaseTest2 {


    @Test
    public void test4() throws Exception {
        String clob1 = loadResource(efcOriginal);
        Csp csp1 = Csp.parse(clob1);


        csp1 = csp1.condition("YR_2013 !YR_2014");

        int size1 = csp1.serialize().length();
        BigInteger count1 = csp1.toDnnf().getSmooth().getSatCount();

        csp1.simplifySeriesModelAnds();
        int size2 = csp1.serialize().length();
        BigInteger count2 = csp1.toDnnf().getSmooth().getSatCount();

        assertEquals(count1, count2);
        assertTrue(size2 < size1);


    }


}
