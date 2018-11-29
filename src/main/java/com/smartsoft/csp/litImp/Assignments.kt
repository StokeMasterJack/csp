package com.smartsoft.csp.litImp

import com.smartsoft.csp.ast.*
import com.smartsoft.csp.dnnf.products.Cube
import com.smartsoft.csp.util.Bit
import java.util.*


class Assignments(val decisionLit: Lit) {

    private var _space: Space? = null

    private var map: MutableMap<Var, Boolean>? = null

    val space: Space get() = _space!!

    init {
        assign(decisionLit)
    }

    fun eq(o: Assignments): Boolean {
        return Objects.equals(map, o.map)
    }

    override fun equals(other: Any?): Boolean {
        return eq(other!! as Assignments)
    }

    val entries: Sequence<LitEntry> get() = map?.asSequence() ?: emptySequence()

    val litSeq: Sequence<Lit> get() = entries.map { it.asLit }

    @Throws(ConflictingAssignmentException::class)
    fun assign(lit: Lit): Boolean {
        return assign(lit.vr, lit.sign())
    }

    @Throws(ConflictingAssignmentException::class)
    fun assign(vr: Var, newValue: Boolean): Boolean {
        if (_space == null) _space = vr.space
        if (map == null) map = mutableMapOf()
        val oldValue = map!!.put(vr, newValue)
        return when (oldValue) {
            null -> true //new entry
            newValue -> false //dup
            !newValue -> throw ConflictingAssignmentException()
            else -> throw IllegalStateException()
        }
    }

    val isEmpty: Boolean get() = map == null || map!!.isEmpty()

    fun assignAll(cube: Cube): Boolean {
        var anyChange = false
        for (lit in cube) {
            val ch = assign(lit.vr, lit.sign())
            if (ch) anyChange = true
        }
        return anyChange
    }

    fun assignToDynCube(cc: DynCube = DynCube(space)): Boolean {
        return litSeq.any { cc.assign(it) }
    }

    operator fun get(vr: Var): Bit {
        return if (map == null) Bit.OPEN
        else {
            val b: Boolean? = map!![vr]
            when (b) {
                null -> Bit.OPEN
                true -> Bit.TRUE
                false -> Bit.FALSE
            }
        }
    }

    fun contains(lit: Lit): Boolean {
        val bit = this[lit.vr]
        return lit.eqSign(bit);
    }

    val size: Int get() = map?.size ?: 0

    override fun toString(): String {
        if (map == null) return ""
        return map!!.entries.joinToString(" ") { "${if (it.value) "" else "!"}${it.key}" }
    }

    fun toCube(): ConditionOn {
        return if (isEmpty) {
            throw IllegalStateException()
        } else if (size == 1) {
            litSeq.first()
        } else {
            val d = DynCube(space)
            assignToDynCube(d)
            d
        }

    }


//    fun toVvs(lit:Lit):List<Exp>{
//        if(map==null) return emptyList()
//        else{
//            map.values.forEach()
//        }
//    }
}