package com.tms.inv;

import com.google.common.collect.ImmutableSet;
import com.tms.csp.ast.Space;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.util.varSets.VarSetBuilder;

import java.util.Set;

public class CubeSet {

    private final ImmutableSet<Cube> cubes;

    public CubeSet(Set<Cube> cubes) {
        this.cubes = ImmutableSet.copyOf(cubes);
    }

//    public boolean containsInvLine(Line line) {
//        for (Cube cube : cubes) {
//            VarSet cubeVars = cube.getTrueVars();
//            VarSet lineVars = line.getVars();
//            if (cubeVars.equals(lineVars)) {
//                return true;
//            }
//        }
//        return false;
//    }

    public VarSet getVars() {
        Space space = cubes.iterator().next().getSpace();
        VarSetBuilder b = space.newMutableVarSet();
        for (Cube cube : cubes) {
            VarSet vars = cube.getVars();
            b.addVars(vars);
        }
        return b.build();
    }
}
