package com.tms.csp.fm.dnnf.cartesian;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class MyConjunction extends Conjunction<Integer> {


    List<List<Integer>> conjuncts = new ArrayList<List<Integer>>();


    public MyConjunction() {


        conjuncts.add(ImmutableList.of(0, 1, 2));
        conjuncts.add(ImmutableList.of(10, 11));
        conjuncts.add(ImmutableList.of(100, 200, 300, 666, 65));
        conjuncts.add(ImmutableList.of(543, 3));

    }


    /*

     */
//    ImmutableList<Conjunct> conjuncts = ImmutableList.of(
//            new Conjunct("Fee", "EE"),
//            new Conjunct("Fi"),
//            new Conjunct("WWWW", "QW", "Fo")
//    );

    //        ImmutableList<Conjunct> conjuncts = ImmutableList.of(
    //                new Conjunct("Fi", "sd"),
    //                new Conjunct("WWWW", "QW")
    //        );


    @Override
    public int getConjunctCount() {
        return conjuncts.size();
    }

    @Override
    public int getCubeCountForConjunct(int conjunctIndex) {
        List<Integer> conjunct = conjuncts.get(conjunctIndex);
        return conjunct.size();
    }

    @Override
    public Integer getCube(int conjunctIndex, int cubeIndex) {
        List<Integer> conjunct = conjuncts.get(conjunctIndex);
        return conjunct.get(cubeIndex);
    }
}