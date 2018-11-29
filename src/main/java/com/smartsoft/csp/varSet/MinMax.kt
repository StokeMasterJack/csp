package com.smartsoft.csp.varSet

data class MinMax(val min: VarSet, val max: VarSet) {

    init {
        assert(min.rank <= max.rank)
    }

    companion object {
        @JvmStatic
        fun mk(e1: VarSet, e2: VarSet): MinMax {
            return if (e1.rank < e2.rank) {
                MinMax(e1, e2)
            } else {
                MinMax(e2, e1)
            }
        }


    }


}