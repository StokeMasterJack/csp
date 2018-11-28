package com.smartsoft.csp.util

import com.google.common.collect.ImmutableListMultimap
import com.smartsoft.csp.ast.FConstraintSet

/**
 *
 * leader is his own parent
 *
 * initially each constraint is the leader of his own one-constraint group
 *
 */


typealias GroupIndex = Int

typealias ConstraintIndex = Int

class UnionFind<T>(internal var constraints: FConstraintSet<T>) {

    private val uf: IntArray   //maps a constraintIndex to direct-parent (also a constraintIndex)
    private val rank: IntArray

    init {

        //init uf and rank array
        val constraintCount = constraints.constraintCount
        uf = IntArray(constraintCount)
        rank = IntArray(constraintCount)

        for (i in uf.indices) {
            uf[i] = i
            rank[i] = 0
        }
    }

    val fccCount: Int by lazy {
        val set = mutableSetOf<Int>()
        for (c: ConstraintIndex in uf.indices) {
            val fcc: GroupIndex = findGroup(c)
            set.add(fcc)
        }
        set.size
    }

    private val fccKeys: Set<GroupIndex> by lazy { fccMultimap.keySet() }

    val fccs: List<List<T>> by lazy {
        fccKeys.map { fcc -> fccMultimap.get(fcc).map { c -> constraints.getConstraint(c) } }
    }

    private val fccMultimap: ImmutableListMultimap<GroupIndex, ConstraintIndex> by lazy {
        val mm = ImmutableListMultimap.builder<GroupIndex, ConstraintIndex>()
        for (c: ConstraintIndex in uf.indices) {
            val fcc: GroupIndex = findGroup(c)
            mm.put(fcc, c)
        }
        mm.build()
    }

    private fun processAllUniquePairs() {
        val max = uf.size - 1
        for (c1 in 0..max) {
            for (c2 in c1 + 1..max) {
                val related = constraints.isDirectlyRelated(c1, c2)
                if (related) {
                    union(c1, c2)
                }
            }
        }
    }

    /**
     * returns the group for complex constraint c
     */
    private fun findGroup(c: ConstraintIndex): GroupIndex {
        var x = c
        while (true) {
            if (uf[x] == uf[uf[x]]) {
                x = uf[x]
                break
            }

            x = uf[x]

        }
        uf[c] = x
        return x
    }

    /**
     *
     * Puts c1 and c2 into the same group
     * Points x's leader to y's leader, effectively merging the two trees
     */
    fun union(c1: Int, c2: Int) {

        var cc1 = findGroup(c1)
        var cc2 = findGroup(c2)

        if (cc1 == c2) {
            //x's leader is y - no change needed
            return
        }

        // make sure rank[cc1] is smaller
        if (rank[cc1] > rank[cc2]) {
            val tmp = cc1
            cc1 = cc2
            cc2 = tmp
        }

        // if both are equal, the combined tree becomes 1 deeper
        if (rank[cc1] == rank[cc2]) {
            rank[cc2]++
        }

        uf[cc1] = cc2
    }

    //    public VarSet getFccIds() {
    //        VarSetBuilder aa = space.vsBuilder();
    //        for (int i = 0; i < uf.length; i++) {
    //            int fcc = find(i);
    //            aa.add(fcc);
    //        }
    //        return aa.build();
    //    }

    fun getFccFor(constraint: Int): Int {
        return findGroup(constraint)
    }

    fun printFccs() {
        for (i in uf.indices) {
            val fcc = findGroup(i)
            System.err.println(i.toString() + ": " + fcc)
        }
    }

    companion object {

        @JvmStatic
        fun <T> compute(constraints: FConstraintSet<T>): UnionFind<T> {
            val unionFind = UnionFind<T>(constraints)
            unionFind.processAllUniquePairs()
            return unionFind
        }
    }
}

