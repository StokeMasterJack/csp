package com.smartsoft.csp.ast

import com.google.common.collect.Lists

import java.util.Collections
import java.util.Comparator

class ExpComparator : Comparator<Exp> {

    override fun compare(e1In: Exp, e2In: Exp): Int {
        var e1 = e1In
        var e2 = e2In

        val s1 = sortArity(e1)
        val s2 = sortArity(e2)

        var aa = s1.compareTo(s2)
        if (aa != 0) {
            return aa
        } else {
            if (e1.isConstant) {
                assert(e2.isConstant)
                return aa
            } else if (e1.isLit) {
                assert(e2.isLit)
                return aa
            } else {
                if (e1.isNot) {
                    assert(e2.isNot)
                    e1 = e1.arg
                    e2 = e2.arg
                }

                assert(e1.argCount >= 2)
                assert(e1.argCount == e2.argCount)

                val L = e1.argCount

                val args1 = e1.args.toMutableList()
                val args2 = e2.args.toMutableList()

                args1.sortWith(INSTANCE)
                args2.sortWith(INSTANCE)

                for (i in 0 until L) {
                    val a1 = args1.get(i)
                    val a2 = args2.get(i)

                    aa = compare(a1, a2)
                    if (aa != 0) {
                        return aa
                    }

                }


                return 0

            }
        }


    }

    companion object {


        val INSTANCE = ExpComparator()

        fun sortArity(eIn: Exp): String {
            var e = eIn

            val negated = e.isNegated
            val sb = StringBuilder()

            if (negated) {
                sb.append(0)
                if (e.isNegLit) {
                    e = e.flip
                } else {
                    e = e.arg
                }
            } else {
                sb.append(1)
            }

            sb.append('-')


            if (e.isConstant || e.isAnd && e.size() == 0) {
                sb.append(0)
            } else if (e.isPosLit) {
                sb.append(1)
            } else if (e.isPair) {
                sb.append(2)
            } else if (e.isNary) {
                sb.append(e.argCount)
            } else {
                throw IllegalStateException(e.toString() + " " + e.javaClass)
            }


            sb.append('-')

            if (e.isConstant || e.isAnd && e.size() == 0) {
                sb.append(0)
            } else if (e.isPosLit) {
                sb.append(e.varCode)
            } else if (e.isPair) {
                sb.append(e.complexOpToken)
            } else if (e.isNary) {
                sb.append(e.complexOpToken)
            } else {
                throw IllegalStateException()
            }

            return sb.toString()


        }

        fun sortCopyIt(args: Iterable<Exp>): List<Exp> {
            val copy = Lists.newArrayList(args)
            Collections.sort(copy, INSTANCE)
            return copy
        }

        fun sortCopyArray(args: Array<Exp>): List<Exp> {
            val copy = Lists.newArrayList(*args)
            Collections.sort(copy, INSTANCE)
            return copy
        }

        fun sortCopyList(args: List<Exp>): List<Exp> {
            val copy = Lists.newArrayList(args)
            Collections.sort(copy, INSTANCE)
            return copy
        }
    }
}
