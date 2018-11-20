package com.smartsoft.csp.util;

import com.smartsoft.csp.ast.FConstraintSet;

import java.util.HashSet;

/**
 *
 * leader is his own parent
 *
 * initially each constraint is the leader of his own one-constraint group
 *
 */
public class UnionFind {

    FConstraintSet constraints;

    private final int[] uf;   //maps a constraint to his direct-parent (also a constraint)
    private final int[] rank;

    public UnionFind(FConstraintSet constraints) {
        this.constraints = constraints;

        //init uf and rank array
        uf = new int[constraints.getConstraintCount()];
        rank = new int[constraints.getConstraintCount()];

        for (int i = 0; i < uf.length; i++) {
            uf[i] = i;
            rank[i] = 0;
        }
    }

    public void processAllUniquePairs() {
        int max = uf.length - 1;
        for (int c1 = 0; c1 <= max; c1++) {
            for (int c2 = c1 + 1; c2 <= max; c2++) {
                boolean related = constraints.isDirectlyRelated(c1, c2);
                if (related) {
                    union(c1, c2);
                }
            }
        }
    }

    /**
     * returns the group for complex constraint c
     */
    public int find(int c) {
        int x = c;
        while (true) {
            if (uf[x] == uf[uf[x]]) {
                x = uf[x];
                break;
            }

            x = uf[x];

        }
        uf[c] = x;
        return x;
    }

    /**
     *
     * Puts c1 and c2 into the same group
     * Points x's leader to y's leader, effectively merging the two trees
     */
    public void union(int c1, int c2) {

        int cc1 = find(c1);
        int cc2 = find(c2);

        if (cc1 == c2) {
            //x's leader is y - no change needed
            return;
        }

        // make sure rank[cc1] is smaller
        if (rank[cc1] > rank[cc2]) {
            int tmp = cc1;
            cc1 = cc2;
            cc2 = tmp;
        }

        // if both are equal, the combined tree becomes 1 deeper
        if (rank[cc1] == rank[cc2]) {
            rank[cc2]++;
        }

        uf[cc1] = cc2;
    }

    public int getFccCount() {
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < uf.length; i++) {
            int fcc = find(i);
            set.add(fcc);
        }
        return set.size();
    }

//    public VarSet getFccIds() {
//        VarSetBuilder aa = space.vsBuilder();
//        for (int i = 0; i < uf.length; i++) {
//            int fcc = find(i);
//            aa.add(fcc);
//        }
//        return aa.build();
//    }

    public int getFccFor(int constraint) {
        return find(constraint);
    }

    public void printFccs() {
        for (int i = 0; i < uf.length; i++) {
            int fcc = find(i);
            System.err.println(i + ": " + fcc);
        }
    }


    public static UnionFind compute(FConstraintSet constraints) {
        UnionFind unionFind = new UnionFind(constraints);
        unionFind.processAllUniquePairs();
        return unionFind;
    }
}

