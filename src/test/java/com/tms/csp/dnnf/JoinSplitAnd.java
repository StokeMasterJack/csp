package com.tms.csp.dnnf;

import com.tms.csp.ast.Csp;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Prefix;
import com.tms.csp.ast.Space;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.ssutil.TT;
import com.tms.csp.util.CspBaseTest2;
import com.tms.csp.util.varSets.VarSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JoinSplitAnd extends CspBaseTest2 {


    @Test
    public void efcOriginal() throws Exception {

        TT tt = new TT();
        String clob = loadResource(efcOriginal);
//        String clob = loadText(efcOriginalFile);
        tt.t("load text");
        Csp csp = Csp.parse(clob);

        Space space = csp.getSpace();

        csp.conditionOutAtVars();   //at makes toyota-wide compile slower
        tt.t("parse text");

        Exp nRough = csp.toDnnf();
        tt.t("toDnnf");

        Exp nSmooth = nRough.getSmooth();
        tt.t("getSmooth");

        long satCount = nSmooth.getSatCount();
        tt.t("satCountSmooth");

//        assertEquals(3460501125462739908L, satCountSmooth);    //no at
        assertEquals(3460501125462739789L, satCount);    //at

        Exp nn = nSmooth.copyToOtherSpace();
        tt.t("copyToOtherSpace");

        String tiny = nn.getSpace().serializeTinyDnnf();
        tt.t("serializeTinyDnnf");

        Exp nnn = Exp.parseTinyDnnf(tiny);
        tt.t("parseTinyDnnf");

        satCount = nnn.getSatCount();
        tt.t("satCountSmooth");

//        assertEquals(3460501125462739908L, satCountSmooth);   //no at
        assertEquals(3460501125462739789L, satCount);   //at

//        space.printPosComplexTableReport();

        System.err.println();


        VarSet yearVars = space.getVars().filter(Prefix.YR);
        VarSet seriesVars = space.getVars().filter(Prefix.SER);
        VarSet ys = VarSet.union(space, yearVars, seriesVars);
        Exp ysCombos = nnn.project(ys);


        System.err.println("All Series Years [" + tiny.length() + "]");
        for (Cube cube : ysCombos.computeCubesSmooth()) {
            Exp syCsp = nnn.condition(cube).gc();
            String tinySY = syCsp.getSpace().serializeTinyDnnf();
            System.err.println("  " + cube.getTrueVars() + ": \t" + tinySY.length());
        }


    }


}