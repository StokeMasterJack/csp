package com.tms.csp.dnnf;

import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.data.CspSample;
import com.tms.csp.util.CspBaseTest2;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class DnnfProjectTest extends CspBaseTest2 {


    @Test
    public void forgetConditionThenProjectTiny() throws Exception {
        Csp csp = CspSample.TinyNoDc.parseCsp();
        Exp n1 = csp.toDnnf().getSmooth();

        assert (n1.isDnnf() && n1.checkDnnf());
        assertEquals(BigInteger.valueOf(3), n1.getSatCount());
        n1.printCubes();


        System.err.println("n1: " + n1);
        System.err.println("n1.dnnf: " + n1.isDnnf());

        Exp n2 = n1.con("!c");
        System.err.println("n2: " + n2);


        System.err.println("n2.dnnf: " + n2.isDnnf());



        assert (n2.isDnnf());
        assert (n2.checkDnnf());


        n2.printCubes();

        Exp n3 = n2.project("a").getSmooth();

        assert (n3.isDnnf() && n3.checkDnnf());

        assert n3.isTrue() || n3.isDontCareOr() : n3.toString();


    }

    @Test
    public void test1() throws Exception {

    }


}