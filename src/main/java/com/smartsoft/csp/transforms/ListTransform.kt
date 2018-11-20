package com.smartsoft.csp.transforms

import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.util.ConstraintSink
import java.util.ArrayList

import com.google.common.base.Preconditions.checkState

abstract class ListTransform {

    abstract fun transform(inExp: List<Exp>): List<Exp>

    companion object {

        fun transform(listIn: List<Exp>, t: Transformer, out: ConstraintSink) {
            for (e in listIn) {
                val s = t.transform(e)
                out.addConstraint(s)
            }
        }

        fun transform(listIn: List<Exp>, vararg transformer: Transformer): List<Exp> {
            return if (transformer.size == 0) {
                listIn
            } else if (transformer.size == 1) {
                transformList(listIn, transformer[0])
            } else {
                multiTransformList(listIn, transformer)
            }
        }

        fun transformList(listIn: List<Exp>, transformer: Transformer): List<Exp> {
            var elementsChanged = 0
            val listOut = ArrayList<Exp>()
            for (argIn in listIn) {
                checkState(argIn != null)
                val argOut = transformer.transform(argIn)
                if (argOut == null) {
                    throw IllegalArgumentException("transform returned null. Transformer[" + transformer.javaClass.name + "]  expIn[" + argIn + "]")
                } else if (argOut !== argIn) {
                    elementsChanged++
                    listOut.add(argOut)
                } else {
                    listOut.add(argOut)
                }
            }

            return if (elementsChanged > 0) {
                listOut
            } else {
                listIn
            }

        }

        fun transform2(listIn: Iterable<Exp>, transformer: Transformer): Iterable<Exp> {
            var elementsChanged = 0
            val listOut = ArrayList<Exp>()
            for (argIn in listIn) {
                checkState(argIn != null)
                val argOut = transformer.transform(argIn)
                if (argOut == null) {
                    throw IllegalArgumentException("transform returned null. Transformer[" + transformer.javaClass.name + "]  expIn[" + argIn + "]")
                } else if (argOut !== argIn) {
                    elementsChanged++
                    listOut.add(argOut)
                } else {
                    listOut.add(argOut)
                }
            }

            return if (elementsChanged > 0) {
                listOut
            } else {
                listIn
            }

        }

        fun multiTransformList(listIn: List<Exp>, transformers: Array<out Transformer>): List<Exp> {
            var out = listIn
            for (transformer in transformers) {
                val inExp = out
                out = transformList(inExp, transformer)
            }
            return out
        }
    }


}
