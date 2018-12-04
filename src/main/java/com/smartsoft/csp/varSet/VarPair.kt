package com.smartsoft.csp.varSet

import com.smartsoft.csp.ast.Ser
import com.smartsoft.csp.ast.Space
import com.smartsoft.csp.ast.Var
import com.smartsoft.csp.parse.VarSpace
import com.smartsoft.csp.util.ints.IntIterator
import com.smartsoft.csp.util.ints.Ints

class VarPair(v1: Var, v2: Var) : VarSet() {

    val vr1: Var
    val vr2: Var

    var next: VarPair? = null

    init {
        when {
            v1.vrId < v2.vrId -> {
                vr1 = v1
                vr2 = v2
            }
            v1.vrId > v2.vrId -> {
                vr1 = v2
                vr2 = v1
            }
            else -> throw IllegalArgumentException("Equal vars: var1:$v1  var2:$v2")
        }
    }

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null -> false
            other is VarPair -> vr1 == other.vr1 && vr2 == other.vr2
            other is VarSetBuilder -> other.size == 2 && vr1 == other.minVar && vr2 == other.maxVar
            else -> false
        }
    }

    override fun getVarSpace(): VarSpace = vr1.varSpace

    val vr1Id: Int get() = vr1.vrId

    val vr2Id: Int get() = vr2.vrId

    override fun serialize(a: Ser) {
        a.append(vr1.varCode)
        a.argSep()
        a.append(vr2.varCode)
    }

//    override fun asVarPair(): VarPairK {
//        return this
//    }

    override fun minVrId(): Int = vr1.vrId

    override fun maxVrId(): Int = vr2.vrId

    override fun getSpace(): Space = vr1.space

    override fun intIterator(): IntIterator = object : IntIterator {

        private var next: Var? = vr1

        override fun hasNext(): Boolean = next != null

        override fun next(): Int {
            val ret = next

            this.next = when (ret) {
                vr1 -> vr2
                vr2 -> null
                null -> null
                else -> throw IllegalStateException()
            }

            return ret!!.vrId
        }
    }

    override val size: Int get() = 2

    override fun isEmpty(): Boolean = false

    override fun immutable(): VarSet = this

    override fun isVarPair(): Boolean = true

    override fun computeContentHash(): Int {
        var hash = 2
        hash = Ints.superFastHashIncremental(vr1.vrId, hash)
        hash = Ints.superFastHashIncremental(vr2.vrId, hash)
        return Ints.superFastHashAvalanche(hash)
    }

    @Throws(IndexOutOfBoundsException::class)
    override fun getVarId(index: Int): Int {
        if (index == 0) return vr1.vrId
        if (index == 1) return vr2.vrId
        throw IndexOutOfBoundsException()
    }

    override fun recomputeSize(): Boolean {
        return false
    }

    override fun containsPrefix(prefix: String?): Boolean = vr1.hasPrefix(prefix) || vr2.hasPrefix(prefix)

}