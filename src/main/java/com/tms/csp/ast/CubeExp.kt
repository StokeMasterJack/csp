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

    override val vars: VarSet
        get() {
            return c.v
        }

    override fun computeVars(): VarSet = throw UnsupportedOperationException()

    override val isEmpty: Boolean
        get() {
            return false
        }

    override val trueVars: VarSet
        get() {
            return c.t
        }

    override val falseVars: VarSet
        get() {
            return c.falseVars
        }

    override val falseVarCodes: Set<String>
        get() {
            return c.falseVarCodes
        }
    override val trueVarCodes: Set<String>
        get() {
            return c.trueVarCodes
        }

    override val firstLit: Lit
        get() {
            return c.firstLit
        }

    override val trueVarCount: Int
        get() {
            return c.trueVarCount
        }

    override val falseVarCount: Int
        get() {
            return c.falseVarCount
        }


    override fun isTrue(varId: Int): Boolean {
        return c.isTrue(varId)
    }

    override fun isTrue(vr: Var): Boolean {
        return c.isTrue(vr)
    }

    override fun isTrue(varCode: String): Boolean {
        return c.isTrue(varCode)
    }


    override fun isFalse(varId: Int): Boolean {
        return c.isFalse(varId)
    }

    override fun isFalse(vr: Var): Boolean {
        return c.isFalse(vr)
    }

    override fun isFalse(varCode: String): Boolean {
        return c.isFalse(varCode)
    }

    override fun containsLit(varId: Int, sign: Boolean): Boolean {
        return c.containsLit(varId, sign)
    }

    override fun containsLit(lit: Lit): Boolean {
        return c.containsLit(lit)
    }

    override fun litIt(): Iterable<Lit> {
        return c.litIt()
    }

    override fun serialize(a: Ser, sep: Char) {
        return c.serialize(a, sep)
    }

    override fun serializeTrueVars(a: Ser, sep: Char) {
        return c.serializeTrueVars(a, sep)
    }

    override fun serializeTrueVars(): String {
        return c.serializeTrueVars()
    }

    override fun trueVarIt(): Iterable<Var> {
        return c.trueVarIt()
    }

    override fun trueVarIterator(): Iterator<Var> {
        return c.trueVarIterator()
    }

    override fun getInt32Value(intVarPrefix: String): Int {
        return c.getInt32Value(intVarPrefix)
    }

    override fun asCube(): Cube = this

    override fun asCubeExp(): CubeExp = this

    override fun isCubeExp(): Boolean = true

    override fun argIt(): Iterable<Exp> {
        return super<DAnd>.argIt()
    }

    override val size: Int
        get() {
            return arg.size
        }

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


    override fun argIter(): Iterator<Exp> {
        return super<DAnd>.argIter()
    }

    override fun getArgCount(): Int {
        return args.size
    }

    override fun size(): Int {
        return argCount
    }

    override fun isNary(): Boolean {
        return argCount() > 2
    }

    override fun satCountPL(): Long {
        return 1
    }
}






