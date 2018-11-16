package com.tms.csp.fm.dnnf.products

import com.tms.csp.argBuilder.IArgBuilder
import com.tms.csp.ast.*
import com.tms.csp.data.CspSample

object CubesK {

    fun printCubes(cols: Int = 5, cubes: Iterable<Cube>) {
        for (cube in cubes) {
            val s = cube.serialize(cols = cols)
            println(s)
        }
    }


    val DynCube.litExpIt: Iterable<Exp>
        get() = Iterable { litExpIter }


    val DynCube.litExpIter: Iterator<Exp>
        get() = v.litExpIterator(t)


    fun DynCube.createExpArray(): Array<Exp> {

        val selector = KExp.selector

        val aa: Array<Exp> = arrayOfNulls<Exp?>(size).run {
            litExpIt.forEachIndexed { index, exp ->
                this[index] = exp
            }
            this.requireNoNulls()
        }

        aa.sortBy(selector)

        return aa;


    }

    @JvmStatic
    fun DynCube.mk(): Exp {

        return when {
            this.size == 0 -> space.mkTrue()
            this.size == 1 -> {
                val vr = v.firstVar
                if (t == null || t.isEmpty()) {
                    vr.lit(false)
                } else {
                    assert(t.size == 1)
                    assert(t.firstVar.varId == vr.varId)
                    vr.lit(true)
                }
            }
            else -> {
                val argArray = createExpArray();
                val it: Iterable<Exp> = Iterable { kotlin.jvm.internal.iterator(argArray) }
                val b = object : IArgBuilder {
                    init {
                                            }
                    override val argIt: Iterable<Exp> get() = it
                    override val size: Int = argArray.size
                    override val isFcc: Boolean? = null;
                    override val op: Op = Op.Cube
                    override fun mk(): Exp {
                        assert(space.posComplexSpace.checkArgs(argArray))
                        return space.mkPosComplex(this);
                    }

                    override fun createExpArray(): Array<Exp> {
                        return argArray
                    }
                }
                b.mk()

            }
        }


    }

    fun printEq(c1: Cube, c2: Cube) {
        println("c1: " + c1::class)
        println("c2: " + c2::class)
        println("eqSpace: " + eqSpace(c1, c2))
        println("eqClass: " + eqClass(c1, c2))
        println("eqVarSets: " + eqVarSets(c1, c2))
        println("eqVarCodes: " + eqVarCodes(c1, c2))
        println("eqTrueVarCodes: " + eqTrueVarCodes(c1, c2))
        println("eqFalseVarCodes: " + eqFalseVarCodes(c1, c2))
        println("eqTFVarCodes: " + eqTFVarCodes(c1, c2))
        println("bothDynCube: " + bothDynCube(c1, c2))
        println("eq: " + eq(c1, c2))


    }

    fun eqSpace(c1: Cube, c2: Cube) = c1.space == c2.space;

    fun eqClass(c1: Cube, c2: Cube) = c1::class == c2::class

    fun eqVarSets(c1: Cube, c2: Cube) = c1.vars == c2.vars
    fun eqTrueVarSets(c1: Cube, c2: Cube) = c1.trueVars == c2.trueVars
    fun eqTV(c1: Cube, c2: Cube) = c1.trueVars == c2.trueVars && c1.vars == c2.vars

    fun eqVarCodes(c1: Cube, c2: Cube) = c1.varCodesSorted() == c2.varCodesSorted()
    fun eqTrueVarCodes(c1: Cube, c2: Cube) = c1.trueVarCodes == c2.trueVarCodes
    fun eqFalseVarCodes(c1: Cube, c2: Cube) = c1.falseVarCodes == c2.falseVarCodes

    fun eq(c1: Cube, c2: Cube): Boolean {
        return if (bothDynCube(c1, c2)) {
            eqVarSets(c1, c2) && eqTrueVarSets(c1, c2)
        } else {
            eqVarCodes(c1, c2) && eqTrueVarCodes(c1, c2)
        }
    }

    fun bothDynCube(c1: Cube, c2: Cube) = c1 is DynCube && c2 is DynCube


    fun eqTFVarCodes(c1: Cube, c2: Cube) = eqTrueVarCodes(c1, c2) && eqFalseVarCodes(c1, c2)
}


fun tEq(c1: Cube, c2: Cube): Boolean {
    if (c1.trueVars != c2.trueVars) return true;
    return c1.trueVars.toVarCodeSetSorted() == c2.trueVars.toVarCodeSetSorted()
}


fun Iterable<Cube>.print(cols:Int = 20) {
    CubesK.printCubes(cols = cols, cubes = this)
}

fun Iterable<Cube>.spaceEq(sp: Space): Boolean = this.all { it.space == sp }

//fun Space.mkCsp(clob: String): Csp {
//
//
//    return  Csp(space = this, add = Action(clob));
//
////    CubesK.printCubes(cols = 5, cubes = this)
//
//
//}


fun test() {
    val space: Space = Space();

    val clob = CspSample.TinyDc.loadText()

//    Csp

}