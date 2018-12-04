package com.smartsoft.csp.ast

import com.smartsoft.csp.argBuilder.IArgBuilder
import com.smartsoft.csp.varSet.EmptyVarSet
import com.smartsoft.csp.varSet.VarSet
import java.util.*

class DComplex(
        private val a: ArrayList<Exp> = ArrayList(),
        private var sorted: Boolean = false,
        private var deduped: Boolean = false

) : Iterable<Exp> {

    private var _complexVars: VarSet? = null
    var fcc: FccState = Open()

    private var formula: Exp? = null

    private val space: Space get() = firstArg.space

//    private val litImps: LitImps = ActualLitImps()

    constructor(initSize: Int) : this(ArrayList(initSize))

    val isSorted: Boolean get() = this.sorted
    val isDeduped: Boolean get() = this.deduped


    val vars: VarSet
        get() {
            if (_complexVars == null) {
                _complexVars = computeVars()
            }
            return _complexVars!!
        }


    private fun computeVars(): VarSet {
        return if (isEmpty) {
            EmptyVarSet
        } else {
            val vs = space.varSetBuilder()
            a.forEach {
                vs.addVarSet(it.vars)
            }
            vs.build();
        }
    }


    val firstArg: Exp get() = a.first()

    fun toDnnf(parent: PosComplexMultiVar? = null): Exp {
        return mkFormula(parent).toDnnf()
    }

    fun mkFormula(parent: PosComplexMultiVar? = null): Exp {
        if (formula == null) {
            formula = createFormula()
        }
        if (formula is PosComplexMultiVar && parent != null) {
            formula!!.addParent(parent)
        }
        return formula!!
    }


    private fun createFormula(): Exp {
        if (!deduped) dedup()
        if (!sorted) sort()
        return when {
            isEmpty -> space.mkConstantTrue()
            size == 1 -> firstArg
            else -> {
                val fixedArgs: Array<Exp> = a.toTypedArray()
                val b = object : IArgBuilder {
                    override val fcc: FccState get() = this@DComplex.fcc
                    override val size: Int get() = a.size
                    override val op: Op get() = Op.Formula
                    override val argIt: Iterable<Exp> get() = a
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

    fun mkExp(): Exp = mkFormula()

    val argIt: Iterable<Exp> get() = a

    override fun toString(): String {
        val a = Ser()
        Exp.serializeArgList(a, argIt)
        return a.toString()
    }

    val toStringIt: List<String> get() = a.map { it.serialize() }


    private fun cl() {
        _complexVars = null
        formula = null
        deduped = false
        sorted = false
    }

    val isEmpty: Boolean get() = a.isEmpty()

    val size: Int get() = a.size

    /*
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
        assert(!element.isAndLike)
        assert(!element.isConstant)
        assert(!element.isLit)
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

*/

    @Suppress("UNCHECKED_CAST")
    fun copy(): DComplex = DComplex(a.clone() as ArrayList<Exp>)

    fun containsVar(varCode: String): Boolean = vars.containsVarCode(varCode)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DComplex

        if (a.size != other.size) return false
        for (i in a.indices) {
            if (a[i].expId != other.a[i].expId) return false
        }


        return if (fcc != other.fcc) {
            println("FCC's do not match!!!!")
            false
        } else {
            true
        }

    }

    fun dedup() {
        if (!deduped) {
            val set = a.toMutableSet()
            a.clear()
            a.addAll(set)
            deduped = true
        }
    }

    fun sort() {
        a.sort()
        sorted = true
    }


    fun add(complex: Exp) {
        assert(complex is PosComplex || complex is Not)
        assert(!complex.isAndLike)
        a.add(complex)
        cl()
    }

    fun toList(): List<Exp> {
        return a
    }

    override fun iterator(): Iterator<Exp> {
        return a.iterator()
    }

    fun removeAll(factoryConstraintsToRelax: Iterable<Exp>) {
        a.removeAll(factoryConstraintsToRelax)
        cl()
    }
}




