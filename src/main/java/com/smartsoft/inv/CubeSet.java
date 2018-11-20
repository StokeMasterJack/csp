package com.smartsoft.inv;

import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.dnnf.products.Cube;
import com.smartsoft.csp.util.varSets.VarSet;
import com.smartsoft.csp.util.varSets.VarSetBuilder;

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
