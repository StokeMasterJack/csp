package com.smartsoft.csp.litImp

import com.smartsoft.csp.ast.*
import com.smartsoft.csp.ssutil.Strings

class VarImps(val vr: Var) {

    private var pImps: Assignments = Assignments(vr.pLit())
    private var nImps: Assignments = Assignments(vr.nLit())

    val pImpCount: Int get() = pImps.size
    val nImpCount: Int get() = nImps.size

    val impCount: Int get() = pImpCount + nImpCount

    val complex3Var: MutableList<Exp> = mutableListOf()  //not xors and not vvs
    val complex4Var: MutableList<Exp> = mutableListOf()  //not xors and not vvs
    val complexNVar: MutableList<Exp> = mutableListOf()  //not xors and not vvs

    val score: Int
        get() = Math.min(pImpCount, nImpCount) * 1000 +
                impCount * 1000 +
                complex3Var.size * 500 +
                complex4Var.size * 300 +
                complexNVar.size * 100 +
                vr.vrId

    val space: Space get() = vr.space

    fun eqImps(o: VarImps): Boolean {
        val e1 = pImps.eq(o.pImps)
        val e2 = nImps.eq(o.nImps)
        return e1 && e2
    }

    override fun equals(other: Any?): Boolean {
        return eqImps(other!! as VarImps)
    }

    override fun hashCode(): Int {
        var result = vr.hashCode()
        result = 31 * result + pImps.hashCode()
        result = 31 * result + nImps.hashCode()
        return result
    }


    @Throws(ImpliedLitException::class)
    fun addPImp(lit: Lit): Boolean {
        return try {
            pImps.assign(lit)
        } catch (e: ConflictingAssignmentException) {
            throw ImpliedLitException(vr.nLit())
        }


    }

    @Throws(ImpliedLitException::class)
    fun addNImp(lit: Lit): Boolean {
        return try {
            nImps.assign(lit)
        } catch (e: ConflictingAssignmentException) {
            throw ImpliedLitException(vr.pLit())
        }


    }

    override fun toString(): String {
        return "$vr pImps: $pImps nImps:$nImps"
    }

    //not xors and not vvs
    fun addOtherComplex(cc: Exp): Boolean {
        assert(!cc.isVv)
        assert(!cc.isXor)
        when {
            cc.varCount == 3 -> complex3Var.add(cc)
            cc.varCount == 4 -> complex4Var.add(cc)
            else -> complexNVar.add(cc)
        }

        return true
    }

//    fun toVvs():List<Exp>{
//        if(pImps!= null){
//
//
//        }
//    }

    fun print(depth: Int) {
        println("${Strings.indent(depth)}vr: $vr")
        println("${Strings.indent(depth)}  pImps:${pImpCount}:${pImps}")
        println("${Strings.indent(depth)}  nImps:${nImpCount}:${nImps}")
        println("${Strings.indent(depth)}  complex3var:${complex3Var.size}")
        println("${Strings.indent(depth)}  complex4var:${complex4Var.size}")
        println("${Strings.indent(depth)}  complexNvar:${complexNVar.size}")
        println("${Strings.indent(depth)}  score:${score}")
        println()
    }

    fun imps(sign: Boolean): Assignments {
        if (sign) return pImps
        return nImps
    }

    fun hasImps(sign: Boolean): Boolean = if (sign) pImpCount > 0 else nImpCount > 0


    fun impsCube(sign: Boolean): ConditionOn {
        return if (sign) {
            pImps.toCube()
        } else {
            nImps.toCube()
        }
    }

    fun pCube(): ConditionOn = impsCube(true)
    fun nCube(): ConditionOn = impsCube(false)

}