package com.smartsoft.csp.forEach;

import com.smartsoft.csp.combo.Paths;
import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.fm.dnnf.Dnnf;
import com.smartsoft.csp.fm.dnnf.forEach.ForEach;
import com.smartsoft.csp.fm.dnnf.products.Cube;
import com.smartsoft.csp.fm.dnnf.vars.VarGrp;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class DTest {

    public Paths paths = Paths.EFC_ORIGINAL;

    public Dnnf csp;
    public Exp baseConstraint;
    public Space ut;
    public Space space;

    public int maxSatCountForComputeCubes = 1000;


    public String userConstraint;
    public int maxRecordCount = 100;


    long base = 3460501125462739789L;
    long conditioned = 12376;

    long inv = 4;
    long fio = 4;
    long core = 1;

    long years = 1;
    long models = 1;
    long xCols = 1;
    long iCols = 1;

    long yearModels = 1;
    long colorCombos = 1;


    public DTest() {
    }

    public void runTest() {
        baseConstraint = paths.createDNodeFactory();
        space = baseConstraint.getSpace();

        ForEach forEach = new ForEach(baseConstraint);
        assertEquals(base, forEach.computeSatCount());

        forEach.setPics(userConstraint);
        assertEquals(conditioned, forEach.computeSatCount());

        //projections
        checkNode(inv, VarGrp.INV);
        checkNode(fio, VarGrp.FIO);
        checkNode(core, VarGrp.CORE);

        checkNode(years, VarGrp.YR);
        checkNode(models, VarGrp.MDL);
        checkNode(xCols, VarGrp.XCOL);
        checkNode(iCols, VarGrp.ICOL);

        checkNode(yearModels, VarGrp.YEAR_MODEL);
        checkNode(colorCombos, VarGrp.XCOL_ICOL);


    }


    public void checkNode(long expectedSatCount, VarGrp key) {
        if (expectedSatCount == -1) return;

        assert key != null;

        ForEach forEach = new ForEach(baseConstraint);
        forEach.setPics(userConstraint);
        forEach.setOutVars(key);


        long satCount = forEach.computeSatCount().longValue();

        if (satCount < maxSatCountForComputeCubes) {
            Collection<Cube> cubes = forEach.execute();
            assertEquals(satCount, cubes.size());
//                Exp.printCubes(cubes);
        }


    }


}


