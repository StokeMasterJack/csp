package com.smartsoft.inv;

import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.ast.*;
import com.smartsoft.csp.dnnf.forEach.ForEach;
import com.smartsoft.csp.dnnf.models.Solution;
import com.smartsoft.csp.dnnf.products.Cube;
import com.smartsoft.csp.util.Bit;
import com.smartsoft.csp.varSets.VarSet;

import java.util.Set;

public class Dealers implements PLConstants {


    static public VarSet getDealerVars() {
        throw new UnsupportedOperationException();
    }


    private static Set<Var> computeDealers1(Exp n, Cube pics) {

        assert n.isOpen();

        //Exp conditioned = n.condition(picks);  //todo this
        Exp conditioned = n.condition(new DynCube(n.getSpace(), pics));

        assert !conditioned.isFalse();

        VarSet allDealerVars = getDealerVars();

        ForEach forEach = new ForEach(conditioned);
        forEach.setOutVars(allDealerVars);
        Set<Cube> products2 = forEach.execute();


        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (Cube product2 : products2) {
            VarSet trueVars = product2.getTrueVars();
            b.addAll(trueVars);
        }

        return b.build();


    }

    private static Set<Var> computeDealers1(Exp conditioned) {

        assert conditioned.isOpen();

        VarSet allDealerVars = getDealerVars();

        ForEach forEach = new ForEach(conditioned);
        forEach.setOutVars(allDealerVars);
        Set<Cube> products2 = forEach.execute();


        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (Cube product2 : products2) {
            VarSet trueVars = product2.getTrueVars();
            b.addAll(trueVars);
        }

        return b.build();


    }

    private static Set<Var> computeDealers2(Exp conditioned) {

        assert conditioned.isOpen();

        VarSet allDealerVars = getDealerVars();

        ImmutableSet.Builder<Var> b = ImmutableSet.builder();

        for (Var dealerVar : allDealerVars) {
            Bit value = conditioned.getValue(dealerVar);
            if (value.isOpen() || value.isTrue()) {
                b.add(dealerVar);
            }
        }

        return b.build();


    }

    public static Set<Integer> computeDealers(Exp n, Cube pics) {
        return computeDealers3(n, pics);
    }

    public static Set<Integer> computeDealers(Exp n) {
        Space space = n.getSpace();
        Exp dealers = n.project(getDealerVars());
        ImmutableSet.Builder<Integer> b = ImmutableSet.builder();
        Set<Cube> dealerCubes = dealers.getCubesSmooth();
        for (Cube dealerCube : dealerCubes) {
            b.add(dealerCube.getInt32Value(DLR_PREFIX));
        }
        return b.build();
    }

    private static Set<Var> computeDealers2(Exp n, Cube pics) {

        assert n.isOpen();

        //Exp conditioned = n.condition(picks);  //todo this
        Exp conditioned = n.condition(new DynCube(n.getSpace(), pics));

        assert !conditioned.isFalse();

        VarSet allDealerVars = getDealerVars();

        ImmutableSet.Builder<Var> b = ImmutableSet.builder();

        for (Var dealerVar : allDealerVars) {
            Bit value = conditioned.getValue(dealerVar);
            if (value.isOpen() || value.isTrue()) {
                b.add(dealerVar);
            }
        }

        return b.build();


    }


    private static Set<Integer> computeDealers3(Exp n, Cube pics) {
        Space space = n.getSpace();
        Exp dealers = n.condition(pics).project( getDealerVars());

        ImmutableSet.Builder<Integer> b = ImmutableSet.builder();
        Set<Cube> dealerCubes = dealers.getCubesSmooth();
        for (Cube dealerCube : dealerCubes) {
            int varCount = dealerCube.getVarCount();
            if (varCount == 32) {
                int dealer = dealerCube.getInt32Value(DLR_PREFIX);
                b.add(dealer);
            } else {
                VarSet cubeVars = dealerCube.getVars();
                VarSet dealerVars = getDealerVars();
                VarSet dcVars = dealerVars.minus(cubeVars);
                Solution solution = new Solution(space, dealerCube, dcVars);
                for (Cube fullCube : solution) {
                    int dealer = fullCube.getInt32Value(DLR_PREFIX);
                    b.add(dealer);
                }
            }
        }

        return b.build();
    }
//    public static String convertDealerIntToVarCode(int int32Value) {
//        return DLR_PREFIX + "_" + lpad(Integer.toString(int32Value), '0', 5);
//    }

//    public static VarSet convertDealersToVars(Space space, int... dealerCodes) {
//        VarSetBuilder b = space.varSetBuilder();
//        for (int dealerCode : dealerCodes) {
//            try {
//                String varCode = convertDealerIntToVarCode(dealerCode);
//                Var vr = space.getVr(varCode);
//                b.add(vr);
//            } catch (BadVarCodeException e) {
//                // Don't add to the list
//                // e.fillInStackTrace();
//            }
//        }
//        return b.build();
//    }


//    public static Cube computeNegatedDealers(Space space, int... inDealersCodes) {
//        VarSet inDealers = convertDealersToVars(space, inDealersCodes);
//        DynCube outDealers = new DynCube(space);
//        VarSet allDealers = space.getDealerVars();
//
//        for (Var d : allDealers) {
//            if (inDealers.containsVar(d)) {
//                continue;
//            }
//            outDealers.assign(d.mkNegLit());
//        }
//        return outDealers;
//    }

}
