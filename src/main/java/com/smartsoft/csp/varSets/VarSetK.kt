package com.smartsoft.csp.varSets

import com.google.common.collect.ImmutableSet
import com.smartsoft.csp.ast.Space

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

    @JvmStatic
    fun minus(word1: Long, word2: Long): Long {
        return word1 and word2.inv()
    }

    @JvmStatic
    fun mkVarSet(space: Space, sVarCodes: String): VarSet {
        val varCodes: List<String> = parseVarCodes(sVarCodes)
        return when (varCodes.size) {
            0 -> EmptyVarSet.getInstance()
            1 -> space.getVar(varCodes[0]).mkSingletonVarSet()
            2 -> {
                val v1 = space.getVar(varCodes[0])
                val v2 = space.getVar(varCodes[1])
                if (v1 === v2) {
                    v1.mkSingletonVarSet()
                } else {
                    VarPair(v1, v2)
                }
            }
            else -> {
                val b = space.varSetBuilder()
                for (varCode in varCodes) {
                    val vr = space.getVar(varCode)
                    b.addVar(vr)
                }
                b.build()
            }
        }
    }

    @JvmStatic
    fun mkVarSetBuilder(space: Space, varCodes: String): VarSetBuilder {
        val a = parseVarCodes(varCodes)
        val b = space.varSetBuilder()
        b.addVarCodes1(a)
        return b
    }

    @JvmStatic
    fun parseVarCodes(sVarCodes: String): List<String> {
        val a = sVarCodes.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val set = ImmutableSet.copyOf(a)
        return set.toList()
    }

    @JvmStatic
    fun copyToVarSetBuilder(vs: VarSet): VarSetBuilder {
        val space = vs.space
        return when (vs) {
            is EmptyVarSet -> space.varSetBuilder()
            is SingletonVarSet -> {
                val b = space.varSetBuilder()
                b.addVar(vs.vr)
                return b
            }
            is VarPair -> {
                val b = space.varSetBuilder()
                b.addVar(vs.var1)
                b.addVar(vs.var2)
                b
            }
            is VarSetBuilder -> VarSetBuilder(vs)
            else -> throw IllegalStateException()
        }
    }


}

