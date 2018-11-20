package com.smartsoft.inv.tundra;

import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.VarInfo;
import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.VarInf;
import com.smartsoft.csp.dnnf.Dnnf;
import com.smartsoft.csp.dnnf.products.Cube;
import com.smartsoft.csp.util.CspBaseTest2;
import com.smartsoft.csp.util.varSets.VarSet;
import com.smartsoft.inv.ComboCsp;
import com.smartsoft.inv.Dealers;
import com.smartsoft.inv.Line;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TundraSitTest extends CspBaseTest2 {

    String vPath = "varInfo.txt";
    String fPath = "factory.pl.txt";
    String iPath = "inventory.cfg.txt";
    String sitPath = "inventory.sit.txt";

    @Test
    public void testFloor() throws Exception {
        int msrp = 23476;
        int floor = msrp / 100 * 100;
        System.err.println("floor[" + floor + "]");
    }

    @Test
    public void testExtractMsrp() throws Exception {
        String sLine = "MDL_8363 YR_2015 XCOL_01G3 ICOL_FC20 ACY_SP ACY_G_S7 ACY_RE ACY_EE ACY_G_P4 ACY_G_BT ACY_G_BM ACY_G_TX ACY_G_CK 1 $53992 DLR_42276";
        Integer msrp = Line.extractMsrp(sLine);
        System.err.println("msrp[" + msrp + "]");
    }

    @Test
    public void testVarInfo() throws Exception {

        String vClob = loadResource(this, vPath);

        VarInfo varInfo = VarInf.parse(vClob);

        //ACY_5T.YR_2015.SER_tundra.MDL_8241.vtc=true
        ImmutableSet<String> set = ImmutableSet.of("YR_2015", "SER_tundra", "MDL_8241");
        String value = varInfo.getAttribute(set, "ACY_5T", "vtc");
        assertEquals("true", value);
        System.err.println("ACY_5T.YR_2015.SER_tundra.MDL_8241.vtc[" + value + "]");

        //ACY_5F.vtc=true
        set = null;
        value = varInfo.getAttribute(set, "ACY_5F", "vtc");
        assertEquals("true", value);
        System.err.println("ACY_5F.vtc[" + value + "]");

        //DISC_jbl.derived=false
        //DISC_jbl.YR_2015.SER_tundra.MDL_8241.derived=false
        set = ImmutableSet.of("YR_2015", "SER_tundra", "MDL_8241");
        value = varInfo.getAttribute(set, "DISC_jbl", "derived");
        assertEquals("false", value);
        System.err.println("DISC_jbl.derived[" + value + "]");


//        ACY_H1.type=FIO
        boolean v = varInfo.isFio("ACY_H1");
        assertTrue(v);
        System.err.println("ACY_H1.isFio()[" + value + "]");
    }


    //2h 21m
    //2m 6s
    //2m 2s
    //1m 39s
    //2m 46s  tinyDnnf.length: 3071185
    @Test
    public void compileThenSerialize1() throws Exception {

        String fClob = loadResource(this, fPath);
        String vClob = loadResource(this, vPath);
        String iClob = loadResource(this, iPath);

        VarInfo varInfo = VarInf.parse(vClob, "SER_tundra");

        ComboCsp combo = new ComboCsp(fClob, iClob, varInfo);
        combo.processInventory();

        Exp d = combo.getFactoryInvDnnf();

        BigInteger satCount = d.getSatCount();
        System.err.println("satCount[" + satCount + "]");

        Dnnf dnnfCsp = new Dnnf(d);

        Collection<Cube> cubes = dnnfCsp.computeInventory();

        System.err.println("cubes.size()[" + cubes.size() + "]");


        int i = 0;
        for (Cube cube : cubes) {
            System.err.println(cube.getTrueVarCodes());
//            if (i > 100) break;
//            System.err.println("$" + cube.getInt32Value(MSRP_PREFIX) + "  " + Dealers.computeDealers(dnnfCsp.getBaseConstraint(), cube));


//            Exp dealers = dnnf.getBaseConstraint().condition(cube).project(space.getDealerVars());
//            Set<Cube> dealerCubes = dealers.getCubes();
//            for (Cube dealerCube : dealerCubes) {
//                System.err.println("  DLR_" + dealerCube.getInt32Value(DLR_PREFIX));
//            }
//            i++;
        }


        if (false) {

            String tinyDnnf = combo.getFactoryInvTinyDnnf();

            System.err.println("tinyDnnf.length()[" + tinyDnnf.length() + "]");

            Exp factoryInvDnnf = combo.getFactoryInvDnnf();
            System.err.println("factoryInvDnnf.getSatCount()[" + factoryInvDnnf.getSatCount() + "]");

            writeText("tundra/fact-invClob.sit.dnnf.txt", tinyDnnf);

            //tinyDnnf.length
            //    buckets:  3,071,185
            //    strict:  18,624,892

            String tinyDnnf2 = loadText("/Users/dford/temp/csp/tundra/fact-invClob.sit.dnnf.txt");

            Exp rootDNode = Exp.Companion.parseTinyDnnf(tinyDnnf2);


//            BigInteger satCount = rootDNode.getSatCount();
//            System.err.println("satCount[" + satCount + "]");


        }


    }

    @Test
    public void compileThenSerialize2() throws Exception {

        String fClob = loadResource(this, fPath);
        String vClob = loadResource(this, vPath);
        String iClob = loadResource(this, sitPath);

        VarInfo varInfo = VarInf.parse(vClob, "SER_tundra");

        ComboCsp combo = new ComboCsp(fClob, iClob, varInfo);
        combo.processInventory();

        String tinyDnnf = combo.getFactoryInvTinyDnnf();

        System.err.println("tinyDnnf.length()[" + tinyDnnf.length() + "]");

        Exp factoryInvDnnf = combo.getFactoryInvDnnf();
        System.err.println("factoryInvDnnf.getSatCount()[" + factoryInvDnnf.getSatCount() + "]");

        writeText("tundra/fact-invClob.sit.dnnf.txt", tinyDnnf);

        //tinyDnnf.length
        //    buckets:  3,071,185
        //    strict:  18,624,892

        String tinyDnnf2 = loadText("/Users/dford/temp/csp/tundra/fact-invClob.sit.dnnf.txt");

        Exp rootDNode = Exp.Companion.parseTinyDnnf(tinyDnnf2);

        BigInteger satCount = rootDNode.getSatCount();
        System.err.println("satCount[" + satCount + "]");

        Dnnf dnnfCsp = new Dnnf(rootDNode);

        Collection<Cube> cubes = dnnfCsp.computeInventory();

        System.err.println("cubes.size()[" + cubes.size() + "]");


        int i = 0;
        for (Cube cube : cubes) {
            if (i > 100) break;
            System.err.println("$" + cube.getInt32Value(MSRP_PREFIX) + "  " + Dealers.computeDealers(dnnfCsp.getBaseConstraint(), cube));


//            Exp dealers = dnnf.getBaseConstraint().condition(cube).project(space.getDealerVars());
//            Set<Cube> dealerCubes = dealers.getCubes();
//            for (Cube dealerCube : dealerCubes) {
//                System.err.println("  DLR_" + dealerCube.getInt32Value(DLR_PREFIX));
//            }
//            i++;
        }


    }

    @Test
    public void runFactoryInventorySitFromTinyDnnf() throws Exception {

        String dClob = loadResource(this, "fact-invClob.sit.varPer.dnnf.txt");

        Exp d = Exp.Companion.parseTinyDnnf(dClob);
        Space space = d.getSpace();

        VarSet dealerVars = space.getVars("DLR");
        VarSet msrpVars = space.getVars("MSRP");

        System.err.println(dealerVars.size() + ":" + dealerVars);
        System.err.println(msrpVars.size() + ":" + msrpVars);

        Dnnf dnnfCsp = new Dnnf(d);


        System.err.println("dnnf.getFacetCount[" + dnnfCsp.getFacetCount("ab") + "]");


    }


}
