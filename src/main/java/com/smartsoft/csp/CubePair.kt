package com.smartsoft.csp

import com.smartsoft.csp.ast.DynCube
import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.Lit
import com.smartsoft.csp.ast.sp
import com.smartsoft.csp.dnnf.products.Cube

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