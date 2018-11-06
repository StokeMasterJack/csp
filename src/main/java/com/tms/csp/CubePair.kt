package com.tms.csp

import com.tms.csp.ast.DynCube
import com.tms.csp.ast.Exp
import com.tms.csp.ast.Lit
import com.tms.csp.ast.sp
import com.tms.csp.fm.dnnf.products.Cube

data class CubePair(val intersection: Cube, val disjoint: Cube) {


    companion object {

        fun split(cube: Cube?, e: Exp): CubePair {
            val sp = e.sp
            if (cube == null || cube.isEmpty || e.vars.isEmpty()) {
                return CubePair(sp.mkEmptyCube(), sp.mkEmptyCube());
            } else {
                val intersection: DynCube = sp.mkSimple()
                val disjoint: DynCube = sp.mkSimple();

                for (lit in cube.litIt()) {
                    if (e.containsVar(lit)) {
                        intersection.assignSafe(lit)
                    } else {
                        disjoint.assignSafe(lit)
                    }
                }
                return CubePair(intersection, disjoint);

            }


        }
    }

}

fun Cube?.split(e: Exp): CubePair {
    return CubePair.split(this, e);
}

typealias LitFilter = (Lit) -> Boolean;

fun Cube.filter(filter: LitFilter): Cube {
    val c: DynCube = space.mkSimple();
    for (lit in litIt()) {
        if (filter(lit)) {
            c.assignSafe(lit)
        }
    }
    return c
}