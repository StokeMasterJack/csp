package com.tms.csp.fm.dnnf.products

import com.tms.csp.ItTo
import com.tms.csp.IterTo
import com.tms.csp.ast.*
import com.tms.csp.util.Bit
import com.tms.csp.util.varSets.VarSet
import java.util.*

interface Cube : PLConstants, HasVars, VarPredicate {


    /**
     * get the value of a boolean feature
     */
    fun getValue(varCode: String): Bit    //OPEN if not an outVar

    /**
     * get the value of a int feature
     */
    fun getInt32Value(intVarPrefix: String): Int   //at present, must me MSRP or DLR


    val trueVars: VarSet
    val falseVars: VarSet

    val falseVarCodes: Set<String>
    val trueVarCodes: Set<String>

    val firstLit: Lit

    val isEmpty: Boolean

    val trueVarCount: Int

    val falseVarCount: Int

    val size: Int


    fun isTrue(varId: Int): Boolean

    override fun isTrue(vr: Var): Boolean

    fun isTrue(varCode: String): Boolean


    fun isFalse(varId: Int): Boolean
    fun isFalse(vr: Var): Boolean
    fun isFalse(varCode: String): Boolean


    fun containsLit(varId: Int, sign: Boolean): Boolean

    fun containsLit(vr: Var, sign: Boolean): Boolean = containsLit(vr.getVarId(), sign)

    fun containsLit(lit: Lit): Boolean

    fun litIterator(): Iterator<Lit>

    fun litIt(): Iterable<Lit>

    fun argIt(): Iterable<Exp> = ItTo.it(litIt()) { it.asExp() }
    fun argIter(): Iterator<Exp> = IterTo.iter(litIterator()) { it.asExp() }


    fun serialize(a: Ser, sep: Char)

    fun serialize(): String

    fun serializeTrueVars(a: Ser, sep: Char)

    fun serializeTrueVars(): String

    fun serialize(a: Ser)

    fun trueVarIt(): Iterable<Var>

    fun trueVarIterator(): Iterator<Var>


    fun getValue(`var`: Var): Bit

    fun asLitSet(): Set<Lit>

    fun serialize(cols: Int = 5): String {
        val a = Ser()
        serialize(a, this, cols);
        return a.toString();
    }

//    fun serializeFixedWidth(a: Ser, cols: Int = 5) {
//        serialize(a, this, cols);
//    }

    fun varCodesSorted(): SortedSet<String> = vars.toVarCodeSetSorted()

    companion object {

        @JvmStatic
        fun serialize(cube: Cube, cols: Int): String {
            val a = Ser()
            serialize(a, cube, cols)
            return a.toString();

        }

        @JvmStatic
        fun serialize(a: Ser, cube: Cube, cols: Int) {
            for (lit in cube.litIt()) {
                a.append(lit.toString(cols))
            }
        }
    }


}


val Cube?.isNullOrEmpty: Boolean get() = this == null || this.isEmpty