package com.tms.csp.ast

import com.tms.csp.fm.dnnf.DAnd
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.util.varSets.VarSet

class CubeExp(space: Space, id: Int, args: Array<Exp>) : DAnd(space, id, args), Cube {

    var dc: DynCube? = null

    val c: DynCube
        get() {
            if (dc == null) {
                dc = DynCube(space, args)
            }
            return dc!!
        }

    init {
        assert(args.isNotEmpty())
        assert(Exp.isAllLits(args))
    }

    override val vars: VarSet get() = c.v

    override fun computeVars(): VarSet = throw UnsupportedOperationException()

    override val isEmpty: Boolean = false

    override val trueVars: VarSet get() = c.t

    override val falseVars: VarSet get() = c.falseVars

    override val falseVarCodes: Set<String> get() = c.falseVarCodes
    override val trueVarCodes: Set<String> get() = c.trueVarCodes

    override val firstLit: Lit get() = c.firstLit

    override val trueVarCount: Int get() = c.trueVarCount

    override val falseVarCount: Int get() = c.falseVarCount


    override fun isTrue(varId: Int) = c.isTrue(varId)

    override fun isTrue(vr: Var): Boolean = c.isTrue(vr)

    override fun isTrue(varCode: String): Boolean = c.isTrue(varCode)


    override fun isFalse(varId: Int): Boolean = c.isFalse(varId)

    override fun isFalse(vr: Var): Boolean = c.isFalse(vr)

    override fun isFalse(varCode: String): Boolean = c.isFalse(varCode)

    override fun containsLit(varId: Int, sign: Boolean): Boolean = c.containsLit(varId, sign)

    override fun containsLit(lit: Lit): Boolean = c.containsLit(lit)

    override fun litIt(): Iterable<Lit> = c.litIt()

    override fun serialize(a: Ser, sep: Char) = c.serialize(a, sep)

    override fun serializeTrueVars(a: Ser, sep: Char) = c.serializeTrueVars(a, sep)

    override fun serializeTrueVars(): String = c.serializeTrueVars()

    override fun trueVarIt(): Iterable<Var> = c.trueVarIt()

    override fun trueVarIterator(): Iterator<Var> = c.trueVarIterator()

    override fun getInt32Value(intVarPrefix: String): Int = c.getInt32Value(intVarPrefix)

    override fun asCube(): Cube = this

    override fun asCubeExp(): CubeExp = this

    override fun isCubeExp(): Boolean = true

    override fun argIt(): Iterable<Exp> = super<DAnd>.argIt()

    override val size: Int get() = arg.size

    override fun asLitSet(): Set<Lit> {
        return litIt().toSet()
    }

    override fun getOp(): Op {
        return Op.Cube
    }

    override fun isCube(): Boolean {
        return true
    }

    override fun toString(): String = super<DAnd>.toString()


    override fun argIter(): Iterator<Exp> = super<DAnd>.argIter()

    override fun getArgCount(): Int = args.size

    override fun size(): Int = argCount

    override fun isNary(): Boolean = argCount() > 2

    override fun satCountPL(): Long {
        return 1
    }
}






