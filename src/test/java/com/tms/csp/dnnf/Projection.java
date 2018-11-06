package com.tms.csp.dnnf;

import com.tms.csp.ast.*;
import com.tms.csp.data.CspSample;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.fm.dnnf.vars.VarGrp;
import com.tms.csp.util.CspBaseTest2;
import com.tms.csp.util.varSets.VarSet;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class Projection extends CspBaseTest2 {


    @Test
    public void tiny() throws Exception {
        Exp n = loadDnnfTinyNoDc().getSmooth();

        assertEquals(3, n.getSatCount());
        assertEquals(3, n.getForEachSatCount());

        n.printCubes();
        System.err.println();


        n = n.project("a", "b").getSmooth();


        assertEquals(2, n.getVars().size());
        assertEquals(2, n.getSatCount());


        n.printCubes();

    }

    /*
    add(CspSample.Trim, 8, "L4", "V6", "Hybrid", "Base", "LE", "SE");
     */
    @Test
    public void trimWithOutVars() throws Exception {
        Csp csp = loadCsp(CspSample.Trim, false);

        Space space = csp.getSpace();

        Exp n1 = csp.toDnnf().getSmooth();
        VarSet outVars = space.getVars(Prefix.ENG);
        Exp n2 = n1.project(outVars);

        long cubeCount = n2.getCubes().size();
        assertEquals(3, cubeCount);
        for (Cube cube : n2.getCubes()) {
            System.err.println(cube.getTrueVars());
        }


    }

    //satCount FACTORY  [5669]    - 5788
    //satCount INV      [6564]
    @Test
    public void testEfc() throws Exception {
//        Exp n = loadTinyDnnf(dnnfEfc);
        Exp n = loadTinyDnnf(efcOriginalDnnf);

        Space space = n.getSpace();


        Set<Var> vars = space.getVars(VarGrp.CORE);

        n = n.project(vars);
        Exp smooth = n.getSmooth();

        long satCount = smooth.getSatCount();
        System.err.println("satCount[" + satCount + "]");


        assertEquals(5788, satCount);


    }

}