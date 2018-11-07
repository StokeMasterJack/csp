package com.tms.csp.ast

import com.google.common.collect.ImmutableSet
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.parse.GlobalTokenizer

/*
_vars(Root,engine,transmission,colorfamily,ICOL_FB13)
invVars(43 44 46 47 49 61 63 64 65 66 76 79 80 83 92 93 94 96 97 148 149 150 151 153 154 155 162 163 164 165 167 169 170 174 175 178 180 181 182 183 185 186 187 188 189 190 191 192 194 199 200 201 203 204 205 207 208 210 212 213 252 253 254 255 256 257 258 259 260 261)
constraintLines
 */
class Parse(val space: Space) : PLConstants {


//    fun parseCubeToDynCube(sLits: String): Cube {
//        return if (sLits.isNullOrBlank()) {
//            space.mkEmptyCube()
//        } else {
//            parseLines()
//            val ss: Set<Lit> = space.createLitSet(sLits);
//            val createLitSet(sLits)
//            DynCube(space, litSet)
//        }
//    }


//    fun parseCubesToDynCubeSet(sCubes: String): Set<Cube> {
//        val b = ImmutableSet.builder<Cube>()
//        val lines = Space.parseLines1(sCubes)
//        for (sLits in lines) {
//            if (sLits.isNullOrBlank()) {
//                b.add(space.mkEmptyCube())
//            } else {
//                val litSet = space.expFactory.mkLitSet(sLits)
//                val a = DynCube(space, litSet)
//                b.add(a)
//            }
//        }
//        return b.build()
//    }

//
//    companion object {
//
//        @JvmStatic
//        @JvmOverloads
//        fun parseLines(constraints: String): Iterable<String> {
//            return Parse.parseLines(constraints)
//        }
//
//
//        @JvmStatic
//        @JvmOverloads
//        fun fact(fact: String): Parse {
//            return Parse(fact = fact)
//        }
//
////        @JvmStatic
////        @JvmOverloads
////        fun parse(factoryClob: String, invClob: String? = null, varInfoClob: String? = null, extraVars: Set<String>? = null, space: Space = Space()): Space {
////            val p = Parse(factoryClob, invClob, varInfoClob, extraVars).parse()
////
////            val raw = p.parse(factoryClob, invClob, varInfoClob).extraVars(extraVars)
////            return raw.mkSpace(space)
////        }
//
//        private fun extractVarCodesFromPLConstraintLine(expLine: String, b: ImmutableSet.Builder<String>) {
//            val tokens = GlobalTokenizer.INSTANCE.tokenize(expLine)
//            while (!tokens.isEmpty) {
//                val token = tokens.take()
//                if (token.isVar) {
//                    var varCode = token.stringify()
//                    varCode = varCode.trim { it <= ' ' }
//                    b.add(varCode)
//                }
//            }
//        }
//
//        fun extractVarCodes(lines: Iterable<String>, b: ImmutableSet.Builder<String>) {
//            for (line in lines) {
//                throw UnsupportedOperationException()
////                val line: String = Parser.preProcessLine(line) ?: continue
////                extractVarCodesFromPLConstraintLine(line, b)
//            }
//        }
//
//
//        fun isComment(constraintLine: String): Boolean {
//            val tCon = constraintLine.trim()
//            return tCon.isEmpty() || tCon.startsWith("#")
//        }
//
//
//    }


}



