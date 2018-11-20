package com.smartsoft.csp.ast

/*
initial create
                disjoint    mixed   constraint   condition
                unknown     yes

                split complex and simple
                add complex condion c simple

               check disjoint






xor or simple decision
    remove complex
    add complex back condiion c lit

    complex-mew = (complex-old comdition c lit)

propagate
    complex-mew = (complex-old comdition c lit)


assign
propagate


 */





//sealed class Action
//
//
//sealed class Init:Action()
//
//
//sealed class Propagate(c:Lit):Action<Lit>
//
//sealed class SimpleDecision (c: Lit):<Lit>
//
//
//sealed class XorDecision (c: XorCube):<XorCube>
//
//


//    fun anyOverlap(cc: Exp): Boolean {
//        return when {
//            isCube -> cc.anyVarOverlap(asCube)
//            isLit -> cc.anyVarOverlap(asLit)
//            else -> false
//        }
//    }



//

//
//    fun assignSafe(csp: Csp) {
//
//        fun ass(lit: Lit) {
//            csp._assignSafe(lit)
//        }
//
//        fun ass(cube: Cube) {
//            csp._assignSafe(cube)
//        }
//
//        when {
//            isXorCube -> ass(asXorCube)
//            isCube -> ass(asCube)
//            isLit -> ass(asLit)
//        }
//    }




