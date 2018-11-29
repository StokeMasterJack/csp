package com.smartsoft.csp.dnnf.products

import com.smartsoft.csp.argBuilder.IArgBuilder
import com.smartsoft.csp.ast.*
import com.smartsoft.csp.util.XorCube
import com.smartsoft.csp.varSet.VarSetK

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

        val selector = Exp.selector

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
    fun DynCube.mk1(): Exp {

        return when {
            this.size == 0 -> space.mkTrue()
            this.size == 1 -> {
                val vr = v.firstVar
                if (t == null || t.isEmpty()) {
                    vr.lit(false)
                } else {
                    assert(t.size == 1)
                    assert(t.firstVar.vrId == vr.vrId)
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
                    override val fcc: FccState get() = Open()
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
        println("eqVars: " + VarSetK.eq(c1.vars, c2.vars))
        println("eqTrueVars: " + VarSetK.eq(c1.trueVars, c2.trueVars))
        println("bothDynCube: " + bothDynCube(c1, c2))
        println("eq: " + eq(c1, c2))


    }

    fun eqSpace(c1: Cube, c2: Cube) = c1.space == c2.space;

    fun eqClass(c1: Cube, c2: Cube) = c1::class == c2::class

    private fun eqDynCubeDynCube(c1: DynCube, c2: DynCube): Boolean {
        return VarSetK.eqNullSafe(c1.v, c2.v) && VarSetK.eqNullSafe(c1.t, c2.t)
    }

    private fun eqXorCubeXorCube(c1: XorCube, c2: XorCube): Boolean {
        return c1.xor === c2.xor && c1.trueVar === c2.trueVar;
    }

    private fun eqDynCubeCube(c1: DynCube, c2: Cube): Boolean {
        return VarSetK.eqNullSafe(c1.v, c2.vars) && VarSetK.eqNullSafe(c1.t, c2.trueVars)
    }

    private fun eqCubeDynCube(c1: Cube, c2: DynCube): Boolean {
        return VarSetK.eqNullSafe(c1.vars, c2.v) && VarSetK.eqNullSafe(c1.trueVars, c2.t)
    }

    private fun eqCubeCube(c1: Cube, c2: Cube): Boolean {
        return VarSetK.eq(c1.vars, c2.vars) && VarSetK.eqNullSafe(c1.trueVars, c2.trueVars)
    }

    @JvmStatic
    fun eq(c1: Cube, c2: Cube): Boolean {
        assert(eqSpace(c1, c2));
        return when {
            c1 is DynCube && c2 is DynCube -> eqDynCubeDynCube(c1, c2)
            c1 is XorCube && c2 is XorCube -> eqXorCubeXorCube(c1, c2)
            c1 is DynCube && c2 !is DynCube -> eqDynCubeCube(c1, c2)
            c1 !is DynCube && c2 is DynCube -> eqCubeDynCube(c1, c2)
            else -> eqCubeCube(c1, c2)
        }
    }

    fun bothDynCube(c1: Cube, c2: Cube) = c1 is DynCube && c2 is DynCube





}

fun Iterable<Cube>.print(cols: Int = 20) {
    CubesK.printCubes(cols = cols, cubes = this)
}




