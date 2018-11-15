package com.tms.inv;

//import com.tms.csp.Build;

import com.google.common.collect.Sets;
import com.tms.csp.VarInfo;
import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Space;
import com.tms.csp.ast.VarInf;
import com.tms.csp.util.CspBaseTest2;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class InvParseTest extends CspBaseTest2 {

    String fPath1 = "csp/g/ProdFactoryRules.txt";

    String iPath1 = "csp/g/inventoryrulesfromVIN45.txt";

    String iPath2 = "csp/f/inventory.json";
    String iPath3 = "csp/2014/06/03/inventoryruleswdealers.txt";
    String iPath4 = "csp/g/inventoryrulesfromVIN45-deduped.txt";

    String vClob = loadResource(VAR_INFO);

    VarInfo varInfo = VarInf.parse(vClob);


    @Test
    public void test_extractVarCodes() throws Exception {
        extractVarCodes(iPath1);
        extractVarCodes(iPath2);
        extractVarCodes(iPath3);

    }


    public void extractVarCodes(String iPath) throws Exception {
        String invClob = loadResource(iPath);
        Set<String> varCodes = KInv.extractVarCodes(invClob);

        System.err.println("extracted vr codes for[" + iPath + "]");
        for (String varCode : varCodes) {
            System.err.println("  " + varCode);
        }

    }


    @Test
    public void test_parseInvNewSpace() throws Exception {
        String iClob = loadResource(iPath4);
        Inv inv = Inv.parseInvNewSpace(iClob, varInfo);
        Space space = inv.getSpace();
        assertEquals(6811, inv.size());


        Csp csp = new Csp(space);
        inv.addConstraintsToCsp(csp);

        Exp exp = inv.mkYearXor();
        System.err.println("exp[" + exp + "]");

        System.err.println("Compiling dnnf...");
        long t1 = System.currentTimeMillis();
        Exp dnnf = csp.toDnnf();
        long t2 = System.currentTimeMillis();
        System.err.println("  Compiling complete: " + (t2 - t1) + "ms");
        Exp smooth = dnnf.getSmooth();
        long satCountTotal = smooth.getSatCountLong();


        //6857 rows in inv file

        Exp yr_2013 = smooth.condition("YR_2013").getSmooth();
        Exp yr_2014 = smooth.condition("YR_2014").getSmooth();

        long satCount2013 = yr_2013.getSatCountLong();
        long satCount2014 = yr_2014.getSatCountLong();
        System.err.println("#rows[" + 6857 + "]");
        System.err.println("total[" + satCountTotal + "]");
        System.err.println("yr_2013[" + satCount2013 + "]");
        System.err.println("yr_2014[" + satCount2014 + "]");

        System.err.println("Sum: " + (satCount2013 + satCount2014));
    }

    @Test
    public void test_show_models() throws Exception {
        String fClob = loadResource(fPath1);
        Csp csp = Csp.parse(fClob);
        Set<String> modelCodes = csp.getModelCodesForSeries("SER_avalon");
        System.err.println("models[" + modelCodes + "]");
    }

//
//    @Test
//    public void test2() throws Exception {
//        Exp ff = createAvalonSubsetOfFactory();
//        Inv ii = createAvalonSubsetOfInv();
//
//        Set<String> ffVars = ff.getVars().toVarCodeSetSorted();
//        Set<String> iiVars = ii.getVars().toVarCodeSetSorted();
//
//
//        System.err.println("ff-ii:" + Sets.difference(ffVars,iiVars));
//        System.err.println("ii-ff:" + Sets.difference(iiVars,ffVars));
//
//
//        Set<String> vars = Sets.union(ffVars, iiVars);
//        Space space = new Space(vars);
//        space.setVarInfo(this.varInfo);
//
//
//        System.err.println(space.getVarMeta());
//        System.err.println(space.getVarMeta().varInfo);
//
//        Csp csp = new Csp(space);
//
//        csp.addConstraint(ff.serialize());
//
//        ii.addConstraintsToCsp(csp);
//
//        Exp smooth = csp.toDnnfSmooth();
//
//        System.err.println("smooth[" + smooth.getSatCountLong() + "]");
//
//    }

    public Inv createAvalonSubsetOfInv() throws Exception {
        String iClob = loadResource(iPath1);
        Inv inv = Inv.parseInvNewSpace(iClob,varInfo);
        return inv.filterOnAvalon();
    }


    public Exp createAvalonSubsetOfFactory() throws Exception {
        String fClob = loadResource(fPath1);
        Csp csp = Csp.parse(fClob);
        csp.assign("SER_avalon");
        return csp.reduce().mkFormula();
    }


}
