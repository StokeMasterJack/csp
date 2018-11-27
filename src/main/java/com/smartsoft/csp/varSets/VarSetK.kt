package com.smartsoft.csp.varSets

object VarSetK {
    @JvmStatic
    fun getAllPairs(vars: VarSet): Set<VarPair> {
        return when (vars) {
            is EmptyVarSet -> emptySet<VarPair>()
            is SingletonVarSet -> emptySet<VarPair>()
            is VarPair -> setOf(vars)
            is VarSetBuilder -> {
                val set = mutableSetOf<VarPair>()
                val max = vars.size - 1
                for (c1 in 0..max) {
                    for (c2 in c1 + 1..max) {
                        val v1 = vars[c1]
                        val v2 = vars[c2]
                        set.add(VarPair(v1, v2))
                    }
                }
                set
            }
            else -> throw IllegalStateException()
        }

    }



}

