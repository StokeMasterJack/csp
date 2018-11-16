package com.tms.csp.ast

import com.tms.csp.argBuilder.IArgBuilder
import com.tms.csp.util.varSets.EmptyVarSet
import com.tms.csp.util.varSets.VarSet
import java.util.function.Predicate
import java.util.function.UnaryOperator

class DComplex(private val a: ArrayList<Exp> = ArrayList()) : MutableList<Exp> by a {

    private var _complexVars: VarSet? = null
    var isFcc: Boolean? = null

    private var formula: Exp? = null

    private val space: Space get() = get(0).space

    constructor(initSize: Int) : this(ArrayList(initSize))

    val vars: VarSet
        get() {
            if (_complexVars == null) {
                _complexVars = computeVars()
            }
            return _complexVars!!
        }


    private fun computeVars(): VarSet {
        return if (isEmpty()) {
            EmptyVarSet.getInstance()
        } else {
            val vs = space.varSetBuilder()
            forEach {
                vs.addVarSet(it.vars)
            }
            vs.build();
        }
    }


    val firstArg: Exp get() = first()

    fun toDnnf(): Exp {
        return mkFormula().toDnnf()
    }

    fun mkFormula(): Exp {
        if (formula == null) {
            formula = createFormula()
        }
        return formula!!
    }

    private fun createFormula(): Exp {
        return when {
            isEmpty() -> space.mkConstantTrue()
            size == 1 -> firstArg
            else -> {
                val fixedArgs: Array<Exp> = Exp.fixArgs(a.toTypedArray())
                if (fixedArgs.size == 1) {
                    fixedArgs[0]
                } else {
                    val it = fixedArgs.asIterable()
                    val b = object : IArgBuilder {
                        override val isFcc: Boolean? get() = this@DComplex.isFcc
                        override val size: Int get() = fixedArgs.size
                        override val op: Op get() = Op.Formula
                        override val argIt: Iterable<Exp> get() = it
                        override fun mk(): Exp = throw UnsupportedOperationException()
                        override fun createExpArray(): Array<Exp> = fixedArgs
                    }
//                    val ok = PosComplexSpace.checkArgItOrder(it)
//                    if (!ok) {
//                        println(111111)
//                    }
                    return space.mkPosComplex(b)
                }
            }
        }
    }

    fun mkExp(): Exp = mkFormula()

    val argIt: Iterable<Exp> get() = this

    override fun toString(): String {
        val a = Ser()
        Exp.serializeArgList(a, argIt)
        return a.toString()
    }

    val toStringIt: List<String> get() = map { it.serialize() }


    private fun cl(ch: Boolean) {
        if (ch) {
            _complexVars = null
            formula = null
        }
    }

    override fun removeIf(filter: Predicate<in Exp>): Boolean {
        return a.removeIf(filter).apply { cl(this) }
    }

    override fun addAll(elements: Collection<Exp>): Boolean {
        return a.addAll(elements).apply { cl(this) }
    }

    override fun clear() {
        a.clear().apply { cl(true) }
    }

    override fun replaceAll(operator: UnaryOperator<Exp>) {
        a.replaceAll(operator).apply { cl(true) }
    }

    override fun removeAll(elements: Collection<Exp>): Boolean {
        return a.removeAll(elements).apply { cl(this) }
    }

    override fun add(element: Exp): Boolean {
        return a.add(element).apply { cl(this) }
    }

    override fun add(index: Int, element: Exp) {
        a.add(index, element).apply { cl(true) }
    }

    override fun removeAt(index: Int): Exp {
        return a.removeAt(index).apply { cl(true) }
    }

    override fun remove(element: Exp): Boolean {
        return a.remove(element).apply { cl(this) }
    }

    override fun set(index: Int, element: Exp): Exp {
        return a.set(index, element).apply { cl(element != this) }
    }

    override fun retainAll(elements: Collection<Exp>): Boolean {
        return a.retainAll(elements).apply { cl(this) }
    }

    @Suppress("UNCHECKED_CAST")
    fun copy(): DComplex = DComplex(a.clone() as ArrayList<Exp>)

    fun containsVar(varCode: String): Boolean = vars.containsVarCode(varCode)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DComplex

        if (a.size != other.size) return false
        for (i in a.indices) {
            if (a[i].expId != other[i].expId) return false
        }


        if (isFcc != other.isFcc) return false

        return true
    }

    override fun hashCode(): Int {
        var result = a.hashCode()
        result = 31 * result + (isFcc?.hashCode() ?: 0)
        return result
    }


}

