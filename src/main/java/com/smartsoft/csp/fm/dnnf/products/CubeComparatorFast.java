package com.smartsoft.csp.fm.dnnf.products;

import com.smartsoft.csp.util.varSets.VarSet;

import java.util.Comparator;

public class CubeComparatorFast implements Comparator<Cube> {

    @Override
    public int compare(Cube c1, Cube c2) {

        Integer vCount1 = c1.getVarCount();
        Integer vCount2 = c2.getVarCount();

        if (vCount1 < vCount2) {
            return -1;
        }
        if (vCount1 > vCount2) {
            return 1;
        }

        Integer tCount1 = c1.getTrueVarCount();
        Integer tCount2 = c2.getTrueVarCount();

        if (tCount1 < tCount2) {
            return -1;
        }
        if (tCount1 > tCount2) {
            return 1;
        }

        VarSet tVars1 = c1.getTrueVars();
        VarSet tVars2 = c2.getTrueVars();
        int ct = tVars1.compareTo(tVars2);

        if (ct < 0) {
            return -1;
        }
        if (ct > 0) {
            return 1;
        }

        VarSet vars1 = c1.getVars();
        VarSet vars2 = c2.getVars();

        return vars1.compareTo(vars2);
    }

}
