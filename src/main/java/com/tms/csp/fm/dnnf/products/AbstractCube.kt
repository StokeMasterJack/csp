package com.tms.csp.fm.dnnf.products

import com.google.common.base.Objects
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Sets
import com.tms.csp.ast.*
import com.tms.csp.util.Bit
import com.tms.csp.util.ints.Ints
import com.tms.csp.util.varSets.VarSet
import com.tms.csp.varCodes.VarCode
import java.util.*

abstract class AbstractCube : Cube {


    override val firstLit: Lit
        get() = litIterator().next()

    override val isEmpty: Boolean
        get() = size == 0

    override val size: Int
        get() = varCount


    override val trueVars: VarSet
        get() = throw UnsupportedOperationException(javaClass.name)

    override val falseVars: VarSet
        get() = vars.minus(trueVars)

    override val varCount: Int
        get() = vars.size

    override val trueVarCount: Int
        get() = trueVars.size

    override val falseVarCount: Int
        get() = varCount - trueVarCount


    override val trueVarCodes: Set<String>
        get() = trueVars.toVarCodeSet()

    open val varCodes: Set<String>
        get() = vars.toVarCodeSet()

    override val falseVarCodes: Set<String>
        get() = Sets.difference(varCodes, trueVarCodes)

    val firstVar: Var
        get() = varIterator().next()

    override fun isVarDisjoint(vs: VarSet): Boolean {
        return !anyVarOverlap(vs)
    }

    override fun isVarDisjoint(exp: Exp): Boolean {
        return !anyVarOverlap(exp)
    }

    override fun isVarDisjoint(cube: Cube): Boolean {
        return !anyVarOverlap(cube)
    }

    override fun getValue(vr: Var): Bit {
        return if (containsVar(vr)) {
            if (isTrue(vr)) {
                Bit.TRUE
            } else {
                Bit.FALSE
            }
        } else {
            Bit.OPEN
        }
    }

    override fun getValue(varCode: String): Bit {
        val vr = space.getVar(varCode)
        return getValue(vr)
    }


    fun chk(expected: String): Boolean {
        val actual = toString()
        if (expected == actual) {
            return true
        } else {
            System.err.println("expected  [$expected]")
            System.err.println("actual    [$actual]")
            throw IllegalStateException()
        }
    }


    override fun serialize(): String {
        val a = Ser()
        serialize(a)
        return a.toString()
    }

    fun serialize(argSep: Char): String {
        val a = Ser()
        serialize(a, argSep)
        return a.toString()
    }

    override fun serialize(a: Ser, sep: Char) {
        serializeLits(a, sep)
    }

    fun serializeTrueVars(argSep: Char): String {
        val a = Ser()
        serializeTrueVars(a, argSep)
        return a.toString()
    }

    fun serializeLits(a: Ser, argSeparator: Char) {
        for (lit in litIt()) {
            lit.serialize(a)
            a.append(argSeparator)
        }
    }

    fun serializeVars(a: Ser, argSeparator: Char) {
        for (vr in varIt()) {
            a.ap(vr.varCode)
            a.argSep()
        }

    }

    override fun serializeTrueVars(a: Ser, argSeparator: Char) {
        Cubes.serializeCubeTrueVars(a, this)
    }

    override fun serializeTrueVars(): String {
        val a = Ser()
        serializeTrueVars(a, PLConstants.LF)
        return a.toString().trim { it <= ' ' }
    }

    override fun serialize(a: Ser) {
        //        serializeSingleLine(a);
        serializeMultiLine(a)
    }

    fun serializeMultiLine(a: Ser) {
        serialize(a, PLConstants.LF)
    }

    fun serializeSingleLine(a: Ser) {
        serialize(a, PLConstants.ARG_SEP)
    }

    fun serializeSingleLine(): String {
        val a = Ser()
        serializeSingleLine(a)
        return a.toString().trim { it <= ' ' }
    }


    override fun toString(): String {
        val a = Ser()
        serializeSingleLine(a)
        return a.toString().trim { it <= ' ' }
    }

    override fun isTrue(varCode: String): Boolean {
        val vr = getVar(varCode)
        return isTrue(vr!!)
    }

    override fun isFalse(varCode: String): Boolean {
        val vr = space.getVar(varCode)
        return isFalse(vr)
    }

    override fun isFalse(vr: Var): Boolean {
        return containsVar(vr) && !isTrue(vr)
    }


    fun getVar(varId: Int): Var? {
        return if (containsVarId(varId)) space.getVar(varId) else null
    }

    open fun getVar(varCode: String): Var? {
        val vr = space.getVar(varCode)
        return if (containsVar(vr)) vr else null
    }


    override fun containsVar(vr: Var): Boolean {
        return vars.containsVarId(vr.getVarId())
    }

    override fun containsVar(varCode: String): Boolean {
        val vr = space.getVar(varCode)
        return containsVar(vr)
    }


    override fun isTrue(vr: Var): Boolean {
        return isTrue(vr.getVarId())
    }

    fun hashCode1(): Int {
        var hash = size

        if (varCount > 0) {
            hash = Ints.superFastHashIncremental(vars.hashCode(), hash)
        } else {
            hash = Ints.superFastHashIncremental(0, hash)
        }

        if (trueVarCount > 0) {
            hash = Ints.superFastHashIncremental(trueVars.hashCode(), hash)
        } else {
            hash = Ints.superFastHashIncremental(0, hash)
        }

        return Ints.superFastHashAvalanche(hash)
    }

    fun hashCode2(): Int {
        return super.hashCode()
    }

    override fun hashCode(): Int {
        //using hashcode2 *majorly* slowed down computeCubes for a camry c jvm
        //hashCode2 may (I think) have speed something else (can'tCon remember what - maybe client-side) up
        //todo test this - maybe use hashCode1 for jvm and hashCode2 for browser
        return hashCode1()
    }

    fun varsEq(cube: Cube): Boolean {
        return Objects.equal(vars, cube.vars)
    }

    fun trueVarsEq(cube: Cube): Boolean {
        return Objects.equal(trueVarCount, cube.trueVars)
    }

    //    public void print(int depth) {
    //        Exp.prindent(depth, serializeSingleLine());
    //    }
    //


    /**
     * Does not include msrp or dlr _vars
     */
    override fun varIt(): Iterable<Var> {
        return object : Iterable<Var> {
            override fun iterator(): Iterator<Var> {
                return varIterator()
            }
        }
    }

    override fun trueVarIt(): Iterable<Var> {
        return object : Iterable<Var> {
            override fun iterator(): Iterator<Var> {
                return trueVarIterator()
            }
        }
    }

    override fun trueVarIterator(): Iterator<Var> {
        throw UnsupportedOperationException()
    }

    override fun isFalse(varId: Int): Boolean {
        return containsVarId(varId) && !isTrue(varId)
    }

    override fun containsLit(varId: Int, sign: Boolean): Boolean {
        return if (!containsVarId(varId)) false else isTrue(varId) == sign
    }


    override fun litIt(): Iterable<Lit> {
        return Iterable { this.litIterator() }
    }

    override fun litIterator(): Iterator<Lit> {
        return LitIterator(varIterator(), this)
    }

    override fun containsLit(lit: Lit): Boolean {
        return containsLit(lit.varId, lit.sign())
    }

    override fun containsLit(vr: Var, sign: Boolean): Boolean {
        return containsLit(vr.getVarId(), sign)
    }

    fun isAssigned(varId: Int): Boolean {
        return containsVarId(varId)
    }

    fun isAssigned(vr: Var): Boolean {
        return isAssigned(vr.getVarId())
    }

    fun containsAllVars(that: VarSet?): Boolean {
        val vars = vars
        return if (that == null) false else vars.containsAllVars(that)
    }

    override fun anyVarOverlap(exp: Exp): Boolean {
        return vars.anyVarOverlap(exp)
    }

    override fun anyVarOverlap(vs: VarSet): Boolean {
        return vars.anyVarOverlap(vs)
    }

    override fun anyVarOverlap(cube: Cube): Boolean {
        return vars.anyVarOverlap(cube)
    }


    fun forEachVarId(h: VarIdCallback) {
        for (vr in varIt()) {
            h.onVar(vr.getVarId())
        }
    }


    //    public boolean isPos() {
    //        int trueVarCount = getTrueVarCount();
    //        int falseVarCount = getFalseVarCount();
    //        return trueVarCount >= falseVarCount;
    //    }

    override fun getInt32Value(intVarPrefix: String): Int {
        val space = space
        val b = StringBuilder()
        for (i in 0..31) {
            val varCode = VarCode(intVarPrefix, i)
            val vr = space.getVar(varCode.toString())
            val value = getValue(vr)
            if (value.isFalse) {
                b.append('0')
            } else if (value.isTrue) {
                b.append('1')
            } else {
                throw IllegalStateException("Cube not allowed to contain OPEN bits")
            }
        }

        val bitString = b.toString()
        val intValue = Integer.parseInt(bitString, 2)

        assert(intValue >= 0)

        return intValue
    }

    //    @Override
    //    public static int getIntValue(Space space, Set<Exp> lits) {
    //        StringBuilder b = new StringBuilder();
    //        for (int i = 0; i < 32; i++) {
    //            VarCode varCode = new VarCode(intVarPrefix, i);
    //            Var vr = space.getVr(varCode.toString());
    //            Bit value = getValue(vr);
    //            if (value.isFalse()) {
    //                b.append('0');
    //            } else if (value.isTrue()) {
    //                b.append('1');
    //            } else {
    //                throw new IllegalStateException("Cube not allowed to contain OPEN bits");
    //            }
    //        }
    //
    //        String bitString = b.toString();
    //        int intValue = Integer.parseInt(bitString);
    //
    //        assert intValue >= 0;
    //
    //        return intValue;
    //    }

    fun serializeInventoryRecord(a: Ser) {
        val trueVars = trueVars
        val model = trueVars.model
        val year = trueVars.year
        val xCol = trueVars.xCol
        val iCol = trueVars.iCol

        a.ap(model)
        a.argSep()
        a.ap(year)
        a.argSep()
        a.ap(xCol)
        a.argSep()
        a.ap(iCol)
        a.argSep()

        val acyVars = trueVars.acyVars
        for (acyVar in acyVars) {
            a.ap(acyVar)
            a.argSep()
        }


        val trueMsrpVars = trueVars.filter(PLConstants.MSRP_PREFIX)
        //        assert trueMsrpVars.size() == 1 : trueMsrpVars.size();

        for (trueMsrpVar in trueMsrpVars) {
            a.ap(trueMsrpVar)
            a.argSep()
        }

    }

    fun serializeInventoryRecord(): String {
        val a = Ser()
        serializeInventoryRecord(a)
        return a.toString()
    }

    fun toSortedCodes(): SortedSet<String> {
        throw UnsupportedOperationException()
    }

    operator fun minus(complexVars: VarSet): DynCube {
        val space = space
        val cube = DynCube(space)
        for (lit in litIt()) {
            if (!complexVars.contains(lit.vr)) {
                cube.assign(lit)
            }
        }
        return cube
    }

    override fun containsVar(lit: Exp): Boolean {
        return vars.containsVar(lit.vr)
    }

    override fun containsVar(lit: Lit): Boolean {
        return vars.containsVar(lit.getVr())
    }

    override fun asLitSet(): Set<Lit> {
        return ImmutableSet.copyOf(litIt())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cube) return false;

        val that: Cube = other as Cube

        if (!varsEq(that)) return false
        if (!trueVarsEq(that)) return false

        return true
    }

    companion object {

        fun isVarDisjoint(ass: Cube?, bb: Cube): Boolean {
            return ass?.isVarDisjoint(bb) ?: true
        }
    }

}