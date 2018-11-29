package com.smartsoft.csp.parse

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedSet
import com.google.common.collect.Iterators
import com.smartsoft.csp.Vars
import com.smartsoft.csp.ast.PLConstants
import com.smartsoft.csp.ast.Ser
import com.smartsoft.csp.ast.Var
import com.smartsoft.csp.util.HasCode
import com.smartsoft.csp.util.it.Its
import com.smartsoft.csp.varSet.VarSet
import java.util.*

class ParseUtil : PLConstants {
    companion object {
//
//        @JvmStatic
//        fun parseLines(clob: String, lineProcessor: LineProcessor) {
//            checkNotNull(clob)
//            clob.lineSequence()
//                    .filterNotNull()
//                    .filterNot { Parser.isComment(it) }
//                    .forEach { lineProcessor.processLine(it) }
//        }
//
//        @JvmStatic
//        fun parseLines(clob: String): List<String> {
//            return clob.lineSequence()
//                    .map { Space.preProcessLine(it) }
//                    .filterNotNull()
//                    .filterNot { Parser.isComment(it) }
//                    .toList()
//
//
//        }

        @JvmStatic
        fun stripHeadFootFromLine(line: String, head: String, foot: String): String {
            val p1 = head.length
            val p2 = line.length - foot.length
            return line.substring(p1, p2)
        }

        @JvmStatic
        fun parseVarMap(token: String, varMapIn: String): List<String> {
            var varMap = varMapIn
            val i1 = token.length + 1
            val i2 = varMap.length - 1
            varMap = varMap.substring(i1, i2)
            val varCodes = varMap.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val a = ArrayList<String>()
            Collections.addAll(a, *varCodes)
            return a
        }

        @JvmStatic
        fun isVar(token: Token): Boolean {

            if (token.isLParen) {
                return false
            }
            if (token.isRParen) {
                return false
            }
            if (token.isArgSep) {
                return false
            }
            if (token.isConstantTrue) {
                return false
            }
            if (token.isConstantFalse) {
                return false
            }
            if (token.isNot) {
                return false
            }
            return if (token.isPosComplex) {
                false
            } else true

            //must be vr
        }

        //    public static void extractVarCodesFromVarsLine(String varsLine, ImmutableSet.Builder<VarCode> b) {
        //        VarMap.parseVarMap(varsLine, b);
        //    }

        @JvmStatic
        fun extractFConstraintsFromFLines(fLines: ImmutableList<String>): ImmutableList<String> {
            return if (Vars.isVarsLine(fLines[0])) {
                fLines.subList(1, fLines.size)
            } else {
                fLines
            }
        }

        @JvmStatic
        fun getPrefixFromVarCode(varCode: String): String {
            val i = varCode.indexOf("_")
            if (i == -1) {
                return ""
            }
            var prefix: String? = varCode.substring(0, i)
            if (prefix == null || prefix.trim { it <= ' ' } == "") {
                prefix = ""
            }
            return prefix
        }

        @JvmStatic
        fun getSuffixFromVarCode(varCode: String): String {
            val i = varCode.indexOf("_")
            return if (i == -1) {
                varCode
            } else varCode.substring(i)
        }


        @JvmStatic
        fun serializeCodes(codes: Collection<HasCode>): String {
            val a = Ser()
            serializeCodes(a, codes)
            return a.toString()
        }

        @JvmStatic
        fun serializeCodes(a: Ser, codes: Iterable<HasCode>) {
            serializeCodes(a, codes.iterator())
        }

        @JvmStatic
        fun serializeCodes(a: Ser, codes: Iterator<HasCode>) {
            val codeSet = Its.toSortedCodeSet(codes)
            val it = codeSet.iterator()
            while (it.hasNext()) {
                val code = it.next()
                a.append(code)
                if (it.hasNext()) {
                    a.argSep()
                }
            }
        }

        @JvmStatic
        fun serializeVarCodes(a: Ser, codes: Iterable<String>) {
            val it = codes.iterator()
            while (it.hasNext()) {
                val code = it.next()
                a.append(code)
                if (it.hasNext()) {
                    a.argSep()
                }
            }
        }

        @JvmStatic
        fun serializeVarCodes(codes: Iterable<String>):String {
            val a = Ser()
            serializeVarCodes(a, codes)
            return a.toString()
        }

        fun serializeCodes2(intCodes: Collection<Int>): String {
            val a = Ser()
            serializeCodes2(a, intCodes)
            return a.toString()
        }

        fun serializeCodes2(a: Ser, intCodes: Iterable<Int>) {
            serializeCodes2(a, intCodes.iterator())
        }

        fun serializeCodes2(a: Ser, intCodes: Iterator<Int>) {
            val codeSet = ImmutableSortedSet.copyOf(intCodes)
            val it = codeSet.iterator()
            while (it.hasNext()) {
                val code = it.next().toString() + ""
                a.append(code)
                if (it.hasNext()) {
                    a.argSep()
                }
            }
        }

        @JvmStatic
        fun serializeVarArray(a: Ser, vararg vars: Var) {
            serializeCodes(a, Iterators.forArray(*vars))
        }

        @JvmStatic
        fun serializeVarIt(a: Ser, vars: Iterable<Var>) {
            serializeCodes(a, vars)
        }


        //
        //    public static String[] extractVarCodesFromVarsLine(String varsLine) {
        //        assert isVarsLine(varsLine);
        //        String argList = stripHeadFootFromLine(varsLine, VARS_LINE_HEAD, ")");
        //        return argList.split(ARG_SEP_TOKEN);
        //    }

        fun serializeVarSet(a: Ser, vars: VarSet) {
            vars.serialize(a)
        }

        fun serializeVarCodes(a: Ser, vars: Collection<String>) {
            val L = vars.size
            val it = vars.iterator()
            for (i in 0 until L) {
                val varCode = it.next()
                a.ap(varCode)
                if (i != L - 1) {
                    a.argSep()
                }
            }
        }


    }


//    public static InvVar parseInvVar(String varCode) {
//        String prefix = getPrefixFromVarCode(varCode);
//        String suffix = getSuffixFromVarCode(varCode);
//        return new InvVar(prefix, suffix);
//    }
}
