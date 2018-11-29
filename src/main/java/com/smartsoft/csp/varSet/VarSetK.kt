package com.smartsoft.csp.varSet

import com.google.common.collect.ImmutableSet
import com.smartsoft.csp.ast.*
import com.smartsoft.csp.bitSet.BitSet64

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
    @JvmOverloads
    fun copyToVarSetBuilder(vs: VarSet, adjust: Adjust = Adjust.None): VarSetBuilder {
        val copy = copyToVarSetBuilderInternal(vs)
        copy.adjust(adjust)
        return copy;
    }

    @JvmStatic
    private fun copyToVarSetBuilderInternal(vs: VarSet): VarSetBuilder {
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
                b.addVar(vs.vr1)
                b.addVar(vs.vr2)
                b
            }
            is VarSetBuilder -> VarSetBuilder(vs)
            else -> throw IllegalStateException()
        }
    }

    @JvmStatic
    fun copyWords(words: LongArray): LongArray {
        return words.copyOf()
    }

    @JvmStatic
    fun eqNullSafe(vs1: VarSet?, vs2: VarSet?): Boolean {
        if (vs1 == null && vs2 == null) return true
        if (vs1 == null && vs2 != null) return false
        if (vs1 != null && vs2 == null) return false
        return eq(vs1!!, vs2!!)
    }

    @JvmStatic
    fun eq(vs1: VarSet, vs2: VarSet): Boolean {
        val (s1, s2) = MinMax.mk(vs1.immutable(), vs2)
        if (vs1 === vs2) return true;
        return when (s1) {
            is EmptyVarSet -> when (s2) {
                is EmptyVarSet -> true
                is SingletonVarSet -> false
                is VarPair -> false
                is VarNSet -> false
                is VarSetBuilder -> s2.isEmpty()
                else -> throw IllegalStateException()
            }
            is SingletonVarSet -> when (s2) {
                is SingletonVarSet -> s1.eqMin(s2)
                is VarSetBuilder -> s2.eq(s1)
                else -> false
            }
            is VarPair -> when (s2) {
                is VarPair -> eq(s1, s2)
                is VarSetBuilder -> s2.eq(s1)
                else -> false
            }
            is VarSetBuilder -> when (s2) {
                is VarSetBuilder -> s2.eq(s1)
                is VarNSet -> s2.eq(s1)
                else -> false
            }
            is VarNSet -> when (s2) {
                is VarSetBuilder -> s1.eq(s2)
                is VarNSet -> s1.eq(s2)

                else -> false
            }
            else -> throw IllegalStateException()

        }
    }

    @JvmStatic
    fun copy(vs: VarSet): VarSet {
        return when (vs) {
            is EmptyVarSet -> copyEmptyVarSet(vs)
            is SingletonVarSet -> copySingletonVarSet(vs)
            is VarPair -> copyVarPair(vs)
            is VarSetBuilder -> vs.copyToVarSetBuilder()
            is VarNSet -> vs.copyToVarNSet()
            else -> Exp.th()
        }
    }

    @JvmStatic
    fun copyEmptyVarSet(vs: EmptyVarSet): EmptyVarSet = vs

    @JvmStatic
    fun copyVarPair(vs: VarPair): VarPair = vs

    @JvmStatic
    fun copySingletonVarSet(vs: SingletonVarSet): SingletonVarSet = vs


//    fun eq(s1: SingletonVarSet, s2: SingletonVarSet): Boolean = s1.eqVr(s2)
//    fun eq(s1: VarPair, s2: VarPair): Boolean = s1.eqVars(s2)
//    fun eq(s1: VarSetBuilder, s2: VarSetBuilder): Boolean = s1.contentEquals(s2)
//    fun eq(s1: VarNSet, s2: VarNSet): Boolean = s1.contentEquals(s2)

    @JvmStatic
    fun getMaskForLongWord(vr: Var): Long {
        val varIndex = vr.varIndex
        return 1L shl varIndex
    }

    fun foo(word1: Long, word2: Long): Long {
        return word1 and word2.inv()
    }
}

fun SingletonVarSet.eqVr(o: SingletonVarSet): Boolean = vr.vrId == o.vrId

fun VarPair.eqVr1(o: VarPair): Boolean = vr1Id == o.vr1Id
fun VarPair.eqVr2(o: VarPair): Boolean = vr2Id == o.vr2Id

fun VarPair.eqVars(o: VarPair): Boolean = this.eqVr1(o) && eqVr2(o)

fun VarSetBuilder.eq(o: EmptyVarSet): Boolean = isEmpty()
fun VarSetBuilder.eq(o: SingletonVarSet): Boolean = sizeIs(1) && eqMin(o)
fun VarSetBuilder.eq(o: VarPair): Boolean = sizeIs(2) && eqMinMax(o)
fun VarSetBuilder.eq(o: VarSetBuilder) = contentEquals(o)
fun VarSetBuilder.eq(o: VarNSet) = o.eq(this)

fun VarSetBuilder.sizeIs(sz: Int): Boolean {
    var size = 0
    words.forEach {
        size += it.bitCount
        if (size > sz) return false
    }
    return size == sz
}



val VarSet.rank: Int
    get() = when (this) {
        is EmptyVarSet -> 0
        is SingletonVarSet -> 1
        is VarPair -> 2
        is VarSetBuilder -> 3
        is VarNSet -> 4
        else -> throw IllegalStateException()
    }

val VarSet.min: VarId get() = minVrId()
val VarSet.max: VarId get() = maxVrId()

fun VarSet.eqMin(vs: VarSet): Boolean = min == vs.min
fun VarSet.eqMax(vs: VarSet): Boolean = max == vs.max
fun VarSet.eqMinMax(vs: VarSet): Boolean = eqMin(vs) && eqMax(vs)


sealed class Adjust {

    object None : Adjust()

    data class AddVar(val vr: Var) : Adjust()
    data class AddVarSet(val vs: VarSet) : Adjust()
    data class RemoveVar(val vr: Var) : Adjust()
    data class RemoveVarSet(val vs: VarSet) : Adjust()

    companion object {
        @JvmStatic
        val none: None = None

        @JvmStatic
        fun add(vr: Var): AddVar = AddVar(vr)

        @JvmStatic
        fun rem(vr: Var): RemoveVar = RemoveVar(vr)

        @JvmStatic
        fun add(vs: VarSet): AddVarSet = AddVarSet(vs)

        @JvmStatic
        fun rem(vs: VarSet): RemoveVarSet = RemoveVarSet(vs)
    }

}